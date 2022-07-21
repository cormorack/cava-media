package media

class EveryHourJob {

    def tagService

    static triggers = {
        simple repeatInterval: 3600000l // execute job every hour (in milliseconds)
    }

    def execute() {
        tagService.clearTags()
    }
}
