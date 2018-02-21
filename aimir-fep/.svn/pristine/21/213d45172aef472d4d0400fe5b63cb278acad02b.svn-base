package com.aimir.fep.protocol.security;


import com.aimir.fep.protocol.fmp.frame.GeneralDataConstants;
import com.aimir.fep.protocol.fmp.frame.GeneralDataFrame;
import com.aimir.fep.protocol.security.frame.AuthFrameConstants;
import com.aimir.fep.util.DataUtil;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderException;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 * Decodes a Authentication General Data Frame into MCU Input Stream.
 *
 * @author
 * @version 
 */
public class AuthGeneralDecoder
{
    private static Log log = LogFactory.getLog(AuthGeneralDecoder.class);


    // check remain frame
    private boolean remainFrame(IoBuffer in) throws Exception
    {
        int totallen = 0;
        //
        int pos = in.position();
        
        //
        while(in.remaining() > AuthFrameConstants.HEADER_LEN)
        {
        	byte lenfield[] = new byte[AuthFrameConstants.PAYLOAD_LENGTH_LEN];
            DataUtil.arraycopy(in,in.position()+AuthFrameConstants.PAYLOAD_LENGTH_OFFSET, lenfield,0,
            		lenfield.length);           
            totallen = DataUtil.getIntToBytes(lenfield)
                + AuthFrameConstants.HEADER_LEN
                + AuthFrameConstants.TAIL_LEN;

            log.debug("pos[" + in.position() + "] totallen["+totallen+"] in.remaining[" + in.remaining() + "]");
            if(in.remaining() == totallen)
            {
                in.position(pos);
                return false;
            }
            else if (in.remaining() < totallen){
                in.position(pos);
                return true;
            }
            // 
            in.position(in.position()+totallen);
        }
        in.position(pos);
        return true;
    }
    

    /**
     * @param buff
     * @param pos
     * @return
     */
    private int getTotalLength(IoBuffer buff,int pos)
    {
    	byte lenfield[] = new byte[AuthFrameConstants.PAYLOAD_LENGTH_LEN];
        DataUtil.arraycopy(buff,pos+AuthFrameConstants.PAYLOAD_LENGTH_OFFSET,lenfield,0,
        		lenfield.length);
        int len = DataUtil.getIntToBytes(lenfield)
                + AuthFrameConstants.HEADER_LEN
                + AuthFrameConstants.TAIL_LEN;
        log.debug("Frame Payload Lenth = " + len);
        return len;

    }

//    /**
//     * decode input stream
//     *
//     * @param session <code>ProtocolSession</code> session
//     * @param in <code>ByteBuffer</code> input stream
//     * @param out <code>ProtocolDecoderOutput</code> save decoding frame
//     * @throws  ProtocolViolationException
//     */
//    public boolean doDecodeOld(IoSession session, IoBuffer in, ProtocolDecoderOutput out, int startPos)
//    throws ProtocolDecoderException
//    {
//        try
//        {
//            int totallen = 0;
//
//            totallen = getTotalLength(in,startPos);
//            log.info("decode : TOTAL_LEN[" + totallen + "] POS[" + startPos + "] IN_LIMIT["+ in.limit() + "]");
//
//            in.position(startPos);
//            if((totallen+startPos) == in.limit())
//            {
//                decodeFrame(session,in.slice(),out);
//                in.position(in.limit());
//                return true;
//            }
//            else if((totallen+startPos) < in.limit())
//            {
//                int limit = in.limit();
//                in.limit(startPos+totallen);
//                decodeFrame(session,in.slice(),out);
//
//                in.position(startPos+totallen);
//                in.limit(limit);
//                return !remainFrame(in);
//            }
//            else {
//                log.debug("[Less] Buffer Length < Frame Total Length");
//                return false;
//            }
//        } catch(Exception ex)
//        {
//            log.error("AuthDecorder::decode failed : ", ex);
//            throw new ProtocolDecoderException(ex.getMessage());
//        }
//    }
    /**
     * @param session
     * @param in
     * @param out
     * @param startPos
     * @return
     * @throws ProtocolDecoderException
     */
    public boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out, int startPos)
    throws ProtocolDecoderException
    {
        try
        {
            int totallen = 0;

            totallen = getTotalLength(in,startPos);
            log.info("decode : TOTAL_LEN[" + totallen + "] POS[" + startPos + "] IN_LIMIT["+ in.limit() + "]");

            in.position(startPos);
            if((totallen+startPos) == in.limit())
            {
                //decodeFrame(session,in.slice(),out);
            	out.write(in.slice());
                in.position(in.limit());
                return true;
            }
            else if((totallen+startPos) < in.limit())
            {
                int limit = in.limit();
                in.limit(startPos+totallen);
               // decodeFrame(session,in.slice(),out);
                out.write(in.slice());
                in.position(startPos+totallen);
                in.limit(limit);
                return !remainFrame(in);
            }
            else {
                log.debug("[Less] Buffer Length < Frame Total Length");
                return false;
            }
        } catch(Exception ex)
        {
            log.error("AuthDecorder::decode failed : ", ex);
            throw new ProtocolDecoderException(ex.getMessage());
        }
    } 
    class DecodeFrameThread implements Runnable {
        IoSession session;
        IoBuffer in;
        ProtocolDecoderOutput out;
        
        DecodeFrameThread(IoSession session, IoBuffer in, ProtocolDecoderOutput out) {
            this.session = session;
            this.in = in;
            this.out = out;
        }

        @Override
        public void run() {
         // log.debug("HEX[" + in.getHexDump() + "] POSTION[" + in.position() + "] LIMIT[" + in.limit() + "]");
//            GeneralDataFrame frame = null;
//            try {
//                //if control data frame
//                if(FrameUtil.isControlDataFrame(in.get(2)))
//                {
//                    Object ns_obj = session.getAttribute("nameSpace");
//                    String ns = ns_obj!=null? (String)ns_obj:null;
//                    frame = GeneralDataFrame.decode(ns,in.rewind());
//                    if (frame != null) out.write(frame);
//                    return;
//                }
//                
//                // if multi-frame
//                if(!FrameUtil.isSingleFrame(in.get(2)))
//                {
//                    SlideWindow sw = (SlideWindow)session.getAttribute("slidewindow");
//                    if(sw == null)
//                    {
//                        sw = new SlideWindow();
//                        session.setAttribute("slidewindow", sw);
//                    }
//                    sw.put(in);
//                }
//                // if single frame
//                else
//                {
//                    boolean isValidFrameCrc = FrameUtil.checkCRC(in);
//                    if(!isValidFrameCrc)
//                    {
//                        log.error("CRC check failed Received Data ["
//                            + session.getRemoteAddress()+ "] :" + in.getHexDump());
//                        ControlDataFrame nak = FrameUtil.getNAK(in.get(1));
//                        session.write(nak);
//                        return;
//                    }
//    
//                    Object ns_obj = session.getAttribute("nameSpace");
//                    String ns = ns_obj!=null? (String)ns_obj:null;
//                    frame = GeneralDataFrame.decode(ns,in);
//                    if(frame.isServiceDataFrame() &&
//                            frame.getSvc() != GeneralDataConstants.SVC_C)
//                    {
//                        log.debug("send ACK seq=" + new Integer(frame.getSequence()));
//                        session.write(FrameUtil.getACK(frame));
//                    }
//                }
//                if (frame != null) out.write(frame);
//            }
//            catch (Exception e) {
//                log.error(e, e);
//            }
        }
        
    }
}
