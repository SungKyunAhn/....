package com.aimir.fep.meter.parser.plc;

import com.aimir.fep.util.DataUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Data Request(통신 상황 데이터 수집)
 * @author kaze
 * 2009. 6. 23.
 */
public class HDataRequest extends PLCData
{
    private static Log log = LogFactory.getLog(HDataRequest.class);
    public String meterId;

    /**
     * @param sof
     * @param protocolDirection
     * @param protocolVersion
     * @param dId
     * @param sId
     * @param length
     * @param meterId : "" mean all meter
     */
    public HDataRequest(byte sof, byte protocolDirection, byte protocolVersion, String dId, String sId, int length, String meterId) {
    	super(sof, protocolDirection, protocolVersion, dId, sId, length, PLCDataConstants.COMMAND_H, DataUtil.getFixedLengthByte(meterId, PLCDataConstants.METER_ID_LENGTH));
	}

    /* (non-Javadoc)
	 * @see nuri.aimir.moa.protocol.fmp.frame.plc.PLCData#getServiceType()
	 */
	@Override
	public Integer getServiceType() {
		return new Integer(1);
	}
}
