/** 
 * @(#)MX2_Handler.java       1.0 2011-07-21 *
 * Copyright (c) 2008-2009 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 * 
 * <참고문서><br>
 * MX2_AMR_Communication_Specification-2011-06-16_Signed.pdf<Br>
 * NAMR_P213GP(2011)_Protocol.doc		  
 * 
 * @author kskim
 */
package com.aimir.fep.protocol.mrp.protocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.MeterProgramKind;
import com.aimir.constants.CommonConstants.OperatorType;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.MeterTimeSyncLogDao;
import com.aimir.fep.meter.data.Response;
import com.aimir.fep.meter.parser.MX2Table.DisplayItemSetting;
import com.aimir.fep.meter.parser.MX2Table.IdentTable;
import com.aimir.fep.meter.parser.MX2Table.NegotiateTable;
import com.aimir.fep.meter.parser.MX2Table.SummerTime;
import com.aimir.fep.meter.parser.MX2Table.SummerTimeDateSet;
import com.aimir.fep.meter.parser.MX2Table.TOUCalendar;
import com.aimir.fep.meter.parser.MX2Table.TOUCalendarBuilder;
import com.aimir.fep.protocol.fmp.datatype.BYTE;
import com.aimir.fep.protocol.fmp.datatype.INT;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.protocol.fmp.datatype.OID;
import com.aimir.fep.protocol.fmp.datatype.OPAQUE;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
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
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.MIBUtil;
import com.aimir.model.device.Meter;
import com.aimir.model.device.MeterTimeSyncLog;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeUtil;

/**
 * MX2 미터 통신 프로토콜 구현 객체. <br>
 * MX2 미터와 통신하여 검침값을 읽거나 미터 설정등의 기능을 수행한다.<br>
 * @author kskim
 */
public class MX2_Handler extends MeterProtocolHandler {
    // User ID
    private static final byte[] USER_ID = new byte[] { 0x10, 0x01 };

    // User identification (ASCII)
    private static final byte[] USER_NAME =  "LICENSEMEA".getBytes();

    // ctrl의 toggleBit : 패킷이 중복되지 않도록 전환 비트를 설정한다. 0x20 <-> 0x00
    private byte toggleBit = 0x00;
    
    private Socket socket = null;
    private InputStream sin = null;
    private OutputStream sout = null;
    
	/**
	 * MX2미터의 예외 사항 정의.
	 * @author kskim
	 */
	private class MX2Exception extends Exception
	{
	    /**
		 * 
		 */
		private static final long serialVersionUID = -4008476513551686090L;
		
		private Response response;

	    private MX2Exception()
	    {
	        super();
	    }

	    private MX2Exception(Response response)
	    {
	        super();
	        this.response = response;
	    }
	    private MX2Exception(String msg)
	    {
	        super();
	        this.response = new Response(Response.Type.ERR,msg);
	    }

	    private MX2Exception(Throwable t)
	    {
	        super(t);
	    }
	    
		private Response getResponse() {
			return response;
		}
	    
		public String toString(){
		    if (this.response != null) return this.response.getMessage();
		    else return super.getMessage();
		}

	}

	/**
	 * 미터에 기록된 데이터의 범위(Range) 를 나타내는 구조.<br>
	 * Index 와 Count Field 로 구성되어 있고 Index는 Record 의 위치를 나타내며 Count 는 Index로부터의
	 * 갯수를 나타낸다. 이 데이터 구조를 Array 나 HashTable 을 이용할경우 구조파악이 어렵고 명확하지 않아 별도록 데이터
	 * 구조를 만든것임.
	 * 
	 * @author kskim
	 * 
	 */
	@SuppressWarnings("unused")
	private class RecordRange {
		private int index;
		private int count;

		private RecordRange() {
			this.index = 0;
			this.count = 0;
		}

		private RecordRange(int index, int count) {
			this.index = index;
			this.count = count;
		}

		private int getIndex() {
			return index;
		}

		private void setIndex(int index) {
			this.index = index;
		}

		private int getCount() {
			return count;
		}

		private void setCount(int count) {
			this.count = count;
		}
	}
	
	private static Log log = LogFactory.getLog(MX2_Handler.class);
	private static Log rlog = LogFactory.getLog("READ");
	
	
	//검침된 미터 아이디. meter Serial
	private String meterId;

	//{상속받은 메소드 용 필드
	@SuppressWarnings("unused")
	private String modemId;
	@SuppressWarnings("unused")
	private String groupId = "";
	@SuppressWarnings("unused")
	private String memberId = "";
	@SuppressWarnings("unused")
	private String mcuSwVersion = "";
	@SuppressWarnings("unused")
	private byte[] messageBuffer;
	//}

	private IoSession session;
	
	@SuppressWarnings("unused")
	private MRPClientProtocolHandler handler;
	
	//미터에서 올려주는 Billing Data 의 Rate 개수이다.
	private static final int RATE_COUNT = 8; 
	
	// IF4 에서 사용되는 Rate 개수이다. 미터에서 올려주는 내용을 이 갯수만큼만 저장한다.
	private static final int NECESSARY_RATE_COUNT = 4; 

	//timeout 시간(초,seconds)
	private static final int TIMEOUT = 30;

	//재시도 횟수
	private static final int RETRY_COUNT_MAX = 3;
	
	//OnDemand 명령시 Range 옵션을 실제 날짜 데이터로 변환된 데이터.
	private Date fromDate;
	private Date toDate;

	private int retryCount = 0;
	
	//자동으로 시간 동기화 할것인지 설정.
	private boolean autoTimeSync = false;
	//설정 초(seconds) 만큼의 시간 차이가 날때 자동으로 시간동기화 수행.
	private Integer timeDiffLimit = null;
	
	//명령 타입(사람 or System)
	private OperatorType operatorType=OperatorType.SYSTEM;
	
	public MX2_Handler(IoSession session) throws IOException {
        this.session = session;
        new MX2_Handler();
    }
	
	public MX2_Handler(Socket socket) throws IOException {
	    this.socket = socket;
	    this.sin = this.socket.getInputStream();
	    this.sout = this.socket.getOutputStream();
	    this.socket.setSoTimeout(TIMEOUT*1000);
	    new MX2_Handler();
	}
	
	public MX2_Handler() {
		
		//시간 동기화 설정 불러옴.
		autoTimeSync = "true".equals(FMPProperty.getProperty("timesync.auto", "true"));
		String _TimeDiffLimit = FMPProperty.getProperty("timesync.auto.difflimit", "10");
		if(_TimeDiffLimit!=null){
			try{
				timeDiffLimit = Integer.parseInt(_TimeDiffLimit);
			}catch(NumberFormatException e){
				timeDiffLimit = 10;
			}
		}
	}

	public CommandData execute(MRPClientProtocolHandler handler,
            IoSession session, CommandData command) throws MRPException {
	    this.handler = handler;
        this.session = session;
        // 속성을 설정해 줘야 디코더에서 IoBuffer 로 디코딩됨.

        return execute(null, command);
	}
	
	/**
	 * 미터에 명령을 보내는 메소드<br>
	 * 전송받은 데이터들은 IF4 포멧으로 변환하는 과정을 거치며 포멧정의는 문서를 참고한다.<br>
	 * @throws MX2Exception 
	 * 
	 * @see NAMR_P213GP(2011)_Protocol.doc
	 * @see com.aimir.fep.protocol.mrp.MeterProtocolHandler#execute(com.aimir.fep.protocol.mrp.client.MRPClientProtocolHandler,
	 *      org.apache.mina.core.session.IoSession,
	 *      com.aimir.fep.protocol.fmp.frame.service.CommandData)
	 */
	public CommandData execute(Socket socket, CommandData command) throws MRPException {
	    try {
	        if (socket != null) {
    	        this.socket = socket;
    	        this.sin = this.socket.getInputStream();
    	        this.sout = this.socket.getOutputStream();
    	        this.socket.setSoTimeout(TIMEOUT*1000);
	        }
	    
    		// 속성을 설정해 줘야 디코더에서 IoBuffer 로 디코딩됨.
            // session.setAttribute(MRPClientProtocolHandler.isActiveKey, false);
    
    		CommandData commandData = null;
    
    		// mib 파일 유틸
    		MIBUtil mibUtil = MIBUtil.getInstance();
    		String commandName = mibUtil.getName(command.getCmd().getValue());
    		log.debug("==============MX2_Handler start cmd:" + commandName
    				+ "================");
    
    		// 미터 와의 통신 초기화 및 로그인
			init();
    
    		// 명령별로 분기
    		if (commandName.equals("cmdSyncMeterTime")) {
    			//관리자에 의해 명령이 올경우
    			setOperatorType(OperatorType.OPERATOR);
    			
    			//meter serial 정보를 읽어온다. 디비 미터 정보와 멥핑 목적.
    			readMeterSerial();
    			
    			commandData = cmdSyncMeterTime();
    
    		} else if (commandName.equals("cmdOnDemandMeter")) { //commandName.equals("cmdGetMeterSchedule")
    			SMIValue[] smiValue = command.getSMIValue();
    			try {
    				commandData = cmdOnDemandMeter(smiValue);
    			} catch (Exception e) {
    			    log.error(e, e);
    				throw new MRPException(MRPError.ERR_CONNECTTOMETER_CLASS, e.getCause());
    			}
    		} else if(commandName.equals("cmdWriteTable")){
    			
    			Object[] objs = command.getObjValue();
    			MeterProgramKind tableType = null;
    			if(objs[0] instanceof MeterProgramKind){
    				tableType = (MeterProgramKind)objs[0];
    			}else{
    				throw new MRPException(MRPError.ERR_INVALID_PARAM);
    			}
    			
    			switch(tableType){
    			case TOUCalendar:
    				//TOU Calendar
    				TOUCalendar calendar = (TOUCalendar)objs[1];
    				commandData = setTOUCalendar(calendar);
    				break;
    			case DaySavingTime:
    				//Summer Time
    				SummerTime st = (SummerTime)objs[1];
    				commandData = setSummerTime(st);
    				break;
    			case DisplayItemSetting:
    				//DisplayItemSetting
    				DisplayItemSetting displayItemSetting = (DisplayItemSetting)objs[1];
    				commandData = setDisplayItemSetting(displayItemSetting);
    				break;
    			default:
    				break;
    			
    			}
    		} else if(commandName.equals("cmdReadTable")){
    			byte[] resultData = null;
    			Object[] objs = command.getObjValue();
    			MeterProgramKind tableType = null;
    			if(objs[0] instanceof MeterProgramKind){
    				tableType = (MeterProgramKind)objs[0];
    			}else{
    				throw new MRPException(MRPError.ERR_INVALID_PARAM);
    			}
    			
    			switch(tableType){
    			case SAPTable:
    				//SAP 시스템 포멧으로 파일 출력하기 위해 필요한 테이블 정볼를 읽어온다
    				byte[] meterInfo = (byte[]) configureReadWithHeader(); // conretryableInvoke(M_CONFIGURE);
    				byte[] sapData = (byte[]) sapReadWithHeader(); // retryableInvoke(M_SAP);
    				resultData = new byte[meterInfo.length+sapData.length];
    				
    		    	System.arraycopy(meterInfo, 0, resultData, 0, meterInfo.length);
    		    	System.arraycopy(sapData, 0, resultData, meterInfo.length, sapData.length);
    		    	
    				CommandData cmdData = new CommandData();
    				cmdData.append(resultData);
    				commandData = cmdData;
    				
    				break;
    			}
    		}
    
    		logoff();
    
    		log.debug("==============MX2_Handler end ================");
    
    		return commandData;
	    }
        catch (IOException e) {
            throw new MRPException(MRPError.ERR_MODEMERROR, e.getCause());
        }
	    catch (MX2Exception e1) {
	        log.error(e1, e1);
            throw new MRPException(MRPError.ERR_INITMODEM_CLASS, e1.getCause());
        }
	}

	/**
	 * TOU Calendar 설정
	 * @param session
	 * @param csv
	 */
	private CommandData setTOUCalendar(TOUCalendar calendar) {
		CommandData cmdd = new CommandData();
		try {
			byte[] data;
			
			//Activation Date
			data = calendar.getActivationDate();
			wActivationDate( data);
			
			//Season change
			data = calendar.getSeasonChange();
			wSeasonChange(data);
			
			//Day pattern
			data = calendar.getDayPattern();
			wDayPattern(data);
			
			//fixed recurring holiday
			data = calendar.getFrHoliday();
			wFixedRecurringHoliday(data);
			
			//Non recurring holiday
			data = calendar.getNrHoliday();
			wNonRecurringHoliday(data);
			
			//End message
			data = calendar.getEndMessage();
			wEndMessage(data);
			
		}catch (MX2Exception mx2e){
			log.error(mx2e,mx2e);
			cmdd.setResponse(mx2e.getResponse());
			return cmdd;
		}
		catch (Exception e) {
			log.error(e,e);
			Response r = new Response(Response.Type.ERR,e.getMessage());
			cmdd.setResponse(r);
			return cmdd;
		}
		
		Response r = new Response(Response.Type.OK,"Sucess");
		cmdd.setResponse(r);
		cmdd.setSvc("C".getBytes()[0]); // Code of (C)Command
		return cmdd;
	}
	
