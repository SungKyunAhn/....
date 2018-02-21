#!/bin/sh
#UTF-8
nohup mvn -e -f pom-CommandBatch_MOE.xml antrun:run -DthreadCount=5 -DkeepAliveTime=1 -DcommandType=cmdGetCurrentLoadLimit -Dinterval=5 2>&1 > /dev/null &