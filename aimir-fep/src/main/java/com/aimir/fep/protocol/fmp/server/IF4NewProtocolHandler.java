package com.aimir.fep.protocol.fmp.server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.Interface;
import com.aimir.fep.protocol.fmp.common.SlideWindow;
import com.aimir.fep.protocol.fmp.common.SlideWindow.COMPRESSTYPE;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.protocol.fmp.datatype.WORD;
import com.aimir.fep.protocol.fmp.exception.FMPACKTimeoutException;
import com.aimir.fep.protocol.fmp.frame.ControlDataConstants;
import com.aimir.fep.protocol.fmp.frame.ControlDataFrame;
import com.aimir.fep.protocol.fmp.frame.GeneralDataConstants;
import com.aimir.fep.protocol.fmp.frame.GeneralDataFrame;
import com.aimir.fep.protocol.fmp.frame.ServiceDataFrame;
import com.aimir.fep.protocol.fmp.frame.service.CommandData;
import com.aimir.fep.protocol.fmp.frame.service.MDData;
import com.aimir.fep.protocol.fmp.frame.service.NDData;
import com.aimir.fep.protocol.fmp.frame.service.RMDData;
import com.aimir.fep.protocol.fmp.frame.service.ServiceData;
import com.aimir.fep.protocol.fmp.processor.ProcessorHandler;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.FrameUtil;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.CommLog;
import com.aimir.model.system.Code;
import com.aimir.util.TimeUtil;

/**
 * {@link IF4NewProtocolHandler} implementation of FEP FMP(AiMiR and MCU Protocol).
 *
 * @author goodjob (goodjob@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2014-08-19 10:00:00 +0900 $,
 */
@Component
public class IF4NewProtocolHandler extends IoHandlerAdapter
{
    private static Log log = LogFactory.getLog(IF4NewProtocolHandler.class);

    private Object ackMonitor = new Object();
    private ControlDataFrame ack = null;

    private int idleTime = Integer.parseInt(FMPProperty.getProperty(
                "protocol.idle.time","5"));
    private int retry = Integer.parseInt(FMPProperty.getProperty(
                "protocol.retry","3"));
    private int ackTimeout = Integer.parseInt(
            FMPProperty.getProperty("protocol.ack.timeout","3"));
    private CommLog commLog = new CommLog();
    private String startTime = null;
    private String endTime = null;
    private long   startLongTime = 0;
    private long   endLongTime = 0;
    private Integer activatorType = new Integer(
            FMPProperty.getProperty("protocol.system.MCU","2"));
    private Integer targetType = new Integer(
            FMPProperty.getProperty("protocol.system.FEP","1"));
    private final int SENDCONTROLDATASIZE = 29; //ENQ+ACK
    private final int SENDENQDATASIZE = 15; //ENQ Data Size
    private final int RECVCONTROLDATASIZE = 14; //EOT
    private Integer protocolType = new Integer(FMPProperty.getProperty("protocol.type.default","3"));
    private String authenticationKey =  FMPProperty.getProperty("ota.auth.code" , "NURIPlatform2010");

    private static String pkgName = "com.aimir.fep.command.response.action";
    
    @Autowired
    private ProcessorHandler processorHandler;
    
    private void putServiceData(String serviceType, Serializable data) {
        try {
            processorHandler.putServiceData(serviceType, data);
        }
        catch (Exception e) {
            log.error(e);
        }
    }

    /**
     * inherited method from ProtocolHandlerAdapter
     */
    public void exceptionCaught(IoSession session,
            Throwable cause )
    {
        // Close connection when unexpected exception is caught.
        log.error(cause, cause);
        log.error(cause+"  from "+session.getRemoteAddress());
        // session.write(FrameUtil.getEOT());
        // FrameUtil.waitAfterSendFrame();
        session.closeNow();
    }
    
