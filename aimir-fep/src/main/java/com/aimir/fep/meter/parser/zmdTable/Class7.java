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
 * Include LP Active/Reactive Power+ Scale. <p>
 */
public class Class7 {
	
	public final static int OFS_ARP_SCALE = 17;
	public final static int LEN_ARP_SCALE = 1;
	
	private byte[] data;
	
	/**
	 * Constructor. <p>
	 * @param data
	 */
	public Class7(byte[] data){
		this.data = data;	
	}
	
	/**
	 * Get LP Scale array.<p>
	 * @return
	 */
	public byte[] parseLPScale(){
		
		byte[] scale = new byte[2];
		
		scale[0] = data[OFS_ARP_SCALE];
		scale[1] = data[OFS_ARP_SCALE];
		
		return scale;
	}

	/**
	 * Get Active Power+ Scale.<p>
	 * @return
	 */
	public byte parseAPScale(){
		return data[OFS_ARP_SCALE];
	}
	
	/**
	 * Get Reactive Power+ Scale.<p>
	 * @return
	 */
	public byte parseRPScale(){
		return data[OFS_ARP_SCALE];
	}

}
