package com.aimir.fep.protocol.smcp;

import com.aimir.util.TimeUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderException;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 * Decodes a General Data Frame into MCU Input Stream.
 *
 * @author goodjob (goodjob@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2014-08-19 10:00:00 +0900 $,
 */
public class SMCPDecoder extends CumulativeProtocolDecoder
{
    private static Log log = LogFactory.getLog(SMCPDecoder.class);

    /**
     * 프레임의 첫번째 바이트를 체크해 올바른 프레임인지 체크한다
     * SMCPFrame = 0x5A
     *
     * @param buff
     * @param pos
     * @return
     */
    private boolean isValidFrame(IoBuffer buff,int pos)
    {
        if(buff.get(pos) != SMCPDataConstants.SOH) {
            if (buff.hasRemaining()) {
				buff.position(buff.limit());
			}
            return false;
        }
        return true;
    }

    /**
     * decode input stream
     *
     * @param session <code>ProtocolSession</code> session
     * @param in <code>ByteBuffer</code> input stream
     * @param out <code>ProtocolDecoderOutput</code> save decoding frame
     * @throws  ProtocolViolationException
     */
    public boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out)
    throws ProtocolDecoderException
    {
        try
        {
            log.info("Received [" + session.getRemoteAddress() + 
                    "] : LIMIT["+in.limit()+"] POSITION[" + in.position() + "]");
            log.debug(" IN_HEX : " + in.getHexDump());
            if (session.getAttribute("startLongTime") == null) {
                session.setAttribute("startLongTime", System.currentTimeMillis());
                session.setAttribute("startTime", TimeUtil.getCurrentTime());
            }
            
            int startPos = in.position();

            log.debug("SOF: "+in.get(startPos));
            // Validation Check : Start Of Header is  0x00
            if(!isValidFrame(in,startPos))
            {
                log.error("data["+in.getHexDump()+"] is invalid Frame");
                // in.position(in.limit());
                return true;
            }
            
    	    //FMPGeneralDecoder d = new FMPGeneralDecoder();
    	    //return d.doDecode(session, in, out, startPos);
            return true;
        } catch(Exception ex)
        {
            log.error("FMPDecoder::decode failed : ", ex);
            throw new ProtocolDecoderException(ex.getMessage());
        }
    }
}
