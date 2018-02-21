package com.aimir.fep.command.ws.client;

import java.util.List;
import java.util.Map;

import javax.jws.WebParam;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "sendSMS", propOrder = {
	"commandName",
    "messageType",
    "mobliePhNum",
    "euiId",
    "commandCode",
    "paramList",
    "cmdMap"
})

public class SendSMS {
	@XmlElement(name = "CommandName")
	String commandName;
	
    @XmlElement(name = "MessageType")
    protected String messageType;

    @XmlElement(name = "MobliePhNum")
    protected String mobliePhNum;
	
    @XmlElement(name = "EuiId")
	protected String euiId;
    
    @XmlElement(name = "CommandCode")
   	protected String commandCode;
    
    @XmlElement(name = "ParamList")
   	protected List<String> paramList;
    
    @XmlElement(name = "CmdMap")
   	protected String cmdMap;
    
    
	public String getCommandName() {
		return commandName;
	}

	public void setCommandName(String commandName) {
		this.commandName = commandName;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getMobliePhNum() {
		return mobliePhNum;
	}

	public void setMobliePhNum(String mobliePhNum) {
		this.mobliePhNum = mobliePhNum;
	}

	public String getEuiId() {
		return euiId;
	}

	public void setEuiId(String euiId) {
		this.euiId = euiId;
	}

	public String getCommandCode() {
		return commandCode;
	}

	public void setCommandCode(String commandCode) {
		this.commandCode = commandCode;
	}

	public List<String> getParamList() {
		return paramList;
	}

	public void setParamList(List<String> paramList) {
		this.paramList = paramList;
	}
	
	public String getCmdMap() {
		return cmdMap;
	}

	public void setCmdMap(String cmdMap) {
		this.cmdMap = cmdMap;
	}
	
}
