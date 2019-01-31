package cavamedia

class Post {

    Date date

    String title, excerpt, type, mimeType, guid, status

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
     * Returns the associated latitude Meta
     * @return cavamedia.Meta
     */
    Meta getLatitude() {
        Meta.findByMetaKeyAndPost("latitude", this)
    }

    /**
     * Returns the associated longitude Meta
     * @return cavamedia.Meta
     */
    Meta getLongitude() {
        Meta.findByMetaKeyAndPost("longitude", this)
    }

    /**
     * Returns the associated video URL Meta
     * @return cavamedia.Meta
     */
    Meta getVideoURL() {
        Meta.findByMetaKeyAndPost("_jwppp-video-url-1", this)
    }

    /**
     * Returns the associated video poster image Meta
     * @return cavamedia.Meta
     */
    Meta getVideoPoster() {
        Meta.findByMetaKeyAndPost("_jwppp-video-image-1", this)
    }

    /**
     * Returns the associated attached file Meta
     * @return cavamedia.Meta
     */
    Meta getAttachedFile() {
        Meta.findByMetaKeyAndPost("_wp_attached_file", this)
    }
}
