package cavamedia

import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import grails.transaction.Transactional
import grails.util.Holders
import grails.util.Environment
import org.springframework.beans.factory.annotation.Value

import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import javax.servlet.ServletContext

@Transactional(readOnly = true)
class RestService {

    def config = Holders.config

    /**
     * The username and password are set as environmental variables in production
     */
    @Value('${wpuser}')
    private String defaultWpUser

    @Value('${wppassword}')
    private String defaultPassword

    private final String restURI = "wp-json/wp/v2/"

    private final String streamURL = "http://stream.ocean.washington.edu/streamService/newSaveFile"

    /**
     * Uploads videos to the streaming server. If successful, then uploads the poster image to WP
     * and adds the video metadata to WP
     * @param post
     * @param values
     * @param context
     * @return boolean value
     */
    boolean doUploadProcess(Upload upload, ServletContext context) {

        if (!addToStreamingServer(upload)) {
            log.error("The videos could not be uploaded to the streaming server")
            return false
        }

        if (!uploadVideo(context, upload)) {
            log.error("The videos could not be uploaded to the WP API")
            return false
        }
        return true
    }

    /**
     * Uploads videos to the streaming server and then deletes it
     * @param upload
     * @return boolean value
     */
    boolean addToStreamingServer(Upload upload) {

        List videos = []

        if (upload.desktopVideo) videos.add(upload.desktopVideo)

        if (upload.phoneVideo) videos.add(upload.phoneVideo)

        for (File video in videos) {

            RestResponse resp = postFileToApi(streamURL, video, false, false)

            video.delete()

            if (resp?.status != 200) {
                log.error(resp.json.toString())
                return false
            }
        }
        return true
    }

    /**
     * Uploads a placeholder video file, per asinine WP. Then deletes it from the file system.
     * @param post
     * @param context
     * @param values
     * @return calls updateFile() to add the video metadata (a boolean value)
     */
    boolean uploadVideo(ServletContext context, Upload upload) {

        File bFile = upload.desktopVideo

        if (!bFile) {
            log.error("The desktop video could not be accessed")
            return
        }

        // Get an existing placeholder video (.mov or .mp4) based on the file's extension
        String path = "${config.filesDir}/placeholder"

        def extension = org.apache.commons.io.FilenameUtils.getExtension(bFile.path)

        String fullPath = path + '.' + extension

        String pathOnServer = context.getRealPath(fullPath)

        if (!pathOnServer) {
            log.error("The placeholder video file was not found on the server")
            return false
        }

        File jFile = new File(pathOnServer)

        if (!jFile || !jFile.exists()) {
            log.error("The placeholder video file could not instantiated on the server")
            return false
        }

        // Create a new placeholder video with a unique filename
        String datedPath = DateUtils.addDate(path)

        File tmpFile = new File(context.getRealPath("${datedPath}.${extension}"))

        if (!tmpFile) {
            log.error("tmpFile is null")
            return false
        }

        InputStream srcStream = jFile.newDataInputStream()

        OutputStream dstStream = tmpFile.newDataOutputStream()

        // Copy the file bytes from the original placeholder file to the new one
        dstStream << srcStream

        srcStream.close()
        dstStream.close()

        // Post the placeholder file to WP
        String url = "${apiUrl()}" + "${restURI}media"

        RestResponse resp = postFileToApi(url, tmpFile, true, true)

        tmpFile.delete()

        if (!resp?.json?.id) {
            log.error("A valid JSON identifier was not found in the response: " + resp?.json)
            return false
        }

        return updateFile(resp.json.id, upload)
    }

    /**
     * Updates the file metadata via the WP REST API
     * @param post
     * @param id
     * @param values
     * @return boolean value
     */
    boolean updateFile(Integer id, Upload upload) {

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

            metaMap.put("_jwppp-video-title-1", upload.title)

            jwShortcode = '[jwp-video="1"]' + "\n" + upload.content
        }


        Closure json = {
            title = upload.title
            description = jwShortcode ?: upload.content
            caption = upload.content
            meta = metaMap
        }

        RestResponse resp = postToApi(url, json)

        /**
         * For some reason WP returns a 403 here even though the content is updated...
         * so we can't check for a 201
         */
        /*if (resp?.status != 201) {
            log.error(resp?.json?.toString())
            return false
        }*/
        if (!resp?.status) {
            log.error(resp?.json?.toString())
            return false
        }
        return true
    }

    /**
     * Adds an image to WP
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

                auth cavaWpUser(), cavaWpPassword()
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
     * Makes an HTTP POST with file data to the stream API
     * @param url
     * @param jFile
     * @param metaMap
     * @return a grails.plugins.rest.client.RestResponse
     */
    private RestResponse postFileToApi(String url, File jFile, boolean authenticate = false, boolean useSSL = false) {

        def rest = new RestBuilder()
        RestResponse resp = null
        if (useSSL) disableSSLCheck()

        try {

            resp = rest.post(url) {

                if (authenticate) auth cavaWpUser(), cavaWpPassword()
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
                return config.cavaWpRestUrl
                break
            case Environment.PRODUCTION:
                return config.cavaWpRestUrl
                break
        }
    }

    /**
     * Returns a username based on the runtime environment
     */
    String cavaWpUser() {

        switch (Environment.current) {
            case Environment.DEVELOPMENT:
                return config.cavaWpUser
                break
            case Environment.PRODUCTION:
                return defaultWpUser
                break
        }
    }

    /**
     * Returns a password based on the runtime environment
     */
    String cavaWpPassword() {

        switch (Environment.current) {
            case Environment.DEVELOPMENT:
                return config.cavaWpPassword
                break
            case Environment.PRODUCTION:
                return defaultPassword
                break
        }
    }
}
