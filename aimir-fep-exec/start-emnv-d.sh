#!/bin/sh
#UTF-8
nohup mvn -e -f pom-emnv-d.xml antrun:run -DfepName=EMnVCommandServer-d -DjmxPort=1299 -DenableWS=true 2>&1 > /dev/null & 

