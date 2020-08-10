<%@ page contentType="text/html;charset=UTF-8" %>
<g:set var="serverURL" value="${grailsApplication.config.grails.serverURL}" />
<!DOCTYPE html>
<html>

    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <link rel="stylesheet" type="text/css" href="https://unpkg.com/swagger-ui-dist@3/swagger-ui.css" >
        <style>
            html {
                box-sizing: border-box;
                overflow: -moz-scrollbars-vertical;
                overflow-y: scroll;
            }
            *,
            *:before,
            *:after {
                box-sizing: inherit;
            }
            body {
                margin:0;
                background: #fafafa;
            }
        </style>
    </head>

    <body>
        <div id="swagger-ui"></div>
        <asset:javascript src="jquery-3.3.1.min.js"/>
        <script src="https://unpkg.com/swagger-ui-dist@3/swagger-ui-bundle.js"> </script>
        <script type="text/javascript">

            var data = {};
            var swaggerURI = '${serverURL}/swagger/api.json';

            var newSpec = {
                "openapi": "3.0.2",
                "info": {
                    "title": "Media Service",
                    "description": "Media service for Interactive Oceans.",
                    "version": "1.0"
                }
            };

            window.onload = function() {
                $.ajax({
                    type: "GET",
                    dataType: "json",
                    url: swaggerURI,
                    success: function (jsonData) {
                        data = jsonData;
                        delete data["swagger"];
                        $.extend(data, newSpec);
                        SwaggerUIBundle({
                            deepLinking: true,
                            dom_id: '#swagger-ui',
                            showExtensions: true,
                            showCommonExtensions: true,
                            spec: data
                        });
                    }
                });
            };
        </script>
    </body>

</html>