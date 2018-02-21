package com.aimir.fep.protocol.fmp.client;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderException;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.aimir.fep.protocol.fmp.frame.ControlDataFrame;
import com.aimir.fep.protocol.fmp.frame.GeneralDataConstants;
import com.aimir.fep.protocol.fmp.frame.GeneralDataFrame;
import com.aimir.fep.protocol.fmp.frame.ServiceDataFrame;
import com.aimir.fep.util.FrameUtil;
/**
 * Encodes MCU Communication Stream  into General Data Frame.
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class FMPClientEncoder extends ProtocolEncoderAdapter
{
    private static Log log = LogFactory.getLog(FMPClientEncoder.class);
    private void waitAck(IoSession session, int sequence)
    { 
        FMPClientProtocolHandler handler = 
           (FMPClientProtocolHandler)session.getHandler(); 
        //handler.waitAck(session,sequence);
        while(sequence != handler.getAckSequence())
        {
            try { Thread.sleep(200);
            }catch(Exception ex) { ex.printStackTrace(); }
        }
    }

    /**
     * encode FMP Protocol Frame
     *
     * @param session <code>ProtocolSession</code> session
     * @param message <code>Object</code> GeneralDataFrame or ByteBuffer
     * @param out <code>ProtocolEncoderOutput</code> save encoded byte stream
     * @throws ProtocolViolationException  indicating that is was error of Encode
     */
    public void encode(IoSession session, Object message,
                       ProtocolEncoderOutput out )
    throws ProtocolEncoderException
    { 
        log.info(message.getClass().getName());
        try 
        {
            if(message instanceof ServiceDataFrame)
            {
                GeneralDataFrame frame = (GeneralDataFrame)message; 
                byte[] bx = frame.encode(); 
                byte[] mbx = null;
                IoBuffer buf = null;
                
                log.info("SESSION FrameMaxLen[" + session.getAttribute("frameMaxLen") + "]");
                ArrayList<?> framelist = FrameUtil.makeMultiEncodedFrame(bx, session);
                session.setAttribute("sendframes",framelist); 
                Iterator<?> iter = framelist.iterator(); 
                int cnt = 0; 
                while(iter.hasNext()) 
                { 
                    mbx = (byte[])iter.next(); 
                    buf = IoBuffer.allocate(mbx.length); 
                    buf.put(mbx); 
                    buf.flip(); 
                    log.debug("Sended["+session.getRemoteAddress()
                           +"] : " + buf.limit() + " : " 
                            + buf.getHexDump()); 
                    out.write(buf); 
                    if(((cnt+1) % GeneralDataConstants.FRAME_WINSIZE) 
                            == 0) 
                        waitAck(session,cnt); 
                    cnt++; 
                } 
                if(frame.getSvc() != GeneralDataConstants.SVC_C 
                        && !frame.isAttrByte( 
                            GeneralDataConstants.ATTR_FRAME)) 
                { 
                    waitAck(session,cnt-1); 
                } 
            } 
            else if(message instanceof ControlDataFrame) 
            { 
                ControlDataFrame frame = (ControlDataFrame)message; 
                byte[] bx = frame.encode(); 
                byte[] crc = FrameUtil.getCRC(bx); 
                IoBuffer buff = IoBuffer.allocate(bx.length 
                        +crc.length); 
                buff.put(bx); 
                buff.put(crc); 
                buff.flip(); 
                log.debug("Sended["+session.getRemoteAddress()
                       +"] : " + buff.limit() + " : " 
                        + buff.getHexDump()); 
                out.write(buff); 
            } 
            else if(message instanceof IoBuffer) 
            { 
                IoBuffer buffer =  (IoBuffer)message; 
                log.debug("Sended["+session.getRemoteAddress()
                       +"] : " + buffer.limit() + " : " 
                        + buffer.getHexDump()); 
                out.write(buffer); 
            }
        }catch(Exception ex)
        {
            log.error("encode failed " + message, ex); 
            new ProtocolEncoderException( ex.getMessage());
        }
    }
}
