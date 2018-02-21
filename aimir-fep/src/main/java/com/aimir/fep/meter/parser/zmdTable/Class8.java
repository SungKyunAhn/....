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
 * Include Active/Reactive Energy Scale. <p>
 */
public class Class8 {
	
	public final static int OFS_ARE_SCALE = 17;
	public final static int LEN_DATE_TIME = 8;
	
	private byte[] data;
	
	/**
	 * Constructor. <p>
	 * @param data
	 */
	public Class8(byte[] data){
		this.data = data;	
	}

	/**
	 * Get Active Energy Scale.<p>
	 * All same 
	 * (Active Energy+ Total, Reactive Energy+ Total, 
	 *  Active Energy+, Reactive Energy+)
	 * @return
	 */
	public byte parseAEScale(){
		return data[OFS_ARE_SCALE];
	}

}
