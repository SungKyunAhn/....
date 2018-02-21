#!/bin/sh
#UTF-8
nohup mvn -e -f pom-feph.xml antrun:run -DfepName=FEP1 -DcommPort=8000 -DbypassPort=8900 -DjmxPort=1199 > /home/iot_integration/3.6/aimir-fep-exec/start-feph.log 2>&1 &
#nohup mvn -e -f pom-feph.xml antrun:run -DfepName=FEP1 -DcommPort=8000 -DbypassPort=8900 -DjmxPort=1199 > /dev/null 2>&1 &
