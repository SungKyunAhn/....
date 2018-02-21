/* 
 * @(#)NURI_CB.java       1.0 07/05/02 *
 * 
 * Current Billing.
 * Copyright (c) 2007-2008 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.kV2cTable;

/**
 * @author Park YeonKyoung goodjob@nuritelecom.com
 */
public class NURI_CB extends ST023{

	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public NURI_CB(byte[] rawData, 
                   int nbr_tiers, int nbr_sum, int nbr_dmd, int nbr_coin,
                   int energyscale, int powerscale, int displayscale, int dispmult) {
        
        super(rawData, nbr_tiers, nbr_sum, nbr_dmd, nbr_coin, 
              energyscale, powerscale,displayscale, dispmult);
	}

}
