package com.aimir.fep.protocol.nip.server;

import com.aimir.constants.CommonConstants.ThresholdName;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.EventStatus;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.fep.protocol.fmp.frame.service.MDData;
import com.aimir.fep.protocol.fmp.processor.ProcessorHandler;
import com.aimir.fep.protocol.nip.client.actions.NICommandActionHandlerAdaptor;
import com.aimir.fep.protocol.nip.client.actions.NICommandAction;
import com.aimir.fep.protocol.nip.client.actions.NI_MBB_Action_SP;
import com.aimir.fep.protocol.nip.client.multisession.MultiSession;
import com.aimir.fep.protocol.nip.common.GeneralDataFrame;
import com.aimir.fep.protocol.nip.frame.GeneralFrame;
import com.aimir.fep.protocol.nip.frame.NetworkStatus;
import com.aimir.fep.protocol.nip.frame.NetworkStatusEthernet;
import com.aimir.fep.protocol.nip.frame.NetworkStatusMBB;
import com.aimir.fep.protocol.nip.frame.NetworkStatusSub1GhzForSORIA;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.NIAttributeId;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.NetworkType;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.FrameControl_Ack;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.FrameControl_Pending;
import com.aimir.fep.protocol.nip.frame.payload.AlarmEvent;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.protocol.nip.frame.payload.Firmware;
import com.aimir.fep.protocol.nip.frame.payload.MeterEvent;
import com.aimir.fep.protocol.nip.frame.payload.MeterEvent.MeterEventFrame;
import com.aimir.fep.protocol.nip.frame.payload.Command.Attribute;
import com.aimir.fep.protocol.nip.frame.payload.Command.Attribute.Data;
import com.aimir.fep.protocol.nip.frame.payload.MeteringData;
import com.aimir.fep.protocol.security.OacServerApi;
import com.aimir.fep.trap.actions.SP.EV_SP_200_65_0_Action;
import com.aimir.fep.trap.actions.SP.EV_SP_200_66_0_Action;
import com.aimir.fep.trap.actions.SP.EV_SP_220_2_0_Action;
import com.aimir.fep.trap.actions.SP.EV_SP_240_2_0_Action;
import com.aimir.fep.trap.common.EV_Action.OTA_UPGRADE_RESULT_CODE;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.FrameUtil;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.Message;
import com.aimir.fep.util.threshold.CheckThreshold;
import com.aimir.model.device.Device.DeviceType;
import com.aimir.model.device.EnergyMeter;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MMIU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.device.SubGiga;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.Location;
import com.aimir.model.system.Supplier;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.IPUtil;
import com.aimir.util.StringUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import java.io.Serializable;
import java.net.SocketAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

/**
 * {@link NiProtocolHandler}).
 *
 * @author DJ Kim
 * @version $Rev: 1 $, $Date: 2016-05-21 15:59:15 +0900 $,
 */
@Component
public class NiProtocolHandler extends NICommandActionHandlerAdaptor implements IoHandler //extends IoHandlerAdapter
{
    private static Log log = LogFactory.getLog(NiProtocolHandler.class);
    private boolean isCmdAdapter = false;
    private int responseTimeout = Integer.parseInt(FMPProperty.getProperty("protocol.ni.response.timeout", "180"));
    private int writeTimeout = Integer.parseInt(FMPProperty.getProperty("protocol.ni.write.timeout", "180")) * 1000;
    private Object resMonitor = new Object();
//    private Hashtable<Long, IoBuffer> response = new Hashtable<Long, IoBuffer>();
    private Hashtable<Long, Object> response = new Hashtable<Long, Object>();
    private ProcessorHandler processorHandler;
    
    public NiProtocolHandler() throws Exception { }
    
    public NiProtocolHandler(boolean isCmdAdapter, String handlerName) throws Exception {
    	setHandlerName(handlerName);
    	log.debug("############### New NiProtocolHandler : HandlerName=" + getHandlerName() + " ##############");
    	
        this.isCmdAdapter = isCmdAdapter;
    }
    
	private void putServiceData(IoSession session, String serviceType, Serializable data) {
        try {
            if (processorHandler == null) processorHandler = DataUtil.getBean(ProcessorHandler.class);
            
            if (!Boolean.parseBoolean(FMPProperty.getProperty("kafka.enable")))
                processorHandler.putServiceData(serviceType, data);
            else
                processorHandler.putServiceData(serviceType, makeCommLog(session, (MDData)data));
        }
        catch (Exception e) {
            log.error(e, e);
        }
    }
	
	private Message makeCommLog(IoSession session, MDData data) throws Exception {
        String nameSpace = (String)session.getAttribute("nameSpace");
        String ipaddr = session.getRemoteAddress().toString();
        ipaddr = ipaddr.substring(ipaddr.indexOf("/")+1, ipaddr.lastIndexOf(":"));
        
        //통신 로그 저장 로직
        Message commLog = new Message();
        commLog.setNameSpace(nameSpace == null? "":nameSpace);
        commLog.setData(data.getMdData());
        commLog.setDataType(ProcessorHandler.SERVICE_MEASUREMENTDATA);
        commLog.setSenderIp(ipaddr);
        commLog.setSenderId(data.getMcuId().toString());
        commLog.setReceiverId(DataUtil.getFepIdString());
        commLog.setSendBytes(session.getWrittenBytes());
        commLog.setRcvBytes(session.getReadBytes());
        commLog.setStartDateTime(DateTimeUtil.getDateString(session.getCreationTime()));
        commLog.setEndDateTime(DateTimeUtil.getDateString(session.getLastWriteTime()));
        log.debug("startTime["+commLog.getStartDateTime()+"] endTime["+commLog.getEndDateTime()+"]");
        log.debug("startLongTime["+session.getCreationTime()+"] endLongTime["+session.getLastWriteTime()+"]");
        if(session.getLastWriteTime() - session.getCreationTime() > 0) {
            commLog.setTotalCommTime((int)(session.getLastWriteTime() - session.getCreationTime()));
        }
        else {
            commLog.setTotalCommTime(0);
        }
        log.debug(data.getNetworkType() +" "+ commLog.toString());
        if (data.getNetworkType() == NetworkType.MBB) {
            commLog.setProtocolType(Protocol.GPRS);
        }
        else {
            commLog.setProtocolType(Protocol.IP);
        }
        
        return commLog;
    }
    
    @Override
    public void exceptionCaught(IoSession session, Throwable cause)
            throws Exception {
        try {
            log.debug("HandlerName=[" + getHandlerName() + "][NISV][EXCEPTION]"+session.getId() + ", RemoteAddress = " + session.getRemoteAddress() != null ? session.getRemoteAddress() : "");
            log.debug("HandlerName=[" + getHandlerName() + "]" + cause != null ? cause.getMessage() : "Throwable object is null.");
            log.debug("HandlerName=[" + getHandlerName() + "]" + cause.getMessage() != null ? cause.getMessage() : cause.toString());
            log.debug("HandlerName=[" + getHandlerName() + "]Session information => " + session.toString());            
            
            if (cause != null && cause.getMessage() != null && cause.getMessage().contains("SSL handshake failed")) {
                // Security Alarm
                /*
                 * Seurity alarm has to be done with threshold */
                try {
                    EventUtil.sendEvent("Security Alarm",
                            TargetClass.Modem, IPUtil.formatTrim(session.getRemoteAddress().toString()),
                            new String[][] {{"message", "Uncertificated Access"}});
                }
                catch (Exception e) {
                    log.error(e, e);
                }
                // INSERT START SP-193
                CheckThreshold.updateCount(session.getRemoteAddress().toString(), ThresholdName.AUTHENTICATION_ERROR);
                // INSERT END SP-193  
            }
            else if (cause.getMessage().contains("Connection reset by peer")) {
                log.warn("HandlerName=[" + getHandlerName() + "]" + cause.getMessage());
            }
            else {
                // INSERT START SP-193
                CheckThreshold.updateCount(session.getRemoteAddress().toString(), ThresholdName.INVALID_PACKET);
                // INSERT END SP-193        
            }
        }
        finally {
        	log.debug("HandlerName=[" + getHandlerName() + "][code trace] sessionId=" + session.getId() + ", exceptionCaught 호출1=" + session.getRemoteAddress());
            session.closeOnFlush();
            log.debug("HandlerName=[" + getHandlerName() + "][code trace] sessionId=" + session.getId() + ", exceptionCaught 호출2=" + session.getRemoteAddress());
        }
    }
    
