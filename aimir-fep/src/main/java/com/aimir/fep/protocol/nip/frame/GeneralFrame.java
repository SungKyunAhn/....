package com.aimir.fep.protocol.nip.frame;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.nip.client.actions.CommandActionResult;
import com.aimir.fep.protocol.nip.exception.NiException;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Ack;
import com.aimir.fep.protocol.nip.frame.payload.Ack_CRC;
import com.aimir.fep.protocol.nip.frame.payload.AlarmEvent;
import com.aimir.fep.protocol.nip.frame.payload.Bypass;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.protocol.nip.frame.payload.Command.Attribute;
import com.aimir.fep.protocol.nip.frame.payload.Command.Attribute.Data;
import com.aimir.fep.protocol.nip.frame.payload.Firmware;
import com.aimir.fep.protocol.nip.frame.payload.MeterEvent;
import com.aimir.fep.protocol.nip.frame.payload.MeteringData;
import com.aimir.fep.protocol.nip.frame.payload.PayloadFrame;
import com.aimir.fep.util.CRCUtil;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.util.DateTimeUtil;

public class GeneralFrame {
	private static Log log = LogFactory.getLog(GeneralFrame.class);

	private static final byte[] START_FLAG = new byte[] { (byte) 0x51, (byte) 0xF8 };

	private CommandActionResult commandActionResult;
	
	public enum NIAttributeId {
		ResetModem(new byte[] { (byte) 0x00, (byte) 0x01 }),
		UploadMeteringData(new byte[] { (byte) 0x00, (byte) 0x02 }),
        FactorySetting(new byte[] { (byte) 0x00, (byte) 0x03 }),
        RecoveryPulseLoadProfileData(new byte[] { (byte) 0x00, (byte) 0x04 }),
        ReAuthenticate(new byte[] { (byte) 0x00, (byte) 0x05 }),
        WatchdogTest(new byte[] { (byte) 0x00, (byte) 0x08 }),
        RealTimeMetering(new byte[] { (byte) 0x00, (byte) 0x0A }),
        ModemInformation(new byte[] { (byte) 0x10, (byte) 0x01 }),
        NB_PLCInformation(new byte[] { (byte) 0x10, (byte) 0x02 }),
        ModemStatus(new byte[] { (byte) 0x10, (byte) 0x04 }),
        MeterInformation(new byte[] { (byte) 0x10, (byte) 0x05 }),
        ModemEventLog(new byte[] { (byte) 0x10, (byte) 0x06 }),
        CloneOnOff(new byte[] { (byte) 0x00, (byte) 0x0B }),
        ModemTime(new byte[] { (byte) 0x20, (byte) 0x01 }),
        ModemResetTime(new byte[] { (byte) 0x20, (byte) 0x02 }),
        ModemMode(new byte[] { (byte) 0x20, (byte) 0x03 }),
        ModemMode_GET(new byte[] { (byte) 0x20, (byte) 0x03 }),
        MeterTimesync(new byte[] { (byte) 0x20, (byte) 0x04 }),
        MeteringInterval(new byte[] { (byte) 0x20, (byte) 0x05 }),
        MeteringInterval_GET(new byte[] { (byte) 0x20, (byte) 0x05 }),
        ModemTXPower(new byte[] { (byte) 0x20, (byte) 0x08 }),
        KeepAliveMessageInterval(new byte[] { (byte) 0x20, (byte) 0x09 }),
        ActiveKeepTime(new byte[] { (byte) 0x20, (byte) 0x0A }),
        NetworkScanInterval(new byte[] { (byte) 0x20, (byte) 0x0B }),
        Form_JoinNetwork(new byte[] { (byte) 0x20, (byte) 0x0C }),
        NetworkSpeed(new byte[] { (byte) 0x20, (byte) 0x0D }),
        NetworkJoinTimeout(new byte[] { (byte) 0x20, (byte) 0x0E }),
        ModemIpInformation(new byte[] { (byte)0x20, (byte) 0x0F }), 
        ModemIpInformation_GET(new byte[] { (byte)0x20, (byte) 0x0F }),
        ModemPortInformation(new byte[] { (byte)0x20, (byte) 0x10 }),
        ModemPortInformation_GET(new byte[] { (byte)0x20, (byte) 0x10 }),
        Alarm_EventCommandON_OFF(new byte[] { (byte) 0x20, (byte) 0x11 }),
        Alarm_EventCommandON_OFF_GET(new byte[] { (byte) 0x20, (byte) 0x11 }),
        MeterBaud(new byte[] { (byte) 0x20, (byte) 0x12 }),
        MeterBaud_GET(new byte[] { (byte) 0x20, (byte) 0x12 }),
        TransmitFrequency(new byte[] { (byte) 0x20, (byte) 0x13 }),
        TransmitFrequency_GET(new byte[] { (byte) 0x20, (byte) 0x13 }),
        RetryCount(new byte[] { (byte) 0x20, (byte) 0x14 }),
        RetryCount_GET(new byte[] { (byte) 0x20, (byte) 0x14 }),
        SnmpTrapOnOff(new byte[] { (byte) 0x20, (byte) 0x15 }),
        SnmpTrapOnOff_GET(new byte[] { (byte) 0x20, (byte) 0x15 }),
        RawROMAccess(new byte[] { (byte) 0x20, (byte) 0x16 }),
        RawROMAccess_GET(new byte[] { (byte) 0x20, (byte) 0x16 }),
        NB_PLCTMR(new byte[] { (byte) 0x20, (byte) 0x06 }),
        NB_PLCModulation(new byte[] { (byte) 0x20, (byte) 0x07 }),
        MeterLoadprofileinterval(new byte[] { (byte) 0x30, (byte) 0x01 }),
        SelfReadscript(new byte[] { (byte) 0x30, (byte) 0x02 }),
        SingleActionSchedule(new byte[] { (byte) 0x30, (byte) 0x03 }),
        RecoveryMetering(new byte[] { (byte) 0x30, (byte) 0x04 }),
        OBISListup(new byte[] { (byte) 0x40, (byte) 0x01 }),
        OBISAdd(new byte[] { (byte) 0x40, (byte) 0x02 }),
        OBISRemove(new byte[] { (byte) 0x40, (byte) 0x03 }),
        OBISListChange(new byte[] { (byte) 0x40, (byte) 0x04 }),
        KEPCOOBISNewList(new byte[] { (byte) 0x40, (byte) 0x05 }),
        KEPCOOBISListVersion(new byte[] { (byte) 0x40, (byte) 0x06 }),
        TestConfiguration(new byte[] { (byte) 0x50, (byte) 0x01 }),
        TestDataUpload(new byte[] { (byte) 0x50, (byte) 0x02 }),
        CoordinatorInformation(new byte[] { (byte) 0xA0, (byte) 0x01 }),
        NetworkPermit(new byte[] { (byte) 0xA0, (byte) 0x02 }),
        BootloaderJump(new byte[] { (byte) 0xA0, (byte) 0x03 }),
        CoordinatorBootup(new byte[] { (byte) 0xA0, (byte) 0x04 }),
        NetworkIPv6Prefix(new byte[] { (byte) 0xA0, (byte) 0x05 }),
        APN(new byte[] { (byte) 0xB7, (byte) 0x06 }),
        AcceptJoin(new byte[] { (byte) 0xC0, (byte) 0x01 }),
        AcceptLeave(new byte[] { (byte) 0xC0, (byte) 0x02 }),
        JoinBackoffTimer(new byte[] { (byte) 0xC0, (byte) 0x03 }),
        AuthBackoffTimer(new byte[] { (byte) 0xC0, (byte) 0x04 }),
        MeterSharedKey(new byte[] { (byte) 0xC0, (byte) 0x05 }),
        NullBypassOpen(new byte[] { (byte) 0xC1, (byte) 0x01 }),
        NullBypassClose(new byte[] { (byte) 0xC1, (byte) 0x02 }),
        ROMRead(new byte[] { (byte) 0xC2, (byte) 0x01 }),
        GatheringDataAction(new byte[] { (byte) 0xC2, (byte) 0x02 }),
        GatheringDataPoll(new byte[] { (byte) 0xC2, (byte) 0x03 }),
        ParentNodeInfo(new byte[] { (byte) 0xC3, (byte) 0x01 }),
        HopCount(new byte[] { (byte) 0xC3, (byte) 0x02 }),
        HopNeighborList(new byte[] { (byte) 0xC3, (byte) 0x03 }),
        ChildNodeList(new byte[] { (byte) 0xC3, (byte) 0x04 }),
        NodeAuthorization(new byte[] { (byte) 0xC3, (byte) 0x05 });

