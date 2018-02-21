package com.aimir.fep.meter.parser.MBusTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

public class FixedDataHeader implements java.io.Serializable{
	private static Log log = LogFactory.getLog(FixedDataHeader.class);
	private byte[] rawData = null;
	private byte[] IDENTIFICATIONNUMBER = new byte[4];
	private byte[] MENUFACTURER = new byte[2];
	private byte[] VERSION = new byte[1];
	private byte[] MEDIUM = new byte[1];
	private byte[] ACCESSNUMBER = new byte[1];
	private byte[] STATUS = new byte[1];
	private byte[] SIGNATURE = new byte[2];

	private String identificationNumber="";
	private String menufacturer="";
	int version=0;
	String mediumStr="";
	int accessNumber=0;
	int statusCode=0;
	String statusStr="";
	int signature=0;

	/**
	 * @param data
	 * @param mode
	 * @throws Exception
	 */
	public FixedDataHeader(byte[] data, int mode) throws Exception {
		if(data.length!=12){
			throw new Exception("FixedDataHeader Length["+(data.length)+"] Invalid Exception");
		}

		int pos=0;
		rawData=data;

		System.arraycopy(rawData, pos, IDENTIFICATIONNUMBER, 0, IDENTIFICATIONNUMBER.length);
        pos += IDENTIFICATIONNUMBER.length;
        //Convert Endian
        if(mode==1){
        	DataUtil.convertEndian(IDENTIFICATIONNUMBER);
        }
        identificationNumber=DataUtil.getBCDtoBytes(IDENTIFICATIONNUMBER);
        log.debug("IDENTIFICATIONNUMBER[" + identificationNumber + "]");

        System.arraycopy(rawData, pos, MENUFACTURER, 0, MENUFACTURER.length);
        pos += MENUFACTURER.length;
        //Convert Endian
        if(mode==1){
        	DataUtil.convertEndian(MENUFACTURER);
        }
        menufacturer=Hex.decode(MENUFACTURER);
        log.debug("MENUFACTURER[" + menufacturer + "]");

        System.arraycopy(rawData, pos, VERSION, 0, VERSION.length);
        pos += VERSION.length;
        version=DataUtil.getIntToBytes(VERSION);
        log.debug("VERSION[" + version + "]");

        System.arraycopy(rawData, pos, MEDIUM, 0, MEDIUM.length);
        pos += MEDIUM.length;
        int medium=DataUtil.getIntToBytes(MEDIUM);
        if(medium==0x00){
        	mediumStr="Other";
        }
        else if(medium==0x01){
        	mediumStr="Oil";
        }
        else if(medium==0x02){
        	mediumStr="Electricity";
        }
        else if(medium==0x03){
        	mediumStr="Gas";
        }
        else if(medium==0x04){
        	mediumStr="Heat(Volume measured at return temperature: outlet)";
        }
        else if(medium==0x05){
        	mediumStr="Steam";
        }
        else if(medium==0x06){
        	mediumStr="Hot Water";
        }
        else if(medium==0x07){
        	mediumStr="Water";
        }
        else if(medium==0x08){
        	mediumStr="Heat Cost Allocator.";
        }
        else if(medium==0x09){
        	mediumStr="Compressed Air";
        }
        else if(medium==0x0A){
        	mediumStr="Cooling load meter(Volume measured at return temperature: outlet)";
        }
        else if(medium==0x0B){
        	mediumStr="Cooling load meter(Volume measured at return temperature: inlet)";
        }
        else if(medium==0x0C){
        	mediumStr="Heat(Volume measured at return temperature: inlet)";
        }
        else if(medium==0x0D){
        	mediumStr="Heat/Cooling load meter";
        }
        else if(medium==0x0E){
        	mediumStr="Bus/System";
        }
        else if(medium==0x0F){
        	mediumStr="Unknown Medium";
        }
        else if(medium>=0x10 && medium<=0x15){
        	mediumStr="Reserved";
        }
        else if(medium==0x16){
        	mediumStr="Cold Water";
        }
        else if(medium==0x17){
        	mediumStr="Dual Water";
        }
        else if(medium==0x18){
        	mediumStr="Pressure";
        }
        else if(medium==0x19){
        	mediumStr="A/D Converter";
        }
        else if(medium>=0x20 && medium>=0xFF){
        	mediumStr="Reserved";
        }
        else{
        	mediumStr="Unknown";
        }
        log.debug("MEDIUM[" + mediumStr + "]");

        System.arraycopy(rawData, pos, ACCESSNUMBER, 0, ACCESSNUMBER.length);
        pos += ACCESSNUMBER.length;
        accessNumber=DataUtil.getIntToBytes(ACCESSNUMBER);
        log.debug("ACCESSNUMBER[" + accessNumber + "]");

        System.arraycopy(rawData, pos, STATUS, 0, STATUS.length);
        pos += STATUS.length;
        statusCode=DataUtil.getIntToBytes(STATUS);
        if((statusCode&0x03)==0){
        	statusStr="No Error";
        }
        else if((statusCode&0x03)==1){
        	statusStr="Application Busy";
        }
        else if((statusCode&0x03)==2){
        	statusStr="Any Application Error";
        }
        else if((statusCode&0x03)==3){
        	statusStr="Reserved";
        }
        log.debug("STATUS[" + statusStr + "]");

        System.arraycopy(rawData, pos, SIGNATURE, 0, SIGNATURE.length);
        pos += SIGNATURE.length;
        //Convert Endian
        if(mode==1){
        	DataUtil.convertEndian(SIGNATURE);
        }
        signature=DataUtil.getIntToBytes(SIGNATURE);
        log.debug("SIGNATURE[" + signature + "]");
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getIdentificationNumber() {
		return identificationNumber;
	}

	public void setIdentificationNumber(String identificationNumber) {
		this.identificationNumber = identificationNumber;
	}

	public String getMenufacturer() {
		return menufacturer;
	}

	public void setMenufacturer(String menufacturer) {
		this.menufacturer = menufacturer;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getMediumStr() {
		return mediumStr;
	}

	public void setMediumStr(String mediumStr) {
		this.mediumStr = mediumStr;
	}

	public int getAccessNumber() {
		return accessNumber;
	}

	public void setAccessNumber(int accessNumber) {
		this.accessNumber = accessNumber;
	}

	public String getStatusStr() {
		return statusStr;
	}

	public void setStatusStr(String statusStr) {
		this.statusStr = statusStr;
	}

	public int getSignature() {
		return signature;
	}

	public void setSignature(int signature) {
		this.signature = signature;
	}

	public static void main(String args[]){
		byte[] data= Hex.encode("78563412244001075500000000");
		try{
			FixedDataHeader fix=new FixedDataHeader(data, 1);
		}catch (Exception e) {
			log.error(e.getMessage());
		}
	}
}