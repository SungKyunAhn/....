package com.aimir.fep.protocol.coap.server.resources;

import static org.eclipse.californium.core.coap.CoAP.ResponseCode.*;
import org.eclipse.californium.core.server.resources.CoapExchange;
import com.aimir.fep.protocol.coap.server.ResourceBase;

public class ObserveReset extends ResourceBase {

	public ObserveReset() {
		super("obs-reset");
	}
	
	@Override
	public void handlePOST(CoapExchange exchange) {
		if (exchange.getRequestText().equals("sesame")) {
			System.out.println("obs-reset received POST. Clearing observers");
			
			// clear observers of the obs resources
			Observe obs = (Observe) this.getParent().getChild("obs");
			ObserveNon obsNon = (ObserveNon) this.getParent().getChild("obs-non");
			obs.clearObserveRelations();
			obsNon.clearObserveRelations();
			
			exchange.respond(CHANGED);
			
		} else {
			exchange.respond(FORBIDDEN);
		}
	}
	
}
