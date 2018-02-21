#!/bin/sh
#UTF-8
# 타겟 파일명 : EMnVLPRecoveryBatch_list.txt

nohup mvn -e -f pom-EMnVLPRecoveryBatch.xml antrun:run 2>&1 > /dev/null &

