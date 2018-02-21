/**
 * (@)# CommandNIProxy.java
 *
 * 2016. 5. 30.
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
package com.aimir.fep.protocol.nip;

import java.util.HashMap;

import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.constants.CommonConstants.McuType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.fep.protocol.fmp.common.Target;
import com.aimir.fep.protocol.nip.client.NiClient;
import com.aimir.fep.protocol.nip.common.GeneralDataFrame;
import com.aimir.fep.protocol.nip.frame.GeneralFrame;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.FrameControl_Ack;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.FrameControl_Pending;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.FrameOption_AddressType;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.FrameOption_NetworkStatus;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.FrameOption_Type;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.NIAttributeId;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.NetworkType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.util.CmdUtil;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.Modem;

/**
 * @author simhanger
 *
 *         When implement abstract NI command, check the bellow
 *         {@link com.aimir.fep.protocol.nip.frame.NIFrameConstants}
 *         {@link GeneralFrame} {@link NiClient}
 */
public class CommandNIProxy implements CommandNIProxyMBean, MBeanRegistration {
	private static Logger logger = LoggerFactory.getLogger(CommandNIProxy.class);

	@SuppressWarnings("unused")
	private ObjectName objectName = null;
	private static final int MAX_COUNT = Integer.parseInt(FMPProperty.getProperty("client.max.count"));
	private static int TARGET_MODEM_PORT = 0;
	private static Object lock = new Object();
	private static int connCount = 0;
	private static int waitCount = 0;
	private NiClient client = null;

	/**
	 * 호출할때마다 1씩 증가시킨값
	 * 
	 * @return
	 */
	private int sequenceCounter = 0;

	public void initSequenceCounter() {
		sequenceCounter = 0;
	}

	public byte[] getSequenceCounter() {
		if (255 < sequenceCounter) {
			sequenceCounter = 0;
		}
		return new byte[] { DataUtil.getByteToInt(sequenceCounter++) };
	}

	public static synchronized NiClient getClient(Target target, GeneralFrame generalFrame) throws Exception {
		/**
		 * Network Type 설정
		 */
		if (target.getReceiverType().equals("MMIU") || target.getTargetType() == McuType.MMIU) {
			if (target.getProtocol() == Protocol.SMS) { // MBB Modem / TCP
				/**
				 * 실제 접속할 Server의 PORT 설정
				 */
				TARGET_MODEM_PORT = Integer.parseInt(FMPProperty.getProperty("soria.protocol.modem.port.tls.server"));
				if (TARGET_MODEM_PORT == 0) {
					TARGET_MODEM_PORT = 8004; // TLS Port
				}
				target.setPort(TARGET_MODEM_PORT);

				generalFrame.setNetworkType(NetworkType.MBB);
			} else if (target.getProtocol() == Protocol.IP) { // Ethernet Modem / TCP
				/**
				 * 실제 접속할 Server의 PORT 설정
				 */
				TARGET_MODEM_PORT = Integer.parseInt(FMPProperty.getProperty("soria.protocol.modem.port.tls.server"));
				if (TARGET_MODEM_PORT == 0) {
					TARGET_MODEM_PORT = 8004; // TLS Port
				}
				target.setPort(TARGET_MODEM_PORT);
				generalFrame.setNetworkType(NetworkType.Ethernet);

			}
		} else if (target.getReceiverType().equals("SubGiga")) {
			/**
			 * 실제 접속할 Server의 PORT 설정
			 */
			TARGET_MODEM_PORT = Integer.parseInt(FMPProperty.getProperty("soria.protocol.modem.port.dtls.server"));
			if (TARGET_MODEM_PORT == 0) {
				TARGET_MODEM_PORT = 8006; // DTLS Port
			}
			target.setPort(TARGET_MODEM_PORT);

			if (target.getProtocol() == Protocol.IP) { // RF Modem / UDP
				generalFrame.setNetworkType(NetworkType.Sub1Ghz_SORIA);
			} else {
				generalFrame.setNetworkType(NetworkType.Ethernet);
			}
		}

		logger.debug("## NetowrkType = {}, Protocol = {}, Target info = [{}]", generalFrame.getNetworkType().name(), target.getProtocol().name(), target.toString());

		// lock(0);

		return new NiClient();
	}

