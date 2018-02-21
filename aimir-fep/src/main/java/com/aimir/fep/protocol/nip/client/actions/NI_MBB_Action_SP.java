package com.aimir.fep.protocol.nip.client.actions;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.OTATargetType;
import com.aimir.constants.CommonConstants.TR_STATE;
import com.aimir.dao.device.AsyncCommandLogDao;
import com.aimir.dao.device.AsyncCommandParamDao;
import com.aimir.dao.device.AsyncCommandResultDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.system.ObisCodeDao;
import com.aimir.fep.bypass.BypassDevice;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.meter.data.MeterData;
import com.aimir.fep.meter.parser.DLMSKaifa;
import com.aimir.fep.meter.parser.MeterDataParser;
import com.aimir.fep.meter.saver.DLMSKaifaMDSaver;
import com.aimir.fep.protocol.fmp.common.Target;
import com.aimir.fep.protocol.nip.CommandNIProxy;
import com.aimir.fep.protocol.nip.client.NiClient;
import com.aimir.fep.protocol.nip.client.batch.CallableBatchExcutor.CBE_RESULT_CONSTANTS;
import com.aimir.fep.protocol.nip.client.batch.CallableBatchExcutor.CBE_STATUS_CONSTANTS;
import com.aimir.fep.protocol.nip.client.batch.job.SORIAMeterOTACallable;
import com.aimir.fep.protocol.nip.client.batch.job.SORIAModemOTACallable;
import com.aimir.fep.protocol.nip.client.bypass.BypassClient;
import com.aimir.fep.protocol.nip.client.bypass.BypassResult;
import com.aimir.fep.protocol.nip.command.NullBypassOpen;
import com.aimir.fep.protocol.nip.command.RomRead;
import com.aimir.fep.protocol.nip.command.RomReadDataReq;
import com.aimir.fep.protocol.nip.frame.GeneralFrame;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.NIAttributeId;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.smsp.SMSConstants.COMMAND_TYPE;
import com.aimir.fep.util.CmdUtil;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.*;
import com.aimir.model.system.DeviceConfig;
import com.aimir.model.system.DeviceModel;
import com.aimir.util.Condition;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.StringUtil;
import net.sf.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionStatus;

import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author HanSeJin
 */
//@Service(value="NI_MBB_Action_SP")
public class NI_MBB_Action_SP  {
    private String regexIPv4 = "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$";
    private static Log log = LogFactory.getLog(NI_MBB_Action_SP.class);
    private IoSession session;
    private GeneralFrame generalFrame;
    private BypassDevice bypassDevice;
    private BypassClient bclient;
    private String destIpAddress = "";

    public void executeAction(IoSession _session, GeneralFrame _gFrame) throws Exception {
        this.session = _session;
        this.generalFrame = _gFrame;

        /*
         * Procedure
         * 1. Search AsyncCommandLog
         * 2. Change ACL state
         * 3. Get a last ACL which has running state
         */
        searchAsyncCommandLog();
        
        // Get CommandCode from param
        HashMap<String, Object> hashMap = bypassDevice.getArgMap();
        if(hashMap == null || hashMap.isEmpty())
        	throw new Exception ("AsyncCommandLog is null");
        
        String commandCode = (String)hashMap.get("CommandCode");
        String commandName = bypassDevice.getCommand();
        
        if (commandCode == null || commandName == null)
            throw new Exception ("Command code[" + commandCode + "] or name[" + commandName + "] is null");
        
        // Select the Protocol = NI or COAP        
        if( commandCode.contains(COMMAND_TYPE.NI.getTypeCode()) ){
            if (commandName.equals("cmdMeterParamAct") 
                    || commandName.equals("cmdMeterParamGet")
                    || commandName.equals("cmdMeterParamSet")
                    || commandName.equals("cmdOnDemandMeter")) {
                //미터커맨드는 OBISCODE 포함 여부 확인
                //commandName.startsWith("cmdMeterParamAct")
                //commandName.startsWith("cmdMeterParamGet")
                //commandName.startsWith("cmdMeterParamSet")
                //commandName.startsWith("cmdOnDemandMeter")
                callMeterCommand();
            } else if(commandName.equals("cmdGetMeterFWVersion")){ 
                callMeterMeterFWVersionCommand(bypassDevice.getArgMap());
            } else if(commandName.equals("cmdSORIAGetMeterKey")){ 
                callSORIAGetMeterKeyCommand(bypassDevice.getArgMap());
            } else if(commandName.equals("cmdMeterOTAStart")){
                callOTACommand(OTATargetType.METER, bypassDevice.getModemId(), bypassDevice.getArgMap());
            } else if(commandName.equals("cmdModemOTAStart")){
                callOTACommand(OTATargetType.MODEM, bypassDevice.getModemId(), bypassDevice.getArgMap());
            } else if(commandName.equals("cmdGetROMRead")){
                callROMReadCommand(bypassDevice.getArgMap());
                // INSERT START SP-279
            } else if(commandName.equals("cmdSyncTime")){
                callMeterSyncTimeCommand();             
                // INSERT END SP-279
            } else {
                 /*
                 * NI
                 * 4. Make the GeneralFrameData
                 * 5. session.write
                 * */             
                callNiAsyncCommand(); 
            }
             
        }
        else if( commandCode.contains(COMMAND_TYPE.COAP.getTypeCode()) ){
            /* 
             * COAP
             * Command List : coapGetInfo, modemReset, coapPing
             * 4. Get Parameter Per Command Name
             * 5. Call the Coap Function
             */
             
            callCoapCommand(bypassDevice);      
        }else if( commandCode.contains(COMMAND_TYPE.SNMP.getTypeCode()) ){
            /*
             * SNMP
             * Command List : snmpDaemon
             */
            callSnmpAsyncCommand();
        }else
            throw new Exception ("CommandCode[" + commandCode + "] is invalid");

    }


