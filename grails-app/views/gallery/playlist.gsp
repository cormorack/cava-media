<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>

    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <asset:stylesheet href="bootstrap4.css"/>
        <asset:javascript src="jquery-3.3.1.min.js"/>
        <asset:javascript src="jwplayer/jwplayer.js"/>
        <title>Video Playlist</title>
        <script>jwplayer.key="mj9LTtvpJ1Dy6DpIHY30xijBS9IQ4QJdGQoKHaS4D7o=";</script>
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
                    <script type="text/javascript">
                        var serviceURL = location.origin + '${context}';
                        var serviceURI = serviceURL + '/gallery/findAllVideos.json?tag=${params.tag}';
                        $.ajax({
                            type: "GET",
                            dataType: "json",
                            url: serviceURI,
                            success: function (jsonData) {
                                jwplayer('mediaplayer').setup({
                                    width: "100%",
                                    aspectratio: "16:9",
                                    responsive: true,
                                    'playlist': jsonData.playlist,
                                    'controlbar': 'bottom',
                                    'displayMode': 'shelf'
                                });
                            }
                        });
                    </script>
                </div>
            </div>
        </div>
        <script type="text/javascript" src="https://s3-us-west-2.amazonaws.com/media.ooica.net/js/iframeResizer.contentWindow.min.js" defer></script>
    </body>

</html>