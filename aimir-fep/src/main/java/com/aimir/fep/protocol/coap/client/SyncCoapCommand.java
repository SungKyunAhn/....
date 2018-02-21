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
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.core.network.config.NetworkConfig.Keys;
import org.eclipse.californium.scandium.DTLSConnector;

import com.aimir.fep.protocol.coap.frame.GeneralFrameForETHERNET;
import com.aimir.fep.protocol.coap.frame.GeneralFrameForMBB;
import com.aimir.constants.CommonConstants.McuType;
import com.aimir.fep.protocol.coap.frame.GeneralFrame;
import com.aimir.fep.protocol.fmp.common.Target;
import com.aimir.fep.protocol.security.DtlsConnector;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.Hex;
import com.aimir.util.IPUtil;

/**
 * The PlugtestClient uses the developer API of Californium to test if the test
 * cases can be implemented successfully. This client does not implement the
 * tests that are meant to check the server functionality (e.g., stop Observe on
 * DELETE).
 * 
 * Use this flag to customize logging output:
 * -Djava.util.logging.config.file=../run/Californium-logging.properties
 */
public class SyncCoapCommand extends org.eclipse.californium.core.CoapClient {
    private static Log log = LogFactory.getLog(SyncCoapCommand.class);
    private String coapProtocol = (FMPProperty.getProperty("protocol.ssl.coap.enable").equals("true")? "coaps":"coap") + "://";
    private int coapPort = Integer.parseInt(FMPProperty.getProperty("soria.coap.port", "coapPort"));
    private double fwVer = Double.parseDouble(FMPProperty.getProperty("pana.modem.fw.ver"));

    public GeneralFrame sendCommand(Target target, GeneralFrame command) throws Exception {
        NetworkConfig.getStandard().setInt(Keys.MAX_MESSAGE_SIZE, 1024); // 64
        String uri = null;
        if (target.getIpAddr() != null && !"".equals(target.getIpAddr()))
            uri = coapProtocol + target.getIpAddr() + ":" + target.getPort();
        else
            uri = coapProtocol + "[" + target.getIpv6Addr() + "]:" + target.getPort();
        String uriAndCommand = uri + command.getUriAddr();
        CoapClient clientPing = new CoapClient(uri);
        if (clientPing.ping(2000)) {
            throw new Exception(uri + " does not respond to ping, exiting...");
        } else {
            // System.out.println(uri+" reponds to ping");
            // add special interceptor for message traces
            // EndpointManager.getEndpointManager().getDefaultEndpoint().addInterceptor(new
            // MessageTracer());
        }
        // request
        long stime = System.currentTimeMillis();
        CoapClient client = new CoapClient();
        // parameter setting
        if (uriAndCommand.indexOf("/10/log_type") >= 0) {
            uriAndCommand = uriAndCommand + "?s=" + command.getStartIndex() + "&e=" + command.getEndIndex();
        }
        client.setURI(uriAndCommand);
        CoapResponse response = client.get();
        
        if (response.getPayload() == null) {
            throw new Exception("[CoAP][PayLoad Null]");
        }

        if (response.advanced().getCode().value >= 128) {// error 발생
            throw new Exception("[CoAP][Error]" + response.advanced().getCode().name());
        }

        long etime = System.currentTimeMillis();

        // System.out.println("[Coap][Response Time]" + (etime - stime )+"ms");
        // System.out.println("[Payload]"+Hex.decode(response.getPayload()));
        command.decode(response);
        return command;
    }

    private String makeCoapUrl(String ipV4, String ipV6, String url) throws Exception {
        String coapUrl = "";
        if(isIPv4(ipV4, ipV6)) {
            coapUrl = coapProtocol + ipV4 + ":" + coapPort + ((url == null || url.equals(""))?"":url);
        }
        // IPv6
        else {
            coapUrl = coapProtocol + "[" + ipV6 + "]:" + coapPort + ((url == null || url.equals(""))?"":url);
        }
        
        return coapUrl;
    }
    
