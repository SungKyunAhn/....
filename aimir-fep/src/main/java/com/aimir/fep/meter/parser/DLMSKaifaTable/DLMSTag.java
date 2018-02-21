package com.aimir.fep.meter.parser.DLMSKaifaTable;

import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.DLMS_TAG_TYPE;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

public class DLMSTag {
    private DLMS_TAG_TYPE tag;
    private int length;
    private byte[] data;
    private Object value;
    
    public DLMS_TAG_TYPE getTag() {
        return tag;
    }
    
    public void setTag(DLMS_TAG_TYPE tag) {
        this.tag = tag;
    }
    
    public void setTag(int tag) {
        for (DLMS_TAG_TYPE _tag : DLMS_TAG_TYPE.values()) {
            if (_tag.getValue() == tag) {
                this.tag = _tag;
                return;
            }
        }
        this.tag = DLMS_TAG_TYPE.Null;
    }
    
    public int getLength() {
        return length;
    }
    public void setLength(int length) {
        this.length = length;
    }
    public byte[] getData() {
        return data;
    }
    public void setData(byte[] data) {
        this.data = data;
        
        if (tag != null) {
            switch (tag) {
            case Structure :
            case Group :
            case Boolean :
            case Enum :
            case BitString :
            case OctetString :
            case VisibleString :
            case BCD :
            case INT64 :
            case UINT64 :
            case Datetime :
            case Date :
            case Time :
            case Null :
                value = new OCTET(data);
                break;
            case FLOAT32 :
                try {
                    value = new Float(DataUtil.getFloat(data, 0));
                }catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case Array :
            case CompactArray :
            case INT8 :
            case UINT8 :
                value = new Integer(DataUtil.getIntToBytes(data));
                break;
            case INT16 :
            case UINT16 :
            case INT32 :
            case UINT32 :
                value = new Long(DataUtil.getLongToBytes(data));
                break;
            default :
                value = new OCTET(data);
            }
        }
    }
    
    public Object getValue() {
        return this.value;
    }
    
    public OCTET getOCTET() {
        return (OCTET)value;
    }
    
    public Integer getUint8() {
        return (Integer)value;
    }
    
    public Integer getInt8() {
        return (Integer)value;
    }
    
    public String toString() {
        StringBuffer buf = new StringBuffer();
        
        buf.append("TAG[" + tag + "] LENGTH[" + length + 
                "] DATA[" + Hex.decode(data) + "] VALUE[" + (value instanceof OCTET? Hex.decode(((OCTET)value).getValue()):value) + "]");
        return buf.toString();
    }
}
