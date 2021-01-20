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

        "/gallery/images"(controller: "gallery", action: "findAllImages", method: "GET")

        "/media/gallery/images"(controller: "gallery", action: "findAllImages", method: "GET")

        "/gallery/video" {
            controller = "gallery"
            action = "video"
            type = "video"
        }

        "/gallery/videos"(controller: "gallery", action: "findAllVideos", method: "GET")

        "/media/gallery/videos"(controller: "gallery", action: "findAllVideos", method: "GET")

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
            action = "index"
        }

        "/issueWebhook"(controller: "videoUpload", action: "issueWebhook")

        "/post/summary/$id?"(controller: "media", action: "summary")

        "/api"(controller: "media", action: "index", method: "GET")

        "/api/$id"(controller: "media", action: "summary", method: "GET")

        "/media/api"(controller: "media", action: "index", method: "GET")

        "/media/api/$id"(controller: "media", action: "summary", method: "GET")

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

