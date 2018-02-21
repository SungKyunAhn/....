/**
 * (@)# EMnVGeneralFrameDecoder.java
 *
 * 2015. 4. 22.
 *
 * Copyright (c) 2013 NURITELECOM, Inc.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of 
 * NURITELECOM, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with NURITELECOM, Inc.
 *
 * For more information on this product, please see
 * http://www.nuritelecom.co.kr
 *
 */
package com.aimir.fep.protocol.emnv.server;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderException;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.fep.protocol.emnv.actions.EMnVActions.EMnVActionType;
import com.aimir.fep.protocol.emnv.frame.EMnVConstants;
import com.aimir.fep.protocol.emnv.frame.EMnVConstants.FrameControlSecurity;
import com.aimir.fep.protocol.emnv.frame.EMnVConstants.FrameType;
import com.aimir.fep.protocol.emnv.frame.EMnVFrameControl;
import com.aimir.fep.protocol.emnv.frame.EMnVGeneralDataFrame;
import com.aimir.fep.protocol.emnv.frame.payload.EMnVAckNackFramePayLoad;
import com.aimir.fep.protocol.emnv.frame.payload.EMnVMeteringDataFramePayLoad;
import com.aimir.fep.protocol.emnv.frame.payload.EMnVAckNackFramePayLoad.EMnVAckNackType;
import com.aimir.fep.protocol.emnv.log.EMnVTransactionDataLogging;
import com.aimir.fep.util.DataUtil;

/**
 * @author simhanger
 *
 */
public class EMnVGeneralFrameDecoder {
	private static Logger log = LoggerFactory.getLogger(EMnVGeneralFrameDecoder.class);

	// General Frame Format 전체 길이 구하기
	public int getTotalLength(IoBuffer in, int pos) {
		int frameControlPos = EMnVConstants.SOF_LEN 
				+ EMnVConstants.AUTH_LEN 
				+ EMnVConstants.FRAME_TYPE_LEN;

		//General Frame Format - Frame Control
		byte[] frameControl = new byte[EMnVConstants.FRAME_CONTROL_LEN];
		DataUtil.arraycopy(in, pos + frameControlPos, frameControl, 0, frameControl.length);
		
		//DataUtil.convertEndian(lengthField);
		EMnVFrameControl vFrameControl = new EMnVFrameControl(frameControl);

		frameControlPos += EMnVConstants.FRAME_CONTROL_LEN 
				+ vFrameControl.getDST_ADDR_TYPE().getLength() 
				+ vFrameControl.getSRC_ADDR_TYPE().getLength() 
				+ EMnVConstants.SEQUENCE_LEN;

		int payloadLength = 0;
		//General Frame Format - Lenght
		byte[] lenthByte = new byte[EMnVConstants.LENGTH_LEN];
		DataUtil.arraycopy(in, pos + frameControlPos, lenthByte, 0, lenthByte.length);

		if (vFrameControl.getSECURITY_ENABLE() == FrameControlSecurity.SECURITY_DISABLE) { // 보안모드 사용안함.
			payloadLength = DataUtil.getIntTo4Byte(lenthByte);
		} else {
			int paddingLength = (lenthByte[0] & 0xf0) >> 4; // 패딩바이트 길이 
			if (paddingLength == 0) { // 패딩바이트가 16byte일경우 패딩비트를 0으로 세팅하므로 길이구할때 이렇게 처리해야함.
				paddingLength = 16;
			}

			lenthByte[0] = (byte) (lenthByte[0] & 0x0f);
			payloadLength = DataUtil.getIntTo4Byte(lenthByte);
			payloadLength = paddingLength + payloadLength; // 암호화된 payload의 길이 = padding 길이 + 암호화된 payload의 길이
		}

		frameControlPos += EMnVConstants.LENGTH_LEN 
				+ payloadLength 
				+ EMnVConstants.CRC32_LEN;

		return frameControlPos;
	}

	/**
	 * decode input stream
	 *
	 * @param session
	 *            <code>ProtocolSession</code> session
	 * @param in
	 *            <code>ByteBuffer</code> input stream
	 * @param out
	 *            <code>ProtocolDecoderOutput</code> save decoding frame
	 * @throws ProtocolViolationException
	 */
	public boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out, int startPos) throws ProtocolDecoderException {
		try {
			int totallen = 0;

			totallen = getTotalLength(in, startPos);
			log.info("decode : TOTAL_LEN[{}] POS[{}] IN_LIMIT[{}] IN_CAPACITY[{}]]", new Object[] { totallen, startPos, in.limit(), in.capacity() });
			log.info("IoBuffer to HEX ==> {}", in.getHexDump());
			
			in.position(startPos);
			
			/*
			 * 현재는 데이터의 전체길이가 실제 받은 전체길이와 동일한 경우만 처리.
			 */
			if ((startPos + totallen) == in.limit()) {
				decodeFrame(session, in, out, startPos);
				in.position(in.limit());
				return true;
			} else if ((totallen + startPos) < in.limit()) {		
				log.error("===>[Over Data] Buffer Length > Frame Total Length");				
				log.error("[PROTOCOL][GENERAL_FRAME_FORMAT] ==> Processing Stop.");
				return true;
				
			} else {
				log.error("===>[Less Data]  Buffer Length < Frame Total Length");
				
				return false;  // false 리턴시 다음번 decode때 합쳐서 들어옴.				
			}
		} catch (Exception ex) {
			log.error("EMnVGeneralFrameDecoder::decode failed : ", ex);
			throw new ProtocolDecoderException(ex.getMessage());
		} 
	}

	private void decodeFrame(IoSession session, IoBuffer in, ProtocolDecoderOutput out, int startPos) throws Exception {
		//http://www.javased.com/?api=org.apache.mina.core.buffer.IoBuffer 이거 참조
		
		EMnVGeneralDataFrame frame = new EMnVGeneralDataFrame();		
		frame.decode(in.rewind());

		if (!frame.checkCRC32()) {
			log.error("CRC check failed Received Data [{}] : {}" , session.getRemoteAddress(), in.getHexDump());
			session.write(EMnVActionType.EOT);
			return;
		}else{
			if(frame.getFrameType() == FrameType.METERING_DATA_FRAME){
				byte[] rawByte = new byte[in.limit()];
				in.get(rawByte);
						
				EMnVTransactionDataLogging.writeRawMeteringData(frame.getSourceAddress(), rawByte);				
			}
		}
		
		if (frame != null){
			out.write(frame);
		}
	}
}