    private boolean isIPv4(String ipV4, String ipV6) throws Exception {
        if(ipV4 != null && IPUtil.checkIPv4(ipV4)) {
            return true;
        }
        else if (ipV6 != null && IPUtil.checkIPv6(ipV6)) {
            return false;
        }
        else throw new Exception("Invalid IPv4[" + ipV4 + "] and IPv6[" + ipV6 + "]");
    }
    
    public boolean sendCoapPing(Target target, String device) throws Exception {
        NetworkConfig.getStandard().setInt(Keys.MAX_MESSAGE_SIZE, 1024); // .setInt(NetworkConfigDefaults.DEFAULT_BLOCK_SIZE, 512);
        NetworkConfig.getStandard().setInt(Keys.ACK_TIMEOUT, 60000);
        NetworkConfig.getStandard().setInt(Keys.MAX_RETRANSMIT, 0);
        String coapUrl = "";
        
        DTLSConnector dtlsConn = null;
        CoapClient client = null;
        
        try {
            if(device.equals("modem")){
                coapUrl = makeCoapUrl(target.getIpAddr(), target.getIpv6Addr(), "/3/u_t");
            }
            else if (device.equals("mcu")) {
                coapUrl = makeCoapUrl(target.getIpAddr(), target.getIpv6Addr(), "/time/time_zone");
            }
            
            client = new CoapClient(coapUrl);
            log.debug(coapUrl);
            if(target.getFwVer() == null || target.getFwVer().equals(""))
            	target.setFwVer("0"); // 버전이 없거나 공백인 경우 3pass를 타도록  (Ethernet, MBB 은 target에 버전 안넣음)
            if (Boolean.parseBoolean(FMPProperty.getProperty("protocol.ssl.coap.enable", "true"))) {
                if (Double.parseDouble(target.getFwVer()) >= fwVer)
                    dtlsConn = DtlsConnector.newDtlsClientConnector(isIPv4(target.getIpAddr(), target.getIpv6Addr()), target.getProtocol(), coapPort, true);
                else
                    dtlsConn = DtlsConnector.newDtlsClientConnector(isIPv4(target.getIpAddr(), target.getIpv6Addr()), target.getProtocol(), coapPort, false);
                
                client.setEndpoint(new CoapEndpoint(dtlsConn, NetworkConfig.getStandard()));
            }
            
            client.setTimeout(60000);
            CoapResponse response = client.get();
            //Thread.sleep(16000);
            log.debug(response.advanced().getCode().name());
            if(response.advanced().getCode().name().equals("CONTENT"))
                return true;
            else
                return false;   
        }
        catch (Exception e) {
            return false;
        }
        finally {
            if (dtlsConn != null) {
                dtlsConn.close(client.getEndpoint().getAddress());
                dtlsConn.destroy();
            }
        }

}