    /** TR_STATE Information
     * TR_STATE.Success   (0) : Command 수행 성공및 정상종료 상태
     * TR_STATE.Waiting   (1) : 서버측에서 SMS전송후 모뎀의 접속을 기다리는 상태
     * TR_STATE.Running   (2) : Command 수행중인 상태
     * TR_STATE.Terminate (4) : Command 가 성공하지 못한 상태에서 종료된 상태 (ex. crc error)
     * TR_STATE.Delete    (8) : 다른 Tranasction의 동일 Command가 실행되어(단지 실행) 삭제처리된 상태
     * TR_STATE.Unknown (255) : 아몰랑.
     */
    public void searchAsyncCommandLog(){
        // Init TargetDevice
        bypassDevice = (BypassDevice) session.getAttribute(session.getRemoteAddress());
        if (bypassDevice == null) bypassDevice = new BypassDevice();
        
        bypassDevice.setModemId(Hex.decode(generalFrame.getSrcAddress()));
        log.debug("[NI:MBB_Action]Remote Address= " + session.getRemoteAddress().toString());
        this.destIpAddress = session.getRemoteAddress().toString();
        JpaTransactionManager txmanager = (JpaTransactionManager)DataUtil.getBean("transactionManager");
        TransactionStatus txstatus = null;

        AsyncCommandLogDao aclDao = null;
        aclDao = DataUtil.getBean(AsyncCommandLogDao.class);
        Set<Condition> condition = null;

        // query async command from web
        List<AsyncCommandLog> aclList = null;
        try{
            txstatus = txmanager.getTransaction(null);
            condition = new LinkedHashSet<Condition>();
            condition.add(new Condition("id.mcuId", new Object[] { bypassDevice.getModemId() }, null, Condition.Restriction.EQ));
            condition.add(new Condition("id.trId", null, null, Condition.Restriction.ORDERBY));
            condition.add(new Condition("state", new Object[] { CommonConstants.TR_STATE.Waiting.getCode() }, null, Condition.Restriction.EQ));
            aclList = aclDao.findByConditions(condition);

            log.debug("[NI:MBB_Action]ModemID= "+bypassDevice.getModemId()+", Number of Waiting,Running State= "+aclList.size());

            // Set the state of each AsyncCommandLog record
            /*
             * TR_STATE.Success (0) : Command Success, Normal Complete
             * TR_STATE.Waiting (1) : Waiting for connection from modem when after the sending a SMS
             * TR_STATE.Running (2) : Command on running
             * TR_STATE.Terminate (4) : Terminate before Success (ex. CRC error)
             * TR_STATE.Delete (8) : Flag which show not used (ex. duplicated command)
             * TR_STATE.Unknown (255) : Unknown
             */
            if(aclList != null && aclList.size() > 0){
                // Set the State attribute. Latest one is STATE.Running, else STATE.Delete
                AsyncCommandLog asyncCmdLog = null;
                List<AsyncCommandParam> acpList = null;
                
                for(int i=0; i < aclList.size(); i++){
                    asyncCmdLog = aclList.get(i);
                    if(i != aclList.size() -1){
                        asyncCmdLog.setState(CommonConstants.TR_STATE.Terminate.getCode());
                        aclDao.update(asyncCmdLog);
                    }
                }

                // Select the latest record to run the command
                asyncCmdLog = aclList.get(aclList.size() -1);
                log.debug("AsyncCmdLog ==> " + asyncCmdLog.toJSONString());
                
                bypassDevice.setTrState(CommonConstants.TR_STATE.Running);
                bypassDevice.setModemId(asyncCmdLog.getMcuId());
                bypassDevice.setTransactionId(asyncCmdLog.getTrId());
                bypassDevice.setCommand(asyncCmdLog.getCommand());

                // query command params (trId, mcuId is IDX on Schema)
                log.debug("get AsyncCommandParams trId=" + asyncCmdLog.getTrId() + ", deviceId=" + asyncCmdLog.getMcuId());
                condition = new HashSet<Condition>();
                condition.add(new Condition("id.trId", new Object[] { asyncCmdLog.getTrId() }, null, Condition.Restriction.EQ));
                condition.add(new Condition("id.mcuId", new Object[] { asyncCmdLog.getMcuId() }, null, Condition.Restriction.EQ));
                AsyncCommandParamDao acpDao = DataUtil.getBean(AsyncCommandParamDao.class);
                acpList = acpDao.findByConditions(condition);
               //acpList = asyncCmdLog.getParams();

                log.debug("PARAM SIZE[" + acpList.size() + "]");
                // Put Parameters to TargetDevice
                HashMap<String, Object> map = new HashMap<String, Object>();
                for (AsyncCommandParam param : acpList) {
                    log.debug("PARAM_TYPE[" + param.getParamType() + "] VALUE[" + param.getParamValue() + "]");
                    map.put(param.getParamType(), param.getParamValue());
                }
                bypassDevice.setArgMap(map);
                log.debug("BypassDevice ==> " + bypassDevice.toString());
            }else {
                // If there are no AsyncCommandLog records, then return null
            }
            
            txmanager.commit(txstatus);
        }catch (NullPointerException e){
            log.error("FindByConditions produce -" + e, e);
            if(txstatus != null)
                txmanager.rollback(txstatus);
        }catch (Exception ec){
            log.error("txmanager produce -" + ec, ec);
            if(txstatus != null)
                txmanager.rollback(txstatus);
        }
    }

