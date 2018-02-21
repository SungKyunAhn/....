package com.aimir.fep.protocol.fmp.server;

import com.aimir.fep.meter.parser.plc.PLCDataConstants;
import com.aimir.fep.meter.parser.plc.PLCDataFrame;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FrameUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderException;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 * Decodes a PLC Data Frame into MCU Input Stream.
 *
 * @author elevas (elevas@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2013-03-25 15:59:15 +0900 $,
 */
public class FMPPLCDecoder
{
    private static Log log = LogFactory.getLog(FMPPLCDecoder.class);
    private byte[] PLClengthField =
        new byte[PLCDataConstants.LENGTH_LEN];
    private PLCDataFrame previousRequest;//Previous Request PLC Frame

    /**
	 * @return the previousRequestPLC
	 */
	public PLCDataFrame getPreviousRequest() {
		return previousRequest;
	}

	public PLCDataFrame getPreviousRequest(IoSession session) {
		FMPProtocolHandler handler = (FMPProtocolHandler)session.getHandler();
		if(handler!=null ) {
			previousRequest =handler.getPreviousRequestPLC();
		}
		return previousRequest;
	}

	/**
	 * @param previousRequestPLC the previousRequestPLC to set
	 */
	public void setPreviousRequest(PLCDataFrame previousRequest) {
		this.previousRequest = previousRequest;
	}

    // decode frame buffer stream
    private PLCDataFrame decodeFrame(IoSession session,
            IoBuffer in) throws Exception
    {
        PLCDataFrame frame = null;

        boolean isValidFrameCrc = FrameUtil.checkPLCCRC(in);
        if(!isValidFrameCrc)
        {
            log.error("PLC CRC check failed Received Data ["
                + session.getRemoteAddress()+ "] :" + in.getHexDump());
            PLCDataFrame nak = FrameUtil.getPLCNAK(previousRequest, PLCDataConstants.ERR_CODE_CRC);
            session.write(nak);
            return null;
        }

        frame = PLCDataFrame.decode(in);
        if(FrameUtil.isAck(frame))
        {
            log.debug("send ACK - DIRECTION["+frame.getProtocolDirection()+"] COMMAND[" + (char)frame.getCommand()+"]");
            session.write(FrameUtil.getPLCACK(frame));
        }

        return frame;
    }

    private boolean remainFrame(IoBuffer in)
    {
        int totallen = 0;
        int pos = in.position();
        while(in.remaining() > PLCDataConstants.SOF_LEN+PLCDataConstants.HEADER_LEN)
        {
            totallen = getTotalLength(in, pos);

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
     * Get PLC Total Length
     * @param buff
     * @param pos
     * @return
     */
    private int getTotalLength(IoBuffer buff,int pos)
    {
        DataUtil.arraycopy(buff,pos+PLCDataConstants.SOF_LEN+PLCDataConstants.HEADER_LEN-3,PLClengthField,0,
                PLClengthField.length);
        DataUtil.convertEndian(!PLCDataConstants.isConvert, PLClengthField);
        //log.debug("Length Field: "+DataUtil.getIntToBytes(PLClengthField));
        return PLCDataConstants.SOF_LEN
        +PLCDataConstants.DIR_LEN+PLCDataConstants.VER_LEN+PLCDataConstants.DID_LEN+PLCDataConstants.SID_LEN+PLCDataConstants.LENGTH_LEN
        +DataUtil.getIntToBytes(PLClengthField)+PLCDataConstants.EOF_LEN;//length Field + 26
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
            log.debug("########################");
            log.debug("###### PLC FRAME #######");
            log.debug("########################");
            getPreviousRequest(session);
            int totallen = getTotalLength(in,startPos);

            PLCDataFrame frame = null;

            in.position(startPos);
            if(totallen == in.limit())
            {
                log.debug("[Equal] Buffer Length = PLC Frame Total Length");
                frame = decodeFrame(session,in.slice());
                if (frame != null) out.write(frame);
                return true;
                
            }
            else if(totallen < in.limit())
            {
                log.debug("[Large] Buffer Length > PLC Frame Total Length");
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
                log.debug("[Less] Buffer Length < PLC Frame Total Length");
                return false;
            }
        } catch(Exception ex)
        {
            log.error("FMPDecoder::decode failed : ", ex);
            throw new ProtocolDecoderException(ex.getMessage());
        }
    }
}