		private byte[] code;

		NIAttributeId(byte[] code) {
			this.code = code;
		}

		public byte[] getCode() {
			return this.code;
		}

		public static NIAttributeId getItem(String name) {
			for (NIAttributeId fc : NIAttributeId.values()) {
				if (fc.name().equals(name)) {
					return fc;
				}
			}
			return null;
		}

		public static NIAttributeId getItem(byte[] code) {
			for (NIAttributeId fc : NIAttributeId.values()) {
				if (fc.getCode()[0] == code[0] && fc.getCode()[1] == code[1]) {
					return fc;
				}
			}
			return null;
		}
	}

	public enum CommandFlow {
		Request((byte) 0x00), Response_Trap((byte) 0x80), //한전
		Trap((byte) 0xC0);//Soria
		private byte code;

		CommandFlow(byte code) {
			this.code = code;
		}

		public byte getCode() {
			return this.code;
		}
	}

	public enum NetworkType {
		Sub1Ghz((byte) 0x00, 9), NB_PLC((byte) 0x01, 3), Zigbee((byte) 0x02, 0), // 2016.06.09 현재 : 값없음
		MBB((byte) 0x03, 7), Ethernet((byte) 0x04, 6), Sub1Ghz_SORIA((byte) 0x05, 18);

		private byte code;
		private int statusLength;

		NetworkType(byte code, int statusLength) {
			this.code = code;
			this.statusLength = statusLength;
		}

		public byte getCode() {
			return this.code;
		}

		public int getStatusLength() {
			return this.statusLength;
		}

		public static NetworkType getItem(byte value) {
			for (NetworkType fc : NetworkType.values()) {
				if (fc.code == value) {
					return fc;
				}
			}
			return null;
		}
	}

	public enum FrameControl_Pending {
		LastFrame((byte) 0x00), MultiFrame((byte) 0x80);

		private byte code;

		FrameControl_Pending(byte code) {
			this.code = code;
		}

		public byte getCode() {
			return this.code;
		}

		public static FrameControl_Pending getItem(byte value) {
			for (FrameControl_Pending fc : FrameControl_Pending.values()) {
				if (fc.code == value) {
					return fc;
				}
			}
			return null;
		}

	}

	public enum FrameControl_Ack {
		None((byte) 0x00), Ack((byte) 0x01), Task((byte) 0x02), CRC((byte)0x03);

		private byte code;

		FrameControl_Ack(byte code) {
			this.code = code;
		}

		public byte getCode() {
			return this.code;
		}

