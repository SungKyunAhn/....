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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;

/**
 * The PlugtestClient uses the developer API of Californium to test
 * if the test cases can be implemented successfully.
 * This client does not implement the tests that are meant to check
 * the server functionality (e.g., stop Observe on DELETE).
 * 
 * Use this flag to customize logging output:
 * -Djava.util.logging.config.file=../run/Californium-logging.properties
 */
public class CoapCommand{
    private static Log log = LogFactory.getLog(CoapCommand.class);

    /**
     * request
     * @param methodType
     * @param strUri
     * @param headers
     * @param body
     * @return
     * @throws IotException
     */
    public CoAP.ResponseCode request(CoAP.Code methodType, String strUri, 
            Map<Integer, Object> headers, String body) throws Exception {
        URI uri = null; // URI parameter of the request
	  		
        try {
            uri = new URI(strUri);
        } catch (URISyntaxException e) {
            throw new Exception("600"+"[" + strUri + "] URI error");
        }
	  	
        Response response = push(methodType, uri, headers, body);
	  		
        return response.getCode();
    }
	  	
    /**
     * push
     * @param methodType
     * @param uri
     * @param headers
     * @param body
     * @return
     * @throws IotException
     */
    private Response push(CoAP.Code methodType, URI uri, 
            Map<Integer, Object> headers, String body) throws Exception {
        Response response = null;
	  		
        Request request = null;
        if (CoAP.Code.GET.equals(methodType)) {
            request = Request.newGet();
        }
        else if (	CoAP.Code.POST.equals(methodType)) {
            request = Request.newPost();
        }
        else if (	CoAP.Code.PUT.equals(methodType)) {
            request = Request.newPut();
        }
        else if (	CoAP.Code.DELETE.equals(methodType)) {
            request = Request.newDelete();
        }
        else {
            request = Request.newGet();
        }
	  		
        request.setURI(uri);
        if (headers != null) {
            for( Map.Entry<Integer, Object> header : headers.entrySet() ){
                log.debug("header.getKey : {}, header.getValue : {}"+ header.getKey()+ header.getValue());
	  	            
                switch (header.getKey()) {
                    case 12:
                    case 267:
                        request.getOptions().addOption(new Option(header.getKey(), (Integer)header.getValue()));
                        break;
                    default:
                        request.getOptions().addOption(new Option(header.getKey(), (String)header.getValue()));
                        break;
                }
            }
        }
	  		
        if (body != null) {
            request.setPayload(body);
        }
	  		
        request.send();
	  		
        // receive response
        try {
            response = request.waitForResponse(3000);
	  			
            if (response != null) {
                // response received, output a pretty-print
                log.debug("response.getPayloadString : {}"+ response.getPayloadString());
            } else {
                log.debug("No response received.");
            }
	  			
        } catch (InterruptedException e) {
            throw new Exception("600"+"Receiving of response interrupted: " + e.getMessage());
        }
	  		
        return response;
    }
	
}