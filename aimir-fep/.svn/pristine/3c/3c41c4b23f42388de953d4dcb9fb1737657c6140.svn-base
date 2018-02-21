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
 * Include CT RATIO.       <p>
 */
public class Class4 {
	
	public final static int OFS_CT_RATIO = 15;
	public final static int LEN_CT_RATIO = 2;
	
	private byte[] data;
	
	/**
	 * Constructor. <p>
	 * @param data
	 */
	public Class4(byte[] data){
		this.data = data;	
	}

	/**
	 * Get ENERGY Factor Field.<p>
	 * @return
	 */
	public byte[] parseCTRatio() throws Exception {
		return DataFormat.select(data,OFS_CT_RATIO,LEN_CT_RATIO);
	}
}
