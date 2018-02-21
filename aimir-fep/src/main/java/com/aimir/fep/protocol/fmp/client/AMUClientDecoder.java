package com.aimir.fep.protocol.fmp.client;

import java.util.ArrayList;

import com.aimir.fep.protocol.fmp.frame.AMUFrameControl;
import com.aimir.fep.protocol.fmp.frame.AMUGeneralDataConstants;
import com.aimir.fep.protocol.fmp.frame.AMUGeneralDataFrame;
import com.aimir.fep.protocol.fmp.frame.GeneralDataConstants;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FrameUtil;
import com.aimir.fep.util.Hex;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderAdapter;
import org.apache.mina.filter.codec.ProtocolDecoderException;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 * Decodes AMUGeneralDataFrame into input stream
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 2. 19. 오후 6:20:43$
 */
public class AMUClientDecoder extends ProtocolDecoderAdapter{

	private Log log = LogFactory.getLog(AMUClientDecoder.class);
	private IoBuffer dataBuff 		= null;
	private int dataBuffSize 		= 0;
	private boolean isAmuRemained 	= false;
	private byte[] AMUlengthField 	= 
	        new byte[GeneralDataConstants.LENGTH_LEN]; 
	
    /**
     * AMUGeneralDataFrame Data Decode frame buffer stream
     * @param session
     * @param in
     * @return
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
            return null;
        }
        
        log.debug("Received Full Data : " + in.getHexDump());
        frame = AMUGeneralDataFrame.decode(in);
        
        if(FrameUtil.isAmuAck(frame))
        {
            log.debug("AMUClient send ACK - DIRECTION["+Hex.decode(frame.getSource_addr())+"]");
            session.write(FrameUtil.getAMUACK(session,frame));
        }
        
        return frame;
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
    	ArrayList res = new ArrayList();
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
        return (IoBuffer[])res.toArray(new IoBuffer[0]);
    }
    
    /**
     * Get AMU Total Length
     * @param buff
     * @param pos
     * @return	int Total Length
     * @throws Exception 
     */
    private int getAMUTotalLength(IoBuffer buff,int pos) throws Exception
    {
    	
    	log.debug("buffer Size : "+ buff.limit());
    	byte[] fc	= new byte[2];
    		
    	// Get Frame Control Field
		fc[0] = buff.get(1);	
    	fc[1] = buff.get(2);
    	
    	AMUFrameControl afc 	= AMUFrameControl.decode(fc);
    	int destAddressLen		= afc.getDestTypeLenth();
    	int sourceAddressLen	= afc.getSourceTypeLenth();
    	
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
     * Validation Check Frame Header
     * @param buff
     * @param pos
     * @return
     */
    private boolean isValidFrame(IoBuffer buff,int pos)
    {
        if(buff.get(pos) != (byte)0x5E && buff.get(pos) != (byte)0xE1 && buff.get(pos) == (byte) 0x00) {
            if (buff.hasRemaining()) {
				buff.position(buff.limit());
			}
            return false;
        }
        return true;
    }
    
    /**
     * decode input stream
     *
     * @param session <code>ProtocolSession</code> session
     * @param in <code>ByteBuffer</code> input stream
     * @param out <code>ProtocolDecoderOutput</code> save decoding frame
     * @throws  ProtocolDecoderException
     */
	public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out)
			throws ProtocolDecoderException {
		
		try{
			
			log.info("############ AMUClinetDecoder Decode [Start] ############");
	        log.info("Received [" + session.getRemoteAddress() + "] : "+in.getHexDump());
	        log.debug("SOH: "+in.get(0));
	        
			 boolean isActiveAMU = ((Boolean)session.getAttribute(
                        AMUClientProtocolHandler.isActiveAMUKey))
                .booleanValue();
            log.debug("decode isActiveAMU["+isActiveAMU+"]");

            if(!isActiveAMU)
            {
                byte[] bx = new byte[in.limit()];
                in.get(bx,0,bx.length);
                out.write(bx);
                return;
            }
			//--------------------
	        //	AMU General Frame
	        //--------------------
            log.debug("############# isAmuRemained " + isAmuRemained );
            if(isAmuRemained || FrameUtil.isAmuGeneralDataFrame(in.get(0))){
            	
            	log.debug("################################");
                log.debug("###### AMU General FRAME #######");
                log.debug("################################");
                
                AMUGeneralDataFrame	frame	= null;
                
                if(isAmuRemained){
                	processingAMURemain(session,in,out);
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
                // buffer size ==  Frame Total Length
                }else if(totallen == in.limit()){
                	log.debug("[Equal] Buffer Length = AMU Frame Total Length");
                    frame = decodeAMUGeneralFrame(session,in);
                    
                    if(frame != null) {
                        if (in.hasRemaining()) {
                            in.position(in.limit());
                        }
                        out.write(frame);
                    }
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
            // 예외처리  0D 0A 4E 4F 20 43 41 52 52 49 45 52 0D 0A (NO CARRIER)
            else{
            	log.debug("###################");
            	byte[] msg = new byte[in.limit()];
                in.get(msg,0,msg.length);
                String message = new String(msg);
                if(message.toLowerCase().indexOf("carrier") > 0){
                    log.debug("exception  handling [ "+ message +"]");
                    out.write(in);
                    return;
                }else{
                	throw new Exception("Mobile is busy!");
                }
            }
	        log.info("############ AMUClinetDecoder Decode [End] ############");
		} catch(Exception ex)
        {
            log.error("AMUClinetDecoder::decode failed : ", ex);
            throw new ProtocolDecoderException(ex.getMessage());
        }
		
	}
}