	/**
	 * Display Item Setting 설정
	 * @param session
	 * @param csv
	 */
	 
	private CommandData setDisplayItemSetting(DisplayItemSetting displayItemSetting) {
		CommandData cmdd = new CommandData();
		try {
			byte[] data;
			log.debug("setDisplayItemSetting");
			
			//Normal Display Items Select
			data = displayItemSetting.getNormalDisplayItemsSelect();
			wNormalDisplayItemsSelect(data);
			
			//Alternate Display Items Select
			data = displayItemSetting.getAlternateDisplayItemsSelect();
			wAlternateDisplayItemsSelect(data);
			
			//Test Display Items Select
			data = displayItemSetting.getTestDisplayItemsSelect();
			wTestDisplayItemsSelect(data);
			
			//Normal DisplayId Set1
			data = displayItemSetting.getNormalDisplayIdSet1();
			wNormalDisplayIdSet1(data);
			
			//Normal DisplayId Set2
			data = displayItemSetting.getNormalDisplayIdSet2();
			wNormalDisplayIdSet2(data);
			
			//Alternate DisplayId Set1
			data = displayItemSetting.getAlternateDisplayIdSet1();
			wAlternateDisplayIdSet1(data);
			
			//Alternate DisplayId Set2
			data = displayItemSetting.getAlternateDisplayIdSet2();
			wAlternateDisplayIdSet2(data);
			
			//Test DisplayId
			data = displayItemSetting.getTestDisplayId();
			wTestDisplayId(data);
			
		}catch (MX2Exception mx2e){
			log.error(mx2e,mx2e);
			cmdd.setResponse(mx2e.getResponse());
			return cmdd;
		}
		catch (Exception e) {
			log.error(e,e);
			Response r = new Response(Response.Type.ERR,e.getMessage());
			cmdd.setResponse(r);
			return cmdd;
		}
		
		Response r = new Response(Response.Type.OK,"Sucess");
		cmdd.setResponse(r);
		cmdd.setSvc("C".getBytes()[0]); // Code of (C)Command
		return cmdd;
	}

