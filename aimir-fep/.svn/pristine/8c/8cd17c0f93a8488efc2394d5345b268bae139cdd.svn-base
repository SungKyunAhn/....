package com.aimir.fep.meter.parser.DLMSNamjunTable;

import com.aimir.fep.meter.parser.DLMSNamjunTable.DLMSVARIABLE.DLMS_CLASS;
import com.aimir.fep.meter.parser.DLMSNamjunTable.DLMSVARIABLE.DLMS_CLASS_ATTR;
import com.aimir.fep.meter.parser.DLMSNamjunTable.DLMSVARIABLE.OBIS;

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
            break;
		case CLOCK:
			if(attr == DLMS_CLASS_ATTR.CLOCK_ATTR01.getAttr()){
				this.attr = DLMS_CLASS_ATTR.CLOCK_ATTR01;
			}else if(attr == DLMS_CLASS_ATTR.CLOCK_ATTR02.getAttr()){
				this.attr = DLMS_CLASS_ATTR.CLOCK_ATTR02;
			}else if(attr == DLMS_CLASS_ATTR.CLOCK_ATTR03.getAttr()){
				this.attr = DLMS_CLASS_ATTR.CLOCK_ATTR03;
			}else if(attr == DLMS_CLASS_ATTR.CLOCK_ATTR04.getAttr()){
				this.attr = DLMS_CLASS_ATTR.CLOCK_ATTR04;
			}else if(attr == DLMS_CLASS_ATTR.CLOCK_ATTR05.getAttr()){
				this.attr = DLMS_CLASS_ATTR.CLOCK_ATTR05;
			}else if(attr == DLMS_CLASS_ATTR.CLOCK_ATTR06.getAttr()){
				this.attr = DLMS_CLASS_ATTR.CLOCK_ATTR06;
			}else if(attr == DLMS_CLASS_ATTR.CLOCK_ATTR07.getAttr()){
				this.attr = DLMS_CLASS_ATTR.CLOCK_ATTR07;
			}else if(attr == DLMS_CLASS_ATTR.CLOCK_ATTR08.getAttr()){
				this.attr = DLMS_CLASS_ATTR.CLOCK_ATTR08;
			}else if(attr == DLMS_CLASS_ATTR.CLOCK_ATTR09.getAttr()){
				this.attr = DLMS_CLASS_ATTR.CLOCK_ATTR09;
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
        case SCRIPT_TABLE :
            if (attr == DLMS_CLASS_ATTR.SCRIPT_TABLE_ATTR01.getAttr())
                this.attr = DLMS_CLASS_ATTR.SCRIPT_TABLE_ATTR01;
            else if (attr == DLMS_CLASS_ATTR.SCRIPT_TABLE_ATTR02.getAttr())
                this.attr = DLMS_CLASS_ATTR.SCRIPT_TABLE_ATTR02;
            break;
        case G3_PLC_6LoWPAN : 
        	if(attr == DLMS_CLASS_ATTR.ADP_WEAK_LQI_VALUE.getAttr()){
        		this.attr = DLMS_CLASS_ATTR.ADP_WEAK_LQI_VALUE;
        	}
        	break;
        case HDLC : 
        	if(attr == DLMS_CLASS_ATTR.HDLC_ATTR01.getAttr()){
        		this.attr = DLMS_CLASS_ATTR.HDLC_ATTR01;
        	}
        	else if(attr == DLMS_CLASS_ATTR.HDLC_ATTR02.getAttr()){
        		this.attr = DLMS_CLASS_ATTR.HDLC_ATTR02;
        	}
        	else if(attr == DLMS_CLASS_ATTR.HDLC_ATTR03.getAttr()){
        		this.attr = DLMS_CLASS_ATTR.HDLC_ATTR03;
        	}
        	else if(attr == DLMS_CLASS_ATTR.HDLC_ATTR04.getAttr()){
        		this.attr = DLMS_CLASS_ATTR.HDLC_ATTR04;
        	}
        	else if(attr == DLMS_CLASS_ATTR.HDLC_ATTR05.getAttr()){
        		this.attr = DLMS_CLASS_ATTR.HDLC_ATTR05;
        	}
        	else if(attr == DLMS_CLASS_ATTR.HDLC_ATTR06.getAttr()){
        		this.attr = DLMS_CLASS_ATTR.HDLC_ATTR06;
        	}
        	else if(attr == DLMS_CLASS_ATTR.HDLC_ATTR07.getAttr()){
        		this.attr = DLMS_CLASS_ATTR.HDLC_ATTR07;
        	}
        	else if(attr == DLMS_CLASS_ATTR.HDLC_ATTR08.getAttr()){
        		this.attr = DLMS_CLASS_ATTR.HDLC_ATTR08;
        	}
        	if(attr == DLMS_CLASS_ATTR.HDLC_ATTR09.getAttr()){
        		this.attr = DLMS_CLASS_ATTR.HDLC_ATTR09;
        	}
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
