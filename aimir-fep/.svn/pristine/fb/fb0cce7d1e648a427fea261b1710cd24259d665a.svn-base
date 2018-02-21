package com.aimir.fep.protocol.reversegprs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderException;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.aimir.fep.meter.parser.plc.PLCDataConstants;
import com.aimir.fep.protocol.fmp.frame.AMUGeneralDataConstants;
import com.aimir.fep.protocol.fmp.frame.GeneralDataConstants;
import com.aimir.fep.protocol.fmp.server.FMPAMUDecoder;
import com.aimir.fep.protocol.fmp.server.FMPGeneralDecoder;
import com.aimir.fep.protocol.fmp.server.FMPPLCDecoder;
import com.aimir.fep.util.FrameUtil;
import com.aimir.util.TimeUtil;

public class CommDecoder extends CumulativeProtocolDecoder
{
    private static Log log = LogFactory.getLog(CommDecoder.class);

    /**
     * 프레임의 첫번째 바이트를 체크해 올바른 프레임인지 체크한다
     * GeneralFrame = 0x5E
     * PLCFrame = 0xE1
     * AMUFrame = 0xAA
     *
     * @param buff
     * @param pos
     * @return
     */
    private boolean isValidFrame(IoBuffer buff,int pos)
    {
        if(buff.get(pos) != GeneralDataConstants.SOH && 
                buff.get(pos) != PLCDataConstants.SOF && 
                buff.get(pos) != AMUGeneralDataConstants.SOH) {
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
                log.debug("data["+in.getHexDump()+"] is bypass Frame");
                // in.position(in.limit());
                byte[] b = new byte[in.limit()];
                in.rewind();
                in.get(b, 0, b.length);
                out.write(b);
                return true;
            }
            
            //--------------------
            //  AMU General Frame
            //--------------------
            if(FrameUtil.isAmuGeneralDataFrame(in.get(startPos))){
                FMPAMUDecoder d = new FMPAMUDecoder();
                return d.doDecode(session, in, out, startPos);
            }
            //-------------
            //  PLC Frame
            //-------------
            else if(FrameUtil.isPLCDataFrame(in.get(startPos))){
                FMPPLCDecoder d = new FMPPLCDecoder();
                return d.doDecode(session, in, out, startPos);
            }
        	//-----------------
        	//	General Frame
        	//-----------------
        	else {
        	    FMPGeneralDecoder d = new FMPGeneralDecoder();
        	    return d.doDecode(session, in, out, startPos);
        	}
        } catch(Exception ex)
        {
            log.error("FMPDecoder::decode failed : ", ex);
            throw new ProtocolDecoderException(ex.getMessage());
        }
    }
}
