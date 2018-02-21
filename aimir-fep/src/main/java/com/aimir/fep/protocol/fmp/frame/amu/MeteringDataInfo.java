package com.aimir.fep.protocol.fmp.frame.amu;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

/**
 * MeteringData
 * 
 * Metering Data Info Frame 
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 2. 24. 오후 6:26:29$
 */
public class MeteringDataInfo extends AMUFramePayLoad{

	private static Log log = LogFactory.getLog(MeteringDataInfo.class);
	
	byte meterInfoCnt;
	
	ArrayList<MeteringInfo> meterInfo  = null;
	
	
	/**
	 * constructor
	 */
	public MeteringDataInfo(){
	}
	
	/**
	 * constructor
	 * @param framePayLoad
	 * @throws Exception
	 */
	public MeteringDataInfo(byte[] framePayLoad)throws Exception{
		try{
			decode(framePayLoad);
		}catch(Exception e){
			throw e;
		}
	}

	/**
	 * get Meter data Count
	 * @return
	 */
	public byte getMeterInfoCnt() {
		return meterInfoCnt;
	}

	/**
	 * set Meter data Count
	 * @param meterInfoCnt
	 */
	public void setMeterInfoCnt(byte meterInfoCnt) {
		this.meterInfoCnt = meterInfoCnt;
	}

	/**
	 * get MeterInfo Data
	 * @return
	 */
	public ArrayList<MeteringInfo> getMeterInfo() {
		return meterInfo;
	}

	/**
	 * set MeterInfo Data
	 * @param meterInfo
	 */
	public void setMeterInfo(ArrayList<MeteringInfo> meterInfo) {
		this.meterInfo = meterInfo;
	}

	/**
	 * decode
	 * @param framePayLoad
	 * @throws Exception
	 */
	public void decode(byte[] framePayLoad) throws Exception{
		
		meterInfo = new ArrayList<MeteringInfo>();
		try{
			int pos =0;
			
			log.debug("MeteringData Info identifier:  : " + Hex.decode(new byte[]{framePayLoad[pos]}));
			this.identifier = framePayLoad[pos];
			pos += AMUFramePayLoadConstants.FormatLength.FRAME_IDENTIFIER;
			
			this.meterInfoCnt = framePayLoad[pos];
			pos += AMUFramePayLoadConstants.FormatLength.MD_INFO_COUNT;
			
			int mInfoCnt = DataUtil.getIntToByte(meterInfoCnt);
			log.debug("MeterInfo Count : " + mInfoCnt);
			
			for(int i = 0; i < mInfoCnt ; i++){
				
				MeteringInfo tmp = new MeteringInfo();
				
				log.debug("[+"+ i +"]Port :  : " + Hex.decode(new byte[]{framePayLoad[pos]}));
				tmp.setPort(framePayLoad[pos]);
				pos += AMUFramePayLoadConstants.FormatLength.MD_INFO_PORT;
				
				log.debug("[+"+ i +"]ServiceType :  : " + Hex.decode(new byte[]{framePayLoad[pos]}));
				tmp.setServiceType(framePayLoad[pos]);
				pos += AMUFramePayLoadConstants.FormatLength.MD_INFO_SERVICE_TYPE;
				
				log.debug("[+"+ i +"]SensorType :  : " + Hex.decode(new byte[]{framePayLoad[pos]}));
				tmp.setSensorType(framePayLoad[pos]);
				pos += AMUFramePayLoadConstants.FormatLength.MD_INFO_SENSOR_TYPE;
				
				log.debug("[+"+ i +"]VendorType :  : " + Hex.decode(new byte[]{framePayLoad[pos]}));
				tmp.setVendorType(framePayLoad[pos]);
				pos += AMUFramePayLoadConstants.FormatLength.MD_INFO_VENDOR_TYPE;
				
				log.debug("[+"+ i +"]ModelCode :  : " + Hex.decode(new byte[]{framePayLoad[pos]}));
				tmp.setModelCode(framePayLoad[pos]);
				
				meterInfo.add(tmp);
				
			}
			
		}catch (Exception e) {
			log.error("Meter Information encode fail : ", e);
			throw e;
		}
	}
	
	/**
	 * Encode
	 * @return
	 * @throws Exception
	 */
	public byte[] encode() throws Exception {
		
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		
		try{
			bao.write(new byte[]{this.identifier}				, 0, AMUFramePayLoadConstants.FormatLength.FRAME_IDENTIFIER);
			bao.write(new byte[]{this.meterInfoCnt}				, 0, AMUFramePayLoadConstants.FormatLength.MD_INFO_COUNT);
			
			log.debug("MeteringDataInfo Encode [meter Information Count] " + DataUtil.getIntToByte(meterInfoCnt) );
			log.debug("MeteringDataInfo Encode [meter Info ArrayList Size] " +meterInfo.size() );
			for(int i = 0 ; i < meterInfo.size() ; i++){
				MeteringInfo tmp = (MeteringInfo)meterInfo.get(i);
				bao.write(new byte[]{tmp.getPort()}			, 0, AMUFramePayLoadConstants.FormatLength.MD_INFO_PORT);
				bao.write(new byte[]{tmp.getServiceType()}	, 0, AMUFramePayLoadConstants.FormatLength.MD_INFO_SERVICE_TYPE);
				bao.write(new byte[]{tmp.getSensorType()}	, 0, AMUFramePayLoadConstants.FormatLength.MD_INFO_SENSOR_TYPE);
				bao.write(new byte[]{tmp.getVendorType()}	, 0, AMUFramePayLoadConstants.FormatLength.MD_INFO_VENDOR_TYPE);
				bao.write(new byte[]{tmp.getModelCode()}	, 0, AMUFramePayLoadConstants.FormatLength.MD_INFO_MODEL_CODE);
			}
			
		}catch(Exception e){
			log.error("Get Metering Infomation Field Data Failed " , e);
		}
		return bao.toByteArray();
	}

	/**
	 * get Metering Information Data
	 * @return
	 * @throws Exception
	 */
	public byte[] getMeteringInfoData() throws Exception {
		
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		
		try{
			
			log.debug("getMeteringInfoData [meter Information Count] " + DataUtil.getIntToByte(meterInfoCnt) );
			log.debug("getMeteringInfoData [meter Info ArrayList Size] " +meterInfo.size() );
			
			for(int i = 0 ; i < meterInfo.size() ; i++){
				MeteringInfo tmp = (MeteringInfo)meterInfo.get(i);
				bao.write(new byte[]{tmp.getPort()}			, 0, AMUFramePayLoadConstants.FormatLength.MD_INFO_PORT);
				bao.write(new byte[]{tmp.getServiceType()}	, 0, AMUFramePayLoadConstants.FormatLength.MD_INFO_SERVICE_TYPE);
				bao.write(new byte[]{tmp.getSensorType()}	, 0, AMUFramePayLoadConstants.FormatLength.MD_INFO_SENSOR_TYPE);
				bao.write(new byte[]{tmp.getVendorType()}	, 0, AMUFramePayLoadConstants.FormatLength.MD_INFO_VENDOR_TYPE);
				bao.write(new byte[]{tmp.getModelCode()}	, 0, AMUFramePayLoadConstants.FormatLength.MD_INFO_MODEL_CODE);
			}
			
		}catch(Exception e){
			log.error("Get Metering Infomation Field Data Failed " , e);
		}
		return bao.toByteArray();
	}
}


