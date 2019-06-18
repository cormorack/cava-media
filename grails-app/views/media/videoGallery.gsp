<%@ page contentType="text/html;charset=UTF-8" %>
<g:set var="serverURL" value="${grailsApplication.config.grails.serverURL}" />
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <asset:stylesheet href="bootstrap.css"/>
        <asset:stylesheet href="gallery.css"/>
        <asset:javascript src="jquery-3.3.1.min.js"/>
        <title>Video Gallery</title>
    </head>

    <body>
        <div class="container">
            <g:form method="get" action="${actionName}" controller="${controllerName}" class="navbar-form navbar-left" role="search">
                <div class="form-group">
                    <input class="form-control" type="text" id="q" name="q" value="${params?.q?.encodeAsHTML()}"/>
                    <button class="btn btn-secondary" type="submit">Search</button>
                    <g:link
                        class="btn btn-secondary"
                        role="button" controller="${controllerName}"
                        action="${actionName}">Clear Search Results
                    </g:link>
                </div>
            </g:form>
        </div>
        <div class="container">
            <div id="video-wall"></div>
            <a href="javascript:prevPage()" id="btn_prev" class="btn btn-secondary">Back </a>
            <a href="javascript:nextPage()" id="btn_next" class="btn btn-secondary">Next </a>
        </div>

        <script type="text/javascript" src="https://content.jwplatform.com/libraries/Jq6HIbgz.js"></script>
        <script type="text/javascript">

            var query = getParam('q');
            var max = (getParam('max') != null) ? getParam('max') : 24;
            max = parseInt(max, 10);

            var offset = offset = (getParam('offset') != null) ? getParam('offset') : 0;
            offset = parseInt(offset, 10);

            var total = 0;
            var current_page = 1;
            var serviceURL = '${serverURL}';
            var serviceURI = serviceURL + '/media/findAllVideos.json' + '?max=' + max + '&offset=' + offset;

            if (query) {
                serviceURI = serviceURI  + '&q=' + query;
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
                            total = json.total;
                            generate_grid(json);
                        } else {
                            console.log(httpRequest.statusText);
                        }
                    }
                }
                console.log(serviceURI);
                httpRequest.open('GET', serviceURI, false);
                httpRequest.send();
            })();

            // Render thumbnails into grid layout
            function generate_grid(data) {

                var playlist = data.playlist;

                var counter = 0;

                for (var i = 0; i < playlist.length; i++) {

                    if (i % 4 == 0) {

                        var flexDiv = document.createElement('div');
                        flexDiv.setAttribute('class', 'row-flex-6');
                        document.getElementById('video-wall').appendChild(flexDiv);

                        for (x=0; x < 2; x++ ) {

                            var columnDiv = document.createElement('div');
                            columnDiv.setAttribute('class', 'column');
                            flexDiv.appendChild(columnDiv);

                            var largeThumb = document.createElement('div');
                            largeThumb.setAttribute('class', 'thumb large');

                            var video = playlist[counter];

                            if (video == null) {
                                continue;
                            }

                            fillVideoContent(video, largeThumb);

                            columnDiv.appendChild(largeThumb);

                            counter++;

                            var anotherLargeThumb = document.createElement('div');
                            anotherLargeThumb.setAttribute('class', 'thumb large');

                            video = playlist[counter];

                            if (video == null) {
                                continue;
                            }

                            fillVideoContent(video, anotherLargeThumb);

                            columnDiv.appendChild(anotherLargeThumb);

                            counter++;
                        }
                    }
                }
            }

            function fillVideoContent(video, thumb) {

                var titleText = document.createElement('div');

                titleText.className = 'title-text';
                titleText.innerHTML = video.title + '<br/>' + video.description;
                thumb.appendChild(titleText);
                thumb.setAttribute('id', video.id + 1);
                thumb.style.backgroundImage = "url('" + video.image + "')";

                thumb.addEventListener('click', function(e) {
                    handleActivePlayer(e, video);
                });
            }

            var thumbs = document.querySelectorAll('.thumb');
            var player;

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

            function prevPage() {

                if (current_page == 1) {
                    current_page--;
                    offset = offset - max;
                    changePage(current_page);
                }
            }

            function nextPage() {

                if (current_page < numPages()) {
                    current_page++;
                    offset = offset + max;
                    changePage(current_page);
                }
            }

            function changePage(page) {

                if (page < 1) page = 1;
                if (page > numPages()) page = numPages();

                window.location.href =
                    window.location.protocol +
                    "//" +
                    window.location.host +
                    window.location.pathname +
                    '?' +
                    $.param({'max':max,'offset':offset, 'q':query});
            }

            function numPages() {

                return Math.round( Math.ceil( total / max));
            }

            var btn_next = document.getElementById("btn_next");
            var btn_prev = document.getElementById("btn_prev");

            if (offset == 0) {
                btn_prev.style.visibility = "hidden";
            }

            var maxPlusOffset = parseInt(max, 10) + parseInt(offset, 10);

            if (maxPlusOffset > total) {
                btn_next.style.visibility = "hidden";
            }
        </script>

    </body>

</html>