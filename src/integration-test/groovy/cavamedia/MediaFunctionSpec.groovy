package cavamedia

import grails.test.mixin.integration.Integration
import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import spock.lang.*

/**
 * See http://www.gebish.org/manual/current/ for more instructions
 */
@Integration
class MediaFunctionSpec extends Specification {

    @Shared RestBuilder rest = new RestBuilder()

    def setup() {
    }

    def cleanup() {
    }

    void "test default api"() {

        when:
        RestResponse resp = rest.get("http://localhost:${serverPort}/media")

        then:
        resp.status == 200
        resp.json.size() > 0
        resp.json.type == 'FeatureCollection'
    }

    void "test non geoReferenced images"() {
        when:
        RestResponse resp = rest.get("http://localhost:${serverPort}/media?type=image&geoReferenced=false&tag=axial&max=1")

        then:
        resp.json.title.size == 1
        resp.json.date.size == 1
        resp.json.excerpt.size == 1
        resp.json.url.size == 1
        resp.json.thumbnail.size == 1
        resp.json.tinyThumbnail.size == 1
        resp.json.type == "image/png" || "image/jpeg" || "image/jpg"
    }

    void "test non geoReferenced videos"() {
        when:
        RestResponse resp = rest.get("http://localhost:${serverPort}/media?type=video&geoReferenced=false&tag=axial&max=1")

        then:
        resp.json.title.size == 1
        resp.json.date.size == 1
        resp.json.excerpt.size == 1
        resp.json.url.size == 1
        resp.json.videoPoster.size == 1
        resp.json.videoURL.size == 1
        resp.json.type == "video/quicktime" || "video/mp4"
    }

    void "test geoReferenced videos"() {
        when:
        RestResponse resp = rest.get("http://localhost:${serverPort}/media?type=video&geoReferenced=true&tag=axial&max=1")

        then:
        resp.json.type == "FeatureCollection"
        resp.json.features.type == ["Feature"]
        resp.json.features.geometry.type == ["Point"]
        resp.json.features.geometry.coordinates.size == 1
        resp.json.features[0].properties.title != null
        resp.json.features[0].properties.date != null
        resp.json.features[0].properties.excerpt != null
        resp.json.features[0].properties.url != null
        resp.json.features[0].properties.videoPoster != null
        resp.json.features[0].properties.videoURL != null
        resp.json.features[0].properties.type == "video/quicktime" || "video/mp4"
    }

    void "test geoReferenced images"() {
        when:
        RestResponse resp = rest.get("http://localhost:${serverPort}/media?type=image&geoReferenced=true&tag=axial&max=1")

        then:
        resp.json.type == "FeatureCollection"
        resp.json.features.type == ["Feature"]
        resp.json.features.geometry.type == ["Point"]
        resp.json.features.geometry.coordinates.size == 1
        resp.json.features[0].properties.title != null
        resp.json.features[0].properties.date != null
        resp.json.features[0].properties.excerpt != null
        resp.json.features[0].properties.url != null
        resp.json.features[0].properties.thumbnail != null
        resp.json.features[0].properties.tinyThumbnail != null
        resp.json.features[0].properties.type == "image/png" || "image/jpeg" || "image/jpg"
    }
}
