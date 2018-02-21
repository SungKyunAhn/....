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
package com.aimir.fep.protocol.coap.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.network.EndpointManager;
import org.eclipse.californium.core.network.interceptors.MessageTracer;

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
public class AsyncCoapCommand {
	  private static Log log = LogFactory.getLog(AsyncCoapCommand.class);
	  public GeneralFrame sendCommand(Target target, final GeneralFrame command) throws Exception {	
		//Config used for CoapStartClient
//		NetworkConfig.getStandard()
//				.setInt(NetworkConfigDefaults.MAX_MESSAGE_SIZE, 1024)//64 
//				.setInt(NetworkConfigDefaults.DEFAULT_BLOCK_SIZE, 512);//64
		//set CoAP uri info
		String uri = "coap://["+target.getIpv6Addr()+"]:"+target.getPort();
		String uriAndCommand = uri+command.getUriAddr();
		System.out.println("uriAndCommand :"+uriAndCommand);
		
		//check uri ping
		CoapClient clientPing = new CoapClient(uri);
		if (!clientPing.ping(2000)) {
			System.out.println(uriAndCommand+" does not respond to ping, exiting...");
		} else {
			System.out.println(uriAndCommand+" reponds to ping");
			// add special interceptor for message traces
            EndpointManager.getEndpointManager().getDefaultEndpoint().addInterceptor(new MessageTracer());
		}
		
		//request
		CoapClient client = new CoapClient();
		
		//parameter setting
		if(uriAndCommand.indexOf("/10/log_type") >= 0){
			uriAndCommand=uriAndCommand+"?s="+command.getStartIndex()+"&e="+command.getEndIndex();
		}
		
		client.setURI(uriAndCommand);
		String decodeStr;
		//asynchronous
		Thread.sleep(100);
		client.get(new CoapHandler() {
			@Override 
			public void onLoad(CoapResponse response) {
				try{
					// System.out.println("AsyncCoapCommand-"+"Hex.decode:"+Hex.decode(response.getPayload()));
					System.out.println("AsyncCoapCommand-"+response.advanced().getType() + "-" + response.getCode());
					System.out.println("AsyncCoapCommand-"+response.getResponseText());
					command.decode(response);
				}catch (Exception e) {
					e.printStackTrace();
					System.out.println("Error[response.getPayload()]");
				}
			}
			@Override 
			public void onError() {
				System.err.println("Failed[client.get]");
			}
		});
		Thread.sleep(100);
        return command;
	}
}