package cavamedia

class GalleryController extends BaseController {

    def postService

    /**
     * Renders the gallery page
     */
    def index() {
        render view: 'image'
    }

    def map() {}

    /**
     * Renders the gallery page
     */
    def image() {}

    /**
     * Renders the video page
     */
    def video() {}

    /**
     * Returns videos that DO NOT have images as JSON
     * @return
     */
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
                params.geoReferenced
        )

        response.setContentType("application/json;charset=UTF-8")

        if (!videos) {
            render "{[]}"
            return
        }

        List videoList = Utilities.buildFullVideoList(videos)

        Map dataMap = [playlist: videoList, total: videos.getTotalCount(), offset: params.offset, max:params.max]

        respond dataMap
    }

    /**
     * Returns a list of images as JSON
     * @return
     */
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
                params.geoReferenced
        )

        response.setContentType("application/json;charset=UTF-8")

        if (!images) {
            render "{[]}"
            return
        }

        List imageList = Utilities.buildImageList(images)

        Map dataMap = [images: imageList, total: images.getTotalCount(), offset: params.offset, max: params.max]

        respond dataMap
    }
}
