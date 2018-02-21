/** 
 * @(#)ST021.java       1.0 06/12/14 *
 * 
 * Actual Dimension Register Table.
 * Copyright (c) 2006-2007 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.kV2cTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;

/**
 * @author Park YeonKyoung goodjob@nuritelecom.com
 */
public class ST021 {
	
	public static final int OFS_REG_FUNC1_FLAG  = 0;
	public static final int OFS_REG_FUNC2_FLAG  = 1;
	public static final int OFS_NBR_SELF_READS  = 2;
	public static final int OFS_NBR_SUMMATIONS  = 3;
	public static final int OFS_NBR_DEMANDS     = 4;
	public static final int OFS_NBR_COINCIDENT  = 5;
	public static final int OFS_NBR_OCCUR       = 6;
	public static final int OFS_NBR_TIERS       = 7;
	public static final int OFS_NBR_PRESENT_DMD = 8;
	public static final int OFS_NBR_PRESENT_VAL = 9;
	
	private byte[] data;
    private static Log log = LogFactory.getLog(ST021.class);
	
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public ST021(byte[] data) {
		this.data = data;
	}
	
	/**
	 * Number of self-reads in use.
	 */
	public int getNBR_SELF_READS() {
		return DataFormat.hex2unsigned8(data[OFS_NBR_SELF_READS]);
	}
	
	/**
	 * Number of summations per data block.
	 */
	public int getNBR_SUMMATIONS() {
		return DataFormat.hex2unsigned8(data[OFS_NBR_SUMMATIONS]);
	}
	
	/**
	 * Number of demands per data block.
	 */
	public int getNBR_DEMANDS() {
		return DataFormat.hex2unsigned8(data[OFS_NBR_DEMANDS]);
	}
	
	/**
	 * Number of coincident values saved concurrently in each data block.
	 */
	public int getNBR_COINCIDENT() {
		return DataFormat.hex2unsigned8(data[OFS_NBR_COINCIDENT]);
	}

	/**
	 * Number of occurrences stored for each selection.
	 */
	public int getNBR_OCCUR() {
		return DataFormat.hex2unsigned8(data[OFS_NBR_OCCUR]);
	}
	
	/**
	 * Number of tiers in use.
	 */
	public int getNBR_TIERS() {
		return DataFormat.hex2unsigned8(data[OFS_NBR_TIERS]);
	}

	/**
	 * Number of present demands reported in ST28 
	 */
	public int getNBR_PRESENT_DMD() {
		return DataFormat.hex2unsigned8(data[OFS_NBR_PRESENT_DMD]);
	}
	
	/**
	 * Not used in I210
	 */
	public int getNBR_PRESENT_VAL() {
		return DataFormat.hex2unsigned8(data[OFS_NBR_PRESENT_VAL]);
	}
    
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("ST021 DATA[");        
            sb.append("(NBR_SELF_READS=").append(getNBR_SELF_READS()).append("),");
            sb.append("(NBR_SUMMATIONS=").append(getNBR_SUMMATIONS()).append("),");
            sb.append("(NBR_DEMANDS=").append(getNBR_DEMANDS()).append("),");
            sb.append("(NBR_COINCIDENT=").append(getNBR_COINCIDENT()).append("),");
            sb.append("(NBR_OCCUR=").append(getNBR_OCCUR()).append("),");
            sb.append("(NBR_TIERS=").append(getNBR_TIERS()).append("),");
            sb.append("(NBR_PRESENT_DMD=").append(getNBR_PRESENT_DMD()).append("),");
            sb.append("(NBR_PRESENT_VAL=").append(getNBR_PRESENT_VAL()).append(')');
            sb.append("]\n");
        }catch(Exception e){
            log.warn("NURI_MDM TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }


}
