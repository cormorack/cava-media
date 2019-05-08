filesDir = "/files"
streamURL = "stream.ocean.washington.edu"
videoPrefix = "https://${streamURL}:443/rsn/mp4:"
videoSuffix = "/playlist.m3u8"
maxFileSize = 1000 * 1024 * 100 //100 MB
cavaWpPassword = "iRmQcp@YOCrbRxOHgCT9#4ZT"
cavaWpUser = "io"

environments {

    production {
        cavaWpRestUrl = "https://ooica.net/"
        streamUpload = "http://stream.ocean.washington.edu/streamService/saveFile"
    }
    development {
        cavaWpRestUrl = "http://localhost:8888/"
        streamUpload = "http://localhost:8080/Stream/streamService/saveFile"
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

/*grails.naming.entries = ['Cava': [
        type: "javax.sql.DataSource", //required
        auth: "Container", // optional
        description: "Data source for ...", //optional
        //properties for particular type of resource
        url: "jdbc:mysql://localhost:8889/cavaweb?useLegacyDatetimeCode=false&serverTimezone=America/Los_Angeles",
        username: "cavauser",
        password: "iHazN0i0@",
        driverClassName: "com.mysql.cj.jdbc.Driver",
        maxActive: "8", //and so on
        maxIdle: "4"
    ]
]*/

/*dataSource {
    pooled = true
    readOnly = true
    url = "jdbc:mysql://localhost:8889/cavaweb?useLegacyDatetimeCode=false&serverTimezone=America/Los_Angeles"
    driverClassName = "com.mysql.cj.jdbc.Driver"
    jndiName = "java:comp/env/Cava"
}*/

/*dataSource {
    jndiName = "java:comp/env/Cava"
}*/
