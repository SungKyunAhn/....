package com.aimir.fep.protocol.reversegprs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.aimir.fep.protocol.fmp.frame.GeneralDataFrame;
import com.aimir.fep.util.FrameUtil;

public class CommEncoder implements ProtocolEncoder {

    private static Log log = LogFactory.getLog(CommEncoder.class);
    
	@Override
	public void dispose(IoSession session) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void encode(IoSession session, Object message,
			ProtocolEncoderOutput out) throws Exception {

		if(message instanceof GeneralDataFrame)
        {
            GeneralDataFrame frame = (GeneralDataFrame)message;
            byte[] bx = frame.encode();
            byte[] crc = FrameUtil.getCRC(bx);
            IoBuffer buff = IoBuffer.allocate(bx.length
                    + crc.length);
            buff.put(bx);
            buff.put(crc);
            buff.flip();
            log.info("Sended : ["+session.getRemoteAddress()
                    +"] " + buff.limit() + " : "
                    + buff.getHexDump());
            out.write(buff);
        }else if(message instanceof IoBuffer) 
        { 
            IoBuffer buffer =  (IoBuffer)message; 
            log.info("Sended : ["+session.getRemoteAddress()
                    +"] " + buffer.limit() + " : "
                    + buffer.getHexDump());
            out.write(buffer); 
        }
        else if(message instanceof byte[])
        {
            byte[] bx = (byte[])message;
            IoBuffer buffer = IoBuffer.allocate(bx.length);
            buffer.put(bx);
            buffer.flip();
            log.info("Sended : ["+session.getRemoteAddress()
                    +"] " + buffer.limit() + " : "
                    + buffer.getHexDump());
            out.write(buffer); 
        }
	}

}
