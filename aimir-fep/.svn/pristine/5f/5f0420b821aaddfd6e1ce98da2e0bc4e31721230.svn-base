package com.aimir.fep.protocol.fmp.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.constants.CommonConstants.ThresholdName;
import com.aimir.fep.protocol.fmp.common.CDMATarget;
import com.aimir.fep.protocol.fmp.common.CircuitTarget;
import com.aimir.fep.protocol.fmp.common.GSMTarget;
import com.aimir.fep.protocol.fmp.common.PSTNTarget;
import com.aimir.fep.protocol.fmp.common.SlideWindow;
import com.aimir.fep.protocol.fmp.common.SlideWindow.COMPRESSTYPE;
import com.aimir.fep.protocol.fmp.exception.FMPACKTimeoutException;
import com.aimir.fep.protocol.fmp.exception.FMPENQTimeoutException;
import com.aimir.fep.protocol.fmp.exception.FMPException;
import com.aimir.fep.protocol.fmp.exception.FMPResponseTimeoutException;
import com.aimir.fep.protocol.fmp.frame.ControlDataConstants;
import com.aimir.fep.protocol.fmp.frame.ControlDataFrame;
import com.aimir.fep.protocol.fmp.frame.GeneralDataConstants;
import com.aimir.fep.protocol.fmp.frame.GeneralDataFrame;
import com.aimir.fep.protocol.fmp.frame.ServiceDataFrame;
import com.aimir.fep.protocol.fmp.frame.service.CommandData;
import com.aimir.fep.protocol.fmp.frame.service.ServiceData;
import com.aimir.fep.protocol.fmp.processor.ProcessorHandler;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.FrameUtil;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.threshold.CheckThreshold;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

