
//import grails.util.Environment

filesDir = "/files"

streamURL = "stream.ocean.washington.edu"

videoPrefix = "https://${streamURL}:443/rsn/mp4:"

videoSuffix = "/playlist.m3u8"

maxFileSize = 314572800  //100 MB 100 * 1024 * 1024

maxImageSize = 52428800  //50 MB 1000 * 1024 * 5

fileTypes = ['mov', 'mp4', 'm4v', 'jpg', 'jpeg', 'png', 'gif']

maxPerPage = 8

totalMax = 100

//https://media-dev.ooica.net/CavaMedia

if(System.properties['lambdaURL']) {
    lambdaURL = System.properties['lambdaURL']
} else {
    lambdaURL = "https://cava-tiles.ooica.net"
}

environments {

    production {
        cavaWpRestUrl = "https://interactiveoceans.washington.edu/"
        cavaWpPassword = ""
        cavaWpUser = ""
        cavaHost = "interactiveoceans.washington.edu"
        if(System.properties['grails.serverURL']) {
            grails.serverURL = System.properties['grails.serverURL']
        } else {
            grails.serverURL = "https://media.interactiveoceans.washington.edu/CavaMedia"
        }
        println "serverURL is ${grails.serverURL}"
    }
    development {
        cavaWpRestUrl = "http://localhost:8888/"
        cavaWpPassword = "iRmQcp@YOCrbRxOHgCT9#4ZT"
        cavaWpUser = "io"
        cavaHost = "localhost:8080"
        if(System.properties['grails.serverURL']) {
            grails.serverURL = System.properties['grails.serverURL']
        } else {
            grails.serverURL = "http://${cavaHost}"
        }
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

