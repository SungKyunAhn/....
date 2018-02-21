package com.aimir.fep.command.ws.client;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdConnectByPass", propOrder = {
    "mcuId",
    "portNumber",
    "param"
})

public class CmdConnectByPass {

	@XmlElement(name = "McuId")
    protected String mcuId;
	@XmlElement(name = "PortNumber")
	protected int portNumber;
    @XmlElement(name = "Param")
    protected List<java.lang.String> param;

	public String getMcuId() {
		return mcuId;
	}

	public void setMcuId(String mcuId) {
		this.mcuId = mcuId;
	}

	public int getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

	public List<java.lang.String> getParam() {
		return param;
	}

	public void setParam(List<java.lang.String> param) {
		this.param = param;
	}
	
}
