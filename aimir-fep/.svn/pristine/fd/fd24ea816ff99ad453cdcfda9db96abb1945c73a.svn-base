package com.aimir.fep.protocol.mrp.protocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.MeterModel;
import com.aimir.fep.meter.parser.MX2Table.NegotiateTable;
import com.aimir.fep.meter.parser.a1830rlnTable.ST55;
import com.aimir.fep.protocol.fmp.datatype.BYTE;
import com.aimir.fep.protocol.fmp.datatype.INT;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.protocol.fmp.datatype.OID;
import com.aimir.fep.protocol.fmp.datatype.OPAQUE;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.datatype.TIMESTAMP;
import com.aimir.fep.protocol.fmp.datatype.WORD;
import com.aimir.fep.protocol.fmp.frame.service.CommandData;
import com.aimir.fep.protocol.fmp.frame.service.entry.meterLPEntry;
import com.aimir.fep.protocol.mrp.MeterProtocolHandler;
import com.aimir.fep.protocol.mrp.client.MRPClientProtocolHandler;
import com.aimir.fep.protocol.mrp.exception.MRPError;
import com.aimir.fep.protocol.mrp.exception.MRPException;
import com.aimir.fep.util.ByteArray;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.util.TimeUtil;

import java.util.Arrays;

/**
 * @author JiHoon Kim, jihoon@nuritelecom.com
 */
public class A1830RLN_Handler extends MeterProtocolHandler {

	private static Log log = LogFactory.getLog(A1830RLN_Handler.class);
	private String meterId;
	private String modemId;
	private String groupId = "";
	private String memberId = "";
	private String mcuSwVersion = "";

	private byte before_txd_control;
	private byte meter_length;

	private static final long TIMEOUT = 10;

	private byte[][] keys = new byte[16][48];

	private int headerLength = 4;
	protected long sendBytes;

	/**
	 * Constructor.
	 * <p>
	 */
	public A1830RLN_Handler() {

	}

