package cavamedia

class GalleryController extends BaseController {

    def postService

    /**
     * Renders the gallery page
     */
    def index() {
        render view: 'image'
    }

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

        response.setContentType("text/json")

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

        response.setContentType("text/json")

        if (!videos) {
            render "{[]}"
            return
        }

        List videoList = Utilities.buildFullVideoList(videos)

        Map paramMap = [playlist: videoList, total: videos.getTotalCount(), offset: params.offset, max:params.max]

        respond paramMap
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

        response.setContentType("text/json")

        if (!images) {
            render "{[]}"
            return
        }

        List imageList = Utilities.buildImageList(images)

        Map paramMap = [images: imageList, total: images.getTotalCount(), offset: params.offset, max: params.max]

        respond paramMap
    }
}
