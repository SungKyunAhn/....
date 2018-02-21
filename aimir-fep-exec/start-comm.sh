#!/bin/sh
#UTF-8
export AIMIR_HOME=/home/aimir3
export CARGO_HOME=$AIMIR_HOME/tomcat
export JAVA_HOME=/usr/local/jdk
export M2_HOME=/usr/local/maven
export MAVEN_OPTS=-Xmx1024m
export DERBY_HOME=/disk1/aimir/db-derby-10.6.1.1-bin
export PATH=$M2_HOME/bin:$JAVA_HOME/bin:DERBY_HOME/bin:$PATH

nohup mvn -e -f pom-comm.xml antrun:run -DcommName=COMM  -DcommPort=7003 2>&1 > debug1.log & 
