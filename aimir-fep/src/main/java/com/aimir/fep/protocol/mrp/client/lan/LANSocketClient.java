package com.aimir.fep.protocol.mrp.client.lan;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.GeneralSecurityException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.fep.protocol.fmp.client.Client;
import com.aimir.fep.protocol.fmp.common.LANTarget;
import com.aimir.fep.protocol.fmp.common.Target;
import com.aimir.fep.protocol.fmp.frame.ErrorCode;
import com.aimir.fep.protocol.fmp.frame.service.AlarmData;
import com.aimir.fep.protocol.fmp.frame.service.CommandData;
import com.aimir.fep.protocol.fmp.frame.service.EventData;
import com.aimir.fep.protocol.fmp.frame.service.MDData;
import com.aimir.fep.protocol.fmp.frame.service.RMDData;
import com.aimir.fep.protocol.fmp.frame.service.ServiceData;
import com.aimir.fep.protocol.fmp.processor.ProcessorHandler;
import com.aimir.fep.protocol.mrp.MeterProtocolFactory;
import com.aimir.fep.protocol.mrp.MeterProtocolHandler;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.MIBUtil;
import com.aimir.model.device.CommLog;
import com.aimir.util.TimeUtil;

public class LANSocketClient implements Client {

    private Log log = LogFactory.getLog(LANSocketClient.class);
    private LANTarget target = null;
    private int CONNECT_TIMEOUT = 
        Integer.parseInt(FMPProperty.getProperty("protocol.connection.timeout", 30+"")); // seconds
    private Socket socket = null;
    private long MAX_MCUID = 4294967295L;
    private ProcessorHandler logProcessor = null;
    private Integer activatorType = new Integer(
            FMPProperty.getProperty("protocol.system.FEP","1"));
    private Integer targetType = new Integer(
            FMPProperty.getProperty("protocol.system.MCU","2"));
    private Integer protocolType = new Integer(
            FMPProperty.getProperty("protocol.type.LAN", CommonConstants.getProtocolCode(Protocol.LAN)+""));
    
    /**
     * constructor
     * @throws IOException 
     * @throws GeneralSecurityException 
     */
    public LANSocketClient() throws GeneralSecurityException, IOException
    {
    }
    
    /**
     * constructor
     * @param target <code>TcpTarget</code> target
     * @throws IOException 
     * @throws GeneralSecurityException 
     */
    public LANSocketClient(LANTarget target) throws GeneralSecurityException, IOException
    {
        this.target = target;
    }

    /**
     * initialize
     * create IoProtocolsocket
     * create FMPClientProtocolProvider
     * @throws IOException 
     * @throws GeneralSecurityException 
     */
    private void init() throws GeneralSecurityException, IOException
    {     
        socket = new Socket();
    }
    