    @Override
    public void messageReceived(IoSession session, Object message) throws Exception
    {
        log.debug((isCmdAdapter == true ? "[FEP-COMMAND-SERVER] ": "") + "############## HandlerName=[" + getHandlerName() + "] NI MessageReceived : " + message.toString() + ", RemoteAddress=" + session.getRemoteAddress());
        byte bx[] = null;

        if (message instanceof IoBuffer) {
            log.debug("HandlerName=[" + getHandlerName() + "][NISV][RECEIVE]"+session.getId());
            IoBuffer buffer = (IoBuffer) message;
            bx = buffer.array();
            log.debug("HandlerName=[" + getHandlerName() + "][messageReceived]"+Hex.decode(((IoBuffer)message).array()));    // 51F804005300000B1211111111110C52000000000009820001C10100020000A1DE
            GeneralFrame gframe = new GeneralFrame();
            GeneralDataFrame data = new GeneralDataFrame();
             
            Map<Integer, byte[]> multiFrame = (Map<Integer, byte[]>)session.getAttribute("multiFrame");
            multiFrame = gframe.decode(multiFrame, bx);
            
           // log.debug("[messageReceived]"+Hex.decode(((IoBuffer)message).array()));
            
            byte[] ackData = null;
            if (gframe.fcAck == FrameControl_Ack.Ack) {
                ackData = gframe.ack(null);
                //requesting
                session.write(ackData);
                log.debug("HandlerName=[" + getHandlerName() + "]send ACK Data:"+ Hex.decode(ackData));
            }
            else if (gframe.fcAck == FrameControl_Ack.CRC) {
                byte[] crc = (byte[])session.getAttribute("crc");
                log.debug("CRC[" + Hex.decode(crc) + "]");
                ackData = gframe.ack(crc);
                //requesting
                session.write(ackData);
                log.debug("HandlerName=[" + getHandlerName() + "]send CRC ACK Data:"+ Hex.decode(ackData));
            }
            
            // if frame is multi, continue to get last frame
            if (gframe.fcPending == FrameControl_Pending.MultiFrame) {
                session.setAttribute("multiFrame", multiFrame);
                log.debug("MULTI_SIZE[" + multiFrame.size() + "]");
                return;
            }
            else {
                multiFrame = null;
            }
            
            String nodeId = Hex.decode(gframe.getSrcAddress());
            String ipAddr = IPUtil.formatTrim(session.getRemoteAddress().toString());
             
            
            //request Ack On
            NICommandAction commandAction = null;
            switch (gframe.foType) {
            
                case Ack:
                	commandAction = getNICommandAction(session);
                    if(commandAction != null && commandAction.isUseAck() == true){
                        MultiSession bpSession = commandAction.getMultiSession(session);
                        commandAction.executeAck(bpSession, gframe);
                    }else{
                        byte[] sendData = data.make(gframe,null);
                        //requesting
                        session.write(sendData);
                        log.debug("HandlerName=[" + getHandlerName() + "][ACK]sendData : " + sendData);                        
                    }
                    break;
                case Command:
                    // packet is modem information response for command
                    log.debug("HandlerName=[" + getHandlerName() + "][NIPT]:isCmdAdapter[" + isCmdAdapter + "]");
                    if (isCmdAdapter
                            && gframe._commandType == CommandType.Trap
                            && gframe._commandFlow == CommandFlow.Trap) {

                        // AsyncCommandLog --- need a debuging
                        log.debug("HandlerName=[" + getHandlerName() + "][NIPT]:isCmdAdapter");
                        NI_MBB_Action_SP niMbbAction = new NI_MBB_Action_SP();
                        niMbbAction.executeAction(session, gframe);

                    }
                    else if (!isCmdAdapter 
                            && gframe._commandType == CommandType.Trap 
                            && gframe._commandFlow == CommandFlow.Trap) {
                        // 모뎀 미터 정보 생성
                        Command command = (Command)gframe.getPayload();
                        doTrap(nodeId, ipAddr, command);
                        
                        //FIRMWARE Update
                        boolean mbbOTAOnUploadChannel = Boolean.parseBoolean(FMPProperty.getProperty("protocol.ni.allow.mbbotaonuploadchannel", "false"));
                        if(mbbOTAOnUploadChannel){
                            NI_MBB_Action_SP niMbbAction = new NI_MBB_Action_SP();
                            niMbbAction.executeAction(session, gframe);
                        }
                    }
                    else {
                        // There is only one command request from modem. MeterSharedKey
                        if (gframe._commandFlow == CommandFlow.Request) {
                            Command command = (Command)gframe.getPayload();
                            doCommand(session, gframe, command);
                            
                            log.debug("## HandlerName=[" + getHandlerName() + "]Command closeNow call start");
                            session.closeOnFlush();
                            log.debug("## HandlerName=[" + getHandlerName() + "]Command closeNow call end");
                        }
                    }
                    
                    //response.put(session.getId(), (IoBuffer) message);
                    response.put(session.getId(), gframe);
                    
                    break;
                case AlarmEvent:
                    log.debug("NI AlarmEvent Received ~!! []" + gframe.toString());
                    AlarmEvent alarmEvt = (AlarmEvent)gframe.getPayload();
                    makeModemEvent(nodeId, alarmEvt);
                    break;
                case MeterEvent:
                    log.debug("NI MeterEvent Received ~!! []" + gframe.toString());
                    MeterEvent meterEvt = (MeterEvent)gframe.getPayload();
                    makeMeterEvent(nodeId, meterEvt);
                    break;
                case Metering :
                    // get a metering and then in queue
                    /*Object meteringObj = session.getAttribute("metering");
                    ByteArrayOutputStream meteringOut = null;
                    if (meteringObj == null)
                        meteringOut = new ByteArrayOutputStream();
                    else
                        meteringOut = (ByteArrayOutputStream)meteringObj;
                    
                    // 무조건 바이트어레이로 만든다.
                    meteringOut.write(((MeteringData)gframe.getPayload()).getData());
                    session.setAttribute("metering", meteringOut);
                    
                    if (gframe.getFcPending() == FrameControl_Pending.LastFrame) {
                        ((MeteringData)gframe.getPayload()).decode(meteringOut.toByteArray());
                        meteringOut.close();
                        session.removeAttribute("metering");
                        
                        MDData mdData = ((MeteringData)gframe.getPayload()).getMDData();
                        mdData.setNetworkType(gframe.getNetworkType());
                        mdData.setIpAddr(session.getRemoteAddress().toString());
                        mdData.setMcuId(nodeId);
                        mdData.setNetworkType(gframe.getNetworkType());
                        putServiceData(session, ProcessorHandler.SERVICE_MEASUREMENTDATA, mdData);
                    }
                    else {
                        log.debug("METERING_DATA_PENDING");
                    }*/
                    MDData mdData = ((MeteringData)gframe.getPayload()).getMDData();
                    mdData.setNetworkType(gframe.getNetworkType());
                    mdData.setIpAddr(session.getRemoteAddress().toString());
                    mdData.setMcuId(nodeId);
                    mdData.setNetworkType(gframe.getNetworkType());
                    putServiceData(session, ProcessorHandler.SERVICE_MEASUREMENTDATA, mdData);
                    
                    break;
                case Firmware :
                	commandAction = getNICommandAction(session);
                	MultiSession mSession = commandAction.getMultiSession(session);
                    if(mSession != null){
                        try {
                            commandAction.executeTransaction(mSession, gframe);                            
                        } catch (Exception e) {
                    		ModemDao modemDao = DataUtil.getBean(ModemDao.class);
                    		Modem modem = modemDao.get(mSession.getBypassDevice().getModemId());
                    		TargetClass tClass = TargetClass.valueOf(modem.getModemType().name());
                    		
                            String openTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
                            EventAlertLog event = new EventAlertLog();
                            event.setStatus(EventStatus.Open);
                            event.setOpenTime(openTime);

                            Firmware firmwareFrame = (Firmware) gframe.getPayload();
                            String command = firmwareFrame.get_upgradeCommand().name();
                            
                            /* OTA END&RESULT Event */
                            openTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
                            EV_SP_200_65_0_Action action2 = new EV_SP_200_65_0_Action();
                            action2.makeEvent(
                            		  tClass
                            		, mSession.getBypassDevice().getModemId()
                            		, tClass
                            		, openTime
                            		, "0"
                            		, OTA_UPGRADE_RESULT_CODE.OTAERR_NI_TRN_FAIL
                            		, "NICommandAction excute Fail - " + command
                            		, "HES");
                            action2.updateOTAHistory(mSession.getBypassDevice().getModemId(), DeviceType.Modem, openTime, OTA_UPGRADE_RESULT_CODE.OTAERR_NI_TRN_FAIL, "NICommandAction excute Fail - " + command);
                            
                            EV_SP_200_66_0_Action action3 = new EV_SP_200_66_0_Action();
                            action3.makeEvent(
                            		  tClass
                            		, mSession.getBypassDevice().getModemId()
                            		, tClass
                            		, openTime
                            		, OTA_UPGRADE_RESULT_CODE.OTAERR_NI_TRN_FAIL
                            		, null
                            		, "HES");
                            action3.updateOTAHistory(mSession.getBypassDevice().getModemId(), DeviceType.Modem, openTime, OTA_UPGRADE_RESULT_CODE.OTAERR_NI_TRN_FAIL);

                            log.error("HandlerName=[" + getHandlerName() + "][" + commandAction.getClass().getSimpleName() + 
                                    "] Command Action Excute error [" + command + "][Session=" + session.getRemoteAddress() + "] - " + e.toString());
                            
            				// Mulit session close
                            log.debug("## HandlerName=[" + getHandlerName() + "]closeMultiSession 호출1");
                            closeMultiSession(session);
                            log.debug("## HandlerName=[" + getHandlerName() + "]closeMultiSession 호출2");
                        }
                    }else{
                    	log.error("HandlerName=[" + getHandlerName() + "]Can not found MultiSession.");
                    	throw new Exception("Can not found MultiSession.");
                    }

                    break;
			default:
				break;
            }
            
            NetworkStatus ns = gframe.getNetworkStatus();
            
            JpaTransactionManager txmanager = DataUtil.getBean(JpaTransactionManager.class);
            ModemDao modemDao = DataUtil.getBean(ModemDao.class);
            TransactionStatus txstatus = null;
            
            try {
                txstatus = txmanager.getTransaction(null);
                
                Modem node = modemDao.get(nodeId);
                
                if (node == null) {
                    String supplierName = new String(FMPProperty.getProperty("default.supplier.name").getBytes("8859_1"), "UTF-8");
                    log.debug("Supplier Name[" + supplierName + "]");
                    SupplierDao supplierDao = DataUtil.getBean(SupplierDao.class);
                    Supplier supplier = supplierName !=null ? supplierDao.getSupplierByName(supplierName):null;
                    
                    // TLS 통신 모뎀 (MBB, Ethernet) 은 RF (SubGiga) 용 모뎀은 없음.
                    if (ns instanceof NetworkStatusSub1GhzForSORIA) {
                        node = new SubGiga();
                        if (IPUtil.checkIPv6(ipAddr))
                            ((SubGiga)node).setIpv6Address(ipAddr);
                        else
                            node.setIpAddr(ipAddr);
                        node.setProtocolType(Protocol.IP.name());
                        node.setModemType(ModemType.SubGiga.name());
                        
                        String parentNodeId = ((NetworkStatusSub1GhzForSORIA)ns).getParentNode();
                        Modem parentNode = modemDao.get(parentNodeId);
                        if (parentNode != null) {
                            if (node != null) {
                                node.setModem(parentNode);
                            }
                        }
                        
                        // INSERT START SP-316
                        int rssi = (int)(((NetworkStatusSub1GhzForSORIA)ns).getRssi());
                        if (node != null) {
                            ((SubGiga)node).setRssi(rssi);
                        }
                        // INSERT END SP-316                                  
                    }
                    else if (ns instanceof NetworkStatusMBB) {
                        node = new MMIU();
                        if (IPUtil.checkIPv6(ipAddr))
                            ((MMIU)node).setIpv6Address(ipAddr);
                        else
                            node.setIpAddr(ipAddr);
                        node.setProtocolType(Protocol.SMS.name());
                        node.setModemType(ModemType.MMIU.name());
                    }
                    else if (ns instanceof NetworkStatusEthernet) {
                        node = new MMIU();
                        if (IPUtil.checkIPv6(ipAddr))
                            ((MMIU)node).setIpv6Address(ipAddr);
                        else
                            node.setIpAddr(ipAddr);
                        node.setProtocolType(Protocol.IP.name());
                        node.setModemType(ModemType.MMIU.name());
                    }
                    
                    String defaultLocName = FMPProperty.getProperty("loc.default.name");
                    LocationDao locDao = DataUtil.getBean(LocationDao.class);
                    CodeDao codeDao = DataUtil.getBean(CodeDao.class);
                    
                    if(defaultLocName != null && !"".equals(defaultLocName)){               
                        if(locDao.getLocationByName(StringUtil.toDB(defaultLocName))!=null 
                                && locDao.getLocationByName(StringUtil.toDB(defaultLocName)).size()>0) {
                            node.setLocation(locDao.getLocationByName(StringUtil.toDB(defaultLocName)).get(0));
                        }else {
                            node.setLocation(locDao.getAll().get(0));   
                        }
                    }
                    
                    node.setDeviceSerial(nodeId);
                    node.setSupplier(supplier);
                    node.setInstallDate(DateTimeUtil.getDateString(new Date()));
                    node.setLastLinkTime(DateTimeUtil.getDateString(new Date()));
                    node.setProtocolVersion("0102");
                    node.setNameSpace("SP");
                    node.setModemStatus(codeDao.findByCondition("code", "1.2.7.3"));
                    modemDao.add(node);
                    
                    // equipment install event
                    EventUtil.sendEvent("Equipment Installation",
                            TargetClass.valueOf(node.getModemType().name()),
                            node.getDeviceSerial(),
                            new String[][] {});
                }
                else {
                    boolean isUpdated = false;
    
                    if (node.getModemStatus() == null || 
                            !node.getModemStatus().getName().equals("Normal")) {
                        CodeDao codeDao = DataUtil.getBean(CodeDao.class);
                        node.setModemStatus(codeDao.findByCondition("code", "1.2.7.3"));
                        isUpdated = true;
                    }
                    
                    String lastLinkTime = DateTimeUtil.getDateString(new Date());
                    if (node.getLastLinkTime() == null || 
                            !node.getLastLinkTime().substring(0, 12).equals(lastLinkTime.substring(0, 12))) {
                        isUpdated = true;
                        node.setLastLinkTime(lastLinkTime);
                    }
                    
                    if (ns instanceof NetworkStatusSub1GhzForSORIA) {
                        String parentNodeId = ((NetworkStatusSub1GhzForSORIA)ns).getParentNode();
                        Modem parentNode = modemDao.get(parentNodeId);
                        if (parentNode != null && node.getParentModemId() != null && node.getParentModemId() != parentNode.getId()) {
                            isUpdated = true;
                            node.setModem(parentNode);
                        }
                        
                        // INSERT START SP-316
                        int rssi = (int)(((NetworkStatusSub1GhzForSORIA)ns).getRssi());
                        if (!((SubGiga)node).getRssi().equals(rssi)) {
                            isUpdated = true;
                            ((SubGiga)node).setRssi(rssi);
                        }
                        // INSERT END SP-316                    
                        
                        if (IPUtil.checkIPv6(ipAddr)) {
                            if (((SubGiga)node).getIpv6Address() != null && 
                                    !((SubGiga)node).getIpv6Address().equals(ipAddr)) {
                                isUpdated = true;
                                ((SubGiga)node).setIpv6Address(ipAddr);
                            }
                        }
                        else {
                            if (node.getIpAddr() != null && 
                                    !node.getIpAddr().equals(ipAddr)) {
                                isUpdated = true;
                                node.setIpAddr(ipAddr);
                            }
                        }
                    }
                    else if (ns instanceof NetworkStatusMBB) {
                        if (IPUtil.checkIPv6(ipAddr)) {
                            if(((MMIU)node).getIpv6Address() != null && 
                                    !((MMIU)node).getIpv6Address().equals(ipAddr)){
                                isUpdated = true;
                                ((MMIU)node).setIpv6Address(ipAddr);
                            }
                        }
                        else {
                            if(((MMIU)node).getIpAddr() != null &&
                                    !((MMIU)node).getIpAddr().equals(ipAddr)){
                                isUpdated = true;
                                ((MMIU)node).setIpAddr(ipAddr);
                            }
                        }
                        
                        if (!node.getProtocolType().equals(Protocol.SMS.name())) {
                            isUpdated = true;
                            node.setProtocolType(Protocol.SMS.name());
                        }
                    }
                    else if (ns instanceof NetworkStatusEthernet) {
                        if (IPUtil.checkIPv6(ipAddr)) {
                            if(((MMIU)node).getIpv6Address() != null && 
                                    !((MMIU)node).getIpv6Address().equals(ipAddr)){
                                isUpdated = true;
                                ((MMIU)node).setIpv6Address(ipAddr);
                            }
                        }
                        else {
                            if(((MMIU)node).getIpAddr() != null &&
                                    !((MMIU)node).getIpAddr().equals(ipAddr)){
                                isUpdated = true;
                                ((MMIU)node).setIpAddr(ipAddr);
                            }
                        }
                        
                        if (!node.getProtocolType().equals(Protocol.IP.name())) {
                            isUpdated = true;
                            node.setProtocolType(Protocol.IP.name());
                        }
                    }
                    
                    if (isUpdated) {
                        modemDao.update(node);
                    }
                }
                // CommLog
                txmanager.commit(txstatus);
            }
            catch (Exception e) {
                if (txstatus != null) txmanager.rollback(txstatus);
            }
        }
    }  //~messageReceived
    
