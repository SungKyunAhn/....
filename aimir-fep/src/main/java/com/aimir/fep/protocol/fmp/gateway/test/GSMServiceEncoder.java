package com.aimir.fep.protocol.fmp.gateway.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderException;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 * Encodes GSM Service.
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2006-10-16 15:59:15 +0900 $,
 */
public class GSMServiceEncoder implements ProtocolEncoder
{
    private static Log log = LogFactory.getLog(GSMServiceEncoder.class);

    /**
     * encode GSMService Data
     *
     * @param session <code>IoSession</code> session
     * @param message <code>Object</code> GeneralDataFrame or ByteBuffer
     * @param out <code>ProtocolEncoderOutput</code> save encoded byte stream
     * @throws ProtocolViolationException  indicating that is was error of Encode
     */
    public void encode(IoSession session, Object message,
                       ProtocolEncoderOutput out )
            throws ProtocolEncoderException
    { 
        try 
        { 
            if(message instanceof IoBuffer)
            {
                IoBuffer buffer =  (IoBuffer)message; 
                log.debug("Sended["+session.getRemoteAddress() 
                        +"] : " + buffer.limit() + " : " 
                        + buffer.getHexDump()); 
                out.write(buffer); 
            } else if(message instanceof byte[])
            {
                byte[] bx = (byte[])message;
                IoBuffer buffer = IoBuffer.allocate(bx.length);
                buffer.put(bx);
                buffer.flip();
                log.debug("Sended["+session.getRemoteAddress() 
                        +"] : " + buffer.limit() + " : " 
                        + buffer.getHexDump()); 
                out.write(buffer); 
            } else
            {
                log.debug("Sended Message  does not Supported format");
            }
        }catch(Exception ex)
        {
            log.error("encode failed " + message, ex); 
            new ProtocolEncoderException( ex.getMessage());
        }
    }

    public void dispose(IoSession session) throws Exception
    {
        // TODO Auto-generated method stub
        
    }
}
