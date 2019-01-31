package cavamedia

class UrlMappings {

    static mappings = {

        /*"/images" {
            controller = "media"
            action = "index"
            type = "image"
        }

        "/videos" {
            controller = "media"
            action = "index"
            type = "video"
        }*/

        "/" {
            controller = "apiDoc"
            action = "getDocuments"
        }

        "/api/v1/media"(controller: "media", action: "index", method: "GET")

        "/apidoc/$action?/$id?"(controller: "apiDoc", action: "getDocuments")

        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
