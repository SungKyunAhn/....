package com.aimir.fep.protocol.smsp;

public class SMSConstants {
	
	public enum COMMAND_TYPE {
		ACCESS_TECHNOLOGY("01"), RSSI("02"), APN_USED("03"), IP_ADDRESS("04"), 
		SET_INIT_MODEM("50"), RESTART_COMM_STACK("51"), RESTART_COMM_MODEM("52"),
		CHANGE_APN("53"), WATCHDOG("54"), NI("55"), COAP("56"), SNMP("57");
		
		private String typeCode;

		private COMMAND_TYPE(String typeCode) {
			this.typeCode = typeCode;
		}

		public String getTypeCode() {
			return this.typeCode;
		}

		public COMMAND_TYPE getItem(String typeCode) {
			for (COMMAND_TYPE fc : COMMAND_TYPE.values()) {
				if (fc.typeCode.equals(typeCode)) {
					return fc;
				}
			}
			return null;
		}
	}
	
	public enum COMMAND_NAME {
		ACCESS_TECHNOLOGY("getAccessTechnology"), RSSI("getRSSI"), APN_USED("getUseddAPN"), 
		IP_ADDRESS("getIpAddress"), SET_INIT_MODEM("setInitModem"), RESTART_COMM_STACK("restartCommStack"),
		RESTART_COMM_MODEM("restartCommModem"), CHANGE_APN("setAPN"), WATCHDOG("doWatchdog");
		
		private String commandName;

		private COMMAND_NAME(String commandName) {
			this.commandName = commandName;
		}

		public String getCmdName() {
			return this.commandName;
		}

		public COMMAND_NAME getItem(String commandName) {
			for (COMMAND_NAME fc : COMMAND_NAME.values()) {
				if (fc.commandName.equals(commandName)) {
					return fc;
				}
			}
			return null;
		}
	}
	
	public enum MESSAGE_TYPE {
		REQ_ACK("0"), REQ_NON_ACK("1"), SUCCESS_RESPONSE("2"), BAD_RESPONSE("4");

		private String typeCode;

		private MESSAGE_TYPE(String typeCode) {
			this.typeCode = typeCode;
		}

		public String getTypeCode() {
			return this.typeCode;
		}

		public MESSAGE_TYPE getItem(String typeCode) {
			for (MESSAGE_TYPE fc : MESSAGE_TYPE.values()) {
				if (fc.typeCode.equals(typeCode)) {
					return fc;
				}
			}
			return null;
		}
	}
	
	public enum ERROR_TYPE {
		INVALID_FORMAT("00"), WRONG_HASH_CODE("01"), CHECK_FAIL_MOBILE_MODULE("02"), OUTSTANDING_WATCHDOG_REQUEST("03"),
		BUSY_FOR_METERING_TASK("04"), BUSY_FOR_NI_COMM("05"), BUSY_FOR_COAP_COMM("06"), BUSY_FOR_MODEM_INIT("07"), BUSY_FOR_SNMP_COMM("08");
		
		private String typeCode;

		private ERROR_TYPE(String typeCode) {
			this.typeCode = typeCode;
		}

		public String getTypeCode() {
			return this.typeCode;
		}

		public COMMAND_TYPE getItem(String typeCode) {
			for (COMMAND_TYPE fc : COMMAND_TYPE.values()) {
				if (fc.typeCode.equals(typeCode)) {
					return fc;
				}
			}
			return null;
		}
	}
	
}
