@echo off

mvn -e -f pom-comm.xml antrun:run -DcommName=COMM  -DcommPort=7003