    private String[] callNiCommand(NIAttributeId attrId, HashMap<String, Object> argsMap) {
        // Create new NiClient
        NiClient niClient = new NiClient();
        niClient.setSession(session);
        
        // Frame
        GeneralFrame command = CommandNIProxy.setGeneralFrameOption(GeneralFrame.FrameOption_Type.Command, attrId);
        // command.setNIAttributeId(attrId.getCode());

        // CommandNIProxy niProxy = new CommandNIProxy();
        // niProxy.setGeneralFrameOption(GeneralFrame.FrameOption_Type.Command, attrId.name(), command);

        // Set SequenceCounter to 0.
        // niProxy.initSequenceCounter();
        byte[] writeData = CommandNIProxy.makeGeneralFrameData("MBB", attrId, argsMap);
        log.debug("[NI:MBB_Action] Write Data ["+Hex.decode(writeData)+"]");
       
        String cmdData = "";
        String cmdResult = "";
        try {
            // Send         
            log.debug("[NI:MBB_Action] Send Command [command="+command+"]");   
            niClient.executeCommand(writeData, command);
            //gf = niClient.sendCommand(target,gf,writeData);
            if(command != null){
                AbstractCommand abc = command.abstractCmd;
                if (abc instanceof NullBypassOpen) {
                    NullBypassOpen ret = (NullBypassOpen)abc;
                    int modemStatus = Integer.parseInt(String.valueOf(ret.getStatus()));
                    log.debug("####### reqNullBypassOpen response ==>>> {}" + modemStatus);
                    
                    if (modemStatus == 0) { // 널 바이패스 가능 상태
                        cmdResult = "Execution OK";
                        log.debug("NullBypassOpen Success.");
                    }else if(modemStatus == -1){ 
                        cmdResult = "Execution Fail";
                        log.debug("NI NullBypass Open request Failure - Modem Connection fail.");
                    } else {
                        cmdResult = "Execution Fail";
                        log.debug("NI NullBypass Open request is refused - Modem Busy.");
                    } 
                }
                else {
                if(abc.toString() == null){
                    cmdData = "undefined";
                    cmdResult = "Execution Fail";
                }else{
                    // Success
                    cmdData = abc.toString();
                    cmdResult = "Execution OK";
            }
                }
            }
        } catch (Exception e) {
            log.error(e, e);
            cmdResult = "Execution Fail";
        } 
        return new String[]{cmdData, cmdResult};
    }
    
    // Command For NI
    private void callNiAsyncCommand() {
        /*
         *  Make General Frame
         *  Network Type         : get from generalFrame
         *  Frame Control        : set to (0x00)
         *  Frame Option         : set to (0x03)
         *  Sequence Number      : set to (0x00)
         *  Source Address       : X
         *  Destination Address  : X
         *  Network Status       : X
         *  Frame Payload        : set by GeneralDataFrame().make
         */

        String[] resultData = callNiCommand(GeneralFrame.NIAttributeId.getItem(bypassDevice.getCommand()),
                bypassDevice.getArgMap());
        //TODO resultData를 json형식으로 변환해야 함. NI결과에 맞춰 처리해줘야 함.
        String jsonString = resultData[0];
        if(resultData[0] != null){
            jsonString = "{\"NI_RESULT\":\""+ resultData[0] + "\"}";
            jsonString = jsonString.replaceAll("[\\n]", " ");
        }
        
        saveAsyncCommandResult(jsonString);
        saveAsyncCommandLog(resultData[1]);

        //session.write(writeData);
    }

    // Command For COAP
    private void callCoapCommand(BypassDevice bypassDevice){
        //IP 주소만 얻기 위해서 '/'와  port 제거
        String ipAddress = this.destIpAddress;
        ipAddress = ipAddress.replace("/", "");                         // '/'제거
        ipAddress = ipAddress.substring(0, ipAddress.lastIndexOf(":")); // :port 제거
        log.debug("CoAP IP Address: " + ipAddress);
        
        String commandName = bypassDevice.getCommand();
        Map map = new HashMap<String, String>();
        String cmdResult = "Execution Fail";
        CommandGW gw = new CommandGW();
        
        // ipAddress가 IPv4인지 IPv6인지 구분 
        String ipv4="";
        String ipv6="";
        Pattern pattern;
        pattern = Pattern.compile(regexIPv4);
        if (pattern.matcher(ipAddress).matches() == true)
            ipv4 = ipAddress;
        else
            ipv6 = ipAddress;
        
        try{
            /*
             * coapPing
             * coapGetInfo
             * modemReset
             * coapBrowser
             */
            if(commandName.equals("coapPing")){   
                String tempStr = gw.coapPing(ipAddress, "modem", "mbb");
                map.put("RESULT", tempStr);
                if(tempStr.equals("FAIL"))  
                    throw new Exception("[FAIL] CoAP Ping"); 
            }else if(commandName.equals("coapGetInfo")){
                map = gw.coapGetInfo(ipv4, ipv6, "mbb");
                map.put("type", "mbb");
                // mbb모뎀은 해당 데이터를 보내주지 않기 때문에 -값으로 대체
                if(map.containsKey("LQI"))
                    map.put("LQI","-(MBB)");
                if(map.containsKey("LQI6"))
                    map.put("LQI6","-(MBB)");
                if(map.containsKey("ETX"))
                    map.put("ETX","-(MBB)");
                if(map.containsKey("ETX6"))
                    map.put("ETX6","-(MBB)");
                if(map.containsKey("ParentId"))
                    map.put("ParentId","-(MBB)");
                if(map.containsKey("ParentId6"))
                    map.put("ParentId6","-(MBB)");
                
            }else if(commandName.equals("modemReset")){
                map = gw.modemReset(ipv4, ipv6, "mbb");  
            }else if(commandName.equals("coapBrowser")){
                Map<String,Object> param = bypassDevice.getArgMap();
                String uri="";
                String query="";
                String config="";
                
                if(param.containsKey("uri") && param.get("uri") != null){
                    uri = param.get("uri").toString();
                    log.debug(uri);
                }
                if(param.containsKey("query") && param.get("query") != null){
                    query = param.get("query").toString();
                    log.debug(query);
                }
                if(param.containsKey("config") && param.get("config") != null){
                    config = param.get("config").toString();
                    log.debug(config);
                }
                map = gw.coapBrowser(ipv4, ipv6, uri, query, config, "mbb"); 
                map.put("uri", uri);
            }else{
                map.put("Exception", "The Command Function is not defined in NI-MBB-ACTION-SP");
            }

            // MAP to JSON String
            ObjectMapper om = new ObjectMapper();
            String jsonStr = om.writeValueAsString(map);
            log.debug("[NI:MBB_Action] Get Return From Coap Function");
            // Save CommandResult
            saveAsyncCommandResult(jsonStr);
            
            cmdResult = "Execution OK";
        }
        catch (Exception ec){
            log.error("Exception comes from callCoapCommand : "+ec, ec);
        }
        finally {
            saveAsyncCommandLog(cmdResult);
        }
    }

