package com.aimir.fep.protocol.mrp.client;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.aimir.constants.CommonConstants.McuType;
import com.aimir.constants.CommonConstants.MeterModel;
import com.aimir.fep.protocol.fmp.client.FMPClientResource;
import com.aimir.fep.protocol.fmp.common.CircuitTarget;
import com.aimir.fep.protocol.fmp.common.GPRSTarget;
import com.aimir.fep.protocol.fmp.common.SlideWindow;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.protocol.fmp.datatype.OID;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.datatype.UINT;
import com.aimir.fep.protocol.fmp.exception.FMPACKTimeoutException;
import com.aimir.fep.protocol.fmp.exception.FMPResponseTimeoutException;
import com.aimir.fep.protocol.fmp.frame.ControlDataConstants;
import com.aimir.fep.protocol.fmp.frame.ControlDataFrame;
import com.aimir.fep.protocol.fmp.frame.GeneralDataConstants;
import com.aimir.fep.protocol.fmp.frame.GeneralDataFrame;
import com.aimir.fep.protocol.fmp.frame.ServiceDataConstants;
import com.aimir.fep.protocol.fmp.frame.ServiceDataFrame;
import com.aimir.fep.protocol.fmp.frame.service.CommandData;
import com.aimir.fep.protocol.fmp.frame.service.ServiceData;
import com.aimir.fep.protocol.mrp.command.frame.Command;
import com.aimir.fep.protocol.mrp.command.frame.ieiu.AuthPassCommandIEIU;
import com.aimir.fep.protocol.mrp.command.frame.ieiu.ByPassModeCommandIEIU;
import com.aimir.fep.protocol.mrp.command.frame.ieiu.ByPassModeStopCommandIEIU;
import com.aimir.fep.protocol.mrp.command.frame.ieiu.CommandIEIU;
import com.aimir.fep.protocol.mrp.command.frame.ieiu.GetBasePulse;
import com.aimir.fep.protocol.mrp.command.frame.ieiu.GetCummDatas;
import com.aimir.fep.protocol.mrp.command.frame.ieiu.GetDemandRomDatas;
import com.aimir.fep.protocol.mrp.command.frame.mmiu.AuthPassCommand;
import com.aimir.fep.protocol.mrp.command.frame.mmiu.MeterModeCommand;
import com.aimir.fep.protocol.mrp.exception.MRPError;
import com.aimir.fep.protocol.mrp.exception.MRPException;
import com.aimir.fep.protocol.mrp.protocol.EDMI_Mk6N_DataConstants;
import com.aimir.fep.util.ByteArray;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.FrameUtil;
import com.aimir.fep.util.Hex;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * {@link MRPListeningClientProtocolHandler} implementation of FEP (AiMiR and Meter Protocol).
 * 
 * @author Yeon Kyoung Park
 * @version $Rev: 1 $, $Date: 2008-01-05 15:59:15 +0900 $,
 */
public class MRPListeningClientProtocolHandler extends IoHandlerAdapter
{
    private static Log log = LogFactory.getLog(MRPListeningClientProtocolHandler.class);

    private Object enqMonitor = new Object();
    private boolean isReceivedENQ = false;

    private Object ackMonitor = new Object();
    private ControlDataFrame ack = null;

    private Object resMonitor = new Object();
    private Hashtable response = new Hashtable();

    private Object msgMonitor = new Object();
    private Object msg = null;

    private int count = 0;

    private int idleTime = Integer.parseInt(FMPProperty.getProperty(
                "protocol.idle.time","5"));
    private int retry = Integer.parseInt(FMPProperty.getProperty(
                "protocol.retry","3"));
    private int responseTimeout = Integer.parseInt(
            FMPProperty.getProperty("protocol.response.timeout","15"));
    private int ackTimeout = Integer.parseInt(
            FMPProperty.getProperty("protocol.ack.timeout","3"));
    private int circuitIdleTime = Integer.parseInt(FMPProperty.getProperty(
    				"protocol.circuit.connect.request.idletime","60"));
    private FMPClientResource resource = null;
    private Object resourceObj = null;

    public  final static String isActiveKey = "isActive";
    private Boolean  active =  Boolean.TRUE;
    private Boolean  deActive = Boolean.FALSE;
    private boolean  isSessionOpened = false;
    private boolean  isCircuitOpened = false;
    private long openCircuitTime = 0L;
    private long closedCircuitTime = 0L;
    public final static String ODOA= new String(new byte[]{0x0D,0x0A});
    private long MAX_MCUID = 4294967295L;
    private static IoSession session = null;
    private GPRSTarget target=null;
    
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
    
    

    /**
	 * 
	 */
	public MRPListeningClientProtocolHandler() {
		log.debug("\r\n\r\n ###### MRPListeningClientProtocolHandler\r\n ");
	}



	// processing controldata frame
    private void receivedControlDataFrame(IoSession session,
            ControlDataFrame cdf) throws Exception
    { 
        log.debug("RECEIVED CONTROL DATA \n" 
            +"ControlDataFrame :" + cdf.toString()); 
        byte code = cdf.getCode(); 
        if(code == ControlDataConstants.CODE_ENQ) 
        { 
            synchronized(enqMonitor) 
            { 
                isReceivedENQ = true; 
                enqMonitor.notify(); 
            } 
        } 
        else if(code == ControlDataConstants.CODE_ACK) 
        { 
            synchronized(ackMonitor) 
            { 
                ack = cdf; 
                ackMonitor.notify(); 
            } 
            int sequence = FrameUtil.getAckSequence(ack); 
            if(cdf.getSvc() != GeneralDataConstants.SVC_C) 
            { 
                Integer is = 
                    (Integer)session.getAttribute("lastSequence"); 
            } 
        } 
        else if(code == ControlDataConstants.CODE_NAK) 
        { 
            int seqs[] = FrameUtil.getNakSequence(cdf); 
            ArrayList framelist = (ArrayList) 
                session.getAttribute("sendframes"); 
            for(int i = 0 ; i < seqs.length ; i++) 
            { 
                byte[] mbx = (byte[])framelist.get(seqs[i]); 
                IoBuffer buffer = IoBuffer.allocate(mbx.length); 
                buffer.put(mbx,0,mbx.length); 
                buffer.flip(); 
                session.write(buffer); 
                FrameUtil.waitSendFrameInterval(); 
            } 
            ControlDataFrame wck = 
                (ControlDataFrame)session.getAttribute("wck"); 
            if(wck != null) 
                session.write(wck); 
        } 
        else if(code == ControlDataConstants.CODE_EOT) 
        { 
            log.debug("Received EOT"); 
            closeCircuit(session);
            session.closeNow();
        }
        else if(code == ControlDataConstants.CODE_WCK) 
        {
            byte[] args = cdf.getArg().getValue(); 
            int startseq = DataUtil.getIntToByte(args[0]); 
            int endseq = DataUtil.getIntToByte(args[1]); 
            SlideWindow sw = (SlideWindow)session.getAttribute(""); 
            if(sw == null) 
            { 
                session.write(FrameUtil.getACK(
                            DataUtil.getByteToInt(endseq))); 
                return; 
            } 
            byte[] naks = sw.checkWindow(startseq,endseq); 
            if(naks.length < 1) 
            { 
                if(sw.isReceivedEndFrame()) 
                { 
                    try {
                        Object ns_obj = session.getAttribute("nameSpace");
                        String ns = ns_obj!=null? (String)ns_obj:null;
                        
                        GeneralDataFrame frame = GeneralDataFrame.decode(
                                ns,sw.getCompletedFrameBuffer()); 
                        receivedServiceDataFrame(session,
                                (ServiceDataFrame)frame);
                    }catch(Exception ex)
                    {
                        log.error(ex, ex);
                        // System.exit(-1);
                    }
                } 
                session.write(FrameUtil.getACK(args[1])); 
            } 
            else 
                session.write(FrameUtil.getNAK(naks)); 
        }
    }

