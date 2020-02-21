package cavamedia

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification
import static javax.servlet.http.HttpServletResponse.SC_OK

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(GalleryController)
@Mock([Post, Meta, PostService])
class GalleryControllerSpec extends Specification {

    def setup() {
        //controller.transactionManager = transactionManager
    }

    def cleanup() {
    }

    void "test findVideos"() {
        given:
        controller.postService = Mock(PostService)

        when:
        request.method = 'GET'
        controller.findVideos()

        then:
        response.contentType == "application/json;charset=UTF-8"

        and:
        response.status == SC_OK

        and:
        1 * controller.postService.getMedia(
                100,
                0,
                'date',
                'desc',
                null,
                null,
                'false'
        )
    }

    void "test findAllVideos"() {
        given:
        controller.postService = Mock(PostService)

        when:
        request.method = 'GET'
        Map m = controller.findAllVideos()

        then:
        response.contentType == "application/json;charset=UTF-8"

        and:
        response.status == SC_OK

        and:
        1 * controller.postService.getMedia(
                100,
                0,
                'date',
                'desc',
                null,
                'video',
                'false'
        )
    }

    void "test findAllImages"() {
        given:
        controller.postService = Mock(PostService)

        when:
        request.method = 'GET'
        Map m = controller.findAllVideos()

        then:
        response.contentType == "application/json;charset=UTF-8"

        and:
        response.status == SC_OK

        and:
        1 * controller.postService.getMedia(
                100,
                0,
                'date',
                'desc',
                null,
                'image',
                'false'
        )
    }
}
