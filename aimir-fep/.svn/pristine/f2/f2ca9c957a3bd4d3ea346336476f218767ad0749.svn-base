package com.aimir.fep.protocol.mrp.client.reversegprs;

import java.util.ArrayList;
import java.util.Iterator;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.aimir.constants.CommonConstants;
import com.aimir.fep.protocol.fmp.client.Client;
import com.aimir.fep.protocol.fmp.common.GPRSTarget;
import com.aimir.fep.protocol.fmp.common.Target;
import com.aimir.fep.protocol.fmp.datatype.BYTE;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.datatype.UINT;
import com.aimir.fep.protocol.fmp.datatype.WORD;
import com.aimir.fep.protocol.fmp.exception.FMPException;
import com.aimir.fep.protocol.fmp.exception.FMPResponseTimeoutException;
import com.aimir.fep.protocol.fmp.frame.ControlDataFrame;
import com.aimir.fep.protocol.fmp.frame.ErrorCode;
import com.aimir.fep.protocol.fmp.frame.GeneralDataConstants;
import com.aimir.fep.protocol.fmp.frame.GeneralDataFrame;
import com.aimir.fep.protocol.fmp.frame.ServiceDataConstants;
import com.aimir.fep.protocol.fmp.frame.ServiceDataFrame;
import com.aimir.fep.protocol.fmp.frame.service.AlarmData;
import com.aimir.fep.protocol.fmp.frame.service.CommandData;
import com.aimir.fep.protocol.fmp.frame.service.EventData;
import com.aimir.fep.protocol.fmp.frame.service.MDData;
import com.aimir.fep.protocol.fmp.frame.service.RMDData;
import com.aimir.fep.protocol.fmp.frame.service.ServiceData;
import com.aimir.fep.protocol.fmp.processor.ProcessorHandler;
import com.aimir.fep.protocol.reversegprs.CommHandler;
import com.aimir.fep.protocol.reversegprs.ReverseGPRSAdapter;
import com.aimir.fep.protocol.reversegprs.ReverseGPRSAdapterMBean;
import com.aimir.fep.protocol.reversegprs.SessionCache;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.FrameUtil;
import com.aimir.model.device.CommLog;
import com.aimir.util.TimeUtil;

public class REVERSEGPRSMMIUClient implements Client
{    
	private static Log log = LogFactory.getLog(REVERSEGPRSMMIUClient.class);
    private GPRSTarget target = null;

    private static IoSession session = null;
    private ProcessorHandler logProcessor = null;    

    private CommHandler handler;
    private JMXConnector jmxc = null;
    /**
     * constructor
     */
    public REVERSEGPRSMMIUClient()
    {
        log.debug("\r\n\r\n ####### REVERSEGPRSMMIU \r\n");
    }

    /**
     * constructor
     * @param target <code>GSMTarget</code> target
     */
    public REVERSEGPRSMMIUClient(GPRSTarget target)
    {
        this.target = target;
    }

    /**
     * set Target
     *
     * @param target <code>GSMTarget</code> target
     */
    public void setTarget(Target target) throws Exception
    {
        if(!(target instanceof GPRSTarget))
            throw new Exception("not supported target");
        this.target = (GPRSTarget)target;
    }

    private void saveCommLog(CommLog commLog)
    {
        try
        {
            if(this.logProcessor != null)
                this.logProcessor.putServiceData(ProcessorHandler.LOG_COMMLOG, commLog);
            else
                log.warn("Log Processor not registered");
        } catch(Exception ex)
        {
            log.error("save Communication Log failed",ex);
        }
    }

