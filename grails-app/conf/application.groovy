filesDir = "/files"
streamURL = "stream.ocean.washington.edu"
videoPrefix = "https://${streamURL}:443/rsn/mp4:"
videoSuffix = "/playlist.m3u8"
maxFileSize = 104857600  //100 MB 100 * 1024 * 1024
maxImageSize = 52428800  //50 MB 1000 * 1024 * 5


fileTypes = ['mov', 'mp4', 'm4v', 'jpg', 'jpeg', 'png', 'gif']

environments {

    production {
        cavaWpRestUrl = "https://ooica.net/"
        streamUpload = "http://stream.ocean.washington.edu/streamService/saveFile"
        cavaWpPassword = "iRmQcp@YOCrbRxOHgCT9#4ZT"
        cavaWpUser = "io"
    }
    development {
        cavaWpRestUrl = "http://localhost:8888/"
        streamUpload = "http://localhost:8080/Stream/streamService/saveFile"
        cavaWpPassword = "iRmQcp@YOCrbRxOHgCT9#4ZT"
        cavaWpUser = "io"
    }
}

swagger {
    info {
        description = "Cava Media API Documentation"
        version = "1.0.0"
        title = "Cava Media"
        termsOfServices = "http://app-dev.ooica.net"
        contact {
            name = "Contact Us"
            url = "http://app-dev.ooica.net"
            email = "contact@gmail.com"
        }
        license {
            name = "licence under ..."
            url = "http://app-dev.ooica.net"
        }
    }
    schemes = [io.swagger.models.Scheme.HTTP]
    //consumes = ["application/json"]
}

