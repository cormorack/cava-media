package cavamedia

import grails.transaction.Transactional
import grails.plugin.cache.*
import grails.util.Holders
import grails.util.Environment

@Transactional(readOnly = true)
class TagService {

    def restService
    def config = Holders.config

    /**
     * Gets a list of tags (WP Terms) from a remote endpoint.
     * The result is cached after the first successful request.
     * @return
     */
    @Cacheable('tags')
    List getTags() {

        List tagList = []

        def tagJson = restService.getURL( tagURL() )

        if (tagJson.status == 200 && tagJson.json) {

            for (Term tag in tagJson.json) {
                if (tag.count > 0) {
                    Map t = [name: tag.name, slug: tag.slug]
                    tagList.add(t)
                }
            }
        }
        return tagList
    }

    /**
     * Returns a URL from config based on the runtime environment.
     */
    def tagURL = {

        switch (Environment.current) {
            case Environment.DEVELOPMENT:
                return config.tagURL
                break
            case Environment.PRODUCTION:
                return config.tagURL
                break
        }
    }
}
