package com.aimir.fep.protocol.fmp.frame.amu;

import java.io.ByteArrayOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;

/**
 * MIU Timesync Command Data
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 4. 23. 오후 4:08:09$
 */
public class MiuTimeSyncCommandData {

	private Log log = LogFactory.getLog(MiuTimeSyncCommandData.class);
	
	byte[] mainServerIP					= null;
	byte[] eventServerIP				= null;
	byte[] mainServerPort				= null;
	byte[] eventServerPort				= null;
	byte[] mcuMobileModuleNumber		= null;
	byte[] mcuEthernetModuleIP			= null;
	byte[] mcuCoordinatorMouduleEUI64ID	= null;
	byte[] timeZone						= null;
	byte[] dst							= null;
	byte[] year							= null;
	byte month;
	byte day;
	byte hour;
	byte minute;
	byte second;
	
	byte[] rawData	= null;
	
	/**
	 * constructor
	 */
	public MiuTimeSyncCommandData(){
	}
	
	/**
	 * constructor
	 * @param rawData
	 * @throws Exception
	 */
	public MiuTimeSyncCommandData(byte[] rawData)throws Exception{
		try {
			decode(rawData);
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * get Main Server IP
	 * @return
	 */
	public byte[] getMainServerIP() {
		return mainServerIP;
	}

	/**
	 * set Main Server IP
	 * @param mainServerIP
	 */
	public void setMainServerIP(byte[] mainServerIP) {
		this.mainServerIP = mainServerIP;
	}

	/**
	 * get Event Server IP
	 * @return
	 */
	public byte[] getEventServerIP() {
		return eventServerIP;
	}

	/**
	 * set Event Server IP
	 * @param eventServerIP
	 */
	public void setEventServerIP(byte[] eventServerIP) {
		this.eventServerIP = eventServerIP;
	}

	/**
	 * get Main Server Port
	 * @return
	 */
	public byte[] getMainServerPort() {
		return mainServerPort;
	}

	/**
	 * set Main Server Port
	 * @param mainServerPort
	 */
	public void setMainServerPort(byte[] mainServerPort) {
		this.mainServerPort = mainServerPort;
	}

	/**
	 * get Event Sever Port
	 * @return
	 */
	public byte[] getEventServerPort() {
		return eventServerPort;
	}

	/**
	 * set Event Server Port
	 * @param eventServerPort
	 */
	public void setEventServerPort(byte[] eventServerPort) {
		this.eventServerPort = eventServerPort;
	}

	/**
	 * get MCU Mobile Module Number
	 * @return
	 */
	public byte[] getMcuMobileModuleNumber() {
		return mcuMobileModuleNumber;
	}

	/**
	 * set MCU Mobile Module Number
	 * @param mcuMobileModuleNumber
	 */
	public void setMcuMobileModuleNumber(byte[] mcuMobileModuleNumber) {
		this.mcuMobileModuleNumber = mcuMobileModuleNumber;
	}

	/**
	 * get MCU Ethernet Module IP
	 * @return
	 */
	public byte[] getMcuEthernetModuleIP() {
		return mcuEthernetModuleIP;
	}

	/**
	 * set MCU Ethernet Module IP
	 * @param mcuEthernetModuleIP
	 */
	public void setMcuEthernetModuleIP(byte[] mcuEthernetModuleIP) {
		this.mcuEthernetModuleIP = mcuEthernetModuleIP;
	}

	/**
	 * get MCU Coordinator Moudle EUI64ID
	 * @return
	 */
	public byte[] getMcuCoordinatorMouduleEUI64ID() {
		return mcuCoordinatorMouduleEUI64ID;
	}

	/**
	 * set MCU Coordinator Moudle EUI64ID
	 * @param mcuCoordinatorMouduleEUI64ID
	 */
	public void setMcuCoordinatorMouduleEUI64ID(byte[] mcuCoordinatorMouduleEUI64ID) {
		this.mcuCoordinatorMouduleEUI64ID = mcuCoordinatorMouduleEUI64ID;
	}

	/**
	 * get Time Zone
	 * @return
	 */
	public byte[] getTimeZone() {
		return timeZone;
	}

	/**
	 * set Time Zone
	 * @param timeZone
	 */
	public void setTimeZone(byte[] timeZone) {
		this.timeZone = timeZone;
	}

	/**
	 * get DST
	 * @return
	 */
	public byte[] getDst() {
		return dst;
	}

	/**
	 * set DST
	 * @param dst
	 */
	public void setDst(byte[] dst) {
		this.dst = dst;
	}

	/**
	 * get Year
	 * @return
	 */
	public byte[] getYear() {
		return year;
	}

	/**
	 * set Year
	 * @param year
	 */
	public void setYear(byte[] year) {
		this.year = year;
	}

	/**
	 * get Month
	 * @return
	 */
	public byte getMonth() {
		return month;
	}

	/**
	 * set Month
	 * @param month
	 */
	public void setMonth(byte month) {
		this.month = month;
	}

	/**
	 * get Day
	 * @return
	 */
	public byte getDay() {
		return day;
	}

	/**
	 * set Day
	 * @param day
	 */
	public void setDay(byte day) {
		this.day = day;
	}

	/**
	 * get Hour
	 * @return
	 */
	public byte getHour() {
		return hour;
	}

	/**
	 * set Hour
	 * @param hour
	 */
	public void setHour(byte hour) {
		this.hour = hour;
	}

	/**
	 * get Minute
	 * @return
	 */
	public byte getMinute() {
		return minute;
	}

	/**
	 * set Minute
	 * @param minute
	 */
	public void setMinute(byte minute) {
		this.minute = minute;
	}

	/**
	 * get Second
	 * @return
	 */
	public byte getSecond() {
		return second;
	}

	/**
	 * set Second
	 * @param second
	 */
	public void setSecond(byte second) {
		this.second = second;
	}

	
	/**
	 * decode
	 * @param framePayLoad
	 * @throws Exception
	 */
	public void decode(byte[] framePayLoad) throws Exception{
		try{
			int pos =0;
			this.mainServerIP = DataFormat.select(framePayLoad, pos, 
					AMUFramePayLoadConstants.FormatLength.CMD_MIU_MAIN_SERVER_IP);
			pos += AMUFramePayLoadConstants.FormatLength.CMD_MIU_MAIN_SERVER_IP;
			
			this.eventServerIP = DataFormat.select(framePayLoad, pos, 
					AMUFramePayLoadConstants.FormatLength.CMD_MIU_EVENT_SERVER_IP);
			pos += AMUFramePayLoadConstants.FormatLength.CMD_MIU_EVENT_SERVER_IP;
			
			this.mainServerPort = DataFormat.select(framePayLoad, pos, 
					AMUFramePayLoadConstants.FormatLength.CMD_MIU_MAIN_SERVER_PORT);
			pos += AMUFramePayLoadConstants.FormatLength.CMD_MIU_MAIN_SERVER_PORT;
			
			this.eventServerPort = DataFormat.select(framePayLoad, pos, 
					AMUFramePayLoadConstants.FormatLength.CMD_MIU_EVENT_SERVER_PORT);
			pos += AMUFramePayLoadConstants.FormatLength.CMD_MIU_EVENT_SERVER_PORT;
			
			this.mcuMobileModuleNumber = DataFormat.select(framePayLoad, pos, 
					AMUFramePayLoadConstants.FormatLength.CMD_MIU_MCU_MOBILE_MODULE_NUMBER);
			pos += AMUFramePayLoadConstants.FormatLength.CMD_MIU_MCU_MOBILE_MODULE_NUMBER;
			
			this.mcuEthernetModuleIP = DataFormat.select(framePayLoad, pos, 
					AMUFramePayLoadConstants.FormatLength.CMD_MIU_MCU_ETHERNET_MODULE_IP);
			pos += AMUFramePayLoadConstants.FormatLength.CMD_MIU_MCU_ETHERNET_MODULE_IP;
			
			this.mcuCoordinatorMouduleEUI64ID = DataFormat.select(framePayLoad, pos, 
					AMUFramePayLoadConstants.FormatLength.CMD_MIU_MCU_COORDINATOR_MOUDULE_EUI64ID);
			pos += AMUFramePayLoadConstants.FormatLength.CMD_MIU_MCU_COORDINATOR_MOUDULE_EUI64ID;
			
			this.timeZone = DataFormat.select(framePayLoad, pos, 
					AMUFramePayLoadConstants.FormatLength.CMD_MIU_TIMEZONE);
			pos += AMUFramePayLoadConstants.FormatLength.CMD_MIU_TIMEZONE;
			
			this.dst = DataFormat.select(framePayLoad, pos, 
					AMUFramePayLoadConstants.FormatLength.CMD_MIU_DST);
			pos += AMUFramePayLoadConstants.FormatLength.CMD_MIU_DST;
			
			this.year = DataFormat.select(framePayLoad, pos, 
					AMUFramePayLoadConstants.FormatLength.CMD_MIU_YEAR);
			pos += AMUFramePayLoadConstants.FormatLength.CMD_MIU_YEAR;
			
			this.month = framePayLoad[pos];
			pos += AMUFramePayLoadConstants.FormatLength.CMD_MIU_MONTH;
			
			this.day = framePayLoad[pos];
			pos += AMUFramePayLoadConstants.FormatLength.CMD_MIU_DAY;
			
			this.hour = framePayLoad[pos];
			pos += AMUFramePayLoadConstants.FormatLength.CMD_MIU_HOUR;
			
			this.minute = framePayLoad[pos];
			pos += AMUFramePayLoadConstants.FormatLength.CMD_MIU_MINUTE;
			
			this.second = framePayLoad[pos];
			pos += AMUFramePayLoadConstants.FormatLength.CMD_MIU_SECOND;
			
		}catch(Exception e){
			log.error("MIU Timesync Command Data decode failed : ", e);
			throw e;
		}
	}
	
	/**
	 * encode
	 * @return
	 * @throws Exception
	 */
	public byte[] encode() throws Exception{
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		
		try{
			
			bao.write(this.mainServerIP					, 0 , AMUFramePayLoadConstants.FormatLength.CMD_MIU_MAIN_SERVER_IP);
			bao.write(this.eventServerIP				, 0 , AMUFramePayLoadConstants.FormatLength.CMD_MIU_EVENT_SERVER_IP);
			bao.write(this.mainServerPort				, 0 , AMUFramePayLoadConstants.FormatLength.CMD_MIU_MAIN_SERVER_PORT);
			bao.write(this.eventServerPort				, 0 , AMUFramePayLoadConstants.FormatLength.CMD_MIU_EVENT_SERVER_PORT);
			bao.write(this.mcuMobileModuleNumber		, 0 , AMUFramePayLoadConstants.FormatLength.CMD_MIU_MCU_MOBILE_MODULE_NUMBER);
			bao.write(this.mcuEthernetModuleIP			, 0 , AMUFramePayLoadConstants.FormatLength.CMD_MIU_MCU_ETHERNET_MODULE_IP);
			bao.write(this.mcuCoordinatorMouduleEUI64ID	, 0 , AMUFramePayLoadConstants.FormatLength.CMD_MIU_MCU_COORDINATOR_MOUDULE_EUI64ID);
			bao.write(this.timeZone						, 0 , AMUFramePayLoadConstants.FormatLength.CMD_MIU_TIMEZONE);
			bao.write(this.dst							, 0 , AMUFramePayLoadConstants.FormatLength.CMD_MIU_DST);
			bao.write(this.year							, 0 , AMUFramePayLoadConstants.FormatLength.CMD_MIU_YEAR);
			bao.write(new byte[]{this.month}			, 0 , AMUFramePayLoadConstants.FormatLength.CMD_MIU_MONTH);
			bao.write(new byte[]{this.day}				, 0 , AMUFramePayLoadConstants.FormatLength.CMD_MIU_DAY);
			bao.write(new byte[]{this.hour}				, 0 , AMUFramePayLoadConstants.FormatLength.CMD_MIU_HOUR);
			bao.write(new byte[]{this.minute}			, 0 , AMUFramePayLoadConstants.FormatLength.CMD_MIU_MINUTE);
			bao.write(new byte[]{this.second}			, 0 , AMUFramePayLoadConstants.FormatLength.CMD_MIU_SECOND);
			
		}catch(Exception e){
			log.error("MIU Timesync Command Data encode failed : ", e);
			throw e;
		}
		return bao.toByteArray();	
	}
}


