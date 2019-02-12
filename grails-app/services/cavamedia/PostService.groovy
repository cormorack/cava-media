package cavamedia

import grails.transaction.Transactional

@Transactional(readOnly = true)
class PostService {

    List getPosts(String type) {

        String videoType = "post.mimeType='video/quicktime'"

        String imageType = "post.mimeType='image/png' or post.mimeType='image/jpeg'"

        def query = StringBuilder.newInstance()

        query << "select distinct post from Post post , Meta meta where post.id=meta.post"

        query << " and (meta.metaKey='latitude' and meta.metaValue != '')"

        if (type) {

            if (type == "image") {
                query << " and (${imageType})"
            }

            if (type == "video") {
                query << " and ${videoType}"
            }

        } else {
            query << " and (${imageType} or ${videoType})"
        }

        List posts = Post.executeQuery(query.toString())
    }
}
