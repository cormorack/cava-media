<%@ page contentType="text/html;charset=UTF-8" %>
<g:set var="serverURL" value="${grailsApplication.config.grails.serverURL}" />
<!DOCTYPE html>
<html>

    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <link rel="stylesheet" type="text/css" href="https://unpkg.com/swagger-ui-dist@3/swagger-ui.css" >
    </head>

    <body>
    <h1>Media</h1>
    <a href="${serverURL}/swagger/api.json">/swagger/api.json</a>
    <div id="swagger-ui"></div>
    <script src="https://unpkg.com/swagger-ui-dist@3/swagger-ui-bundle.js"> </script>
    <script type="text/javascript">
        var swaggerURI = '${serverURL}/swagger/api.json';
        window.onload = function() {
            // Swagger-ui configuration goes here.
            // See further: https://github.com/swagger-api/swagger-ui/blob/master/docs/usage/configuration.md
            SwaggerUIBundle({
                deepLinking: true,
                dom_id: '#swagger-ui',
                showExtensions: true,
                showCommonExtensions: true,
                url: swaggerURI
            });
        };
    </script>

    </body>

</html>