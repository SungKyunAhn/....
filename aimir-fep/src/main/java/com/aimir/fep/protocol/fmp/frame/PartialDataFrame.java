/**
 * 
 */
package com.aimir.fep.protocol.fmp.frame;

import java.util.Arrays;

import com.aimir.fep.protocol.fmp.datatype.INT;

/**
 * @author simhanger
 *
 */
public class PartialDataFrame extends GeneralDataFrame {
	public byte[] svcBody = null;

	public PartialDataFrame() {
		super(GeneralDataConstants.SOH, (byte) 0, new INT(0), (byte) 0x00, (byte) 0);
	}

	public byte[] getSvcBody() {
		return svcBody;
	}

	public void setSvcBody(byte[] svcBody) {
		this.svcBody = svcBody;
	}

	@Override
	public String toString() {
		return "PartialDataFrame [svcBody=" + Arrays.toString(svcBody) + "]";
	}

}
