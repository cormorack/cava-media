<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <asset:stylesheet href="bootstrap.css"/>
        <title>Success</title>
    </head>

    <body>
        <div class="container">
            <g:if test="${flash.message}">
                <div class="alert alert-success" role="alert"><h2>${flash.message}</h2></div>
            </g:if>
            <h3><g:link class="btn btn-secondary" role="button" controller="videoUpload" action="videoForm">Upload Another</g:link> </h3>
        </div>
    </body>
</html>