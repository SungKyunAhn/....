package com.aimir.fep.protocol.nip.command;

public class NiProtocolEventCode {
	
	
	public enum NIPEventCode {
		PowerFail("0101", "Power Fail",""),
		PowerRestore("0102", "Power Restore",""),
		TimeSynchronization("0501", "Time Synchronization",""),
		EquipmentConfigurationChanged("0701", "Equipment.Configuration Changed",""),
		EquipmentFirmwareUpdate("0907", "Equipment.Firmware Update",""),
		EquipmentInstalled("0902", "Equipment.Installed",""),
		OTADownloadDownload("0d01", "OTA Download.Download",""),
		OTADownloadStart("0d02", "OTA Download.Start",""),
		OTADownloadEnd("0d03", "OTA Download.End",""),
		OTADownloadResult("0d04", "OTA Download.Result",""),
		MeterNoResponse("0b02", "Meter No Response",""),
		MalfunctionMemoryError("0f03", "Malfunction.Memory Error",""),
		MeteringFailHLS("1301", "Metering Fail HLS",""),
		CommunicationFailSecurity("1302", "Communication Failure(Security)",""),
		CommunicationStartDTLS("1303", "Communication Start(DTLS)",""),
		CommunicationFailure("0e01", "Communication Failure",""),
		CommunicationRestore("0e02", "Communication Restore",""),
		Bootup("1001", "Boot up",""),
		ModemTimesync("1002", "Modem Timesync",""),
		ModemFactorySetting("1003", "Modem Factory Setting",""),
		Reset("1008", "Reset", ""),
		VoltageLow("1009", "Voltage Low",""),
		CLIStatus("100a", "CLI Status",""),
		CommStatus("100c", "Communication Status",""),
		NetworkDataTransfer("2002", "Network Data Transferation",""),
		NetworkStatus("2004", "Network Status", ""),
		PPPStatus("2005", "PPP Status",""),
		MeterReadStart("3000", "Meter Read Start",""),
		MeterReadFail("3001", "Meter Read Fail",""),
		MeterRead("3003", "Meter Read",""),
		MeterEvent("3004", "Meter Event",""),
		MeterSleep("5001", "Meter Event",""),
		ChildSet("a001", "Child Setting",""),
		ChildData("a002", "Child Data","");
		
		
		private String hexCode;
		private String eventName;
		private String descr;
	    
		NIPEventCode(String hexCode, String eventName, String descr) {
	        this.hexCode = hexCode;
	        this.eventName = eventName;
	        this.descr = descr;
	    }

		public String getHexCode() {
			return hexCode;
		}

		public void setHexCode(String hexCode) {
			this.hexCode = hexCode;
		}

		public String getEventName() {
			return eventName;
		}

		public void setEventName(String eventName) {
			this.eventName = eventName;
		}
		
		public String getDescription() {
			return descr;
		}

		public void setDescription(String descr) {
			this.descr = descr;
		}
		
