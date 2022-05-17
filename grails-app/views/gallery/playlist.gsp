<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>

    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <title>Video Playlist</title>
        <asset:stylesheet href="bootstrap4.css"/>
        <asset:javascript src="jquery-3.3.1.min.js"/>
        <asset:javascript src="jwplayer-8.24.6/jwplayer.js"/>
        <script>jwplayer.key="${grailsApplication.config.jwPlayerKey}";</script>
        <style>
            .jw-state-idle .jw-controlbar {
                display: flex !important;
            }
        </style>
    </head>

    <body>
        <div class="container-image" data-iframe-height>
            <div class="row">
                <div class="col-12">
                    <div id='mediaplayer'></div>
                    <div id="searchResults"></div>
                    <script type="text/javascript">
                        var serviceURL = location.origin + '${context}';
                        var serviceURI = serviceURL + '/gallery/findAllVideos.json';
                        var message = "";
                        <g:if test="${params.tag}">
                            serviceURI = serviceURI + '?tag=${params.tag}';
                        </g:if>
                        $.ajax({
                            type: "GET",
                            dataType: "json",
                            url: serviceURI,
                            success: function (jsonData) {
                                if (jsonData.playlist.length > 0) {
                                    jwplayer('mediaplayer').setup({
                                        width: "100%",
                                        aspectratio: "16:9",
                                        responsive: true,
                                        'playlist': jsonData.playlist,
                                        'controlbar': 'bottom',
                                        'displayMode': 'shelf'
                                    });
                                } else {
                                    var results = document.getElementById('searchResults');
                                    message = '<h3>Your request returned no results</h3>';
                                    results.innerHTML = message;
                                }
                            }
                        });
                    </script>
                </div>
            </div>
        </div>
        <script type="text/javascript" src="https://s3-us-west-2.amazonaws.com/media.ooica.net/js/iframeResizer.contentWindow.min.js" defer></script>
    </body>

</html>