package cavamedia

import io.swagger.annotations.*

@Api(value = "/api/v1", tags = ["Media"], description = "Interactive Oceans Media (images and videos)")
class MediaController extends BaseController {

    static namespace = 'v1'

    def postService

    /**
     * Returns json or geoJson derived from WP_Posts that have coordinates and image or video mime types.
     * The type parameter can be used to filter for images or videos.  If no parameter is supplied,
     * both types are returned.
     * @param type
     * @return a geoJson string
     */
    @ApiOperation(
            value = "Returns json or geoJson derived from WP_Posts",
            nickname = "media",
            produces = "application/json",
            httpMethod = "GET",
            response = java.lang.String.class
    )
    @ApiResponses([
            @ApiResponse(
                    code = 405,
                    message = "Method Not Allowed. Only GET is allowed"),

            @ApiResponse(
                    code = 404,
                    message = "Method Not Found")
    ])
    @ApiImplicitParams([
            @ApiImplicitParam(
                    name = "max",
                    paramType = "query",
                    required = false,
                    value = "Max amount for paging. The default and maximum are 100.",
                    dataType = "string"),

            @ApiImplicitParam(
                    name = "offset",
                    paramType = "query",
                    required = false,
                    value = "Offset amount for paging. The default is 0.",
                    dataType = "string"),

            @ApiImplicitParam(
                    name = "sort",
                    paramType = "query",
                    required = false,
                    value = "Result sorting method. It can be either 'date' or 'title'.  The default is 'date'.",
                    dataType = "string"),

            @ApiImplicitParam(
                    name = "order",
                    paramType = "query",
                    required = false,
                    value = "Result ordering method. It can be either 'asc' or 'desc'.  The default is 'asc'.",
                    dataType = "string"),

            @ApiImplicitParam(
                    name = "q",
                    paramType = "query",
                    required = false,
                    value = "Query string for searching.",
                    dataType = "string"),

            @ApiImplicitParam(
                    name = "type",
                    paramType = "query",
                    required = false,
                    value = "Can be used to filter media by type.  Options are 'image' or 'video'.",
                    dataType = "string"),

            @ApiImplicitParam(
                    name = "geoReferenced",
                    paramType = "query",
                    required = false,
                    value = "Option for returning geo-referenced media.  Options are 'true' or 'false'.  The default is true.",
                    dataType = "string"),

            @ApiImplicitParam(
                    name = "tag",
                    paramType = "query",
                    required = false,
                    value = "Enables searching by tag slug (i.e. axial-caldera).",
                    dataType = "string")
    ])
    def index() {

        setParams(params)

        List posts = postService.getMedia(
                params.max.toInteger(),
                params.offset.toInteger(),
                params.sort,
                params.order,
                params.q,
                params.type,
                params.geoReferenced,
                params.tag)

        response.setContentType("application/json;charset=UTF-8")

        if (!posts) {
            render "[]"
            return
        }

        boolean geoRef = params.geoReferenced == "true"

        render Utilities.buildJson(posts, geoRef)
    }

    /**
     *
     * @param id
     * @return
     */
    def summary(Long id) {

        Post post = postService.getPost(id)

        response.setContentType("application/json;charset=UTF-8")

        if (!post) {
            render "[]"
            return
        }

        Long featureId = 0

        Meta featured = post.getMetaValue("_thumbnail_id")

        if (!featured) {
            render "[]"
            return
        }

        featureId = featured.metaValue.toInteger()

        Post featuredMedia = postService.getPost(featureId)

        if (!featuredMedia) {
            render "[]"
            return
        }

        render Utilities.buildJson(post, featuredMedia)
    }
}
