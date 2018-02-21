package com.aimir.fep.protocol.fmp.server;

import com.aimir.fep.protocol.fmp.frame.AMUFrameControl;
import com.aimir.fep.protocol.fmp.frame.AMUGeneralDataConstants;
import com.aimir.fep.protocol.fmp.frame.AMUGeneralDataFrame;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FrameUtil;
import com.aimir.fep.util.Hex;

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
public class FMPAMUDecoder
{
    private static Log log = LogFactory.getLog(FMPAMUDecoder.class);
    private byte[] AMUlengthField =
        new byte[AMUGeneralDataConstants.PAYLOAD_LEN];

    /*
     * AMUGeneralDataFrame Data Decode frame buffer stream
     * @param session
     * @param in
     * @return AMUGeneralDataFrame
     * @throws Exception
     */
    private AMUGeneralDataFrame decodeFrame(IoSession session,
            IoBuffer in) throws Exception
    {
        AMUGeneralDataFrame frame = null;

        boolean isValidFrameCrc = FrameUtil.checkAMUCRC(in);
        log.debug("is ValidFrameCrc : " + isValidFrameCrc);

        if(!isValidFrameCrc)
        {
            log.error("AMU Frame CRC check failed Received Data ["
                + session.getRemoteAddress()+ "] :" + in.getHexDump());
            // When CRC Error ,Session Init
            FrameUtil.amuSessionInit(session);
            return null;
        }

        log.debug("Received Full Data : " + in.getHexDump());
        frame = AMUGeneralDataFrame.decode(in);

        if(FrameUtil.isAmuAck(frame))
        {
            log.debug("AMUGeneralDataFrame Send ACK - DIRECTION["+Hex.decode(frame.getSource_addr())+"]");
            session.write(FrameUtil.getAMUACK(session ,frame));
        }
        return frame;
    }

    private boolean remainFrame(IoBuffer in) throws Exception
    {
        int totallen = 0;
        int pos = in.position();
        
        byte[] fc = new byte[AMUGeneralDataConstants.FRAME_CTRL_LEN];
        fc[0] = in.get(1);
        fc[1] = in.get(2);
        AMUFrameControl afc = AMUFrameControl.decode(fc);
        int destAndSrcAddrLen = afc.getDestTypeLenth() + afc.getSourceTypeLenth();
        
        while(in.remaining() > AMUGeneralDataConstants.SOH_LEN + AMUGeneralDataConstants.FRAME_CTRL_LEN
                + AMUGeneralDataConstants.SEQ_LEN+ AMUGeneralDataConstants.PAYLOAD_LEN+destAndSrcAddrLen)
        {
            totallen= getTotalLength(in, pos);
            log.debug("pos[" + in.position() + "] totallen["+totallen+"] in.remaining[" + in.remaining() + "]");
            if(totallen > in.remaining())
            {
                in.position(pos);
                return false;
            }
            else if (in.remaining() < totallen) {
                in.position(pos);
                return true;
            }
            // 현재 위치에서 totallen 만큼 포지션을 이동한다.
            in.position(in.position()+totallen);
        }
        in.position(pos);
        return true;
    }
    
    /**
     * Get AMU Total Length
     * @param buff
     * @param pos
     * @return  int Total Length
     * @throws Exception
     */
    private int getTotalLength(IoBuffer buff,int pos) throws Exception
    {

        log.debug("buffer Size : "+ buff.limit());
        byte[] fc   = new byte[2];

        // Get Frame Control Field
        fc[0] = buff.get(1);
        fc[1] = buff.get(2);

        AMUFrameControl afc     = AMUFrameControl.decode(fc);
        int destAddressLen      = afc.getDestTypeLenth();
        int sourceAddressLen    = afc.getSourceTypeLenth();

        //log.debug("frame Control  : " + afc.toString());
        DataUtil.arraycopy(buff,
                    pos
                    + AMUGeneralDataConstants.SOH_LEN
                    + AMUGeneralDataConstants.FRAME_CTRL_LEN
                    + AMUGeneralDataConstants.SEQ_LEN
                    + destAddressLen
                    + sourceAddressLen
                    , AMUlengthField
                    , 0
                    , AMUlengthField.length);
        /*
        log.debug("  SOH(1)[" + AMUGeneralDataConstants.SOH_LEN +"]\n"
                 +"  CTRL_LEN(2)[" + AMUGeneralDataConstants.FRAME_CTRL_LEN +"]\n"
                 +"  SEQ_LEN(1)[" +AMUGeneralDataConstants.SEQ_LEN +"]\n"
                 +"  DestType["+afc.getDestAddrDesc()+"] : "+destAddressLen+"\n"
                 +"  SourceType["+afc.getSourceAddrDesc()+"] : "+sourceAddressLen+"\n"
                 +"  PayloadLen Length (2)[" + Hex.decode(AMUlengthField) +"]\n"
                 +"  PayLoadData Length [" + DataUtil.getIntTo2Byte(AMUlengthField) +"]\n"
                 +"  Frame CRC (2)  [" +AMUGeneralDataConstants.FRAME_CRC_LEN)+"]";
        */
        // Start of Header Length
        return  (AMUGeneralDataConstants.SOH_LEN
        // Frame Control Length
        + AMUGeneralDataConstants.FRAME_CTRL_LEN
        // Sequence Number Length
        + AMUGeneralDataConstants.SEQ_LEN
        // Dest Address Length
        + destAddressLen
        // Source Address Length
        + sourceAddressLen
        // Payload Length Length
        + AMUGeneralDataConstants.PAYLOAD_LEN
        // Frame Payload Length
        + DataUtil.getIntTo2Byte(AMUlengthField)
        // Frame CRC Length
        + AMUGeneralDataConstants.FRAME_CRC_LEN);
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
            log.debug("################################");
            log.debug("###### AMU General FRAME #######");
            log.debug("################################");

            AMUGeneralDataFrame frame   = null;

            // 한 패킷의 길이를 구해온다.
            int totallen = getTotalLength(in,startPos);
            log.info("decode : TOTAL_LEN[" + totallen + "] IN_LEN["+ in.limit() + "]");
            /*
             * buffer size 가 Frame Total Length 보다 작다는 것은 아직 모든 Data가 오지 않고 남아 있는 것
             * buffer size가 작아 한번에 Data를 Buffer에 담을 수 없으므로
             */
            in.position(startPos);
            if(totallen == in.limit()) {
                log.debug("[Equal] Buffer Length = AMU Frame Total Length");
                frame = decodeFrame(session,in.slice());
                if (frame != null) out.write(frame);
                return true;
            }
            else if( totallen < in.limit()) {
                log.debug("[Large] Buffer Length > AMU Frame Total Length");
                int limit = in.limit();
                in.limit(startPos+totallen);
                frame = decodeFrame(session,in.slice());
                if (frame != null) out.write(frame);
                // 다음 프레임 시작 위치로 설정한다.
                in.position(startPos+totallen);
                in.limit(limit);
                return !remainFrame(in);
            }
            else {
                log.debug("[Less] Buffer Length < AMU Frame Total Length");
                return false;
            }
        } catch(Exception ex)
        {
            log.error("FMPDecoder::decode failed : ", ex);
            throw new ProtocolDecoderException(ex.getMessage());
        }
    }
}
