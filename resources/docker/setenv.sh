#!/usr/bin/env bash
#echo "Setting parameters from $CATALINA_HOME/bin/setenv.sh"
#echo "_______________________________________________"

export CATALINA_OPTS="$CATALINA_OPTS -DdB_USERNAME=$USERNAME"
export CATALINA_OPTS="$CATALINA_OPTS -DdB_PW=$PASSWORD"
export CATALINA_OPTS="$CATALINA_OPTS -DdB_HOST=$HOST"
export CATALINA_OPTS="$CATALINA_OPTS -DdB_PORT=$PORT"
export CATALINA_OPTS="$CATALINA_OPTS -DdB_QUERY=$QUERY"
export CATALINA_OPTS="$CATALINA_OPTS -DdB_DBNAME=$DBNAME"
export CATALINA_OPTS="$CATALINA_OPTS -DMAXFILESIZE=$MAXFILESIZE"
export CATALINA_OPTS="$CATALINA_OPTS -Dgrails.serverURL=$SERVER_URL"
export CATALINA_OPTS="$CATALINA_OPTS -Xmx$TOMCAT_MAX_MEM -Xms$TOMCAT_MIN_MEM"
export CATALINA_OPTS="$CATALINA_OPTS -DISSUES_SECRET=$ISSUES"
export CATALINA_OPTS="$CATALINA_OPTS -DSECURE=$SECURE"
export CATALINA_OPTS="$CATALINA_OPTS -DDATA_URL=$DATA_URL"

#echo "Using CATALINA_OPTS:"
#for arg in $CATALINA_OPTS
#do
#    echo ">> " $arg
#done
#echo ""
