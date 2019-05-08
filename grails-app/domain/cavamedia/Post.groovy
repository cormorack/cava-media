package cavamedia

class Post {

    Date date

    String title, excerpt, type, mimeType, guid, status, content

    Set<Meta> metas

    static hasMany = [metas: Meta]

    static mapping = {
        table "wp_posts"
        version false
        id column: "ID"
        title type: "text", column: "post_title"
        excerpt type: "text", column: "post_excerpt"
        type column: "post_type"
        date column: "post_date"
        mimeType column: "post_mime_type"
        status column: "post_status"
        content column: "post_content"
        cache true
    }

    /**
     * Returns associated Metas that have coordinates
     * @return List
     */
    List<Meta> getGeoMetas() {

        def metas = Meta.withCriteria {
            eq("post", this)
            or {
                eq("metaKey", "latitude")
                eq("metaKey", "longitude")
            }
        }
    }

    /**
     * Finds and returns a Meta by metaKey
     * @param key
     * @return cavamedia.Meta
     */
    Meta getMetaValue(String key) {
        Meta.findByMetaKeyAndPost(key, this)
    }
}
