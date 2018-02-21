/**
 * (@)# BypassSORIAFactory.java
 *
 * 2016. 4. 15.
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
package com.aimir.fep.bypass.decofactory.protocolfactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForSORIA.ActionResult;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForSORIA.DataAccessResult;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForSORIA.ImageTransferStatus;
import com.aimir.fep.bypass.decofactory.consts.HdlcConstants.HdlcObjectType;
import com.aimir.fep.bypass.decofactory.decoframe.INestedFrame;
import com.aimir.fep.bypass.decofactory.decoframe.SORIA_DLMSFrame;
import com.aimir.fep.bypass.decofactory.decorator.NestedDLMSDecoratorForSORIA;
import com.aimir.fep.bypass.decofactory.decorator.NestedHDLCDecoratorForSORIA;
import com.aimir.fep.bypass.decofactory.decorator.NestedNIBypassDecoratorForSORIA;
import com.aimir.fep.command.conf.DLMSMeta.CONTROL_STATE; // INSERT 2016/09/20 SP-117
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.DLMS_CLASS;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.DLMS_CLASS_ATTR;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.OBIS;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.RELAY_STATUS_KAIFA; // INSERT 2016/09/20 SP-117
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.UNIT;
import com.aimir.fep.protocol.nip.client.multisession.MultiSession;
import com.aimir.fep.trap.actions.SP.EV_SP_200_63_0_Action;
import com.aimir.fep.trap.actions.SP.EV_SP_200_65_0_Action;
import com.aimir.fep.trap.actions.SP.EV_SP_200_66_0_Action;
import com.aimir.fep.trap.common.EV_Action.OTA_UPGRADE_RESULT_CODE;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.Device.DeviceType;
import com.aimir.util.DateTimeUtil;

/**
 * @author simhanger
 *
 */
public class BypassSORIAFactory extends BypassFrameFactory {
	private static Logger logger = LoggerFactory.getLogger(BypassSORIAFactory.class);
	private INestedFrame frame;
	private String meterId; // OAC에서 인증키를 받기위해 반드시 필요.
	private String command;
	private Procedure step;
	private MultiSession session;
	private HashMap<String, Object> params;
	private BypassFrameResult bypassFrameResult = new BypassFrameResult();
	private int sendHdlcPacketMaxSize = 0;

	/*
	 * Meter F/W OTA용
	 */
	private String imageIdentifier;
	private int fwSize;
	private int packetSize;
	private int offset;
	private byte[] fwImgArray;
	private int totalImageBlockNumber;
	private int imageBlockNumber;
	private int remainPackateLength;
	private byte[] sendPacket = null;
	private String progressRate = "0%";

	private int verificationRetryCount;
	private boolean isTakeOverMode;
	private boolean isTakeOverCheckStep = false;

	private Timer blockTransferRetryTimer;
	private int timerCreateFlag;
	private int retryTime = 20000;
	private NeedImangeBlockTransferRetry blockTransferRetryTask;
	//private RetrySendTimerTask retrySendTimerTask;
	private final int NEED_IMAGE_BLOCK_TRANSFER_MAX_RETRY_COUNT = 5;
	private final int VERIFICATION_MAX_RETRY_COUNT = 5;
	private long procedureStartTime;

	// Get-Response-With-Datablock 으로 받은 데이터 배열. 모든 블럭을 다 받은후 처리하기 위함.
	private byte[] dataBlockArrayOfGetRes;
	private List<HashMap<String, Object>> channelData;

	private HashMap<String, Object> optionalData;

	private int sendDelayTime;

	public void pushOptionalData(String key, Object value) {
		if (optionalData == null) {
			optionalData = new HashMap<String, Object>();
		}
		optionalData.put(key, value);
	}

	/*
	 * ACTION_IMAGE_BLOCK_TRANSFER 상태 플래그
	 * TRUE = SEND 했음
	 * FALSE = RECEIVE 했음.
	 */
	private boolean needImangeBlockTransferRetry = false;
	//	private boolean RetrySendTaskFlag = false;

	/**
	 * SP-519
	 * 
	 * @param meterId
	 * @param command
	 * @param useNiBypass
	 */
	public BypassSORIAFactory(String meterId, String command, boolean useNiBypass) {
		this.meterId = meterId;
		this.command = command;

		logger.debug("BypassSORIAFactory init.. MeterId={}, Command={} NiBypass={}", meterId, command, useNiBypass);

		//		int niBypassSetting  = Integer.parseInt(FMPProperty.getProperty("soria.protocol.modem.nibypass.use" , "0"));
		if (useNiBypass) {
			this.frame = new NestedNIBypassDecoratorForSORIA(new NestedHDLCDecoratorForSORIA(new NestedDLMSDecoratorForSORIA(new SORIA_DLMSFrame())));
		} else {
			// For Null Bypass
			this.frame = new NestedHDLCDecoratorForSORIA(new NestedDLMSDecoratorForSORIA(new SORIA_DLMSFrame()));
		}

		this.frame.setMeterId(meterId);

		setDefalutRetryTimeOut();
		params = new LinkedHashMap<String, Object>();
	}

	public BypassSORIAFactory(String meterId, String command) {
		this.meterId = meterId;
		this.command = command;

		logger.debug("BypassSORIAFactory init.. MeterId={}, Command={}", meterId, command);

		// For Normal Bypass
		// this.frame = new NestedNIDecorator(new NestedHDLCDecoratorForSORIA(new NestedDLMSDecoratorForSORIA(new SORIA_DLMSFrame())));

		// For Null Bypass
		this.frame = new NestedHDLCDecoratorForSORIA(new NestedDLMSDecoratorForSORIA(new SORIA_DLMSFrame()));
		this.frame.setMeterId(meterId);

		setDefalutRetryTimeOut();
		params = new LinkedHashMap<String, Object>();
	}

	private void setDefalutRetryTimeOut() {
		if (Boolean.parseBoolean(FMPProperty.getProperty("soria.protocol.modem.rf.dtls.use", "true"))) {
			retryTime = 20000; // DTLS의 경우 Modem의 타임아웃이 30초임. 나머지 바이패스는 60초.
		} else {
			retryTime = Integer.parseInt(FMPProperty.getProperty("ota.firmware.meter.retry.timeout", "25")) * 1000;
		}
	}

	@Override
	public void setParam(HashMap<String, Object> param) {
		params = param;
	}

	@Override
	public boolean start(MultiSession session, Object type) throws Exception {
		boolean result = false;
		this.session = session;
		step = Procedure.HDLC_SNRM;

		if (command.equals("cmdSetDLMSMeterTime") || command.equals("cmdGetDLMSMeterTime") || command.equals("cmdSetRegisterValue") || command.equals("cmdGetRegisterValue") || command.equals("cmdSetRegisterUnit") || command.equals("cmdGetRegisterUnit") || command.equals("cmdGetProfileBuffer") || command.equals("cmdGetProfileObject") || command.equals("cmdSetProfilePeriod")
				|| command.equals("cmdGetProfilePeriod") || command.equals("cmdSetThresholdNormal") || command.equals("cmdGetThresholdNormal") || command.equals("cmdSetMinOverThresholdDuration") || command.equals("cmdGetMinOverThresholdDuration") || command.equals("cmdGetRelayState") || command.equals("cmdSetRelayState") || command.equals("cmdActRelayState")
				|| command.equals("cmdGetMeterFWVersion") || command.equals("cmdSORIAGetMeterKey") || command.equals("cmdGetValue") || command.equals("cmdSetValue") || command.equals("cmdGetRelayStatusAll") // INSERT 2016/08/24 SP117
				|| command.equals("cmdActSetEncryptionKey") || command.equals("cmdActTransferKey") || command.equals("cmdGetLoadProfileOnDemand") || command.equals("cmdGetLoadProfileOnDemandMbb")) {
			result = sendBypass();
		} else if (command.equals("cmdMeterOTAStart")) {
			if (0 < session.getBypassDevice().getSendDelayTime()) {
				sendDelayTime = session.getBypassDevice().getSendDelayTime();
			}

			procedureStartTime = System.currentTimeMillis();
			logger.debug("## [cmdMeterOTAStart] Start : {}", DateTimeUtil.getDateString(procedureStartTime));

			if (params != null && params.get("take_over") != null) {
				isTakeOverMode = Boolean.parseBoolean(String.valueOf(params.get("take_over")));
			}

			if (params != null && params.get("image") != null) {
				imageIdentifier = (String) params.get("image_identifier");

				/*
				 *  Image Key : 6자리
				 */
				// imageIdentifier = imageIdentifier.substring(0, 6);

				fwImgArray = (byte[]) params.get("image");
				fwSize = fwImgArray.length;
				remainPackateLength = fwSize;

				result = sendBypass();
			}
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public BypassFrameResult receiveBypass(MultiSession session, byte[] rawFrame) throws Exception {
		boolean result = false;
		bypassFrameResult.clean();
		
		if (frame.decode(rawFrame, this.step, command)) {
			try {
			    /*
			    if (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType())) == HdlcObjectType.UNKNOWN) {
		            logger.debug("HdlcObjectType frameType is  {}", HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType())));
		            logger.debug("Return nothing to receive again");
		            return null;
		        }
		        */
			    
				/**
				 * 공통
				 */
				switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
				case UA:
					if (this.step == Procedure.HDLC_SNRM) {

						// 결과 확인
						sendHdlcPacketMaxSize = Integer.parseInt(String.valueOf(frame.getResultData()));
						logger.debug("### Send HDLC Packet Max Size = {}", sendHdlcPacketMaxSize);

						this.step = Procedure.HDLC_AARQ;
						result = sendBypass();
					} else if (this.step == Procedure.HDLC_DISC) {
						logger.info("### HDLC DISC !!");
						//session.closeNow();
						logger.debug("BypassSORIAFactory [HDLC_DISC] MultiSession destroy start");
						session.destroy();
						logger.debug("BypassSORIAFactory [HDLC_DISC] MultiSession destroy end");
						result = true;
					}
					break;
				case AARE:
					if (this.step == Procedure.HDLC_AARQ) {
						// 결과 확인
						boolean param = Boolean.valueOf(String.valueOf(frame.getResultData()));
						logger.debug("## HDLC_AARQ Result => {}", param);
						
						if (param) {
							this.step = Procedure.HDLC_ASSOCIATION_LN;							
							result = sendBypass();
						}else{
							if(command.equals("cmdSORIAGetMeterKey")){
								logger.debug("### !!! SORIA Get Meter Key !!! ###");
								this.step = Procedure.GET_SORIA_METER_KEY_A;
								result = sendBypass();
							}
						}
					}
					break;
//				case KAIFA_CUSTOM:
//					if(command.equals("cmdSORIAGetMeterKey")){
//						logger.debug("### !!! SORIA Get Meter Key !!! ###");
//						this.step = Procedure.GET_SORIA_METER_KEY_A;
//						result = sendBypass();
//					}
//					break;
				default:
					break;

				}

				if (command.equals("cmdSetDLMSMeterTime")) {
					switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
					case ACTION_RES:
						if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
							// 결과 확인
							ActionResult param = (ActionResult) frame.getResultData();
							logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
							if (param == ActionResult.SUCCESS) {
								this.step = Procedure.SET_METER_TIME;
								result = sendBypass();
							}
						}
						break;
					case SET_RES:
						// 결과 확인
						DataAccessResult param = (DataAccessResult) frame.getResultData();
						logger.debug("## SET_METER_TIME => {}", param.name());

						if (param == DataAccessResult.SUCCESS) {
							bypassFrameResult.setLastProcedure(Procedure.SET_METER_TIME);
							bypassFrameResult.setResultValue("Success");
							result = true;

							this.step = Procedure.HDLC_DISC;
							result = sendBypass();
						}
						break;
					default:
						break;
					}
				} else if (command.equals("cmdGetDLMSMeterTime")) {
					switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
					case ACTION_RES:
						if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
							// 결과 확인
							ActionResult param = (ActionResult) frame.getResultData();
							logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
							if (param == ActionResult.SUCCESS) {
								this.step = Procedure.GET_METER_TIME;
								result = sendBypass();
							}
						}
						break;
					case GET_RES:
						// 결과 확인
						Object param = frame.getResultData();
						if (param instanceof DataAccessResult) {
							result = false;
							logger.debug("## [{}]GET_RES_DataAccessResult => {}", step.name(), ((DataAccessResult) param).name());
						} else {
							if (this.step == Procedure.GET_METER_TIME) {
								// 결과 확인
								HashMap<String, String> resultData = (HashMap<String, String>) param;
								if (!resultData.equals("")) {
									logger.debug("## GET_METER_TIME => {}", resultData);

									bypassFrameResult.setLastProcedure(Procedure.GET_METER_TIME);
									bypassFrameResult.setResultValue(resultData);

									result = true;
								}

								this.step = Procedure.HDLC_DISC;
								result = sendBypass();
							}
						}
						break;
					default:
						break;
					}
				} else if (command.equals("cmdSetRegisterValue")) {
					switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
					case ACTION_RES:
						if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
							// 결과 확인
							ActionResult param = (ActionResult) frame.getResultData();
							logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
							if (param == ActionResult.SUCCESS) {
								this.step = Procedure.SET_REGISTER_VALUE;
								result = sendBypass();
							}
						}
						break;
					case SET_RES:
						// 결과 확인
						DataAccessResult param = (DataAccessResult) frame.getResultData();
						logger.debug("## SET_RESIGETER_VALUE => {}", param.name());

