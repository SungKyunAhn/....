package com.aimir.fep.meter.parser.plc;

import com.aimir.fep.util.DataUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Data Request(검침 데이터 수집)
 * @author kaze
 * 2009. 6. 23.
 */
public class DDataRequest extends PLCData
{
    private static Log log = LogFactory.getLog(DDataRequest.class);
    public int dType;
    public String meterId;

    /**
     * @param sof
     * @param protocolDirection
     * @param protocolVersion
     * @param dId
     * @param sId
     * @param length
     * @param dType
     * @param meterId : "" means all meter
     */
    public DDataRequest(byte sof, byte protocolDirection, byte protocolVersion, String dId, String sId, int length, int dType, String meterId) {
    	super(sof, protocolDirection, protocolVersion, dId, sId, length, PLCDataConstants.COMMAND_D, DataUtil.append(new byte[]{DataUtil.getByteToInt(dType)}, DataUtil.getFixedLengthByte(meterId, PLCDataConstants.METER_ID_LENGTH)));
	}

    /* (non-Javadoc)
	 * @see nuri.aimir.moa.protocol.fmp.frame.plc.PLCData#getServiceType()
	 */
	@Override
	public Integer getServiceType() {
		return new Integer(1);
	}
}
