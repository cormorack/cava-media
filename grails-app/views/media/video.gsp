<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <asset:stylesheet href="bootstrap.css"/>
        <asset:javascript src="jquery-3.3.1.min.js"/>
        <asset:javascript src="jwplayer.js"/>
        <title>Video Gallery</title>
    </head>

    <body>
        <div class="container">
            <div id="mediaplayer"></div>
            <div class="jumbotron">
                <g:form method="get" action="${actionName}" controller="${controllerName}" class="form-signin">
                    <div class="form-group">
                        <div class="col-lg-9">
                            <input class="form-control" type="text" id="q" name="q" value="${params?.q?.encodeAsHTML()}"/>
                        </div>
                        <div class="col-lg-3">
                            <button class="btn btn-secondary" type="submit">Search</button>
                            <g:link class="btn btn-secondary" role="button" controller="${controllerName}" action="${actionName}">Clear Search</g:link>
                        </div>
                    </div>
                </g:form>
            </div>
        </div>
        <script>

            var query = getParam('q');
            var serviceURL = 'http://localhost:8080/';
            var serviceURI = serviceURL + 'media/findVideos.json';

            if (query) {
                serviceURI = serviceURI + '?q=' + query;
            }

            function getParam(name) {
                if (name = (new RegExp('[?&]' + encodeURIComponent(name) + '=([^&]*)')).exec(location.search))
                    return decodeURIComponent(name[1]);
            }

            $.getJSON(serviceURI, function(data) {

                jwplayer('mediaplayer').setup({
                    'id': 'playerID',
                    androidhls: true,
                    displaydescription: true,
                    width: "100%",
                    aspectratio: "16:9",
                    'playlist': data,
                    //'controlbar': 'bottom',
                    listbar: {
                        position: "right",
                        size: 340
                    }
                });
            });
        </script>
    </body>

</html>