package com.aimir.fep.protocol.coap.server.resources;

import static org.eclipse.californium.core.coap.CoAP.ResponseCode.*;
import org.eclipse.californium.core.server.resources.CoapExchange;
import com.aimir.fep.protocol.coap.server.ResourceBase;

public class Shutdown extends ResourceBase {

	public Shutdown() {
		super("shutdown");
	}
	
	@Override
	public void handlePOST(CoapExchange exchange) {
		if (exchange.getRequestText().equals("sesame")) {
			exchange.respond(CHANGED);
			
			System.out.println("Shutdown resource received POST. Exiting");
			try {
				Thread.sleep(500);
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.exit(0);
			
		} else {
			exchange.respond(FORBIDDEN);
		}
	}
	
}
