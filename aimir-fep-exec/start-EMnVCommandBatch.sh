#!/bin/sh
# #UTF-8

# 타겟 파일명 : EMnVCommandBatch_list.txt
#   - cmdOnDemand, cmdSetMeterTime, cmdSetMeterScan : 미터아이디 목록필요 
#   - cmdInverterInfo, cmdInverterSetup : 인버터아이디 목록 필요
#   - 나머지는 모뎀전화번호 목록 필요
# commandType : 명령
# 	EMnV Command 종류
#  	 cmdOTAStart
#  	 cmdServerIp
#	 cmdServerPort
#	 cmdLPInterval
#	 cmdHWResetInterval
# 	 cmdNVReset
#	 cmdMNumber
#	 cmdHWReset
#	 cmdEventLog
#	 cmdKeyChange
# 	 cmdOnDemand      
# 	 cmdSetMeterTime
# 	 cmdSetMeterScan
# 	 cmdInverterInfo
# 	 cmdInverterSetup
# param1, param2, interval : 파라미터
#
#  ex) cmdOTAStart 의 경우
#      param1=0 : F/W 파일명
#    nohup mvn -e -f pom-EMnVCommandBatch.xml antrun:run -DcommandType=cmdOTAStart -Dparam1=NURI-TELECOM,NURI_MODEM,Kepco_V010.bin 2>&1 > /dev/null &
#
#  ex) cmdOnDemand 의 경우
#      param1=0 : LP_index 0번 부터
#      param2=3 : LP_index 3번 까지 400개를 가져와라~!
#      interval : sms 보내는 주기. 분단위
#    nohup mvn -e -f pom-EMnVCommandBatch.xml antrun:run -DcommandType=cmdOnDemand -Dinterval=10 -Dparam1=1 -Dparam2=3 2>&1 > /dev/null &
#      0~3번까지 실행한뒤 0번 LP Index를 한번더 실행함. 이유는 Ondemand를 오랫동안할경우 정규 검침을 한번도 저장하지 못해서
#      0번 LP Index가 저장되지 못하고 비어버리는 현상이 발생하기 때문                                           

nohup mvn -e -f pom-EMnVCommandBatch.xml antrun:run -DcommandType=cmdOnDemand -Dinterval=5 -Dparam1=0 -Dparam2=89 2>&1 > /dev/null &
#nohup mvn -e -f pom-EMnVCommandBatch.xml antrun:run -DcommandType=cmdOTAStart -Dparam1=NURI-TELECOM,NURI_MODEM,Kepco_V0015.bin 2>&1 > /dev/null &

##### 테스트 코드  ###
# mvn -e -f pom-EMnVCommandBatch.xml antrun:run -DcommandType=cmdSetMeterTime
# mvn -e -f pom-EMnVCommandBatch.xml antrun:run -DcommandType=cmdSetMeterScan

# mvn -e -f pom-EMnVCommandBatch.xml antrun:run -DcommandType=cmdServerIp -Dparam1=211.232.103.234
# mvn -e -f pom-EMnVCommandBatch.xml antrun:run -DcommandType=cmdServerIp -Dparam1=125.141.144.150

# mvn -e -f pom-EMnVCommandBatch.xml antrun:run -DcommandType=cmdServerPort -Dparam1=8198
# mvn -e -f pom-EMnVCommandBatch.xml antrun:run -DcommandType=cmdServerPort -Dparam1=8199

# mvn -e -f pom-EMnVCommandBatch.xml antrun:run -DcommandType=cmdLPInterval -Dparam1=3
# mvn -e -f pom-EMnVCommandBatch.xml antrun:run -DcommandType=cmdLPInterval -Dparam1=1

# mvn -e -f pom-EMnVCommandBatch.xml antrun:run -DcommandType=cmdHWResetInterval -Dparam1=120
# mvn -e -f pom-EMnVCommandBatch.xml antrun:run -DcommandType=cmdHWResetInterval -Dparam1=60

# mvn -e -f pom-EMnVCommandBatch.xml antrun:run -DcommandType=cmdHWReset

# mvn -e -f pom-EMnVCommandBatch.xml antrun:run -DcommandType=cmdNVReset
