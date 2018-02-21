package com.aimir.fep.protocol.fmp.server;

import java.util.ArrayList;
import java.util.Iterator;

import com.aimir.fep.protocol.fmp.frame.ControlDataFrame;
import com.aimir.fep.protocol.fmp.frame.GeneralDataConstants;
import com.aimir.fep.protocol.fmp.frame.GeneralDataFrame;
import com.aimir.fep.protocol.fmp.frame.ServiceDataFrame;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FrameUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderException;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
/**
 * Encodes MCU Communication Stream  into General Data Frame.
 *
 * @author goodjob (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2014-08-19 10:00:00 +0900 $,
 */

public class IF4NewEncoder extends ProtocolEncoderAdapter
{
    private static Log log = LogFactory.getLog(IF4NewEncoder.class);

    private void waitAck(IoSession session, int sequence)
        throws Exception
    {
        IF4NewProtocolHandler handler =
           (IF4NewProtocolHandler)session.getHandler();
        handler.waitAck(session,sequence);
    }

    /**
     * encode FMP Protocol Frame
     *
     * @param session <code>ProtocolSession</code> session
     * @param message <code>Object</code> GeneralDataFrame or ByteBuffer
     * @param out <code>ProtocolEncoderOutput</code> save encoded byte stream
     * @throws ProtocolViolationException  indicating that is was error of Encode
     */
    public void encode( IoSession session, Object message,
                       ProtocolEncoderOutput out )
            throws ProtocolEncoderException
    {
        try
        {
        	log.info("Encode [Start]");
            if(message instanceof ServiceDataFrame)
            {
                GeneralDataFrame frame = (GeneralDataFrame)message;
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
                Iterator<?> iter = framelist.iterator();
                int cnt = 0;
                int seq = 0;
                ControlDataFrame wck = null;
                while(iter.hasNext())
                {
                    if(!isSetLastSequence && ((lastIdx - cnt) /
                                GeneralDataConstants.FRAME_MAX_SEQ)
                            < 1)
                    {
                        session.setAttribute("lastSequence",
                                new Integer(lastSequence));
                        isSetLastSequence = true;
                    }
                    mbx = (byte[])iter.next();
                    seq = DataUtil.getIntToByte(mbx[1]);
                    buf = IoBuffer.allocate(mbx.length);
                    buf.put(mbx,0,mbx.length);
                    buf.flip();
                    log.info("Sended : ["+session.getRemoteAddress()
                            +"] " + buf.limit() + " : "
                            + buf.getHexDump());
                    session.write(buf);
                    FrameUtil.waitSendFrameInterval();
                    if(((cnt+1) % GeneralDataConstants.FRAME_WINSIZE)
                            == 0)
                    {
                        log.debug("WCK : start : " + (seq -
                            GeneralDataConstants.FRAME_WINSIZE + 1)
                                +"end : " + seq);
                        wck =FrameUtil.getWCK((seq-
                            GeneralDataConstants.FRAME_WINSIZE + 1),
                                seq);
                        session.write(wck);
                        session.setAttribute("wck",wck);
                        waitAck(session,cnt);
                    }
                    cnt++;
                }
                if(frame.getSvc() != GeneralDataConstants.SVC_C)
                {
                    if((cnt % GeneralDataConstants.FRAME_WINSIZE)
                            != 0)
                    {
                        if(cnt > 1)
                        {
                            log.debug("WCK : start : " + (seq -(seq%
                                GeneralDataConstants.FRAME_WINSIZE))
                                    + "end : " + seq);
                            wck =FrameUtil.getWCK(seq-(seq%
                                GeneralDataConstants.FRAME_WINSIZE),
                                    seq);
                            session.write(wck);
                            session.setAttribute("wck",wck);
                        }
                        waitAck(session,cnt-1);
                    }
                }
                FrameUtil.waitSendFrameInterval();
                session.removeAttribute("wck");
            } else if(message instanceof ControlDataFrame)
            {
                GeneralDataFrame frame = (GeneralDataFrame)message;
                byte[] bx = frame.encode();
                byte[] crc = FrameUtil.getCRC(bx);
                IoBuffer buff = IoBuffer.allocate(bx.length
                        + crc.length);
                buff.put(bx);
                buff.put(crc);
                buff.flip();
                log.info("Sended : ["+session.getRemoteAddress()
                        +"] " + buff.limit() + " : "
                        + buff.getHexDump());
                out.write(buff);
            }else if(message instanceof IoBuffer)
            {
                IoBuffer buffer =  (IoBuffer)message;
                log.info("Sended : ["+session.getRemoteAddress()
                        + "] " +buffer.limit() + " : "
                        + buffer.getHexDump());
                out.write(buffer);
            }
            log.info("Encode [End]");
        }
        catch(Exception ex)
        {
            log.error("encode failed " + message, ex);
            new ProtocolEncoderException( ex.getMessage());
        }
    }
}
