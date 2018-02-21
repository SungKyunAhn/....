/*******************************************************************************
 * Copyright (c) 2014, Institute for Pervasive Computing, ETH Zurich.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the Institute nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE INSTITUTE AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE INSTITUTE OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * 
 * This file is part of the Californium (Cf) CoAP framework.
 ******************************************************************************/
/**
 * 
 */
package com.aimir.fep.protocol.coap.back;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.californium.core.coap.Response;

import com.aimir.fep.protocol.coap.client.SyncCoapCommand;
import com.aimir.fep.protocol.coap.frame.GeneralFrame;
import com.aimir.fep.protocol.fmp.common.Target;

/**
 * The PlugtestClient uses the developer API of Californium to test
 * if the test cases can be implemented successfully.
 * This client does not implement the tests that are meant to check
 * the server functionality (e.g., stop Observe on DELETE).
 * 
 * Use this flag to customize logging output:
 * -Djava.util.logging.config.file=../run/Californium-logging.properties
 */
public class CoapStartClientAllTest {
	private static Log log = LogFactory.getLog(CoapStartClientAllTest.class);
	//public GeneralFrame sendCommand(Target target, GeneralFrame command) throws Exception {	
	public static void main(String[] args) throws Exception {
		//defind parameter 
		Target target = new Target();
		target.setIpv6Addr("FD00:544A:16AF:F699:AABB:CCDD:EEFF:0101");
		target.setPort(5683);

		//all command defind
		String [] cmdTime  ={"/1/utc_time","/1/time_zone"};
		
		String [] cmdMeterInfo ={"/2/meter_serial","/2/meter_manufacture","/2/customer_number"
				,"/2/model_name","/2/hw_version","/2/sw_version","/2/meter_status"
				,"/2/last_update_time","/2/last_comm_time","/2/lp_channel_count","/2/lp_interval"
				,"/2/cummulative_active_power","/2/cummulative_active_power_time"};
		
		String [] cmdEMInfo ={"/3/ct","/3/pt","/3/transformer_ratio","/3/phase_configuration",
				"/3/frequency","/3/va_sf","/3/vah_sf","/3/disp_scalar","/3/disp_multiplier"};
		
		String [] cmdMTInfo ={"/4/primary_power_source_type","/4/secondary_power_source_type"
						     ,"/4/reset_count","/4/reset_reason","/4/operation_time","/4/network_management_system"};
		
		String [] cmdDataCC ={"/dcu_info/model_name","/dcu_info/hw_version","/dcu_info/sw_version","/dcu_info/id"};
		
		String [] cmdCommIF  ={"/6/main_type","/6/sub_type"};
		
		String [] cmdEthIF  ={"/7/main_ip_address","/7/main_port_number"};
		
		String [] cmdMobileIF  ={"/8/mobile_type","/8/mobile_id","/8/imei","/8/mobile_number"
								,"/8/mobile_mode","/8/apn","/8/id","/8/password"
								,"/8/misisdn","/8/ip_address","/8/access_technology_used","/8/rac_lac_cell_id"
								,"/8/operator","/8/connection_status"};
		
		String [] cmd6LowInfo ={"/9/ipv6_address","/9/hops_to_base_station","/9/frequency"
							   ,"/9/bandwidth","/9/listen_port"};
		
		String [] cmdLog ={"/10/log_type"}; 
		
		//Config used for CoapStartClient
		//config.setInt(MAX_MESSAGE_SIZE, 1024);
		//config.setInt(DEFAULT_BLOCK_SIZE, 512);
//		NetworkConfig.getStandard()
//				.setInt(NetworkConfigDefaults.MAX_MESSAGE_SIZE, 1024)//64 
//				.setInt(NetworkConfigDefaults.DEFAULT_BLOCK_SIZE, 512);//64
		
		//startService
		GeneralFrame frm = new GeneralFrame();
		//cmdTime
		for(int i=0 ;i <cmdTime.length;i++ ){
			frm.setUriAddr(cmdTime[i]);
			frm.setURI_Addr();
			SyncCoapCommand client = new SyncCoapCommand();
			//System.out.println("result:"+client.sendCommand(target, frm));
			client.sendCommand(target, frm);
			response(frm);
		}
		
		//cmdMeterInfo
		for(int i=0 ;i <cmdMeterInfo.length;i++ ){
			frm.setUriAddr(cmdMeterInfo[i]);
			frm.setURI_Addr();
			SyncCoapCommand client = new SyncCoapCommand();
			client.sendCommand(target, frm);
			////System.out.println("result:"+client.sendCommand(target, frm));
			response(frm);
		}
	
		//cmdEMInfo
		for(int i=0 ;i <cmdEMInfo.length;i++ ){
			frm.setUriAddr(cmdEMInfo[i]);
			frm.setURI_Addr();
			SyncCoapCommand client = new SyncCoapCommand();
			client.sendCommand(target, frm);
			response(frm);
		}
		
		//cmdMTInfo
		for(int i=0 ;i <cmdMTInfo.length;i++ ){
			frm.setUriAddr(cmdMTInfo[i]);
			frm.setURI_Addr();
			SyncCoapCommand client = new SyncCoapCommand();
			client.sendCommand(target, frm);
			response(frm);
		}
		
		/*
		//cmdDataCC
		for(int i=0 ;i <cmdDataCC.length;i++ ){
			frm.setUriAddr(cmdDataCC[i]);
			frm.setURI_Addr();
			SyncCoapCommand client = new SyncCoapCommand();
			client.sendCommand(target, frm);
			response(frm);
		}
		*/
		/*
		//cmdCommIF
		for(int i=0 ;i <cmdCommIF.length;i++ ){
			frm.setUriAddr(cmdCommIF[i]);
			frm.setURI_Addr();
			SyncCoapCommand client = new SyncCoapCommand();
			client.sendCommand(target, frm);
			response(frm);
		}
		*/
		/*
		//cmdEthIF
		for(int i=0 ;i <cmdEthIF.length;i++ ){
			frm.setUriAddr(cmdEthIF[i]);
			frm.setURI_Addr();
			SyncCoapCommand client = new SyncCoapCommand();
			client.sendCommand(target, frm);
			response(frm);
		}
		*/
		/*
		//cmdMobileIF
		for(int i=0 ;i <cmdMobileIF.length;i++ ){
			frm.setUriAddr(cmdMobileIF[i]);
			frm.setURI_Addr();
			SyncCoapCommand client = new SyncCoapCommand();
			client.sendCommand(target, frm);
			response(frm);
		}
		*/
		/*
		//cmd6LowInfo
		for(int i=0 ;i <cmd6LowInfo.length;i++ ){
			frm.setUriAddr(cmd6LowInfo[i]);
			frm.setURI_Addr();
			SyncCoapCommand client = new SyncCoapCommand();
			client.sendCommand(target, frm);
			response(frm);
		}
		*/
		/*
		//cmdLog
		for(int i=0 ;i <cmdLog.length;i++ ){
			frm.setUriAddr(cmdLog[i]);
			frm.setURI_Addr();
			SyncCoapCommand client = new SyncCoapCommand();
			client.sendCommand(target, frm);
			response(frm);
		}
		*/
	}
	
