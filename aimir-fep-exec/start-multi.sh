#!/bin/sh
#UTF-8
PID=(`/bin/ps -eaf | /bin/grep java | /bin/grep log4j-multispeak | /bin/awk '{print $2}'`)
PLEN=${#PID[@]}

if [ $PLEN -eq 1 ]
then 
    echo "Multispeak alive, try to stop"
else
    while [ $PLEN -eq 0 ]
    do
        mvn -e -f pom-multispeak.xml antrun:run -DfepName=Multispeak -Dport=8089 -DjmxPort=1399 2>&1 > /dev/null &
        echo "start multispeak"
        sleep 30
        PID=(`/bin/ps -eaf | /bin/grep java | /bin/grep log4j-multispeak | /bin/awk '{print $2}'`)
        PLEN=${#PID[@]}
        echo "Multispeak $PLEN"
    done
fi