		public static FrameControl_Ack getItem(byte value) {
			for (FrameControl_Ack fc : FrameControl_Ack.values()) {
				if (fc.code == value) {
					return fc;
				}
			}
			return null;
		}
	}

	public enum FrameOption_NetworkStatus {
		None((byte) 0x00), Include((byte) 0x40);

		private byte code;

		FrameOption_NetworkStatus(byte code) {
			this.code = code;
		}

		public byte getCode() {
			return this.code;
		}

		public static FrameOption_NetworkStatus getItem(byte value) {
			for (FrameOption_NetworkStatus fc : FrameOption_NetworkStatus.values()) {
				if (fc.code == value) {
					return fc;
				}
			}
			return null;
		}
	}

	public enum FrameOption_Type {
		Ack((byte) 0x00), Bypass((byte) 0x01), Metering((byte) 0x02), Command((byte) 0x03), Firmware((byte) 0x04), AlarmEvent((byte) 0x05), MeterEvent((byte) 0x06);

		private byte code;

		FrameOption_Type(byte code) {
			this.code = code;
		}

		public byte getCode() {
			return this.code;
		}

		public static FrameOption_Type getItem(byte value) {
			for (FrameOption_Type fc : FrameOption_Type.values()) {
				if (fc.code == value) {
					return fc;
				}
			}
			return null;
		}

	}

	public enum FrameOption_AddressType {
		None((byte) 0x00), Source((byte) 0x10), Destination((byte) 0x20), SrcDest((byte) 0x30);

		private byte code;

		FrameOption_AddressType(byte code) {
			this.code = code;
		}

		public byte getCode() {
			return this.code;
		}

		public static FrameOption_AddressType getItem(byte value) {
			for (FrameOption_AddressType fc : FrameOption_AddressType.values()) {
				if (fc.code == value) {
					return fc;
				}
			}
			return null;
		}
	}

	public enum CommandType {
		Get((byte) 0x01), Set((byte) 0x02), Trap((byte) 0x03);

		private byte code;

		CommandType(byte code) {
			this.code = code;
		}

		public byte getCode() {
			return this.code;
		}
	}

	private byte[] startFlag = new byte[2];
	private byte[] networkType = new byte[1];
	private byte[] frameControl = new byte[1];
	private byte[] frameOption = new byte[1];
	private byte[] seqNumber = new byte[1];
	private byte[] srcAddress = new byte[8];
	private byte[] dstAddress = new byte[8];
	private byte[] payloadLength = new byte[2];
	private byte[] crc = new byte[2];
	private byte[] payloadData;

	public PayloadFrame payload;
	//public NetworkStatusSub1GhzForSORIA networkStatus;
	public NetworkStatus networkStatus;
	public NetworkType _networkType;
	public FrameControl_Pending fcPending = FrameControl_Pending.LastFrame;
	public int frameSequence = 0;   // NI Protocol 2.3.3 Frame sequence : 2016.12.13 현재 Modem OTA에서만 사용하고 있음 
	public FrameControl_Ack fcAck = FrameControl_Ack.None;
	public FrameOption_Type foType = FrameOption_Type.Ack;
	public FrameOption_AddressType foAddrType = FrameOption_AddressType.None;
	public FrameOption_NetworkStatus foNetworkStatus = FrameOption_NetworkStatus.None;
	public CommandFlow _commandFlow = CommandFlow.Request;
	public CommandType _commandType = CommandType.Get;
	public NIAttributeId niAttributeId;
	public AbstractCommand abstractCmd;
	public HashMap<String, Object> map = new HashMap<String, Object>();

	public byte[] getPayloadData() {
		return payloadData;
	}

	public void setPayloadData(byte[] payloadData) {
		this.payloadData = payloadData;
	}

	public NetworkStatus newNetworkStatus(NetworkType nType) {
		NetworkStatus ns = null;

		switch (nType) {
		case Sub1Ghz:
			ns = new NetworkStatusSub1Ghz();
			break;
		case MBB:
		    ns = new NetworkStatusMBB();
		    break;
		case Ethernet:
			ns = new NetworkStatusEthernet();
			break;
		case NB_PLC:
			ns = new NetworkStatusNBPLC();
			break;
		case Zigbee:
			// 2016.06.09 현재 : 값없음
			break;
		case Sub1Ghz_SORIA:
			ns = new NetworkStatusSub1GhzForSORIA();
			break;
		default:
			break;
		}
		return ns;
	}

	public NetworkStatus getNetworkStatus() {
		return networkStatus;
	}

	public void setNetworkStatus(NetworkStatus networkStatus) {
		this.networkStatus = networkStatus;
	}

	public void setFrameControl_Pending(FrameControl_Pending fcPending) {
		this.fcPending = fcPending;
	}

	public void setFrameControl_Ack(FrameControl_Ack fcAck) {
		this.fcAck = fcAck;
	}

	public void setFrameOption_Type(FrameOption_Type foType) {
		this.foType = foType;
	}

	public void setFrameOption_AddressType(FrameOption_AddressType foAddrType) {
		this.foAddrType = foAddrType;
	}

	public void setFrameOption_NetworkStatus(FrameOption_NetworkStatus foNetworkStatus) {
		this.foNetworkStatus = foNetworkStatus;
	}

	public CommandFlow getCommandFlow() {
		return this._commandFlow;
	}

	public void setCommandFlow(CommandFlow flow) {
		this._commandFlow = flow;
	}

	public void setCommandFlow(byte code) {
		for (CommandFlow c : CommandFlow.values()) {
			if (c.getCode() == code) {
				_commandFlow = c;
				break;
			}
		}
	}

	public NIAttributeId getNIAttributeId() {
		return niAttributeId;
	}

