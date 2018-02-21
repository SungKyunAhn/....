package com.aimir.fep.protocol.fmp.server;

import java.util.ArrayList;

import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.constants.CommonConstants.ThresholdName;
import com.aimir.fep.meter.parser.plc.PLCDataConstants;
import com.aimir.fep.meter.parser.plc.PLCDataFrame;
import com.aimir.fep.protocol.fmp.common.SlideWindow;
import com.aimir.fep.protocol.fmp.frame.AMUFrameControl;
import com.aimir.fep.protocol.fmp.frame.AMUGeneralDataConstants;
import com.aimir.fep.protocol.fmp.frame.AMUGeneralDataFrame;
import com.aimir.fep.protocol.fmp.frame.ControlDataFrame;
import com.aimir.fep.protocol.fmp.frame.GeneralDataConstants;
import com.aimir.fep.protocol.fmp.frame.GeneralDataFrame;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.FrameUtil;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.threshold.CheckThreshold;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderAdapter;
import org.apache.mina.filter.codec.ProtocolDecoderException;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 * Decodes a General Data Frame into MCU Input Stream.
 *
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class FMPDecoder extends ProtocolDecoderAdapter
{
    private static Log log = LogFactory.getLog(FMPDecoder.class);
    private IoBuffer dataBuff = null;
    private int dataBuffSize = 0;
    private boolean isRemained = false;
    private boolean isPlcRemained = false;
    private boolean isAmuRemained = false;
    private byte[] lengthField =
        new byte[GeneralDataConstants.LENGTH_LEN];
    private byte[] PLClengthField =
        new byte[PLCDataConstants.LENGTH_LEN];
    private byte[] AMUlengthField =
        new byte[AMUGeneralDataConstants.PAYLOAD_LEN];
    private PLCDataFrame previousRequestPLC;//Previous Request PLC Frame

    /**
	 * @return the previousRequestPLC
	 */
	public PLCDataFrame getPreviousRequestPLC() {
		return previousRequestPLC;
	}

	public PLCDataFrame getPreviousRequestPLC(IoSession session) {
		FMPProtocolHandler handler = (FMPProtocolHandler)session.getHandler();
		if(handler!=null ) {
			previousRequestPLC=handler.getPreviousRequestPLC();
		}
		return previousRequestPLC;
	}

	/**
	 * @param previousRequestPLC the previousRequestPLC to set
	 */
	public void setPreviousRequestPLC(PLCDataFrame previousRequestPLC) {
		this.previousRequestPLC = previousRequestPLC;
	}

	// decode frame buffer stream
    private GeneralDataFrame decodeFrame(IoSession session,
            IoBuffer in) throws Exception
    {
        GeneralDataFrame frame = null;
        //if control data frame
        if(FrameUtil.isControlDataFrame(in.get(2)))
        {
            Object ns_obj = session.getAttribute("nameSpace");
            String ns = ns_obj!=null? (String)ns_obj:null;
            frame = GeneralDataFrame.decode(ns,in.rewind());
            return frame;
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
                
                /*
                 * not alarm
                 * save invalid packet error in threshold.
                String activatorId = session.getRemoteAddress().toString();
                if (activatorId.contains("/") && activatorId.contains(":"))
                    activatorId = activatorId.substring(activatorId.indexOf("/")+1, activatorId.indexOf(":"));
                
                EventUtil.sendEvent("Invalid Frame",
                        TargetClass.MCU,
                        activatorId,
                        new String[][] {{"message", "CRC check error"}});
                
                CheckThreshold.updateCount(session.getRemoteAddress().toString(), ThresholdName.CRC); // INSERT SP-193
                return null;
                */
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

        return frame;
    }

    // decode frame buffer stream
    private PLCDataFrame decodePLCDataFrame(IoSession session,
            IoBuffer in) throws Exception
    {
        PLCDataFrame frame = null;

        boolean isValidFrameCrc = FrameUtil.checkPLCCRC(in);
        if(!isValidFrameCrc)
        {
            log.error("PLC CRC check failed Received Data ["
                + session.getRemoteAddress()+ "] :" + in.getHexDump());
            PLCDataFrame nak = FrameUtil.getPLCNAK(previousRequestPLC, PLCDataConstants.ERR_CODE_CRC);
            session.write(nak);
            
            CheckThreshold.updateCount(session.getRemoteAddress().toString(), ThresholdName.CRC); // INSERT SP-193
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

    /**
     * AMUGeneralDataFrame Data Decode frame buffer stream
     * @param session
     * @param in
     * @return AMUGeneralDataFrame
     * @throws Exception
     */
    private AMUGeneralDataFrame decodeAMUGeneralFrame(IoSession session,
            IoBuffer in) throws Exception
    {
        AMUGeneralDataFrame frame = null;

        boolean isValidFrameCrc = FrameUtil.checkAMUCRC(in);
        log.debug("is ValidFrameCrc : " + isValidFrameCrc);

        if(!isValidFrameCrc)
        {
            log.error("AMU Frame CRC check failed Received Data ["
                + session.getRemoteAddress()+ "] :" + in.getHexDump());
            
            CheckThreshold.updateCount(session.getRemoteAddress().toString(), ThresholdName.CRC); // INSERT SP-193

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

    // splite stream by frame unit
    private IoBuffer[] getFrameByteBufferList(IoBuffer in)
    {
        ArrayList<IoBuffer> res = new ArrayList<IoBuffer>();
        IoBuffer buf = null;
        int totallen = 0;
        byte[] bx;
        // 남은 길이가 헤더보다 크면 여러개의 프레임을 처리한다.
        while(in.remaining() > GeneralDataConstants.HEADER_LEN)
        {
            int pos = in.position();
            DataUtil.arraycopy(in,pos+3,lengthField,0,
                    lengthField.length);
            DataUtil.convertEndian(lengthField);
            totallen = DataUtil.getIntToBytes(lengthField)
                + GeneralDataConstants.HEADER_LEN
                + GeneralDataConstants.TAIL_LEN;
            log.debug("totallen["+totallen+"]");
            log.debug("in.remaining["+in.remaining()+"]");
            if(totallen > in.remaining())
            {
                dataBuff = IoBuffer.allocate(totallen);
                dataBuffSize = in.remaining();
                isRemained = true;
                bx = new byte[dataBuffSize];
                in.get(bx,0,bx.length);
                dataBuff.put(bx);
                log.debug("getFrameByteBufferList : "
                    +" dataBuff put finished : dataBuffSize["
                    +dataBuffSize+"] isRemained["
                    +isRemained+"] totallen["
                    +totallen+"]!!!!!!!!!!!!!!!");
                break;
            }
            bx = new byte[totallen];
            in.get(bx,0,bx.length);
            buf = IoBuffer.allocate(totallen);
            buf.put(bx);
            buf.flip();
            // 동일한 패킷이 있을 수 있기 때문에 강제로 필터링한다.
            if (!res.contains(buf)) {
				res.add(buf);
			}
        }
        // 헤더보다 작은 길이가 남았을때
        if (in.remaining() > 0) {
            dataBuff = IoBuffer.allocate(in.remaining());
            dataBuffSize = in.remaining();
            isRemained = true;
            bx = new byte[dataBuffSize];
            in.get(bx,0,bx.length);
            dataBuff.put(bx);
            log.debug("getFrameByteBufferList : "
                +" dataBuff put finished : dataBuffSize["
                +dataBuffSize+"] isRemained["
                +isRemained+"] totallen["
                +totallen+"]!!!!!!!!!!!!!!!");
        }
        return res.toArray(new IoBuffer[0]);
    }

    /**
     * splite stream by frame unit
     * @param in
     * @return
     */
    private IoBuffer[] getPLCFrameByteBufferList(IoBuffer in)
    {
        ArrayList<IoBuffer> res = new ArrayList<IoBuffer>();
        IoBuffer buf = null;
        int totallen = 0;
        byte[] bx;
        while(in.remaining() > PLCDataConstants.SOF_LEN+PLCDataConstants.HEADER_LEN)
        {
            int pos = in.position();
            totallen=getPLCTotalLength(in, pos);

            log.debug("totallen["+totallen+"]");
            log.debug("in.remaining["+in.remaining()+"]");
            if(totallen > in.remaining())
            {
                dataBuff = IoBuffer.allocate(totallen);
                dataBuffSize = in.remaining();
                isPlcRemained = true;
                bx = new byte[dataBuffSize];
                in.get(bx,0,bx.length);
                dataBuff.put(bx);
                log.debug("getFrameByteBufferList : "
                    +" dataBuff put finished : dataBuffSize["
                    +dataBuffSize+"] isPlcRemained["
                    +isPlcRemained+"] totallen["
                    +totallen+"]!!!!!!!!!!!!!!!");
                break;
            }
            bx = new byte[totallen];
            in.get(bx,0,bx.length);
            buf = IoBuffer.allocate(totallen);
            buf.put(bx);
            buf.flip();
            res.add(buf);
        }

        return res.toArray(new IoBuffer[0]);
    }

    /**
     * splite stream by frame unit
     * @param in
     * @return
     * @throws Exception
     */
    private IoBuffer[] getAMUGeneralFrameByteBufferList(IoBuffer in) throws Exception
    {
        log.debug("########### getAMUGeneralFrameByteBufferList Start ###########");
        ArrayList<IoBuffer> res = new ArrayList<IoBuffer>();
        IoBuffer buf = null;
        int totallen = 0;
        byte[] bx;

        // Frame Control 에서  Dest Type과  Source Type을 뽑아서 해당하는 길이 정도와 최소 길이를 구한다
        byte[] fc = new byte[AMUGeneralDataConstants.FRAME_CTRL_LEN];
        fc[0] = in.get(1);
        fc[1] = in.get(2);
        AMUFrameControl afc = AMUFrameControl.decode(fc);
        int destAndSrcAddrLen = afc.getDestTypeLenth() + afc.getSourceTypeLenth();

        log.debug("remain length : " + in.remaining());

        while(in.remaining() > AMUGeneralDataConstants.SOH_LEN + AMUGeneralDataConstants.FRAME_CTRL_LEN
                + AMUGeneralDataConstants.SEQ_LEN+ AMUGeneralDataConstants.PAYLOAD_LEN+destAndSrcAddrLen)
        {
            int pos = in.position();
            log.debug(" IoBuffer In position : " + pos);
            totallen= getAMUTotalLength(in, pos);
            log.debug(" AMU FrameByteBufferList Total Length ["+totallen+"] in.remaining["+in.remaining()+"]");

            if( totallen > in.remaining())
            {
                dataBuff = IoBuffer.allocate(totallen);
                dataBuffSize = in.remaining();
                isAmuRemained = true;
                bx = new byte[dataBuffSize];
                in.get(bx,0,bx.length);
                dataBuff.put(bx);
                log.debug("getAMUGeneralFrameByteBufferList : "
                    +" dataBuff put finished : dataBuffSize["
                    +dataBuffSize+"] isAmuRemained["
                    +isAmuRemained+"] totallen["
                    +totallen+"]!!!!!!!!!!!!!!!");
                break;
            }
            bx = new byte[totallen];
            in.get(bx,0,bx.length);
            buf = IoBuffer.allocate(totallen);
            buf.put(bx);
            buf.flip();
            res.add(buf);
        }

        // 헤더보다 작은 길이가 남았을때
        if (in.remaining() > 0) {
            dataBuff = IoBuffer.allocate(in.remaining());
            dataBuffSize = in.remaining();
            isAmuRemained = true;
            bx = new byte[dataBuffSize];
            in.get(bx,0,bx.length);
            dataBuff.put(bx);
            log.debug("getAMUGeneralFrameByteBufferList : "
                +" dataBuff put finished : dataBuffSize["
                +dataBuffSize+"] isAmuRemained["
                +isAmuRemained+"] totallen["
                +totallen+"]!!!!!!!!!!!!!!!");
        }

        return res.toArray(new IoBuffer[0]);
    }

    /**
     * 프레임의 첫번째 바이트를 체크해 올바른 프레임인지 체크한다
     * GeneralFrame = 0x5E
     * PLCFrame = 0xE1
     * AMUFrame = 0xAA
     *
     * @param buff
     * @param pos
     * @return
     */
    private boolean isValidFrame(IoBuffer buff,int pos)
    {
        if(buff.get(pos) != GeneralDataConstants.SOH && buff.get(pos) != PLCDataConstants.SOF && buff.get(pos) != AMUGeneralDataConstants.SOH) {
            if (buff.hasRemaining()) {
				buff.position(buff.limit());
			}
            return false;
        }
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
     * Get PLC Total Length
     * @param buff
     * @param pos
     * @return
     */
    private int getPLCTotalLength(IoBuffer buff,int pos)
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
     * Get AMU Total Length
     * @param buff
     * @param pos
     * @return  int Total Length
     * @throws Exception
     */
    private int getAMUTotalLength(IoBuffer buff,int pos) throws Exception
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

    private void release(IoBuffer bytebuffer)
    {
        log.debug("FREE ByteBuffer");
        try { bytebuffer.free(); }catch(Exception ex) {}
    }

    private void release(IoBuffer[] bytebuffers)
    {
        log.debug("FREE ByteBuffers");
        for(int i = 0 ; i < bytebuffers.length ; i++)
        {
            try { bytebuffers[i].free(); }catch(Exception ex) {}
        }
    }
    private void processingRemain(IoSession session,
            IoBuffer in, ProtocolDecoderOutput out )
            throws Exception
    {
        // 2010.03.26 dataBuff가 헤더길이보다 작으면 길이를 계산할 수 없기 때문에
        // in에서 길이가 있는 데이타를 가져와서 dataBuff에 넣는다.
        int totallen = 0;
        if (dataBuff.limit() < GeneralDataConstants.HEADER_LEN - 1) {
            int len = GeneralDataConstants.HEADER_LEN - dataBuff.limit() + 1;
            byte[] bx = new byte[len];
            dataBuff = IoBuffer.allocate(len);
            dataBuffSize += len;
            in.get(bx, 0, bx.length);
            dataBuff.put(bx);
        }
        totallen = getTotalLength(dataBuff,0);
        // 2010.03.26 dataBuff 길이가 데이타 길이보다 작을때 메모리를 다시 할당한다.
        if (dataBuff.limit() < totallen)
            dataBuff = IoBuffer.allocate(totallen);

        int recvsum = dataBuffSize + in.limit();
        log.debug("RECVSUM[" + recvsum + "] DATABUFFSIZE[" + dataBuffSize +
                "] IN.LIMIT[" + in.limit() + "] TOTALLEN[" + totallen + "]");
        GeneralDataFrame frame = null;
        if(recvsum < totallen)
        {
            log.debug("processingRemain : "
                    +" dataBuff put start : dataBuffSize["
                    +dataBuffSize+"] isRemained["
                    +isRemained+"] totallen["
                    +totallen+"]!!!!!!!!!!!!!!!");
            dataBuff.put(in);
            isRemained = true;
            dataBuffSize+=in.limit();
            log.debug("processingRemain : "
                    +" dataBuff put finished : dataBuffSize["
                    +dataBuffSize+"] isRemained["
                    +isRemained+"] totallen["
                    +totallen+"]!!!!!!!!!!!!!!!");
            return;
        } else if( recvsum == totallen)
        {
            dataBuff.put(in);
            dataBuff.flip();
            isRemained = false;
            dataBuffSize = 0;
            log.debug("processingRemain : "
                    +" dataBuff decode: dataBuffSize["
                    +dataBuffSize+"] isRemained["
                    +isRemained+"] totallen["
                    +totallen+"]!!!!!!!!!!!!!!!");
            frame = decodeFrame(session,dataBuff);
            if(frame != null) {
				out.write(frame);
			}
            release(dataBuff);
            //dataBuff.release();
            return;
        } else
        {
            int resize = (totallen - dataBuffSize);
            byte[] bx = new byte[resize];
            in.get(bx,0,bx.length);
            dataBuff.put(bx);
            dataBuff.flip();
            log.info("header : " + dataBuff.getHexDump(GeneralDataConstants.HEADER_LEN));
            isRemained = false;
            dataBuffSize = 0;
            log.debug("processingRemain : "
                    +" dataBuff decode: dataBuffSize["
                    +dataBuffSize+"] isRemained["
                    +isRemained+"] totallen["
                    +totallen+"]!!!!!!!!!!!!!!!");
            frame = decodeFrame(session,dataBuff);
            if(frame != null) {
				out.write(frame);
			}
            release(dataBuff);
            //dataBuff.release();

            if(!isValidFrame(in,in.position()))
            {
                log.error("data["+in.getHexDump()+"] is invalid Frame");
                return;
            }

            if (in.limit()-in.position() > GeneralDataConstants.HEADER_LEN) {
				log.info("header : " + in.getHexDump(GeneralDataConstants.HEADER_LEN));
			}
			else {
				log.info("header : " + in.getHexDump());
			}

            // 헤더에 있는 길이를 가져온다.
            int restotallen = getTotalLength(in,in.position());
            log.debug("RES Total Len[" + restotallen + "]");

            if(restotallen > (recvsum - totallen))
            {
                // dataBuff.expand(restotallen);
                dataBuff = IoBuffer.allocate(restotallen);
                bx = new byte[(recvsum-totallen)];
                in.get(bx,0,bx.length);
                log.debug("processingRemain : "
                        +" dataBuff put start: dataBuffSize["
                    +dataBuffSize+"] isRemained["
                    +isRemained+"] totallen["
                    +totallen+"]!!!!!!!!!!!!!!!");
                dataBuff.put(bx);
                isRemained = true;
                dataBuffSize=bx.length;
                log.debug("processingRemain : "
                        +" dataBuff put finished: dataBuffSize["
                    +dataBuffSize+"] isRemained["
                    +isRemained+"] totallen["
                    +totallen+"]!!!!!!!!!!!!!!!");
                return;
            } else if(restotallen == (recvsum - totallen))
            {
                dataBuff = IoBuffer.allocate(restotallen);
                bx = new byte[(recvsum-totallen)];
                in.get(bx,0,bx.length);
                dataBuff.put(bx);
                dataBuff.flip();
                log.debug("processingRemain : "
                        +" dataBuff decode : dataBuffSize["
                    +dataBuffSize+"] isRemained["
                    +isRemained+"] totallen["
                    +totallen+"]!!!!!!!!!!!!!!!");
                dataBuffSize = 0;
                isRemained = false;
                frame = decodeFrame(session,dataBuff);
                if(frame != null) {
					out.write(frame);
				}
                release(dataBuff);
                //dataBuff.release();
                return;
            }
            else
            {
                IoBuffer[] ins = getFrameByteBufferList(in);
                try {
                for(int i = 0 ; i < ins.length; i++)
                {
                    /* modified by D.J Park in 2006.10.24
                     * check CRC in decodeFrame Fuction
                    boolean isValidFrameCrc =
                        FrameUtil.checkCRC(ins[i]);
                    if(!isValidFrameCrc)
                    {
                        log.error("crc check failed Received Data ["
                            + session.getRemoteAddress()
                            + "] :" + ins[i].getHexDump());
                        if(FrameUtil.isServiceDataFrame(ins[i].get(2))
                                || FrameUtil.isSingleFrame(
                                    ins[i].get(2)))
                        {
                            ControlDataFrame nak = FrameUtil.getNAK(
                                    in.get(1));
                            session.write(nak);
                        }
                        ins[i].release();
                        continue;
                    }
                    */
                    frame = decodeFrame(session,ins[i]);
                    if(frame != null) {
						out.write(frame);
					}
                    ins[i].free();
                }
                }catch(Exception ex)
                {
                    release(ins);
                    throw ex;
                }
            }
        }
    }

    /**
     * Processing PLC Remain Frame
     * @param session
     * @param in
     * @param out
     * @throws Exception
     */
    private void processingPLCRemain(IoSession session,
            IoBuffer in, ProtocolDecoderOutput out )
            throws Exception
    {
        int totallen = getPLCTotalLength(dataBuff,0);
        int recvsum = dataBuffSize + in.limit();
        PLCDataFrame frame = null;
        if(recvsum < totallen)
        {
            log.debug("processingRemain : "
                    +" dataBuff put start : dataBuffSize["
                    +dataBuffSize+"] isPlcRemained["
                    +isPlcRemained+"] totallen["
                    +totallen+"]!!!!!!!!!!!!!!!");
            dataBuff.put(in);
            isPlcRemained = true;
            dataBuffSize+=in.limit();
            log.debug("processingRemain : "
                    +" dataBuff put finished : dataBuffSize["
                    +dataBuffSize+"] isPlcRemained["
                    +isPlcRemained+"] totallen["
                    +totallen+"]!!!!!!!!!!!!!!!");
            return;
        } else if( recvsum == totallen)
        {
            dataBuff.put(in);
            dataBuff.flip();
            isPlcRemained = false;
            dataBuffSize = 0;
            log.debug("processingRemain : "
                    +" dataBuff decode: dataBuffSize["
                    +dataBuffSize+"] isPlcRemained["
                    +isPlcRemained+"] totallen["
                    +totallen+"]!!!!!!!!!!!!!!!");
            frame = decodePLCDataFrame(session,dataBuff);
            if(frame != null) {
                out.write(frame);
            }
            release(dataBuff);
            return;
        } else
        {
            int resize = (totallen - dataBuffSize);
            byte[] bx = new byte[resize];
            in.get(bx,0,bx.length);
            dataBuff.put(bx);
            dataBuff.flip();
            isPlcRemained = false;
            dataBuffSize = 0;
            log.debug("processingRemain : "
                    +" dataBuff decode: dataBuffSize["
                    +dataBuffSize+"] isPlcRemained["
                    +isPlcRemained+"] totallen["
                    +totallen+"]!!!!!!!!!!!!!!!");
            frame = decodePLCDataFrame(session,dataBuff);
            if(frame != null) {
                out.write(frame);
            }
            release(dataBuff);


            if(!isValidFrame(in,in.position()))
            {
                log.error("data["+in.getHexDump()+"] is invalid Frame");
                return;
            }

            int restotallen = getPLCTotalLength(in,in.position());

            log.debug("RES Total Len[" + restotallen + "]");
            if(restotallen > (recvsum - totallen))
            {
                dataBuff.expand(restotallen);
                bx = new byte[(recvsum-totallen)];
                in.get(bx,0,bx.length);
                log.debug("processingRemain : "
                        +" dataBuff put start: dataBuffSize["
                    +dataBuffSize+"] isPlcRemained["
                    +isPlcRemained+"] totallen["
                    +totallen+"]!!!!!!!!!!!!!!!");
                dataBuff.put(bx);
                isPlcRemained = true;
                dataBuffSize=bx.length;
                log.debug("processingRemain : "
                        +" dataBuff put finished: dataBuffSize["
                    +dataBuffSize+"] isPlcRemained["
                    +isPlcRemained+"] totallen["
                    +totallen+"]!!!!!!!!!!!!!!!");
                return;
            } else if(restotallen == (recvsum - totallen))
            {
                dataBuff = IoBuffer.allocate(restotallen);
                bx = new byte[(recvsum-totallen)];
                in.get(bx,0,bx.length);
                dataBuff.put(bx);
                dataBuff.flip();
                log.debug("processingRemain : "
                        +" dataBuff decode : dataBuffSize["
                    +dataBuffSize+"] isPlcRemained["
                    +isPlcRemained+"] totallen["
                    +totallen+"]!!!!!!!!!!!!!!!");
                dataBuffSize = 0;
                isPlcRemained = false;
                frame = decodePLCDataFrame(session,dataBuff);
                if(frame != null) {
                    out.write(frame);
                }
                release(dataBuff);
                return;
            }
            else
            {
                IoBuffer[] ins = getPLCFrameByteBufferList(in);
                try {
                for(int i = 0 ; i < ins.length; i++)
                {
                    frame = decodePLCDataFrame(session,ins[i]);
                    if(frame != null) {
                        out.write(frame);
                    }
                    ins[i].free();
                }
                }catch(Exception ex)
                {
                    release(ins);
                    throw ex;
                }
            }
        }
    }

    /**
     * Processing AMU Remain Frame
     * @param session
     * @param in
     * @param out
     * @throws Exception
     */
    private void processingAMURemain(IoSession session,
            IoBuffer in, ProtocolDecoderOutput out )
            throws Exception
    {
        log.debug("#############  ProcessingAMURemain Start  ####################");
        int totallen = getAMUTotalLength(dataBuff,0);
        int recvsum = dataBuffSize + in.limit();
        AMUGeneralDataFrame frame = null;
        log.debug("total length(get AMU TotalLength) : " + totallen);
        log.debug("recvsum( dataBuffSize + in.limit()) : " + recvsum);

        if(recvsum < totallen)
        {

            log.debug("processingAMURemain (recvsum < totallen): "
                    +" dataBuff put start : dataBuffSize["
                    +dataBuffSize+"] isAmuRemained["
                    +isAmuRemained+"] totallen["
                    +totallen+"]!!!!!!!!!!!!!!!");
            dataBuff.put(in);
            isAmuRemained = true;
            dataBuffSize+=in.limit();
            log.debug("processingAMURemain : "
                    +" dataBuff put finished : dataBuffSize["
                    +dataBuffSize+"] isAmuRemained["
                    +isAmuRemained+"] totallen["
                    +totallen+"]!!!!!!!!!!!!!!!");
            return;
        } else if( recvsum == totallen)
        {
            dataBuff.put(in);
            dataBuff.flip();
            isAmuRemained = false;
            dataBuffSize = 0;
            log.debug("processingAMURemain(recvsum == totallen): "
                    +" dataBuff decode: dataBuffSize["
                    +dataBuffSize+"] isAmuRemained["
                    +isAmuRemained+"] totallen["
                    +totallen+"]!!!!!!!!!!!!!!!");
            frame = decodeAMUGeneralFrame(session,dataBuff);
            if(frame != null) {
                out.write(frame);
            }
            release(dataBuff);
            return;
        } else
        {
            int resize = (totallen - dataBuffSize);
            byte[] bx = new byte[resize];
            in.get(bx,0,bx.length);
            dataBuff.put(bx);
            dataBuff.flip();
            isAmuRemained = false;
            dataBuffSize = 0;
            log.debug("processingAMURemain (recvsum > totallen): "
                    +" dataBuff decode: dataBuffSize["
                    +dataBuffSize+"] isAmuRemained["
                    +isAmuRemained+"] totallen["
                    +totallen+"]!!!!!!!!!!!!!!!");

            frame = decodeAMUGeneralFrame(session,dataBuff);
            if(frame != null) {
                out.write(frame);
            }
            release(dataBuff);

            if(!isValidFrame(in,in.position()))
            {
                log.error("data["+in.getHexDump()+"] is invalid Frame");
                return;
            }

            int retotallen = getAMUTotalLength(in,in.position());

            log.debug("re Total Len["+ retotallen +"]");
            if(retotallen > (recvsum - totallen))
            {
                dataBuff.expand(retotallen);
                bx = new byte[(recvsum-totallen)];
                in.get(bx,0,bx.length);
                log.debug("processingAMURemain(retotallen > (recvsum - totallen)) : "
                        +" dataBuff put start: dataBuffSize["
                    +dataBuffSize+"] isAmuRemained["
                    +isAmuRemained+"] totallen["
                    +totallen+"]!!!!!!!!!!!!!!!");
                dataBuff.put(bx);
                isAmuRemained = true;
                dataBuffSize=bx.length;
                log.debug("processingAMURemain : "
                        +" dataBuff put finished: dataBuffSize["
                    +dataBuffSize+"] isAmuRemained["
                    +isAmuRemained+"] totallen["
                    +totallen+"]!!!!!!!!!!!!!!!");
                return;
            } else if(retotallen == (recvsum - totallen))
            {
                dataBuff = IoBuffer.allocate(retotallen);
                bx = new byte[(recvsum-totallen)];
                in.get(bx,0,bx.length);
                dataBuff.put(bx);
                dataBuff.flip();
                log.debug("processingAMURemain(retotallen == (recvsum - totallen) : "
                        +" dataBuff decode : dataBuffSize["
                    +dataBuffSize+"] isAmuRemained["
                    +isAmuRemained+"] totallen["
                    +totallen+"]!!!!!!!!!!!!!!!");
                dataBuffSize = 0;
                isAmuRemained = false;
                frame = decodeAMUGeneralFrame(session,dataBuff);
                if(frame != null) {
                    out.write(frame);
                }
                release(dataBuff);
                return;
            }
            else
            {
                log.debug("retotallen < (recvsum - totallen)");
                IoBuffer[] ins = getAMUGeneralFrameByteBufferList(in);
                log.debug("int Length : " + ins.length);
                try {
                    for(int i = 0 ; i < ins.length; i++)
                    {
                        frame = decodeAMUGeneralFrame(session,ins[i]);
                        if(frame != null) {
                            out.write(frame);
                        }
                        ins[i].free();
                    }
                }catch(Exception ex)
                {
                    release(ins);
                    throw ex;
                }
            }
        }
    }

    /**
     * decode input stream
     *
     * @param session <code>ProtocolSession</code> session
     * @param in <code>ByteBuffer</code> input stream
     * @param out <code>ProtocolDecoderOutput</code> save decoding frame
     * @throws  ProtocolViolationException
     */
    public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out)
    throws ProtocolDecoderException
    {
        try
        {
            log.info("Received [" + session.getRemoteAddress() + "] : "+in.limit());
            log.debug(" IN_HEX : " + in.getHexDump());

            log.debug("SOF: "+in.get(0) + " isRemained[" + isRemained + "] isAmuRemained[" + isAmuRemained + "]");
            //--------------------
            //  AMU General Frame
            //--------------------
            if(isAmuRemained || ((!isRemained) && (!isPlcRemained) && FrameUtil.isAmuGeneralDataFrame(in.get(0)))){

                log.debug("################################");
                log.debug("###### AMU General FRAME #######");
                log.debug("################################");

                AMUGeneralDataFrame frame   = null;

                /**
                 * 처음에 들어온 Data는 isAmuRemained가 false로 processingAMURemain 를 거치지 않고
                 * 아래 logic를  타게 되다가   buffer 의 사이즈  크거나 작으면 이후로는
                 * isAmuRemained가 true가 되어 다음 로직들은 processingAMURemain에서 처리한다.
                 */
                log.debug("############# isAmuRemained " + isAmuRemained );
                if(isAmuRemained){
                    processingAMURemain(session,in,out);
                    //isAmuRemained = false;
                    return;
                }
                // Validation Check : Start Of Header is  0x00
                if(!isValidFrame(in,0))
                {
                    log.error("data["+in.getHexDump()+"] is invalid Frame");
                    return;
                }

                int totallen = getAMUTotalLength(in,0);
                log.info("decode : TOTAL_LEN[" + totallen + "] IN_LEN["+ in.limit() + "]");
                /*
                 * buffer size 가 Frame Total Length 보다 작다는 것은 아직 모든 Data가 오지 않고 남아 있는 것
                 * buffer size가 작아 한번에 Data를 Buffer에 담을 수 없으므로
                 */
                if(totallen > in.limit()){
                    log.debug("[Less] Buffer Length < AMU Frame Total Length");
                    dataBuff = IoBuffer.allocate(totallen);
                    log.debug("######decode : dataBuff put start: dataBuffSize["
                        +dataBuffSize+"] isAmuRemained["
                        +isAmuRemained+"] totallen["
                        +totallen+"] ########");
                    dataBuff.put(in);
                    isAmuRemained = true;
                    dataBuffSize=in.limit();
                    log.debug("decode : "
                            +" dataBuff put finished: dataBuffSize["
                            +dataBuffSize+"] isAmuRemained["
                            +isAmuRemained+"] totallen["
                            +totallen+"]!!!!");
                // buffer size 와  Frame Total Length가 같다
                }else if(totallen == in.limit()){
                    log.debug("[Equal] Buffer Length = AMU Frame Total Length");
                    frame = decodeAMUGeneralFrame(session,in);

                    if(frame != null) {
                        if (in.hasRemaining()) {
                            in.position(in.limit());
                        }
                        out.write(frame);
                    }
                /*
                 *  buffer size 가  Frame Total Length 보다 크다는 것 더 많은 값이 버퍼에  들어왔을때
                 *  ex) 중복 , multi
                 */
                }else if( totallen < in.limit()){

                     log.debug("[Large] Buffer Length > AMU Frame Total Length");
                     log.debug("buffer position : " + in.position() );
                     IoBuffer[] ins = getAMUGeneralFrameByteBufferList(in);
                     log.debug("Frame Count : " + ins.length);

                     for(int i = 0 ; i < ins.length; i++)
                     {
                         try
                         {
                             frame = decodeAMUGeneralFrame(session,ins[i]);
                             if(frame != null) {
                                 out.write(frame);
                             }
                             ins[i].free();
                         }catch(Exception ex)
                         {
                             release(ins);
                             throw ex;
                         }
                     }
                }
            }
            //-------------
            //  PLC Frame
            //-------------
            else if((!isRemained && FrameUtil.isPLCDataFrame(in.get(0))) || isPlcRemained){
                log.debug("########################");
                log.debug("###### PLC FRAME #######");
                log.debug("########################");
                getPreviousRequestPLC(session);
                int totallen = getPLCTotalLength(in,0);

                PLCDataFrame frame = null;

                if(isPlcRemained)
                {
                    processingPLCRemain(session,in,out);
                    return;
                }
                if(!isValidFrame(in,0))
                {
                    log.error("data["+in.getHexDump()+"] is invalid Frame");
                    return;
                }
                if(totallen > in.limit())
                {
                    log.debug("[Less] Buffer Length < PLC Frame Total Length");
                    dataBuff = IoBuffer.allocate(totallen);
                    log.debug("decode : dataBuff put start: dataBuffSize["
                        +dataBuffSize+"] isRemained["
                        +isRemained+"] totallen["
                        +totallen+"]!!!!!!!!!!!!!!!");
                    dataBuff.put(in);
                    isPlcRemained = true;
                    dataBuffSize=in.limit();
                    log.debug("decode : "
                            +" dataBuff put finished: dataBuffSize["
                            +dataBuffSize+"] isPlcRemained["
                            +isPlcRemained+"] totallen["
                            +totallen+"]!!!!!!!!!!!!!!!");
                }
                else if(totallen == in.limit())
                {
                    log.debug("[Equal] Buffer Length = PLC Frame Total Length");
                    frame = decodePLCDataFrame(session,in);
                    if(frame != null) {
                        if (in.hasRemaining()) {
                            in.position(in.limit());
                        }
                        out.write(frame);
                    }
                } else if(totallen < in.limit())
                {
                    log.debug("[Large] Buffer Length > PLC Frame Total Length");
                    IoBuffer[] ins = getPLCFrameByteBufferList(in);
                    for(int i = 0 ; i < ins.length; i++)
                    {
                        try
                        {
                            frame = decodePLCDataFrame(session,ins[i]);
                            if(frame != null) {
                                out.write(frame);
                            }
                            ins[i].free();
                        }catch(Exception ex)
                        {
                            release(ins);
                            throw ex;
                        }
                    }
                }
            }
        	//-----------------
        	//	General Frame
        	//-----------------
        	else{
	            GeneralDataFrame frame = null;
	            boolean isValidFrameCrc = false;
	            //log.debug("totallen = " + totallen);
	            //log.debug("Received.length = " + in.limit());

	            int totallen = 0;

	            if(isRemained)
	            {
	                processingRemain(session,in,out);
	                return;
	            }
	            if(!isValidFrame(in,0))
	            {
	                log.error("data["+in.getHexDump()+"] is invalid Frame");
	                return;
	            }
	            totallen = getTotalLength(in,0);
	            log.info("decode : TOTAL_LEN[" + totallen + "] IN_LEN["+ in.limit() + "]");
	            if (in.limit() > GeneralDataConstants.HEADER_LEN) {
					log.info("header : " + in.getHexDump(GeneralDataConstants.HEADER_LEN));
				}
				else {
					log.info("header : " + in.getHexDump());
				}

	            if(totallen > in.limit())
	            {
	                dataBuff = IoBuffer.allocate(totallen);
	                log.debug("decode : dataBuff put start: dataBuffSize["
	                    +dataBuffSize+"] isRemained["
	                    +isRemained+"] totallen["
	                    +totallen+"]!!!!!!!!!!!!!!!");
	                dataBuff.put(in);
	                isRemained = true;
	                dataBuffSize=in.limit();
	                log.debug("decode : "
	                        +" dataBuff put finished: dataBuffSize["
	                        +dataBuffSize+"] isRemained["
	                        +isRemained+"] totallen["
	                        +totallen+"]!!!!!!!!!!!!!!!");
	                if (in.hasRemaining()) {
						in.position(in.limit());
					}
	            }
	            else if(totallen == in.limit())
	            {
	                /* modified by D.J Park in 2006.10.24
	                 * check CRC in decodeFrame Fuction
	                isValidFrameCrc = FrameUtil.checkCRC(in);
	                if(!isValidFrameCrc)
	                {
	                    log.error("crc check failed Received Data ["
	                            + session.getRemoteAddress()+ "] :"
	                            + in.getHexDump());
	                    if(FrameUtil.isServiceDataFrame(in.get(2))
	                            || FrameUtil.isSingleFrame(in.get(2)))
	                    {
	                        ControlDataFrame nak = FrameUtil.getNAK(
	                                in.get(1));
	                        session.write(nak);
	                    }
	                    return;
	                }
	                */
	                frame = decodeFrame(session,in);
	                if(frame != null) {
	                    if (in.hasRemaining()) {
							in.position(in.limit());
						}
	                    out.write(frame);
	                }
	            } else if(totallen < in.limit())
	            {
	                IoBuffer[] ins = getFrameByteBufferList(in);
	                // 2010.03.26 위치를 마지막으로 옮긴다.
	                if (in.hasRemaining()) {
                        in.position(in.limit());
                    }

	                for(int i = 0 ; i < ins.length; i++)
	                {
	                    //isValidFrameCrc = FrameUtil.checkCRC(ins[i]);
	                    try
	                    {
	                        /* modified by D.J Park in 2006.10.24
	                         * check CRC in decodeFrame Fuction
	                        if(!isValidFrameCrc)
	                        {
	                            log.error("crc check failed Received Data ["
	                                + session.getRemoteAddress()
	                                + "] :" + ins[i].getHexDump());
	                            if(FrameUtil.isServiceDataFrame(ins[i].get(2))
	                                    || FrameUtil.isSingleFrame(ins[i].get(2)))
	                            {
	                                ControlDataFrame nak = FrameUtil.getNAK(
	                                        ins[i].get(1));
	                                session.write(nak);
	                            }
	                            ins[i].release();
	                            continue;
	                        }
	                        */
	                        frame = decodeFrame(session,ins[i]);
	                        if(frame != null) {
								out.write(frame);
							}
	                        ins[i].free();
	                    }catch(Exception ex)
	                    {
	                        release(ins);
	                        throw ex;
	                    }
	                }
	            }
	            /*
	            else
	            {
	                log.error("length check failed Received Data["
	                        + session.getRemoteAddress()
	                        + "] :" + in.getHexDump());
	                if(FrameUtil.isServiceDataFrame(in.get(2))
	                        || FrameUtil.isSingleFrame(in.get(2)))
	                {
	                    ControlDataFrame nak = FrameUtil.getNAK(
	                            in.get(1));
	                    session.write(nak);
	                }
	                return;
	            }
	            */
        	}
        } catch(Exception ex)
        {
            log.error("FMPDecoder::decode failed : ", ex);
            throw new ProtocolDecoderException(ex.getMessage());
        }
    }
}
