/* 
 * @(#)DIFE.java       1.0 2008-06-02 *
 * 
 * Data Information config
 * Copyright (c) 2008-2009 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
/**
 * @author YK.Park
 */
package com.aimir.fep.meter.parser.kdhTable;

public class DIFE {    

    private byte rawData = 0x00;

	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public DIFE(byte rawData) {
        this.rawData = rawData;
	}
    
    public int getCount()
    {
        int dife = rawData & 0xFF;
        return dife;
    }
    
    public String toString()
    {
        return "Count=["+getCount()+"]\n";
    }
}
