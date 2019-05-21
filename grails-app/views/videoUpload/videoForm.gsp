<%@ page contentType="text/html;charset=UTF-8" %>
<!doctype html>
<html lang="en">
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <title>Upload Video</title>
        <asset:stylesheet href="bootstrap.css"/>
        <script type=text/javascript>

            function validate() {

                var desktopVideo = upload.desktopVideo.value;
                var posterImage = upload.posterImage.value;
                var title = upload.title.value;
                var content = upload.content.value;

                if (title == '') {
                    alert('You must enter a title.');
                    event.returnValue = false;
                }
                if (content == '') {
                    alert('You must enter a description.');
                    event.returnValue = false;
                }
                if (desktopVideo == '') {
                    alert('You must select a video.');
                    event.returnValue = false;
                }
                if (posterImage == '') {
                    alert('You must select a poster image.');
                    event.returnValue = false;
                }
            }
        </script>
        <asset:javascript src="jquery-3.3.1.min.js"/>
        <asset:javascript src="jquery-progress-upload.js"/>
    </head>

    <body>
        <div class="container">
            <h2>Upload Videos</h2>
            <g:if test="${flash.invalidToken}">
                <div class="alert alert-danger" role="alert">Don't click the upload button twice!</div>
            </g:if>
            <g:if test="${flash.message}">
                <div class="alert alert-danger" role="alert">${flash.message}</div>
            </g:if>
            <div id="progressTimer"></div>
            <g:uploadForm
                method="post"
                controller="videoUpload"
                action="uploadVideo"
                name="upload"
                useToken="true"
                onsubmit="validate();"
            >
                <div class="form-group">
                    <label for="title">Required Title:</label>
                    <input class="form-control" type="text" id="title" name="title" value=""/>
                </div>
                <div class="form-group">
                    <label for="title">Required Description:</label>
                    <textarea class="form-control" id="content" name="content" cols="18" rows="4"></textarea>
                </div>
                <div class="form-group">
                    <label for="title">Required Video (Less than 100MB):</label>
                    <input class="form-control-file" type="file" id="desktopVideo" name="desktopVideo"/>
                </div>
                <div class="form-group">
                    <label for="title">Required Poster Image:</label>
                    <input class="form-control-file" type="file" id="posterImage" name="posterImage"/>
                </div>
                <div class="form-group">
                    <label for="title">Optional Mobile Video:</label>
                    <input class="form-control-file" type="file" id="phoneVideo" name="phoneVideo"/>
                </div>
                <button type="submit" class="btn btn-secondary">Upload</button>
            </g:uploadForm>
        </div>
        %{--<script>
            var settings = {};
            $('#desktopVideo').setProgressedUploader(settings);
        </script>--}%
    </body>

</html>