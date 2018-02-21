/**
 * (@)# EMnVProtocolHandler.java
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

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.TR_STATE;
import com.aimir.dao.device.AsyncCommandLogDao;
import com.aimir.fep.protocol.emnv.actions.ActionDevice;
import com.aimir.fep.protocol.emnv.actions.EMnVActions;
import com.aimir.fep.protocol.emnv.actions.EMnVActions.EMnVActionType;
import com.aimir.fep.protocol.emnv.actions.EMnVActions.RWSType;
import com.aimir.fep.protocol.emnv.actions.EMnVEventLogAction;
import com.aimir.fep.protocol.emnv.actions.EMnVHWResetAction;
import com.aimir.fep.protocol.emnv.actions.EMnVHWResetIntervalAction;
import com.aimir.fep.protocol.emnv.actions.EMnVInverterInfoAction;
import com.aimir.fep.protocol.emnv.actions.EMnVInverterLogAction;
import com.aimir.fep.protocol.emnv.actions.EMnVInverterSetupAction;
import com.aimir.fep.protocol.emnv.actions.EMnVKeyChangeAction;
import com.aimir.fep.protocol.emnv.actions.EMnVLPIntervalAction;
import com.aimir.fep.protocol.emnv.actions.EMnVMNumberAction;
import com.aimir.fep.protocol.emnv.actions.EMnVMeterScanAction;
import com.aimir.fep.protocol.emnv.actions.EMnVMeterTimeSyncAction;
import com.aimir.fep.protocol.emnv.actions.EMnVNVResetAction;
import com.aimir.fep.protocol.emnv.actions.EMnVOTAAction;
import com.aimir.fep.protocol.emnv.actions.EMnVOnDemandAction;
import com.aimir.fep.protocol.emnv.actions.EMnVServerIpAction;
import com.aimir.fep.protocol.emnv.actions.EMnVServerPortAction;
import com.aimir.fep.protocol.emnv.frame.EMnVConstants.EMnVCommandSubFrameType;
import com.aimir.fep.protocol.emnv.frame.EMnVConstants.EMnVOTASubFrameType;
import com.aimir.fep.protocol.emnv.frame.EMnVConstants.FrameType;
import com.aimir.fep.protocol.emnv.frame.EMnVFrameUtil;
import com.aimir.fep.protocol.emnv.frame.EMnVGeneralDataFrame;
import com.aimir.fep.protocol.emnv.frame.payload.EMnVAckNackFramePayLoad;
import com.aimir.fep.protocol.emnv.frame.payload.EMnVAckNackFramePayLoad.EMnVAckNackType;
import com.aimir.fep.protocol.emnv.frame.payload.EMnVCommandFramePayLoad;
import com.aimir.fep.protocol.emnv.frame.payload.EMnVLinkFramePayLoad;
import com.aimir.fep.protocol.emnv.frame.payload.EMnVMeteringDataFramePayLoad;
import com.aimir.fep.protocol.emnv.frame.payload.EMnVOTAFramePayLoad;
import com.aimir.fep.protocol.emnv.frame.payload.ResponseLink;
import com.aimir.fep.protocol.fmp.processor.ProcessorHandler;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.FrameUtil;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.AsyncCommandLog;
import com.aimir.model.system.Code;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;

/**
 * @author simhanger
 */
@Component
public class EMnVProtocolHandler extends IoHandlerAdapter {
	private static Logger logger = LoggerFactory.getLogger(EMnVProtocolHandler.class);
	private int sessionIdleTime = Integer.parseInt(FMPProperty.getProperty("protocol.idle.time", "5"));
	private int enqTimeout = Integer.parseInt(FMPProperty.getProperty("protocol.enq.timeout", "10"));
	private int retry = Integer.parseInt(FMPProperty.getProperty("protocol.retry", "3"));
	private int ackTimeout = Integer.parseInt(FMPProperty.getProperty("protocol.ack.timeout", "3"));
	private Integer protocolType = new Integer(FMPProperty.getProperty("protocol.type.LTE", "12")); // LTE
	private EMnVActions action;
	private AsyncCommandLogDao acld = null;
	private Set<Condition> condition = null;

	@Autowired
	private ProcessorHandler processorHandler;

	private void putServiceData(String serviceType, Serializable data) {
		try {
			processorHandler.putServiceData(serviceType, data);
		} catch (Exception e) {
			logger.error("processorHandler putServiceData Error - ", e);
		}
	}

	/**
	 * 
	 */
	public EMnVProtocolHandler() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		session.getConfig().setWriteTimeout(enqTimeout);
		session.getConfig().setIdleTime(IdleStatus.READER_IDLE, sessionIdleTime);
		
