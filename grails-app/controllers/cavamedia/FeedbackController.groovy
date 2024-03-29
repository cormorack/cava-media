package cavamedia

import grails.converters.JSON

import io.swagger.annotations.ApiOperation

import org.apache.tika.langdetect.OptimaizeLangDetector
import org.apache.tika.language.detect.LanguageDetector

import org.springframework.beans.factory.annotation.Value

class FeedbackController extends BaseController {

    def clientService

    static allowedMethods = [save: 'POST']
    static final String ISSUES_URL = "https://api.github.com"
    static final String ISSUES_URI = "/repos/cormorack/feedback/issues"

    @Value('${ISSUES_SECRET}')
    private String issuesPassword

    @Value('${FEEDBACK_HOST}')
    private String feedbackHost

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
    @ApiOperation(hidden = true)
    def save() {

        if (isProduction()) {

            String host = request.getHeader("HOST")

            if (host != feedbackHost) {

                log.error("Illegal access by an unauthorized host was attempted.")
                log.error("Host is ${host}")
                response.sendError(403)
                return
            }
        }

        String name = ""
        String description = ""
        String email = ""
        String labels = ""
        String parameters = ""

        if (request.format == "json") {
            def jsonObj = request.JSON
            name = jsonObj.Name
            description = jsonObj.Description
            email = jsonObj.Email
            labels = jsonObj.Labels
            parameters = jsonObj
        }
        else {
            name = params.Name
            description = params.Description
            email = params.Email
            labels = params.Labels
            parameters = params
        }

        if (!description || !name || !email || !labels) {

            log.info("A request was rejected because of missing parameters")

            Map data = ["message": "A required parameter is missing", "data": [] ]
            Map results = ["succes": false, "data": data]
            render results as JSON
            return
        }

        if (!acceptValues(description, email)) {

            log.info("A request was rejected because of unwanted data")

            Map data = ["message": "Data is invalid", "data": [] ]
            Map results = ["succes": false, "data": data]
            render results as JSON
            return
        }

        String titleString = "${labels} feedback from ${name}"

        Map paramMap = [title: titleString]

        List labelList = [labels]

        paramMap."labels" = labelList
        paramMap."assignees" = setAssignees(labelList)
        paramMap.put("body", setDescription( cleanHtml(description, 'none'), name, email, labels))

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
     * Adds assignees to a List depending on the value of labels
     * @param labels
     * @return List of assignees
     */
    private List setAssignees(List labels) {

        List assignees = []

        for (String label in labels) {

            if (label.equalsIgnoreCase("Website")) {
                assignees.add("sdthomas69")
                assignees.add("hunterhad")

            } else if (label.equalsIgnoreCase("Data Portal")) {
                assignees.add("lsetiawan")
                assignees.add("dwinasolihin")
                assignees.add("mvardaro")

            } else if (label.equalsIgnoreCase("Expedition")) {
                assignees.add("mvardaro")

            } else if (label.equalsIgnoreCase("Proposal")) {
                assignees.add("dskelley")
            }
        }
        return assignees
    }

    /**
     * Formats and fills the body with the name, email, label and description
     * @param description
     * @param name
     * @param email
     * @param labels
     * @return
     */
    private String setDescription(String description, String name, String email, String labels) {

        String bodyString = """\
        ## Overview
        ${description}

        ## Details
        Sender: ${name}
        Sender email: ${email}
        Question type: ${labels}
        """.stripIndent()
    }

    /**
     * Determines whether or not to accept the values based on language or email domain.
     * @param lang a String of text
     * @param email The email address
     * @return boolean value
     */
    private boolean acceptValues(String lang, String email) {

        if (detectLanguage(lang) != "en") {
            return false
        }
        if (excludeEmail(email)) {
            return false
        }
        return true
    }

    /**
     * Evaluates the email domain
     * @param email The email address
     * @return boolean value
     */
    private boolean excludeEmail(String email) {
        return email.contains(".ru")
    }

    /**
     * Determines the language
     * @param text String of text
     * @return a language ISO code or 'no' if it can't be determined
     */
    private String detectLanguage(String text) {
        LanguageDetector detector = new OptimaizeLangDetector().loadModels()
        detector.addText(text)
        return detector.detect().getLanguage()
    }

}
