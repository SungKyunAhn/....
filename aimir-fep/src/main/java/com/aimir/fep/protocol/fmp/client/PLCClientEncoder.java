package com.aimir.fep.protocol.fmp.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderException;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.aimir.fep.meter.parser.plc.PLCDataConstants;
import com.aimir.fep.meter.parser.plc.PLCDataFrame;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FrameUtil;
/**
 * Encodes MCU Communication Stream  into General Data Frame.
 *
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class PLCClientEncoder extends ProtocolEncoderAdapter
{
    private static Log log = LogFactory.getLog(PLCClientEncoder.class);
        /**
     * encode PLC Data Frame
     *
     * @param session <code>ProtocolSession</code> session
     * @param message <code>Object</code> PLCDataFrame
     * @param out <code>ProtocolEncoderOutput</code> save encoded byte stream
     * @throws ProtocolViolationException  indicating that is was error of Encode
     */
    public void encode(IoSession session, Object message,
                       ProtocolEncoderOutput out )
    throws ProtocolEncoderException
    {
        try
        {
        	log.info("Encode [Start]");
            if(message instanceof PLCDataFrame)
            {
            	byte[] bx = ((PLCDataFrame)message).encode();
            	byte[] crc = FrameUtil.getCRC(bx, 1, bx.length-1);
            	IoBuffer buffer = IoBuffer.allocate(bx.length+3);
            	buffer.put(bx);
            	buffer.put(crc);
            	buffer.put(PLCDataConstants.EOF);
            	buffer.flip();
            	log.debug("Sended["+session.getRemoteAddress()
                        +"] Command["+DataUtil.getPLCCommandStr(((PLCDataFrame)message).getCommand())+"]: " + buffer.limit() + " : "
                         + buffer.getHexDump());
            	out.write(buffer);
            }else {
            	log.error("Sended Object is not PLCDataFrame!!");
            	new Exception("Sended Object is not PLCDataFrame!!");
            }
            log.info("Encode [End]");
        }catch(Exception ex)
        {
            log.error("encode failed " + message, ex);
            new ProtocolEncoderException( ex.getMessage());
        }
    }
}
