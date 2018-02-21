package com.aimir.fep.protocol.fmp.server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.Interface;
import com.aimir.constants.CommonConstants.ThresholdName;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.fep.meter.parser.plc.PLCData;
import com.aimir.fep.meter.parser.plc.PLCDataConstants;
import com.aimir.fep.meter.parser.plc.PLCDataFrame;
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
import com.aimir.fep.protocol.fmp.frame.service.DFData;
import com.aimir.fep.protocol.fmp.frame.service.MDData;
import com.aimir.fep.protocol.fmp.frame.service.NDData;
import com.aimir.fep.protocol.fmp.frame.service.RMDData;
import com.aimir.fep.protocol.fmp.frame.service.ServiceData;
import com.aimir.fep.protocol.fmp.log.MDLogger;
import com.aimir.fep.protocol.fmp.processor.ProcessorHandler;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.FrameUtil;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.Message;
import com.aimir.model.device.CommLog;
import com.aimir.model.system.Code;
import com.aimir.util.TimeUtil;
import com.aimir.fep.util.threshold.CheckThreshold;

/**
 * {@link FMPProtocolHandler} implementation of FEP FMP(AiMiR and MCU Protocol).
 *
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
@Component
public class FMPProtocolHandler extends IoHandlerAdapter
{
    private static Log log = LogFactory.getLog(FMPProtocolHandler.class);

    private Object ackMonitor = new Object();
    private ControlDataFrame ack = null;

    private int enqTimeout = Integer.parseInt(FMPProperty.getProperty("protocol.enq.timeout", "10"));
    private int idleTime = Integer.parseInt(FMPProperty.getProperty(
                "protocol.idle.time","5"));
    private int ackTimeout = Integer.parseInt(
            FMPProperty.getProperty("protocol.ack.timeout","3"));
    private CommLog commLog = new CommLog();
    private String startTime = null;
    private String endTime = null;
    private long   startLongTime = 0;
    private long   endLongTime = 0;
    private Integer targetType = new Integer(
            FMPProperty.getProperty("protocol.system.FEP","1"));
    public static final int SENDCONTROLDATASIZE = 29; //ENQ+ACK
    public static final int SENDENQDATASIZE = 15; //ENQ Data Size
    public static final int RECVCONTROLDATASIZE = 14; //EOT
    private Integer protocolType = new Integer(FMPProperty.getProperty("protocol.type.default","3"));

    private PLCDataFrame previousRequestPLC;//Previous Request PLC Frame

    private static String pkgName = "com.aimir.fep.command.response.action";
    
    @Autowired
    private ProcessorHandler processorHandler;
    
    private void putServiceData(String serviceType, Serializable data) {
        try {
            processorHandler.putServiceData(serviceType, data);
        }
        catch (Exception e) {
            log.error(e, e);
        }
    }
    
    /**
     * @return the previousRequestPLC
     */
    public PLCDataFrame getPreviousRequestPLC() {
        return previousRequestPLC;
    }

    /**
     * @param previousRequestPLC the previousRequestPLC to set
     */
    public void setPreviousRequestPLC(PLCDataFrame previousRequestPLC) {
        this.previousRequestPLC = previousRequestPLC;
    }

    /**
     * inherited method from ProtocolHandlerAdapter
     */
    public void exceptionCaught(IoSession session,
            Throwable cause )
    {
        log.debug(cause.getMessage());
        try {
            if (cause.getMessage().contains("SSL handshake failed")) {
                // Security Alarm
                String activatorId = session.getRemoteAddress().toString();
                if (activatorId.contains("/") && activatorId.contains(":"))
                    activatorId = activatorId.substring(activatorId.indexOf("/")+1, activatorId.indexOf(":"));
                
                try {
                    EventUtil.sendEvent("Security Alarm",
                            TargetClass.Unknown, activatorId,
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
                
            }
            else {
                // INSERT START SP-193
                CheckThreshold.updateCount(session.getRemoteAddress().toString(), ThresholdName.INVALID_PACKET);
                // INSERT END SP-193        
            }
            // Close connection when unexpected exception is caught.
            log.error(cause, cause);
            log.error(cause+"  from "+session.getRemoteAddress());
            // session.write(FrameUtil.getEOT());
            // FrameUtil.waitAfterSendFrame();
        }
        finally {
            session.closeOnFlush();
        }
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
            if(args != null){
                log.info("NEG[" + Hex.decode(args) + "]");
            }

            // enq 버전이 1.2 인 경우 frame size와 window size를 session에 저장한다.
            String version = FMPProperty.getProperty("protocol.version");
            log.info("version[" + version+"]");
            
            if ((args[0] == 0x01 && args[1] == 0x02) || (args[0] == 0x01 && args[1] == 0x03)) {
                int frameMaxLen = DataUtil.getIntTo2Byte(new byte[] {args[3], args[2]});
                int frameWinSize = DataUtil.getIntToByte(args[4]);
                String nameSpace = new String(DataUtil.select(args, 5, 2));
                session.setAttribute("frameMaxLen", frameMaxLen);
                session.setAttribute("frameWinSize", frameWinSize);
                session.setAttribute("nameSpace", nameSpace);
                log.info("NEG V["+version+"] Frame Size[" + frameMaxLen + "] Window Size[" + frameWinSize + "] NameSpace["+nameSpace+"]");
                session.write(FrameUtil.getNEGR());
                //byte[] protocolVersion = new byte[2];
                //protocolVersion[0] = args[0];
                //protocolVersion[1] = args[1];
                
                //session.write(FrameUtil.getNEGR(protocolVersion));
            }
            else {
                byte[] protocolVersion = new byte[2];
                protocolVersion[0] = args[0];
                protocolVersion[1] = args[1];
                session.setAttribute("frameMaxLen", GeneralDataConstants.FRAME_MAX_LEN);
                session.setAttribute("frameWinSize", GeneralDataConstants.FRAME_WINSIZE);
                ControlDataFrame negrFrame = FrameUtil.getNEGR();
                //ControlDataFrame negrFrame = FrameUtil.getNEGR(protocolVersion);
                negrFrame.setArg(new OCTET(new byte[]{ControlDataConstants.NEG_R_UNSUPPORTED_VERSION}));
                session.write(negrFrame);
                Thread.sleep(1000);
                session.closeNow();
            }            
        }
        else if(code == ControlDataConstants.CODE_NEGR){
            log.debug(getProtoName()+" NEG_R Received ");
            byte[] args = cdf.getArg().getValue();
            
            if(args[0] == ControlDataConstants.NEG_R_NOERROR){
                log.debug(getProtoName()+" NEG_R_NOERROR ");
            }else if(args[0] == ControlDataConstants.NEG_R_UNSUPPORTED_VERSION){
                log.debug(getProtoName()+" NEG_R_UNSUPPORTED_VERSION ");
                session.closeNow();
            }else if(args[0] == ControlDataConstants.NEG_R_UNKNOWN_NAMESPACE){
                log.debug(getProtoName()+" NEG_R_UNKNOWN_NAMESPACE ");
                session.closeNow();
            }else if(args[0] == ControlDataConstants.NEG_R_INVALID_SIZE){
                log.debug(getProtoName()+" NEG_R_INVALID_SIZE ");
                session.closeNow();
            }else if(args[0] == ControlDataConstants.NEG_R_INVALID_COUNT){
                log.debug(getProtoName()+" NEG_R_INVALID_COUNT ");
                session.closeNow();
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
                            boolean kafkaEnable = Boolean.parseBoolean(FMPProperty.getProperty("kafka.enable"));
                            String filename = saveSlideWindow(data);
                            if (kafkaEnable) {
                                sendCommLog(session, data.length, cdf.getLength().getValue(),
                                        ProcessorHandler.SERVICE_DATAFILEDATA, filename);
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
                                commLog.setOperationCode(ProcessorHandler.SERVICE_DATAFILEDATA);
                                commLog.setSvcTypeCode(CommonConstants.getHeaderSvc("P"));
                                commLog.setSendBytes(SENDENQDATASIZE+cdf.getLength().getValue()); //ENQ+Response Service Data Frame
                                commLog.setUncompressedSendBytes(SENDENQDATASIZE+cdf.getLength().getValue());
                                commLog.setRcvBytes(data.length+RECVCONTROLDATASIZE);//included EOT that received
                                commLog.setUnconPressedRcvBytes(data.length+RECVCONTROLDATASIZE);
                                commLog.setReceiver(System.getProperty("fepName"));
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
                            }
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
    	String nameSpace = (String)session.getAttribute("nameSpace");
        ServiceData sd = null;
        String ipaddr = session.getRemoteAddress().toString();
        ipaddr = ipaddr.substring(ipaddr.indexOf("/")+1, ipaddr.lastIndexOf(":")).toUpperCase();
        sd = ServiceData.decode(nameSpace, sdf, ipaddr);
        if(sd != null)
        {
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
                commLog.setReceiver(System.getProperty("fepName"));
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
                commLog.setReceiver(System.getProperty("fepName"));
                //commLog.setSuppliedId(supplierId+"");
                commLog.setCommResult(1);

                if (sd instanceof MDData) {
                    MDData data = (MDData)sd;
                    data.setIpAddr(session.getRemoteAddress().toString());
                    commLog.setTotalMeasumentDataCnt(data.getCnt().getValue());
                    commLog.setCnt(data.getCnt().getValue()+"");
                    
                    //log.debug("the MDData before into the queue => " + data.toString());
                    log.debug("MDData - NameSpace => " + data.getNS());
                    log.debug("MDData - MCU => " + data.getMcuId());
                    log.debug("MDData - ipaddr => " + data.getIpAddr());
                }
                else if (sd instanceof NDData) {
                    NDData data = (NDData)sd;
                    commLog.setTotalMeasumentDataCnt(data.getCnt().getValue());
                    commLog.setCnt(data.getCnt().getValue()+"");
                }
                else if (sd instanceof RMDData){
                    RMDData data = (RMDData)sd;
                    commLog.setTotalMeasumentDataCnt(data.getCnt().getValue());
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

	private Class<?> getActionClass(String clsName) throws Exception
    {
        try
        {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			if(cl == null) cl = getClass().getClassLoader();//fallback
			Class<?> instance = cl.loadClass(clsName);
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
	private String getCmdActionName(String oid, String nameSpace)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(pkgName);
		sb.append(".Cmd_");
		if (nameSpace != null && !"".equals(nameSpace))
		    sb.append(nameSpace + "_");
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
        Class<?> cls = null;

        if( oidStr != null)
        {
            clsName = getCmdActionName(oidStr, (String)session.getAttribute("nameSpace"));
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

	@Test
	public void test() {
		FMPProtocolHandler fmp = new FMPProtocolHandler();
		IoSession session = null;
		ServiceData sd = null;
		try {
			fmp.cmdExecute(session, sd);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(fmp.getCmdActionName("100.1.0", null));
	}

    /**
     * @param session
     * @param pdf
     * @throws Exception
     */
    private void receivedPLCDataFrame(IoSession session, PLCDataFrame pdf)
    throws Exception
    {
        PLCData pd = null;
        String ipaddr = session.getRemoteAddress().toString();
        ipaddr = ipaddr.substring(ipaddr.indexOf("/")+1, ipaddr.indexOf(":"));
        pd = PLCData.decode(pdf, ipaddr);

        if(pd != null)
        {
            log.debug(getProtoName()+ " ServiceData :"
                    + pd.getType() );
            log.debug(getProtoName()+" RECEIVED SERVICE DATA \n"+pd);


            endLongTime = System.currentTimeMillis();
            endTime = TimeUtil.getCurrentTime();

            commLog.setStartDateTime(startTime);
            commLog.setStartDate(startTime.substring(0,8));
            commLog.setStartTime(startTime.substring(8,14));
            commLog.setEndTime(endTime);
            //TODO Check PLC Interface
            //commLog.setProtocolCode(CommonConstants.getProtocol(protocolType+""));
            commLog.setInterfaceCode(CommonConstants.getInterface(Interface.IF4.getName()));
            //commLog.setSenderTypeCode(CommonConstants.getSenderReceiver(activatorType+""));
            commLog.setSenderId(pd.getSId().toString());
            commLog.setReceiverTypeCode(CommonConstants.getSenderReceiver(targetType+""));
            commLog.setReceiverId(DataUtil.getFepIdString());
            commLog.setOperationCode(pd.getType());
            commLog.setSvcTypeCode(CommonConstants.getDataSvc(pd.getServiceType()+""));
            commLog.setSendBytes(PLCDataConstants.ACK_FRAME_TOTAL_LEN); //ACK
            commLog.setRcvBytes(pd.getTotalLength());//included EOT that received
            commLog.setUnconPressedRcvBytes(pd.getUncompressedTotalLength());
            commLog.setReceiver(System.getProperty("fepName"));
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

            processingPLC(session,pd);
            //log.debug(getProtoName()+" "+commLog.toString());
            //commonLogger.sendCommLog(commLog);
            //saveCommLog(commLog);

            startLongTime = endLongTime;
            startTime = endTime;
        }
        else {
            log.debug(getProtoName()+ " ServiceData is null");
        }
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
            boolean kafkaEnable = Boolean.parseBoolean(FMPProperty.getProperty("kafka.enable"));
            if(message instanceof PLCDataFrame) {
                if (kafkaEnable)
                    sendCommLog(session, (PLCDataFrame)message);
                else
                    receivedPLCDataFrame(session, (PLCDataFrame)message);
            }
            else if(message instanceof GeneralDataFrame) {
                GeneralDataFrame frame = (GeneralDataFrame)message;
                if(frame instanceof ServiceDataFrame) {
                    if (kafkaEnable && frame.getSvc() != 'C')
                        sendCommLog(session, (ServiceDataFrame)frame);
                    else
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
                throw new Exception("Message is Unknown Frame!!!");
            }
        }catch(Exception ex)
        {
            log.error(getProtoName()
                    + " FMPProtocolHandler::messageReceived "
                    + " failed" ,ex);
            // INSERT START SP-193
            CheckThreshold.updateCount(session.getRemoteAddress().toString(), ThresholdName.INVALID_PACKET);
            // INSERT END SP-193
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
            if(message instanceof PLCDataFrame) {
                log.debug("[Send] PLCDataFrame");
                if(((PLCDataFrame) message).getProtocolDirection()==PLCDataConstants.PROTOCOL_DIRECTION_FEP_IRM) {
                    previousRequestPLC=(PLCDataFrame) message;
                }
            }
            else if(message instanceof GeneralDataFrame) {
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
        session.closeNow();
        /*
        if(session.getIdleCount(IdleStatus.READER_IDLE) >= retry)
        {
            session.write(FrameUtil.getEOT());
            FrameUtil.waitAfterSendFrame();
            session.closeNow();
        }
        */
    }

    /**
     * inherited method from ProtocolHandlerAdapter
     */
    public void sessionOpened(IoSession session)
    {
		// 로그 확인 편하도록....
		log.info("    ");
		log.info("    ");
		log.info("############################## Logging Start ~!! ################################################");		
        log.info("sessionOpened : " + session.getRemoteAddress() + ", writeIdleTime : " + session.getLastWriterIdleTime());
        startLongTime = System.currentTimeMillis();
        try {
            startTime = TimeUtil.getCurrentTime();
        }
        catch (ParseException e) {
            log.warn(e);
        }
        
        session.getConfig().setWriteTimeout(enqTimeout);
        // session.getConfig().setIdleTime(IdleStatus.READER_IDLE,
        //        idleTime);
        session.getConfig().setBothIdleTime(idleTime);
        
        log.debug("send ENQ");
        session.write(new ControlDataFrame(
                ControlDataConstants.CODE_ENQ));
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
        //log.debug("Session Closed");
        log.info("### Bye Bye ~ " + getProtoName()+" Session Closed : " + session.getRemoteAddress());
        log.info("   ");
        session.closeOnFlush();
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
    private void processing(IoSession session,ServiceData sd)
    {
        processing(sd);
    }

    /**
     * process plc data
     */
    private void processingPLC(IoSession session,PLCData pd)
    {
        processingPLC(pd);
    }

    private String saveSlideWindow(byte[] bx) {
        FileOutputStream fos = null;
        String serviceType = ProcessorHandler.SERVICE_DATAFILEDATA;

        String filename = null;
        try {
            int compressTypeCode = DataUtil.getIntToByte(bx[0]);
            COMPRESSTYPE compressType = SlideWindow.getCompressType(compressTypeCode);
            filename = getFileName(compressType.getName());

            fos = new FileOutputStream(filename);

            int off = 0;
            // 압축을 하지 않은 것은 압축유형(1), 길이(4)를 제거하고 생성한다.
            if (compressType == COMPRESSTYPE.DAT)
                off = 5;

            log.debug("CompressType:"+compressType.getName()+" compress code=["+compressTypeCode+"]");
            log.debug("Compress Header:"+Hex.decode(DataUtil.select(bx, 0, 13)));
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
                if (filename != null && !Boolean.parseBoolean(FMPProperty.getProperty("kafka.enable")))
                    putServiceData(serviceType, filename);
            }
            catch (Exception e) {
                log.warn(e.getMessage());
            }
        }
        return filename;
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
            fileName = file.getAbsolutePath() + File.separator + UUID.randomUUID() + "." + compressType;
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
            log.error(e, e);
        }
    }

    /**
     * process the service data
     * @param pd
     */
    private void processingPLC(PLCData pd)
    {
        String serviceType = pd.getType();
        try {
            putServiceData(serviceType, pd);
        }
        catch (Exception e) {
            log.error(e, e);
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
    
    private void sendCommLog(IoSession session, ServiceDataFrame sdf) throws Exception {
        String nameSpace = (String)session.getAttribute("nameSpace");
        String ipaddr = session.getRemoteAddress().toString();
        ipaddr = ipaddr.substring(ipaddr.indexOf("/")+1, ipaddr.lastIndexOf(":"));
        
        endLongTime = System.currentTimeMillis();
        endTime = TimeUtil.getCurrentTime();

        //통신 로그 저장 로직
        Message commLog = new Message();
        commLog.setNameSpace(nameSpace == null? "":nameSpace);
        commLog.setData(sdf.encode());
        commLog.setDataType(ProcessorHandler.SERVICE_DATA);
        commLog.setSenderIp(ipaddr);
        commLog.setSenderId(sdf.getMcuId().toString());
        commLog.setReceiverId(DataUtil.getFepIdString());
        commLog.setSendBytes(SENDCONTROLDATASIZE); //ENQ+ACK
        commLog.setRcvBytes(sdf.getRcvBodyLength()+RECVCONTROLDATASIZE);//included EOT that received
        commLog.setStartDateTime(startTime);//Start Time은 sessionOpen 시 지정
        commLog.setEndDateTime(endTime);
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
        
        String serviceType = null;
        if(sdf.getSvc() == GeneralDataConstants.SVC_M ||
                sdf.getSvc() == GeneralDataConstants.SVC_S ||
                sdf.getSvc() == GeneralDataConstants.SVC_G)
        {
            serviceType = ProcessorHandler.SERVICE_MEASUREMENTDATA;
            ServiceData sdata = ServiceData.decode(commLog.getNameSpace(), sdf, commLog.getSenderId());
            if (sdata instanceof MDData) {
                MDLogger mdlog = new MDLogger();
                String filename = mdlog.writeObject(sdata);
                commLog.setFilename(filename);
            }
        }
        else if(sdf.getSvc() == GeneralDataConstants.SVC_E)
        {
            String ns = (String)session.getAttribute("nameSpace");
            serviceType = ProcessorHandler.SERVICE_EVENT;
            if(ns != null && !"".equals(ns)){
                serviceType = ProcessorHandler.SERVICE_EVENT_1_2;
            }
        }
        else if(sdf.getSvc() == GeneralDataConstants.SVC_B)
        {
            serviceType = ProcessorHandler.SERVICE_EVENT_1_2;
        }
        else if(sdf.getSvc() == GeneralDataConstants.SVC_D)
        {
            serviceType = ProcessorHandler.SERVICE_DATAFILEDATA;
            ServiceData sdata = ServiceData.decode(commLog.getNameSpace(), sdf, commLog.getSenderId());
            if (sdata instanceof DFData) {
                MDLogger mdlog = new MDLogger();
                String filename = mdlog.writeObject(sdata);
                commLog.setFilename(filename);
            }
        }
        saveCommLog(serviceType, commLog);
        startLongTime = endLongTime;
        startTime = endTime;
    }
    
    private void sendCommLog(IoSession session, PLCDataFrame frame) throws Exception {
        String nameSpace = (String)session.getAttribute("nameSpace");
        String ipaddr = session.getRemoteAddress().toString();
        ipaddr = ipaddr.substring(ipaddr.indexOf("/")+1, ipaddr.lastIndexOf(":"));
        
        endLongTime = System.currentTimeMillis();
        endTime = TimeUtil.getCurrentTime();

        //통신 로그 저장 로직
        Message commLog = new Message();
        commLog.setNameSpace(nameSpace == null? "":nameSpace);
        commLog.setData(frame.encode());
        commLog.setDataType(ProcessorHandler.SERVICE_PLC);
        commLog.setSenderId(frame.getSId());
        commLog.setSenderIp(ipaddr);
        commLog.setSenderId(frame.getSId().toString());
        commLog.setReceiverId(DataUtil.getFepIdString());
        commLog.setSendBytes(SENDCONTROLDATASIZE); //ENQ+ACK
        commLog.setRcvBytes(frame.getData().length + RECVCONTROLDATASIZE);//included EOT that received
        commLog.setStartDateTime(startTime);//Start Time은 sessionOpen 시 지정
        commLog.setEndDateTime(endTime);
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
        saveCommLog(ProcessorHandler.SERVICE_PLC, commLog);
        startLongTime = endLongTime;
        startTime = endTime;
    }
    
    private void sendCommLog(IoSession session, int rcvBytes, int sendBytes,
            String serviceType, String filename) throws Exception {
        String nameSpace = (String)session.getAttribute("nameSpace");
        String ipaddr = session.getRemoteAddress().toString();
        ipaddr = ipaddr.substring(ipaddr.indexOf("/")+1, ipaddr.lastIndexOf(":"));
        
        //통신 로그 저장 로직
        startLongTime = (Long)session.getAttribute("startLongTime");
        endTime = TimeUtil.getCurrentTime();
        endLongTime = System.currentTimeMillis();
        
        Message commLog = new Message();
        commLog.setNameSpace(nameSpace == null? "":nameSpace);
        commLog.setDataType(serviceType);
        commLog.setSenderId(ipaddr);
        commLog.setSenderIp(ipaddr);
        commLog.setReceiverId(DataUtil.getFepIdString());
        commLog.setSendBytes(SENDENQDATASIZE+sendBytes); //ENQ+Response Service Data Frame
        commLog.setRcvBytes(rcvBytes+RECVCONTROLDATASIZE);//included EOT that received
        commLog.setStartDateTime((String)session.getAttribute("startTime"));//Start Time은 sessionOpen 시 지정
        commLog.setEndDateTime(endTime);
        commLog.setFilename(filename);
        log.debug("startTime["+startTime+"] endTime["+endTime+"]");
        log.debug("startLongTime["+startLongTime+"] endLongTime["
                +endLongTime+"]");
        if(endLongTime - startLongTime > 0) {
            commLog.setTotalCommTime((int)(endLongTime - startLongTime));
        }
        else {
            commLog.setTotalCommTime(0);
        }
        saveCommLog(serviceType, commLog);
        session.removeAttribute("startLongTime");
        session.removeAttribute("startTime");
    }
    
    private void saveCommLog(String serviceType, Message obj)
    {
        try {
            putServiceData(serviceType, (Serializable)obj);
        }
        catch (Exception e) {
            log.error(e);
        }
    }
}