	public static GeneralFrame setGeneralFrameOption(FrameOption_Type fOptionType, NIAttributeId attrId) {
		GeneralFrame generalFrame = new GeneralFrame();
		generalFrame.setFrameOption_Type(fOptionType);

		if (attrId != null) {
			generalFrame.setNIAttributeId(attrId.getCode());
			generalFrame.setNIAttributeId(attrId);
		}

		switch (fOptionType) {
		case Ack:
			break;
		case AlarmEvent:
			break;
		case Bypass:
			break;
		case MeterEvent:
			break;
		case Metering:
			break;
		case Command:
			switch (attrId) {
			case ResetModem:
				/*
				 *  추가 구현할것.
				 */
				break;
			case UploadMeteringData:
				/*
				 *  추가 구현할것.
				 */
				break;
			case FactorySetting:
				/*
				 *  추가 구현할것.
				 */
				break;
			case RecoveryPulseLoadProfileData:
				/*
				 *  추가 구현할것.
				 */
				break;
			case ReAuthenticate:
				/*
				 *  추가 구현할것.
				 */
				break;

			case WatchdogTest:
				/*
				 *  추가 구현할것.
				 */
				generalFrame.setCommandType(CommandType.Set);
				generalFrame.setCommandFlow(CommandFlow.Request);
				generalFrame.setFcPending(FrameControl_Pending.LastFrame);
				generalFrame.setFcAck(FrameControl_Ack.None);
				generalFrame.setFoNetworkStatus(FrameOption_NetworkStatus.None);
				generalFrame.setFoAddrType(FrameOption_AddressType.None);
				generalFrame.setFoType(FrameOption_Type.Command);
				generalFrame.setSeqNumber(new byte[] { DataUtil.getByteToInt(0) });
				break;

			case ModemInformation:
				/*
				 *  추가 구현할것.
				 */
				break;
			case NB_PLCInformation:
				/*
				 *  추가 구현할것.
				 */
				break;
			case ModemStatus:
				/*
				 *  추가 구현할것.
				 */
				break;
			case MeterInformation:
				/*
				 *  추가 구현할것.
				 */
				break;
			case ModemEventLog:
				/*
				 *  HanSejin - GET modem event log
				 */
				generalFrame.setCommandType(CommandType.Get);
				generalFrame.setCommandFlow(CommandFlow.Request);
				generalFrame.setFcPending(FrameControl_Pending.LastFrame);
				generalFrame.setFcAck(FrameControl_Ack.None);
				generalFrame.setFoNetworkStatus(FrameOption_NetworkStatus.None);
				generalFrame.setFoAddrType(FrameOption_AddressType.None);
				generalFrame.setFoType(FrameOption_Type.Command);
				generalFrame.setSeqNumber(new byte[] { DataUtil.getByteToInt(0) });
				//generalFrame.setSeqNumber(new byte[]{DataUtil.getByteToInt(0)});
				break;
			case CloneOnOff:
				generalFrame.setCommandType(CommandType.Set);
				generalFrame.setCommandFlow(CommandFlow.Request);
				generalFrame.setFcPending(FrameControl_Pending.LastFrame);
				generalFrame.setFcAck(FrameControl_Ack.None);
				generalFrame.setFoNetworkStatus(FrameOption_NetworkStatus.None);
				generalFrame.setFoAddrType(FrameOption_AddressType.None);
				generalFrame.setFoType(FrameOption_Type.Command);
				generalFrame.setSeqNumber(new byte[] { DataUtil.getByteToInt(0) });
				
				break;
			case ModemTime:
				/*
				 *  추가 구현할것.
				 */
				break;
			case ModemResetTime:
				generalFrame.setCommandType(CommandType.Set);//SET
				generalFrame.setCommandFlow(CommandFlow.Request);
				generalFrame.setFcPending(FrameControl_Pending.LastFrame);
				generalFrame.setFcAck(FrameControl_Ack.None);
				generalFrame.setFoNetworkStatus(FrameOption_NetworkStatus.None);
				generalFrame.setFoAddrType(FrameOption_AddressType.None);
				generalFrame.setFoType(FrameOption_Type.Command);
				generalFrame.setSeqNumber(new byte[] { DataUtil.getByteToInt(0) });
				break;
			case ModemMode:
				generalFrame.setCommandType(CommandType.Set);//SET
				generalFrame.setCommandFlow(CommandFlow.Request);
				generalFrame.setFcPending(FrameControl_Pending.LastFrame);
				generalFrame.setFcAck(FrameControl_Ack.None);
				generalFrame.setFoNetworkStatus(FrameOption_NetworkStatus.None);
				generalFrame.setFoAddrType(FrameOption_AddressType.None);
				generalFrame.setFoType(FrameOption_Type.Command);
				generalFrame.setSeqNumber(new byte[] { DataUtil.getByteToInt(0) });
				break;
			case ModemMode_GET:
				generalFrame.setCommandType(CommandType.Get);
				generalFrame.setCommandFlow(CommandFlow.Request);
				generalFrame.setFcPending(FrameControl_Pending.LastFrame);
				generalFrame.setFcAck(FrameControl_Ack.None);
				generalFrame.setFoNetworkStatus(FrameOption_NetworkStatus.None);
				generalFrame.setFoAddrType(FrameOption_AddressType.None);
				generalFrame.setFoType(FrameOption_Type.Command);
				generalFrame.setSeqNumber(new byte[] { DataUtil.getByteToInt(0) });
				break;
			case MeterTimesync:
				/*
				 *  추가 구현할것.
				 */
				break;
			case MeteringInterval:
				/*
				 *  HanSejin - SET metering interval
				 */
				generalFrame.setCommandType(CommandType.Set);//SET
				generalFrame.setCommandFlow(CommandFlow.Request);
				generalFrame.setFcPending(FrameControl_Pending.LastFrame);
				generalFrame.setFcAck(FrameControl_Ack.None);
				generalFrame.setFoNetworkStatus(FrameOption_NetworkStatus.None);
				generalFrame.setFoAddrType(FrameOption_AddressType.None);
				generalFrame.setFoType(FrameOption_Type.Command);
				generalFrame.setSeqNumber(new byte[] { DataUtil.getByteToInt(0) });
				break;
			case MeteringInterval_GET:
				/*
				 *  HanSejin - GET metering interval
				 */
				generalFrame.setCommandType(CommandType.Get);
				generalFrame.setCommandFlow(CommandFlow.Request);
				generalFrame.setFcPending(FrameControl_Pending.LastFrame);
				generalFrame.setFcAck(FrameControl_Ack.None);
				generalFrame.setFoNetworkStatus(FrameOption_NetworkStatus.None);
				generalFrame.setFoAddrType(FrameOption_AddressType.None);
				generalFrame.setFoType(FrameOption_Type.Command);
				generalFrame.setSeqNumber(new byte[] { DataUtil.getByteToInt(0) });
				break;
			case ModemTXPower:
				/*
				 *  추가 구현할것.
				 */
				break;
			case KeepAliveMessageInterval:
				/*
				 *  추가 구현할것.
				 */
				break;
			case ActiveKeepTime:
				/*
				 *  추가 구현할것.
				 */
				break;
			case NetworkScanInterval:
				/*
				 *  추가 구현할것.
				 */
				break;
			case Form_JoinNetwork:
				/*
				 *  추가 구현할것.
				 */
				break;
			case NetworkSpeed:
				/*
				 *  추가 구현할것.
				 */
				break;
			case NetworkJoinTimeout:
				/*
				 *  추가 구현할것.
				 */
				break;
			case Alarm_EventCommandON_OFF:
				generalFrame.setCommandType(CommandType.Set);
				generalFrame.setCommandFlow(CommandFlow.Request);
				generalFrame.setFcPending(FrameControl_Pending.LastFrame);
				generalFrame.setFcAck(FrameControl_Ack.None);
				generalFrame.setFoNetworkStatus(FrameOption_NetworkStatus.None);
				generalFrame.setFoAddrType(FrameOption_AddressType.None);
				generalFrame.setFoType(FrameOption_Type.Command);
				generalFrame.setSeqNumber(new byte[] { DataUtil.getByteToInt(0) });
				break;
			case Alarm_EventCommandON_OFF_GET:
				generalFrame.setCommandType(CommandType.Get);
				generalFrame.setCommandFlow(CommandFlow.Request);
				generalFrame.setFcPending(FrameControl_Pending.LastFrame);
				generalFrame.setFcAck(FrameControl_Ack.None);
				generalFrame.setFoNetworkStatus(FrameOption_NetworkStatus.None);
				generalFrame.setFoAddrType(FrameOption_AddressType.None);
				generalFrame.setFoType(FrameOption_Type.Command);
				generalFrame.setSeqNumber(new byte[] { DataUtil.getByteToInt(0) });
				break;
			case MeterBaud:
				/*
				 *  HanSejin - SET meterbaudrate
				 */
				generalFrame.setCommandType(CommandType.Set);//SET
				generalFrame.setCommandFlow(CommandFlow.Request);
				generalFrame.setFcPending(FrameControl_Pending.LastFrame);
				generalFrame.setFcAck(FrameControl_Ack.None);
				generalFrame.setFoNetworkStatus(FrameOption_NetworkStatus.None);
				generalFrame.setFoAddrType(FrameOption_AddressType.None);
				generalFrame.setFoType(FrameOption_Type.Command);
				generalFrame.setSeqNumber(new byte[] { DataUtil.getByteToInt(0) });
				break;
			case MeterBaud_GET:
				/*
				 *  HanSejin - GET meterbaudrate
				 */
				generalFrame.setCommandType(CommandType.Get);
				generalFrame.setCommandFlow(CommandFlow.Request);
				generalFrame.setFcPending(FrameControl_Pending.LastFrame);
				generalFrame.setFcAck(FrameControl_Ack.None);
				generalFrame.setFoNetworkStatus(FrameOption_NetworkStatus.None);
				generalFrame.setFoAddrType(FrameOption_AddressType.None);
				generalFrame.setFoType(FrameOption_Type.Command);
				generalFrame.setSeqNumber(new byte[] { DataUtil.getByteToInt(0) });
				break;

			case RealTimeMetering:
				/*
				 *  IBK - SET
				 */
				generalFrame.setCommandType(CommandType.Set);
				generalFrame.setCommandFlow(CommandFlow.Request);
				generalFrame.setFcPending(FrameControl_Pending.LastFrame);
				generalFrame.setFcAck(FrameControl_Ack.None);
				generalFrame.setFoNetworkStatus(FrameOption_NetworkStatus.None);
				generalFrame.setFoAddrType(FrameOption_AddressType.None);
				generalFrame.setFoType(FrameOption_Type.Command);
				generalFrame.setSeqNumber(new byte[] { DataUtil.getByteToInt(0) });
				break;
			case TransmitFrequency:
				generalFrame.setCommandType(CommandType.Set);//SET
				generalFrame.setCommandFlow(CommandFlow.Request);
				generalFrame.setFcPending(FrameControl_Pending.LastFrame);
				generalFrame.setFcAck(FrameControl_Ack.None);
				generalFrame.setFoNetworkStatus(FrameOption_NetworkStatus.None);
				generalFrame.setFoAddrType(FrameOption_AddressType.None);
				generalFrame.setFoType(FrameOption_Type.Command);
				generalFrame.setSeqNumber(new byte[] { DataUtil.getByteToInt(0) });
				break;
			case TransmitFrequency_GET:
				generalFrame.setCommandType(CommandType.Get);
				generalFrame.setCommandFlow(CommandFlow.Request);
				generalFrame.setFcPending(FrameControl_Pending.LastFrame);
				generalFrame.setFcAck(FrameControl_Ack.None);
				generalFrame.setFoNetworkStatus(FrameOption_NetworkStatus.None);
				generalFrame.setFoAddrType(FrameOption_AddressType.None);
				generalFrame.setFoType(FrameOption_Type.Command);
				generalFrame.setSeqNumber(new byte[] { DataUtil.getByteToInt(0) });
				break;
			case RetryCount:
				/*
				 *  HanSejin - SET retry count
				 */
				generalFrame.setCommandType(CommandType.Set);
				generalFrame.setCommandFlow(CommandFlow.Request);
				generalFrame.setFcPending(FrameControl_Pending.LastFrame);
				generalFrame.setFcAck(FrameControl_Ack.None);
				generalFrame.setFoNetworkStatus(FrameOption_NetworkStatus.None);
				generalFrame.setFoAddrType(FrameOption_AddressType.None);
				generalFrame.setFoType(FrameOption_Type.Command);
				generalFrame.setSeqNumber(new byte[] { DataUtil.getByteToInt(0) });
				break;
			case RetryCount_GET:
				/*
				 *  HanSejin - GET retry count
				 */
				generalFrame.setCommandType(CommandType.Get);
				generalFrame.setCommandFlow(CommandFlow.Request);
				generalFrame.setFcPending(FrameControl_Pending.LastFrame);
				generalFrame.setFcAck(FrameControl_Ack.None);
				generalFrame.setFoNetworkStatus(FrameOption_NetworkStatus.None);
				generalFrame.setFoAddrType(FrameOption_AddressType.None);
				generalFrame.setFoType(FrameOption_Type.Command);
				generalFrame.setSeqNumber(new byte[] { DataUtil.getByteToInt(0) });
				break;

			case SnmpTrapOnOff:
				/*
				 *  추가 구현할것.
				 */
				generalFrame.setCommandType(CommandType.Set);
				generalFrame.setCommandFlow(CommandFlow.Request);
				generalFrame.setFcPending(FrameControl_Pending.LastFrame);
				generalFrame.setFcAck(FrameControl_Ack.None);
				generalFrame.setFoNetworkStatus(FrameOption_NetworkStatus.None);
				generalFrame.setFoAddrType(FrameOption_AddressType.None);
				generalFrame.setFoType(FrameOption_Type.Command);
				generalFrame.setSeqNumber(new byte[] { DataUtil.getByteToInt(0) });
				break;
			case SnmpTrapOnOff_GET:
				/*
				 *  추가 구현할것.
				 */
				generalFrame.setCommandType(CommandType.Get);
				generalFrame.setCommandFlow(CommandFlow.Request);
				generalFrame.setFcPending(FrameControl_Pending.LastFrame);
				generalFrame.setFcAck(FrameControl_Ack.None);
				generalFrame.setFoNetworkStatus(FrameOption_NetworkStatus.None);
				generalFrame.setFoAddrType(FrameOption_AddressType.None);
				generalFrame.setFoType(FrameOption_Type.Command);
				generalFrame.setSeqNumber(new byte[] { DataUtil.getByteToInt(0) });
				break;
			case RawROMAccess:
				/*
				 *  add by kh.yoon 20170117
				 */
				generalFrame.setCommandType(CommandType.Set);
				generalFrame.setCommandFlow(CommandFlow.Request);
				generalFrame.setFcPending(FrameControl_Pending.LastFrame);
				generalFrame.setFcAck(FrameControl_Ack.None);
				generalFrame.setFoNetworkStatus(FrameOption_NetworkStatus.None);
				generalFrame.setFoAddrType(FrameOption_AddressType.None);
				generalFrame.setFoType(FrameOption_Type.Command);
				generalFrame.setSeqNumber(new byte[] { DataUtil.getByteToInt(0) });
				break;	
			case RawROMAccess_GET:
				/*
				 *  add by kh.yoon 20170117
				 */
				generalFrame.setCommandType(CommandType.Get);
				generalFrame.setCommandFlow(CommandFlow.Request);
				generalFrame.setFcPending(FrameControl_Pending.LastFrame);
				generalFrame.setFcAck(FrameControl_Ack.None);
				generalFrame.setFoNetworkStatus(FrameOption_NetworkStatus.None);
				generalFrame.setFoAddrType(FrameOption_AddressType.None);
				generalFrame.setFoType(FrameOption_Type.Command);
				generalFrame.setSeqNumber(new byte[] { DataUtil.getByteToInt(0) });
				break;	

			case NB_PLCTMR:
				/*
				 *  추가 구현할것.
				 */
				break;
			case NB_PLCModulation:
				/*
				 *  추가 구현할것.
				 */
				break;
			case MeterLoadprofileinterval:
				/*
				 *  추가 구현할것.
				 */
				break;
			case SelfReadscript:
				/*
				 *  추가 구현할것.
				 */
				break;
			case SingleActionSchedule:
				/*
				 *  추가 구현할것.
				 */
				break;
			case RecoveryMetering:
				/*
				 *  추가 구현할것.
				 */
				break;
			case OBISListup:
				/*
				 *  추가 구현할것.
				 */
				break;
			case OBISAdd:
				/*
				 *  추가 구현할것.
				 */
				break;
			case OBISRemove:
				/*
				 *  추가 구현할것.
				 */
				break;
			case OBISListChange:
				/*
				 *  추가 구현할것.
				 */
				break;
			case KEPCOOBISNewList:
				/*
				 *  추가 구현할것.
				 */
				break;
			case KEPCOOBISListVersion:
				/*
				 *  추가 구현할것.
				 */
				break;
			case TestConfiguration:
				/*
				 *  추가 구현할것.
				 */
				break;
			case TestDataUpload:
				/*
				 *  추가 구현할것.
				 */
				break;
			case CoordinatorInformation:
				/*
				 *  추가 구현할것.
				 */
				break;
			case NetworkPermit:
				/*
				 *  추가 구현할것.
				 */
				break;
			case BootloaderJump:
				/*
				 *  추가 구현할것.
				 */
				break;
			case CoordinatorBootup:
				/*
				 *  추가 구현할것.
				 */
				break;
			case NetworkIPv6Prefix:
				/*
				 *  추가 구현할것.
				 */
				break;
			case APN:
				generalFrame.setCommandType(CommandType.Get);
				generalFrame.setCommandFlow(CommandFlow.Request);
				generalFrame.setFcPending(FrameControl_Pending.LastFrame);
				generalFrame.setFcAck(FrameControl_Ack.None);
				generalFrame.setFoNetworkStatus(FrameOption_NetworkStatus.None);
				generalFrame.setFoAddrType(FrameOption_AddressType.None);
				generalFrame.setFoType(FrameOption_Type.Command);
				generalFrame.setSeqNumber(new byte[] { DataUtil.getByteToInt(0) });
				break;
			case AcceptJoin:
				/*
				 *  추가 구현할것.
				 */
				break;
			case AcceptLeave:
				/*
				 *  추가 구현할것.
				 */
				break;
			case JoinBackoffTimer:
				/*
				 *  추가 구현할것.
				 */
				break;
			case AuthBackoffTimer:
				/*
				 *  추가 구현할것.
				 */
				break;
			case MeterSharedKey:
				/*
				 *  추가 구현할것.
				 */
				break;
			case NullBypassOpen:
				generalFrame.setCommandFlow(CommandFlow.Request);
				generalFrame.setCommandType(CommandType.Set);
				generalFrame.setFcPending(FrameControl_Pending.LastFrame);
				generalFrame.setFcAck(FrameControl_Ack.None);
				generalFrame.setFoNetworkStatus(FrameOption_NetworkStatus.None);
				generalFrame.setFoAddrType(FrameOption_AddressType.None);
				generalFrame.setFoType(FrameOption_Type.Command);
				generalFrame.setSeqNumber(new byte[] { DataUtil.getByteToInt(0) });

				break;
			case NullBypassClose:
				generalFrame.setCommandFlow(CommandFlow.Request);
				generalFrame.setCommandType(CommandType.Set);
				generalFrame.setFcPending(FrameControl_Pending.LastFrame);
				generalFrame.setFcAck(FrameControl_Ack.None);
				generalFrame.setFoNetworkStatus(FrameOption_NetworkStatus.None);
				generalFrame.setFoAddrType(FrameOption_AddressType.None);
				generalFrame.setFoType(FrameOption_Type.Command);
				generalFrame.setSeqNumber(new byte[] { DataUtil.getByteToInt(0) });
				break;
			case ROMRead:
				/*
				 *  추가 구현할것.
				 */
				// INSERT SP-179
				generalFrame.setCommandFlow(CommandFlow.Request);
				generalFrame.setCommandType(CommandType.Get);
				generalFrame.setFcPending(FrameControl_Pending.LastFrame);
				generalFrame.setFcAck(FrameControl_Ack.None);
				generalFrame.setFoNetworkStatus(FrameOption_NetworkStatus.None);
				generalFrame.setFoAddrType(FrameOption_AddressType.None);
				generalFrame.setFoType(FrameOption_Type.Command);
				generalFrame.setSeqNumber(new byte[] { DataUtil.getByteToInt(0) });
				// INSERT SP-179
				break;
			case GatheringDataAction:
				/*
				 *  추가 구현할것.
				 */
				break;
			case GatheringDataPoll:
				/*
				 *  추가 구현할것.
				 */
				break;
			case ParentNodeInfo:
				/*
				 *  추가 구현할것.
				 */
				break;
			case HopCount:
				/*
				 *  추가 구현할것.
				 */
				break;
			case HopNeighborList:
				/*
				 *  추가 구현할것.
				 */
				break;
			case ChildNodeList:
				/*
				 *  추가 구현할것.
				 */
				break;
			case NodeAuthorization:
				/*
				 *  추가 구현할것.
				 */
				break;
			case ModemIpInformation:
				generalFrame.setCommandType(CommandType.Set);
				generalFrame.setCommandFlow(CommandFlow.Request);
				generalFrame.setFcPending(FrameControl_Pending.LastFrame);
				generalFrame.setFcAck(FrameControl_Ack.None);
				generalFrame.setFoNetworkStatus(FrameOption_NetworkStatus.None);
				generalFrame.setFoAddrType(FrameOption_AddressType.None);
				generalFrame.setFoType(FrameOption_Type.Command);
				generalFrame.setSeqNumber(new byte[] { DataUtil.getByteToInt(0) });
				break;
			case ModemIpInformation_GET:
				generalFrame.setCommandType(CommandType.Get);
				generalFrame.setCommandFlow(CommandFlow.Request);
				generalFrame.setFcPending(FrameControl_Pending.LastFrame);
				generalFrame.setFcAck(FrameControl_Ack.None);
				generalFrame.setFoNetworkStatus(FrameOption_NetworkStatus.None);
				generalFrame.setFoAddrType(FrameOption_AddressType.None);
				generalFrame.setFoType(FrameOption_Type.Command);
				generalFrame.setSeqNumber(new byte[] { DataUtil.getByteToInt(0) });
				break;
			case ModemPortInformation:
				generalFrame.setCommandType(CommandType.Set);
				generalFrame.setCommandFlow(CommandFlow.Request);
				generalFrame.setFcPending(FrameControl_Pending.LastFrame);
				generalFrame.setFcAck(FrameControl_Ack.None);
				generalFrame.setFoNetworkStatus(FrameOption_NetworkStatus.None);
				generalFrame.setFoAddrType(FrameOption_AddressType.None);
				generalFrame.setFoType(FrameOption_Type.Command);
				generalFrame.setSeqNumber(new byte[] { DataUtil.getByteToInt(0) });
				break;
			case ModemPortInformation_GET:
				generalFrame.setCommandType(CommandType.Get);
				generalFrame.setCommandFlow(CommandFlow.Request);
				generalFrame.setFcPending(FrameControl_Pending.LastFrame);
				generalFrame.setFcAck(FrameControl_Ack.None);
				generalFrame.setFoNetworkStatus(FrameOption_NetworkStatus.None);
				generalFrame.setFoAddrType(FrameOption_AddressType.None);
				generalFrame.setFoType(FrameOption_Type.Command);
				generalFrame.setSeqNumber(new byte[] { DataUtil.getByteToInt(0) });
				break;
			}
			break;
		case Firmware:
			generalFrame.setFcPending(FrameControl_Pending.LastFrame);
			generalFrame.setFcAck(FrameControl_Ack.None);
			generalFrame.setFoNetworkStatus(FrameOption_NetworkStatus.None);
			generalFrame.setFoAddrType(FrameOption_AddressType.None);
			generalFrame.setSrcAddress(null);
			generalFrame.setDstAddress(null);
			break;
		default:
			break;
		}

		logger.debug("## FrameOption Type = {}, Command = {}", fOptionType.name(), attrId);

		return generalFrame;
	}

