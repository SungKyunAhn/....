package com.aimir.fep.protocol.nip.server;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.constants.CommonConstants.ThresholdName;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.protocol.fmp.frame.AMUGeneralDataConstants;
import com.aimir.fep.protocol.fmp.frame.service.MDData;
import com.aimir.fep.protocol.fmp.processor.ProcessorHandler;
import com.aimir.fep.protocol.nip.client.actions.NICommandActionHandlerAdaptor;
import com.aimir.fep.protocol.nip.client.multisession.MultiSession;
import com.aimir.fep.protocol.nip.client.actions.NICommandAction;
import com.aimir.fep.protocol.nip.common.GeneralDataFrame;
import com.aimir.fep.protocol.nip.frame.GeneralFrame;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.FrameControl_Ack;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.FrameControl_Pending;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.NIAttributeId;
import com.aimir.fep.protocol.nip.frame.NetworkStatus;
import com.aimir.fep.protocol.nip.frame.NetworkStatusEthernet;
import com.aimir.fep.protocol.nip.frame.NetworkStatusMBB;
import com.aimir.fep.protocol.nip.frame.NetworkStatusSub1GhzForSORIA;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.protocol.nip.frame.payload.Firmware;
import com.aimir.fep.protocol.nip.frame.payload.Command.Attribute;
import com.aimir.fep.protocol.nip.frame.payload.Command.Attribute.Data;
import com.aimir.fep.protocol.nip.frame.payload.MeteringData;
import com.aimir.fep.protocol.security.OacServerApi;
import com.aimir.fep.trap.actions.SP.EV_SP_200_65_0_Action;
import com.aimir.fep.trap.actions.SP.EV_SP_200_66_0_Action;
import com.aimir.fep.trap.common.EV_Action.OTA_UPGRADE_RESULT_CODE;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.FrameUtil;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.threshold.CheckThreshold;
import com.aimir.model.device.Device.DeviceType;
import com.aimir.model.device.EnergyMeter;
import com.aimir.model.device.MCU;
import com.aimir.model.device.MMIU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.device.SubGiga;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.Location;
import com.aimir.model.system.Supplier;
import com.aimir.util.Condition;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.IPUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.Condition.Restriction;
import com.aimir.fep.util.FMPProperty;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.eclipse.californium.elements.RawData;
import org.eclipse.californium.elements.RawDataChannel;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.ScandiumLogger;
import org.eclipse.californium.scandium.dtls.SessionId;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionStatus;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

/**
 * {@link NiDtlsProtocolHandler}).
 *
 * @author DJ Kim
 * @version $Rev: 1 $, $Date: 2016-05-21 15:59:15 +0900 $,
 */
public class NiDtlsProtocolHandler extends NICommandActionHandlerAdaptor implements RawDataChannel
{
    static {
        ScandiumLogger.initialize(); 
        ScandiumLogger.setLevel(Level.ALL);
    }
    
    private static Log log = LogFactory.getLog(NiDtlsProtocolHandler.class);
    
    private boolean isCmdAdapter = false;
    
    private int responseTimeout = Integer.valueOf(FMPProperty.getProperty("protocol.dtls.response.timeout","60"));
    
    private Object resMonitor = new Object();
    
    private Hashtable<SessionId, Object> response = new Hashtable<SessionId, Object>();
    
    private DTLSConnector dtlsConnector;
    
    private SessionId sessionId;
    
    // if frame is multi
    private Map<Integer, byte[]> multiFrame = null;
    
    public NiDtlsProtocolHandler(boolean isCmdAdapter, DTLSConnector dtlsConnector, InetSocketAddress peerAddr, String handlerName) throws Exception {
    	setHandlerName(handlerName);
    	log.debug("############### New NiDtlsProtocolHandler : HandlerName=" + getHandlerName() + " ##############");
        this.isCmdAdapter = isCmdAdapter;
        this.dtlsConnector = dtlsConnector;
    }
    
