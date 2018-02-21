@echo on
mvn -o -e -f pom-new.xml antrun:run -DfepName=FEP1 -DcommPort=8000 -DbypassPort=8900 -DjmxPort=1199
rem mvn -e antrun:run -DfepName=FEP2 -DcommPort=8002 -DbypassPort=8901 -DjmxPort=1399
