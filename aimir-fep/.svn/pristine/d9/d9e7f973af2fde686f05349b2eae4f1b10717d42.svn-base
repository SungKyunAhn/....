/*
 * Created on 2004. 12. 27.
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.aimir.fep.meter.parser.zmdTable;

/**
 * @author Park YeonKyoung yeonkyoung@hanmail.net
 *
 * LandisGyr+ ZMD4 meter Class. <p>
 * Include MDA SCALE. <p>
 */
public class Class9 {
	
	public final static int OFS_MDASCALE = 17;
	public final static int LEN_MDASCALE = 4;
	
	private byte[] data;
	
	/**
	 * Constructor. <p>
	 * @param data
	 */
	public Class9(byte[] data){
		this.data = data;	
	}

	/**
	 * Get MDA Scale.<p>
	 * @return
	 */
	public byte parseMDAScale(){
		return data[OFS_MDASCALE];
	}

}
