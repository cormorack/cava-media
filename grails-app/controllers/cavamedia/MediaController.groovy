package cavamedia

import de.ailis.pherialize.Pherialize
import groovy.json.JsonBuilder
import org.apache.commons.lang.StringEscapeUtils
import io.swagger.annotations.*
import grails.util.Holders

@Api(value = "/api/v1", tags = ["Media"], description = "Media (images and videos)")
class MediaController {

    static namespace = 'v1'

    private final String BASE_URL = "https://s3-us-west-2.amazonaws.com/media.ooica.net/"
    private final String VIDEO_IMAGE = "_jwppp-video-image-1"
    private final String VIDEO_URL = "_jwppp-video-url-1"
    private final String IO_ID = "ioID"
    private final String S3_INFO = "amazonS3_info"
    private final String WP_METADATA = "_wp_attachment_metadata"

    def postService
    def config = Holders.config

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
     * Renders the video page
     */
    def video() {}

    /**
     * Renders the video gallery page
     */
    def videoGallery() {}

    /**
     * Returns videos that DO NOT have images as JSON
     * @return
     */
    def findVideos() {

        setParams(params)

        List videos = postService.getVideos(
            params.max.toInteger(),
            params.offset.toInteger(),
            params.sort,
            params.order,
            params.q,
            params.type
        )

        response.setContentType("text/json")

        if (!videos) {
            render "{[]}"
            return
        }

        List videoList = buildVideoListNoImages(videos)

        respond videoList
    }

    /**
     * Returns a list of videos formatted as JW Player JSON
     * @return
     */
    def findAllVideos() {

        setParams(params)

        List videos = postService.getVideos(
            params.max.toInteger(),
            params.offset.toInteger(),
            params.sort,
            params.order,
            params.q,
            params.type
        )

        response.setContentType("text/json")

        if (!videos) {
            render "{[]}"
            return
        }

        List videoList = buildFullVideoList(videos)

        Map paramMap = [playlist: videoList, total: videos.getTotalCount(), offset: params.offset, max:params.max]

        respond paramMap
    }

    /**
     * Builds the JWPlayer JSON.  Only videos without images are included.
     * @param videos
     * @return
     */
    private List buildVideoListNoImages(List videos) {

        List videoList = []

        for (Post post in videos) {

            if (post.getMetaValue(VIDEO_IMAGE) || !post.getMetaValue(VIDEO_URL) || !post.getMetaValue(IO_ID)) {
                continue
            }

            Map values = [:]

            values.put("id", post.id)
            values.put(IO_ID, post.getMetaValue(IO_ID).metaValue)
            values.put("title", post.title ?: "no title")
            values.put("description", DateUtils.cleanText(post.content))

            values.put("file", post.getMetaValue(VIDEO_URL).metaValue)

            List l = []

            l.add(post.getMetaValue(VIDEO_URL).metaValue)

            values.put("sources", l.collect { [file: it] })

            videoList.add(values)
        }
        return videoList
    }

    /**
     * Builds the JWPlayer JSON.  Videos without images are given a default image.
     * @param videos
     * @return
     */
    private List buildFullVideoList(List videos) {

        List videoList = []

        String defaultImage = "https://s3-us-west-2.amazonaws.com/media.ooica.net/wp-content/uploads/2019/02/07214108/ooi-rsn-logo.png"

        for (Post post in videos) {

            if (!post.getMetaValue(VIDEO_URL)) {
                continue
            }

            Map values = [:]

            values.put("id", post.id)
            values.put("title", post.title)
            values.put("description", DateUtils.cleanText(post.content))

            String videoImage = post.getMetaValue(VIDEO_IMAGE)?.metaValue ?: defaultImage

            values.put("image", videoImage)

            List l = []

            values.put("file", post.getMetaValue(VIDEO_URL).metaValue)

            l.add(post.getMetaValue(VIDEO_URL).metaValue)

            values.put("sources", l.collect { [file: it] })

            videoList.add(values)
        }
        return videoList
    }

    /**
     * Sets default values for parameters.
     * sort=date, order=desc, offset=0, max=default value in config.properties
     * max cannot be larger than the configuration value, less than 0
     * @param params
     * @return
     */
    private setParams(params) {

        if (!params.max || !isNumeric(params.max.toString()) || params.max.toInteger() < 0) {
            params.max = config.maxPerPage
        }

        if (!params.sort) params.sort = "date"
        if (!params.order) params.order = "desc"

        if (!params.offset || !isNumeric (params.offset.toString()) ) params.offset = 0
    }

    /**
     * Returns true or false if the string is a number or not
     * @param str
     * @return
     */
    private Boolean isNumeric(String str) {

        return org.apache.commons.lang.math.NumberUtils.isNumber(str)
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

        boolean isVideo = false
        if (post.mimeType == "video/quicktime" || post.mimeType == "video/mp4") {
            isVideo = true
        }

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
            if (post.getMetaValue(VIDEO_URL)) {
                vUrl = post.getMetaValue(VIDEO_URL).metaValue
            }
            if (post.getMetaValue(VIDEO_IMAGE)) {
                vPoster = post.getMetaValue(VIDEO_IMAGE).metaValue
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

        if(!post?.getMetaValue(S3_INFO)?.metaValue) {

            log.error("No ${S3_INFO} found")
            return uri
        }

        String serializedData = getSerializedData(post.getMetaValue(S3_INFO).metaValue, "key")

        if (serializedData) return BASE_URL + serializedData

        else return uri
    }

    /**
     * Creates an image thumbnail from the WP Metatdata
     * @param post
     * @return String
     */
    private String constructThumbnail(Post post) {

        String data = ""

        if (!post?.getMetaValue(WP_METADATA)?.metaValue) {
            log.error("No ${WP_METADATA} found")
            return data
        }

        if (!post?.getMetaValue(S3_INFO)?.metaValue) {
            log.error("No ${S3_INFO} found")
            return data
        }

        String s3Data = getSerializedData(post.getMetaValue(S3_INFO).metaValue, "key")

        if (!s3Data) {
            return data
        }

        String s3Id = extractID(s3Data)

        String sizes = getSerializedData(post.getMetaValue(WP_METADATA).metaValue, "sizes")

        if (!sizes) {
            return data
        }

        if (sizes.split("medium_large=").length <= 1) {
            return data
        }

        data = sizes.split("medium_large=")[1]

        String target = "{file="

        data = data.substring(data.indexOf(target) + target.size()).split(",")[0]

        return BASE_URL + "wp-content/uploads/" + s3Id + "/" + data
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
     * @return deserialized String or empty String
     */
    private String getSerializedData(String data, String key) {

        de.ailis.pherialize.MixedArray list = Pherialize.unserialize(data)?.toArray()

        if (!list || !list.get(key)) {
            return ""
        }

        return list.get(key).toString()
    }
}
