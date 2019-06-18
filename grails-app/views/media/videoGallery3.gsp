<%@ page contentType="text/html;charset=UTF-8" %>
<g:set var="serverURL" value="${grailsApplication.config.grails.serverURL}" />
<html>

<head>
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <asset:stylesheet href="bootstrap.css"/>
    <asset:javascript src="jquery-3.3.1.min.js"/>
    <asset:javascript src="jquery.twbsPagination.min.js"/>
    <asset:stylesheet href="gallery.css"/>
    <title>Video Gallery</title>
</head>

<body>
<div class="container">
    <div id="" class="video-wall">
        <g:each in="${(0..2)}">
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
    <div id="pager">
        <ul id="pagination" class="pagination-sm"></ul>
    </div>
</div>
<asset:javascript src="jwGallery.js"/>
<script type="text/javascript">

    var query = getParam('q');
    var serviceURL = '${serverURL}';
    var serviceURI = serviceURL + '/media/findVideos.json';

    var $pagination = $('#pagination'),
        totalRecords = 415,
        records = [],
        displayRecords = [],
        recPerPage = 24,
        page = 1,
        totalPages = 0;

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
        //totalRecords = playlist.total;
        alert("total is " + totalRecords);
        //recPerPage = playlist.max;
        alert("recPerPage is " + recPerPage);
        totalPages = Math.ceil(totalRecords / recPerPage);
        apply_pagination();

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

    function apply_pagination() {
        $pagination.twbsPagination({
            totalPages: totalPages,
            visiblePages: 6,
            onPageClick: function (event, page) {
                displayRecordsIndex = Math.max(page - 1, 0) * recPerPage;
                endRec = (displayRecordsIndex) + recPerPage;

                displayRecords = records.slice(displayRecordsIndex, endRec);
                //generate_table();
            }
        });
    }
</script>
</body>

</html>