package cavamedia

import javax.servlet.ServletContext
import org.springframework.web.multipart.MultipartFile
import static org.springframework.http.HttpStatus.*
import grails.util.Holders
import org.apache.commons.io.FilenameUtils


class VideoUploadController {

    def config = Holders.config
    def restService

    static allowedMethods = [uploadVideo:'POST']

    /**
     * Forwards to the video upload page
     */
    def videoForm() { }

    /**
     * Uploads the videos to the streaming server.  Adds metadata and the poster image to WP via its REST API
     * @param upload (simple DTO)
     * @return Message regarding the success or failure of the upload
     */
    def uploadVideo(Upload upload) {

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

                if (checkFileSize(mfile.getSize())) {

                    flash.message = "Your file: ${mfile.getOriginalFilename()} exceeds the size limit!"
                    render(view:'videoForm')
                    return
                }

                if (!checkFileType(mfile)) {
                    flash.message = "Your file type is not allowed"
                    render(view:'videoForm')
                    return
                }

                String name = stripCharacters(mfile.getOriginalFilename())

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

                if (it.equals("desktopVideo")) {

                    upload.desktopVideoURL = config.videoPrefix + relativeName + config.videoSuffix

                    upload.desktopVideo = renamedFile
                }

                if (it.equals("phoneVideo")) {

                    upload.phoneVideoURL = config.videoPrefix + relativeName + config.videoSuffix

                    upload.phoneVideo = renamedFile
                }

                if (it.equals("posterImage")) {

                    upload.posterImage = renamedFile
                }

            }

            if (restService.doUploadProcess(upload, context)) {

                flash.message = "Your video(s) have been uploaded."

            } else flash.message = "Your video(s) could not be uploaded!"

            render view: "uploaded"
        }
    }

    /**
     * Checks file size against the config value
     * @param fileSize
     * @param type
     * @return
     */
    private boolean checkFileSize(Long fileSize) {

        return fileSize > config.maxImageSize
    }

    /**
     * Checks file types against the config values
     * @param mfile
     * @return
     */
    private Boolean checkFileType(MultipartFile mfile) {

        String extension = FilenameUtils.getExtension(mfile.getOriginalFilename()).toLowerCase()

        if(!extension || !config.fileTypes.contains(extension)) {
            return false
        }
        return true
    }

    /**
     *
     * @param filename
     * @return
     */
    private String stripCharacters(String filename) {

        filename = filename.toLowerCase()
        filename = filename.replaceAll("[^\\d\\w\\.\\-]", "")
    }
}
