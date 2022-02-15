<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3" crossorigin="anonymous">
    <script src="https://kit.fontawesome.com/636d54db3c.js" crossorigin="anonymous"></script>
    <title>Instrument Data</title>
    <style>
    ul.list-group {
        list-style: none;
        padding: 0;
        margin: 0;
    }

    li.list-group-item {
        padding-left: 1em;
        text-indent: -.7em;
    }

    li.list-group-item:before {
        content: "\f111";
        font-family: FontAwesome;
        color: blue;
        margin-right: 4px;
    }

    li.list-group-item.off:before {
        content: "\f057";
        font-family: FontAwesome;
        color: grey;
        margin-right: 4px;
    }
    </style>
</head>

<body>
<div class="container-fluid" id="app">
    <div class="row">
        <h3 class="text-center">Instrument Data</h3>
        <div class="col">
            <div class="card">
                <ul class="list-group">
                    <li v-for="(value, key) in this.instrumentData" :key="key" class="list-group-item">
                        {{key}}: {{value}}
                    </li>
                </ul>
            </div>
        </div>
    </div>
</div>
<asset:javascript src="vue.min.js"/>
<asset:javascript src="axios.min.js"/>
<script>

    let refDes = '';
    const urlParams = new URLSearchParams(window.location.search);
    refDes = urlParams.get("ref");
    const url = "${dataURL}/feed?" + refDes;

    const unitMap = {
        "pressure_temp": "Seawater Pressure (dBar)",
        "seawater_temperature": "Seawater Temperature (ºC)",
        "seawater_pressure": "Seawater Pressure (dBar)",
        "depth": "Depth (m)",
        "density": "Density (kg m-3)",
        "conductivity": "Seawater Conductivity (counts)",
        "corrected_dissolved_oxygen": "Corrected Dissolved Oxygen (µmol kg-1)",
        "seawater_conductivity": "Seawater Conductivity (S m-1)",
        "temperature": "Seawater Temperature (ºC)",
        "time": "Time (UTC)",
        "pressure": "CTD Seawater Pressure (dbar)",
        "practical_salinity": "Practical Salinity (1)",
        "ctd_tc_oxygen": "Oxygen (µmol kg-1)"
    };

    const vm = new Vue({
        el: '#app',
        data: {
            instrumentData: {}
        },
        mounted() {
            axios.get(url).then( response => {
                this.parseData(response.data);
            })
        },
        methods: {
            parseData(data) {
                const values = JSON.parse(data.value);
                const dataObject = values;
                const keys = Object.keys(dataObject);
                let finalData = {};
                keys.forEach(key => {
                    finalData[ unitMap[key] ] = dataObject[key]
                });
                this.instrumentData = finalData;
            }
        }
    });

</script>
</body>
</html>