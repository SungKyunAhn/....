/** 
 * @(#)NT026.java       1.0 09/04/23 *
 * 
 * Self Read Data Class.(TOU Version)
 * Copyright (c) 2009-2010 NuriTelecom, Inc.
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

import com.aimir.fep.util.DataFormat;

/**
 * @author YK.Park
 */
public class NT026 implements java.io.Serializable {

	private static final long serialVersionUID = 7806688878589452409L;

	private byte[] data;

    private static Log log = LogFactory.getLog(NT026.class);

    private NT025[] nt025 = null;
    
    protected final int OFS_LIST_STATUS = 0;
    protected final int OFS_NBR_VALID_ENTRIES = 1;
    protected final int OFS_LAST_ENTRY_ELEMENT = 2;
    protected final int OFS_LAST_ENTRY_SEQ_NUMBER = 3;
    protected final int OFS_NBR_UNREAD_ENTRIES = 5;
    protected final int OFS_SELF_READ_ENTRIES = 6;

	private int NBR_TIERS;
	private int NBR_SUM;
	private int NBR_DMD;
	private int NBR_COIN;
    private int energyscale;
    private int powerscale;
    private int displayscale;
    private int dispmult;

    public NT026() {}
    
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public NT026(byte[] data, int nbr_tiers, int nbr_sum, int nbr_dmd, int nbr_coin,
                              int energyscale,int powerscale, int displayscale, int dispmult) 
    {
		this.data = data;
		this.NBR_TIERS = 3;
		this.NBR_SUM   = 2;
		this.NBR_DMD   = 2;
		this.NBR_COIN  = 0;
        this.energyscale = energyscale;
        this.powerscale = powerscale;
        this.displayscale = displayscale;
        this.dispmult = dispmult;

		try {
			parseData();
		} catch (Exception e) {
			log.warn("ST26 parse error",e);
		}
	}

	private void parseData() throws Exception 
    {
        int offset = OFS_SELF_READ_ENTRIES;
        int len = 133;//nt025 block size
        int nbr_valid_entries = getNBR_VALID_ENTRIES();
        log.info("nbr_valid_entries="+nbr_valid_entries);
        if( nbr_valid_entries  > 0)
        {
            nt025 = new NT025[nbr_valid_entries];
            for(int i = 0; i < nbr_valid_entries; i++)
            {
                //log.debug("selfread="+Util.getHexString(DataFormat.select(data,offset,len)));
                nt025[i] = new NT025(DataFormat.select(data,offset,len),
                                     NBR_TIERS, NBR_SUM, NBR_DMD, NBR_COIN,
                                     energyscale, powerscale, displayscale, dispmult);
                offset += (i+1)*len;  

                //log.debug("resettime="+nt025[i].getResetTime());
            }
        }
	}
    
    public NT025[] getSelfReads()
    {
        return this.nt025;
    }
    
    public int getNBR_VALID_ENTRIES()
    {
        //NBR_VALID_ENTRIES
        return DataFormat.hex2unsigned8(data[OFS_NBR_VALID_ENTRIES]);
    }
    
    public int getLAST_ENTRY_ELEMENT()
    {
        //LAST_ENTRY_ELEMENT
        return DataFormat.hex2unsigned8(data[OFS_LAST_ENTRY_ELEMENT]);
    }
    
    public int getNBR_UNREAD_ENTRIES()
    {
        //LAST_ENTRY_ELEMENT
        return DataFormat.hex2unsigned8(data[OFS_NBR_UNREAD_ENTRIES]);
    }
}