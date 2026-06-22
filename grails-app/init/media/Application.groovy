package media

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import groovy.transform.CompileStatic
import org.springframework.retry.annotation.EnableRetry
/*import io.swagger.config.FilterFactory*/
import cavamedia.AccessHiddenSpecFilter

@CompileStatic
@EnableRetry
class Application extends GrailsAutoConfiguration {

    static void main(String[] args) {
        //FilterFactory.setFilter(new AccessHiddenSpecFilter())
        GrailsApp.run(Application, args)
    }
}