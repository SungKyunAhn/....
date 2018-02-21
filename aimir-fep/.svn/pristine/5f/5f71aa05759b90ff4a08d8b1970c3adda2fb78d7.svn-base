
package com.aimir.fep.moclasses;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ManagedSystem implements ManagedSystemMBean {
    private static Log log = LogFactory.getLog(ManagedSystem.class);
	Integer state;

	public ManagedSystem() { 
		this.state = new Integer(1);
	}

	public ManagedSystem(Integer state) { 
		this.state = state;	
	}

	public Integer getState() {
		log.info("System state = " + state.intValue());
		return state;
	}

	public void setState(Integer state) {
		log.info("state changed : " + this.state.intValue() + " => " + state.intValue());
		this.state = state;
	}

	public void printState() {
		log.info("Current state: " + state.intValue());
	}
}

