package com.aimir.fep.protocol.nip.client;

import java.util.HashMap;

import com.aimir.fep.protocol.fmp.common.Target;
import com.aimir.fep.protocol.nip.command.AcceptJoin;
import com.aimir.fep.protocol.nip.command.AcceptLeave;
import com.aimir.fep.protocol.nip.command.ActiveKeepTime;
import com.aimir.fep.protocol.nip.command.AlarmEventCommandOnOff;
import com.aimir.fep.protocol.nip.command.Apn;
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
import com.aimir.fep.protocol.nip.command.ModemMode;
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
import com.aimir.fep.protocol.nip.command.ReAuthenticate;
import com.aimir.fep.protocol.nip.command.RecoveryLPData;
import com.aimir.fep.protocol.nip.command.RecoveryMetering;
import com.aimir.fep.protocol.nip.command.ResetModem;
import com.aimir.fep.protocol.nip.command.RetryCount;
import com.aimir.fep.protocol.nip.command.RomRead;
import com.aimir.fep.protocol.nip.command.SelfRead;
import com.aimir.fep.protocol.nip.command.SingleActionSchedule;
import com.aimir.fep.protocol.nip.command.TestConfiguration;
import com.aimir.fep.protocol.nip.command.TestDataUpload;
import com.aimir.fep.protocol.nip.command.TransmitFrequency;
import com.aimir.fep.protocol.nip.command.UploadMeteringData;
import com.aimir.fep.protocol.nip.common.GeneralDataFrame;
import com.aimir.fep.protocol.nip.frame.GeneralFrame;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.NIAttributeId;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.NetworkType;
import com.aimir.fep.protocol.nip.frame.NetworkStatusSub1GhzForSORIA;

