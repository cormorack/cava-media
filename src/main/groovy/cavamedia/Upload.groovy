package cavamedia


class Upload implements grails.validation.Validateable {

    String desktopVideoURL, phoneVideoURL, title, content

    File desktopVideo, phoneVideo, posterImage
}
