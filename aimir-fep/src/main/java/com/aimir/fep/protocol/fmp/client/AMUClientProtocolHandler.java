package com.aimir.fep.protocol.fmp.client;

import java.util.Hashtable;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.aimir.fep.protocol.fmp.common.CDMATarget;
import com.aimir.fep.protocol.fmp.common.CircuitTarget;
import com.aimir.fep.protocol.fmp.common.GSMTarget;
import com.aimir.fep.protocol.fmp.common.PSTNTarget;
import com.aimir.fep.protocol.fmp.exception.FMPACKTimeoutException;
import com.aimir.fep.protocol.fmp.exception.FMPException;
import com.aimir.fep.protocol.fmp.exception.FMPResponseTimeoutException;
import com.aimir.fep.protocol.fmp.frame.AMUGeneralDataConstants;
import com.aimir.fep.protocol.fmp.frame.AMUGeneralDataFrame;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.FrameUtil;

/**
 * {@link AMUProtocolHandler} implementation of AMU Protocol).
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 2. 19. 오후 5:25:43$
 */
public class AMUClientProtocolHandler extends IoHandlerAdapter{

	private static Log log = LogFactory.getLog(AMUClientProtocolHandler.class);

    //private Object enqMonitor = new Object();
    private Object resMonitor = new Object();
    private Object msgMonitor = new Object();
    private Object resourceObj = null;
    
    private Object msg = null;   
    private int idleTime = Integer.parseInt(FMPProperty.getProperty(
                "Protocol.idleTime","5"));
    private int retry = Integer.parseInt(FMPProperty.getProperty(
               "Protocol.retry","3"));
    private int responseTimeout = Integer.parseInt(
            FMPProperty.getProperty("Protocol.responseTimeout","15"));

    private Hashtable response = new Hashtable();
    private FMPClientResource resource = null;
    public  final static String isActiveAMUKey = "isActiveAMU";
    private Boolean  activeAMU =  Boolean.TRUE;
    private Boolean  deActiveAMU = Boolean.FALSE;
    private boolean  isSessionOpened = false;
    private boolean  isCircuitOpened = false;
    private long openCircuitTime = 0L;
    private long closedCircuitTime = 0L;
     
    /**
     * inherited method from ProtocolHandlerAdapter
     */
    public void exceptionCaught(IoSession session, Throwable cause )
    {
        // Close connection when unexpected exception is caught.
        cause.printStackTrace();
        session.write(FrameUtil.getEOT());
        FrameUtil.waitAfterSendFrame();
        closeCircuit(session);
        session.closeNow();
    }
    
    
    // processing received receivedAMUGeneralDataFrame
    private void receivedAMUGeneralDataFrame(IoSession session,
            AMUGeneralDataFrame frame) throws Exception
    { 
    	
    	log.debug("###### receivedAMUGeneralDataFrame start ######");
    	// ##### Sequence Serialization Check #####
        if(!isSequenceSerial(session,frame)){
        	log.debug("#### Sequence not Serialize!! #####");
        	return;
        }
        
        if(frame != null) 
        { 
        	long targetId = DataUtil.getLongToBytes(frame.getSource_addr()); 
        	String key = Long.toString(targetId);
            log.info("Response Put Key(targetId)["+key+"]");
            response.put(key, frame); 
            
            synchronized(resMonitor) 
            { 
                resMonitor.notify(); 
            }
        } 
        else{
            log.info("received AMUGeneralDataFrame is null");
        }
        log.debug("###### receivedAMUGeneralDataFrame End ######");
        
    }

    
    /**
     * inherited method from ProtocolHandlerAdapter
     * handling GeneralDataFrame
     *
     * @param session <code>IoSession</code> session
     * @param message <code>Object</code> decoded GeneralDataFrame
     */
    public void messageReceived(IoSession session, Object message )
    {
        try 
        {
            if(message instanceof AMUGeneralDataFrame)
            {
            	AMUGeneralDataFrame frame = (AMUGeneralDataFrame)message;
            	log.debug("messageReceived :: AMUGeneralDataFrame ");
            	receivedAMUGeneralDataFrame(session , frame);
               
            }else if(message instanceof IoBuffer){
            	// 예외처리
            	sessionClosed(session);
            }
            else
            {
                synchronized(msgMonitor) 
                {
                    msg = message;
                    msgMonitor.notify();
                }
            }
        }catch(Exception ex)
        {
            log.error("AMUClientProtocolHandler::messageReceived "
                    + " failed" ,ex);
        }
    }

