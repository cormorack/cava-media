<%@ page contentType="text/html;charset=UTF-8" %>
<g:set var="serverURL" value="${grailsApplication.config.grails.serverURL}" />
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        %{--<asset:stylesheet href="bootstrap.css"/>--}%
        <asset:stylesheet href="gallery.css"/>
        <asset:javascript src="jquery-3.3.1.min.js"/>
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
            <g:each in="${(0..4)}">
                <div class="row-flex-6">
                    %{--<div class="column">
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
                    </div>--}%
                    <div class="column">
                        <div class="thumb large"></div>
                        <div class="thumb large"></div>
                    </div>
                    <div class="column">
                        <div class="thumb large"></div>
                        <div class="thumb large"></div>
                    </div>
                </div>
            </g:each>
            <a href="javascript:prevPage()" id="btn_prev">Back </a>
            <a href="javascript:nextPage()" id="btn_next">Next </a>
        </div>
        </div>
        %{--<asset:javascript src="jwGallery.js"/>--}%
        <script type="text/javascript" src="https://content.jwplatform.com/libraries/Jq6HIbgz.js"></script>
        <script type="text/javascript">

            var query = getParam('q');
            var max = 24;
            max = (getParam('max') != null) ? getParam('max') : max;
            max = parseInt(max, 10);

            var offset = 0;
            offset = (getParam('offset') != null) ? getParam('offset') : offset;
            offset = parseInt(offset, 10);

            var total = [];
            var current_page = 1;
            var records_per_page = max;
            var serviceURL = '${serverURL}';
            var serviceURI = serviceURL + '/media/findVideos.json' + '?max=' + max + '&offset=' + offset;

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
                            //console.log(httpRequest.response);
                            getThumbnails(json);
                        } else {
                            console.log(httpRequest.statusText);
                        }
                    }
                }
                console.log(serviceURI);
                httpRequest.open('GET', serviceURI);
                httpRequest.send();
            })();

            // Render thumbnails into grid layout
            var thumbs = document.querySelectorAll('.thumb');
            var player;

            function getThumbnails(data) {

                var playlist = data.playlist;
                total = data.total;


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


            console.log(offset);
            console.log(max);

            function changePage(page) {

                //alert('current_page is ' + page);

                var btn_next = document.getElementById("btn_next");
                var btn_prev = document.getElementById("btn_prev");

                // Validate page
                if (page < 1) page = 1;
                if (page > numPages()) page = numPages();

                /*if (page == 1) {
                    btn_prev.style.visibility = "hidden";
                } else {
                    btn_prev.style.visibility = "visible";
                }

                if (page == numPages()) {
                    btn_next.style.visibility = "hidden";
                } else {
                    btn_next.style.visibility = "visible";
                }*/

                window.location.href =
                    window.location.protocol +
                    "//" +
                    window.location.host +
                    window.location.pathname +
                    '?' +
                    $.param({'max':max,'offset':offset, 'q':query});

            }

            function numPages() {

                //console.log("number of pages is " +  total / records_per_page);
                //alert("number of pages is " +  total / records_per_page);
                alert('total is ' + total);
                return Math.round(Math.ceil(total / max));
            }

            /*if(offset > max) {
                document.getElementById("btn_prev").style.visibility = "visible";
            } else {
                document.getElementById("btn_prev").style.visibility = "hidden";
            }
            if(offset > max) {
                document.getElementById("btn_next").style.visibility = "hidden";
            }*/

            /*function paginate(
                totalItems,
                currentPage,
                pageSize,
                maxPages
            ) {
                // calculate total pages
                var totalPages = Math.ceil(totalItems / pageSize);

                // ensure current page isn't out of range
                if (currentPage < 1) {
                    currentPage = 1;
                } else if (currentPage > totalPages) {
                    currentPage = totalPages;
                }

                var startPage, endPage;

                if (totalPages <= maxPages) {
                    // total pages less than max so show all pages
                    startPage = 1;
                    endPage = totalPages;

                } else {
                    // total pages more than max so calculate start and end pages
                    var maxPagesBeforeCurrentPage = Math.floor(maxPages / 2);
                    var maxPagesAfterCurrentPage = Math.ceil(maxPages / 2) - 1;

                    if (currentPage <= maxPagesBeforeCurrentPage) {
                        // current page near the start
                        startPage = 1;
                        endPage = maxPages;

                    } else if (currentPage + maxPagesAfterCurrentPage >= totalPages) {
                        // current page near the end
                        startPage = totalPages - maxPages + 1;
                        endPage = totalPages;

                    } else {
                        // current page somewhere in the middle
                        startPage = currentPage - maxPagesBeforeCurrentPage;
                        endPage = currentPage + maxPagesAfterCurrentPage;
                    }
                }

                // calculate start and end item indexes
                var startIndex = (currentPage - 1) * pageSize;
                var endIndex = Math.min(startIndex + pageSize - 1, totalItems - 1);

                // create an array of pages to ng-repeat in the pager control
                var pages = Array.from(Array((endPage + 1) - startPage).keys()).map(i = startPage + i); // return object with all pager properties required by the view
                return {
                    totalItems: totalItems,
                    currentPage: currentPage,
                    pageSize: pageSize,
                    totalPages: totalPages,
                    startPage: startPage,
                    endPage: endPage,
                    startIndex: startIndex,
                    endIndex: endIndex,
                    pages: pages
                };
            }*/

            //export = paginate;
        </script>
    </body>

</html>