package com.aimir.fep.protocol.security.frame;

import com.aimir.fep.util.DataUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import com.aimir.fep.util.Hex;

public class AuthGeneralFrame implements java.io.Serializable{	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(AuthGeneralFrame.class);
	// 

	public enum Pending {
		FALSE((byte)0x00),
		TRUE((byte)0x01);

		private byte code;

		Pending(byte code) {
			this.code = code;
		}

		public byte getCode() {
			return this.code;
		}
	}
    
	
	public static final byte[] SOH 					= {(byte)0x01, (byte)0x14};

	public Pending 	foPending;
	public byte foFrameCount;
	public FrameType foFrameType;

	public short version = 0;
    public byte  sequenceNumber = 0;
    public byte[] deviceSerial = null;
    public int   payloadLength = 0;
    public byte  payload[];
    

    public AuthGeneralFrame(FrameType frameType, Pending pending, byte frameCount, 
    		short version, byte sequenceNumber, byte[] deviceSerial, int payload_length,  byte[] payload)
    {
    	this.foFrameType = frameType;
    	this.foFrameCount = frameCount;
    	this.foPending = pending;
    	this.version = version;
    	this.deviceSerial = deviceSerial;
    	this.payload = new byte[payload_length];
    	System.arraycopy(payload, 0, this.payload, 0, payload_length);
    	this.payloadLength = payload_length;
    	this.sequenceNumber = sequenceNumber;
    }
 
	/**
	 * Copy Constructor
	 *
	 * @param AuthFrameData a <code>AuthFrameData</code> object
	 */
	public AuthGeneralFrame(AuthGeneralFrame pData)
	{	
	    this.foFrameType = pData.foFrameType;
    	this.foFrameCount = pData.foFrameCount;
    	this.foPending = pData.foPending;
	    this.version = pData.version;
	    this.deviceSerial = pData.deviceSerial;
	    this.payloadLength = pData.payloadLength;
	    this.payload = pData.payload;
	    this.sequenceNumber =pData.sequenceNumber;
	}


	public AuthGeneralFrame() {

	}



	/**
	 * @return the foPending
	 */
	public Pending getFoPending() {
		return foPending;
	}

	/**
	 * @param foPending the foPending to set
	 */
	public void setFoPending(Pending foPending) {
		this.foPending = foPending;
	}
	
	public void setFoPending(byte code) {
		for (Pending c : Pending.values()) {
			if (c.getCode() == code){ 
				foPending = c;
				break;
			}
		}
	}
	/**
	 * @return the foFrameCount
	 */
	public byte getFoFrameCount() {
		return foFrameCount;
	}

	/**
	 * @param foFrameCount the foFrameCount to set
	 */
	public void setFoFrameCount(byte foFrameCount) {
		this.foFrameCount = foFrameCount;
	}

	/**
	 * @return the foFrameType
	 */
	public FrameType getFoFrameType() {
		return foFrameType;
	}

	/**
	 * @param foFrameType the foFrameType to set
	 */
	public void setFoFrameType(FrameType foFrameType) {
		this.foFrameType = foFrameType;
	}
	
	public void setFoFrameType(byte code) {
		for (FrameType c : FrameType.values()) {
			if (c.getCode() == code){ 
				foFrameType = c;
				break;
			}
		}
	}
	/**
	 * @return the sequenceNumber
	 */
	public byte getSequenceNumber() {
		return sequenceNumber;
	}