						if (param == DataAccessResult.SUCCESS) {
							bypassFrameResult.setLastProcedure(Procedure.SET_REGISTER_VALUE);
							bypassFrameResult.setResultValue("Success");
							result = true;

							this.step = Procedure.HDLC_DISC;
							result = sendBypass();
						}
						break;
					default:
						break;
					}
				} else if (command.equals("cmdGetRegisterValue")) {
					switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
					case ACTION_RES:
						if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
							// 결과 확인
							ActionResult param = (ActionResult) frame.getResultData();
							logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
							if (param == ActionResult.SUCCESS) {
								this.step = Procedure.GET_REGISTER_VALUE;
								result = sendBypass();
							}
						}
						break;
					case GET_RES:
						// 결과 확인
						Object param = frame.getResultData();
						if (param instanceof DataAccessResult) {
							result = false;
							logger.debug("## [{}]GET_RES_DataAccessResult => {}", step.name(), ((DataAccessResult) param).name());
						} else {
							if (this.step == Procedure.GET_REGISTER_VALUE) {
								// 결과 확인
								long resultData = (Long) param;
								logger.debug("## GET_RESIGETER_VALUE => {}", resultData);

								bypassFrameResult.setLastProcedure(Procedure.GET_REGISTER_VALUE);
								bypassFrameResult.setResultValue(resultData);

								result = true;

								this.step = Procedure.HDLC_DISC;
								result = sendBypass();
							}
						}
						break;
					default:
						break;
					}
				} else if (command.equals("cmdGetRegisterUnit")) {
					switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
					case ACTION_RES:
						if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
							// 결과 확인
							ActionResult param = (ActionResult) frame.getResultData();
							logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
							if (param == ActionResult.SUCCESS) {
								this.step = Procedure.GET_REGISTER_UNIT;
								result = sendBypass();
							}
						}
						break;
					case GET_RES:
						// 결과 확인
						Object param = frame.getResultData();
						if (param instanceof DataAccessResult) {
							result = false;
							logger.debug("## [{}]GET_RES_DataAccessResult => {}", step.name(), ((DataAccessResult) param).name());
						} else {
							if (this.step == Procedure.GET_REGISTER_UNIT) {
								// 결과 확인
								HashMap<String, Object> resultData = (HashMap<String, Object>) param;

								logger.debug("## GET_REGISTER_UNIT => {}", resultData);
								bypassFrameResult.setLastProcedure(Procedure.GET_REGISTER_UNIT);
								if (resultData.size() > 0) {
									bypassFrameResult.setResultValue(resultData);
								}
								result = true;

								this.step = Procedure.HDLC_DISC;
								result = sendBypass();
							}
						}
						break;
					default:
						break;
					}
				} else if (command.equals("cmdSetRegisterUnit")) {
					switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
					case ACTION_RES:
						if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
							// 결과 확인
							ActionResult param = (ActionResult) frame.getResultData();
							logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
							if (param == ActionResult.SUCCESS) {
								this.step = Procedure.SET_REGISTER_UNIT;
								result = sendBypass();
							}
						}
						break;
					case SET_RES:
						// 결과 확인
						DataAccessResult param = (DataAccessResult) frame.getResultData();
						logger.debug("## SET_REGISTER_UNIT => {}", param.name());

						if (param == DataAccessResult.SUCCESS) {
							bypassFrameResult.setLastProcedure(Procedure.SET_REGISTER_UNIT);
							bypassFrameResult.setResultValue("Success");
							result = true;

							this.step = Procedure.HDLC_DISC;
							result = sendBypass();
						}
						break;
					default:
						break;
					}
				} else if (command.equals("cmdGetProfileBuffer")) {
					switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
					case ACTION_RES:
						if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
							// 결과 확인
							ActionResult param = (ActionResult) frame.getResultData();
							logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
							if (param == ActionResult.SUCCESS) {
								if (params != null && params.get("obisCode") != null) {
									logger.debug("## obiscode => {}" + params.get("obisCode").toString());
									if (params.get("obisCode").toString().equals(DataUtil.convertObis(OBIS.MBUSMASTER_LOAD_PROFILE.getCode()))) {
										logger.debug("## next step = GET_PROFILE_BUFFER");
										this.step = Procedure.GET_PROFILE_BUFFER;
									} else {
										logger.debug("## next step = GET_PROFILE_OBJECT");
										this.step = Procedure.GET_PROFILE_OBJECT;
									}
								} else {
									this.step = Procedure.GET_PROFILE_OBJECT;
								}
								result = sendBypass();
							}
						}
						break;
					case GET_RES:
						// 결과 확인
						Object param = frame.getResultData();
						if (param instanceof DataAccessResult) {
							result = false;
							logger.debug("## [{}]GET_RES_DataAccessResult => {}", step.name(), ((DataAccessResult) param).name());
						} else {
							if (this.step == Procedure.GET_PROFILE_OBJECT) {
								// 결과 확인
								List<HashMap<String, Object>> resultData = (ArrayList<HashMap<String, Object>>) param;
								channelData = resultData;

								logger.debug("## GET_PROFILE_OBJECT => {}", resultData);

								bypassFrameResult.setLastProcedure(Procedure.GET_PROFILE_OBJECT);
								result = true;

								this.step = Procedure.GET_PROFILE_BUFFER;

								result = sendBypass();
							} else if (this.step == Procedure.GET_PROFILE_BUFFER) {
								// 결과 확인
								Map<String, Object> map = (Map<String, Object>) param;
								Boolean isBlock = map.get("isBlock") == null ? false : (Boolean) map.get("isBlock");
								Boolean isLast = map.get("isLast") == null ? true : (Boolean) map.get("isLast");
								Integer blockNumber = map.containsKey("blockNumber") == false ? 0 : (Integer) map.get("blockNumber");
								logger.debug("## GET_PROFILE_BUFFER => {}", map);

								if (dataBlockArrayOfGetRes == null) {
									dataBlockArrayOfGetRes = new byte[] {};
								}

								dataBlockArrayOfGetRes = DataUtil.append(dataBlockArrayOfGetRes, (byte[]) map.get("rawData")); // 누적. 여러차례에 걸쳐 넘어오는 raw data를 하나로 모은다.
								logger.debug("dataBlockArrayOfGetRes=" + Hex.decode(dataBlockArrayOfGetRes));
								if (isLast) { // 마지막 블럭 처리
									params.clear();

									// 합산데이터 파싱처리.
									Object resultObj = frame.customDecode(Procedure.GET_PROFILE_BUFFER, dataBlockArrayOfGetRes);
									List<Object> obj = (List<Object>) resultObj;

									bypassFrameResult.setLastProcedure(this.step);
									bypassFrameResult.addResultValue("channelData", channelData);
									bypassFrameResult.addResultValue("rawData", dataBlockArrayOfGetRes);
									bypassFrameResult.addResultValue("listData", obj);

									this.step = Procedure.HDLC_DISC;

									result = true;
									result = sendBypass();
								} else {
									params.put("isBlock", isBlock);
									params.put("blockNumber", blockNumber);

									result = true;
									result = sendBypass();
								}

							}
						}
						break;
					default:
						break;
					}
				} else if (command.equals("cmdGetProfileObject")) {
					switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
					case ACTION_RES:
						if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
							// 결과 확인
							ActionResult param = (ActionResult) frame.getResultData();
							logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
							if (param == ActionResult.SUCCESS) {
								this.step = Procedure.GET_PROFILE_OBJECT;
								result = sendBypass();
							}
						}
						break;
					case GET_RES:
						// 결과 확인
						List<Map<String, Object>> resultList = (List<Map<String, Object>>) frame.getResultData();
						logger.debug("## GET_PROFILE_OBJECT => {}", resultList);

						bypassFrameResult.setLastProcedure(Procedure.GET_PROFILE_OBJECT);
						if (resultList.size() > 0) {
							bypassFrameResult.setResultValue(resultList);
						}
						result = true;

						this.step = Procedure.HDLC_DISC;
						result = sendBypass();
						break;
					default:
						break;
					}
				} else if (command.equals("cmdSetProfilePeriod")) {
					switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
					case ACTION_RES:
						if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
							// 결과 확인
							ActionResult param = (ActionResult) frame.getResultData();
							logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
							if (param == ActionResult.SUCCESS) {
								this.step = Procedure.SET_PROFILE_PERIOD;
								result = sendBypass();
							}
						}
						break;
					case SET_RES:
						// 결과 확인
						DataAccessResult param = (DataAccessResult) frame.getResultData();
						logger.debug("## SET_PROFILE_PERIOD => {}", param.name());

						if (param == DataAccessResult.SUCCESS) {
							bypassFrameResult.setLastProcedure(Procedure.SET_PROFILE_PERIOD);
							bypassFrameResult.setResultValue("Success");
							result = true;

							this.step = Procedure.HDLC_DISC;
							result = sendBypass();
						}
						break;
					default:
						break;
					}
				} else if (command.equals("cmdGetProfilePeriod")) {
					switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
					case ACTION_RES:
						if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
							// 결과 확인
							ActionResult param = (ActionResult) frame.getResultData();
							logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
							if (param == ActionResult.SUCCESS) {
								this.step = Procedure.GET_PROFILE_PERIOD;
								result = sendBypass();
							}
						}
						break;
					case GET_RES:
						// 결과 확인
						Object param = frame.getResultData();
						if (param instanceof DataAccessResult) {
							result = false;
							logger.debug("## [{}]GET_RES_DataAccessResult => {}", step.name(), ((DataAccessResult) param).name());
						} else {
							if (this.step == Procedure.GET_PROFILE_PERIOD) {
								// 결과 확인
								long resultData = (Long) param;
								logger.debug("## GET_PROFILE_PERIOD => {}", resultData);

								bypassFrameResult.setLastProcedure(Procedure.GET_PROFILE_PERIOD);
								bypassFrameResult.setResultValue(resultData);

								result = true;

								this.step = Procedure.HDLC_DISC;
								result = sendBypass();
							}
						}
						break;
					default:
						break;
					}
				} else if (command.equals("cmdSetThresholdNormal")) {
					switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
					case ACTION_RES:
						if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
							// 결과 확인
							ActionResult param = (ActionResult) frame.getResultData();
							logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
							if (param == ActionResult.SUCCESS) {
								this.step = Procedure.SET_THRESHOLD_NORMAL;
								result = sendBypass();
							}
						}
						break;
					case SET_RES:
						// 결과 확인
						DataAccessResult param = (DataAccessResult) frame.getResultData();
						logger.debug("## SET_THRESHOLD_NORMAL => {}", param.name());

						if (param == DataAccessResult.SUCCESS) {
							bypassFrameResult.setLastProcedure(Procedure.SET_THRESHOLD_NORMAL);
							bypassFrameResult.setResultValue("Success");
							result = true;

							this.step = Procedure.HDLC_DISC;
							result = sendBypass();
						}
						break;
					default:
						break;
					}
				} else if (command.equals("cmdGetThresholdNormal")) {
					switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
					case ACTION_RES:
						if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
							// 결과 확인
							ActionResult param = (ActionResult) frame.getResultData();
							logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
							if (param == ActionResult.SUCCESS) {
								this.step = Procedure.GET_THRESHOLD_NORMAL;
								result = sendBypass();
							}
						}
						break;
					case GET_RES:
						// 결과 확인
						Object param = frame.getResultData();
						if (param instanceof DataAccessResult) {
							result = false;
							logger.debug("## [{}]GET_RES_DataAccessResult => {}", step.name(), ((DataAccessResult) param).name());
						} else {
							if (this.step == Procedure.GET_THRESHOLD_NORMAL) {
								// 결과 확인
								long resultData = (Long) param;
								logger.debug("## GET_THRESHOLD_NORMAL => {}", resultData);

								bypassFrameResult.setLastProcedure(Procedure.GET_THRESHOLD_NORMAL);
								bypassFrameResult.setResultValue(resultData);

								result = true;

								this.step = Procedure.HDLC_DISC;
								result = sendBypass();
							}
						}
						break;
					default:
						break;
					}
				} else if (command.equals("cmdSetMinOverThresholdDuration")) {
					switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
					case ACTION_RES:
						if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
							// 결과 확인
							ActionResult param = (ActionResult) frame.getResultData();
							logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
							if (param == ActionResult.SUCCESS) {
								this.step = Procedure.SET_MINOVER_THRESHOLD_DURATION;
								result = sendBypass();
							}
						}
						break;
					case SET_RES:
						// 결과 확인
						DataAccessResult param = (DataAccessResult) frame.getResultData();
						logger.debug("## SET_MINOVER_THRESHOLD_DURATION => {}", param.name());

						if (param == DataAccessResult.SUCCESS) {
							bypassFrameResult.setLastProcedure(Procedure.SET_MINOVER_THRESHOLD_DURATION);
							bypassFrameResult.setResultValue("Success");
							result = true;

							this.step = Procedure.HDLC_DISC;
							result = sendBypass();
						}

						break;
					default:
						break;
					}
				} else if (command.equals("cmdGetMinOverThresholdDuration")) {
					switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
					case ACTION_RES:
						if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
							// 결과 확인
							ActionResult param = (ActionResult) frame.getResultData();
							logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
							if (param == ActionResult.SUCCESS) {
								this.step = Procedure.GET_MINOVER_THRESHOLD_DURATION;
								result = sendBypass();
							}
						}
						break;
					case GET_RES:
						// 결과 확인
						Object param = frame.getResultData();
						if (param instanceof DataAccessResult) {
							result = false;
							bypassFrameResult.setResultValue(((DataAccessResult) param).name());
							logger.debug("## [{}]GET_RES_DataAccessResult => {}", step.name(), ((DataAccessResult) param).name());
						} else {
							if (this.step == Procedure.GET_MINOVER_THRESHOLD_DURATION) {
								// 결과 확인
								long resultData = (Long) param;
								logger.debug("## GET_MINOVER_THRESHOLD_DURATION => {}", resultData);

								bypassFrameResult.setLastProcedure(Procedure.GET_MINOVER_THRESHOLD_DURATION);
								bypassFrameResult.setResultValue(resultData);

								result = true;

								this.step = Procedure.HDLC_DISC;
								result = sendBypass();
							}
						}
						break;
					default:
						break;
					}
				} else if (command.equals("cmdSetRelayState")) {
					switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
					case ACTION_RES:
						if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
							// 결과 확인
							ActionResult param = (ActionResult) frame.getResultData();
							logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
							if (param == ActionResult.SUCCESS) {
								this.step = Procedure.SET_DISCONNECT_CONTROL;
								result = sendBypass();
							}
						}
						break;
					case SET_RES:
						// 결과 확인
						DataAccessResult param = (DataAccessResult) frame.getResultData();
						logger.debug("## SET_RESIGETER_VALUE => {}", param.name());

						if (param == DataAccessResult.SUCCESS) {
							bypassFrameResult.setLastProcedure(Procedure.SET_DISCONNECT_CONTROL);
							bypassFrameResult.setResultValue("Success");
							result = true;

							this.step = Procedure.HDLC_DISC;
							result = sendBypass();
						}
						break;
					default:
						break;
					}
				} else if (command.equals("cmdGetRelayState")) {
					switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
					case ACTION_RES:
						if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
							// 결과 확인
							ActionResult param = (ActionResult) frame.getResultData();
							logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
							if (param == ActionResult.SUCCESS) {
								this.step = Procedure.GET_DISCONNECT_CONTROL;
								result = sendBypass();
							}
						}
						break;
					case GET_RES:
						// 결과 확인
						Object param = frame.getResultData();
						if (param instanceof DataAccessResult) {
							result = false;
							logger.debug("## [{}]GET_RES_DataAccessResult => {}", step.name(), ((DataAccessResult) param).name());
						} else {
							if (this.step == Procedure.GET_DISCONNECT_CONTROL) {
								// 결과 확인
								//=> UPDATE START 2017.02.20 SP-530
								//long resultData = (Long) param;
								Boolean resultData = (Boolean) param;
								//=> UPDATE END   2017.02.20 SP-530
								logger.debug("## GET_RESIGETER_VALUE => {}", resultData);

								bypassFrameResult.setLastProcedure(Procedure.GET_DISCONNECT_CONTROL);
								//=> UPDATE START 2017.02.20 SP-530
								//bypassFrameResult.setResultValue(resultData);
								if ( resultData ){
									bypassFrameResult.setResultValue("Connected (true)");
								}else{
									bypassFrameResult.setResultValue("Disonnected (false)");
								}
								//=> UPDATE END   2017.02.20 SP-530

								result = true;

								this.step = Procedure.HDLC_DISC;
								result = sendBypass();
							}
						}
						break;
					default:
						break;
					}
				// -> UPDATE START 2016/09/14 SP-117
				/*
				} else if (command.equals("cmdActRelayState")) {
					switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
					case ACTION_RES:
						if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
							// 결과 확인
							ActionResult param = (ActionResult) frame.getResultData();
							logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
							if (param == ActionResult.SUCCESS) {
								this.step = Procedure.ACTION_DISCONNECT_CONTROL;
								result = sendBypass();
							}
						}
						else if ( this.step  == Procedure.ACTION_DISCONNECT_CONTROL){
							logger.debug("## ACTION_DISCONNECT_CONTROL Result => {}", frame.getResultData());
					
							if ( frame.getResultData() instanceof ActionResult){// != ActionResult.SUCCESS
								bypassFrameResult.setResultValue(frame.getResultData());
								bypassFrameResult.addResultValue("status",frame.getResultData() );
							}
							else if (frame.getResultData() instanceof HashMap ){ // ActionResult.SUCCESS
								HashMap<String,Object> param = (HashMap<String,Object>) frame.getResultData();
								bypassFrameResult.setResultValue(param.get("status"));
								bypassFrameResult.addResultValue("status", param.get("status"));
								if (  param.get("value") != null ){
									bypassFrameResult.addResultValue("value",  param.get("value"));
								}
							}
	
							bypassFrameResult.setLastProcedure(Procedure.ACTION_DISCONNECT_CONTROL);
							result = true;
							this.step = Procedure.HDLC_DISC;
							result = sendBypass();
						}
						break;
					case GET_RES:
						// 결과 확인
						Object param = frame.getResultData();
						if (param instanceof DataAccessResult) {
							result = false;
							logger.debug("## [{}]GET_RES_DataAccessResult => {}", step.name(), ((DataAccessResult) param).name());
						} else {
							if (this.step == Procedure.ACTION_DISCONNECT_CONTROL) {
								// 결과 확인
								long resultData = (Long) param;
								logger.debug("## GET_RESIGETER_VALUE => {}", resultData);
					
								bypassFrameResult.setLastProcedure(Procedure.ACTION_DISCONNECT_CONTROL);
								bypassFrameResult.setResultValue(resultData);
					
								result = true;
					
								this.step = Procedure.HDLC_DISC;
								result = sendBypass();
							}
						}
						break;	
					default:
						break;
					}
				*/
				/* */ // >>>>>>>>>>
				} else if (command.equals("cmdActRelayState")) {
					switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
					case ACTION_RES:
						if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
							// 결과 확인
							ActionResult param = (ActionResult) frame.getResultData();
							logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
							if (param == ActionResult.SUCCESS) {
								this.step = Procedure.ACTION_DISCONNECT_CONTROL;
								result = sendBypass();
							}
						} else if (this.step == Procedure.ACTION_DISCONNECT_CONTROL) {
							logger.debug("## ACTION_DISCONNECT_CONTROL Result => {}", frame.getResultData());

							if (frame.getResultData() instanceof ActionResult) {// != ActionResult.SUCCESS
								pushOptionalData("ActRelayStatus", frame.getResultData());

								bypassFrameResult.setLastProcedure(Procedure.ACTION_DISCONNECT_CONTROL);
								result    = true;
								this.step = Procedure.HDLC_DISC;
							} else if (frame.getResultData() instanceof HashMap) { // ActionResult.SUCCESS
								HashMap<String, Object> param = (HashMap<String, Object>) frame.getResultData();
								pushOptionalData("ActRelayStatus", param.get("status"));

								// check RelayOn or RelayOff
								String vl = (String) params.get("value");
								if (vl.equals("true")) {
									logger.debug("## ActRelay [ON]");
									pushOptionalData("ActRelayOnOff", true);
								} else {
									logger.debug("## ActRelay [OFF]");
									pushOptionalData("ActRelayOnOff", false);
								}

								// Set Next(RelayStatus)
								params.put("attributeNo", String.valueOf(DLMS_CLASS_ATTR.REGISTER_ATTR02.getAttr()));
								params.put("dataType", null);
								params.put("value", null);

								result    = true;
								this.step = Procedure.GET_REGISTER_VALUE;
							} else {
								bypassFrameResult.setLastProcedure(Procedure.ACTION_DISCONNECT_CONTROL);
								result    = true;
								this.step = Procedure.HDLC_DISC;
							}
							result = sendBypass();
						}
						break;

					case GET_RES:
						// 결과 확인
						Object param = frame.getResultData();
						if (param instanceof DataAccessResult) {
							result = false;
							logger.debug("## [{}]GET_RES_DataAccessResult => {}", step.name(), ((DataAccessResult) param).name());
						} else {
							if (this.step == Procedure.GET_REGISTER_VALUE) {
								// Relay Status
								Boolean resultData = (Boolean) param;
								logger.debug("## GET_RESIGETER_VALUE => {}", resultData);

								bypassFrameResult.setResultValue(optionalData.get("ActRelayStatus"));
								bypassFrameResult.addResultValue("status", optionalData.get("ActRelayStatus"));
								// -> UPDATE START 2016/09/20 SP-117
								// bypassFrameResult.addResultValue( "value" , resultData );
								if (resultData == true) {
									bypassFrameResult.addResultValue("Relay Status", RELAY_STATUS_KAIFA.Connected);
								} else {
									bypassFrameResult.addResultValue("Relay Status", RELAY_STATUS_KAIFA.Disconnected);
								}
								bypassFrameResult.addResultValue("ActRelayOnOff", optionalData.get("ActRelayOnOff"));
								// <- UPDATE END   2016/09/20 SP-117
								bypassFrameResult.setLastProcedure(Procedure.GET_REGISTER_VALUE);
								result    = true;
								this.step = Procedure.HDLC_DISC;
								result    = sendBypass();
							}
						}
						break;
					default:
						break;
					}
				/* */ // <<<<<<<<<<
				// <- UPDATE END   2016/09/14 SP-117
				} else if (command.equals("cmdActSetEncryptionKey")) {
					switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
					case ACTION_RES:
						if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
							// 결과 확인
							ActionResult param = (ActionResult) frame.getResultData();
							logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
							if (param == ActionResult.SUCCESS) {
								this.step = Procedure.ACTION_SET_ENCRYPTION_KEY;
								result = sendBypass();
							}
						} else if (this.step == Procedure.ACTION_SET_ENCRYPTION_KEY) {
							logger.debug("## ACTION_DISCONNECT_CONTROL Result => {}", frame.getResultData());

							if (frame.getResultData() instanceof ActionResult) {// != ActionResult.SUCCESS
								bypassFrameResult.setResultValue(frame.getResultData());
							} else if (frame.getResultData() instanceof HashMap) { // ActionResult.SUCCESS
								HashMap<String, Object> param = (HashMap<String, Object>) frame.getResultData();
								bypassFrameResult.setResultValue(param.get("status"));
								bypassFrameResult.addResultValue("status", param.get("status"));
							}

							bypassFrameResult.setLastProcedure(Procedure.ACTION_SET_ENCRYPTION_KEY);
							result = true;
							this.step = Procedure.HDLC_DISC;
							result = sendBypass();
						}
						break;

					default:
						break;
					}
				} else if (command.equals("cmdActTransferKey")) {
					switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
					case ACTION_RES:
						if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
							// 결과 확인
							ActionResult param = (ActionResult) frame.getResultData();
							logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
							if (param == ActionResult.SUCCESS) {
								this.step = Procedure.ACTION_TRANSFER_KEY;
								result = sendBypass();
							}
						} else if (this.step == Procedure.ACTION_TRANSFER_KEY) {
							logger.debug("## ACTION_DISCONNECT_CONTROL Result => {}", frame.getResultData());

							if (frame.getResultData() instanceof ActionResult) {// != ActionResult.SUCCESS
								bypassFrameResult.setResultValue(frame.getResultData());
							} else if (frame.getResultData() instanceof HashMap) { // ActionResult.SUCCESS
								HashMap<String, Object> param = (HashMap<String, Object>) frame.getResultData();
								bypassFrameResult.setResultValue(param.get("status"));
								bypassFrameResult.addResultValue("status", param.get("status"));
							}

							bypassFrameResult.setLastProcedure(Procedure.ACTION_TRANSFER_KEY);
							result = true;
							this.step = Procedure.HDLC_DISC;
							result = sendBypass();
						}
						break;

					default:
						break;
					}
				}
				/*************************
				 * SORIA MBB, Ethernet, RF Modem / Meter FW OTA
				 */
				else if (command.equals("cmdMeterOTAStart")) {
						logger.debug("cmdMeterOTAStart Frame Type ==> {}", HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType())));
						
						switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
						case ACTION_RES:
							if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
								// 결과 확인
								ActionResult param = (ActionResult) frame.getResultData();
								logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
								if (param == ActionResult.SUCCESS) {
									this.step = Procedure.GET_IMAGE_TRANSFER_ENABLE;
									result = sendBypass();
								}
							} else if (this.step == Procedure.ACTION_IMAGE_TRANSFER_INIT) {
								// 결과 확인
								ActionResult param = (ActionResult) frame.getResultData();
								logger.debug("## ACTION_IMAGE_TRANSFER_INIT => {}", param.name());

								if (param == ActionResult.SUCCESS) {
									this.step = Procedure.GET_IMAGE_TRANSFER_STATUS;
									result = sendBypass();
								}
							} else if (this.step == Procedure.ACTION_IMAGE_BLOCK_TRANSFER) {
								// 결과 확인
								ActionResult param = (ActionResult) frame.getResultData();
								logger.debug("## ACTION_IMAGE_BLOCK_TRANSFER => {}", param.name());

								needImangeBlockTransferRetry = false;
								blockTransferRetryTask.cancel();
								int temp = blockTransferRetryTimer.purge();
								logger.debug("##퍼지 됬음.  ==>> {}", temp);

								if (param == ActionResult.SUCCESS) {
									/*
									 * 성공응답을 받은뒤 전송할 다음블럭을 세팅한다
									 */
									setNextBlockTrigger();
									
									/*
									 * 더 보낼 Block이 없을때 처리
									 */
									if (remainPackateLength <= 0) {
										int tempPacketLength = remainPackateLength - packetSize;
										imageBlockNumber--;  // 이미지블럭을 다 보냈기때문에 이미 증가한 이미지 블럭넘버를 보정해준다. Logging처리를 위함.
										logger.info("[ACTION_IMAGE_BLOCK_TRANSFER] Finished !! Image Block Count={}/{}, RemainPacket Size={}", imageBlockNumber, totalImageBlockNumber, (tempPacketLength <= 0 ? 0 : tempPacketLength));
										this.step = Procedure.GET_IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER;

										// 다 보내면 타이머 해지
										stopTransferImageTimer();
										logger.debug("## Imange transfer Finished and Stop Timer ==> needImangeBlockTransferRetry={}", needImangeBlockTransferRetry);
									}

									result = sendBypass();
								} else { // 실패시 타이머 해지
									stopTransferImageTimer();
									
									bypassFrameResult.addResultValue("Meter Message", param);
									bypassFrameResult.addResultValue("Progress Rate", progressRate);
									
									logger.debug("## Fail Result 수신시 Timer 해지~! ==> needImangeBlockTransferRetry={}", needImangeBlockTransferRetry);
								}
							} else if (this.step == Procedure.ACTION_IMAGE_VERIFY) {
								// 결과 확인
								ActionResult param = (ActionResult) frame.getResultData();
								logger.debug("## ACTION_IMAGE_VERIFY => {}", param.name());

								if (param == ActionResult.SUCCESS) {
								
									/*
									 * SORIA KAIFA Meter는 GET_IMAGE_TO_ACTIVATE_INFO 단계를 건너뛴다.
									 * 아래주석 삭제 금지!!!									 * 
									 */
									//this.step = Procedure.GET_IMAGE_TO_ACTIVATE_INFO;
									this.step = Procedure.ACTION_IMAGE_ACTIVATE;  

									result = sendBypass();
								} else if (param == ActionResult.TEMPORARY_FAIL) {
									/*
									 * Image transfer status 체크
									 * Procedure.ACTION_IMAGE_VERIFY를 유지한체 IMAGE_TRANSFER_STATUS 검증 => GET으로 받기때문에 밑에서 처리
									 */
									byte[] req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_IMAGE_TRANSFER_STATUS, null, command);
									if (req != null) {
										logger.debug("### [ACTION_IMAGE_VERIFY][GET_IMAGE_TRANSFER_STATUS] HDLC_REQUEST => {}", Hex.decode(req));

										this.session.write(req);
										result = true;
									}
									break;
								} else {
									// 나머지는 다 에러.
								}
							} else if (this.step == Procedure.ACTION_IMAGE_ACTIVATE) {
								// 결과 확인
								ActionResult param = (ActionResult) frame.getResultData();
								logger.debug("## ACTION_IMAGE_ACTIVATE => {}", param.name());

								if (param == ActionResult.SUCCESS) {
									logger.debug("### Meter F/W OTA Successful. ###");
									logger.debug("### Meter F/W OTA Successful. ###");
									logger.debug("### Meter F/W OTA Successful. ###");

									bypassFrameResult.setLastProcedure(Procedure.ACTION_IMAGE_ACTIVATE);
									bypassFrameResult.setResultValue("success");

									/*
									 * 종료처리
									 */
									//this.step = Procedure.HDLC_DISC;

									/*
									 * 미터 F/W 버전 갱신
									 */
									Thread.sleep(40000);
									this.step = Procedure.GET_FIRMWARE_VERSION;

									result = sendBypass();
								} else if (param == ActionResult.TEMPORARY_FAIL) {
									/*
									 * 2016-08-06
									 * 현재 SORIA용 KAIFA Meter의 경우 ACTION_IMAGE_ACTIVATE 요청시 TEMPORARY_FAIL로 응답을보내주고
									 * 바로 연결을 끊어버리기 때문에 SUCCESS를 받을수가 없다.
									 * 하지만 테스트해본결과 F/W는 정상적으로 업데이트가 되기 때문에 이 문제가
									 * 해결되기 전까지는 성공한것으로 처리하도록한다.
									 */
									///////////////////////////////////////////////
									bypassFrameResult.setLastProcedure(Procedure.ACTION_IMAGE_ACTIVATE);
									bypassFrameResult.setResultValue("success");
									result = true;
									bypassFrameResult.setFinished(true);
									////////////////////////////////////////////////

									/*
									 * Image transfer status 체크
									 * Procedure.ACTION_IMAGE_ACTIVATE를 유지한체 IMAGE_TRANSFER_STATUS 검증 => GET으로 받기때문에 밑에서 처리
									 */
									byte[] req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_IMAGE_TRANSFER_STATUS, null, command);
									if (req != null) {
										logger.debug("### [ACTION_IMAGE_ACTIVATE][GET_IMAGE_TRANSFER_STATUS] HDLC_REQUEST => {}", Hex.decode(req));

										this.session.write(req);
										result = true;
									}
									break;
								} else {
									// 나머지는 다 에러.
								}
							}else if (this.step == Procedure.GET_IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER) {
								/*
								 * UDP의 경우 패킷이 잘못 전달될수 있다.
								 * 잘못 전달된 패킷을 수신시 블럭 넘버 체크하도록 함.
								 * 마지막 블럭인지 확인
								 */
								logger.warn("### HES received Inadequate DLMS Packet. Try request image first not transferred block number.");
								logger.warn("### HES received Inadequate DLMS Packet. Try request image first not transferred block number.");
								logger.warn("### HES received Inadequate DLMS Packet. Try request image first not transferred block number.");
								
								this.step = Procedure.GET_IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER;
								stopTransferImageTimer();
								result = sendBypass();	
							}
							break;
						case GET_RES:
							// 결과 확인
							Object param = frame.getResultData();

							if (param instanceof DataAccessResult) {
								result = false;
								logger.debug("## [{}]GET_RES_DataAccessResult => {}", step.name(), ((DataAccessResult) param).name());
							} else {
								if (this.step == Procedure.GET_IMAGE_TRANSFER_ENABLE) {
									// 결과 확인
									boolean resultData = Boolean.parseBoolean(String.valueOf(param));
									logger.debug("## GET_IMAGE_TRANSFER_ENABLE => {}", resultData);

									if (resultData) {
										this.step = Procedure.GET_IMAGE_BLOCK_SIZE;
								} else {
										this.step = Procedure.SET_IMAGE_TRANSFER_ENABLE;
									}
									result = sendBypass();
								} else if (this.step == Procedure.GET_IMAGE_BLOCK_SIZE) {
									// 결과 확인
									long IMAGE_TRANSFER_BLOCK_SIZE = (Long) param;
									packetSize = (int) IMAGE_TRANSFER_BLOCK_SIZE;
									if ((long) packetSize != IMAGE_TRANSFER_BLOCK_SIZE) {
										logger.error("IMAGE_TRANSFER_BLOCK_SIZE Casting Error to Integer. => {}", IMAGE_TRANSFER_BLOCK_SIZE);
										result = false;
									} else {
										totalImageBlockNumber = fwSize / packetSize;
										if (0 < (fwSize % packetSize)) {
											totalImageBlockNumber++;
										}

										totalImageBlockNumber--; // Loging 처리시 보기 편하도록 블럭번호를 수정해줌.
										
										if (isTakeOverMode) {
											this.step = Procedure.GET_IMAGE_TRANSFER_STATUS;
											isTakeOverCheckStep = true;
										} else {
											this.step = Procedure.ACTION_IMAGE_TRANSFER_INIT;
										}

										logger.debug("## TAKEOVER_MODE => {}, IS_TAKEOVER_MODE_CHECK_STEP =? {}, IMAGE_TRANSFER_BLOCK_SIZE => {}", isTakeOverMode, isTakeOverCheckStep, IMAGE_TRANSFER_BLOCK_SIZE);
										result = sendBypass();
									}
								} else if (this.step == Procedure.GET_IMAGE_TRANSFER_STATUS) {
									// 결과 확인
									Integer resultData = (Integer) param;
									logger.debug("## GET_IMAGE_TRANSFER_STATUS => {}", ImageTransferStatus.getItem(resultData));

									// takeover 모드의 경우 IMAGE_TRANSFER_INITIATED 상태가 아니면 처음부터 진행하도록 한다.
								if (isTakeOverCheckStep == true && ImageTransferStatus.getItem(resultData) != ImageTransferStatus.IMAGE_TRANSFER_INITIATED) {
										logger.debug("## IS_TAKEOVER_MODE CHECK_STEP => {}", isTakeOverCheckStep);
										isTakeOverCheckStep = false;
										this.step = Procedure.ACTION_IMAGE_TRANSFER_INIT;
										result = sendBypass();
								} else {
										if (ImageTransferStatus.getItem(resultData) == ImageTransferStatus.IMAGE_TRANSFER_INITIATED) {   // 정상
											this.step = Procedure.GET_IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER;
											result = sendBypass();
										} else if (ImageTransferStatus.getItem(resultData) == ImageTransferStatus.IMAGE_TRANSFER_NOT_INITIATED) {
											this.step = Procedure.ACTION_IMAGE_TRANSFER_INIT;
											result = sendBypass();
										} else if (ImageTransferStatus.getItem(resultData) == ImageTransferStatus.IMAGE_VERIFICATION_INITIATED) {
											this.step = Procedure.ACTION_IMAGE_VERIFY;
											result = verificationCheckRetry();
										} else if (ImageTransferStatus.getItem(resultData) == ImageTransferStatus.IMAGE_VERIFICATION_SUCCESSFUL) {
										//											this.step = Procedure.GET_IMAGE_TO_ACTIVATE_INFO;

											this.step = Procedure.ACTION_IMAGE_ACTIVATE;  // SORIA KAIFA Meter는 GET_IMAGE_TO_ACTIVATE_INFO 단계를 건너뛴다.
											result = sendBypass();
										} else if (ImageTransferStatus.getItem(resultData) == ImageTransferStatus.IMAGE_ACTIVATION_INITIATED) {
											this.step = Procedure.ACTION_IMAGE_ACTIVATE;
											result = activationCheckRetry();
										} else if (ImageTransferStatus.getItem(resultData) == ImageTransferStatus.IMAGE_ACTIVATION_SUCCESSFUL) {
											this.step = Procedure.HDLC_DISC;
											result = sendBypass();
									}
								}

								} else if (this.step == Procedure.GET_IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER) {
									/*
									 * 마지막 블럭인지 확인
									 */
									// 결과 확인
									long resultData = (Long) param;
									int firstNotTransferredBlockNumber = (int) resultData;
									if ((long) firstNotTransferredBlockNumber != resultData) {
										logger.error("IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER Casting Error to Integer. => {}", resultData);
										result = false;
									} else {
										//if (totalImageBlockNumber <= firstNotTransferredBlockNumber) {
										if (totalImageBlockNumber < firstNotTransferredBlockNumber) {
											this.step = Procedure.ACTION_IMAGE_VERIFY;
											logger.debug("## GET_IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER : Last block = {}, Not transferred block = {}", imageBlockNumber, firstNotTransferredBlockNumber);

											/*
											 * OTA Download Event save.
											 */
											String openTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
											EV_SP_200_63_0_Action action2 = new EV_SP_200_63_0_Action();
											action2.makeEvent(TargetClass.EnergyMeter, frame.getMeterId(), TargetClass.EnergyMeter, openTime, "HES");
											action2.updateOTAHistory(frame.getMeterId(), DeviceType.Meter, openTime);
											
											result = sendBypass();
										} else {
											/**
											 * firstNotTransferredBlockNumber 확인시 더보내야할 블럭이 있을경우
											 * 미전송 블럭부터 전송.
											 */
											
											// Timer 생성
											blockTransferRetryTimer = new Timer(true);
											logger.debug("Block Transfer Timer Create...." + timerCreateFlag++);
											
											this.step = Procedure.ACTION_IMAGE_BLOCK_TRANSFER;

											imageBlockNumber = firstNotTransferredBlockNumber;
											offset = firstNotTransferredBlockNumber * packetSize;
											remainPackateLength = fwSize - offset;

											logger.warn("###### Image not transferred block is exist  ==> totalImageBlockNumber={}, firstNotTransferredBlockNumber={}, packetSize={}, offset={}" + ",  remainPackateLength={}", totalImageBlockNumber, firstNotTransferredBlockNumber, packetSize, offset, remainPackateLength);
											result = sendBypass();
										}
									}
								} else if (this.step == Procedure.ACTION_IMAGE_VERIFY) {
									// 결과 확인
									Integer resultData = (Integer) param;
									ImageTransferStatus status = ImageTransferStatus.getItem(resultData);
									logger.debug("## GET_IMAGE_TRANSFER_STATUS => {}", status);

									if (status == ImageTransferStatus.IMAGE_VERIFICATION_SUCCESSFUL) {
									//										this.step = Procedure.GET_IMAGE_TO_ACTIVATE_INFO;

										this.step = Procedure.ACTION_IMAGE_ACTIVATE;  // SORIA KAIFA Meter는 GET_IMAGE_TO_ACTIVATE_INFO 단계를 건너뛴다.
										result = sendBypass();
									}
									/*
									 * 초기화중... 30초간 대기후 재시도. 3회 실시.
									 */
									else if (status == ImageTransferStatus.IMAGE_VERIFICATION_INITIATED && verificationRetryCount < VERIFICATION_MAX_RETRY_COUNT) {
										result = verificationCheckRetry();
									}
									/*
									 * status == ImageTransferStatus.IMAGE_VERIFICATION_FAILED)
									 */
									else {
										// Image Verify 실패
										logger.debug("ACTION_IMAGE_VERIFY 검증 실패");
										logger.debug("ACTION_IMAGE_VERIFY 검증 실패");
										logger.debug("ACTION_IMAGE_VERIFY 검증 실패");
									}
								} else if (this.step == Procedure.ACTION_IMAGE_ACTIVATE) {
									// 결과 확인
									Integer resultData = (Integer) param;
									ImageTransferStatus status = ImageTransferStatus.getItem(resultData);
									logger.debug("## GET_IMAGE_TRANSFER_STATUS => {}", status);

									if (status == ImageTransferStatus.IMAGE_ACTIVATION_SUCCESSFUL) {
										logger.debug("### Meter F/W OTA Successful. ###");
										logger.debug("### Meter F/W OTA Successful. ###");
										logger.debug("### Meter F/W OTA Successful. ###");

										bypassFrameResult.setLastProcedure(Procedure.ACTION_IMAGE_ACTIVATE);
										bypassFrameResult.setResultValue("success");

										/*
										 * 종료처리
										 */
										//this.step = Procedure.HDLC_DISC;

										/*
										 * 미터 F/W 버전 갱신
										 */
										Thread.sleep(50000);
										this.step = Procedure.GET_FIRMWARE_VERSION;

										result = sendBypass();
									}
									/*
									 * 초기화중... 30초간 대기후 재시도. 3회 실시.
									 */
									else if (status == ImageTransferStatus.IMAGE_ACTIVATION_INITIATED && verificationRetryCount < VERIFICATION_MAX_RETRY_COUNT) {
										result = activationCheckRetry();
									}
									/*
									 * status == ImageTransferStatus.IMAGE_ACTIVATION_FAILED)
									 */
									else {
										// Image Activation 실패
										logger.debug("ACTION_IMAGE_ACTIVATE 검증 실패");
									}
								} 
								/* SORIA KAIFA Meter는 GET_IMAGE_TO_ACTIVATE_INFO 단계를 건너뛴다.
								 * 주석 삭제 하지 말것 !!!
								 * 주석 삭제 하지 말것 !!!
								else if (this.step == Procedure.GET_IMAGE_TO_ACTIVATE_INFO) {
									// 결과 확인
									HashMap<String, Object> resultData = (HashMap<String, Object>) param;

									if (resultData != null && 0 < resultData.size()) {
										long image_to_activate_size = Long.parseLong(String.valueOf(resultData.get("image_to_activate_size")));
										byte[] image_to_activate_identification = (byte[]) resultData.get("image_to_activate_identification");

										logger.debug("## GET_IMAGE_TO_ACTIVATE_INFO => image_to_activate_size - Send = {}, Receive = {}", fwSize, image_to_activate_size);
										logger.debug("## GET_IMAGE_TO_ACTIVATE_INFO => image_to_activate_identification - Send = {}, Receive = {}", imageIdentifier, DataUtil.getString(image_to_activate_identification));

										if (resultData.containsKey("image_to_activate_signature")) {
											byte[] image_to_activate_signature = (byte[]) resultData.get("image_to_activate_signature");
											logger.debug("## GET_IMAGE_TO_ACTIVATE_INFO => image_to_activate_signature - {}", Hex.decode(image_to_activate_signature));
										}

										// 검증
										if (image_to_activate_size == fwSize && imageIdentifier.equals(DataUtil.getString(image_to_activate_identification))) {
											this.step = Procedure.ACTION_IMAGE_ACTIVATE;

											result = sendBypass();
										} else {
											logger.debug("## IMAGE_TO_ACTIVATE _INFO - Validation Fail.");
										}
									}
								} 
								*/	
								else if (this.step == Procedure.GET_FIRMWARE_VERSION) {
									// 결과 확인
									String resultData = (String) param;
									if (!resultData.equals("")) {
										logger.debug("## GET_FIRMWARE_VERSION => {}", resultData);

										bypassFrameResult.setLastProcedure(Procedure.GET_FIRMWARE_VERSION);
										bypassFrameResult.setResultValue(resultData);

										result = true;
									}

									this.step = Procedure.HDLC_DISC;
									result = sendBypass();
								}

							}

							break;
						case SET_RES:

							if (this.step == Procedure.SET_IMAGE_TRANSFER_ENABLE) {
								// 결과 확인
								DataAccessResult daResult = (DataAccessResult) frame.getResultData();
								logger.debug("## SET_IMAGE_TRANSFER_ENABLE => {}", daResult.name());

								if (daResult == DataAccessResult.SUCCESS) {
									this.step = Procedure.GET_IMAGE_BLOCK_SIZE;
									result = sendBypass();
								}
							}
							
						default:
							 HdlcObjectType frameType = HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()));
							// 아래 코드 UDP환경에서 에러나서 주석처리함.