	public void closeClient(NiClient client) {
		client.dispose();
		release();
	}

	public Object execute(Modem modem, NIAttributeId command, HashMap<String, Object> param) throws Exception {
		Target target = CmdUtil.getNullBypassTarget(modem);
		if (target == null) {
			throw new Exception("Can not found target. please check Meter & Modem information.");
		}
		return execute(target, command, param, null);
	}

	public Object execute(Target target, NIAttributeId command, HashMap<String, Object> param, IoSession externalNISession) {
		try {
			GeneralFrame generalFrame = setGeneralFrameOption(FrameOption_Type.Command, command);
			;
			generalFrame.setNIAttributeId(command.getCode());

			//client = getClient(target, command, generalFrame);
			client = getClient(target, generalFrame);
			if (externalNISession != null) {
				client.setSession(externalNISession);
			}
			// setGeneralFrameOption(FrameOption_Type.Command, command);

			byte[] sendData = new GeneralDataFrame().make(generalFrame, param);
			generalFrame = client.sendCommand(target, generalFrame, sendData);

			/*
			 * 결과처리
			 */
			return (AbstractCommand) generalFrame.abstractCmd; 
		} catch (Exception e) {
			logger.error("CommandNIProxy Execute Error - {}", e);
		} finally {
			// 자원 해제
			logger.debug("[code trace] command=" + command + ", closeClient 호출1");
			closeClient(client);
			logger.debug("[code trace] command=" + command + ", closeClient 호출2");
		}

		return null;
	}

