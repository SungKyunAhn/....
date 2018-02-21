package com.aimir.fep.protocol.nip.frame.payload;

public class FrameHeader{
	  
    public enum Vendor {
        Unknown ((byte)0x00),
		GE ((byte)0x02),
		Landis_Gyr ((byte)0x04),
		LS ((byte)0x06),
		Actaris ((byte)0x08),
		EDMI ((byte)0x0A),
		NamCheun ((byte)0x0C),
		ABB ((byte)0x0E),
		ILJIN ((byte)0x10),
		PSTec ((byte)0x12),
		SEOCHANG ((byte)0x14),
		AIMAL ((byte)0x16),
		AMSTech ((byte)0x18),
		Micronic((byte)0x1A),
		MSM ((byte)0x1C),
		YPP ((byte)0x1E),
		AEG ((byte)0x20),
		Sensus ((byte)0x22),
		Kromschroder ((byte)0x24),
		Mitsubishi ((byte)0x26),
		Kamstrup ((byte)0x01),
		Elster ((byte)0x03),
		Aidon ((byte)0x05),
		Wizit ((byte)0x07),
		GasMeter ((byte)0x09),
		KETI ((byte)0x0B),
		TAIHAN ((byte)0x0D),
		KHE ((byte)0x0F),
		Teakwang ((byte)0x11),
		KT ((byte)0x13),
		ChunIl ((byte)0x15),
		DmPower ((byte)0x17),
		Omnisystem ((byte)0x19),
		HeupSin ((byte)0x1B),
		PworPlus ((byte)0x1D),
		Pyungil ((byte)0x1F),
		ANSI ((byte)0x21),
		Itron ((byte)0x23),
		Siemens ((byte)0x25),
		Osaki ((byte)0x27),
		Kamstrup2 ((byte)0x28),
		Kaifa ((byte)0x2D),
		ETC ((byte)0xFE),
		Nuri ((byte)0xFF);

        private byte code;
        Vendor(byte code) {
            this.code = code;
        }
        
        public byte getCode() {
            return this.code;
        }
    }
	
    private String sid;
    private String mid;
    private int sType;
    private int svc;
    private int dataCnt;
    private int length;
    private String timeStamp;
    private Vendor _vendor;
    
    public int getSvc() {
		return svc;
	}

	public void setSvc(int svc) {
		this.svc = svc;
	}
    
    public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public int getsType() {
		return sType;
	}

	public void setsType(int sType) {
		this.sType = sType;
	}

	public int getDataCnt() {
		return dataCnt;
	}

	public void setDataCnt(int dataCnt) {
		this.dataCnt = dataCnt;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
  
    public void setVendor(byte code) {
    	for (Vendor f : Vendor.values()) {
            if (f.getCode() == code) {
            	_vendor = f;
                break;
            }
        }
    }
    
    public Vendor getVendor() {
        return this._vendor;
    }
}