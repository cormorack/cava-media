package cavamedia

import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import io.swagger.v3.oas.annotations.Hidden
import grails.converters.JSON

//@Hidden
@Api(value = "/gallery/", tags = ["Gallery"])
class GalleryController extends BaseController {

    def postService
    def tagService

    /**
     * Renders the gallery page
     */
    @ApiOperation(hidden = true)
    def index() {
        render view: 'image'
    }

    @ApiOperation(hidden = true)
    def map() {}

    /**
     * Renders the gallery page
     */
    @ApiOperation(hidden = true)
    def image() {}

    /**
     * Renders the video page
     */
    @ApiOperation(hidden = true)
    def video() {}

    @ApiOperation(hidden = true)
    def media() {}

    /**
     * Returns videos that DO NOT have images as JSON
     * @return
     */
    @ApiOperation(hidden = true)
    def findVideos() {

        params.geoReferenced = "false"

        setParams(params)

        List videos = postService.getMedia(
                params.max.toInteger(),
                params.offset.toInteger(),
                params.sort,
                params.order,
                params.q,
                params.type,
                params.geoReferenced
        )

        response.setContentType("application/json;charset=UTF-8")

        if (!videos) {
            render "{[]}"
            return
        }

        List videoList = Utilities.buildVideoListNoImages(videos)

        respond videoList
    }

    /**
     * Returns a list of videos formatted as JW Player JSON
     * @return
     */
    @ApiOperation(
            value = "Returns a JSON list of videos formatted for the JW Player",
            nickname = "findAllVideos",
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
                    name = "tag",
                    paramType = "query",
                    required = false,
                    value = "Enables searching by tag slug (i.e. axial-caldera).",
                    dataType = "string")
    ])
    def findAllVideos() {

        params.geoReferenced = "false"
        params.type = "video"

        setParams(params)

        List videos = postService.getMedia(
                params.max.toInteger(),
                params.offset.toInteger(),
                params.sort,
                params.order,
                params.q,
                params.type,
                params.geoReferenced,
                params.tag
        )

        List videoList = []

        if (videos) {
            videoList = Utilities.buildFullVideoList(videos)
        }

        List tagList = tagService.getTags()

        Map dataMap = [
                playlist: videoList,
                total: videos.getTotalCount(),
                offset: params.offset,
                max:params.max,
                tags: tagList
        ]

        render dataMap as JSON
    }

    /**
     * Returns a list of images as JSON
     * @return
     */
    //@ApiOperation(hidden = true)
    @ApiOperation(
            value = "Returns a JSON list of images",
            nickname = "findAllMedia",
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
                    name = "tag",
                    paramType = "query",
                    required = false,
                    value = "Enables searching by tag slug (i.e. axial-caldera).",
                    dataType = "string")
    ])
    def findAllImages() {

        params.geoReferenced = "false"
        params.type = "image"

        setParams(params)

        List images = postService.getMedia(
                params.max.toInteger(),
                params.offset.toInteger(),
                params.sort,
                params.order,
                params.q,
                params.type,
                params.geoReferenced,
                params.tag
        )

        List imageList = []

        if (images) {
            imageList = Utilities.buildImageList(images)
        }

        List tagList = tagService.getTags()

        Map dataMap = [
                images: imageList,
                total: images?.getTotalCount(),
                offset: params.offset,
                max: params.max,
                tags: tagList
        ]

        render dataMap as JSON
    }

    @ApiOperation(hidden = true)
    def findAllMedia() {

        params.geoReferenced = "false"

        setParams(params)

        List images = postService.getMedia(
                params.max.toInteger(),
                params.offset.toInteger(),
                params.sort,
                params.order,
                params.q,
                params.type,
                params.geoReferenced,
                params.tag
        )

        List imageList = []

        if (images) {
            imageList = Utilities.buildMediaList(images)
        }

        List tagList = tagService.getTags()

        Map dataMap = [
                images: imageList,
                total: images?.getTotalCount(),
                offset: params.offset,
                max: params.max,
                tags: tagList
        ]

        render dataMap as JSON
    }
}