	public void setNIAttributeId(NIAttributeId niAttributeId) {
		this.niAttributeId = niAttributeId;
	}

	public void setNIAttributeId(byte[] code) {
		for (NIAttributeId c : NIAttributeId.values()) {
			if (c.getCode()[0] == code[0] && c.getCode()[1] == code[1]) {
				niAttributeId = c;
				break;
			}
		}
	}

	public CommandType getCommandType() {
		return this._commandType;
	}

	public void setCommandType(CommandType type) {
		this._commandType = type;
	}

	public void setCommandType(byte code) {
		for (CommandType c : CommandType.values()) {
			if (c.getCode() == code) {
				_commandType = c;
				break;
			}
		}
	}

	public byte[] getSeqNumber() {
		return seqNumber;
	}

	public void setSeqNumber(byte[] seqNumber) {
		this.seqNumber = seqNumber;
	}

	public PayloadFrame getPayload() {
		return payload;
	}

	public void setPayload(PayloadFrame payload) {
		this.payload = payload;
	}

	public NetworkType getNetworkType() {
		return _networkType;
	}

	public void setNetworkType(NetworkType _networkType) {
		this._networkType = _networkType;
	}

	public FrameControl_Pending getFcPending() {
		return fcPending;
	}

	public void setFcPending(FrameControl_Pending fcPending) {
		this.fcPending = fcPending;
	}
	
	public int getFrameSequence() {
		return frameSequence;
	}

	public byte getFrameSequenceByte() {
		String binaryString = String.format("%05d", new BigInteger(Integer.toBinaryString(frameSequence)));
		return (byte) Integer.parseInt("0" + binaryString + "00", 2);
	}
	
	public void setFrameSequence(int frameSequence) {
		this.frameSequence = frameSequence;
	}

	public FrameControl_Ack getFcAck() {
		return fcAck;
	}

	public void setFcAck(FrameControl_Ack fcAck) {
		this.fcAck = fcAck;
	}

	public FrameOption_Type getFoType() {
		return foType;
	}

	public void setFoType(FrameOption_Type foType) {
		this.foType = foType;
	}

	public FrameOption_AddressType getFoAddrType() {
		return foAddrType;
	}

	public void setFoAddrType(FrameOption_AddressType foAddrType) {
		this.foAddrType = foAddrType;
	}

	public FrameOption_NetworkStatus getFoNetworkStatus() {
		return foNetworkStatus;
	}

	public void setFoNetworkStatus(FrameOption_NetworkStatus foNetworkStatus) {
		this.foNetworkStatus = foNetworkStatus;
	}

	public byte[] getStartFlag() {
		return startFlag;
	}

	public void setStartFlag(byte[] startFlag) {
		this.startFlag = startFlag;
	}

	public byte[] getSrcAddress() {
		return srcAddress;
	}

	public void setSrcAddress(byte[] srcAddress) {
		this.srcAddress = srcAddress;
	}

	public byte[] getDstAddress() {
		return dstAddress;
	}

	public void setDstAddress(byte[] dstAddress) {
		this.dstAddress = dstAddress;
	}

	public byte[] getCrc() {
		return crc;
	}

	public void setCrc(byte[] crc) {
		this.crc = crc;
	}

	public void decode(byte[] data) throws NiException {
	    decode(null, data);
	}

