package com.aimir.fep.protocol.fmp.client;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderAdapter;
import org.apache.mina.filter.codec.ProtocolDecoderException;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.aimir.fep.protocol.fmp.common.SlideWindow;
import com.aimir.fep.protocol.fmp.frame.ControlDataFrame;
import com.aimir.fep.protocol.fmp.frame.GeneralDataConstants;
import com.aimir.fep.protocol.fmp.frame.GeneralDataFrame;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FrameUtil;

/**
 * Decodes FMP GeneralDataFrame into input stream
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class FMPClientDecoder extends ProtocolDecoderAdapter
{
    private Log log = LogFactory.getLog(FMPClientDecoder.class);
    private IoBuffer dataBuff = null;
    private int dataBuffSize = 0;
    private boolean isRemained = false;
    private byte[] lengthField = 
        new byte[GeneralDataConstants.LENGTH_LEN]; 

    // decode frame
    private GeneralDataFrame decodeFrame(IoSession session,
            IoBuffer in) throws Exception
    { 
        GeneralDataFrame frame = null; 
        if(FrameUtil.isSetBit(in.get(2), 
                    GeneralDataConstants.ATTR_FRAME)) 
        { 
            Object ns_obj = session.getAttribute("nameSpace");
            String ns = ns_obj!=null? (String)ns_obj:null;
            frame = GeneralDataFrame.decode(ns, in.rewind()); 
            return frame;
        } 
        if(!FrameUtil.isSingleFrame(in.get(2))) 
        { 
            //log.debug("isMultiframe is true "); 
            SlideWindow sw = (SlideWindow)session.getAttribute(""); 
            if(sw == null) 
            { 
                sw = new SlideWindow(); 
                session.setAttribute("", sw); 
            } 
            sw.put(in); 
        } 
        else 
        { 
            //log.debug("isMultiframe is false "); 
            Object ns_obj = session.getAttribute("nameSpace");
            String ns = ns_obj!=null? (String)ns_obj:null;
            frame = GeneralDataFrame.decode(ns,in); 
            if(frame.isServiceDataFrame() && 
                    frame.getSvc() != GeneralDataConstants.SVC_C) 
            { 
                log.debug("send ACK"); 
                session.write(FrameUtil.getACK(frame)); 
            } 
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
        while(in.remaining() > 6)
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

    private int getTotalLength(IoBuffer buff,int pos)
    { 
        DataUtil.arraycopy(buff,pos+3,lengthField,0,
                lengthField.length);
        DataUtil.convertEndian(lengthField);
        return DataUtil.getIntToBytes(lengthField) 
            + GeneralDataConstants.HEADER_LEN 
            + GeneralDataConstants.TAIL_LEN;
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
        int totallen = getTotalLength(dataBuff,0); 
        int recvsum = dataBuffSize + in.limit(); 
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
            if(frame != null)
                out.write(frame); 
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
            isRemained = false;
            dataBuffSize = 0; 
            log.debug("processingRemain : "
                    +" dataBuff decode: dataBuffSize["
                    +dataBuffSize+"] isRemained["
                    +isRemained+"] totallen["
                    +totallen+"]!!!!!!!!!!!!!!!");
            frame = decodeFrame(session,dataBuff); 
            if(frame != null)
                out.write(frame); 
            release(dataBuff);
            //dataBuff.release(); 
            int restotallen = getTotalLength(in,in.position()); 
            if(restotallen > (recvsum - totallen)) 
            { 
                dataBuff.expand(restotallen); 
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
                if(frame != null)
                    out.write(frame); 
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
                        ins[i].free();
                        continue; 
                    }
                    frame = decodeFrame(session,ins[i]);
                    if(frame != null)
                        out.write(frame);
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
     * @param session <code>IoSession</code> session
     * @param in <code>IoBuffer</code> input stream
     * @param out <code>ProtocolDecoderOutput</code> save decoding frame 
     * @throws  ProtocolViolationException
     */
    public void decode(IoSession session, IoBuffer in,
                       ProtocolDecoderOutput out )
            throws ProtocolDecoderException
    {
        try
        { 
            log.debug("Received [" + session.getRemoteAddress()
                    + "] : "+in.limit()+" :"+in.getHexDump());

            boolean isActiveFMP = ((Boolean)session.getAttribute(
                        FMPClientProtocolHandler.isActiveFMPKey))
                .booleanValue();
            log.debug("decode isActiveFMP["+isActiveFMP+"]");

            if(!isActiveFMP)
            {
                byte[] bx = new byte[in.limit()];
                in.get(bx,0,bx.length);
                out.write(bx);
                return;
            }

            GeneralDataFrame frame = null;
            boolean isValidFrameCrc = false;

            int totallen = 0;

            if(isRemained)
            {
                processingRemain(session,in,out);
                return;
            }

            totallen = getTotalLength(in,0);
            log.debug("Total.length[" + totallen + " IN.length = " + in.limit());
            
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
            }
            else if(totallen == in.limit())
            {
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
                frame = decodeFrame(session,in);
                if(frame != null) {
                    if (in.hasRemaining())
                        in.position(in.limit());
                    out.write(frame);
                }
            } else if(totallen < in.limit())
            {
                IoBuffer[] ins = getFrameByteBufferList(in);
                for(int i = 0 ; i < ins.length; i++)
                {
                    isValidFrameCrc = FrameUtil.checkCRC(ins[i]);
                    try
                    {
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
                                    ins[i].get(1)); 
                            session.write(nak);
                        }
                        ins[i].free();
                        continue; 
                    }
                    frame = decodeFrame(session,ins[i]);
                    if(frame != null)
                        out.write(frame);
                    ins[i].free();
                    }catch(Exception ex) {
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
        } catch(Exception ex)
        {
            log.error("FMPDecoder::decode failed : ", ex);
            throw new ProtocolDecoderException(ex.getMessage());
        }
    }
}