    private void makeModemEvent(String nodeId, AlarmEvent alarmEvt) {
        EV_SP_240_2_0_Action action_240 = DataUtil.getBean(EV_SP_240_2_0_Action.class);
        JpaTransactionManager txmanager = (JpaTransactionManager)DataUtil.getBean("transactionManager");
        TransactionStatus txstatus = null;
        try {
            txstatus = txmanager.getTransaction(null);
            for (int i = 0; i < alarmEvt.getCount(); i++) {
                action_240.makeModemEvent(nodeId, alarmEvt.getTime()[i], 
                        alarmEvt.getAlarmId()[i].getCode(), 
                        Hex.decode(DataUtil.get4ByteToInt(alarmEvt.getPayload()[i])));
            }
            txmanager.commit(txstatus);
        }
        catch (Exception e) {
            log.error(e, e);
            
            if (txstatus != null) txmanager.rollback(txstatus);
        }
    }
    
    private void makeMeterEvent(String nodeId, MeterEvent meterEvt) throws Exception {
        EV_SP_220_2_0_Action action_220 = DataUtil.getBean(EV_SP_220_2_0_Action.class);
        JpaTransactionManager txmanager = (JpaTransactionManager)DataUtil.getBean("transactionManager");
        TransactionStatus txstatus = null;
        try {
            txstatus = txmanager.getTransaction(null);
            for (MeterEventFrame mef : meterEvt.getMeterEventFrame()) {
                action_220.doAlarm(nodeId, mef.getValue());
            }
            
            txmanager.commit(txstatus);
        }
        catch (Exception e) {
            log.error(e, e);
            
            if (txstatus != null) txmanager.rollback(txstatus);
        }
    }
    
