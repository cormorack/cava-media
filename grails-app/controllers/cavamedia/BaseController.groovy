package cavamedia

import grails.util.Environment
import grails.util.Holders
import io.swagger.annotations.ApiOperation
import io.swagger.v3.oas.annotations.Hidden
import org.springframework.beans.factory.annotation.Value

@Hidden
class BaseController {

    def config = Holders.config

    @Value('${SECURE}')
    private String isSecure

    @ApiOperation(hidden = true)
    def index() { }

    /**
     * Sets default values for parameters.
     * sort=date, order=desc, offset=0, max=default value in config.properties
     * max cannot be larger than the configuration value, less than 0
     * @param params
     * @return
     */
    protected setParams(params) {

        if (!params.max || !Utilities.isNumeric( params.max.toString() ) || params.max.toInteger() < 0) {
            params.max = config.totalMax
        }
        params.max = Math.min( params.max?.toInteger() ?: 0, config.totalMax.toInteger() )

        if (!params.sort) {
            params.sort = "date"
        }
        if (!params.order) {
            params.order = "desc"
        }
        if (!params.geoReferenced) {
            params.geoReferenced = "true"
        }

        if (!params.offset || !Utilities.isNumeric( params.offset.toString()) ) {
            params.offset = 0
        }
    }

    /**
     * Returns a URL from the servletContext.
     * Checks the runtime environment and the SECURE env_var to determine
     * whether or not to use https.
     * @param remove Optional string to remove from the URL
     * @return URL String
     */
    protected String getURL(String remove) {

        String uri = request.getRequestURL().toString()

        // Force https
        if (isProduction() && isSecure == "true") {
            uri = uri.replaceAll("http:", "https:")
        }

        if (!remove) {
            return uri
        }
        uri = uri.replaceAll(remove, "")
    }

    /**
     * Returns boolean value whether production or not
     * @return boolean value
     */
    protected boolean isProduction() {

        switch (Environment.current) {
            case Environment.DEVELOPMENT:
                return false
                break
            case Environment.PRODUCTION:
                return true
                break
        }
    }

    /**
     * Returns the application context name from the application.yml
     * @return
     */
    protected String getAppContext() {

        String context = grailsApplication.config.getProperty('server.servlet.context-path') ?: ""
    }
}
