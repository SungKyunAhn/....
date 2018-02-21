package com.aimir.fep.protocol.nip.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderException;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.aimir.fep.protocol.nip.common.MultiDataProcessor;
import com.aimir.fep.util.FrameUtil;

/**
 * Decodes NI into input stream
 * 
 * @author DJ Kim
 * @version $Rev: 1 $, $Date: 2016-04-21 15:59:15 +0900 $,
 */
public class NiClientDecoder extends CumulativeProtocolDecoder //implements ProtocolDecoder
{
    private static Log log = LogFactory.getLog(NiClientDecoder.class);
    
    private MultiDataProcessor cons;
    private int cnt=0;
    private int pos=0;
    private int tempPos=0;
    
    @Override
    protected boolean doDecode(IoSession session, IoBuffer in,
            ProtocolDecoderOutput out) throws Exception
    {
        pos = tempPos;
        tempPos = in.limit();
        int limit = in.limit();
        try
        { 
            log.debug("Received [" + session.getRemoteAddress()
	               + "] : "+in.limit()+" :"+in.getHexDump());
            if(pos == 0){//first
                //Check Crc
                if(FrameUtil.checkAMUCRC(in)){
                    throw new Exception("[NiClientDecoder][CRC] check fail");
                }
                cons =  new MultiDataProcessor();
                cons.decode(in.array(),1,limit);
            }
            else {
                byte[] b = new byte[limit-pos];
                System.arraycopy(in.array(), pos, b, 0, b.length);
                cons.decode(b,2,limit);
            }
            cnt++;
            if(cons.getFramePanding() == 1){
                log.debug("FRAME_PANDING[false]");
                return false;
            }
            else{
                byte[] bx = new byte[in.limit()];
                in.rewind();
                in.get(bx,0,bx.length);
                IoBuffer data = IoBuffer.wrap(bx);
                if(pos>0){
                    IoBuffer rcvData = IoBuffer.wrap(cons.chgMFtoSF(data.array(),cnt,pos,limit));
                    out.write(rcvData);
                }
                else{
                    out.write(data);
                }
                cnt=0;
                pos=0;
                tempPos=0;
                cons=null;
                return true;
            }
        }
        catch(Exception ex) {
            log.error("NiDecoder::decode failed : ", ex);
            throw new ProtocolDecoderException(ex.getMessage());
        }
    }
    
//    @Override
//    protected boolean doDecode(IoSession session, IoBuffer in,
//    		ProtocolDecoderOutput out) throws Exception {
//    	// TODO Auto-generated method stub
//    	
//       try
//       { 
//           System.out.println("Received [" + session.getRemoteAddress()
//                   + "] : "+in.limit()+" :"+in.getHexDump());
//               byte[] bx = new byte[in.limit()];
//               in.get(bx,0,bx.length);
//               IoBuffer data = IoBuffer.wrap(bx);
//               out.write(data);
//               
//       } catch(Exception ex)
//       {
//           log.error("NiDecoder::decode failed : ", ex);
//           throw new ProtocolDecoderException(ex.getMessage());
//       }
//    	return true;
//    }
}
