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
package com.aimir.fep.protocol.coap.server;

import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.logging.Level;

import org.eclipse.californium.core.CaliforniumLogger;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.Endpoint;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.core.network.config.NetworkConfig.Keys;
import org.eclipse.californium.core.network.interceptors.MessageTracer;

import com.aimir.fep.protocol.coap.server.rsc.TimeTimeZone;
import com.aimir.fep.protocol.coap.server.rsc.TimeUtcTime;



// ETSI Plugtest environment
//import java.net.InetSocketAddress;
//import org.eclipse.californium.core.network.CoAPEndpoint;


/**
 * The class PlugtestServer implements the test specification for the
 * ETSI IoT CoAP Plugtests, Las Vegas, NV, USA, 19 - 22 Nov 2013.
 * 
 * @author Matthias Kovatsch
 */
public class StartServer extends Server {
	static {
		CaliforniumLogger.initialize();
		CaliforniumLogger.setLevel(Level.FINER);
	}
    // exit codes for runtime errors
    public static final int ERR_INIT_FAILED = 1;
    
    // allows port configuration in Californium.properties
    private static final int port = NetworkConfig.getStandard().getInt(Keys.COAP_PORT);
    
    public static void main(String[] args) {
    	Server server = null;
        try {
        	server = new StartServer();
//            server.addEndpoint(new CoAPEndpoint(new InetSocketAddress("::1", port)));
            server.addEndpoint(new CoapEndpoint(new InetSocketAddress(" 2002:bb01:1ed9::bb01:1ed9", port)));
//            server.addEndpoint(new CoAPEndpoint(new InetSocketAddress("2a01:c911:0:2010::10", port)));
//            server.addEndpoint(new CoAPEndpoint(new InetSocketAddress("10.200.1.2", port)));
            server.start();
            
            // add special interceptor for message traces
            for (Endpoint ep:server.getEndpoints()) {
            	ep.addInterceptor(new MessageTracer());
            }
            
            System.out.println(StartServer.class.getSimpleName()+" listening on port " + port);
            
        } catch (Exception e) {
        	server.stop(); 
            System.err.printf("Failed to create "+StartServer.class.getSimpleName()+": %s\n", e.getMessage());
            System.err.println("Exiting");
            System.exit(ERR_INIT_FAILED);
        }
        
    }
    
    public StartServer() throws SocketException {
    	
    	NetworkConfig.getStandard() // used for plugtest
    			.setInt(Keys.MAX_MESSAGE_SIZE, 64)
    			// .setInt(Keys.DEFAULT_BLOCK_SIZE, 64)
    			.setInt(Keys.NOTIFICATION_CHECK_INTERVAL_COUNT, 4)
    			.setInt(Keys.NOTIFICATION_CHECK_INTERVAL_TIME, 30000);
        
        // add resources to the server
//    	add(new CommIFMainType());
//    	add(new CommIFSubType());
//    	add(new MeterInfoCummulativeActivePower());
//    	add(new MeterInfoCummulativeActivePowerTime());
//    	add(new DataCCHwVersion());
//    	add(new DataCCId());
//    	add(new DataCCModelName());
//    	add(new DataCCSwVersion());
//    	add(new EMInfoCt());
//    	add(new EMInfoDispMultiplier());
//    	add(new EMInfoDispScalar());
//    	add(new EMInfoFrequency());
//    	add(new EMInfoPhaseConfiguration());
//    	add(new EMInfoPt());
//    	add(new EMInfoTransFormerRatio());
//    	add(new EMInfoVahSf());
//    	add(new EMInfoVaSf());
//    	add(new EthIFMainIpAddress());
//    	add(new EthIFMainPortNumber());
//    	add(new LogLogType());
//    	add(new LowInfoBandWidth());
//    	add(new LowInfoFrequency());
//    	add(new LowInfoHopeToBaseStation());
//    	add(new LowInfoIpv6Address());
//    	add(new LowInfoListenPort());
//    	add(new MeterInfoCustomerNumber());
//    	add(new MeterInfoHwVersion());
//    	add(new MeterInfoLastCommTime());
//    	add(new MeterInfoLastUpdateTime());
//    	add(new MeterInfoLpChannelCount());
//    	add(new MeterInfoLpInterval());
//    	add(new MeterInfoMeterMenufacture());
//    	add(new MeterInfoMeterSerial());
//    	add(new MeterInfoMeterStatus());
//    	add(new MeterInfoModelName());
//    	add(new MeterInfoSwVersion());
//    	add(new MobileIFID());
//    	add(new MobileIFImei());
//    	add(new MobileIFMobileApn());
//    	add(new MobileIFMobileId());
//    	add(new MobileIFMobileMode());
//    	add(new MobileIFMobileNumber());
//    	add(new MobileIFMobileType());
//    	add(new MobileIFPassword());
//    	add(new MTInfoOperationTime());
//    	add(new MTInfoPrimaryPowerSourceType());
//    	add(new MTInfoResetCount());
//    	add(new MTInfoResetReason());
//    	add(new MTInfoSecondaryPowerSourceType());
    	add(new TimeTimeZone());
    	add(new TimeUtcTime());

    }
    
    
    // Application entry point /////////////////////////////////////////////////
    
}