    private void doTrap(String modemId, String ipAddr, Command cmd) throws Exception {
        Attribute attr = cmd.getAttribute();
        Modem modem = null;
        Meter meter = null;
        
        String supplierName = new String(FMPProperty.getProperty("default.supplier.name").getBytes("8859_1"), "UTF-8");
        log.debug("Supplier Name[" + supplierName + "]");
        
        JpaTransactionManager txmanager = DataUtil.getBean(JpaTransactionManager.class);
        SupplierDao supplierDao = DataUtil.getBean(SupplierDao.class);
        Supplier supplier = supplierName !=null ? supplierDao.getSupplierByName(supplierName):null;
        
        String defaultLocName = FMPProperty.getProperty("loc.default.name");
        LocationDao locDao = DataUtil.getBean(LocationDao.class);
        Location loc = null;
        TransactionStatus txstatus = null;
        
        try {
            txstatus = txmanager.getTransaction(null);
            
            if(defaultLocName != null && !"".equals(defaultLocName)){    
                loc = locDao.getLocationByName(StringUtil.toDB(defaultLocName)).get(0);
            }
            else {
                loc = locDao.getAll().get(0);
            }
            
            NIAttributeId attrId = null;
            for (Data d : attr.getData()) {
                attrId = NIAttributeId.getItem(d.getId());
                if (attrId == NIAttributeId.ModemInformation) {
                    byte[] value = d.getValue();
                    
                    int pos = 0;
                    byte[] b = new byte[8];
                    System.arraycopy(value, pos, b, 0, b.length);
                    pos += b.length;
                    
                    log.debug("MODEM_ID[" + Hex.decode(b) + "]");
                    
                    b = new byte[1];
                    System.arraycopy(value, pos, b, 0, b.length);
                    pos += b.length;
                    
                    byte modemType = b[0];
                    log.debug("MODEM_TYPE[" + modemType + "]");
                    
                    b = new byte[1];
                    System.arraycopy(value, pos, b, 0, b.length);
                    pos += b.length;
                    
                    int resetTime = DataUtil.getIntToBytes(b);
                    log.debug("RESET_TIME[" + resetTime + "]");
                    
                    b = new byte[20];
                    System.arraycopy(value, pos, b, 0, b.length);
                    pos += b.length;
                    
                    String nodeKind = new String(b).trim();
                    log.debug("NODE_KIND[" + nodeKind + "]");
                    
                    b = new byte[2];
                    System.arraycopy(value, pos, b, 0, b.length);
                    pos += b.length;
                    
                    String swVer = Hex.decode(b);
                    swVer = Double.parseDouble(swVer.substring(0, 2) + "." + swVer.substring(2, 4)) + "";
                    log.debug("SW_VER[" + swVer + "]");
                    
                    b = new byte[2];
                    System.arraycopy(value, pos, b, 0, b.length);
                    pos += b.length;
                    
                    String buildNo = DataUtil.getIntTo2Byte(b)+"";
                    log.debug("BUILD_NUM[" + buildNo + "]");
                    
                    b = new byte[2];
                    System.arraycopy(value, pos, b, 0, b.length);
                    pos += b.length;
                    
                    String hwVer = Hex.decode(b);
                    hwVer = Double.parseDouble(hwVer.substring(0, 2) + "." + hwVer.substring(2, 4)) + "";
                    log.debug("HW_VER[" + hwVer + "]");
                    
                    b = new byte[1];
                    System.arraycopy(value, pos, b, 0, b.length);
                    pos += b.length;
                    
                    byte modemStatus = b[0];
                    log.debug("STATUS[" + modemStatus + "]");
                    
                    b = new byte[1];
                    System.arraycopy(value, pos, b, 0, b.length);
                    pos += b.length;
                    
                    byte modemMode = b[0];
                    log.debug("MODE[" + modemMode + "]");
                    
                    ModemDao modemDao = DataUtil.getBean(ModemDao.class);
                    modem = modemDao.get(modemId);
                    
                    if (modem != null) {
                        boolean isUpdated = false;
                        if (modem.getFwVer() == null || !modem.getFwVer().equals(swVer)) {
                            isUpdated = true;
                            modem.setFwVer(swVer);
                        }
                        if (modem.getSwVer() == null || !modem.getSwVer().equals(swVer)) {
                            isUpdated = true;
                            modem.setSwVer(swVer);
                        }
                        if (modem.getFwRevision() == null || !modem.getFwRevision().equals(buildNo)) {
                            isUpdated = true;
                            modem.setFwRevision(buildNo);
                        }
                        if (modem.getHwVer() == null || !modem.getHwVer().equals(hwVer)) {
                            isUpdated = true;
                            modem.setHwVer(hwVer);
                        }
                        if (modem.getNodeKind() == null || !modem.getNodeKind().equals(nodeKind)) {
                            isUpdated = true;
                            modem.setNodeKind(nodeKind);
                        }
                        if (modem.getResetCount() == null || modem.getResetCount() != resetTime) {
                            isUpdated = true;
                            modem.setResetCount(resetTime);
                        }
                        if (modem.getNameSpace() == null || "".equals(modem.getNameSpace())) {
                            isUpdated = true;
                            modem.setNameSpace("SP");
                        }
                        if (modem.getProtocolVersion() == null || "".equals(modem.getProtocolVersion())) {
                            isUpdated = true;
                            modem.setProtocolVersion("0102");
                        }
                        
                        if (modem.getModemStatus() == null || !modem.getModemStatus().getName().equals("Normal")) {
                            CodeDao codeDao = DataUtil.getBean(CodeDao.class);
                            modem.setModemStatus(codeDao.findByCondition("code", "1.2.7.3"));
                            isUpdated = true;
                        }
                        
                        if (modemType == 0x20 || modemType == 0x021) {
                            if (modem.getProtocolType() == null || 
                                    (modem.getProtocolType() != null && 
                                    !modem.getProtocolType().equals(Protocol.IP.name()))) {
                                isUpdated = true;
                                modem.setProtocolType(Protocol.IP.name());
                                modem.setModemType(ModemType.SubGiga.name());
                            }
                            
                            if (IPUtil.checkIPv6(ipAddr)) {
                                if (((SubGiga)modem).getIpv6Address() == null || 
                                        (!((SubGiga)modem).getIpv6Address().equals(ipAddr))) {
                                    isUpdated = true;
                                    ((SubGiga)modem).setIpv6Address(ipAddr);
                                }
                            }
                            else {
                                if (modem.getIpAddr() == null || !modem.getIpAddr().equals(ipAddr)) {
                                    isUpdated = true;
                                    modem.setIpAddr(ipAddr);
                                }
                            }
                        }
                        // MBB
                        else if (modemType == 0x22) {
                            if (modem.getProtocolType() == null || 
                                    (modem.getProtocolType() != null &&
                                    !modem.getProtocolType().equals(Protocol.SMS.name()))) {
                                isUpdated = true;
                                modem.setProtocolType(Protocol.SMS.name());
                                modem.setModemType(ModemType.MMIU.name());
                            }
                            
                            if (IPUtil.checkIPv6(ipAddr)) {
                                if (((MMIU)modem).getIpv6Address() == null || 
                                        (!((MMIU)modem).getIpv6Address().equals(ipAddr))) {
                                    isUpdated = true;
                                    ((MMIU)modem).setIpv6Address(ipAddr);
                                }
                            }
                            else {
                                if (modem.getIpAddr() == null || !modem.getIpAddr().equals(ipAddr)) {
                                    isUpdated = true;
                                    modem.setIpAddr(ipAddr);
                                }
                            }
                            
                        }
                        // Ethernet
                        else if (modemType == 0x23) {
                            if (modem.getProtocolType() == null || 
                                    (modem.getProtocolType() != null &&
                                    !modem.getProtocolType().equals(Protocol.IP.name()))) {
                                isUpdated = true;
                                modem.setProtocolType(Protocol.IP.name());
                                modem.setModemType(ModemType.MMIU.name());
                            }
                            
                            if (IPUtil.checkIPv6(ipAddr)) {
                                if (((MMIU)modem).getIpv6Address() == null || 
                                        (!((MMIU)modem).getIpv6Address().equals(ipAddr))) {
                                    isUpdated = true;
                                    ((MMIU)modem).setIpv6Address(ipAddr);
                                }
                            }
                            else {
                                if (modem.getIpAddr() == null || !modem.getIpAddr().equals(ipAddr)) {
                                    isUpdated = true;
                                    modem.setIpAddr(ipAddr);
                                }
                            }
                        }
                        
                        String lastLinkTime = DateTimeUtil.getDateString(new Date());
                        if (modem.getLastLinkTime() == null || 
                                !modem.getLastLinkTime().substring(0, 12).equals(lastLinkTime.substring(0, 12))) {
                            isUpdated = true;
                            modem.setLastLinkTime(lastLinkTime);
                        }
                        
                        if (isUpdated) modemDao.update(modem);
                    }
                    else {
                        if (modemType == 0x20 || modemType == 0x021) {
                            modem = new SubGiga();
                            modem.setProtocolType(Protocol.IP.name());
                            modem.setModemType(ModemType.SubGiga.name());
                            
                            if (IPUtil.checkIPv6(ipAddr))
                                ((SubGiga)modem).setIpv6Address(ipAddr);
                            else
                                modem.setIpAddr(ipAddr);
                        }
                        // MBB
                        else if (modemType == 0x22) {
                            modem = new MMIU();
                            modem.setProtocolType(Protocol.SMS.name());
                            modem.setModemType(ModemType.MMIU.name());
                            
                            if (ipAddr != null && ipAddr.contains(":"))
                                ((MMIU)modem).setIpv6Address(ipAddr);
                            else
                                modem.setIpAddr(ipAddr);
                            
                        }
                        // Ethernet
                        else if (modemType == 0x23) {
                            modem = new MMIU();
                            modem.setProtocolType(Protocol.IP.name());
                            modem.setModemType(ModemType.MMIU.name());
                            
                            if (ipAddr != null && ipAddr.contains(":"))
                                ((MMIU)modem).setIpv6Address(ipAddr);
                            else
                                modem.setIpAddr(ipAddr);
                        }
                        DeviceModelDao modelDao = DataUtil.getBean(DeviceModelDao.class);
                        DeviceModel model = modelDao.findByCondition("name", nodeKind);
                        if (model != null) modem.setModel(model);
                        
                        modem.setDeviceSerial(modemId);
                        modem.setFwVer(swVer);
                        modem.setSwVer(swVer);
                        modem.setFwRevision(buildNo);
                        modem.setHwVer(hwVer);
                        modem.setSupplier(supplier);
                        modem.setLocation(loc);
                        modem.setInstallDate(DateTimeUtil.getDateString(new Date()));
                        modem.setLastLinkTime(DateTimeUtil.getDateString(new Date()));
                        modem.setNameSpace("SP");
                        modem.setProtocolVersion("0102");
                        
                        CodeDao codeDao = DataUtil.getBean(CodeDao.class);
                        modem.setModemStatus(codeDao.findByCondition("code", "1.2.7.3"));
                        
                        modemDao.add(modem);
                        try {
                            EventUtil.sendEvent("Equipment Installation",
                                    TargetClass.valueOf(modem.getModemType().name()),
                                    modem.getDeviceSerial(),
                                    new String[][] {});
                        }
                        catch (Exception e) {
                            log.error(e, e);
                        }
                    }
                }
                else if (attrId == NIAttributeId.MeterInformation) {
                    byte[] value = d.getValue();
                    
                    int pos = 0;
                    byte[] b = new byte[1];
                    System.arraycopy(value, pos, b, 0, b.length);
                    pos += b.length;
                    
                    byte meterCommStatus = b[0];
                    log.debug("METER_COMM_STATUS[" + meterCommStatus + "]");
                    
                    b = new byte[1];
                    System.arraycopy(value, pos, b, 0, b.length);
                    pos += b.length;
                    
                    int meterCnt = DataUtil.getIntToBytes(b);
                    log.debug("METER_COUNT[" + meterCnt + "]");
                    
                    b = new byte[20];
                    // 485인경우 여러개의 미터가 붙을 수 있지만 소리아는 한개
                    String meterId = null;
                    for (int i = 0; i < meterCnt; i++) {
                        System.arraycopy(value, pos, b, 0, b.length);
                        pos += b.length;
                        
                        meterId = new String(b).trim();
                    }
                    MeterDao meterDao = DataUtil.getBean(MeterDao.class);
                    meter = meterDao.get(meterId);
                    
                    if (meter == null) {
                        meter = new EnergyMeter();
                        meter.setMdsId(meterId);
                        meter.setSupplier(supplier);
                        meter.setLocation(loc);
                        meter.setMeterType(CommonConstants.getMeterTypeByName("EnergyMeter"));
                        
                        CodeDao codeDao = DataUtil.getBean(CodeDao.class);
                        meter.setMeterStatus(codeDao.findByCondition("code", "1.3.3.8")); // New Registered
                        meterDao.add(meter);
                        
                        try {
                            EventUtil.sendEvent("Equipment Installation",
                                    TargetClass.valueOf(meter.getMeterType().getName()),
                                    meter.getMdsId(),
                                    new String[][] {});
                        }
                        catch (Exception e) {
                            log.error(e, e);
                        }
                    }
                }
            }
            
            validateRelation(meter, modem);
            txmanager.commit(txstatus);
        }
        catch (Exception e) {
            if (txstatus != null) txmanager.rollback(txstatus);
        }
    }
    
