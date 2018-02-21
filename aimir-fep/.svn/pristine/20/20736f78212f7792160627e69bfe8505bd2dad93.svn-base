package com.aimir.fep.meter.parser.MBusTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataUtil;

public class DIF implements java.io.Serializable{
	private static Log log = LogFactory.getLog(DIF.class);
	private byte[] rawData = new byte[1];

	private int extensionBit=0;
	private int LSBOfStorageNumber=0;
	private int functionField=0;
	private String functionDescr="";
	private int dataField=0;
	private int dataLength=0;
	private String dataType="";
	private boolean mdhIsExisted=false;

	/**
	 * @param data
	 */
	public DIF(byte[] data) {
		initDIF();
		rawData=data;
		int dif=DataUtil.getIntToBytes(rawData);
		extensionBit=((dif&0x80)>>7);
		//log.debug("EXTENSIONBIT[" + extensionBit + "]");

		LSBOfStorageNumber=((dif&0x40)>>6);
		//log.debug("LSBOFSTORAGENUMBER[" + LSBOfStorageNumber + "]");

		functionField=((dif&0x30)>>4);
		if(functionField==0x00){
			functionDescr="Instaneous value";
		}
		else if(functionField==0x01){
			functionDescr="Maximum value";
		}
		else if(functionField==0x02){
			functionDescr="Minimum value";
		}
		else if(functionField==0x03){
			functionDescr="Value during error state";
		}
		//log.debug("FUNCTIONDESCR[" + functionDescr + "]");

		dataField=(dif&0x0F);
		if(dataField==0){
			dataLength=0;
			dataType="No Data";
		}
		else if(dataField==1){
			dataLength=1;
			dataType="Integer";
		}
		else if(dataField==2){
			dataLength=2;
			dataType="Integer";
		}
		else if(dataField==3){
			dataLength=3;
			dataType="Integer";
		}
		else if(dataField==4){
			dataLength=4;
			dataType="Integer";
		}
		else if(dataField==5){
			dataLength=4;
			dataType="Real";
		}
		else if(dataField==6){
			dataLength=6;
			dataType="Integer";
		}
		else if(dataField==7){
			dataLength=8;
			dataType="Integer";
		}
		else if(dataField==8){
			dataLength=0;
			dataType="Selection for Readout";
		}
		else if(dataField==9){
			dataLength=1;
			dataType="BCD";
		}
		else if(dataField==10){
			dataLength=2;
			dataType="BCD";
		}
		else if(dataField==11){
			dataLength=3;
			dataType="BCD";
		}
		else if(dataField==12){
			dataLength=4;
			dataType="BCD";
		}
		//
		else if(dataField==13){
			dataLength=0;
			dataType="variable length";
		}
		else if(dataField==14){
			dataLength=6;
			dataType="BCD";
		}
		else if(dataField==15){
			dataLength=8;
			dataType="Special Functions";
		}
		//log.debug("DATALENGTH[" + dataLength + "] dataType[" + dataType + "]");

		if(dataField==0x0F || dataField==0x1F){
			mdhIsExisted=true;
		}
		//log.debug("MDHISEXISTED[" + mdhIsExisted + "]");
	}

	public int getExtensionBit() {
		return extensionBit;
	}

	public void setExtensionBit(int extensionBit) {
		this.extensionBit = extensionBit;
	}

	public int getLSBOfStorageNumber() {
		return LSBOfStorageNumber;
	}

	public void setLSBOfStorageNumber(int ofStorageNumber) {
		LSBOfStorageNumber = ofStorageNumber;
	}

	public int getFunctionField() {
		return functionField;
	}

	public void setFunctionField(int functionField) {
		this.functionField = functionField;
	}

	public String getFunctionDescr() {
		return functionDescr;
	}

	public void setFunctionDescr(String functionDescr) {
		this.functionDescr = functionDescr;
	}

	public int getDataField() {
		return dataField;
	}

	public void setDataField(int dataField) {
		this.dataField = dataField;
	}

	public int getDataLength() {
		return dataLength;
	}

	public void setDataLength(int dataLength) {
		this.dataLength = dataLength;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public boolean isMdhIsExisted() {
		return mdhIsExisted;
	}

	public void setMdhIsExisted(boolean mdhIsExisted) {
		this.mdhIsExisted = mdhIsExisted;
	}

	/**
	 * Initialize DIF
	 */
	public void initDIF(){
		setExtensionBit(0);
		setLSBOfStorageNumber(0);
		setFunctionField(0);
		setFunctionDescr("");
		setDataField(0);
		setDataLength(0);
		setDataType("");
		setMdhIsExisted(false);
	}

	/**
	 * Constructs a <code>String</code> with all attributes
	 * in name = value format.
	 *
	 * @return a <code>String</code> representation
	 * of this object.
	 */
	public String toString()
	{
	    final String TAB = "    ";

	    StringBuffer retValue = new StringBuffer();

	    retValue.append("DIF ( ")
	        .append(super.toString()).append(TAB)
	        .append("extensionBit = ").append(this.extensionBit).append(TAB)
	        .append("LSBOfStorageNumber = ").append(this.LSBOfStorageNumber).append(TAB)
	        .append("functionField = ").append(this.functionField).append(TAB)
	        .append("functionDescr = ").append(this.functionDescr).append(TAB)
	        .append("dataField = ").append(this.dataField).append(TAB)
	        .append("dataLength = ").append(this.dataLength).append(TAB)
	        .append("dataType = ").append(this.dataType).append(TAB)
	        .append("mdhIsExisted = ").append(this.mdhIsExisted).append(TAB)
	        .append(" )");

	    return retValue.toString();
	}

}
