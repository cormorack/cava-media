package cavamedia

class DataController extends BaseController {

    private String dataURL

    def index() {

        String parameterURL = "/metadata/parameters"
        String context = getAppContext()

        if (!isProduction()) {
            dataURL = "${context}/files/data.json"
            parameterURL = "${context}/files/parameters.json"
        }

        if (isProduction()) {
            String contextPath = config.server.contextPath
            String domain = contextPath.replaceAll(context, "")
            parameterURL = domain + parameterURL
        }

        [dataURL: dataURL, parameterURL: parameterURL]
    }
}