public class NiUdpAllTestClient{
	public static void main(String[] args) {
		try{
		// TODO Auto-generated method stub
		  NiClient client= new NiClient();
    	  Target target = new Target();
    	  target.setIpv6Addr("FD00:544A:16AF:F699:AABB:CCDD:EEFF:0101");//모뎀
//    	  target.setIpv6Addr("fd00:544a:16af:f699::1");
    	  
    	  target.setPort(65363);
    	  GeneralFrame command = new GeneralFrame();
    	  GeneralDataFrame data = new GeneralDataFrame();
    	  /**
    	   * Client Parameter Infomation
    	   * 1)Attribute List:ResetModem,
    	   * 2)Command Flow:Request,Response
    	   * 3)Command Type:Get,Set,Trap
    	   * 4)Network Type:Sub1Ghz,NB_PLC,Zigbee,MBB,Ethernet
    	   * 5)FrameControl Pending:MultiFrame
    	   * 6)FrameControl Ack:Ack,Task
    	   * 7)FrameOption NetworkStatus:None,Include
    	   * 8)FrameOption AddressType:None,Source,Destination,SrcDest
    	   * 9)FrameOption Type:Ack,Bypass,Metering,Command,Firmware
    	   * 10)maxPayloadcnt :(byte)0x00~0xFF(0~255)
    	   * 11)Source Station : EUI64 8byte
    	   * 12)destination Station : EUI64 8byte
    	   */
    	  //foAddrType에 따라 source/destination 적용한다. 없으면 null,null로 보냄
    	  byte[] maxPayloadcnt = new byte[]{(byte)0x00};
    	  byte[] source  =  new byte[]{(byte)0x00, (byte)0x01, (byte)0x02, (byte)0x03 ,(byte)0x04, (byte)0x05, (byte)0x06, (byte)0x07};
    	  byte[] destination  =  new byte[]{(byte)0x10, (byte)0x11, (byte)0x12, (byte)0x13 ,(byte)0x14, (byte)0x15, (byte)0x16, (byte)0x17};
    	  //MeterInformation
    	  //command Frame
    	  
    	  for (NIAttributeId c : NIAttributeId.values()) {
              command.setClientBaseInfo(
        			  c
        			  ,command._commandFlow.Request
        			  ,command._commandType.Get
        			  ,command._networkType.Sub1Ghz_SORIA
        			  ,command.fcPending.LastFrame
        			  ,command.fcAck.None
        			  ,command.foNetworkStatus.None
        			  ,command.foAddrType.None
        			  ,command.foType.Command
        			  ,maxPayloadcnt
        			  ,source
        			  ,destination
        	  );
        	  
        	  /**
        	   * Network Status Field 의 유.무를 결정하기 위한 필드이다.
        	   * Network 경로를 확인하고, 모뎀의 네트워크 상태를 확인하기 위한 필드이다.
        	   * Include Network Status Field 이면
        	   */
          	  switch (command.foNetworkStatus) {
          	  	case Include:
          	  	//한전 Sub1Ghz는 사용안함.
        	    //Soria RF
	      	  	command.newNetworkStatus(command._networkType);
	      	  	command.networkStatus.setParentNode("5051525354555657");//8byte
	      	  	command.networkStatus.setRssi((byte)0x10);
	      	    command.networkStatus.setEtx((new byte[]{(byte)0x10,(byte)0x10}));

	      	    if(command._networkType == NetworkType.Sub1Ghz_SORIA){
		      	    ((NetworkStatusSub1GhzForSORIA)command.networkStatus).setCpu((byte)0x80);//80%
		      	    ((NetworkStatusSub1GhzForSORIA)command.networkStatus).setMemory((byte)0x70);//70%
		      	    ((NetworkStatusSub1GhzForSORIA)command.networkStatus).setTotalTx(new byte[]{(byte)0x01,(byte)0x01,(byte)0x01,(byte)0x01});	      	    	
	      	    }
	      	    
        		break;
          	  	case None:
        		break;
          	  }
          	  /**
          	   * create request data
          	   */
          	  HashMap param =new HashMap();
				switch (c) {
				case ResetModem:
					command.abstractCmd = new ResetModem();
					break;
				case UploadMeteringData:
					command.abstractCmd = new UploadMeteringData();
					param.put("uploadTime", 1);
					param.put("meterCount", 2);
					param.put("uploadTime", new int[] { 1111, 2222 });
					command.payload = command.abstractCmd.set();
					break;
				case FactorySetting:
					command.abstractCmd = new FactorySetting();
					command.payload = command.abstractCmd.set();
					break;
				case RecoveryPulseLoadProfileData:
					command.abstractCmd = new RecoveryLPData();
					param.put("offset", 1);
					param.put("count", 2);
					command.payload = command.abstractCmd.get(param);
					break;
				case ReAuthenticate:
					command.abstractCmd = new ReAuthenticate();
					command.payload = command.abstractCmd.get();
					break;
				case ModemInformation:
					command.abstractCmd = new ModemInformation();
					switch (command._commandType) {
					case Get:
						command.payload = command.abstractCmd.get();
						break;
					case Trap:
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
					case Get:
						command.payload = command.abstractCmd.get();
						break;
					case Trap:
						command.payload = command.abstractCmd.trap();
						break;
					}
					break;
				case ModemEventLog:
					command.abstractCmd = new ModemEventLog();
					param.put("offset", 1);
					param.put("count", 2);
					command.payload = command.abstractCmd.get(param);
					break;
				case CloneOnOff:
					command.abstractCmd = new CloneOnOff();
					param.put("count", 1);
					command.payload = command.abstractCmd.get(param);
					break;	
				case ModemTime:
					command.abstractCmd = new ModemTime();
					switch (command._commandType) {
					case Get:
						command.payload = command.abstractCmd.get();
						break;
					case Set:
						param.put("yyyymmddhhmmss", "20160520");
						command.payload = command.abstractCmd.set(param);
						break;
					}
					break;
				case ModemResetTime:
					command.abstractCmd = new ModemResetTime();
					param.put("resetTime", 1);
					command.payload = command.abstractCmd.set(param);
					break;
				case ModemMode:
					command.abstractCmd = new ModemMode();
					param.put("resetTime", 1);
					command.payload = command.abstractCmd.set(param);
					break;
				case MeterTimesync:
					command.abstractCmd = new MeterTimeSync();
					command.payload = command.abstractCmd.set(param);
					break;
				case MeteringInterval:
					command.abstractCmd = new MeteringInterval();
					switch (command._commandType) {
					case Get:
						command.payload = command.abstractCmd.get();
						break;
					case Set:

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
				case Alarm_EventCommandON_OFF:
					command.abstractCmd = new AlarmEventCommandOnOff();
					switch (command._commandType) {
					case Get:
						command.payload = command.abstractCmd.get(param);
						break;
					case Set:
						command.payload = command.abstractCmd.set(param);
						break;
					}
					break;
				case MeterBaud:
					command.abstractCmd = new MeterBaud();
					switch (command._commandType) {
					case Get:
						command.payload = command.abstractCmd.get();
						break;
					case Set:
						command.payload = command.abstractCmd.set(param);
						break;
					}
					break;
				case TransmitFrequency:
					command.abstractCmd = new TransmitFrequency();
					switch (command._commandType) {
					case Get:
						command.payload = command.abstractCmd.get();
						break;
					case Set:
						command.payload = command.abstractCmd.set(param);
						break;
					}
					break;
				case RetryCount:
					command.abstractCmd = new RetryCount();
					switch (command._commandType) {
					case Get:
						command.payload = command.abstractCmd.get();
						break;
					case Set:
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
					case Get:
						command.payload = command.abstractCmd.get();
						break;
					case Set:
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
					case Get:
						command.payload = command.abstractCmd.get();
						break;
					case Trap:
						command.payload = command.abstractCmd.trap();
						break;
					}
					break;
				case NetworkPermit:
					command.abstractCmd = new NetworkPermit();
					switch (command._commandType) {
					case Get:
						command.payload = command.abstractCmd.get();
						break;
					case Set:
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
					case Get:
						command.payload = command.abstractCmd.get();
						break;
					case Set:
						command.payload = command.abstractCmd.set(param);
						break;
					}
					break;
				case AcceptJoin:
					command.abstractCmd = new AcceptJoin();
					switch (command._commandType) {
					case Get:
						command.payload = command.abstractCmd.get();
						break;
					case Trap:
						command.payload = command.abstractCmd.trap();
						break;
					}
					break;
				case AcceptLeave:
					command.abstractCmd = new AcceptLeave();
					switch (command._commandType) {
					case Get:
						command.payload = command.abstractCmd.get();
						break;
					case Trap:
						command.payload = command.abstractCmd.trap();
						break;
					}
					break;
				case JoinBackoffTimer:
					command.abstractCmd = new JoinBackoffTimer();
					switch (command._commandType) {
					case Get:
						command.payload = command.abstractCmd.get();
						break;
					case Set:
						command.payload = command.abstractCmd.set(param);
						break;
					}
					break;
				case AuthBackoffTimer:
					command.abstractCmd = new AuthBackoffTimer();
					switch (command._commandType) {
					case Get:
						command.payload = command.abstractCmd.get();
						break;
					case Set:
						command.payload = command.abstractCmd.set(param);
						break;
					}
					break;
				case MeterSharedKey:
					command.abstractCmd = new MeterSharedKey();
					switch (command._commandType) {
					case Get:
						command.payload = command.abstractCmd.get(param);
						break;
					case Set:
						command.payload = command.abstractCmd.set(param);
						break;
					}
					break;
				case NullBypassOpen:
					command.abstractCmd = new NullBypassOpen();
					switch (command._commandType) {
					case Get:
						command.payload = command.abstractCmd.get();
						break;
					case Set:
						command.payload = command.abstractCmd.set(param);
						break;
					}
					break;
				case NullBypassClose:
					command.abstractCmd = new NullBypassClose();
					switch (command._commandType) {
					case Get:
						command.payload = command.abstractCmd.get();
						break;
					case Set:
						command.payload = command.abstractCmd.set(param);
						break;
					case Trap:
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
					case Get:
						command.payload = command.abstractCmd.get(param);
						break;
					case Set:
						command.payload = command.abstractCmd.set(param);
						break;
					}
				case APN:
					command.abstractCmd = new Apn();
					command.payload = command.abstractCmd.get();
					break;
	       	}
              //[ModemEventLog]
          	  //param.put("count", 2);

//            [Uploading Metering Data]
//            param.put("uploadTime", 10);
//            param.put("meterCount", 2);
//            param.put("port", new int[]{5000,5001});
//        	  [payload]

          	  /**
          	   * create request data
          	   */
          	  byte[] sendData = data.make(command,param);
        	  client.sendCommand(target,command,sendData);
        	  
        	  /**
          	   * response All data 
          	   */
//        	  System.out.println(((ModemEventLog)command.abstractCmd).toString());
//        	  [Ack]
//        	  System.out.println(((Ack)command.payload).toString());
//        	  [ByPass]
//        	  System.out.println(((Bypass)command.payload).toString());
//        	  [Status]
//        	  System.out.println(((ModemEventLog)command.abstractCmd).get());
//            [Uploading Metering Data]
//    		  System.out.println(((UploadMeteringData)command.abstractCmd).toString());
//        	  [ModemTime]
        	  System.out.println(((ModemTime)command.abstractCmd).toString());
          }
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