    private void validateRelation(Meter meter, Modem modem) throws Exception
    {
        if (meter != null) {
            ModemDao modemDao = DataUtil.getBean(ModemDao.class);
            MeterDao meterDao = DataUtil.getBean(MeterDao.class);
            
            Modem orgModem = meter.getModem();
    
            if (orgModem == null || !orgModem.getDeviceSerial().equals(modem.getDeviceSerial())) {
                // TODO meterDao.update(meter);
                
                if (modem.getModemType() == ModemType.ZEUMBus || modem.getModemType() == ModemType.SubGiga) {
                    Set<Meter> m = modem.getMeter();
                    if (m == null) {
                        m = new HashSet<Meter>();
                    }
                    m.add(meter);
                    modem.setMeter(m);
                }
                else {
                    for (Meter _m : modem.getMeter()) {
                        if (_m != null) _m.setModem(null);
                    }
                    
                    Set<Meter> m = new HashSet<Meter>();
                    m.add(meter);
                    modem.setMeter(m);
                    
                    // 모뎀 교체로 처리한다.
                    if (orgModem != null) {
                        orgModem.setMeter(null);
                        
                        EventUtil.sendEvent("Equipment Replacement",
                                TargetClass.valueOf(modem.getModemType().name()),
                                modem.getDeviceSerial(),
                                new String[][] {{"equipType", modem.getModemType().name()},
                                                {"oldEquipID", orgModem.getDeviceSerial()},
                                                {"newEquipID", modem.getDeviceSerial()}
                                });
                    }
                }
                
                meter.setModem(modem);
                
                modemDao.update(modem);
                meterDao.update(meter);
            }
            // TODO 관계 검증
        }
    }
    
