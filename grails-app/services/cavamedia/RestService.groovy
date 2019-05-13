package cavamedia

import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse

import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import javax.servlet.ServletContext

import grails.transaction.Transactional
import grails.util.Holders
import grails.util.Environment

@Transactional(readOnly = true)
class RestService {

    def config = Holders.config

    //private String defaultWpUser = config.cavaUser
    private String defaultWpUser = "io"
    //private String defaultPassword = config.cavaWpPassword
    private String defaultPassword = "iRmQcp@YOCrbRxOHgCT9#4ZT"
    private final String restURI = "wp-json/wp/v2/"

    /**
     *
     * @param post
     * @param values
     * @param context
     * @return
     */
    boolean doUploadProcess(Post post, Upload upload, ServletContext context) {

        if ( !addToStreamingServer(upload)) {
            log.error("The videos could not be uploaded to the streaming server")
            return false
        }

        if ( !uploadVideo(post, context, upload)) {
            log.error("The videos could not be uploaded to the WP API")
            return false
        }
        return true
    }

    /**
     *
     * @param upload
     * @return
     */
    boolean addToStreamingServer(Upload upload) {

        List videos = []

        if (upload.desktopVideo) videos.add(upload.desktopVideo)

        if (upload.phoneVideo) videos.add(upload.phoneVideo)

        String url = "http://stream.ocean.washington.edu/streamService/newSaveFile"

        for (File video in videos) {

            RestResponse resp = postFileToApi(url, video, false, false)

            video.delete()

            if (resp?.status != 200) {
                log.error(resp.json.toString())
                return false
            }
        }
        return true
    }

    /**
     * Uploads a placeholder video file, per asinine WP.
     * @param post
     * @param context
     * @param values
     * @return calls updateFile() to add metadata
     */
    boolean uploadVideo(Post post, ServletContext context, Upload upload) {

        File bFile = upload.desktopVideo

        if (! bFile) {
            log.error("The desktop video could not be accessed")
            return
        }

        // Get an existing placeholder video (.mov or .mp4) based on the file's extension
        String path = "${config.filesDir}/placeholder"

        def extension = org.apache.commons.io.FilenameUtils.getExtension(bFile.path)

        String fullPath = path + '.' + extension

        String pathOnServer = context.getRealPath(fullPath)

        if ( !pathOnServer) {
            log.error("The placeholder video file was not found on the server")
            return false
        }

        File jFile = new File(pathOnServer)

        if ( !jFile || !jFile.exists()) {
            log.error("The placeholder video file could not instantiated on the server")
            return false
        }

        // Create a new placeholder video with a unique filename
        String datedPath = DateUtils.addDate(path)

        File tmpFile = new File(context.getRealPath("${datedPath}.${extension}"))

        if ( !tmpFile) {
            log.error("tmpFile is null")
            return false
        }

        InputStream srcStream = jFile.newDataInputStream()

        OutputStream dstStream = tmpFile.newDataOutputStream()

        // Copy the file bytes with from the original placeholder file to the new one
        dstStream << srcStream

        srcStream.close()
        dstStream.close()

        // Post the dummy file to WP
        String url = "${apiUrl()}" + "${restURI}media"

        RestResponse resp = postFileToApi(url, tmpFile, true, true)

        tmpFile.delete()

        if (!resp?.json?.id) {
            log.error("A valid JSON identifier was not found in the response: " + resp.json)
            return false
        }

        return updateFile(post, resp.json.id, upload)
    }