    //  RF (GetInfo/ModemReset)
    public GeneralFrame sendCoapCommand(Target target, GeneralFrame command) throws Exception {
        NetworkConfig.getStandard().setInt(Keys.MAX_MESSAGE_SIZE, 1024); // .setInt(NetworkConfigDefaults.DEFAULT_BLOCK_SIZE, 512);
        NetworkConfig.getStandard().setInt(Keys.ACK_TIMEOUT, 60000);
        NetworkConfig.getStandard().setInt(Keys.MAX_RETRANSMIT, 0);
        String uri = "";

        CoapClient client = null;
        DTLSConnector dtlsConn = null;
        try {
            uri = makeCoapUrl(target.getIpAddr(), target.getIpv6Addr(), "");
            //reset일 경우
            if(command.getUriAddr().equals("/1/reset")){
                String uriAndCommand = uri + command.getUriAddr()+"?set=1";
                log.debug(uriAndCommand);
                client = new CoapClient(uriAndCommand);
                if(target.getFwVer() == null || target.getFwVer().equals(""))
                	target.setFwVer("0"); // 버전이 없거나 공백인 경우 3pass를 타도록  (Ethernet, MBB 은 target에 버전 안넣음)
                if (Boolean.parseBoolean(FMPProperty.getProperty("protocol.ssl.coap.enable", "true"))) {
                    if (Double.parseDouble(target.getFwVer()) >= fwVer)
                        dtlsConn = DtlsConnector.newDtlsClientConnector(isIPv4(target.getIpAddr(), target.getIpv6Addr()), target.getProtocol(), coapPort, true);
                    else
                        dtlsConn = DtlsConnector.newDtlsClientConnector(isIPv4(target.getIpAddr(), target.getIpv6Addr()), target.getProtocol(), coapPort, false);
                    client.setEndpoint(new CoapEndpoint(dtlsConn, NetworkConfig.getStandard()));
                }
                Request request = new Request(CoAP.Code.PUT);
                CoapResponse response = client.advanced(request);
                client.setTimeout(60000);
                //Thread.sleep(16000);
                if(response.advanced().getCode().name().equals("CONTENT"))
                    return command;
                else
                    throw new Exception("[CoAP][Return Code Error]");
            }
            
            //get info일 경우
            String uriAndCommand = uri + command.getUriAddr() + "?get=1";
            log.debug(uriAndCommand);
            // request
            // parameter setting
            if (uriAndCommand.indexOf("/10/log_type") >= 0) {
                uriAndCommand = uriAndCommand + "?s=" + command.getStartIndex() + "&e=" + command.getEndIndex();
            }
        
            client = new CoapClient();
            if(target.getFwVer() == null || target.getFwVer().equals(""))
            	target.setFwVer("0"); // 버전이 없거나 공백인 경우 3pass를 타도록  (Ethernet, MBB 은 target에 버전 안넣음)
            if (Boolean.parseBoolean(FMPProperty.getProperty("protocol.ssl.coap.enable", "true"))) {
                if (dtlsConn == null) {
                    if (Double.parseDouble(target.getFwVer()) >= fwVer)
                        dtlsConn = DtlsConnector.newDtlsClientConnector(isIPv4(target.getIpAddr(), target.getIpv6Addr()), target.getProtocol(), coapPort, true);
                    else
                        dtlsConn = DtlsConnector.newDtlsClientConnector(isIPv4(target.getIpAddr(), target.getIpv6Addr()), target.getProtocol(), coapPort, false);
                }
                client.setEndpoint(new CoapEndpoint(dtlsConn, NetworkConfig.getStandard()));
            }
            client.setTimeout(60000);
            client.setURI(uriAndCommand);
            CoapResponse response = client.get();
            //Thread.sleep(16000);
            if (response.getPayload() == null) {
                throw new Exception("[CoAP][PayLoad Null]");
            }
    
            if (response.advanced().getCode().value >= 128) {// error 발생
                if(response.advanced().getCode().name().equals("BAD_REQUEST") && command.getUriAddr().equals("/4/l_comm_t")){
                    command.setYyyymmddhhmmss("00000000000000");
                    return command;
                }else
                    throw new Exception("[CoAP][Error]" + response.advanced().getCode().name());
            }
    
            command.decode(response);
            return command;
        }
        finally {
            if (dtlsConn != null) {
                dtlsConn.close(client.getEndpoint().getAddress());
                dtlsConn.destroy();
            }
        }
    }
    