	public Map<Integer, byte[]> decode(Map<Integer, byte[]> multiFrame, byte[] data) throws NiException {
        try {
            log.debug(Hex.decode(data));
            int pos = 0;
            System.arraycopy(data, pos, startFlag, 0, startFlag.length);
            pos += startFlag.length;
            log.debug("START_FLAG[" + Hex.decode(startFlag) + "]");

            System.arraycopy(data, pos, networkType, 0, 1);
            pos++;
            setNetworkType();
            log.debug("[NI][DECODE] NetworkType => " + getNetworkType().name());

            System.arraycopy(data, pos, frameControl, 0, 1);
            pos++;
            setFrameControl();
            log.debug("[NI][DECODE] Frame Control - Pending => " + getFcPending());
            log.debug("[NI][DECODE] Frame Control - Sequence => " + getFrameSequence());
            log.debug("[NI][DECODE] Frame Control - Request ACK => " + getFcAck());

            System.arraycopy(data, pos, frameOption, 0, 1);
            pos++;
            setFrameOption();
            log.debug("[NI][DECODE] Frame Option - NetworkStatusFieldType => " + getFoNetworkStatus());
            log.debug("[NI][DECODE] Frame Option - AddressFieldType => " + getFoAddrType());
            log.debug("[NI][DECODE] Frame Option - FrameType => " + getFoType());

            System.arraycopy(data, pos, seqNumber, 0, 1);
            pos++;
            log.debug("[NI][DECODE] Sequence Number => " + DataUtil.getIntToBytes(seqNumber));

            String optionFrame = DataUtil.getBit(frameOption[0]);
            //setFrameOption_AddressType(DataUtil.getByteToInt(DataUtil.getBitToInt(optionFrame.substring(2,4),"%d0000")));
            switch (foAddrType) {
            case None:
                break;
            case Source:
                System.arraycopy(data, pos, srcAddress, 0, srcAddress.length);
                pos += srcAddress.length;
                log.debug("SRC_ADDR[" + Hex.decode(srcAddress) + "]");
                break;
            case Destination:
                System.arraycopy(data, pos, dstAddress, 0, dstAddress.length);
                pos += dstAddress.length;
                log.debug("DST_ADDR[" + Hex.decode(dstAddress) + "]");
                break;
            case SrcDest:
                System.arraycopy(data, pos, srcAddress, 0, srcAddress.length);
                pos += srcAddress.length;

                System.arraycopy(data, pos, dstAddress, 0, dstAddress.length);
                pos += dstAddress.length;
                log.debug("SRC_ADDR[" + Hex.decode(srcAddress) + "] DST_ADDR[" + Hex.decode(dstAddress) + "]");
                break;
            }

            //log.debug("[NI][DECODE] Source Address => " + (DataUtil.getIntTo8Byte(srcAddress) == 0 ? "" : DataUtil.getIntTo8Byte(srcAddress))); //sourceAddress = DataUtil.getString(temp).trim();
            //log.debug("[NI][DECODE] Destination Address => " + (DataUtil.getIntTo8Byte(dstAddress) == 0 ? "" : DataUtil.getIntTo8Byte(dstAddress)));

            if (getFoNetworkStatus() == FrameOption_NetworkStatus.Include) {
                byte[] networkStatusData = new byte[getNetworkType().statusLength];
                System.arraycopy(data, pos, networkStatusData, 0, networkStatusData.length);
                networkStatus = newNetworkStatus(NetworkType.getItem(networkType[0]));
                networkStatus.decode(networkStatusData);
                pos += networkStatusData.length;

                log.debug("[NI][DECODE] Network Status => " + networkStatus.toString());
            }

            System.arraycopy(data, pos, payloadLength, 0, payloadLength.length);
            pos += payloadLength.length;
            log.debug("[NI][DECIDE] Payload Length => " + DataUtil.getIntTo2Byte(payloadLength));

            /*
            log.debug("[NICL][decode][startFlag]"+Hex.decode(startFlag));
            log.debug("[NICL][decode][networkType]"+Hex.decode(networkType));
            log.debug("[NICL][decode][frameAddressFieldType]"+optionFrame.substring(2,4));
            log.debug("[NICL][decode][frameControl]"+Hex.decode(frameControl));
            log.debug("[NICL][decode][frameOption]"+Hex.decode(frameOption));
            log.debug("[NICL][decode][seqNumber]"+Hex.decode(seqNumber));
            log.debug("[NICL][decode][srcAddress]"+Hex.decode(srcAddress));
            log.debug("[NICL][decode][dstAddress]"+Hex.decode(dstAddress));
            log.debug("[NICL][decode][payloadLen]"+Hex.decode(payloadLength));
            */

            byte[] bx = null;
            bx = new byte[DataUtil.getIntTo2Byte(payloadLength)];
            System.arraycopy(data, pos, bx, 0, bx.length);
            pos += bx.length;
            
            if (getFcPending() == FrameControl_Pending.MultiFrame) {
                if (multiFrame == null) {
                    multiFrame = new HashMap<Integer, byte[]>();
                }
                
                multiFrame.put(DataUtil.getIntToBytes(seqNumber), bx);
                log.debug("MULTI_SIZE[" + multiFrame.size() + "]");
            }
            else {
                if (multiFrame != null) {
                    multiFrame.put(DataUtil.getIntToBytes(seqNumber), bx);
                    ByteArrayOutputStream out = null;
                    try {
                        out = new ByteArrayOutputStream();
                        for (int i = 0; i < multiFrame.size(); i++) {
                            out.write(multiFrame.get(i));
                        }
                        bx = out.toByteArray();
                    }
                    finally {
                        if (out != null) out.close();
                        if (multiFrame != null) multiFrame = null;
                    }
                }
                
                switch (foType) {
                case Ack:
                    setFrame();
                    break;
                case Bypass:
                    setFrame();
                    // bx = new byte[DataUtil.getIntTo2Byte(payloadLength)];
                    // System.arraycopy(data, pos, bx, 0, bx.length);
                    payload.decode(bx);
                    /*
                    log.debug("[NICL][Bypass]"+Hex.decode(data));
                    log.debug("[NICL][Bypass]"+pos);
                    log.debug("[NICL][Bypass]"+Hex.decode(bx));
                    */
                    break;
                case Metering:
                    setFrame();
                    payload.decode(bx);
                    /*
                    log.debug("[NICL][Metering]"+Hex.decode(data));
                    log.debug("[NICL][Metering]"+pos);
                    log.debug("[NICL][Metering]"+Hex.decode(bx));
                    */
                    break;
                case Command:
                    setFrame();
                    payload.decode(bx);
                    Command commandPayload = (Command) payload;
                    setCommandFlow(commandPayload.getCommandFlow());
                    setCommandType(commandPayload.getCommandType());
    
                    pos += crc.length;
    
                    /*
                    log.debug("[NICL][Command][payload]"+Hex.decode(bx));
                    log.debug("[NICL][Command][frameType bit]"+optionFrame);
                    log.debug("[NICL][Command][frameType typ]"+optionFrame.substring(4,8));
                    log.debug("[NICL][Command][frameType int]"+Hex.decode(new byte[]{DataUtil.getByteToInt(DataUtil.getBitToInt(optionFrame.substring(4,8),"%01d"))}));
                    log.debug("[NICL][Command][commandFrame]"+commandFrame);
                    log.debug("[NICL][Command][FLOW]"+Hex.decode(new byte[]{bFlow})); //FLOW
                    log.debug("[NICL][Command][TYPE]"+Hex.decode(new byte[]{bType})); //TYPE
                    log.debug("[NICL][Command][bx]"+Hex.decode(bx)); //FLOW
                    log.debug("[NICL][Command][payloadLength]"+payloadLength.length); 
                    log.debug("[NICL][Command][payLoadLen]"+Hex.decode(payLoadLen)); 
                    log.debug("[NICL][Command][payloadData]"+Hex.decode(payloadData));
                    log.debug("[NICL][Command][crc]"+Hex.decode(crc));
                    */
                    break;
                case Firmware:
                    setFrame();
                    payload.decode(bx);
                    /*
                    log.debug("[NICL][Firmware]"+Hex.decode(data));
                    log.debug("[NICL][Firmware]"+pos);
                    log.debug("[NICL][Firmware]"+Hex.decode(bx));
                    */
                    break;
                case AlarmEvent:
                    setFrame();
                    payload.decode(bx);
                    /*
                    log.debug("[NICL][AlarmEvent]"+Hex.decode(data));
                    log.debug("[NICL][AlarmEvent]"+pos);
                    log.debug("[NICL][AlarmEvent]"+Hex.decode(bx));
                    */
                    break;
                case MeterEvent:
                    setFrame();
                    payload.decode(bx);
                    /*
                    log.debug("[NICL][MeterEvent]"+Hex.decode(data));
                    log.debug("[NICL][MeterEvent]"+pos);
                    log.debug("[NICL][MeterEvent]"+Hex.decode(bx));
                    */
                    break;
                }
            }
        } catch (NullPointerException e) {
            throw new NiException("[GeneralFrame][decode] check NULL in rececived data.", e);
        } catch (Exception e) {
            log.error(e, e);
        }
        
        return multiFrame;
    }
	
