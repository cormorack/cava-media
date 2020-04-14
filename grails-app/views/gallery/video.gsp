<%@ page contentType="text/html;charset=UTF-8" %>
<g:set var="serverURL" value="${grailsApplication.config.grails.serverURL}" />
<!DOCTYPE html>
<html>

<head>
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <asset:stylesheet href="bootstrap4.css"/>
    <asset:stylesheet href="gallery2.css"/>
    <link rel="stylesheet" href="https://code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
    <asset:stylesheet href="pagination.css"/>
    <title>Video Gallery</title>
    <script type="text/javascript" src="https://io.ocean.washington.edu/jwplayer_new/jwplayer.js"></script>
    <script>jwplayer.key="TlrRuCKIJtPFH4TCqTcHNr5P2KxNL5zIzfOOx1yFCCU=";</script>
</head>

<body>
<div class="container" data-iframe-height>
    <div class="row">
        <div class="col-8">
            <div id="cover" style="display:none; z-index: 10">
                <asset:image src="loader.gif" width="300px" height="300px"/>
            </div>
            <div class="row" id="gallery"></div>

            <!-- Modal -->
            <div class="modal fade" id="videoModal" tabindex="-1" role="dialog">
                <div class="modal-dialog modal-dialog-centered modal-lg" role="document">
                    <div class="modal-content">
                        <div class="modal-header bg-dark border-dark">
                            <button type="button" class="close text-white" data-dismiss="modal">&times;</button>
                        </div>
                        <div class="modal-body bg-dark p-0">
                            <div id="videoHeader" class="header-bar"></div>
                            <div id="videoContainer"></div>
                            <div id="videoDescription" class="header-bar"></div>
                        </div>
                    </div>
                </div>
            </div>

            %{--<nav aria-label="Page navigation">
                <ul class="pagination">
                    <li class="page-item"><a id="btn_prev" class="page-link" href="javascript:prevPage()">Previous</a></li>
                    <li class="page-item"><a id="btn_next" class="page-link" href="javascript:nextPage()">Next</a></li>
                </ul>
            </nav>--}%
            <div id="pagination-fancy"></div>
        </div>
        <div class="col-4">
            <div class="row">
                <div class="searchResultsWrapper">
                    <div id="searchResults"></div>
                    <g:form method="get" action="${actionName}" controller="${controllerName}" class="navbar-form navbar-left" role="search">
                        <div class="input-group">
                            <input
                                    class="form-control form-control-sm border-secondary py-2"
                                    type="text"
                                    id="q"
                                    name="q"
                                    value="${params?.q?.encodeAsHTML()}"/>
                            <select class="form-control form-control-sm" id="selectMax" name="max">
                                <option>28</option>
                                <option>48</option>
                                <option>96</option>
                            </select>
                            <div class="input-group-append">
                                <button class="btn btn-secondary btn-sm" type="submit">Search</button>
                                <g:link
                                        class="btn btn-outline-secondary btn-sm"
                                        role="button"
                                        controller="${controllerName}"
                                        action="${actionName}">Reset
                                </g:link>
                            </div>
                        </div>
                    </g:form>
                </div>
            </div>
            <div class="row" id="tagcloud"></div>
        </div>
    </div>
