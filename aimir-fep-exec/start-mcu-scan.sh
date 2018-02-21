#!/bin/sh
#UTF-8
# searchType : 0 (all), 1 (MCU SID), 2 (MCU IP ADDRESS)
# target file name : mcu_scanning_list.txt            

nohup mvn -e -f pom-McuScanning.xml antrun:run -DsearchType=0 2>&1 > /dev/null &