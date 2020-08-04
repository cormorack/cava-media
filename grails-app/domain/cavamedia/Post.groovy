package cavamedia

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel(description = "Partial representation of a WP Post")
class Post {

    Date date

    @ApiModelProperty(notes = "Title of the Post", name="title", dataType = "String")
    String title
    String excerpt
    String type
    String mimeType
    String guid
    String status
    String content
    String name

    Set<Meta> metas

    Set<AWS> awsSet

    Set<Term> terms

    static hasMany = [metas: Meta, awsSet: AWS, terms: Term]

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
        name column: "post_name"
        terms joinTable: [name: "wp_term_relationships", key: 'object_id' ]
        cache true
    }

    def searchableProps = ['title', 'excerpt', 'type', 'content']

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
