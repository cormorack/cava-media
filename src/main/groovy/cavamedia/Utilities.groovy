package cavamedia

import de.ailis.pherialize.MixedArray
import de.ailis.pherialize.Pherialize
import grails.util.Holders
import groovy.json.JsonBuilder
import groovy.util.logging.Slf4j
import org.apache.commons.lang.math.NumberUtils
import org.grails.plugins.htmlcleaner.HtmlCleaner
import java.text.BreakIterator

@Slf4j
class Utilities {

    static final String BASE_URL = "https://s3-us-west-2.amazonaws.com/media.ooica.net/"
    static final String VIDEO_IMAGE = "_jwppp-video-image-1"
    static final String VIDEO_URL = "_jwppp-video-url-1"
    static final String WP_METADATA = "_wp_attachment_metadata"
    static final String IO_ID = "ioID"
    static final String DEFAULT_IMAGE = "https://s3-us-west-2.amazonaws.com/media.ooica.net/wp-content/uploads/2019/02/07214108/ooi-rsn-logo.png"
    static final String IO_URL = "https://interactiveoceans.washington.edu/"

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

        return NumberUtils.isNumber(str)
    }

    /**
     * Builds a json or geoJson string depending on the value of geoReferenced
     * @param posts
     * @return a String of json
     */
    static String buildJson(List posts, boolean geoReferenced=true) {

        StringBuilder json = StringBuilder.newInstance()

        if (geoReferenced) {
            json << '{"type": "FeatureCollection", "features": ['
        }
        else {
            json << '['
        }

        posts.eachWithIndex { post, index ->

            json << buildMedia(post, geoReferenced)

            if (index + 1 < posts.size()) {
                json << ","
            }
        }

        if (geoReferenced) {
            json << "]}"
        }
        else {
            json << "]"
        }

        return json.toString()
    }

    /**
     * Builds a composite JSON object from a Post and its featuredMedia
     * @param post
     * @param featuredMedia
     * @return
     */
    static String buildJson(Post post, Post featuredMedia) {

        JsonBuilder jb = new JsonBuilder()

        Map j = jb {
            id post.id
            title post.title
            excerpt setText(post)
            url "${IO_URL}${post.type}/${post.name}"
            if (featuredMedia) {
                thumbnail constructThumbnail(featuredMedia, "medium_large")
                tinyThumbnail constructThumbnail(featuredMedia, "thumbnail")
                imageUrl constructURL(featuredMedia)
            }
        } as Map

        return "[${jb.toString()}]"
    }

    /**
     * Creates or filters text. Any HTML or WP shortcodes are stripped out.  Text length is constrained to 600 characters or less.
     * @param post
     * @return
     */
    static String setText(Post post) {

        String defaultText = "Description not available."

        HtmlCleaner htmlCleaner = Holders.applicationContext.getBean('htmlCleaner') as HtmlCleaner

        if (post.excerpt) {
            return htmlCleaner.cleanHtml(post.excerpt, 'none')
        }
        else if (!post.excerpt && post.content) {

            String description = htmlCleaner.cleanHtml(post.content, 'none')

            description = description.replaceAll("\\[(.*?)\\]", "")

            description = description.size() > 600 ? truncate(description, 600) : description

            if (description.size() > 0)  {
                return description
            }
            else return defaultText
        }
        else return defaultText
    }

    /**
     * Truncates String text according the integer size.  The truncation respects word boundaries.
     * @param content
     * @param contentLength
     * @return
     */
    static String truncate(String content, int contentLength ) {

        String result

        if(content.size() > contentLength) {

            BreakIterator bi = BreakIterator.getWordInstance()
            bi.setText(content)

            def first_after = bi.following(contentLength)
            result = content.substring(0, first_after) + "..."

        } else {
            result = content
        }
        return result
    }

    /**
     * Constructs a Media object whose content varies depending on whether the Post is an image or video
     * @param post
     * @param geoReferenced
     * @return String
     */
    static String buildMedia(Post post, boolean geoReferenced) {

        Media media = new Media(uri: post.guid, title: post.title, type: post.type)

        if (post.mimeType == "video/quicktime" || post.mimeType == "video/mp4") {
            media.isVideo = true
        }

        media.dateString = DateUtils.convertFromTimeStamp(post.date).toString()

        if (post.excerpt) {
            media.excerpt = post.excerpt
        }

        if (!post.excerpt) {
            media.excerpt = post.content
        }

        media.excerpt = DateUtils.cleanText(media.excerpt)

        if (geoReferenced && post.getMetaValue("latitude") && post.getMetaValue("longitude")) {

            media.lat = post.getMetaValue("latitude").metaValue
            media.lon = post.getMetaValue("longitude").metaValue
        }

        if (!media.isVideo) {
            media.uri = constructURL(post)
            media.thumb = constructThumbnail(post, "medium_large")
            media.tinyThumb = constructThumbnail(post, "thumbnail")
        }

        if (media.isVideo) {
            if (post.getMetaValue(VIDEO_URL)) {
                media.vUrl = post.getMetaValue(VIDEO_URL).metaValue
            }
            if (post.getMetaValue(VIDEO_IMAGE)) {
                media.vPoster = post.getMetaValue(VIDEO_IMAGE).metaValue
            }
        }

        if (geoReferenced) {
            return buildMediaGeoJson(media, post)
        }
        else  {
            return buildMediaJson(media, post)
        }
    }

    /**
     *
     * @param media
     * @param post
     * @return
     */
    static String buildMediaGeoJson(Media media, Post post) {

        JsonBuilder jb = new JsonBuilder()

        Map json = jb {
            type "Feature"
            geometry {
                type "Point"
                if (media.lat && media.lon) {
                    coordinates([media.lon, media.lat])
                }
            }
            properties {
                id post.id
                title post.title
                type post.mimeType
                excerpt media.excerpt
                url media.uri
                date media.dateString
                if (!media.isVideo) {
                    thumbnail media.thumb
                    tinyThumbnail media.tinyThumb
                }
                if (media.isVideo) {
                    if (media.vUrl) {
                        videoURL media.vUrl
                    }
                    if (media.vPoster) {
                        videoPoster media.vPoster
                    }
                }
            }
        } as Map
        return jb.toString()
    }

    /**
     *
     * @param media
     * @param post
     * @return
     */
    static String buildMediaJson(Media media, Post post) {

        JsonBuilder jb = new JsonBuilder()

        def json = jb {
            id post.id
            title post.title
            type post.mimeType
            excerpt media.excerpt
            url media.uri
            date media.dateString
            if (!media.isVideo) {
                thumbnail media.thumb
                tinyThumbnail media.tinyThumb
            }
            if (media.isVideo) {
                if (media.vUrl) {
                    videoURL media.vUrl
                }
                if (media.vPoster) {
                    videoPoster media.vPoster
                }
            }
        }
        return jb.toString()
    }

    /**
     * Replace the default uri with the S3 uri
     * @param uri
     * @return String
     */
    static String constructURL(Post post) {

        String uri = post.guid

        AWS aws = AWS.findByPost(post)

        if (!aws || !aws.path) {
            log.error("No AWS reference found")
            return uri
        }

        return BASE_URL + aws.path
    }

    /**
     * Creates an image thumbnail from the WP Metatdata
     * @param post
     * @return String
     */
    static String constructThumbnail(Post post, String size) {

        String data = ""

        if (!post?.getMetaValue(WP_METADATA)?.metaValue) {
            log.error("No WP_METADATA found")
            return data
        }

        AWS aws = AWS.findByPost(post)

        if (!aws || !aws.path) {
            log.error("No AWS reference found")
            return data
        }

        String s3Id = extractID(aws.path)

        if (!s3Id) {
            return data
        }

        String sizes = getSerializedData(post.getMetaValue(WP_METADATA).metaValue, "sizes")

        if (!sizes) {
            return data
        }

        if (sizes.split(size).length <= 1) {
            return data
        }

        data = sizes.split(size)[1]

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

        if (a) {
            b = a.substring(a.indexOf("/"), +9)
        }

        if (b) {
            c = a - b
        }

        if (c) {
            d = c.split("/")[0]
        }

        if (d) {
            return b.drop(1) + d
        }
        else return ""
    }

    /**
     * Returns deserialized data
     * @param data
     * @param key
     * @return deserialized String or empty String
     */
    static String getSerializedData(String data, String key) {

        MixedArray list = Pherialize.unserialize(data)?.toArray()

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

        for (Post post in videos) {

            if (!post.getMetaValue(VIDEO_URL)) {
                continue
            }

            Map values = [:]

            values.put("id", post.id)
            values.put("title", post.title)
            values.put("description", DateUtils.cleanText(post.content))

            String videoImage = post.getMetaValue(VIDEO_IMAGE)?.metaValue ?: DEFAULT_IMAGE

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
     *
     * @param media
     * @return
     */
    static List buildMediaList(List media) {

        List mediaList = []

        for (Post post in media) {

            String uri, thumb = ""

            Map values = [:]

            values.put("id", post.id)
            values.put("title", post.title)
            values.put("description", setText(post))
            values.put("type", post.mimeType)

            if (post.mimeType == "video/quicktime" || post.mimeType == "video/mp4") {

                if (!post.getMetaValue(VIDEO_URL)) {
                    continue
                }

                thumb = post.getMetaValue(VIDEO_IMAGE)?.metaValue ?: DEFAULT_IMAGE
                uri = post.getMetaValue(VIDEO_URL).metaValue

                List l = []

                l.add(post.getMetaValue(VIDEO_URL).metaValue)

                values.put("sources", l.collect { [file: it] })

            } else {
                uri = constructURL(post)
                thumb = constructThumbnail(post, "medium") ?: DEFAULT_IMAGE
            }

            values.put("image", thumb)
            values.put("file", uri)

            mediaList.add(values)
        }
        return mediaList
    }

    /**
     *
     * @param images
     * @return
     */
    static List buildImageList(List images) {

        List imageList = []

        for (Post post in images) {

            String uri = constructURL(post)

            String thumb = constructThumbnail(post, "medium") ?: DEFAULT_IMAGE

            Map values = [:]

            values.put("id", post.id)
            values.put("title", post.title)
            values.put("description", setText(post))
            values.put("image", thumb)
            values.put("file", uri)

            imageList.add(values)
        }
        return imageList
    }
}
