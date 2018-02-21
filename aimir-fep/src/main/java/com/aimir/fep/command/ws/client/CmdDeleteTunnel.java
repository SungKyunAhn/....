package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdDeleteTunnel", propOrder = {
    "mcuId",
    "meterId"
})

public class CmdDeleteTunnel {

	@XmlElement(name = "McuId")
    protected String mcuId;	
	@XmlElement(name = "MeterId")
    protected String meterId;
	
	public String getMcuId() {
		return mcuId;
	}
	
	public void setMcuId(String mcuId) {
		this.mcuId = mcuId;
	}
	
	public String getMeterId() {
		return meterId;
	}
	
	public void setMeterId(String meterId) {
		this.meterId = meterId;
	}
	
}