    /**
     *
     * @param post
     * @param id
     * @param values
     * @return
     */
    boolean updateFile(Post post, Integer id, Upload upload) {

        Integer fileId = id

        String url = "${apiUrl()}" + "${restURI}media/${fileId}"

        Map metaMap = [:]

        /*if (file.latitude) metaMap.put("latitude", file.latitude)

        if (file.longitude) metaMap.put("longitude", file.longitude)*/

        String posterImageURL = ""
        String jwShortcode = ""

        if (upload.posterImage) {
            posterImageURL = uploadPosterImage(upload)
        }

        // Add streaming video metas
        if (upload) {
            if (upload.desktopVideoURL) metaMap.put("_jwppp-video-url-1", upload.desktopVideoURL)

            if (upload.phoneVideoURL) metaMap.put("_jwppp-1-source-1-url", upload.phoneVideoURL)

            if (posterImageURL) metaMap.put("_jwppp-video-image-1", posterImageURL)

            metaMap.put("_jwppp-video-title-1", post.title)

            jwShortcode = '[jwp-video="1"]' + "\n" + post.content
        }


        Closure json = {
            title = post.title
            description = jwShortcode ?: post.content
            caption = post.content
            meta = metaMap
        }

        RestResponse resp = postToApi(url, json)

        /*if (resp?.status != 200) {
            log.error(resp?.json?.toString())
            return false
        }*/
        if ( !resp?.status) {
            log.error(resp?.json?.toString())
            return false
        }
        return true
    }

    /**
     *
     * @param values
     * @return
     */
    String uploadPosterImage(Upload upload) {

        File file = upload.posterImage

        String url = "${apiUrl()}" + "${restURI}media"

        RestResponse resp = postFileToApi(url, file, true, true)

        if (!resp?.json?.id) {
            log.error("A valid JSON identifier was not found in the response")
            return ""
        }

        file.delete()

        String fileURL = ""

        if (resp.json?.source_url) fileURL = resp.json.source_url.toString()
    }

    /**
     * Makes an HTTP JSON POST to the API
     * @param url
     * @param customJson
     * @param metaMap
     * @return a grails.plugins.rest.client.RestResponse
     */
    private RestResponse postToApi(String url, Closure customJson) {

        def rest = new RestBuilder()
        RestResponse resp = null
        disableSSLCheck()

        try {

            resp = rest.post(url) {

                auth defaultWpUser, defaultPassword
                contentType "application/json"
                json customJson
            }
        } catch (Exception e) {
            log.error(e.printStackTrace())
            return null
        }
        return resp
    }

    /**
     * Makes an HTTP POST with file data to the API
     * @param url
     * @param jFile
     * @param metaMap
     * @return a grails.plugins.rest.client.RestResponse
     */
    private RestResponse postFileToApi(String url, File jFile, boolean authenticate=false, boolean useSSL=false) {

        def rest = new RestBuilder()
        RestResponse resp = null
        if (useSSL) disableSSLCheck()

        try {

            resp = rest.post(url) {

                if (authenticate) auth defaultWpUser, defaultPassword
                contentType "multipart/form-data"
                file = jFile
            }
        } catch (Exception e) {
            log.error(e.printStackTrace())
            return null
        }
        return resp
    }

    /**
     * Enables SSL without a TrustManager
     * @return
     */
    def static disableSSLCheck() {

        def nullTrustManager = [
                checkClientTrusted: { chain, authType -> },
                checkServerTrusted: { chain, authType -> },
                getAcceptedIssuers: { null }
        ]

        def nullHostnameVerifier = [
                verify: { hostname, session -> true }
        ]

        SSLContext sc = SSLContext.getInstance("SSL")
        sc.init(null, [nullTrustManager as X509TrustManager] as TrustManager[], null)
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory())
        HttpsURLConnection.setDefaultHostnameVerifier(nullHostnameVerifier as HostnameVerifier)
    }

    /**
     * Returns a WP URL based on the runtime environment
     */
    def apiUrl = {

        switch (Environment.current) {
            case Environment.DEVELOPMENT:
                //return config.cavaWpRestUrl
                return "http://localhost:8888/"
                break
            case Environment.PRODUCTION:
                //return config.cavaWpRestUrl
                return "https://ooica.net/"
                break
        }
    }

    /**
     * Returns a streaming server URL based on the runtime environment
     */
    def uploadUrl = {

        switch (Environment.current) {
            case Environment.DEVELOPMENT:
                //return config.streamUpload
                //return "http://localhost:8089/Stream/streamService/newSaveFile"
                return "http://stream.ocean.washington.edu/streamService/newSaveFile"
                break
            case Environment.PRODUCTION:
                //return config.streamUpload
                return "http://stream.ocean.washington.edu/streamService/saveFile"
                break
        }
    }
}
