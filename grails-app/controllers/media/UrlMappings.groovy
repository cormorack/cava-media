package media

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

        "/gallery/media" {
            controller = "gallery"
            action = "media"
        }

        "/docs" {
            controller = "media"
            action = "docs"
        }

        "/" {
            controller = "media"
            action = "docs"
        }

        "/post/summary/$id?"(controller: "media", action: "summary")

        "/media/api/v1/media"(controller: "media", action: "index", method: "GET")

        "/media/api/v1/media/$id"(controller: "media", action: "summary", method: "GET")

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