		public static NIPEventCode getNipEventCode(String hexCode, String hexValue) {
			for (NIPEventCode fc : NIPEventCode.values()) {
				if (fc.hexCode.equals(hexCode)) {
					switch(fc.eventName){
					case "Equipment.Installed":
						if(hexValue.equals("00000000"))
							fc.setDescription("INSTALLED_SUCCESS");
						else if(hexValue.equals("00000001"))
							fc.setDescription("INSTALLED_START");
						else if(hexValue.equals("00000002"))
							fc.setDescription("INSTALLED_FAIL_COUNT");
						else if(hexValue.equals("00000003"))
							fc.setDescription("INSTALLED_FAIL_COUNT");
						break;
					case "Communication Start(DTLS)":
						if(hexValue.equals("01000000"))
							fc.setDescription("HES_SERVER");
						else if(hexValue.equals("02000000"))
							fc.setDescription("DCU_SERVER");
						else if(hexValue.equals("03000000"))
							fc.setDescription("COAP_SERVER");
						else if(hexValue.equals("04000000"))
							fc.setDescription("BYPASS_SERVER");
						else if(hexValue.equals("05000000"))
							fc.setDescription("CLIENT");
						break;
					case "Reset":
						if(hexValue.equals("00000001"))
							fc.setDescription("CLI");
						else if(hexValue.equals("00000002"))
							fc.setDescription("SCHEDULE");
						else if(hexValue.equals("00000003"))
							fc.setDescription("HARDFAULT");
						else if(hexValue.equals("00000004"))
							fc.setDescription("ONDEMAND");
						else if(hexValue.equals("00000005"))
							fc.setDescription("MAC_COMMAND");
						else if(hexValue.equals("00000006"))
							fc.setDescription("BLACKLIST");
						else if(hexValue.equals("00000007"))
							fc.setDescription("NETWORK_DOWN");
						else if(hexValue.equals("00000008"))
							fc.setDescription("NETWORK_SCAN_FAIL");
						else if(hexValue.equals("00000009"))
							fc.setDescription("FACTORY_SETTING");
						else if(hexValue.equals("0000000a"))
							fc.setDescription("TX_FAIL_MAX_COUNT");
						else if(hexValue.equals("0000000b"))
							fc.setDescription("OTA");
						else if(hexValue.equals("0000000c"))
							fc.setDescription("MAC_OTA");
						else if(hexValue.equals("0000000d"))
							fc.setDescription("NO_COMMUNICATION");
						else if(hexValue.equals("0000000e"))
							fc.setDescription("NO_COMM_FW_BACKUP");
						else if(hexValue.equals("0000000f"))
							fc.setDescription("ONDEMAND_COAP");
						else if(hexValue.equals("00000010"))
							fc.setDescription("ONDEMAND_SNMP_PROXY");
						else if(hexValue.equals("00000011"))
							fc.setDescription("FACTORY_SETTING_ONDEMAND_COAP");
						else if(hexValue.equals("00000012"))
							fc.setDescription("FACTORY_SETTING_ONDEMAND_SNMP_PROXY");
						else if(hexValue.equals("00000013"))
							fc.setDescription("WATCH_DOG");
						else if(hexValue.equals("00000014"))
							fc.setDescription("NO_RPL_PARENT");
						else if(hexValue.equals("00000015"))
							fc.setDescription("POISONING");
						else if(hexValue.equals("00000016"))
							fc.setDescription("NO_TSCH_PARENT");
						else if(hexValue.equals("00000017"))
							fc.setDescription("TSCH_SYNC_FAIL");
						else if(hexValue.equals("00000018"))
							fc.setDescription("NETWORK_FULL");
						else if(hexValue.equals("00000019"))
							fc.setDescription("CLONE_PROCESSOR_END");
						else if(hexValue.equals("0000001a"))
							fc.setDescription("PARENT_SELECT_MAX_FAIL");
						else if(hexValue.equals("0000001b"))
							fc.setDescription("DLMS_DATA_INTEGRITY_CHECK_FAILED");
						else if(hexValue.equals("0000001c"))
							fc.setDescription("WHILE_METERING_EXCEPTION");
						else if(hexValue.equals("0000001d"))
							fc.setDescription("COORDI_RA_FACTORY");
						else if(hexValue.equals("0000001e"))
							fc.setDescription("COORDI_RA_RESET");
						else if(hexValue.equals("0000001f"))
							fc.setDescription("DIFFERENT_METER");
						else if(hexValue.equals("00000020"))
							fc.setDescription("TEST_MODE_METER_SERIAL_READ ");
						break;
					case "CLI Status":
						if(hexValue.equals("00000001"))
							fc.setDescription("LOGIN");
						else if(hexValue.equals("00000002"))
							fc.setDescription("LOGOUT");
						else if(hexValue.equals("00000003"))
							fc.setDescription("INVALID_PASSWORD");
						break;
					case "Communication Status":
						if(hexValue.equals("00000001"))
							fc.setDescription("BASIC_UPLOADED");
						else if(hexValue.equals("00000002"))
							fc.setDescription("LP_UPLOADED");
						else if(hexValue.equals("00000003"))
							fc.setDescription("AGENT_UPLOADED");
						else if(hexValue.equals("00000004"))
							fc.setDescription("POLL_UPLOADED");
						else if(hexValue.equals("00000005"))
							fc.setDescription("MODEM_EVENT_SENT");
						else if(hexValue.equals("00000006"))
							fc.setDescription("METER_EVENT_SENT");
						else if(hexValue.equals("00000007"))
							fc.setDescription("NI_INFO_SENT");
						else if(hexValue.equals("00000008"))
							fc.setDescription("METER_KEY_RECVED");
						else if(hexValue.equals("00000009"))
							fc.setDescription("METER_KEY_FAILED");
						else if(hexValue.equals("0000000a"))
							fc.setDescription("METER_KEY_REQUESTE");
						break;
					case "Network Status":
						if(hexValue.equals("00000001"))
							fc.setDescription("STATUS_UP");
						else if(hexValue.equals("00000002"))
							fc.setDescription("STATUS_DOWN");
						else if(hexValue.equals("00000003"))
							fc.setDescription("STATUS_CHANGE");
						break;
					case "PPP Status":
						if(hexValue.equals("00000001"))
							fc.setDescription("STATUS_UP");
						else if(hexValue.equals("00000002"))
							fc.setDescription("STATUS_DOWN");
						break;
					case "Child Setting":
						if(hexValue.equals("00000000"))
							fc.setDescription("SUCCESS");
						else if(hexValue.equals("00000001"))
							fc.setDescription("FAIL_TIMESYNC");
						else if(hexValue.equals("00000002"))
							fc.setDescription("FAIL_FULL");
						break;
						
					case "Child Data":
						if(hexValue.equals("00000001"))
							fc.setDescription("SUCCESS");
						else if(hexValue.equals("00000002"))
							fc.setDescription("FAIL_TIMESYNC");
						else if(hexValue.equals("00000003"))
							fc.setDescription("FAIL_FULL");
						break;
					}
					return fc;
				}
			}
			return null;
		}
		
	}	
}
