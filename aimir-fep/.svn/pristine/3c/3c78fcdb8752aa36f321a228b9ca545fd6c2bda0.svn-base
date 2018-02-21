package com.aimir.fep.protocol.nip.common;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.nip.command.AcceptJoin;
import com.aimir.fep.protocol.nip.command.AcceptLeave;
import com.aimir.fep.protocol.nip.command.ActiveKeepTime;
import com.aimir.fep.protocol.nip.command.AlarmEventCommandOnOff;
import com.aimir.fep.protocol.nip.command.AuthBackoffTimer;
import com.aimir.fep.protocol.nip.command.BootloaderJump;
import com.aimir.fep.protocol.nip.command.ChildNodeList;
import com.aimir.fep.protocol.nip.command.CloneOnOff;
import com.aimir.fep.protocol.nip.command.CoordinatorBootup;
import com.aimir.fep.protocol.nip.command.CoordinatorInformation;
import com.aimir.fep.protocol.nip.command.FactorySetting;
import com.aimir.fep.protocol.nip.command.GatheringDataAction;
import com.aimir.fep.protocol.nip.command.GatheringDataPoll;
import com.aimir.fep.protocol.nip.command.HopCount;
import com.aimir.fep.protocol.nip.command.HopNeighborList;
import com.aimir.fep.protocol.nip.command.JoinBackoffTimer;
import com.aimir.fep.protocol.nip.command.JoinNetwork;
import com.aimir.fep.protocol.nip.command.KeepAliveMessageInterval;
import com.aimir.fep.protocol.nip.command.KepcoObisListVersion;
import com.aimir.fep.protocol.nip.command.KepcoObisNewList;
import com.aimir.fep.protocol.nip.command.MeterBaud;
import com.aimir.fep.protocol.nip.command.MeterInformation;
import com.aimir.fep.protocol.nip.command.MeterLoadProfileInterval;
import com.aimir.fep.protocol.nip.command.MeterSharedKey;
import com.aimir.fep.protocol.nip.command.MeterTimeSync;
import com.aimir.fep.protocol.nip.command.MeteringInterval;
import com.aimir.fep.protocol.nip.command.ModemEventLog;
import com.aimir.fep.protocol.nip.command.ModemInformation;
import com.aimir.fep.protocol.nip.command.ModemIpInformation;
import com.aimir.fep.protocol.nip.command.ModemMode;
import com.aimir.fep.protocol.nip.command.ModemPortInformation;
import com.aimir.fep.protocol.nip.command.ModemResetTime;
import com.aimir.fep.protocol.nip.command.ModemStatus;
import com.aimir.fep.protocol.nip.command.ModemTime;
import com.aimir.fep.protocol.nip.command.ModemTxPower;
import com.aimir.fep.protocol.nip.command.NbPlcInformation;
import com.aimir.fep.protocol.nip.command.NbPlcModulatioin;
import com.aimir.fep.protocol.nip.command.NbPlcTmrMode;
import com.aimir.fep.protocol.nip.command.NetworkIpv6Prefix;
import com.aimir.fep.protocol.nip.command.NetworkJoinTimeout;
import com.aimir.fep.protocol.nip.command.NetworkPermit;
import com.aimir.fep.protocol.nip.command.NetworkScanInterval;
import com.aimir.fep.protocol.nip.command.NetworkSpeed;
import com.aimir.fep.protocol.nip.command.NodeAuthorization;
import com.aimir.fep.protocol.nip.command.NullBypassClose;
import com.aimir.fep.protocol.nip.command.NullBypassOpen;
import com.aimir.fep.protocol.nip.command.ObisAdd;
import com.aimir.fep.protocol.nip.command.ObisListChange;
import com.aimir.fep.protocol.nip.command.ObisListUp;
import com.aimir.fep.protocol.nip.command.ObisRemove;
import com.aimir.fep.protocol.nip.command.ParentNodeInfo;
import com.aimir.fep.protocol.nip.command.RawRomAccess;
import com.aimir.fep.protocol.nip.command.ReAuthenticate;
import com.aimir.fep.protocol.nip.command.RealTimeMetering;
import com.aimir.fep.protocol.nip.command.RecoveryLPData;
import com.aimir.fep.protocol.nip.command.RecoveryMetering;
import com.aimir.fep.protocol.nip.command.ResetModem;
import com.aimir.fep.protocol.nip.command.RetryCount;
import com.aimir.fep.protocol.nip.command.RomRead;
import com.aimir.fep.protocol.nip.command.SelfRead;
import com.aimir.fep.protocol.nip.command.SingleActionSchedule;
import com.aimir.fep.protocol.nip.command.SnmpTrapOnOff;
import com.aimir.fep.protocol.nip.command.TestConfiguration;
import com.aimir.fep.protocol.nip.command.TestDataUpload;
import com.aimir.fep.protocol.nip.command.TransmitFrequency;
import com.aimir.fep.protocol.nip.command.UploadMeteringData;
import com.aimir.fep.protocol.nip.command.WatchdogTest;
import com.aimir.fep.protocol.nip.frame.GeneralFrame;
import com.aimir.fep.protocol.nip.frame.payload.Bypass;
import com.aimir.fep.protocol.nip.frame.payload.Bypass.TID_Type;
import com.aimir.fep.util.Hex;

public class GeneralDataFrame {
    private static Log log = LogFactory.getLog(GeneralDataFrame.class); 

