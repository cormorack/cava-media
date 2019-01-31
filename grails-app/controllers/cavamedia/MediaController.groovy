package cavamedia

import groovy.json.JsonBuilder
import org.apache.commons.lang.StringEscapeUtils
import io.swagger.annotations.*

@Api(value = "/api/v1", tags = ["Media"], description = "Media (images and videos)")
class MediaController {

    static namespace = 'v1'

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

        String videoType = "post.mimeType='video/quicktime'"

        String imageType = "post.mimeType='image/png' or post.mimeType='image/jpeg'"

        String query = "select distinct post from Post post , Meta meta where post.id=meta.post"

        query += " and (meta.metaKey='latitude' and meta.metaValue != '')"

        if (params.type) {

            if (params?.type == "image") {
                query += " and (${imageType})"
            }

            if (params?.type == "video") {
                query += " and ${videoType}"
            }

        } else {
            query += " and (${imageType} or ${videoType})"
        }

        List posts = Post.executeQuery(query)

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
     * Constructs individual geoJson Feature that varies depending on whether the Post is an image or video
     * @param post
     * @return String
     */
    private String buildFeature(Post post) {

        boolean isVideo = post.mimeType == "video/quicktime"

        def jb = new JsonBuilder()

        Map feature = jb {
            type "Feature"
            geometry {
                type "Point"
                if (post.latitude && post.longitude) {
                    coordinates([post.longitude.metaValue, post.latitude.metaValue])
                }
            }
            properties {
                title post.title
                type post.mimeType
                url post.guid
                excerpt StringEscapeUtils.escapeHtml(post.excerpt)
                if (isVideo) {
                    if (post.videoURL) {
                        videoURL post.videoURL.metaValue
                    }
                    if (post.videoPoster) {
                        videoPoster post.videoPoster.metaValue
                    }
                }
            }
        }
        jb.toString()
    }
}
