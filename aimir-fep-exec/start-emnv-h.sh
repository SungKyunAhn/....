#!/bin/sh
#UTF-8
nohup mvn -e -f pom-emnv-h.xml antrun:run -DfepName=EMnVCommandServer-h -DmnvPort=8199 -DjmxPort=1199 2>&1 > /dev/null &