	public byte[] encode(byte[] attData) throws NiException {
		byte[] b = null;
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			out.write(START_FLAG);
			out.write(_networkType.getCode());
			
			//frameControl[0] = (byte) (fcPending.getCode() | fcAck.getCode());
			frameControl[0] = (byte) (fcPending.getCode() | getFrameSequenceByte() | fcAck.getCode());
			out.write(frameControl);
			
			frameOption[0] = (byte) (foNetworkStatus.getCode() | foAddrType.getCode() | foType.getCode());
			out.write(frameOption);
			out.write(seqNumber);
			
			switch (foAddrType) {
			case None:
				break;
			case Source:
				out.write(srcAddress);
				break;
			case Destination:
				out.write(dstAddress);
				break;
			case SrcDest:
				out.write(srcAddress);
				out.write(dstAddress);
				break;
			}
			/**
			 * Network Status Network 경로를 확인하고, 모뎀의 네트워크 상태를 확인하기 위한 필드이다. 이 필드는
			 * Network Status Field Type에 따라 필드의 유무가 정해 진다
			 */
			switch (foNetworkStatus) {
			//Soria RF
			case Include:
				// out.write(networkStatus.encode());
				break;
			default:
				break;
			}

			byte[] _payload = payload.encode();
			out.write(DataUtil.get2ByteToInt(_payload.length));
			out.write(_payload);

			byte[] crc = CRCUtil.Calculate_ZigBee_Crc(out.toByteArray(), (char) 0x0000);
			DataUtil.convertEndian(crc);
			out.write(crc);
			b = out.toByteArray();
			/*
			setFrame();
			b = attData;
			if (attData != null) {
				out.write(DataUtil.get2ByteToInt(attData.length));
				out.write(attData);
			}
			b = out.toByteArray();
			CRCUtil crc16 = new CRCUtil();
			byte[] crc = crc16.Calculate_ZigBee_Crc(b, (char) 0x0000);
			DataUtil.convertEndian(crc);
			out.write(crc);
			b = out.toByteArray();
			*/
//			log.debug(Hex.decode(b));
		} catch (Exception e) {
			log.error(e, e);
		}
		return b;
	}

	private void setNetworkType() {
		for (NetworkType n : NetworkType.values()) {
			if (networkType[0] == n.getCode()) {
				_networkType = n;
				break;
			}
		}
	}

	private void setFrameControl() {
		//        for (FrameControl_Pending f : FrameControl_Pending.values()) {
		//            if ((frameControl[0] & f.getCode()) != (byte)0x00) {
		//                fcPending = f;
		//                break;
		//            }
		//        }
		//        
		//        for (FrameControl_Ack f : FrameControl_Ack.values()) {
		//            if ((frameControl[0]& f.getCode()) != (byte)0x00) {
		//                fcAck = f;
		//                break;
		//            }
		//        }

		fcPending = FrameControl_Pending.getItem((byte) (frameControl[0] & (byte) 0x80));
		
		/*  Sequence. 2016.12.13 현재 Modem OTA시에 사용하고 있음 */
		int frameControlValue = DataUtil.getIntToByte(frameControl[0]); 
		String seqByteString = String.format("%08d", new BigInteger(Integer.toBinaryString(frameControlValue)));
		frameSequence = Integer.parseInt(seqByteString.substring(1, 6), 2);
		log.debug("### Framesequence ====> " + frameSequence);
		
		fcAck = FrameControl_Ack.getItem((byte) (frameControl[0] & (byte) 0x03));
	}

	public void setFrameControl_Ack(byte code) {
		for (FrameControl_Ack f : FrameControl_Ack.values()) {
			if (f.getCode() == code) {
				fcAck = f;
				break;
			}
		}
	}

	public void setFrameControl_Pending(byte code) {
		for (FrameControl_Pending f : FrameControl_Pending.values()) {
			if (f.getCode() == code) {
				fcPending = f;
				break;
			}
		}
	}

	public void setFrameOption_Type(byte code) {
		for (FrameOption_Type f : FrameOption_Type.values()) {
			if (f.getCode() == code) {
				foType = f;
				break;
			}
		}
	}

	public void setFrameOption_NetworkStatus(byte code) {
		for (FrameOption_NetworkStatus f : FrameOption_NetworkStatus.values()) {
			if (f.getCode() == code) {
				foNetworkStatus = f;
				break;
			}
		}
	}

	public void setFrameOption_AddressType(byte code) {
		for (FrameOption_AddressType f : FrameOption_AddressType.values()) {
			if (f.getCode() == code) {
				foAddrType = f;
				break;
			}
		}
	}

	public void setNetworkType(byte code) {
		for (NetworkType f : NetworkType.values()) {
			if (f.getCode() == code) {
				_networkType = f;
				break;
			}
		}
	}

	private void setFrameOption() {
		//        for (FrameOption_Type f : FrameOption_Type.values()) {
		//            if ((frameOption[0] & f.getCode()) != (byte)0x00) {
		//                foType = f;
		//                break;
		//            }
		//        }
		//        for (FrameOption_AddressType f : FrameOption_AddressType.values()) {
		//            if ((frameOption[0] & f.getCode()) != (byte)0x00) {
		//                foAddrType = f;
		//                break;
		//            }
		//        }
		//        for (FrameOption_NetworkStatus f : FrameOption_NetworkStatus.values()) {
		//            if ((frameOption[0] & f.getCode()) != (byte)0x00) {
		//                foNetworkStatus = f;
		//                break;
		//            }
		//        }

		foType = FrameOption_Type.getItem((byte) (frameOption[0] & (byte) 0x0F));
		foAddrType = FrameOption_AddressType.getItem((byte) (frameOption[0] & (byte) 0x30));
		foNetworkStatus = FrameOption_NetworkStatus.getItem((byte) (frameOption[0] & (byte) 0x40));

	}

	private void setNetworkStatus(byte[] data, int pos) {
		if (networkType[0] == NetworkType.Sub1Ghz.getCode()) {
			byte[] b = new byte[NetworkType.Sub1Ghz.getStatusLength()];
			System.arraycopy(data, pos, b, 0, b.length);
			pos += b.length;
			networkStatus = new NetworkStatusSub1Ghz();
			networkStatus.decode(b);
		} else if (networkType[0] == NetworkType.NB_PLC.getCode()) {
			byte[] b = new byte[NetworkType.NB_PLC.getStatusLength()];
			System.arraycopy(data, pos, b, 0, b.length);
			pos += b.length;
			networkStatus = new NetworkStatusSub1GhzForSORIA();
			networkStatus.decode(b);
		} else if (networkType[0] == NetworkType.Zigbee.getCode()) {
			/*
			 * 2016.06.09 현재 없음.
			 */
		} else if (networkType[0] == NetworkType.MBB.getCode()) {
			byte[] b = new byte[NetworkType.MBB.getStatusLength()];
			System.arraycopy(data, pos, b, 0, b.length);
			pos += b.length;
			networkStatus = new NetworkStatusSub1GhzForSORIA();
			networkStatus.decode(b);
		} else if (networkType[0] == NetworkType.Ethernet.getCode()) {
			byte[] b = new byte[NetworkType.Ethernet.getStatusLength()];
			System.arraycopy(data, pos, b, 0, b.length);
			pos += b.length;
			networkStatus = new NetworkStatusSub1GhzForSORIA();
			networkStatus.decode(b);
		} else if (networkType[0] == NetworkType.Sub1Ghz_SORIA.getCode()) {
			byte[] b = new byte[NetworkType.Sub1Ghz_SORIA.getStatusLength()];
			System.arraycopy(data, pos, b, 0, b.length);
			pos += b.length;
			networkStatus = new NetworkStatusSub1GhzForSORIA();
			networkStatus.decode(b);
		}

	}

	//GeneralFrame Setting
	public void setClientBaseInfo(NIAttributeId attList, CommandFlow commandFlow, CommandType commandType, NetworkType networkType, FrameControl_Pending frameControlPending, FrameControl_Ack frameControlAck, FrameOption_NetworkStatus frameOptionNetworkStatus, FrameOption_AddressType frameOptionAddressType, FrameOption_Type frameOptionType, byte[] maxPayloadcnt, byte[] source, byte[] destination)
			throws NiException {
		try {
			setNIAttributeId(attList);
			setCommandFlow(commandFlow);
			setCommandType(commandType);
			setNetworkType(networkType);
			setFrameControl_Pending(frameControlPending);
			setFrameControl_Ack(frameControlAck);
			setFrameOption_NetworkStatus(frameOptionNetworkStatus);
			setFrameOption_AddressType(frameOptionAddressType);
			setFrameOption_Type(frameOptionType);
			setSeqNumber(maxPayloadcnt);
			setSrcAddress(source);
			setDstAddress(destination);

		} catch (NullPointerException e) {
			throw new NiException("[GeneralFrame][setClientBaseInfo]check parameter " + e.getMessage());
		}
	}

	/**
	 * Frame Type Setting
	 */
	public void setFrame() {
		switch (foType) {
		case Ack:
			payload = new Ack();
			break;
		case Bypass:
			payload = new Bypass();
			break;
		case Metering:
			payload = new MeteringData();
			break;
		case Command:
			payload = new Command();
			payload.setCommandFlow(_commandFlow.getCode());
			payload.setCommandType(_commandType.getCode());
			break;
		case Firmware:
			payload = new Firmware();
			break;
		case AlarmEvent:
			payload = new AlarmEvent();
			break;
		case MeterEvent:
			payload = new MeterEvent();
			break;
		}
	}
	
	
	public CommandActionResult getCommandActionResult() {
		return commandActionResult;
	}

	public void setCommandActionResult(CommandActionResult commandActionResult) {
		this.commandActionResult = commandActionResult;
	}

	public byte[] ack(byte[] crc) throws Exception {
        GeneralFrame frame = new GeneralFrame();
        frame._networkType = this._networkType;
        frame.fcPending = FrameControl_Pending.LastFrame;
        frame.fcAck = FrameControl_Ack.None;
        frame.foNetworkStatus = FrameOption_NetworkStatus.None;
        frame.foAddrType = FrameOption_AddressType.None;
        frame.foType = FrameOption_Type.Ack;
        frame.setSeqNumber(new byte[]{DataUtil.getByteToInt(0)});
        if (crc == null)
            frame.setPayload(new Ack());
        else
            frame.setPayload(new Ack_CRC(crc));
        
        return frame.encode(null);
    }
        
    public byte[] msKeyFrame(byte[] tid, 
            String masterKey, String unicastKey, String multicastKey,
            String authKey) throws Exception {
        GeneralFrame frame = new GeneralFrame();
        frame._networkType = this._networkType;
        frame.fcPending = FrameControl_Pending.LastFrame;
        frame.fcAck = FrameControl_Ack.None;
        frame.foNetworkStatus = FrameOption_NetworkStatus.None;
        frame.foAddrType = FrameOption_AddressType.None;
        frame.foType = FrameOption_Type.Command;
        frame.setSeqNumber(new byte[]{DataUtil.getByteToInt(0)});
        
        Command cmd = new Command();
        cmd.setCommandFlow(CommandFlow.Response_Trap.getCode());
        cmd.setCommandType(CommandType.Get.getCode());
        cmd.setTid(tid);
        
        Attribute attr = cmd.newAttribute();
        attr.newData(1);
        Data[] data = attr.getData();
        data[0].setId(NIAttributeId.MeterSharedKey.getCode());
        ByteArrayOutputStream out = null;
        try {
            log.debug("MASTER_KEY[" + masterKey + "] MULTICAST_KEY[" + multicastKey + 
                    "] UNITCAST_KEY[" + unicastKey + "] AUTH_KEY[" + authKey + "]");
            out = new ByteArrayOutputStream();
            out.write(Hex.encode(masterKey));
            out.write(Hex.encode(multicastKey));
            out.write(Hex.encode(unicastKey));
            out.write(Hex.encode(authKey));
            
            data[0].setValue(out.toByteArray());
        }
        finally {
            if (out != null) out.close();
        }
        attr.setData(data);
        cmd.setAttribute(attr);
        frame.setPayload(cmd);
        
        return frame.encode(null);
    }
    
    public byte[] modemTimeFrame(byte[] tid) throws Exception {
        GeneralFrame frame = new GeneralFrame();
        frame._networkType = this._networkType;
        frame.fcPending = FrameControl_Pending.LastFrame;
        frame.fcAck = FrameControl_Ack.None;
        frame.foNetworkStatus = FrameOption_NetworkStatus.None;
        frame.foAddrType = FrameOption_AddressType.None;
        frame.foType = FrameOption_Type.Command;
        frame.setSeqNumber(new byte[]{DataUtil.getByteToInt(0)});
        
        Command cmd = new Command();
        cmd.setCommandFlow(CommandFlow.Response_Trap.getCode());
        cmd.setCommandType(CommandType.Get.getCode());
        cmd.setTid(tid);
        
        Attribute attr = cmd.newAttribute();
        attr.newData(1);
        Data[] data = attr.getData();
        data[0].setId(NIAttributeId.ModemTime.getCode());
        ByteArrayOutputStream out = null;
        String curTime = DateTimeUtil.getDateString(new Date());
        
        try {
            out = new ByteArrayOutputStream();
            out.write(DataUtil.get2ByteToInt(Integer.parseInt(curTime.substring(0, 4))));
            out.write(DataUtil.getByteToInt(Integer.parseInt(curTime.substring(4, 6))));
            out.write(DataUtil.getByteToInt(Integer.parseInt(curTime.substring(6, 8))));
            out.write(DataUtil.getByteToInt(Integer.parseInt(curTime.substring(8, 10))));
            out.write(DataUtil.getByteToInt(Integer.parseInt(curTime.substring(10, 12))));
            out.write(DataUtil.getByteToInt(Integer.parseInt(curTime.substring(12, 14))));
            
            data[0].setValue(out.toByteArray());
        }
        finally {
            if (out != null) out.close();
        }
        attr.setData(data);
        cmd.setAttribute(attr);
        frame.setPayload(cmd);
        
        return frame.encode(null);
    }
    
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("StartFlag=" + (getStartFlag() == null ? "" : Hex.decode(getStartFlag())));
		sb.append(", NetworkType=" + (getNetworkType() == null ? "" : getNetworkType().name()));
		sb.append(", FrameControl-Pending=" + (getFcPending() == null ? "" : getFcPending().name()));
		sb.append(", FrameControl-Sequence=" + getFrameSequence());
		sb.append(", FrameControl-RequestACK=" + (getFcAck() == null ? "" : getFcAck()));
		sb.append(", SeqNumber=" + (getSeqNumber() == null ? "" : Hex.decode(getSeqNumber())));
		sb.append(", SrcAddress=" + (getSrcAddress() == null ? "" : Hex.decode(getSrcAddress())));
		sb.append(", DstAddress=" + (getDstAddress() == null ? "" : Hex.decode(getDstAddress())));
		sb.append(", payloadLength=" + (getPayloadData() == null ? "" : getPayloadData().length));
		sb.append(", CRC=" + (getCrc() == null ? "" : Hex.decode(getCrc())));
		sb.append(", PayloadData=" + (getPayload() == null ? "" : getPayload().toString()));
		
		return sb.toString();
	}
}
