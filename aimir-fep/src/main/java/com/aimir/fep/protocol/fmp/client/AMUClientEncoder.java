package com.aimir.fep.protocol.fmp.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderException;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.aimir.fep.protocol.fmp.frame.AMUGeneralDataFrame;

/**
 * Encodes AMU Communication Stream  into AMU GeneralDataFrame.
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 2. 19. 오후 6:20:13$
 */
public class AMUClientEncoder extends ProtocolEncoderAdapter{

	private Log log = LogFactory.getLog(AMUClientEncoder.class);
	
	public void encode(IoSession session, Object message,
			ProtocolEncoderOutput out) throws Exception 
	{ 
        try 
        {
        	log.info("############ AMUClinetDecoder Encode [Start] ############");
            
        	if(message instanceof AMUGeneralDataFrame)
            {
                AMUGeneralDataFrame frame = (AMUGeneralDataFrame)message; 
                byte[] bx = frame.encode(); 
      
                IoBuffer buf = null;

                buf = IoBuffer.allocate(bx.length); 
                buf.put(bx); 
                buf.flip(); 
                log.debug("Sended["+session.getRemoteAddress()
                       +"] : " + buf.limit() + " : " 
                        + buf.getHexDump()); 
                out.write(buf);  
            }
            else if(message instanceof IoBuffer) 
            { 
            	IoBuffer buffer =  (IoBuffer)message; 
                log.debug("Sended["+session.getRemoteAddress()
                       +"] : " + buffer.limit() + " : " 
                        + buffer.getHexDump()); 
                out.write(buffer); 
            }
            log.info("############ AMUClinetDecoder Encode [End] ############");
        }catch(Exception ex)
        {
            log.error("AMUClientEncoder encode failed " + message, ex); 
            new ProtocolEncoderException( ex.getMessage());
        }
    }
}