//						    logger.debug("HdlcObjectType frameType is  {}", HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType())));
//						    logger.debug("Try to request empty packet to receive again");
//						    // return null;
//						    this.session.write(new byte[]{});
//						    result = true;
							
							/*
							 * 미터에서 보낸 응답 메시지를 수신하지 못해서 동일한 블럭을 재전송한경우
							 * 보내지 못한 블럭 넘버 체크후 재전송하도록 한다.
							 */
							if(this.step == Procedure.ACTION_IMAGE_BLOCK_TRANSFER && frameType == HdlcObjectType.UNKNOWN){
								logger.debug("### Unknown frame received. Check Image first not transferred block number ###");
								logger.debug("### Unknown frame received. Check Image first not transferred block number ###");
								logger.debug("### Unknown frame received. Check Image first not transferred block number ###");
								
								this.step = Procedure.GET_IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER;
								stopTransferImageTimer();
								result = sendBypass();	
							}else if(this.step == Procedure.GET_IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER && frameType == HdlcObjectType.UNKNOWN){
								logger.debug("### SEND NEXT BLOCK ~~!! ###");
								logger.debug("### SEND NEXT BLOCK ~~!! ###");
								logger.debug("### SEND NEXT BLOCK ~~!! ###");
								
								needImangeBlockTransferRetry = false;
								blockTransferRetryTask.cancel();
								int temp = blockTransferRetryTimer.purge();
								logger.debug("## Block Transfer Retry Timer purge.  ==>> {}", temp);
								
								setNextBlockTrigger(true);

								this.step = Procedure.ACTION_IMAGE_BLOCK_TRANSFER;
								result = sendBypass();	
							}

							/*
							 * 미터에서 보낸 응답 메시지를 수신하지 못해서 동일한 블럭을 재전송한경우
							 * 다음 블럭을 전송하도록 한다.
							 */
