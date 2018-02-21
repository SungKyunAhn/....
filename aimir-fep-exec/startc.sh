#!/bin/sh
#UTF-8
nohup mvn -o antrun:run -DmoaName=MOA_COMMAND1 -DnasConfigName=all -DhttpPort=19191 -DrmiPort=12199 -DserverIp=172.30.152.172 -DserverPort=1099 -DcommPort=18001 -DjmxrmiPort=12299 -Duser.timezone=GMT+1 2>&1 > /dev/null &
