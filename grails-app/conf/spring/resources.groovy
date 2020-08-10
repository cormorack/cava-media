import io.swagger.models.SecurityRequirement
import io.swagger.models.Swagger
//import io.swagger.models.auth.ApiKeyAuthDefinition
//import io.swagger.models.auth.In
import swagger.grails.SwaggerCache

def sInfo = {
    description = "InteractiveOceans Media API Documentation"
    version = "1.0.1"
    title = "InteractiveOceans Media"
    termsOfServices = "https://api.interactiveoceans.washington.edu"
    contact {
        name = "Contact Us"
        url = "https://interactiveoceans.washington.edu/contact/"
    }
    license {
        name = "licence under ..."
        url = "https://api.interactiveoceans.washington.edu"
    }
}

// Place your Spring DSL code here
beans = {
    swagger(Swagger) { bean ->
        basePath = grailsApplication.config.server.contextPath ?: null
        //securityDefinitions = ["apiKey": new ApiKeyAuthDefinition("apiKey", In.HEADER)]
        //security = [new SecurityRequirement().requirement("apiKey")]
        //swagger = "2.0"
        info = sInfo
    }

    swaggerCache(SwaggerCache) { bean ->
        swagger = ref('swagger')
    }
}
