/**
 * (@)# EMnVCumulativeDecoder.java
 *
 * 2015. 4. 16.
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
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderException;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.fep.protocol.emnv.actions.EMnVActions.EMnVActionType;
import com.aimir.fep.protocol.emnv.frame.EMnVConstants;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FrameUtil;
import com.aimir.fep.util.Hex;
import com.aimir.util.TimeUtil;

/**
 * @author nuri
 */
public class EMnVDecoder extends CumulativeProtocolDecoder {
	private static Logger log = LoggerFactory.getLogger(EMnVDecoder.class);

	/**
	 * 프레임의 첫 3바이트를 체크해 올바른 프레임인지 체크한다
	 * EMnVGeneralFrame = 0x4B 0x45 0x50   // KEP
	 * @param buff
	 * @param pos
	 * @return
	 */
	
	private boolean isValidFrame(IoBuffer buff, byte[] sofByte){		
		if(!FrameUtil.isEMnVGeneralFrame(sofByte)){
			if(buff.hasRemaining()){
				buff.position(buff.limit());
			}
			return false;			
		}
		return true;
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
	@Override
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws ProtocolDecoderException {
		try {
			
			// 로그 확인 편하도록....
			log.info("    ");
			log.info("    ");
			log.info("    ");
			log.info("############################## 로그확인 시작 ################################################");
			
			
			log.info("Received [{} SessionId = {}] : LIMIT[{}] POSITION[{}] CAPACITY[{}]"
					, new Object[]{session.getRemoteAddress(), session.getId(), in.limit(), in.position(), in.capacity()});
			log.debug(" IN_HEX : {}", in.getHexDump());
			
			if (session.getAttribute("startLongTime") == null) {
				session.setAttribute("startLongTime", System.currentTimeMillis());
				session.setAttribute("startTime", TimeUtil.getCurrentTime());
			}

			int startPos = in.position();
			
			// 앞 3바이트
			byte[] sofByte = new byte[EMnVConstants.SOF_LEN];
			in.get(sofByte, 0, EMnVConstants.SOF_LEN);
			
			log.info("[PROTOCOL][GENERAL_FRAME_FORMAT] START_FLAG(3):[{}] ==> {}", DataUtil.getString(sofByte), Hex.decode(sofByte));

			if (!isValidFrame(in, sofByte)) {
				log.error("Data[{}] is Unknown Frame ~~ !!", in.getHexDump());
				session.write(EMnVActionType.EOT);
				return true;
			}

			/**
			 * EMnV General Frame 처리
			 */
			EMnVGeneralFrameDecoder decode = new EMnVGeneralFrameDecoder();
			if(!decode.doDecode(session, in.rewind(), out, startPos)){
				log.info("[PROTOCOL][GENERAL_FRAME_FORMAT] ==> Processing Skip. and next continue");
				return false;    // false로 리턴하면 데이터가 다오지 않은것으로 간주하고 다음에 오는 데이터와 합쳐서 보내준다.
			}
			
			return true;
		} catch (Exception e) {
			log.error("EMnVDecoder::decode failed : ", e);
			throw new ProtocolDecoderException(e.getMessage());
		}
	}



}
