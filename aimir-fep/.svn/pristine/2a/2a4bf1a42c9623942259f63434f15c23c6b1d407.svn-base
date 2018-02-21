package com.aimir.fep.protocol.fmp.server;

import com.aimir.fep.meter.parser.plc.PLCDataConstants;
import com.aimir.fep.protocol.fmp.frame.AMUGeneralDataConstants;
import com.aimir.fep.protocol.fmp.frame.GeneralDataConstants;
import com.aimir.fep.protocol.fmp.frame.GeneralDataFrame;
import com.aimir.fep.util.FrameUtil;
import com.aimir.fep.util.Hex;
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
 * @author elevas (elevas@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2013-03-25 15:59:15 +0900 $,
 */
public class FMPCumulativeDecoder extends CumulativeProtocolDecoder
{
    private static Log log = LogFactory.getLog(FMPCumulativeDecoder.class);

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
                log.error("data["+in.getHexDump()+"] is invalid Frame");
                // in.position(in.limit());
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