    //  Ethernet (GetInfo/ModemReset)
    public GeneralFrameForETHERNET sendCoapCommand(Target target, GeneralFrameForETHERNET command) throws Exception {
        NetworkConfig.getStandard().setInt(Keys.MAX_MESSAGE_SIZE, 1024); //.setInt(NetworkConfigDefaults.DEFAULT_BLOCK_SIZE, 512);
        NetworkConfig.getStandard().setInt(Keys.ACK_TIMEOUT, 60000);
        NetworkConfig.getStandard().setInt(Keys.MAX_RETRANSMIT, 0);
        String uri = "";

        //reset일 경우
        DTLSConnector dtlsConn = null;
        CoapClient client = null;
        try {
            uri = makeCoapUrl(target.getIpAddr(), target.getIpv6Addr(), "");
            if(command.getUriAddr().equals("/1/reset")){
                String uriAndCommand = uri + command.getUriAddr()+"?set=1";
                log.debug(uriAndCommand);
                client = new CoapClient(uriAndCommand);
                //TODO check default value whether it is true or not
                if(target.getFwVer() == null || target.getFwVer().equals(""))
                	target.setFwVer("0"); // 버전이 없거나 공백인 경우 3pass를 타도록  (Ethernet, MBB 은 target에 버전 안넣음)
                if (Boolean.parseBoolean(FMPProperty.getProperty("protocol.ssl.coap.enable", "true"))) {
                    if (Double.parseDouble(target.getFwVer()) >= fwVer)
                        dtlsConn = DtlsConnector.newDtlsClientConnector(isIPv4(target.getIpAddr(), target.getIpv6Addr()), target.getProtocol(), coapPort, true);
                    else
                        dtlsConn = DtlsConnector.newDtlsClientConnector(isIPv4(target.getIpAddr(), target.getIpv6Addr()), target.getProtocol(), coapPort, false);
                    
                    client.setEndpoint(new CoapEndpoint(dtlsConn, NetworkConfig.getStandard()));
                }
                client.setTimeout(60000);
                Request request = new Request(CoAP.Code.PUT);
                CoapResponse response = client.advanced(request);
                // Thread.sleep(16000);
                if(response.advanced().getCode().name().equals("CONTENT"))
                    return command;
                else
                    throw new Exception("[CoAP][Return Code Error]");
            }
                    
            //get info일 경우
            String uriAndCommand = uri + command.getUriAddr()+ "?get=1";
            log.debug(uriAndCommand);
            // request
            client = new CoapClient();
            
            // parameter setting
            if (uriAndCommand.indexOf("/10/log_type") >= 0) {
                uriAndCommand = uriAndCommand + "?s=" + command.getStartIndex() + "&e=" + command.getEndIndex();
            }
            if(target.getFwVer() == null || target.getFwVer().equals(""))
            	target.setFwVer("0"); // 버전이 없거나 공백인 경우 3pass를 타도록  (Ethernet, MBB 은 target에 버전 안넣음)
            if (Boolean.parseBoolean(FMPProperty.getProperty("protocol.ssl.coap.enable", "true"))) {
                if (dtlsConn == null) {
                    if (Double.parseDouble(target.getFwVer()) >= fwVer)
                        dtlsConn = DtlsConnector.newDtlsClientConnector(isIPv4(target.getIpAddr(), target.getIpv6Addr()), target.getProtocol(), coapPort, true);
                    else
                        dtlsConn = DtlsConnector.newDtlsClientConnector(isIPv4(target.getIpAddr(), target.getIpv6Addr()), target.getProtocol(), coapPort, false);
                }
                client.setEndpoint(new CoapEndpoint(dtlsConn, NetworkConfig.getStandard()));
            }
            client.setURI(uriAndCommand);
            client.setTimeout(60000);
            CoapResponse response = client.get();
            // Thread.sleep(16000);
            if (response.getPayload() == null) {
                throw new Exception("[CoAP][PayLoad Null]");
            }
    
            if (response.advanced().getCode().value >= 128) {// error 발생
                if(response.advanced().getCode().name().equals("BAD_REQUEST") && command.getUriAddr().equals("/4/l_comm_t")){
                    command.setYyyymmddhhmmss("00000000000000");
                    return command;
                }else
                    throw new Exception("[CoAP][Error]" + response.advanced().getCode().name());
            }
    
            command.decode(response);
            return command;
        }
        finally {
            if (dtlsConn != null) {
                dtlsConn.close(client.getEndpoint().getAddress());
                dtlsConn.destroy();
            }
        }
    }
    
