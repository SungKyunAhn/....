#!/bin/sh
#UTF-8
nohup mvn -e -f pom-fepd.xml antrun:run -DfepName=FEP1 -DjmxPort=1299 > /home/iot_integration/3.6/aimir-fep-exec/start-fepd.log 2>&1 &
#nohup mvn -e -f pom-fepd.xml antrun:run -DfepName=FEP1 -DjmxPort=1299 > /dev/null 2>&1 &