    public void setLogProcessor(ProcessorHandler logProcessor)
    {
        this.logProcessor = logProcessor;
    }

    
    /**
     * connect to Target and wait ENQ
     *
     * @throws Exception
     */
    private synchronized void connect() throws Exception
    {

    	/*
        String jmxUrl = FMPProperty.getProperty("fep.jmxrmi");
        JMXServiceURL url = new JMXServiceURL(jmxUrl);
        
        log.info("About to jmxcnect to : " + url.toString());

        jmxc = JMXConnectorFactory.connect(url);
        
        MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
        ObjectName objectName = new ObjectName(ReverseGPRSAdapter.ADAPTER_DOMAIN+":name="+"ReverseGPRSAdapter");
        ReverseGPRSAdapterMBean adapter = JMX.newMBeanProxy(mbsc, objectName, ReverseGPRSAdapterMBean.class, true);
        try{
        	session = adapter.getSession(target.getTargetId());
        }catch(Exception e){
        	log.error(e,e);
        }

        if(session != null && session.isConnected()){
        	log.info("GetClientSession from =["+target.getTargetId()+","+session.getRemoteAddress()+"]");
        	//handler = (CommHandler) session.getHandler();
        }else{
        	throw new Exception("No session for target=["+target.getTargetId()+"]"+" from ReverseGPRSAdapter");
        }
        */

    }

    public CommandData sendCommand(CommandData command)
            throws Exception{
    	
    	CommandData responseCommandData = null; 
    	
    	try{ 	

            String jmxUrl = FMPProperty.getProperty("fep.jmxrmi");
            JMXServiceURL url = new JMXServiceURL(jmxUrl);
            
            log.info("About to jmxcnect to : " + url.toString());

            jmxc = JMXConnectorFactory.connect(url);
            
            MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
            ObjectName objectName = new ObjectName(ReverseGPRSAdapter.ADAPTER_DOMAIN+":name="+"ReverseGPRSAdapter");
            ReverseGPRSAdapterMBean adapter = JMX.newMBeanProxy(mbsc, objectName, ReverseGPRSAdapterMBean.class, true);
            try{
            	responseCommandData = adapter.cmdExecute(target.getTargetId(), command);
            }catch(Exception e){
            	log.error(e,e);
            }
            
    	} finally {
    		try {
    			if (jmxc != null)
    				jmxc.close();
    		}
    		catch (Exception e){
    			log.error(e,e);
    		}
    	}
		if(responseCommandData == null){
			responseCommandData = command;
			responseCommandData.removeSmiValues();
		}
		responseCommandData.setAttr(ServiceDataConstants.C_ATTR_RESPONSE);
		responseCommandData.setErrCode(new BYTE(ErrorCode.IF4ERR_NOERROR));
		responseCommandData.setCnt(new WORD(1));
    	return responseCommandData;
    }
    /**
     * request command data to Target and response 
     *
     * @param command <code>CommandData</code> request command
     * @return response <code>ServiceData</code> response
     * @throws Exception
     */
    /*
    public CommandData sendCommand(CommandData command)
        throws Exception
    {    	
            
    	connect();
        log.info("GSMClient::sendCommand("+command+")");
		try {
			CommLog commLog = new CommLog();
			ServiceDataFrame frame = new ServiceDataFrame();
			log.debug("target get model : "+target.getMeterModel());
			
			long mcuId = Long.parseLong(target.getTargetId());
			log.debug("Target ID: "+target.getTargetId());
			
            command.setAttr(ServiceDataConstants.C_ATTR_REQUEST);
            command.setTid(FrameUtil.getCommandTid());
            command.setMcuId(target.getTargetId());
                    
            log.debug("SEND DATA");            
            
            ServiceData sd = null;
            String startTime = TimeUtil.getCurrentTime();
            commLog.setStartDate(startTime.substring(0,8));
            commLog.setStartTime(startTime.substring(8,14));
            commLog.setStartDateTime(startTime);
            	            	            	            	            	            
            long s = System.currentTimeMillis();
            Integer sendBytes = 0;
            String cmd = command.getCmd().getValue();
            log.debug("CDMAMMIUClient::sendCommand::start Wait Response");
       
                frame = new ServiceDataFrame();
                frame.setSvc(GeneralDataConstants.SVC_C);
                frame.setMcuId(new UINT(mcuId));
                frame.setSvcBody(command.encode());
                //------------------
                //  SEND!!!!!!!
                //------------------
            	sendBytes = send(frame);

            //sd = getResponse(session,command.getTid().getValue());
            log.debug("CDMAMMIUClient::sendCommand::end Wait Response");
            long e = System.currentTimeMillis();
            if(sd == null) 
                return null;
            log.debug("Received Response TID : " + ((CommandData)sd).getTid());
            commLog.setEndTime(TimeUtil.getCurrentTime());
            commLog.setSendBytes(sendBytes);
            commLog.setRcvBytes(sd.getTotalLength());
            commLog.setUnconPressedRcvBytes(sd.getTotalLength());	            
            if(((CommandData)sd).getErrCode().getValue() > 0)
            {
                commLog.setCommResult(0);//0이 실패
                commLog.setDescr(ErrorCode.getMessage(((CommandData)sd).getErrCode().getValue()));
            }            
            else
            {
                commLog.setCommResult(1);//1이 성공
            }
            commLog.setTotalCommTime((int)(e-s));	            
            commLog.setSvcTypeCode(CommonConstants.getHeaderSvc("C"));
            commLog.setSuppliedId(target.getSupplierId());
            log.info(commLog.toString());
            saveCommLog(commLog);	            
            return (CommandData)sd;
		} catch (Exception ex) {			
			log.error("sendCommand failed : command[" + command + "]", ex);
			throw ex;
		} finally {
            try {
                if (jmxc != null)
                    jmxc.close();
            }
            catch (Exception e){
            	log.error(e,e);
            }
        }
    }
    */

