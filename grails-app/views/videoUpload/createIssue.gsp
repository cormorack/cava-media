<%@ page contentType="text/html;charset=UTF-8" %>
<!doctype html>
<html lang="en">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>Create an Issue</title>
    <asset:stylesheet href="bootstrap.css"/>
    %{--<asset:javascript src="jquery-3.3.1.min.js"/>--}%
</head>

<body>
<div class="container">
    <h2>Submit an Issue</h2>
    <g:if test="${flash.invalidToken}">
        <div class="alert alert-danger" role="alert">Don't click the upload more than once!</div>
    </g:if>
    <g:if test="${flash.message}">
        <div class="alert alert-danger" role="alert">${flash.message}</div>
    </g:if>
    <g:form
            method="post"
            controller="videoUpload"
            action="submitIssue"
            name="theForm"
            id="theForm"
            useToken="true">
        <div class="form-group">
            <label for="title">Required Title:</label>
            <input class="form-control" type="text" id="title" name="title" value="" required=""/>
        </div>
        <div class="form-group">
            <label for="title">Required Description:</label>
            <textarea class="form-control" id="body" name="body" cols="18" rows="4" required=""></textarea>
        </div>
        <div class="form-group">
            <label>Optional Labels:</label>
        </div>
       %{--<div class="form-check">
           <input class="form-check-input" type="radio" name="labels" id="dataPortal">
           <label class="form-check-label" for="dataPortal">
               Data Portal
           </label>
       </div>
       <div class="form-check">
           <input class="form-check-input" type="radio" name="labels" id="website">
           <label class="form-check-label" for="website">
               Website
           </label>
       </div>
       <div class="form-check">
           <input class="form-check-input" type="radio" name="labels" id="expedition">
           <label class="form-check-label" for="expedition">
               Expedition
           </label>
       </div>--}%
        <div class="form-check">
            <g:radioGroup
                class="form-check-input"
                name="labels"
                labels="['Data Portal','Website','Expeditions']"
                values="['Data Portal','Website','Expedition']">
                ${it.radio} <label class="form-check-label" for="${it.label}">${it.label}</label><br />
            </g:radioGroup>
        </div>
        <hr />
       <div id="submitbutton">
           <button type="submit" class="btn btn-secondary" >Submit</button>
       </div>
    </g:form>
</div>
</body>

</html>