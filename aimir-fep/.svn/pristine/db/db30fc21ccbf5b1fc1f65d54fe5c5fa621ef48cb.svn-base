package com.aimir.fep.protocol.fmp.server;

import com.aimir.constants.CommonConstants.ThresholdName;
import com.aimir.fep.protocol.fmp.common.SlideWindow;
import com.aimir.fep.protocol.fmp.frame.ControlDataFrame;
import com.aimir.fep.protocol.fmp.frame.GeneralDataConstants;
import com.aimir.fep.protocol.fmp.frame.GeneralDataFrame;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.FrameUtil;
import com.aimir.fep.util.threshold.CheckThreshold;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderException;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 * Decodes a General Data Frame into MCU Input Stream.
 *
 * @author elevas (elevas@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2013-03-25 15:59:15 +0900 $,
 */
public class FMPGeneralDecoder
{
    private static Log log = LogFactory.getLog(FMPGeneralDecoder.class);
    private byte[] lengthField =
        new byte[GeneralDataConstants.LENGTH_LEN];

	// decode frame buffer stream
    private void decodeFrame(IoSession session,
            IoBuffer in, ProtocolDecoderOutput out) throws Exception
    {
        GeneralDataFrame frame = null;
        String version = FMPProperty.getProperty("protocol.version");
        log.info("version[" + version+"]");
        log.info("buffer limit [" + in.limit() + "]" + "position [" + in.position() +"]");
        
        //if control data frame
        if(FrameUtil.isControlDataFrame(in.get(2)))
        {
        	log.debug("controlFrame");
            Object ns_obj = session.getAttribute("nameSpace");
            String ns = ns_obj!=null? (String)ns_obj:null;
            frame = GeneralDataFrame.decode(ns,in.rewind());
            if (frame != null) out.write(frame);
            return;
        }
        
        // if multi-frame
        if(!FrameUtil.isSingleFrame(in.get(2)))
        {
        	log.debug("multiFrame");
            SlideWindow sw = (SlideWindow)session.getAttribute("slidewindow");
            if(sw == null)
            {
                sw = new SlideWindow();
                session.setAttribute("slidewindow", sw);
            }
            
            if(FrameUtil.isCryptFrame(in.get(2)) && "0103".equals(version)) { // 1.3v
            	sw.put(GeneralDataFrame.decryptFrame(in));
            } else {
            	sw.put(in);
            }            
        }
        // if single frame
        else
        {
        	log.debug("singleFrame");
            boolean isValidFrameCrc = FrameUtil.checkCRC(in);
            if(!isValidFrameCrc)
            {
                log.error("CRC check failed Received Data ["
                    + session.getRemoteAddress()+ "] :" + in.getHexDump());
                ControlDataFrame nak = FrameUtil.getNAK(in.get(1));
                session.write(nak);
                CheckThreshold.updateCount(session.getRemoteAddress().toString(), ThresholdName.CRC); // INSERT SP-193
                return;
            }

            Object ns_obj = session.getAttribute("nameSpace");
            String ns = ns_obj!=null? (String)ns_obj:null;
            
            if(FrameUtil.isCryptFrame(in.get(2)) && "0103".equals(version)) { // 1.3v
            	in = GeneralDataFrame.decryptFrame(in);
            }
            
            frame = GeneralDataFrame.decode(ns,in);
            if(frame.isServiceDataFrame() &&
                    frame.getSvc() != GeneralDataConstants.SVC_C)
            {
                log.debug("send ACK seq=" + new Integer(frame.getSequence()));
                session.write(FrameUtil.getACK(frame));
            }
        }
        if (frame != null) out.write(frame);
    }

    // check remain frame
    private boolean remainFrame(IoBuffer in) throws Exception
    {
        int totallen = 0;
        // 시작위치를 기억한다.
        int pos = in.position();
        
        // 남은 길이가 헤더보다 크면 여러개의 프레임을 처리한다.
        while(in.remaining() > GeneralDataConstants.HEADER_LEN)
        {
            DataUtil.arraycopy(in,in.position()+3,lengthField,0,
                    lengthField.length);
            DataUtil.convertEndian(lengthField);
            totallen = DataUtil.getIntToBytes(lengthField)
                + GeneralDataConstants.HEADER_LEN
                + GeneralDataConstants.TAIL_LEN;
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
            // 현재 위치에서 totallen 만큼 포지션을 이동한다.
            in.position(in.position()+totallen);
        }
        in.position(pos);
        return true;
    }
    
    /*
     * 총 패킷 길이를 buff에서 데이타 길이를 가져온 후 헤더(8), 테일(2)를 더하여 구한다.
     */
    private int getTotalLength(IoBuffer buff,int pos)
    {
        DataUtil.arraycopy(buff,pos+3,lengthField,0,
                lengthField.length);
        DataUtil.convertEndian(lengthField);
        return DataUtil.getIntToBytes(lengthField)
            + GeneralDataConstants.HEADER_LEN
            + GeneralDataConstants.TAIL_LEN;
    }

    /**
     * decode input stream
     *
     * @param session <code>ProtocolSession</code> session
     * @param in <code>ByteBuffer</code> input stream
     * @param out <code>ProtocolDecoderOutput</code> save decoding frame
     * @throws  ProtocolViolationException
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
                decodeFrame(session,in.slice(),out);
                in.position(in.limit());
                return true;
            }
            else if((totallen+startPos) < in.limit())
            {
                int limit = in.limit();
                in.limit(startPos+totallen);
                decodeFrame(session,in.slice(),out);
                // 다음 프레임 시작 위치로 설정한다.
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
            log.error("FMPDecoder::decode failed : ", ex);
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
            GeneralDataFrame frame = null;
            try {
                //if control data frame
                if(FrameUtil.isControlDataFrame(in.get(2)))
                {
                    Object ns_obj = session.getAttribute("nameSpace");
                    String ns = ns_obj!=null? (String)ns_obj:null;
                    frame = GeneralDataFrame.decode(ns,in.rewind());
                    if (frame != null) out.write(frame);
                    return;
                }
                
                // if multi-frame
                if(!FrameUtil.isSingleFrame(in.get(2)))
                {
                    SlideWindow sw = (SlideWindow)session.getAttribute("slidewindow");
                    if(sw == null)
                    {
                        sw = new SlideWindow();
                        session.setAttribute("slidewindow", sw);
                    }
                    sw.put(in);
                }
                // if single frame
                else
                {
                    boolean isValidFrameCrc = FrameUtil.checkCRC(in);
                    if(!isValidFrameCrc)
                    {
                        log.error("CRC check failed Received Data ["
                            + session.getRemoteAddress()+ "] :" + in.getHexDump());
                        ControlDataFrame nak = FrameUtil.getNAK(in.get(1));
                        session.write(nak);
                        CheckThreshold.updateCount(session.getRemoteAddress().toString(), ThresholdName.CRC); // INSERT SP-193
                        return;
                    }
    
                    Object ns_obj = session.getAttribute("nameSpace");
                    String ns = ns_obj!=null? (String)ns_obj:null;
                    frame = GeneralDataFrame.decode(ns,in);
                    if(frame.isServiceDataFrame() &&
                            frame.getSvc() != GeneralDataConstants.SVC_C)
                    {
                        log.debug("send ACK seq=" + new Integer(frame.getSequence()));
                        session.write(FrameUtil.getACK(frame));
                    }
                }
                if (frame != null) out.write(frame);
            }
            catch (Exception e) {
                log.error(e, e);
            }
        }
        
    }
}