    public void doCommand(IoSession session, GeneralFrame gframe, Command cmd) throws Exception {
        Attribute attr = cmd.getAttribute();
        NIAttributeId attrId = null;
        for (Data d : attr.getData()) {
            attrId = NIAttributeId.getItem(d.getId());
            if (attrId == NIAttributeId.MeterSharedKey) {
                byte[] value = d.getValue();
                
                int pos = 0;
                byte[] b = new byte[1];
                System.arraycopy(value, pos, b, 0, b.length);
                pos += b.length;
                log.debug("ReqeustInfo[" + DataUtil.getIntToBytes(b) + "]");
                
                b = new byte[8];
                System.arraycopy(value, pos, b, 0, b.length);
                pos += b.length;
                log.debug("ModemEUI[" + Hex.decode(b) + "]");
                String modemId = Hex.decode(b);
                
                b = new byte[1];
                System.arraycopy(value, pos, b, 0, b.length);
                pos += b.length;
                int len = DataUtil.getIntToBytes(b);
                log.debug("Len[" + len + "]");
                
                b = new byte[len];
                System.arraycopy(value, pos, b, 0, b.length);
                pos += b.length;
                String meterId = new String(b);
                log.debug("MeterId[" + meterId + "]");
                
                OacServerApi api  = new OacServerApi();
                HashMap<String,String> sharedKey = api.getMeterSharedKey(modemId, meterId);
                if ( sharedKey != null ){
                    String masterKey = sharedKey.get("MasterKey");
                    String unicastKey = sharedKey.get("UnicastKey");
                    String multicastKey = sharedKey.get("MulticastKey");
                    String authKey = sharedKey.get("AuthenticationKey");
                    
                    byte[] cmdFrame = gframe.msKeyFrame(cmd.getFrameTid(), masterKey, unicastKey, multicastKey, authKey);
                    
                    session.write(cmdFrame);
                }
            }
            else if (attrId == NIAttributeId.ModemTime) {
                session.write(gframe.modemTimeFrame(cmd.getFrameTid()));
            }
        }
    }
    
//    public byte[] getResponse(IoSession session, long tid)
//            throws Exception
//    {
//        long key = tid;
//        long stime = System.currentTimeMillis();
//        long ctime = 0;
//        int waitResponseCnt = 0;
//        while(session.isConnected())
//        { 
//            if(response.containsKey(key)) 
//            { 
//                byte[] obj = ((IoBuffer)response.get(key)).array(); 
//                response.remove(key); 
//                if(obj == null) 
//                    continue; 
//                return obj; 
//            } 
//            else
//            {
//                waitResponse();
//                ctime = System.currentTimeMillis();
//                if(((ctime - stime)/1000) > responseTimeout)
//                {
//                    log.debug("getResponse:: SESSION IDLE COUNT["+session.getIdleCount(IdleStatus.BOTH_IDLE)+"]");
//                    response.remove(key); 
//                    throw new Exception("[NICL][TID : " + key +"],[Response Timeout:"+responseTimeout +"]");
//                }
//            }
//        }
//        return null;
//    }

    
    public GeneralFrame getResponse(IoSession session, long tid)
            throws Exception
    {
        long key = tid;
        long stime = System.currentTimeMillis();
        long ctime = 0;
        //int waitResponseCnt = 0;
        GeneralFrame obj = null;
        
        while(session.isConnected())
        { 
            if(response.containsKey(key)) 
            { 
                obj = (GeneralFrame) response.get(key); 
                response.remove(key); 
                if(obj == null) 
                    continue;
                log.debug("HandlerName=[" + getHandlerName() + "] getResponse success.");
                return obj; 
            } 
            else
            {
                waitResponse();
                ctime = System.currentTimeMillis();
                if(((ctime - stime)/1000) > responseTimeout)
                {
                    log.debug("HandlerName=[" + getHandlerName() + "]getResponse:: SESSION IDLE COUNT["+session.getIdleCount(IdleStatus.BOTH_IDLE)+"]");
                    response.remove(key); 
                    throw new Exception("[NICL][TID : " + key +"],[Response Timeout:"+responseTimeout +"]");
                }
            }
        }
        return null;
    }
    
    
    
