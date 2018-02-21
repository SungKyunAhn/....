package com.aimir.fep.meter.parser.plc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * 변압기 감시 Configuration Data Request
 * @author kaze
 * 2009. 6. 23.
 */
public class JDataRequest extends PLCData
{
    private static Log log = LogFactory.getLog(JDataRequest.class);

    public JDataRequest(byte sof, byte protocolDirection, byte protocolVersion, String dId, String sId, int length) {
    	super(sof, protocolDirection, protocolVersion, dId, sId, length, PLCDataConstants.COMMAND_J);
	}

    /* (non-Javadoc)
	 * @see nuri.aimir.moa.protocol.fmp.frame.plc.PLCData#getServiceType()
	 */
	@Override
	public Integer getServiceType() {
		return new Integer(1);
	}
}
