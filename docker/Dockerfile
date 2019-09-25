
FROM openjdk:8 as buildwar

# Set customizable env vars defaults.
# Set Grails version
ENV GRAILS_VERSION 3.2.11

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
COPY src/backend/CavaMedia /tmp/CavaMedia

WORKDIR /tmp/CavaMedia

RUN grails -version

# Build the WAR
RUN bash /tmp/CavaMedia/docker/setup.sh

# Install Tomcat
FROM tomcat:7.0.88-jre8

# Setup DB Credentials
ENV USERNAME ''

ENV PASSWORD ''

ENV HOST ''

ENV PORT ''

ENV DBNAME ''

ENV WPUSER ''

ENV WPPASSWORD ''

ENV SERVER_URL ''

# Setup Tomcat configuration files
COPY src/backend/CavaMedia/docker/context.xml $CATALINA_HOME/conf/context.xml

COPY src/backend/CavaMedia/docker/server.xml $CATALINA_HOME/conf/server.xml

COPY src/backend/CavaMedia/docker/web.xml $CATALINA_HOME/conf/web.xml

COPY src/backend/CavaMedia/docker/setenv.sh $CATALINA_HOME/bin/setenv.sh

RUN chmod +x $CATALINA_HOME/bin/setenv.sh

# Deploy the WAR in Tomcat
COPY --from=buildwar /tmp/CavaMedia/build/libs/*.war $CATALINA_HOME/webapps/
