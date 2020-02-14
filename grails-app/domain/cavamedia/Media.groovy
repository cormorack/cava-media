package cavamedia

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel
class Media {

    static mapWith = 'none'

    @ApiModelProperty(position = 1, value = "The media title")
    String title

    @ApiModelProperty(value = "A description of the media")
    String excerpt

    @ApiModelProperty(value = "The media mime type")
    String type

    @ApiModelProperty(value = "A 768x431 thumbnail image")
    String thumb

    @ApiModelProperty(value = "A 150x84 thumbnail image")
    String tinyThumb

    @ApiModelProperty(value = "The media latitude")
    String lat

    @ApiModelProperty(value = "The media longitude")
    String lon

    @ApiModelProperty(value = "The media video URL")
    String vUrl

    @ApiModelProperty(value = "The media poster image")
    String vPoster

    @ApiModelProperty(value = "The media date")
    String dateString

    @ApiModelProperty(value = "The media URL")
    String uri

    @ApiModelProperty(value = "Is the media a video?")
    boolean isVideo = false

    //static transients = ['title', 'excerpt', 'type', 'thumb', 'tinyThumb', 'lat', 'lon', 'vUrl', 'vPoster', 'dateString', 'uri', 'isVideo']
}
