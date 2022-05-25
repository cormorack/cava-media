<%@ page contentType="text/html;charset=UTF-8" %>
<g:if env="development">
    <g:set var="dataURL" value="${context}/files/data.json" />
    <g:set var="parameterURL" value="${context}/files/parameters.json" />
</g:if>
<g:if env="production">
    <g:set var="dataURL" value="/feed/" />
    <g:set var="parameterURL" value="/metadata/parameters" />
</g:if>
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
            color: #1aff00;
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
        <div class="container-fluid" id="app" data-iframe-height>
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
        <asset:javascript src="lodash.min.js"/>
        <script>

            const parameterURL = location.origin + "${parameterURL}";
            const baseUrl = location.origin + "${dataURL}";
            let url = baseUrl;
            let rawData = {};
            const urlParams = new URLSearchParams(window.location.search);

            refDes = urlParams.get("ref");

            if (refDes != null) {
                url = baseUrl + "?ref=" + refDes;
            }

            let message = "No data is available";

            const vm = new Vue({
                el: '#app',
                data: {
                    instrumentData: {},
                },
                mounted() {
                    axios.get(url).then( response => {
                        this.parseData(response.data);
                    }),
                    axios.get(parameterURL).then( response => {
                        this.parseParameters(response.data);
                    })
                },
                methods: {
                    parseData(data) {
                        _.mapValues(data, (value) => {
                            rawData = value;
                        });
                    },
                    parseParameters(data) {
                        const dataParams = data;
                        this.formatData(rawData, dataParams)
                    },
                    formatData(data, params) {
                        let finalData = {};
                        let parameters = {};

                        if (nullCheck(data)) {
                            finalData[message] = ""
                            return this.instrumentData = finalData;
                        } else {
                            _.mapValues(params, (value) => {
                                filter(parameters, value);
                            });
                            _.mapValues(data, (value) => {
                                Object.entries(value).forEach( ([key, val]) => {
                                    if (_.isUndefined(parameters[key])) {
                                        return;
                                    }
                                    finalData[ parameters[key] ] = round(key, val);
                                });
                            });
                        }
                        this.instrumentData = finalData;
                    }
                }
            });

            function nullCheck(value) {

                if (!value || value === message || value == null) {
                    return true;
                }
                else if (_.isObject(value)) {
                    return false;
                }
                else return true;
            }

            function filter(p, value) {
                if (value.unit === '1') {
                    return p[value.reference_designator] = value.parameter_name;
                }
                else return p[value.reference_designator] = value.parameter_name + " (" + value.unit + ")";
            }

            function round(key, val) {
                if (key.includes("time")) {
                    return val;
                }
                else if (_.isNumber(val)) {
                    return _.round(val, 4);
                }
                else return val;
            }

        </script>
    </body>
</html>