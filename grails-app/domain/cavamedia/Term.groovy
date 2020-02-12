package cavamedia

class Term {

    String name, slug

    static hasMany = [posts: Post]

    static belongsTo = Post

    static mapping = {
        table "wp_terms"
        version false
        id column: "term_id"
        posts joinTable: [name: "wp_term_relationships", key: 'term_taxonomy_id' ]
        cache true
    }
}
