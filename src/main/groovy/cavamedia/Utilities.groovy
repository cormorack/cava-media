package cavamedia

import de.ailis.pherialize.Pherialize
import groovy.json.JsonBuilder
import org.apache.commons.lang.StringEscapeUtils
import groovy.util.logging.Slf4j

@Slf4j
class Utilities {

    static final String BASE_URL = "https://s3-us-west-2.amazonaws.com/media.ooica.net/"
    static final String VIDEO_IMAGE = "_jwppp-video-image-1"
    static final String VIDEO_URL = "_jwppp-video-url-1"
    static final String S3_INFO = "amazonS3_info"
    static final String WP_METADATA = "_wp_attachment_metadata"
    static final String IO_ID = "ioID"

    /**
     * Used to clean up file names.  Makes the name lower case and strips problematic characters
     * @param filename: A (String) name of a file
     * @return filename: The "stripped" name
     */
    static String stripCharacters(String filename) {

        filename = filename.toLowerCase()
        filename = filename.replaceAll("[^\\d\\w\\.\\-]", "")
    }

    /**
     * Returns true or false if the string is a number or not
     * @param str
     * @return
     */
    static Boolean isNumeric(String str) {

        return org.apache.commons.lang.math.NumberUtils.isNumber(str)
    }

    /**
     * Builds a geoJson string
     * @param posts
     * @return a String of geoJson
     */
    static String buildJson(List posts) {

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
    static String buildFeature(Post post) {

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
    static String constructURL(Post post) {

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
    static String constructThumbnail(Post post) {

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

        if (!s3Id) {
            return data
        }

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
    static String extractID(String url) {

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
    static String getSerializedData(String data, String key) {

        de.ailis.pherialize.MixedArray list = Pherialize.unserialize(data)?.toArray()

        if (!list || !list.get(key)) {
            return ""
        }

        return list.get(key).toString()
    }

    /**
     * Builds the JWPlayer JSON.  Only videos without images are included.
     * @param videos
     * @return
     */
    static List buildVideoListNoImages(List videos) {

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
    static List buildFullVideoList(List videos) {

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

    static List buildImageList(List images) {

        List imageList = []

        String defaultImage = "https://s3-us-west-2.amazonaws.com/media.ooica.net/wp-content/uploads/2019/02/07214108/ooi-rsn-logo.png"

        for (Post post in images) {

            /*if (!constructThumbnail(post)) {
                continue
            }*/

            String uri = constructURL(post)

            String thumb = constructThumbnail(post) ?: defaultImage

            Map values = [:]

            values.put("id", post.id)
            values.put("title", post.title)
            values.put("description", DateUtils.cleanText(post.content))
            values.put("image", thumb)
            values.put("file", uri)

            imageList.add(values)
        }
        return imageList
    }
}
