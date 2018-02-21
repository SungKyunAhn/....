package com.aimir.fep.protocol.security;


import java.net.InetSocketAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import org.springframework.stereotype.Component;

import com.aimir.constants.CommonConstants.ThresholdName;
import com.aimir.fep.protocol.security.AuthDelayCheck;
import com.aimir.fep.protocol.security.frame.AuthDataFrame;
import com.aimir.fep.protocol.security.frame.AuthFrameConstants;
import com.aimir.fep.protocol.security.frame.AuthFrameUtil;
import com.aimir.fep.protocol.security.frame.AuthFrameWindow;
import com.aimir.fep.protocol.security.frame.AuthGeneralFrame;
import com.aimir.fep.protocol.security.frame.FrameType;
import com.aimir.fep.protocol.security.frame.StatusCode;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.FrameUtil;
import com.aimir.fep.util.threshold.CheckThreshold;

/**
 * implementation of FEP Authentication().
 *
 * @author 
 * @version $Rev: 1 $, $Date: 2016-05-13 15:59:15 +0900 $,
 */
@Component
public class AuthProtocolHandler extends IoHandlerAdapter
{
    private Object lock = new Object();
    private static Log log = LogFactory.getLog(AuthProtocolHandler.class);

    private int enqTimeout = Integer.parseInt(FMPProperty.getProperty("protocol.enq.timeout", "10"));
    private int idleTime = Integer.parseInt(FMPProperty.getProperty(
                "protocol.idle.time","5"));
    private int enableDelay = Integer.parseInt(FMPProperty.getProperty("protocol.security.authdelay", "0"));
 
    // SP-451 check to verify
    private boolean checkVerify = false;
    
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
        FrameType lastRecvFrameType = (FrameType) session.getAttribute("lastRecvFrameType");
        if ( lastRecvFrameType != null ){
            writeAck(session, lastRecvFrameType, StatusCode.LocalError);
            // FrameUtil.waitAfterSendFrame();
        }
        
