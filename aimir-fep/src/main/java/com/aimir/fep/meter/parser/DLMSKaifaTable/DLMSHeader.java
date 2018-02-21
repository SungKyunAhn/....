package com.aimir.fep.meter.parser.DLMSKaifaTable;

import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.DLMS_CLASS;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.DLMS_CLASS_ATTR;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.OBIS;


public class DLMSHeader {
    private OBIS obis;
    private DLMS_CLASS clazz;
    private DLMS_CLASS_ATTR attr;
    private int length;
    
    public OBIS getObis() {
        return obis;
    }
    public void setObis(OBIS obis) {
        this.obis = obis;
    }
    public void setObis(String obisCode) {
        for (OBIS o : OBIS.values()) {
            if (o.getCode().equals(obisCode)) {
                this.obis = o;
                return;
            }
        }
    }
    public DLMS_CLASS getClazz() {
        return clazz;
    }
    public void setClazz(DLMS_CLASS clazz) {
        this.clazz = clazz;
    }
    public void setClazz(int clazz) {
        for (DLMS_CLASS _clazz : DLMS_CLASS.values()) {
            if (_clazz.getClazz() == clazz) {
                this.clazz = _clazz;
                return;
            }
        }
    }
    public DLMS_CLASS_ATTR getAttr() {
        return attr;
    }
    public void setAttr(DLMS_CLASS_ATTR attr) {
        this.attr = attr;
    }
    public void setAttr(int attr) {
        if (clazz == null) return;
        
        switch (clazz) {
        case DATA :
            this.attr = DLMS_CLASS_ATTR.DATA_ATTR01;
            break;
        case REGISTER :
            if (attr == DLMS_CLASS_ATTR.REGISTER_ATTR02.getAttr())
                this.attr = DLMS_CLASS_ATTR.REGISTER_ATTR02;
            else if (attr == DLMS_CLASS_ATTR.REGISTER_ATTR03.getAttr())
                this.attr = DLMS_CLASS_ATTR.REGISTER_ATTR03;
            break;            
        case EXTEND_REGISTER:
            if (attr == DLMS_CLASS_ATTR.REGISTER_ATTR02.getAttr())
                this.attr = DLMS_CLASS_ATTR.REGISTER_ATTR02;
            else if (attr == DLMS_CLASS_ATTR.REGISTER_ATTR03.getAttr())
                this.attr = DLMS_CLASS_ATTR.REGISTER_ATTR03;
        	break;
        case PROFILE_GENERIC :
            if (attr == DLMS_CLASS_ATTR.PROFILE_GENERIC_ATTR02.getAttr())
                this.attr = DLMS_CLASS_ATTR.PROFILE_GENERIC_ATTR02;
            else if (attr == DLMS_CLASS_ATTR.PROFILE_GENERIC_ATTR03.getAttr())
                this.attr = DLMS_CLASS_ATTR.PROFILE_GENERIC_ATTR03;
            else if (attr == DLMS_CLASS_ATTR.PROFILE_GENERIC_ATTR04.getAttr())
                this.attr = DLMS_CLASS_ATTR.PROFILE_GENERIC_ATTR04;
            else if (attr == DLMS_CLASS_ATTR.PROFILE_GENERIC_ATTR07.getAttr())
                this.attr = DLMS_CLASS_ATTR.PROFILE_GENERIC_ATTR07;
            // 2017.02.11 attr 8 error fixed by elevas
            else if (attr == DLMS_CLASS_ATTR.PROFILE_GENERIC_ATTR08.getAttr())
                this.attr = DLMS_CLASS_ATTR.PROFILE_GENERIC_ATTR08;
            break;
		case CLOCK:
			if(attr == DLMS_CLASS_ATTR.CLOCK_ATTR02.getAttr()){
				this.attr = DLMS_CLASS_ATTR.CLOCK_ATTR02;
			} else if (attr == DLMS_CLASS_ATTR.CLOCK_ATTR07.getAttr()){
				this.attr = DLMS_CLASS_ATTR.CLOCK_ATTR07;
			}
			break;
		case SAP_ASSIGN: 
            if (attr == DLMS_CLASS_ATTR.REGISTER_ATTR02.getAttr())
                this.attr = DLMS_CLASS_ATTR.REGISTER_ATTR02;
            else if (attr == DLMS_CLASS_ATTR.REGISTER_ATTR03.getAttr())
                this.attr = DLMS_CLASS_ATTR.REGISTER_ATTR03;
			break;
		case RELAY_CLASS:
            if (attr == DLMS_CLASS_ATTR.REGISTER_ATTR02.getAttr())
                this.attr = DLMS_CLASS_ATTR.REGISTER_ATTR02;
            else if (attr == DLMS_CLASS_ATTR.REGISTER_ATTR03.getAttr())
                this.attr = DLMS_CLASS_ATTR.REGISTER_ATTR03;
            else if (attr == DLMS_CLASS_ATTR.REGISTER_ATTR04.getAttr())
                this.attr = DLMS_CLASS_ATTR.REGISTER_ATTR04;
			break;
		case LIMITER_CLASS:
			if (attr == DLMS_CLASS_ATTR.LIMIT_ATTR06.getAttr())
				this.attr = DLMS_CLASS_ATTR.LIMIT_ATTR06;
			else if (attr == DLMS_CLASS_ATTR.LIMIT_ATTR07.getAttr())
				this.attr = DLMS_CLASS_ATTR.LIMIT_ATTR07;
			break;
		case MBUS_CLIENT_CLASS:
			if (attr == DLMS_CLASS_ATTR.DATA_ATTR06.getAttr())
				this.attr = DLMS_CLASS_ATTR.DATA_ATTR06;
			else if (attr == DLMS_CLASS_ATTR.DATA_ATTR07.getAttr())
				this.attr = DLMS_CLASS_ATTR.DATA_ATTR07;
			else if (attr == DLMS_CLASS_ATTR.DATA_ATTR09.getAttr())
				this.attr = DLMS_CLASS_ATTR.DATA_ATTR09;
			else if (attr == DLMS_CLASS_ATTR.DATA_ATTR04.getAttr())
				this.attr = DLMS_CLASS_ATTR.DATA_ATTR04;
			break;
		default:
			break;
        }
    }
    public int getLength() {
        return length;
    }
    public void setLength(int length) {
        this.length = length;
    }
}