    private void receivedControlDataFrame(IoSession session,
            ControlDataFrame cdf) throws Exception
    {
        byte code = cdf.getCode();
        if(code == ControlDataConstants.CODE_ENQ)
        {
            // 확장 처리해야 함.
            byte[] args = cdf.getArg().getValue();
            log.info("ENQ[" + Hex.decode(args) + "]");
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
        }
        else if(code == ControlDataConstants.CODE_NEG){
            byte[] args = cdf.getArg().getValue();
            log.info("NEG[" + Hex.decode(args) + "]");
            // enq 버전이 1.2 인 경우 frame size와 window size를 session에 저장한다.
            if (args[0] == 0x01 && args[1] == 0x02) {
                int frameMaxLen = DataUtil.getIntTo2Byte(new byte[] {args[3], args[2]});
                int frameWinSize = DataUtil.getIntToByte(args[4]);
                String nameSpace = new String(DataUtil.select(args, 5, 2));
                session.setAttribute("frameMaxLen", frameMaxLen);
                session.setAttribute("frameWinSize", frameWinSize);
                session.setAttribute("nameSpace", frameWinSize);
                log.info("ENQ V1.2 Frame Size[" + frameMaxLen + "] Window Size[" + frameWinSize + "] NameSpace["+nameSpace+"]");
                
                session.write(FrameUtil.getNEGR());
            }
            else {
                session.setAttribute("frameMaxLen", GeneralDataConstants.FRAME_MAX_LEN);
                session.setAttribute("frameWinSize", GeneralDataConstants.FRAME_WINSIZE);
                ControlDataFrame negrFrame = FrameUtil.getNEGR();
                negrFrame.setArg(new OCTET(new byte[]{ControlDataConstants.NEG_R_UNSUPPORTED_VERSION}));
                session.write(negrFrame);
                Thread.sleep(1000);
                CloseFuture future = session.closeNow();
            }            
        }
        else if(code == ControlDataConstants.CODE_NEGR){
            log.debug(getProtoName()+" NEG_R Received ");
            byte[] args = cdf.getArg().getValue();
            
            if(args[0] == ControlDataConstants.NEG_R_NOERROR){
                log.debug(getProtoName()+" NEG_R_NOERROR ");
            }else if(args[0] == ControlDataConstants.NEG_R_UNSUPPORTED_VERSION){
            	log.debug(getProtoName()+" NEG_R_UNSUPPORTED_VERSION ");
            	CloseFuture future = session.closeNow();
            }else if(args[0] == ControlDataConstants.NEG_R_UNKNOWN_NAMESPACE){
            	log.debug(getProtoName()+" NEG_R_UNKNOWN_NAMESPACE ");
            	CloseFuture future = session.closeNow();
            }else if(args[0] == ControlDataConstants.NEG_R_INVALID_SIZE){
            	log.debug(getProtoName()+" NEG_R_INVALID_SIZE ");
            	CloseFuture future = session.closeNow();
            }else if(args[0] == ControlDataConstants.NEG_R_INVALID_COUNT){
            	log.debug(getProtoName()+" NEG_R_INVALID_COUNT ");
            	CloseFuture future = session.closeNow();
            }            
        }
        else if(code == ControlDataConstants.CODE_ACK)
        {
            int sequence = FrameUtil.getAckSequence(ack);
            log.debug(" ACK sequence #" + sequence);
            synchronized(ackMonitor)
            {
                ack = cdf;
                ackMonitor.notify();
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
            if(wck != null) {
                        session.write(wck);
                  }

        }
        else if(code == ControlDataConstants.CODE_EOT)
        {
            log.debug(getProtoName()+" EOT Received ");
            CloseFuture future = session.closeNow();
            // future.awaitUninterruptibly();
        }
        else if(code == ControlDataConstants.CODE_WCK)
        {
            byte[] args = cdf.getArg().getValue();
            int startseq = DataUtil.getIntToByte(args[0]);
            int endseq = DataUtil.getIntToByte(args[1]);
            log.info("WCK start[" + startseq + "] end[" + endseq + "]");

            SlideWindow sw = (SlideWindow)session.getAttribute("slidewindow");
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
                        if (data[7] == 'A') {
                            log.debug("AlarmData");
                            // alarm frame is not multi
                        }
                        else {
                            InetSocketAddress isa = (InetSocketAddress)session.getRemoteAddress();
                            //통신 로그 저장 로직
                            startLongTime = (Long)session.getAttribute("startLongTime");
                            endTime = TimeUtil.getCurrentTime();
                            endLongTime = System.currentTimeMillis();
                            
                            commLog.setStartDateTime((String)session.getAttribute("startTime"));//Start Time은 sessionOpen 시 지정
                            commLog.setStartDate(commLog.getStartDateTime().substring(0,8));
                            commLog.setStartTime(commLog.getStartDateTime().substring(8,14));
                            commLog.setEndTime(endTime);
                            commLog.setInterfaceCode(CommonConstants.getInterface( Interface.IF4.getName()));
                            // commLog.setProtocolCode(CommonConstants.getProtocol(protocolType+""));
                            // commLog.setSenderTypeCode(CommonConstants.getSenderReceiver(activatorType+""));
                            commLog.setSenderId(isa.getAddress().getHostAddress());
                            commLog.setReceiverTypeCode(CommonConstants.getSenderReceiver(targetType+""));
                            commLog.setReceiverId(DataUtil.getFepIdString());
                            commLog.setOperationCode("ServiceData.DFData");
                            commLog.setSvcTypeCode(CommonConstants.getHeaderSvc("P"));
                            commLog.setSendBytes(SENDENQDATASIZE+cdf.getLength().getValue()); //ENQ+Response Service Data Frame
                            commLog.setUncompressedSendBytes(SENDENQDATASIZE+cdf.getLength().getValue());
                            commLog.setRcvBytes(data.length+RECVCONTROLDATASIZE);//included EOT that received
                            commLog.setUnconPressedRcvBytes(data.length+RECVCONTROLDATASIZE);
                            // commLog.setSuppliedId(supplierId+"");
                            commLog.setCommResult(1);
                            log.debug("startTime["+startTime+"] endTime["+endTime+"]");
                            log.debug("startLongTime["+startLongTime+"] endLongTime["
                                    +endLongTime+"]");
                            if(endLongTime - startLongTime > 0) {
                                commLog.setTotalCommTime((int)(endLongTime - startLongTime));
                            }
                            else {
                                commLog.setTotalCommTime(0);
                            }
                            log.info(getProtoName()+" "+commLog.toString());
                            saveCommLog(commLog);
                            session.removeAttribute("startLongTime");
                            session.removeAttribute("startTime");
                            saveSlideWindow(data);
                        }
                    }catch(Exception ex)
                    {
                        log.error(ex);
                        // System.exit(-1);
                    }
                }
                session.write(FrameUtil.getACK(args[1]));
            }
            else {
                session.write(FrameUtil.getNAK(naks));
            }
        }
    }

    private void receivedServiceDataFrame(IoSession session, ServiceDataFrame sdf)
    throws Exception
    {
        ServiceData sd = null;
        String ipaddr = session.getRemoteAddress().toString();
        ipaddr = ipaddr.substring(ipaddr.indexOf("/")+1, ipaddr.indexOf(":"));
        sd = ServiceData.decode(sdf, ipaddr);

        if(sd != null)
        {
            int supplierId=0;
            String interfaceName = Interface.IF4.getName();

            log.debug(getProtoName()+ " ServiceData :"
                    + sd.getType() );
            log.debug(getProtoName()+" RECEIVED SERVICE DATA \n"+sd);
            //------------------------------------------------
            // Command Data Frame - 서버가 수신한 커맨드를 처리하고 응답을 리턴함
            //------------------------------------------------
            if(sd.getType().equals("ServiceData.CommandData"))
            {
                //Task 처리
                ServiceData responseServiceData = cmdExecute(session, sd);
                if(responseServiceData !=null) {
                	ServiceDataFrame responseSdf = sdf;
                	responseSdf.setSvcBody(responseServiceData.encode());
                	log.info("Send Command Response: "+responseSdf +" \nServicd Data:" + sd);
                	session.write(responseSdf);
                }

                //TODO command에 대한 응답은 커넥션이 open된 상태에서 처리됨으로 que에 넣어서 다시 꺼내서 쓸 수 없다.
                //processing(session,sd);

                endLongTime = System.currentTimeMillis();
                endTime = TimeUtil.getCurrentTime();

                //통신 로그 저장 로직
                commLog.setStartDateTime(startTime);//Start Time은 sessionOpen 시 지정
                commLog.setStartDate(startTime.substring(0,8));
                commLog.setStartTime(startTime.substring(8,14));
                commLog.setEndTime(endTime);
                commLog.setInterfaceCode(CommonConstants.getInterface(interfaceName));
                // commLog.setProtocolCode(CommonConstants.getProtocol(protocolType+""));
                // commLog.setSenderTypeCode(CommonConstants.getSenderReceiver(activatorType+""));
                commLog.setSenderId(sd.getMcuId());
                commLog.setReceiverTypeCode(CommonConstants.getSenderReceiver(targetType+""));
                commLog.setReceiverId(DataUtil.getFepIdString());
                commLog.setOperationCode(sd.getType());
                commLog.setSvcTypeCode(CommonConstants.getHeaderSvc(String.valueOf((char)sd.getSvc())));
                commLog.setSendBytes(SENDENQDATASIZE+responseServiceData.getTotalLength()); //ENQ+Response Service Data Frame
                commLog.setUncompressedSendBytes(SENDENQDATASIZE+responseServiceData.getUncompressedTotalLength());
                commLog.setRcvBytes(sd.getTotalLength()+RECVCONTROLDATASIZE);//included EOT that received
                commLog.setUnconPressedRcvBytes(sd.getUncompressedTotalLength()+RECVCONTROLDATASIZE);
                // commLog.setSuppliedId(supplierId+"");
                commLog.setCommResult(1);
                log.debug("startTime["+startTime+"] endTime["+endTime+"]");
                log.debug("startLongTime["+startLongTime+"] endLongTime["
                        +endLongTime+"]");
                if(endLongTime - startLongTime > 0) {
                    commLog.setTotalCommTime((int)(endLongTime - startLongTime));
                }
                else {
                    commLog.setTotalCommTime(0);
                }
                log.debug(getProtoName()+" "+commLog.toString());
                saveCommLog(commLog);
                startLongTime = endLongTime;
                startTime = endTime;
            }
            //------------------------------------------------
            // Measurement, Alarm, Event, File, New Measurement,
            // Partial, Data File Frame
            //------------------------------------------------
            else
            {
                endLongTime = System.currentTimeMillis();
                endTime = TimeUtil.getCurrentTime();
                /*
                if(sd.getType().equals("ServiceData.EventData"))
                {
                    String evtCode =
                        ((EventData)sd).getCode().toString();
                    if(evtCode.startsWith("255."))
                    {
                        log.debug(getProtoName()
                        +" Received Controll Event["+sd+"]");
                        return;
                    }
                    startTime =
                        ((EventData)sd).getTimeStamp().toString();
                }
                else if(sd.getType().equals("ServiceData.AlarmData"))
                {
                    startTime =
                        ((AlarmData)sd).getMcuTimeStamp().toString();
                } else
                {
                    startTime = endTime;
                }
                */

                commLog.setStartDateTime(startTime);
                commLog.setStartDate(startTime.substring(0,8));
                commLog.setStartTime(startTime.substring(8,14));
                commLog.setEndTime(endTime);
                commLog.setInterfaceCode(CommonConstants.getInterface(interfaceName));
                //commLog.setProtocolCode(CommonConstants.getProtocol(protocolType+""));
                //commLog.setSenderTypeCode(CommonConstants.getSenderReceiver(activatorType+""));
                commLog.setSenderId(sd.getMcuId());
                commLog.setReceiverTypeCode(CommonConstants.getSenderReceiver(targetType+""));
                commLog.setReceiverId(DataUtil.getFepIdString());
                commLog.setOperationCode(sd.getType());
                log.debug("svcType"+sd.getSvc());
                commLog.setSvcTypeCode(CommonConstants.getHeaderSvc(String.valueOf((char)sd.getSvc())));
                //commLog.setSendBytes(new Long(GeneralDataConstants.HEADER_LEN
                //            +GeneralDataConstants.TAIL_LEN+4));
                commLog.setSendBytes(SENDCONTROLDATASIZE); //ENQ+ACK
                commLog.setRcvBytes(sd.getTotalLength()+RECVCONTROLDATASIZE);//included EOT that received
                commLog.setUnconPressedRcvBytes(sd.getUncompressedTotalLength()+RECVCONTROLDATASIZE);
                //commLog.setSuppliedId(supplierId+"");
                commLog.setCommResult(1);

                if (sd instanceof MDData) {
                    MDData data = (MDData)sd;
                    commLog.setCnt(data.getCnt().getValue()+"");
                }
                else if (sd instanceof NDData) {
                    NDData data = (NDData)sd;
                    commLog.setCnt(data.getCnt().getValue()+"");
                }
                else if (sd instanceof RMDData){
                    RMDData data = (RMDData)sd;
                    commLog.setCnt(data.getCnt().getValue()+"");
                }
                /*
                 * must be calculate time of start that session is opened
                 * so, start time is set up in sessionOpened function
                if(!startTime.equals(endTime))
                {
                    long commTime = TimeUtil.getLongTime(endTime)
                        - TimeUtil.getLongTime(startTime);
                    commLog.setTotalCommTime(new Long(commTime));
                }
                */
                log.debug("startTime["+startTime+"] endTime["+endTime+"]");
                log.debug("startLongTime["+startLongTime+"] endLongTime["
                        +endLongTime+"]");
                if(endLongTime - startLongTime > 0) {
                    commLog.setTotalCommTime((int)(endLongTime - startLongTime));
                }
                else {
                    commLog.setTotalCommTime(0);
                }

                processing(session,sd);
                log.debug(getProtoName()+" "+commLog.toString());
                //commonLogger.sendCommLog(commLog);
                saveCommLog(commLog);

                startLongTime = endLongTime;
                startTime = endTime;
            }
        }
        else {
            log.debug(getProtoName()+ " ServiceData is null");
        }
    }

	private Class getActionClass(String clsName) throws Exception
    {
        try
        {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			if(cl == null) cl = getClass().getClassLoader();//fallback
			Class instance = cl.loadClass(clsName);
	    	return instance;

        }catch(ClassNotFoundException cnfe)
        {
            log.error("ClassNotFound : "+clsName);
            return null;
        }
    }

	/**
	 * @param oid
	 * @return
	 */
	private String getCmdActionName(String oid)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(pkgName);
		sb.append(".Cmd_");
		sb.append(oid.replaceAll("\\.","_"));
		sb.append("_Action");

		return sb.toString();
	}

	/**
	 * 요청으로 받은 커맨드 데이타의 OID에 따라 실행할 액션 코드 파일을 찾아서 execute 해주고
	 * 그 결과를 ServiceData 형태로 돌려 받는다.
	 * @param session
	 * @param receiveSd
	 * @return
	 * @throws Exception
	 */
	private ServiceData cmdExecute(IoSession session, ServiceData receiveSd) throws Exception {
		ServiceData responseSd = null;
		String oidStr = ((CommandData)receiveSd).getCmd().value;
        String clsName = "";
        Class cls = null;

        if( oidStr != null)
        {
            clsName = getCmdActionName(oidStr);
            cls = getActionClass(clsName);
        }

        // OID에 해당하는 Action을 찾아서 execute 하고 결과를 받아옴
        if(cls != null)
        {
            Object obj = DataUtil.getBean(cls); // cls.newInstance();
            Method method = cls.getDeclaredMethod("execute",
                    new Class[] {
                    receiveSd.getClass() });
            Object responceObj = method.invoke(obj,new Object[] { receiveSd } );
            if(responceObj instanceof ServiceData) {
            	responseSd = (ServiceData)responceObj;
            }
        }else {
        	log.error(clsName + "File Dose not Exist!!");
        	responseSd = receiveSd;
        }
        return responseSd;
	}

    /**
     * inherited method from ProtocolHandlerAdapter
     * handling GeneralDataFrame
     *
     * @param session <code>ProtocolSession</code> session
     * @param message <code>Object</code> decoded GeneralDataFrame
     */
    public void messageReceived(IoSession session, Object message )
    {
        try
        {
            log.info("###### Message [ " + message.getClass().getName() + "]");
            if(message instanceof GeneralDataFrame) {
                GeneralDataFrame frame = (GeneralDataFrame)message;
                if(frame instanceof ServiceDataFrame) {
                    receivedServiceDataFrame(session,
                            (ServiceDataFrame)frame);
                }
                else if(frame instanceof ControlDataFrame) {
                    receivedControlDataFrame(session,
                            (ControlDataFrame)frame);
                }
            }
            else {
                log.warn("Message is Unknown Frame!!!");
            }
        }catch(Exception ex)
        {
            log.error(getProtoName()
                    + " FMPProtocolHandler::messageReceived "
                    + " failed" ,ex);
        }
    }

    /*
     * Save Request Frame
     * @see org.apache.mina.core.service.IoHandlerAdapter#messageSent(org.apache.mina.core.session.IoSession, java.lang.Object)
     */
    public void messageSent(IoSession session, Object message) throws Exception {
        log.debug("[Start] MessageSent");
        try
        {
        	if(message instanceof GeneralDataFrame) {
                log.debug("[Send] GeneralDataFrame");
            } 
        }catch(Exception ex)
        {
            log.error(getProtoName()
                    + " FMPProtocolHandler::MessageSent "
                    + " failed" ,ex);
        }
        log.debug("[End] MessageSent");
    }

    /**
     * inherited method from ProtocolHandlerAdapter
     */
    public void sessionIdle(IoSession session, IdleStatus status)
    throws Exception
    {
        log.debug(getProtoName()+" IDLE COUNT : "
                + session.getIdleCount(IdleStatus.READER_IDLE));
        if(session.getIdleCount(IdleStatus.READER_IDLE) >= retry)
        {
            session.write(FrameUtil.getEOT());
            FrameUtil.waitAfterSendFrame();
            session.closeNow();
        }
    }

    /**
     * inherited method from ProtocolHandlerAdapter
     */
    public void sessionOpened(IoSession session)
    {
        log.info("sessionOpened : "
                + session.getRemoteAddress());
        startLongTime = System.currentTimeMillis();
        try {
            startTime = TimeUtil.getCurrentTime();
        }
        catch (ParseException e) {
            log.warn(e);
        }
        log.debug("send ENQ");
        session.write(new ControlDataFrame(
                    ControlDataConstants.CODE_ENQ));

        session.getConfig().setIdleTime(IdleStatus.READER_IDLE,
                idleTime);
        this.ack = null;
    }

    public void sessionClosed(IoSession session)
    {
        synchronized(ackMonitor)
        {
            ackMonitor.notify();
        }
        session.removeAttribute("");
        session.removeAttribute("sendframes");
        processingMeasurementData(session);
        processingNewMeasurementData(session);
        log.info(getProtoName()+" Session Closed : "
                + session.getRemoteAddress());
    }

    /**
     * wait ACK ControlDataFrame
     *
     * @return ack <code>ControlDataFrame</code> ack
     */
    public ControlDataFrame waitAck()
    {
        synchronized(ackMonitor)
        {
            try {
                //log.debug("ACK Wait");
                ackMonitor.wait(500);
                //log.debug("ACK Received");
            } catch(InterruptedException ie) {ie.printStackTrace();}
        }
        return ack;
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
     * @param session <code>ProtocolSession</code> session
     * @param sequence <code>int</code> wait ack sequence
     */
    public void waitAck(IoSession session, int sequence)
        throws Exception
    {
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
                int ackseq = FrameUtil.getAckSequence(ack);
                if(sequence == ackseq)
                {
                    setAck(null);
                    break;
                }
                else {
                    setAck(null);
                }
            }
        }
    }

    private void saveCommLog(CommLog obj)
    {
        String serviceType = ProcessorHandler.LOG_COMMLOG;
        try {
            putServiceData(serviceType, obj);
        }
        catch (Exception e) {
            log.error(e);
        }
    }

    /**
     * process service data
     */
    @SuppressWarnings("unchecked")
    private void processing(IoSession session,ServiceData sd)
    {
        processing(sd);
    }

    private void saveSlideWindow(byte[] bx) {
        FileOutputStream fos = null;
        ProcessorHandler handler = null;
        String serviceType = ProcessorHandler.SERVICE_DATAFILEDATA;

        String fileName = null;
        try {
            int compressTypeCode = DataUtil.getIntToByte(bx[0]);
            COMPRESSTYPE compressType = SlideWindow.getCompressType(compressTypeCode);
            fileName = getFileName(compressType.getName());

            fos = new FileOutputStream(fileName);

            int off = 0;
            // 압축을 하지 않은 것은 압축유형(1), 길이(4)를 제거하고 생성한다.
            if (compressType == COMPRESSTYPE.DAT)
                off = 5;

            fos.write(bx, off, bx.length-off);
        }
        catch (Exception ex) {
            log.error(ex, ex);
        }
        finally {
            if (fos != null) {
                try {
                    fos.close();
                }
                catch (Exception e) {}
            }
            try {
                if (fileName != null)
                    putServiceData(serviceType, fileName);
            }
            catch (Exception e) {
                log.warn(e.getMessage());
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

    /**
     * process the service data
     */
    private void processing(ServiceData sd)
    {
        String serviceType = FMPProperty.getProperty(sd.getType());
        try {
            putServiceData(serviceType, sd);
        }
        catch (Exception e) {
            log.error(e);
        }
    }

    /**
     * process Measurement Data after session closed
     */
    @SuppressWarnings("unchecked")
    private void processingMeasurementData(IoSession session)
    {
        Object tmpObj = session.getAttribute("MD");
        if(tmpObj == null) {
            return;
        }
        ArrayList<MDData> al = (ArrayList<MDData>)tmpObj;
        MDData[] mdDatas = al.toArray(new MDData[al.size()]);

        int cnt = 0;
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        byte[] bx;
        MDData mdData = new MDData();
        String mcuId = null;
        for(int i = 0 ; i < mdDatas.length; i++)
        {
            mcuId = mdDatas[i].getMcuId();
            if(cnt + mdDatas[i].getCnt().getValue() > 65535)
            {
                mdData = new MDData();
                mdData.setMcuId(mcuId);
                mdData.setCnt(new WORD(cnt));
                mdData.setMdData(bao.toByteArray());
                processing(mdData);
                cnt = 0;
                bao = new ByteArrayOutputStream();
            }
            cnt+=mdDatas[i].getCnt().getValue();
            bx = mdDatas[i].getMdData();
            bao.write(bx,0,bx.length);
        }

        if(cnt > 0)
        {
            mdData = new MDData();
            mdData.setMcuId(mcuId);
            mdData.setCnt(new WORD(cnt));
            mdData.setMdData(bao.toByteArray());
            processing(mdData);
        }

        bao = null;
        session.removeAttribute("MD");
    }

    /**
     * process New Measurement Data after session closed
     */
    @SuppressWarnings("unchecked")
    private void processingNewMeasurementData(IoSession session)
    {
        Object tmpObj = session.getAttribute("ND");
        if(tmpObj == null) {
            return;
        }
        ArrayList<NDData> al = (ArrayList<NDData>)tmpObj;
        NDData[] ndDatas = al.toArray(new NDData[al.size()]);

        int cnt = 0;
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        byte[] bx;
        NDData ndData = new NDData();
        String mcuId = null;
        for(int i = 0 ; i < ndDatas.length; i++)
        {
            mcuId = ndDatas[i].getMcuId();
            if(cnt + ndDatas[i].getCnt().getValue() > 65535)
            {
                ndData = new NDData();
                ndData.setMcuId(mcuId);
                ndData.setCnt(new WORD(cnt));
                ndData.setMdData(bao.toByteArray());
                processing(ndData);
                cnt = 0;
                bao = new ByteArrayOutputStream();
            }
            cnt+=ndDatas[i].getCnt().getValue();
            bx = ndDatas[i].getMdData();
            bao.write(bx,0,bx.length);
        }

        if(cnt > 0)
        {
            ndData = new NDData();
            ndData.setMcuId(mcuId);
            ndData.setCnt(new WORD(cnt));
            ndData.setMdData(bao.toByteArray());
            processing(ndData);
        }

        bao = null;
        session.removeAttribute("ND");
    }

    /**
     * set Protocol Type(1:CDMA,2:GSM,3:GPRS,4:PSTN,5:LAN)
     * @param protocolType <code>Integer</code> Protocol Type
     */
    public void setProtocolType(Integer protocolType)
    {
        this.protocolType = protocolType;
    }

    /**
     * get Protocol Type(1:CDMA,2:GSM,3:GPRS,4:PSTN,5:LAN)
     * @return protocolType <code>Integer</code> Protocol Type
     */
    public Integer getProtocolType()
    {
        return this.protocolType;
    }

    /**
     * get Protocol Type String(1:CDMA,2:GSM,3:GPRS,4:PSTN,5:LAN)
     * @return protocolType <code>String</code> Protocol Type
     */
    public String getProtoName()
    {
        int proto = this.protocolType.intValue();
        Code code = CommonConstants.getProtocol(proto+"");
        return "[" + code.getName() + "]";
    }
}
