package cavamedia

import grails.transaction.Transactional
import grails.util.Holders

@Transactional(readOnly = true)
class PostService {

    def config = Holders.config

    Integer maxPerPage = config.maxPerPage

    /**
     * Returns a list of Posts from the DB.  Includes parameters for searching and paging.
     * @param max: defaults to the configuration value
     * @param offset: defaults to 0
     * @param sortBy: can be either 'date' or 'title'
     * @param orderBy: can be either 'asc' or 'desc'
     * @param q: query string
     * @param type: video or image
     * @return java.util.List
     */
    List getVideos(
            Integer max=maxPerPage,
            Integer offset=0,
            String sortBy = "",
            String orderBy = "",
            String q="",
            String type="") {

        /*orderBy = setOrderBy(orderBy)

        sortBy = setSortBy(sortBy)*/

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

            if (type) {

                if (type == "image") {
                    or {
                        eq("mimeType", "image/png")
                        eq("mimeType", "image/jpeg")
                    }
                }

                if (type == "video") {
                    or {
                        eq("mimeType", "video/quicktime")
                        eq("mimeType", "video/mp4")
                    }
                }

            } else {

                or {
                    or {
                        eq("mimeType", "video/quicktime")
                        eq("mimeType", "video/mp4")
                    }
                    or {
                        eq("mimeType", "image/png")
                        eq("mimeType", "image/jpeg")
                    }
                }
            }

            /*or {
                eq("mimeType", "video/quicktime")
                eq("mimeType", "video/mp4")
            }*/

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
                    or {
                        eq("mimeType", "video/quicktime")
                        eq("mimeType", "video/mp4")
                    }
                }

            } else {

                or {
                    or {
                        eq("mimeType", "video/quicktime")
                        eq("mimeType", "video/mp4")
                    }
                    or {
                        eq("mimeType", "image/png")
                        eq("mimeType", "image/jpeg")
                    }
                }
            }
        }
    }

    private String setOrderBy(String orderBy) {

        if (orderBy == "asc") {
            orderBy = "asc"
        }

        else if (orderBy == "desc") {
            orderBy == "desc"
        }

        else if (orderBy != "asc" || orderBy != "desc") {
            orderBy = "asc"
        }
    }

    private String setSortBy(String sortBy) {

        if (sortBy == "title") {
            sortBy = "title"
        }

        else if (sortBy == "date") {
            sortBy = "date"
        }

        else if (sortBy != "title" || sortBy != "date") {
            sortBy = "date"
        }
    }
}
