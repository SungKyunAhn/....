package com.aimir.fep.protocol.fmp.client;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderAdapter;
import org.apache.mina.filter.codec.ProtocolDecoderException;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.aimir.fep.meter.parser.plc.PLCDataConstants;
import com.aimir.fep.meter.parser.plc.PLCDataFrame;
import com.aimir.fep.protocol.fmp.frame.GeneralDataConstants;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FrameUtil;

/**
 * Decodes a General Data Frame into MCU Input Stream.
 *
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class PLCClientDecoder extends ProtocolDecoderAdapter
{
    private static Log log = LogFactory.getLog(PLCClientDecoder.class);
    private IoBuffer dataBuff = null;
    private int dataBuffSize = 0;
    private boolean isRemained = false;
    private boolean isPlcRemained = false;
    private byte[] lengthField =
        new byte[GeneralDataConstants.LENGTH_LEN];
    private byte[] PLClengthField =
        new byte[PLCDataConstants.LENGTH_LEN];
    private PLCDataFrame previousRequestPLC;//Previous Request PLC Frame

    /**
	 * @return the previousRequestPLC
	 */
	public PLCDataFrame getPreviousRequestPLC() {
		return previousRequestPLC;
	}

	public PLCDataFrame getPreviousRequestPLC(IoSession session) {
		PLCClientProtocolHandler handler = (PLCClientProtocolHandler)session.getHandler();
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
            return null;
        }

        frame = PLCDataFrame.decode(in);
        return frame;
    }

    // splite stream by frame unit
    private IoBuffer[] getFrameByteBufferList(IoBuffer in)
    {
        ArrayList res = new ArrayList();
        IoBuffer buf = null;
        int totallen = 0;
        byte[] bx;
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
            res.add(buf);
        }

        return (IoBuffer[])res.toArray(new IoBuffer[0]);
    }

    /**
     * splite stream by frame unit
     * @param in
     * @return
     */
    private IoBuffer[] getPLCFrameByteBufferList(IoBuffer in)
    {
        ArrayList res = new ArrayList();
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

        return (IoBuffer[])res.toArray(new IoBuffer[0]);
    }

    private boolean isValidFrame(IoBuffer buff,int pos)
    {
        if(buff.get(pos) != (byte)0x5E && buff.get(pos) != (byte)0xE1) {
            if (buff.hasRemaining()) {
				buff.position(buff.limit());
			}
            return false;
        }
        return true;
    }

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
        	log.info("Decode [Start]");
            log.info("Received [" + session.getRemoteAddress() + "] Command["+DataUtil.getPLCCommandStr(in.get(25))+"]: "+in.limit()+" : "+in.getHexDump());

            //-------------
            //	PLC Frame
            //-------------
            log.debug("SOF: "+in.get(0));
        	if((!isRemained && FrameUtil.isPLCDataFrame(in.get(0))) || isPlcRemained){
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
        			log.debug("[Less] Buffer Length["+in.limit()+"] < PLC Frame Total Length["+totallen+"]");
	                dataBuff = IoBuffer.allocate(totallen);
	                log.debug("decode : dataBuff put start: dataBuffSize["
	                    +dataBuffSize+"] isPlcRemained["
	                    +isPlcRemained+"] totallen["
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
        	log.info("Decode [End]");
        } catch(Exception ex)
        {
            log.error("FMPDecoder::decode failed : ", ex);
            throw new ProtocolDecoderException(ex.getMessage());
        }
    }
}
