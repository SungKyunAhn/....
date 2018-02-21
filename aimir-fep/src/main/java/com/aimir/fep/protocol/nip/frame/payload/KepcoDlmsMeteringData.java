package com.aimir.fep.protocol.nip.frame.payload;

import java.util.Arrays;

public class KepcoDlmsMeteringData{
	
	public enum ObisCodeId {
        LP((new byte[]{(byte)0x00,(byte)0x01})),
        CurrRead((new byte[]{(byte)0x00,(byte)0x02})),
        RegularRead((new byte[]{(byte)0x00,(byte)0x04})),
        JeongBokJean((new byte[]{(byte)0x00,(byte)0x08})),
        PeakLoad((new byte[]{(byte)0x00,(byte)0x10})),
        CurrMaxDemand((new byte[]{(byte)0x00,(byte)0x20})),
        GtypeVoltAmpere((new byte[]{(byte)0x00,(byte)0x40})),
        MeterInfo((new byte[]{(byte)0x00,(byte)0x80}));
        private byte[] code;
        
        ObisCodeId(byte[] code) {
            this.code = code;
        }
        
        public byte[] getCode() {
            return this.code;
        }
    }
	
	private ObisCodeId _obisCodeId;
	private String headerObis;
	private String headerClass;
	private String headerAttr;
	private int headerLength;
	private String tagTag;
	private String tagDataOrLengthData;

    public ObisCodeId get_obisCodeId() {
		return _obisCodeId;
	}

	public void set_obisCodeId(ObisCodeId _obisCodeId) {
		this._obisCodeId = _obisCodeId;
	}

	public String getHeaderObis() {
		return headerObis;
	}

	public void setHeaderObis(String headerObis) {
		this.headerObis = headerObis;
	}

	public String getHeaderClass() {
		return headerClass;
	}

	public void setHeaderClass(String headerClass) {
		this.headerClass = headerClass;
	}

	public String getHeaderAttr() {
		return headerAttr;
	}

	public void setHeaderAttr(String headerAttr) {
		this.headerAttr = headerAttr;
	}

	public int getHeaderLength() {
		return headerLength;
	}

	public void setHeaderLength(int headerLength) {
		this.headerLength = headerLength;
	}

	public String getTagTag() {
		return tagTag;
	}

	public void setTagTag(String tagTag) {
		this.tagTag = tagTag;
	}

	public String getTagDataOrLengthData() {
		return tagDataOrLengthData;
	}

	public void setTagDataOrLengthData(String tagDataOrLengthData) {
		this.tagDataOrLengthData = tagDataOrLengthData;
	}

	public void setObisCodeId(byte[] code) {
    	for (ObisCodeId f : ObisCodeId.values()) {
            if (Arrays.equals(f.getCode(), code)){
//            		f.getCode()[0] == code[0] && f.getCode()[1] == code[1]) {
            	_obisCodeId = f;
                break;
            }
        }
    }
}