    /**
     * send Alarm to Target 
     *
     * @param alarm <code>AlarmData</code> send alarm
     * @throws Exception
     */
    public void sendAlarm(AlarmData alarm) throws Exception
    {
    }

    /**
     * send Event to Target 
     *
     * @param event <code>EventData</code> event alarm
     * @throws Exception
     */
    public void sendEvent(EventData event) throws Exception
    {
    }

    /**
     * send Measurement Data to Target 
     *
     * @param md <code>MDData</code> Measurement Data
     * @throws Exception
     */
    public void sendMD(MDData md) throws Exception
    {
        ServiceDataFrame frame = new ServiceDataFrame();
        frame.setAttrByte(GeneralDataConstants.ATTR_ACK);
        frame.setAttrByte(GeneralDataConstants.ATTR_START);
        frame.setAttrByte(GeneralDataConstants.ATTR_END);
        long mcuId = Long.parseLong(target.getTargetId());
        frame.setMcuId(new UINT(mcuId));
        frame.setSvcBody(md.encode());
        frame.setSvc(GeneralDataConstants.SVC_M);
        send(frame);

        log.info("sendMD : finished");
    }
    
    /**
     * send R Measurement Data to Target 
     *
     * @param md <code>RMDData</code>R Measurement Data
     * @throws Exception
     */
    public void sendRMD(RMDData rmd) throws Exception
    {
        ServiceDataFrame frame = new ServiceDataFrame();
        frame.setAttrByte(GeneralDataConstants.ATTR_ACK);
        frame.setAttrByte(GeneralDataConstants.ATTR_START);
        frame.setAttrByte(GeneralDataConstants.ATTR_END);
        long mcuId = Long.parseLong(target.getTargetId());
        frame.setMcuId(new UINT(mcuId));
        frame.setSvcBody(rmd.encode());
        frame.setSvc(GeneralDataConstants.SVC_R);

        send(frame);

        log.info("sendMD : finished");
    }

    /**
     * close GSMClient session
     */
    public void close()
    {
        close(false);
    }

    /**
     * close GPRSClient session and wait completed
     *
     * @param immediately <code>boolean</code> immediately
     */
    public void close(boolean immediately)
    {
        log.debug(immediately);
        if(session != null) {
            session.write(FrameUtil.getEOT());
            CloseFuture future = session.closeNow();
            //future.awaitUninterruptibly();
        }

        //if(connector != null) {
        //    connector.dispose();
        //}

        session = null;
    }

    /**
     * check whether connected or not
     *
     * @return connected <code>boolean</code>
     */
    public boolean isConnected()
    {
        if(session == null)
            return false;
        return session.isConnected();
    }

    /**
     * wait ACK
     */
    private void waitAck(IoSession session, int sequence)
        throws Exception
    { 
        log.debug("waitACK.SEQ:" + sequence);
        handler.waitAck(session,sequence);
    }
 
    /**
     * send GeneralDataFrame to Target
     *
     * @param frame <code>GeneralDataFrame</code>
     */
    private synchronized int send(GeneralDataFrame frame) 
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
        session.removeAttribute("wck");
        return sendBytes;
    }
}