//							if(this.step == Procedure.ACTION_IMAGE_BLOCK_TRANSFER && frameType == HdlcObjectType.UNKNOWN){
//								logger.debug("### SEND NEXT BLOCK ~~!! ###");
//								logger.debug("### SEND NEXT BLOCK ~~!! ###");
//								logger.debug("### SEND NEXT BLOCK ~~!! ###");
//								
//								needImangeBlockTransferRetry = false;
//								blockTransferRetryTask.cancel();
//								int temp = blockTransferRetryTimer.purge();
//								logger.debug("##퍼지 됬음.  ==>> {}", temp);
//								
//								setNextBlockTrigger();
//								
//								/*
//								 * 더 보낼 Block이 없을때 처리
//								 */
//								if (remainPackateLength <= 0) {
//									logger.info("[ACTION_IMAGE_BLOCK_TRANSFER] Finished !! Image Block Count={}/{}, RemainPacket Size={}", imageBlockNumber, totalImageBlockNumber, (remainPackateLength - packetSize));
//									this.step = Procedure.GET_IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER;
//
//									// 다 보내면 타이머 해지
//									stopTransferImageTimer();
//									logger.debug("## Timer 다보낸뒤 해지~! ==> needImangeBlockTransferRetry={}", needImangeBlockTransferRetry);
//								}
//
//								result = sendBypass();								
//							}
						}
				} else if (command.equals("cmdGetMeterFWVersion")) {
						switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
						case ACTION_RES:
							if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
								// 결과 확인
								ActionResult param = (ActionResult) frame.getResultData();
								logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
								if (param == ActionResult.SUCCESS) {
									this.step = Procedure.GET_FIRMWARE_VERSION;
									result = sendBypass();
								}
							}
							break;
						case GET_RES:
							// 결과 확인
							Object param = frame.getResultData();

							if (param instanceof DataAccessResult) {
								result = false;
								logger.debug("## [{}]GET_RES_DataAccessResult => {}", step.name(), ((DataAccessResult) param).name());
							} else {
								if (this.step == Procedure.GET_FIRMWARE_VERSION) {
									// 결과 확인
									String resultData = (String) param;
									if (!resultData.equals("")) {
										logger.debug("## GET_FIRMWARE_VERSION => {}", resultData);

										bypassFrameResult.setLastProcedure(Procedure.GET_FIRMWARE_VERSION);
										bypassFrameResult.setResultValue(resultData);

										result = true;
									}

									this.step = Procedure.HDLC_DISC;
									result = sendBypass();
								}
							}
							break;
						default:
							break;
						}
				}else if (command.equals("cmdSORIAGetMeterKey")) {
					switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
					case KAIFA_CUSTOM:
						if (this.step == Procedure.GET_SORIA_METER_KEY_A) {
							// 결과 확인
							Object param = frame.getResultData();
							logger.debug("## GET_SORIA_METER_KEY_A => {}", param);

							if (param != null) {
								bypassFrameResult.setLastProcedure(Procedure.GET_SORIA_METER_KEY_A);
								bypassFrameResult.addResultValue(Procedure.GET_SORIA_METER_KEY_A.name(), String.valueOf(param));

								result = true;
							}
							this.step = Procedure.GET_SORIA_METER_KEY_B;
							result = sendBypass();
						}else if(this.step == Procedure.GET_SORIA_METER_KEY_B) {
							// 결과 확인
							Object param = frame.getResultData();
							logger.debug("## GET_SORIA_METER_KEY_B => {}", param);

							if (param != null) {
								bypassFrameResult.setLastProcedure(Procedure.GET_SORIA_METER_KEY_B);
								bypassFrameResult.addResultValue(Procedure.GET_SORIA_METER_KEY_B.name(), String.valueOf(param));

								result = true;
							}
							this.step = Procedure.GET_SORIA_METER_KEY_C;
							result = sendBypass();
						}else if(this.step == Procedure.GET_SORIA_METER_KEY_C) {
							// 결과 확인
							Object param = frame.getResultData();
							logger.debug("## GET_SORIA_METER_KEY_C => {}", param);

							if (param != null) {
								bypassFrameResult.setLastProcedure(Procedure.GET_SORIA_METER_KEY_C);
								bypassFrameResult.addResultValue(Procedure.GET_SORIA_METER_KEY_C.name(), String.valueOf(param));

								result = true;
							}
							
							this.step = Procedure.HDLC_DISC;
							result = sendBypass();
						}
						break;
					default:
						break;
					}
			} else if (command.equals("cmdSetValue")) {
						switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
						case ACTION_RES:
							if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
								// 결과 확인
								ActionResult param = (ActionResult) frame.getResultData();
								logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
								if (param == ActionResult.SUCCESS) {
									this.step = Procedure.SET_VALUE;
									result = sendBypass();
								}
							}
							break;
						case SET_RES:
							// 결과 확인
							DataAccessResult param = (DataAccessResult) frame.getResultData();
							logger.debug("## SET_RESIGETER_VALUE => {}", param.name());

							if (param == DataAccessResult.SUCCESS) {
								bypassFrameResult.setLastProcedure(Procedure.SET_VALUE);
								bypassFrameResult.setResultValue("Success");
								result = true;

								this.step = Procedure.HDLC_DISC;
								result = sendBypass();
							}
							break;
						default:
							break;
						}
					} else if (command.equals("cmdGetValue")) {
						switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
						case ACTION_RES:
							if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
								// 결과 확인
								ActionResult param = (ActionResult) frame.getResultData();
								logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
								if (param == ActionResult.SUCCESS) {
									this.step = Procedure.GET_VALUE;
									result = sendBypass();
								}
							}
							break;
						case GET_RES:
							// 결과 확인
							Object param = frame.getResultData();
							if (param instanceof DataAccessResult) {
								result = false;
								logger.debug("## [{}]GET_RES_DataAccessResult => {}", step.name(), ((DataAccessResult) param).name());
							} else {
								if (this.step == Procedure.GET_VALUE) {
									logger.debug("## GET_RESIGETER_VALUE => {}", param.toString());

									bypassFrameResult.setLastProcedure(Procedure.GET_VALUE);
									bypassFrameResult.setResultValue(param.toString());

									result = true;

									this.step = Procedure.HDLC_DISC;
									result = sendBypass();
								}
							}
							break;
						default:
							break;
						}
					// -> INSERT START 2016/08/24 SP117
					} else if (command.equals("cmdGetRelayStatusAll")) {
						switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
						case ACTION_RES:
							if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
								ActionResult param = (ActionResult) frame.getResultData();
								logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
								if (param == ActionResult.SUCCESS) {
									// this.step = Procedure.GET_DISCONNECT_CONTROL;
									this.step = Procedure.GET_REGISTER_VALUE;
									result = sendBypass();
								}
							}
							break;
						case GET_RES:
							Object param = frame.getResultData();
							if (param instanceof DataAccessResult) {
								result = false;
								logger.debug("## [{}]GET_RES_DataAccessResult => {}", step.name(), ((DataAccessResult) param).name());
							} else {
								// if (this.step == Procedure.GET_DISCONNECT_CONTROL) {
								if (this.step == Procedure.GET_REGISTER_VALUE) {
									// long resultData = (Long) param;
									// logger.debug("## GET_REGISTER_VALUE => {}", resultData);
									int attributeno = 0;

								attributeno = Integer.parseInt(params.get("attributeNo").toString());
								if (attributeno == DLMS_CLASS_ATTR.REGISTER_ATTR02.getAttr()) {
										// Relay Status
									Boolean resultData = (Boolean) param;
										logger.debug("## GET_REGISTER_VALUE => {}", resultData);

									logger.debug("## next step = GET_REGISTER_VALUE(RELAY LOAD CONTROL STATE)");
										pushOptionalData("RelayStatus", resultData);
									params.put("attributeNo", String.valueOf(DLMS_CLASS_ATTR.REGISTER_ATTR03.getAttr()));
										result = true;
										this.step = Procedure.GET_REGISTER_VALUE;

								} else if (attributeno == DLMS_CLASS_ATTR.REGISTER_ATTR03.getAttr()) {
										// Relay Load Control State
									Integer resultData = (Integer) param;
										logger.debug("## GET_REGISTER_VALUE => {}", resultData);

										pushOptionalData("LoadControlStatus", resultData);
									logger.debug("## next step = GET_REGISTER_VALUE(RELAY LOAD CONTROL MODE)");
									params.put("attributeNo", String.valueOf(DLMS_CLASS_ATTR.REGISTER_ATTR04.getAttr()));
										result = true;
										this.step = Procedure.GET_REGISTER_VALUE;

								} else if (attributeno == DLMS_CLASS_ATTR.REGISTER_ATTR04.getAttr()) {
										// Relay Load Control Mode
									Integer resultData = (Integer) param;
										logger.debug("## GET_REGISTER_VALUE => {}", resultData);

										pushOptionalData("LoadControlMode", resultData);
									logger.debug("## next step = HDLC_DISC");
										result = true;
										this.step = Procedure.HDLC_DISC;

									} else {
									logger.debug("## ERROR:next step = HDLC_DISC");
										result = false;
										this.step = Procedure.HDLC_DISC;
										break;
									}

								if (this.step == Procedure.HDLC_DISC) {
										// -> UPDATE START 2016/09/20 SP-117
										// bypassFrameResult.setLastProcedure( Procedure.GET_DISCONNECT_CONTROL );
										// bypassFrameResult.addResultValue( "RelayStatus"      , optionalData.get("RelayStatus"));
										// bypassFrameResult.addResultValue( "LoadControlStatus", optionalData.get("LoadControlStatus"));
										// bypassFrameResult.addResultValue( "LoadControlMode"  , optionalData.get("LoadControlMode"));
									Boolean relaystatus = (Boolean) optionalData.get("RelayStatus");
									Integer loadcontrolstatus = (Integer) optionalData.get("LoadControlStatus");

									bypassFrameResult.setLastProcedure(Procedure.GET_DISCONNECT_CONTROL);
										// Relay Status
									if (relaystatus == true) {
										bypassFrameResult.addResultValue("Relay Status", RELAY_STATUS_KAIFA.Connected);
									} else {
										bypassFrameResult.addResultValue("Relay Status", RELAY_STATUS_KAIFA.Disconnected);
										}
										// Load Control Status
									if (loadcontrolstatus == CONTROL_STATE.Connected.ordinal()) {
										bypassFrameResult.addResultValue("LoadControlStatus", CONTROL_STATE.Connected);
									} else if (loadcontrolstatus == CONTROL_STATE.Disconnected.ordinal()) {
										bypassFrameResult.addResultValue("LoadControlStatus", CONTROL_STATE.Disconnected);
									} else if (loadcontrolstatus == CONTROL_STATE.ReadyForReconnection.ordinal()) {
										bypassFrameResult.addResultValue("LoadControlStatus", CONTROL_STATE.ReadyForReconnection);
										}
										// Load Control Mode
									bypassFrameResult.addResultValue("LoadControlMode", optionalData.get("LoadControlMode"));
										// <- UPDATE START 2016/09/20 SP-117
									}
									result = sendBypass();
								}
							}
							break;
						default:
							break;
						}
					// <- INSERT END   2016/08/24 SP117
				} else if ((command.equals("cmdGetLoadProfileOnDemand")) || (command.equals("cmdGetLoadProfileOnDemandMbb"))) {
						switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
						case ACTION_RES:
							if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
								// 결과 확인
								ActionResult param = (ActionResult) frame.getResultData();
								logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
								if (param == ActionResult.SUCCESS) {
									if (params != null && params.get("obisCode") != null) {
									logger.debug("## obiscode => {}" + params.get("obisCode").toString());
									if (params.get("obisCode").toString().equals(DataUtil.convertObis(OBIS.MBUSMASTER_LOAD_PROFILE.getCode()))) {
										logger.debug("## next step = GET_PROFILE_BUFFER");
											this.step = Procedure.GET_PROFILE_BUFFER;
									} else {
										logger.debug("## next step = GET_PROFILE_OBJECT");
											this.step = Procedure.GET_PROFILE_OBJECT;
										}
									} else {
										this.step = Procedure.GET_PROFILE_OBJECT;
									}
									result = sendBypass();
								}
							}
							break;
						case GET_RES:
							// 결과 확인
							Object param = frame.getResultData();
							if (param instanceof DataAccessResult) {
								result = false;
								logger.debug("## [{}]GET_RES_DataAccessResult => {}", step.name(), ((DataAccessResult) param).name());
							} else {
								if (this.step == Procedure.GET_PROFILE_OBJECT) {
									// 결과 확인
									List<HashMap<String, Object>> resultData = (ArrayList<HashMap<String, Object>>) param;
									channelData = resultData;

									logger.debug("## GET_PROFILE_OBJECT => {}", resultData);

									bypassFrameResult.setLastProcedure(Procedure.GET_PROFILE_OBJECT);
									result = true;

									this.step = Procedure.GET_PROFILE_BUFFER;

									result = sendBypass();
								} else if (this.step == Procedure.GET_PROFILE_BUFFER) {
									// 결과 확인
									Map<String, Object> map = (Map<String, Object>) param;
									Boolean isBlock = map.get("isBlock") == null ? false : (Boolean) map.get("isBlock");
									Boolean isLast = map.get("isLast") == null ? true : (Boolean) map.get("isLast");
									Integer blockNumber = map.containsKey("blockNumber") == false ? 0 : (Integer) map.get("blockNumber");
									logger.debug("## GET_PROFILE_BUFFER => {}", map);
	
									if (dataBlockArrayOfGetRes == null) {
										dataBlockArrayOfGetRes = new byte[] {};
									}
	
									dataBlockArrayOfGetRes = DataUtil.append(dataBlockArrayOfGetRes, (byte[]) map.get("rawData")); // 누적. 여러차례에 걸쳐 넘어오는 raw data를 하나로 모은다.
									logger.debug("dataBlockArrayOfGetRes=" + Hex.decode(dataBlockArrayOfGetRes));
									if (isLast) { // 마지막 블럭 처리
										// 합산데이터 파싱처리.
										Object resultObj = frame.customDecode(Procedure.GET_PROFILE_BUFFER, dataBlockArrayOfGetRes);
										List<Object> obj = (List<Object>) resultObj;
	
										bypassFrameResult.setLastProcedure(this.step);
										bypassFrameResult.addResultValue("channelData", channelData);
										bypassFrameResult.addResultValue("rawData", dataBlockArrayOfGetRes);
										bypassFrameResult.addResultValue("listData", obj);
	
										if (params.get("obisCode").toString().equals(DataUtil.convertObis(OBIS.ENERGY_LOAD_PROFILE.getCode()))) {
											String obisCode = DataUtil.convertObis(OBIS.CUMULATIVE_ACTIVEENERGY_IMPORT.getCode());
											int classId = DLMS_CLASS.REGISTER.getClazz();
											int attrId = DLMS_CLASS_ATTR.REGISTER_ATTR02.getAttr();
											params.put("obisCode", obisCode);
											params.put("classId", String.valueOf(classId));
											params.put("attributeNo", String.valueOf(attrId));
											this.step = Procedure.GET_REGISTER_VALUE;
										} else { // MBUSMASTER_LOAD_PROFILE
											params.clear();
											this.step = Procedure.HDLC_DISC;
										}
										
										result = sendBypass();
									} else {
											params.put("isBlock", isBlock);
											params.put("blockNumber", blockNumber);
	
										result = sendBypass();
									}
							} else if (this.step == Procedure.GET_REGISTER_VALUE) {
									long resultData = (Long) param;
									logger.debug("## GET_RESIGETER_VALUE => {}", resultData);

								logger.debug("## obiscode => {}" + params.get("obisCode").toString());
									bypassFrameResult.setLastProcedure(this.step);
								if (params.get("obisCode").toString().equals(DataUtil.convertObis(OBIS.CUMULATIVE_ACTIVEENERGY_IMPORT.getCode()))) {
									logger.debug("## next step = GET_REGISTER_VALUE(CUMULATIVE_ACTIVEENERGY_EXPORT)");
										pushOptionalData(OBIS.CUMULATIVE_ACTIVEENERGY_IMPORT.name(), resultData);
										String obisCode = DataUtil.convertObis(OBIS.CUMULATIVE_ACTIVEENERGY_EXPORT.getCode());
									params.put("obisCode", obisCode);
										this.step = Procedure.GET_REGISTER_VALUE;
								} else if (params.get("obisCode").toString().equals(DataUtil.convertObis(OBIS.CUMULATIVE_ACTIVEENERGY_EXPORT.getCode()))) {
									logger.debug("## next step = GET_REGISTER_VALUE(CUMULATIVE_REACTIVEENERGY_IMPORT)");
										pushOptionalData(OBIS.CUMULATIVE_ACTIVEENERGY_EXPORT.name(), resultData);
										String obisCode = DataUtil.convertObis(OBIS.CUMULATIVE_REACTIVEENERGY_IMPORT.getCode());
									params.put("obisCode", obisCode);
										this.step = Procedure.GET_REGISTER_VALUE;
								} else if (params.get("obisCode").toString().equals(DataUtil.convertObis(OBIS.CUMULATIVE_REACTIVEENERGY_IMPORT.getCode()))) {
									logger.debug("## next step = GET_REGISTER_VALUE(CUMULATIVE_REACTIVEENERGY_EXPORT)");
										pushOptionalData(OBIS.CUMULATIVE_REACTIVEENERGY_IMPORT.name(), resultData);
										String obisCode = DataUtil.convertObis(OBIS.CUMULATIVE_REACTIVEENERGY_EXPORT.getCode());
									params.put("obisCode", obisCode);
										this.step = Procedure.GET_REGISTER_VALUE;
								} else if (params.get("obisCode").toString().equals(DataUtil.convertObis(OBIS.CUMULATIVE_REACTIVEENERGY_EXPORT.getCode()))) {
									//										logger.debug("## next step = HDLC_DISC" );
									//										params.clear();
										pushOptionalData(OBIS.CUMULATIVE_REACTIVEENERGY_EXPORT.name(), resultData);
									//										
									//										this.step = Procedure.HDLC_DISC;
									logger.debug("## next step = GET_REGISTER_UNIT(CUMULATIVE_ACTIVEENERGY_IMPORT)");
										 String obisCode = DataUtil.convertObis(OBIS.CUMULATIVE_ACTIVEENERGY_IMPORT.getCode());
										 int classId = DLMS_CLASS.REGISTER.getClazz();
										 int attrId = DLMS_CLASS_ATTR.REGISTER_ATTR03.getAttr(); // scalar unit
										 params.put("obisCode", obisCode);
									params.put("classId", String.valueOf(classId));
										 params.put("attributeNo", String.valueOf(attrId));
										 this.step = Procedure.GET_REGISTER_UNIT;
								} else {
									logger.debug("## next step = HDLC_DISC");
										params.clear();
										this.step = Procedure.HDLC_DISC;
									}
								//									bypassFrameResult.setLastProcedure(this.step);
								//									bypassFrameResult.addResultValue("channelData", channelData);
								//									bypassFrameResult.addResultValue("rawData", dataBlockArrayOfGetRes);
								//									bypassFrameResult.addResultValue("ActiveEnergyExport", optionalData.get("ActiveEnergyExport"));
								//									bypassFrameResult.addResultValue("ActiveEnergyImport", optionalData.get("ActiveEnergyImport"));
								//									bypassFrameResult.addResultValue("ReactiveEnergyExport",optionalData.get("ReactiveEnergyExport"));
								//									bypassFrameResult.addResultValue("ReactiveEnergyImport",optionalData.get("ReactiveEnergyImport"));
								//									bypassFrameResult.setLastProcedure(Procedure.GET_REGISTER_VALUE);
									result = sendBypass();

							} else if (this.step == Procedure.GET_REGISTER_UNIT) {
									// map  "scaler", int; "unit" int
								HashMap<String, Object> resultData = (HashMap<String, Object>) param;
									logger.debug("## GET_REGISTER_UNIT => {}", resultData);
								if (resultData != null && resultData.get("unit") != null) {
										String unitString = "";
										UNIT unit = UNIT.getItem((int) resultData.get("unit"));
									if (unit != null)
											unitString = unit.getName();
									logger.debug("## GET_REGISTER_UNIT => scaler = {}, unit =  {}", unitString, (int) resultData.get("unit"));
								} else {
										logger.debug("## GET_REGISTER_UNIT => {}", resultData);
									}
								logger.debug("## obiscode => {}" + params.get("obisCode").toString());
									bypassFrameResult.setLastProcedure(Procedure.GET_REGISTER_UNIT);
								if (params.get("obisCode").toString().equals(DataUtil.convertObis(OBIS.CUMULATIVE_ACTIVEENERGY_IMPORT.getCode()))) {
									logger.debug("## next step = GET_REGISTER_UNIT(CUMULATIVE_ACTIVEENERGY_EXPORT)");
										pushOptionalData(OBIS.CUMULATIVE_ACTIVEENERGY_IMPORT.name() + "_UNIT", resultData);
										String obisCode = DataUtil.convertObis(OBIS.CUMULATIVE_ACTIVEENERGY_EXPORT.getCode());
									params.put("obisCode", obisCode);
										this.step = Procedure.GET_REGISTER_UNIT;
								} else if (params.get("obisCode").toString().equals(DataUtil.convertObis(OBIS.CUMULATIVE_ACTIVEENERGY_EXPORT.getCode()))) {
									logger.debug("## next step = GET_REGISTER_UNIT(CUMULATIVE_REACTIVEENERGY_IMPORT)");
										pushOptionalData(OBIS.CUMULATIVE_ACTIVEENERGY_EXPORT.name() + "_UNIT", resultData);
										String obisCode = DataUtil.convertObis(OBIS.CUMULATIVE_REACTIVEENERGY_IMPORT.getCode());
									params.put("obisCode", obisCode);
										this.step = Procedure.GET_REGISTER_UNIT;
								} else if (params.get("obisCode").toString().equals(DataUtil.convertObis(OBIS.CUMULATIVE_REACTIVEENERGY_IMPORT.getCode()))) {
									logger.debug("## next step = GET_REGISTER_UNIT(CUMULATIVE_REACTIVEENERGY_EXPORT)");
										pushOptionalData(OBIS.CUMULATIVE_REACTIVEENERGY_IMPORT.name() + "_UNIT", resultData);
										String obisCode = DataUtil.convertObis(OBIS.CUMULATIVE_REACTIVEENERGY_EXPORT.getCode());
									params.put("obisCode", obisCode);
										this.step = Procedure.GET_REGISTER_UNIT;
								} else if (params.get("obisCode").toString().equals(DataUtil.convertObis(OBIS.CUMULATIVE_REACTIVEENERGY_EXPORT.getCode()))) {
									logger.debug("## next step = HDLC_DISC");
										pushOptionalData(OBIS.CUMULATIVE_REACTIVEENERGY_EXPORT.name() + "_UNIT", resultData);
									params.clear();
										this.step = Procedure.HDLC_DISC;

									OBIS cumulatives[] = new OBIS[4];
										cumulatives[0] = OBIS.CUMULATIVE_ACTIVEENERGY_IMPORT;
										cumulatives[1] = OBIS.CUMULATIVE_ACTIVEENERGY_EXPORT;
										cumulatives[2] = OBIS.CUMULATIVE_REACTIVEENERGY_IMPORT;
										cumulatives[3] = OBIS.CUMULATIVE_REACTIVEENERGY_EXPORT;
										bypassFrameResult.addResultValue("channelData", channelData);
										bypassFrameResult.addResultValue("rawData", dataBlockArrayOfGetRes);
									for (int i = 0; i < cumulatives.length; i++) {
										bypassFrameResult.addResultValue(cumulatives[i].name(), optionalData.get(cumulatives[i].name()));
										bypassFrameResult.addResultValue(cumulatives[i].name() + "_UNIT", optionalData.get(cumulatives[i].name() + "_UNIT"));

										}
								} else {
									logger.debug("## next step = HDLC_DISC");
										params.clear();
										this.step = Procedure.HDLC_DISC;
								}
									result = sendBypass();
								}

							}
							break;
						default:
							break;
						}
					}
			} catch (Exception e) {
				logger.error("BYPASS_SORIA RECEIVE ERROR - {}", e);
				result = false;
			}
		}

		bypassFrameResult.setStep(this.step);
		bypassFrameResult.setResultState(result);
		return bypassFrameResult;
	}

	private boolean sendBypass() {
		boolean result = false;
		byte[] req = null;
		HashMap<String, Object> initParam = null;

		try {
			// DelayTime설정시 전송전에 딜레이시간을 준다.
			/*logger.debug("Set delay time [{}] before send DLMS packet." , sendDelayTime);
			if(0 < sendDelayTime){			
				Thread.sleep(sendDelayTime);  
			}			
			*/
			logger.debug("STEP [{}] before send DLMS packet.", this.step);
			switch (this.step) {
			case HDLC_SNRM:
				//req = frame.encode(HdlcObjectType.SNRM, null, null);
				req = frame.encode(HdlcObjectType.SNRM, null, params, command); // SP-519
				if (req != null) {
					logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

					this.session.write(req);
					result = true;
				}
				break;
			case HDLC_AARQ:
				req = frame.encode(HdlcObjectType.AARQ, null, null, command);

				if (req != null) {
					logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

					this.session.write(req);
					result = true;
				}
				break;
			case HDLC_ASSOCIATION_LN:
				req = frame.encode(HdlcObjectType.ACTION_REQ, Procedure.HDLC_ASSOCIATION_LN, null, command);

				if (req != null) {
					logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

					this.session.write(req);
					result = true;
				}
				break;
			case HDLC_DISC:
				req = frame.encode(HdlcObjectType.DISC, null, null, command);
				bypassFrameResult.setFinished(true);

				if (req != null) {
					logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

					this.session.write(req);
					result = true;
				}
				break;
			case SET_METER_TIME:
				req = frame.encode(HdlcObjectType.SET_REQ, Procedure.SET_METER_TIME, params, command);

				if (req != null) {
					logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

					this.session.write(req);
					result = true;
				}
				break;
			case GET_METER_TIME:
				req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_METER_TIME, params, command);

				if (req != null) {
					logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

					this.session.write(req);
					result = true;
				}
				break;
			case SET_REGISTER_VALUE:
				req = frame.encode(HdlcObjectType.SET_REQ, Procedure.SET_REGISTER_VALUE, params, command);

				if (req != null) {
					logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

					this.session.write(req);
					result = true;
				}
				break;
			case GET_REGISTER_VALUE:
				req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_REGISTER_VALUE, params, command);

				if (req != null) {
					logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

					this.session.write(req);
					result = true;
				}
				break;
			case SET_REGISTER_UNIT:
				req = frame.encode(HdlcObjectType.SET_REQ, Procedure.SET_REGISTER_UNIT, params, command);

				if (req != null) {
					logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

					this.session.write(req);
					result = true;
				}
				break;
			case GET_REGISTER_UNIT:
				req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_REGISTER_UNIT, params, command);

				if (req != null) {
					logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

					this.session.write(req);
					result = true;
				}
				break;
			case SET_PROFILE_PERIOD:
				req = frame.encode(HdlcObjectType.SET_REQ, Procedure.SET_PROFILE_PERIOD, params, command);

				if (req != null) {
					logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

					this.session.write(req);
					result = true;
				}
				break;
			case GET_PROFILE_PERIOD:
				req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_PROFILE_PERIOD, params, command);

				if (req != null) {
					logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

					this.session.write(req);
					result = true;
				}
				break;
			case GET_PROFILE_OBJECT:
				req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_PROFILE_OBJECT, params, command);

				if (req != null) {
					logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

					this.session.write(req);
					result = true;
				}
				break;
			case GET_PROFILE_BUFFER:
				req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_PROFILE_BUFFER, params, command);

				if (req != null) {
					logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));
					this.session.write(req);
					result = true;
				}
				break;
			case SET_THRESHOLD_NORMAL:
				req = frame.encode(HdlcObjectType.SET_REQ, Procedure.SET_THRESHOLD_NORMAL, params, command);

				if (req != null) {
					logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

					this.session.write(req);
					result = true;
				}
				break;
			case GET_THRESHOLD_NORMAL:
				req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_THRESHOLD_NORMAL, params, command);

				if (req != null) {
					logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

					this.session.write(req);
					result = true;
				}
				break;
			case SET_MINOVER_THRESHOLD_DURATION:
				req = frame.encode(HdlcObjectType.SET_REQ, Procedure.SET_MINOVER_THRESHOLD_DURATION, params, command);

				if (req != null) {
					logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

					this.session.write(req);
					result = true;
				}
				break;
			case GET_MINOVER_THRESHOLD_DURATION:
				req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_MINOVER_THRESHOLD_DURATION, params, command);

				if (req != null) {
					logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

					this.session.write(req);
					result = true;
				}
				break;
			case SET_DISCONNECT_CONTROL:
				req = frame.encode(HdlcObjectType.SET_REQ, Procedure.SET_DISCONNECT_CONTROL, params, command);

				if (req != null) {
					logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

					this.session.write(req);
					result = true;
				}
				break;
			case GET_DISCONNECT_CONTROL:
				req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_DISCONNECT_CONTROL, params, command);

				if (req != null) {
					logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

					this.session.write(req);
					result = true;
				}
				break;
			case ACTION_DISCONNECT_CONTROL:
				req = frame.encode(HdlcObjectType.ACTION_REQ, Procedure.ACTION_DISCONNECT_CONTROL, params, command);

				if (req != null) {
					logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

					this.session.write(req);
					result = true;
				}
				/*******************************************************
				 * Meter F/W 용
				 */
			case GET_IMAGE_TRANSFER_ENABLE:
				req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_IMAGE_TRANSFER_ENABLE, null, command);

				if (req != null) {
					logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

					this.session.write(req);
					result = true;
				}
				break;
			case SET_IMAGE_TRANSFER_ENABLE:
				req = frame.encode(HdlcObjectType.SET_REQ, Procedure.SET_IMAGE_TRANSFER_ENABLE, null, command);

				if (req != null) {
					logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

					this.session.write(req);
					result = true;
				}
				break;
			case GET_IMAGE_BLOCK_SIZE:
				req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_IMAGE_BLOCK_SIZE, null, command);

				if (req != null) {
					logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

					this.session.write(req);
					result = true;
				}
				break;
			case ACTION_IMAGE_TRANSFER_INIT:
				initParam = new HashMap<String, Object>();
				initParam.put("image_identifier", imageIdentifier);
				initParam.put("image_size", String.valueOf(fwSize));

				req = frame.encode(HdlcObjectType.ACTION_REQ, Procedure.ACTION_IMAGE_TRANSFER_INIT, initParam, command);

				if (req != null) {
					logger.debug("### [{}] HDLC_REQUEST => Image={}, size={} / {}", this.step.name(), initParam.get("image_identifier"), initParam.get("image_size"), Hex.decode(req));

					this.session.write(req);
					result = true;
				}
				break;
			case GET_IMAGE_TRANSFER_STATUS:
				req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_IMAGE_TRANSFER_STATUS, null, command);

				if (req != null) {
					logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

					this.session.write(req);
					result = true;
				}
				break;
			case ACTION_IMAGE_BLOCK_TRANSFER:
				if (0 < remainPackateLength) {
					// 전송실패시 어디까지 보냈는지 저장하기위함.
					bypassFrameResult.setResultValue(imageBlockNumber + "/" + totalImageBlockNumber);

					if (packetSize < remainPackateLength) {
						sendPacket = new byte[packetSize];
					} else {
						sendPacket = new byte[remainPackateLength];
					}
					System.arraycopy(fwImgArray, offset, sendPacket, 0, sendPacket.length);

					initParam = new HashMap<String, Object>();
					initParam.put("image_block_number", imageBlockNumber);
					initParam.put("image_block_value", sendPacket);

					req = frame.encode(HdlcObjectType.ACTION_REQ, Procedure.ACTION_IMAGE_BLOCK_TRANSFER, initParam, command);

					if (req != null) {
						logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

						this.session.write(req);
						result = true;
					} else {
						needImangeBlockTransferRetry = false;
						throw new Exception("ACTION_IMAGE_BLOCK_TRANSFER Encoding Error");
					}

					double tempa = fwImgArray.length;
					double tempb = offset + sendPacket.length;
					progressRate = String.format("%.2f", tempb / tempa * 100) + "%";
					int tempPacketLength = remainPackateLength - packetSize;
					logger.info("[ACTION_IMAGE_BLOCK_TRANSFER][{}] Sended Image Block Number={}/{}, Packet Size={}, RemainPacket Size={}, Progress Rate={}", frame.getMeterId(), imageBlockNumber, totalImageBlockNumber, sendPacket.length, (tempPacketLength <= 0 ? 0 : tempPacketLength), progressRate);

					//					remainPackateLength -= packetSize;
					//					offset += sendPacket.length;
					//					imageBlockNumber++;222
					/*
					 *  재전송해야할 필요가 있는지 체크하는 타이머
					 *  retryTime초뒤에 실행, retryTime초 간격으로 NEED_IMAGE_BLOCK_TRANSFER_MAX_RETRY_COUNT 만큼 재실행
					 */
					needImangeBlockTransferRetry = true;
					blockTransferRetryTask = new NeedImangeBlockTransferRetry(this.session, req, NEED_IMAGE_BLOCK_TRANSFER_MAX_RETRY_COUNT);
					blockTransferRetryTimer.scheduleAtFixedRate(blockTransferRetryTask, retryTime, retryTime);
				} else {
					stopTransferImageTimer();
					//logger.debug("## Timer 중지!! ==> needImangeBlockTransferRetry ={}", needImangeBlockTransferRetry);
				}

				break;
			case GET_IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER:
				req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER, null, command);

				if (req != null) {
					logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

					this.session.write(req);
					result = true;
				}
				break;
			case ACTION_IMAGE_VERIFY:
				initParam = new HashMap<String, Object>();
				initParam.put("image_verify_data", (byte) 0x00);

				req = frame.encode(HdlcObjectType.ACTION_REQ, Procedure.ACTION_IMAGE_VERIFY, initParam, command);

				if (req != null) {
					logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

					this.session.write(req);
					result = true;
				}
				break;
			/* SORIA KAIFA Meter는 GET_IMAGE_TO_ACTIVATE_INFO 단계를 건너뛴다.
			 * 주석 삭제하지 말것~!!
			 * 주석 삭제하지 말것~!!
			 * 주석 삭제하지 말것~!!
			case GET_IMAGE_TO_ACTIVATE_INFO:
				req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_IMAGE_TO_ACTIVATE_INFO, null);
			
				if (req != null) {
					logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));
			
					this.session.write(req);
					result = true;
				}
				break;
			*/
			case ACTION_IMAGE_ACTIVATE:
				initParam = new HashMap<String, Object>();
				initParam.put("image_activate_data", (byte) 0x00);

				req = frame.encode(HdlcObjectType.ACTION_REQ, Procedure.ACTION_IMAGE_ACTIVATE, initParam, command);

				if (req != null) {
					logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

					this.session.write(req);
					result = true;
				}
				break;
			case GET_FIRMWARE_VERSION:
				req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_FIRMWARE_VERSION, null, command);

				if (req != null) {
					logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

					this.session.write(req);
					result = true;
				}
				break;
			case GET_SORIA_METER_KEY_A:
				req = frame.encode(HdlcObjectType.KAIFA_CUSTOM, Procedure.GET_SORIA_METER_KEY_A, null, command);

				if (req != null) {
					logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

					this.session.write(req);
					result = true;
				}
				break;
			case GET_SORIA_METER_KEY_B:
				req = frame.encode(HdlcObjectType.KAIFA_CUSTOM, Procedure.GET_SORIA_METER_KEY_B, null, command);

				if (req != null) {
					logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

					this.session.write(req);
					result = true;
				}
				break;
			case GET_SORIA_METER_KEY_C:
				req = frame.encode(HdlcObjectType.KAIFA_CUSTOM, Procedure.GET_SORIA_METER_KEY_C, null, command);

				if (req != null) {
					logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

					this.session.write(req);
					result = true;
				}
				break;
				
			/**
			 * Meter F/W 용
			 ********************************************************/
			case ACTION_SET_ENCRYPTION_KEY:
				req = frame.encode(HdlcObjectType.ACTION_REQ, Procedure.ACTION_SET_ENCRYPTION_KEY, params, command);

				if (req != null) {
					logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

					this.session.write(req);
					result = true;
				}
				break;
			case ACTION_TRANSFER_KEY:
				req = frame.encode(HdlcObjectType.ACTION_REQ, Procedure.ACTION_SET_ENCRYPTION_KEY, params, command);

				if (req != null) {
					logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

					this.session.write(req);
					result = true;
				}
				break;
			case GET_VALUE:
				req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_VALUE, params, command);

				if (req != null) {
					logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

					this.session.write(req);
					result = true;
				}
				break;
			case SET_VALUE:
				req = frame.encode(HdlcObjectType.SET_REQ, Procedure.SET_VALUE, params, command);

				if (req != null) {
					logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

					this.session.write(req);
					result = true;
				}
				break;
			default:
				break;
			}
		} catch (Exception e) {
			logger.error("BYPASS_SP SEND ERROR - {}", e);
			result = false;
		}

		return result;
	}

	/**
	 * Image Transfer next block setting.
	 */
	private void setNextBlockTrigger() {
		setNextBlockTrigger(false);
	}
	private void setNextBlockTrigger(boolean force) {
		/*
		 * Meter RS count를 확인해서 R이 증가됬을경우에만 다음블럭을 전송, 동일한 count일 경우에는 동일 블럭 재전송.
		 */
		int[] meterRScount = frame.getMeterRSCount();
		logger.debug("Meter RS Count : before[{}] -> current[{}]", meterRScount[0] + ", " + meterRScount[1], (2 < meterRScount.length ? meterRScount[2] : "-") + ", " + (3 < meterRScount.length ? meterRScount[3] : "-"));
		
		// for RS rotation.
		if(meterRScount[0] == 7){
			meterRScount[0] = -1;
		}
		
		if (meterRScount.length == 2 || meterRScount[0] < meterRScount[2] || force) {
			remainPackateLength -= packetSize;
			offset += sendPacket.length;
			imageBlockNumber++;
		}else{
			logger.warn("### The same RS number is received and the same block is sending...");
			logger.warn("### The same RS number is received and the same block is sending...");
			logger.warn("### The same RS number is received and the same block is sending...");
		}
	}

	private boolean verificationCheckRetry() {
		boolean result = false;
		/*
		 * Image transfer status 체크
		 */
		try {
			Thread.sleep(30000);

			byte[] req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_IMAGE_TRANSFER_STATUS, null, command);
			if (req != null) {
				verificationRetryCount++; // 재시도 횟수 카운팅
				logger.debug("### [ACTION_IMAGE_VERIFY][GET_IMAGE_TRANSFER_STATUS][{}] Retry Count={} HDLC_REQUEST => {}", frame.getMeterId(), verificationRetryCount, Hex.decode(req));

				session.write(req);
				result = true;
			}
		} catch (Exception e) {
			logger.equals("verificationCheckRetry Error - " + e);
		}
		return result;
	}

	private boolean activationCheckRetry() {
		boolean result = false;

		/*
		 * Image transfer status 체크
		 */
		try {
			Thread.sleep(30000);
			byte[] req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_IMAGE_TRANSFER_STATUS, null, command);
			if (req != null) {
				verificationRetryCount++; // 재시도 횟수 카운팅
				logger.debug("### [ACTION_IMAGE_ACTIVATE][GET_IMAGE_TRANSFER_STATUS][{}] Retry Count={} HDLC_REQUEST => {}", frame.getMeterId(), verificationRetryCount, Hex.decode(req));

				session.write(req);
				result = true;
			}
		} catch (Exception e) {
			logger.equals("activationCheckRetry Error - " + e);
		}

		return result;
	}

	/**
	 * Timer, Timer Task cancel
	 */
	private void stopTransferImageTimer() {
		needImangeBlockTransferRetry = false;
		blockTransferRetryTask.cancel();
		blockTransferRetryTimer.cancel();

		logger.debug("## Timer Task Stop.");
	}

	/**
	 * Image Block Transfer를 반복 실행하는 TimerTask
	 * 
	 * @author simhanger
	 *
	 */
	protected class NeedImangeBlockTransferRetry extends TimerTask {
		//private IoSession session;
		private MultiSession session;
		private byte[] req;
		private int maxRetryCount;
		private int retryCount;

		//public NeedImangeBlockTransferRetry(IoSession session, byte[] req, int maxRetryCount) {
		public NeedImangeBlockTransferRetry(MultiSession session, byte[] req, int maxRetryCount) {
			this.session = session;
			this.req = req;
			this.maxRetryCount = maxRetryCount;
		}

		@Override
		public void run() {
			if (needImangeBlockTransferRetry == true && this.retryCount < this.maxRetryCount) {
				logger.info("[ACTION_IMAGE_BLOCK_TRANSFER][{}] Retry={} Sended Image Block Number={}/{}", frame.getMeterId(), retryCount + 1, imageBlockNumber, totalImageBlockNumber);
				this.session.write(this.req);
				this.retryCount++;
			} else {
				this.cancel();
				logger.debug("NeedImangeBlockTransferRetry cancle!! Progress Rate = " + progressRate);

				/*
				 * OTA 종료후 Event 저장
				 */
				String issueDate = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
				EV_SP_200_65_0_Action action = new EV_SP_200_65_0_Action();
				action.makeEvent(TargetClass.EnergyMeter, session.getBypassDevice().getMeterId(), TargetClass.EnergyMeter, issueDate, "0", OTA_UPGRADE_RESULT_CODE.OTAERR_BYPASS_TRN_FAIL, "[ACTION_IMAGE_BLOCK_TRANSFER] Progress Rate=" + progressRate + ", Retry count=" + retryCount, "HES");
				action.updateOTAHistory(session.getBypassDevice().getMeterId(), DeviceType.Meter, issueDate, OTA_UPGRADE_RESULT_CODE.OTAERR_BYPASS_TRN_FAIL, "[ACTION_IMAGE_BLOCK_TRANSFER] Progress Rate=" + progressRate + ", Retry count=" + retryCount);

				EV_SP_200_66_0_Action action2 = new EV_SP_200_66_0_Action();
				action2.makeEvent(TargetClass.EnergyMeter, session.getBypassDevice().getMeterId(), TargetClass.EnergyMeter, issueDate, OTA_UPGRADE_RESULT_CODE.OTAERR_BYPASS_TRN_FAIL, null, "HES");
				action2.updateOTAHistory(session.getBypassDevice().getMeterId(), DeviceType.Meter, issueDate, OTA_UPGRADE_RESULT_CODE.OTAERR_BYPASS_TRN_FAIL);

				stop(session);
			}
		}
	}

	@Override
	//public void stop(IoSession session) {
	public void stop(MultiSession session) {
		// Timer Stop.
		needImangeBlockTransferRetry = false;

		byte[] frameArray = frame.encode(HdlcObjectType.DISC, null, null, command);

		if (frameArray != null) {
			this.step = Procedure.HDLC_DISC;

			logger.debug("### Stop Bypass ~ !! => {}", Hex.decode(frameArray));
			logger.debug("### Stop Bypass ~ !! => {}", Hex.decode(frameArray));
			logger.debug("### Stop Bypass ~ !! => {}", Hex.decode(frameArray));

			this.session.write(frameArray);
		}
	}
	
	// INSERT START SP-628
	public String getStepName() {
		return this.step.name();
	}
	
	public boolean isHandshakeFinish() throws Exception {
		boolean result = false;

		if ((this.step == Procedure.HDLC_SNRM) ||
			(this.step == Procedure.HDLC_AARQ) ||
			(this.step == Procedure.HDLC_ASSOCIATION_LN)
		){
			result = false;
		}
		else {
			result = true;
		}
		
		return result;
	}
	public void retryHandshake() throws Exception {
		boolean result = false;
		
		try {
			logger.debug("Retry DLMS handshake. STEP [{}] ", this.step);
			
			if ((this.step == Procedure.HDLC_SNRM) ||
					(this.step == Procedure.HDLC_AARQ) ||
					(this.step == Procedure.HDLC_ASSOCIATION_LN)
				){

				result = sendBypass();
				
			}
		}
		catch (Exception e) {
			logger.error("BYPASS_SORIA RECEIVE ERROR - {}", e);
			result = false;
		}
	}
	// INSERT END SP-628

    @Override
    public BypassFrameResult receiveBypass(IoSession session, byte[] rawFrame) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean start(IoSession session, Object type) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void stop(IoSession session) {
        // TODO Auto-generated method stub
        
    }

}
