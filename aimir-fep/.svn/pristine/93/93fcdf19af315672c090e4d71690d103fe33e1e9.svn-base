/** 
 * @(#)ST22.java       1.0 06/06/09 *
 * 
 * Data Selection Table.
 * Copyright (c) 2006-2007 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.a1830rlnTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;

/**
 * ex)
 * 00 04 0c ff ff 
 * 28 2c 34 ff ff 
 * 1f 
 * ff ff ff ff ff ff ff ff ff ff 
 * 00 00 01 01 02 02 03 03 04 04 
 */
/**
 * @author Park YeonKyoung yeonkyoung@hanmail.net
 */
public class ST22 implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3644317916341284289L;
	public int LEN_SUMMATION_SELECT;
	public int LEN_DEMAND_SELECT;
	public int LEN_MIN_OR_MAX_FLAGS;
	public int LEN_COINCIDENT_SELECT;
	public int LEN_COINCIDENT_DEMAND_ASSOC;
	
	private byte[] data;
    private Log logger = LogFactory.getLog(getClass());
	
	private int NBR_SUM;
	private int NBR_DMD;
	private int NBR_COIN;

	public ST22(byte[] data, int nbr_sum, int nbr_dmd, int nbr_coin) {
		this.data = data;
		this.NBR_SUM  = nbr_sum;
		this.NBR_DMD  = nbr_dmd;
		this.NBR_COIN = nbr_coin;		
		this.LEN_SUMMATION_SELECT        = this.NBR_SUM*1;
		this.LEN_DEMAND_SELECT           = this.NBR_DMD*1;
		this.LEN_MIN_OR_MAX_FLAGS        = (this.NBR_DMD+7)/8;
		this.LEN_COINCIDENT_SELECT       = this.NBR_COIN*1;
		this.LEN_COINCIDENT_DEMAND_ASSOC = this.NBR_COIN*1;
	}
	
	public byte[] getSUMMATION_SELECT() throws Exception{		
		int ofs = 0;
		return DataFormat.select(data,ofs,this.NBR_SUM);		
	}
	
	public byte[] getDEMAND_SELECT() throws Exception {
		int ofs = this.NBR_SUM;
		return DataFormat.select(data,ofs,this.LEN_DEMAND_SELECT);	
	}
	
	public byte[] getCOINCIDENT_SELECT() throws Exception {
		int ofs = this.NBR_SUM+this.LEN_DEMAND_SELECT+LEN_MIN_OR_MAX_FLAGS;
		return DataFormat.select(data,ofs,this.LEN_COINCIDENT_SELECT);	
	}
	
	public byte[] getCOINCIDENT_DEMAND_ASSOC() throws Exception {
		int ofs = this.NBR_SUM
				+ this.LEN_DEMAND_SELECT
				+ this.LEN_MIN_OR_MAX_FLAGS
				+ this.LEN_COINCIDENT_SELECT;
		return DataFormat.select(data,ofs,this.LEN_COINCIDENT_DEMAND_ASSOC);	
	}

}
