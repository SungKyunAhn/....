/**
 * (@)# ResponseLink.java
 *
 * 2015. 6. 22.
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
package com.aimir.fep.protocol.emnv.frame.payload;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.constants.CommonConstants.TR_STATE;
import com.aimir.dao.device.AsyncCommandLogDao;
import com.aimir.fep.protocol.emnv.frame.EMnVConstants.EMnVLinkSubFrameType;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.AsyncCommandLog;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;

/**
 * @author simhanger
 *
 */
public class ResponseLink implements ILinkFrame {

	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(RequestLink.class);
	private EMnVLinkSubFrameType type = EMnVLinkSubFrameType.LINK_RESPONSE;

	private Long transactionId;      // 4byte
	private String date = null;     // 7byte, yyyyMMddhhmmss
	private String serverIp = null; // 4byte
	private int serverPort = 0;     // 2byte

	public EMnVLinkSubFrameType getType() {
		return type;
	}

	public Long getTransactionId() {
		return transactionId;
	}

	public String getDate() {
		return date;
	}

	public String getServerIp() {
		return serverIp;
	}

	public int getServerPort() {
		return serverPort;
	}

	@Override
	public void decode(byte[] data) {
		try {
			log.debug("[PROTOCOL][LINK_TYPE={}] RESPONSE LINK({}byte) ==> {}", new Object[] { type.name(), data.length, Hex.decode(data) });

			byte[] transctionIdByte = new byte[4];
			System.arraycopy(data, 0, transctionIdByte, 0, 4);
			transactionId = DataUtil.getLongToBytes(transctionIdByte);

			byte[] dateByte = new byte[7];
			System.arraycopy(data, 4, dateByte, 0, 7);
			date = makeDateTime(dateByte);

			byte[] serverIpByte = new byte[4];
			System.arraycopy(data, 11, serverIpByte, 0, 4);
			serverIp = DataUtil.decodeIpAddr(serverIpByte);

			byte[] serverPortByte = new byte[2];
			System.arraycopy(data, 15, serverPortByte, 0, 2);
			serverPort = DataUtil.getIntTo2Byte(serverPortByte);

			//			byte[] reserved = new byte[6];    //사용안함.
			//			System.arraycopy(data, 17, reserved, 0, 6);
			//			reserved = DataUtil.trim0x00Byte(reserved);

			log.debug("[PROTOCOL][LINK_TYPE][{}] RESPONSE_LINK({}byte):[{}] ==> {}", new Object[] { type.name(), data.length, "", toString() });
		} catch (Exception e) {
			log.debug("ResponseLink decode error - {}", e);
		}
	}

	private String makeDateTime(byte[] data) throws Exception {
		int year = DataFormat.getIntTo2Byte(DataFormat.select(data, 0, 2));
		int month = DataFormat.getIntToByte(data[2]);
		int day = DataFormat.getIntToByte(data[3]);
		int hour = DataFormat.getIntToByte(data[4]);
		int min = DataFormat.getIntToByte(data[5]);
		int second = DataFormat.getIntToByte(data[6]);

		return String.format("%4d%02d%02d%02d%02d%02d", year, month, day, hour, min, second);
	}

	@Override
	public byte[] encode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ResponseLink [type=");
		builder.append(type);
		builder.append(", transactionId=");
		builder.append(transactionId);
		builder.append(", date=");
		builder.append(date);
		builder.append(", serverIp=");
		builder.append(serverIp);
		builder.append(", serverPort=");
		builder.append(serverPort);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public boolean isValidation(Object deviceId) {
		boolean result = true;
		
		if(type == null || transactionId == 0 || date == null || serverIp == null || serverPort == 0){
			return false;
		}

		//임시
//		log.debug("############임시코드지워라########");
//		deviceId = "01220000068";
//		Long transactionId = Long.parseLong("44252599");
		
		
		/** KEMCO에서는 아래와 같은 의미로 사용한다.
		 * TR_STATE.Success   (0) : Command 수행 성공및 정상종료 상태
		 * TR_STATE.Waiting   (1) : 서버측에서 SMS전송후 모뎀의 접속을 기다리는 상태
		 * TR_STATE.Running   (2) : Command 수행중인 상태
		 * TR_STATE.Terminate (4) : Command 가 성공하지 못한 상태에서 종료된 상태 (ex. crc error)
		 * TR_STATE.Delete    (8) : 다른 Tranasction의 동일 Command가 실행되어(단지 실행) 삭제처리된 상태
		 * TR_STATE.Unknown (255) : 아몰랑.
		 */
		// 해당 Transaction ID가 있는지 확인
		// 비동기 내역 조회
        AsyncCommandLogDao acld = DataUtil.getBean(AsyncCommandLogDao.class);
        Set<Condition> condition = new HashSet<Condition>();
        condition.add(new Condition("deviceId", new Object[]{deviceId.toString()}, null, Restriction.EQ));
        condition.add(new Condition("id.trId", new Object[]{transactionId}, null, Restriction.EQ));
        condition.add(new Condition("state", new Object[]{TR_STATE.Waiting.getCode()}, null, Restriction.EQ));
        List<AsyncCommandLog> acllist = acld.findByConditions(condition);
        
		log.debug("ResponseLink AsyncCommandLog_SIZE[{}][{}]", acllist == null ? "null" : acllist.size(), deviceId.toString() + "-" + transactionId);        
        
		if(acllist == null || acllist.size() < 1){
			result = false;
		}
		
		return result;
	}


}
