package cavamedia

import grails.util.Holders
import io.swagger.annotations.ApiOperation
import io.swagger.v3.oas.annotations.Hidden

@Hidden
class BaseController {

    def config = Holders.config

    @ApiOperation(hidden = true)
    def index() { }

    /**
     * Sets default values for parameters.
     * sort=date, order=desc, offset=0, max=default value in config.properties
     * max cannot be larger than the configuration value, less than 0
     * @param params
     * @return
     */
    protected setParams(params) {

        if (!params.max || !Utilities.isNumeric(params.max.toString()) || params.max.toInteger() < 0) {
            params.max = config.totalMax
        }
        params.max = Math.min(params.max?.toInteger() ?: 0, config.totalMax.toInteger())

        if (!params.sort) params.sort = "date"
        if (!params.order) params.order = "desc"
        if (!params.geoReferenced) params.geoReferenced = "true"

        if (!params.offset || !Utilities.isNumeric(params.offset.toString()) ) params.offset = 0
    }
}
