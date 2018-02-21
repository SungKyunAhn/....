package com.aimir.fep.command.mbean;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;

import com.aimir.fep.meter.data.MeterData;
import com.aimir.fep.modem.LPData;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.exception.FMPMcuException;
import com.aimir.fep.protocol.fmp.frame.service.entry.drLevelEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.endDeviceEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.idrEntry;
import com.aimir.fep.util.GroupInfo;
import com.aimir.model.device.MCU;
import com.aimir.model.device.MCUCodi;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;

/**
 * Command Proxy MBean which execute to MCU
 *
 * @author Y.S Kim
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public interface CommandBOMBean
{
    /**
     * start CommandGW
     */
    public void start() throws Exception;

    /**
     * get CommandGW ObjectName String
     */
    public String getName();

    /**
     * stop CommandGW
     */
    public void stop();

    public String cmdAidonMccb(String mcuId, String meterId, String req)
    throws Exception;

    public Object[] cmdKamstrupCID(String mcuId, String meterId, String[] req)
    throws Exception;

    public Object[] cmdKamstrupCID(String mcuId, String meterId, String kind, String[] req)
    throws Exception;

    public Map<String, String> getRelaySwitchStatus( String mcuId, String meterId ) throws Exception;

    public Map<String, String> cmdRelaySwitchAndActivate( String mcuId, String meterId, int cmdNum) throws Exception;

    public MCU doMCUScanning(CommandGW gw, MCU mcu) throws Exception;
    
    public MCU doMCUScanning(String mcuId) throws Exception;

    /**
     * readcount - log count (max 50)
     * @param mcuId
     * @param modemId
     * @return
     * @throws Exception
     */
    @SuppressWarnings({ "unchecked", "unused"})
    public Map getModemBatteryLog(String mcuId, String modemId, int readcount,
            int serviceType, String operator)
    throws Exception;

    /**
     * readcount - event count (max 250)
     * @param mcuId
     * @param modemId
     * @param count
     * @return
     * @throws Exception
     */
    public Map getModemEventLog(String mcuId, String modemId, int readcount,
            int serviceType, String operator)
    throws Exception;

    /**
     * day lp days
     * ex) day 1 ==> current day
     * ex) day 2 ==> current day, yesterday
     * @param mcuId
     * @param modemId
     * @param day
     * @return
     * @throws Exception
     */
    public LPData[] getModemLPLog(String mcuId, String modemId, int day)
        throws Exception;

    /**
     * 
     * @param gw
     * @param modem
     * @param mcuId
     * @param modemId
     * @param serviceType
     * @param operator
     * @return resultStatus:ResultStatus, 비동기 처리(commandMethod:AsynchronousCall, trId) 동기처리(commandMethod:SynchronousCall, modem:모뎀객체)
     * @throws Exception
     */
    public Hashtable doGetModemROM(CommandGW gw, Modem modem,
            String mcuId, String modemId, int serviceType, String operator)
    throws Exception;

    public void registerMCU(MCU mcu) throws Exception;

    public void registerModem(Modem modem) throws Exception;

    /**
     * 집중기 상태 점검
     * 
     * @param mcuId
     * @return Hashtable에 정전상태(gpioPowerFail), LowBattery상태(gpioLowBattery), 
     *    <br>플래쉬 메모리 총 용량(flashTotalSize), 사용중인 플래쉬 메모리 용량(flashUseSize),
     *    <br>메모리 총 용량(memTotalSize), 사용중인 메모리 용량(memUseSize)
     *    <br>시스템온도(sysCurTemp), 현재시각(sysTime), 코디네이터 장치 정보(codiDevice)
     * @throws Exception
     */
    public Hashtable getMCUStatus(String mcuId) throws Exception;

    /**
     * @param mcuId
     * @param modemId
     * @param serviceType
     * @param operator
     * @return hashtable resultStatus:ResultStatus, 비동기 처리(commandMethod:AsynchronousCall, trId) 동기처리(commandMethod:SynchronousCall, modem:모뎀객체)
     * @throws Exception
     */
    public Hashtable getModemStatus(String mcuId,String modemId, int serviceType, String operator)
    throws Exception;

    public void timeSynchronization(String mcuId) throws Exception;

    public void mcuReset(String mcuId) throws Exception;

    /**
     * 온디맨드
     * @param meter
     * @param serviceType
     * @param operator
     * @return resultStatus:(Success:Fail), commandResult:(AsynchronousCall|SynchronousCall)
     * <br> 결과가 비동기 처리인 경우 transactionId:트랜잭션아이디
     * <br> 결과가 동기인 경우 ondemandResult:파싱된 해쉬 데이타(LinkedHashMap), meteringValue:현재값 
     * @throws Exception
     */
    public Hashtable doOnDemand(Meter meter, int serviceType, String operator) throws Exception;

    public MeterData[] getSavedMeteringDataInMCU(String mcuId, String meterId,
            String fromTime, String toTime) throws Exception;
    
    public  Hashtable getMCUDiagnosis(String mcuId) throws Exception;

    public Object cmdGetMeterSchedule(String mcuId, String modemId, int nOption, int nOffset, int nCount)
        throws Exception;

    public void cmdMcuSetDST(String mcuId, String fileName) throws Exception;

    public long cmdMcuSetGMT(String mcuId) throws Exception;

    public void cmdMcuSetConfiguration(String mcuId) throws Exception;

    public Map<String, String> cmdMcuGetConfiguration(String mcuId) throws Exception;

    public String[] cmdMeterTimeSync(String mcuId, String meterId) throws Exception;

    public MCUCodi[] findCodi(String mcuId) throws Exception;

    public void setDefaultMcuConfig(String mcuId) throws Exception;

    public Hashtable getDefaultMcuConfig(String className) throws Exception;

    public void setInstallDate(MCU mcu, String time)
        throws Exception;

    public void setLastTimeSyncDate(MCU mcu, String time)
        throws Exception;

    public void setInstallDate(MCU[] mcus, String time)
        throws Exception;

    /**
     * get Equip Version Information
     * @param equipKind
     * @param mcuId
     */
    public boolean getEquipVersion(int equipKind,String triggerId, String mcuId);

    /**
     * @param triggerId
     * @param mcuId
     * @return Modem List
     * @throws Exception
     */
    public Modem[] getModemVersion(String triggerId, String mcuId) throws Exception;

    /**
     * @param gw
     * @param triggerId
     * @param mcuId
     * @throws Exception
     */
    public MCU getMCUVersion(String triggerId, String mcuId) throws Exception;

    /**
     * @param gw
     * @param triggerId
     * @param mcuId
     * @throws Exception
     */
    public MCUCodi[] getCodiVersion(String triggerId, String mcuId) throws Exception;

    /**
     * send message for alarm unit installation
     * 2009.11.26
     * @param unitId        alarm unit identification
     * @param installDate   installation date
     * @throws JMSException
     */
    public void sendInstallAlarmUnit(String unitId, String installDate)
    throws Exception;
    
    /**
     * send event or alarm
     * 2009.12.07
     * 
     * @param unitId        alarm unit identification
     * @param eventId       event id
     * @param eventStatus   event status (4 bytes)
     */
    public void sendAlarmEvent(String unitId, Byte eventId, Integer eventStatus,
            String timestamp) throws Exception;
    
    /**
     * send alarm unit connection status
     * 
     * @param unitId        alarm unit identification
     * @param status        false:no connection, true:connection restore
     * @param timestamp     event timestatmp
     * @throws Exception
     */
    public void sendAlarmUnitConnectionStatus(String unitId, boolean status, String timestamp)
    throws Exception;
    
    
    /**
     * (102.53)
     * 그룹의 멤버에 비동기 명령을 수행한다
     * @param mcuId
     * @param groupKey
     * @param command
     * @param option - 0x01 : ASYNC_OPT_RETURN_CODE_EVT
                     - 0x02 : ASYNC_OPT_RESULT_DATA_EVT
                     - 0x10 : ASYNC_OPT_RETURN_CODE_SAVE
                     - 0x20 : ASYNC_OPT_RESULT_DATA_SAVE
     * @param day Keep Option
     * @param nice Request
     * @param ntry
     * @return trId
     * @throws Exception
     */
    public long cmdGroupAsyncCall(String mcuId, int groupKey, String command, int nOption, int nDay, int nNice, int nTry, List<SMIValue> param)
	throws Exception;
    
    /**
     * (102.54) 그룹을 추가한다
     * @param mcuId
     * @param groupName
     * @throws Exception
     */
    public void cmdGroupAdd(String mcuId, String groupName) 	
    throws Exception;
    
    /**
     * (102.55) 그룹을 삭제한다
     * @param mcuId
     * @param groupKey
     * @throws Exception
     */
    public void cmdGroupDelete(String mcuId, int groupKey) 
    throws Exception;
    
    /**
     * (102.56) 그룹에 멤버를 추가한다
     * @param mcuId
     * @param groupKey
     * @param modemId
     * @throws Exception
     */
    public void cmdGroupAddMember(String mcuId, int groupKey, String modemId)
    throws Exception;
    
    /**
     * (102.57) 그룹에 멤버를 삭제한다
     * @param mcuId
     * @param groupKey
     * @param modemId
     * @throws Exception
	 *	  		IF4ERR_INVALID_PARAM		: 잘못된 Parameter가 전달 
	 *	  		IF4ERR_GROUP_DB_OPEN_FAIL		: 그룹 DB의 Open 실패
	 *	  		IF4ERR_TRANSACTION_UPDATE_FAIL	: Transaction 갱신 실패
	 *	  		IF4ERR_TRANSACTION_DELETE_FAIL	: Transaction 삭제 실패
	 *	  		IF4ERR_GROUP_INVALID_MEMBER	: 해당 그룹에 멤버가 존재하지 않음
     */
    public void cmdGroupDeleteMember(String mcuId, int groupKey, String modemId) 
    throws Exception;
    
    
    /**
     * (102.58) 현재 그룹 정보 전체를 조회한다
     * @param mcuId
     * @param modemId
     * @throws Exception
     */
    public GroupInfo[] cmdGroupInfo(String mcuId) throws Exception;
    
    /**
     * (102.58) 현재 그룹 정보를 그룹명으로 조회한다
     * @param mcuId
     * @param groupKey
     * @param modemId
     * @throws Exception
     */
    public GroupInfo[] cmdGroupInfo(String mcuId, int groupKey) throws Exception;
    
    /**
     * (102.58) 현재 그룹 정보를 모뎀이 속한 정보로 검색한다.
     * @param mcuId
     * @param groupName
     * @param modemId
     * @param bSearchId - 파라미터가 true이면 모뎀이 속한 그룹을 조회
     * @throws FMPMcuException
     * @throws Exception
	 *	  IF4ERR_GROUP_DB_OPEN_FAIL		: 그룹 DB의 Open 실패
     */
    public GroupInfo[] cmdGroupInfo(String mcuId, String modemId, boolean bSearchId) throws FMPMcuException, Exception;

    
	/**
     * (104.14)DR 대상 정보 리스트
     * @param mcuId
     * @param sensorId
     * @param parser
     * @throws Exception
     */
	public List cmdGetDRAssetInfo(String mcuId, String sensorId, String parser) throws Exception;
	
	/**
     * (104.15)DR 대상 레벨 요청
     * @param mcuId
     * @param sensorId
     * @throws Exception
     */
	public byte cmdGetEnergyLevel(String mcuId, String sensorId) throws Exception;
	
	/**
     * (104.16)DR 대상 레벨 수정
     * @param mcuId
     * @param sensorId
     * @throws Exception
     */
	public void cmdSetEnergyLevel(String mcuId, String sensorId, String level) throws Exception;

	/**
	  * cmdSendMessage
	  * 111.4
	  * @param id TargetId
	  * @param nMessageId Message ID
	  * @param nMessageType Message Type
	  * @param nDuration Lazy, Passive
	  * @param nErrorHandler Error handler Action Code
	  * @param nPreHandler Pre-Action Handler
	  * @param nPostHandler Post-Action Handler
	  * @param nUseData User Data
	  * @param pszData Message
	  * @throws Exception
	*/
	public void cmdSendMessage(String mcuId, String sensorId, int messageId, int messageType, int duration,  int errorHandler, int preHandler, int postHandler, int userData, String pszData ) throws Exception;
			
	
	
	/**
     * (130.1)DR 프로그램 참여 유도
     * @param mcuId
     * @param drLevelEntry
     * @throws FMPMcuException
     * @throws Exception
     */
    public void cmdDRAgreement(String mcuId, drLevelEntry drLevelEntry) throws FMPMcuException,Exception;		
	
    /**
	 * (130.2)DR 취소 메시지
	 * @param mcuId
	 * @param deviceId
	 * @throws FMPMcuException
	 * @throws Exception
	 */
	public void cmdDRCancel(String mcuId, String deviceId) throws FMPMcuException,Exception;
	
	/**
	 * (130.3)Incentive DR Start
	 * @param mcuId
	 * @param idrEntry
	 * @throws FMPMcuException
	 * @throws Exception
	 */
	public void cmdIDRStart(String mcuId, idrEntry idrEntry) throws FMPMcuException,Exception;	
	
	/**
	 * (130.4)Incentive DR Cancel
	 * @param mcuId
	 * @param eventId
	 * @throws FMPMcuException
	 * @throws Exception
	 */
	public void cmdIDRCancel(String mcuId, String eventId) throws FMPMcuException,Exception;
	
	/**
	 * (130.5)DR Level Monitoring 장비의 DR Level 조회
	 * @param mcuId
	 * @param deviceId
	 * @return
	 * @throws FMPMcuException
	 * @throws Exception
	 */
	public endDeviceEntry cmdGetDRLevel(String mcuId, String deviceId) throws FMPMcuException,Exception;
	
	/**
	 * (130.6)DR Level Control DR Level 제어
	 * @param mcuId
	 * @param endDeviceEntry
	 * @throws FMPMcuException
	 * @throws Exception
	 */
	public void cmdSetDRLevel(String mcuId, endDeviceEntry endDeviceEntry) throws FMPMcuException,Exception;	
	
	/**
	 * (130.7)가전기기 스마트 기기 제어
	 * @param mcuId
	 * @param serviceId
	 * @param deviceId
	 * @param eventId
	 * @param drLevel
	 * @throws FMPMcuException
	 * @throws Exception
	 */
	public void cmdEndDeviceControl(String mcuId, String serviceId, String deviceId, String eventId, String drLevel) throws FMPMcuException, Exception;
	
	//TODO 파라미터가 deviceId인 경우가 있고, groupName인 경우가 있는데 어떻게 구분할까?
	/**
	 * (130.8)DR 자원 정보 요청
	 * @param mcuId
	 * @param deviceId
	 * @return
	 * @throws FMPMcuException
	 * @throws Exception
	 */
	public endDeviceEntry cmdGetDRAsset(String mcuId, String deviceId) throws FMPMcuException, Exception;
}
