package cavamedia

import com.zaxxer.hikari.HikariDataSource
import grails.util.Holders
import javax.sql.DataSource

class TestController {

    def index() {
        render ""
    }

    /*def index () {

        DataSource ds = getProxiedDataSource(getBeanByName(DataSource))

        def message = "DataSource: ${ds.class.canonicalName}"

        def properties = [:]

        if (ds instanceof HikariDataSource) {
            properties.maxPoolSize = ds.maximumPoolSize
        }

        println "properties are ${ds.properties}"

        [message: message, properties: properties]
    }

    private static DataSource getProxiedDataSource(DataSource proxy) {

        if (proxy.hasProperty('targetDataSource')) {
            return getProxiedDataSource(proxy.targetDataSource as DataSource)
        } else {
            return proxy
        }
    }

    static <T> T getBeanByName(Class<T> clazz) {

        def beanName = clazz.simpleName.uncapitalize()
        return Holders.applicationContext.getBean(beanName) as T
    }*/
}
