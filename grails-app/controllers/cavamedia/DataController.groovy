package cavamedia

import org.springframework.beans.factory.annotation.Value

class DataController extends BaseController {

    @Value('${DATA_URL}')
    private String dataURL

    def index() {

        String parameterURL = "https://api-development.ooica.net/metadata/parameters"

        if (!isProduction()) {
            dataURL = "http://localhost:8080/media/files/data.json"
            parameterURL = "http://localhost:8080/media/files/parameters.json"
        }

        [dataURL: dataURL, parameterURL: parameterURL]
    }
}
