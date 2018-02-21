package com.aimir.fep.util;

import com.aimir.fep.bypass.dlms.enums.ObjectType;

public class DLMSCmdUtil {

	public static String getCmdName(int classId, String attributeNo, String cmdType) {
		String cmd=null;
		
		if (classId == ObjectType.REGISTER.getValue()) {
			switch (attributeNo) {
			case "2":
				cmd = "cmd"+cmdType+"RegisterValue";
				break;
			case "3":
				cmd = "cmd"+cmdType+"RegisterUnit";
			default:
				break;
			}
		} else if (classId == ObjectType.PROFILE_GENERIC.getValue()) {
			switch (attributeNo) {
			case "2":
				cmd = "cmd"+cmdType+"ProfileBuffer";
				break;
			case "3":
				cmd = "cmd"+cmdType+"ProfileObject";
				break;
			case "4":
				cmd = "cmd"+cmdType+"ProfilePeriod";
				break;

			default:
				break;
			}
		} else if (classId == ObjectType.CLOCK.getValue()) {
			switch (attributeNo) {
			case "2":
				cmd = "cmd"+cmdType+"DLMSMeterTime";
				break;

			default:
				break;
			}
		} else if (classId == ObjectType.LIMITER.getValue()) {
			switch (attributeNo) {
			case "4":
				cmd = "cmd"+cmdType+"ThresholdNormal";
				break;
			case "6":
				cmd = "cmd"+cmdType+"MinOverThresholdDuration";
			default:
				break;
			}
		} else if (classId == ObjectType.DISCONNECT_CONTROL.getValue() ) {
			switch (attributeNo) {
			case "2":
				cmd = "cmd"+cmdType+"RelayState";
				break;
			default:
				break;
			}
		}
		else if ( classId == ObjectType.MBUS_CLIENT.getValue()){
			if ( "Act".equals(cmdType)) {
				switch ( attributeNo ){
					case "7" :
						cmd = "cmd" + cmdType + "SetEncryptionKey";
						break;
					case "8" :
						cmd = "cmd" + cmdType + "TransferKey";
						break;
					default : 
						break;
				}
			}
		}
		if ( cmd == null ){
			cmd = "cmd" + cmdType + "Value";
		}
		return cmd;
	}
	
}
