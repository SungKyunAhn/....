#!/bin/sh
#UTF-8
nohup mvn -e -f pom-bems.xml antrun:run -DfepName=BEMS1 -DjmxPort=1599 -DcommPort=7003 -DrelayServerIp=localhost -DrelayServerPort=0 2>&1 > /dev/null &
