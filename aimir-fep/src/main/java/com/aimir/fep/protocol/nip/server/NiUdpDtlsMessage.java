package com.aimir.fep.protocol.nip.server;

import org.eclipse.californium.elements.Connector;
import org.eclipse.californium.elements.RawData;

public class NiUdpDtlsMessage {
	public RawData rawdata;
	public Connector connector;
	
	public NiUdpDtlsMessage(Connector con,RawData raw){
		rawdata = raw;
		connector = con;
	}
	
	byte[] getBytes(){
		return rawdata.getBytes();
	}
	
	void write(byte[] msg){
		connector.send(new RawData(msg, rawdata.getAddress(), rawdata.getPort())); 
	}
}
