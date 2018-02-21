package com.aimir.fep.protocol.fmp.frame.amu;

import java.io.ByteArrayOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

/**
 * Meter Pooling 
 * 수신된 Meter Data를 통합 저장하는 Class
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 4. 16. 오후 2:47:56$
 */
public class MeterPooling implements java.io.Serializable  {
	
	private Log log = LogFactory.getLog(MeterPooling.class);
	
	String sourceAddr = null;
	String destAddr = null;
	byte meterInfoCnt;
	byte[] meterPoolData 	= null;
	
	/**
	 * constructor
	 */
	public MeterPooling(){
	}
	
	public String getSourceAddr() {
        return sourceAddr;
    }

    public void setSourceAddr(String sourceAddr) {
        this.sourceAddr = sourceAddr;
    }

    public String getDestAddr() {
        return destAddr;
    }

    public void setDestAddr(String destAddr) {
        this.destAddr = destAddr;
    }

    /**
	 * get Meter Info Count
	 * @return
	 */
	public byte getMeterInfoCnt() {
		return meterInfoCnt;
	}

	/**
	 * set Meter Info Count 
	 * @param meterInfoCnt
	 */
	public void setMeterInfoCnt(byte meterInfoCnt) {
		this.meterInfoCnt = meterInfoCnt;
	}

	/**
	 * get Meter Pool Data
	 * @return
	 */
	public byte[] getMeterPoolData() {
		return meterPoolData;
	}

	/**
	 * set Meter Pool Data
	 * @param meterPoolData
	 */
	public void setMeterPoolData(byte[] meterPoolData) {
		this.meterPoolData = meterPoolData;
	}

	/**
	 * MeterPooling Data encode
	 * @return
	 * @throws Exception
	 */
	public byte[] encode() throws Exception{
		
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		try{
			
			// bao.write(mcuId 					, 0 , mcuId.length);
			// bao.write(new byte[]{meterInfoCnt} 	, 0 , AMUFramePayLoadConstants.FormatLength.MD_INFO_COUNT );
			bao.write(meterPoolData 			, 0 , meterPoolData.length);
			
		}catch(Exception e){
			log.error("MeteringPooling encode Failed ", e);
		}
		return bao.toByteArray();
	}
	
	/**
	 * Add Meter Pooling Data  
	 * @param data
	 * @throws Exception
	 */
	public void addMeterPoolData(byte[] data) throws Exception{
		
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		try{
			// Session 저장된 Pooling Data
			bao.write(meterPoolData ,0 ,meterPoolData.length);
			// Length (4byte)
			int length = data.length;
			
			byte[] intTobyteLength = DataUtil.get4ByteToInt(length);
			
			log.debug("meterPool Add Payload Length  ["+ Hex.decode(intTobyteLength) +"] ==>  "+ length);
			bao.write(intTobyteLength, 0 , intTobyteLength.length);
			// Data
			bao.write(data, 0 , data.length);
			
			// Add Data
			setMeterPoolData(bao.toByteArray());
			
		}catch(Exception e){
			log.error("Metering Data Append Failed " ,e);
		}
	}
}