        // INSERT START SP-193
        if (cause.getMessage().matches(".*" + "CRC Error" + ".*" )) {
            CheckThreshold.updateCount(session.getRemoteAddress().toString(), ThresholdName.CRC);
        }
        else if (cause.getMessage().matches(".*" + "Invalid FrameType" + ".*" )) {
            CheckThreshold.updateCount(session.getRemoteAddress().toString(), ThresholdName.INVALID_PACKET);
        }
        // INSERT END SP-193
        session.closeOnFlush();
    }
    
    private void writeAck(IoSession session, FrameType frameType, StatusCode code){
        byte[] payload = new byte[2];
        
        AuthFrameWindow sw = (AuthFrameWindow)session.getAttribute("slidewindow");      
        if(sw != null)
        {
            sw.resetAll();
        }
        if ( frameType == null || code == null){
            log.debug("frameType or StatusCode is null");
            return;
        }
        payload[0] = frameType.getCode();
        payload[1] = code.getCode();
        // deviceSerial
        byte[] serverId = new byte[AuthFrameConstants.DEVICE_SERIAL_LEN];
        AuthGeneralFrame frame = new AuthGeneralFrame( 
                FrameType.AckNack,
                AuthGeneralFrame.Pending.FALSE,
                (byte) 0, // FrameCount
                AuthFrameConstants.VERSION,
                (byte) 0, // SequenceNumber
                serverId,
                2, payload);
        log.debug("Send ACK/NAK FrameType : " + frameType.name() + ", StatusCode : " + code.name());
        writeResponseData(session,frame);
    }
    
    private void writeResponseData(IoSession  session, AuthGeneralFrame frame)
    {
        byte[] bx = frame.encode();
          IoBuffer buffer = IoBuffer.allocate(bx.length);
          buffer.put(bx);
          buffer.flip();
          log.debug("Sended["+session.getRemoteAddress()
                 +"] : " + buffer.limit() + " : " 
                  + buffer.getHexDump()); 
          session.write(buffer);
          session.setAttribute("lastSendFrameType", frame.foFrameType);
    }
    private void CinfoRequest(IoSession session , byte[] deviceSerial, byte[] payload)      
            throws Exception

    {

        log.debug("---- Cinfo Request Start ------");
        if ( this.enableDelay == 1 ){
            if (!AuthDelayCheck.isAuthDelay( (InetSocketAddress)session.getRemoteAddress() ) ) {
                session.closeNow();
                return;
            }
        }
        AuthISIoTClient  iotCli = (AuthISIoTClient)session.getAttribute("IsIoTCli");
        if( iotCli == null)
        {
            iotCli = new AuthISIoTClient();
            session.setAttribute("IsIoTCli", iotCli);
        }
        iotCli.setdeviceSerial(deviceSerial);
        iotCli.setIPAddress(session.getRemoteAddress().toString());     // INSERT SP-193
        
        StatusCode status = iotCli.cinfoRequest(payload);
        if ( status == StatusCode.Success ){
            byte[] serverToken = iotCli.getServerToken();
            // deviceSerial
            byte[] serverId = new byte[AuthFrameConstants.DEVICE_SERIAL_LEN];
            AuthGeneralFrame frame = new AuthGeneralFrame( 
                    FrameType.CinfoResponse,
                    AuthGeneralFrame.Pending.FALSE,
                    (byte) 0, // FrameCount
                    AuthFrameConstants.VERSION,
                    (byte) 0, // SequenceNumber
                    serverId,
                    serverToken.length, serverToken);
            writeResponseData(session,frame);
            checkVerify = true;
        }
        else { // Error         
            writeAck(session, FrameType.CinfoRequest, status );      
            // FrameUtil.waitAfterSendFrame();
            session.closeOnFlush();
        }
        if ( this.enableDelay == 1 ){
            AuthDelayCheck.updateAuthDelay(false, (InetSocketAddress)session.getRemoteAddress());
        }
        log.debug("---- Cinfo Request End  ------");
    }
    
    private void CertificateRequest(IoSession session, byte[] deviceSerial, byte[] payload) throws Exception
    {
        log.debug("---- Certificate Request Start ------");
        AuthISIoTClient  iotCli = (AuthISIoTClient)session.getAttribute("IsIoTCli");
        if( iotCli == null ){ // error
            log.error( " CinfoRequest has not processed");
            writeAck(session, FrameType.CertificateRequest, StatusCode.LocalError);
            // FrameUtil.waitAfterSendFrame();
            // session.closeNow();
            session.closeOnFlush();
            return;
        }

        iotCli.setdeviceSerial(deviceSerial);
        iotCli.setIPAddress(session.getRemoteAddress().toString());     // INSERT SP-193

        StatusCode status = iotCli.certificateRequest(payload);
        if ( status == StatusCode.Success ){
            //=> UPDATE START 2016.11.21 SP-318
            if ( !iotCli.getRenewalFlag() ) {
                byte[] cipher = iotCli.getCipher();
                // deviceSerial
                byte[] serverId = new byte[AuthFrameConstants.DEVICE_SERIAL_LEN];
                AuthGeneralFrame frame = new AuthGeneralFrame( 
                        FrameType.CertificateSUCCESSReseponse,
                        AuthGeneralFrame.Pending.FALSE,
                        (byte) 0, // FrameCount
                        AuthFrameConstants.VERSION,
                        (byte) 0, // SequenceNumber
                        serverId,
                        cipher.length, cipher);
                writeResponseData(session,frame);
                //session.write(frame);
            } else {
                // renewal certificaete
                log.debug("---- Certificate Renewal Start ------");
                status = iotCli.renewalCertificate();
                if ( status == StatusCode.Success ){
                    log.debug("---- Certificate Renewal Success ------");
                    byte[] renewalPayload = iotCli.getRenewalPayload();
                    // deviceSerial
                    byte[] serverId = new byte[AuthFrameConstants.DEVICE_SERIAL_LEN];
                    AuthGeneralFrame frame = new AuthGeneralFrame( 
                            FrameType.CertificateRenewalResponse,
                            AuthGeneralFrame.Pending.FALSE,
                            (byte) 0, // FrameCount
                            AuthFrameConstants.VERSION,
                            (byte) 0, // SequenceNumber
                            serverId,
                            renewalPayload.length, renewalPayload);
                    writeResponseData(session,frame);
                    //session.write(frame);
                } else {
                    log.debug("---- Certificate Renewal Fail ------ status: " + status.name());
                    if ( this.enableDelay == 1 ) {
                        if ( AuthDelayCheck.isAuthDelay( (InetSocketAddress)session.getRemoteAddress() ) ) {
                            writeAck(session, FrameType.CertificateRenewalResponse, status);
                            FrameUtil.waitAfterSendFrame();
                        }
                    } else { // no delay
                        writeAck(session, FrameType.CertificateRenewalResponse, status);
                        FrameUtil.waitAfterSendFrame();
                    }
                    session.closeNow();
                }
            }
            //=> UPDATE END   2016.11.21 SP-318
        }
        else {
            if ( this.enableDelay == 1 ) {
                if ( AuthDelayCheck.isAuthDelay( (InetSocketAddress)session.getRemoteAddress() ) ) {
                    writeAck(session, FrameType.CertificateRequest, status);
                    // FrameUtil.waitAfterSendFrame();
                }
            } else { // no delay
                writeAck(session, FrameType.CertificateRequest, status);
                // FrameUtil.waitAfterSendFrame();
            }
            session.closeOnFlush();
            // session.closeNow();
        }
        log.debug("---- CertificateRequest Request End  ------");   
    }

    private void AckReceived(IoSession session, byte[] deviceSerial, byte payload[])
    {
        FrameType frameType = FrameType.getByCode(payload[0]);
        StatusCode statusCode = StatusCode.getByCode(payload[1]);
        if ( frameType == null ){
            log.error("Received ACK/NAK Unknown FrameType : " + payload[0]);
        }
        else {
            log.info("Received ACK/NAK FrameType : " + frameType.name() + ", StatusCode : " + statusCode.name());
        }
        
        FrameType lastSendFrameType = (FrameType) session.getAttribute("lastSendFrameType");
        if ( frameType == FrameType.CertificateSUCCESSReseponse  && 
                lastSendFrameType == FrameType.CertificateSUCCESSReseponse && checkVerify) 
        {
            AuthISIoTClient  iotCli = (AuthISIoTClient)session.getAttribute("IsIoTCli");
            if ( iotCli != null ){
                iotCli.setdeviceSerial(deviceSerial);
                iotCli.setIPAddress(session.getRemoteAddress().toString());     // INSERT SP-193

                if (statusCode == StatusCode.Success ){
                    iotCli.SendCmdAuthSPModem(AuthISIoTClient.CMD_AUTH_SPMODEM_SUCCESS);
                    if ( this.enableDelay == 1 ) {
                        AuthDelayCheck.updateAuthDelay(true, (InetSocketAddress)session.getRemoteAddress());
                    }
                }
                else {
                    iotCli.SendCmdAuthSPModem(AuthISIoTClient.CMD_AUTH_SPMODEM_FAIL);
                }
                
                checkVerify = false;
            }
        }
        session.closeNow();
    }
    /**
     * @param session
     * @param pdf
     * @throws Exception
     */
    private void receivedAuthDataFrame(IoSession session, AuthDataFrame pdf)
    throws Exception
    {

//      lastRecvFrameType = pdf.frameType;
        session.setAttribute("lastRecvFrameType", pdf.frameType);
        switch ( pdf.frameType){
        case CinfoRequest:
            CinfoRequest(session, pdf.deviceSerial, pdf.payload);
            break;
            
        case  CertificateRequest:
            CertificateRequest(session, pdf.deviceSerial, pdf.payload);
            break;
            
        case AckNack :
            AckReceived(session, pdf.deviceSerial, pdf.payload);
            break;
            
        default:
            log.error("Invalid FrameType : " + pdf.frameType.toString());
            throw new Exception("Invalid FrameType : " + pdf.frameType.toString());
        }

    }
    
    
    public void doDecode(IoSession session, IoBuffer in) throws Exception
    {
        AuthDataFrame frame = null;

        if(!AuthFrameUtil.isValidFrame(in,0))
        {
            log.error("Invalid StartFlag  ["+in.getHexDump()+"]");
            throw new Exception("Invalid StartFalg");
        }
        boolean isValidFrameCrc = AuthFrameUtil.checkCRC(in);

        if(!isValidFrameCrc)
        {
            log.error("CRC check failed Received Data ["
                    + session.getRemoteAddress()+ "] :" + in.getHexDump());
            throw new Exception("CRC Error");
        }
        AuthFrameWindow sw = null;
        
        synchronized(lock)
        {
            sw = (AuthFrameWindow)session.getAttribute("slidewindow");      
            if(sw == null)
            {
                sw = new AuthFrameWindow();
                session.setAttribute("slidewindow", sw);
            }
            sw.put(in);
            if ( sw.isReceivedEndFrame() ){
                byte naks[] = sw.checkWindow();
                if(naks.length < 1)
                {
                    log.debug("All Frame are Received");
                    IoBuffer buf = sw.getCompletedFrameBuffer();
                    byte[] data = new byte[buf.limit()];
                    buf.get(data,0,data.length);
                    frame = new AuthDataFrame(sw.getFrameType(), sw.getVersion(), sw.getdeviceSerial(), data.length, data);
                    sw.resetAll();
                }
                else {
                    log.debug("Frame Missing"); 
                }
            }
        }
        if ( frame != null ){
            receivedAuthDataFrame(session, frame);
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
//            if(message instanceof AuthDataFrame) {
//              log.debug("AuthDataFrame Message Receive");
//              receivedAuthDataFrame(session, (AuthDataFrame) message);
//            }
            if ( message instanceof IoBuffer ) { 
                IoBuffer in = (IoBuffer)message;
                doDecode(session, in);
            }
            else {
                log.warn("Message is Unknown Frame!!!");
            }
        }catch(Exception ex)
        {
            log.error(ex,ex);
            FrameType lastRecvFrameType = (FrameType) session.getAttribute("lastRecvFrameType") ;
            if ( lastRecvFrameType != null ){
                writeAck(session, lastRecvFrameType, StatusCode.LocalError);
                FrameUtil.waitAfterSendFrame();
            }
            session.closeNow();
        }
    }

    /*
     * 
     * @see org.apache.mina.core.service.IoHandlerAdapter#messageSent(org.apache.mina.core.session.IoSession, java.lang.Object)
     */
    public void messageSent(IoSession session, Object message) throws Exception {
        log.debug("[Start] MessageSent");
        try
        {
            if(message instanceof AuthDataFrame) {
                log.debug("[Send] AuthDataFrame");
            }
        }catch(Exception ex)
        {
            log.error(" AuthProtocolHandler::MessageSent "
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
        log.debug(" READER IDLE TIME : " + session.getConfig().getReaderIdleTime());
        log.debug(" IDLE COUNT : "
                + session.getIdleCount(IdleStatus.READER_IDLE));
        session.closeNow();
        /*
        if(session.getIdleCount(IdleStatus.READER_IDLE) >= retry)
        {
            log.info("IdleCount >= " + retry +" , Session Close");
            session.closeNow();
        }
        */
    }

    /**
     * inherited method from ProtocolHandlerAdapter
     */
    public void sessionOpened(IoSession session)
    {
        log.info("    ");
        log.info("    ");
        log.info("############################## Logging Start ~!! ################################################");      
        log.info("sessionOpened : " + session.getRemoteAddress() + ", writeIdleTime : " + session.getLastWriterIdleTime());
        session.getConfig().setWriteTimeout(enqTimeout);
        session.getConfig().setIdleTime(IdleStatus.READER_IDLE,
                idleTime);
    }

    public void sessionClosed(IoSession session)
    {
        session.removeAttribute("slidewindow");
        session.removeAttribute("IsIoTCli");
        //log.debug("Session Closed");
        log.info("### Bye Bye ~  Session Closed : " + session.getRemoteAddress());
        log.info("   ");
        session.closeNow();
    }

}
