/** 
 * @(#)ST012.java       1.0 2007.09.20 *
 * 
 * LP Channel Kind
 * Copyright (c) 2007-2008 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.SM110Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.ByteArray;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Hex;

/**
 * ex) 

 */
/**
 * @author Park YeonKyoung yeonkyoung@hanmail.net
 */
public class ST012 implements java.io.Serializable {

	private static final long serialVersionUID = 2300267615696197913L;

	public static final int LEN_UOM_ENTRIES = 4;

    private ByteArray[] UOM_ENTRY_RCD = null;
    private int NBR_UOM_ENTRIES = 0;
    
	private byte[] data;
    private static Log log = LogFactory.getLog(ST012.class);

    public ST012() {}
    
	public ST012(byte[] data) 
    {
		this.data = data;
        parse();
	}
    
    public void parse()
    {
        this.NBR_UOM_ENTRIES = data.length/LEN_UOM_ENTRIES;
        this.UOM_ENTRY_RCD = new ByteArray[NBR_UOM_ENTRIES];
        for(int i = 0; i < NBR_UOM_ENTRIES; i++)
        {
            this.UOM_ENTRY_RCD[i] = new ByteArray();
            this.UOM_ENTRY_RCD[i].append(data, i*LEN_UOM_ENTRIES, LEN_UOM_ENTRIES);
        }
    }
    
    public ByteArray getUOM_ENTRY_RCD(int index) throws Exception 
    {
        if(index > (this.NBR_UOM_ENTRIES-1))
        {
            throw new Exception("Invalid Index:"+index+",Max index is "+(this.NBR_UOM_ENTRIES-1));
        }
        return this.UOM_ENTRY_RCD[index];
    }
    
    public ByteArray[] getUOM_ENTRYIES ()
    {
        return this.UOM_ENTRY_RCD;
    }
    
    public String decodeUOM_ENTRYIES(ByteArray ba) 
    throws Exception
    {
        byte[] bx = DataFormat.LSB2MSB(ba.toByteArray());
        String hexStr = Hex.decode(bx);        
        return hexStr;
    }

    public String[] getUOM_CODE(int[] index)
    throws Exception
    {
        String[] ret = new String[index.length];
        for(int i = 0; i < index.length; i++)
        {
            ret[i] = decodeUOM_ENTRYIES(getUOM_ENTRY_RCD(index[i]));
        }
        return ret;
    }

}