    /**
     * It transmits from Fep to (DCU or MIU)
     * @param command
     * @param param
     * @return
     */
    public byte[] make(GeneralFrame command, HashMap param) {
        try {
            byte[] cmdArray1 = null;
            switch (command.foType) {
                case  Ack:
                    break;
                case  Bypass:
                    //Request Data
                    command.setFrame();
                    ((Bypass)command.payload).setTidType((TID_Type)param.get("tidType"));
                    ((Bypass)command.payload).setTidLocation((byte)param.get("tidLocation"));
                    ((Bypass)command.payload).set_tid((byte)param.get("tranId"));
                    ((Bypass)command.payload).setPayload((byte[])param.get("payload"));
                    cmdArray1 = command.payload.encode();
                    break;
                case  Metering:
                    //log.debug("[NICL][FOTYPE]Metering");
                    //Request Data
                    break;
                case  Command:
                    //Attribute 처리해서 Command 가져옴.
                    switch (command.niAttributeId) {
                        case  ResetModem:
                            command.abstractCmd = new ResetModem();
                            command.payload = command.abstractCmd.set();
                            break;
                        case  UploadMeteringData:
                            command.abstractCmd = new UploadMeteringData();
                            command.payload = command.abstractCmd.set(param);
                            break;
                        case FactorySetting:
                            command.abstractCmd = new FactorySetting();
                            command.payload = command.abstractCmd.set();
                            break;
                        case RecoveryPulseLoadProfileData:
                            command.abstractCmd = new RecoveryLPData();
                            command.payload = command.abstractCmd.get(param);
                            break;
                        case ReAuthenticate:
                            command.abstractCmd = new ReAuthenticate();
                            command.payload = command.abstractCmd.get();
                            break;
                        case WatchdogTest:
        	                command.abstractCmd = new WatchdogTest();
        	                command.payload = command.abstractCmd.set();
        	                break;
                        case ModemInformation:
                            command.abstractCmd = new ModemInformation();
                            switch (command._commandType) {
                                case  Get:
                                    command.payload = command.abstractCmd.get();
                                    break;
                                case  Trap:
                                    command.payload = command.abstractCmd.trap();
                                    break;
                            }
                            break;
                        case NB_PLCInformation:
                            command.abstractCmd = new NbPlcInformation();
                            command.payload = command.abstractCmd.get();
                            break;
                        case ModemStatus:
                            command.abstractCmd = new ModemStatus();
                            command.payload = command.abstractCmd.get();
                            break;
                        case MeterInformation:
                            command.abstractCmd = new MeterInformation();
                            switch (command._commandType) {
                                case  Get:
                                    command.payload = command.abstractCmd.get();
                                    break;
                                case  Trap:
                                    command.payload = command.abstractCmd.trap();
                                    break;
                            }
                            break;	 
                        case ModemEventLog:
                            command.abstractCmd = new ModemEventLog(); 
                            command.payload = command.abstractCmd.get(param);
                            break;
                        case CloneOnOff:
                            command.abstractCmd = new CloneOnOff(); 
                            command.payload = command.abstractCmd.set(param);
                            break;
                        case ModemTime:
                            command.abstractCmd = new ModemTime();
                            switch (command._commandType) {
                                case  Get:
                                    command.payload = command.abstractCmd.get();
                                    break;
                                case  Set:
                                    command.payload = command.abstractCmd.set(param);
                                    break;
                            }
                            break;
                        case ModemResetTime:	
                            command.abstractCmd = new ModemResetTime();
                            command.payload = command.abstractCmd.set(param);
                            break;
                        case ModemMode:
                        case ModemMode_GET:
                            command.abstractCmd = new ModemMode();
                            switch (command._commandType) {
                            case Get:
                                command.payload = command.abstractCmd.get();
                            	break;
                            case Set:
                                command.payload = command.abstractCmd.set(param);
                            	break;
                            }
                            break;
                        case MeterTimesync:
                            command.abstractCmd = new MeterTimeSync();
                            command.payload = command.abstractCmd.set(param);
                            break;
                        case MeteringInterval:
                        case MeteringInterval_GET:
                            command.abstractCmd = new MeteringInterval();
                            switch (command._commandType) {
                                case  Get:
                                    command.payload = command.abstractCmd.get();
                                    break;
                                case  Set:
                                    command.payload = command.abstractCmd.set(param);
                                    break;
                            }
                            break;
                        case ModemTXPower:
                            command.abstractCmd = new ModemTxPower();
                            command.payload = command.abstractCmd.set(param);
                            break;
                        case KeepAliveMessageInterval:
                            command.abstractCmd = new KeepAliveMessageInterval();
                            command.payload = command.abstractCmd.set(param);
                            break;
                        case ActiveKeepTime:
                            command.abstractCmd = new ActiveKeepTime();
                            command.payload = command.abstractCmd.set(param);
                            break;
                        case NetworkScanInterval:
                            command.abstractCmd = new NetworkScanInterval();
                            command.payload = command.abstractCmd.set(param);
                            break;
                        case Form_JoinNetwork:
                            command.abstractCmd = new JoinNetwork();
                            command.payload = command.abstractCmd.set(param);
                            break;
                        case NetworkSpeed:
                            command.abstractCmd = new NetworkSpeed();
                            command.payload = command.abstractCmd.set(param);
                            break;	 
                        case NetworkJoinTimeout:
                            command.abstractCmd = new NetworkJoinTimeout();
                            command.payload = command.abstractCmd.set(param);
                            break;	 
                        case ModemIpInformation:	
                        case ModemIpInformation_GET:
                            command.abstractCmd = new ModemIpInformation();
                            switch (command._commandType) {
                            	case  Get:
                            		command.payload = command.abstractCmd.get(param);
                            		break;
                            	case  Set:
                            		command.payload = command.abstractCmd.set(param);
                            		break;
                            }
                            break;
                        case ModemPortInformation:	
                        case ModemPortInformation_GET:
                            command.abstractCmd = new ModemPortInformation();
                            switch (command._commandType) {
                            	case  Get:
                            		command.payload = command.abstractCmd.get(param);
                            		break;
                            	case  Set:
                            		command.payload = command.abstractCmd.set(param);
                            		break;
                            }
                            break;                                 
                        case Alarm_EventCommandON_OFF:
                        case Alarm_EventCommandON_OFF_GET:
                            command.abstractCmd = new AlarmEventCommandOnOff();
                            switch (command._commandType) {
                                case  Get:
                                    command.payload = command.abstractCmd.get(param);
                                    break;
                                case  Set:
                                    command.payload = command.abstractCmd.set(param);
                                    break;
                            }
                            break;	
                        case MeterBaud:
                        case MeterBaud_GET:
                            command.abstractCmd = new MeterBaud();
                            switch (command._commandType) {
                                case  Get:
                                    command.payload = command.abstractCmd.get();
                                    break;
                                case  Set:
                                    command.payload = command.abstractCmd.set(param);
                                    break;
                            }
                            break;
                        case RealTimeMetering:
                            command.abstractCmd = new RealTimeMetering();
                            command.payload = command.abstractCmd.set(param);
                            break;
                        case TransmitFrequency:
                        case TransmitFrequency_GET:
                            command.abstractCmd = new TransmitFrequency();
                            switch (command._commandType) {
                                case  Get:
                                    command.payload = command.abstractCmd.get();
                                    break;
                                case  Set:
                                    command.payload = command.abstractCmd.set(param);
                                    break;
                            }
                            break;
                        case RetryCount:
                        case RetryCount_GET:
                            command.abstractCmd = new RetryCount();
                            switch (command._commandType) {
                                case  Get:
                                    command.payload = command.abstractCmd.get();
                                    break;
                                case  Set:
                                    command.payload = command.abstractCmd.set(param);
                                    break;
                            }
                            break;
                            
                        case SnmpTrapOnOff:
                        case SnmpTrapOnOff_GET:
        	                command.abstractCmd = new SnmpTrapOnOff();
        	                switch (command._commandType) {
                            case  Get:
                                command.payload = command.abstractCmd.get();
                                break;
                            case  Set:
                                command.payload = command.abstractCmd.set(param);
                                break;
        	                }
        	                break;
                        case RawROMAccess:
                        case RawROMAccess_GET:
                        	command.abstractCmd = new RawRomAccess();
                        	switch (command._commandType) {
                            case  Get:
                                command.payload = command.abstractCmd.get();
                                break;
                            case  Set:
                                command.payload = command.abstractCmd.set(param);
                                break;
                        	}
                        	break;   
                        case NB_PLCTMR:
                            command.abstractCmd = new NbPlcTmrMode();
                            command.payload = command.abstractCmd.set(param);
                            break;
                        case NB_PLCModulation:
                            command.abstractCmd = new NbPlcModulatioin();
                            command.payload = command.abstractCmd.set(param);
                            break;
                        case MeterLoadprofileinterval:
                            command.abstractCmd = new MeterLoadProfileInterval();
                            command.payload = command.abstractCmd.set(param);
                            break;
                        case SelfReadscript:
                            command.abstractCmd = new SelfRead();
                            command.payload = command.abstractCmd.set(param);
                            break;
                        case SingleActionSchedule:
                            command.abstractCmd = new SingleActionSchedule();
                            command.payload = command.abstractCmd.set(param);
                            break;
                        case RecoveryMetering:
                            command.abstractCmd = new RecoveryMetering();
                            command.payload = command.abstractCmd.set(param);
                            break;
                        case OBISListup:
                            command.abstractCmd = new ObisListUp();
                            command.payload = command.abstractCmd.get();
                            break;
                        case OBISAdd:
                            command.abstractCmd = new ObisAdd();
                            command.payload = command.abstractCmd.set(param);
                            break;
                        case OBISRemove:
                            command.abstractCmd = new ObisRemove();
                            command.payload = command.abstractCmd.set(param);
                            break;
                        case OBISListChange:
                            command.abstractCmd = new ObisListChange();
                            command.payload = command.abstractCmd.set(param);
                            break;
                        case KEPCOOBISNewList:
                            command.abstractCmd = new KepcoObisNewList();
                            command.payload = command.abstractCmd.set(param);
                            break;
                        case KEPCOOBISListVersion:
                            command.abstractCmd = new KepcoObisListVersion();
                            command.payload = command.abstractCmd.get();
                            break;
                        case TestConfiguration:
                            command.abstractCmd = new TestConfiguration();
                            switch (command._commandType) {
                                case  Get:
                                    command.payload = command.abstractCmd.get();
                                    break;
                                case  Set:
                                    command.payload = command.abstractCmd.set(param);
                                    break;
                            }
                            break;
                        case TestDataUpload:
                            command.abstractCmd = new TestDataUpload();
                            command.payload = command.abstractCmd.get(param);
                            break;
                        case CoordinatorInformation:
                            command.abstractCmd = new CoordinatorInformation();
                            switch (command._commandType) {
                                case  Get:
                                    command.payload = command.abstractCmd.get();
                                    break;
                                case  Trap:
                                    command.payload = command.abstractCmd.trap();
                                    break;
                            }
                            break;
                        case NetworkPermit:
                            command.abstractCmd = new NetworkPermit();
                            switch (command._commandType) {
                                case  Get:
                                    command.payload = command.abstractCmd.get();
                                    break;
                                case  Set:
                                    command.payload = command.abstractCmd.set(param);
                                    break;
                            }
                            break;
                        case BootloaderJump:
                            command.abstractCmd = new BootloaderJump();
                            command.payload = command.abstractCmd.set(param);
                            break;
                        case CoordinatorBootup:
                            command.abstractCmd = new CoordinatorBootup();
                            command.payload = command.abstractCmd.trap();
                            break;
                        case NetworkIPv6Prefix:
                            command.abstractCmd = new NetworkIpv6Prefix();
                            switch (command._commandType) {
                                case  Get:
                                    command.payload = command.abstractCmd.get();
                                    break;
                                case  Set:
                                    command.payload = command.abstractCmd.set(param);
                                    break;
                            }
                            break;
                        case AcceptJoin:
                            command.abstractCmd = new AcceptJoin();
                            switch (command._commandType) {
                                case  Get:
                                    command.payload = command.abstractCmd.get();
                                    break;
                                case  Trap:
                                    command.payload = command.abstractCmd.trap();
                                    break;
                            }
                            break;
                        case AcceptLeave:
                            command.abstractCmd = new AcceptLeave();
                            switch (command._commandType) {
                                case  Get:
                                    command.payload = command.abstractCmd.get();
                                    break;
                                case  Trap:
                                    command.payload = command.abstractCmd.trap();
                                    break;
                            }
                            break;
                        case JoinBackoffTimer:
                            command.abstractCmd = new JoinBackoffTimer();
                            switch (command._commandType) {
                                case  Get:
                                    command.payload = command.abstractCmd.get();
                                    break;
                                case  Set:
                                    command.payload = command.abstractCmd.set(param);
                                    break;
                            }
                            break;
                        case AuthBackoffTimer:
                            command.abstractCmd = new AuthBackoffTimer();
                            switch (command._commandType) {
                                case  Get:
                                    command.payload = command.abstractCmd.get();
                                    break;
                                case  Set:
                                    command.payload = command.abstractCmd.set(param);
                                    break;
                            }
                            break;
                        case MeterSharedKey:
                            command.abstractCmd = new MeterSharedKey();
                            switch (command._commandType) {
                                case  Get:
                                    command.payload = command.abstractCmd.get(param);
                                    break;
                                case  Set:
                                    command.payload = command.abstractCmd.set(param);
                                    break;
                            }
                            break;
                        case NullBypassOpen:
                            command.abstractCmd = new NullBypassOpen();
                            switch (command._commandType) {
                                case  Get:
                                    command.payload = command.abstractCmd.get();
                                    break;
                                case  Set:
                                    command.payload = command.abstractCmd.set(param);
                                    break;
                            }
                            break;
                        case NullBypassClose:
                            command.abstractCmd = new NullBypassClose();
                            switch (command._commandType) {
                                case  Get:
                                    command.payload = command.abstractCmd.get();
                                    break;
                                case  Set:
                                    command.payload = command.abstractCmd.set(param);
                                    break;
                                case  Trap:
                                    command.payload = command.abstractCmd.trap();
                                    break;
                            }
                            break;
                        case ROMRead:
                            command.abstractCmd = new RomRead();
                            command.payload = command.abstractCmd.get(param);
                            break;
                        case GatheringDataAction:
                            command.abstractCmd = new GatheringDataAction();
                            command.payload = command.abstractCmd.set(param);
                            break;
                        case GatheringDataPoll:
                            command.abstractCmd = new GatheringDataPoll();
                            command.payload = command.abstractCmd.get(param);
                            break;
                        case ParentNodeInfo:
                            command.abstractCmd = new ParentNodeInfo();
                            command.payload = command.abstractCmd.get();
                            break;
                        case HopCount:
                            command.abstractCmd = new HopCount();
                            command.payload = command.abstractCmd.get();
                            break;
                        case HopNeighborList:
                            command.abstractCmd = new HopNeighborList();
                            command.payload = command.abstractCmd.get();
                            break;
                        case ChildNodeList:
                            command.abstractCmd = new ChildNodeList();
                            command.payload = command.abstractCmd.get();
                            break;
                        case NodeAuthorization:
                            command.abstractCmd = new NodeAuthorization();
                            switch (command._commandType) {
                                case  Get:
                                    command.payload = command.abstractCmd.get(param);
                                    break;
                                case  Set:
                                    command.payload = command.abstractCmd.set(param);
                                    break;
                            }
                            break;
                    }
                   	command.payload.setCommandFlow(command._commandFlow.getCode());
                   	command.payload.setCommandType(command._commandType.getCode());
                   	//Request Data
                   	cmdArray1 = command.payload.encode();
                   	// log.debug("[NICL][PayLoad Data]"+Hex.decode(cmdArray1));
                   	break;
                case  Firmware:
                    // log.debug("[NICL][FOTYPE]Firmware");
                    break;
                case  AlarmEvent:
                    // log.debug("[NICL][FOTYPE]AlarmEvent");
        	 		break;
                case  MeterEvent:
                    // log.debug("[NICL][FOTYPE]MeterEvent");
                    break;
            }
            log.debug("[NICL][GeneraDatalFrame|cmdArray1]"+Hex.decode(cmdArray1));
        	//GeneralFrame + Payload
        	byte[] cmdArray2 = command.encode(cmdArray1);
        	
        	log.debug("[NICL][GeneraDatalFrame|cmdArray2]"+Hex.decode(cmdArray2));
        	
        	//Sending
        	//multi sending test
        	//byte[] test = new byte[]{(byte)0x51,(byte)0xF8,(byte)0x00,(byte)0x01,(byte)0x03,(byte)0x00,(byte)0x00,(byte)0x09,(byte)0x00,(byte)0x01,(byte)0x02,(byte)0x03,(byte)0x04,(byte)0x05,(byte)0x06,(byte)0x07,(byte)0x08,(byte)0x09,(byte)0x11,(byte)0x12,(byte)0x13,(byte)0x14,(byte)0x15,(byte)0x16,(byte)0x17,(byte)0x18,(byte)0x19,(byte)0x10,(byte)0x11,(byte)0x12,(byte)0x13,(byte)0x14,(byte)0x15,(byte)0x16,(byte)0x17,(byte)0x18,(byte)0x19,(byte)0x20,(byte)0x21,(byte)0x22,(byte)0x23,(byte)0x24,(byte)0x25,(byte)0x26,(byte)0x27,(byte)0x28,(byte)0x29,(byte)0x30,(byte)0x31,(byte)0x32,(byte)0x33,(byte)0x34,(byte)0x35,(byte)0x36,(byte)0x37,(byte)0x38,(byte)0x39,(byte)0x40,(byte)0x41,(byte)0x42,(byte)0x43,(byte)0x44,(byte)0x45,(byte)0x46,(byte)0x47,(byte)0x48,(byte)0x49,(byte)0x50,(byte)0x51,(byte)0x52,(byte)0x53,(byte)0x54,(byte)0x55,(byte)0x56,(byte)0x57,(byte)0x58,(byte)0x59,(byte)0x60,(byte)0x61,(byte)0x62,(byte)0x63,(byte)0x64,(byte)0x65,(byte)0x66,(byte)0x67,(byte)0x68,(byte)0x69,(byte)0x70,(byte)0x71,(byte)0x72,(byte)0x73,(byte)0x74,(byte)0x75,(byte)0x76,(byte)0x77,(byte)0x78,(byte)0x79,(byte)0x80,(byte)0x81,(byte)0x82,(byte)0x83,(byte)0x84,(byte)0x85,(byte)0x86,(byte)0x87,(byte)0x88,(byte)0x89,(byte)0x90,(byte)0x91,(byte)0x92,(byte)0x93,(byte)0x94,(byte)0x95,(byte)0x96,(byte)0x97,(byte)0x98,(byte)0x99,(byte)0xFF,(byte)0x00,(byte)0x01,(byte)0x02,(byte)0x03,(byte)0x04,(byte)0x05,(byte)0x06,(byte)0x07,(byte)0x08,(byte)0x09,(byte)0x11,(byte)0x12,(byte)0x13,(byte)0x14,(byte)0x15,(byte)0x16,(byte)0x17,(byte)0x18,(byte)0x19,(byte)0x10,(byte)0x11,(byte)0x12,(byte)0x13,(byte)0x14,(byte)0x15,(byte)0x16,(byte)0x17,(byte)0x18,(byte)0x19,(byte)0x20,(byte)0x21,(byte)0x22,(byte)0x23,(byte)0x24,(byte)0x25,(byte)0x26,(byte)0x27,(byte)0x28,(byte)0x29,(byte)0x30,(byte)0x31,(byte)0x32,(byte)0x33,(byte)0x34,(byte)0x35,(byte)0x36,(byte)0x37,(byte)0x38,(byte)0x39,(byte)0x40,(byte)0x41,(byte)0x42,(byte)0x43,(byte)0x44,(byte)0x45,(byte)0x46,(byte)0x47,(byte)0x48,(byte)0x49,(byte)0x50,(byte)0x51,(byte)0x52,(byte)0x53,(byte)0x54,(byte)0x55,(byte)0x56,(byte)0x57,(byte)0x58,(byte)0x59,(byte)0x60,(byte)0x61,(byte)0x62,(byte)0x63,(byte)0x64,(byte)0x65,(byte)0x66,(byte)0x67,(byte)0x68,(byte)0x69,(byte)0x70,(byte)0x71,(byte)0x72,(byte)0x73,(byte)0x74,(byte)0x75,(byte)0x76,(byte)0x77,(byte)0x78,(byte)0x79,(byte)0x80,(byte)0x81,(byte)0x82,(byte)0x83,(byte)0x84,(byte)0x85,(byte)0x86,(byte)0x87,(byte)0x88,(byte)0x89,(byte)0x90,(byte)0x91,(byte)0x92,(byte)0x93,(byte)0x94,(byte)0x95,(byte)0x96,(byte)0x97,(byte)0x98,(byte)0x99,(byte)0xFF,(byte)0x00,(byte)0x01,(byte)0x02,(byte)0x03,(byte)0x04,(byte)0x05,(byte)0x06,(byte)0x07,(byte)0x08,(byte)0x09,(byte)0x11,(byte)0x12,(byte)0x13,(byte)0x14,(byte)0x15,(byte)0x16,(byte)0x17,(byte)0x18,(byte)0x19,(byte)0x10,(byte)0x11,(byte)0x12,(byte)0x13,(byte)0x14,(byte)0x15,(byte)0x16,(byte)0x17,(byte)0x18,(byte)0x19,(byte)0x20,(byte)0x21,(byte)0x22,(byte)0x23,(byte)0x24,(byte)0x25,(byte)0x26,(byte)0x27,(byte)0x28,(byte)0x29,(byte)0x30,(byte)0x31,(byte)0x32,(byte)0x33,(byte)0x34,(byte)0x35,(byte)0x36,(byte)0x37,(byte)0x38,(byte)0x39,(byte)0x40,(byte)0x41,(byte)0x42,(byte)0x43,(byte)0x44,(byte)0x45,(byte)0x46,(byte)0x47,(byte)0x48,(byte)0x49,(byte)0x50,(byte)0x51,(byte)0x52,(byte)0x53,(byte)0x54,(byte)0x55,(byte)0x56,(byte)0x57,(byte)0x58,(byte)0x59,(byte)0x60,(byte)0x61,(byte)0x62,(byte)0x63,(byte)0x64,(byte)0x65,(byte)0x66,(byte)0x67,(byte)0x68,(byte)0x69,(byte)0x70,(byte)0x71,(byte)0x72,(byte)0x73,(byte)0x74,(byte)0x75,(byte)0x76,(byte)0x77,(byte)0x78,(byte)0x79,(byte)0x80,(byte)0x81,(byte)0x82,(byte)0x83,(byte)0x84,(byte)0x85,(byte)0x86,(byte)0x87,(byte)0x88,(byte)0x89,(byte)0x90,(byte)0x91,(byte)0x92,(byte)0x93,(byte)0x94,(byte)0x95,(byte)0x96,(byte)0x97,(byte)0x98,(byte)0x99,(byte)0xFF,(byte)0x00,(byte)0x01,(byte)0x02,(byte)0x03,(byte)0x04,(byte)0x05,(byte)0x06,(byte)0x07,(byte)0x08,(byte)0x09,(byte)0x11,(byte)0x12,(byte)0x13,(byte)0x14,(byte)0x15,(byte)0x16,(byte)0x17,(byte)0x18,(byte)0x19,(byte)0x10,(byte)0x11,(byte)0x12,(byte)0x13,(byte)0x14,(byte)0x15,(byte)0x16,(byte)0x17,(byte)0x18,(byte)0x19,(byte)0x20,(byte)0x21,(byte)0x22,(byte)0x23,(byte)0x24,(byte)0x25,(byte)0x26,(byte)0x27,(byte)0x28,(byte)0x29,(byte)0x30,(byte)0x31,(byte)0x32,(byte)0x33,(byte)0x34,(byte)0x35,(byte)0x36,(byte)0x37,(byte)0x38,(byte)0x39,(byte)0x40,(byte)0x41,(byte)0x42,(byte)0x43,(byte)0x44,(byte)0x45,(byte)0x46,(byte)0x47,(byte)0x48,(byte)0x49,(byte)0x50,(byte)0x51,(byte)0x52,(byte)0x53,(byte)0x54,(byte)0x55,(byte)0x56,(byte)0x57,(byte)0x58,(byte)0x59,(byte)0x60,(byte)0x61,(byte)0x62,(byte)0x63,(byte)0x64,(byte)0x65,(byte)0x66,(byte)0x67,(byte)0x68,(byte)0x69,(byte)0x70,(byte)0x71,(byte)0x72,(byte)0x73,(byte)0x74,(byte)0x75,(byte)0x76,(byte)0x77,(byte)0x78,(byte)0x79,(byte)0x80,(byte)0x81,(byte)0x82,(byte)0x83,(byte)0x84,(byte)0x85,(byte)0x86,(byte)0x87,(byte)0x88,(byte)0x89,(byte)0x90,(byte)0x91,(byte)0x92,(byte)0x93,(byte)0x94,(byte)0x95,(byte)0x96,(byte)0x97,(byte)0x98,(byte)0x99,(byte)0xFF,(byte)0x00,(byte)0x01,(byte)0x02,(byte)0x03,(byte)0x04,(byte)0x05,(byte)0x06,(byte)0x07,(byte)0x08,(byte)0x09,(byte)0x11,(byte)0x12,(byte)0x13,(byte)0x14,(byte)0x15,(byte)0x16,(byte)0x17,(byte)0x18,(byte)0x19,(byte)0x10,(byte)0x11,(byte)0x12,(byte)0x13,(byte)0x14,(byte)0x15,(byte)0x16,(byte)0x17,(byte)0x18,(byte)0x19,(byte)0x20,(byte)0x21,(byte)0x22,(byte)0x23,(byte)0x24,(byte)0x25,(byte)0x26,(byte)0x27,(byte)0x28,(byte)0x29,(byte)0x30,(byte)0x31,(byte)0x32,(byte)0x33,(byte)0x34,(byte)0x35,(byte)0x36,(byte)0x37,(byte)0x38,(byte)0x39,(byte)0x40,(byte)0x41,(byte)0x42,(byte)0x43,(byte)0x44,(byte)0x45,(byte)0x46,(byte)0x47,(byte)0x48,(byte)0x49,(byte)0x50,(byte)0x51,(byte)0x52,(byte)0x53,(byte)0x54,(byte)0x55,(byte)0x56,(byte)0x57,(byte)0x58,(byte)0x59,(byte)0x60,(byte)0x61,(byte)0x62,(byte)0x63,(byte)0x64,(byte)0x65,(byte)0x66,(byte)0x67,(byte)0x68,(byte)0x69,(byte)0x70,(byte)0x71,(byte)0x72,(byte)0x73,(byte)0x74,(byte)0x75,(byte)0x76,(byte)0x77,(byte)0x78,(byte)0x79,(byte)0x80,(byte)0x81,(byte)0x82,(byte)0x83,(byte)0x84,(byte)0x85,(byte)0x86,(byte)0x87,(byte)0x88,(byte)0x89,(byte)0x90,(byte)0x91,(byte)0x92,(byte)0x93,(byte)0x94,(byte)0x95,(byte)0x96,(byte)0x97,(byte)0x98,(byte)0x99,(byte)0xFF,(byte)0x00,(byte)0x01,(byte)0x02,(byte)0x03,(byte)0x04,(byte)0x05,(byte)0x06,(byte)0x07,(byte)0x08,(byte)0x09,(byte)0x11,(byte)0x12,(byte)0x13,(byte)0x14,(byte)0x15,(byte)0x16,(byte)0x17,(byte)0x18,(byte)0x19,(byte)0x10,(byte)0x11,(byte)0x12,(byte)0x13,(byte)0x14,(byte)0x15,(byte)0x16,(byte)0x17,(byte)0x18,(byte)0x19,(byte)0x20,(byte)0x21,(byte)0x22,(byte)0x23,(byte)0x24,(byte)0x25,(byte)0x26,(byte)0x27,(byte)0x28,(byte)0x29,(byte)0x30,(byte)0x31,(byte)0x32,(byte)0x33,(byte)0x34,(byte)0x35,(byte)0x36,(byte)0x37,(byte)0x38,(byte)0x39,(byte)0x40,(byte)0x41,(byte)0x42,(byte)0x43,(byte)0x44,(byte)0x45,(byte)0x46,(byte)0x47,(byte)0x48,(byte)0x49,(byte)0x50,(byte)0x51,(byte)0x52,(byte)0x53,(byte)0x54,(byte)0x55,(byte)0x56,(byte)0x57,(byte)0x58,(byte)0x59,(byte)0x60,(byte)0x61,(byte)0x62,(byte)0x63,(byte)0x64,(byte)0x65,(byte)0x66,(byte)0x67,(byte)0x68,(byte)0x69,(byte)0x70,(byte)0x71,(byte)0x72,(byte)0x73,(byte)0x74,(byte)0x75,(byte)0x76,(byte)0x77,(byte)0x78,(byte)0x79,(byte)0x80,(byte)0x81,(byte)0x82,(byte)0x83,(byte)0x84,(byte)0x85,(byte)0x86,(byte)0x87,(byte)0x88,(byte)0x89,(byte)0x90,(byte)0x91,(byte)0x92,(byte)0x93,(byte)0x94,(byte)0x95,(byte)0x96,(byte)0x97,(byte)0x98,(byte)0x99,(byte)0xFF,(byte)0x00,(byte)0x01,(byte)0x02,(byte)0x03,(byte)0x04,(byte)0x05,(byte)0x06,(byte)0x07,(byte)0x08,(byte)0x09,(byte)0x11,(byte)0x12,(byte)0x13,(byte)0x14,(byte)0x15,(byte)0x16,(byte)0x17,(byte)0x18,(byte)0x19,(byte)0x10,(byte)0x11,(byte)0x12,(byte)0x13,(byte)0x14,(byte)0x15,(byte)0x16,(byte)0x17,(byte)0x18,(byte)0x19,(byte)0x20,(byte)0x21,(byte)0x22,(byte)0x23,(byte)0x24,(byte)0x25,(byte)0x26,(byte)0x27,(byte)0x28,(byte)0x29,(byte)0x30,(byte)0x31,(byte)0x32,(byte)0x33,(byte)0x34,(byte)0x35,(byte)0x36,(byte)0x37,(byte)0x38,(byte)0x39,(byte)0x40,(byte)0x41,(byte)0x42,(byte)0x43,(byte)0x44,(byte)0x45,(byte)0x46,(byte)0x47,(byte)0x48,(byte)0x49,(byte)0x50,(byte)0x51,(byte)0x52,(byte)0x53,(byte)0x54,(byte)0x55,(byte)0x56,(byte)0x57,(byte)0x58,(byte)0x59,(byte)0x60,(byte)0x61,(byte)0x62,(byte)0x63,(byte)0x64,(byte)0x65,(byte)0x66,(byte)0x67,(byte)0x68,(byte)0x69,(byte)0x70,(byte)0x71,(byte)0x72,(byte)0x73,(byte)0x74,(byte)0x75,(byte)0x76,(byte)0x77,(byte)0x78,(byte)0x79,(byte)0x80,(byte)0x81,(byte)0x82,(byte)0x83,(byte)0x84,(byte)0x85,(byte)0x86,(byte)0x87,(byte)0x88,(byte)0x89,(byte)0x90,(byte)0x91,(byte)0x92,(byte)0x93,(byte)0x94,(byte)0x95,(byte)0x96,(byte)0x97,(byte)0x98,(byte)0x99,(byte)0xFF,(byte)0xD9,(byte)0xDE};
//        	log.debug("test.length:"+test.length);
//        	log.debug("test:"+Hex.decode(test));
        	return cmdArray2;
//		} catch (NullPointerException ex) {
//			throw new NiException("[GeneralDataFrame]Prarameter Null이 존재 합니다.");
        } catch (Exception e) {
            log.error(e, e);
        }
		 return null;
	 }

	
	/**
	 * It decode received from MIU  
	 * @param command
	 * @param data
	 */
	public void decode(GeneralFrame command,byte[] data) {
	    try {
	        switch (command.niAttributeId) {
	            case  ResetModem:
	                command.abstractCmd = new ResetModem();
	                break;
	            case  UploadMeteringData:
	                command.abstractCmd = new UploadMeteringData();
	                break;
	            case FactorySetting:
	                command.abstractCmd = new FactorySetting();
	                break;
	            case RecoveryPulseLoadProfileData:
	                command.abstractCmd = new RecoveryLPData();
	                break;
	            case ReAuthenticate:
	                command.abstractCmd = new ReAuthenticate();
	                break;
	            case WatchdogTest:
	                command.abstractCmd = new WatchdogTest();
	                break;
	            case ModemInformation:
	                command.abstractCmd = new ModemInformation();
	                break;
	            case NB_PLCInformation:
	                command.abstractCmd = new NbPlcInformation();
	                break;
	            case ModemStatus:
	                command.abstractCmd = new ModemStatus();
	                break;
	            case MeterInformation:
	                command.abstractCmd = new MeterInformation();
	                break;	 
	            case ModemEventLog:
	                command.abstractCmd = new ModemEventLog();
	                break;
	            case CloneOnOff:
	                command.abstractCmd = new CloneOnOff();
	                break;    
	                
	            case ModemTime:
	                command.abstractCmd = new ModemTime();
	                break;
	            case ModemResetTime:
	                command.abstractCmd = new ModemResetTime();
	                break;
	            case ModemMode:
	                command.abstractCmd = new ModemMode();
	                break;
	            case MeterTimesync:
	                command.abstractCmd = new MeterTimeSync();
	                break;
	            case MeteringInterval:
	                command.abstractCmd = new MeteringInterval();
	                break;
	            case ModemTXPower:
	                command.abstractCmd = new ModemTxPower();
	                break;
	            case KeepAliveMessageInterval:
	                command.abstractCmd = new KeepAliveMessageInterval();
	                break;
	            case ActiveKeepTime:
	                command.abstractCmd = new ActiveKeepTime();
	                break;
	            case NetworkScanInterval:
	                command.abstractCmd = new NetworkScanInterval();
	                break;
	            case Form_JoinNetwork:
	                command.abstractCmd = new JoinNetwork();
	                break;
	            case NetworkSpeed:
	                command.abstractCmd = new NetworkSpeed();
	                break;	 
	            case NetworkJoinTimeout:
	                command.abstractCmd = new NetworkJoinTimeout();
	                break;	 
	            case ModemIpInformation:
	                command.abstractCmd = new ModemIpInformation();
	                break;
	            case ModemPortInformation:
	                command.abstractCmd = new ModemPortInformation();
	                break;      
	            case Alarm_EventCommandON_OFF:
	                command.abstractCmd = new AlarmEventCommandOnOff();
	                break;	
	            case MeterBaud:
	                command.abstractCmd = new MeterBaud();
	                break;
	            case TransmitFrequency:
	                command.abstractCmd = new TransmitFrequency();
	                break;
	            case RetryCount:
	                command.abstractCmd = new RetryCount();
	                break;
	            case SnmpTrapOnOff:
	                command.abstractCmd = new SnmpTrapOnOff();
	                break;
	            case RawROMAccess:
	                command.abstractCmd = new RawRomAccess();
	                break;   
	            case NB_PLCTMR:
	                command.abstractCmd = new NbPlcTmrMode();
	                break;
	            case NB_PLCModulation:
	                command.abstractCmd = new NbPlcModulatioin();
	                break;
	            case MeterLoadprofileinterval:
	                command.abstractCmd = new MeterLoadProfileInterval();
	                break;
	            case SelfReadscript:
	                command.abstractCmd = new SelfRead();
	                break;
	            case SingleActionSchedule:
	                command.abstractCmd = new SingleActionSchedule();
	                break;
	            case RecoveryMetering:
	                command.abstractCmd = new RecoveryMetering();
	                break;
	            case OBISListup:
	                command.abstractCmd = new ObisListUp();
	                break;
	            case OBISAdd:
	                command.abstractCmd = new ObisAdd();
	                break;
	            case OBISRemove:
	                command.abstractCmd = new ObisRemove();
	                break;
	            case OBISListChange:
	                command.abstractCmd = new ObisListChange();
	                break;
	            case KEPCOOBISNewList:
	                command.abstractCmd = new KepcoObisNewList();
	                break;
	            case KEPCOOBISListVersion:
	                command.abstractCmd = new KepcoObisListVersion();
	                break;
	            case TestConfiguration:
	                command.abstractCmd = new TestConfiguration();
	                break;
	            case TestDataUpload:
	                command.abstractCmd = new TestDataUpload();
	                break;
	            case CoordinatorInformation:
	                command.abstractCmd = new CoordinatorInformation();
	                break;
	            case NetworkPermit:
	                command.abstractCmd = new NetworkPermit();
	                break;
	            case BootloaderJump:
	                command.abstractCmd = new BootloaderJump();
	                break;
	            case CoordinatorBootup:
	                command.abstractCmd = new CoordinatorBootup();
	                break;
	            case NetworkIPv6Prefix:
	                command.abstractCmd = new NetworkIpv6Prefix();
	                break;
	            case AcceptJoin:
	                command.abstractCmd = new AcceptJoin();
	                break;
	            case AcceptLeave:
	                command.abstractCmd = new AcceptLeave();
	                break;
	            case JoinBackoffTimer:
	                command.abstractCmd = new JoinBackoffTimer();
	                break;
	            case AuthBackoffTimer:
	                command.abstractCmd = new AuthBackoffTimer();
	                break;
	            case MeterSharedKey:
	                command.abstractCmd = new MeterSharedKey();
	                break;
	            case NullBypassOpen:
	                command.abstractCmd = new NullBypassOpen();
	                break;
	            case NullBypassClose:
	                command.abstractCmd = new NullBypassClose();
	                break;
	            case ROMRead:
	                command.abstractCmd = new RomRead();
	                break;
	            case GatheringDataAction:
	                command.abstractCmd = new GatheringDataAction();
	                break;
	            case GatheringDataPoll:
	                command.abstractCmd = new GatheringDataPoll();
	                break;
	            case ParentNodeInfo:
	                command.abstractCmd = new ParentNodeInfo();
	                break;
	            case HopCount:
	                command.abstractCmd = new HopCount();
	                break;
	            case HopNeighborList:
	                command.abstractCmd = new HopNeighborList();
	                break;
	            case ChildNodeList:
	                command.abstractCmd = new ChildNodeList();
	                break;
	            case NodeAuthorization:
	                command.abstractCmd = new NodeAuthorization();
	                break;
	            case RealTimeMetering:
	            	command.abstractCmd = new RealTimeMetering();
	            	break;
	        }
	        command.abstractCmd.decode(data);
	        log.debug("[NICL][GeneralDataFrame]"+Hex.decode(data));
	    }
	    catch (Exception e) {
	        log.error(e, e);
	    }
	 }
}