	/**
	 * @param sequenceNumber the sequenceNumber to set
	 */
	public void setSequenceNumber(byte sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	/**
	 * @return the version
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(short version) {
		this.version = version;
	}

	/**
	 * @return the deiveceSerial
	 */
	public byte[] getdeiveceSerial() {
		return this.deviceSerial;
	}

	/**
	 * @param deviceSerial the deviceSerial to set
	 */
	public void setdeiveceSerial(byte[] deviceSerial) {
		this.deviceSerial = deviceSerial;
	}

	/**
	 * @return the payload_length
	 */
	public int getPayloadLength() {
		return payloadLength;
	}

	/**
	 * @param payload_length the payload_length to set
	 */
	public void setPayloadLength(int payload_length) {
		this.payloadLength = payload_length;
	}

	/**
	 * @return the payload
	 */
	public byte[] getPayload() {
		return payload;
	}


	/**
	 * @param payload the payload to set
	 */
	public void setPayload(byte[] payload) {
		this.payload = payload;
	}
	
	/**
	 * @param bytebuffer
	 * @return
	 * @throws Exception
	 */
	public static AuthGeneralFrame decode(IoBuffer bytebuffer)
	throws Exception{
		 log.debug("###########  AuthDataFrame DECODE START ####################");
		
		 AuthGeneralFrame frame = new AuthGeneralFrame();
		
		 try{
			byte[] soh 			= new byte[AuthFrameConstants.START_FLAG_LEN];
			byte frameOption = 0;
					
			// Start Of Header
			bytebuffer.get(soh, 0, AuthFrameConstants.START_FLAG_LEN);
			
			// FrameOption
			frameOption = bytebuffer.get();
	        log.debug("frameOption = " + frameOption);
	        
	        byte pending = AuthFrameUtil.getOptionPending(frameOption);
	        frame.setFoPending(pending);
	        log.debug("pending = " + frame.foPending);
	        
	        byte  frameCount =  AuthFrameUtil.getOptionFrameCount(frameOption);
	        log.debug("frameCount = " + frameCount);
	        frame.setFoFrameCount(frameCount);
	        
	        byte  frameType = AuthFrameUtil.getOptionFrametype(frameOption);

	        frame.setFoFrameType(frameType);
	        log.debug("frameType = " + frame.foFrameType );
	        
	        // Version
	        byte[] version = new byte[AuthFrameConstants.VERSION_LEN];
	        bytebuffer.get(version, 0, version.length);
	        frame.version = (short)DataUtil.getIntTo2Byte(version);
	        log.debug("version = " +  frame.version);
	        
	        // Sequence Number
	        byte sequenceNumber = bytebuffer.get();
	        frame.sequenceNumber = sequenceNumber;
	        log.debug("sequenceNumber = " + sequenceNumber);

	        // deviceSerial
	        byte[] deviceSerial = new byte[AuthFrameConstants.DEVICE_SERIAL_LEN];
	        bytebuffer.get(deviceSerial, 0, deviceSerial.length);
	        frame.deviceSerial = deviceSerial;
	        log.debug("deviceSerial = " + Hex.decode(deviceSerial));
	        
	        // Payload Length 
	        byte[] lengthbuff = new byte[AuthFrameConstants.PAYLOAD_LENGTH_LEN];
	        bytebuffer.get(lengthbuff, 0, lengthbuff.length);
	        frame.payloadLength = DataUtil.getIntTo2Byte(lengthbuff);
	        log.debug("payloadLength = " + frame.payloadLength);
	        
	        frame.payload = new byte[frame.payloadLength];
	        bytebuffer.get(frame.payload, 0, frame.payloadLength);
	        
	        frame.sequenceNumber = sequenceNumber;
	        log.debug("sequenceNumber = " + sequenceNumber);	
		 }catch(Exception e){
			log.error("AuthDataFrame failed : ", e);
			throw e;
		 }
		 return frame;
	}
	
	/**
	 * @param pending
	 * @param reserved
	 * @param frameCount
	 * @param frameType
	 * @return
	 */
	public byte getFrameOption(byte pending, byte reserved, byte frameCount, byte frameType){
		// Frame Option
		int  op = 0;
		
		// Pending 
		int pend = ((int)pending  & 0xFF ) << 7;
		op = op | pend;
		
		// Reserved = 0
		int reserv = ((int)reserved & 0xFF ) << 6;
		
		// Frame Count = 0
		int fcnt = ((int)frameCount & 0xFF ) << 4;
		op = op | fcnt;
		
		// FrameType 
		op = op | ((int)frameType & 0xFF );
		
		byte option = DataUtil.getByteToInt(op);
		return option;
		
	}
	/**
	 * @return
	 */
	public byte[] encode() {
		int length = AuthFrameConstants.HEADER_LEN + payloadLength + AuthFrameConstants.TAIL_LEN;
		byte[]  frame = new byte[length];
		int offset = 0;

		// Start Flag
		System.arraycopy(AuthFrameConstants.SOH,  0,  frame, offset, AuthFrameConstants.START_FLAG_LEN);
		offset += AuthFrameConstants.START_FLAG_LEN;
		
		// Frame Option	
		byte option = getFrameOption(foPending.getCode(),(byte)0, foFrameCount, foFrameType.getCode());
		frame[offset] = option;
		offset ++;
		
		// Version
		byte[] version = DataUtil.get2ByteToInt((int)AuthFrameConstants.VERSION);
		System.arraycopy(version, 0,  frame, offset, AuthFrameConstants.VERSION_LEN);
		offset +=  AuthFrameConstants.VERSION_LEN;
		
		// Sequence Number
		byte seqNo = 0;
		frame[offset] = seqNo;
		offset ++;
		
		// device serial
		byte[] deviceSerial = new byte[AuthFrameConstants.DEVICE_SERIAL_LEN];
		System.arraycopy(deviceSerial, 0,  frame, offset, AuthFrameConstants.DEVICE_SERIAL_LEN);
		offset +=  AuthFrameConstants.DEVICE_SERIAL_LEN;
		
		// Payload Length
		byte[] len = DataUtil.get2ByteToInt(payloadLength);
		System.arraycopy(len, 0, frame, offset, AuthFrameConstants.VERSION_LEN);
		offset +=  AuthFrameConstants.PAYLOAD_LENGTH_LEN;
		
		// Payload 
		System.arraycopy(payload, 0, frame, offset, payloadLength);
		offset += payloadLength;
		
//		//CRC - Left
//		CRC16 crc16 = new CRC16();
//		crc16.update(frame, 0, offset);
//		short check = crc16.checksum();
///
//		// CRC - Right
		CRCR crc16r = new CRCR();
		crc16r.update(frame, 0, offset);
		short check = (short)(crc16r.getValue()) ; 
		
		byte[] crc = DataUtil.get2ByteToInt(check);
		byte[] crcReverse = new byte[2];
		crcReverse[0] = crc[1];
		crcReverse[1] = crc[0];
		System.arraycopy(crcReverse, 0, frame, offset, AuthFrameConstants.TAIL_LEN);
		
		log.debug("Frame:" + Hex.getHexDump(frame));
		return frame;
	}
	
	/**
	 * Constructs a <code>String</code> with all attributes
	 * in name = value format.
	 *
	 * @return a <code>String</code> representation
	 * of this object.
	 */
	public String toString()
	{
	    final String TAB = "\n";

	    StringBuffer retValue = new StringBuffer();
	    
	    retValue.append("AuthGeneralFrame ( ")
	        .append(super.toString()).append(TAB)
	        .append("framePending = ").append(this.foPending).append(TAB)
	        .append("frameCount = ").append(this.foFrameCount).append(TAB)
	        .append("frameType = ").append(this.foFrameType).append(TAB)
	        .append("version = ").append(this.version).append(TAB)
	        .append("deviceSerial = ").append(Hex.decode(this.deviceSerial)).append(TAB)
	        .append("payload_length = ").append(this.payloadLength).append(TAB)
	        .append("payload = ").append(Hex.getHexDump(this.payload)).append(TAB)
	        .append(" )");

	    return retValue.toString();
	}
}
