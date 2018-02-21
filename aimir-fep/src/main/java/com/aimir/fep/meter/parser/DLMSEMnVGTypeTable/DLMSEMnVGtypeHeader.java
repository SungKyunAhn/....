package com.aimir.fep.meter.parser.DLMSEMnVGTypeTable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.fep.meter.parser.DLMSEMnVGTypeTable.DLMSEMnVGtypeVARIABLE.DLMS_CLASS;
import com.aimir.fep.meter.parser.DLMSEMnVGTypeTable.DLMSEMnVGtypeVARIABLE.DLMS_CLASS_ATTR;
import com.aimir.fep.meter.parser.DLMSEMnVGTypeTable.DLMSEMnVGtypeVARIABLE.OBIS;
import com.aimir.fep.protocol.emnv.frame.EMnVConstants.EMnVMeteringDataType;

public class DLMSEMnVGtypeHeader {
	private static Logger log = LoggerFactory.getLogger(DLMSEMnVGtypeHeader.class);

	private OBIS obis;
	private DLMS_CLASS clazz;
	private DLMS_CLASS_ATTR attr;
	private int length;
	private EMnVMeteringDataType meteringDataType; // Billing or Load Profile

	public OBIS getObis() {
		return obis;
	}

	public EMnVMeteringDataType getObisType() {
		return meteringDataType;
	}

	public void setObis(EMnVMeteringDataType type, OBIS obis) {
		this.meteringDataType = type;
		this.obis = obis;
	}

	public void setObis(EMnVMeteringDataType type, String obisCode) {
		this.meteringDataType = type;

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
		case CLOCK:
			this.attr = DLMS_CLASS_ATTR.CLOCK_ATTR02;
			break;
		case DATA:
			this.attr = DLMS_CLASS_ATTR.DATA_ATTR02;
			break;
		case REGISTER:
			if (attr == DLMS_CLASS_ATTR.REGISTER_ATTR02.getAttr()) {
				this.attr = DLMS_CLASS_ATTR.REGISTER_ATTR02;
			} else if (attr == DLMS_CLASS_ATTR.REGISTER_ATTR03.getAttr()) {
				this.attr = DLMS_CLASS_ATTR.REGISTER_ATTR03;
			}
			break;
		case PROFILE_GENERIC:
			if (attr == DLMS_CLASS_ATTR.PROFILE_GENERIC_ATTR02.getAttr()) {
				this.attr = DLMS_CLASS_ATTR.PROFILE_GENERIC_ATTR02;
				//            else if (attr == DLMS_CLASS_ATTR.PROFILE_GENERIC_ATTR03.getAttr())
				//            	this.attr = DLMS_CLASS_ATTR.PROFILE_GENERIC_ATTR03;
				//            else if (attr == DLMS_CLASS_ATTR.PROFILE_GENERIC_ATTR04.getAttr())
				//            	this.attr = DLMS_CLASS_ATTR.PROFILE_GENERIC_ATTR04;
			} else if (attr == DLMS_CLASS_ATTR.PROFILE_GENERIC_ATTR07.getAttr()) {
				this.attr = DLMS_CLASS_ATTR.PROFILE_GENERIC_ATTR07;
			} else if (attr == DLMS_CLASS_ATTR.PROFILE_GENERIC_ATTR08.getAttr()) {
				this.attr = DLMS_CLASS_ATTR.PROFILE_GENERIC_ATTR08;
			}
			break;
		//        case DEMAND_REGISTER :
		//        	if (attr == DLMS_CLASS_ATTR.DEMAND_REGISTER_ATTR01.getAttr())
		//        		this.attr = DLMS_CLASS_ATTR.DEMAND_REGISTER_ATTR01;
		//        	else if (attr == DLMS_CLASS_ATTR.DEMAND_REGISTER_ATTR02.getAttr())
		//        		this.attr = DLMS_CLASS_ATTR.DEMAND_REGISTER_ATTR02;
		//        	else if (attr == DLMS_CLASS_ATTR.DEMAND_REGISTER_ATTR03.getAttr())
		//        		this.attr = DLMS_CLASS_ATTR.DEMAND_REGISTER_ATTR03;
		//        	else if (attr == DLMS_CLASS_ATTR.DEMAND_REGISTER_ATTR04.getAttr())
		//        		this.attr = DLMS_CLASS_ATTR.DEMAND_REGISTER_ATTR04;
		//        	else if (attr == DLMS_CLASS_ATTR.DEMAND_REGISTER_ATTR05.getAttr())
		//        		this.attr = DLMS_CLASS_ATTR.DEMAND_REGISTER_ATTR05;
		//        	else if (attr == DLMS_CLASS_ATTR.DEMAND_REGISTER_ATTR06.getAttr())
		//        		this.attr = DLMS_CLASS_ATTR.DEMAND_REGISTER_ATTR06;
		//        	else if (attr == DLMS_CLASS_ATTR.DEMAND_REGISTER_ATTR07.getAttr())
		//        		this.attr = DLMS_CLASS_ATTR.DEMAND_REGISTER_ATTR07;
		//        	else if (attr == DLMS_CLASS_ATTR.DEMAND_REGISTER_ATTR08.getAttr())
		//        		this.attr = DLMS_CLASS_ATTR.DEMAND_REGISTER_ATTR08;
		//        	else if (attr == DLMS_CLASS_ATTR.DEMAND_REGISTER_ATTR09.getAttr())
		//        		this.attr = DLMS_CLASS_ATTR.DEMAND_REGISTER_ATTR09;
		//        case SCRIPT_TABLE :
		//            if (attr == DLMS_CLASS_ATTR.SCRIPT_TABLE_ATTR01.getAttr())
		//                this.attr = DLMS_CLASS_ATTR.SCRIPT_TABLE_ATTR01;
		//            else if (attr == DLMS_CLASS_ATTR.SCRIPT_TABLE_ATTR02.getAttr())
		//                this.attr = DLMS_CLASS_ATTR.SCRIPT_TABLE_ATTR02;
		case EXTENDED_REGISTER:
			if (attr == DLMS_CLASS_ATTR.EXTENDED_REGISTER_ATTR02.getAttr()) {
				this.attr = DLMS_CLASS_ATTR.EXTENDED_REGISTER_ATTR02;
			} else if (attr == DLMS_CLASS_ATTR.EXTENDED_REGISTER_ATTR03.getAttr()) {
				this.attr = DLMS_CLASS_ATTR.EXTENDED_REGISTER_ATTR03;
			} else if (attr == DLMS_CLASS_ATTR.EXTENDED_REGISTER_ATTR05.getAttr()) {
				this.attr = DLMS_CLASS_ATTR.EXTENDED_REGISTER_ATTR05;
			}
			break;
		case SINGLE_ACTION_SCHEDULE:
			if (attr == DLMS_CLASS_ATTR.SINGLE_ACTION_SCHEDULE_ATTR04.getAttr()) {
				this.attr = DLMS_CLASS_ATTR.SINGLE_ACTION_SCHEDULE_ATTR04;
			}
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
