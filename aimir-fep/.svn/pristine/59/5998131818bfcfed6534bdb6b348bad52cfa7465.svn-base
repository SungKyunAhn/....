package com.aimir.fep.bypass;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.aimir.fep.bypass.actions.CommandAction;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.datatype.UINT;
import com.aimir.fep.protocol.fmp.exception.FMPACKTimeoutException;
import com.aimir.fep.protocol.fmp.frame.ControlDataConstants;
import com.aimir.fep.protocol.fmp.frame.ControlDataFrame;
import com.aimir.fep.protocol.fmp.frame.GeneralDataConstants;
import com.aimir.fep.protocol.fmp.frame.ServiceDataConstants;
import com.aimir.fep.protocol.fmp.frame.ServiceDataFrame;
import com.aimir.fep.protocol.fmp.frame.service.AlarmData;
import com.aimir.fep.protocol.fmp.frame.service.CommandData;
import com.aimir.fep.protocol.fmp.frame.service.DFData;
import com.aimir.fep.protocol.fmp.frame.service.EventData;
import com.aimir.fep.protocol.fmp.frame.service.EventData_1_2;
import com.aimir.fep.protocol.fmp.frame.service.MDData;
import com.aimir.fep.protocol.fmp.frame.service.NDData;
import com.aimir.fep.protocol.fmp.frame.service.RMDData;
import com.aimir.fep.protocol.fmp.frame.service.ServiceData;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.FrameUtil;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.MIBUtil;
import com.aimir.model.device.AsyncCommandParam;

/**
 * Bypass Port 통신 프로토콜 구현.<br>
 * <p>Test Receive Packet</p>
 * 05 01 00 FF <br>
 * [EOT:1byte][LEN:2byte][MODEM_SERIAL:LEN]<br>
 * @author elevas
 */
public class BypassHandler implements IoHandler {
    private static Log log = LogFactory.getLog(BypassHandler.class);
    private final int IDLE_TIME = Integer.parseInt(FMPProperty.getProperty(
            "protocol.idle.time","5"));
    private final int BYPASS_WAIT_TIME = Integer.parseInt(FMPProperty.getProperty(
            "protocol.bypass.waittime","30")) ;
    private int ackTimeout = Integer.parseInt(
            FMPProperty.getProperty("protocol.ack.timeout","3"));
    
    private Object ackMonitor = new Object();
    private ControlDataFrame ack = null;
    private CommandAction action = null;
    
	private static final int IDLE_CNT = 2;
	
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
     * set ACK ControlDataFrame
     *
     * @param ack <code>ControlDataFrame</code> ack
     */
    public void setAck(ControlDataFrame ack)
    {
        this.ack = ack;
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
		// 로그 확인 편하도록....
		log.info("    ");
		log.info("    ");
		log.info("    ");
		log.info("############################## Logging Start ~!! ################################################");
		
    	SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    	Date d = new Date(session.getLastWriterIdleTime());
    	log.info("### sessionOpened : " + session.getRemoteAddress() + ", lastWriteIdleTime : " + sf.format(d));
		
        
        session.setAttribute(session.getRemoteAddress(), new BypassDevice());
        session.getConfig().setWriteTimeout(BYPASS_WAIT_TIME);
        session.getConfig().setIdleTime(IdleStatus.READER_IDLE, BYPASS_WAIT_TIME);
        
        session.write(new ControlDataFrame(ControlDataConstants.CODE_ENQ));
        this.ack = null;
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        log.info("### Bye Bye ~ Client session closed from " + session.getRemoteAddress().toString());
        session.removeAttribute(session.getRemoteAddress());
        session.closeNow();
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status)
            throws Exception {
		log.info("### Client session closed from " + session.getRemoteAddress().toString());
		session.removeAttribute(session.getRemoteAddress());
		session.closeNow();
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause)
            throws Exception {
        session.removeAttribute(session.getRemoteAddress());
        session.closeNow();
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        // TODO Auto-generated method stub
    }
    
    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
    	log.info("### [MESSAGE_RECEIVED] from=" + session.getRemoteAddress().toString() + "  SessionId=" +  session.getId() + " ###");
        
