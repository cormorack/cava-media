<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title></title>
    <meta name="layout" content="main" />
</head>

<body>
    <h1>Upload Versions</h1>
    <g:uploadForm method="post" controller="videoUpload" action="uploadVideo">

        <div class="dialog">
            <table>
                <tbody>
                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="title">Title:</label>
                    </td>
                    <td valign="top">
                        <input type="text" id="title" name="title" value=""/>
                    </td>
                </tr>
                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="title">Description:</label>
                    </td>
                    <td valign="top">
                        <input type="text" id="content" name="content" value=""/>
                    </td>
                </tr>
                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="title">Large Video:</label>
                    </td>
                    <td valign="top">
                        <input type="file" name="desktopVideo"/>
                    </td>
                </tr>
                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="title">Small Video:</label>
                    </td>
                    <td valign="top">
                        <input type="file" name="phoneVideo"/>
                    </td>
                </tr>
                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="title">Poster Image:</label>
                    </td>
                    <td valign="top">
                        <input type="file" name="posterImage"/>
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