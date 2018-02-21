package com.aimir.fep.meter.parser.plc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * IRM Status Request
 * @author kaze
 * 2009. 6. 23.
 */
public class BDataRequest extends PLCData
{
    private static Log log = LogFactory.getLog(BDataRequest.class);

    public BDataRequest(byte sof, byte protocolDirection, byte protocolVersion, String dId, String sId, int length) {
    	super(sof, protocolDirection, protocolVersion, dId, sId, length, PLCDataConstants.COMMAND_B);
	}

	/* (non-Javadoc)
	 * @see nuri.aimir.moa.protocol.fmp.frame.plc.PLCData#getServiceType()
	 */
	@Override
	public Integer getServiceType() {
		return new Integer(1);
	}
}
