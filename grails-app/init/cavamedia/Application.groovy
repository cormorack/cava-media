package cavamedia

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import org.apache.catalina.Context
import org.apache.catalina.startup.Tomcat
import org.apache.catalina.Container;
import org.apache.tomcat.util.descriptor.web.ContextResource
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainer
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory
import org.springframework.context.annotation.Bean

class Application extends GrailsAutoConfiguration {

    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }

    /*@Bean
    public TomcatEmbeddedServletContainerFactory tomcatFactory() {
        return new TomcatEmbeddedServletContainerFactory() {

            @Override
            protected TomcatEmbeddedServletContainer getTomcatEmbeddedServletContainer(Tomcat tomcat) {
                tomcat.enableNaming();
                TomcatEmbeddedServletContainer container =
                        super.getTomcatEmbeddedServletContainer(tomcat);
                for (Container child: container.getTomcat().getHost().findChildren()) {
                    if (child instanceof Context) {
                        ClassLoader contextClassLoader =((Context)child).getLoader().getClassLoader();
                        Thread.currentThread().setContextClassLoader(contextClassLoader);
                        break;
                    }
                }
                return container;
            }

            @Override
            protected void postProcessContext(Context context) {
                context.getNamingResources().addResource(preconfigureDbResource("Cava", "localhost"))
            }
        }

    }

    private ContextResource preconfigureDbResource(String name, String ip) {

        ContextResource resource = new ContextResource()
        resource.setType("javax.sql.DataSource")
        resource.setName(name)
        //resource.setProperty("jndiName", "java:comp/env/Cava")
        resource.setProperty("url", "jdbc:mysql://" + ip + ":8889:cavaweb")
        resource.setProperty("driverClassName", "com.mysql.cj.jdbc.Driver")
        resource.setProperty("username", "cavauser")
        resource.setProperty("password", "iHazN0i0@")
        resource.setProperty("auth", "Container")
        resource.setProperty("maxTotal", "100")
        resource.setProperty("maxIdle", "30")
        resource.setProperty("maxWaitMillis", "10000")
        resource.setProperty("useLegacyDatetimeCode", "false")
        resource.setProperty("serverTimezone", "America/Los_Angeles")
        return resource
    }*/
}