package cavamedia

import grails.transaction.Transactional

@Transactional(readOnly = true)
class PostService {

    /**
     * Returns a list of Posts from the DB.  Includes parameters for searching and paging.
     * @param max: defaults to 400
     * @param offset: defaults to 0
     * @param sortBy: can be either 'date' or 'title'
     * @param orderBy: can be either 'asc' or 'desc'
     * @return
     */
    List getVideos(Integer max=400, Integer offset=0, String sortBy = "", String orderBy = "", String q="") {

        if (orderBy == "asc") {
            orderBy = "asc"
        }

        else if (orderBy == "desc") {
            orderBy == "desc"
        }

        else if (orderBy != "asc" || orderBy != "desc") {
            orderBy = "asc"
        }

        if (sortBy == "title") {
            sortBy = "title"
        }

        else if (sortBy == "date") {
            sortBy = "date"
        }

        else if (sortBy != "title" || sortBy != "date") {
            sortBy = "date"
        }

        def query = {

            eq("mimeType", "video/quicktime")

            if(q) {
                Post instance = new Post()
                or {
                    instance.searchableProps.each {
                        ilike(it, "%${q}%")
                    }
                }
            }
            order(sortBy, orderBy)
            maxResults(max)
            firstResult(offset)
        }

        List posts = Post.createCriteria().list(['max': max, 'offset': offset], query)
    }

    /**
     *
     * @param type
     * @return
     */
    List getPosts(String type) {

        def posts = Post.withCriteria {

            metas {
                eq("metaKey", "latitude")
                ne("metaValue", "")
            }

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
        }
    }
}
