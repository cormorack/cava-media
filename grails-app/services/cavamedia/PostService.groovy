package cavamedia

//import grails.transaction.Transactional
import grails.gorm.transactions.Transactional

import grails.util.Holders
import org.hibernate.FetchMode as FM

@Transactional(readOnly = true)
class PostService {

    def config = Holders.config

    Integer maxPerPage = config.maxPerPage

    /**
     * Looks up and returns a Post by ID
     * @param id
     * @return
     */
    Post getPost(Long id) {
        Post post = Post.get(id)
    }

    Post getFeaturedMedia(Long postId) {

        /*def idQuery = Post.where {
            id == postId
            metas { id == idQuery && metaKey == "_thumbnail_id" }
        }
        def metaQuery = idQuery.where {
            metas { id == idQuery && metaKey == "_thumbnail_id" }
        }
        Post p = idQuery.find()*/
    }

    /**
     * Returns a list of Posts from the DB.  Includes parameters for searching, paging, and sorting.
     * @param max: defaults to the configuration value
     * @param offset: defaults to 0
     * @param sortBy: can be either 'date' or 'title'
     * @param orderBy: can be either 'asc' or 'desc'
     * @param q: query string
     * @param type: can be either video or image
     * @param geoReferenced: can be either true or false
     * @param tag: filter by tag
     * @return java.util.List
     */
    List getMedia(
            Integer max = maxPerPage,
            Integer offset = 0,
            String sortBy = "date",
            String orderBy = "asc",
            String q = "",
            String type = "",
            String geoReferenced = "true",
            String tag = ""
        ) {

        orderBy = setOrderBy(orderBy)

        sortBy = setSortBy(sortBy)

        geoReferenced = setGeoReferenced(geoReferenced)

        def query = {
            if (geoReferenced == "true") {
                metas {
                    eq("metaKey", "latitude")
                    ne("metaValue", "")
                }
            }
            if (type) {
                if (type == "image") {
                    or {
                        eq("mimeType", "image/png")
                        eq("mimeType", "image/jpeg")
                        eq("mimeType", "image/jpg")
                    }
                    /*imageOrClause.delegate = delegate
                    imageOrClause.call()*/
                }
                if (type == "video") {
                    or {
                        eq("mimeType", "video/quicktime")
                        eq("mimeType", "video/mp4")
                    }
                    /*videoOrClause.delegate = delegate
                    videoOrClause.call()*/
                }
            } else {
                or {
                    or {
                        eq("mimeType", "video/quicktime")
                        eq("mimeType", "video/mp4")
                    }
                    /*videoOrClause.delegate = delegate
                    videoOrClause.call()*/
                    or {
                        eq("mimeType", "image/png")
                        eq("mimeType", "image/jpeg")
                        eq("mimeType", "image/jpg")
                    }
                    /*imageOrClause.delegate = delegate
                    imageOrClause.call()*/
                }
            }
            if (q) {
                Post instance = new Post()
                or {
                    instance.searchableProps.each {
                        ilike(it, "%${q}%")
                    }
                }
            }
            if (tag) {
                terms {
                    eq("slug", tag)
                }
            }
            /*metas {
                fetchMode("post", FM.JOIN)
            }
            awsSet {
                fetchMode("post", FM.JOIN)
            }*/
            order(sortBy, orderBy)
            maxResults(max)
            firstResult(offset)
        }

        List posts = Post.createCriteria().list(['max': max, 'offset': offset], query)
    }

    def imageOrClause = {
        return {
            or {
                eq("mimeType", "image/png")
                eq("mimeType", "image/jpeg")
                eq("mimeType", "image/jpg")
            }
        }
    }

    def videoOrClause = {
        return {
            or {
                eq("mimeType", "video/quicktime")
                eq("mimeType", "video/mp4")
            }
        }
    }

    /**
     *
     * @param geoReferenced
     * @return
     */
    private String setGeoReferenced(String geoReferenced) {

        if (geoReferenced == "true") geoReferenced = "true"

        else if (geoReferenced == "false") geoReferenced = "false"

        else if (geoReferenced != "true" || geoReferenced != "false") geoReferenced = "true"

        return geoReferenced
    }

    /**
     *
     * @param orderBy
     * @return
     */
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
        return orderBy
    }

    /**
     *
     * @param sortBy
     * @return
     */
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
        return sortBy
    }
}
