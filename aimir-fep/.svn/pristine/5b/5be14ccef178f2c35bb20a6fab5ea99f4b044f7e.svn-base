package com.aimir.fep.protocol.snmp;

import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.springframework.stereotype.Component;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.EventStatus;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.fep.util.CommandMIBNode;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.MIBNode;
import com.aimir.fep.util.SnmpMibUtil;
import com.aimir.model.device.EventAlertLog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.PDU;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;

@Component
public class SnmpProtocolHandler extends IoHandlerAdapter{
    private static Logger logger = LoggerFactory.getLogger(SnmpProtocolHandler.class);

    private int sessionIdleTime = Integer.parseInt(FMPProperty.getProperty("protocol.idle.time", "5"));
    private int enqTimeout = Integer.parseInt(FMPProperty.getProperty("protocol.enq.timeout", "10"));
    private int retry = Integer.parseInt(FMPProperty.getProperty("protocol.retry", "3"));

    SnmpMibUtil mu = null;

    public SnmpProtocolHandler(){
        //...
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        session.getConfig().setWriteTimeout(enqTimeout);
        session.getConfig().setIdleTime(IdleStatus.READER_IDLE, sessionIdleTime);

        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date d = new Date(session.getLastWriterIdleTime());
        logger.info("### sessionOpened : " + session.getRemoteAddress() + ", lastWriteIdleTime : " + sf.format(d));
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        Iterator<Object> keys = session.getAttributeKeys().iterator();
        while (keys.hasNext()) {
            Object key = keys.next();
            logger.info("key={}, value={}", key, session.getAttribute(key));
            session.removeAttribute(key);
        }

        logger.info("### Bye Bye ~ Client session closed from {}", session.getRemoteAddress().toString());
        session.closeNow();
        logger.info("### this Session is being closed or closed? = {}, Session isConnected = {}, Session isBothIdle = {}", new Object[] { session.isClosing(), session.isConnected(), session.isBothIdle() });
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        int idleCount = session.getIdleCount(IdleStatus.READER_IDLE);
        logger.info("### Session = {}, IDLE COUNT={}", session.getRemoteAddress(), idleCount);
        if (idleCount >= retry) {
            session.write(SnmpConstants.SnmpActionType.UNKNOWN);
        }
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        logger.info("### [MESSAGE_RECEIVED] from={}  SessionId={} ###", session.getRemoteAddress().toString(), session.getId());
        if(mu == null){
            mu = SnmpMibUtil.getInstance("snmpv2c");
        }
        // PDU 처리
        try {
            if(message instanceof PDU) {
                PDU pdu = (PDU)message;
                Map<String,Object> receiveMap = new HashMap<String,Object>();
                
                if(pdu != null){                                        
                    int pduType = pdu.getType();
                    logger.info("### [PDU] {}", pdu.toString());
                    if(pduType == PDU.TRAP) {
                        //TRAP 정보를 receiveMap에 저장
                        Vector vector = new Vector(5,2);
                        vector = pdu.getVariableBindings();                        
                        
                        int vSize = vector.size();                        
                        logger.info("SIZE OF Variables {}", vSize);                        
                        for(int i=0; i<vSize; i++){
                            VariableBinding vb = (VariableBinding) vector.get(i);
                            OID oid = vb.getOid();
                            String oidStr = oid.toString();
                            String trapName = mu.getName(oidStr);
                                                       
                            if(trapName!=null){
                                receiveMap.put(trapName, vb.getVariable());
                                String varStr = vb.getVariable().toString();
                                String varName = mu.getName(varStr);
                                receiveMap.put(varStr, varName);
                            }else{
                                logger.info("MIB Information of [{}] is Null", oid.toString());
                            }

                        }

                        //Sender를 맵에 저장                        
                        String senderIp = session.getRemoteAddress().toString();
                        // session에서 얻은 정보가 /0.0.0.0:162 형태로 있어서 ip만 남도록 가공함
                        senderIp = senderIp.substring(1, senderIp.lastIndexOf(":"));
                        receiveMap.put("sender", senderIp);
                        receiveMap.put("activatorIp", senderIp);                        
                       
                        //StartTime
                        receiveMap.put("startTime", session.getAttribute("startTime").toString());
                        
                        //이벤트 저장 함수  (실패면 세션 닫음)  
                        sendTrapEvent(receiveMap);
                        session.closeNow();
                    }else {
                        // 트랩이 아닌 PDU
                    	logger.info("PDU type is not trap. The data will not be saved.");
                    	session.closeNow();
                    }
                }
            }
        } catch (Exception ex){
            logger.error("Error in SnmpProtocolHandler", ex);
        }

    }
    
    // 전달받은 맵에서 트랩 종류를 구분하여, 이벤트로 전송
    public boolean sendTrapEvent(Map _receiveMap) throws Exception{
    	HashMap<String,Object> saveMap = (HashMap<String,Object>)_receiveMap;
    	OID trapOid = null;
    	String oidStr = null;
    	String trapName = null;
    	String sender = null;
    	String message = null;
    	TargetClass target = TargetClass.FEP;
    	// 사전 협의된 Trap OID가 있는지 확인
    	if(saveMap.containsKey("SnmpTrapOID")){
    		trapOid = (OID)saveMap.get("SnmpTrapOID");
    		oidStr = trapOid.toString();
    		
    		if(oidStr.contains("1.3.6.1.4.1.3204.31.10.1")){
    			// trapFromMeter, 미터시리얼을 포함하고있음
    			if(saveMap.containsKey("trapMeterSerial")){
    				sender = saveMap.get("trapMeterSerial").toString();
    				trapName = saveMap.get(oidStr).toString();
    				target = TargetClass.SubGiga;
    				message = "Trap From Meter ["+ sender +"], OID ["+ oidStr +"]";
    			}else{
    				// 미터시리얼이 없으면 에러 처리
    				return false;
    			}    			    			
    		}else if(oidStr.contains("1.3.6.1.4.1.3204.31.10.2")){
    			// trapFromMT, sender의 IP를 이용하여 이벤트 저장
    			sender = saveMap.get("sender").toString();
    			trapName = saveMap.get(oidStr).toString();
    			target = TargetClass.Modem;
    			message = "Trap From Meter-Terminal ["+ sender +"], OID ["+ oidStr +"]";
    		}else{
    			logger.error("{} is not a valid oid.", oidStr);
    			return false;
    		}
    	}else{
    		// SNMP TRAP OID가 없음
    		logger.error("saveMap has no SnmpTrapOID item.");
    		return false;
    	}    	
    	
    	//logger.info("### [MAP] {}", saveMap.toString());

    	//이벤트 전송
    	if(trapName == null || sender == null){
    		logger.error("# trapName or Sender is null. Manager can not save the trap event.");
    		return false;
    	}else{
    		EventAlertLog event = new EventAlertLog();
            event.setStatus(EventStatus.Open);
            event.setActivatorIp(saveMap.get("activatorIp").toString());
            
			EventUtil.sendEvent("Snmp Trap Event", target, sender,
			        saveMap.get("startTime").toString(),
			        new String[][] {{"message", message},{"trapname", trapName}}, event);
			
    	}        
        
        return true;
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        super.messageSent(session, message);
        logger.debug("MessageSent - [local-{}][remote-{}][msg-{}]", new Object[]{session.getLocalAddress(), session.getRemoteAddress(), message});
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        cause.printStackTrace();
        //logger.error(cause);
        logger.info(cause.getMessage(), cause);
    }
}