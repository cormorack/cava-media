package cavamedia

import de.ailis.pherialize.Pherialize
import groovy.json.JsonBuilder
import org.apache.commons.lang.StringEscapeUtils
import io.swagger.annotations.*

@Api(value = "/api/v1", tags = ["Media"], description = "Media (images and videos)")
class MediaController {

    static namespace = 'v1'

    private final String baseURL = "https://s3-us-west-2.amazonaws.com/media.ooica.net/"

    def postService

    /**
     * Map for Testing
     */
    def map() {}

    /**
     * Returns geoJson derived from WP_Posts that have coordinates and image or video mime types.
     * The type parameter can be used to filter for images or videos.  If no parameter is supplied,
     * both types are returned.
     * @param type
     * @return a geoJson string
     */
    @ApiOperation(
            value = "Returns geoJson derived from WP_Posts with coordinates",
            nickname = "media",
            produces = "application/json",
            httpMethod = "GET",
            response = java.lang.String.class
    )
    @ApiResponses([
            @ApiResponse(code = 405,
                    message = "Method Not Allowed. Only GET is allowed"),

            @ApiResponse(code = 404,
                    message = "Method Not Found")
    ])
    @ApiImplicitParams([
            @ApiImplicitParam(name = "type",
                    paramType = "query",
                    required = false,
                    value = "Optionally filter by type (video or image).",
                    dataType = "string")
    ])
    def index() {

        List posts = postService.getPosts(params.type)

        response.setContentType("text/json")

        if (!posts) {
            render "{[]}"
            return
        }
        render buildJson(posts)
    }

    /**
     * Builds a geoJson string
     * @param posts
     * @return a String of geoJson
     */
    private String buildJson(List posts) {

        def json = StringBuilder.newInstance()

        json << '{ "type": "FeatureCollection", "features": ['

        posts.eachWithIndex { post, index ->

            json << buildFeature(post)

            if(index + 1 < posts.size()) json << ","
        }
        json << "]}"

        return json.toString()
    }

    /**
     * Constructs an individual geoJson Feature that varies depending on whether the Post is an image or video
     * @param post
     * @return String
     */
    private String buildFeature(Post post) {

        boolean isVideo = post.mimeType == "video/quicktime"

        String uri = post.guid

        String thumb, lat, lon, vUrl, vPoster = ""

        if (post.getMetaValue("latitude") && post.getMetaValue("longitude")) {

            lat = post.getMetaValue("latitude").metaValue

            lon = post.getMetaValue("longitude").metaValue
        }

        if (!isVideo) {
            uri = constructURL(post)
            thumb = constructThumbnail(post)
        }

        // If it's a video, add the video-specific Metas
        if (isVideo) {
            if (post.getMetaValue("_jwppp-video-url-1")) {
                vUrl = post.getMetaValue("_jwppp-video-url-1").metaValue
            }
            if (post.getMetaValue("_jwppp-video-image-1")) {
                vPoster = post.getMetaValue("_jwppp-video-image-1").metaValue
            }
        }

        def jb = new JsonBuilder()

        Map feature = jb {
            type "Feature"
            geometry {
                type "Point"
                if (lat && lon) {
                    coordinates([lon, lat])
                }
            }
            properties {
                title post.title
                type post.mimeType
                excerpt StringEscapeUtils.escapeHtml(post.excerpt)
                url uri
                if (!isVideo) {
                    thumbnail thumb
                }
                if (isVideo) {
                    if (vUrl) {
                        videoURL vUrl
                    }
                    if (vPoster) {
                        videoPoster vPoster
                    }
                }
            }
        }
        jb.toString()
    }

    /**
     * Replace the default uri with the S3 uri
     * @param uri
     * @return String
     */
    private String constructURL(Post post) {

        String uri = post.guid

        if(!post?.getMetaValue("amazonS3_info")?.metaValue) {

            log.error("No amazonS3_info found")
            return uri
        }

        String serializedData = getSerializedData(post.getMetaValue("amazonS3_info").metaValue, "key")

        if (serializedData) return baseURL + serializedData

        else return uri
    }

    /**
     * Todo: this method needs the unique AWS ID in order to be accurate
     * @param post
     * @return
     */
    private String constructThumbnail(Post post) {

        String data = ""

        if (!post?.getMetaValue("_wp_attachment_metadata")?.metaValue) {

            log.error("No _wp_attachment_metadata found")
            return data
        }

        if(!post?.getMetaValue("amazonS3_info")?.metaValue) {

            log.error("No amazonS3_info found")
            return data
        }

        String s3Data = getSerializedData(post.getMetaValue("amazonS3_info").metaValue, "key")
        
        if (!s3Data) {
            return data
        }

        String s3Id = extractID(s3Data)

        String sizes = getSerializedData(post.getMetaValue("_wp_attachment_metadata").metaValue, "sizes")

        if (!sizes) {
            return data
        }

        if (sizes.split("medium_large=").length <= 1) {
            return data
        }

        data = sizes.split("medium_large=")[1]

        String target = "{file="

        data = data.substring(data.indexOf(target) + target.size()).split(",")[0]

        return baseURL + "wp-content/uploads/" + s3Id + "/" + data
    }

    /**
     * Given a url like:
     * https://s3-us-west-2.amazonaws.com/media.ooica.net/wp-content/uploads/2019/01/02232110/flatfish_shr_sm_sulis20180625215451.jpg
     * extract the S3 ID (/2019/01/02232110)
     * @param url
     * @return
     */
    private String extractID(String url) {

        String a,b,c,d = ""

        a = url.split("uploads")[1]

        if (a) b = a.substring(a.indexOf("/"), +9)

        if (b) c = a - b

        if (c) d = c.split("/")[0]

        if (d) return b.drop(1) + d

        else return ""
    }

    /**
     * Returns deserialized data
     * @param data
     * @param key
     * @return deserialized String
     */
    private String getSerializedData(String data, String key) {

        de.ailis.pherialize.MixedArray list = Pherialize.unserialize(data)?.toArray()

        if (!list) {
            log.error("Data could not be unserialized")
            return ""
        }

        if (!list.get(key)) {
            log.error("Data could not be looked up by key")
            return ""
        }

        return list.get(key).toString()
    }
}