	public void setModemNumber(String modemId) {
		this.modemId = modemId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public void setMcuSwVersion(String mcuSwVersion) {
		this.mcuSwVersion = mcuSwVersion;
	}

	@Override
	public CommandData execute(MRPClientProtocolHandler handler,
			IoSession session, CommandData command) throws MRPException {

		this.handler = handler;

		session.setAttribute(MRPClientProtocolHandler.isActiveKey, false);

		CommandData commandData = null;
		byte[] mti = null;
		byte[] tpb = null;
		byte[] tcb = null;
		byte[] lpd = null;
		byte[] ist = null;
		byte[] eld = null;
		byte[] pqm = null;
		ByteArray ba = new ByteArray();
		int nOffset = 0;
		int nCount = 96;
		String from = "";
		String cmd = command.getCmd().getValue();
		log.debug("==============A1830RLN_Handler start cmd:" + cmd + "================");
		
		if(cmd.equals("105.2.0") || cmd.equals("105.5.0")){
        	
        	log.debug("A1830RLN Meter Time Sync .............");

        	String currentTime = "";
        	try {
				currentTime = TimeUtil.getCurrentTime();
			} catch (ParseException e) {
				log.debug(e,e);
				e.printStackTrace();
			}
        	SMIValue[] smiValue = command.getSMIValue();
            if(smiValue != null && smiValue.length >= 2){
            	currentTime = ((TIMESTAMP)smiValue[1].getVariable()).getValue();
            }      
            log.debug(currentTime);
            byte[] time = new byte[6];
            time[0] = DataUtil.getByteToInt( Integer.parseInt( currentTime.substring(0, 4) )%1000 );
            time[1] = DataUtil.getByteToInt( Integer.parseInt( currentTime.substring(4, 6) ) );
            time[2] = DataUtil.getByteToInt( Integer.parseInt( currentTime.substring(6, 8) ) );
            time[3] = DataUtil.getByteToInt( Integer.parseInt( currentTime.substring(8, 10) ) );
            time[4] = DataUtil.getByteToInt( Integer.parseInt( currentTime.substring(10, 12) ) );
            time[5] = DataUtil.getByteToInt( Integer.parseInt( currentTime.substring(12, 14) ) );
            
            try {
            	//미터 인증
            	initProcess(session);
            	//미터동기화 및 결과값 리턴.
            	HashMap result = meterTimeSync(session, (byte) 0x00, time);
            	
            	//시간동기화 변경 전후의 값 셋팅.
            	byte[] md = new byte[30];
            	if(result !=null){
                	byte[] before = new byte[6];
                	byte[] after = new byte[6];
                	if(result.get("beforeTime")  != null ) {
                		before = ((OCTET)result.get("beforeTime")).getValue();
                		log.debug(Hex.getHexDump(before));
                		md[6] 	= (byte)DataFormat.hex2unsigned8(before[0]);
                		md[7] 	= (byte)DataFormat.hex2unsigned8(before[1]);
                		md[8] 	= (byte)DataFormat.hex2unsigned8(before[2]);
                		md[9] 	= (byte)DataFormat.hex2unsigned8(before[3]);
                		md[10] 	= (byte)DataFormat.hex2unsigned8(before[4]);
                		md[11] 	= (byte)DataFormat.hex2unsigned8(before[5]);
                	}
                	if(result.get("afterTime") != null){
                		after = ((OCTET)result.get("afterTime")).getValue();
                		log.debug(Hex.getHexDump(after));
                		md[21] = (byte)DataFormat.hex2unsigned8(after[0]);
                		md[22] = (byte)DataFormat.hex2unsigned8(after[1]);
                		md[23] = (byte)DataFormat.hex2unsigned8(after[2]);
                		md[24] = (byte)DataFormat.hex2unsigned8(after[3]);
                		md[25] = (byte)DataFormat.hex2unsigned8(after[4]);
                		md[26] = (byte)DataFormat.hex2unsigned8(after[5]);
                	}

                }

                commandData = new CommandData();
                commandData.setCnt(new WORD(1));
                commandData.setTotalLength(md.length);
                commandData.append(new SMIValue(new OID("1.12.0"),new OCTET(md)));
            	
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.debug(e,e);
			}

        }else if (cmd.equals("104.6.0") || cmd.equals("104.13.0")) {
        	
			SMIValue[] smiValue = command.getSMIValue();
			int baCnt = 0;
			if (smiValue != null && smiValue.length >= 2) {
				int kind = ((INT) smiValue[1].getVariable()).getValue();

				if (smiValue.length == 4) {
					nOffset = ((INT) smiValue[2].getVariable()).getValue();
					nCount = ((INT) smiValue[3].getVariable()).getValue();

					try {
						from = TimeUtil.getPreDay(TimeUtil.getCurrentTime(), nOffset);
					} catch (ParseException e) {
						log.debug(e,e);
					}
				} else {
					try {
						from = TimeUtil.getPreDay(TimeUtil.getCurrentTime(), 2);
					} catch (ParseException e) {
						log.debug(e,e);
					}
				}

				// Modem Info( 30 byte)
				// byte[] mcuId = new byte[17];
				// System.arraycopy(command.getMcuId().getBytes(), 0, mcuId, 0,
				// command.getMcuId().length());
				// ba.append(new byte[]{0x00,0x00,0x00,0x00,0x00});
				// ba.append(new byte[]{0x00});
				// ba.append(mcuId);
				// ba.append(new byte[]{(byte)0x86});
				// ba.append(new byte[]{(byte)0x00});
				// ba.append(new byte[]{(byte)0x00});
				// ba.append(new byte[]{0x00,0x00,0x00,0x00});
				switch (kind) {
				case A1830RLN_DataConstants.CMD_ALL:// all
					initProcess(session);
					mti = configureRead(session).toByteArray();
					tpb = billRead(session).toByteArray();
					tcb = currbillRead(session).toByteArray();
//					nOffset = 0; nCount = 1; // test
					if(nCount > 5) nCount = 1;	//오프셋 미지정시 하루만 읽기 처리.
					lpd = lpRead(session, String.valueOf(nOffset), String.valueOf(nCount), 15).toByteArray();
					eld = eventlogRead(session).toByteArray();
					ist = instRead(session).toByteArray();
					pqm = pqRead(session).toByteArray();

					//MT
					ba.append("MT".getBytes());
					ba.append(DataUtil.get2ByteToInt(mti.length));
					ba.append(mti);
					
					//PB
					ba.append("PB".getBytes());
					ba.append(DataUtil.get2ByteToInt(tpb.length));
					ba.append(tpb);
					
					//CB
					ba.append("CB".getBytes());
					ba.append(DataUtil.get2ByteToInt(tcb.length));
					ba.append(tcb);
					
					//LD
					ba.append("LD".getBytes());
					ba.append(DataUtil.get2ByteToInt(lpd.length));
					ba.append(lpd);
					
					//EL
					ba.append("EL".getBytes());
					ba.append(DataUtil.get2ByteToInt(eld.length));
					ba.append(eld);
					
					//IS
					ba.append("IS".getBytes());
					ba.append(DataUtil.get2ByteToInt(ist.length));
					ba.append(ist);
					
					//PQ
					ba.append("PQ".getBytes());
					ba.append(DataUtil.get2ByteToInt(pqm.length));
					ba.append(pqm);
					
					break;
					

				default:
					throw new MRPException(MRPError.ERR_INVALID_PARAM,
							"Invalid parameters");
				}

			} else {
				throw new MRPException(MRPError.ERR_INVALID_PARAM,
						"Invalid parameters");
			}
			
			//ModemSerial
			String strModemSerial = smiValue[0].getVariable().toString();
			log.debug("modemserial :" + strModemSerial);
			OCTET ocModemSerial = new OCTET(8); // 생성시 크기가 고정되도록 생성한다.
			ocModemSerial.setValue(Hex.encode(strModemSerial));
			
			// meter id
			OCTET ocMeterId = new OCTET(20);
			ocMeterId.setIsFixed(true);
			byte[] bMeterid = fillCopy(this.meterId.getBytes(),(byte) 0x20,20);//빈공간은 space 로 채운다. 0x32
			
			ocMeterId.setValue(bMeterid);
			log.debug("meter id:" + bMeterid);

			meterLPEntry me = new meterLPEntry();
			
			me.setMlpId(ocModemSerial);
			me.setMlpMid(ocMeterId);
			baCnt=1;
			me.setMlpDataCount(new WORD(baCnt));
			me.setMlpServiceType(new BYTE(1)); // 1: electric
			me.setMlpType(new BYTE(CommonConstants.ModemType.Converter
					.getIntValue()));// 19:Converter
			me.setMlpVendor(new BYTE(CommonConstants.MeterVendor.ELSTER
					.getCode()[0].intValue()));// ELSTER

			// Measurement Data List
			ByteArray measData = new ByteArray();
			
			// Time stamp
			try {
				String nowTime = TimeUtil.getCurrentTime();
				measData.append(DataFormat.encodeTime(nowTime));
				byte[] encodeTime = DataFormat.encodeTime(nowTime);
				log.debug("Time Stamp : " + nowTime + " / Encode["
						+ Hex.getHexDump(encodeTime) + "]");
			} catch (Exception e) {
				e.printStackTrace();
				throw new MRPException(MRPError.ERR_HEADER_FORMAT_ERROR, e
						.toString());
			}
			
			measData.append(ba.toByteArray());
			
			/**
			 * Fixed가 True 가 아닐경우 Encode 할때 length Byte 가 붙는데 MeasurementData 는
			 * Fixed옵션을 무시하고 무조건 True일경우로 파싱하기때문에 True로 설정해준다.
			 * 
			 * @author kskim
			 * @see MeasurementData::decode()
			 */
			byte[] lpData = measData.toByteArray();
			
			OCTET octetLpData = new OCTET();
			octetLpData.setValue(lpData);
			me.setMlpData(octetLpData);
			commandData = new CommandData();
			commandData.setCnt(new WORD(1));
			commandData.setTotalLength(ba.toByteArray().length);
			commandData.append(new SMIValue(new OID("10.1.0"), new OPAQUE(me)));
			return commandData;			

		}

		log.debug("==============A1830RLN_Handler end ================");
		return commandData;
	}

	@Override
	public void initProcess(IoSession session) throws MRPException {
		byte[] passwd = new byte[8];

		passwd = identRequest(session);
		negotiate(session);
		logon(session);
		authenticate(passwd, session);

		log.debug("인증 성공");

	}

	/**
	 * Identification service 요청을 보냄.
	 * 
	 * @param session
	 * @throws MRPException
	 */
	protected byte[] identRequest(IoSession session) throws MRPException {
		byte[] read = new byte[26];
		byte[] passwd = new byte[8];

		this.before_txd_control = 0x20;
		this.meter_length = 1;
		byte kind = 0;

		try {

			A1830RLN_RequestDataFrame frame = new A1830RLN_RequestDataFrame(
					this.before_txd_control, kind, ANSI.IDENT, 9,
					this.meter_length);

			IoBuffer buf = frame.getIoBuffer();
			this.before_txd_control = frame.getTXDControl();
			log.debug("IDENTIFICATION: " + buf.getHexDump());

			sendMessage(session, buf);

			// 메시지를 받는다.
			byte[] message = readMessage(session);

			log.debug("READ IDENT RESPONSE : " + Hex.getHexDump(message));

			for (int i = 0; i < 8; i++) {
				passwd[i] = message[8 + i];
			}

			log.debug("READ IDENT PassWd : " + Hex.getHexDump(passwd));
		} catch (Exception e) {
			log.warn(e, e);
			throw new MRPException(MRPError.ERR_HEADER_FORMAT_ERROR);
		}

		return passwd;
	}

	/**
	 * Identification service 요청에 대한 답을 받아 예외처리.
	 * 
	 * @param session
	 * @throws MRPException
	 */
	public byte[] identReceive(IoSession session) throws MRPException {
		try {

			byte[] message = readMessage(session);

			log.debug("READ IDENT RESPONSE : " + Hex.getHexDump(message));

			// receive프레임을 생성
			A1830RLN_ReceiveDataFrame receiveFrame = new A1830RLN_ReceiveDataFrame(
					message);

			// Ident 프레임을 읽어온다.
			// IdentTable revIdentTable = receiveFrame.getIdent();
			byte[] receiveByte = receiveFrame.getIdent();
			return receiveByte;
			/*
			 * // 요청이 성공인지 판단한다. switch (revIdentTable.getStatus()) { case
			 * ANSI.OK: // ACK 보낸다. sendMessage(session, ACK()); break; case
			 * ANSI.ISSS: throw new MRPException(MRPError.ERR_MODEMERROR,
			 * "Isss: Present state is not Base state"); case ANSI.BSY: throw
			 * new MRPException(MRPError.ERR_MODEMERROR,
			 * "Bsy: Internal processing"); case ANSI.ERR: throw new
			 * MRPException(MRPError.ERR_MODEMERROR, "Err: Unused"); default:
			 * throw new MRPException(MRPError.ERR_MODEMERROR,
			 * "Err: Header format error"); }
			 */
		} catch (MRPException e) {
			log.warn(e, e);
			throw new MRPException(e.getType());
		} catch (Exception e) {
			log.warn(e, e);
			throw new MRPException(MRPError.ERR_READ_METER_CLASS,
					"Data receive error");
		}
	}

	/**
	 * Negotiate service 요청.
	 * 
	 * @param session
	 * @throws MRPException
	 */
	protected void negotiate(IoSession session) throws MRPException {

		// byte read[] = new byte[14];
		this.meter_length = 4;

		byte[] negotiate = { (byte) 0x04, (byte) 0x00, (byte) 0x80 };
		byte kind = 1;

		try {
			A1830RLN_RequestDataFrame frame = new A1830RLN_RequestDataFrame(
					this.before_txd_control, kind, ANSI.NEGO, new OCTET(
							negotiate), 13, this.meter_length);

			IoBuffer buf = frame.getIoBuffer();
			this.before_txd_control = frame.getTXDControl();
			log.debug("NEGOTIATE: " + buf.getHexDump());

			sendMessage(session, buf);
			byte[] read = readMessage(session);

			log.debug("READ negotiate : " + Hex.getHexDump(read));

			// negotiateReceive(session);
		} catch (Exception e) {
			log.warn(e.getMessage());
			throw new MRPException(MRPError.ERR_HEADER_FORMAT_ERROR);
		}

	}

	/**
	 * Negotiate service 요청에 대한 답을 받아 예외처리.
	 * 
	 * @param session
	 * @throws MRPException
	 */
	protected void negotiateReceive(IoSession session) throws MRPException {

		try {
			byte[] read = readMessage(session);

			// receive프레임을 생성
			A1830RLN_ReceiveDataFrame receiveFrame = new A1830RLN_ReceiveDataFrame(
					read);

			NegotiateTable negotiateTable = receiveFrame.getNegotiate();

			// 요청이 성공인지 판단한다.
			switch (negotiateTable.getStatus()) {
			case ANSI.OK:
				// ACK 보낸다.
				sendMessage(session, ACK());
				break;
			case ANSI.SNS:
				throw new MRPException(MRPError.ERR_MODEMERROR,
						"sns: Request code not match any meter service (after check all meter service)");
			case ANSI.ISSS:
				throw new MRPException(MRPError.ERR_MODEMERROR,
						"Isss: Present state is not Base state");
			case ANSI.BSY:
				throw new MRPException(MRPError.ERR_MODEMERROR,
						"Bsy: Internal processing");
			case ANSI.ERR:
				throw new MRPException(
						MRPError.ERR_MODEMERROR,
						"Err: packet-size is not 128 or nbr-packets is not 0x01 or baud-rate is not 0x06(0x08)");
			default:
				throw new MRPException(MRPError.ERR_MODEMERROR,
						"Err: Header format error");
			}

		} catch (MRPException e) {
			log.warn(e, e);
			throw new MRPException(e.getType());
		} catch (Exception e) {
			log.warn(e, e);
			throw new MRPException(MRPError.ERR_READ_METER_CLASS,
					"Data receive error");
		}

	}

	/**
	 * Logon service 요청
	 * 
	 * @param session
	 * @throws MRPException
	 */
	protected void logon(IoSession session) throws MRPException {

		byte read[] = new byte[10];
		this.meter_length = 13;
		byte[] loginid = { 0x00, 0x02, 'A', 'd', 'm', 'i', 'n', 'i', 's', 't',
				'r', 'a' };
		byte kind = 1;

		try {
			A1830RLN_RequestDataFrame frame = new A1830RLN_RequestDataFrame(
					this.before_txd_control, kind, ANSI.LOGON, new OCTET(
							loginid), 22, this.meter_length);

			IoBuffer buf = frame.getIoBuffer();
			this.before_txd_control = frame.getTXDControl();
			log.debug("LOGIN: " + buf.getHexDump());
			sendMessage(session, buf);
			logonReceive(session);
		} catch (Exception e) {
			log.warn(e.getMessage());
			throw new MRPException(MRPError.ERR_HEADER_FORMAT_ERROR);
		}
	}

	/**
	 * Logon service 요청에 대한 답을 받아 예외처리.
	 * 
	 * @param session
	 * @throws MRPException
	 */
	protected void logonReceive(IoSession session) throws MRPException {

		try {
			byte[] dataFrame = readMessage(session);

			// 요청이 성공인지 판단한다.
			switch (dataFrame[0]) {
			case ANSI.OK:
				// ACK 보낸다.
				sendMessage(session, ACK());
				break;
			case ANSI.IAR:
				throw new MRPException(MRPError.ERR_MODEMERROR, "iar: Unused");
			case ANSI.ISSS:
				throw new MRPException(MRPError.ERR_MODEMERROR,
						"Isss: Present state is not Base state");
			case ANSI.BSY:
				throw new MRPException(MRPError.ERR_MODEMERROR,
						"Bsy: Internal processing");
			case ANSI.ERR:
				throw new MRPException(MRPError.ERR_MODEMERROR,
						"Err: Invalid User ID or Name");
			default:
				throw new MRPException(MRPError.ERR_MODEMERROR,
						"Err: Header format error");
			}

		} catch (MRPException e) {
			log.warn(e, e);
			throw new MRPException(e.getType());
		} catch (Exception e) {
			log.warn(e, e);
			throw new MRPException(MRPError.ERR_READ_METER_CLASS,
					"Data receive error");
		}

	}

	public void authenticate(byte[] meter_pwd, IoSession session)
			throws MRPException {

		byte[] read = new byte[20];
		this.meter_length = 11;
		byte kind = 1;

		byte[] auth_codeHead = { 0x09, 0x00 };
		byte[] auth_codePass = encrypt_password(meter_pwd);

		byte[] auth_code = new byte[auth_codeHead.length + auth_codePass.length];
		System.arraycopy(auth_codeHead, 0, auth_code, 0, auth_codeHead.length);
		System.arraycopy(auth_codePass, 0, auth_code, auth_codeHead.length,
				auth_codePass.length);
		log.debug("auth_code : " + Hex.getHexDump(auth_code) + " ,length: "
				+ auth_code.length);
		try {
			A1830RLN_RequestDataFrame frame = new A1830RLN_RequestDataFrame(
					this.before_txd_control, kind, ANSI.AUTH, new OCTET(
							auth_code), 20, this.meter_length);
			IoBuffer buf = frame.getIoBuffer();
			this.before_txd_control = frame.getTXDControl();
			log.debug("AUTUENTICATE: " + buf.getHexDump());

			sendMessage(session, buf);
			byte[] dataFrame = readMessage(session);
			log.debug("Receive Authenticate: " + Hex.getHexDump(dataFrame));

			// 요청이 성공인지 판단한다.
			switch (dataFrame[0]) {
			case ANSI.OK:
				// ACK 보낸다.
				sendMessage(session, ACK());
				break;
			case ANSI.IAR:
				throw new MRPException(MRPError.ERR_MODEMERROR, "iar: Unused");
			case ANSI.ISSS:
				throw new MRPException(MRPError.ERR_MODEMERROR,
						"Isss: Present state is not Base state");
			case ANSI.BSY:
				throw new MRPException(MRPError.ERR_MODEMERROR,
						"Bsy: Internal processing");
			case ANSI.ERR:
				throw new MRPException(MRPError.ERR_MODEMERROR,
						"Err: Invalid User ID or Name");
			default:
				throw new MRPException(MRPError.ERR_MODEMERROR,
						"Err: Header format error");
			}

		} catch (MRPException e) {
			log.warn(e, e);
			throw new MRPException(e.getType());
		} catch (Exception e) {
			log.warn(e, e);
			throw new MRPException(MRPError.ERR_READ_METER_CLASS,
					"Data receive error");
		}
	}

	// 암호화 과정 시작
	
	/**
	 * Elester PassWord Encrypt
	 * @param meter_password,  Identification 요청시 리턴되는 패킷을 암호화 한다.
	 * @return
	 */
	protected byte[] encrypt_password(byte[] meter_password) {

		int i;
		byte[] ticket = new byte[64];
		byte[] key_data = new byte[64];
		byte[] pwd = new byte[8];
		byte[] e_key = { 0x31, 0x32, 0x33, 0x34, 0x32, 0x30, 0x30, 0x34 };// 12342004

		for (i = 0; i < 8; i++) ticket[0 + i] = (byte) ((meter_password[0] >> (7 - i)) & 0x01);
		for (i = 0; i < 8; i++) ticket[8 + i] = (byte) ((meter_password[1] >> (7 - i)) & 0x01);
		for (i = 0; i < 8; i++) ticket[16 + i] = (byte) ((meter_password[2] >> (7 - i)) & 0x01);
		for (i = 0; i < 8; i++) ticket[24 + i] = (byte) ((meter_password[3] >> (7 - i)) & 0x01);
		for (i = 0; i < 8; i++) ticket[32 + i] = (byte) ((meter_password[4] >> (7 - i)) & 0x01);
		for (i = 0; i < 8; i++) ticket[40 + i] = (byte) ((meter_password[5] >> (7 - i)) & 0x01);
		for (i = 0; i < 8; i++) ticket[48 + i] = (byte) ((meter_password[6] >> (7 - i)) & 0x01);
		for (i = 0; i < 8; i++) ticket[56 + i] = (byte) ((meter_password[7] >> (7 - i)) & 0x01);

		for (i = 0; i < 8; i++) key_data[0 + i] = (byte) ((e_key[0] >> (7 - i)) & 0x01);
		for (i = 0; i < 8; i++) key_data[8 + i] = (byte) ((e_key[1] >> (7 - i)) & 0x01);
		for (i = 0; i < 8; i++) key_data[16 + i] = (byte) ((e_key[2] >> (7 - i)) & 0x01);
		for (i = 0; i < 8; i++) key_data[24 + i] = (byte) ((e_key[3] >> (7 - i)) & 0x01);
		for (i = 0; i < 8; i++) key_data[32 + i] = (byte) ((e_key[4] >> (7 - i)) & 0x01);
		for (i = 0; i < 8; i++) key_data[40 + i] = (byte) ((e_key[5] >> (7 - i)) & 0x01);
		for (i = 0; i < 8; i++) key_data[48 + i] = (byte) ((e_key[6] >> (7 - i)) & 0x01);
		for (i = 0; i < 8; i++) key_data[56 + i] = (byte) ((e_key[7] >> (7 - i)) & 0x01);

		des(key_data, ticket, 1);

		for (i = 0; i < 8; i++) pwd[0] |= ((ticket[i + 0] & 0x01) << (7 - i));
		for (i = 0; i < 8; i++) pwd[1] |= ((ticket[i + 8] & 0x01) << (7 - i));
		for (i = 0; i < 8; i++) pwd[2] |= ((ticket[i + 16] & 0x01) << (7 - i));
		for (i = 0; i < 8; i++) pwd[3] |= ((ticket[i + 24] & 0x01) << (7 - i));
		for (i = 0; i < 8; i++) pwd[4] |= ((ticket[i + 32] & 0x01) << (7 - i));
		for (i = 0; i < 8; i++) pwd[5] |= ((ticket[i + 40] & 0x01) << (7 - i));
		for (i = 0; i < 8; i++) pwd[6] |= ((ticket[i + 48] & 0x01) << (7 - i));
		for (i = 0; i < 8; i++) pwd[7] |= ((ticket[i + 56] & 0x01) << (7 - i));

		return pwd;
	}

	protected void des(byte[] key_data, byte[] ticket, int _encrypt) {

		byte[] des_key = new byte[64];
		byte[] des_data = new byte[64];
		byte[] des_right = new byte[64];

		int i, j;

		permutation(des_key, key_data, 0, 56, ANSI.PERM1);

		byte[] NULL = null;

		for (i = 1; i <= 16; i++) {

			permutation(des_key, NULL, 0, 56, ANSI.PERM2);

			if ((i != 1) && (i != 2) && (i != 9) && (i != 16))
				permutation(des_key, NULL, 0, 56, ANSI.PERM2);

			int encrypt = 0;
			if (_encrypt != 0)
				encrypt = i - 1;
			else
				encrypt = 16 - i;

			permutation(keys[encrypt], des_key, 0, 48, ANSI.PERM3);
		}

		permutation(des_data, ticket, 0, 64, ANSI.PERM4);

		for (i = 1; i <= 16; i++) {

			permutation(des_right, des_data, 32, 48, ANSI.PERM5);

			xor(des_right, keys[i - 1], 48);

			for (j = 0; j < 8; j++)
				sboxes(des_right, 4 * j, des_right, 6 * j, ANSI.SBOXES[j]);

			permutation(des_right, NULL, 0, 32, ANSI.PERM6);
			xor(des_right, des_data, 32);
			copy(des_data, 0, des_data, 32, 32);
			copy(des_data, 32, des_right, 0, 32);
		}
		copy(ticket, 0, des_data, 32, 32);
		copy(ticket, 32, des_data, 0, 32);
		permutation(ticket, NULL, 0, 64, ANSI.PERM7);

	}

	protected void xor(byte[] dst, byte[] src, int len) {

		for (int i = 0; i < len; i++)
			dst[i] ^= src[i];
	}

	protected void sboxes(byte[] dst, int dstart, byte[] src, int sstart,
			byte[] sbox) {

		int i;

		i = src[sstart + 4];
		i |= src[sstart + 3] << 1;
		i |= src[sstart + 2] << 2;
		i |= src[sstart + 1] << 3;
		i |= src[sstart + 5] << 4;
		i |= src[sstart + 0] << 5;

		i = sbox[i];

		dst[dstart + 3] = (byte) (i & 1);
		dst[dstart + 2] = (byte) (i >> 1 & 1);
		dst[dstart + 1] = (byte) (i >> 2 & 1);
		dst[dstart + 0] = (byte) (i >> 3 & 1);

	}

	protected void copy(byte[] dst, int dstart, byte[] src, int sstart, int len) {

		for (int i = 0; i < len; i++)
			dst[dstart + i] = src[sstart + i];
	}

	protected void permutation(byte[] dst, byte[] src, int sstart, int len,
			byte[] perm_table) {

		byte[] tmp = new byte[64];

		if (src == null) {
			src = tmp;
			System.arraycopy(dst, 0, src, sstart, 64);
		}

		for (int i = 0; i < len; i++) {
			dst[i] = src[sstart + perm_table[i] - 1];
		}

	}

	// 암호화 과정 끝
	
	@Override
	public ByteArray configureRead(IoSession session) throws MRPException {
		log.debug("========== Configure Read Start ===============");
		ByteArray ba = new ByteArray();

		try {

			byte[] st05 = classRead(session, ANSI.READ_ST_05);
			byte[] st55 = classRead(session, ANSI.READ_ST_55);
			byte[] st15 = classRead(session, ANSI.READ_ST_15);
			byte[] mt15 = classRead(session, A1830RLN_DataConstants.READ_MT_15);
			byte[] mt51 = classRead(session, A1830RLN_DataConstants.READ_MT_51);
			byte[] mt16 = classRead(session, A1830RLN_DataConstants.READ_MT_16);
			byte[] st03 = classRead(session, ANSI.READ_ST_03);

			ST55 meterTime = new ST55(st55);
			byte[] year = meterTime.parseDateTime();					// 7byte

			byte zero = 0x00;

			log.debug("st05=" + new OCTET(st05).toHexString());
			log.debug("st55=" + new OCTET(st55).toHexString());
			log.debug("st15=" + new OCTET(st15).toHexString());
			log.debug("mt15=" + new OCTET(mt15).toHexString());
			log.debug("mt51=" + new OCTET(mt51).toHexString());
			log.debug("mt16=" + new OCTET(mt16).toHexString());
			log.debug("st03=" + new OCTET(st03).toHexString());

			ba.append("Elster-A1830".getBytes()); 						// 12Byte Meter Model
			byte[] meterid = DataUtil.arrayAppend(st05, 12, 8, st05, 0, 0);
			this.meterId = DataFormat.getString(meterid);
			ba.append(meterid); 										// meter Serial 08Byte
			ba.append(year); 											// meter Time 07Byte
			log.debug(Hex.getHexDump(year));
			ba.append(DataUtil.arrayAppend(mt16, 0, 1, mt16, 0, 0)); 	// Instrument 01Byte
			ba.append(DataUtil.arrayAppend(st15, 25, 6, st15, 0, 0)); 	// Meter Constant 06Byte
			ba.append(DataUtil.arrayAppend(mt15, 5, 1, mt15, 0, 0)); 	// Meter Constant Scale 01Byte
			ba.append(DataUtil.arrayAppend(st15, 38, 6, st15, 44, 6)); 	// CT VT Ratio 12Byte
			ba.append(DataUtil.arrayAppend(mt51, 5, 2, mt51, 0, 0)); 	// Meter Elements 02Byte
			ba.append(DataUtil.arrayAppend(st03, 1, 2, st03, 4, 13)); 	// Meter Status 15Byte
			log.debug(Hex.getHexDump(ba.toByteArray()));
		} catch (Exception e) {
			log.error(e, e);
		}

		log.debug("========== Configure Read End ===============\n");
		return ba;
	}

	@Override
	public ByteArray billRead(IoSession session) throws MRPException {

		log.debug("========== Prev Bill Read Start ===============");
		ByteArray ba = new ByteArray();

		byte[] st21 = classRead(session, ANSI.READ_ST_21);
		log.debug("st21=" + Hex.getHexDump(st21));

		byte[] st25 = classRead(session, ANSI.READ_ST_25);
		log.debug("st25=" + Hex.getHexDump(st25));

		ba.append(st21);
		ba.append(st25);

		log.debug("========== Prev Bill Read End ===============");
		return ba;
	}

	@Override
	public ByteArray lpRead(IoSession session, String oset, String cnt,
			int lpCycle) throws MRPException {

		byte[] NBR_BLKS_SET1 = new byte[2];
		byte[] NBR_BLKS_INTS_SET1 = new byte[2];
		byte[] INT_TIME_SET1 = new byte[1];
		byte[] NBR_CHNS_SET1 = new byte[1];

		log.debug("========== LoadProfile Read Start ===============");
		ByteArray ba = new ByteArray();

		byte[] st61 = classRead(session, ANSI.READ_ST_61);// lp configuration

		System.arraycopy(st61, 7, NBR_BLKS_SET1, 0, 2);
		System.arraycopy(st61, 9, NBR_BLKS_INTS_SET1, 0, 2);
		System.arraycopy(st61, 12, INT_TIME_SET1, 0, 1);
		System.arraycopy(st61, 11, NBR_CHNS_SET1, 0, 1);

		int INT_NBR_CHNS_SET1 		= DataUtil.getIntToBytes(NBR_CHNS_SET1);
		int INT_NBR_BLKS_INTS_SET1 	= 0;
		int INT_NBR_BLKS_SET1 		= 0;

		try {
			INT_NBR_BLKS_SET1 		= DataFormat.getIntTo2Byte( DataFormat.LSB2MSB(NBR_BLKS_SET1) );
			INT_NBR_BLKS_INTS_SET1 	= DataFormat.getIntTo2Byte( DataFormat.LSB2MSB(NBR_BLKS_INTS_SET1) );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int st62PreDataSize 	= 3 * INT_NBR_CHNS_SET1 + 1;
		int st62DataSize 		= INT_NBR_CHNS_SET1 * 4;
		int st64OneBlockSize 	= (INT_NBR_BLKS_INTS_SET1 + 7) / 8 + INT_NBR_BLKS_INTS_SET1 * (1 + INT_NBR_CHNS_SET1 * 5 / 2) + 5;

		byte[] s62 	= classRead(session, ANSI.READ_ST_62);
		byte[] st62 = new byte[st62DataSize];

		// st62테이블 파싱
		try {
			ByteArrayInputStream bis2 = new ByteArrayInputStream(s62);
			bis2.skip(st62PreDataSize);
			bis2.read(st62);
			bis2.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 온디맨드 날짜계산
		byte[] s64 		= classPartialRead(session, ANSI.READ_ST_64, (char) 0, (char) (st64OneBlockSize * 2));
		byte[] time 	= new byte[1];
		byte[] time1 	= new byte[5];
		byte[] time2 	= new byte[5];

		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(s64);
			bis.read(time1);
			bis.skip(st64OneBlockSize - 5);
			bis.read(time2); // 두번째 블럭 마지막 시각
			bis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.debug(e,e);
		} // 첫번째 블럭 마지막 시각

		System.arraycopy(time2, 3, time, 0, 1);
		int blk2EndTime = DataFormat.getIntToByte(time[0]);
		int preOffset = blk2EndTime / 4; // 전날 오프셋계산
		int offset = 0;
		if (Integer.parseInt(oset) - Integer.parseInt(cnt) < 0) {
			offset = 0;
		} else {
			offset = ( Integer.parseInt(oset) - Integer.parseInt(cnt) ) * 6
					+ preOffset + 1; // 
		}
		// 온디맨드 날짜계산 끝
		log.debug("#############################################################");
		log.debug("#############################################################");
		log.debug("**********      oset : "+oset+", cnt : "+cnt+"         ********************");
		log.debug("#############################################################");
		log.debug("#############################################################");
		
		byte[] st64 = classPartialRead(session, ANSI.READ_ST_64,
				(char) (offset * st64OneBlockSize),
				(char) (st64OneBlockSize * 7 * Integer.parseInt(cnt)));

		// return 값 저장
		ba.append(NBR_BLKS_SET1); 		// 2byte
		ba.append(NBR_BLKS_INTS_SET1); 	// 2byte
		ba.append(INT_TIME_SET1); 		// 1byte
		ba.append(NBR_CHNS_SET1); 		// 1byte

		ba.append(st62);
		ba.append(st64);
		log.debug("hexba :" + Hex.getHexDump(ba.toByteArray()));
		log.debug("========== LoadProfile Read End ===============");

		return ba;
	}

	@Override
	public ByteArray eventlogRead(IoSession session) throws MRPException {

		log.debug("========== Event Log Read Start ===============");
		ByteArray ba = new ByteArray();
		byte[] st76 = classRead(session, ANSI.READ_ST_76);
		ba.append(st76);
		log.debug("========== Event Log Read End ===============");
		return ba;
	}

	@Override
	public ByteArray instRead(IoSession session) throws MRPException {

		log.debug("========== Instrument Read Start ===============");
		byte[] NBR_CHNS_SET1 = new byte[1];
		byte[] NBR_CHNS_SET2 = new byte[1];
		byte[] Nbr_Blk_Ints_Set1 = new byte[2];
		byte[] Nbr_Blk_Ints_Set2 = new byte[2];
		byte[] Nbr_Blks_Set1 = new byte[2];
		byte[] Nbr_Blks_Set2 = new byte[2];
		byte[] mt64Head = new byte[8];
		byte[] mt65Head = new byte[8];

		ByteArray ba = new ByteArray();

		byte[] mt61 = classRead(session, A1830RLN_DataConstants.READ_MT_61);
		byte[] mt62 = classRead(session, A1830RLN_DataConstants.READ_MT_62);
		byte[] mt63 = classRead(session, A1830RLN_DataConstants.READ_MT_63);

		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(mt61);

			bis.skip(7);
			bis.read(Nbr_Blks_Set1);
			bis.read(Nbr_Blk_Ints_Set1);
			bis.read(NBR_CHNS_SET1);
			bis.skip(1);
			bis.read(Nbr_Blks_Set2);
			bis.read(Nbr_Blk_Ints_Set2);
			bis.read(NBR_CHNS_SET2);

			bis.close();

		} catch (IOException e) {
			e.printStackTrace();
			log.debug(e,e);
		}

		int INT_NBR_CHNS_SET1 = DataUtil.getIntToBytes(NBR_CHNS_SET1);
		int INT_NBR_CHNS_SET2 = DataUtil.getIntToBytes(NBR_CHNS_SET2);
		int INT_Nbr_Blk_Ints_Set1 = 0;
		int INT_Nbr_Blk_Ints_Set2 = 0;
		int INT_Nbr_Blks_Set1 = 0;
		int INT_Nbr_Blks_Set2 = 0;

		try {
			INT_Nbr_Blk_Ints_Set1 = DataFormat.getIntTo2Byte(DataFormat.LSB2MSB(Nbr_Blk_Ints_Set1));
			INT_Nbr_Blk_Ints_Set2 = DataFormat.getIntTo2Byte(DataFormat.LSB2MSB(Nbr_Blk_Ints_Set2));

			INT_Nbr_Blks_Set1 = DataFormat.getIntTo2Byte(DataFormat.LSB2MSB(Nbr_Blks_Set1));
			INT_Nbr_Blks_Set2 = DataFormat.getIntTo2Byte(DataFormat.LSB2MSB(Nbr_Blks_Set2));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int oneblocksize1 = (5 + (INT_Nbr_Blk_Ints_Set1 + 7) / 8 + INT_Nbr_Blk_Ints_Set1 * (1 + INT_NBR_CHNS_SET1 * 5 / 2));
		int oneblocksize2 = (5 + (INT_Nbr_Blk_Ints_Set2 + 7) / 8 + INT_Nbr_Blk_Ints_Set2 * (1 + INT_NBR_CHNS_SET2 * 5 / 2));

		// byte[] tempMt61 = new byte[12];
		// //mt61테이블 파싱.
		// System.arraycopy(mt61, 7, tempMt61, 0, 12);

		int offset 		= INT_NBR_CHNS_SET1 * 3 + 1;
		byte[] tempMt62 = new byte[INT_NBR_CHNS_SET1 * 4 + INT_NBR_CHNS_SET2 * 4];

		// 여러게 얻기...
		// mt62테이블 파싱. set1,2 Scalars, set1,2 Divisors
		System.arraycopy(mt62, offset, tempMt62, 0, INT_NBR_CHNS_SET1 * 4);
		System.arraycopy(mt62, offset + INT_NBR_CHNS_SET1 * 4
				+ INT_NBR_CHNS_SET2 * 3 + 1, tempMt62, INT_NBR_CHNS_SET1 * 4,
				INT_NBR_CHNS_SET2 * 4);

		// 64,65테이블 앞부분 패킷.
		System.arraycopy(mt61, 7, mt64Head, 0, 6);
		System.arraycopy(mt63, 11, mt64Head, 6, 2); // NBR_VALID_INT_SET1 (mt63)
		System.arraycopy(mt61, 13, mt65Head, 0, 6);
		System.arraycopy(mt63, 24, mt65Head, 6, 2); // NBR_VALID_INT_SET1 (mt63)

		byte[] mt64 = classPartialRead(session, A1830RLN_DataConstants.READ_MT_64, (char) 0, (char) (oneblocksize1 * 6));// oneblocksize1*INT_Nbr_Blks_Set1));
		byte[] mt65 = classPartialRead(session, A1830RLN_DataConstants.READ_MT_65, (char) 0, (char) (oneblocksize2 * 6));// oneblocksize2*INT_Nbr_Blks_Set2));

		ba.append(mt61);
		ba.append(tempMt62);

		ba.append(DataUtil.arrayAppend(mt64Head, 0, 8, mt64, 0, mt64.length));
		ba.append(DataUtil.arrayAppend(mt65Head, 0, 8, mt65, 0, mt65.length));

		log.debug("========== Instrument Read End ===============");
		return ba;
	}

	@Override
	public ByteArray currbillRead(IoSession session) throws MRPException {

		log.debug("========== Current Bill Read Start ===============");
		ByteArray ba 	= new ByteArray();
		byte[] st21 	= classRead(session, A1830RLN_DataConstants.READ_ST_21);
		byte[] st23 	= classRead(session, A1830RLN_DataConstants.READ_ST_23);
		ba.append(st21);
		ba.append(st23);
		log.debug("========== Current Bill Read End ===============");
		return ba;
	}

	@Override
	public ByteArray pqRead(IoSession session) throws MRPException {

		log.debug("========== PQM Read Start ===============");
		ByteArray ba = new ByteArray();

		int INT_NUM_SAG_LOG_ENTRIES = 0;
		byte[] CONFIG_BFLD = new byte[1];
		byte[] SAG_MINIMUM_TIME = new byte[1];
		byte[] SAG_MAXIMUM_TIME = new byte[2];
		byte[] NUM_SAG_LOG_ENTRIES = new byte[2];

		byte[] mt50 = classRead(session, A1830RLN_DataConstants.READ_MT_50);
		byte[] mt52 = classRead(session, A1830RLN_DataConstants.READ_MT_52);
		byte[] mt54 = classRead(session, A1830RLN_DataConstants.READ_MT_54);

		// MT_52 테이블 파싱.
		ByteArrayInputStream bis = new ByteArrayInputStream(mt52);

		try {
			bis.read(CONFIG_BFLD);
			bis.read(SAG_MINIMUM_TIME);
			bis.read(SAG_MAXIMUM_TIME);
			bis.read(NUM_SAG_LOG_ENTRIES);
			bis.close();
			INT_NUM_SAG_LOG_ENTRIES = DataFormat.getIntTo2Byte(
										DataFormat.LSB2MSB(NUM_SAG_LOG_ENTRIES)	);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.debug(e,e);
		}

		byte[] mt53 = classPartialRead(session,
				A1830RLN_DataConstants.READ_MT_53, (char) 0,
				(char) (9 + 7 * INT_NUM_SAG_LOG_ENTRIES));

		ba.append(mt50);
		ba.append(mt54);
		ba.append(mt53);

		log.debug("========== PQM Read End ===============");
		return ba;
	}

	
	/**
	 * @param session
	 * @param control
	 * @param datetime
	 * @return
	 * @throws Exception
	 */
	public HashMap meterTimeSync(IoSession session, byte control, byte[] datetime) 
    throws Exception 
    {
		HashMap<String, Object> map = new HashMap<String, Object>();
		
        byte[] tableid 	= new byte[2];
        byte kind 		= 1;
        tableid[0] 		= (byte)(ANSI.READ_ST_07 >> 8);
        tableid[1] 		= (byte)ANSI.READ_ST_07;
        
        /* - A3-Tables-Jan31-01.PDF -
         * ST07  
         * PROC 		2BYTE
         * SEQ_NBR		1BYTE
         * PARAMETER 	8BYTE (Set date and/or time, Proc ID: 10)
         * 
         * ST08
         * PROC			2BYTE
         * SEQ_NBR		1BYTE
         * RESULT_CODE	1BYTE
         * RESP_DATA	12BYTE
         * 
         */
        byte[] proc_id 	= new byte[]{(byte)0x0A,0x00};//Program Control
        byte seq 		= 0x00;
        byte setMask 	= (byte)0x07;
        
        this.meter_length = 17;

        byte[] msg = new byte[15];

        int idx = 0;
        msg[idx++] = tableid[0];//table id
        msg[idx++] = tableid[1];//table id
        msg[idx++] = 0x00;//len
        msg[idx++] = 0x0B;//len
        
        msg[idx++] = proc_id[0];
        msg[idx++] = proc_id[1];
        //SEQ_NBR
        msg[idx++] = seq;
        //PARAMETER_DATA = 8 bytes
        msg[idx++] = setMask;
        msg[idx++] = datetime[0];
        msg[idx++] = datetime[1];
        msg[idx++] = datetime[2];
        msg[idx++] = datetime[3];
        msg[idx++] = datetime[4];
        msg[idx++] = datetime[5];
        msg[idx++] = (byte)0x60;
        log.debug(Hex.getHexDump(msg));
        A1830RLN_RequestDataFrame frame 
            = new A1830RLN_RequestDataFrame(this.before_txd_control,kind,ANSI.WRITE,new OCTET(msg),4,26,true,this.meter_length);

        IoBuffer buf = frame.getIoBuffer();
        this.before_txd_control = frame.getTXDControl();
        log.debug("Send\n "+buf.getHexDump());
        sendMessage(session, buf);
        //결과값 리턴
        byte[] message = readMessage(session);

		log.debug("READ IDENT RESPONSE : " + Hex.getHexDump(message));
//        session.write(buf);
        try{ Thread.sleep(500); }catch(InterruptedException e){}
        

        /*변경 전후 값 
         * RESP_DATA = 12 bytes
				bytes 1-6: OLD_TIME
				UINT8 YY,MM,DD,HH,MM,SS
				bytes 7-12: NEW_TIME
				UINT8 YY,MM,DD,HH,MM,SS
         */
        byte[] st08 = classRead(session, ANSI.READ_ST_08);
        byte[] oldTime = new byte[6];
        byte[] newTime = new byte[6];
        
        ByteArrayInputStream bis = new ByteArrayInputStream(st08);  
        bis.skip(4);//PROC(2), SEQ_NBR(1), RESULT_CODE(1)
        bis.read(oldTime);
        bis.read(newTime);
        bis.close();
        
        map.put("beforeTime", new OCTET(oldTime));
        map.put("afterTime", new OCTET(newTime));
        
        return map;

    }
	
	/**
	 * Get Receive Class Read Data.
	 * <p>
	 * 
	 * @return
	 */
	protected byte[] read(IoSession session, A1830RLN_RequestDataFrame frame,
			int size, byte meter_cmd) throws MRPException {

		byte[] read = null;
		try {
			read = (byte[]) handler.getMsg(session, 15, size);// 13
			// log.debug("READ: "+Util.getHexString(read));
			if (!check_data_frame(read, size, meter_cmd)) {
				throw new MRPException(MRPError.ERR_READ_METER_CLASS,
						"Data receive error");
			}

		} catch (MRPException e) {
			throw new MRPException(e.getType());
		} catch (Exception e) {
			throw new MRPException(MRPError.ERR_READ_METER_CLASS,
					"Data receive error");
		}
		return read;
	}

	protected byte[] read(IoSession session, byte meter_cmd)
			throws MRPException {

		byte[] read = null;
		long timeout = 15;

		try {

			int i = 0;
			int idx = 0;
			int headerlen = 0;
			int ofs_len = 0;

			ofs_len = ANSI.OFS_LENGTH;
			read = (byte[]) handler.getMessage(session, ofs_len,
					MeterModel.ELSTER_A1830RLNQ.getCode().intValue());

			if (!check_data_frame(read, read.length, meter_cmd))
				throw new MRPException(MRPError.ERR_READ_METER_CLASS,
						"Data receive error");
		} catch (MRPException e) {
			log.warn(e, e);
			throw new MRPException(e.getType());
		} catch (Exception e) {
			log.warn(e, e);
			throw new MRPException(MRPError.ERR_READ_METER_CLASS,
					"Data receive error");
		}

		return read;
	}

	protected byte[] read(IoSession session, byte meter_cmd, boolean rr)
			throws MRPException {

		byte[] read = null;
		long timeout = 60;

		try {

			int i = 0;
			int idx = 0;
			int headerlen = 0;
			int ofs_len = 0;

			if (rr) {
				headerlen = ANSI.LEN_HEADER - 1;
				ofs_len = ANSI.OFS_LENGTH - 1;
			} else {
				headerlen = ANSI.LEN_HEADER;
				ofs_len = ANSI.OFS_LENGTH;
			}

			byte[] temp = new byte[headerlen];

			read = (byte[]) handler.getMsg(session, timeout, headerlen);

			/*
			 * int rlen = DataUtil.getIntTo2Byte(
			 * DataUtil.select(temp,ofs_len,2)); read = new
			 * byte[headerlen+rlen+ANSI.LEN_CRC];
			 * 
			 * if(temp.length < headerlen+rlen+ANSI.LEN_CRC){
			 * log.debug("read more");
			 * System.arraycopy(temp,0,read,0,temp.length); idx = temp.length;
			 * 
			 * i = 0;
			 * 
			 * temp = (byte[])handler.getMsg(session,timeout,rlen+ANSI.LEN_CRC);
			 * System.arraycopy(temp,0,read,idx,temp.length); }else{
			 * log.debug("read done"); read = temp; }
			 */

			if (!check_data_frame(read, read.length, meter_cmd))
				throw new MRPException(MRPError.ERR_READ_METER_CLASS,
						"Data receive error");
		} catch (MRPException e) {
			log.warn(e, e);
			throw new MRPException(e.getType());
		} catch (Exception e) {
			log.warn(e, e);
			throw new MRPException(MRPError.ERR_READ_METER_CLASS,
					"Data receive error");
		}

		return read;
	}

	public byte[] classRead(IoSession session, char meter_read_class)
			throws MRPException {
		byte[] read = null;

		this.meter_length = 3;
		byte kind = 1;

		byte[] read_class = new byte[2];
		read_class[0] = (byte) (meter_read_class >> 8);
		read_class[1] = (byte) (meter_read_class);

		try {
			A1830RLN_RequestDataFrame frame = new A1830RLN_RequestDataFrame(
					this.before_txd_control, kind, ANSI.READ, new OCTET(
							read_class), 12, this.meter_length);
			IoBuffer buf = frame.getIoBuffer();
			this.before_txd_control = frame.getTXDControl();
			log.debug("CLASSREAD: [" + (int) meter_read_class + "]"
					+ buf.getHexDump());

			sendMessage(session, buf);

			read = readMessage(session);

			ByteArrayInputStream bis = new ByteArrayInputStream(read);

			bis.skip(1);// ok , nok

			// 데이터 길이가 있는 부분을 추출해서 길이를 구한다.
			byte[] dataLengthLayer = new byte[2];
			bis.read(dataLengthLayer);
			int dataLength = DataUtil.getIntTo2Byte(dataLengthLayer);

			// 데이터 부분을 구한다.
			byte[] dataLayer = new byte[dataLength];

			bis.read(dataLayer);

			// 데이터에서 채크섬을 가져온다.
			byte checksumLayer = (byte) bis.read();

			bis.close();

			// 구한 데이터로 채크섬 값을 구한다.
			byte calChecksum = ANSI.chkSum(dataLayer, 0, dataLayer.length);

			// 계산한 채크섬과 가져온 채크섬을 비교한다.
			if (checksumLayer != calChecksum) {
				throw new MRPException(MRPError.ERR_CRC_CHECK_ERROR,
						"Check Sum Error.");
			}

			return dataLayer;

		} catch (Exception e) {
			log.warn(e.getStackTrace());
			throw new MRPException(MRPError.ERR_READ_METER_CLASS,
					"Data receive error");
		}

	}

	public byte[] classPartialRead(IoSession session, char meter_read_class,
			char meter_count) throws MRPException {
		int defaultsize = 256;
		int defaultread = 0xE6 & 0xFF;

		byte[] read_data = new byte[defaultsize];

		int poffset = 0;
		int count = meter_count / defaultread;
		if (meter_count % defaultread != 0) {
			count += 1;
		}

		for (int i = 0; i < count; i++) {
			byte[] pkt = classPartialRead(session, meter_read_class,
					(char) poffset, (char) defaultread);

			int blen = read_data.length;
			int len = blen + pkt.length;

			byte[] obuf = new byte[blen];
			System.arraycopy(read_data, 0, obuf, 0, blen);
			read_data = new byte[len];
			System.arraycopy(obuf, 0, read_data, 0, blen);
			System.arraycopy(pkt, 0, read_data, blen, pkt.length);

			poffset += defaultread;
		}

		return read_data;
	}

	public byte[] classPartialRead(IoSession session, char meter_read_class,
			char poffset, char meter_count) throws MRPException {
		byte[] read_data = null;
		this.meter_length = 8;
		byte kind = 1;

		byte[] read_function = new byte[7];

		read_function[0] = (byte) (meter_read_class >> 8);
		read_function[1] = (byte) (meter_read_class);

		read_function[2] = 0x00;
		read_function[3] = (byte) (poffset >> 8);
		read_function[4] = (byte) (poffset);

		read_function[5] = (byte) (meter_count >> 8);// read data length
		read_function[6] = (byte) (meter_count);

		try {
			// 프레임 생성
			A1830RLN_RequestDataFrame frame = new A1830RLN_RequestDataFrame(
					this.before_txd_control, kind, ANSI.PREAD, new OCTET(
							read_function), 17, this.meter_length);
			// 버퍼 생성
			IoBuffer buf = frame.getIoBuffer();
			// 토글 비트 설정
			this.before_txd_control = frame.getTXDControl();
			log.debug("CLASSPARTIALREAD: [" + (int) meter_read_class + "]"
					+ buf.getHexDump());

			// 패킷 미터 전송
			session.write(buf);
			read_data = readMessage(session);

			ByteArrayInputStream bis = new ByteArrayInputStream(read_data);

			bis.skip(1);// ok , nok

			// 데이터 길이가 있는 부분을 추출해서 길이를 구한다.
			byte[] dataLengthLayer = new byte[2];
			bis.read(dataLengthLayer);
			int dataLength = DataUtil.getIntTo2Byte(dataLengthLayer);

			// 데이터 부분을 구한다.
			byte[] dataLayer = new byte[dataLength];

			bis.read(dataLayer);

			// 데이터에서 채크섬을 가져온다.
			byte checksumLayer = (byte) bis.read();

			bis.close();

			// 구한 데이터로 채크섬 값을 구한다.
			byte calChecksum = ANSI.chkSum(dataLayer, 0, dataLayer.length);

			// 계산한 채크섬과 가져온 채크섬을 비교한다.
			if (checksumLayer != calChecksum) {
				throw new MRPException(MRPError.ERR_CRC_CHECK_ERROR,
						"Check Sum Error.");
			}

			return dataLayer;

		} catch (Exception e) {
			log.warn(e.getMessage());
			throw new MRPException(MRPError.ERR_READ_METER_CLASS,
					"Data receive error");
		}

	}

	/**
	 * Continue Read
	 * 
	 * @param read
	 * @return
	 */
	public byte[] RR(IoSession session) throws Exception {

		session.write(new BYTE(ANSI.ACK));
		Thread.sleep(1000);
		byte[] temp = null;
		temp = read(session, ANSI.RR, true);
		return temp;
	}

	/**
	 * 메시지를 전송한다.
	 * 
	 * @param session
	 * @param buffer
	 * @throws MRPException
	 * @throws InterruptedException
	 */
	private void sendMessage(IoSession session, IoBuffer buffer)
			throws MRPException, InterruptedException {
		// read 명령 사용 설정.
		session.getConfig().setUseReadOperation(true);

		// 메시지 전송
		WriteFuture future = session.write(buffer);

		// 메시지가 전송 될때까지 기다림.
		future.awaitUninterruptibly(TIMEOUT * 1000);
		if (future.isWritten()) {
			// 전송 완료.
			buffer.free();
			Thread.sleep(200);
		} else {
			// 전송 안됨.
			throw new MRPException(MRPError.ERR_CONNECTTOMETER_CLASS);
		}

	}

	/**
	 * read 명령. table을 읽고, 전송받은 테이블 데이터를 객체로 만들어 ByteArray형태로 리턴된다.<br>
	 * 리턴받은 파서에서는 데이터를 다시 객체 형태로 변환한다.<br>
	 * 사실상 파싱은 객체 생성 시점에 모두끝난다.<br>
	 * 세이버에서는 객체별로 작업한다.
	 * 
	 * @param session
	 * @param tableid
	 *            byte(size 2)
	 * @param index
	 * @param count
	 * @return
	 * @throws MRPException
	 */
	public byte[] read(IoSession session, char tableid, int index, int count)
			throws MRPException {
		try {
			IoBuffer buf = A1830RLN_RequestDataFrame.getRead(tableid, index,
					count);
			log.debug("Read Service");
			sendMessage(session, buf);
			return readTableData(session);
		} catch (Exception e) {
			log.warn(e.getMessage());
			throw new MRPException(MRPError.ERR_HEADER_FORMAT_ERROR);
		}
	}

	/**
	 * 요청한 테이블의 데이터를 오류 검사하고 데이터 부분만 추출해서 반환한다.
	 * 
	 * @param allData
	 * @return
	 * @throws MRPException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	protected byte[] readTableData(IoSession session) throws MRPException,
			IOException, InterruptedException {
		byte[] readData = readMessage(session);

		// 요청이 성공인지 판단한다.
		switch (readData[0]) {
		case ANSI.OK:
			// ACK 보낸다.
			sendMessage(session, ACK());
			break;
		case ANSI.SNS:
			throw new MRPException(MRPError.ERR_MODEMERROR,
					"sns: Request code not match any meter service (after check all meter service)");
		case ANSI.ISSS:
			throw new MRPException(MRPError.ERR_MODEMERROR,
					"isss: Present state is not Session state");
		case ANSI.IAR:
			throw new MRPException(MRPError.ERR_MODEMERROR,
					"iar: Invalid Table ID");
		case ANSI.ISC:
			throw new MRPException(MRPError.ERR_MODEMERROR,
					"isc: Insufficient Access level");
		case ANSI.ONP:
			throw new MRPException(MRPError.ERR_MODEMERROR,
					"onp: Invalid Index or Invalid element-count");
		case ANSI.BSY:
			throw new MRPException(MRPError.ERR_MODEMERROR,
					"bsy: Internal processing");
		case ANSI.ERR:
			throw new MRPException(MRPError.ERR_MODEMERROR, "err: Unused");
		default:
			throw new MRPException(MRPError.ERR_MODEMERROR,
					"err: Header format error");
		}

		ByteArrayInputStream bis = new ByteArrayInputStream(readData);

		bis.skip(1);

		// 데이터 길이가 있는 부분을 추출해서 길이를 구한다.
		byte[] dataLengthLayer = new byte[2];

		bis.read(dataLengthLayer);

		int dataLength = DataUtil.getIntTo2Byte(dataLengthLayer);

		// 데이터 부분을 구한다.
		byte[] dataLayer = new byte[dataLength];

		bis.read(dataLayer);

		// 데이터에서 채크섬을 가져온다.
		byte checksumLayer = (byte) bis.read();

		bis.close();

		// 구한 데이터로 채크섬 값을 구한다.
		byte calChecksum = ANSI.chkSum(dataLayer, 0, dataLayer.length);

		// 계산한 채크섬과 가져온 채크섬을 비교한다.
		if (checksumLayer != calChecksum) {
			throw new MRPException(MRPError.ERR_CRC_CHECK_ERROR,
					"Check Sum Error.");
		}
		return dataLayer;
	}

	/**
	 * 메시지를 기다리고 byte array 형태로 받는다.<br>
	 * 받은 데이터는 crc 채크하고 데이터 부분만 추출해서 넘겨준다.
	 * 
	 * @param session
	 * @return
	 * @throws MRPException
	 */
	private byte[] readMessage(IoSession session) throws MRPException {

		ReadFuture future;
		ByteArray message = new ByteArray();

		boolean ACK = false;

		// 메시지를 다받았는지
		boolean isFinished = false;

		do {
			// 메시지를 받는다.
			future = session.read();

			// 메시지를 기다린다.
			future.awaitUninterruptibly(TIMEOUT * 1000);
			if (future.isRead()) {
				// 메시지를 받는다.
				byte[] receiveMessage = (byte[]) future.getMessage();

				// ACK 처리//
				// ACK 일경우 다시한번더 읽어온다.
				if (receiveMessage[0] == ANSI.ACK && !ACK) {
					ACK = true;
					if (receiveMessage.length > 1) {
						// ACK뒤에 패킷이 붙어서 온경우
						byte[] cutAck = new byte[receiveMessage.length - 1];
						System.arraycopy(receiveMessage, 1, cutAck, 0,
								cutAck.length);
						message.append(cutAck);
					}
				} else if (receiveMessage[0] == ANSI.NAK && !ACK) {
					throw new MRPException(MRPError.ERR_CONNECTTOMETER_CLASS,
							"Receive NAK:" + Hex.getHexDump(receiveMessage));
				} else {
					// 멀티 패킷일경우 다음패킷은 ACK가 오지 않는다 ACK받은걸로 처리.
					ACK = true;

					message.append(receiveMessage);
				}
				// 패킷 길이를 구해서 모두 받았는지 확인한다.
				isFinished = isReadDone(message.toByteArray());

				log.debug("red:" + Hex.getHexDump(message.toByteArray()));
			} else {
//				log.debug(future.getException().getMessage());
				throw new MRPException(MRPError.ERR_CONNECTTOMETER_CLASS,
						future.getException().getMessage());
			}
		} while (!isFinished);

		byte[] messageArray = message.toByteArray();

		// crc 를 확인해서 패킷에 이상이 없는지 검사한다.
		boolean crc_ok = crc_check(messageArray);
		if (!crc_ok) {
			throw new MRPException(MRPError.ERR_CRC_CHECK_ERROR,
					"CRC Check Error.");
		}

		log.debug("readMessage : " + Hex.getHexDump(messageArray));

		// 받은 패킷의 해더를 분석해서 멀티 패킷인지 확인한다.
		byte ctrl = messageArray[2];
		byte multiPacket = (byte) (ctrl & 0x80);

		if (multiPacket != 0) {
			// 멀티 패킷일경우

			// sequence number 를 가져온다. 0이 될때까지 패킷을 받으면 된다.
			byte sequenceNumber = messageArray[3];
			if (sequenceNumber != 0) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				try {
					bos.write(getDataFrame(messageArray));
					// 다음패킷 요청
					sendMessage(session, ACK());
					// 재귀하여 sequence number 0이 될때까지 실행된다.
					bos.write(readMessage(session));
					byte[] packets = bos.toByteArray();
					bos.close();
					return packets;
				} catch (IOException e) {
					throw new MRPException(MRPError.ERR_READ_METER_CLASS,
							"Read Multi Packet OutputStream Error");
				} catch (InterruptedException e) {
					throw new MRPException(MRPError.ERR_READ_METER_CLASS,
							"Read Multi Packet OutputStream Error");
				}
			} else {
				return getDataFrame(messageArray);
			}
		}

		// 헤더부분을 잘라내고 넘긴다.
		return getDataFrame(messageArray);
	}

	private boolean isReadDone(byte[] data) {
		// 최소 해더 길이 확인
		if (data.length < 6) {
			return false;
		} else {
			// 해더와 풋터를 뺀 데이터 길이.
			byte[] dataLength = new byte[] { data[4], data[5] };

			// 받아야 하는 총 패킷 길이. (해더 + 풋터 = 8)
			int totalLangth = byteArrayToInt(dataLength) + 8;

			// 길이보다 더 많은 패킷이 왔을때 완료가 안된다.
			if (data.length == totalLangth) {
				return true;
			} else if (data.length > totalLangth) {
				// 받아야할 패킷보다 더 많이 받았을경우 뒤에것들은 잘라 버린다.
				byte[] fixData = new byte[totalLangth];
				System.arraycopy(data, 0, fixData, 0, fixData.length);
				data = fixData;
				return true;
			} else {
				return false;
			}
		}
	}

	public boolean crc_check(byte[] message) {
		// 메시지에서 crc 부분을 추출한다.
		byte[] msg_crc = new byte[2];
		System.arraycopy(message, message.length - 2, msg_crc, 0, 2);

		// 계산된 crc
		byte[] cal_crc = DataUtil.get2ByteToChar(ANSI.crc(message.length - 2,
				(byte) 0, message));

		// 계산된 crc와 받은 crc를 비교한다.
		return Arrays.equals(cal_crc, msg_crc);
	}

	/**
	 * 전체 프레임 중에 데이터 프레임 부분만 구해준다.<br>
	 * 프레임에 ACK 가 포함되어있는지 확인한다.
	 * 
	 * @param allFrame
	 * @return
	 * @throws MRPException
	 */
	public byte[] getDataFrame(byte[] allFrame) throws MRPException {
		byte[] dataFrame = null;

		// 데이터 Size 정보가 있는 비트 인덱스
		int index_dataSize = 4;

		// 데이터의 위치.
		int index_data = 6;

		// 프레임에 ACK 가 포함되어있는지 확인한다.
		if (allFrame[0] != ANSI.STP) {
			// 첫번째 비트가 STP(Start of packet character)가 아닐경우

			if (allFrame[0] == ANSI.ACK && allFrame[1] == ANSI.STP) {
				// 정상. 패킷에 ACK가 포함되어있음.

				// ACK가 포함되어 인덱스가 하나씩 밀림
				index_dataSize++;
				index_data++;
			} else if (allFrame[0] == ANSI.NAK || allFrame[1] != ANSI.STP) {
				// 비정상 포멧 이거나 NAK 를 수신했을때.

				log.debug("header format error");

				// 포멧 에러 발생.
				throw new MRPException(MRPError.ERR_HEADER_FORMAT_ERROR,
						"Receive header format error");
			}
		}

		// 데이터 프레임의 길이를 구한다.
		int dataFrameSize = DataUtil.getIntTo2Byte(allFrame[index_dataSize],
				allFrame[index_dataSize + 1]);

		// 데이터 프레임 부분만 추출해서 복사한다.
		dataFrame = new byte[dataFrameSize];
		System.arraycopy(allFrame, index_data, dataFrame, 0, dataFrameSize);

		return dataFrame;
	}

	private IoBuffer ACK() {
		IoBuffer ackBuff = IoBuffer.allocate(1);
		ackBuff.put(ANSI.ACK);
		ackBuff.flip();
		return ackBuff;
	}

	/**
	 * Byte Array 를 int 로 변환해준다. 4byte 초과일경우 변환하지 않고 -1 리턴
	 * 
	 * @param byteArray
	 * @return
	 */
	protected int byteArrayToInt(byte[] byteArray) {
		int MAXIMUM_LENGTH = 4;
		if (byteArray.length > MAXIMUM_LENGTH)
			return -1;

		// 버퍼 생성.
		ByteBuffer buf = ByteBuffer.allocate(MAXIMUM_LENGTH);

		int skip = MAXIMUM_LENGTH - byteArray.length;

		// 4byte 미만일경우 차이만큼 이동해서 쓴다.
		buf.position(skip);
		buf.put(byteArray);
		buf.flip();

		// int 로 형변환 한다.
		int n = buf.getInt();
		return n;
	}

	@Override
	public long getSendBytes() throws MRPException {

		return this.sendBytes;// session.getWrittenBytes()
	}

	protected byte[] cutHeaderTail(byte[] b) {

		byte[] data;

		try {

			if (b[0] == ANSI.ACK && b[1] == ANSI.STP) {
				int rlen = b.length - (ANSI.LEN_HEADER + ANSI.LEN_CRC);
				data = new byte[rlen];
				System.arraycopy(b, ANSI.LEN_HEADER, data, 0, rlen);
				return data;
			} else if (b[0] == ANSI.STP) {
				int rlen = b.length - (ANSI.LEN_HEADER - 1 + ANSI.LEN_CRC);
				data = new byte[rlen];
				System.arraycopy(b, ANSI.LEN_HEADER - 1, data, 0, rlen);
				return data;
			} else {
				data = new byte[0];
				return data;
			}

		} catch (Exception e) {
			log.warn("CUT HEADER TAIL ERROR : " + e.getMessage());
		}

		return null;

	}

	private boolean check_data_frame(byte[] buf, int len, byte meter_cmd) {

		log.debug("check_data_frame:" + new OCTET(buf).toHexString());
		boolean ret = true;

		if (len == 0) {
			log.debug("length check error");
			ret = false;
		}

		if (buf == null || buf.length == 0) {
			log.debug("length check error");
			ret = false;
		}

		if (meter_cmd == ANSI.RR) {
			if (buf[0] != ANSI.STP) {
				log.debug("header format error");
				ret = false;
			}

		} else {

			if (buf[0] == ANSI.STP) {
				return true;
			}
			if (buf[0] == (byte) 0xFF || buf[0] == (byte) 0xFE) {
				if (buf[1] == ANSI.NAK || buf[2] != ANSI.STP) {
					log.debug("header format error");
					return false;
				}
			} else if (buf[0] == ANSI.NAK || buf[1] != ANSI.STP) {
				log.debug("header format error");
				return false;
			}
		}

		/*
		 * if(!ANSI.crc_check(buf, len, meter_cmd)) {
		 * log.debug("crc check error"); return false; }
		 */

		return ret;
	}

	private byte[] cutLenChkSum(byte[] b) {

		byte[] data;

		try {
			int rlen = b.length - 4;
			data = new byte[rlen];
			System.arraycopy(b, 3, data, 0, rlen);
			return data;

		} catch (Exception e) {
			log.warn("CUT LEN CHECKSUM ERROR : " + e.getMessage());
		}
		return null;
	}

	/**
	 * 날짜 등을 BCD ByteArray로 변환하는 역할을 한다.<br>
	 * 
	 * @param val
	 * @return
	 * @throws IOException
	 */
	public byte[] getBCD(String val) throws IOException {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		for (int i = 0; i < val.length(); i += 2) {
			// String 을 2 size 읽어와 16진수 숫자로 변환한다.
			String hexString = val.substring(i, i + 2);
			int hexNum = Integer.parseInt(hexString, 16);

			// 변환된 수를 ByteArray 에 입력한다.
			bos.write(hexNum);
		}

		bos.close();

		return bos.toByteArray();
	}
	
	/**
	 * 채울 바이트와 크기를 입력받아 크기만큼 생성하여 앞쪽엔 ByteArray 복사하고 나머지는 fillByte로 채운다.
	 * @param bytes
	 * @param fillByte
	 * @param size
	 * @return
	 */
	private byte[] fillCopy(byte[] bytes, byte fillByte, int size) {
		byte[] data = new byte[size];
		
		System.arraycopy(bytes, 0, data, 0, bytes.length);
		
		for(int i = bytes.length;i<size;i++){
			data[i] = fillByte;
		}
		
		return data;
	}

	@Override
	public boolean checkCRC(byte[] src) throws MRPException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void quit() throws MRPException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setGroupNumber(String groupNumber) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setMemberNumber(String memberNumber) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setModemId(String modemId) {
		// TODO Auto-generated method stub

	}

	@Override
	public HashMap timeCheck(IoSession session) throws MRPException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap timeSync(IoSession session, int timethreshold)
			throws MRPException {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    public CommandData execute(Socket socket, CommandData command)
            throws MRPException {
        // TODO Auto-generated method stub
        return null;
    }

}
