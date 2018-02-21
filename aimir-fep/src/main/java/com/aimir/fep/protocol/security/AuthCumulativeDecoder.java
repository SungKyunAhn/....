package com.aimir.fep.protocol.security;


import com.aimir.fep.protocol.security.frame.AuthFrameUtil;
import com.aimir.util.TimeUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderException;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 * 
 *
 * @author 
 * @version 
 */
public class AuthCumulativeDecoder extends CumulativeProtocolDecoder
{
    private static Log log = LogFactory.getLog(AuthCumulativeDecoder.class);

    /**
     *
     * @param buff
     * @param pos
     * @return
     */


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

            if(!AuthFrameUtil.isValidFrame(in,startPos))
            {
                log.error("data["+in.getHexDump()+"] is invalid Frame");
                //in.position(in.limit());
                return true;
            }
            if (in.hasRemaining()) {
				in.position(in.limit());
			}
            AuthGeneralDecoder d = new AuthGeneralDecoder();
            boolean ret = d.doDecode(session, in, out, startPos);
            return ret;
        } catch(Exception ex)
        {
            log.error("AuthenticatorDecorder::decode failed : ", ex);
            throw new ProtocolDecoderException(ex.getMessage());
        }
    }
}