    // Command For SNMP
    private void callSnmpAsyncCommand() {
        /*
         *  Nothing to do
         */
        // callSnmpCommand(...)

        //16-10-04 There is only one command which is 'Snmp Daemon Open'
        String mbbRemoteAddress = StringUtil.nullToBlank(this.destIpAddress);
        String jsonString = "";
        if(mbbRemoteAddress.isEmpty()){
            jsonString = "{\"cmdResult\":\"Failed to get a remote address of SNMP agent.\"}";
        }else{
            jsonString = "{\"cmdResult\":\"SNMP agent is opened at " + mbbRemoteAddress + "\"}";
        }

        saveAsyncCommandResult(jsonString);
        saveAsyncCommandLog("Execution OK");
    }

    public void saveAsyncCommandLog(String _cmdResult){

        JpaTransactionManager txmanager = (JpaTransactionManager)DataUtil.getBean("transactionManager");
        TransactionStatus txstatus = null;

        AsyncCommandLogDao aclDao = null;
        aclDao = DataUtil.getBean(AsyncCommandLogDao.class);
        Set<Condition> condition = null;

        // query Running state
        List<AsyncCommandLog> aclList = null;
        try{
            txstatus = txmanager.getTransaction(null);
            condition = new HashSet<Condition>();
            condition.add(new Condition("id.mcuId", new Object[] { bypassDevice.getModemId() }, null, Condition.Restriction.EQ));
            condition.add(new Condition("id.trId", new Object[] { bypassDevice.getTransactionId() }, null, Condition.Restriction.EQ));
            aclList = aclDao.findByConditions(condition);

            log.debug("AsyncCommandLog List size = " + aclList.size());
            if (aclList.size() == 1) {
                AsyncCommandLog log = aclList.get(0);
                if (_cmdResult.equals("Execution OK"))
                    log.setState(TR_STATE.Success.getCode());
                else
                    log.setState(TR_STATE.Terminate.getCode());
                
                aclDao.update(log);
            }
            
            log.debug("[NI:MBB_Action]ModemID= "+bypassDevice.getModemId()+
                    ", TR_ID= "+bypassDevice.getTransactionId() + ", Result=" + _cmdResult);
            txmanager.commit(txstatus);
        }catch (NullPointerException e){
            log.error("FindByConditions produce -" + e, e);
            if(txstatus != null)
                txmanager.rollback(txstatus);
        }catch (Exception ec){
            log.error("txmanager produce -" + ec, ec);
            if(txstatus != null)
                txmanager.rollback(txstatus);
        }
    }
    
       
    public void saveAsyncCommandResult(String _jsonStr){
        log.debug("[NI:MBB_Action]Save Command Result[" + _jsonStr + "]");
        // 헤더에 Network Type이 MBB로 오는 경우 이 함수를 이용하여 응답을 테이블에 저장
        
        String cmdName = bypassDevice.getCommand();
        
        JpaTransactionManager txmanager = (JpaTransactionManager)DataUtil.getBean("transactionManager");
        TransactionStatus txstatus = null;
        
        AsyncCommandResultDao asyncResultDao = null;
        asyncResultDao = DataUtil.getBean(AsyncCommandResultDao.class);
            
        try{
            int maxNum = asyncResultDao.getMaxNum(bypassDevice.getModemId(), bypassDevice.getTransactionId());
            int maxlen = 450;
            txstatus = txmanager.getTransaction(null);
            for (int i = 0, pos = 0; i < _jsonStr.length() / maxlen + 1; i++) {
                AsyncCommandResult acr = new AsyncCommandResult();          
                acr.setTrId(bypassDevice.getTransactionId());
                acr.setMcuId(bypassDevice.getModemId());
                acr.setResultType(cmdName);
                    if (_jsonStr.length() > pos + maxlen) {
                        acr.setResultValue(_jsonStr.substring(pos, pos + maxlen));                    
                        pos += maxlen;
                        }
                    else {
                        acr.setResultValue(_jsonStr.substring(pos));
                        }
                    // SMS_Receiver.java save the result row which num is 0. setNUM should be set from 1.                
                    acr.setNum(++maxNum);
            
                    log.debug("save AsyncCommandResult = " + acr.toJSONString());
                asyncResultDao.add(acr);
            }
                                    
            txmanager.commit(txstatus);
        }catch (Exception ec){
            log.error("Exception occurs when insert the AsyncCommandResult -" + ec, ec);
            if(txstatus != null)
                txmanager.rollback(txstatus);
        }
    }
    
    public Map<String, Object> callMeterCommand() {
        //commandName.startsWith("cmdMeterParamAct")
        //commandName.startsWith("cmdMeterParamGet")
        //commandName.startsWith("cmdMeterParamSet")
        //commandName.startsWith("cmdOnDemandMeter")
        // NullBypassOpen has to be called and then bypass
        Map<String,Object> result = new HashMap<String,Object>();
        String cmdResult = "Execution Fail";
        
        try {
        if (callNullBypass(true)) {
                log.debug("[NI:MBB_Action] [command="+bypassDevice.getCommand()+"] Started after 3000ms");
                // Thread.sleep(3000);
                Target target = CmdUtil.getTarget(bypassDevice.getModemId());
                target.setPort(Integer.parseInt(FMPProperty.getProperty("soria.protocol.modem.port.nullbypass")));
                
                HashMap<String, Object> targetParams = bypassDevice.getArgMap();

                bclient = new BypassClient(target);
                bclient.setParams(targetParams);
                BypassResult bypassResult = bclient.executeMBB(bypassDevice.getCommand());
            
                log.debug("[cmdMeterParamGet] Execute Result = " + bypassResult.toString());
            
            if(bypassResult.getResultValue() instanceof Map) {
                result = (Map<String, Object>) bypassResult.getResultValue();
            } else {
                Map<String,Object> map = new HashMap<String,Object>();
                result.put("RESULT_VALUE", bypassResult.getResultValue());
            }
            
                cmdResult = "Execution OK";
                
//                ObjectMapper om = new ObjectMapper();
//                String jsonStr = om.writeValueAsString(result);
                
                String jsonStr = "";
                if(targetParams.containsKey("paramGet")){
                    jsonStr = appendBypassParam(result, targetParams.get("paramGet").toString());
                    saveAsyncCommandResult(jsonStr);
                }else if(targetParams.containsKey("paramSet")){
                    jsonStr = appendBypassParam(result, targetParams.get("paramSet").toString());
                    saveAsyncCommandResult(jsonStr);
                }else if(targetParams.containsKey("paramAct")){
                    jsonStr = appendBypassParam(result, targetParams.get("paramAct").toString());
                    saveAsyncCommandResult(jsonStr);
                }
                else{
                    jsonStr = appendBypassParam(result, null);
                    saveAsyncCommandResult(jsonStr);
                }
                                                
                callNullBypass(false);
        }
        }
        catch (Exception e) {
            log.error("Exception comes from callMeterCommand : "+ e, e);
        }
        finally {
            saveAsyncCommandLog(cmdResult);
        }
        
        return result;
    }
    
