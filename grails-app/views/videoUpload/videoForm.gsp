<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Upload Video</title>
    <script type=text/javascript>

        function validate() {

            var desktopVideo = upload.desktopVideo.value;
            var posterImage = upload.posterImage.value;
            var title = upload.title.value;
            var content = upload.content.value;

            if (desktopVideo == '') {
                alert('You must select a video.');
                event.returnValue = false;
            }
            if (posterImage == '') {
                alert('You must select a poster image.');
                event.returnValue = false;
            }
            if (title == '') {
                alert('You must enter a title.');
                event.returnValue = false;
            }
            if (content == '') {
                alert('You must enter a description.');
                event.returnValue = false;
            }
        }
    </script>
</head>

<body>
    <h1>Upload Video</h1>
    <g:uploadForm method="post" controller="videoUpload" action="uploadVideo" name="upload" onsubmit="validate();">
        <div class="dialog">
            <table>
                <tbody>
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="title">Required Title:</label>
                        </td>
                        <td valign="top">
                            <input type="text" id="title" name="title" value=""/>
                        </td>
                    </tr>
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="title">Required Description:</label>
                        </td>
                        <td valign="top">
                            <textarea id="content" name="content" cols="18" rows="4">

                            </textarea>
                        </td>
                    </tr>
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="title">Required Video:</label>
                        </td>
                        <td valign="top">
                            <input type="file" name="desktopVideo"/>
                        </td>
                    </tr>
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="title">Required Poster Image:</label>
                        </td>
                        <td valign="top">
                            <input type="file" name="posterImage"/>
                        </td>
                    </tr>
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="title">Optional Mobile Video:</label>
                        </td>
                        <td valign="top">
                            <input type="file" name="phoneVideo"/>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div class="buttons">
            <span class="button"><input class="save" type="submit" value="Upload" /></span>
        </div>
    </g:uploadForm>
    </body>

</html>