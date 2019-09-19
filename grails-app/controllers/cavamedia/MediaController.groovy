package cavamedia

import io.swagger.annotations.*

@Api(value = "/api/v1", tags = ["Media"], description = "Media (images and videos)")
class MediaController extends BaseController {

    static namespace = 'v1'

    def postService

    /**
     * Returns geoJson derived from WP_Posts that have coordinates and image or video mime types.
     * The type parameter can be used to filter for images or videos.  If no parameter is supplied,
     * both types are returned.
     * @param type
     * @return a geoJson string
     */
    @ApiOperation(
            value = "Returns geoJson derived from WP_Posts with coordinates",
            nickname = "media",
            produces = "application/json",
            httpMethod = "GET",
            response = java.lang.String.class
    )
    @ApiResponses([
            @ApiResponse(code = 405,
                    message = "Method Not Allowed. Only GET is allowed"),

            @ApiResponse(code = 404,
                    message = "Method Not Found")
    ])
    @ApiImplicitParams([
            @ApiImplicitParam(name = "type",
                    paramType = "query",
                    required = false,
                    value = "Optionally filter by type (video or image).",
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
                params.geoReferenced)

        response.setContentType("text/json")

        if (!posts) {
            render "{[]}"
            return
        }
        render Utilities.buildJson(posts)
    }
}
