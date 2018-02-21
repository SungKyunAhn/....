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
package com.aimir.fep.protocol.coap.server.rsc;

import static org.eclipse.californium.core.coap.CoAP.ResponseCode.*;
import static org.eclipse.californium.core.coap.MediaTypeRegistry.*;

import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.server.resources.CoapExchange;

import com.aimir.fep.protocol.coap.server.ResourceBase;


/**
 * This resource implements a test of specification for the ETSI IoT CoAP Plugtests, Las Vegas, NV, USA, 19 - 22 Nov 2013.
 * 
 * @author Matthias Kovatsch
 */
public class TimeTimeZone extends ResourceBase {
	public static final byte[] utc_time= new byte[] {(byte)0x62, (byte)0x45, (byte)0xBD, (byte)0x05,
		(byte)0xE0, (byte)0x07, (byte)0x04, (byte)0x11, (byte)0x14, (byte)0x00, (byte)0x11};
	public TimeTimeZone() {
		super("time_zone");
		getAttributes().setTitle("time_zone");
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		// Check: Type, Code
		System.out.println("##########TimeTimeZone#########");
		// complete the request
		exchange.setMaxAge(30);
		exchange.respond(CONTENT, utc_time, APPLICATION_OCTET_STREAM);
	}

	@Override
	public void handlePOST(CoapExchange exchange) {

		// Check: Type, Code, has Content-Type
		exchange.setLocationPath("/location1/location2/location3");
		exchange.respond(CREATED);
	}

	@Override
	public void handlePUT(CoapExchange exchange) {

		// Check: Type, Code, has Content-Type
		
		if (exchange.getRequestOptions().hasIfNoneMatch()) {
			exchange.respond(PRECONDITION_FAILED);
		} else {
			exchange.respond(CHANGED);
		}
	}

	@Override
	public void handleDELETE(CoapExchange exchange) {
		// complete the request
		exchange.respond(DELETED);
	}
}
