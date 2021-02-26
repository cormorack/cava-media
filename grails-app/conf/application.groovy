
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

server {
    contextPath = "/media"
}

environments {

    production {
        cavaWpRestUrl = "https://interactiveoceans.washington.edu/"
        cavaWpPassword = ""
        cavaWpUser = ""
        cavaHost = "interactiveoceans.washington.edu"
        tagURL = "${cavaWpRestUrl}wp-json/wp/v2/tags?per_page=400"

        if(System.properties['grails.serverURL']) {
            server.contextPath = System.properties['grails.serverURL']
        } else {
            server.contextPath = "https://api.interactiveoceans.washington.edu/media"
        }
        println "server.contextPath is ${server.contextPath}"
    }
    development {
        cavaWpRestUrl = "http://localhost:8888/"
        cavaWpPassword = "iRmQcp@YOCrbRxOHgCT9#4ZT"
        cavaWpUser = "io"
        cavaHost = "localhost:8080/media"
        tagURL = "http://localhost:8888/wp-json/wp/v2/tags?per_page=400"

        if(System.properties['grails.serverURL']) {
            server.contextPath = System.properties['grails.serverURL']
        } else {
            server.contextPath = "http://${cavaHost}"
        }
    }
}

swagger {
    info {
        description = "InteractiveOceans Media API Documentation"
        version = "1.0.1"
        title = "InteractiveOceans Media"
        termsOfServices = "https://api.interactiveoceans.washington.edu"
        contact {
            name = "Contact Us"
            url = "https://interactiveoceans.washington.edu/contact/"
        }
        license {
            name = "licence under ..."
            url = "https://api.interactiveoceans.washington.edu"
        }
    }
    schemes = [io.swagger.models.Scheme.HTTPS, io.swagger.models.Scheme.HTTP]
    //consumes = ["application/json"]
}

trustedURLs = ['interactiveoceans.washington.edu', 'api-development.ooica.net', 'localhost:8080']

