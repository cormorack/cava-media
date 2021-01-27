# /bin/bash

if [[ -f build/.dependencies ]];then 
    rm build/.dependencies; 
fi

grails prod war