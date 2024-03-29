package cavamedia

import grails.converters.JSON
import grails.util.Environment
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.http.MediaType
import javax.servlet.ServletContext
import org.apache.commons.io.FilenameUtils
import org.springframework.web.multipart.MultipartFile

@Api(value = "/media/video/", tags = ["Video"])
class VideoUploadController extends BaseController {

    def restService

    //def beforeInterceptor = [action: this.checkHost()]

    static allowedMethods = [uploadVideo: 'POST', submitIssue: 'POST']

    /**
     * Forwards to the video upload page
     */
    @ApiOperation(hidden = true)
    def videoForm() {
        [context: getAppContext()]
    }

    /**
     * Checks the uploaded file size and file type. Then uploads the videos to the streaming server and adds metadata
     * and the poster image to WP via its REST API.
     *
     * @param Upload (a simple DTO)
     * @return Message regarding the success or failure of the upload
     */
    @ApiOperation(
            value = "Uploads a video and its poster image",
            nickname = "uploadVideo",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = "application/json",
            httpMethod = "POST"
    )
    @ApiResponses([
            @ApiResponse(
                    code = 200,
                    message = "The POST call was successful"),
            @ApiResponse(
                    code = 405,
                    message = "Method Not Allowed. Only POST is allowed"),
            @ApiResponse(
                    code = 404,
                    message = "Method Not Found")
    ])
    @ApiImplicitParams([
            @ApiImplicitParam(
                    name = 'desktopVideo',
                    paramType = 'body',
                    required = true,
                    value = "Video File",
                    dataType = "java.io.File"),
            @ApiImplicitParam(
                    name = 'posterImage',
                    paramType = 'body',
                    required = false,
                    value = "Poster Image",
                    dataType = "java.io.File"),
            @ApiImplicitParam(
                    name = "title",
                    paramType = "form",
                    required = true,
                    value = "Video Title",
                    dataType = "string"),
            @ApiImplicitParam(
                    name = "content",
                    paramType = "form",
                    required = true,
                    value = "Video Description",
                    dataType = "string")
    ])
    def uploadVideo( @ApiParam(hidden = true) Upload upload) {

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

                    if (!renamedFile.parentFile.exists()) {
                        renamedFile.parentFile.mkdirs()
                    }
                    mfile.transferTo(renamedFile)

                } catch (Exception e) {
                    log.error("An error occurred because of ${e}")
                    flash.message = "The video could not be uploaded"
                    render(view: 'videoForm')
                    return
                }

                String[] s = name.split("[/]")

                String relativeName = s[s.length - 1]

                setUpload(upload, it, relativeName, renamedFile)
            }

            String message = restService.doUploadProcess(upload, context)

            withFormat {
                html {
                    flash.message = message
                    render view: "uploaded"
                }
                json {
                    Map m = ['link': message]
                    render m as JSON
                }
            }
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

        if (file == "desktopVideo") {

            upload.desktopVideoURL = url

            upload.desktopVideo = renamedFile
        }

        if (file == "phoneVideo") {

            upload.phoneVideoURL = url

            upload.phoneVideo = renamedFile
        }

        if (file == "posterImage") {

            upload.posterImage = renamedFile
        }
    }

    /**
     * Checks the uploaded file type against the config values
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
