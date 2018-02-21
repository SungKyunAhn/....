/*
 * Created on 2004. 12. 27.
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.aimir.fep.meter.parser.zmdTable;

import com.aimir.fep.util.DataFormat;

/**
 * @author Park YeonKyoung yeonkyoung@hanmail.net
 *
 * LandisGyr+ ZMD4 meter Class. <p>
 * Include VT RATIO.            <p>
 */
public class Class5 {
	
	public final static int OFS_VT_RATIO = 15;
	public final static int LEN_VT_RATIO = 2;
	
	private byte[] data;
	
	/**
	 * Constructor. <p>
	 * @param data
	 */
	public Class5(byte[] data){
		this.data = data;	
	}

	/**
	 * Get VT Ratio Field.<p>
	 * @return
	 */
	public byte[] parseVTRatio() throws Exception {
		return DataFormat.select(data,OFS_VT_RATIO,LEN_VT_RATIO);
	}
}
