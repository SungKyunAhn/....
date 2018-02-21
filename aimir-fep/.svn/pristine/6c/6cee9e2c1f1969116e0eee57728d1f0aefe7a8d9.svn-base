/**
 * @(#)MT072.java       1.0 06/12/14 *
 *
 * Line-Side diagnostics/Power Quality Data Table Class.
 * Copyright (c) 2006-2007 NuriTelecom, Inc.
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
public class MT072 {

	public static final int OFS_CURRENT_ANGLE_PHA    = 0;
	public static final int OFS_VOLTAGE_ANGLE_PHA    = 2;
	public static final int OFS_CURRENT_ANGLE_PHB    = 4;
	public static final int OFS_VOLTAGE_ANGLE_PHB    = 6;
	public static final int OFS_CURRENT_ANGLE_PHC    = 8;
	public static final int OFS_VOLTAGE_ANGLE_PHC    = 10;

	public static final int OFS_CURRENT_MAG_PHA      = 12;
	public static final int OFS_VOLTAGE_MAG_PHA      = 14;
	public static final int OFS_CURRENT_MAG_PHB      = 16;
	public static final int OFS_VOLTAGE_MAG_PHB      = 18;
	public static final int OFS_CURRENT_MAG_PHC      = 20;
	public static final int OFS_VOLTAGE_MAG_PHC      = 22;

	public static final int OFS_DU_PF                = 24;

	public static final int OFS_DIAG1_COUNTERS       = 25;
	public static final int OFS_DIAG2_COUNTERS       = 26;
	public static final int OFS_DIAG3_COUNTERS       = 27;
	public static final int OFS_DIAG4_COUNTERS       = 28;
	public static final int OFS_DIAG5_PHA_COUNTERS   = 29;
	public static final int OFS_DIAG5_PHB_COUNTERS   = 30;
	public static final int OFS_DIAG5_PHC_COUNTERS   = 31;
	public static final int OFS_DIAG5_TOTAL_COUNTERS = 32;
	public static final int OFS_DIAG6_COUNTERS       = 33;
	public static final int OFS_DIAG7_COUNTERS       = 34;
	public static final int OFS_DIAG8_COUNTERS       = 35;

	public static final int OFS_DIAG_CAUTIONS        = 36;

	public static final int LEN_CURRENT_ANGLE_PHA    = 2;
	public static final int LEN_VOLTAGE_ANGLE_PHA    = 2;
	public static final int LEN_CURRENT_ANGLE_PHB    = 2;
	public static final int LEN_VOLTAGE_ANGLE_PHB    = 2;
	public static final int LEN_CURRENT_ANGLE_PHC    = 2;
	public static final int LEN_VOLTAGE_ANGLE_PHC    = 2;

	public static final int LEN_CURRENT_MAG_PHA      = 2;
	public static final int LEN_VOLTAGE_MAG_PHA      = 2;
	public static final int LEN_CURRENT_MAG_PHB      = 2;
	public static final int LEN_VOLTAGE_MAG_PHB      = 2;
	public static final int LEN_CURRENT_MAG_PHC      = 2;
	public static final int LEN_VOLTAGE_MAG_PHC      = 2;

	public static final int LEN_DU_PF                = 1;

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

	public static final int LEN_DIAG_CAUTIONS        = 1;

	private byte[] data;
    private Log log = LogFactory.getLog(MT072.class);

	/**
	 * Constructor .<p>
	 *
	 * @param data - read data (header,crch,crcl)
	 */
	public MT072(byte[] data) {
		this.data = data;
	}

	public PowerQualityMonitor getData(){

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

			return pq;
		}catch(Exception e){
			log.warn("pq transfrom error: "+e.getMessage());
		}
		return null;
	}

	public double getCURRENT_ANGLE_PHA() throws Exception {

		int val = 0;
		val =  DataFormat.hex2dec(
			DataFormat.LSB2MSB(
				DataFormat.select(
					data,OFS_CURRENT_ANGLE_PHA,LEN_CURRENT_ANGLE_PHA)));

		if(val != 32767 && val > 0){
			return val/10;
		}else{
			return val;
		}
	}

	public double getVOLTAGE_ANGLE_PHA() throws Exception {

		int val = 0;
		val = DataFormat.hex2dec(
			DataFormat.LSB2MSB(
				DataFormat.select(
					data,OFS_CURRENT_ANGLE_PHA,LEN_CURRENT_ANGLE_PHA)));

		if(val != 32767 && val > 0){
			return val/10;
		}else{
			return val;
		}
	}

	public double getCURRENT_ANGLE_PHB() throws Exception {

		int val = 0;
		val = DataFormat.hex2dec(
			DataFormat.LSB2MSB(
				DataFormat.select(
					data,OFS_CURRENT_ANGLE_PHB,LEN_CURRENT_ANGLE_PHB)));

		if(val != 32767 && val > 0){
			return val/10;
		}else{
			return val;
		}
	}

	public double getVOLTAGE_ANGLE_PHB() throws Exception {

		int val = 0;
		val = DataFormat.hex2dec(
			DataFormat.LSB2MSB(
				DataFormat.select(
					data,OFS_VOLTAGE_ANGLE_PHB,LEN_VOLTAGE_ANGLE_PHB)));

		if(val != 32767 && val > 0){
			return val/10;
		}else{
			return val;
		}
	}

	public double getCURRENT_ANGLE_PHC() throws Exception {

		int val = 0;
		val = DataFormat.hex2dec(
			DataFormat.LSB2MSB(
				DataFormat.select(
					data,OFS_CURRENT_ANGLE_PHC,LEN_CURRENT_ANGLE_PHC)));

		if(val != 32767 && val > 0){
			return val/10;
		}else{
			return val;
		}
	}

	public double getVOLTAGE_ANGLE_PHC() throws Exception {

		int val = 0;
		val = DataFormat.hex2dec(
			DataFormat.LSB2MSB(
				DataFormat.select(
					data,OFS_VOLTAGE_ANGLE_PHC,LEN_VOLTAGE_ANGLE_PHC)));

		if(val != 32767 && val > 0){
			return val/10;
		}else{
			return val;
		}
	}

	public int getCURRENT_MAG_PHA() throws Exception {
		return DataFormat.hex2dec(
			DataFormat.LSB2MSB(
				DataFormat.select(
					data,OFS_CURRENT_MAG_PHA,LEN_CURRENT_MAG_PHA)));
	}

	public int getVOLTAGE_MAG_PHA() throws Exception {
		return DataFormat.hex2dec(
			DataFormat.LSB2MSB(
				DataFormat.select(
					data,OFS_VOLTAGE_MAG_PHA,LEN_VOLTAGE_MAG_PHA)));
	}

	public int getCURRENT_MAG_PHB() throws Exception {
		return DataFormat.hex2dec(
			DataFormat.LSB2MSB(
				DataFormat.select(
					data,OFS_CURRENT_MAG_PHB,LEN_CURRENT_MAG_PHB)));
	}

	public int getVOLTAGE_MAG_PHB() throws Exception {
		return DataFormat.hex2dec(
			DataFormat.LSB2MSB(
				DataFormat.select(
					data,OFS_VOLTAGE_MAG_PHB,LEN_VOLTAGE_MAG_PHB)));
	}

	public int getCURRENT_MAG_PHC() throws Exception {
		return DataFormat.hex2dec(
			DataFormat.LSB2MSB(
				DataFormat.select(
					data,OFS_CURRENT_MAG_PHC,LEN_CURRENT_MAG_PHC)));
	}

	public int getVOLTAGE_MAG_PHC() throws Exception {
		return DataFormat.hex2dec(
			DataFormat.LSB2MSB(
				DataFormat.select(
					data,OFS_VOLTAGE_MAG_PHC,LEN_VOLTAGE_MAG_PHC)));
	}

	public int getDU_PF() throws Exception {
		return  DataFormat.hex2unsigned8(data[OFS_DU_PF]);
	}

	public int getDIAG1_COUNTERS() throws Exception {
		return  DataFormat.hex2unsigned8(data[OFS_DIAG1_COUNTERS]);
	}

	public int getDIAG2_COUNTERS() throws Exception {
		return  DataFormat.hex2unsigned8(data[OFS_DIAG2_COUNTERS]);
	}

	public int getDIAG3_COUNTERS() throws Exception {
		return  DataFormat.hex2unsigned8(data[OFS_DIAG3_COUNTERS]);
	}

	public int getDIAG4_COUNTERS() throws Exception {
		return  DataFormat.hex2unsigned8(data[OFS_DIAG4_COUNTERS]);
	}

	public int getDIAG5_PHA_COUNTERS() throws Exception {
		return  DataFormat.hex2unsigned8(data[OFS_DIAG5_PHA_COUNTERS]);
	}

	public int getDIAG5_PHB_COUNTERS() throws Exception {
		return  DataFormat.hex2unsigned8(data[OFS_DIAG5_PHB_COUNTERS]);
	}

	public int getDIAG5_PHC_COUNTERS() throws Exception {
		return  DataFormat.hex2unsigned8(data[OFS_DIAG5_PHC_COUNTERS]);
	}

	public int getDIAG5_TOTAL_COUNTERS() throws Exception {
		return  DataFormat.hex2unsigned8(data[OFS_DIAG5_TOTAL_COUNTERS]);
	}

	public int getDIAG6_COUNTERS() throws Exception {
		return  DataFormat.hex2unsigned8(data[OFS_DIAG6_COUNTERS]);
	}

	public int getDIAG7_COUNTERS() throws Exception {
		return  DataFormat.hex2unsigned8(data[OFS_DIAG7_COUNTERS]);
	}

	public int getDIAG8_COUNTERS() throws Exception {
		return  DataFormat.hex2unsigned8(data[OFS_DIAG8_COUNTERS]);
	}

    public int getDIAG1_STATUS() throws Exception {
        return  getStatus(data[OFS_DIAG_CAUTIONS],7);
    }

    public int getDIAG2_STATUS() throws Exception {
        return  getStatus(data[OFS_DIAG_CAUTIONS],6);
    }

    public int getDIAG3_STATUS() throws Exception {
        return  getStatus(data[OFS_DIAG_CAUTIONS],5);
    }

    public int getDIAG4_STATUS() throws Exception {
        return  getStatus(data[OFS_DIAG_CAUTIONS],4);
    }

    public int getDIAG5_STATUS() throws Exception {
        return  getStatus(data[OFS_DIAG_CAUTIONS],3);
    }

    public int getDIAG6_STATUS() throws Exception {
        return  getStatus(data[OFS_DIAG_CAUTIONS],2);
    }

    public int getDIAG7_STATUS() throws Exception {
        return  getStatus(data[OFS_DIAG_CAUTIONS],1);
    }

    public int getDIAG8_STATUS() throws Exception {
        return  getStatus(data[OFS_DIAG_CAUTIONS],0);
    }

    public String getDIAG1_STATUS_STR() throws Exception {
        return  getStatusString(data[OFS_DIAG_CAUTIONS],7);
    }

    public String getDIAG2_STATUS_STR() throws Exception {
        return  getStatusString(data[OFS_DIAG_CAUTIONS],6);
    }

    public String getDIAG3_STATUS_STR() throws Exception {
        return  getStatusString(data[OFS_DIAG_CAUTIONS],5);
    }

    public String getDIAG4_STATUS_STR() throws Exception {
        return  getStatusString(data[OFS_DIAG_CAUTIONS],4);
    }

    public String getDIAG5_STATUS_STR() throws Exception {
        return  getStatusString(data[OFS_DIAG_CAUTIONS],3);
    }

    public String getDIAG6_STATUS_STR() throws Exception {
        return  getStatusString(data[OFS_DIAG_CAUTIONS],2);
    }

    public String getDIAG7_STATUS_STR() throws Exception {
        return  getStatusString(data[OFS_DIAG_CAUTIONS],1);
    }

    public String getDIAG8_STATUS_STR() throws Exception {
        return  getStatusString(data[OFS_DIAG_CAUTIONS],0);
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

    protected String getStatusString(byte b, int index){
        int val = DataFormat.hex2unsigned8(b);
        val = val & (int)Math.pow(2, index);
        if(val == 1){
            return "In progress";
        }else{
            return "-";
        }
    }

}
