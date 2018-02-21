package com.aimir.fep.protocol.nip.client;

import java.util.HashMap;

import com.aimir.fep.protocol.fmp.common.Target;
import com.aimir.fep.protocol.nip.command.ModemTime;
import com.aimir.fep.protocol.nip.common.GeneralDataFrame;
import com.aimir.fep.protocol.nip.frame.GeneralFrame;
import com.aimir.fep.protocol.nip.frame.NetworkStatusSub1GhzForSORIA;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.NetworkType;
import com.aimir.fep.util.Hex;

public class NiUdpStartClient{
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
    	  command.setClientBaseInfo(
    			  command.niAttributeId.MeterInformation
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
          //[ModemEventLog]
      	  //param.put("count", 2);

//        [Uploading Metering Data]
//        param.put("uploadTime", 10);
//        param.put("meterCount", 2);
//        param.put("port", new int[]{5000,5001});
//    	  [payload]

      	  /**
      	   * create request data
      	   */
      	  byte[] sendData = data.make(command,param);
    	  client.sendCommand(target,command,sendData);
    	  
    	  /**
      	   * response All data 
      	   */
//    	  System.out.println(((ModemEventLog)command.abstractCmd).toString());
//    	  [Ack]
//    	  System.out.println(((Ack)command.payload).toString());
//    	  [ByPass]
//    	  System.out.println(((Bypass)command.payload).toString());
//    	  [Status]
//    	  System.out.println(((ModemEventLog)command.abstractCmd).get());
//        [Uploading Metering Data]
//		  System.out.println(((UploadMeteringData)command.abstractCmd).toString());
//    	  [ModemTime]
    	  System.out.println(((ModemTime)command.abstractCmd).toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
