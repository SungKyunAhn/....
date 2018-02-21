package com.aimir.fep.bems.sender;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;

import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.protocol.fmp.exception.FMPEncodeException;
import com.aimir.fep.protocol.fmp.frame.service.EventData;

/**
 * @author kskim
 */
public class EventDataSender extends DataSender {
	protected static Log log = LogFactory.getLog("eventDataSender");
	
	@Override
	public boolean send(Object obj){
		if(obj instanceof EventData && this.connect()){
			EventData ed = (EventData)obj;
			byte[] bdata = null;
			try {
				bdata = ed.encode();
				IoBuffer buf = IoBuffer.allocate(bdata.length);
		        buf.put(bdata);
		        buf.flip();
				return this.send(buf);
			} catch (FMPEncodeException e) {
				log.error(e);
				return false;
			}
		}else{
			log.error(new IllegalArgumentException());
		}
		return false;
	}
}
