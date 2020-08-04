package cavamedia

import io.swagger.annotations.ApiOperation
import io.swagger.v3.oas.annotations.Hidden

@Hidden
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
    @ApiOperation(hidden = true)
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

        response.setContentType("application/json;charset=UTF-8")

        List videoList = []

        if (videos) videoList = Utilities.buildFullVideoList(videos)

        List tagList = tagService.getTags()

        Map dataMap = [
                playlist: videoList,
                total: videos.getTotalCount(),
                offset: params.offset,
                max:params.max,
                tags: tagList
        ]

        respond dataMap
    }

    /**
     * Returns a list of images as JSON
     * @return
     */
    @ApiOperation(hidden = true)
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

        response.setContentType("application/json;charset=UTF-8")

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

        respond dataMap
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

        response.setContentType("application/json;charset=UTF-8")

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

        respond dataMap
    }
}