    /**
     * connect to Target
     *
     * @throws Exception
     */
    private void connect() throws Exception
    {
        init();
        
        if(socket != null && socket.isConnected())
            return;

        for( ;; )
        {
            try
            {
                socket.connect(
                        new InetSocketAddress(
                        target.getIpAddr(),
                        target.getPort()), CONNECT_TIMEOUT*1000);
                log.info("SESSION CONNECTED[" + socket.isConnected() + "] LOCAL ADDRESS[" + socket.getLocalSocketAddress() + "]");
                
                break;
            }
            catch( Exception e )
            {
                log.error( "Failed to connect. host["
                        + target.getIpAddr()+"] port["
                        + target.getPort()+"]",e );
                throw e;
            }
        }

        if(socket == null)
            throw new Exception("Failed to connect. host["
                        + target.getIpAddr()+"] port["
                        + target.getPort()+"]");
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
     * set Target
     *
     * @param target <code>TcpTarget</code> target
     */
    public void setTarget(Target target) throws Exception
    {
        if(!(target instanceof LANTarget))
            throw new Exception("not supported target");
        this.target = (LANTarget)target;
    }

    /**
     * request command data to Target and response 
     *
     * @param command <code>CommandData</code> request command
     * @return response <code>ServiceData</code> response
     * @throws Exception
     */
    public CommandData sendCommand(CommandData command)
        throws Exception
    {
        try {
            //ProtocolSession session = connect();
            if(socket == null || !socket.isConnected())
                connect();
            CommLog commLog = new CommLog();
            
			MeterProtocolHandler handler = MeterProtocolFactory.getHandler(
			target.getIpAddr(), target.getMeterModel(), target
					.getGroupNumber(), target.getMemberNumber(), target
					.getFwVer());
            ServiceData sd;
            String startTime = TimeUtil.getCurrentTime();
            //TODO 데이터타입 추가.
            commLog.setSuppliedId(target.getSupplierId());
            commLog.setStartDate(startTime.substring(0,8));
            commLog.setStartTime(startTime.substring(8,14));
            commLog.setStartDateTime(startTime);
            commLog.setProtocolCode(CommonConstants.getProtocol(protocolType+""));
            commLog.setSenderTypeCode(CommonConstants.getSenderReceiver(activatorType+""));
            commLog.setSenderId(target.getTargetId());
            commLog.setSenderIp(DataUtil.getFepIdString());
            commLog.setReceiverTypeCode(CommonConstants.getSenderReceiver(targetType+""));
            commLog.setReceiverId(target.getTargetId());
            commLog.setOperationCode(MIBUtil.getInstance().getName(
                        command.getCmd().toString()));
            long s = System.currentTimeMillis();
            commLog.setSendBytes((int)socket.getSendBufferSize());
            sd = handler.execute(socket, command);
            long e = System.currentTimeMillis();
            if(sd == null) 
                return null;
            log.debug("Received Response TID : " 
                    + ((CommandData)sd).getTid());
            commLog.setEndTime(TimeUtil.getCurrentTime());
            // commLog.setRcvBytes((int)session.getReadBytes());
            commLog.setUnconPressedRcvBytes(sd.getTotalLength());
            commLog.setTotalCommTime((int)(e-s));
            commLog.setSvcTypeCode(CommonConstants.getHeaderSvc("C"));
            if(((CommandData)sd).getErrCode().getValue() > 0)
            {
                commLog.setCommResult(0);
                commLog.setDescr(ErrorCode.getMessage(((CommandData)sd).getErrCode().getValue()));
            }            
            else
            {
                commLog.setCommResult(1);
            }
            commLog.setSuppliedId(target.getSupplierId());
            log.info(commLog.toString());
            saveCommLog(commLog);
            return (CommandData)sd;
        }
        catch (Exception e)
        {
            if(!command.getCmd().toString().equals("198.3.0"))
            {
                log.error("sendCommand failed : command["+command+"]",e);
            } else {
                log.error("sendCommand failed : command["+command.getCmd()
                        +"]",e);
            }
            throw e;
        }
        finally
        {
            close();
        }
    }

    /**
     * close TCPClient session
     */
    public void close()
    {
        close(false);
    }

    /**
     * close TCPClient session and wait completed
     *
     * @param immediately <code>boolean</code> immediately
     */
    public void close(boolean immediately)
    {
        log.debug(immediately);
        try {
            if(socket != null) {
                socket.close();
            }
            socket = null;
        }
        catch (IOException e) {}
    }

    /**
     * check whether connected or not
     *
     * @return connected <code>boolean</code>
     */
    public boolean isConnected()
    {
        if(socket == null)
            return false;
        return socket.isConnected();
    }

    @Override
    public void sendAlarm(AlarmData alarmData) throws Exception {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void sendEvent(EventData eventData) throws Exception {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void sendMD(MDData mdData) throws Exception {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void sendRMD(RMDData rmdData) throws Exception {
        // TODO Auto-generated method stub
        
    }

    /**
     * wait ACK
     */
    /*
    private void waitAck(IoSession session, int sequence)
        throws Exception
    { 
        log.debug("waitACK.SEQ:" + sequence);
        MRPClientProtocolHandler handler = 
           (MRPClientProtocolHandler)session.getHandler(); 
        handler.waitAck(session,sequence);
    }
    */
}