    private String appendBypassParam(Map<String,Object> _result, String _param){
        // 1.0.11.35.127.255|3|2|RW|double-long-unsigned|[{"value":"7"}]
        //String param = "1.0.11.35.127.255|3|2|RW|double-long-unsigned|[{\"value\":\"7\"}]";
        String[] cmdParams = _param.split("[|]");
        
        ObisCodeDao obisCodeDao = DataUtil.getBean(ObisCodeDao.class);
        Map<String,Object> condition = new HashMap<String,Object>();
        if(cmdParams.length>=3){
            String obis = cmdParams[0];
            String classId = cmdParams[1];
            String attrNo = cmdParams[2];
            
            _result.put("ObisCode", obis);
            _result.put("ClassId", classId);
            _result.put("AttributeNo", attrNo);
            
            if(cmdParams.length>=6){
                String value = cmdParams[5];
                
                _result.put("Value", value);
            }
            
        }else{
            _result.put("param", _param);
        }
        
        ObjectMapper om = new ObjectMapper();
        try {
            String jsonStr = om.writeValueAsString(_result);
            return jsonStr;
        } catch (Exception e) {
            log.error("Exception comes from appendBypassParam : "+ e, e);
        } 
        
        return null;
    }
    
    private boolean callNullBypass(boolean isOpen) {
        log.debug("CallNullBypass[" + isOpen + "] ~~!!");
        HashMap<String, Object> param = new HashMap<String, Object>(); 
        
        int MODEM_NULLBYPASS_PORT = Integer.parseInt(FMPProperty.getProperty("soria.protocol.modem.port.nullbypass"));
        int MODEM_TIMEOUT = Integer.parseInt(FMPProperty.getProperty("soria.protocol.modem.port.nullbypass.timeout"));   // 초
        
        param.put("port", MODEM_NULLBYPASS_PORT);
        param.put("timeout", MODEM_TIMEOUT * 1000);
        
        String[] result = callNiCommand(isOpen? NIAttributeId.NullBypassOpen:NIAttributeId.NullBypassClose, param);
        
        if (result[1].equals("Execution OK"))
            return true;
        else
            return false;
    }
    
    /**
     * Reading Meter F/W Version.
     * @param argMap
     * @throws Exception
     */
    private void callMeterMeterFWVersionCommand(HashMap<String, Object> argMap) throws Exception {
        BypassClient bypassClient = null;
        String cmdResult = "Execution Fail";
        log.debug("callMeterMeterFWVersionCommand param = " + argMap.toString());
        
        try {
//          ModemDao modemDao = DataUtil.getBean(ModemDao.class);
//          Modem modem = modemDao.get(argMap.get("deviceSerial").toString());          
//          Target target = CmdUtil.getNullBypassTarget(modem);
            
            MeterDao meterDao = DataUtil.getBean(MeterDao.class);
            Target target = CmdUtil.getNullBypassTarget(meterDao.get(argMap.get("meterId").toString()));
            if(target == null){
                throw new Exception("Can not found target. please check Meter & Modem information.");
            }
            bypassClient = new BypassClient(target);
            bypassClient.setExternalNISession(session);
            
            BypassResult bypassResult = bypassClient.excute("cmdGetMeterFWVersion");
            log.debug("[cmdBypassGetMeterFWVersion] Excute Result = " + bypassResult.toString());

            @SuppressWarnings("unchecked")
            Map<String, Object> resultMap = (HashMap<String, Object>)bypassResult.getResultValue();
            log.debug("### cmdBypassGetMeterFWVersion 결과 = " + String.valueOf(resultMap.get("resultValue")));                       

            cmdResult = "Execution OK";         
            
            /*
             * 결과 저장
             */
            ObjectMapper om = new ObjectMapper();
            String jsonStr = om.writeValueAsString(resultMap);
            saveAsyncCommandResult(jsonStr);
        } catch (Exception e) {
            bypassClient.close();
            log.error("Exception comes from callMeterMeterFWVersionCommand : "+ e);
        } finally {
            saveAsyncCommandLog(cmdResult);
        }
    }  
    
