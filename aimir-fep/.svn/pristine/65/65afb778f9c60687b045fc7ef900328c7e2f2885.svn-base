/** 
 * @(#)MT000.java       1.0 2008.06.26 *
 * 
 * GE Device Table
 * Copyright (c) 2008-2009 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.SM110Table;

/**
 * GE Device Table
 * @author YK.Park
 */
public class MT000 implements java.io.Serializable {

	private static final long serialVersionUID = -1141012949490289564L;

    private byte[] MFG_VERSION_NBR = new byte[1];
    private byte[] MFG_REVISION_NBR = new byte[1];
    private byte[] ROM_GE_PART_NUMBER = new byte[5];
    private byte[] ROM_FW_VERSION = new byte[1];
    private byte[] ROM_FW_REVISION = new byte[1];
    private byte[] ROM_FW_BUILD = new byte[1];
    private byte[] MFG_TEST_VECTOR = new byte[4];
    private byte[] METER_TYPE = new byte[1];
    private byte[] METER_MODE = new byte[1];
    private byte[] REGISTER_FUNCTION = new byte[1];
    private byte[] INSTALLED_OPTION1 = new byte[1];
    private byte[] INSTALLED_OPTION2 = new byte[1];
    private byte[] INSTALLED_OPTION3 = new byte[1];
    private byte[] INSTALLED_OPTION4 = new byte[1];
    private byte[] INSTALLED_OPTION5 = new byte[1];
    private byte[] INSTALLED_OPTION6 = new byte[1];
    
    private byte[] UPGRADES_BFLD = new byte[4];
    private byte[] RESERVED = new byte[4];
    private byte[] TDK_GE_PART_NUMBER = new byte[5];
    private byte[] TDK_FW_VERSION = new byte[1];
    private byte[] TDK_FW_REVISION = new byte[1];
    private byte[] TDK_FW_BUILD = new byte[1];
		
	private byte[] data;
    
    public MT000() {}
    
	/**
	 * Constructor .<p>
	 * @param data - read data (header,crch,crcl)
	 */
	public MT000(byte[] data) {
		this.data = data;
		parse();
	}	
	
	public void parse(){
		
		int pos = 0;

		if(data.length >= 39){

			System.arraycopy(data, pos, MFG_VERSION_NBR, 0, MFG_VERSION_NBR.length);
			pos += MFG_VERSION_NBR.length;
			System.arraycopy(data, pos, MFG_REVISION_NBR, 0, MFG_REVISION_NBR.length);
			pos += MFG_REVISION_NBR.length;
			System.arraycopy(data, pos, ROM_GE_PART_NUMBER, 0, ROM_GE_PART_NUMBER.length);
			pos += ROM_GE_PART_NUMBER.length;
			System.arraycopy(data, pos, ROM_FW_VERSION, 0, ROM_FW_VERSION.length);
			pos += ROM_FW_VERSION.length;
			System.arraycopy(data, pos, ROM_FW_REVISION, 0, ROM_FW_REVISION.length);
			pos += ROM_FW_REVISION.length;
			System.arraycopy(data, pos, ROM_FW_BUILD, 0, ROM_FW_BUILD.length);
			pos += ROM_FW_BUILD.length;			
			System.arraycopy(data, pos, MFG_TEST_VECTOR, 0, MFG_TEST_VECTOR.length);
			pos += MFG_TEST_VECTOR.length;
			System.arraycopy(data, pos, METER_TYPE, 0, METER_TYPE.length);
			pos += METER_TYPE.length;
			System.arraycopy(data, pos, METER_MODE, 0, METER_MODE.length);
			pos += METER_MODE.length;
			System.arraycopy(data, pos, REGISTER_FUNCTION, 0, REGISTER_FUNCTION.length);
			pos += REGISTER_FUNCTION.length;
			System.arraycopy(data, pos, INSTALLED_OPTION1, 0, INSTALLED_OPTION1.length);
			pos += INSTALLED_OPTION1.length;
			System.arraycopy(data, pos, INSTALLED_OPTION2, 0, INSTALLED_OPTION2.length);
			pos += INSTALLED_OPTION2.length;
			System.arraycopy(data, pos, INSTALLED_OPTION3, 0, INSTALLED_OPTION3.length);
			pos += INSTALLED_OPTION3.length;
			System.arraycopy(data, pos, INSTALLED_OPTION4, 0, INSTALLED_OPTION4.length);
			pos += INSTALLED_OPTION4.length;
			System.arraycopy(data, pos, INSTALLED_OPTION5, 0, INSTALLED_OPTION5.length);
			pos += INSTALLED_OPTION5.length;
			System.arraycopy(data, pos, INSTALLED_OPTION6, 0, INSTALLED_OPTION6.length);
			pos += INSTALLED_OPTION6.length;
		    
			System.arraycopy(data, pos, UPGRADES_BFLD, 0, UPGRADES_BFLD.length);
			pos += UPGRADES_BFLD.length;
			System.arraycopy(data, pos, RESERVED, 0, RESERVED.length);
			pos += RESERVED.length;
			System.arraycopy(data, pos, TDK_GE_PART_NUMBER, 0, TDK_GE_PART_NUMBER.length);
			pos += TDK_GE_PART_NUMBER.length;
			System.arraycopy(data, pos, TDK_FW_VERSION, 0, TDK_FW_VERSION.length);
			pos += TDK_FW_VERSION.length;
			System.arraycopy(data, pos, TDK_FW_REVISION, 0, TDK_FW_REVISION.length);
			pos += TDK_FW_REVISION.length;
			System.arraycopy(data, pos, TDK_FW_BUILD, 0, TDK_FW_BUILD.length);
			pos += TDK_FW_BUILD.length;
		    
		}else{
			System.arraycopy(data, pos, METER_MODE, 0, METER_MODE.length);
			pos += METER_MODE.length;
		}
	}
	
	public int getMETER_MODE() throws Exception {
	    return METER_MODE[0] & 0xFF;
	}
    
    public String getMETER_MODE_NAME() throws Exception {
        
        int val = getMETER_MODE();
        switch(val){
        case 0: return "Demand Only";
        case 1: return "Demand LP";
        case 2: return "TOU";
        default :return "unknown+["+val+"]";
        }
    }
}
