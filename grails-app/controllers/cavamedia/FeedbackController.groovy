package cavamedia

import grails.converters.JSON
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.beans.factory.annotation.Value

@Api(value = "/media/feedback/", tags = ["Feedback"])
class FeedbackController extends BaseController {

    def clientService

    static allowedMethods = [save: 'POST']
    static final String ISSUES_URL = "https://api.github.com"
    static final String ISSUES_URI = "/repos/cormorack/feedback/issues"

    @Value('${ISSUES_SECRET}')
    private String issuesPassword

    @ApiOperation(hidden = true)
    def index() {
        render ""
    }

    /**
     * Forwards to issue form
     * @return
     */
    @ApiOperation(hidden = true)
    def create() {
        [context: getAppContext()]
    }

    /**
     * Creates a Github issue
     * @return JSON response
     */
    @ApiOperation(
            value = "Creates a Github Issue",
            nickname = "save",
            consumes = "application/json",
            produces = "application/json",
            httpMethod = "POST"
    )
    @ApiResponses([
            @ApiResponse(
                    code = 200,
                    message = "The POST call was successful"),
            @ApiResponse(
                    code = 405,
                    message = "Method Not Allowed. Only POST is allowed"),
            @ApiResponse(
                    code = 404,
                    message = "Method Not Found")
    ])
    @ApiImplicitParams([
            @ApiImplicitParam(
                    name = "name",
                    paramType = "query",
                    required = true,
                    value = "Name",
                    dataType = "string"),
            @ApiImplicitParam(
                    name = "body",
                    paramType = "query",
                    required = true,
                    value = "Issue Description",
                    dataType = "string"),
            @ApiImplicitParam(
                    name = "email",
                    paramType = "query",
                    required = true,
                    value = "Email Address",
                    dataType = "string"),
            @ApiImplicitParam(
                    name = "labels",
                    allowMultiple = true,
                    paramType = "query",
                    required = true,
                    value = "Issue Label",
                    dataType = "[Ljava.util.List;")
    ])
    def save() {

        if (!params.body || !params.name || !params.email || !params.labels) {

            Map data = ["message": "A required parameter is missing", "data": [] ]
            Map results = ["succes": false, "data": data]
            render results as JSON
            return
        }

        String titleString = "${params.labels} feedback from ${params.name}"

        Map paramMap = [title: titleString]

        List labels = [params.labels]

        paramMap.put("labels", labels)
        paramMap.put("body", setDescription())

        Map headerMap = ['Authorization': "token ${issuesPassword}", 'User-Agent': 'ooi-data-bot']

        if (!clientService.postIssue(ISSUES_URL, ISSUES_URI, paramMap, headerMap)) {

            log.error("An error occurred when submitting an issue")
            Map data = ["message": "The operation could not be completed", "data": [] ]
            Map results = ["succes": false, "data": data]
            render results as JSON
            return
        }

        Map data = ["message": "Your issue has been reported", "data": [] ]
        Map results = ["succes": true, "data": data]

        render results as JSON
    }

    /**
     * Formats and fills the body with the name, email, label and body
     * @param title
     * @return formatted String
     */
    private String setDescription() {

        String bodyString = """\
        ## Overview
        ${params.body}

        ## Details
        Sender: ${params.name}
        Sender email: ${params.email}
        Question type: ${params.labels}
        """.stripIndent()
    }
}
