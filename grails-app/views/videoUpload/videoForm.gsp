<%@ page contentType="text/html;charset=UTF-8" %>
<g:set var="serverURL" value="${grailsApplication.config.grails.serverURL}" />
<!doctype html>
<html lang="en">
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <title>Upload Video</title>
        <asset:stylesheet href="bootstrap.css"/>
        <asset:javascript src="jquery-3.3.1.min.js"/>
    </head>

    <body>
        <div class="container">
            <h2>Upload Videos</h2>
            <g:if test="${flash.invalidToken}">
                <div class="alert alert-danger" role="alert">Don't click the upload more than once!</div>
            </g:if>
            <g:if test="${flash.message}">
                <div class="alert alert-danger" role="alert">${flash.message}</div>
            </g:if>
            <g:uploadForm
                method="post"
                controller="videoUpload"
                action="uploadVideo"
                name="upload"
                id="upload"
                useToken="true">
                <div class="form-group">
                    <label for="title">Required Title:</label>
                    <input class="form-control" type="text" id="title" name="title" value="" required=""/>
                </div>
                <div class="form-group">
                    <label for="title">Required Description:</label>
                    <textarea class="form-control" id="content" name="content" cols="18" rows="4" required=""></textarea>
                </div>
                <div class="form-group">
                    <label for="title">Required Video (Less than 300MB):</label>
                    <input class="form-control-file" type="file" id="desktopVideo" name="desktopVideo" required="" />
                </div>
                <div class="form-group">
                    <label for="title">Required Poster Image:</label>
                    <input class="form-control-file" type="file" id="posterImage" name="posterImage" required=""/>
                </div>
                <div id="submitbutton">
                    <button type="submit" class="btn btn-secondary" >Upload</button>
                </div>
            </g:uploadForm>
            <div id="cover" style="display:none; z-index: 10; text-align: center;">
                <asset:image src="io-load-animation.png" width="200px" height="200px"/>
            </div>
            <div id="output"><!-- error or success results --></div>
        </div>
        <script>
            $("form#upload").submit(function(e) {
                e.preventDefault();
                var formData = new FormData(this);
                var serviceURL = location.origin + '${context}';
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
                    url: serviceURL + '/${controllerName}/uploadVideo.json',
                    data: formData,
                    processData: false,
                    contentType: false,
                    success: function (data) {
                        $("form#upload")[0].reset();
                        var videoLink = document.createElement('a');
                        var linkText = document.createTextNode("Video Link");
                        videoLink.appendChild(linkText);
                        videoLink.setAttribute('href', data.link);
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