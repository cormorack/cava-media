package cavamedia

import grails.test.mixin.TestFor
import grails.test.mixin.Mock
import spock.lang.Specification

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
        response.contentType == "text/json"
    }
}