    //  MBB (GetInfo/ModemReset)
    public GeneralFrameForMBB sendCoapCommand(Target target, GeneralFrameForMBB command) throws Exception {
        NetworkConfig.getStandard().setInt(Keys.MAX_MESSAGE_SIZE, 1024); //.setInt(NetworkConfigDefaults.DEFAULT_BLOCK_SIZE, 512);
        NetworkConfig.getStandard().setInt(Keys.ACK_TIMEOUT, 60000);
        NetworkConfig.getStandard().setInt(Keys.MAX_RETRANSMIT, 0);
        String uri = "";
        
        DTLSConnector dtlsConn = null;
        CoapClient client = null;
        
        try {
            uri = makeCoapUrl(target.getIpAddr(), target.getIpv6Addr(), "");

            //reset일 경우
            if(command.getUriAddr().equals("/1/reset")){
                String uriAndCommand = uri + command.getUriAddr()+"?set=1";
                log.debug(uriAndCommand);
                client = new CoapClient(uriAndCommand);
                if(target.getFwVer() == null || target.getFwVer().equals(""))
                	target.setFwVer("0"); // 버전이 없거나 공백인 경우 3pass를 타도록  (Ethernet, MBB 은 target에 버전 안넣음)
                if (Boolean.parseBoolean(FMPProperty.getProperty("protocol.ssl.coap.enable", "true"))) {
                    if (Double.parseDouble(target.getFwVer()) >= fwVer)
                        dtlsConn = DtlsConnector.newDtlsClientConnector(isIPv4(target.getIpAddr(), target.getIpv6Addr()), target.getProtocol(), coapPort, true);
                    else
                        dtlsConn = DtlsConnector.newDtlsClientConnector(isIPv4(target.getIpAddr(), target.getIpv6Addr()), target.getProtocol(), coapPort, false);
                    client.setEndpoint(new CoapEndpoint(dtlsConn, NetworkConfig.getStandard()));
                }
                client.setTimeout(60000);
                Request request = new Request(CoAP.Code.PUT);
                CoapResponse response = client.advanced(request);
                // Thread.sleep(16000);
                if(response.advanced().getCode().name().equals("CONTENT"))
                    return command;
                else
                    throw new Exception("[CoAP][Return Code Error]");
            }
                    
            //get info일 경우
            String uriAndCommand = uri + command.getUriAddr()+ "?get=1";
            log.debug(uriAndCommand);
            client = new CoapClient();
            
            if (uriAndCommand.indexOf("/10/log_type") >= 0) {
                uriAndCommand = uriAndCommand + "?s=" + command.getStartIndex() + "&e=" + command.getEndIndex();
            }
            if(target.getFwVer() == null || target.getFwVer().equals(""))
            	target.setFwVer("0"); // 버전이 없거나 공백인 경우 3pass를 타도록  (Ethernet, MBB 은 target에 버전 안넣음)
            if (Boolean.parseBoolean(FMPProperty.getProperty("protocol.ssl.coap.enable", "true"))) {
                if (dtlsConn == null) {
                    if (Double.parseDouble(target.getFwVer()) >= fwVer)
                        dtlsConn = DtlsConnector.newDtlsClientConnector(isIPv4(target.getIpAddr(), target.getIpv6Addr()), target.getProtocol(), coapPort, true);
                    else
                        dtlsConn = DtlsConnector.newDtlsClientConnector(isIPv4(target.getIpAddr(), target.getIpv6Addr()), target.getProtocol(), coapPort, false);
                }
                client.setEndpoint(new CoapEndpoint(dtlsConn, NetworkConfig.getStandard()));
            }
            
            client.setTimeout(60000);
            client.setURI(uriAndCommand);
            CoapResponse response = client.get();
            // Thread.sleep(16000);
            if (response.getPayload() == null) {
                throw new Exception("[CoAP][PayLoad Null]");
            }
    
            if (response.advanced().getCode().value >= 128) {// error 발생
                if(response.advanced().getCode().name().equals("BAD_REQUEST") && command.getUriAddr().equals("/4/l_comm_t")){
                    command.setYyyymmddhhmmss("00000000000000");
                    return command;
                }else
                    throw new Exception("[CoAP][Error]" + response.advanced().getCode().name());
            }
    
            command.decode(response);
            return command;
        }
        finally {
            if (dtlsConn != null) {
                dtlsConn.close(client.getEndpoint().getAddress());
                dtlsConn.destroy();
            }
        }
    }
    
