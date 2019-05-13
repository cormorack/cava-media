<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <title></title>
    </head>

    <body>
        <g:if test="${flash.message}">
            <h2>${flash.message}</h2>
        </g:if>
        <h3><g:link controller="videoUpload" action="videoForm">Upload Another</g:link> </h3>
    </body>
</html>