    /**
     * Reading SORIA Kaifa Meter Key
     * @param argMap
     * @throws Exception
     */
    private void callSORIAGetMeterKeyCommand(HashMap<String, Object> argMap) throws Exception {
        BypassClient bypassClient = null;
        String cmdResult = "Execution Fail";
        log.debug("callSORIAGetMeterKeyCommand param = " + argMap.toString());
        
        try {
            MeterDao meterDao = DataUtil.getBean(MeterDao.class);
            Target target = CmdUtil.getNullBypassTarget(meterDao.get(argMap.get("meterId").toString()));
            if(target == null){
                throw new Exception("Can not found target. please check Meter & Modem information.");
            }
            bypassClient = new BypassClient(target);
            bypassClient.setExternalNISession(session);
            
            BypassResult bypassResult = bypassClient.excute("cmdSORIAGetMeterKey");
            log.debug("[cmdBypassSORIAGetMeterKey] Excute Result = " + bypassResult.toString());

            @SuppressWarnings("unchecked")
            Map<String, Object> resultMap = (HashMap<String, Object>)bypassResult.getResultValue();
            log.debug("### cmdBypassSORIAGetMeterKey 결과 = " + String.valueOf(resultMap.get("resultValue")));                       

            cmdResult = "Execution OK";         
            
            /*
             * 결과 저장
             */
            ObjectMapper om = new ObjectMapper();
            String jsonStr = om.writeValueAsString(resultMap);
            saveAsyncCommandResult(jsonStr);
        } catch (Exception e) {
            bypassClient.close();
            log.error("Exception comes from callSORIAGetMeterKeyCommand : "+ e);
        } finally {
            saveAsyncCommandLog(cmdResult);
        }
    }
    