        if (message instanceof ControlDataFrame) {
            ControlDataFrame cdf = (ControlDataFrame)message;
            byte code = cdf.getCode();
            log.info("==> Control Frame Code=["+code+"]");
            if(code == ControlDataConstants.CODE_NEG)
            {
            	log.debug("CODE_NEG Received ");
                receiveNEG(session, cdf);   
            }
            else if (code == ControlDataConstants.CODE_EOT) {
                log.debug("CODE_EOT Received ");
                session.closeNow();
            }
        }
        else if (message instanceof ServiceDataFrame) {
            ServiceDataFrame sdf = (ServiceDataFrame)message;
            // 모뎀이나 미터 시리얼번호 응답이 오면 bypassService를 실행한다.
            String ns = (String)session.getAttribute("nameSpace");
            ServiceData sd = ServiceData.decode(ns, sdf, session.getRemoteAddress().toString());
            log.info("==> ServiceData Frame");
            if (sd instanceof CommandData) {  // cmdIdentifyDevice, cmdFactorySetting, cmdMeterTimeSync, cmdMeterTimeSync, 
            	log.debug("#### CommandData");
                try {
                    action = (CommandAction)Class.forName("com.aimir.fep.bypass.actions.CommandAction_"+ns).newInstance();
                    action.execute(session, (CommandData)sd);
                }
                catch (Exception e) {
                    log.error(e, e);
                }
            }else if(sd instanceof AlarmData){
            	log.debug("#### AlarmData");
            }else if(sd instanceof DFData){
            	log.debug("#### DFData");
            }else if(sd instanceof EventData_1_2){  //cmdReadModemConfiguration
            	log.debug("#### EventData_1_2");
            }else if(sd instanceof EventData){
            	log.debug("#### EventData");
            }else if(sd instanceof MDData){
            	log.debug("#### MDData");
            }else if(sd instanceof NDData){
            	log.debug("#### NDData");
            }else if(sd instanceof RMDData){
            	log.debug("#### RMDData");
            }
            
        }
        else if (message instanceof byte[]) {
            byte[] frame = (byte[])message;
            log.debug("BypassFrame[" + Hex.decode(frame) + "]");
            if (action == null) {
                String ns = (String)session.getAttribute("nameSpace");
                action = (CommandAction)Class.forName("com.aimir.fep.bypass.actions.CommandAction_"+ns).newInstance();
            }
            action.executeBypass(frame, session);
        }
    }
    
    /*
     * NEG 프레임을 수신하면 프레임 사이즈와 윈도우 개수를 저장하여 활용한다.
     * 바이패스 핸들러는 명령을 수행하기 위한 것으로 모뎀이 접속하면 어떤 명령을 수행하려 했는지
     * 비동기 내역에서 찾아야 하는데 이때 접속한 모뎀의 시리얼 번호를 먼저 가져와야 한다.
     */
    private void receiveNEG(IoSession session, ControlDataFrame cdf) 
    throws Exception
    {
        byte[] args = cdf.getArg().getValue();
        if(args != null){
            log.info("NEG[" + Hex.decode(args) + "]");
        }

        // enq 버전이 1.2 인 경우 frame size와 window size를 session에 저장한다.
        if (args[0] == 0x01 && args[1] == 0x02) {
            int frameMaxLen = DataUtil.getIntTo2Byte(new byte[] {args[3], args[2]});
            int frameWinSize = DataUtil.getIntToByte(args[4]);
            String nameSpace = new String(DataUtil.select(args, 5, 2));
            session.setAttribute("frameMaxLen", frameMaxLen);
            session.setAttribute("frameWinSize", frameWinSize);
            session.setAttribute("nameSpace", nameSpace);
            log.info("NEG V1.2 Frame Size[" + frameMaxLen + "] Window Size[" + frameWinSize + "] NameSpace["+nameSpace+"]");
            
            // NEG에 대한 응답을 보내고
            session.write(FrameUtil.getNEGR());
            // 어떤 장비인지 식별하기 위한 명령을 보낸다.
            cmdIdentifyDevice(session);
        }
        else {
            session.setAttribute("frameMaxLen", GeneralDataConstants.FRAME_MAX_LEN);
            session.setAttribute("frameWinSize", GeneralDataConstants.FRAME_WINSIZE);
            ControlDataFrame negrFrame = FrameUtil.getNEGR();
            negrFrame.setArg(new OCTET(new byte[]{ControlDataConstants.NEG_R_UNSUPPORTED_VERSION}));
            session.write(negrFrame);
            
            if(session.containsAttribute("modemSerial")){
                String modemSerial = (String) session.getAttribute("modemSerial");
                bypassService(session,modemSerial);
            }
            
            Thread.sleep(1000);
        } 
    }
    
    /*
     * 모뎀과 미터 시리얼번호를 가져오기 위한 명령
     */
    private void cmdIdentifyDevice(IoSession session) 
    throws Exception
    {
        sendCommand(session, "cmdIdentifyDevice", null);
    }
    
    private void bypassService(IoSession session, String modemSerial) {
        try {
            Thread.sleep(BYPASS_WAIT_TIME*1000);
            log.info("====[Bypass Open]====");
            BypassRegister bs = BypassRegister.getInstance();
            
            final String fmodemSerial = modemSerial;
            bs.execute(modemSerial, session);
        } catch (Exception e) {
            log.error(e, e);
        } finally {
            
        }

    }
    
    private CommandData command(String ns, String cmdName, List<?> params){
        MIBUtil mu = MIBUtil.getInstance(ns);
        CommandData command = new CommandData();
        log.debug(mu.getMIBNodeByName(cmdName));
        command.setCmd(mu.getMIBNodeByName(cmdName).getOid());
        if (params != null) {
            for (int i = 0; i < params.size(); i++) {
                Object obj = params.get(i);
                if (obj instanceof SMIValue) {
                    command.append((SMIValue) obj);
                } else if(obj instanceof AsyncCommandParam){
                    AsyncCommandParam param = (AsyncCommandParam) obj;
                    SMIValue smiValue;
                    try {
                        smiValue = DataUtil.getSMIValueByOid(ns, param.getParamType(), param.getParamValue());
                        command.append(smiValue);
                    } catch (Exception e) {
                        log.error(e,e);
                    }

                }
            }
        }
        return command;
    }
    
    public void sendCommand(IoSession session, String cmdName, List<?> params)
            throws Exception
    {
        CommandData command = null;
        try {
            String ns = (String)session.getAttribute("nameSpace");
            command = command(ns, cmdName, params);
            command.setAttr(ServiceDataConstants.C_ATTR_REQUEST);
            command.setTid(FrameUtil.getCommandTid());
            
            ServiceDataFrame frame = new ServiceDataFrame();
            long mcuId = 0L;
            frame.setMcuId(new UINT(mcuId));
            frame.setSvc(GeneralDataConstants.SVC_C);
            frame.setAttr((byte)(GeneralDataConstants.ATTR_START | GeneralDataConstants.ATTR_END));
            frame.setSvcBody(command.encode());
            session.write(frame);
        }
        catch(Exception ex)
        {
            log.error(ex, ex);
            if(!command.getCmd().toString().equals("198.3.0"))
            {
                log.error("sendCommand failed : command["+command+"]",ex);
            } else {
                log.error("sendCommand failed : command["+command.getCmd()
                        +"]",ex);
            }
            throw ex;
        }
    }

    @Override
    public void inputClosed(IoSession session) throws Exception {
        log.info("### Bye Bye ~ Client session closed from " + session.getRemoteAddress().toString());
        session.removeAttribute(session.getRemoteAddress());
        session.closeNow();
    }
}
