package cavamedia

import org.springframework.beans.factory.annotation.Value

class DataController extends BaseController {

    @Value('${DATA_URL}')
    private String dataURL

    def index() {

        if (!isProduction()) {
            dataURL = "http://localhost:8081"
        }

        [dataURL: dataURL]
    }
}
