package cavamedia

import grails.test.mixin.TestFor
import grails.test.mixin.Mock
import spock.lang.Specification
import static javax.servlet.http.HttpServletResponse.SC_OK

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(MediaController)
@Mock([Post, Meta, PostService])
class MediaControllerSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test index response type"() {
        when:
        request.method = 'GET'
        controller.index()

        then:
        response.contentType == "application/json;charset=UTF-8"
    }

    void "test index"() {
        given:
        controller.postService = Mock(PostService)

        when:
        request.method = 'GET'
        String m = controller.index()

        then:
        response.contentType == "application/json;charset=UTF-8"

        and:
        response.status == SC_OK

        and:
        1 * controller.postService.getMedia(
                100, 0, 'date', 'desc', null, null, 'true', null
        )
    }
}
