package com.aimir.fep.protocol.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderException;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.aimir.fep.protocol.security.frame.AuthDataFrame;
import com.aimir.fep.protocol.security.frame.AuthGeneralFrame;
/**
 *  Encode Authenticate Data Frame.
 *
 * @author 
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */

public class AuthEncoder extends ProtocolEncoderAdapter
{
    private static Log log = LogFactory.getLog(AuthEncoder.class);

    public void encode( IoSession session, Object message,
                       ProtocolEncoderOutput out )
            throws ProtocolEncoderException
    {
        try
        {
        	log.info("Encode [Start]");
//            if(message instanceof AuthGeneralFrame )
//            {
//            	AuthGeneralFrame frame = (AuthGeneralFrame)message;
//            	        
//                byte[] bx = frame.encode();
//                IoBuffer buf = null;
// 
//                buf = IoBuffer.allocate(bx.length);
//                buf.put(bx,0,bx.length);
//                buf.flip();
//                log.info("Sended : ["+session.getRemoteAddress()
//                            +"] " + buf.limit() + " : "
//                            + buf.getHexDump());
//                out.write(buf);
//                
//            	AuthISIoTClient  iotCli = (AuthISIoTClient)session.getAttribute("IsIoTCli");
//            	if ( iotCli != null){
//            		iotCli.addSendedAuthFrameTypes(frame.foFrameType);
//            	}
//            }
	       	if(message instanceof byte[])
	           {
	       	  IoBuffer buffer = IoBuffer.allocate(((byte[])message).length);
	       	  	  buffer.put((byte[])message);
	 	          buffer.flip();
	 	          System.out.println("Sended["+session.getRemoteAddress()
		                 +"] : " + buffer.limit() + " : " 
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
