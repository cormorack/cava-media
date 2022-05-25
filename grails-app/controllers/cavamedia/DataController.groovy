package cavamedia

class DataController extends BaseController {

    def index() {
        [context: getAppContext()]
    }
}
