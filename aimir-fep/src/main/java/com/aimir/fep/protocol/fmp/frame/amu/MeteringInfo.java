package com.aimir.fep.protocol.fmp.frame.amu;

import java.io.ByteArrayOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Meterinf Infomation
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 4. 16. 오후 2:54:54$
 */
public class MeteringInfo extends AMUFramePayLoad{

	private Log log = LogFactory.getLog(MeteringInfo.class);
	
	byte port;
	byte serviceType;
	byte sensorType;
	byte vendorType;
	byte modelCode;
	
	/**
	 * constructor
	 */
	public MeteringInfo(){
	}
	
	/**
	 * constructor
	 * 
	 * @param rawData
	 * @throws Exception
	 */
	public MeteringInfo(byte[] rawData) throws Exception{
		try{
			decode(rawData);
		}catch(Exception e){
			throw e;
		}
	}
	
	/**
	 * get Port
	 * @return
	 */
	public byte getPort() {
		return port;
	}

	/**
	 * set Port
	 * @param port
	 */
	public void setPort(byte port) {
		this.port = port;
	}

	/**
	 * get Service Type
	 * @return
	 */
	public byte getServiceType() {
		return serviceType;
	}

	/**
	 * set Service Type
	 * @param serviceType
	 */
	public void setServiceType(byte serviceType) {
		this.serviceType = serviceType;
	}

	/**
	 * get Sensor Type
	 * @return
	 */
	public byte getSensorType() {
		return sensorType;
	}

	/**
	 * set Sernor Type
	 * @param sensorType
	 */
	public void setSensorType(byte sensorType) {
		this.sensorType = sensorType;
	}

	/**
	 * get Vendor Type
	 * @return
	 */
	public byte getVendorType() {
		return vendorType;
	}

	/**
	 * set Vendor Type
	 * @param vendorType
	 */
	public void setVendorType(byte vendorType) {
		this.vendorType = vendorType;
	}

	/**
	 * get Model Code
	 * @return
	 */
	public byte getModelCode() {
		return modelCode;
	}

	/**
	 * set Model code
	 * @param modelCode
	 */
	public void setModelCode(byte modelCode) {
		this.modelCode = modelCode;
	}

	/**
	 * decode 
	 * @param framePayLoad
	 * @throws Exception
	 */
	public void decode(byte[] framePayLoad) throws Exception{
		
		try{
			
			int pos=0;			
			this.port 			= framePayLoad[pos++];
			this.serviceType 	= framePayLoad[pos++];
			this.sensorType 	= framePayLoad[pos++];
			this.vendorType 	= framePayLoad[pos++];
			this.modelCode 		= framePayLoad[pos++];
		}catch (Exception e) {
			log.error("Meter Information decode fail : ", e);
			throw e;
		}
	}
	
	/**
	 * encode
	 * @return
	 */
	public byte[] encode() throws Exception{
		
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		
		try{
			
			bao.write(new byte[]{this.port} 		, 0 , 1);
			bao.write(new byte[]{this.serviceType} 	, 0 , 1);
			bao.write(new byte[]{this.sensorType} 	, 0 , 1);
			bao.write(new byte[]{this.vendorType} 	, 0 , 1);
			bao.write(new byte[]{this.modelCode} 	, 0 , 1);
			
		}catch (Exception e) {
			log.error("Meter Information encode fail : ", e);
			throw e;
		}
		return bao.toByteArray();
	}
}


