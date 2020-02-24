package cavamedia

class UrlMappings {

    static mappings = {

        "/gallery" {
            controller = "gallery"
            action = "index"
            type = "image"
        }

        "/gallery/image" {
            controller = "gallery"
            action = "image"
            type = "image"
        }

        "/gallery/video" {
            controller = "gallery"
            action = "video"
            type = "video"
        }

        "/docs" {
            controller = "apiDoc"
            action = "getDocuments"
        }

        "/" {
            controller = "media"
            action = "index"
        }

        "/post/summary/$id?"(controller: "media", action: "summary")

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
