
ARG OPENJDK_IMAGE_VERSION=11
ARG TOMCAT_IMAGE_VERSION=8-jre11
ARG GRAILS_VERSION=4.0.3

FROM openjdk:$OPENJDK_IMAGE_VERSION as buildwar

ARG GRAILS_VERSION

# Install Grails
WORKDIR /opt

# TODO put grails zips on your own server with decent bandwidth
RUN wget https://github.com/grails/grails-core/releases/download/v$GRAILS_VERSION/grails-$GRAILS_VERSION.zip && \
    unzip grails-$GRAILS_VERSION.zip && \
    rm -rf grails-$GRAILS_VERSION.zip && \
    ln -s grails-$GRAILS_VERSION grails

# Setup Grails path.
ENV GRAILS_HOME /opt/grails

ENV PATH $GRAILS_HOME/bin:$PATH

# Copy the app into Docker
COPY . /tmp/CavaMedia

WORKDIR /tmp/CavaMedia

RUN grails -version

# Build the WAR
RUN bash /tmp/CavaMedia/resources/docker/setup.sh

# Install Tomcat
#FROM tomcat:7.0.88-jre8
FROM tomcat:$TOMCAT_IMAGE_VERSION

# Setup DB Credentials
ENV USERNAME ''

ENV PASSWORD ''

ENV HOST ''

ENV PORT ''

ENV DBNAME ''

ENV WPUSER ''

ENV WPPASSWORD ''

ENV SERVER_URL ''

ENV ISSUES ''

ENV SECURE 'false'

ENV TOMCAT_MIN_MEM '2048M'

ENV TOMCAT_MAX_MEM '2048M'

ENV LOG4J_NO_LOOKUPS 'TRUE'

# Setup Tomcat configuration files
COPY resources/docker/context.xml $CATALINA_HOME/conf/context.xml

COPY resources/docker/server.xml $CATALINA_HOME/conf/server.xml

COPY resources/docker/web.xml $CATALINA_HOME/conf/web.xml

COPY resources/docker/setenv.sh $CATALINA_HOME/bin/setenv.sh

RUN chmod +x $CATALINA_HOME/bin/setenv.sh

# Deploy the WAR in Tomcat
COPY --from=buildwar /tmp/CavaMedia/build/libs/*.war $CATALINA_HOME/webapps/
