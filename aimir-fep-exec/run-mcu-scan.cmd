@echo on
mvn -o -e -f pom-EMnVSelectiveLP.xml antrun:run -DstartIdx=0 -DendIdx=3
