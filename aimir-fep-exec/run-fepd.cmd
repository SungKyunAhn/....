@echo on
mvn -o -e -f pom-fepd.xml antrun:run -DfepName=FEP1 -DjmxPort=1299