    public void sessionIdle(IoSession session, IdleStatus status)
    throws Exception
    {
        log.info("IDLE COUNT : " 
                + session.getIdleCount(IdleStatus.BOTH_IDLE));
        if(session.getIdleCount(IdleStatus.BOTH_IDLE) >= retry)
        {
            /** modified by D.J Park in 2006.04.10
             * if wait response ,then do not close session
             */
            if(response.values().size() < 1)
            {
                session.write(FrameUtil.getEOT());
                //FrameUtil.waitAfterSendFrame();
                closeCircuit(session);
                session.closeNow();
            }
        }
    }

    /**
     * inherited method from ProtocolHandlerAdapter
     */
    public void sessionOpened(IoSession session)
    {
        session.setAttribute(isActiveAMUKey,activeAMU);
        session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE,
                idleTime);
        isSessionOpened = true;
        log.debug("sessionOpened : " + session.getRemoteAddress());
    }

    /**
     * inherited method from ProtocolHandlerAdapter
     */
    public void sessionClosed(IoSession session)
    {
        synchronized(resMonitor) { resMonitor.notify(); }
        synchronized(msgMonitor) { msgMonitor.notify(); }

        session.removeAttribute("sendframes");
        session.removeAttribute("lastSequence");
        session.removeAttribute("wck");

        try
        {
            if(resource != null && resourceObj != null)
            {
                log.debug("#### AMUClientProtocalHandler: Resource Release ###");
                resource.release(resourceObj);
                log.debug("AMUClientProtocalHandler: "+resource.getActiveResourcesString());
                log.debug("AMUClientProtocalHandler: "+resource.getIdleResourcesString());
            }
            session.closeNow();
        }catch(Exception ex)
        {
            log.error(ex,ex);
        }
        
        isSessionOpened = false;
        isCircuitOpened = false;
       
        log.debug("Session Closed : "+ session.getRemoteAddress());
        
    }

    /**
     * set command data response timeout
     *
     * @param responseTimeout <code>String</code> timeout of response for request command
     */
    public void setResponseTimeout(int responseTimeout)
    {
        this.responseTimeout = responseTimeout;
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

    /**
     * wait util received command response data and return Response
     * 
     * @param session <code>IoSession</code> session
     * @throws FMPException
     */
    public AMUGeneralDataFrame getResponse(IoSession session, String key)
    throws FMPException
	{
	    log.debug("Get Target ID : " + key);
	    long stime = System.currentTimeMillis();
	    long ctime = 0;
	    
	    while(session.isConnected())
	    { 
	        log.debug("session isConnected ");
	        
	        if(response.isEmpty()){
	        	log.debug("reponse is empty");
	        }else{
	       
	        	Iterator itr = response.keySet().iterator();
	        	while (itr.hasNext()) {
	        	    String ikey = (String)itr.next();
	        	    log.debug(key + " - " + response.get(ikey));
	        	}
	        }
	        
	        log.debug("response.containsKey : " + response.containsKey(key));
	        
	        if(response.containsKey(key)) 
	        { 
	            Object obj = (AMUGeneralDataFrame)response.get(key); 
	            response.remove(key); 
	            if(obj == null) {
	            	log.debug("############ continue ############");
	            	continue;
	            }
	            log.debug("##### get response , retrun AMUGeneralDataFrame #####");
	            
	            return (AMUGeneralDataFrame)obj; 
	        } 
	        else
	        {
	        	log.debug("##### wait Response #####");
	            waitResponse();
	            ctime = System.currentTimeMillis();
	
	            if(((ctime - stime)/1000) > responseTimeout)
	            {
	                log.debug("getResponse:: SESSION IDLE COUNT["
	                        +session.getIdleCount(IdleStatus.BOTH_IDLE) 
	                        +"]");
	                if(session.getIdleCount(IdleStatus.BOTH_IDLE) >= retry)
	                {
	                    response.remove(key); 
	                    throw new FMPResponseTimeoutException(" tid : " 
	                            + key +" Response Timeout[" 
	                            +responseTimeout +"]");
	                }
	            }
	        }
	    }
	    return null;
	}
    /*
    public AMUGeneralDataFrame getResponse(IoSession session)
        throws Exception
    {
    	try{
    		byte[] reponseStream = (byte[]) getMsg(session, 100);
            IoBuffer resbuf = null;
            resbuf = IoBuffer.allocate(reponseStream.length); 
            resbuf.put(reponseStream); 
            resbuf.flip();
            return (AMUGeneralDataFrame)AMUGeneralDataFrame.decode(resbuf);
    	}catch(Exception e){
    		throw new Exception("get Response Error");
    	}
    }
    */
    public void setFMPResource(FMPClientResource resource,Object resourceObj)
    {
        this.resource = resource;
        this.resourceObj = resourceObj;
    }

    /**
     * wait util received Message
     */
    public void waitMsg()
    {
        synchronized(msgMonitor)
        { 
            try { msgMonitor.wait(100); 
            } catch(InterruptedException ie) {ie.printStackTrace();}
        }
    }

    /**
     * wait util received command response data and return Response
     * 
     * @param session <code>IoSession</code> session
     * @param tid <code>int</code> command request id
     * @throws FMPException
     */
    public Object getMsg(IoSession session, long timeout) 
        throws FMPException
    {
        this.msg = null;
        long stime = System.currentTimeMillis();
        long ctime = 0;
        while(session.isConnected())
        { 
            waitMsg();
            //waitResponse();
            if(this.msg != null)
                break;
            ctime = System.currentTimeMillis();
            if(((ctime - stime)/1000.0) > timeout)
            {
                log.debug(" waitTime["+((ctime - stime)/1000)+"] timeout["
                    + timeout+"]");
                throw new FMPACKTimeoutException(" Msg Receive Timeout["
                        +timeout +"]");
            }
        }
        return this.msg;
    }

    private final char CR = '\r';
    private final char LF = '\n';
    private final String CRLF = "\r\n";

    /**
     * Circuit Call
     * 
     * @param session <code>IoSession</code> session
     * @param target <code>CircuitTarget</code> Circuit Connect Information
     * @throws Exception
     */
    private void connectCircuit(IoSession session, CircuitTarget target)
    throws FMPException
    {
        String ATZ = 
            FMPProperty.getProperty("Protocol.Circuit.Command.ATZ","ATZ");

        String message = null;
        boolean atzOk = false;
        int okcnt = 0;
        for(int i = 0 ; i < 10 ; i++)
        {
            session.write(FrameUtil.getByteBuffer(ATZ+CRLF));
            try 
            {  
                message = new String((byte[])getMsg(session,3));
                log.debug("initCircuit Read MSG["+message+"]");
                if(message != null) 
                {
                    if(message.toLowerCase().indexOf("ok") >= 0)
                    {
                        okcnt++;
                        if(okcnt > 1)
                        {
                            atzOk = true;
                            break;
                        }
                    }
                    else if(message.toLowerCase().indexOf("connect") >= 0)
                        return;
                    else if(i == 0)
                    {
                        message = new String((byte[])getMsg(session,3));
                        log.debug("initCircuit Read MSG["+message+"]");
                    }
                } 
            }catch(Exception ex) {}
        }
        if(!atzOk)
            throw new FMPException("Init Modem(ATZ Command) Failed");


        String AT = "AT+CRM=129";
        if(target instanceof GSMTarget)
            AT = FMPProperty.getProperty("Protocol.Circuit.Command.AT.GSM");
        else if(target instanceof CDMATarget)
            AT = FMPProperty.getProperty("Protocol.Circuit.Command.AT.CDMA");
        else if(target instanceof PSTNTarget)
            AT = FMPProperty.getProperty("Protocol.Circuit.Command.AT.PSTN");

        log.debug("initCircuit AT["+AT+"]");
        if(AT != null && AT.length() > 1)
        {
            session.write(FrameUtil.getByteBuffer(AT+CR+LF));
            message = new String((byte[])getMsg(session,3));
            log.debug("initCircuit Read MSG["+message+"]");
            if(message == null || message.toLowerCase().indexOf("ok") < 0)
            { 
                throw new FMPException("Init Modem(AT Command) Failed");
            }
        } else
        {
            log.debug("initCurcuit AT Command Skip");
        }

        String ATDT = 
            FMPProperty.getProperty("Protocol.Circuit.Command.ATDT","ATDT");
        String mobileId = target.getMobileId();
        if(mobileId != null && mobileId.length() > 0)
            mobileId = mobileId.replaceAll("-","");
        //session.write(FrameUtil.getByteBuffer(ATDT+" "+mobileId+CR+LF));
        session.write(FrameUtil.getByteBuffer(ATDT+mobileId+CR+LF));
   
        message = new String((byte[])getMsg(session,120));
        log.debug("initCircuit Read MSG["+message+"]");
        if(message != null && message.toLowerCase().indexOf("connect") < 0) {
            log.debug("ATDT Command Failed=>"+message);
            if(message.toLowerCase().indexOf("carrier") < 0){
                throw new FMPException("Mobile is busy!");
            }
            if(message.toLowerCase().indexOf("busy") < 0){
                throw new FMPException("Mobile is busy!");
            }
            if(message.toLowerCase().indexOf("answer") < 0){
                throw new FMPException("Mobile no answer!");
            }
            if(message.toLowerCase().indexOf("dialtone") < 0){
                throw new FMPException("Mobile no dialtone!");
            }
            if(message.toLowerCase().indexOf("error") < 0){
                throw new FMPException("Modem error!");
            }
            else
            {
                throw new FMPException("Dialing Failed!");
            }
        }
    }

    /**
     * Circuit Call and intialize FMP Protocol Service 
     * 
     * @param session <code>ProtocolSession</code> session
     * @param target <code>CircuitTarget</code> Circuit Connect Information
     * @throws Exception
     */
    public void initCircuit(IoSession session,CircuitTarget target) 
        throws Exception
    {
        log.debug("initCircuit Start");
        String mobileId = target.getMobileId();
        if(mobileId == null || mobileId.length() < 5)
        {
            throw new Exception("Target Mobile ID["+mobileId
                    +"] is Invalid"); 
        }

        while(!isSessionOpened) 
        { 
            log.debug("initCircuit:: wait session opened"); 
            try { Thread.sleep(10); } catch(Exception exx) {} 
        }

        log.debug("initCircuit set isActiveAMU[false]");
        session.setAttribute(isActiveAMUKey,deActiveAMU);
        log.debug("initCircuit get isActiveAMU["
                +session.getAttribute(isActiveAMUKey)+"]");

        //String message = null;

        int callRetry = 3;
        try { callRetry= Integer.parseInt(
                FMPProperty.getProperty("Protocol.Circuit.Connect.Retry","3"));
        }catch(Exception exx){}

        int callCnt = 0;
        for(;;)
        {
            callCnt++;
            try 
            { 
                connectCircuit(session,target); 
                log.debug("#### Call CNT["+callCnt+"]");
                break;
            }catch(FMPException ex)
            {
                log.debug("#### Call CNT["+callCnt+"]");
                if(callCnt >= callRetry){
                    log.error("##Circuit Call Failed ::"
                              +ex.getMessage());
                    throw new Exception(ex.getMessage());
                } else{
                    try { Thread.sleep(1000); }catch(Exception ee) {}
                }
            }catch(Exception ex)
            {
                log.debug("#### Call CNT["+callCnt+"]");
                if(callCnt >= callRetry){
                    log.error("##Circuit Call Failed ::"
                              +ex.getMessage());
                    throw new Exception(ex.getMessage());
                } else{
                    try { Thread.sleep(1000); }catch(Exception ee) {}
                }
            }

        }

        isCircuitOpened = true;
        openCircuitTime = System.currentTimeMillis();
       
        try { Thread.sleep(1000); }catch(Exception ex) 
        {
        	throw new Exception("Sleep Failed After Calling"); 
        }
        
        int circuitIdleTime = Integer.parseInt(FMPProperty.getProperty(
                "Protocol.Circuit.Connect.Request.IdleTime","5"));
        session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE,
                circuitIdleTime);

        session.setAttribute(isActiveAMUKey,activeAMU);
        log.debug("initCircuit set isActiveAMU[true]");
    }

    /**
     * finish the circuit call
     * 
     * @param session <code>IoSession</code> session
     * @param target <code>CircuitTarget</code> Circuit Connect Information
     * @throws Exception
     */
    public void closeCircuit(IoSession session) 
    {
        try
        {
            if(!isCircuitOpened)
                return;

            log.debug("closeCircuit set isActiveAMU[false]");
            session.setAttribute(isActiveAMUKey,deActiveAMU);
            log.debug("closeCircuit get isActiveAMU["
                    +session.getAttribute(isActiveAMUKey)+"]");

            String ATH = 
                FMPProperty.getProperty("Protocol.Circuit.Command.ATH.1","+++");
            session.write(FrameUtil.getByteBuffer(ATH));
            Object recvObj = null;
            try { recvObj = getMsg(session,1); }catch(Exception ex) {}
            ATH = 
                FMPProperty.getProperty("Protocol.Circuit.Command.ATH.2","ATH");
            session.write(FrameUtil.getByteBuffer(ATH+CRLF));
            try { recvObj = getMsg(session,1); }catch(Exception ex) {}
            closedCircuitTime = System.currentTimeMillis();

            log.debug("Circuit Communication Time["
                    +(closedCircuitTime - openCircuitTime)+"]ms");
        }catch(Exception exx)
        {
            log.error(exx,exx);
        }
    }
	
    /**
     * Check Sequence Serialization
     * @param session
     * @param agdf
     * @return
     */
    public boolean isSequenceSerial(IoSession session, AMUGeneralDataFrame agdf){
    	
    	int seq = DataUtil.getIntToByte(agdf.getSequence());
    	int before_seq =0;
        if(seq == 0){
        	session.setAttribute(AMUGeneralDataConstants.RX_SEQ, (Integer)0);
        }else{
        	if(session.getAttribute(AMUGeneralDataConstants.RX_SEQ)!= null){
        		before_seq =  (Integer)session.getAttribute(AMUGeneralDataConstants.RX_SEQ);
        	}
        	if((before_seq+1) != seq){
        		session.removeAttribute(AMUGeneralDataConstants.RX_SEQ);
        		return false;
        	}
        	session.setAttribute(AMUGeneralDataConstants.RX_SEQ, seq);
        }
        return true;
    }
}