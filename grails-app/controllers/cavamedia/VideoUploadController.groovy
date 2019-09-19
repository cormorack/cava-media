package cavamedia

import grails.util.Environment
import grails.util.Holders
import javax.servlet.ServletContext
import org.apache.commons.io.FilenameUtils
import org.springframework.web.multipart.MultipartFile

class VideoUploadController {

    def config = Holders.config
    def restService

    //def beforeInterceptor = [action: this.checkHost()]

    static allowedMethods = [uploadVideo: 'POST']

    /**
     * Forwards to the video upload page
     */
    def videoForm() {
        //println request.getRemoteAddr()
        //println request.getHeader("X-FORWARDED-FOR")
        //println request.getRemoteHost()
    }

    /**
     * Uploads the videos to the streaming server.  Adds metadata and the poster image to WP via its REST API.
     * Checks the uploaded file size and file type.
     * @param upload (a simple DTO)
     * @return Message regarding the success or failure of the upload
     */
    def uploadVideo(Upload upload) {

        if (!checkParams()) {

            flash.message = "A required field is missing"
            render(view:'videoForm')
            return
        }

        withForm {

            ServletContext context = getServletContext()

            String filesDir = config.filesDir

            String path = context.getRealPath(filesDir)

            request.getFileNames().each {

                MultipartFile mfile = request.getFile(it)

                if (!mfile || mfile.isEmpty()) {

                    flash.message = "A required file was not selected"
                    render(view:'videoForm')
                    return
                }

                if (mfile.getSize() > config.maxFileSize) {

                    flash.message = "Your file: ${mfile.getOriginalFilename()} exceeds the size limit!"
                    render(view:'videoForm')
                    return
                }

                if (!checkFileType(mfile)) {

                    flash.message = "Your file type is not allowed"
                    render(view:'videoForm')
                    return
                }

                String name = Utilities.stripCharacters(mfile.getOriginalFilename())

                File renamedFile = new File("$path/$name")

                try {

                    if (!renamedFile.parentFile.exists()) renamedFile.parentFile.mkdirs()

                    mfile.transferTo(renamedFile)

                } catch (Exception e) {
                    log.error("An error occurred in ${controllerName}.${actionName} because of ${e.getCause()}")
                    flash.message = "The video could not be uploaded"
                    render(view: 'videoForm')
                    return
                }

                String[] s = name.split("[/]")

                String relativeName = s[s.length - 1]

                setUpload(upload, it, relativeName, renamedFile)
            }

            if (!restService.doUploadProcess(upload, context)) {

                flash.message = "Your video(s) could not be uploaded!"
            }

            flash.message = "Your video(s) have been uploaded."

            render view: "uploaded"
        }
    }

    /**
     * Sets the Upload properties
     * @param upload
     * @param file
     * @param relativeName
     * @param renamedFile
     * @return
     */
    private setUpload(upload, file, relativeName, renamedFile) {

        String url = config.videoPrefix + relativeName + config.videoSuffix

        if (file.equals("desktopVideo")) {

            upload.desktopVideoURL = url

            upload.desktopVideo = renamedFile
        }

        if (file.equals("phoneVideo")) {

            upload.phoneVideoURL = url

            upload.phoneVideo = renamedFile
        }

        if (file.equals("posterImage")) {

            upload.posterImage = renamedFile
        }
    }

    /**
     * Checks file types against the config values
     * @param mfile
     * @return boolean value
     */
    private Boolean checkFileType(MultipartFile mfile) {

        String extension = FilenameUtils.getExtension(mfile.getOriginalFilename()).toLowerCase()

        if (!extension || !config.fileTypes.contains(extension)) {
            return false
        }
        return true
    }

    /**
     * Checks for required parameters
     * @return boolean value
     */
    private boolean checkParams() {

        if (!params.title || !params.content || !params.desktopVideo || !params.posterImage) {
            return false
        }
        return true
    }

    /**
     * Only requests from the specified host in the config are allowed
     * @param request
     * @return boolean value
     */
    private boolean checkHost() {

        if (!request.getRemoteAddr()) {
            return false
        }

        String host = request.getRemoteAddr()

        switch (Environment.current) {
            case Environment.DEVELOPMENT:
                return config.cavaHost == host
                break
            case Environment.PRODUCTION:
                return config.cavaHost == host
                break
        }
    }

}
