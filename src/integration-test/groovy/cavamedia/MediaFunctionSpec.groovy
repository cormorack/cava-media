package cavamedia

import grails.test.mixin.integration.Integration
import grails.transaction.*
import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse

import spock.lang.*
import geb.spock.*

/**
 * See http://www.gebish.org/manual/current/ for more instructions
 */
@Integration
@Rollback
class MediaFunctionSpec extends Specification {

    @Shared RestBuilder rest = new RestBuilder()

    def setup() {
    }

    def cleanup() {
    }

    void "test api"() {

        when:
        RestResponse resp = rest.get("http://localhost:${serverPort}/api/v1/media?type=image")

        then:
        resp.status == 200
        resp.json.size() > 0
        //resp.json.find { it.type == 'FeatureCollection'}
    }
}