    /**
     * Meter or Modem OTA Call
     * @param targetType
     * @param modemDeviceSerial
     * @param argMap
     * @throws Exception
     */
    private void callOTACommand(OTATargetType targetType, String modemDeviceSerial, HashMap<String, Object> argMap) throws Exception {
        String cmdResult = "Execution Fail";
        log.debug("callOTACommand modemId = " + modemDeviceSerial + ", param = " + argMap.toString());
        
        try {            
            Map<CBE_RESULT_CONSTANTS, Object> returnMap = new HashMap<>();
            Map<String, Object> resultMap = new HashMap<String, Object>();
            
            // Param validation check
            if(argMap == null || !argMap.containsKey("fw_crc") || !argMap.containsKey("fw_version") || !argMap.containsKey("imageKey") || !argMap.containsKey("fw_path") || !argMap.containsKey("take_over")){
                returnMap.put(CBE_RESULT_CONSTANTS.RESULT_STATE, "Fail");
                returnMap.put(CBE_RESULT_CONSTANTS.RESULT_VALUE, "Invalid parameter error.");
            }else{
                String fw_crc = (String) argMap.get("fw_crc");
                String fw_version = (String) argMap.get("fw_version");
                String imageKey = (String) argMap.get("imageKey");
                String fw_path = (String) argMap.get("fw_path");
                String take_over = (String) argMap.get("take_over");                
                
                /*
                 * OTA 파일 준비
                 */
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("fw_crc", fw_crc);
                params.put("fw_version", fw_version);
                params.put("image_identifier", imageKey);               
                params.put("fw_path", FMPProperty.getProperty("ota.firmware.download.dir") + "/" + fw_path);                
                params.put("take_over", take_over); 
                
                Path filePath = Paths.get((String)params.get("fw_path"));               
                byte[] fileArray = Files.readAllBytes(filePath);
                
                params.put("image", fileArray.length + "byte");             
                log.debug("MBB Modem OTA params = " + params.toString());
                
                params.put("image", fileArray);
                                
                /*
                 * Meter OTA
                 */
                if(targetType == OTATargetType.METER){
                    MeterDao meterDao = DataUtil.getBean(MeterDao.class);
                    Target target = CmdUtil.getNullBypassTarget(meterDao.get(argMap.get("meterId").toString()));
                    if(target == null){
                        throw new Exception("Can not found target. please check Meter & Modem information.");
                    }
                    log.debug("OTA TargetType = " + target.getTargetType() + ", session = " + session.getRemoteAddress());

                    SORIAMeterOTACallable callable = new SORIAMeterOTACallable(target, params);
                    callable.setExternalNISession(session);
                    returnMap = callable.call();
                }
                /*
                 * Modem OTA
                 */
                else if(targetType == OTATargetType.MODEM){
                    // DEBUGING Code
                    if(!modemDeviceSerial.equals(argMap.get("modemId").toString())){
                        log.error("Target Modem missmatch~!! ==> Target modem = " + argMap.get("modemId").toString() + ", Connected modem = " + modemDeviceSerial);
                        throw new Exception("Target Modem missmatch~!! ==> Target modem = " + argMap.get("modemId").toString() + ", Connected modem = " + modemDeviceSerial);
                    }
                    
                    ModemDao modemDao = DataUtil.getBean(ModemDao.class);
                    Modem modem = modemDao.get(modemDeviceSerial);
                    
                    Target target = CmdUtil.getNullBypassTarget(modem);
                    if(target == null){
                        throw new Exception("Can not found target. please check Meter & Modem information.");
                    }
                    log.debug("OTA TargetType = " + target.getTargetType() + ", session = " + session.getRemoteAddress());
                    
                    SORIAModemOTACallable callable = new SORIAModemOTACallable(target, params);
                    callable.setExternalNISession(session);             
                    returnMap = callable.call();                    
                }else{
                    throw new Exception("Unknown OTA Target Type.");
                }
                
                /*
                 * Return value edit
                 */
                JSONObject jo = new JSONObject();
                jo.put("RESULT", ((CBE_STATUS_CONSTANTS) returnMap.get(CBE_RESULT_CONSTANTS.RESULT_STATE)) == CBE_STATUS_CONSTANTS.SUCCESS ? true : false);
                jo.put("RESULT_VALUE", returnMap.get(CBE_RESULT_CONSTANTS.RESULT_VALUE));
                log.info("TARGET_ID=" + returnMap.get(CBE_RESULT_CONSTANTS.TARGET_ID) + ", RESULT=" + returnMap.get(CBE_RESULT_CONSTANTS.RESULT_STATE) + ", RESULT_VALUE=" + returnMap.get(CBE_RESULT_CONSTANTS.RESULT_VALUE));
                
                resultMap.put((String) returnMap.get(CBE_RESULT_CONSTANTS.TARGET_ID), jo.toString());
                
                cmdResult = "Execution OK";                 
            }                       
            
            /*
             * 결과 저장
             */
            ObjectMapper om = new ObjectMapper();
            String jsonStr = om.writeValueAsString(resultMap);
            saveAsyncCommandResult(jsonStr);

        } catch (Exception e) {
            log.error("Exception comes from callOTACommand : "+ e, e);
        } finally {
            saveAsyncCommandLog(cmdResult);
        }
    }    
    
    
    
    
    /**
     * Reading RomRead.
     * @param argMap
     * @throws Exception
     */
    private void callROMReadCommand(HashMap<String, Object> argMap) throws Exception {
        BypassClient bypassClient = null;
        String cmdResult = "Execution Fail";
        RomRead resultObj = null;
        String command = "ROMRead";
        byte[][] resultData = null;
        JpaTransactionManager txmanager = (JpaTransactionManager)DataUtil.getBean("transactionManager");
        TransactionStatus txstatus = null;
        
        Map<String, Object> resultMap = new HashMap<String,Object>();
        try {
            // get parameters
            String meterId = String.valueOf(argMap.get("meterId"));
            String fromDate = String.valueOf(argMap.get("fromDate"));
            String toDate = String.valueOf(argMap.get("toDate"));
            log.debug("callROMReadCommand meterId[" + meterId +"],fromDate["+fromDate + "],toDate[" + toDate + "]");
            
            MeterDao meterDao = DataUtil.getBean(MeterDao.class);
            Meter  meter  = meterDao.get(argMap.get("meterId").toString());
            Modem modem = meter.getModem();
        
            ModemType modemType = modem.getModemType();
            DeviceModel deviceModel = meter.getModel();
            
            int  modemPort =  meter.getModemPort() == null ? 0 : meter.getModemPort().intValue();
            if ( modemPort > 5 || modemPort < 0 ){
                log.debug("modemPort:" + modemPort + " is not support");
                throw new Exception("modemPort:" + modemPort + " is not support");
            }
            
            log.debug("meterModel=" + deviceModel.getId());
            int lpInterval = meter.getLpInterval();

            int[] offsetCount = CmdUtil.convertOffsetCount(deviceModel, lpInterval, modemType, fromDate, toDate);
            Target target = CmdUtil.getNullBypassTarget(modem);
            if(target == null){
                throw new Exception("Can not found target. please check Meter & Modem information.");
            }
            HashMap<String, Object> niParam = new HashMap<String,Object>();

            niParam.put("type",2);
            RomReadDataReq req = new RomReadDataReq();
            req.setPollData(modemPort + 1, offsetCount[0], offsetCount[1] );    // DLMS Load profile
            niParam.put("romReadData", req);
            
            CommandNIProxy niProxy = new CommandNIProxy();

            AbstractCommand abc = callNiCommandforROMRead(NIAttributeId.ROMRead, niParam);
             
            if ( abc instanceof RomRead ){
                resultObj =  (RomRead)abc;
            }
            if ( resultObj == null ){
                throw new Exception("Command Execute Fail");
            }

            resultData = resultObj.getRomData();
            MeterData emd = new MeterData();

            MeterDataParser parser = null;
            DeviceConfig deviceConfig = deviceModel.getDeviceConfig();

            log.debug("Parser[" + deviceConfig.getParserName() + "]");
            if (deviceConfig.getOndemandParserName() != null && !"".equals(deviceConfig.getOndemandParserName()))
                parser = (MeterDataParser) Class.forName(deviceConfig.getOndemandParserName()).newInstance();
            else
                parser = (MeterDataParser) Class.forName(deviceConfig.getParserName()).newInstance();

            log.debug("Parser Instance Created..");

            if (parser == null) {
                throw new Exception("parser is null, check meterId[" + meter.getMdsId() + "], check deviceModel[" + meter.getModel() + "]");
            }
            try{
                txstatus = txmanager.getTransaction(null);  
                meter  = meterDao.get(argMap.get("meterId").toString());
                modem = meter.getModem();
                
                parser.setMeter(meter);
                parser.setMeteringTime(DateTimeUtil.getDateString(new Date()));
                parser.setMeterTime(parser.getMeteringTime());
                parser.setOnDemand(true);
                if ( parser instanceof  DLMSKaifa ) {
                    ((DLMSKaifa)parser).setModemId(modem.getDeviceSerial());            
                    ((DLMSKaifa)parser).setModemPort(modemPort);
                }
                int dataCnt = resultData.length;
                int bufLen = 0;							// INSERT SP-487
                for (int i=0; i<dataCnt; i++)
                {
                    parser.parse((byte[])resultData[i]);
                    bufLen += resultData[i].length;		// INSERT SP-487
                }
                emd.setParser(parser);
                if(emd != null && emd.getMap() != null){
                    String htmlStr = DLMSKaifaMDSaver.getOndemandDetailString(emd.getMap());
                    
                    resultMap.put("detail", htmlStr);
                    resultMap.put("result", "Success");
                    cmdResult = "Execution OK";
                }
                else {
                    resultMap.put("detail", "");
                    resultMap.put("result", "Success");
                    cmdResult = "Execution OK";
                }
                // Save Data 
                // UPDATE START SP-487
//                if ( parser instanceof  DLMSKaifa ) {
//                    DLMSKaifaMDSaver saver = (DLMSKaifaMDSaver)DataUtil.getBean(DLMSKaifaMDSaver.class);
//                    saver.saveMeterData(meter, (DLMSKaifa)parser);
//            
//                    modem.setLastLinkTime(meter.getLastReadDate());
//                    ModemDao modemDao = DataUtil.getBean(ModemDao.class);
//                    modemDao.update(modem);         
//                }
    			ByteBuffer buf = ByteBuffer.allocate(bufLen);
    			buf.clear();
    			for (int i=0; i<dataCnt; i++)
    			{
    				buf.put((byte[])resultData[i]);
    			}
    			CommandGW commandGw = DataUtil.getBean(CommandGW.class);
    			commandGw.saveMeteringDataByQueue("127.0.0.1", meterId, modem.getDeviceSerial(), buf.array());
    			// UPDATE END SP-487
                
                txmanager.commit(txstatus);
            }catch (Exception ec){
                log.error("txmanager produce -" + ec, ec);
                if(txstatus != null)
                    txmanager.rollback(txstatus);
            }       
        } catch (Exception e) {
            log.error("Exception comes from callRomReadCommand : "+ e, e);
            resultMap.put("detail", "");
            resultMap.put("result", "Fail");
            resultMap.put("rtnStr", e.getMessage());
        } finally {
            if ( resultMap.size() > 0 ){
                ObjectMapper om = new ObjectMapper();
                String jsonStr = om.writeValueAsString(resultMap);
                saveAsyncCommandResult(jsonStr);
            }
            saveAsyncCommandLog(cmdResult);
        }
    }
    private AbstractCommand  callNiCommandforROMRead(NIAttributeId attrId, HashMap<String, Object> argsMap) {
        AbstractCommand abc = null;
        // Create new NiClient
        NiClient niClient = new NiClient();
        niClient.setSession(session);
        
        // Frame
        GeneralFrame command = CommandNIProxy.setGeneralFrameOption(GeneralFrame.FrameOption_Type.Command, attrId);
        // command.setNIAttributeId(attrId.getCode());

        // CommandNIProxy niProxy = new CommandNIProxy();
        // niProxy.setGeneralFrameOption(GeneralFrame.FrameOption_Type.Command, attrId.name(), command);

        // Set SequenceCounter to 0.
        // niProxy.initSequenceCounter();
        byte[] writeData = CommandNIProxy.makeGeneralFrameData("MBB", attrId, argsMap);
        log.debug("[NI:MBB_Action] Write Data ["+Hex.decode(writeData)+"]");
       
        String cmdData = "";
        String cmdResult = "";
        try {
            // Send         
            log.debug("[NI:MBB_Action] Send Command [command="+command+"]");   
            niClient.executeCommand(writeData, command);
            //gf = niClient.sendCommand(target,gf,writeData);
            if(command != null){
                abc = command.abstractCmd;
                if (abc instanceof NullBypassOpen) {
                    NullBypassOpen ret = (NullBypassOpen)abc;
                    int modemStatus = Integer.parseInt(String.valueOf(ret.getStatus()));
                    log.debug("####### reqNullBypassOpen response ==>>> {}" + modemStatus);
                    
                    if (modemStatus == 0) { // 널 바이패스 가능 상태
                        cmdResult = "Execution OK";
                        log.debug("NullBypassOpen Success.");
                    }else if(modemStatus == -1){ 
                        cmdResult = "Execution Fail";
                        log.debug("NI NullBypass Open request Failuer - Modem Connection fail.");
                    } else {
                        cmdResult = "Execution Fail";
                        log.debug("NI NullBypass Open request is refused - Modem Busy.");
                    } 
                }
                else {
                    return abc;
                }
            }
        } catch (Exception e) {
            log.error(e, e);
        } 
        return abc;
    }
    
