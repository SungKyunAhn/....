package com.aimir.fep.protocol.nip.frame.payload;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;

public abstract class AbstractCommand {
	protected Log log = LogFactory.getLog(AbstractCommand.class);
	
	private byte[] id;
	
	public AbstractCommand(byte[] id) {
	    this.id = id;
	}
	
	public byte[] getAttributeID() {
	    return this.id;
	}
	
	public abstract Command get() throws Exception;
	public abstract Command get(HashMap p) throws Exception;
	public abstract Command set() throws Exception;
	public abstract Command set(HashMap p) throws Exception;
	public abstract Command trap() throws Exception;
	public abstract void decode(byte[] p) throws Exception;
	public abstract void decode(byte[] p1, CommandType commandType) throws Exception;
}
