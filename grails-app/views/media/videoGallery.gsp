<%@ page contentType="text/html;charset=UTF-8" %>
<g:set var="serverURL" value="${grailsApplication.config.grails.serverURL}" />
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        %{--<asset:stylesheet href="bootstrap.css"/>--}%
        <asset:stylesheet href="gallery.css"/>
        <title>Video Gallery</title>
    </head>

    <body>
        <div class="container">
            <div class="video-wall">
                %{--<div class="header-bar">
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
                    </g:form>--}%
                </div>
                <g:each in="${(0..12)}">
                    <div class="row-flex-6">
                        <div class="column">
                            <div class="thumb large"></div>
                            <div class="row-flex-2">
                                <div class="thumb"></div>
                                <div class="thumb"></div>
                            </div>
                        </div>
                        <div class="column">
                            <div class="row-flex-2">
                                <div class="thumb"></div>
                                <div class="thumb"></div>
                            </div>
                            <div class="thumb large"></div>
                        </div>
                    </div>
                </g:each>
            </div>
        </div>
        <asset:javascript src="jwGallery.js"/>
        <script type="text/javascript">

            var query = getParam('q');
            var serviceURL = '${serverURL}';
            var serviceURI = serviceURL + '/media/findVideos.json';

            if (query) {
                serviceURI = serviceURI + '?q=' + query;
            }

            function getParam(name) {
                if (name = (new RegExp('[?&]' + encodeURIComponent(name) + '=([^&]*)')).exec(location.search))
                    return decodeURIComponent(name[1]);
            }

            // Request playlist data
            (function() {
                var httpRequest = new XMLHttpRequest();
                if (!httpRequest) {
                    return false;
                }
                httpRequest.onreadystatechange = function() {
                    if (httpRequest.readyState === XMLHttpRequest.DONE) {
                        if (httpRequest.status === 200) {
                            var json = JSON.parse(httpRequest.response);
                            getThumbnails(json);
                        } else {
                            console.log(httpRequest.statusText);
                        }
                    }
                }
                httpRequest.open('GET', serviceURI);
                httpRequest.send();
            })();

            // Render thumbnails into grid layout
            var thumbs = document.querySelectorAll('.thumb');
            var player;

            function getThumbnails(data) {

                var playlist = data;

                thumbs.forEach(function(thumb, i) {

                    var video = playlist[i];
                    var titleText = document.createElement('div');

                    titleText.className = 'title-text';
                    titleText.innerHTML = video.title;
                    thumb.appendChild(titleText);
                    thumb.setAttribute('id', video.id + 1);
                    thumb.style.backgroundImage = "url('" + video.image + "')";

                    thumb.addEventListener('click', function(e) {
                        handleActivePlayer(e, video);
                    });
                })
            };

            // On click, destroy existing player, setup new player in target div
            function handleActivePlayer(e, video) {

                var activeDiv = e.target;

                if (player) {
                    player.remove();
                }
                thumbs.forEach(function(thumb) {
                    thumb.classList.remove('active');
                })
                activeDiv.classList.add('active');

                // Chain .play() onto player setup (rather than autostart: true)
                player = jwplayer(activeDiv.id).setup({
                    file: video.file,
                    displaydescription: true
                }).play();

                // Destroy the player and replace with thumbnail
                player.on('complete', function() {
                    player.remove();
                    player = null;
                });
            }
        </script>
    </body>

</html>