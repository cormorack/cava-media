
ARG OPENJDK_IMAGE_VERSION=11
ARG TOMCAT_IMAGE_VERSION=9.0.118-jre11-temurin-noble
ARG GRAILS_VERSION=6.2.3

FROM eclipse-temurin:11-jdk-alpine AS buildwar
RUN apk update && apk add --no-cache bash

ARG GRAILS_VERSION

# Install Grails
WORKDIR /opt

# TODO put grails zips on your own server with decent bandwidth
RUN wget https://github.com/grails/grails-core/releases/download/v$GRAILS_VERSION/grails-$GRAILS_VERSION.zip && \
    unzip grails-$GRAILS_VERSION.zip && \
    rm -rf grails-$GRAILS_VERSION.zip && \
    ln -s grails-$GRAILS_VERSION grails

# Setup Grails path.
ENV GRAILS_HOME=/opt/grails

ENV PATH=$GRAILS_HOME/bin:$PATH

# Copy the app into Docker
COPY . /tmp/CavaMedia

WORKDIR /tmp/CavaMedia

RUN grails -version

# Build the WAR
RUN bash /tmp/CavaMedia/resources/docker/setup.sh

# Install Tomcat
FROM tomcat:$TOMCAT_IMAGE_VERSION

# Setup DB Credentials
ENV USERNAME=''

ENV PASSWORD=''

ENV HOST=''

ENV PORT=''

ENV DBNAME=''

ENV WPUSER=''

ENV WPPASSWORD=''

ENV SERVER_URL=''

ENV DATA_URL=''

ENV ISSUES=''

ENV SECURE='false'

ENV TOMCAT_MIN_MEM='1024M'

ENV TOMCAT_MAX_MEM='1024M'

ENV QUERY=''

ENV LOG4J_NO_LOOKUPS='TRUE'

ENV FEEDBACK_HOST=''

# Setup Tomcat configuration files
COPY resources/docker/context.xml $CATALINA_HOME/conf/context.xml

COPY resources/docker/server.xml $CATALINA_HOME/conf/server.xml

COPY resources/docker/web.xml $CATALINA_HOME/conf/web.xml

COPY resources/docker/setenv.sh $CATALINA_HOME/bin/setenv.sh

RUN chmod +x $CATALINA_HOME/bin/setenv.sh

# Deploy the WAR in Tomcat
COPY --from=buildwar /tmp/CavaMedia/build/libs/media.war $CATALINA_HOME/webapps/