    // INSERT START SP-279
    public Map<String, Object> callMeterSyncTimeCommand() {

        // NullBypassOpen has to be called and then bypass
        Map<String,Object> result = new HashMap<String,Object>();
        String cmdResult = "Execution Fail";
        
        try {
            if (callNullBypass(true)) {
                log.debug("[NI:MBB_Action] [command="+bypassDevice.getCommand()+"] Started after 3000ms");
    
                Target target = CmdUtil.getTarget(bypassDevice.getModemId());
                target.setPort(Integer.parseInt(FMPProperty.getProperty("soria.protocol.modem.port.nullbypass")));
                
                HashMap<String, Object> targetParams = bypassDevice.getArgMap();
                String meterId = String.valueOf(targetParams.get("meterId"));
    
                // cmdSyncTime -> cmdMeterParamSet
                bypassDevice.setCommand("cmdMeterParamSet");                    
                String param = targetParams.get("paramSet").toString();
                DLMSKaifaMDSaver saver = (DLMSKaifaMDSaver)DataUtil.getBean(DLMSKaifaMDSaver.class);
                log.debug("[cmdMeterParamSet] synctime option");
                log.debug("[cmdMeterParamSet] synctime option paramSet = " + param);
                if (("|").equals(param.substring(param.length()-1))) {
                	// If called from aimir-web, no travelTime.
                	param = param + saver.getTravelTime(meterId);
                }
                targetParams.replace("paramSet", param );
                log.debug("[cmdMeterParamSet] synctime option replace paramSet = " + targetParams.get("paramSet").toString());
                
                bclient = new BypassClient(target);
                bclient.setParams(targetParams);
                BypassResult bypassResult = bclient.executeMBB(bypassDevice.getCommand());
            
                log.debug("[callMeterSyncTimeCommand] Execute Result = " + bypassResult.toString());
                    
                if(bypassResult.getResultValue() instanceof Map) {
                    result = (Map<String, Object>) bypassResult.getResultValue();
                } else {
                    Map<String,Object> map = new HashMap<String,Object>();
                    result.put("RESULT_VALUE", bypassResult.getResultValue());
                }
                
                cmdResult = "Execution OK";
                
                String jsonStr = "";
                // cmdMeterParamSet -> cmdSyncTime
                bypassDevice.setCommand("cmdSyncTime");
                if(targetParams.containsKey("paramSet")){
                    jsonStr = appendBypassParam(result, targetParams.get("paramSet").toString());
                    saveAsyncCommandResult(jsonStr);
                }
                else{
                    jsonStr = appendBypassParam(result, null);
                    saveAsyncCommandResult(jsonStr);
                }
                
                // save time sync log
                int syncResult = 0;
                if (result.get("RESULT_VALUE") == null || result.get("RESULT_VALUE").toString() != "Success") {
                    syncResult = 1;
                }
                String afterTime = result.get("aftertime").toString();
                if (afterTime != null) {
                    saver.updateMeterTimeSyncLog(meterId, afterTime, syncResult);                
                }                            
                callNullBypass(false);
            }
        }
        catch (Exception e) {
            log.error("Exception comes from callMeterCommand : "+ e, e);
        }
        finally {
            saveAsyncCommandLog(cmdResult);
        }
        
        return result;
    }    
    // INSERT END SP-279
}

