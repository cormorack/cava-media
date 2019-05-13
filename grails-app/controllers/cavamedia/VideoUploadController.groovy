package cavamedia

import javax.servlet.ServletContext
import org.springframework.web.multipart.MultipartFile
import static org.springframework.http.HttpStatus.*
import grails.util.Holders

class VideoUploadController {

    def config = Holders.config
    def restService

    static allowedMethods = [uploadVideo:'POST']

    /**
     *
     */
    def videoForm() { }

    /**
     *
     * @return
     */
    def uploadVideo() {

        Post post = new Post(params)

        Upload upload = new Upload()

        ServletContext context = getServletContext()

        String filesDir = config.filesDir

        String path = context.getRealPath(filesDir)

        request.getFileNames().each {

            MultipartFile mfile = request.getFile(it)

            if ( !mfile.isEmpty()) {

                String name = stripCharacters(mfile.getOriginalFilename())

                File renamedFile = new File("$path/$name")

                try {

                    if(!renamedFile.parentFile.exists()) renamedFile.parentFile.mkdirs()

                    mfile.transferTo(renamedFile)

                } catch (Exception e) {
                    log.error("An error occurred in ${controllerName}.${actionName} because of ${e.getCause()}")
                    flash.message = "The video could not be uploaded"
                    render(view:'videoForm')
                    return
                }

                String[] s = name.split("[/]")

                String relativeName = s[s.length -1]

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
        }
        if (restService.doUploadProcess(post, upload, context)) {

            flash.message = "Your video(s) have been uploaded!"
        }
        else flash.message = "Your video(s) could not be uploaded!"

        render view: "uploaded"
    }


    /**
     *
     * @param mfile
     * @return
     */
    private addFile(MultipartFile mfile) {

        String name = fileService.stripCharacters(mfile.getOriginalFilename())

        java.io.File renamedFile = null

        def path = getServletContext().getRealPath(config.filesDir)

        renamedFile = new java.io.File("$path/$name")

        if(renamedFile) {

            try {

                if(!renamedFile.parentFile.exists()) renamedFile.parentFile.mkdirs()

                mfile.transferTo(renamedFile)

            } catch (Exception e) {
                log.error("An error occurred in ${controllerName}.${actionName} because of ${e.getCause()}")
                return null
            }
        }
        return renamedFile
    }

    private Boolean checkFile(MultipartFile mfile) {

        if (mfile.isEmpty()) {
            render(view:'videoForm', model:[file:file, params:params])
            flash.message = "A file was not selected"
        }

        else if (mfile.getSize() > 10000000) {
            render(view:'videoForm', model:[file:file, params:params])
            flash.message = "The image exceeds the 10 MB limit"
        }
    }

    private String stripCharacters(String filename) {

        filename = filename.toLowerCase()
        filename = filename.replaceAll("[^\\d\\w\\.\\-]", "")
    }
}