	/**
	 * 데이터를 읽어온다.
	 * 
	 * @param session
	 * @param smiValue
	 * @return
	 * @throws MX2Exception
	 * @throws IOException
	 */
	private CommandData cmdOnDemandMeter(SMIValue[] smiValue)
			throws MX2Exception, IOException, MRPException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] baData = null;

		if (smiValue != null && smiValue.length >= 2) {
			int kind = ((INT) smiValue[1].getVariable()).getValue();

			// 테이블을 읽을때 사용하는 Range 정보
			RecordRange lpDataRange = null;

			// 날짜 파라미터가 있는 지 확인한다.
			if (smiValue.length >= 4) {

				// 3-4번째 파라미터에 Date 가 있는지 확인.
				Object dateParam1 = smiValue[2].getVariable();
				Object dateParam2 = smiValue[3].getVariable();
				if (dateParam1 instanceof INT && dateParam2 instanceof INT) {

					/**
					 * offset 은 오늘날짜 기준으로 fromDate 와의 일수 차이 값이다. fromDate 날짜에
					 * count(Day) 를 더하면 toDate 가 나온다.
					 */
					int offset = ((INT) dateParam1).getValue();
					int count = ((INT) dateParam2).getValue();

					// 파라미터로 받은 날짜 정보들을 계산하기 쉽도록 Date Type 으로 Convert한다.
					this.fromDate = previousOfToday(offset);
					this.toDate = previousOfToday(offset - count);
					Date lastLpdataDate = getLastLpdataDate();
					int lpInterval = getLpDataInterval();

					// 각 테이블별로 검침 범위를 구한다.
					lpDataRange = getLpDateRange(this.fromDate, this.toDate,
							lastLpdataDate, lpInterval);

				}

			} else {
				// 날짜 옵션이 없을경우 디폴트 범위
				// Interval 15분 기준 하루 최대 96개
				lpDataRange = new RecordRange(0, 240);
			}

			switch (kind) {
			case MX2_DataConstants.CMD_ALL:// all
				// MeterInfo 정보를 불러온다.
				bos.write(configureReadWithHeader());

				// Billing Data
				bos.write(billReadWithHeader());

				// Event Log
				bos.write(eventlogReadWithHeader());
			    
				// Load Recent Data Table
				bos.write(lpReadWithHeader(lpDataRange));
				
				// SAP
				bos.write(sapReadWithHeader());

				/*
				try {
					byte[] resultData = null;
					
					Method[] callMethods = new Method[]{
							M_CONFIGURE,
							M_BILLING,
							M_EVENTLOG,
							M_LP,
							M_SAP
					};
					
					Object[][] pararms = new Object[][]{
						{},
						{},
						{},
						{lpDataRange},
						{}
					};
					
					for (int i=0;i<callMethods.length;i++) {
						resultData = null;
						resultData = (byte[]) retryableInvoke(callMethods[i],pararms[i]);
						bos.write(resultData);
					}
					
					
				}catch (IllegalArgumentException e) {
					log.error(e, e);
					throw new MX2Exception(e.getMessage());
				}
				*/
				
				break;
			default:
				throw new MX2Exception("Invalid parameters");
			}

		} else {
			throw new MX2Exception("Invalid parameters");
		}

		bos.flush();
		baData = bos.toByteArray();

		bos.close();

		if (baData != null && baData.length > 0) {
			log
					.debug(smiValue[0].getVariable().toString() + ","
							+ this.meterId);

			meterLPEntry me = new meterLPEntry();

			/**
			 * Modem Serial<br>
			 * Hex.encode() 하여 8byte 값을 넘겨줘야한다.
			 */
			String strModemSerial = smiValue[0].getVariable().toString();
			log.debug("modemserial :" + strModemSerial);
			OCTET ocModemSerial = new OCTET(8); // 생성시 크기가 고정되도록 생성한다.
			ocModemSerial.setValue(Hex.encode(strModemSerial));

			// meter id
			OCTET ocMeterId = new OCTET(20);
			ocMeterId.setIsFixed(true);
			byte[] bMeterid = DataUtil.fillCopy(this.meterId.getBytes(), (byte) 0x20, 20);// 빈공간은
			// space
			// 로
			// 채운다.
			// 0x32

			ocMeterId.setValue(bMeterid);
			log.debug("meter id:" + bMeterid);

			me.setMlpId(ocModemSerial);
			me.setMlpMid(ocMeterId);
			me.setMlpDataCount(new WORD(1));
			me.setMlpServiceType(new BYTE(1)); // 1: electric
			me.setMlpType(new BYTE(CommonConstants.ModemType.Converter
					.getIntValue()));// 19:Converter
			me.setMlpVendor(new BYTE(CommonConstants.MeterVendor.Mitsubishi
					.getCode()[0].intValue()));// MITSUBISHI

			// Measurement Data List
			ByteArray measData = new ByteArray();

			// length timestamp + data length
			// OCTET decode 할때 length 를 붙여주기 때문에 따로 입력하지 않는다.

			// Time stamp
			try {
				String nowTime = TimeUtil.getCurrentTime();
				measData.append(DataFormat.encodeTime(nowTime));
				byte[] encodeTime = DataFormat.encodeTime(nowTime);
				log.debug("Time Stamp : " + nowTime + " / Encode["
						+ Hex.getHexDump(encodeTime) + "]");
			} catch (Exception e) {
				throw new MX2Exception(e.getCause());
			}

			// timestamp + data
			measData.append(baData);
			byte[] lpData = measData.toByteArray();
			OCTET octetLpData = new OCTET();
			octetLpData.setValue(lpData);
			me.setMlpData(octetLpData);
			CommandData commandData = new CommandData();
			commandData.setCnt(new WORD(1));
			commandData.setTotalLength(baData.length);
			commandData.append(new SMIValue(new OID("10.1.0"), new OPAQUE(me)));
			commandData.setSvc("M".getBytes()[0]); // Code of (M)Metering
			return commandData;
		} else {
			throw new MX2Exception("Data is null");
		}

	}

	/**
	 * @param session
	 * @param tableId
	 * @param data
	 * @throws MX2Exception
	 */
	@SuppressWarnings("unused")
	private void writeTable(char tableId, byte[] data) throws IOException, MX2Exception{
		// data 를 write service frame 에 담아 buffer 를 생성한다.
		IoBuffer writeFrame = getWrite(tableId, data);

		sendMessage(writeFrame);
		writeServiceReceive();
	}
	
	/**
	 * Activation Date Write Table.
	 * @param session
	 * @param data
	 * @throws MX2Exception
	 */
	private void wActivationDate(byte[] data)
			throws IOException, MX2Exception {

		if (data.length != TOUCalendarBuilder.LEN_ACTIV_DATE)
			throw new MX2Exception("invalid data");

		// retryableInvoke(M_WRITE_TABLE, MX2_DataConstants.TABLE_ACTIVATION_DATE,data);
		writeTable( MX2_DataConstants.TABLE_ACTIVATION_DATE, data);
	}
	
	/**
	 * Season Change Write Table Service.
	 * @param session
	 * @param data
	 * @throws MX2Exception 
	 */
	private void wSeasonChange(byte[] data)
			throws IOException, MX2Exception {

		if (data.length != TOUCalendarBuilder.LEN_SEASON_CHANGE)
			throw new MX2Exception("invalid data");

		// retryableInvoke(M_WRITE_TABLE,MX2_DataConstants.TABLE_SEASON_CHANGE,data);
		writeTable( MX2_DataConstants.TABLE_SEASON_CHANGE, data);
	}
	
	/**
	 * 데이터 길이 만큼 연속된 Table ID에 Write 하는 기능. 누락된 데이터는 0xff로 채움.
	 * @param session
	 * @param data 1차배열은 Table겟수를 나타내고, 2차 배열은 해당 테이블에 Write 할 데이터이다.
	 * @param tableId 시작 테이블의 ID이며, 데이터 수만큼 ID를 증가해서 Write 한다.
	 * @param cnt 테이블 겟수
	 * @param len 데이터 길이
	 * @throws MX2Exception
	 */
	private void multiWriteTable(byte[] data,int cnt, int len, char tableId) 
	        throws IOException, MX2Exception{

		if (data==null || data.length < len || data.length > (len*cnt) || data.length % len !=0)
			throw new MX2Exception("invalid data");

		int count = data.length/len;
		
		byte[][] muData = new byte[cnt][len];
		
		ByteArrayInputStream bos = new ByteArrayInputStream(data);
		
		for(int i=0;i<cnt;i++){
			try {
				if(i>=count){
					//값이 없을경우0xff로 채운다.
					muData[i] = DataUtil.fillBytes((byte)0xff, len);
				}else{
					bos.read(muData[i]);
				}
			} catch (IOException e) {
				throw new MX2Exception(e.getCause());
			}
		}
		try {
			bos.close();
		} catch (IOException e) {
			throw new MX2Exception(e.getCause());
		}
		
		for (int i = 0; i < count; i++) {
			// retryableInvoke(M_WRITE_TABLE,(char) (tableId + i),muData[i]);
		    writeTable( (char)(tableId+i), muData[i]);
		}
	}
	
	/**
	 * Normal Display Items Select
	 * 
	 * @param session
	 * @param data 
	 * @throws MX2Exception 
	 */
	private void wNormalDisplayItemsSelect(byte[] data) throws IOException, MX2Exception{
		writeTable( MX2_DataConstants.TABLE_NORMAL_DISPLAY_ITEMS_SELECT,data);
	}
	
	/**
	 * Alternate Display Items Select
	 * 
	 * @param session
	 * @param data 
	 * @throws MX2Exception 
	 */
	private void wAlternateDisplayItemsSelect(byte[] data) throws IOException, MX2Exception{
		writeTable( MX2_DataConstants.TABLE_ALTERVATE_DISPLAY_ITEMS_SELECT,data);
	}
	
	/**
	 * Test Display Items Select
	 * 
	 * @param session
	 * @param data 
	 * @throws MX2Exception 
	 */
	private void wTestDisplayItemsSelect(byte[] data) throws IOException, MX2Exception{
		writeTable( MX2_DataConstants.TABLE_TEST_DISPLAY_ITEMS_SELECT,data);
	}
	
	/**
	 * Normal DisplayId Set1
	 * 
	 * @param session
	 * @param data 
	 * @throws MX2Exception 
	 */
	private void wNormalDisplayIdSet1(byte[] data) throws IOException, MX2Exception{
		writeTable( MX2_DataConstants.TABLE_NORMAL_DISPLAY_ID_SET1,data);
	}
	
	/**
	 * Normal DisplayId Set2
	 * 
	 * @param session
	 * @param data 
	 * @throws MX2Exception 
	 */
	private void wNormalDisplayIdSet2(byte[] data) throws IOException, MX2Exception{
		writeTable( MX2_DataConstants.TABLE_NORMAL_DISPLAY_ID_SET2,data);
	}
	
	/**
	 * Alternate DisplayId Set1
	 * 
	 * @param session
	 * @param data 
	 * @throws MX2Exception 
	 */
	private void wAlternateDisplayIdSet1(byte[] data) throws IOException, MX2Exception{
		writeTable( MX2_DataConstants.TABLE_ALTERVATE_DISPLAY_ID_SET1,data);
	}
	
	/**
	 * Alternate DisplayId Set2
	 * 
	 * @param session
	 * @param data 
	 * @throws MX2Exception 
	 */
	private void wAlternateDisplayIdSet2(byte[] data) throws IOException, MX2Exception{
		writeTable( MX2_DataConstants.TABLE_ALTERVATE_DISPLAY_ID_SET2,data);
	}
	
	/**
	 * Alternate DisplayId Set1
	 * 
	 * @param session
	 * @param data 
	 * @throws MX2Exception 
	 */
	private void wTestDisplayId(byte[] data) throws IOException, MX2Exception{
		writeTable( MX2_DataConstants.TABLE_TEST_DISPLAY_ID,data);
	}
	
	/**
	 * Day Pattern Write Table.
	 * 
	 * @param session
	 * @param data 
	 * @throws MX2Exception 
	 */
	private void wDayPattern(byte[] data) throws IOException, MX2Exception{
		final int CNT = TOUCalendarBuilder.CNT_DAY_PATTERN;
		final int LEN = TOUCalendarBuilder.LEN_DAY_PATTERN;
		multiWriteTable( data,CNT,LEN,MX2_DataConstants.TABLE_DAY_PATTERN_SEASON1);
	}
	
	/**
	 * Fixed Recurring Holiday Write Table.
	 * @param session
	 * @param data
	 * @throws MX2Exception
	 */
	private void wFixedRecurringHoliday(byte[] data) throws IOException, MX2Exception{
		final int CNT = TOUCalendarBuilder.CNT_FR_HOLIDAY;
		final int LEN = TOUCalendarBuilder.LEN_FR_HOLIDAY;
		multiWriteTable( data,CNT,LEN,MX2_DataConstants.TABLE_FIXED_RECURRING_HOLIDAY_SET1);
	}
	
	/**
	 * Non Recurring Holiday Write Table of one set.
	 * @param session
	 * @param data 데이터
	 * @return 
	 * @throws MX2Exception
	 */
	private void wNonRecurringHoliday(byte[] data)
			throws IOException, MX2Exception {
		final int CNT = TOUCalendarBuilder.CNT_NR_HOLIDAY;
		final int LEN = TOUCalendarBuilder.LEN_NR_HOLIDAY;
		multiWriteTable( data,CNT,LEN,MX2_DataConstants.TABLE_NON_RECURRING_HOLIDAY);
	}
	
	/**
	 * End Message Write Table.
	 * @param session
	 * @param data
	 * @throws MX2Exception 
	 */
	private void wEndMessage(byte[] data) throws IOException, MX2Exception{
		if(data.length != TOUCalendarBuilder.LEN_END_MESSAGE)
			throw new MX2Exception("invalid EndMessage of data param");
		
		// write table
		// retryableInvoke(M_WRITE_TABLE, MX2_DataConstants.TABLE_END_MESSAGE, data);
		writeTable( MX2_DataConstants.TABLE_END_MESSAGE, data);
	}
	

	/**
	 * retryableInvoke는 Method를 실행하면서 생길수 있는 MRPException 을 Catch하여, 재시도 한계(RETRY_COUNT_MAX)까지 재시도 한다.
	 * @param m 실행할 method
	 * @param objects 매개변수
	 * @return 실행된 method 의 return object
	 * @throws MRPException IoSession Object가 매개변수에 포함되지 않았거나, 재시도 한계를 넘어가면 발생된다.
	 */
	private Object retryableInvoke1(Method m, Object... objects)
			throws IOException, MX2Exception {

		Object obj = null;
		try {
			// 메소드 호출
			obj = m.invoke(this, objects);
			initRetryCount();
		} catch (IllegalArgumentException e) {
			log.error(e, e);
		} catch (IllegalAccessException e) {
			log.error(e, e);
		} catch (InvocationTargetException e) {
			log.error(e,e);
			// 메소드 실행중 Exception발생시
			if (e.getTargetException().getClass().equals(MX2Exception.class)) {
				
				
				if (retryIncrement() > RETRY_COUNT_MAX && socket.isConnected()) {
					throw new MX2Exception("Retry Count is Full.");
				} else {

					// 재시도 하기위해 session 을 초기화 한다.
					sendMessage( NAK());
					init();

					// 재귀하여 재시도 한다.
					return retryableInvoke1( m, objects);
				}
			} else {
				throw new MX2Exception(e.getMessage());
			}

		}
		return obj;
	}

	public byte[] eventlogReadWithHeader()
			throws MRPException {

		// 100개의 이벤트 로그를 가져온다.
		byte[] bEventLog = read(MX2_DataConstants.TABLE_EVENT_LOG, 0, 100);

		return carryHeader("EL".getBytes(), bEventLog);
	}

	/**
	 * Meter Info 읽어온다.
	 * 
	 * @param session
	 * @return
	 * @throws MRPException
	 */
	@SuppressWarnings("unused")
	private byte[] configureReadWithHeader()
			throws MRPException {

		ByteArrayOutputStream configureStream = new ByteArrayOutputStream();

		try {
			// Meter Model
			configureStream.write(MX2_DataConstants.MeterModel);
			
			// MeterSerial
			byte[] meterSerial = readMeterSerial();
			ByteBuffer buf = ByteBuffer.allocate(16);
			buf.put(meterSerial);
			buf.flip();
			configureStream.write(buf.array());
			buf.clear();
			log.debug("meter Serial : " + this.meterId);

			// Meter Time
			byte[] meterTime = read(MX2_DataConstants.TABLE_METER_TIME, 0, 0);
			
			// auto time sync
			if(this.autoTimeSync){
				Date meterDate = getBcdDate(meterTime, "yyMMddHHmmssWW");
				Calendar cal = Calendar.getInstance();
				Date nowTime = cal.getTime();
				
				Long diff = nowTime.getTime() - meterDate.getTime();
				if(diff < 0){
					diff = diff * -1; //정수로
				}
				diff = diff / 1000; //초(seconds)단위로
				
				//diff 검사
				if(this.timeDiffLimit < diff){
					//시간동기화
					CommandData cmdData = cmdSyncMeterTime();
					
					//미터시간을 다시 읽는다.
					meterTime = read(MX2_DataConstants.TABLE_METER_TIME, 0, 0);
				}
			}
			
			configureStream.write(meterTime);
			log.debug("meter time : " + Hex.getHexDump(meterTime));

			// Meter Rating : pw/f/v/b/max
			byte[] meterRating = read(MX2_DataConstants.TABLE_METER_RATING, 0, 0);
			log.debug("Meter Rating : " + Hex.getHexDump(meterRating));

			// Phase & Wires
			configureStream.write(meterRating[0]);

			//Reference Voltage 와 Reference Frequency 비트 위치가 서로 바뀐것 같음. 문서 오류인것 같음.
			// Reference Voltage
			configureStream.write(meterRating[2]);
			// Reference Frequency
			configureStream.write(meterRating[1]);
			
			// Basic Current
			configureStream.write(meterRating[3]);

			// Maximum Current
			configureStream.write(meterRating[4]);
			
			
			// Phase Angle
			byte[] phaseAngleV = read(MX2_DataConstants.TABLE_PHASE_ANGLE_V, 0, 0);
			configureStream.write(phaseAngleV);
			
			byte[] phaseAngleI = read(MX2_DataConstants.TABLE_PHASE_ANGLE_I, 0, 0);
			configureStream.write(phaseAngleI);
			
			// configureStream.flush();

			byte[] configureData = configureStream.toByteArray();

			configureStream.close();

			return carryHeader("MT".getBytes(), configureData);
		} catch (Exception e) {
			throw new MRPException(MRPError.ERR_CONNECTTOMETER_CLASS, e.getCause());
		}
	}

	/**
	 * 미터 시리얼 정보를 읽어온다.
	 * @param session
	 * @return
	 * @throws MRPException 
	 */
	private byte[] readMeterSerial() throws MRPException {
		byte[] meterSerial = read(MX2_DataConstants.TABLE_METER_ID, 0, 0);
		this.meterId = DataUtil.getBCDtoBytes(meterSerial);
		return meterSerial;
	}

	/**
	 * Time Sync 명령을 수행한다.
	 * 
	 * @param session
	 * @return
	 * @throws MRPException
	 */
	private CommandData cmdSyncMeterTime() throws MRPException {
		
		//타임싱크 결과
		ResultStatus rs = ResultStatus.SUCCESS;
		
		SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmss");
		String beforTime = null;
		String afterTime = null;
		
		try {
			// Get Meter Time
			byte[] bMeterTime = read(MX2_DataConstants.TABLE_METER_TIME, 0, 0);

			Date dMeterTime = getBcdDate(bMeterTime, "yyMMddHHmmssWW");
			beforTime = format.format(dMeterTime);

			// Time Sync 명령 전송
			afterTime = meterTimeSync();

			byte[] bBeforTime = byteArrayInIntegerForString(beforTime, 2);
			byte[] bAfterTime = byteArrayInIntegerForString(afterTime, 2);

			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			// 앞부분 5byte 무슨 역할인지 몰라서 공백처리
			bos.write(new byte[6]);

			// Befor Time
			bos.write(bBeforTime);

			// 무슨 역할인지 몰라서 공백처리
			bos.write(new byte[9]);

			// After Time
			bos.write(bAfterTime);

			byte[] resultByteArray = bos.toByteArray();

			bos.close();

			CommandData resultCommandData = new CommandData();
			resultCommandData.setCnt(new WORD(1));
			resultCommandData.setTotalLength(resultByteArray.length);
			resultCommandData.append(new SMIValue(new OID("10.1.0"), new OCTET(
					resultByteArray)));
			
			//시간 관련 미터 데이터를 업데이트 한다.
			updateMeterTimeData(afterTime,beforTime);
			
			return resultCommandData;

		} catch (Exception e) {
			rs = ResultStatus.FAIL;
			throw new MRPException(MRPError.ERR_CONNECTTOMETER_CLASS,
					"can not read meter time or invalid format - "
							+ e.toString());
		} finally {
			//타임싱크를 하고 나면 미터 정보를 갱신해주어야 한다.
			saveTimeSyncLog(afterTime,beforTime,rs);
		}

	}

	/**
	 * 미터 테이블의 시간 관련 정보를 업데이트 한다.
	 * @param afterTime
	 * @param beforTime
	 */
	private void updateMeterTimeData(String afterTime, String beforTime) {
		SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmss");
		Date _afterTime = null;
		Date _beforTime = null;
		Long timeDiff = null;

		// 미터에 변경된 시간 정보 반영
		Meter meter = getMeter(this.meterId);
		if (meter != null) {
			meter.setLastTimesyncDate(DateTimeUtil
					.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
			try {
				_afterTime = format.parse(afterTime);
				_beforTime = format.parse(beforTime);

				timeDiff = (_afterTime.getTime() - _beforTime.getTime()) / 1000;

				meter.setTimeDiff(timeDiff);

				saveMeter(meter);

			} catch (Exception e) {
				log.error(e, e);
			}
		}
		
	}

	/**
	 * 미터 정보중에 TimeSynch 관련 정보를 업데이트 한다.
	 * @param afterTime
	 * @param beforTime
	 * @param rs 
	 */
	private void saveTimeSyncLog(String afterTime, String beforTime, ResultStatus rs) {
		SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmss");
		SimpleDateFormat longFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		Date _afterTime = null;
		Date _beforTime = null;
		Long timeDiff = null;
		
		// Meter Time Sync Log 저장.
		try {
			_afterTime = format.parse(afterTime);
			_beforTime = format.parse(beforTime);
			timeDiff = (_afterTime.getTime() - _beforTime.getTime()) / 1000;
			
			Meter meter = getMeter(this.meterId);
			if(meter == null){
				log.info(String.format("can not found meter - meter serial : %s",this.meterId));
				return;
			}

			MeterTimeSyncLogDao mtslDao = (MeterTimeSyncLogDao) DataUtil.getBean("metertimesynclogDao");
			
			MeterTimeSyncLog mstsLog = new MeterTimeSyncLog();
			mstsLog.setAfterDate(longFormat.format(_afterTime));
			mstsLog.setBeforeDate(longFormat.format(_beforTime));
			mstsLog.setLocation(meter.getLocation());
			mstsLog.setMeter(meter);
			mstsLog.setMeterDate(mstsLog.getAfterDate());
			mstsLog.setSupplier(meter.getSupplier());
			mstsLog.setTimeDiff(timeDiff);
			mstsLog.setWriteDate(DateTimeUtil
					.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
			mstsLog.setResult(rs.name());
			mstsLog.setOperatorType(this.operatorType.name());
			mstsLog.setId(TimeUtil.getCurrentLongTime());

			mtslDao.add(mstsLog);
			mtslDao.flushAndClear();
		} catch (Exception e) {
			log.error(e, e);
		}
	}

	/**
	 * 변경된 미터 데이터 디비에 반영
	 * @param meter
	 */
	private void saveMeter(Meter meter) {
		try{
			MeterDao meterDao = (MeterDao) DataUtil.getBean("meterDao");
			meterDao.saveOrUpdate(meter);
			meterDao.flushAndClear();
		}catch(Exception e){
			log.error(e,e);
		}
	}

	/**
	 * 미터 정보를 읽어온다.
	 * @param meterSerial 미터 레코드id
	 * @return
	 */
	private Meter getMeter(String meterSerial) {
		try{
			MeterDao meterDao = (MeterDao) DataUtil.getBean("meterDao");
			Meter meter = meterDao.get(meterSerial);
			return meter;
		}catch(Exception e){
			log.error(e,e);
			return null;
		}
	}

	/**
	 * String 을 입력받에Byte Array 로 변환한다 이때 String 을 Integer로 컨버트한다.<br>
	 * ex)<br>
	 * value = "110101"<br>
	 * i = 2<br>
	 * "110101" => split 2 => "11", "01", "01"<br>
	 * parse integer => 11, 01, 01<br>
	 * parse byte array {0x11,0x01,0x01}<br>
	 * 
	 * @param value
	 * @param i
	 *            value값을 splite 할 단위다.
	 * @return
	 * @throws Exception
	 * @throws
	 */
	private byte[] byteArrayInIntegerForString(String value, int i)
			throws Exception {
		StringReader stringReader = new StringReader(value);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		char[] c = new char[i];

		// 읽을수 있을때까지 i길이만큼 char를 읽어온다.
		while (stringReader.read(c) != -1) {
			String str = new String(c);// String 으로 변환한다.
			int ival = Integer.valueOf(str); // int로 형변환한다.
			bos.write(ival);
		}
		stringReader.close();
		bos.flush();
		return bos.toByteArray();

	}

	/**
	 * 오늘 기준으로 dayCount 만큼 이동하여 Date 를 구한다. 단위는 day(하루) 다.
	 * 
	 * @param dayCount
	 * @return
	 */
	private Date previousOfToday(int dayCount) {
		Calendar today = Calendar.getInstance();
		// 오늘 날짜에서 dayCount 만큼 과거로 간다.
		today.add(Calendar.DAY_OF_MONTH, dayCount * (-1));
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		return today.getTime();
	}

	/**
	 * Record 범위를 지정해서 데이터를 읽어온다.<br>
	 * 0BA0 Table 에 4,800 ~ 4,561(240개) 범위의 데이터가 저장되며 <br>
	 * 과거 값들이 240개씩 저장된다. 0BA0, 0BA1, ...,0BB3(240 ~ 1).
	 * 
	 * @param session
	 * @param lpDataRange
	 * @return
	 * @throws MRPException
	 */
	@SuppressWarnings("unused")
	private byte[] lpReadWithHeader(RecordRange lpDataRange)
			throws MRPException {

		// 범위 값에서 240개씩 잘라야한다. 한테이블에 최대 240개 데이터가 저장된다.
		RecordRange[] ranges = getLpDataRanges(lpDataRange);
		
		rlog.debug("LP READ");
		

		try {
			ByteArrayOutputStream lpDataStream = new ByteArrayOutputStream();

			for (int i = 0; i < ranges.length; i++) {
				
				rlog.debug(String.format("Rnage : %d - %d",ranges[i].index,ranges[i].count));

				// 테이블 갯수가 19개
				if (i > 19)
					break;

				if (ranges[i] != null) {
					byte[] lp_byte = lpRead(
							(char) (MX2_DataConstants.TABLE_LOAD_RECENT_DATA + i),
							ranges[i].getIndex(), ranges[i].getCount())
							.toByteArray();
					lpDataStream
							.write(lp_byte);
					
					
				}
			}
			lpDataStream.flush();
			byte[] baLpData = lpDataStream.toByteArray();
			lpDataStream.close();

			return carryHeader("LD".getBytes(), baLpData);
		} catch (Exception e) {
			throw new MRPException(MRPError.ERR_CONNECTTOMETER_CLASS, e.getCause());
		}
	}

	private byte[] sapReadWithHeader() throws MRPException {
		ByteArrayOutputStream sapDataStream = new ByteArrayOutputStream();
		byte[] sapData = null;
		byte[] rtnData = null;
		try {

			// Character Code3
			byte[] charCode3 = read(MX2_DataConstants.TABLE_CHARACTER_CODE, 2, 1);
			sapDataStream.write(charCode3);

			// Long Character Code
			byte[] longCharCode = read(MX2_DataConstants.TABLE_LONG_CHARACTER_CODE, 0, 0);
			sapDataStream.write(longCharCode);

			// Daylight saving present time
			byte[] saving = read(MX2_DataConstants.TABLE_DAYLIGHT_SAVING_TIME, 0, 0);
			sapDataStream.write(saving);

			// Multiplier
			byte[] multiplier = read(MX2_DataConstants.TABLE_MULTIPLIER, 0, 0);
			sapDataStream.write(multiplier);
			
			// ErrorCode 추가.(기존 Billing 데이터를 검침할때 읽어오지만 여기서 다시 한번 읽어온다.
			byte[] errorCode = read(MX2_DataConstants.TABLE_ERROR_CODE, 0, 0);
			sapDataStream.write(errorCode);

			sapDataStream.flush();
			sapData = sapDataStream.toByteArray();
			sapDataStream.close();

			// 해더를 씌운다.
			rtnData = carryHeader("SA".getBytes(), sapData);
			return rtnData;
		} catch (IOException e) {
			throw new MRPException(MRPError.ERR_CONNECTTOMETER_CLASS, e.getCause());
		}
	}
	
	//Test_DisplaySetting을 읽어오기 위함
	public byte[] settingReadWithHeader() throws MRPException, IOException {
		ByteArrayOutputStream byteArry = new ByteArrayOutputStream();
		byte[] sapData = null;
		byte[] rtnData = null;
		try {

			byte[] normalItemSelect = read(MX2_DataConstants.TABLE_NORMAL_DISPLAY_ITEMS_SELECT, 0, 0);
			byteArry.write(normalItemSelect);
			
			byte[] normalIdSet1 = read(MX2_DataConstants.TABLE_NORMAL_DISPLAY_ID_SET1, 0, 0);
			byteArry.write(normalIdSet1);
			
			byte[] normalIdSet2 = read(MX2_DataConstants.TABLE_NORMAL_DISPLAY_ID_SET2, 0, 0);
			byteArry.write(normalIdSet2);

			byte[] alternateItemSelect = read(MX2_DataConstants.TABLE_ALTERVATE_DISPLAY_ITEMS_SELECT, 0, 0);
			byteArry.write(alternateItemSelect);

			byte[] alternateIdSet1 = read(MX2_DataConstants.TABLE_ALTERVATE_DISPLAY_ID_SET1, 0, 0);
			byteArry.write(alternateIdSet1);
			
			byte[] alternateIdSet2 = read(MX2_DataConstants.TABLE_ALTERVATE_DISPLAY_ID_SET2, 0, 0);
			byteArry.write(alternateIdSet2);
			
			byte[] testItemSelect = read(MX2_DataConstants.TABLE_TEST_DISPLAY_ITEMS_SELECT, 0, 0);
			byteArry.write(testItemSelect);

			byte[] testIdSet = read(MX2_DataConstants.TABLE_TEST_DISPLAY_ID, 0, 0);
			byteArry.write(testIdSet);
			
			byteArry.flush();
			sapData = byteArry.toByteArray();
			byteArry.close();

			// 해더를 씌운다.
			return sapData;
		} catch (IOException e) {
			throw new MRPException(MRPError.ERR_CONNECTTOMETER_CLASS, e
					.toString());
		}
	}
	
	/**
	 * byte array 데이터에 tableName | length 붙여 리턴한다.
	 * 
	 * @param tableName
	 *            2byte size 의 아스키 코드값이다.
	 * @param data
	 *            해더를 붙일 데이터. 2byte Length 는 앞에 자동으로 붙는다.
	 * @return [tableName | length | data]
	 * @throws MRPException
	 */
	private byte[] carryHeader(byte[] tableName, byte[] data)
			throws MRPException {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bos.write(tableName);
			bos.write(DataUtil.get2ByteToInt(data.length));
			bos.write(data);
			// bos.flush();
			byte[] headerWithData = bos.toByteArray();
			bos.close();
			return headerWithData;
		} catch (Exception e) {
			throw new MRPException(MRPError.ERR_CONNECTTOMETER_CLASS, e.getCause());
		}
	}

	/**
	 * lpData는 테이블당 240개의 Record 가 저장되어 있기 때문에 테이블 별로 범위를 계산해줄 필요가 있다. <br>
	 * 리턴되는 값은 최근값이 저장되는 테이블의 Range정보부터 과거 정보가 있는 테이블 범위들 까지 계산하여 넘겨준다.
	 * 
	 * @param lpDataRange
	 * @return
	 */
	private RecordRange[] getLpDataRanges(RecordRange lpDataRange) {
		int index = lpDataRange.getIndex();
		int count = lpDataRange.getCount();

		int tableSize = MX2_DataConstants.CNT_LPDATA_RECORD;
		int tableCount = new Double(Math.ceil((double)((double)(index + count) / (double)tableSize))).intValue();//올림
		int tableStartIndex = (index / tableSize);
		int point = index + count;

		RecordRange[] lpDataRanges = new RecordRange[tableCount];

		for (int i = tableStartIndex; i < tableCount; i++) {
			int tablePerIndex = index;
			int tablePerCount = 0;
			
			int interval = ((i+1) * tableSize);
			int bf_interval = (i * tableSize);
			if( interval > point){
				tablePerCount = point-bf_interval-tablePerIndex;
			}else {
				tablePerCount = tableSize - tablePerIndex;
			}		
			index=0;
			lpDataRanges[i] = new RecordRange(tablePerIndex, tablePerCount);
		}
		return lpDataRanges;
	}

	/**
	 * 가장 최근에 저장된 LpData의 Interval 을 가져온다.
	 * 
	 * @param session
	 * @return
	 * @throws MRPException 
	 */
	private int getLpDataInterval() throws MRPException {
		byte[] bLPData = read(MX2_DataConstants.TABLE_LOAD_RECENT_DATA, 0, 1);

		byte intervalByte = bLPData[10];
		return Integer.valueOf(String.format("%02x", intervalByte));
	}

	/**
	 * fromDate 와 toDate 범위에서 시작 Record 날짜(pointDate)를 대입하여 RecordRange를 구한다.<br>
	 * pointDate 는 toDate보다 작아야한다. 그렇지 않으면 계산할 필요가 없다.<br>
	 * 예제)<br>
	 * fromDate | toDate | pointDate | 결과<br>
	 * 20110101 | 20110110 | 20110103 | index = 0, count = 8<br>
	 * 20110105 | 20110110 | 20110103 | index = 2, count = 6<br>
	 * 
	 * @param fromDate
	 * @param toDate
	 * @param pointDate
	 * @return 범위에 속하지 못할경우 null
	 */
	private RecordRange getLpDateRange(Date fromDate, Date toDate,
			Date pointDate, int lpInterval) {
		// Record 날짜(pointDate)가 범위에 없다.
		if (fromDate.compareTo(pointDate) > 0) {
			return null;
		}

		long index = 0;
		long count = 0;

		if (toDate.compareTo(pointDate) < 0) {
			index = differenceOfDay(toDate, pointDate);
			count = differenceOfDay(fromDate, toDate);

		} else {
			count = differenceOfDay(fromDate, pointDate);
		}

		// lpData 의 경우 하루에 24시간중 n(Interval)분마다 검침을 하기때문에
		// RecordRange를 조정해줄필요가 있다
		int n = 24 * 60 / lpInterval;
		// index 및 Count 가 Integer 범위를 벗어나면 안된다. 미터 명령에 최대 0xffff(2byte)만
		// 가능하기때문에 그냥 int형으로 캐스팅한다.
		return new RecordRange((int) index * n, (int) count * n);
	}

	/**
	 * 두 날짜의 차이를 Day 로 환산한다.
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	private long differenceOfDay(Date date1, Date date2) {
		final int n24Hour = 24;
		final int n60Minute = 60;
		final int n60Seconds = 60;
		final int n_milli_seconds = 1000;

		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(date1);
		cal1.set(Calendar.HOUR_OF_DAY, 0);
		cal1.set(Calendar.MINUTE, 0);
		cal1.set(Calendar.SECOND, 0);

		cal2.setTime(date2);
		cal2.set(Calendar.HOUR_OF_DAY, 0);
		cal2.set(Calendar.MINUTE, 0);
		cal2.set(Calendar.SECOND, 0);
		// to date까지의 일수를 구할려면 다음날0시로 계산한다.
		cal2.add(Calendar.DAY_OF_MONTH, 1);

		date1 = cal1.getTime();
		date2 = cal2.getTime();

		long defference = date2.getTime() - date1.getTime();

		// 계산된 차이값이 밀리(milli) 초(seconds) 단위이기 때문에 Day 로 변환한다.
		long defferenceofday = defference
				/ (n24Hour * n60Minute * n60Seconds * n_milli_seconds);

		return defferenceofday;
	}

	/**
	 * 가장 최근에 기록된 lp Data 의 날짜를 구한다.<br>
	 * 다른거와 다르게 초(Seconds)는 기록되지 않는다.
	 * 
	 * @param session
	 * @return
	 * @throws MX2Exception
	 */
	private Date getLastLpdataDate() throws MX2Exception {
		try {
			byte[] lpData = read(MX2_DataConstants.TABLE_LOAD_RECENT_DATA, 0, 1);
			byte[] dateTime = new byte[5];
			System.arraycopy(lpData, 0, dateTime, 0, 5);

			// 초(Seconds, ss) 데이터는 없음.
			Date lastLpdataDate = getBcdDate(dateTime, "yyMMddHHmm");
			log.debug("last LP data Date : " + lastLpdataDate);
			return lastLpdataDate;
		} catch (Exception e) {
			log.error(e);
			throw new MX2Exception("get Last Lp data Date");
		}
	}

	/**
	 * BCD Type 의 byte array 를 Date 으로 변환해준다.
	 * 
	 * @param dateTime
	 * @param pattern
	 * @return
	 * @throws ParseException
	 */
	private Date getBcdDate(byte[] dateTime, String pattern)
			throws ParseException {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < dateTime.length; i++) {
			sb.append(String.format("%02x", dateTime[i]));
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		Date date = dateFormat.parse(sb.toString());
		return date;
	}

	/**
	 * ANSI Protocol 접속시 초기 Terminate, Identification, LogON, Security 과정을 진행한다.
	 * 
	 * @see com.aimir.fep.protocol.mrp.MeterProtocolHandler#initProcess(org.apache.mina.core.session.IoSession)
	 */
	public void init() throws MX2Exception {
		terminate();
		identRequest();
		negotiate();
		logon();
		security();
		updateAbsolutePointer();
	}

	private void updateAbsolutePointer() throws MX2Exception {
		try {
			byte[] data = new byte[] { 0x01 };
			IoBuffer buf = getWrite(MX2_DataConstants.TABLE_UPDATE_ABSOLUTE_POINTER, data);
			log.debug("Update Absolute Pointer");
			sendMessage( buf);
			updateAbsolutePointerReceive();
		} catch (Exception e) {
			log.error(e,e);
			throw new MX2Exception(e.getMessage());
		}
	}

	private void updateAbsolutePointerReceive()
			throws IOException, MX2Exception, InterruptedException {
		byte[] readData = readMessage();

		// 요청이 성공인지 판단한다.
		checkResponse(readData[0]);
		sendMessage( ACK());
	}
	
	/**
	 * Manual demand reset
	 * @param session
	 * @throws MX2Exception
	 */
	@SuppressWarnings("unused")
	private void demandReset() throws MX2Exception{
		try {
			byte[] data = new byte[] { 0x01 };
			IoBuffer buf = getWrite(MX2_DataConstants.TABLE_MANUAL_DEMAND_RESET, data);
			log.debug("Manual demand reset.");
			sendMessage( buf);
			
			byte[] readData = readMessage();
			// 요청이 성공인지 판단한다.
			checkResponse(readData[0]);
			sendMessage( ACK());
		} catch (Exception e) {
			log.warn(e.getMessage());
			throw new MX2Exception(e.getMessage());
		}
	}
	
	/**
	 * Clear Measuring and Load profile data
	 * @deprecated 사용주의! 모든데이터가 삭제됨
	 * @param session
	 * @throws MX2Exception 
	 */
	private void clearData(IoSession session) throws MX2Exception{
		try {
			byte[] data = new byte[] { 0x01 };
			IoBuffer buf = getWrite(MX2_DataConstants.TABLE_CLEAR_MEASUR_LP, data);
			log.debug("Clear Measuring and Load profile data.");
			sendMessage(buf);
			
			byte[] readData = readMessage();
			// 요청이 성공인지 판단한다.
			checkResponse(readData[0]);
			sendMessage(ACK());
		} catch (Exception e) {
			log.warn(e.getMessage());
			throw new MX2Exception(e.getMessage());
		}
	}
	
	/**
	 * Response 메시지를 확인하여 MX2Exception 을 생성한다.
	 * @param code
	 * @throws MX2Exception
	 */
	private void checkResponse(byte code) throws MX2Exception{
		log.debug("response code:"+code);
		switch (code) {
		case ANSI.OK:
			break;
		case ANSI.SNS:
			throw new MX2Exception(new Response(Response.Type.SNS,"Request code not match any meter service (after check all meter service)"));
		case ANSI.ISSS:
			throw new MX2Exception(new Response(Response.Type.ISSS,"Present state is not Session state"));
		case ANSI.IAR:
			throw new MX2Exception(new Response(Response.Type.IAR,"Invalid Table ID or Read-only Table"));
		case ANSI.ISC:
			throw new MX2Exception(new Response(Response.Type.ISC,"Insufficient Access level"));
		case ANSI.ONP:
			throw new MX2Exception(new Response(Response.Type.ONP,"Invalid data length or Invalid Index or Invalid element-count"));
		case ANSI.BSY:
			throw new MX2Exception(new Response(Response.Type.BSY,"Internal processing"));
		case ANSI.ERR:
			throw new MX2Exception(new Response(Response.Type.ERR,"error"));
		default:
			throw new MX2Exception(new Response(Response.Type.ERR,"Header format error"));
		}

	}

	private IoBuffer ACK() {
		IoBuffer ackBuff = IoBuffer.allocate(1);
		ackBuff.put(ANSI.ACK);
		ackBuff.flip();
		return ackBuff;
	}
	private IoBuffer NAK() {
		IoBuffer nakBuff = IoBuffer.allocate(1);
		nakBuff.put(ANSI.NAK);
		nakBuff.flip();
		return nakBuff;
	}

	/**
	 * 받은 데이터중에서 시퀀스 번호에 해당하는 값만 추출해서 가져온다.
	 * 
	 * @param allFrame
	 * @return
	 * @throws MRPException
	 */
	private byte getSeqNumber(byte[] allFrame) throws MRPException {
		byte seq_number = ANSI.OFS_SEQ_NBR - 1;

		// 프레임에 ACK 가 포함되어있는지 확인한다.
		if (allFrame[0] != ANSI.STP) {
			// 첫번째 비트가 STP(Start of packet character)가 아닐경우

			if (allFrame[0] == ANSI.ACK && allFrame[1] == ANSI.STP) {
				// 정상. 패킷에 ACK가 포함되어있음.
				seq_number = ANSI.OFS_SEQ_NBR;
			} else if (allFrame[0] == ANSI.NAK || allFrame[1] != ANSI.STP) {
				// 비정상 포멧 이거나 NAK 를 수신했을때.

				log.debug("header format error");

				// 포멧 에러 발생.
				throw new MRPException(MRPError.ERR_HEADER_FORMAT_ERROR,
						"Receive header format error");
			}
		}
		return seq_number;
	}

	/**
	 * 전체 프레임 중에 데이터 프레임 부분만 구해준다.<br>
	 * 프레임에 ACK 가 포함되어있는지 확인한다.
	 * 
	 * @param allFrame
	 * @return
	 * @throws MRPException
	 */
	private byte[] getDataFrame(byte[] allFrame) throws MX2Exception {
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
				throw new MX2Exception("Receive header format error");
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

	/**
	 * 메시지를 전송한다.
	 * 
	 * @param session
	 * @param buffer
	 * @throws MRPException
	 * @throws InterruptedException
	 */
	private void sendMessage(IoBuffer buffer)
			throws IOException, MX2Exception {
		
		// read 명령 사용 설정.
		// session.getConfig().setUseReadOperation(true);
	    // socket write
	    if (socket != null) {
    	    try {
    	        Thread.sleep(50);
    	    }
    	    catch (InterruptedException e) {}
    		// 메시지 전송
    		sout.write(buffer.array());
    		buffer.free();
	    }
	    else {
    		// read 명령 사용 설정.
            session.getConfig().setUseReadOperation(true);
    
            // 메시지 전송
            WriteFuture future = session.write(buffer);
    
            // 메시지가 전송 될때까지 기다림.
            future.awaitUninterruptibly(TIMEOUT * 1000);
            if (future.isWritten()) {
                // 전송 완료.
                buffer.free();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    log.error(e,e);
                }
            } else {
                // 전송 안됨.
                throw new MX2Exception("Error send message");
            }
	    }
	}

	/**
	 * 메시지를 기다리고 byte array 형태로 받는다.<br>
	 * 받은 데이터는 crc 채크하고 데이터 부분만 추출해서 넘겨준다.
	 * 
	 * @param session
	 * @return
	 * @throws MRPException
	 * @throws InterruptedException
	 */
	private byte[] readMessage() throws IOException, MX2Exception
	{
		ByteArray message = new ByteArray();

		boolean ACK = false;
		boolean isFinished = false;
		
		// socket read
		if (socket != null) {
    	    ByteArrayOutputStream out = null;
    	    byte[] bx = new byte[socket.getReceiveBufferSize()];
    	    int len = 0;
    	    // 메시지를 다받았는지
            
            int retry = 0;
    	    do {
    	        out = new ByteArrayOutputStream();
    	        try {
    	            len = sin.read(bx);
    	        }
    	        catch (SocketTimeoutException e) {
    	            if (retry != 10) retry++;
    	            else throw new MX2Exception("Read Time Out[3s]");
    	            
    	            if (out != null) out.close();
    	            continue;
    	        }
    	        out.write(bx, 0, len);
    	        
        		// 메시지를 받는다.
        		byte[] receiveMessage = out.toByteArray();
        		out.close();
        		
        		log.debug("RecevicedData[" + Hex.decode(receiveMessage) + "]");
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
        		}
        		else if (receiveMessage[0] == ANSI.NAK && !ACK) {
        			throw new MX2Exception("Receive NAK:" + Hex.getHexDump(receiveMessage));
        		}
        		else {
        			// 멀티 패킷일경우 다음패킷은 ACK가 오지 않는다 ACK받은걸로 처리.
        			ACK = true;
        
        			message.append(receiveMessage);
        		}
        		// 패킷 길이를 구해서 모두 받았는지 확인한다.
                isFinished = isReadDone(message.toByteArray());
                
    	    } while (!isFinished);
		}
		else {
		    ReadFuture future;
	        do {
	            // 메시지를 받는다.
	            future = session.read();
	            log.debug("wait for message");
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
	                    throw new MX2Exception("Receive NAK:" + Hex.getHexDump(receiveMessage));
	                } else {
	                    // 멀티 패킷일경우 다음패킷은 ACK가 오지 않는다 ACK받은걸로 처리.
	                    ACK = true;

	                    message.append(receiveMessage);
	                }
	                // 패킷 길이를 구해서 모두 받았는지 확인한다.
	                isFinished = isReadDone(message.toByteArray());
	            } else {
	                throw new MX2Exception(future.getException().getMessage());
	            }
	        } while (!isFinished);
		}
	    
		byte[] messageArray = message.toByteArray();

		// crc 를 확인해서 패킷에 이상이 없는지 검사한다.
		boolean crc_ok = crc_check(messageArray);
				
		if (!crc_ok) {
			log.debug("CRC Check Error. - Connection is " + (socket.isClosed()==true ? "Closed" : "Open"));
			throw new MX2Exception("CRC Check Error.");
		}
		
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
					sendMessage(ACK());
					// 재귀하여 sequence number 0이 될때까지 실행된다.
					bos.write(readMessage());
					byte[] packets = bos.toByteArray();
					bos.close();
					return packets;
				} catch (IOException e) {
					throw new MX2Exception("Read Multi Packet OutputStream Error");
				} 
			} else {
				return getDataFrame(messageArray);
			}
		}

		// 헤더부분을 잘라내고 넘긴다.
		return getDataFrame(messageArray);
	}

	/**
	 * RetryCount 초기화
	 */
	private void initRetryCount() {
		this.retryCount=0;
	}

	/**
	 * 1 증가후 리턴
	 * @return
	 */
	private int retryIncrement() {
		this.retryCount++;
		log.debug("Retry Count is " + this.retryCount);
		return this.retryCount;
	}

	/**
	 * 데이터 의 헤더 부분중 길이 정보를 읽어와 실제 길이와 같은지 확인한다.
	 * 
	 * @param data
	 * @return
	 */
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

	/**
	 * Identification service 요청을 보냄.
	 * 
	 * @param session
	 * @throws MRPException
	 */
	private void identRequest() throws MX2Exception {
		try {
			log.debug("IDENT");
			// request message 버퍼 생성.
			IoBuffer buf = getIdent();

			sendMessage(buf);

			// 메시지를 받는다.
			identReceive();

		} catch (Exception e) {
		    log.error(e, e);
			throw new MX2Exception(e.getCause());
		}
	}

	/**
	 * Identification service 요청에 대한 답을 받아 예외처리.
	 * 
	 * @param session
	 * @throws MRPException
	 */
	private void identReceive() throws MRPException {
		try {

			byte[] message = readMessage();

			log.debug("IDENT RESPONSE");

			// receive프레임을 생성
			MX2_ReceiveDataFrame receiveFrame = new MX2_ReceiveDataFrame(
					message);

			// Ident 프레임을 읽어온다.
			IdentTable revIdentTable = receiveFrame.getIdent();

			// 요청이 성공인지 판단한다.
			switch (revIdentTable.getStatus()) {
			case ANSI.OK:
				// ACK 보낸다.
				sendMessage(ACK());
				break;
			case ANSI.ISSS:
				throw new MRPException(MRPError.ERR_MODEMERROR,
						"Isss: Present state is not Base state");
			case ANSI.BSY:
				throw new MRPException(MRPError.ERR_MODEMERROR,
						"Bsy: Internal processing");
			case ANSI.ERR:
				throw new MRPException(MRPError.ERR_MODEMERROR, "Err: Unused");
			default:
				throw new MRPException(MRPError.ERR_MODEMERROR,
						"Err: Header format error");
			}
		} catch (Exception e) {
		    log.error(e, e);
			throw new MRPException(MRPError.ERR_READ_METER_CLASS,e.getCause());
		}
	}

	private boolean crc_check(byte[] message) {
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
	 * Negotiate service 요청.
	 * 
	 * @param session
	 * @throws MRPException
	 */
	private void negotiate() throws MX2Exception {
		try {
			log.debug("NEGOTIATE");
			IoBuffer buf = getNegotiate();

			sendMessage(buf);

			negotiateReceive();
		} catch (Exception e) {
		    log.error(e, e);
			throw new MX2Exception(e.getCause());
		}

	}

	/**
	 * Negotiate service 요청에 대한 답을 받아 예외처리.
	 * 
	 * @param session
	 * @throws MRPException
	 */
	private void negotiateReceive() throws MRPException {

		try {
			byte[] read = readMessage();

			// receive프레임을 생성
			MX2_ReceiveDataFrame receiveFrame = new MX2_ReceiveDataFrame(read);

			NegotiateTable negotiateTable = receiveFrame.getNegotiate();

			// 요청이 성공인지 판단한다.
			switch (negotiateTable.getStatus()) {
			case ANSI.OK:
				// ACK 보낸다.
				sendMessage(ACK());
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

		} catch (Exception e) {
		    log.error(e, e);
			throw new MRPException(MRPError.ERR_READ_METER_CLASS,e.getCause());
		}

	}

	/**
	 * Logon service 요청
	 * 
	 * @param session
	 * @throws MRPException
	 */
	private void logon() throws MX2Exception {
		try {
			log.debug("LOGON");
			IoBuffer buf = getLogon();
			sendMessage(buf);
			logonReceive();
		} catch (Exception e) {
		    log.error(e, e);
			throw new MX2Exception(e.getCause());
		}
	}

	/**
	 * Logon service 요청에 대한 답을 받아 예외처리.
	 * 
	 * @param session
	 * @throws MRPException
	 */
	private void logonReceive() throws MRPException {

		try {
			byte[] dataFrame = readMessage();

			// 요청이 성공인지 판단한다.
			switch (dataFrame[0]) {
			case ANSI.OK:
				// ACK 보낸다.
				sendMessage(ACK());
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

		} catch (Exception e) {
		    log.error(e, e);
			throw new MRPException(MRPError.ERR_READ_METER_CLASS,e.getCause());
		}

	}

	/**
	 * Security service 요청
	 * 
	 * @param session
	 * @throws MRPException
	 */
	private void security() throws MX2Exception {
		try {
			log.debug("SECURITY");
			IoBuffer buf = getSecurity();
			sendMessage(buf);
			securityReceive();
		} catch (Exception e) {
		    log.error(e, e);
			throw new MX2Exception(e.getCause());
		}
	}

	/**
	 * Security service 요청에 대한 답을 받아 예외처리.
	 * 
	 * @param session
	 * @throws MRPException
	 */
	private void securityReceive() throws MRPException {

		try {
			byte[] dataFrame = readMessage();

			// 요청이 성공인지 판단한다.
			switch (dataFrame[0]) {
			case ANSI.OK:
				// ACK 보낸다.
				sendMessage(ACK());
				break;
			case ANSI.SNS:
				throw new MRPException(MRPError.ERR_MODEMERROR,
						"sns: Request code not match any meter service (after check all meter service)");
			case ANSI.ISSS:
				throw new MRPException(MRPError.ERR_MODEMERROR,
						"Isss: Present state is not Session state or re-access without new logon");
			case ANSI.BSY:
				throw new MRPException(MRPError.ERR_MODEMERROR,
						"Bsy: Internal processing");
			case ANSI.ERR:
				throw new MRPException(MRPError.ERR_MODEMERROR,
						"Err: Invalid password");
			default:
				throw new MRPException(MRPError.ERR_MODEMERROR,
						"Err: Header format error");
			}

		}catch (Exception e) {
		    log.error(e, e);
			throw new MRPException(MRPError.ERR_READ_METER_CLASS,e.getCause());
		}

	}

	/**
	 * Logoff 요청
	 * 
	 * @param session
	 * @throws MRPException
	 */
	private void logoff() throws MRPException {
		try {
			IoBuffer buf = getLogoff();
			log.debug("Logoff");
			sendMessage(buf);
			logoffReceive();
		} catch (Exception e) {
			throw new MRPException(MRPError.ERR_HEADER_FORMAT_ERROR, e.getCause());
		}
	}

	/**
	 * Security service 요청에 대한 답을 받아 예외처리.
	 * 
	 * @param session
	 * @throws MRPException
	 */
	private void logoffReceive() throws MRPException {

		try {
			byte[] dataFrame = readMessage();

			// 요청이 성공인지 판단한다.
			switch (dataFrame[0]) {
			case ANSI.OK:
				// ACK 보낸다.
				sendMessage(ACK());
				break;
			case ANSI.ISSS:
				throw new MRPException(MRPError.ERR_MODEMERROR,
						"Isss: Present state is not Session state");
			case ANSI.BSY:
				throw new MRPException(MRPError.ERR_MODEMERROR,
						"Bsy: Internal processing");
			case ANSI.ERR:
				throw new MRPException(MRPError.ERR_MODEMERROR, "Err: Unused");
			default:
				throw new MRPException(MRPError.ERR_MODEMERROR,
						"Err: Header format error");
			}

		} catch (Exception e) {
			throw new MRPException(MRPError.ERR_READ_METER_CLASS,e.getCause());
		}

	}

	/**
	 * wait 요청<br>
	 * 대기 시간을 설정해서 세션이 자동 종료 되는것을 막는다.
	 * 
	 * @param session
	 * @param seconds
	 *            초단위(seconds), 0 으로 설정할경우 아무 영향도 주지 않는다.
	 * @throws MRPException
	 */
	private void wait(int seconds) throws MRPException {
		try {
			IoBuffer buf = getWait(seconds);
			log.debug("Wait");
			sendMessage(buf);
			waitReceive();
		} catch (Exception e) {
			throw new MRPException(MRPError.ERR_HEADER_FORMAT_ERROR, e.getCause());
		}
	}

	/**
	 * wait service 요청에 대한 답을 받아 예외처리.
	 * 
	 * @param session
	 * @throws MRPException
	 */
	private void waitReceive() throws MRPException {

		try {
			byte[] dataFrame = readMessage();

			// 요청이 성공인지 판단한다.
			switch (dataFrame[0]) {
			case ANSI.OK:
				// ACK 보낸다.
				sendMessage(ACK());
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
				throw new MRPException(MRPError.ERR_MODEMERROR,
						"Err: time is 0");
			default:
				throw new MRPException(MRPError.ERR_MODEMERROR,
						"Err: Header format error");
			}

		}catch (Exception e) {
			throw new MRPException(MRPError.ERR_READ_METER_CLASS,e.getCause());
		}

	}

	/**
	 * Terminate 요청<br>
	 * 
	 * @param session
	 * @throws MRPException
	 */
	private void terminate() throws MX2Exception {
		try {

			log.debug("TERMINATE");

			// request message 버퍼 생성.
			IoBuffer buf = getTerminate();

			sendMessage(buf);

			// 메시지를 받는다.
			terminateReceive();
		} catch (Exception e) {
			throw new MX2Exception(e.getCause());
		}
	}

	/**
	 * terminate service 요청에 대한 답을 받아 예외처리.
	 * 
	 * @param session
	 * @throws MRPException
	 */
	private void terminateReceive() throws MRPException {

		try {

			byte[] dataFrame = readMessage();

			// 요청이 성공인지 판단한다.
			switch (dataFrame[0]) {
			case ANSI.OK:
				// ACK 보낸다.
				sendMessage(ACK());
				break;
			case ANSI.SNS:
				throw new MRPException(MRPError.ERR_MODEMERROR, "sns: Unused");
			case ANSI.ERR:
				throw new MRPException(MRPError.ERR_MODEMERROR, "Err: Unused");
			default:
				throw new MRPException(MRPError.ERR_MODEMERROR,
						"Err: Header format error");
			}

		}catch (Exception e) {
		    log.error(e, e);
			throw new MRPException(MRPError.ERR_READ_METER_CLASS,e.getCause());
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
	private byte[] read(char tableid, int index, int count)
			throws MRPException {
		try {
			IoBuffer buf = getRead(tableid, index, count);
			log.debug("Read Service");
			sendMessage(buf);
			return readTableData();
		} catch (Exception e) {
		    log.error(e, e);
			throw new MRPException(MRPError.ERR_HEADER_FORMAT_ERROR, e.getCause());
		}
	}

	/**
	 * 요청한 테이블의 데이터를 오류 검사하고 데이터 부분만 추출해서 반환한다.
	 * 
	 * @param allData
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws MX2Exception 
	 */
	private byte[] readTableData() throws 
			IOException, InterruptedException, MX2Exception {
		byte[] readData = readMessage();

		// 요청이 성공인지 판단한다.
		checkResponse(readData[0]);
		sendMessage(ACK());

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
			throw new MX2Exception("Check Sum Error.");
		}
		return dataLayer;
	}

	/**
	 * Byte Array 를 int 로 변환해준다. 4byte 초과일경우 변환하지 않고 -1 리턴
	 * 
	 * @param byteArray
	 * @return
	 */
	private int byteArrayToInt(byte[] byteArray) {
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
	public void setGroupNumber(String groupNumber) {
		this.groupId = groupNumber;

	}

	@Override
	public void setMemberNumber(String memberNumber) {
		this.memberId = memberNumber;

	}

	@Override
	public void setModemId(String modemId) {
		this.modemId = modemId;

	}

	/**
	 * 지정된 영역의 Billing Data 를 읽어온다.<br>
	 * 검침된 다수의 데이터들은 각각 1개의 Table로 생성되어 ByteArray로 직렬화 된다.<br>
	 * ex)2개의 데이터를 읽었다면 [BD|Length|...][BD|Length|...] 형태로 ByteArray가 된다.
	 * 
	 * @param session
	 * @param index
	 *            Billing Date&Time Table기준으로 Index만큼 이동한다.
	 * @param count
	 *            이동된 Index부터 count 만큼 데이터를 가져온다.
	 * @return Billing Data Tables(IF4)
	 * @throws MRPException
	 */
	private byte[] billReadWithHeader() throws MRPException {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		try {

			// Billing Date Time
			byte[] billingDateTime = read(
					MX2_DataConstants.TABLE_BILLINGDATE_TIME, 0, 1);
			log.debug("billingDateTime:" + Hex.getHexDump(billingDateTime));
			bos.write(billingDateTime);
			billingDateTime = null;

			// Present Energy total and Rate 1 - 3 까지의 데이터를 요청한다.
			byte[] presentEnergy = readChannelsData(
					MX2_DataConstants.TABLE_PRESENT_ENERGY, 0,
					NECESSARY_RATE_COUNT);
			bos.write(presentEnergy);
			presentEnergy = null;

			// Previous Max DM<br>
			byte[] previousMaxDM = readChannelsData(
					MX2_DataConstants.TABLE_PREVIOUS_MAX_DM, 0,
					NECESSARY_RATE_COUNT);
			bos.write(previousMaxDM);
			previousMaxDM = null;

			// Previous Energy
			byte[] previousEnergy = readChannelsData(
					MX2_DataConstants.TABLE_PREVIOUS_ENERGY, 0,
					NECESSARY_RATE_COUNT);
			bos.write(previousEnergy);
			previousEnergy = null;

			// Multiplier byte 추가.
			byte[] multiplier = read(
					MX2_DataConstants.TABLE_MULTIPLIER, 0, 0);
			log.debug("MULTIPLIER : " + Hex.getHexDump(multiplier));
			bos.write(multiplier);
			multiplier = null;

			// ErrorCode 추가.
			byte[] errorCode = read(
					MX2_DataConstants.TABLE_ERROR_CODE, 0, 0);
			bos.write(errorCode);
			errorCode = null;
			bos.flush();

			byte[] billingData = bos.toByteArray();
			bos.close();

			return carryHeader("BD".getBytes(), billingData);

		} catch (Exception e) {
		    log.error(e, e);
			throw new MRPException(MRPError.ERR_HEADER_FORMAT_ERROR, e.getCause());
		}
	}

	/**
	 * 
	 * @deprecated use {@link #billReadWithHeader(IoSession)}
	 * @param baBillingData
	 */
	@Override
	public ByteArray billRead(IoSession session) throws MRPException {
		return null;
	}

	/**
	 * 채널별로 Table Id가 달라 인덱스값을 증감하여 불러온다.<br>
	 * channelIndexs<br>
	 * 0 = W(imp)<br>
	 * 1 = W(exp)<br>
	 * 3 = var(Q1)<br>
	 * 4 = var(Q2)<br>
	 * 5 = var(Q3)<br>
	 * 6 = var(Q4)<br>
	 * 
	 * @param session
	 * @param TableId
	 * @param index
	 * @param count
	 * @return
	 * @throws IOException
	 * @throws MRPException
	 */
	private byte[] readChannelsData(char TableId, int index,
			int count) throws IOException, MRPException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int[] channelIndexs = MX2_DataConstants.CHANNEL_IDXS;
		byte[] channelData;
		for (int i = 0; i < channelIndexs.length; i++) {
			char tableid = (char) (TableId + channelIndexs[i]);

			channelData = read(tableid, index * RATE_COUNT, count);
			log.debug("readChannelsData(" + i + ") : "
					+ Hex.getHexDump(channelData));
			bos.write(channelData);
		}
		return bos.toByteArray();
	}

	/**
	 * crc 오류를 채크한다.
	 * 
	 * @deprecated 이 메소드를 사용하지 않는다. 패킷 받는 부분에서 일괄적으로 검사함.
	 * @return false always false
	 */
	@Override
	public boolean checkCRC(byte[] src) throws MRPException {
		// crc는 다른곳에서 채크한다.
		return false;
	}

	/**
	 * @deprecated use configureReadWithHeader(IoSession)
	 */
	@Override
	public ByteArray configureRead(IoSession session) throws MRPException {
		return null;
	}

	/**
	 * @deprecated 사용하지 않음 use {@link #billReadWithHeader(IoSession)}
	 * @see com.aimir.fep.protocol.mrp.MeterProtocolHandler#currbillRead(org.apache.mina.core.session.IoSession)
	 */
	@Override
	public ByteArray currbillRead(IoSession session) throws MRPException {
		return null;
	}

	/**
	 * 최근 저장된 Event Log Data 를 100개 불러온다.(IF4 문서 참고)
	 * 
	 * @deprecated use {@link #eventlogReadWithHeader(IoSession)}
	 * @see com.aimir.fep.protocol.mrp.MeterProtocolHandler#eventlogRead(org.apache.mina.core.session.IoSession)
	 */
	@Override
	public ByteArray eventlogRead(IoSession session) throws MRPException {
		return null;
	}

	/**
	 * @deprecated 사용하지 않음
	 * @see com.aimir.fep.protocol.mrp.MeterProtocolHandler#instRead(org.apache.mina.core.session.IoSession)
	 */
	@Override
	public ByteArray instRead(IoSession session) throws MRPException {
		return null;
	}

	/**
	 * 최근 lp data 를 읽어온다. 24시간 96개의 Record 를 기본으로 가져온다.
	 * 
	 * @param session
	 * @return
	 * @throws MRPException
	 */
	private ByteArray lpRead() throws MRPException {
		return lpRead( MX2_DataConstants.TABLE_LOAD_RECENT_DATA, 0, 96);
	}

	/**
	 * lp data 지정 영역을 읽어온다.
	 * 
	 * @param session
	 * @param pos
	 *            recode start index
	 * @param len
	 *            recode count
	 * @return
	 * @throws MRPException
	 */
	private ByteArray lpRead(char tableId, int pos, int len)
			throws MRPException {

		ByteArray allLpData = new ByteArray();

		byte[] bLPData = read(tableId, pos, len);

		// 레코드가 1개 이상일때 단일 사이즈만큼 읽어와 각각 처리한다.
		ByteArrayInputStream bis = new ByteArrayInputStream(bLPData);
		try {
			while (bis.available() > 0) {
				byte[] lpdata = new byte[97]; // Record Size : 97
				bis.read(lpdata);
				byte[] datetime = Arrays.copyOf(lpdata, 5);
				log.debug(String.format("lpdata Time : [%s] %s", Hex
						.getHexDump(DataUtil.get2ByteToChar(tableId)), Hex
						.getHexDump(datetime)));

				// 기록된 데이터가 없을경우 0xff값이 전송된다.
				if (lpdata[0] == -1) {
					// 이 이후의 값들은 없기때문에 빠져나간다.
					break;
				}
				
				//Normal Incompletion

				allLpData.append(lpdata);
			}

			bis.close();

		} catch (Exception e) {
		    log.error(e, e);
			throw new MRPException(MRPError.ERR_LPREAD_CLASS, e.getCause());
		}

		return allLpData;
	}

	/**
	 * @deprecated 사용하지 않음 {@link #lpRead(IoSession)} 를 사용함.
	 * @param session
	 * @param startday
	 * @param endday
	 * @param lpCycle
	 * @return
	 * @throws MRPException
	 */
	@Override
	public ByteArray lpRead(IoSession session, String startday, String endday,
			int lpCycle) throws MRPException {
		return null;
	}

	/**
	 * @deprecated 사용하지 않음
	 * @param session
	 * @return
	 * @throws MRPException
	 */
	@SuppressWarnings("unchecked")
	public HashMap paramSet(IoSession session) throws MRPException {
		return null;
	}

	/**
	 * @deprecated 사용하지 않음
	 * @see com.aimir.fep.protocol.mrp.MeterProtocolHandler#pqRead(org.apache.mina.core.session.IoSession)
	 */
	@Override
	public ByteArray pqRead(IoSession session) throws MRPException {
		return null;
	}

	/**
	 * @deprecated 사용하지 않음
	 * @see com.aimir.fep.protocol.mrp.MeterProtocolHandler#quit()
	 */
	@Override
	public void quit() throws MRPException {
	}

	/**
	 * @see com.aimir.fep.protocol.mrp.MeterProtocolHandler#setMcuSwVersion(java.lang.String)
	 */
	@Override
	public void setMcuSwVersion(String mcuSwVersion) {
		this.mcuSwVersion = mcuSwVersion;

	}

	/**
	 * @deprecated 사용하지 않음
	 * @see com.aimir.fep.protocol.mrp.MeterProtocolHandler#timeCheck(org.apache.mina.core.session.IoSession)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public HashMap timeCheck(IoSession session) throws MRPException {
		return null;
	}

	/**
	 * 미터 시간을 서버 시간으로 동기화 한다.
	 * 
	 * @param session
	 * @return After Time
	 * @throws MX2Exception
	 */
	private String meterTimeSync() throws IOException, MX2Exception {
		IoBuffer buf = IoBuffer.allocate(13);
			// write 명령어
			buf.put((byte) 0x40);

			// table id
			buf.put(DataUtil.get2ByteToChar(MX2_DataConstants.TABLE_TIMESYNCH));

			// count
			buf.put(DataUtil.get2ByteToInt(7));

			// data
			SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
			SimpleDateFormat resultFormat = new SimpleDateFormat("yyMMddHHmmss");
			Calendar cal = Calendar.getInstance();
			Date nowTime = cal.getTime();
			
			//weeks number
			int dow = cal.get(Calendar.DAY_OF_WEEK);
			String currentTime = sdf.format(nowTime);
			// java는 sun ~ sat (1~7) 이기 때문에 미터 설정으로 변경 mon~sun(0~6)
			switch(dow){
			case 1: //sun
				currentTime = String.format("%s%02d", currentTime,6);
				break;
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
				currentTime = String.format("%s%02d", currentTime,dow-2);
				break;
			}
			
			String resultTime = resultFormat.format(nowTime);
			byte[] bcd = null;
			try {
				bcd = DataUtil.getBCD(currentTime);
				buf.put(bcd);
			} catch (IOException e) {
				log.error(e,e);
				throw new MX2Exception(e.getMessage());
			}
			// cksum
			byte calChecksum = ANSI.chkSum(bcd, 0, bcd.length);
			buf.put(calChecksum);
			buf.flip();

			// ANSI 해더와 합친다.
			byte[] timesyncPacket = carryFrame(buf.array());
			buf.free();
			buf = IoBuffer.allocate(timesyncPacket.length);
			buf.put(timesyncPacket);
			buf.flip();
			log.debug("time sync : " + Hex.getHexDump(buf.array()));
			sendMessage(buf);
			writeServiceReceive();

			return resultTime;
	}
	
	/**
	 * Summer Time 설정.
	 * @param session
	 * @param st Date Array(Index 0=start date, 1=end date) List.
	 * @return
	 */
	private CommandData setSummerTime(SummerTime st) {
		log.debug("setSummerTime");
		CommandData cmdd = new CommandData();
		try {
			byte[] set1 = getSummerTimeDateSet(st.getDateSet1());
			byte[] set2 = getSummerTimeDateSet(st.getDateSet2());
			byte[] set3 = getSummerTimeDateSet(st.getDateSet3());
			
			// retryableInvoke(M_WRITE_TABLE, MX2_DataConstants.TABLE_SUMMERTIME1,set1); 
			// retryableInvoke(M_WRITE_TABLE, MX2_DataConstants.TABLE_SUMMERTIME2,set2);
			// retryableInvoke(M_WRITE_TABLE, MX2_DataConstants.TABLE_SUMMERTIME3,set3);
			writeTable(MX2_DataConstants.TABLE_SUMMERTIME1,set1);
			writeTable(MX2_DataConstants.TABLE_SUMMERTIME2,set2);
			writeTable(MX2_DataConstants.TABLE_SUMMERTIME3,set3);
			
		}catch (MX2Exception mx2e){
			log.error(mx2e,mx2e);
			cmdd.setResponse(mx2e.getResponse());
			return cmdd;
		}
		catch (Exception e) {
			log.error(e,e);
			Response r = new Response(Response.Type.ERR,e.getMessage());
			cmdd.setResponse(r);
			return cmdd;
		}
		
		Response r = new Response(Response.Type.OK,"Sucess");
		cmdd.setResponse(r);
		cmdd.setSvc("C".getBytes()[0]); // Code of (C)Command
		return cmdd;
	}
	
	private byte[] getSummerTimeDateSet(List<SummerTimeDateSet> dateSets)
			throws Exception {
		final int tableSize = 40;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		SimpleDateFormat sf = new SimpleDateFormat("yyMMddHH");

		for (SummerTimeDateSet dateSet : dateSets) {

			// summer time table 의 크기는 40 byte이다.
			if (bos.size() == tableSize) {
				break;
			} else if (dateSet.getStartDate() != null
					&& dateSet.getEndDate() != null) {
				Date startDate = dateSet.getStartDate();
				Date endDate = dateSet.getEndDate();

				byte[] sBCD = DataUtil.getBCD(sf.format(startDate));
				byte[] eBCD = DataUtil.getBCD(sf.format(endDate));

				bos.write(sBCD);
				bos.write(eBCD);

			} else {
				continue;
			}
		}
		bos.flush();

		byte[] bcds = bos.toByteArray();

		byte[] data = DataUtil.fillCopy(bcds, (byte) 0xff, tableSize);
		return data;
	}

	/**
	 * @deprecated 사용안함
	 * @param session
	 * @param timethreshold
	 * 
	 * @see com.aimir.fep.protocol.mrp.MeterProtocolHandler#timeSync(org.apache.mina.core.session.IoSession,
	 *      int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public HashMap timeSync(IoSession session, int timethreshold)
			throws MRPException {
		return null;

	}

	/**
	 * Write Service Request 에대한 Response 데이터를 처리한다.
	 * 
	 * @param session
	 * @return
	 * @throws MRPException
	 * @throws MX2Exception 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void writeServiceReceive() throws IOException, MX2Exception{
		byte[] readData = readMessage();

		// 요청이 성공인지 판단한다.
		checkResponse(readData[0]);
		sendMessage(ACK());
	}

	public void setModemNumber(String modemId) {
		this.modemId = modemId;
	}

	private void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	private void setMemberId(String memberId) {
		this.memberId = memberId;
	}
	
	/**
	 * 특정 byte로 초기화된 새로운 byte array를 생성한다.
	 * @param size 생성될 array size
	 * @param fill 초기화 시킬 byte
	 * @return 생성된 byte array
	 */
	private byte[] makeByteArray(int size, byte fill){
		byte[] newByte = new byte[size];
		for (int i = 0; i < newByte.length; i++) {
			newByte[i] = fill;
		}
		return newByte;
	}

	@Override
	public void initProcess(IoSession session) throws MRPException {
	}
	/** 
	 * 보낸 데이터 총 길이
	 */
	@Override
	public long getSendBytes(){
		return 0; // this._session.getWrittenBytes();
	}
	/**
	 * 받은 데이터 총 길이
	 * @return
	 */
	private long getReceiveBytes(){
		return 0; // this._session.getReadBytes();
	}

	private OperatorType getOperatorType() {
		return operatorType;
	}

	private void setOperatorType(OperatorType operatorType) {
		this.operatorType = operatorType;
	}
	
	/**
     * goggleBit의 경우 5 번째 비트를 설정한다. 0010 0000(0x20)<br>
     * xor 비트 연산을 하여 토글 되는 효과를 볼 수 있다.
     * 
     * @return 0x00 or 0x20 값이 토글(switching) 된다.
     */
    private byte getToggleBit() {
        toggleBit = (byte) ((byte) toggleBit ^ 0x20);
        return toggleBit;
    }

    /**
     * ANSI C12.22 Header 정의부분 (자세한 내용은 프로토콜 문서 참조)
     * 
     * @return length 를 제외한 Head부분
     */
    private byte[] getHeader() {
        byte[] rtnByte = new byte[4];
        rtnByte[0] = ANSI.STP;
        rtnByte[1] = ANSI.REV_OPTICAL;
        rtnByte[2] = getToggleBit();
        rtnByte[3] = 0x00;
        return rtnByte;
    }

    /**
     * Int 형 변수를 Word16 형태로 변환하는 기능을 한다.<br>
     * Word16은 16bit 형식의 byte형 변수라고 생각하면된다. Word16형식이 없기때문에 8bit Byte형 변수를
     * 배열(size 2)형태로 리턴한다. Shift 하여 8bit씩 나누어 배열에 각각 저장한다. 8비트에 벗어난 비트들은 byte로
     * 캐스팅하면서 자동으로 소멸한다.
     * 
     * @param number
     *            변환할 int형 변수
     * @return int형 값을 8비트씩 2개로 나누어 리턴한다.(Word16 은 16비트형 변수)
     */
    private byte[] intToWord16(int number) {
        byte[] rtnByte = new byte[2];
        rtnByte[0] = (byte) (number >> 8);
        rtnByte[1] = (byte) (number);
        return rtnByte;
    }

    /**
     * char 타입을 Word16으로 변환.
     * 
     * @param c
     *            type of char
     * @return bytes
     * @see intToWord16
     */
    private byte[] charToWord16(char c) {
        byte[] rtnByte = new byte[2];
        rtnByte[0] = (byte) (c >> 8);
        rtnByte[1] = (byte) (c);
        return rtnByte;
    }

    /**
     * 헤더프레임과 데이터프레임, crc 프레임을 합친다.
     * 
     * @param dataFrame
     * @return
     */
    private byte[] carryFrame(byte[] dataFrame) {

        ByteArray headerFrame = new ByteArray();

        // head
        headerFrame.append(getHeader());

        // length : 데이터 프레임의 길이를 설정한다.
        headerFrame.append(intToWord16(dataFrame.length));

        // 데이터 프레임을 추가한다.
        headerFrame.append(dataFrame);

        // 데이터 프레임 사이즈
        int frame_size = headerFrame.toByteArray().length;

        // crc
        char crc = ANSI.crc(frame_size, (byte) 0, headerFrame.toByteArray());
        headerFrame.append(charToWord16(crc));

        return headerFrame.toByteArray();
    }

    /**
     * 데이터 프레임을 입력받아 해더에 싣고 버퍼를 리턴한다.
     * 
     * @param dataFrame
     * @return
     */
    private IoBuffer getBuffer(byte[] dataFrame) {
        // 헤더프레임과 데이터프레임, crc 프레임을 합친다. |Heder|..data..|crc|
        byte[] allFrame = carryFrame(dataFrame);

        // 완성된 데이터와 헤더를 합쳐 프레임을 생성한다.
        IoBuffer buf = IoBuffer.allocate(allFrame.length);
        buf.put(allFrame);
        buf.flip();
        return buf;
    }

    /**
     * ANSI Protocol Identification Service<br>
     * 서비스에 필요한 프레임을 설정한다.
     * 
     * @return
     * @see MX2_AMR_Communication_Specification.pdf:46p
     */
    private IoBuffer getIdent() {

        byte[] dataFrame = new byte[] { ANSI.IDENT };

        // 헤더프레임과 데이터프레임, crc 프레임을 합친다. |Heder|..data..|crc|
        byte[] allFrame = carryFrame(dataFrame);

        // 완성된 데이터와 헤더를 합쳐 프레임을 생성한다.
        IoBuffer buf = IoBuffer.allocate(allFrame.length);
        buf.put(allFrame);
        buf.flip();

        return buf;
    }

    /**
     * Negotiate service 에 필요한 Request 프레임을 생성한다.
     * 
     * @return 작성된 프레임의 버퍼를 리턴한다.
     * @see MX2_AMR_Communication_Specification.pdf:50p
     */
    private IoBuffer getNegotiate() {

        // 데이터 프레임 부분에 들어갈 데이터들을 정의한다.
        ByteArray dataFrame = new ByteArray();

        // baud-rate-selector 를 설정하는데 MX2 장비에서는 61H 로 설정한다.
        dataFrame.append(ANSI.NEGO_B1);

        // packet-size : 최대 패킷 사이즈를 설정한다. 0x80(128byte). / RAM에 다른 사이즈의 동적 데이터의
        // 분할을 방지하기 위해 설정한다.
        dataFrame.append((byte) 0x00);
        dataFrame.append((byte) 0x80);

        // nbr-packets : 한번에 전송할 패킷 겟수를 설정하는데 이 장비에서는 항상 1개의 패킷만 보낼 수 있도록 0x01 로
        // 설정하도록 되어 있다.
        dataFrame.append((byte) 0x01);

        // baud-rate : 전송 속도를 설정하는데 MX2 에서는 0x06(9,600 baud), 0x08(19,200 baud)
        // 를 택일한다.
        dataFrame.append((byte) 0x08);

        return getBuffer(dataFrame.toByteArray());
    }

    /**
     * Logon service 에 필요한 Request 프레임을 생성한다.
     * 
     * @return 작성된 프레임의 버퍼를 리턴한다.
     * @see MX2_AMR_Communication_Specification.pdf:48p
     */
    private IoBuffer getLogon() {
        // 데이터 프레임 부분에 들어갈 데이터들을 정의한다.
        ByteArray dataFrame = new ByteArray();

        // Logon 명령
        dataFrame.append(ANSI.LOGON);

        // User id
        dataFrame.append(USER_ID);

        // User identification : 유저 이름일수도 있고 인식 코드일수도 있다.(ASCII * 10 char)
        dataFrame.append(USER_NAME);

        return getBuffer(dataFrame.toByteArray());
    }

    /**
     * Security service 에 필요한 Request 프레임을 생성한다.
     * 
     * @return
     * @see MX2_AMR_Communication_Specification.pdf:49p
     */
    private IoBuffer getSecurity() {
        // 데이터 프레임 부분에 들어갈 데이터들을 정의한다.
        ByteArray dataFrame = new ByteArray();

        // Security 명령
        dataFrame.append(ANSI.OPT_AUTH);

        // password
        String password = FMPProperty.getProperty(
                "Circuit.Meter.Security.AuthCode.MX2", "00000000000000000000");
        dataFrame.append(password.getBytes());

        return getBuffer(dataFrame.toByteArray());
    }

    /**
     * Logoff service 에 필요한 Request 프레임을 생성한다.
     * 
     * @return
     * @see MX2_AMR_Communication_Specification.pdf:50p
     */
    private IoBuffer getLogoff() {
        // 데이터 프레임 부분에 들어갈 데이터들을 정의한다.
        ByteArray dataFrame = new ByteArray();

        // Logoff 명령
        dataFrame.append(ANSI.LOGOFF);

        return getBuffer(dataFrame.toByteArray());
    }

    /**
     * Wait service 에 필요한 Request 프레임을 생성한다.<br>
     * 
     * @param seconds
     *            초(seconds) 단위
     * @return
     * @see MX2_AMR_Communication_Specification.pdf:51p
     */
    private IoBuffer getWait(int seconds) {
        // 데이터 프레임 부분에 들어갈 데이터들을 정의한다.
        ByteArray dataFrame = new ByteArray();

        // Wait 명령
        dataFrame.append(ANSI.WAIT);

        // time
        dataFrame.append((byte) seconds);

        return getBuffer(dataFrame.toByteArray());
    }

    /**
     * Terminate service 에 필요한 Request 프레임을 생성한다.<br>
     * 
     * @return
     * @see MX2_AMR_Communication_Specification.pdf:51p
     */
    private IoBuffer getTerminate() {
        // 데이터 프레임 부분에 들어갈 데이터들을 정의한다.
        ByteArray dataFrame = new ByteArray();

        // Terminate 명령
        dataFrame.append(ANSI.TERMI);
        
        byte[] terminateFrame = carryFrame(dataFrame.toByteArray());
        
        IoBuffer buf = IoBuffer.allocate(terminateFrame.length);
        buf.put(terminateFrame);
        buf.flip();

        return buf;
    }

    /**
     * read 에 필요한 프레임을 생성한다.
     * 
     * @param tableid
     *            MX2 장비는 2 size(byte)이 다.
     * @param index
     * @param count
     * @return
     */
    private IoBuffer getRead(char tableid, int index, int count) {
        // 데이터 프레임 부분에 들어갈 데이터들을 정의한다.
        ByteArray dataFrame = new ByteArray();
        
        char cIndex = (char)index;
        char cCount = (char)count;

        // Read 명령
        if (count > 0) {
            dataFrame.append((byte)(ANSI.READ+1)); //인덱스 지정시 명령어는 0x31
        } else {
            dataFrame.append(ANSI.READ);
        }

        // tableid
        dataFrame.append(DataUtil.get2ByteToChar(tableid));
        
        // count 가 0이 아닐경우 인덱스와 같이 설정해준다.
        if (count > 0) {
            dataFrame.append(DataUtil.get2ByteToChar(cIndex));
            dataFrame.append(DataUtil.get2ByteToChar(cCount));
        }

        return getBuffer(dataFrame.toByteArray());
    }
    
    /**
     * Write 에 필요한 프레임을 생성한다.
     * @param tableid
     * @param data
     * @return
     */
    private IoBuffer getWrite(char tableid, byte[] data){
        // 데이터 프레임 부분에 들어갈 데이터들을 정의한다.
        ByteArray dataFrame = new ByteArray();
        // Write 명령
        dataFrame.append(ANSI.WRITE);
    
        // tableid
        dataFrame.append(DataUtil.get2ByteToChar(tableid));
        
        //count
        dataFrame.append(DataUtil.get2ByteToInt(data.length));
        
        //data
        dataFrame.append(data);
        
        //cksum
        byte chkSum = ANSI.chkSum(data, 0, data.length);
        dataFrame.append(chkSum);


        return getBuffer(dataFrame.toByteArray());
    }
}