	/**
	 * response result
	 */
	public static void response(GeneralFrame frm){
		try{
			switch (frm._uriAddr.getCode()) {
				case "/2/customer_number":
				case "/2/hw_version":
				case "/2/model_name":
				case "/2/sw_version":
				case "/8/id":
				case "/8/imei":
				case "/8/apn":
				case "/8/mobile_id":
				case "/8/mobile_number":
				case "/8/password":
				case "/dcu_info/hw_version":
				case "/dcu_info/id":
				case "/dcu_info/model_name":
				case "/dcu_info/sw_version":
				case "/2/meter_manufacture":
				case "/2/meter_serial":
				      System.out.println("[RESPONSE]"+frm.getResult());
		      		  break;
				case "/6/main_type":
				case "/6/sub_type":
				case "/8/mobile_mode":
				case "/8/mobile_type":
				case "/4/primary_power_source_type":
				case "/4/secondary_power_source_type":
			          System.out.println("[RESPONSE]"+frm.getType());
			          break;
				case "/3/disp_multiplier":
				case "/3/disp_scalar":
				case "/3/frequency":
				case "/3/phase_configuration":
				case "/3/vah_sf":
				case "/3/va_sf":
			          System.out.println("[RESPONSE]"+frm.getValue());
			          break;
				case "/3/ct":
				case "/3/pt":
				case "/3/transformer_ratio":
				case "/2/cummulative_active_power":
			         System.out.println("[RESPONSE]"+frm.getValue());
			         break;
				case "/2/meter_status":
			         System.out.println("[RESPONSE]"+frm.getValue());
			         break;
			         
				case "/7/main_port_number":     
				case "/9/listen_port":
			         System.out.println("[RESPONSE]"+frm.getPort());
			         break;
				case "/2/cummulative_active_power_time":
				case "/2/last_comm_time":
				case "/2/last_update_time":
				case "/1/utc_time":
			         System.out.println("[RESPONSE]"+frm.getYyyymmddhhmmss());
					 break;
				case "/7/main_ip_address":
			         System.out.println("[RESPONSE]"+frm.getAddress());
					 break;
				case "/9/bandwidth":
			         System.out.println("[RESPONSE]"+frm.getBandwidth());
					 break;
				case "/9/frequency":
			         System.out.println("[RESPONSE][startFreq:"+frm.getStartFreq()+",endFreq:"+frm.getEndFreq()+"]");
					 break;
				case "/9/hops_to_base_station":
			         System.out.println("[RESPONSE]"+frm.getHops());
					 break;
				case "/9/ipv6_address":
			         System.out.println("[RESPONSE]"+frm.getPassword());
					 break;
				case "/2/lp_channel_count":
			         System.out.println("[RESPONSE]"+frm.getCount());
					 break;
				case "/2/lp_interval":
			         System.out.println("[RESPONSE]"+frm.getInterval());
					 break;
				case "/4/operation_time":
			         System.out.println("[RESPONSE]"+frm.getCount());
					 break;
				case "/4/network_management_system":
			         System.out.println("[RESPONSE][parentNodeId:"+frm.getParentNodeId()
			         +",rssi:"+frm.getRssi()
			         +",etx:"+frm.getEtx()
			         +",cpuUsage:"+frm.getCpuUsage()
			         +",memoryUsage:"+frm.getMemoryUsage()
			         +",totalTxSize:"+frm.getTotalTxSize()
			         +"]");
					 break;	
				case "/4/reset_count":
			         System.out.println("[RESPONSE]"+frm.getCount());
					 break;
				case "/4/reset_reason":
					 System.out.println("[RESPONSE]"+frm.getReason().name());
					 break;
				case "/1/time_zone":
			         System.out.println("[RESPONSE]"+frm.getOffSet());
					 break;
				case "/8/misisdn":
			         System.out.println("[RESPONSE]"+frm.getMisisdn());
					 break;
				case "/8/ip_address":
			         System.out.println("[RESPONSE]"+frm.getIpAddress());
					 break;
				case "/8/access_technology_used":
			         System.out.println("[RESPONSE]"+frm.get_accessThechnology().name());
					 break;
				case "/8/rac_lac_cell_id":
			        System.out.println("[RESPONSE][rac:"+frm.getRac()
			         +",lac:"+frm.getLac()
			         +",cid:"+frm.getCid()
			         +"]");
					break;
				case "/8/operator":
			        System.out.println("[RESPONSE]"+frm.getOperator());
					break;
				case "/8/connection_status":
			        System.out.println("[RESPONSE]"+frm.get_status().name());
					break;
				case "/.well-known/core":
			        System.out.println("[RESPONSE]"+frm.getWellKnownCore());
					break;
				default:
					 //get시 파라미터 존재 하므로....
					 if(frm._uriAddr.getCode().indexOf("/10/log_type") >= 0){
				          System.out.println("[RESPONSE][logCount:"+frm.getLogCount()
				 		         +",index:"+frm.getIndex()
				 		         +",time:"+frm.getTime()
				 		         +",logCode:"+frm.getLogCode()
				 		         +",logValue:"+frm.getLogValue()
				 		         +"]");
				    }
					break;
			 }
		}catch(Exception e){
			
		}
	}
}