	// 비동기 처리용 General Frame 생성
	public static byte[] makeGeneralFrameData(String netType, NIAttributeId command, HashMap<String, Object> param) {
		GeneralFrame gf = setGeneralFrameOption(FrameOption_Type.Command, command);
		//gf.setNIAttributeId(command.getCode());

		if (netType.equals("MBB")) {
			gf.setNetworkType(NetworkType.MBB);
		} else if (netType.equals("Sub1Ghz_SORIA")) {
			gf.setNetworkType(NetworkType.Sub1Ghz_SORIA);
		} else if (netType.equals("Ethernet")) {
			gf.setNetworkType(NetworkType.Ethernet);
		} else {
			gf.setNetworkType(NetworkType.MBB);
		}

		//setGeneralFrameOption(FrameOption_Type.Command, command, gf);

		byte[] sendData = new GeneralDataFrame().make(gf, param);
		logger.debug("[NI Proxy] New GeneralFrame : " + Hex.decode(sendData));

		return sendData;
	}

	private static void lock(int waitNum) throws InterruptedException {
		synchronized (lock) {
			if (connCount == MAX_COUNT) {
				waitNum = waitCount++;
				while (connCount == MAX_COUNT) {
					logger.debug("Waiting number[" + waitNum + "], wait count[" + waitCount + "]");
					lock.wait();
				}
			}
			connCount++;
			//logger.debug("Connecting number[" + waitNum + "], wait count[" + waitCount + "], conn count[" + connCount + "]");
		}
	}

	private static void release() {
		synchronized (lock) {
			//	logger.debug("Release connection count[" + connCount + "]");
			if (waitCount > 0 || connCount > 0) {
				lock.notify();
				if (waitCount > 0)
					waitCount--;
				if (connCount > 0)
					connCount--;
			}
		}
	}

	@Override
	public ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception {
		if (name == null) {
			name = new ObjectName(server.getDefaultDomain() + ":service=" + this.getClass().getName());
		}

		this.objectName = name;

		return name;
	}

	@Override
	public void postRegister(Boolean registrationDone) {
		// TODO Auto-generated method stub

	}

	@Override
	public void preDeregister() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void postDeregister() {
		// TODO Auto-generated method stub

	}

}
