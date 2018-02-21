package com.aimir.fep.meter.parser.plc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Acknowledge(FEP <--> IRM 공통)
 * @author kaze
 * 2009. 6. 23.
 */
public class ADataRequest extends PLCData
{
    private static Log log = LogFactory.getLog(ADataRequest.class);

    public ADataRequest(){

    }

    /**
     * @param sof
     * @param protocolDirection
     * @param protocolVersion
     * @param dId
     * @param sId
     * @param length
     */
    public ADataRequest(byte sof, byte protocolDirection, byte protocolVersion, String dId, String sId, int length) {
    	super(sof, protocolDirection, protocolVersion, dId, sId, length, PLCDataConstants.COMMAND_A);
	}

	/* (non-Javadoc)
	 * @see nuri.aimir.moa.protocol.fmp.frame.plc.PLCData#getServiceType()
	 */
	@Override
	public Integer getServiceType() {
		return new Integer(1);
	}

}
