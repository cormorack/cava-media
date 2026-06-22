package cavamedia

class ErrorsController {

    static allowedMethods = [index: 'GET', error403: 'GET', error404: 'GET', error500: 'GET', error503: 'GET']

    def index() {
        response.setContentType("application/json;charset=UTF-8")
        render(status: 500, text: [error: "Something went wrong"] as grails.converters.JSON, contentType: "application/json")
    }

    def error403() {
        render(view: "/error", status: 403)
    }

    def error404() {
        render(view:"/notFound", status: 404)
    }

    def error500() {
        response.setContentType("application/json;charset=UTF-8")
        render(status: 500, text: [error: "Something went wrong"] as grails.converters.JSON, contentType: "application/json")
    }

    def error503() {
        response.setContentType("application/json;charset=UTF-8")
        render(status: 503, text: [error: "Server is busy"] as grails.converters.JSON, contentType: "application/json")
    }

}
