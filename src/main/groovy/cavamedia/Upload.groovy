package cavamedia

import io.swagger.v3.oas.annotations.*

@Hidden
class Upload implements grails.validation.Validateable {

    String desktopVideoURL, phoneVideoURL, title, content

    File desktopVideo, phoneVideo, posterImage
}
