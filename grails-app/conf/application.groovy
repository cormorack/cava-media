filesDir = "/files"

streamURL = "stream.ocean.washington.edu"

videoPrefix = "https://${streamURL}:443/rsn/mp4:"

videoSuffix = "/playlist.m3u8"

maxFileSize = 314572800  //100 MB 100 * 1024 * 1024

maxImageSize = 52428800  //50 MB 1000 * 1024 * 5

fileTypes = ['mov', 'mp4', 'm4v', 'jpg', 'jpeg', 'png', 'gif']

maxPerPage = 24

environments {

    production {
        cavaWpRestUrl = "https://interactiveoceans.washington.edu/"
        cavaWpPassword = ""
        cavaWpUser = ""
        cavaHost = "interactiveoceans.washington.edu"
        grails.serverURL = "https://media.interactiveoceans.washington.edu"
    }
    development {
        cavaWpRestUrl = "http://localhost:8888/"
        cavaWpPassword = "iRmQcp@YOCrbRxOHgCT9#4ZT"
        cavaWpUser = "io"
        cavaHost = "localhost:8080"
        grails.serverURL = "http://${cavaHost}"
    }
}

swagger {
    info {
        description = "InteractiveOceans Media API Documentation"
        version = "1.0.0"
        title = "InteractiveOceans Media"
        termsOfServices = "http://app.interactiveoceans.washington.edu"
        contact {
            name = "Contact Us"
            url = "http://app.interactiveoceans.washington.edu"
            email = "contact@gmail.com"
        }
        license {
            name = "licence under ..."
            url = "http://app.interactiveoceans.washington.edu"
        }
    }
    schemes = [io.swagger.models.Scheme.HTTP]
    //consumes = ["application/json"]
}

