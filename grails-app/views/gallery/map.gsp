<!DOCTYPE html>
<html>
<head>
    <meta charset='utf-8' />
    <title>Add a third party vector tile source</title>
    <meta name='viewport' content='initial-scale=1,maximum-scale=1,user-scalable=no' />
    <script src='https://api.tiles.mapbox.com/mapbox-gl-js/v0.49.0/mapbox-gl.js'></script>
    <link href='https://api.tiles.mapbox.com/mapbox-gl-js/v0.49.0/mapbox-gl.css' rel='stylesheet' />
    <script src="https://code.jquery.com/jquery-2.2.4.min.js"></script>
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
        font: 12px/20px 'Helvetica Neue', Arial, Helvetica, sans-serif;
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


    </style>
    <script type="text/javascript" src="https://interactiveoceans.washington.edu/jwplayer_new/jwplayer.js"></script>
    <script>jwplayer.key="TlrRuCKIJtPFH4TCqTcHNr5P2KxNL5zIzfOOx1yFCCU=";</script>
</head>

<body>
<div id='menu'></div>
<div id='map'></div>

<script>
    mapboxgl.accessToken = 'pk.eyJ1Ijoic2R0aG9tYXMiLCJhIjoiY2l6a2Njc3VyMDIzYjMzb2R5cmtndjk5YiJ9.vXN6i1-qOJpU1aA5EUR9bQ';

    var map = new mapboxgl.Map({
        container: 'map',
        style: 'mapbox://styles/mapbox/light-v9',
        zoom: 6,
        center: [-130.00, 46.016]
    });

    map.on('load', function() {

        var videoURL = 'http://localhost:8080/api/v1/media?type=video';

        var imageURL = 'http://35.166.196.47:8080/CavaMedia/api/v1/media?type=image';

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

            var imagePopup = new mapboxgl.Popup()
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

            var videoPopup = new mapboxgl.Popup()
            videoPopup.setLngLat(coordinates)
            videoPopup.setHTML(imageHTML);
            videoPopup.addTo(map)

            // Set up the video player
            jwplayer("container").setup({
                file: videoURL,
                width:"580px",
                height: "326px",
                aspectratio: "16:9",
                'image': poster
            });
        });


        var toggleableLayerIds = [ 'videos', 'images' ];

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

        /*map.addLayer({
            'id': 'wms-test-layer',
            'type': 'raster',
            'source': {
                'type': 'raster',
                'tiles': [
                    'http://52.41.106.186:8080/geoserver/bmpyramid/wms?bbox={bbox-epsg-3857}&format=image/png&service=WMS&version=1.1.1&request=GetMap&srs=EPSG:3857&transparent=true&width=256&height=256&layers=bmpyramid:bmpyramid'
                ],
                'tileSize': 256
            },
            'paint': {}
        }, 'aeroway-taxiway');
        map.addLayer({
            'id': 'wms-test-layer1',
            'type': 'raster',
            'source': {
                'type': 'raster',
                'tiles': [
                    'http://52.41.106.186:8080/geoserver/bmpyramid/wms?bbox={bbox-epsg-3857}&format=image/png&service=WMS&version=1.1.1&request=GetMap&srs=EPSG:3857&transparent=true&width=256&height=256&layers=bmpyramid:AxialCaldera'
                ],
                'tileSize': 256
            },
            'paint': {}
        }, 'aeroway-taxiway');
        map.addLayer({
            'id': 'wms-test-layer2',
            'type': 'raster',
            'source': {
                'type': 'raster',
                'tiles': [
                    'http://52.41.106.186:8080/geoserver/bmpyramid/wms?bbox={bbox-epsg-3857}&format=image/png&service=WMS&version=1.1.1&request=GetMap&srs=EPSG:3857&transparent=true&width=256&height=256&layers=bmpyramid:hydrate'
                ],
                'tileSize': 256
            },
            'paint': {}
        }, 'aeroway-taxiway');*/
    });

    map.addControl(new mapboxgl.NavigationControl());

    function removeElement() {
        document.getElementById("containerWrapper").style.display = "none";
        jwplayer( 'container' ).stop();
    }

</script>


</body>

</html>

