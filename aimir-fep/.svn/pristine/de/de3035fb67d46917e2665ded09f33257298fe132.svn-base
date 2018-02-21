package com.aimir.fep.protocol.fmp.frame;

import java.io.ByteArrayOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;

import com.aimir.fep.protocol.fmp.frame.amu.AMUFramePayLoad;
import com.aimir.fep.util.CRCUtil;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

/**
 * AMUGeneralDataFrame
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 2. 19. 오전 9:55:03$
 */
public class AMUGeneralDataFrame implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;

	private static Log log	= LogFactory.getLog(AMUGeneralDataFrame.class);
	
	public byte soh					= AMUGeneralDataConstants.SOH;
	public AMUFrameControl 			amuFrameControl;	// frame control
	public byte sequence			= 0;
	public byte[] dest_addr 		= null;
	public byte[] source_addr  		= null;
	public byte[] payload_len 		= null;
	
	public byte fp[]				= null;				// frame Payload 
	public AMUFramePayLoad			amuFramePayLoad;
	public byte ft[]				= null;				// frame CRC
	
	/**
	 * constructor
	 */
	public AMUGeneralDataFrame(){
	}
	
	/**
	 * constructor
	 * 
	 * @param soh
	 * @param frameControl
	 * @param sequence
	 * @param destAddr
	 * @param srcAddr
	 * @param payloadLen
	 * @param fp
	 * @param ft
	 */
	public AMUGeneralDataFrame(AMUFrameControl amuFrameControl, byte sequence,
			byte[] destAddr, byte[] sourceAddr, byte[] payloadLen, 
			AMUFramePayLoad amupayLoad, byte[] ft) {
		
		this.soh 				= AMUGeneralDataConstants.SOH;
		this.amuFrameControl 	= amuFrameControl;
		this.sequence 			= sequence;
		this.dest_addr 			= destAddr;
		this.source_addr 		= sourceAddr;
		this.payload_len 		= payloadLen;
		this.amuFramePayLoad	= amupayLoad;
		this.ft 				= ft;
	}
	
	/**
	 * get SOH
	 * @return
	 */
	public byte getSoh() {
		return soh;
	}

	/**
	 * set SOH
	 * @param soh
	 */
	public void setSoh(byte soh) {
		this.soh = soh;
	}

	/**
	 * get AMU Frame Control
	 * @return
	 */
	public AMUFrameControl getAmuFrameControl() {
		return amuFrameControl;
	}

	/**
	 * set AMU Frame Control
	 * @param amuFrameControl
	 */
	public void setAmuFrameControl(AMUFrameControl amuFrameControl) {
		this.amuFrameControl = amuFrameControl;
	}

	/**
	 * get Sequence
	 * @return
	 */
	public byte getSequence() {
		return sequence;
	}

	/**
	 * set Sequence
	 * @param sequence
	 */
	public void setSequence(byte sequence) {
		this.sequence = sequence;
	}

	/**
	 * get Dest Address
	 * @return
	 */
	public byte[] getDest_addr() {
		return dest_addr;
	}

	/**
	 * set Dest Address
	 * @param destAddr
	 */
	public void setDest_addr(byte[] destAddr) {
		dest_addr = destAddr;
	}

	/**
	 * get Source Address
	 * @return
	 */
	public byte[] getSource_addr() {
		return source_addr;
	}

	/**
	 * set Source Address
	 * @param sourceAddr
	 */
	public void setSource_addr(byte[] sourceAddr) {
		source_addr = sourceAddr;
	}

	/**
	 * get Payload Length
	 * @return
	 */
	public byte[] getPayload_len() {
		return payload_len;
	}

	/**
	 * set Payload Length
	 * @param payloadLen
	 */
	public void setPayload_len(byte[] payloadLen) {
		payload_len = payloadLen;
	}

	/**
	 * get Frame Payload Data
	 * @return
	 */
	public byte[] getFp() {
		return fp;
	}

	/**
	 * set Frame Payload Data
	 * @param fp
	 */
	public void setFp(byte[] fp) {
		this.fp = fp;
	}

	/**
	 * get AMUFramePayLoad
	 * @return
	 */
	public AMUFramePayLoad getAmuFramePayLoad() {
		return amuFramePayLoad;
	}

	/**
	 * set AMUFramePayLoad
	 * @param amuFramepayload
	 */
	public void setAmuFramePayLoad(AMUFramePayLoad amuFramePayLoad) {
		this.amuFramePayLoad = amuFramePayLoad;
	}

	/**
	 * get Frame Tail
	 * @return
	 */
	public byte[] getFt() {
		return ft;
	}

	/**
	 * set Frame Tail
	 * @param ft
	 */
	public void setFt(byte[] ft) {
		this.ft = ft;
	}

	/**
	 * decode
	 * @param bytebuffer
	 * @return
	 * @throws Exception
	 */
	public static AMUGeneralDataFrame decode(IoBuffer bytebuffer) throws Exception{
		 log.debug("###########  AMUGeneralDataFrame DECODE START ####################");
		
		 AMUGeneralDataFrame frame = new AMUGeneralDataFrame();
		
		 try{
			
			byte soh 			= 0;
			byte[] control		= null;
			AMUFrameControl fc 	= null;
			
			byte sequence		= 0;		
			byte[] dest_addr	= null;
			byte[] source_addr	= null;
			byte[] payload_len	= null;
			
			byte[] fp			= null;
			byte[] ft			= null; // Frame CRC
			
			// Start Of Header
			soh = bytebuffer.get();						
			
			// Start of Header Validation Check
			if(soh != AMUGeneralDataConstants.SOH){
				log.error("SOH is not valid");
				throw new Exception("SOH is not valid!!!!");
			}

			// Frame Control 
			control	= new byte[AMUGeneralDataConstants.FRAME_CTRL_LEN];
			bytebuffer.get(control , 0 , control.length);	// frame control
			
			fc = AMUFrameControl.decode(control);
			
			// Sequence Number
			sequence = (byte)(bytebuffer.get() & 0xFF);
			log.debug("Sequence : " + DataUtil.getIntToByte(sequence));
			
			// Dest. Address 필드가 존재하면
			if(fc.getDestTypeLenth() > 0){
				dest_addr	= new byte[fc.getDestTypeLenth()];
				bytebuffer.get(dest_addr, 0, fc.getDestTypeLenth());
				log.debug("Dest Address : " + Hex.decode(dest_addr));
			}
			
			// Source Address 필드가 존재하면
			if(fc.getSourceTypeLenth() > 0){
				source_addr	= new byte[fc.getSourceTypeLenth()];
				bytebuffer.get(source_addr, 0, fc.getSourceTypeLenth());
				log.debug("Source Address : " + Hex.decode(source_addr));
			}
			
			// PayLoad Length
			payload_len = new byte[AMUGeneralDataConstants.PAYLOAD_LEN];
			bytebuffer.get(payload_len, 0, AMUGeneralDataConstants.PAYLOAD_LEN);
			log.debug("payload_len : " + DataUtil.getIntTo2Byte(payload_len));
			
			// Frame PayLoad
			fp = new byte[DataUtil.getIntTo2Byte(payload_len)];
			bytebuffer.get(fp, 0,  fp.length );
			
			//log.debug("Frame PayLoad(fp) : " + Hex.decode(fp));
			
			// Frame CRC(Frame Tail)
			ft = new byte[AMUGeneralDataConstants.FRAME_CRC_LEN];
			bytebuffer.get(ft, 0, ft.length );
			log.debug("Frame CRC(Frame Tail) : " + Hex.decode(ft));
			
			frame.setSoh(soh);
			frame.setAmuFrameControl(fc);
			frame.setSequence(sequence);
			frame.setDest_addr(dest_addr);
			frame.setSource_addr(source_addr);
			frame.setPayload_len(payload_len);
			// Frame Payload 원본
			frame.setFp(fp);
			// Frame Payload 에서 헤더 부분만 분석하여 이벤트, 커맨드, 검침 정보 추출
			frame.setAmuFramePayLoad(AMUFramePayLoad.decode(fc.getFrameType(), fp));
			frame.setFt(ft);
			
			// Frame PayLoad decode
			log.debug("FrameType : " + fc.getFrameTypeMessage() );
			frame.toString();			
			
		 }catch(Exception e){
			log.error("AMUGeneralDataFrame failed : ", e);
			throw e;
		 }
		 return frame;
	}
	
	/**
	 * encode
	 * 
	 * @return byte[] 
	 * @throws Exception
	 */
	public byte[] encode() throws Exception{
		
		ByteArrayOutputStream bao= new ByteArrayOutputStream();
		
		try{
			// Start of Header
			bao.write(new byte[]{soh}, 			0, AMUGeneralDataConstants.SOH_LEN);
			// Frame Control
			bao.write(amuFrameControl.encode(), 0, AMUGeneralDataConstants.FRAME_CTRL_LEN);
			// Sequence Number
			bao.write(new byte[]{sequence}, 	0, AMUGeneralDataConstants.SEQ_LEN);
			// Dest Address
			if(amuFrameControl.getDestTypeLenth()!= 0){
				bao.write(dest_addr, 			0, amuFrameControl.getDestTypeLenth());
			}
			// Source Address
			if(amuFrameControl.getSourceTypeLenth()!= 0){
				bao.write(source_addr, 			0, amuFrameControl.getSourceTypeLenth());
			}
			
			// PayLoad Length
			payload_len = DataUtil.get2ByteToInt(fp.length);
			bao.write(payload_len, 				0, AMUGeneralDataConstants.PAYLOAD_LEN);
			// Frame PayLoad
			bao.write(fp, 						0, fp.length);
			// Frame CRC
			byte[] baoData = bao.toByteArray();
			bao.write(CRCUtil.Calculate_ZigBee_Crc(baoData, AMUGeneralDataConstants.CRC_CODE));
			
		}catch(Exception e){
			log.error("AMU GeneralDataFrame encode Error : ", e);
			throw e;
		}
		return bao.toByteArray();
	}
	
	/**
	 * get Serviece Type
	 * Frame Type에 따라 Event Data인지 Metering Data지 구분
	 * @return
	 * @throws Exception 
	 */
	public final String getServieceType()
    {
		byte bf = getAmuFrameControl().getFrameType();
		
		if(bf == AMUGeneralDataConstants.FRAMETYPE_EVENT){
			return AMUGeneralDataConstants.SERVICE_AMU_EVENT;
		}else if( bf == AMUGeneralDataConstants.FRAMETYPE_METERING){
			return AMUGeneralDataConstants.SERVICE_AMU_METERING;
		}else{
			log.error(" Can't Found Serviece Type!!");
		}
		return "";
    }
	
	public String toString(){
		
		final String TAB = "\n";
		StringBuffer retVal = new StringBuffer();
		
		retVal.append("AMUGeneralDataFrame ===================== ")
        	.append("sof : ").append(Hex.decode(new byte[]{soh})).append(TAB)
        	.append("Frame Control : ").append(this.amuFrameControl.toString()).append(TAB)
        	.append("sequence : ").append(Hex.decode(new byte[]{sequence})).append(TAB)
        	.append("Dest address : ").append(Hex.decode(dest_addr)).append(TAB)
        	.append("Source address : ").append(Hex.decode(source_addr)).append(TAB)
        	.append("Payload length : ").append(Hex.decode(payload_len)).append(TAB)
			.append("Payload  : ").append(Hex.decode(fp)).append(TAB);

		return retVal.toString();
	}
}