    	SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    	Date d = new Date(session.getLastWriterIdleTime());
		logger.info("### sessionOpened : " + session.getRemoteAddress() + ", lastWriteIdleTime : " + sf.format(d));
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		Iterator<Object> keys = session.getAttributeKeys().iterator();
		while (keys.hasNext()) {
			Object key = keys.next();
			logger.info("key={}, value={}", key, session.getAttribute(key));
			session.removeAttribute(key);
		}
		logger.debug("########## 디버깅 ##############");
		logger.info("### Bye Bye ~ Client session closed from {}", session.getRemoteAddress().toString());
		session.closeNow();
		logger.info("### this Session is being closed or closed? = {}, Session isConnected = {}, Session isBothIdle = {}", new Object[] { session.isClosing(), session.isConnected(), session.isBothIdle() });
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		int idleCount = session.getIdleCount(IdleStatus.READER_IDLE);
		logger.info("### Session = {}, IDLE COUNT={}", session.getRemoteAddress(), idleCount);
		if (idleCount >= retry) {
			session.write(EMnVActionType.EOT);
		}
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		logger.error("exceptionCaught - {}", cause.getMessage());

		// EOT 호출하고 종료            	
		session.write(EMnVActionType.EOT);
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		logger.info("### [MESSAGE_RECEIVED] from={}  SessionId={} ###", session.getRemoteAddress().toString(), session.getId());
		String modemDeviceSerial = ""; // Modem id
		try {

			if (message instanceof EMnVGeneralDataFrame) {
				EMnVGeneralDataFrame gDataFrame = (EMnVGeneralDataFrame) message;
				modemDeviceSerial = gDataFrame.getSourceAddress();

				logger.debug("### [{}] Message Object [{}][{}]", new Object[] { modemDeviceSerial, message.getClass().getName(), gDataFrame.getFrameType() });

				/**
				 * 012로 시작하지 않는 모뎀은 차단 - 01220 : SK 누리 - 01221 : KT 옴니 - 01222 :
				 * LG 네오비
				 */
				if (modemDeviceSerial.length() < 11 || !modemDeviceSerial.substring(0, 3).equals("012")) {
					logger.warn("Invalid Modem - {}", modemDeviceSerial);
					session.write(EMnVActionType.EOT);
					return;
				}

				switch (gDataFrame.getFrameType()) {
				case LINK_FRAME:
					/**
					 * KEMCO에서는 아래와 같은 의미로 사용한다. TR_STATE.Success (0) : Command
					 * 수행 성공및 정상종료 상태 TR_STATE.Waiting (1) : 서버측에서 SMS전송후 모뎀의
					 * 접속을 기다리는 상태 TR_STATE.Running (2) : Command 수행중인 상태
					 * TR_STATE.Terminate (4) : Command 가 성공하지 못한 상태에서 종료된 상태
					 * TR_STATE.Delete (8) : 다른 Tranasction의 동일 Command가 실행되어(단지
					 * 실행) 삭제처리된 상태 TR_STATE.Unknown (255) : 아몰랑.
					 */
					EMnVLinkFramePayLoad lFrame = (EMnVLinkFramePayLoad) gDataFrame.getGeneralFramePayLoad();
					lFrame.decode();
					Long transactionId = ((ResponseLink) lFrame.getLinkFrame()).getTransactionId();
					logger.info("[PROTOCOL][LINK_FRAME][{}][{}] TRANSACTION_ID={}, DATE={}, SERVER_IP={}, SERVER_PORT={}, REMOTE_ADDRESS={}",
							new Object[] { lFrame.getFrameType(), modemDeviceSerial, transactionId, ((ResponseLink) lFrame.getLinkFrame()).getDate(), ((ResponseLink) lFrame.getLinkFrame()).getServerIp(), ((ResponseLink) lFrame.getLinkFrame()).getServerPort(), session.getRemoteAddress() });
					if (lFrame.getFrameType() != null && lFrame.isValidation(modemDeviceSerial)) {
						acld = DataUtil.getBean(AsyncCommandLogDao.class);
						condition = new HashSet<Condition>();
						condition.add(new Condition("deviceId", new Object[] { modemDeviceSerial }, null, Restriction.EQ));
						condition.add(new Condition("id.trId", new Object[] { transactionId }, null, Restriction.EQ));
						condition.add(new Condition("state", new Object[] { TR_STATE.Waiting.getCode() }, null, Restriction.EQ));

						List<AsyncCommandLog> acllist = acld.findByConditions(condition);
						if (0 < acllist.size()) {
							// mobile번호와 Transaction ID로 접속한 모뎀이 해야할일을 지정한다.
							String command = acllist.get(0).getCommand();
							if (command != null && !command.equals("")) {
								ActionDevice ad = null;
								/*
								 * OTA
								 */
								if (command.equals("cmdOTAStart")) {
									ad = new ActionDevice(EMnVActionType.OTA); // 제일 처음 접속시 Session에 ActionDevice를 저장한다.
									action = new EMnVOTAAction(EMnVOTASubFrameType.UPGRADE_START_REQUEST);
									ad.setTransactionId(transactionId);
									ad.setCommand(command);
									ad.setModemId(modemDeviceSerial);
									ad.setTrState(TR_STATE.Unknown);
									session.setAttribute(session.getRemoteAddress(), ad);

									action.execute(lFrame, session, EMnVFrameUtil.getGeneralDataFrame(FrameType.OTA_FRAME, gDataFrame));
								}

								/*
								 * OnDemand
								 */
								else if (command.equals("cmdOnDemand")) {
									ad = new ActionDevice(EMnVActionType.COMMAND); // 제일 처음 접속시 Session에 ActionDevice를 저장한다.
									action = new EMnVOnDemandAction(EMnVCommandSubFrameType.COMMAND_REQUEST);
									ad.setTransactionId(transactionId);
									ad.setCommand(command);
									ad.setModemId(modemDeviceSerial);
									ad.setTrState(TR_STATE.Unknown);
									session.setAttribute(session.getRemoteAddress(), ad);

									action.execute(lFrame, session, EMnVFrameUtil.getGeneralDataFrame(FrameType.COMMAND_FRAME, gDataFrame));
								}

								/*
								 * cmdOnDemandInverterLog
								 */
								else if (command.equals("cmdOnDemandInverterLog")) {
									ad = new ActionDevice(EMnVActionType.COMMAND); // 제일 처음 접속시 Session에 ActionDevice를 저장한다.
									action = new EMnVInverterLogAction(EMnVCommandSubFrameType.COMMAND_REQUEST);
									ad.setTransactionId(transactionId);
									ad.setCommand(command);
									ad.setModemId(modemDeviceSerial);
									ad.setTrState(TR_STATE.Unknown);
									session.setAttribute(session.getRemoteAddress(), ad);

									action.execute(lFrame, session, EMnVFrameUtil.getGeneralDataFrame(FrameType.COMMAND_FRAME, gDataFrame));
								}

								/*
								 * Meter Time Sync
								 */
								else if (command.equals("cmdSetMeterTime")) {
									ad = new ActionDevice(EMnVActionType.COMMAND); // 제일 처음 접속시 Session에 ActionDevice를 저장한다.
									action = new EMnVMeterTimeSyncAction(EMnVCommandSubFrameType.COMMAND_REQUEST);
									ad.setTransactionId(transactionId);
									ad.setCommand(command);
									ad.setModemId(modemDeviceSerial);
									ad.setTrState(TR_STATE.Unknown);
									session.setAttribute(session.getRemoteAddress(), ad);

									action.execute(lFrame, session, EMnVFrameUtil.getGeneralDataFrame(FrameType.COMMAND_FRAME, gDataFrame));
								}

								/*
								 * Meter Scan
								 */
								else if (command.equals("cmdSetMeterScan")) {
									ad = new ActionDevice(EMnVActionType.COMMAND); // 제일 처음 접속시 Session에 ActionDevice를 저장한다.
									action = new EMnVMeterScanAction(EMnVCommandSubFrameType.COMMAND_REQUEST);
									ad.setTransactionId(transactionId);
									ad.setCommand(command);
									ad.setModemId(modemDeviceSerial);
									ad.setTrState(TR_STATE.Unknown);
									session.setAttribute(session.getRemoteAddress(), ad);

									action.execute(lFrame, session, EMnVFrameUtil.getGeneralDataFrame(FrameType.COMMAND_FRAME, gDataFrame));
								}

								/*
								 * Server IP R/W
								 */
								else if (command.equals("cmdServerIp")) {
									ad = new ActionDevice(EMnVActionType.COMMAND); // 제일 처음 접속시 Session에 ActionDevice를 저장한다.
									action = new EMnVServerIpAction(EMnVCommandSubFrameType.COMMAND_REQUEST);
									ad.setTransactionId(transactionId);
									ad.setCommand(command);
									ad.setModemId(modemDeviceSerial);
									ad.setTrState(TR_STATE.Unknown);
									session.setAttribute(session.getRemoteAddress(), ad);

									action.execute(lFrame, session, EMnVFrameUtil.getGeneralDataFrame(FrameType.COMMAND_FRAME, gDataFrame));
								}

								/*
								 * Server PORT R/W
								 */
								else if (command.equals("cmdServerPort")) {
									ad = new ActionDevice(EMnVActionType.COMMAND); // 제일 처음 접속시 Session에 ActionDevice를 저장한다.
									action = new EMnVServerPortAction(EMnVCommandSubFrameType.COMMAND_REQUEST);
									ad.setTransactionId(transactionId);
									ad.setCommand(command);
									ad.setModemId(modemDeviceSerial);
									ad.setTrState(TR_STATE.Unknown);
									session.setAttribute(session.getRemoteAddress(), ad);

									action.execute(lFrame, session, EMnVFrameUtil.getGeneralDataFrame(FrameType.COMMAND_FRAME, gDataFrame));
								}

								/*
								 * Server LP Interval R/W
								 */
								else if (command.equals("cmdLPInterval")) {
									ad = new ActionDevice(EMnVActionType.COMMAND); // 제일 처음 접속시 Session에 ActionDevice를 저장한다.
									action = new EMnVLPIntervalAction(EMnVCommandSubFrameType.COMMAND_REQUEST);
									ad.setTransactionId(transactionId);
									ad.setCommand(command);
									ad.setModemId(modemDeviceSerial);
									ad.setTrState(TR_STATE.Unknown);
									session.setAttribute(session.getRemoteAddress(), ad);

									action.execute(lFrame, session, EMnVFrameUtil.getGeneralDataFrame(FrameType.COMMAND_FRAME, gDataFrame));
								}

								/*
								 * Server H/W Reset Interval R/W
								 */
								else if (command.equals("cmdHWResetInterval")) {
									ad = new ActionDevice(EMnVActionType.COMMAND); // 제일 처음 접속시 Session에 ActionDevice를 저장한다.
									action = new EMnVHWResetIntervalAction(EMnVCommandSubFrameType.COMMAND_REQUEST);
									ad.setTransactionId(transactionId);
									ad.setCommand(command);
									ad.setModemId(modemDeviceSerial);
									ad.setTrState(TR_STATE.Unknown);
									session.setAttribute(session.getRemoteAddress(), ad);

									action.execute(lFrame, session, EMnVFrameUtil.getGeneralDataFrame(FrameType.COMMAND_FRAME, gDataFrame));
								}

								/*
								 * Modem NV Reset
								 */
								else if (command.equals("cmdNVReset")) {
									ad = new ActionDevice(EMnVActionType.COMMAND); // 제일 처음 접속시 Session에 ActionDevice를 저장한다.
									action = new EMnVNVResetAction(EMnVCommandSubFrameType.COMMAND_REQUEST);
									ad.setTransactionId(transactionId);
									ad.setCommand(command);
									ad.setModemId(modemDeviceSerial);
									ad.setTrState(TR_STATE.Unknown);
									session.setAttribute(session.getRemoteAddress(), ad);

									action.execute(lFrame, session, EMnVFrameUtil.getGeneralDataFrame(FrameType.COMMAND_FRAME, gDataFrame));
								}

								/*
								 * Mobile Number R
								 */
								else if (command.equals("cmdMNumber")) {
									ad = new ActionDevice(EMnVActionType.COMMAND); // 제일 처음 접속시 Session에 ActionDevice를 저장한다.
									action = new EMnVMNumberAction(EMnVCommandSubFrameType.COMMAND_REQUEST);
									ad.setTransactionId(transactionId);
									ad.setCommand(command);
									ad.setModemId(modemDeviceSerial);
									ad.setTrState(TR_STATE.Unknown);
									session.setAttribute(session.getRemoteAddress(), ad);

									action.execute(lFrame, session, EMnVFrameUtil.getGeneralDataFrame(FrameType.COMMAND_FRAME, gDataFrame));
								}

								/*
								 * H/W Reset
								 */
								else if (command.equals("cmdHWReset")) {
									ad = new ActionDevice(EMnVActionType.COMMAND); // 제일 처음 접속시 Session에 ActionDevice를 저장한다.
									action = new EMnVHWResetAction(EMnVCommandSubFrameType.COMMAND_REQUEST);
									ad.setTransactionId(transactionId);
									ad.setCommand(command);
									ad.setModemId(modemDeviceSerial);
									ad.setTrState(TR_STATE.Unknown);
									session.setAttribute(session.getRemoteAddress(), ad);

									action.execute(lFrame, session, EMnVFrameUtil.getGeneralDataFrame(FrameType.COMMAND_FRAME, gDataFrame));
								}
								/*
								 * Event Log
								 */
								else if (command.equals("cmdEventLog")) {
									ad = new ActionDevice(EMnVActionType.COMMAND); // 제일 처음 접속시 Session에 ActionDevice를 저장한다.
									action = new EMnVEventLogAction(EMnVCommandSubFrameType.COMMAND_REQUEST);
									ad.setTransactionId(transactionId);
									ad.setCommand(command);
									ad.setModemId(modemDeviceSerial);
									ad.setTrState(TR_STATE.Unknown);
									session.setAttribute(session.getRemoteAddress(), ad);

									action.execute(lFrame, session, EMnVFrameUtil.getGeneralDataFrame(FrameType.COMMAND_FRAME, gDataFrame));
								}
								/*
								 * Static Key Change W
								 */
								else if (command.equals("cmdKeyChange")) {
									ad = new ActionDevice(EMnVActionType.COMMAND); // 제일 처음 접속시 Session에 ActionDevice를 저장한다.
									action = new EMnVKeyChangeAction(EMnVCommandSubFrameType.COMMAND_REQUEST);
									ad.setTransactionId(transactionId);
									ad.setCommand(command);
									ad.setModemId(modemDeviceSerial);
									ad.setTrState(TR_STATE.Unknown);
									session.setAttribute(session.getRemoteAddress(), ad);

									action.execute(lFrame, session, EMnVFrameUtil.getGeneralDataFrame(FrameType.COMMAND_FRAME, gDataFrame));
								}
								/*
								 * Inverter Information R
								 */
								else if (command.equals("cmdInverterInfo")) {
									ad = new ActionDevice(EMnVActionType.COMMAND); // 제일 처음 접속시 Session에 ActionDevice를 저장한다.
									action = new EMnVInverterInfoAction(EMnVCommandSubFrameType.COMMAND_REQUEST);
									ad.setTransactionId(transactionId);
									ad.setCommand(command);
									ad.setModemId(modemDeviceSerial);
									ad.setTrState(TR_STATE.Unknown);
									session.setAttribute(session.getRemoteAddress(), ad);

									action.execute(lFrame, session, EMnVFrameUtil.getGeneralDataFrame(FrameType.COMMAND_FRAME, gDataFrame));
								}
								/*
								 * Inverter Setup W
								 */
								else if (command.equals("cmdInverterSetup")) {
									ad = new ActionDevice(EMnVActionType.COMMAND); // 제일 처음 접속시 Session에 ActionDevice를 저장한다.
									action = new EMnVInverterSetupAction(EMnVCommandSubFrameType.COMMAND_REQUEST);
									ad.setTransactionId(transactionId);
									ad.setCommand(command);
									ad.setModemId(modemDeviceSerial);
									ad.setTrState(TR_STATE.Unknown);
									session.setAttribute(session.getRemoteAddress(), ad);

									action.execute(lFrame, session, EMnVFrameUtil.getGeneralDataFrame(FrameType.COMMAND_FRAME, gDataFrame));
								}
							} else {
								logger.info("[PROTOCOL][LINK_FRAME][{}] ==> Processing Cancel. There is no Command being excuted.", modemDeviceSerial);
								session.write(EMnVActionType.EOT);
							}
						} else {
							logger.info("[PROTOCOL][LINK_FRAME][{}] ==> Processing Cancel. There is no Transaction being excuted.", modemDeviceSerial);
							session.write(EMnVActionType.EOT);
						}
					} else {
						logger.info("[PROTOCOL][LINK_FRAME][{}] ==> Processing Cancel.", modemDeviceSerial);
						session.write(EMnVActionType.EOT);
					}

					break;
				case ACK_NAK_FRAME:
					/*
					 * 현재는 OTA시 EMnVOTASubFrameType.UPGRADE_DATA 절차 진행시에만 사용하고 있음.
					 */
					EMnVAckNackFramePayLoad ackFrame = (EMnVAckNackFramePayLoad) gDataFrame.getGeneralFramePayLoad();
					ackFrame.decode();

					if (ackFrame.getType() == EMnVAckNackType.ACK) {
						action = new EMnVOTAAction(EMnVOTASubFrameType.UPGRADE_DATA);
						logger.info("[PROTOCOL][ACK_NAK_FRAME:UPGRADE_DATA][{}] FRAME_TYPE={}, REMOTE_ADDRESS={}", new Object[] { modemDeviceSerial, ackFrame.getType().name(), session.getRemoteAddress() });

						action.execute(ackFrame, session, EMnVFrameUtil.getGeneralDataFrame(FrameType.OTA_FRAME, gDataFrame));
					} else {
						logger.info("[PROTOCOL][ACK_NAK_FRAME:UPGRADE_DATA][{}] FRAME_TYPE={}, REMOTE_ADDRESS={}  Processing Cancel.", new Object[] { modemDeviceSerial, ackFrame.getType().name(), session.getRemoteAddress() });

						session.write(EMnVActionType.EOT);
					}
					break;
				case COMMAND_FRAME:
					EMnVCommandFramePayLoad commandFrame = (EMnVCommandFramePayLoad) gDataFrame.getGeneralFramePayLoad();
					commandFrame.decode();
					commandFrame.setSubFrameType(EMnVCommandSubFrameType.COMMAND_RESPONSE);

					logger.info("[PROTOCOL][COMMAND_FRAME][{}] ==> RWS_TYPE={}, ERROR_TYPE={}, COMMAND_TYPE={}", new Object[] { modemDeviceSerial, commandFrame.getRwsType().name(), commandFrame.getErrorBitType().name(), commandFrame.getCommandType().name() });

					if (commandFrame.isValidation(modemDeviceSerial)) {
						boolean isAvailable = true;
						ActionDevice aDevice = null;

						switch (commandFrame.getCommandType()) {
						case SERVER_IP:
							action = new EMnVServerIpAction(EMnVCommandSubFrameType.COMMAND_RESPONSE);

							/*
							 * ActionDevice가 없으면 중간에 연결이 끊긴것으로 간주한다.
							 */
							aDevice = (ActionDevice) session.getAttribute(session.getRemoteAddress());
							if (aDevice == null) {
								logger.warn("[PROTOCOL][COMMAND_FRAME][SERVER_IP][{}] 중간에 연결끊겨 종료처리", modemDeviceSerial);
								session.write(EMnVActionType.EOT);
								isAvailable = false;
								break;
							}

							logger.info("[PROTOCOL][COMMAND_FRAME][SERVER_IP][{}]", modemDeviceSerial);

							break;
						case SERVER_PORT:
							action = new EMnVServerPortAction(EMnVCommandSubFrameType.COMMAND_RESPONSE);

							/*
							 * ActionDevice가 없으면 중간에 연결이 끊긴것으로 간주한다.
							 */
							aDevice = (ActionDevice) session.getAttribute(session.getRemoteAddress());
							if (aDevice == null) {
								logger.warn("[PROTOCOL][COMMAND_FRAME][SERVER_PORT][{}] 중간에 연결끊겨 종료처리", modemDeviceSerial);
								session.write(EMnVActionType.EOT);
								isAvailable = false;
								break;
							}

							logger.info("[PROTOCOL][COMMAND_FRAME][SERVER_PORT][{}]", modemDeviceSerial);

							break;
						case LP_INTERVAL:
							action = new EMnVLPIntervalAction(EMnVCommandSubFrameType.COMMAND_RESPONSE);

							/*
							 * ActionDevice가 없으면 중간에 연결이 끊긴것으로 간주한다.
							 */
							aDevice = (ActionDevice) session.getAttribute(session.getRemoteAddress());
							if (aDevice == null) {
								logger.warn("[PROTOCOL][COMMAND_FRAME][LP_INTERVAL][{}] 중간에 연결끊겨 종료처리", modemDeviceSerial);
								session.write(EMnVActionType.EOT);
								isAvailable = false;
								break;
							}

							logger.info("[PROTOCOL][COMMAND_FRAME][LP_INTERVAL][{}]", modemDeviceSerial);
							break;
						case HW_RESET_INTERVAL:
							action = new EMnVHWResetIntervalAction(EMnVCommandSubFrameType.COMMAND_RESPONSE);

							/*
							 * ActionDevice가 없으면 중간에 연결이 끊긴것으로 간주한다.
							 */
							aDevice = (ActionDevice) session.getAttribute(session.getRemoteAddress());
							if (aDevice == null) {
								logger.warn("[PROTOCOL][COMMAND_FRAME][HW_RESET_INTERVAL][{}] 중간에 연결끊겨 종료처리", modemDeviceSerial);
								session.write(EMnVActionType.EOT);
								isAvailable = false;
								break;
							}

							logger.info("[PROTOCOL][COMMAND_FRAME][HW_RESET_INTERVAL][{}]", modemDeviceSerial);
							break;
						case NV_RESET:
							action = new EMnVNVResetAction(EMnVCommandSubFrameType.COMMAND_RESPONSE);

							/*
							 * ActionDevice가 없으면 중간에 연결이 끊긴것으로 간주한다.
							 */
							aDevice = (ActionDevice) session.getAttribute(session.getRemoteAddress());
							if (aDevice == null) {
								logger.warn("[PROTOCOL][COMMAND_FRAME][NV_RESET][{}] 중간에 연결끊겨 종료처리", modemDeviceSerial);
								session.write(EMnVActionType.EOT);
								isAvailable = false;
								break;
							}

							logger.info("[PROTOCOL][COMMAND_FRAME][NV_RESET][{}]", modemDeviceSerial);
							break;
						case M_NUMBER:
							action = new EMnVMNumberAction(EMnVCommandSubFrameType.COMMAND_RESPONSE);

							/*
							 * ActionDevice가 없으면 중간에 연결이 끊긴것으로 간주한다.
							 */
							aDevice = (ActionDevice) session.getAttribute(session.getRemoteAddress());
							if (aDevice == null) {
								logger.warn("[PROTOCOL][COMMAND_FRAME][M_NUMBER][{}] 중간에 연결끊겨 종료처리", modemDeviceSerial);
								session.write(EMnVActionType.EOT);
								isAvailable = false;
								break;
							}

							logger.info("[PROTOCOL][COMMAND_FRAME][M_NUMBER][{}]", modemDeviceSerial);
							break;
						case HW_RESET:
							action = new EMnVHWResetAction(EMnVCommandSubFrameType.COMMAND_RESPONSE);

							/*
							 * ActionDevice가 없으면 중간에 연결이 끊긴것으로 간주한다.
							 */
							aDevice = (ActionDevice) session.getAttribute(session.getRemoteAddress());
							if (aDevice == null) {
								logger.warn("[PROTOCOL][COMMAND_FRAME][HW_RESET][{}] 중간에 연결끊겨 종료처리", modemDeviceSerial);
								session.write(EMnVActionType.EOT);
								isAvailable = false;
								break;
							}

							logger.info("[PROTOCOL][COMMAND_FRAME][HW_RESET][{}]", modemDeviceSerial);
							break;
						case EVENT_LOG:
							action = new EMnVEventLogAction(EMnVCommandSubFrameType.COMMAND_RESPONSE);

							/*
							 * ActionDevice가 없으면 중간에 연결이 끊긴것으로 간주한다.
							 */
							aDevice = (ActionDevice) session.getAttribute(session.getRemoteAddress());
							if (aDevice == null) {
								logger.warn("[PROTOCOL][COMMAND_FRAME][EVENT_LOG][{}] 중간에 연결끊겨 종료처리", modemDeviceSerial);
								session.write(EMnVActionType.EOT);
								isAvailable = false;
								break;
							}

							logger.info("[PROTOCOL][COMMAND_FRAME][EVENT_LOG][{}]", modemDeviceSerial);
							break;
						case KEY_CHANGE:
							action = new EMnVKeyChangeAction(EMnVCommandSubFrameType.COMMAND_RESPONSE);

							/*
							 * ActionDevice가 없으면 중간에 연결이 끊긴것으로 간주한다.
							 */
							aDevice = (ActionDevice) session.getAttribute(session.getRemoteAddress());
							if (aDevice == null) {
								logger.warn("[PROTOCOL][COMMAND_FRAME][KEY_CHANGE][{}] 중간에 연결끊겨 종료처리", modemDeviceSerial);
								session.write(EMnVActionType.EOT);
								isAvailable = false;
								break;
							}

							logger.info("[PROTOCOL][COMMAND_FRAME][KEY_CHANGE][{}]", modemDeviceSerial);
							break;
						case ON_DEMAND:
							action = new EMnVOnDemandAction(EMnVCommandSubFrameType.COMMAND_RESPONSE);

							/*
							 * ActionDevice가 없으면 중간에 연결이 끊긴것으로 간주한다.
							 */
							aDevice = (ActionDevice) session.getAttribute(session.getRemoteAddress());
							if (aDevice == null) {
								logger.warn("[PROTOCOL][COMMAND_FRAME][ON_DEMAND][{}] 중간에 연결끊겨 종료처리", modemDeviceSerial);
								session.write(EMnVActionType.EOT);
								isAvailable = false;
								break;
							}

							logger.info("[PROTOCOL][COMMAND_FRAME][ON_DEMAND][{}]", modemDeviceSerial);
							break;
						case ON_DEMAND_INVERTER_LOG:
							action = new EMnVInverterLogAction(EMnVCommandSubFrameType.COMMAND_RESPONSE);

							/*
							 * ActionDevice가 없으면 중간에 연결이 끊긴것으로 간주한다.
							 */
							aDevice = (ActionDevice) session.getAttribute(session.getRemoteAddress());
							if (aDevice == null) {
								logger.warn("[PROTOCOL][COMMAND_FRAME][ON_DEMAND_INVERTER_LOG][{}] 중간에 연결끊겨 종료처리", modemDeviceSerial);
								session.write(EMnVActionType.EOT);
								isAvailable = false;
								break;
							}

							logger.info("[PROTOCOL][COMMAND_FRAME][ON_DEMAND_INVERTER_LOG][{}]", modemDeviceSerial);
							break;
						case METER_TIMESYNC:
							action = new EMnVMeterTimeSyncAction(EMnVCommandSubFrameType.COMMAND_RESPONSE);

							/*
							 * ActionDevice가 없으면 중간에 연결이 끊긴것으로 간주한다.
							 */
							aDevice = (ActionDevice) session.getAttribute(session.getRemoteAddress());
							if (aDevice == null) {
								logger.warn("[PROTOCOL][COMMAND_FRAME][METER_TIMESYNC][{}] 중간에 연결끊겨 종료처리", modemDeviceSerial);
								session.write(EMnVActionType.EOT);
								isAvailable = false;
								break;
							}

							logger.info("[PROTOCOL][COMMAND_FRAME][METER_TIMESYNC][{}]", modemDeviceSerial);
							break;
						case METER_SCAN:
							action = new EMnVMeterScanAction(EMnVCommandSubFrameType.COMMAND_RESPONSE);

							/*
							 * ActionDevice가 없으면 중간에 연결이 끊긴것으로 간주한다.
							 */
							aDevice = (ActionDevice) session.getAttribute(session.getRemoteAddress());
							if (aDevice == null) {
								logger.warn("[PROTOCOL][COMMAND_FRAME][METER_SCAN][{}] 중간에 연결끊겨 종료처리", modemDeviceSerial);
								session.write(EMnVActionType.EOT);
								isAvailable = false;
								break;
							}

							logger.info("[PROTOCOL][COMMAND_FRAME][METER_SCAN][{}]", modemDeviceSerial);
							break;
						case INVERTER_INFO:
							action = new EMnVInverterInfoAction(EMnVCommandSubFrameType.COMMAND_RESPONSE);

							/*
							 * ActionDevice가 없으면 중간에 연결이 끊긴것으로 간주한다.
							 */
							aDevice = (ActionDevice) session.getAttribute(session.getRemoteAddress());
							if (aDevice == null) {
								logger.warn("[PROTOCOL][COMMAND_FRAME][INVERTER_INFO][{}] 중간에 연결끊겨 종료처리", modemDeviceSerial);
								session.write(EMnVActionType.EOT);
								isAvailable = false;
								break;
							}

							logger.info("[PROTOCOL][COMMAND_FRAME][INVERTER_INFO][{}]", modemDeviceSerial);
							break;
						case INVERTER_SETUP:
							action = new EMnVInverterSetupAction(EMnVCommandSubFrameType.COMMAND_RESPONSE);

							/*
							 * ActionDevice가 없으면 중간에 연결이 끊긴것으로 간주한다.
							 */
							aDevice = (ActionDevice) session.getAttribute(session.getRemoteAddress());
							if (aDevice == null) {
								logger.warn("[PROTOCOL][COMMAND_FRAME][INVERTER_SETUP][{}] 중간에 연결끊겨 종료처리", modemDeviceSerial);
								session.write(EMnVActionType.EOT);
								isAvailable = false;
								break;
							}

							logger.info("[PROTOCOL][COMMAND_FRAME][INVERTER_SETUP][{}]", modemDeviceSerial);
							break;
						default:
							break;
						}

						if (isAvailable) {
							action.execute(commandFrame, session, EMnVFrameUtil.getGeneralDataFrame(FrameType.COMMAND_FRAME, gDataFrame));
						}
					} else {
						logger.info("[PROTOCOL][COMMAND_FRAME][{}] ==> Processing Cancel.", modemDeviceSerial);
						session.write(EMnVActionType.EOT);
					}

					break;
				case METERING_DATA_FRAME:
					EMnVMeteringDataFramePayLoad mFrame = (EMnVMeteringDataFramePayLoad) gDataFrame.getGeneralFramePayLoad();
					mFrame.setSourceAddress(gDataFrame.getSourceAddress());
					mFrame.setModemInfoType(gDataFrame.getvFrameControl().getSRC_ADDR_TYPE()); // SRC ADDRESS 타입이 IPv6 => Sub-Giga, EUI_64 => ZigBee로 처리.
					mFrame.decode();

					logger.info("[PROTOCOL][METERING_DATA_FRAME][{}] ==> METER_TYPE={} | METERING_TYPE={} | STATUS={} | MODEM_INFO_TYPE={} | MODEM_INFO_LENGTH={} | MD_DATA_LENGTH={}", new Object[] { modemDeviceSerial, mFrame.getMeterType(), mFrame.getMeteringDataType(), mFrame.getStatus(), mFrame.getModemInfoType(), mFrame.getModemInfoLength(), mFrame.getMdDataLentgh() });
					if (mFrame.getMeterType() != null && mFrame.getMeteringDataType() != null && mFrame.getStatus() != null && mFrame.getModemInfoType() != null && 0 < mFrame.getModemInfoLength()) {
						putServiceData(ProcessorHandler.SERVICE_EMNV_METERING, mFrame);
					} else {
						logger.info("[PROTOCOL][METERING_DATA_FRAME][{}] ==> Processing Cancel.", modemDeviceSerial);
						//session.write(EMnVActionType.EOT);
					}

					mFrame = null;
					// 서버쪽에서 disconnect처리.
					session.write(EMnVActionType.EOT);
					break;
				case OTA_FRAME:
					EMnVOTAFramePayLoad oFrame = (EMnVOTAFramePayLoad) gDataFrame.getGeneralFramePayLoad();
					oFrame.decode();

					action = new EMnVOTAAction(oFrame.getFrameType());

					if (oFrame.getFrameType() != null && oFrame.isValidation(modemDeviceSerial)) {
						boolean isAvailable = true;
						ActionDevice aDevice = null;

						switch (oFrame.getFrameType()) {
						/**
						 * 1. Upgrad Start Response 수신(FW/HW Version, Packet
						 * Size) 2. Version Check 3. Send Image Size Check 4.
						 * Send Image 5. Upgrade End Request 송신
						 */
						case UPGRADE_START_RESPONSE:
							/*
							 * ActionDevice가 없으면 중간에 연결이 끊긴것으로 간주한다.
							 */
							aDevice = (ActionDevice) session.getAttribute(session.getRemoteAddress());
							if (aDevice == null) {
								logger.warn("## [OTA][UPGRADE_START_RESPONSE] 중간에 연결끊겨 종료처리 ##");
								session.write(EMnVActionType.EOT);
								isAvailable = false;
								break;
							}

							logger.info("[OTA][PROTOCOL][OTA_FRAME][{}]({}byte) ==> EMnVLinkSubFrameType={} | H/W Version={} | F/W Version={} | Available Packet Size={}", new Object[] { modemDeviceSerial, oFrame.getFrameLength(), oFrame.getFrameType(), oFrame.getHwVersion(), oFrame.getFwVersion(), oFrame.getAvailablePacketSize() });
							break;

						/**
						 * 1. Upgrad End Response(Status) 수신 2. 결과저장 3.
						 * Disconnect
						 */
						case UPGRADE_END_RESPONSE:
							/*
							 * ActionDevice가 없으면 중간에 연결이 끊긴것으로 간주한다.
							 */
							aDevice = (ActionDevice) session.getAttribute(session.getRemoteAddress());
							if (aDevice == null) {
								logger.warn("## [OTA][UPGRADE_END_RESPONSE] 중간에 연결끊겨 종료처리 ##");
								session.write(EMnVActionType.EOT);
								isAvailable = false;
								break;
							}

							logger.info("[OTA][PROTOCOL][OTA_FRAME][{}]({}byte) ==> EMnVLinkSubFrameType={} | Status={}", new Object[] { modemDeviceSerial, oFrame.getFrameLength(), oFrame.getFrameType(), oFrame.getStatus() });
							break;
						default:
							break;
						}

						if (isAvailable) {
							action.execute(oFrame, session, EMnVFrameUtil.getGeneralDataFrame(FrameType.OTA_FRAME, gDataFrame));
						}
					} else {
						logger.info("[OTA][PROTOCOL][OTA_FRAME][{}] ==> Processing Cancel.", modemDeviceSerial);
						session.write(EMnVActionType.EOT);
					}

					break;
				case ZIGBEE_INTERFACE_FRAME:

					break;
				case SUBGIGA_INTERFACE_FRAME:

					break;
				case UNKNOWN:

					break;
				default:
					break;
				}
			} else {
				logger.warn("Message is Unknown Frame!!!");
			}
		} catch (Exception ex) {
			logger.error("EMnVProtocolHandler::messageReceived failed - {}", ex);
		}

	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		super.messageSent(session, message);
		//		logger.debug("메시지 쐈다~!! - [local-{}][remote-{}][msg-{}]", new Object[]{session.getLocalAddress(), session.getRemoteAddress(), message});
	}

}