    //coapBrowser용 (modem or dcu)
    public GeneralFrame sendCoapCommand(Target target, GeneralFrame command, String config, String query) throws Exception {
        NetworkConfig.getStandard().setInt(Keys.MAX_MESSAGE_SIZE, 1024); // .setInt(NetworkConfigDefaults.DEFAULT_BLOCK_SIZE, 512);
        NetworkConfig.getStandard().setInt(Keys.ACK_TIMEOUT, 60000);
        NetworkConfig.getStandard().setInt(Keys.MAX_RETRANSMIT, 0);
        
        String uri = "";
        
        DTLSConnector dtlsConn = null;
        CoapClient client = null;
        
        try {
            uri = makeCoapUrl(target.getIpAddr(), target.getIpv6Addr(), "");
            
            // set일경우
            if(config.equals("PUT")){
                String uriAndCommand;
                if(!query.equals("")){
                		uriAndCommand = uri + command.getUriAddr() + query;
                }else
                    uriAndCommand = uri + command.getUriAddr();
                client = new CoapClient(uriAndCommand);
                log.debug(uriAndCommand);
                if(target.getFwVer() == null || target.getFwVer().equals(""))
                	target.setFwVer("0"); // 버전이 없거나 공백인 경우 3pass를 타도록  (Ethernet, MBB 은 target에 버전 안넣음)
                if (Boolean.parseBoolean(FMPProperty.getProperty("protocol.ssl.coap.enable", "true"))) {
                    if (Double.parseDouble(target.getFwVer()) >= fwVer)
                        dtlsConn = DtlsConnector.newDtlsClientConnector(isIPv4(target.getIpAddr(), target.getIpv6Addr()), target.getProtocol(), coapPort, true);
                    else
                        dtlsConn = DtlsConnector.newDtlsClientConnector(isIPv4(target.getIpAddr(), target.getIpv6Addr()), target.getProtocol(), coapPort, false);
                    client.setEndpoint(new CoapEndpoint(dtlsConn, NetworkConfig.getStandard()));
                }
                client.setTimeout(60000);
                Request request = new Request(CoAP.Code.PUT);
                CoapResponse response = client.advanced(request);
                //Thread.sleep(16000);
                if(response.advanced().getCode().name().equals("CONTENT"))
                    return command;
                else
                    throw new Exception("[CoAP][Return Code Error]");
            }
            
            // get일 경우
            String uriAndCommand;
            if(!query.equals(""))
                uriAndCommand = uri + command.getUriAddr() + query;
            else
                uriAndCommand = uri + command.getUriAddr();
            // request
            client = new CoapClient();
            log.debug(uriAndCommand);
            
            if (uriAndCommand.indexOf("/10/log_type") >= 0) {
                uriAndCommand = uriAndCommand + "?s=" + command.getStartIndex() + "&e=" + command.getEndIndex();
            }
            if(target.getFwVer() == null || target.getFwVer().equals(""))
            	target.setFwVer("0"); // 버전이 없거나 공백인 경우 3pass를 타도록  (Ethernet, MBB 은 target에 버전 안넣음)
            if (Boolean.parseBoolean(FMPProperty.getProperty("protocol.ssl.coap.enable", "true"))) {
                if (dtlsConn == null) {
                    if (Double.parseDouble(target.getFwVer()) >= fwVer)
                        dtlsConn = DtlsConnector.newDtlsClientConnector(isIPv4(target.getIpAddr(), target.getIpv6Addr()), target.getProtocol(), coapPort, true);
                    else
                        dtlsConn = DtlsConnector.newDtlsClientConnector(isIPv4(target.getIpAddr(), target.getIpv6Addr()), target.getProtocol(), coapPort, false);
                }
                client.setEndpoint(new CoapEndpoint(dtlsConn, NetworkConfig.getStandard()));
            }
            client.setURI(uriAndCommand);
            client.setTimeout(60000);
            CoapResponse response = client.get();
            // Thread.sleep(16000);
            if (response.getPayload() == null) {
                throw new Exception("[CoAP][PayLoad Null]");
            }
    
            if (response.advanced().getCode().value >= 128) {// error 발생
                    throw new Exception("[CoAP][Error]" + response.advanced().getCode().name());
            }
    
            byte[] data = response.getPayload();
            String resultData = Hex.decode(data);
            command.setCoapResult(resultData);
            return command;
        }
        finally {
            if (dtlsConn != null) {
                dtlsConn.close(client.getEndpoint().getAddress());
                dtlsConn.destroy();
            }
        }
    }
}