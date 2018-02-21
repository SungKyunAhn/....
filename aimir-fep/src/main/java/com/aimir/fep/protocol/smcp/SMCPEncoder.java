package com.aimir.fep.protocol.smcp;

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
 * Encodes SMCP Frame.
 *
 * @author goodjob (goodjob@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2014-09-30 10:00:00 +0900 $,
 */

public class SMCPEncoder extends ProtocolEncoderAdapter
{
    private static Log log = LogFactory.getLog(SMCPEncoder.class);

    /**
     * encode SMCP Protocol Frame
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
 
            log.info("Encode [End]");
        }
        catch(Exception ex)
        {
            log.error("encode failed " + message, ex);
            new ProtocolEncoderException( ex.getMessage());
        }
    }
}
