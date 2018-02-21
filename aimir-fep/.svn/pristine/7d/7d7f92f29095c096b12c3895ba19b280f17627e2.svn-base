package com.aimir.fep.meter.parser.plc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Data Request(변압기 감시 데이터 수집)
 * @author kaze
 * 2009. 6. 23.
 */
public class MDataRequest extends PLCData
{
    private static Log log = LogFactory.getLog(MDataRequest.class);

    public MDataRequest(byte sof, byte protocolDirection, byte protocolVersion, String dId, String sId, int length) {
    	super(sof, protocolDirection, protocolVersion, dId, sId, length, PLCDataConstants.COMMAND_M);
	}

    /* (non-Javadoc)
	 * @see nuri.aimir.moa.protocol.fmp.frame.plc.PLCData#getServiceType()
	 */
	@Override
	public Integer getServiceType() {
		return new Integer(1);
	}
}
