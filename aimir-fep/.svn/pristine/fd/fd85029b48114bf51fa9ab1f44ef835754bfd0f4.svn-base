package com.aimir.fep.protocol.nip.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderException;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 * stream to ni protocol
 * 
 * @author DJ Kim
 * @version $Rev: 1 $, $Date: 2016-04-21 15:59:15 +0900 $,
 */
public class NiClientEncoder implements ProtocolEncoder
{
    private static Log log = LogFactory.getLog(NiClientEncoder.class);
    
    @Override
    public void encode(IoSession session, Object message,
    		ProtocolEncoderOutput out) throws Exception {
    	  // TODO Auto-generated method stub
	      try 
	      {
	      	if(message instanceof byte[])
	          {
	      		IoBuffer buffer = IoBuffer.allocate(((byte[])message).length);
	      	    buffer.put((byte[])message);
		        buffer.flip();
	            log.debug("Sended["+session.getRemoteAddress()
	                     +"] : " + buffer.limit() + " : " 
	                      + buffer.getHexDump()); 
	            out.write(buffer);
	          }
	      }catch(Exception ex)
	      {
	          log.error("encode failed " + message, ex); 
	          new ProtocolEncoderException( ex.getMessage());
	      }
	      session.setAttribute("State", "Ok");
    }
    
    @Override
    public void dispose(IoSession session) throws Exception {
    	// TODO Auto-generated method stub
    	
    }
}
