<%@ page contentType="text/html;charset=UTF-8" %>
<!doctype html>
<html lang="en">
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <title>Create an Issue</title>
        <asset:stylesheet href="bootstrap.css"/>
        <asset:javascript src="jquery-3.3.1.min.js"/>
    </head>

    <body>
        <div class="container">
            <h2>Submit an Issue</h2>
            <g:form
                    method="post"
                    controller="videoUpload"
                    action="issueWebhook"
                    name="theForm"
                    id="theForm">
                <div class="form-group">
                    <label for="title">Required Title:</label>
                    <input class="form-control" type="text" id="title" name="title" value="" />
                </div>
                <div class="form-group">
                    <label for="title">Required Description:</label>
                    <textarea class="form-control" id="body" name="body" cols="18" rows="4" required=""></textarea>
                </div>
                <div class="form-group">
                    <label>Optional Labels:</label>
                </div>
               <div class="form-check">
                   <input class="form-check-input" type="radio" name="labels" id="Data Portal" value="Data Portal">
                   <label class="form-check-label" for="Data Portal">
                       Data Portal
                   </label>
               </div>
               <div class="form-check">
                   <input class="form-check-input" type="radio" name="labels" id="Website" value="Website">
                   <label class="form-check-label" for="Website">
                       Website
                   </label>
               </div>
               <div class="form-check">
                   <input class="form-check-input" type="radio" name="labels" id="Expeditions" value="Expeditions">
                   <label class="form-check-label" for="Expeditions">
                       Expedition
                   </label>
               </div>
                %{--<div class="form-check">
                    <g:radioGroup
                        class="form-check-input"
                        name="labels"
                        labels="['Data Portal','Website','Expeditions']"
                        values="['Data Portal','Website','Expedition']">
                        ${it.radio} <label class="form-check-label" for="${it.label}">${it.label}</label><br />
                    </g:radioGroup>
                </div>--}%
                <hr />
               <div id="submitbutton">
                   <button type="submit" class="btn btn-secondary" >Submit</button>
               </div>
            </g:form>
            <div id="cover" style="display:none; z-index: 10; text-align: center;">
                <asset:image src="io-load-animation.png" width="200px" height="200px"/>
            </div>
            <div id="output"><!-- error or success results --></div>
        </div>
    <script>
        $("form#theForm").submit(function(e) {
            e.preventDefault();
            var formData = new FormData(this);

            var loading = $("#cover");

            $(document).ajaxStart(function () {
                loading.show();
            });

            $(document).ajaxStop(function () {
                loading.hide();
            });

            $.ajax({
                xhr: function () {
                    var xhr = new window.XMLHttpRequest();
                    xhr.upload.addEventListener('progress', function (e) {
                        if (e.lengthComputable) {
                        }
                    });
                    return xhr;
                },
                type: 'POST',
                url: '${serverURL}/${controllerName}/issueWebhook.json',
                data: formData,
                processData: false,
                contentType: false,
                success: function (data) {
                    $("form#theForm")[0].reset();
                    var videoLink = document.createElement('a');
                    var linkText = document.createTextNode(data.message);
                    videoLink.appendChild(linkText);
                    videoLink.setAttribute('href', data.success);
                    videoLink.setAttribute('target', '_blank');
                    document.getElementById("output").appendChild(videoLink);
                },
                error: function (xhr, status, error) {
                    var errorMessage = xhr.status + ': ' + xhr.statusText;
                    alert('Error - ' + errorMessage);
                }
            });
        });
    </script>
    </body>

</html>