/**
 * {@link FMPProtocolHandler} implementation of FEP FMP(AiMiR and MCU Protocol).
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class FMPClientProtocolHandler extends IoHandlerAdapter
{
    private static Log log = LogFactory.getLog(FMPClientProtocolHandler.class);

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
    private int enqTimeout = Integer.parseInt(
            FMPProperty.getProperty("protocol.enq.timeout","3"));
    private int ackTimeout = Integer.parseInt(
            FMPProperty.getProperty("protocol.ack.timeout","3"));

    private FMPClientResource resource = null;
    private Object      resourceObj = null;

    public  final static String isActiveFMPKey = "isActiveFMP";
    private Boolean  activeFMP =  Boolean.TRUE;
    private Boolean  deActiveFMP = Boolean.FALSE;
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
        // cause.printStackTrace();
        log.debug(cause.getMessage());
        if (cause.getMessage().contains("SSL handshake failed")) {
            // Security Alarm
            String activatorId = session.getRemoteAddress().toString();
            if (activatorId.contains("/") && activatorId.contains(":"))
                activatorId = activatorId.substring(activatorId.indexOf("/")+1, activatorId.indexOf(":"));
            /*
            try {
                EventUtil.sendEvent("Security Alarm",
                        TargetClass.Unknown, activatorId,
                        new String[][] {{"message", "Uncertificated Access"}});
            }
            catch (Exception e) {
                log.error(e, e);
            }
            */
            
            // INSERT START SP-193
            CheckThreshold.updateCount(session.getRemoteAddress().toString(), ThresholdName.AUTHENTICATION_ERROR);
            // INSERT END SP-193
        }
        else {
            // INSERT START SP-193
            CheckThreshold.updateCount(session.getRemoteAddress().toString(), ThresholdName.INVALID_PACKET);
            // INSERT END SP-193        
        }
        // session.write(FrameUtil.getEOT());
        FrameUtil.waitAfterSendFrame();
        closeCircuit(session);
        session.closeNow();
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
            // 확장 처리해야 함.
            byte[] args = cdf.getArg().getValue();
            
            // enq 버전이 1.1 인 경우 frame size와 window size를 session에 저장한다.
            if (args[0] == 0x01 && args[1] == 0x01) {
                int frameMaxLen = DataUtil.getIntTo2Byte(new byte[] {args[3], args[2]});
                int frameWinSize = DataUtil.getIntToByte(args[4]);
                session.setAttribute("frameMaxLen", frameMaxLen);
                session.setAttribute("frameWinSize", frameWinSize);                
                log.info("ENQ V1.1 Frame Size[" + frameMaxLen + "] Window Size[" + frameWinSize + "]");
            }
            else {
                session.setAttribute("frameMaxLen", GeneralDataConstants.FRAME_MAX_LEN);
                session.setAttribute("frameWinSize", GeneralDataConstants.FRAME_WINSIZE);
            }
            
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
                        // get body
                        IoBuffer buf = sw.getCompletedFrameBuffer();
                        byte[] data = new byte[buf.limit()];
                        buf.get(data,0,data.length);
                        saveSlideWindow(data);
                    }catch(Exception ex)
                    {
                        log.error(ex);
                    }
                }
                session.write(FrameUtil.getACK(args[1]));
            }
            else 
                session.write(FrameUtil.getNAK(naks)); 
        }
    }

    private void saveSlideWindow(byte[] bx) {
        FileOutputStream fos = null;
        String serviceType = ProcessorHandler.SERVICE_DATAFILEDATA;

        try {
            int compressTypeCode = DataUtil.getIntToByte(bx[0]);
            COMPRESSTYPE compressType = SlideWindow.getCompressType(compressTypeCode);
            String fileName = getFileName(compressType.getName());

            fos = new FileOutputStream(fileName);
            fos.write(bx);
            
            // fileName response
        }
        catch (Exception ex) {
            log.error(ex);
        }
        finally {
            if (fos != null) {
                try {
                    fos.close();
                }
                catch (Exception e) {}
            }
        }
    }

    private String getFileName(String compressType)
    throws Exception
    {
        File file = new File(FMPProperty.getProperty("protocol.slidewindow.dir"));
        if (!file.exists()) {
            file.mkdirs();
        }

        String fileName = null;
        while (fileName == null || file.exists()) {
            fileName = file.getAbsolutePath() + File.separator + (new Date()).getTime() + "." + compressType;
            file = new File(fileName);
        }
        log.info(fileName);
        return fileName;
    }
    
    // processing service data frame
    private void receivedServiceDataFrame(IoSession session,
            ServiceDataFrame sdf) throws Exception
    { 
        Object ns_obj = session.getAttribute("nameSpace");
        String ns = ns_obj!=null? (String)ns_obj:null;
        
        ServiceData sd = ServiceData.decode(ns, sdf, session.getRemoteAddress().toString()); 
        if(sd != null) 
        { 
            log.info("NameSpace=[" + ns + "] ServiceData :" + sd.getType()+ " Count : " + count++); 
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
                else if(frame instanceof ControlDataFrame) 
                    receivedControlDataFrame(session,
                            (ControlDataFrame)frame);
            }else
            {
                synchronized(msgMonitor) 
                {
                    msg = message;
                    msgMonitor.notify();
                }
            }
        }catch(Exception ex)
        {
            log.error("FMPClientProtocolHandler::messageReceived "
                    + " failed" ,ex);
        }
    }

    public void sessionIdle(IoSession session, IdleStatus status)
    throws Exception
    {
        log.info("IDLE COUNT : " 
                + session.getIdleCount(IdleStatus.BOTH_IDLE));
        // if(session.getIdleCount(IdleStatus.BOTH_IDLE) >= retry)
        // {
            /** modified by D.J Park in 2006.04.10
             * if wait response ,then do not close session
             */
        /*
            if(response.values().size() < 1)
            {
                session.write(FrameUtil.getEOT());
                //FrameUtil.waitAfterSendFrame();
                closeCircuit(session);
                session.closeNow();
            }*/
        session.closeNow();
        // }
    }

    /**
     * inherited method from ProtocolHandlerAdapter
     */
    public void sessionOpened(IoSession session)
    {
        session.setAttribute(isActiveFMPKey,activeFMP);
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
                log.debug("#### FMPProtocalHandler: Resource Release ###");
                resource.release(resourceObj);
                log.debug("FMPProtocalHandler: "+resource.getActiveResourcesString());
                log.debug("FMPProtoalHandler: "+resource.getIdleResourcesString());
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
        session.closeNow();
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
     * wait ENQ ControlDataFrame
     *
     * @throws Exception
     */
    public void waitENQ() throws FMPException
    {
        /*
        synchronized(enqMonitor)
        {
            if(isReceivedENQ) {
                return;    
            } else {
                try{ 
                    //log.debug("enqMonitor Wait");
                    enqMonitor.wait();
                    //log.debug("enqMonitor Received");
                }catch(InterruptedException ie)
                { ie.printStackTrace(); }
            }
        }
        */
        log.debug("wait start");
        int waitEnqCnt = 0;
        while(!isReceivedENQ)
        {
            synchronized(enqMonitor)
            {
                try {enqMonitor.wait(500); }
                catch(Exception ex) {}
            }
            waitEnqCnt++;
            if((waitEnqCnt / 2) > enqTimeout)
            { 
                throw new FMPENQTimeoutException(
                        "ENQ Wait Timeout[" +enqTimeout +"]");
            }
        }
        log.debug("wait end");
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
     * @throws FMPException
     */
    public ServiceData getResponse(IoSession session,int tid)
        throws FMPException
    {
        String key = ""+tid;
        long stime = System.currentTimeMillis();
        long ctime = 0;
        int waitResponseCnt = 0;
        while(session.isConnected())
        { 
            // log.debug(tid);
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
                    throw new FMPResponseTimeoutException(" tid : "
                            + tid +" Response Timeout["
                            +responseTimeout +"]");
                }
                */
                if(((ctime - stime)/1000) > responseTimeout)
                {
                    log.debug("getResponse:: SESSION IDLE COUNT["
                            +session.getIdleCount(IdleStatus.BOTH_IDLE) 
                            +"]");
                    // if(session.getIdleCount(IdleStatus.BOTH_IDLE) >= retry)
                    // {
                        response.remove(key); 
                        throw new FMPResponseTimeoutException(" tid : " 
                                + tid +" Response Timeout[" 
                                +responseTimeout +"]");
                    // }
                }
            }
        }
        return null;
    }

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
            FMPProperty.getProperty("protocol.circuit.command.ATZ","ATZ");

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
            AT = FMPProperty.getProperty("protocol.circuit.command.AT.GSM");
        else if(target instanceof CDMATarget)
            AT = FMPProperty.getProperty("protocol.circuit.command.AT.CDMA");
        else if(target instanceof PSTNTarget)
            AT = FMPProperty.getProperty("protocol.circuit.command.AT.PSTN");

        log.debug("initCircuit AT["+AT+"]");
        if(AT != null && AT.length() > 1)
        {
            session.write(FrameUtil.getByteBuffer(AT+CR+LF));
            //log.debug("initCircuit Write AT["+AT+"]");
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
            FMPProperty.getProperty("protocol.circuit.command.ATDT","ATDT");
        String mobileId = target.getMobileId();
        if(mobileId != null && mobileId.length() > 0)
            mobileId = mobileId.replaceAll("-","");
        session.write(FrameUtil.getByteBuffer(ATDT+" "+mobileId+CR+LF));
        //log.debug("initCircuit Write ATDT["+ATDT+"]");
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

        log.debug("initCircuit set isActiveFMP[false]");
        session.setAttribute(isActiveFMPKey,deActiveFMP);
        log.debug("initCircuit get isActiveFMP["
                +session.getAttribute(isActiveFMPKey)+"]");

        String message = null;

        int callRetry = 3;
        try { callRetry= Integer.parseInt(
                FMPProperty.getProperty("protocol.circuit.connect.retry","3"));
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
        	//throw new FMPException("Sleep Failed After ATDT"); 
        	throw new Exception("Sleep Failed After Calling"); 
        }

        String NACS =
            FMPProperty.getProperty("protocol.circuit.command.service.NACS");

        //log.debug("initCircuit NACS["+NACS+"]");
        session.write(FrameUtil.getByteBuffer(NACS+LF+LF+CR+LF));
        //log.debug("initCircuit Write NACS["+NACS+"]");

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        Object recvObj = null;
        try { recvObj = getMsg(session,30); }catch(Exception ex) {}
        byte[] bx =  null;
        while(recvObj != null)
        {
            bx = (byte[])recvObj;
            bao.write(bx,0,bx.length);
            try { recvObj= getMsg(session,3);
            }catch(Exception ex) { recvObj = null; }
        }
        message = new String(bao.toByteArray());
        log.debug("initCircuit Read MSG["+message+"]");

        if(message.toUpperCase().indexOf("ACCEPT") >= 0)
        {
            int idx1 = message.indexOf("\n\n");
            if(idx1+2 < message.length())
                isReceivedENQ = true;
            String msmsg = message.substring(0,idx1);
            log.debug("initCircuit MServer MSG["+msmsg+"] isReceivedENQ["
                    +isReceivedENQ+"]");
            idx1 = msmsg.indexOf(" ", msmsg.indexOf(" ")+1);
            String errMsg = msmsg.substring(idx1+1);

            String[] tmp = msmsg.substring(0,idx1).split("[/| ]");

            String ver = tmp[1];
            String errCode = tmp[2];

            log.debug("VER["+ver+"] ERRCODE["+errCode+"] ERRMSG["+errMsg+"]");
        //} else if(message.toUpperCase().indexOf("ATA") >= 0)
        //{
        //    isReceivedENQ = true;
        } else
        {
            closeCircuit(session);
            //throw new FMPENQTimeoutException("initCircuit:: Can't Received ENQ");
            throw new Exception("initCircuit:: Can't Received ENQ");
        }
        
        
        int circuitIdleTime = Integer.parseInt(FMPProperty.getProperty(
                "protocol.circuit.connect.request.idle.time","5"));
        session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE,
                circuitIdleTime);

        session.setAttribute(isActiveFMPKey,activeFMP);
        log.debug("initCircuit set isActiveFMP[true]");
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

            log.debug("closeCircuit set isActiveFMP[false]");
            session.setAttribute(isActiveFMPKey,deActiveFMP);
            log.debug("closeCircuit get isActiveFMP["
                    +session.getAttribute(isActiveFMPKey)+"]");

            String ATH = 
                FMPProperty.getProperty("protocol.circuit.command.ATH.1","+++");
            session.write(FrameUtil.getByteBuffer(ATH));
            Object recvObj = null;
            try { recvObj = getMsg(session,1); }catch(Exception ex) {}
            ATH = 
                FMPProperty.getProperty("protocol.circuit.command.ATH.2","ATH");
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
}