    /**
     * wait util received command response data
     */
    public void waitResponse()
    {
        synchronized(resMonitor)
        { 
            try { resMonitor.wait(500);
            } catch(InterruptedException ie) {ie.printStackTrace();}
        }
    }

//    @Override
//    public void messageReceived(IoSession session, Object message)
//            throws Exception {
//     System.out.println("[NISV][RECEIVE]"+session.getId());
//         if (message instanceof IoBuffer) {
//            IoBuffer buffer = (IoBuffer) message;
//            int pos = 6;
//            int orgPayLoadCnt =0;
//            int rtnPayLoadCnt =0;
//
//            byte[] bx = buffer.array();
//            byte[] b = new byte[1];
//            ByteBuffer resultData =null;
//            byte[] gerneralData = null;
//            
//            System.out.println("[NISV][soria RF]"+Hex.decode(b));
//            System.arraycopy(bx, 3, b, 0, b.length);//Frame Control
//            String controlFrame = DataUtil.getBit(b[0]);
//            System.out.println("[NISV][message]"+Hex.decode(bx));
//            
//            /**
//             * 0:ACK・ｼ ・肥ｲｭ﨑們ｧ ・喜搆.
//             * 1:ACK ・肥ｲｭ
//             * 2:・倆哩﨑 Task ・肥ｲｭ
//             * - Sleep Node・川・ ・ｬ・ｩ
//             * - ・ｨ・・川・ DCU・・Task (Command, Firmware Upgrade) ・呷梠・､・ｴ ・壱株・ ・肥ｲｭ 﨑 ・・・ｬ・ｩ
//             */
//            System.out.println("[NISV][controlFrame]"+Hex.decode(b));
//            System.out.println("[NISV][controlFrame]"+controlFrame);
//            System.out.println("[NISV][controlFrame]"+controlFrame.substring(6,8));
//            requestAck = DataUtil.getBitToInt(controlFrame.substring(6,8),"%d");
//            System.out.println("[NISV][ACK]"+requestAck);
//            if(requestAck.equals("1")){
//               aCnt++;
//               System.out.println("[NISV][ACK]cnt:"+aCnt);
//             responseData(session,ByteBuffer.wrap(new byte[]{(byte)0x51,(byte)0xF8,(byte)0x00,(byte)0x01
//                     ,(byte)0x50,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x02,(byte)0x03,(byte)0x04,(byte)0x05
//                     ,(byte)0x06,(byte)0x07,(byte)0x50,(byte)0x51,(byte)0x52,(byte)0x53,(byte)0x54,(byte)0x55
//                     ,(byte)0x56,(byte)0x57,(byte)0x10,(byte)0x10,(byte)0x10,(byte)0x80,(byte)0x70,(byte)0x01
//                     ,(byte)0x01,(byte)0x01,(byte)0x01,(byte)0xD3,(byte)0x9B}));
//             //
//            }else if(requestAck.equals("2")){
//             //
//            }
//            
//            b = new byte[1];
//            System.arraycopy(bx, 4, b, 0, b.length);//Frame Option
//            //soria RF
//            String optionFrame = DataUtil.getBit(b[0]);
//            if(optionFrame.substring(1,2).equals("1") ){//include (Network Status)
//             pos +=17;
//            }
//
//            byte addresType = DataUtil.getByteToInt(DataUtil.getBitToInt(optionFrame.substring(2,4),"%d0000"));
//            setFrameOption_AddressType(addresType);
//            switch (foAddrType) {
//              case None:
//                  break;
//              case Source:
//              case Destination:
//                  pos +=8;
//                  break;
//              case SrcDest:
//                  pos +=16;
//                  break;
//              }
//            
//            System.out.println("[NISV][POS]"+pos);
//            byte[] preData = new byte[pos];
//            System.arraycopy(bx, 0, preData, 0, preData.length);
//            System.out.println("[NISV][preData]"+Hex.decode(preData));
//            
//            ResponseResult respRst = new ResponseResult();
//            
//            byte frameType = DataUtil.getByteToInt(DataUtil.getBitToInt(optionFrame.substring(4,8),"%d"));
//            setFrameOption_Type(frameType);
//            byte[] frameData = null;
//            switch (foType) {
//              case Ack:
//                  rtnPayLoadCnt = 2;
//                  frameData= new byte[1];
//                  System.arraycopy(bx, pos+2, frameData, 0, frameData.length);
//                  System.out.println("[NISV][framePayload]"+Hex.decode(frameData));
//                  //preData+payloadTotalLen+frameLen+commandLen+CRC
//                  gerneralData = new byte[preData.length+2+frameData.length+4];
//                  resultData = ByteBuffer.wrap(gerneralData);//payload・ｸ・ｴ ・ ・ｴ・ｩ.
//                  resultData.put(preData);
//                  resultData.put(DataUtil.get2ByteToInt(3));
//                  resultData.put(frameData);
//                  //・ｰ・ｼ・・・ｬ奓ｴ
//                  respRst.setStatus(respRst.status.Success);//・ｱ・ｵ
//                  resultData.put(respRst.status.getCode());
//
//                  System.out.println("[resultData]"+Hex.decode(resultData.array()));
//                  System.out.println("[resultData]"+resultData.array().length);                   
//                  System.out.println("[resultData]"+preData.length+2+frameData.length+2+2+2);
//                  bCnt++;
//                  System.out.println("[NISV][ACK][Bypass]cnt:"+bCnt);
//                  break;
//              case Bypass:
//                  rtnPayLoadCnt = 2;
//                  frameData= new byte[1];
//                  System.arraycopy(bx, pos+2, frameData, 0, frameData.length);
//                  System.out.println("[NISV][framePayload]"+Hex.decode(frameData));
//                  //preData+payloadTotalLen+frameLen+commandLen+CRC
//                  gerneralData = new byte[preData.length+2+frameData.length+4];
//                  resultData = ByteBuffer.wrap(gerneralData);//payload・ｸ・ｴ ・ ・ｴ・ｩ.
//                  resultData.put(preData);
//                  resultData.put(DataUtil.get2ByteToInt(3));
//                  resultData.put(frameData);
//                  //・ｰ・ｼ・・・ｬ奓ｴ
//                  respRst.setStatus(respRst.status.Success);//・ｱ・ｵ
//                  resultData.put(respRst.status.getCode());
//
//                  System.out.println("[resultData]"+Hex.decode(resultData.array()));
//                  System.out.println("[resultData]"+resultData.array().length);                   
//                  System.out.println("[resultData]"+preData.length+2+frameData.length+2+2+2);
//                  bCnt++;
//                  System.out.println("[NISV][ACK][Bypass]cnt:"+bCnt);
//                  break;
//              case Command:
//                     frameData= new byte[5];
//                     System.arraycopy(bx, pos+2, frameData, 0, frameData.length);
//                     System.out.println("[NISV][framePayload]"+Hex.decode(frameData));
//                   //payload length
//                     b = new byte[2];
//                     System.out.println("[NISV][length]"+pos);
//                     System.arraycopy(bx, pos, b, 0, b.length);
//                     pos +=b.length;
//                     System.out.println("[NISV][Payload Len]"+Hex.decode(b));
//                     orgPayLoadCnt = DataUtil.getIntTo2Byte(b);
//                     
//                     //Payload Data
//                     b = new byte[orgPayLoadCnt];
//                     System.arraycopy(bx, pos, b, 0, b.length);
//                     System.out.println("[NISV][PayLoad Data]"+Hex.decode(b));
//                     //Attribute ・駈桁
//                     setAttributeList(new byte[]{b[3],b[4]});
//                     System.out.println("[NISV][PayLoad ID]"+Hex.decode((new byte[]{b[3]}))+","+Hex.decode((new byte[]{b[4]})));
//                     switch (_attributeList) {
//                      case ResetModem:
//                          break;
//                      case UploadMeteringData:
//                          rtnPayLoadCnt = 2;
//                          //preData+payloadTotalLen+frameLen+commandLen+CRC
//                          gerneralData = new byte[preData.length+2+frameData.length+6];
//                          resultData = ByteBuffer.wrap(gerneralData);//payload・ｸ・ｴ ・ ・ｴ・ｩ.
//                          resultData.put(preData);
//                          //Frame cnt+Payload cnt
//                          //command:5+2(payload)+2(status)
//                          resultData.put(DataUtil.get2ByteToInt(9));
//                          resultData.put(frameData);
//                          resultData.put(DataUtil.get2ByteToInt(2));
//                          //・ｰ・ｼ・・・ｬ奓ｴ
//                          respRst.setStatus(respRst.status.Success);//・ｱ・ｵ
//                          resultData.put(respRst.status.getCode());
//                          
//                          System.out.println("[resultData]"+Hex.decode(resultData.array()));
//                          System.out.println("[resultData]"+resultData.array().length);                   
//                          System.out.println("[resultData]"+preData.length+2+frameData.length+2+2+2);
//                          break;
//                      case FactorySetting:
//                          break;
//                      case CoordinatorInformation:
//                          rtnPayLoadCnt = 2;
//                          //preData+payloadTotalLen+frameLen+commandLen+CRC
//                          gerneralData = new byte[preData.length+2+frameData.length+6];
//                          resultData = ByteBuffer.wrap(gerneralData);//payload・ｸ・ｴ ・ ・ｴ・ｩ.
//                          resultData.put(preData);
//                          //Frame cnt+Payload cnt
//                          //command:5+2(payload)+2(status)
//                          resultData.put(DataUtil.get2ByteToInt(9));
//                          resultData.put(frameData);
//                          resultData.put(DataUtil.get2ByteToInt(2));
//                          //・ｰ・ｼ・・・ｬ奓ｴ
//                          respRst.setStatus(respRst.status.Success);//・ｱ・ｵ
//                          resultData.put(respRst.status.getCode());
//                          break;
//                        case ModemStatus:
//                            resultData =ByteBuffer.wrap(new byte[]{(byte)0x51,(byte)0xf8,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0x00,(byte)0x00,(byte)0x08,(byte)0x81,(byte)0x00,(byte)0x01,(byte)0x10,(byte)0x04,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x6f,(byte)0x34});
//                            responseData(session,resultData);
//                          break;
//                        case ModemEventLog:
//                            resultData =ByteBuffer.wrap(new byte[]{(byte)0x51,(byte)0xf8,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0x00,(byte)0x01,(byte)0x44,(byte)0xc3,(byte)0x00,(byte)0x01,(byte)0x10,(byte)0x06,(byte)0x01,(byte)0x3d,(byte)0x00,(byte)0x16,(byte)0x00,(byte)0x15,(byte)0x07,(byte)0xe0,(byte)0x04,(byte)0x14,(byte)0x02,(byte)0x00,(byte)0x02,(byte)0x10,(byte)0x01,(byte)0x00,(byte)0x13,(byte)0x00,(byte)0x0a,(byte)0x00,(byte)0x14,(byte)0x07,(byte)0xe0,(byte)0x04,(byte)0x14,(byte)0x02,(byte)0x00,(byte)0x02,(byte)0x10,(byte)0x01,(byte)0x00,(byte)0x12,(byte)0x00,(byte)0x0a,(byte)0x00,(byte)0x13,(byte)0x07,(byte)0xe0,(byte)0x04,(byte)0x14,(byte)0x02,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x01,(byte)0x00,(byte)0x11,(byte)0x00,(byte)0x0a,(byte)0x00,(byte)0x12,(byte)0x07,(byte)0xe0,(byte)0x04,(byte)0x14,(byte)0x02,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x01,(byte)0x00,(byte)0x10,(byte)0x00,(byte)0x0a,(byte)0x00,(byte)0x11,(byte)0x07,(byte)0xe0,(byte)0x04,(byte)0x14,(byte)0x02,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x01,(byte)0x00,(byte)0x0f,(byte)0x00,(byte)0x0a,(byte)0x00,(byte)0x10,(byte)0x07,(byte)0xe0,(byte)0x04,(byte)0x14,(byte)0x02,(byte)0x00,(byte)0x06,(byte)0x10,(byte)0x08,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x04,(byte)0x00,(byte)0x0f,(byte)0x07,(byte)0xe0,(byte)0x04,(byte)0x14,(byte)0x02,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x01,(byte)0x00,(byte)0x0e,(byte)0x00,(byte)0x0a,(byte)0x00,(byte)0x0e,(byte)0x07,(byte)0xe0,(byte)0x04,(byte)0x14,(byte)0x02,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x01,(byte)0x00,(byte)0x0d,(byte)0x00,(byte)0x0a,(byte)0x00,(byte)0x0d,(byte)0x07,(byte)0xe0,(byte)0x04,(byte)0x14,(byte)0x02,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x01,(byte)0x00,(byte)0x0c,(byte)0x00,(byte)0x0a,(byte)0x00,(byte)0x0c,(byte)0x07,(byte)0xe0,(byte)0x04,(byte)0x14,(byte)0x02,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x01,(byte)0x00,(byte)0x0b,(byte)0x00,(byte)0x0a,(byte)0x00,(byte)0x0b,(byte)0x07,(byte)0xe0,(byte)0x04,(byte)0x14,(byte)0x02,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x01,(byte)0x00,(byte)0x0a,(byte)0x00,(byte)0x07,(byte)0x00,(byte)0x0a,(byte)0x07,(byte)0xe0,(byte)0x04,(byte)0x14,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x01,(byte)0x00,(byte)0x09,(byte)0x00,(byte)0x0a,(byte)0x00,(byte)0x09,(byte)0x07,(byte)0xe0,(byte)0x04,(byte)0x14,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x01,(byte)0x00,(byte)0x08,(byte)0x00,(byte)0x0a,(byte)0x00,(byte)0x08,(byte)0x07,(byte)0xe0,(byte)0x04,(byte)0x14,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x01,(byte)0x00,(byte)0x07,(byte)0x00,(byte)0x0a,(byte)0x00,(byte)0x07,(byte)0x07,(byte)0xe0,(byte)0x04,(byte)0x14,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x01,(byte)0x00,(byte)0x06,(byte)0x00,(byte)0x0a,(byte)0x00,(byte)0x06,(byte)0x07,(byte)0xe0,(byte)0x04,(byte)0x14,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x01,(byte)0x00,(byte)0x05,(byte)0x00,(byte)0x0a,(byte)0x00,(byte)0x05,(byte)0x07,(byte)0xe0,(byte)0x04,(byte)0x14,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x01,(byte)0x00,(byte)0x04,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x04,(byte)0x07,(byte)0xe0,(byte)0x04,(byte)0x14,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x01,(byte)0x00,(byte)0x03,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x03,(byte)0x07,(byte)0xe0,(byte)0x04,(byte)0x14,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x01,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x07,(byte)0x00,(byte)0x02,(byte)0x07,(byte)0xe0,(byte)0x04,(byte)0x14,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x01,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x0a,(byte)0x00,(byte)0x01,(byte)0x07,(byte)0xe0,(byte)0x04,(byte)0x14,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x08,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x09,(byte)0xeb,(byte)0xb4});
//                            responseData(session,resultData);
//                          break;
//                        case NullBypassOpen:
//                              rtnPayLoadCnt = 2;
//                              //preData+payloadTotalLen+frameLen+commandLen+CRC
//                              gerneralData = new byte[preData.length+1+frameData.length+2];
//                              resultData = ByteBuffer.wrap(gerneralData);//payload・ｸ・ｴ ・ ・ｴ・ｩ.
//                              resultData.put(preData);
//                              //Frame cnt+Payload cnt
//                              //command:5+2(payload)+2(status)
//                              resultData.put(DataUtil.get2ByteToInt(9));
//                              resultData.put(frameData);
//                              resultData.put(DataUtil.get2ByteToInt(2));
//                              //・ｰ・ｼ・・・ｬ奓ｴ
//                              respRst.setStatus(respRst.status.Success);//・ｱ・ｵ
//                              resultData.put(respRst.status.getCode());
//                          break;
//                        case NullBypassClose:
//                              rtnPayLoadCnt = 2;
//                              //preData+payloadTotalLen+frameLen+commandLen+CRC
//                              gerneralData = new byte[preData.length+1+frameData.length+2];
//                              resultData = ByteBuffer.wrap(gerneralData);//payload・ｸ・ｴ ・ ・ｴ・ｩ.
//                              resultData.put(preData);
//                              //Frame cnt+Payload cnt
//                              //command:5+2(payload)+2(status)
//                              resultData.put(DataUtil.get2ByteToInt(9));
//                              resultData.put(frameData);
//                              resultData.put(DataUtil.get2ByteToInt(2));
//                              //・ｰ・ｼ・・・ｬ奓ｴ
//                              respRst.setStatus(respRst.status.Success);//・ｱ・ｵ
//                              resultData.put(respRst.status.getCode());
//                              break;
//                        case ModemInformation:
//                              rtnPayLoadCnt = 2;
//                              //preData+payloadTotalLen+frameLen+commandLen+CRC
//                              gerneralData = new byte[preData.length+1+frameData.length+2];
//                              System.out.println("[NISV][gerneralData]"+gerneralData.length); 
//                              
//                              resultData = ByteBuffer.wrap(gerneralData);//payload・ｸ・ｴ ・ ・ｴ・ｩ.
//                              resultData.put(preData);
//                              System.out.println("[NISV][preData]"+preData.length);
//                              //Frame cnt+Payload cnt
//                              //command:5+2(payload)+2(status)
//                              resultData.put(DataUtil.get2ByteToInt(9));
//                              resultData.put(frameData);
//                              System.out.println("[NISV][preData]"+frameData.length);
//                              resultData.put(DataUtil.get2ByteToInt(2));
//                              //・ｰ・ｼ・・・ｬ奓ｴ
//                              respRst.setStatus(respRst.status.Success);//・ｱ・ｵ
//                              resultData.put(respRst.status.getCode());
//                              break;
//                      default:
//                          break;
//                     }
//                     System.out.println("[NISV][ACK][Command]cnt:"+cCnt);
//                  break;
//              default:
//                  break;
//              }
//            
//            if(rtnPayLoadCnt>0){
//                 //CRC ・ｱ・・
//                 b = new byte[2];
//                 
//                 System.out.println("[orgPayLoadCnt]"+orgPayLoadCnt);
//                 System.out.println("[pos]"+pos);
//                 System.out.println("[bx]"+Hex.decode(bx));
//                 
////                   System.arraycopy(bx, orgPayLoadCnt+pos, b, 0, b.length);
////                   resultData.put(b);
//                 
//                 CRCUtil crc16 = new CRCUtil();
//                 byte[] crc =crc16.Calculate_ZigBee_Crc(resultData.array(),(char)0x0000);
//                 DataUtil.convertEndian(crc);
//                 resultData.put(crc);
//                 
//                 requestAck="0";
//                 responseData(session,resultData);
//                 
//            }else{
//             session.closeNow();
//            }
//        }
//    }
    