</div>
<asset:javascript src="jquery-3.3.1.min.js"/>
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.min.js" integrity="sha256-VazP97ZCwtekAsvgPBSUwPFKdrwD3unUfSGVYrahUqU=" crossorigin="anonymous"></script>
%{--<script src="https://unpkg.com/@popperjs/core@2"></script>--}%
%{--<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js"></script>--}%
<asset:javascript src="bootstrap4.js"/>
<asset:javascript src="pagination.js"/>
%{--<asset:javascript src="fullScreen.js"/>--}%
<script type="text/javascript">

    var query = getParam('q');
    var tag = getParam('tag');
    var max = (getParam('max') != null) ? getParam('max') : 28;
    max = parseInt(max, 10);

    var offset = (getParam('offset') != null) ? getParam('offset') : 0;
    offset = parseInt(offset, 10);

    var total = 0;
    var current_page = 1;
    var serviceURL = '${serverURL}';
    var serviceURI = serviceURL + '/gallery/findAllVideos.json?max=' + max + '&offset=' + offset;
    var searchMessage = '';
    var tagMessage = "";

    if (query) {
        serviceURI = serviceURI  + '&q=' + query;
    }
    if (tag) {
        serviceURI = serviceURI  + '&tag=' + tag;
    }

    function getParam(name) {
        if (name = (new RegExp('[?&]' + encodeURIComponent(name) + '=([^&]*)')).exec(location.search))
            return decodeURIComponent(name[1]);
    }

    (function() {

        var loading = $("#cover");

        $(document).ajaxStart(function () {
            loading.show();
        });

        $(document).ajaxStop(function () {
            loading.hide();
        });

        $.ajax({
            type: "GET",
            dataType: "json",
            url: serviceURI,
            success: function (jsonData) {
                total = jsonData.total;
                createTagCloud(jsonData);
                generateGrid(jsonData);
                createSearchResults(total);
                setPaging(total);
            }
        });
    })();

    function createSearchResults(total) {

        var results = document.getElementById('searchResults');

        var current = (parseInt(max, 10) + parseInt(offset, 10));

        if (current > total) {
            var difference = current - total;
            current = current - difference;
        }
        var start = parseInt(offset, 10);

        if (start == 0) {
            start = 1;
        }
        searchMessage = 'Displaying ' + start + ' through ' + current + ' of ' + total + ' videos';
        if (tag) searchMessage += ' with the tag: <strong>' + tagMessage + '</strong>';
        results.innerHTML = searchMessage;
    }

    function generateGrid(data) {

        var images = data.playlist;

        for (var i = 0; i < images.length; i++) {

            var image = images[i];

            createGallery(image);
        }
    }

    function createGallery(image) {

        var columnDiv = document.createElement('div');
        columnDiv.setAttribute('class', 'col-6 col-md-4 col-lg-3');

        var imageDiv = document.createElement('img');
        imageDiv.setAttribute('class', 'w-100');
        imageDiv.setAttribute('src', image.image);
        imageDiv.setAttribute('data-target', '#videoModal');
        imageDiv.setAttribute('data-toggle', 'modal');
        imageDiv.setAttribute('data-file', image.file);
        imageDiv.setAttribute('data-image', image.image);
        imageDiv.setAttribute('data-title', image.title);
        imageDiv.setAttribute('data-description', image.description);
        imageDiv.setAttribute('alt', image.title);
        imageDiv.setAttribute('title', image.title);
        columnDiv.appendChild(imageDiv);
        document.getElementById('gallery').appendChild(columnDiv);
    }

    function createTagCloud(data) {

        var tags = data.tags;
        var tagList = document.createElement('ul');

        for (var i = 0; i < tags.length; i++) {

            var listItem = document.createElement('li');
            var tagLink = document.createElement('a');
            var linkText = document.createTextNode(tags[i].name);
            tagLink.appendChild(linkText);
            var tagURL = serviceURL + '/gallery/video?tag='
            tagLink.setAttribute('href', tagURL + tags[i].slug);
            if (tag != undefined) {
                if (tag.toUpperCase() == tags[i].slug.toUpperCase()) {
                    tagLink.setAttribute('class', 'highlight');
                    setTagMessage(tags[i]);
                }
            }
            listItem.appendChild(tagLink);
            tagList.appendChild(listItem);
        }
        document.getElementById('tagcloud').appendChild(tagList);
    }

    function setTagMessage(tagObject) {
        if (tagMessage === "" || tagMessage == undefined) {
            tagMessage = tagObject.name.toUpperCase();
        }
    }

    function setPaging(jsonTotal) {

        var itemsCount = jsonTotal;
        var itemsOnPage = max;
        var pagingURL = window.location.protocol +
            "//" +
            window.location.host +
            window.location.pathname +
            '?' +
            $.param({'max':max, 'q':query, 'tag':tag});

        var myPagination = new Pagination({

            // Where to render this component
            container: $("#pagination-fancy"),

            // Called when user change page by this component
            // contains one parameter with page number
            /*pageClickCallback: function () {
            },*/

            // The URL to which is browser redirected after user change page by this component
            pageClickUrl: pagingURL + "&page={{page}}",

            //pageClickUrl: function(num) { return "?page=" + num; },

            // If true, pageClickCallback is called immediately after component render (after make method call)
            callPageClickCallbackOnInit: false,

            // The number of visible buttons in pagination panel
            maxVisibleElements: 20,

            // Shows slider for fast navigation between pages
            showSlider: false,

            // Shows text input box for direct navigation to specified page
            showInput: false,

            // The content of tooltip displayed on text input box.
            inputTitle: 'Go to page',

            // If false, standard mode is used (show arrows on the edges, border page numbers, shorting dots and page numbers around current page).
            // If true, standard mode is enhanced, so page number between border number and middle area is also displayed.
            enhancedMode: true

        });
        myPagination.make(itemsCount, itemsOnPage, getParam("page"), max, offset);
    };

    $(function () {
        $('[data-toggle="tooltip"]').tooltip()
        $("#selectMax").val(max).attr('selected', 'selected');
    });

    $(document).ready(function() {
        $("#videoModal").on("show.bs.modal", function(event) {
            var button = $(event.relatedTarget); // Button that triggered the modal
            var url = button.data("file");      // Extract url from data-video attribute
            var poster = button.data("image");
            var title = button.data("title");
            var description = button.data("description");
            $("#videoHeader").html(title);
            $("#videoDescription").html(description);

            jwplayer("videoContainer").setup({
                file: url,
                width: "100%",
                aspectratio: "16:9",
                'image': poster
            });
        });

        // Remove attributes when the modal has finished being hidden from the user
        $("#videoModal").on("hidden.bs.modal", function() {
            //$("#videoModal iframe").removeAttr("src allow");
        });
    });

    /*jQuery( document ).ready(function( $ ) {
        $('[data-toggle="tooltip"]').tooltip({
            container : 'body'
        })
    });*/

</script>
<script
        type="text/javascript"
        src="https://s3-us-west-2.amazonaws.com/media.ooica.net/js/iframeResizer.contentWindow.min.js"
        defer
></script>
</body>

</html>