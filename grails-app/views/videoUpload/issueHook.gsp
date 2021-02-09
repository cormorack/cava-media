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
            <h2>Report an Issue</h2>
            <g:form
                    method="post"
                    controller="videoUpload"
                    action="issueWebhook"
                    name="theForm"
                    id="theForm">
                <div class="form-group">
                    <label for="name">Name:</label>
                    <input class="form-control" type="text" id="name" name="name" value="" required="" maxlength="50"/>
                </div>
                <div class="form-group">
                    <label for="name">Email:</label>
                    <input class="form-control" type="text" id="email" name="email" value="" required=""/>
                </div>
                <div class="form-group">
                    <label for="body">Description:</label>
                    <textarea class="form-control" id="body" name="body" cols="18" rows="4" required="" maxlength="500"></textarea>
                </div>
                <div class="form-group">
                    <label>Labels:</label>
                </div>
                <div class="form-check">
                    <input class="form-check-input" type="radio" name="labels" id="Website" value="Website" checked>
                    <label class="form-check-label" for="Website">
                        Website
                    </label>
                </div>
                <div class="form-check">
                    <input class="form-check-input" type="radio" name="labels" id="Data Portal" value="Data Portal">
                    <label class="form-check-label" for="Data Portal">
                        Data Portal
                    </label>
                </div>
                <div class="form-check">
                    <input class="form-check-input" type="radio" name="labels" id="Expeditions" value="Expeditions">
                    <label class="form-check-label" for="Expeditions">
                        Expedition
                    </label>
                </div>
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
                url: serviceURL + '/${controllerName}/issueWebhook.json',
                data: formData,
                processData: false,
                contentType: false,
                success: function (data) {
                    $("form#theForm")[0].reset();
                    var para = document.createElement('p');
                    var paraText = document.createTextNode("Thank you for your submission.");
                    para.appendChild(paraText);
                    document.getElementById("output").appendChild(para);
                    alert(data.data.message);
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