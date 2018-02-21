/*
 * @(#)NURI_PQ.java       1.0 07/05/02 *
 *
 * Power Quality Data.
 * Copyright (c) 2007-2008 NuriTelecom, Inc.
 * All rights reserved. *
 * This software is the confidential and proprietary information of
 * Nuritelcom, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Nuritelecom.
 */

package com.aimir.fep.meter.parser.SM300Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.PowerQualityMonitor;
import com.aimir.fep.util.DataFormat;

/**
 * @author Park YeonKyoung goodjob@nuritelecom.com
 */
public class NURI_PQ {

    public static final int OFS_DU_PF = 0;
    public static final int OFS_DIAG_COUNTERS = 1;
    public static final int OFS_DIAG_CAUTIONS = 12;
    public static final int OFS_CUM_POWER_OUTAGE_SECS = 14;
    public static final int OFS_NBR_POWER_OUTAGES = 18;
    public static final int OFS_DT_LAST_POWER_OUTAGE = 20;

    public static final int LEN_DU_PF = 1;
    public static final int LEN_DIAG_COUNTERS = 12;
    public static final int LEN_DIAG_CAUTIONS = 1;
    public static final int LEN_CUM_POWER_OUTAGE_SECS = 4;
    public static final int LEN_NBR_POWER_OUTAGES = 2;
    public static final int LEN_DT_LAST_POWER_OUTAGE = 5;

    public static final int OFS_DIAG1_COUNTERS       = 1;
    public static final int OFS_DIAG2_COUNTERS       = 2;
    public static final int OFS_DIAG3_COUNTERS       = 3;
    public static final int OFS_DIAG4_COUNTERS       = 4;
    public static final int OFS_DIAG5_PHA_COUNTERS   = 5;
    public static final int OFS_DIAG5_PHB_COUNTERS   = 6;
    public static final int OFS_DIAG5_PHC_COUNTERS   = 7;
    public static final int OFS_DIAG5_TOTAL_COUNTERS = 8;
    public static final int OFS_DIAG6_COUNTERS       = 9;
    public static final int OFS_DIAG7_COUNTERS       = 10;
    public static final int OFS_DIAG8_COUNTERS       = 11;

    public static final int LEN_DIAG1_COUNTERS       = 1;
    public static final int LEN_DIAG2_COUNTERS       = 1;
    public static final int LEN_DIAG3_COUNTERS       = 1;
    public static final int LEN_DIAG4_COUNTERS       = 1;
    public static final int LEN_DIAG5_PHA_COUNTERS   = 1;
    public static final int LEN_DIAG5_PHB_COUNTERS   = 1;
    public static final int LEN_DIAG5_PHC_COUNTERS   = 1;
    public static final int LEN_DIAG5_TOTAL_COUNTERS = 1;
    public static final int LEN_DIAG6_COUNTERS       = 1;
    public static final int LEN_DIAG7_COUNTERS       = 1;
    public static final int LEN_DIAG8_COUNTERS       = 1;

	private byte[] rawData = null;

    private static Log log = LogFactory.getLog(NURI_PQ.class);

	/**
	 * Constructor .<p>
	 *
	 * @param data - read data (header,crch,crcl)
	 */
	public NURI_PQ(byte[] rawData) {
		this.rawData = rawData;
	}

    public PowerQualityMonitor getData() throws Exception {

        PowerQualityMonitor pq = new PowerQualityMonitor();

        try{
            pq.setPOLARITY_CROSS_PHASE_CNT(getDIAG1_COUNTERS());
            pq.setPOLARITY_CROSS_PHASE_STAT(getDIAG1_STATUS());
            pq.setIMBALANCE_VOL_CNT(getDIAG2_COUNTERS());
            pq.setIMBALANCE_VOL_STAT(getDIAG2_STATUS());
            pq.setLOW_CURR_CNT(getDIAG3_COUNTERS());
            pq.setLOW_CURR_STAT(getDIAG3_STATUS());
            pq.setIMBALANCE_CURR_CNT(getDIAG4_COUNTERS());
            pq.setIMBALANCE_CURR_STAT(getDIAG4_STATUS());
            pq.setDISTORTION_A_CNT(getDIAG5_PHA_COUNTERS());
            pq.setDISTORTION_B_CNT(getDIAG5_PHB_COUNTERS());
            pq.setDISTORTION_C_CNT(getDIAG5_PHC_COUNTERS());
            pq.setDISTORTION_CNT(getDIAG5_TOTAL_COUNTERS());
            pq.setDISTORTION_STAT(getDIAG5_STATUS());
            pq.setLOW_VOL_CNT(getDIAG6_COUNTERS());
            pq.setLOW_VOL_STAT(getDIAG6_STATUS());
            pq.setHIGH_VOL_CNT(getDIAG7_COUNTERS());
            pq.setHIGH_VOL_STAT(getDIAG7_STATUS());
            pq.setHIGH_NEUTRAL_CURR_CNT(getDIAG8_COUNTERS());
            pq.setHIGH_NEUTRAL_CURR_STAT(getDIAG8_STATUS());

            log.debug(toString());

            return pq;
        }catch(Exception e){
            log.warn("pq transfrom error: "+e.getMessage());
            throw new Exception("pq transfrom error: ",e);
        }
    }

    public int getDU_PF() throws Exception {
        return  DataFormat.hex2unsigned8(rawData[OFS_DU_PF]);
    }