    @Override
    public void messageSent(IoSession session, Object message) 
           throws Exception {
       if (message instanceof IoBuffer) {
            IoBuffer buffer = (IoBuffer) message;
            SocketAddress remoteAddress = session.getRemoteAddress();
            log.debug("HandlerName=[" + getHandlerName() + "][NISV][messageSent][SessionID=" + session.getId() + "]:"+remoteAddress+","+Hex.decode(buffer.array()));
        }
    }
    
    @Override
    public void sessionClosed(IoSession session) throws Exception {
        log.debug("HandlerName=[" + getHandlerName() + "][NISV][CLOSED]"+session.getId() + ",  sessionClosed 호출. RemoteAddress=" + session.getRemoteAddress());
		//SP-691
        session.closeOnFlush();
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        log.debug("HandlerName=[" + getHandlerName() + "][NISV][CREATE]"+session.getId() + "[isCmdAdapter : " + isCmdAdapter +"], RemoteAddress[" + session.getRemoteAddress() + "]");
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status)
            throws Exception {
        //・専ｲｩ・・ ・ｬ・ｩ・・・嵓誤ｦｬ・・ｴ・・・ｬ・ｴ・・
        //・ｴ・﨑・・ｰ・ｴ夋 ・・・・・・懍・・們ｧ ・喜揆・・嶸ｸ・罹勢
//        if(session.getIdleCount(IdleStatus.READER_IDLE) >= 3)
//        {
//            session.write(FrameUtil.getEOT());
    	log.debug("HandlerName=[" + getHandlerName() + "][code trace] sessionId=" + session.getId() + ", sessionIdle 호출1=" + session.getRemoteAddress());
            FrameUtil.waitAfterSendFrame();
            log.debug("HandlerName=[" + getHandlerName() + "][code trace] sessionId=" + session.getId() + ", sessionIdle 호출2=" + session.getRemoteAddress());
    		//SP-691
            session.closeOnFlush();
            log.debug("HandlerName=[" + getHandlerName() + "][code trace] sessionId=" + session.getId() + ", sessionIdle 호출3=" + session.getRemoteAddress());
//        }
        log.debug("HandlerName=[" + getHandlerName() + "][NISV][IDLE]"+session.getId());
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        // ・懍ｴ・・ｰ・ｴ奓ｰ・ ・們侠 ・們来・・阜 ・ｸ・們擽 ・罹ｰｩ・ｨ
       log.debug("HandlerName=[" + getHandlerName() + "][NISV][OPEN]"+session.getId() + "[isCmdAdapter : " + isCmdAdapter +"], RemoteAddress[" + session.getRemoteAddress() + "]");
       session.getConfig().setWriteTimeout(writeTimeout);
       // isCmdAdapter is true, ask modem who it is (modem information)
       /*if (isCmdAdapter) {
           log.debug("[NISV][OPEN]"+session.getId() + "[isCmdAdapter]");
       }*/
    }
    
	@Override
	public void inputClosed(IoSession session) throws Exception {
		log.debug("HandlerName=[" + getHandlerName() + "][code trace] sessionId=" + session.getId() + ", inputClosed 호출1=" + session.getRemoteAddress());
		session.closeOnFlush();
		log.debug("HandlerName=[" + getHandlerName() + "][code trace] sessionId=" + session.getId() + ", inputClosed 호출2=" + session.getRemoteAddress());
	}
}
