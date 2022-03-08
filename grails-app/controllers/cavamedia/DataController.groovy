package cavamedia

import org.springframework.beans.factory.annotation.Value

class DataController extends BaseController {

    @Value('${DATA_URL}')
    private String dataURL

    def index() {

        String parameterURL = "/metadata/parameters"
        String context = getAppContext()

        if (!isProduction()) {
            dataURL = "${context}/files/data.json"
            parameterURL = "${context}/files/parameters.json"
        }
        if (isProduction()) {
            parameterURL = "${dataURL}/metadata/parameters"
            dataURL = "${dataURL}/feed"
        }

        [dataURL: dataURL, parameterURL: parameterURL]
    }
}
