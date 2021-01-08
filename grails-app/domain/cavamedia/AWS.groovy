package cavamedia

class AWS {

    String path, bucket, sourcePath

    Post post

    static mapping = {
        table "wp_as3cf_items"
        version false
        id column: "id"
        post column: "source_id"
        cache true
    }
}