    private void putServiceData(String serviceType, Serializable data) {
        ProcessorHandler processorHandler = DataUtil.getBean(ProcessorHandler.class);
        try {
            processorHandler.putServiceData(serviceType, data);
        }
        catch (Exception e) {
            log.error(e, e);
        }
    }
    
    /**
     * For scandium dtls
     * @param cmd
     * @throws Exception
     */
    public void doCommand(GeneralFrame gframe, Command cmd, InetSocketAddress _peerAddr) throws Exception {
        Attribute attr = cmd.getAttribute();
        
        for (Data d : attr.getData()) {
            if (DataUtil.getIntTo2Byte(d.getId()) == 0xC005) {
                byte[] value = d.getValue();
                
                int pos = 0;
                byte[] b = new byte[1];
                System.arraycopy(value, pos, b, 0, b.length);
                pos += b.length;
                log.debug("ReqeustInfo[" + DataUtil.getIntToBytes(b) + "]");
                int reqInfo = (int)b[0];
                
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
                HashMap<String,String> sharedKey = null;
                
                if (reqInfo == 2)
                    sharedKey = api.getPanaMeterSharedKey(modemId, meterId);
                else
                    sharedKey = api.getMeterSharedKey(modemId, meterId);
                    
                if ( sharedKey != null ){
                    String masterKey = sharedKey.get("MasterKey");
                    String unicastKey = sharedKey.get("UnicastKey");
                    String multicastKey = sharedKey.get("MulticastKey");
                    String authKey = sharedKey.get("AuthenticationKey");
                    
                    byte[] cmdFrame = gframe.msKeyFrame(cmd.getFrameTid(), masterKey, unicastKey, multicastKey, authKey);
                    
                    log.debug(_peerAddr);
                    RawData sndRawData = new RawData(cmdFrame, _peerAddr);
                    dtlsConnector.send(sndRawData);
                }
            }
        }
    }
    
