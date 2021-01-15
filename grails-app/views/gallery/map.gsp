<g:set var="serverURL" value="${grailsApplication.config.grails.serverURL}" />
<g:set var="lamdaURL" value="${grailsApplication.config.lambdaURL}" />
<!DOCTYPE html>
<html>
    <head>
        <meta charset='utf-8' />
        <title>Map</title>
        <meta name='viewport' content='initial-scale=1,maximum-scale=1,user-scalable=no' />
        <script src='https://api.tiles.mapbox.com/mapbox-gl-js/v1.4.1/mapbox-gl.js'></script>
        <link href='https://api.tiles.mapbox.com/mapbox-gl-js/v1.4.1/mapbox-gl.css' rel='stylesheet' />
        <asset:javascript src="jquery-3.3.1.min.js"/>
        <style>
        body { margin:0; padding:0; }

        #map {
            position:absolute;
            top:0;
            bottom:0;
            width:100%;
            z-index: 0;
        }

        .mapboxgl-popup {
            max-width: 600px;
        }
        .mapboxgl-popup-content {
            position: relative;
            background: #fff;
            border-radius: 3px;
            box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
            padding: 10px 10px 15px;
            pointer-events: auto;
            width: 600px;
        }

        #menu {
             background: #fff;
             position: absolute;
             z-index: 1;
             top: 10px;
             left: 10px;
             border-radius: 3px;
             width: 120px;
             border: 1px solid rgba(0,0,0,0.4);
             font-family: 'Open Sans', sans-serif;
        }

        #menu a {
            font-size: 13px;
            color: #404040;
            display: block;
            margin: 0;
            padding: 0;
            padding: 10px;
            text-decoration: none;
            border-bottom: 1px solid rgba(0,0,0,0.25);
            text-align: center;
        }

        #menu a:last-child {
            border: none;
        }

        #menu a:hover {
            background-color: #f8f8f8;
            color: #404040;
        }

        #menu a.active {
            background-color: #3887be;
            color: #ffffff;
        }

        #menu a.active:hover {
            background: #3074a4;
        }

        #grotto {
            display: block;
            position: relative;
            margin: 0px auto;
            width: 20%;
            height: 40px;
            padding: 10px;
            border: none;
            border-radius: 3px;
            font-size: 12px;
            text-align: center;
            color: #fff;
            background: #ee8a65;
            top: 10px;
        }

        #info {
            display: block;
            position: relative;
            margin: 0px auto;
            width: 50%;
            padding: 10px;
            border: none;
            border-radius: 3px;
            font-size: 12px;
            text-align: center;
            color: #222;
            background: #fff;
        }

        </style>
        <script type="text/javascript" src="https://io.ocean.washington.edu/jwplayer_new/jwplayer.js"></script>
        <script>jwplayer.key="TlrRuCKIJtPFH4TCqTcHNr5P2KxNL5zIzfOOx1yFCCU=";</script>
    </head>

    <body>
        <div id='menu'></div>
        <div id='map'></div>
        <button id='grotto'>Zoom to Einstein’s Grotto</button>
        <script>
            mapboxgl.accessToken = 'pk.eyJ1Ijoic2R0aG9tYXMiLCJhIjoiY2l6a2Njc3VyMDIzYjMzb2R5cmtndjk5YiJ9.vXN6i1-qOJpU1aA5EUR9bQ';

            var map = new mapboxgl.Map({
                container: 'map',
                style: 'mapbox://styles/mapbox/light-v9',
                zoom: 6,
                minZoom: 6,
                maxZoom: 25,
                center: [-130.00, 46.016]
            });

            map.on('load', function() {

                /*map.on('zoomend', function() {
                    console.log(map.getZoom())
                });*/

                var videoURL = '${serverURL}/media?type=video';

                var imageURL = '${serverURL}/media?type=image';

                map.addSource('imageData', { type: 'geojson', data: imageURL});

                map.addLayer({
                    id: 'images',
                    type: 'circle',
                    source: 'imageData',
                    interactive:true,
                    paint: {
                        "circle-color": "#11b4da",
                        "circle-radius": 4,
                        "circle-stroke-width": 1,
                        "circle-stroke-color": "#fff"
                    }
                });

                var imageRollOverPopup = new mapboxgl.Popup({
                    closeButton: false,
                    closeOnClick: true
                });

                map.on('mouseenter', 'images', function(e) {

                    map.getCanvas().style.cursor = 'pointer';

                    imageRollOverPopup.setLngLat(e.features[0].geometry.coordinates)
                        .setHTML(e.features[0].properties.title)
                        .addTo(map);
                });

                map.on('mouseleave', 'images', function() {
                    map.getCanvas().style.cursor = '';
                    imageRollOverPopup.remove();
                });

                map.on('click', 'images', function (e) {

                    // Change the cursor style as a UI indicator.
                    map.getCanvas().style.cursor = 'pointer';

                    var coordinates = e.features[0].geometry.coordinates.slice();

                    var description = e.features[0].properties.excerpt;

                    var imageURL = e.features[0].properties.url;

                    var imageTitle = e.features[0].properties.title;

                    var thumbnail = e.features[0].properties.thumbnail;

                    while (Math.abs(e.lngLat.lng - coordinates[0]) > 180) {
                        coordinates[0] += e.lngLat.lng > coordinates[0] ? 360 : -360;
                    }

                    var imageHTML = '<h3>' + imageTitle + '</h3><a href=\"' + imageURL + '\"><img src=\"' + thumbnail + '\" width=\"580px\"></a>';

                    var imagePopup = new mapboxgl.Popup();
                    imagePopup.setLngLat(coordinates)
                        .setHTML(imageHTML + '<p>' + description + '</p>')
                        .addTo(map);

                });

                // Change it back to a pointer when it leaves.
                map.on('mouseleave', 'images', function () {
                    map.getCanvas().style.cursor = '';
                });

                map.addSource('videos', { type: 'geojson', data: videoURL});

                map.addLayer({
                    id: 'videos',
                    type: 'circle',
                    source: 'videos',
                    interactive:true,
                    paint: {
                        "circle-color": "#32cd32",
                        "circle-radius": 4,
                        "circle-stroke-width": 1,
                        "circle-stroke-color": "#fff"
                    }
                });

                var videoRollOverPopup = new mapboxgl.Popup({
                    closeButton: false,
                    closeOnClick: true
                });

                map.on('mouseenter', 'videos', function(e) {

                    map.getCanvas().style.cursor = 'pointer';

                    videoRollOverPopup.setLngLat(e.features[0].geometry.coordinates)
                        .setHTML(e.features[0].properties.title)
                        .addTo(map);
                });

                map.on('mouseleave', 'videos', function() {
                    map.getCanvas().style.cursor = '';
                    videoRollOverPopup.remove();
                });

                map.on('click', 'videos', function (e) {

                    // Change the cursor style as a UI indicator.
                    map.getCanvas().style.cursor = 'pointer';

                    var coordinates = e.features[0].geometry.coordinates.slice();

                    var description = e.features[0].properties.excerpt;

                    var imageTitle = e.features[0].properties.title;

                    var poster = e.features[0].properties.videoPoster;

                    var videoURL = e.features[0].properties.videoURL;

                    while (Math.abs(e.lngLat.lng - coordinates[0]) > 180) {
                        coordinates[0] += e.lngLat.lng > coordinates[0] ? 360 : -360;
                    }

                    var imageHTML = '<h3>' + imageTitle + '</h3><div id=\"container\">Loading the player...</div><p>' + description + '</p>';

                    var videoPopup = new mapboxgl.Popup();
                    videoPopup.setLngLat(coordinates);
                    videoPopup.setHTML(imageHTML);
                    videoPopup.addTo(map);

                    // Set up the video player
                    jwplayer("container").setup({
                        file: videoURL,
                        width:"580px",
                        height: "326px",
                        aspectratio: "16:9",
                        'image': poster
                    });
                });

                var toggleableLayerIds = [ 'videos', 'images', 'blue-base-bathymetry', 'base-bathymetry', 'oregon-bathymetry' ,'axial-bathymetry', 'grotto', 'axial-caldera'];

                for (var i = 0; i < toggleableLayerIds.length; i++) {

                    var id = toggleableLayerIds[i];

                    var link = document.createElement('a');
                    link.href = '#';
                    link.className = 'active';
                    link.textContent = id;

                    link.onclick = function (e) {
                        var clickedLayer = this.textContent;
                        e.preventDefault();
                        e.stopPropagation();

                        var visibility = map.getLayoutProperty(clickedLayer, 'visibility');

                        if (visibility === 'visible') {
                            map.setLayoutProperty(clickedLayer, 'visibility', 'none');
                            this.className = '';
                        } else {
                            this.className = 'active';
                            map.setLayoutProperty(clickedLayer, 'visibility', 'visible');
                        }
                    };

                    var layers = document.getElementById('menu');
                    layers.appendChild(link);
                }

                var layers = [
                    {'layerKey': 'blue-base-bathymetry', 'layerURL': 'https://rca-map-layers.s3-us-west-2.amazonaws.com/blue-basemap.tiff'},
                    {'layerKey': 'base-bathymetry', 'layerURL': 'https://rca-map-layers.s3-us-west-2.amazonaws.com/RCA-MAP-CO.tif'},
                    {'layerKey': 'axial-bathymetry', 'layerURL': 'https://rca-map-layers.s3-us-west-2.amazonaws.com/AxialCaldera-SlopeBase.tiff'},
                    {'layerKey': 'oregon-bathymetry', 'layerURL': 'https://rca-map-layers.s3-us-west-2.amazonaws.com/HydrateEndeavourTiled.tiff'},
                    //{'layerKey': 'grotto', 'layerURL':'https://rca-map-layers.s3-us-west-2.amazonaws.com/R1463_4m_EG_GCS.tiff'},
                    {'layerKey': 'axial-caldera', 'layerURL': 'https://rca-map-layers.s3-us-west-2.amazonaws.com/axial-inside-caldera-2.tiff'}
                ];

                var boundLayers = [];

                layers.forEach( function( layer) {

                    var url = '${lamdaURL}/tilejson.json?url=' + layer['layerURL'];

                    boundLayers.push(fetchData(url));
                });

                Promise.all( boundLayers).then(function (values) {
                    values.forEach(function (el, idx) {
                        var index = layers[idx];
                        createLayer(index['layerKey'], index['layerURL'], el.bounds, el.minzoom);
                    })
                });
            });

            function createLayer(layerKey, layerURL, layerBounds, layerMinZoom) {
                map.addLayer({
                    'id': layerKey,
                    'type': 'raster',
                    'source': {
                        'type': 'raster',
                        'tiles': [
                            '${lamdaURL}/tiles/{z}/{x}/{y}.png?url=' + layerURL
                        ],
                        'tileSize': 256,
                        'maxzoom': 25,
                        'minzoom': layerMinZoom,
                        'bounds': layerBounds
                    },
                    'paint': {}
                }, 'aeroway-taxiway');
            }

            function fetchData(url) {
                return $.ajax({
                    type: "GET",
                    dataType: "json",
                    url: url,
                    //async: false,
                    crossDomain: true,
                    success: function (jsonData) {}
                });
            }

            document.getElementById('grotto').addEventListener('click', function() {
                map.fitBounds([[
                    -125.1469607,
                    44.5701122
                ], [
                    -125.1464911,
                    44.5698893,
                ]]);
            });

            /*map.on('mousemove', function (e) {
                //console.log(e);
                var coord = mapboxgl.MercatorCoordinate.fromLngLat(e.lngLat, 0);
                //console.log(coord);
                document.getElementById('info').innerHTML =
                // e.point is the x, y coordinates of the mousemove event relative
                // to the top-left corner of the map
                    JSON.stringify(e.point) + '<br />' +
                    // e.lngLat is the longitude, latitude geographical position of the event
                    JSON.stringify(e.lngLat.wrap());
            });*/

            map.addControl(new mapboxgl.NavigationControl());

            function removeElement() {
                document.getElementById("containerWrapper").style.display = "none";
                jwplayer( 'container' ).stop();
            }

        </script>
    </body>

</html>

