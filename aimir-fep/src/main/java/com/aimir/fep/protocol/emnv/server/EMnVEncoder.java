/**
 * (@)# EMnVEncoder.java
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
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.fep.protocol.emnv.actions.EMnVActions.EMnVActionType;
import com.aimir.fep.protocol.emnv.frame.EMnVCRC32;
import com.aimir.fep.protocol.emnv.frame.EMnVConstants.EMnVOTASubFrameType;
import com.aimir.fep.protocol.emnv.frame.EMnVGeneralDataFrame;
import com.aimir.fep.protocol.emnv.frame.payload.EMnVOTAFramePayLoad;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

/**
 * @author simhanger
 */
public class EMnVEncoder extends ProtocolEncoderAdapter {
	private static Logger logger = LoggerFactory.getLogger(EMnVEncoder.class);

	@Override
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
		if (message instanceof EMnVActionType) {
			switch ((EMnVActionType) message) {
			case EOT:
				session.closeNow();
				break;

			default:
				break;
			}
		} else if (message instanceof EMnVGeneralDataFrame) {
			EMnVGeneralDataFrame gFrame = (EMnVGeneralDataFrame) message;
			byte[] bx = gFrame.encode();
			byte[] crc = DataUtil.get4ByteToInt(new EMnVCRC32().calculate_EMnVCRC32(bx));
			IoBuffer buff = IoBuffer.allocate(bx.length + crc.length);
			buff.put(bx);
			buff.put(crc);

			logger.info("Sended [{} SessionId = {}] : DATA_SIZE[{}], LIMIT[{}], POSITION[{}], CAPACITY[{}]", new Object[] { session.getRemoteAddress(), session.getId(), bx.length + crc.length, buff.limit(), buff.position(), buff.capacity() });

			if (gFrame.getGeneralFramePayLoad() instanceof EMnVOTAFramePayLoad) {
				EMnVOTAFramePayLoad oFrame = (EMnVOTAFramePayLoad) gFrame.getGeneralFramePayLoad();
				if (oFrame.getFrameType() == EMnVOTASubFrameType.UPGRADE_DATA) {
					logger.info("### Sended_OTA Data : Sended_CRC : {} ###", Hex.getHexDump(crc));
				} else {
					logger.info("Sended_HEX : {}", Hex.decode(bx));
					logger.info("Sended_CRC : {}", Hex.decode(crc));
				}
			} else {
				logger.info("Sended_HEX : {}", Hex.decode(bx));
				logger.info("Sended_CRC : {}", Hex.decode(crc));
			}

			buff.flip();
			out.write(buff);

			logger.info("Sended_Finished~!!");
		}
	}

}
