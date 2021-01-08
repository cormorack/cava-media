package cavamedia

import grails.converters.JSON
import grails.plugin.cache.Cacheable
import grails.util.Environment
import grails.util.Holders
import io.swagger.annotations.*
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

import javax.servlet.ServletContext

@Api(value = "/media/", tags = ["Media"])
class MediaController extends BaseController {

    static namespace = 'v1'

    def postService
    def config = Holders.config

    /**
     * Returns a List of json or geoJson objects derived from WP_Posts that have coordinates and image or video mime types.
     * The type parameter can be used to filter for images or videos.  If no parameter is supplied,
     * both types are returned.
     * @param type
     * @return a geoJson string
     */
    @ApiOperation(
            value = "Returns JSON or geoJSON representations of WP_Posts",
            nickname = "api",
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
     * Returns a JSON representation of a WP Post with its associated Featured Media, if it has one
     * @param id
     * @return
     */
    @ApiOperation(
            value = "Returns a JSON representation of a WP Post with its associated Featured Media, if it has one",
            nickname = "api/{id}",
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
            @ApiImplicitParam(name = "id",
                    paramType = "path",
                    required = true,
                    value = "Post Id",
                    dataType = "integer")
    ])
    def summary() {

        Integer id = 0

        if (params.id) {
            id = params?.id?.toInteger()
        }

        if (!id) {
            response.status = 400
            Map error = ["message": "missing id parameter"]
            render error as JSON
        }

        Post post = postService.getPost(id)

        response.setContentType("application/json;charset=UTF-8")

        if (!post) {
            response.status = 404
            Map error = ["message": "Post ${id} not found"]
            render error as JSON
            return
        }

        Long featureId = 0
        Post featuredMedia = null
        Meta featured = post.getMetaValue("_thumbnail_id")

        if (featured) {
            featureId = featured?.metaValue?.toInteger()
            if (featureId) {
                featuredMedia = postService.getPost(featureId)
            }
        }

        render Utilities.buildJson(post, featuredMedia)
    }

    /**
     * Forwards to the docs page.
     */
    @ApiOperation(hidden = true)
    def docs() {
        ServletContext context = getServletContext()
        String apiValue = setApiValue(context)
    }

    /**
     * Looks up the api URL, writes a JSON file to disk and then caches the JSON value as a string.
     * The JSON is modified to replace the Swagger attribute with the Open API attribute and add the info data.
     * The JSON value is then cached so the whole operation only occurs once after the the method is first invoked.
     * @param context
     * @return JSON String value
     */
    @Cacheable('api')
    private String setApiValue(ServletContext context) {

        String api = "{'message':'api not found'}"

        String serverURL = "${url()}/swagger/api.json"

        def slurped = new JsonSlurper().parse(serverURL.toURL())

        if (!slurped) {
            return api
        }

        slurped.remove("swagger")

        slurped << [openapi: "3.0.2"]

        JsonBuilder builder = new JsonBuilder()

        def root = builder.info {
            description "InteractiveOceans Media API Documentation"
            version "1.0.1"
            title"InteractiveOceans Media"
            termsOfServices "https://api.interactiveoceans.washington.edu"
            /*contact {
                name ("Contact Us")
                url ("https://interactiveoceans.washington.edu/contact/")
            }
            license {
                name ("licence under ...")
                url ("https://api.interactiveoceans.washington.edu")
            }*/
        }

        slurped.info = root.info

        api = new JsonBuilder(slurped).toPrettyString()

        File file = new File(context.getRealPath("/files/api.json")).write(api)
    }

    /**
     * Returns the grails.serverURL property based on the runtime environment
     * @return
     */
    private String url() {

        switch (Environment.current) {
            case Environment.DEVELOPMENT:
                return config.grails.serverURL
                break
            case Environment.PRODUCTION:
                return config.grails.serverURL
                break
        }
    }
}

