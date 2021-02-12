package cavamedia

import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient

class ClientService {

    /**
     * Makes an http POST with the specified url, uri, parameters and headers
     * @param url
     * @param uriString
     * @param paramMap
     * @param headerMap
     * @return boolean value (true if CREATED)
     */
    boolean postIssue(String url, String uriString, Map paramMap, Map headerMap) {

        boolean success = true

        HttpClient client = HttpClient.create(url.toURL())

        HttpRequest request = HttpRequest.POST(uriString, paramMap).headers(headerMap)

        try {
            HttpResponse<Map> resp = client.toBlocking().exchange(request, Map)
            if (!resp || resp.status != HttpStatus.CREATED) {
                log.error("An error occurred when submitting an issue")
                success = false
            }
        } catch (Exception e) {
            log.error("Error occurred when posting the issue is ${e}")
            success = false
        }
        return success
    }
}