    public GeneralFrame getResponse()
            throws Exception
    {
        long stime = System.currentTimeMillis();
        long ctime = 0;
        //int waitResponseCnt = 0;
        GeneralFrame obj = null;
        
        while(dtlsConnector.isRunning())
        { 
            if(response != null && sessionId != null) 
            { 
                obj = (GeneralFrame) response.get(sessionId); 
                response.remove(sessionId);                
                sessionId = null;
                if(obj == null) 
                    continue; 
                return obj; 
            } 
            else
            {
                waitResponse();
                ctime = System.currentTimeMillis();
                if(((ctime - stime)/1000) > responseTimeout)
                {
                    if (response != null && sessionId != null)
                        response.remove(sessionId); 
                    throw new Exception("[NICL][TID : " + sessionId +"],[Response Timeout:"+responseTimeout +"]");
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

    @Override
    public void receiveData(RawData rawData) {
        InetSocketAddress _peerAddr = rawData.getInetSocketAddress();
        log.debug((isCmdAdapter == true ? "[FEP-DTLS-COMMAND-SERVER] ": "") + "############## HandlerName=[" + getHandlerName() + "] NI DTLS MessageReceived, RemoteAddress=" + _peerAddr.getHostString().toUpperCase());
        log.debug("HandlerName=[" + getHandlerName() + "]" + _peerAddr);
        
        try {
            GeneralFrame gframe = new GeneralFrame();
            GeneralDataFrame data = new GeneralDataFrame();
             
            log.debug("checkAMUCRC()");
            //Check Crc
            IoBuffer in = IoBuffer.allocate(rawData.getBytes().length);
            in.put(rawData.getBytes());
            
            byte[] crc = new byte[AMUGeneralDataConstants.FRAME_CRC_LEN];
            int len     = in.limit()- AMUGeneralDataConstants.FRAME_CRC_LEN;
            
            crc[0] = in.get(len);
            crc[1] = in.get(len+1);
            
            if(!FrameUtil.checkAMUCRC(in)){
                log.error("CRC error");
                CheckThreshold.updateCount(rawData.getInetSocketAddress().getHostString(), ThresholdName.CRC);                   
            }
            else {
                multiFrame = gframe.decode(multiFrame, rawData.getBytes());
                
                /*
                 * check Ack, then send ack
                 */
                byte[] ackData = null;
                if (gframe.fcAck == FrameControl_Ack.Ack) {
                    ackData = gframe.ack(null);
                    //requesting
                    RawData sndRawData = new RawData(ackData, _peerAddr);
                    dtlsConnector.send(sndRawData);
                    log.debug("HandlerName=[" + getHandlerName() + "]send ACK Data:"+ Hex.decode(ackData));
                }
                else if (gframe.fcAck == FrameControl_Ack.CRC) {
                    log.debug("CRC[" + Hex.decode(crc) + "]");
                    ackData = gframe.ack(crc);
                    //requesting
                    RawData sndRawData = new RawData(ackData, _peerAddr);
                    dtlsConnector.send(sndRawData);
                    log.debug("HandlerName=[" + getHandlerName() + "]send CRC ACK Data:"+ Hex.decode(ackData));
                }
                
                // if frame is multi, continue to get last frame
                if (gframe.fcPending == FrameControl_Pending.MultiFrame) {
                    // session.setAttribute("multiFrame", response);
                    log.debug("MULTI_SIZE[" + multiFrame.size() + "]");
                    return;
                }
                else {
                    multiFrame = null;
                }
                
                log.debug("GeneralDataFrame Info1 = " + gframe);
                String nodeId = Hex.decode(gframe.getSrcAddress());
                String ipAddr = IPUtil.format(_peerAddr.getHostString().toUpperCase());
                log.debug("NOD_ID[" + nodeId + "], HOST_STRING[" + ipAddr + "]");
                
                NICommandAction commandAction = getNICommandAction(_peerAddr);
                //request Ack On
                switch (gframe.foType) {
                    case Ack:
                        if(commandAction != null && commandAction.isUseAck() == true){
                    		MultiSession bpSession = commandAction.getMultiSession(_peerAddr);
                    		commandAction.executeAck(bpSession, gframe);
                        }else{
                            byte[] sendData = data.make(gframe,null);
                            //requesting
                            RawData sndRawData = new RawData(sendData, _peerAddr);
                            dtlsConnector.send(sndRawData);
                            log.debug("HandlerName=[" + getHandlerName() + "][ACK]sendData : " + sendData);                   	
                        }
                        break;
                    case Command:
                    	log.debug("HandlerName=[" + getHandlerName() + "][Command]:isCmdAdapter[" + isCmdAdapter + "]");
                    	if (commandAction != null){
                    		MultiSession bpSession = commandAction.getMultiSession(_peerAddr);
                    		commandAction.executeResponse(bpSession, gframe);
                        } 
                    	else if (isCmdAdapter
                                && gframe._commandFlow == CommandFlow.Response_Trap) {
                            // get AsyncCommandLog
                            // send command
                            sessionId = dtlsConnector.getSessionByAddress(_peerAddr).getSessionIdentifier();
                            log.debug("DTLS SessionId = " + sessionId);
                            response.put(sessionId, gframe);
                        }
                        else if (gframe._commandType == CommandType.Trap  
                                && gframe._commandFlow == CommandFlow.Trap) {
                            // 모뎀 미터 정보 생성
                            Command command = (Command)gframe.getPayload();
                            doTrap(nodeId, ipAddr, command);
                        }                        
                        else {
                            // There is only one command request from modem. MeterSharedKey
                            if (gframe._commandFlow == CommandFlow.Request) {
                                Command command = (Command)gframe.getPayload();
                                doCommand(gframe, command, _peerAddr);
                                
                                // dtlsConnector.close(_peerAddr);
                            }
                        }
                        break;
                    case AlarmEvent:
                    	log.debug("NI AlarmEvent Received ~!! []" + gframe.toString());
                        break;
                    case MeterEvent:
                    	log.debug("NI MeterEvent Received ~!! []" + gframe.toString());
                        break;
                    case Metering :
                        // get a metering and then in queue
                        /*
                        if (meteringOut == null) meteringOut = new ByteArrayOutputStream();
                        // 무조건 바이트어레이로 만든다.
                        meteringOut.write(((MeteringData)gframe.getPayload()).getData());
                        
                        if (gframe.getFcPending() == FrameControl_Pending.LastFrame) {
                            ((MeteringData)gframe.getPayload()).decode(meteringOut.toByteArray());
                            meteringOut.close();
                            meteringOut = null;
                            
                            MDData mdData = ((MeteringData)gframe.getPayload()).getMDData();
                            mdData.setNetworkType(gframe.getNetworkType());
                            mdData.setIpAddr(_peerAddr.getHostString());
                            putServiceData(ProcessorHandler.SERVICE_MEASUREMENTDATA, mdData);
                        }
                        else {
                            log.debug("METERING_DATA_PENDING");
                        }
                        */
                        MDData mdData = ((MeteringData)gframe.getPayload()).getMDData();
                        mdData.setNetworkType(gframe.getNetworkType());
                        mdData.setIpAddr(_peerAddr.getHostString());
                        putServiceData(ProcessorHandler.SERVICE_MEASUREMENTDATA, mdData);
                        
                        break;
                    case Firmware :
                    	MultiSession mSession = commandAction.getMultiSession(_peerAddr);
                		if(mSession != null){
                			try {
                				commandAction.executeTransaction(mSession, gframe);
                			} catch (Exception e) {
                				ModemDao modemDao = DataUtil.getBean(ModemDao.class);
                				Modem modem = modemDao.get(mSession.getBypassDevice().getModemId());
                				TargetClass tClass = TargetClass.valueOf(modem.getModemType().name());
                				
    							Firmware firmwareFrame = (Firmware) gframe.getPayload();
    							String command = firmwareFrame.get_upgradeCommand().name();
    							
    							/* OTA END&RESULT Event */
    							String openTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
    							EV_SP_200_65_0_Action action2 = new EV_SP_200_65_0_Action();
    							action2.makeEvent(
    									  //TargetClass.Modem
    									  tClass
    									, mSession.getBypassDevice().getModemId()
    									//, TargetClass.Modem
    									, tClass
    									, openTime
    									, "0"
    									, OTA_UPGRADE_RESULT_CODE.OTAERR_NI_TRN_FAIL
    									, "NICommandAction excute Fail - " + command
    									, "HES");
    							action2.updateOTAHistory(mSession.getBypassDevice().getModemId(), DeviceType.Modem, openTime, OTA_UPGRADE_RESULT_CODE.OTAERR_NI_TRN_FAIL, "NICommandAction excute Fail - " + command);
    							
    							EV_SP_200_66_0_Action action3 = new EV_SP_200_66_0_Action();
    							action3.makeEvent(
    									  //TargetClass.Modem
    									  tClass
    									, mSession.getBypassDevice().getModemId()
    									//, TargetClass.Modem
    									, tClass
    									, openTime
    									, OTA_UPGRADE_RESULT_CODE.OTAERR_NI_TRN_FAIL
    									, null
    									, "HES");
    							action3.updateOTAHistory(mSession.getBypassDevice().getModemId(), DeviceType.Modem, openTime, OTA_UPGRADE_RESULT_CODE.OTAERR_NI_TRN_FAIL);
    							
                				log.error("HandlerName=[" + getHandlerName() + "][" + commandAction.getClass().getSimpleName() + "] Command Action Excute error[Peer=" + _peerAddr + "] - " + e.toString());
                				
                				// Mulit session close
                				log.debug("## HandlerName=[" + getHandlerName() + "]closeMultiSession 호출1");
                	        	closeMultiSession(_peerAddr);
                	        	log.debug("## HandlerName=[" + getHandlerName() + "]closeMultiSession 호출1");
                			}			
                		}
                    	break;
				default:
					break;
                }
                
                log.debug("GeneralDataFrame Info2 = " + gframe);
                
                NetworkStatus ns = gframe.getNetworkStatus();
                ModemDao modemDao = DataUtil.getBean(ModemDao.class);
                JpaTransactionManager txmanager = DataUtil.getBean(JpaTransactionManager.class);
                TransactionStatus txstatus = null;
                
                try {
                    txstatus = txmanager.getTransaction(null);
                    Modem node = modemDao.get(nodeId);
                    
                    if (node == null) {
                        String supplierName = new String(FMPProperty.getProperty("default.supplier.name").getBytes("8859_1"), "UTF-8");
                        log.debug("Supplier Name[" + supplierName + "]");
                        SupplierDao supplierDao = DataUtil.getBean(SupplierDao.class);
                        Supplier supplier = supplierName !=null ? supplierDao.getSupplierByName(supplierName):null;
                        
                        if (ns instanceof NetworkStatusSub1GhzForSORIA) {
                            node = new SubGiga();
                            if (IPUtil.checkIPv6(ipAddr))
                                ((SubGiga)node).setIpv6Address(ipAddr);
                            else
                                node.setIpAddr(ipAddr);
                            node.setProtocolType(Protocol.IP.name());
                            
                            String parentNodeId = ((NetworkStatusSub1GhzForSORIA)ns).getParentNode();
                            log.debug("PARENT_NODE[" + parentNodeId + "]");
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
                            
                            // 집중기 정보를 찾아온다.
                            // SP-669
                            MCU mcu = findMCUbyIpV6(ipAddr);
                            if (node.getMcu() == null || !mcu.getSysID().equals(node.getMcu().getSysID())) {
                                node.setMcu(mcu);
                            }
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
                        log.debug("DEFAULT_LOCATION[" + defaultLocName + "]");
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
                        // node.setIpAddr(ipAddr);
                        node.setSupplier(supplier);
                        node.setInstallDate(DateTimeUtil.getDateString(new Date()));
                        node.setLastLinkTime(DateTimeUtil.getDateString(new Date()));
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
                        if (node.getModemStatus() == null || !node.getModemStatus().getName().equals("Normal")) {
                            CodeDao codeDao = DataUtil.getBean(CodeDao.class);
                            node.setModemStatus(codeDao.findByCondition("code", "1.2.7.3"));
                            isUpdated = true;
                        }
                        
                        if (ns instanceof NetworkStatusSub1GhzForSORIA) {
                            String parentNodeId = ((NetworkStatusSub1GhzForSORIA)ns).getParentNode();
                            log.debug("PARENT_NODE[" + parentNodeId + "]");
                            
                            Modem parentNode = modemDao.get(parentNodeId);
                            if (parentNode != null && node.getParentModemId() != null && node.getParentModemId() != parentNode.getId()) {
                                isUpdated = true;
                                node.setModem(parentNode);
                            }
                            
                            // INSERT START SP-316
                            int rssi = (int)(((NetworkStatusSub1GhzForSORIA)ns).getRssi());
                            if (((SubGiga)node).getRssi() != null) {
                            	if (!((SubGiga)node).getRssi().equals(rssi)) {
                            		isUpdated = true;
                            		((SubGiga)node).setRssi(rssi);
                            	}
                            }
                            // INSERT END SP-316
                            
                            if (IPUtil.checkIPv6(ipAddr)) {
                                if (((SubGiga)node).getIpv6Address() == null || !((SubGiga)node).getIpv6Address().equals(ipAddr)) {
                                    isUpdated = true;
                                    ((SubGiga)node).setIpv6Address(ipAddr);
                                }
                            }
                            else {
                                if (node.getIpAddr() == null || !node.getIpAddr().equals(ipAddr)) {
                                    isUpdated = true;
                                    node.setIpAddr(ipAddr);
                                }
                            }
                            
                            // 집중기 정보를 찾아온다.
                            // SP-669
                            MCU mcu = findMCUbyIpV6(ipAddr);
                            if (node.getMcu() == null || !mcu.getSysID().equals(node.getMcu().getSysID())) {
                                isUpdated = true;
                                node.setMcu(mcu);
                            }
                        }
                        else if (ns instanceof NetworkStatusMBB) {
                            if (IPUtil.checkIPv6(ipAddr)) {
                                if(((MMIU)node).getIpv6Address() == null || 
                                        !((MMIU)node).getIpv6Address().equals(ipAddr)){
                                    isUpdated = true;
                                    ((MMIU)node).setIpv6Address(ipAddr);
                                }
                            }
                            else {
                                if(((MMIU)node).getIpAddr() == null ||
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
                                if(((MMIU)node).getIpv6Address() == null || 
                                        !((MMIU)node).getIpv6Address().equals(ipAddr)){
                                    isUpdated = true;
                                    ((MMIU)node).setIpv6Address(ipAddr);
                                }
                            }
                            else {
                                if(((MMIU)node).getIpAddr() == null ||
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
                        
                        String lastLinkTime = DateTimeUtil.getDateString(new Date());
                        if (node.getLastLinkTime() == null || 
                                !node.getLastLinkTime().substring(0, 12).equals(lastLinkTime.substring(0, 12))) {
                            isUpdated = true;
                            node.setLastLinkTime(lastLinkTime);
                        }
                        
                        if (isUpdated) {
                            modemDao.update(node);
                        }
                    }
                    txmanager.commit(txstatus);
                }
                catch (Exception e) {
                    if (txstatus != null) txmanager.rollback(txstatus);
                }
            }
        }
        catch (Exception e) {
            log.error(e, e);
        }
    }
    
    // SP-669
    private MCU findMCUbyIpV6(String ipAddr) {
        // 집중기 정보를 찾아온다.
        if (IPUtil.checkIPv6(ipAddr)) {
            int fromIdx = 0;
            for (int i = 0; i < 3; i++) {
                fromIdx = ipAddr.indexOf(":", fromIdx);
                fromIdx++;
            }
            
            String ipv6prefix = ipAddr.substring(0, fromIdx);
            log.debug("IPv6_PREFIX[" + ipv6prefix + "]");
            
            MCUDao mcuDao = DataUtil.getBean(MCUDao.class);
            Set<Condition> conditions = new HashSet<Condition>();
            conditions.add(new Condition("ipv6Addr", new Object[]{ipv6prefix+"%"}, null, Restriction.LIKE));
            List<MCU> mculist = mcuDao.findByConditions(conditions);
            if (mculist != null) {
                if (mculist.size() == 1) {
                    return mculist.get(0);
                }
                else {
                    fromIdx = 0;
                    for (int i = 0; i < 4; i++) {
                        fromIdx = ipAddr.indexOf(":", fromIdx);
                        fromIdx++;
                    }
                    
                    ipv6prefix = ipAddr.substring(0, fromIdx);
                    log.debug("IPv6_PREFIX[" + ipv6prefix + "]");
                    
                    conditions = new HashSet<Condition>();
                    conditions.add(new Condition("ipv6Addr", new Object[]{ipv6prefix+"%"}, null, Restriction.LIKE));
                    mculist = mcuDao.findByConditions(conditions);
                    if (mculist != null && mculist.size() == 1) {
                        return mculist.get(0);
                    }
                }
            }
        }
        return null;
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
                            
                            if (IPUtil.checkIPv6(ipAddr))
                                ((MMIU)modem).setIpv6Address(ipAddr);
                            else
                                modem.setIpAddr(ipAddr);
                            
                        }
                        // Ethernet
                        else if (modemType == 0x23) {
                            modem = new MMIU();
                            modem.setProtocolType(Protocol.IP.name());
                            modem.setModemType(ModemType.MMIU.name());
                            
                            if (IPUtil.checkIPv6(ipAddr))
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
}