    // processing service data frame
    private void receivedServiceDataFrame(IoSession session, ServiceDataFrame sdf) 
    throws Exception
    { 
        ServiceData sd = ServiceData.decode(sdf, session.getRemoteAddress().toString()); 
        if(sd != null) 
        { 
            log.info("ServiceData :" + sd.getType()+ " Count : " 
                    + count++); 
            if(count >= Integer.MAX_VALUE)
                count = 0;
            //log.debug("RECEIVED SERVICE DATA \n"
            //  +"ServiceData :" + sd.toString()); 
            if(sd instanceof CommandData) 
            { 
                int tid = ((CommandData)sd).getTid().getValue(); 
                //response = sd; 
                log.info("Recevice tid["+tid+"]");
                response.put(""+tid,sd); 
                synchronized(resMonitor) 
                { 
                    resMonitor.notify(); 
                }
            }
            
        } 
        else 
            log.info("ServiceData is null");
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
            if(message instanceof GeneralDataFrame)
            {
                GeneralDataFrame frame = (GeneralDataFrame)message;
                if(frame instanceof ServiceDataFrame) 
                    receivedServiceDataFrame(session,
                            (ServiceDataFrame)frame); 

            } else if(message instanceof ControlDataFrame) {
            	ControlDataFrame frame = (ControlDataFrame)message;
                receivedControlDataFrame(session,
                        (ControlDataFrame)frame);
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
            log.error("MRPClientProtocolHandler::messageReceived "
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
        session.setAttribute(isActiveKey,active);
        session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE,
                idleTime);
        isSessionOpened = true;
        this.session=session;
        log.debug("sessionOpened : " + session.getRemoteAddress());
    }

    /**
	 * @return the target
	 */
	public GPRSTarget getTarget() {
		return target;
	}

	/**
	 * @param target the target to set
	 */
	public void setTarget(GPRSTarget target) {
		this.target = target;
	}

	/**
     * inherited method from ProtocolHandlerAdapter
     */
    public void sessionClosed(IoSession session)
    {
        synchronized(enqMonitor) { enqMonitor.notify(); }
        synchronized(ackMonitor) { ackMonitor.notify(); }
        synchronized(resMonitor) { resMonitor.notify(); }
        synchronized(msgMonitor) { msgMonitor.notify(); }

        session.removeAttribute("sendframes");
        session.removeAttribute("lastSequence");
        session.removeAttribute("wck");

        try
        {
            if(resource != null && resourceObj != null)
            {
                log.debug("#### ProtocalHandler: Resource Release ###");
                resource.release(resourceObj);
                log.debug("ProtocalHandler: "+resource.getActiveResourcesString());
                log.debug("ProtoalHandler: "+resource.getIdleResourcesString());
            }
            //if(response != null && response.values().size() > 0)
            //    response.clear();
        }catch(Exception ex)
        {
            log.error(ex,ex);
        }

        
        isReceivedENQ = false; 
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
     * wait ACK ControlDataFrame
     */
    public void waitAck()
    {
        synchronized(ackMonitor)
        { 
            try { 
                //log.debug("ACK Wait");
                ackMonitor.wait(500); 
                //log.debug("ACK Received");
            } catch(InterruptedException ie) {ie.printStackTrace();}
        }
    }

    /**
     * get ACK ControlDataFrame
     *
     * @return ack <code>ControlDataFrame</code>
     */
    public ControlDataFrame getAck()
    {
        return this.ack;
    }

    /**
     * get ACK Sequence 
     *
     * @return sequence <code>int</code> ACK sequence number
     */
    public int getAckSequence()
    {
        if(this.ack == null)
            return -1;
        return FrameUtil.getAckSequence(this.ack);
    }

    /**
     * set ACK ControlDataFrame
     *
     * @param ack <code>ControlDataFrame</code> ack
     */
    public void setAck(ControlDataFrame ack)
    {
        this.ack = ack;
    }

    /**
     * wait util received ACK ControlDataFrame
     *
     * @param session <code>IoSession</code> session
     * @param sequence <code>int</code> wait ack sequence
     */
    public void waitAck(IoSession session, int sequence)
        throws Exception
    { 
        //log.debug("waitAck "+ sequence);
        int waitAckCnt = 0;
        while(session.isConnected())
        {
            if(ack == null)
            {
                waitAck();
                waitAckCnt++;
                if((waitAckCnt / 2) > ackTimeout)
                { 
                    throw new FMPACKTimeoutException(
                        "ACK Wait Timeout[" +ackTimeout +"]");
                }
            }
            else
            {
                int ackseq = FrameUtil.getAckSequence(this.ack);
                //log.debug("ackseq : " + ackseq);
                if(sequence == ackseq)
                {
                    setAck(null);
                    break;
                }
                else
                    setAck(null);
            }
        }
        //log.debug("finished waitAck "+ sequence);
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
     * @param tid <code>int</code> command request id
     * @throws FMPResponseTimeoutException 
     * @throws Exception
     */
    public ServiceData getResponse(IoSession session,int tid)
        throws MRPException, FMPResponseTimeoutException
    {
        String key = ""+tid;
        long stime = System.currentTimeMillis();
        long ctime = 0;
        int waitResponseCnt = 0;
        while(session.isConnected())
        { 
            if(response.containsKey(""+tid)) 
            { 
                Object obj = (ServiceData)response.get(key); 
                response.remove(key); 
                if(obj == null) 
                    continue; 
                return (ServiceData)obj; 
            } 
            else
            {
                waitResponse();
                ctime = System.currentTimeMillis();

                /** modified by D.J Park in 2006.04.10
                 * modify Measurement of Reponse time out  
                 * same monitor notified when other request 
                 * can be wait time less than response time
                waitResponseCnt++;
                if((waitResponseCnt / 2) > responseTimeout)
                {
                    throw new ResponseTimeoutException(" tid : "
                            + tid +" Response Timeout["
                            +responseTimeout +"]");
                }
                */
                if(((ctime - stime)/1000) > responseTimeout)
                {
                    log.debug("getResponse:: SESSION IDLE COUNT["
                            +session.getIdleCount(IdleStatus.BOTH_IDLE) 
                            +"]");
                    if(session.getIdleCount(IdleStatus.BOTH_IDLE) >= retry)
                    {
                        response.remove(key); 
                        throw new FMPResponseTimeoutException(" tid : " 
                                + tid +" Response Timeout[" 
                                +responseTimeout +"]");
                    }
                }
            }
        }
        return null;
    }

    public void setMRPResource(FMPClientResource resource,Object resourceObj)
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
            try { msgMonitor.wait(5000); 
            } catch(InterruptedException ie) {ie.printStackTrace();}
        }
    }

    /**
     * message를 getMsgCnt 만큼 받아오도록 한다
     * @param session
     * @param timeout
     * @param getMsgCnt
     * @return
     * @throws MRPException
     * @throws FMPACKTimeoutException
     */
    public String getMsgs(IoSession session, long timeout,int getMsgCnt){
    	StringBuffer msgBuff = new StringBuffer();
    	msgBuff.append("");
    	Object resMsg=null;
    	for(int i=0;i<getMsgCnt;i++) {
    		try {
    			resMsg=getMsg(session,circuitIdleTime);
    			if(resMsg!=null) {
    				msgBuff.append(new String((byte[])resMsg));
    			}
    		}catch (Exception e) {
    			log.error("getMsg["+getMsgCnt+"] is Error");
			}    								            			
		}
    	return msgBuff.toString();
    }
    
    /**
     * message를 getMsgCnt 만큼 받아오도록 한다
     * @param session
     * @param timeout
     * @param getMsgCnt
     * @return
     * @throws MRPException
     * @throws FMPACKTimeoutException
     */
    public byte[] getMsgsByte(IoSession session, long timeout,int getMsgCnt){    	
    	Object resMsg=null;
    	IoBuffer buf=null;
    	for(int i=0;i<getMsgCnt;i++) {
    		try {
    			resMsg=getMsg(session,circuitIdleTime);
    			if(resMsg!=null) {
    				buf.put((byte[])resMsg,0,((byte[])resMsg).length);    				
    			}
    		}catch (Exception e) {
    			log.error("getMsg["+getMsgCnt+"] is Error");
			}    								            			
		}    	    	
    	buf.flip();
    	return buf.array();
    }
    /**
     * wait util received command response data and return Response
     * 
     * @param session <code>IoSession</code> session
     * @param tid <code>int</code> command request id
     * @throws FMPACKTimeoutException 
     * @throws Exception
     */
    public Object getMsg(IoSession session, long timeout) 
        throws MRPException, FMPACKTimeoutException
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
    
    
    /**
     * wait util received command response data and return Response
     * 
     * @param session <code>IoSession</code> session
     * @param tid <code>int</code> command request id
     * @throws FMPACKTimeoutException 
     * @throws Exception
     */
    public Object getMessage(IoSession session, int off_len) 
        throws MRPException, FMPACKTimeoutException
    {
        this.msg = null;
        long stime = System.currentTimeMillis();
        long ctime = 0;
        long timeout = 30;
        byte[] totmsg = new byte[0];
        int totlen = 0;
        int res_len = -1;
        while(session.isConnected())
        { 
            waitMsg();
            //waitResponse();
            if(totmsg.length == res_len)
            {
                //log.debug("message all received");
                break;
            }
            else if(this.msg != null)
            {
                byte[] newMsg = ((byte[])this.msg);
                byte[] temp = null;
                if(totmsg != null){
                    totlen = totmsg.length;
                    temp = new byte[totlen];
                    System.arraycopy(totmsg, 0, temp, 0, temp.length);
                }
                 
                totmsg = new byte[totlen+newMsg.length];
                if(temp != null && temp.length > 0)
                {
                    System.arraycopy(temp, 0, totmsg, 0, temp.length);
                }
                System.arraycopy(newMsg, 0, totmsg, totmsg.length-newMsg.length, newMsg.length);
                res_len = (int)(totmsg[off_len]&0xFF)+5;
                timeout = res_len;
                this.msg = null;
            }

            log.debug("res_len=>"+res_len+" totmsg=>"+new OCTET(totmsg).toHexString());
                
            ctime = System.currentTimeMillis();
            if(((ctime - stime)/1000.0) > timeout)
            {
                log.debug(" waitTime["+((ctime - stime)/1000)+"] timeout["
                    + timeout+"]");
                throw new FMPACKTimeoutException(" Msg Receive Timeout["
                        +timeout +"]");
            }
        }
        return totmsg;
    }
    
    /**
     * wait util received command response data and return Response
     * 
     * @param session <code>IoSession</code> session
     * @param tid <code>int</code> command request id
     * @throws FMPACKTimeoutException 
     * @throws Exception
     */
    public Object getMessageFromEDMIMeter(IoSession session) 
    	throws MRPException, FMPACKTimeoutException
    {
        this.msg = null;
        long stime = System.currentTimeMillis();
        long ctime = 0;
        long timeout = 15;
        byte[] totmsg = new byte[0];
        int totlen = 0;
      //  int res_len = -1;
        while(session.isConnected())
        { 
            waitMsg();
            //waitResponse();
            if(this.msg != null)
            {
                byte[] newMsg = ((byte[])this.msg);
                byte[] temp = null;
                if(totmsg != null){
                    totlen = totmsg.length;
                    temp = new byte[totlen];
                    System.arraycopy(totmsg, 0, temp, 0, temp.length);
                }
                 
                totmsg = new byte[totlen+newMsg.length];
                if(temp != null && temp.length > 0)
                {
                    System.arraycopy(temp, 0, totmsg, 0, temp.length);
                }
                System.arraycopy(newMsg, 0, totmsg, totmsg.length-newMsg.length, newMsg.length);
                timeout = temp.length /2 + 15; 
                this.msg = null;
            }
            
            int nowlen = totmsg.length;
            if(nowlen>=2){
            	if(totmsg[nowlen-1]==EDMI_Mk6N_DataConstants.ETX){
            		log.debug("res_len=>"+nowlen+" totmsg=>"+new OCTET(totmsg).toHexString());
            		break;
            	}
            }
            log.debug("res_len=>"+nowlen+" totmsg=>"+new OCTET(totmsg).toHexString());
                
            ctime = System.currentTimeMillis();
            if(((ctime - stime)/1000.0) > timeout)
            {
                log.debug(" waitTime["+((ctime - stime)/1000)+"] timeout["
                    + timeout+"]");
                throw new FMPACKTimeoutException(" Msg Receive Timeout["
                        +timeout +"]");
            }
        }
        return totmsg;
    }
    
    /**
     * wait util received command response data and return Response
     * 
     * @param session <code>IoSession</code> session
     * @param tid <code>int</code> command request id
     * @throws FMPACKTimeoutException 
     * @throws Exception
     */
    public Object getMessageToStopByte(IoSession session, byte stopByte) 
    	throws MRPException, FMPACKTimeoutException
    {
        this.msg = null;
        long stime = System.currentTimeMillis();
        long ctime = 0;
        long timeout = 15;
        byte[] totmsg = new byte[0];
        int totlen = 0;
      //  int res_len = -1;
        while(session.isConnected())
        { 
            waitMsg();
            //waitResponse();
            if(this.msg != null)
            {
                byte[] newMsg = ((byte[])this.msg);
                byte[] temp = null;
                if(totmsg != null){
                    totlen = totmsg.length;
                    temp = new byte[totlen];
                    System.arraycopy(totmsg, 0, temp, 0, temp.length);
                }
                 
                totmsg = new byte[totlen+newMsg.length];
                if(temp != null && temp.length > 0)
                {
                    System.arraycopy(temp, 0, totmsg, 0, temp.length);
                }
                System.arraycopy(newMsg, 0, totmsg, totmsg.length-newMsg.length, newMsg.length);
                timeout = temp.length /2 + 15; 
                this.msg = null;
            }
            
            int nowlen = totmsg.length;
            if(nowlen>=2){
            	if(totmsg[nowlen-1] == stopByte){
            		log.debug("res_len=>"+nowlen+" totmsg=>"+new OCTET(totmsg).toHexString());
            		break;
            	}
            }
            log.debug("res_len=>"+nowlen+" totmsg=>"+new OCTET(totmsg).toHexString());
                
            ctime = System.currentTimeMillis();
            if(((ctime - stime)/1000.0) > timeout)
            {
                log.debug(" waitTime["+((ctime - stime)/1000)+"] timeout["
                    + timeout+"]");
                throw new FMPACKTimeoutException(" Msg Receive Timeout["
                        +timeout +"]");
            }
        }
        return totmsg;
    }
    
    public Object getMessageA2RL(IoSession session, int off_len) 
    	throws MRPException, FMPACKTimeoutException
	{
	    this.msg = null;
	    long stime = System.currentTimeMillis();
	    long ctime = 0;
	    long timeout = 20;
	    byte[] totmsg = new byte[0];
	    int totlen = 0;
	    int res_len = -1;
	    while(session.isConnected())
	    {
	        waitMsg();
	        //waitResponse();
	        if(res_len!=-1 && totmsg.length >= res_len)
	        {
	            //log.debug("message all received");
	            break;
	        }
	        else if(this.msg != null)
	        {
	            byte[] newMsg = ((byte[])this.msg);
	            byte[] temp = null;
	            if(totmsg != null){
	                totlen = totmsg.length;
	                temp = new byte[totlen];
	                System.arraycopy(totmsg, 0, temp, 0, temp.length);
	            }
	             
	            totmsg = new byte[totlen+newMsg.length];
	            if(temp != null && temp.length > 0 )
	            {
	                System.arraycopy(temp, 0, totmsg, 0, temp.length);
	            }
	            System.arraycopy(newMsg, 0, totmsg, totmsg.length-newMsg.length, newMsg.length);
	            
            	if(totmsg.length>5){
            		if(totmsg[2]!= (byte)0x00)
    	            	break;
            		
            		res_len = (int)(totmsg[off_len]&0x7f)+7;
            		timeout += (10+ res_len/2);
            	}
				
	            this.msg = null;
	        }
	
	        log.debug("res_len=>"+res_len+" totmsg=>"+new OCTET(totmsg).toHexString());
	            
	        ctime = System.currentTimeMillis();
	        if(((ctime - stime)/1000.0) > timeout)
	        {
	            log.debug(" waitTime["+((ctime - stime)/1000)+"] timeout["
	                + timeout+"]");
	        //    return null;
	            
	            throw new FMPACKTimeoutException(" Msg Receive Timeout["
	                    +timeout +"]");
	        }
	    }
	    return totmsg;
	}
    
    /**
     * wait util received command response data and return Response
     * 
     * @param session <code>IoSession</code> session
     * @param tid <code>int</code> command request id
     * @throws FMPACKTimeoutException 
     * @throws Exception
     */
    public Object getMessage(IoSession session, int off_len, int model) 
        throws MRPException, FMPACKTimeoutException
    {
        this.msg = null;
        long stime = System.currentTimeMillis();
        long ctime = 0;
        long timeout = 20;
        byte[] totmsg = new byte[0];
        int totlen = 0;
        int res_len = off_len;
        while(session.isConnected())
        { 
            waitMsg();
            //waitResponse();
            if(totmsg.length >= res_len)
            {
                //log.debug("message all received");
                break;
            }
            else if(this.msg != null)
            {
                byte[] newMsg = ((byte[])this.msg);
                byte[] temp = null;
                if(totmsg != null){
                    totlen = totmsg.length;
                    temp = new byte[totlen];
                    System.arraycopy(totmsg, 0, temp, 0, temp.length);
                }
                 
                totmsg = new byte[totlen+newMsg.length];
                if(temp != null && temp.length > 0)
                {
                    System.arraycopy(temp, 0, totmsg, 0, temp.length);
                }
                System.arraycopy(newMsg, 0, totmsg, totmsg.length-newMsg.length, newMsg.length);
                if(model==MeterModel.LSIS_LK1210DRB_120.getCode())
                {
                	if(totmsg.length>3){
                		if(totmsg[0]!=(byte)0x7E)
                			return null;
	                	res_len = ((((totmsg[1] & (byte)0x07) & 0xff) << 8) | (totmsg[2] & 0xff)) + 2;
	                	if (res_len>255) return null;
	                	timeout +=5;
	                }
                }
                else if(model == MeterModel.LSIS_LGRW3410.getCode())
                {
                	if(totmsg[0]==(byte)0x15){
                		res_len=1;
                		timeout = res_len;
                	}
                	else if(totmsg.length>7){
                		if(totmsg[0]!=(byte)0x06)
                			return null;	
                		res_len = (((totmsg[5] & 0xff) << 8) | (totmsg[6] & 0xff)) + 9;
                		timeout += 5;
                	}
                }
                else if(model == MeterModel.GE_KV2C.getCode())
                {

                    int len_crc = 2;
                    if(totmsg[0]==(byte)0xFF || totmsg[0]==(byte)0xFE){
                        off_len +=1;
                        if(totmsg[1]!=(byte)0x06)
                            return null;    
                        res_len = (((totmsg[off_len] & 0xff) << 8) | (totmsg[off_len+1] & 0xff)) + len_crc;
                        timeout = res_len;
                    }
                    else if(totmsg.length>off_len){
                        if(totmsg[0]!=(byte)0x06)
                            return null;    
                        res_len = (((totmsg[off_len] & 0xff) << 8) | (totmsg[off_len+1] & 0xff)) + len_crc;
                        timeout = res_len;
                    }
                    log.debug("offset="+off_len+",res_len="+res_len);
                }
                else
                {
                	if(totmsg.length>1){
	                	res_len = (int)(totmsg[off_len]&0xFF)+6;
	                	timeout += 5;
                	}
                }
                
                this.msg = null;
            }

            log.debug("res_len=>"+res_len+" totmsg=>"+new OCTET(totmsg).toHexString());
                
            ctime = System.currentTimeMillis();
            if(((ctime - stime)/1000.0) > timeout)
            {
                log.debug(" waitTime["+((ctime - stime)/1000)+"] timeout["
                    + timeout+"]");
            //    return null;
                
                throw new FMPACKTimeoutException(" Msg Receive Timeout["
                        +timeout +"]");
            }
        }
        return totmsg;
    }
    

    /**
     * wait util received command response data and return Response
     * 
     * @param session <code>IoSession</code> session
     * @param tid <code>int</code> command request id
     * @throws FMPACKTimeoutException 
     * @throws Exception
     */
    public Object getMessageLSIS(IoSession session, int off_len, int model) 
        throws MRPException, FMPACKTimeoutException
    {
        this.msg = null;
        long stime = System.currentTimeMillis();
        long ctime = 0;
        long timeout = 20;
        byte[] totmsg = new byte[0];
        int totlen = 0;
        int res_len = -1;
        while(session.isConnected())
        { 
            waitMsg();
            //waitResponse();
            if(totmsg.length == res_len)
            {
                //log.debug("message all received");
                break;
            }
            else if(this.msg != null)
            {
                byte[] newMsg = ((byte[])this.msg);
                byte[] temp = null;
                if(totmsg != null){
                    totlen = totmsg.length;
                    temp = new byte[totlen];
                    System.arraycopy(totmsg, 0, temp, 0, temp.length);
                }
                 
                totmsg = new byte[totlen+newMsg.length];
                if(temp != null && temp.length > 0)
                {
                    System.arraycopy(temp, 0, totmsg, 0, temp.length);
                }
                System.arraycopy(newMsg, 0, totmsg, totmsg.length-newMsg.length, newMsg.length);
                if(model==MeterModel.LSIS_LK1210DRB_120.getCode())
                {
                	if(totmsg.length>3){
                		if(totmsg[0]!=(byte)0x7E) return null;
	                	res_len = ((((totmsg[1] & (byte)0x07) & 0xff) << 8) | (totmsg[2] & 0xff)) + 2;
	                	
	                	if (res_len>255) return null;
	                	timeout = res_len;
	                }
                }
                else if(model == MeterModel.LSIS_LGRW3410.getCode())
                {
                	if(totmsg[0]==(byte)0x15){
                		res_len=1;
                	}
                	else if(totmsg.length>7){
                		if(totmsg[0]!=(byte)0x06)
                			res_len = totmsg.length;	
                		if(totmsg[1]!=(byte)0xEE)
                			res_len = totmsg.length;
                		res_len = (((totmsg[5] & 0xff) << 8) | (totmsg[6] & 0xff)) + 9;
                	}else{
                		timeout = 5;
                	}
                }
                else
                {
                	if(totmsg.length>1){
	                	res_len = (int)(totmsg[off_len]&0xFF)+6;
	                	timeout = res_len;
                	}
                }
                
                this.msg = null;
            }

            log.debug("res_len=>"+res_len+" totmsg=>"+new OCTET(totmsg).toHexString());
                
            ctime = System.currentTimeMillis();
            if(((ctime - stime)/1000.0) > timeout)
            {
                log.debug(" waitTime["+((ctime - stime)/1000)+"] timeout["
                    + timeout+"]");
            //    return null;
                
                throw new FMPACKTimeoutException(" Msg Receive Timeout["
                        +timeout +"]");
            }
        }
        return totmsg;
    }
    
    /**
     * wait util received command response data and return Response
     * 
     * @param session <code>IoSession</code> session
     * @param tid <code>int</code> command request id
     * @throws FMPACKTimeoutException 
     * @throws Exception
     */
    public Object getMsg(IoSession session, long timeout, int res_len) 
        throws MRPException, FMPACKTimeoutException
    {
        this.msg = null;
        long stime = System.currentTimeMillis();
        long ctime = 0;
        byte[] totmsg = new byte[0];
        int totlen = 0;
        ByteArray ba = new ByteArray();
        log.debug("message read start");
        
        while(session.isConnected())
        { 
            waitMsg();
            //waitResponse();
            if(totmsg.length >= res_len)
            {
                log.debug("message all received");
                break;
            }
            
            if(this.msg != null)
            {
                byte[] newMsg = ((byte[])this.msg);
                ba.append(newMsg);
                totmsg = ba.toByteArray();               
            }
            //log.debug("totmsg=>"+new OCTET(totmsg).toHexString());
                
            ctime = System.currentTimeMillis();
            if(((ctime - stime)/1000.0) > timeout)
            {
                log.debug(" waitTime["+((ctime - stime)/1000)+"] timeout["
                    + timeout+"]");
                throw new FMPACKTimeoutException(" Msg Receive Timeout["
                        +timeout +"]");
            }
        }
        log.debug("final totmsg=>"+new OCTET(totmsg).toHexString());
        return totmsg;
    }

    /**
     * wait util received command response data and return Response
     * 
     * @param session <code>ProtocolSession</code> session
     * @param tid <code>int</code> command request id
     * @throws FMPACKTimeoutException 
     * @throws Exception
     */
    public Object getMsg(IoSession session, long timeout, int res_len, boolean a) 
        throws MRPException, FMPACKTimeoutException
    {
        this.msg = null;
        long stime = System.currentTimeMillis();
        long ctime = 0;
        byte[] totmsg = new byte[0];
        int totlen = 0;
        while(session.isConnected())
        { 
            waitMsg();
            //waitResponse();
            if(totmsg.length == res_len)
            {
                log.debug("message all received");
                break;
            }
            else if(this.msg != null)
            {
                byte[] newMsg = ((byte[])this.msg);
                byte[] temp = null;
                if(totmsg != null){
                    totlen = totmsg.length;
                    temp = new byte[totlen];
                    System.arraycopy(totmsg, 0, temp, 0, temp.length);
                }
                 
                totmsg = new byte[totlen+newMsg.length];
                if(temp != null && temp.length > 0)
                {
                    System.arraycopy(temp, 0, totmsg, 0, temp.length);
                }
                System.arraycopy(newMsg, 0, totmsg, totmsg.length-newMsg.length, newMsg.length);
                
            }
            log.debug("totmsg=>"+new OCTET(totmsg).toHexString());
                
            ctime = System.currentTimeMillis();
            if(((ctime - stime)/1000.0) > timeout)
            {
                log.debug(" waitTime["+((ctime - stime)/1000)+"] timeout["
                    + timeout+"]");
                return null;
            }
        }
        log.debug("final totmsg=>"+new OCTET(totmsg).toHexString());
        return totmsg;
    }
    
    private final char CR = '\r';
    private final char LF = '\n';
    private final String CRLF = "\r\n";
    private final char CTRLZ = 0x1A;
    private final char DQUATA = 0x22;
    
    
    public byte[] setIEIU(IoSession session, CircuitTarget target, CommandIEIU command)
    throws MRPException, FMPACKTimeoutException
    {
    	byte[] message = null;

    	session.write(command.makeCommand());
    	message = (byte[])getMsg(session,20);
    	
    	if(new String(message).indexOf("ACK") >= 0){
    		message = new byte[]{0x01};
    	}else{
    		message = new byte[]{0x00};
    	}
    	return message;
    }
    
    public byte[] getDataFromIEIU(IoSession session, CircuitTarget target, CommandIEIU command)
    throws MRPException, FMPACKTimeoutException
    {
    	byte[] message = null;

    	session.write(command.makeCommand());
    	message = (byte[])getMsg(session,20);
    	
    	return message;
    }
    
    /**
     * connect SMS
     * 
     * @param session <code>IoSession</code> session
     * @param target <code>CircuitTarget</code> Circuit Connect Information
     * @throws FMPACKTimeoutException 
     * @throws Exception
     */
    private void connectSMS(IoSession session, CircuitTarget target)
        throws MRPException, FMPACKTimeoutException
    {
        String ATZ = 
            FMPProperty.getProperty("protocol.circuit.command.ATZ","ATZ");

        String message = null;
        boolean atzOk = false;
        for(int i = 0 ; i < retry && !atzOk; i++)
        {
            session.write(FrameUtil.getByteBuffer(ATZ+CRLF));
            try 
            {  
                message = new String((byte[])getMsg(session,10)).trim();
                log.debug("initCircuit Read MSG["+message+"]");
                if(message != null) 
                {
                    if(message.indexOf("OK") >= 0)
                    {
                        atzOk = true;
                    }
                    else if(i == 0)
                    {
                        message = new String((byte[])getMsg(session,3));
                        log.debug("initCircuit Read MSG["+message+"]");
                    }
                } 
            }catch(Exception ex) {}
        }
        if(!atzOk)
            throw new MRPException(MRPError.ERR_INITLOCALMODEM_CLASS,
                                   "Init Modem(AT Command) Failed");


        String AT = "ATE0";
        //if(target instanceof SMSTarget)
            AT = FMPProperty.getProperty("protocol.circuit.command.AT.GSM");

        log.debug("initCircuit AT["+AT+"]");
        if(AT != null && AT.length() > 1)
        {
            session.write(FrameUtil.getByteBuffer(AT+CR+LF));
            message = new String((byte[])getMsg(session,3)).trim();
            log.debug("initCircuit Read MSG["+message+"]");
            if(message == null || message.indexOf("OK") < 0)
            { 
                throw new MRPException(MRPError.ERR_INITLOCALMODEM_CLASS,
                                       "Init Modem(AT Command) Failed");
            }
        } 
        else
        {
            log.debug("initCircuit AT Command Skip");
        }
        
        

    }
    
    public void deleteSMS(IoSession session)  
	throws MRPException, FMPACKTimeoutException
    {
    	try{
        	String message = null;
        	//session.write(FrameUtil.getByteBuffer("AT+CMGL="+DQUATA+"ALL"+DQUATA+"\r\n"));
        	//message = new String((byte[])getMsg(session,35));
            //log.debug("AT+CMGL Read MSG["+message+"]");
            
            /*
        	StringTokenizer st = new StringTokenizer(message,"+CMGL: ");
        	
        	while(st.hasMoreTokens()){
        		String listMessage = (String)st.nextToken();
        		int sInx = listMessage.indexOf(",");
        		int index = Integer.parseInt(listMessage.substring(0,sInx));
            	session.write(FrameUtil.getByteBuffer("AT+CMGD="+index+"\r\n"));
            	message = new String((byte[])getMsg(session,10));
        	}
        	*/
        	
        	//int fInx = message.lastIndexOf("+CMGL: ");
        	try{
        		/*
        		if(fInx >= 0){
        			int sInx = message.indexOf(",",fInx+7);
            		int index = Integer.parseInt(message.substring(fInx+7,sInx));
            		log.debug("AT+CMGD="+index);
            		if(index >= 0){
            			for(int i = index; i > 0; i--){
                        	session.write(FrameUtil.getByteBuffer("AT+CMGD="+index+"\r\n"));
                        	message = new String((byte[])getMsg(session,10));
                        	log.debug("["+message+"]");
            			}
            		}
        		}
        		*/
        		int index = 40;
    			for(int i = index; i > 0; i--){
                	session.write(FrameUtil.getByteBuffer("AT+CMGD="+i+"\r\n"));
                	message = new String((byte[])getMsg(session,10));
                	//log.debug("["+message+"]");
    			}
        	}catch(Exception e){
        		log.error(e,e);
        	}
    	}catch(Exception e){
    		log.warn(e,e);
    	}
    }
    
    public boolean receiveSMS(IoSession session) 
    	throws MRPException, FMPACKTimeoutException
    {
    	String message = null;
    	
 	
    	message = new String((byte[])getMsg(session,10));
    	
    	if(message.startsWith("+CMTI:")){
    		return true;
    	}
    	return false;
    }
    
    public void sendSMS(IoSession session,String mobileId, String sendMessage) 
    	throws MRPException, FMPACKTimeoutException
    {
    	String message = null;
    	
    	session.write(FrameUtil.getByteBuffer("AT+CMGF=1\r\n"));//set text mode
    	message = new String((byte[])getMsg(session,5));
    	session.write(FrameUtil.getByteBuffer("AT+CSDH=1\r\n"));//set text mode
    	message = new String((byte[])getMsg(session,5));
    	session.write(FrameUtil.getByteBuffer("AT+CSMP=49,167,0,0\r\n"));//set text mode
    	message = new String((byte[])getMsg(session,5));
    	
    	if(message.indexOf("OK") >= 0)
    	{
    		session.write(FrameUtil.getByteBuffer("AT+CMGS="+mobileId+"\r\n"));
        	message = new String((byte[])getMsg(session,5));
        	if(message.indexOf(">") >= 0){
            	session.write(FrameUtil.getByteBuffer(sendMessage));
            	session.write(FrameUtil.getByteBuffer(CTRLZ+"\r\n"));
            	message = new String((byte[])getMsg(session,10));
            	log.debug("AT+CMGS RESULT="+message);
        	}
    	}
    	
    }
    
    public ArrayList readSMS(IoSession session, String modemId, String header, int cnt) 
	throws MRPException, FMPACKTimeoutException
    {
    	ArrayList list = new ArrayList();
    	String message = null;
    	
    	if(cnt == 0){// One time, the settings will be initialized.
        	session.write(FrameUtil.getByteBuffer("AT+CNMI=2,1,0,2,1\r\n"));//set text mode
        	message = new String((byte[])getMsg(session,5));
        	session.write(FrameUtil.getByteBuffer("AT^SMGO=1\r\n"));//set text mode
        	message = new String((byte[])getMsg(session,5));
        	session.write(FrameUtil.getByteBuffer("AT+CSMP=49,167,0,0\r\n"));//set text mode
        	message = new String((byte[])getMsg(session,5));
        	session.write(FrameUtil.getByteBuffer("AT^SSCONF=1\r\n"));//set text mode
        	message = new String((byte[])getMsg(session,5));
    	}
   	
    	//session.write(FrameUtil.getByteBuffer("AT+CMGL="+DQUATA+"REC UNREAD"+DQUATA+"\r\n"));
    	session.write(FrameUtil.getByteBuffer("AT+CMGL="+DQUATA+"ALL"+DQUATA+"\r\n"));
    	
    	message = new String((byte[])getMsg(session,40,1024));
    	
    	log.debug("AT+CMGL READ=>"+message);
    	message = message.replace(ODOA,"");
    	
    	/*
    	StringTokenizer st = new StringTokenizer(message,"+CMGL: ");
    	try{
        	while(st.hasMoreTokens()){
        		String listMessage = (String)st.nextToken();
            	log.debug("AT+CMGL UNREAD TOKEN=>"+listMessage);
        		int sInx = listMessage.indexOf(",");
        		int index = Integer.parseInt(listMessage.substring(0,sInx));
        		session.write(FrameUtil.getByteBuffer("AT+CMGR="+index+"\r\n"));
        		
        		message = new String((byte[])getMsg(session,10));
        		list.add(message);
        	}
    	}catch(Exception e){
    		log.error(e,e);
    		list = null;
    	}*/
   	 
    	int fInx = message.lastIndexOf(header);
    	try{
    		if(fInx >= 0){
    			//int sInx = message.indexOf(",",fInx+7);
        		message = message.substring(fInx,message.length()).trim();
        		log.debug("AT+CMGL="+"MESSAGE="+message);
        		list.add(message);
    		}
    	}catch(Exception e){
    		log.error(e,e);
    		list = null;
    	}

    	return list;
	
    }
    
    public ArrayList readSMS(IoSession session) 
    	throws MRPException, FMPACKTimeoutException
    {
    	ArrayList list = new ArrayList();
    	String message = null;
    	
    	session.write(FrameUtil.getByteBuffer("AT+CNMI=2,1,0,2,1\r\n"));//set text mode
    	message = new String((byte[])getMsg(session,5));
    	session.write(FrameUtil.getByteBuffer("AT^SMGO=1\r\n"));//set text mode
    	message = new String((byte[])getMsg(session,5));
    	session.write(FrameUtil.getByteBuffer("AT+CSMP=49,167,0,0\r\n"));//set text mode
    	message = new String((byte[])getMsg(session,5));
    	session.write(FrameUtil.getByteBuffer("AT^SSCONF=1\r\n"));//set text mode
    	message = new String((byte[])getMsg(session,5));
    	
    	//session.write(FrameUtil.getByteBuffer("AT+CMGL="+DQUATA+"REC UNREAD"+DQUATA+"\r\n"));
    	session.write(FrameUtil.getByteBuffer("AT+CMGL="+DQUATA+"ALL"+DQUATA+"\r\n"));
    	
    	message = new String((byte[])getMsg(session,40,1024));
    	
    	log.debug("AT+CMGL UNREAD=>"+message);
    	message = message.replace(ODOA,"");
    	
    	/*
    	StringTokenizer st = new StringTokenizer(message,"+CMGL: ");
    	try{
        	while(st.hasMoreTokens()){
        		String listMessage = (String)st.nextToken();
            	log.debug("AT+CMGL UNREAD TOKEN=>"+listMessage);
        		int sInx = listMessage.indexOf(",");
        		int index = Integer.parseInt(listMessage.substring(0,sInx));
        		session.write(FrameUtil.getByteBuffer("AT+CMGR="+index+"\r\n"));
        		
        		message = new String((byte[])getMsg(session,10));
        		list.add(message);
        	}
    	}catch(Exception e){
    		log.error(e,e);
    		list = null;
    	}*/
   	 
    	int fInx = message.lastIndexOf("+CMGL: ");
    	try{
    		if(fInx >= 0){
    			int sInx = message.indexOf(",",fInx+7);
        		int index = Integer.parseInt(message.substring(fInx+7,sInx));
        		
        		if(index >= 0){
            		session.write(FrameUtil.getByteBuffer("AT+CMGR="+index+"\r\n"));            		
            		message = new String((byte[])getMsg(session,10));
            		log.debug("AT+CMGR="+index+"MESSAGE="+message);
            		list.add(message);
        		}
    		}
    	}catch(Exception e){
    		log.error(e,e);
    		list = null;
    	}

    	return list;
	
    }
    
    /**
     * Circuit Call
     * 
     * @param session <code>IoSession</code> session
     * @param target <code>CircuitTarget</code> Circuit Connect Information
     * @throws FMPACKTimeoutException 
     * @throws Exception
     */
    private void connectCircuit(IoSession session,GPRSTarget target, boolean meterConnect)
        throws MRPException, FMPACKTimeoutException
    {
        String ATZ = 
            FMPProperty.getProperty("protocol.circuit.command.ATZ","ATZ");
        McuType mcuType = target.getTargetType();
        String fwVer = target.getFwVer();
        
        if(fwVer.equals("NC5K2") || fwVer.equals("NC5K3")) {
        	meterConnect = false;
        }

        String message = null;
        boolean atzOk = false;
        int okcnt = 0;
        
		
        log.info("Target Type["+mcuType+"] Attemp User pass");
        
        if(mcuType == McuType.MMIU){        	
        	userPass(session, target.getTargetId(),0,3);
        }else {
        	userAuthPass(session, mcuType);
        }        
        
        if(mcuType == McuType.IEIU){
        	log.debug("try to connect meter1");
        	String groupNumber = target.getGroupNumber();
            String memberNumber = target.getMemberNumber();

            log.debug("groupNumber :"+ groupNumber);
            log.debug("memberNumber :"+ memberNumber);
            log.debug("try to connect meter2");
            if(meterConnect){
            	connectMeter(session, groupNumber, memberNumber);
            }
        }else{
        	if(meterConnect){
        		connectMeter(session);
        	}
        }
    }

    public void userPass(IoSession session, String mobileId, McuType mcuType) 
        throws MRPException {

        try {

            String session_id = FMPProperty.getProperty("GSM.SESSIONKEY");
            byte[] sessionkey = new byte[16];
            if(!session_id.equals("N/A Code!!")){
                userPass(session,FMPProperty.getProperty("GSM.SESSIONKEY").getBytes(),mobileId, mcuType);
            }else{
                userPass(session,sessionkey,mobileId, mcuType);   
            }

        } catch(Exception e) {
            throw new MRPException(MRPError.ERR_USERPASS_CLASS,"Modem authentication failed.");
        }
    }
    
    /**
     * User Authentication
     * Get from user input  
     * @param sessionkey
     * @throws FEPException
     */
    public void userPass(IoSession session, byte[] sessionkey, String mobileId, McuType mcuType) 
                throws MRPException {

        try {
             String message = null;
             Command command =null;
             if(mcuType == McuType.MMIU)
            	 command =  new AuthPassCommand(AuthPassCommand.CMD_AUTH_PASS,sessionkey,mobileId); 
             else if(mcuType == McuType.IEIU) 
            	 command= new AuthPassCommandIEIU(AuthPassCommandIEIU.CMD_AUTH_PASS,sessionkey,mobileId); 
             IoBuffer buf = null;

             buf = IoBuffer.allocate(command.makeCommand().length);
             buf.put(command.makeCommand());
             buf.flip();

             session.write(buf);
             
           //  message = new String((byte[])getMsg(session,10, 160));
             message = new String((byte[])getMsg(session,10));
             log.debug("userPass MSG["+message+"]");
             
             if(message != null && message.toLowerCase().indexOf("ack") < 0) {
                 log.debug("userPass Command Failed=>"+message);
                 throw new MRPException(MRPError.ERR_USERPASS_CLASS,"Modem authentication failed.");
             }
             
        } catch (Exception e) {
            userAuthPass(session, mcuType);//TEST USER PASS because ocurr many fails for 16 byte session key.
        }
    }


    /**
     * User Authentication
     * No Session Key
     * @throws FEPException
     */
    public void userAuthPass(IoSession session, McuType mcuType) 
                throws MRPException {

        try {
            String message = null;
            Command command = null;
            if(mcuType == McuType.MMIU)
            	command = new AuthPassCommand(AuthPassCommand.CMD_USERAUTH_PASS); 
            else if(mcuType == McuType.IEIU)
            	command = new AuthPassCommandIEIU(AuthPassCommandIEIU.CMD_USERAUTH_PASS); 
            IoBuffer buf = null;
            buf = IoBuffer.allocate(command.makeSingleCommand().length);
            buf.put(command.makeSingleCommand());
            buf.flip();
            session.write(buf);  
            
            if(mcuType == McuType.MMIU)
            	message = new String((byte[])getMsg(session,10));
            else if(mcuType == McuType.IEIU)
            	//message = new String((byte[])getMsg(session,10,160));
            	message = new String((byte[])getMsg(session,10));
            log.debug("userAuthPass MSG["+message+"]");
            
            if(message != null && message.toLowerCase().indexOf("ack") < 0) {
                log.debug("userPass Command Failed=>"+message);
                throw new MRPException(MRPError.ERR_USERPASS_CLASS,"Modem authentication failed.");
            }
    
        } catch (Exception e) {
            userAuthPass2(session, mcuType);
        }
    }
    
    /**
     * @param session
     * @param mcuid
     * @param curTry 현재 재시도 횟수
     * @param tryCnt 재시도 횟수
     */
    public void userPass(IoSession session, String mcuid, int curTry, int tryCnt) 
    {

    	try {
    		curTry++;
    		Object ns_obj = session.getAttribute("nameSpace");
    		String ns = ns_obj!=null? (String)ns_obj:null;
    		log.debug("userPass[" + curTry+"/"+tryCnt + "] nameSpace[" + ns + "]");
    		
	        for(int i=0;i<3;i++) {
		        CommandData command = new CommandData();
		        command.setCmd(new OID("130.14.0")); 
		        
		        ServiceDataFrame frame = new ServiceDataFrame();		        
	//	        frame.setSvc(GeneralDataConstants.SVC_C);
		        frame.setSvc((byte)'C');
		        long mcuId = Long.parseLong(mcuid);
		        frame.setMcuId(new UINT(mcuId));		        
		        command.setAttr(ServiceDataConstants.C_ATTR_REQUEST);
		        command.setTid(FrameUtil.getCommandTid());
		        frame.setSvcBody(command.encode());
		        log.debug("Send Auth!");
		        send(session, frame);
		        		        		        
		        byte[] message = getMsgsByte(session, 60, 3);
		        log.debug("auth response:"+Hex.decode(message));
		        
		        SMIValue[] smiValue = getResult(ns,message);
		        String result = new String(((OCTET)smiValue[0].getVariable()).getValue());
		        
		        if(!result.equals("0"))
		        {
		        	log.debug("userPass Command Failed=>"+result);
		            //throw new MRPException(MRPError.ERR_USERPASS_CLASS, "can't get auth : "+result);
		        }
	        }	        
	    } catch(Exception e) {	    	
	    	if(curTry<tryCnt) {	    		
	    		userPass(session, mcuid, curTry, tryCnt); 
	    	}
	    }
	}
    
    public SMIValue[] getResult(String ns, byte[] message ) throws Exception {
    	
    	GeneralDataFrame respondframe = null;
    	SMIValue[] smiValues =null;
    	IoBuffer buf = IoBuffer.allocate(message.length);
    	buf.put(message,0,message.length);
    	buf.flip();    	
    	try {
    	//	if(message.length>0 && message[0] == )
    		respondframe = GeneralDataFrame.decode(ns, buf); 
    	}catch(Exception ex)
        {
    		throw new Exception("CommandData fail");
        }
    	
    	CommandData cd = (CommandData)ServiceData.decode((ServiceDataFrame)respondframe, "");
    	if (cd != null) {
            if (cd.getErrCode().getValue() != 0)
            {
                log.debug("ErrorCode="+cd.getErrCode().getValue());
                throw new Exception("IF4 Error Code : "+cd.getErrCode().getValue());
            }
            else
            {
                log.debug("No Error");
                smiValues = ((CommandData)cd).getSMIValue();
            }
        }else{
        	throw new Exception("make CommandData fail");
        }
    	
    	return smiValues;
    }

    /**
     * send GeneralDataFrame to Target
     *
     * @param frame <code>GeneralDataFrame</code>
     */
    private synchronized int send(IoSession session, GeneralDataFrame frame) 
        throws Exception
    { 
        int sendBytes = 0;
        byte[] bx = frame.encode(); 
        byte[] mbx = null;
        IoBuffer buf = null;
        
        ArrayList<?> framelist = FrameUtil.makeMultiEncodedFrame(bx, session);
        session.setAttribute("sendframes",framelist); 
        int lastIdx = framelist.size() - 1;
        mbx = (byte[])framelist.get(lastIdx);
        int lastSequence = DataUtil.getIntToByte(mbx[1]);
        boolean isSetLastSequence = false;
        if((lastIdx / GeneralDataConstants.FRAME_MAX_SEQ) < 1)
        {
            session.setAttribute("lastSequence", 
                    new Integer(lastSequence)); 
            isSetLastSequence = true;
        }
        Iterator iter = framelist.iterator(); 
        int cnt = 0; 
        int seq = 0;
        ControlDataFrame wck = null;
        while(iter.hasNext()) 
        { 
            if(!isSetLastSequence && 
                    ((lastIdx - cnt) / 
                     GeneralDataConstants.FRAME_MAX_SEQ) < 1)
            {
                session.setAttribute("lastSequence", 
                        new Integer(lastSequence));
                isSetLastSequence = true;
            }
            mbx = (byte[])iter.next();
            sendBytes+=mbx.length;
            seq = DataUtil.getIntToByte(mbx[1]);
            buf = IoBuffer.allocate(mbx.length);
            buf.put(mbx,0,mbx.length);
            buf.flip();
            session.write(buf); 
            log.debug("send auth: "+buf.getHexDump());
            //FrameUtil.waitSendFrameInterval();
            if(((cnt+1) % GeneralDataConstants.FRAME_WINSIZE)== 0) 
            {
                log.debug("WCK : start : " + (seq - 
                    GeneralDataConstants.FRAME_WINSIZE + 1)
                        +"end : " + seq);
                wck =FrameUtil.getWCK((seq-
                            GeneralDataConstants.FRAME_WINSIZE + 1), 
                        seq);
                sendBytes+=GeneralDataConstants.HEADER_LEN
                    + GeneralDataConstants.TAIL_LEN
                    + wck.getArg().getValue().length;
                session.write(wck);
                session.setAttribute("wck",wck);
                waitAck(session,cnt); 
            }
            cnt++; 
        } 
        //if(frame.getSvc() != GeneralDataConstants.SVC_C) 
        //{ 
        if((cnt % GeneralDataConstants.FRAME_WINSIZE) != 0) 
        { 
            if(cnt > 1)
            {
                log.debug("WCK : start : " + (seq -(seq%
                                GeneralDataConstants.FRAME_WINSIZE)) 
                        + "end : " + seq); 
                wck =FrameUtil.getWCK(seq-(seq%
                            GeneralDataConstants.FRAME_WINSIZE),seq); 
                sendBytes+=GeneralDataConstants.HEADER_LEN 
                    + GeneralDataConstants.TAIL_LEN 
                    + wck.getArg().getValue().length; 
                session.write(wck); 
                session.setAttribute("wck",wck); 
                waitAck(session,cnt-1); 
            } 
        }
        //}
        FrameUtil.waitSendFrameInterval();
        session.removeAttribute("wck");
        return sendBytes;
    }    

    public void userAuthPass2(IoSession session, McuType mcuType) 
                throws MRPException {

        try {
            String message = null;
            Command command = null;
            if(mcuType == McuType.MMIU)
                command = new AuthPassCommand(AuthPassCommand.CMD_USERAUTH_PASS);
            else if(mcuType == McuType.IEIU)
                command = new AuthPassCommandIEIU(AuthPassCommandIEIU.CMD_USERAUTH_PASS);
            IoBuffer buf = null;
            buf = IoBuffer.allocate(command.makeSingleCommand().length);
            buf.put(command.makeSingleCommand());
            buf.flip();
            session.write(buf);
            
       //     message = new String((byte[])getMsg(session,10, 160));
            message = new String((byte[])getMsg(session,10));
            log.debug("userPass MSG["+message+"]");
            
            if(message != null && message.toLowerCase().indexOf("ack") < 0) {
                log.debug("userPass Command Failed=>"+message);
                throw new MRPException(MRPError.ERR_USERPASS_CLASS,"Modem authentication failed.");
            }
    
        } catch (Exception e) {
            throw new MRPException(MRPError.ERR_USERPASS_CLASS,"Modem authentication failed.");
        }
    }
    
    /**
     * Connect to meter
     * @throws FEPException
     */
    public void connectMeter(IoSession session)
                throws MRPException  {

        try {
            String message = null;
            Command c = new MeterModeCommand(MeterModeCommand.CMD_METER_MODE);
            
            IoBuffer buf = null;
            buf = IoBuffer.allocate(c.makeCommand().length);
            buf.put(c.makeCommand());
            buf.flip();
            session.write(buf);  
            
            message = new String((byte[])getMsg(session,10));
            log.debug("connect meter MSG["+message+"]");
            
            if(message != null && message.toLowerCase().indexOf("ack") < 0) {
                log.debug("connect meter Command Failed=>"+message);
                throw new MRPException(MRPError.ERR_CONNECTTOMETER_CLASS,"Meter connection failed");
            }
        } catch(Exception e) {
            throw new MRPException(MRPError.ERR_CONNECTTOMETER_CLASS,"Meter connection failed");
        }
    }

    public void connectMeter(IoSession session, String grpId, String grpMemId )
    	throws MRPException  
    {

		try {
			String message = null;
			Command c = new ByPassModeCommandIEIU(ByPassModeCommandIEIU.CMD_BYPASS_MODE,
			    					Byte.parseByte(grpId), Byte.parseByte(grpMemId));
			
			IoBuffer buf = null;
			buf = IoBuffer.allocate(c.makeCommand().length);
			buf.put(c.makeCommand());
			buf.flip();
			session.write(buf);  
			
			//message = new String((byte[])getMsg(session,13, 160));
			message = new String((byte[])getMsg(session,13));
			log.debug("connect meter MSG["+message+"]");
			
			if(message != null && message.toLowerCase().indexOf("ack") < 0) {
			    log.debug("connect meter Command Failed=>"+message);
			    throw new MRPException(MRPError.ERR_CONNECTTOMETER_CLASS,"Meter connection failed");
			}
		} catch(Exception e) {
			throw new MRPException(MRPError.ERR_CONNECTTOMETER_CLASS,"Meter connection failed");
		}
	}
    
    public boolean disconnectMeter(IoSession session)
	throws MRPException  
	{
    	boolean res = false; 
		try {
			String message = null;
			Command c = new ByPassModeStopCommandIEIU(ByPassModeStopCommandIEIU.CMD_BYPASS_STOP);
			
			IoBuffer buf = null;
			buf = IoBuffer.allocate(c.makeCommand().length);
			buf.put(c.makeCommand());
			buf.flip();
			session.write(buf);  
			
			//message = new String((byte[])getMsg(session,13, 160));
			message = new String((byte[])getMsg(session,13));
			log.debug("disconnect meter MSG["+message+"]");
			
			if(message==null || (message != null && message.toLowerCase().indexOf("ack") < 0)) {
			    log.debug("disconnect meter Command Failed=>"+message);
			}else{
				res = true;
			}
		} catch(Exception e) {
			throw new MRPException(MRPError.ERR_CONNECTTOMETER_CLASS,"Meter disconnection failed");
		}
		return res;
	}
    
    public byte[] getIEIUBasePulse(IoSession session, String grpId, String grpMemId, byte[] date )
	throws MRPException  
	{
    	byte[] res = null; 
		try {
			String message = null;
			Command c = new GetBasePulse(GetBasePulse.CMD_GET_BASEPULSE,
							Byte.parseByte(grpId), Byte.parseByte(grpMemId), date );
			
			IoBuffer buf = null;
			buf = IoBuffer.allocate(c.makeCommand().length);
			buf.put(c.makeCommand());
			buf.flip();
			session.write(buf);  
			
			//message = new String((byte[])getMsg(session,13, 160));
			res = (byte[])getMsg(session,13);
			
			message = new String(res);
			log.debug("connect meter MSG["+message+"]");
			
			if(message==null || 
					(message != null && 
							(message.toLowerCase().indexOf("nak") >=0 )
							|| (message.toLowerCase().indexOf("nau") >=0 )
							|| (message.toLowerCase().indexOf("rzb") >=0 ))) {
			    log.debug("getIEIUBasePulse Command Failed=>"+message);
			    res = null;
			}
			/*else{
				res = message.getBytes();
			}
			*/
		} catch(Exception e) {
			throw new MRPException(MRPError.ERR_CONNECTTOMETER_CLASS,"Meter disconnection failed");
		}
		return res;
	}
    
    public byte[] getIEIUDemandRomData(IoSession session, String grpId, String grpMemId, byte[] date )
	throws MRPException  
	{
    	byte[] res = null; 
		try {
			String message = null;
			Command c = new GetDemandRomDatas(GetDemandRomDatas.CMD_GET_DEMANDROM_DATA,
										Byte.parseByte(grpId), Byte.parseByte(grpMemId), date );
			
			IoBuffer buf = null;
			buf = IoBuffer.allocate(c.makeCommand().length);
			buf.put(c.makeCommand());
			buf.flip();
			session.write(buf);  
			log.debug("getIEIUDemandRomRead Command");
			//message = new String((byte[])getMsg(session,13, 160));
			res = (byte[])getMsg(session,30,859);
			
			message = new String(res);
			//log.debug("getIEIUDemandRomRead Command MSG["+message+"]");
			log.debug("getIEIUDemandRomRead Command DATA["+ new OCTET(res).toHexString()+"]");
			
			if(message==null || 
					(message != null && 
							(message.toLowerCase().indexOf("nak") >=0 )
							|| (message.toLowerCase().indexOf("nau") >=0 )
							|| (message.toLowerCase().indexOf("rzb") >=0 ))) {
			    log.debug("getIEIUDemandRomRead Command Failed=>"+message);
			    res = null;
			}

		} catch(Exception e) {
			throw new MRPException(MRPError.ERR_CONNECTTOMETER_CLASS,"getIEIUDemandRomData failed");
		}
		return res;
	}
	
    public byte[] getIEIUCummData(IoSession session, String grpId, String grpMemId, byte[] date )
	throws MRPException  
	{
    	byte[] res = null; 
		try {
			String message = null;
			Command c = new GetCummDatas(GetCummDatas.CMD_GET_CUMM_DATA,
										Byte.parseByte(grpId), Byte.parseByte(grpMemId), date );
			
			IoBuffer buf = null;
			buf = IoBuffer.allocate(c.makeCommand().length);
			buf.put(c.makeCommand());
			buf.flip();
			session.write(buf);  
			
			//message = new String((byte[])getMsg(session,13, 160));
			res = (byte[])getMsg(session,13);
			
			message = new String(res);
			log.debug("connect meter MSG["+message+"]");
			
			if(message==null || 
					(message != null && 
							(message.toLowerCase().indexOf("nak") >=0 )
							|| (message.toLowerCase().indexOf("nau") >=0 )
							|| (message.toLowerCase().indexOf("rzb") >=0 ))) {
			    log.debug("getIEIUCummData Command Failed=>"+message);
			    res = null;
			}
			/*else{
				res = message.getBytes();
			}
			*/
		} catch(Exception e) {
			throw new MRPException(MRPError.ERR_CONNECTTOMETER_CLASS,"Meter disconnection failed");
		}
		return res;
	}
    
    /**
     * Circuit Call and intialize  Protocol Service 
     * 
     * @param session <code>IoSession</code> session
     * @param target <code>CircuitTarget</code> Circuit Connect Information
     * @throws Exception
     */
    public void initCircuit(IoSession session,GPRSTarget target, boolean meterConnect) 
        throws Exception
    {
        log.debug("initCircuit Start");
        
        

        while(!isSessionOpened) 
        { 
            log.debug("initCircuit:: wait session opened"); 
            try { Thread.sleep(10); } catch(Exception exx) {} 
        }

        log.debug("initCircuit set isActive[false]");
        session.setAttribute(isActiveKey,deActive);
        log.debug("initCircuit get isActive["
                +session.getAttribute(isActiveKey)+"]");

        String message = null;

        int callRetry = new Integer(FMPProperty.getProperty("protocol.circuit.connect.retry"));
        try { callRetry= Integer.parseInt(
                FMPProperty.getProperty("Protocol.Circuit.Connect.Retry","3"));
        }catch(Exception exx){}

        int callCnt = 0;
        for(;;)
        {
            callCnt++;
            try 
            { 
            	log.debug("\r\n#### Call CNT["+callCnt+"]");
                connectCircuit(session,target,meterConnect);                 
                break;
            }catch(Exception ex)
            {            	
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
    }
    
    /**
     * SMS intialize  Protocol Service 
     * 
     * @param session <code>IoSession</code> session
     * @param target <code>CircuitTarget</code> Circuit Connect Information
     * @throws Exception
     */
    public void initSMS(IoSession session,CircuitTarget target) 
        throws Exception
    {
        log.debug("initSMS Start");
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

        log.debug("initCircuit set isActive[false]");
        session.setAttribute(isActiveKey,deActive);
        log.debug("initCircuit get isActive["
                +session.getAttribute(isActiveKey)+"]");

        String message = null;

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
                connectSMS(session,target); 
                log.debug("#### Call CNT["+callCnt+"]");
                break;
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
        
        //change textmode AT+CMGF=1
        /*
        at+CSCS?
        		at+CSCS?
        		+CSCS: "GSM"

        		OK
        		at+csca?
        		at+csca?
        		+CSCA: "+46705008600",145

        		OK
        		at+csms?
        		at+csms?
        		+CSMS: 0,1,1,1

        		OK
        		at+cnmi?
        		at+cnmi?
        		+CNMI: 0,0,0,0,1

        		OK
        		at^SMGO?
        		at^SMGO?
        		^SMGO: 0,0

        		OK
        		AT+CPMS?
        		AT+CPMS?
        		+CPMS: "MT",0,40,"MT",0,40,"MT",0,40

        		OK
        		AT^SSMSS?
        		AT^SSMSS?
        		^SSMSS: 0

        		OK
        		AT+CGSMS?
        		AT+CGSMS?
        		+CGSMS: 3

        		OK
        		AT^SM20?
        		AT^SM20?
        		^SM20: 1,1

        		OK
        		AT+CMGF?
        		AT+CMGF?
        		+CMGF: 0

        		OK
         */
        														
        
        
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

            log.debug("closeCircuit set isActive[false]");
            session.setAttribute(isActiveKey,deActive);
            log.debug("closeCircuit get isActive["
                    +session.getAttribute(isActiveKey)+"]");

            String ATH = 
                FMPProperty.getProperty("Protocol.Circuit.Command.ATH.1","+++");
           // if(!ATH.toUpperCase().equals("NOUSE")){
	            session.write(FrameUtil.getByteBuffer(ATH));
	            Object recvObj = null;
	          //  try { recvObj = getMsg(session,10,160); }catch(Exception ex) {}
	            try { recvObj = getMsg(session,5); }catch(Exception ex) {}
	            ATH = 
	                FMPProperty.getProperty("Protocol.Circuit.Command.ATH.2","ATH0");
	            session.write(FrameUtil.getByteBuffer(ATH+CRLF));
	          //  try { recvObj = getMsg(session,10,160); }catch(Exception ex) {}
	            try { recvObj = getMsg(session,5); }catch(Exception ex) {}
     //       }
            closedCircuitTime = System.currentTimeMillis();

            log.debug("Circuit Communication Time["
                    +(closedCircuitTime - openCircuitTime)+"]ms");
        }catch(Exception exx)
        {
            log.error(exx,exx);
        }
    }
    
    
    /**
     * wait util received command response data and return Response
     * 
     * @param session <code>ProtocolSession</code> session
     * @param tid <code>int</code> command request id
     * @throws FMPACKTimeoutException 
     * @throws Exception
     */
    public Object getMsgForIF4(IoSession session, long timeout) 
        throws MRPException, FMPACKTimeoutException
    {
        this.msg = null;
        long stime = System.currentTimeMillis();
        long ctime = 0;
        byte[] totmsg = new byte[0];
        int totlen = 0;
        int res_len =-1;
        ByteArray ba = new ByteArray();
        log.debug("message read start");
        
        while(session.isConnected())
        { 
            waitMsg();
            //waitResponse();
            log.debug("msg : "+msg);
            if(res_len>0 && totmsg.length == res_len) {
                log.debug("message all received");
                break;
            } else if(this.msg != null) {
                byte[] newMsg = ((byte[])this.msg);
                ba.append(newMsg);
                totmsg = ba.toByteArray();      
                this.msg = null;
                log.debug("totmsg=>"+new OCTET(totmsg).toHexString());
                
                if(res_len<0 && totmsg.length>=GeneralDataConstants.HEADER_LEN){
                	byte[] lengthField = new byte[GeneralDataConstants.LENGTH_LEN];
                    System.arraycopy(totmsg,3,lengthField,0,
                            lengthField.length);
                    DataUtil.convertEndian(lengthField);
                    res_len = DataUtil.getIntTo4Byte(lengthField) +10;
                    log.debug("res_len="+res_len);
            	}
            }
               
            ctime = System.currentTimeMillis();
            if(((ctime - stime)/1000.0) > timeout)
            {
                log.debug(" waitTime["+((ctime - stime)/1000)+"] timeout["
                    + timeout+"]");
                throw new FMPACKTimeoutException(" Msg Receive Timeout["
                        +timeout +"]");
            }
        }
        log.debug("final totmsg=>"+new OCTET(totmsg).toHexString());
        return totmsg;
    }    
    
    
    public CommandData getResult(String ns, byte[] message, int i ) throws Exception {
    	
    	GeneralDataFrame respondframe = null;
    	IoBuffer buf = IoBuffer.allocate(message.length);
    	buf.put(message,0,message.length);
    	buf.flip();
    	
    	try {
    		respondframe = GeneralDataFrame.decode(ns, buf); 
    	}catch(Exception ex)
        {
    		throw new Exception("CommandData fail");
        }
    	
    	CommandData cd = (CommandData)ServiceData.decode((ServiceDataFrame)respondframe, "");
    	
    	return cd;
    }    

	/**
	 * @return the session
	 */
	public IoSession getSession() {
		return session;
	}

	/**
	 * @param session the session to set
	 */
	public void setSession(IoSession session) {
		this.session = session;
	}
}
