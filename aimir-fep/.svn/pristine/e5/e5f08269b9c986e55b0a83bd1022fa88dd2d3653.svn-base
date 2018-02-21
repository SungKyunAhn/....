package com.aimir.fep.protocol.nip.server;

import com.aimir.constants.CommonConstants.ThresholdName;
import com.aimir.fep.protocol.fmp.frame.AMUGeneralDataConstants;
import com.aimir.fep.protocol.nip.common.MultiDataProcessor;
import com.aimir.fep.util.FrameUtil;
import com.aimir.fep.util.threshold.CheckThreshold;

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
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class NiProtocolDecoder extends CumulativeProtocolDecoder //implements ProtocolDecoder
{
    private static Log log = LogFactory.getLog(NiProtocolDecoder.class);
    
    @Override
    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        int cnt = 0;
        int pos = 0;
        int tempPos = 0;   
        
        if(session.containsAttribute("cnt")){
            cnt = Integer.parseInt(String.valueOf(session.getAttribute("cnt")));
        }else{
            session.setAttribute("cnt", cnt);
        }
        
        if(session.containsAttribute("pos")){
            pos = Integer.parseInt(String.valueOf(session.getAttribute("pos")));
        }else{
            session.setAttribute("pos", pos);
        }
        
        if(session.containsAttribute("tempPos")){
            tempPos = Integer.parseInt(String.valueOf(session.getAttribute("tempPos")));
        }else{
            session.setAttribute("tempPos", tempPos);
        }
        
        pos = tempPos;
        tempPos = in.limit();
        int limit = in.limit();
        try {
            log.debug("1111 Received [" + session.getRemoteAddress() + "] pos=" + pos + ", tempPos=" + tempPos + ", limit=" + limit + ", cntt=" + cnt + " : " + in.getHexDump());

            /*
            if(pos == 0){
                //Check Crc
                if(!futil.checkAMUCRC(in)){
                    throw new Exception("[NiProtocolDecoder][CRC] check fail");
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
            if(cons.framePanding == 1){
                log.debug("false");
                return false;
            }
            else{
            */

            // INSERT START SP-193
            if (pos == 0) {
                log.debug("checkAMUCRC()");
                //Check Crc
                byte[] crc = new byte[AMUGeneralDataConstants.FRAME_CRC_LEN];
                int len     = in.limit()- AMUGeneralDataConstants.FRAME_CRC_LEN;
                
                crc[0] = in.get(len);
                crc[1] = in.get(len+1);
                session.setAttribute("crc", crc);
                if (!FrameUtil.checkAMUCRC(in)) {
                    CheckThreshold.updateCount(session.getRemoteAddress().toString(), ThresholdName.CRC);
                }
            }
            // INSERT END SP-193

            byte[] bx = new byte[in.limit()];
            in.rewind();
            in.get(bx, 0, bx.length);
            IoBuffer data = IoBuffer.wrap(bx);
            if (pos > 0) {
                /**************************
                 * Temp code. 
                 **************************/
                log.debug("여기 심과장에게 알려주세요 !!!! 222-1 [" + session.getRemoteAddress() + "] pos=" + pos + ", tempPos=" + tempPos + ", limit=" + limit + ", cnt=" + cnt);
                log.debug("여기 심과장에게 알려주세요 !!!! 222-1 [" + session.getRemoteAddress() + "] pos=" + pos + ", tempPos=" + tempPos + ", limit=" + limit + ", cnt=" + cnt);
                log.debug("여기 심과장에게 알려주세요 !!!! 222-1 [" + session.getRemoteAddress() + "] pos=" + pos + ", tempPos=" + tempPos + ", limit=" + limit + ", cnt=" + cnt);
                log.debug("여기 심과장에게 알려주세요 !!!! 222-1 [" + session.getRemoteAddress() + "] pos=" + pos + ", tempPos=" + tempPos + ", limit=" + limit + ", cnt=" + cnt);
                log.debug("여기 심과장에게 알려주세요 !!!! 222-1 [" + session.getRemoteAddress() + "] pos=" + pos + ", tempPos=" + tempPos + ", limit=" + limit + ", cnt=" + cnt);
                
                IoBuffer rcvData = IoBuffer.wrap(new MultiDataProcessor().chgMFtoSF(data.array(), cnt, pos, limit));
                out.write(rcvData);
                
                log.debug("여기 심과장에게 알려주세요 !!!! 222-2 [" + session.getRemoteAddress() + "] pos=" + pos + ", tempPos=" + tempPos + ", limit=" + limit + ", cnt=" + cnt);
                log.debug("여기 심과장에게 알려주세요 !!!! 222-2 [" + session.getRemoteAddress() + "] pos=" + pos + ", tempPos=" + tempPos + ", limit=" + limit + ", cnt=" + cnt);
                log.debug("여기 심과장에게 알려주세요 !!!! 222-2 [" + session.getRemoteAddress() + "] pos=" + pos + ", tempPos=" + tempPos + ", limit=" + limit + ", cnt=" + cnt);
                log.debug("여기 심과장에게 알려주세요 !!!! 222-2 [" + session.getRemoteAddress() + "] pos=" + pos + ", tempPos=" + tempPos + ", limit=" + limit + ", cnt=" + cnt);
            } else {
                out.write(data);
            }
            
            log.debug("333 [" + session.getRemoteAddress() + "] pos=" + pos + ", tempPos=" + tempPos + ", limit=" + limit + ", cnt=" + cnt);
            
            cnt = 0;
            pos = 0;
            tempPos = 0;
            
            session.setAttribute("cnt", cnt);
            session.setAttribute("pos", pos);
            session.setAttribute("tempPos", tempPos);
            
            return true;
            // }
        } catch (Exception ex) {
            log.error("NiDecoder::decode failed : ", ex);
            throw new ProtocolDecoderException(ex.getMessage());
        }

    }
    //    @Override
    //    protected boolean doDecode(IoSession session, IoBuffer in,
    //          ProtocolDecoderOutput out) throws Exception {
    //      // TODO Auto-generated method stub
    //       try
    //       { 
    //           log.debug("Received [" + session.getRemoteAddress()
    //                   + "] : "+in.limit()+" :"+in.getHexDump());
    //               byte[] bx = new byte[in.limit()];
    //               in.get(bx,0,bx.length);
    //               IoBuffer data = IoBuffer.wrap(bx);
    //               out.write(data);
    //       } catch(Exception ex)
    //       {
    //           log.error("NiDecoder::decode failed : ", ex);
    //           throw new ProtocolDecoderException(ex.getMessage());
    //       }
    //      return true;
    //    }
}
