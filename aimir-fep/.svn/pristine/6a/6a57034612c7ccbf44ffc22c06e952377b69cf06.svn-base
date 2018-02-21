package com.aimir.fep.meter.parser.MBusTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;

public class Data implements java.io.Serializable{
	private static Log log = LogFactory.getLog(Data.class);
	private byte[] rawData = null;
	private double value=0;

	/**
	 * @param data
	 */
	public Data(byte[] data, ControlInformation controlInfomation, DIF dif, VIF vif) {
		checkEndian(controlInfomation,data);
		rawData=data;

		if("Integer".equals(dif.getDataType())){
			value=DataUtil.getIntToBytes(rawData);
		}
		else if("BCD".equals(dif.getDataType())){
			value=Double.parseDouble((DataUtil.getBCDtoBytes(rawData)));
		}
		else if("Real".equals(dif.getDataType())){
			try {
				value=DataFormat.bytesToDouble(rawData);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if("Date".equals(dif.getDataType())){
			value=DataUtil.getDateTo2Byte(rawData);
		}
		//log.debug("Value["+value+"]");
	}

	public Data(byte[] data, ControlInformation controlInfomation) {
		checkEndian(controlInfomation,data);
		rawData=data;
		value=Double.parseDouble((DataUtil.getBCDtoBytes(rawData)));
	}

	public void checkEndian(ControlInformation controlInfomation, byte[] data){
		if(controlInfomation.getMode()==1){
			DataUtil.convertEndian(data);
		}
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

}
