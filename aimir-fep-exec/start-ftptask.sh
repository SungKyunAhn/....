#!/bin/bash
#UTF-8
. $HOME/.bash_profile

cd $HOME/aimiramm/aimir-fep-exec/
mvn -e -f pom-ftptask.xml antrun:run -DfepName=FTPTASK 2>&1 > /dev/null &
#mvn -e -f pom-ftptask.xml antrun:run -DfepName=FTPTASK 2>&1 > ftp_run_tmp.log &
