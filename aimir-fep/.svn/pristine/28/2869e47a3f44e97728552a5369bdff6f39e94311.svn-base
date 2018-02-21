package com.aimir.fep.meter.parser.DLMSEMnVEtype_1_0_Table;

import com.aimir.fep.meter.parser.DLMSEMnVEtype_1_0_Table.DLMSEMnVEType_1_0_VARIABLE.DLMS_CLASS;
import com.aimir.fep.meter.parser.DLMSEMnVEtype_1_0_Table.DLMSEMnVEType_1_0_VARIABLE.DLMS_CLASS_ATTR;
import com.aimir.fep.meter.parser.DLMSEMnVEtype_1_0_Table.DLMSEMnVEType_1_0_VARIABLE.OBIS;
import com.aimir.fep.protocol.emnv.frame.EMnVConstants.EMnVMeteringDataType;

public class DLMSEMnVEType_1_0_Header {
	private OBIS obis;
	private DLMS_CLASS clazz;
	private DLMS_CLASS_ATTR attr;
	private int length;
	private EMnVMeteringDataType meteringDataType; // Load Profile

	public OBIS getObis() {
		return obis;
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
			} else if (attr == DLMS_CLASS_ATTR.PROFILE_GENERIC_ATTR07.getAttr()) {
				this.attr = DLMS_CLASS_ATTR.PROFILE_GENERIC_ATTR07;
			}
			break;
		}
	}

	public void setLength(int length) {
		this.length = length;
	}
}
