package cavamedia

import grails.transaction.Transactional

@Transactional(readOnly = true)
class PostService {

    List getPosts(String type) {

        def posts = Post.withCriteria {

            if (type) {

                if (type == "image") {
                    or {
                        eq("mimeType", "image/png")
                        eq("mimeType", "image/jpeg")
                    }
                }

                if (type == "video") {
                    eq("mimeType", "video/quicktime")
                }

            } else {

                or {
                    eq("mimeType", "video/quicktime")
                    or {
                        eq("mimeType", "image/png")
                        eq("mimeType", "image/jpeg")
                    }
                }
            }

            metas {
                and {
                    eq("metaKey", "latitude")
                    //eq("metaKey", "longitude")
                }
            }
        }
    }
}
