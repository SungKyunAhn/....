@echo on
mvn -e -f pom-feph.xml antrun:run -DfepName=FEP1 -DcommPort=8000 -DbypassPort=8901 -DjmxPort=1199
