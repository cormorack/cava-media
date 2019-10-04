<%@ page contentType="text/html;charset=UTF-8" %>
<g:set var="serverURL" value="${grailsApplication.config.grails.serverURL}" />

<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        %{--<asset:stylesheet href="bootstrap.css"/>--}%
        %{--<asset:stylesheet href="gallery.css"/>--}%
        <link type="text/css" href="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.1.3/css/bootstrap.min.css" rel="stylesheet">
        <asset:stylesheet href="gallery2.css"/>
        <asset:javascript src="jquery-3.3.1.min.js"/>
        <title>Image Gallery</title>
    </head>

    <body>
        <div class="container">
            <div class="row">
                <div class="col-12">
                    <g:form method="get" action="${actionName}" controller="${controllerName}" class="navbar-form navbar-left" role="search">
                        <div class="input-group">
                            <input class="form-control border-secondary py-2" type="text" id="q" name="q" value="${params?.q?.encodeAsHTML()}"/>
                            <div class="input-group-append">
                                <button class="btn btn-outline-secondary" type="submit">Search</button>
                                <g:link
                                    class="btn btn-secondary"
                                    role="button" controller="${controllerName}"
                                    action="${actionName}">Clear
                                </g:link>
                            </div>
                        </div>
                    </g:form>
                </div>
            </div>
        </div>

        <div class="container" data-iframe-height>
            %{--<div class="row" id="gallery" data-toggle="modal" data-target="#exampleModal"></div>--}%
            <div class="row" id="gallery" data-toggle="modal" data-target="#exampleModal"></div>

            <!-- Modal -->

            <div class="modal fade" id="exampleModal" tabindex="-1" role="dialog" aria-hidden="true">
                <div class="modal-dialog modal-full" role="document">
                    <div class="modal-content">
                        <div class="modal-header">
                            %{--<button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>--}%
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                        <div class="modal-body">
                            <div id="carouselExample" class="carousel slide" data-ride="carousel">
                                <ol id="carouseList" class="carousel-indicators"></ol>
                                <div id="carouselModal" class="carousel-inner"></div>
                                <a class="carousel-control-prev" href="#carouselExample" role="button" data-slide="prev">
                                    <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                                    <span class="sr-only">Previous</span>
                                </a>
                                <a class="carousel-control-next" href="#carouselExample" role="button" data-slide="next">
                                    <span class="carousel-control-next-icon" aria-hidden="true"></span>
                                    <span class="sr-only">Next</span>
                                </a>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                        </div>
                    </div>
                </div>
            </div>
            <a href="javascript:prevPage()" id="btn_prev" class="btn btn-secondary">Back </a>
            <a href="javascript:nextPage()" id="btn_next" class="btn btn-secondary">Next </a>
        </div>

        <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js"></script>
        <script type="text/javascript">

            var query = getParam('q');
            var max = (getParam('max') != null) ? getParam('max') : 12;
            max = parseInt(max, 10);

            var offset = offset = (getParam('offset') != null) ? getParam('offset') : 0;
            offset = parseInt(offset, 10);

            var total = 0;
            var current_page = 1;
            var serviceURL = '${serverURL}';
            var serviceURI = serviceURL + '/gallery/findAllImages.json?max=' + max + '&offset=' + offset;

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
                //console.log(serviceURI);
                httpRequest.open('GET', serviceURI, false);
                httpRequest.send();
            })();

            function generate_grid(data) {

                var images = data.images;

                for (var i = 0; i < images.length; i++) {

                    var image = images[i];

                    createGallery(image, i);

                    createStepNav(image, i);

                    createModal(image, i);
                }
            }

            function createGallery(image, counter) {

                var columnDiv = document.createElement('div');
                columnDiv.setAttribute('class', 'col-6 col-md-4 col-lg-3');

                var imageDiv = document.createElement('img');
                imageDiv.setAttribute('class', 'w-100');
                imageDiv.setAttribute('src', image.image);
                imageDiv.setAttribute('data-target', '#carouselExample');
                imageDiv.setAttribute('data-slide-to', counter.toString());
                imageDiv.setAttribute('alt', image.title);
                columnDiv.appendChild(imageDiv);
                document.getElementById('gallery').appendChild(columnDiv);
            }

            function createStepNav(image, counter) {

                var listItem = document.createElement('li');
                listItem.setAttribute('data-target', '#carouselExample');
                listItem.setAttribute('data-slide-to', counter.toString());
                if (counter == 0) {
                    listItem.setAttribute('class', 'active');
                }
                document.getElementById('carouseList').appendChild(listItem);
            }
            
            function createModal(image, counter) {

                var modalItem = document.createElement('div');
                if (counter == 0) {
                    modalItem.setAttribute('class', 'carousel-item active');
                } else {
                    modalItem.setAttribute('class', 'carousel-item');
                }

                var modalImage = document.createElement('img');
                modalImage.setAttribute('class', 'd-block w-100');
                modalImage.setAttribute('src', image.file);
                modalImage.setAttribute('alt', image.title);
                modalItem.appendChild(modalImage);

                /*var textDiv = document.createElement('div');
                textDiv.setAttribute('class', 'row');

                var description = document.createElement('p');
                var node = document.createTextNode(image.title + '<br/>' + image.description);
                description.appendChild(node);
                textDiv.appendChild(description);
                modalItem.appendChild(textDiv);*/
                document.getElementById('carouselModal').appendChild(modalItem);
            }

            function fillVideoContent(video, thumb) {

                var titleText = document.createElement('div');

                titleText.className = 'title-text';
                titleText.innerHTML = video.title + '<br/>' + video.description;
                thumb.appendChild(titleText);
                thumb.setAttribute('id', video.id + 1);
                thumb.style.backgroundImage = "url('" + video.image + "')";
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

            /**
             * Element.requestFullScreen() polyfill
             * @author Chris Ferdinandi
             * @license MIT
             */
            if (!Element.prototype.requestFullscreen) {
                Element.prototype.requestFullscreen = Element.prototype.mozRequestFullscreen || Element.prototype.webkitRequestFullscreen || Element.prototype.msRequestFullscreen;
            }

            /**
             * document.exitFullScreen() polyfill
             * @author Chris Ferdinandi
             * @license MIT
             */
            if (!document.exitFullscreen) {
                document.exitFullscreen = document.mozExitFullscreen || document.webkitExitFullscreen || document.msExitFullscreen;
            }

            /**
             * document.fullscreenElement polyfill
             * Adapted from https://shaka-player-demo.appspot.com/docs/api/lib_polyfill_fullscreen.js.html
             * @author Chris Ferdinandi
             * @license MIT
             */
            if (!document.fullscreenElement) {

                Object.defineProperty(document, 'fullscreenElement', {
                    get: function() {
                        return document.mozFullScreenElement || document.msFullscreenElement || document.webkitFullscreenElement;
                    }
                });

                Object.defineProperty(document, 'fullscreenEnabled', {
                    get: function() {
                        return document.mozFullScreenEnabled || document.msFullscreenEnabled || document.webkitFullscreenEnabled;
                    }
                });
            }

            document.addEventListener('click', function (event) {

                // Ignore clicks that weren't on the toggle button
                if (!event.target.hasAttribute('data-toggle-fullscreen')) return;

                // If there's an element in fullscreen, exit
                // Otherwise, enter it
                if (document.fullscreenElement) {
                    document.exitFullscreen();
                } else {
                    document.documentElement.requestFullscreen();
                }

            }, false);å

        </script>
        <script
            type="text/javascript"
            src="https://s3-us-west-2.amazonaws.com/media.ooica.net/js/iframeResizer.contentWindow.min.js"
            defer
        ></script>
    </body>

</html>