    public int getDIAG1_COUNTERS() throws Exception {
        return  DataFormat.hex2unsigned8(rawData[OFS_DIAG1_COUNTERS]);
    }

    public int getDIAG2_COUNTERS() throws Exception {
        return  DataFormat.hex2unsigned8(rawData[OFS_DIAG2_COUNTERS]);
    }

    public int getDIAG3_COUNTERS() throws Exception {
        return  DataFormat.hex2unsigned8(rawData[OFS_DIAG3_COUNTERS]);
    }

    public int getDIAG4_COUNTERS() throws Exception {
        return  DataFormat.hex2unsigned8(rawData[OFS_DIAG4_COUNTERS]);
    }

    public int getDIAG5_PHA_COUNTERS() throws Exception {
        return  DataFormat.hex2unsigned8(rawData[OFS_DIAG5_PHA_COUNTERS]);
    }

    public int getDIAG5_PHB_COUNTERS() throws Exception {
        return  DataFormat.hex2unsigned8(rawData[OFS_DIAG5_PHB_COUNTERS]);
    }

    public int getDIAG5_PHC_COUNTERS() throws Exception {
        return  DataFormat.hex2unsigned8(rawData[OFS_DIAG5_PHC_COUNTERS]);
    }

    public int getDIAG5_TOTAL_COUNTERS() throws Exception {
        return  DataFormat.hex2unsigned8(rawData[OFS_DIAG5_TOTAL_COUNTERS]);
    }

    public int getDIAG6_COUNTERS() throws Exception {
        return  DataFormat.hex2unsigned8(rawData[OFS_DIAG6_COUNTERS]);
    }

    public int getDIAG7_COUNTERS() throws Exception {
        return  DataFormat.hex2unsigned8(rawData[OFS_DIAG7_COUNTERS]);
    }

    public int getDIAG8_COUNTERS() throws Exception {
        return  DataFormat.hex2unsigned8(rawData[OFS_DIAG8_COUNTERS]);
    }

    public int getDIAG1_STATUS() throws Exception {
        return  getStatus(rawData[OFS_DIAG_CAUTIONS],7);
    }

    public int getDIAG2_STATUS() throws Exception {
        return  getStatus(rawData[OFS_DIAG_CAUTIONS],6);
    }

    public int getDIAG3_STATUS() throws Exception {
        return  getStatus(rawData[OFS_DIAG_CAUTIONS],5);
    }

    public int getDIAG4_STATUS() throws Exception {
        return  getStatus(rawData[OFS_DIAG_CAUTIONS],4);
    }

    public int getDIAG5_STATUS() throws Exception {
        return  getStatus(rawData[OFS_DIAG_CAUTIONS],3);
    }

    public int getDIAG6_STATUS() throws Exception {
        return  getStatus(rawData[OFS_DIAG_CAUTIONS],2);
    }

    public int getDIAG7_STATUS() throws Exception {
        return  getStatus(rawData[OFS_DIAG_CAUTIONS],1);
    }

    public int getDIAG8_STATUS() throws Exception {
        return  getStatus(rawData[OFS_DIAG_CAUTIONS],0);
    }

    protected int getStatus(byte b, int index){
        int val = DataFormat.hex2unsigned8(b);
        val = val & (int)Math.pow(2, index);
        if(val > 0){
            val = 1;
        }else{
            val = 0;
        }
        return val;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("NURI_PQ Meter DATA[");
            sb.append("(DIAG1_COUNTERS=").append(getDIAG1_COUNTERS()).append("),");
            sb.append("(DIAG1_STATUS=").append(getDIAG1_STATUS()).append("),");
            sb.append("(DIAG2_COUNTERS=").append(getDIAG2_COUNTERS()).append("),");
            sb.append("(DIAG2_STATUS()=").append(getDIAG2_STATUS()).append("),");
            sb.append("(DIAG3_COUNTERS=").append(getDIAG3_COUNTERS()).append("),");
            sb.append("(DIAG3_STATUS=").append(getDIAG3_STATUS()).append("),");
            sb.append("(DIAG4_COUNTERS=").append(getDIAG4_COUNTERS()).append("),");
            sb.append("(DIAG4_STATUS=").append(getDIAG4_STATUS()).append("),");
            sb.append("(DIAG5_PHA_COUNTERS=").append(getDIAG5_PHA_COUNTERS()).append("),");
            sb.append("(DIAG5_PHB_COUNTERS=").append(getDIAG5_PHB_COUNTERS()).append("),");
            sb.append("(DIAG5_PHC_COUNTERS=").append(getDIAG5_PHC_COUNTERS()).append("),");
            sb.append("(DIAG5_TOTAL_COUNTERS=").append(getDIAG5_TOTAL_COUNTERS()).append("),");
            sb.append("(DIAG5_STATUS=").append(getDIAG5_STATUS()).append("),");
            sb.append("(DIAG6_COUNTERS=").append(getDIAG6_COUNTERS()).append("),");
            sb.append("(DIAG6_STATUS=").append(getDIAG6_STATUS()).append("),");
            sb.append("(DIAG7_COUNTERS=").append(getDIAG7_COUNTERS()).append("),");
            sb.append("(DIAG7_STATUS=").append(getDIAG7_STATUS()).append("),");
            sb.append("(DIAG8_COUNTERS=").append(getDIAG8_COUNTERS()).append("),");
            sb.append("(DIAG8_STATUS=").append(getDIAG8_STATUS()).append("),");
            sb.append("]\n");
        }catch(Exception e){
            log.warn("NURI_PQ TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }

}
