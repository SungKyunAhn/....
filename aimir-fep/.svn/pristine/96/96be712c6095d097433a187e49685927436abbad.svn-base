/** 
 * @(#)ST023.java       1.0 06/12/14 *
 * 
 * Current Billing Data Class.
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

import com.aimir.fep.meter.data.TOU_BLOCK;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;
import com.aimir.util.DateTimeUtil;

/**
 * @author Park YeonKyoung goodjob@nuritelecom.com
 */
public class ST023 {
    private static Log _log = LogFactory.getLog(ST023.class);
    
	private final int OFS_DATA_FIELD_START = 1;

	private int NBR_TIERS;
	private int NBR_SUM;
	private int NBR_DMD;
	private int NBR_COIN;
    private int energyscale;
    private int powerscale;
    private int displayscale;
    private int dispmult;
	
	private byte[] data;

	private final static int SIZE_SUMMATION  = 6;
	private final static int SIZE_EVENTTIME  = 5;
	private final static int SIZE_CUMDEMAND  = 6;
	private final static int SIZE_CURDEMAND  = 4;
	private final static int SIZE_COINCIDENT = 4;
	
	private TOU_BLOCK[] tou_block;

	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public ST023(byte[] data, int nbr_tiers, int nbr_sum, int nbr_dmd, int nbr_coin,
                              int energyscale,int powerscale, int displayscale, int dispmult) {
		this.data = data;
		this.NBR_TIERS = nbr_tiers;
		this.NBR_SUM   = nbr_sum;
		this.NBR_DMD   = nbr_dmd;
		this.NBR_COIN  = nbr_coin;
        this.energyscale = energyscale;
        this.powerscale = powerscale;
        this.displayscale = displayscale;
        this.dispmult = dispmult;
		this.tou_block = new TOU_BLOCK[this.NBR_TIERS];
		try {
			parseData();
		} catch (Exception e) {
			_log.warn("ST023 Parse Error :",e);
		}
	}
	
	public TOU_BLOCK[] getTOU_BLOCK(){
		return this.tou_block;
	}

	private void parseData() throws Exception {
		
		int offset = OFS_DATA_FIELD_START;
		
		int tot_summation_size  = this.NBR_SUM*SIZE_SUMMATION;
		int tot_event_size      = this.NBR_DMD*SIZE_EVENTTIME;
		int tot_cumdemand_size  = this.NBR_DMD*SIZE_CUMDEMAND;
		int tot_curdemand_size  = this.NBR_DMD*SIZE_CURDEMAND;
		int tot_coincident_size = this.NBR_COIN*SIZE_COINCIDENT;
		
		int tou_blk_size 
			= tot_summation_size
			+ tot_event_size
			+ tot_cumdemand_size
			+ tot_curdemand_size
			+ tot_coincident_size;		
		
		for(int i = 0; i < this.NBR_TIERS; i++){			
			
			byte[] temp = new byte[tou_blk_size];
			System.arraycopy(data,offset,temp,0,tou_blk_size);
			//_log.debug("tou["+i+"]: \n"+Util.getHexString(temp));
			tou_block[i] = new TOU_BLOCK(this.NBR_SUM, 
										 this.NBR_DMD, 
										 this.NBR_DMD, 
									 	 this.NBR_DMD, 
										 this.NBR_COIN);
										 
			tou_block[i].setLowData(temp);
			
			int ofs = 0;
			for(int x = 0; x < this.NBR_SUM; x++){
				tou_block[i].setSummations(x,getEnergyRate(temp,ofs,SIZE_SUMMATION));
				ofs += SIZE_SUMMATION;
			}
			
			for(int x = 0; x < this.NBR_DMD; x++){
				tou_block[i].setEventTime(x,getYyyymmddhhmm(temp,ofs,SIZE_EVENTTIME));
				ofs += SIZE_EVENTTIME;

				tou_block[i].setCumDemand(x,getDemandRate(temp,ofs,SIZE_CUMDEMAND));
				ofs += SIZE_CUMDEMAND;

				tou_block[i].setCurrDemand(x,getDemandRate(temp,ofs,SIZE_CURDEMAND));
				ofs += SIZE_CURDEMAND;
			}
			
			for(int x = 0; x < this.NBR_COIN; x++){
				tou_block[i].setCoincident(x,getDemandRate(temp,ofs,SIZE_COINCIDENT));
				ofs += SIZE_COINCIDENT;
			}
			
			offset += tou_blk_size;
		}

	}
	
	public float getPF(long ch1, long ch2) throws Exception {

		float  pf   = (float)(ch1/(Math.sqrt(Math.pow(ch1,2)+Math.pow(ch2,2))));

		if(ch1 == 0 && ch2 == 0)
			pf = (float) 1.0;
		/* Parsing Transform Results put Data Class */
		if(pf < 0 || pf > 1.0)
			throw new Exception("BILL PF DATA FORMAT ERROR : "+pf);

		return pf;
	}
	
	private Double getEnergyRate(byte[] data,int ofs, int len) throws Exception {
	    long val = DataFormat.hex2signeddec(
					DataFormat.LSB2MSB(
						DataFormat.select(
							data,ofs,len)));
        return new Double(val*energyscale*dispmult/Math.pow(10,(9+displayscale)));
	}
	
	private Double getDemandRate(byte[] data,int ofs, int len) throws Exception {
		long val = DataFormat.hex2signeddec(
					DataFormat.LSB2MSB(
						DataFormat.select(
							data,ofs,len)));
        return new Double(val*powerscale*dispmult/Math.pow(10,(9+displayscale)));
	}

	private byte[] parseRate(byte[] data,int ofs, int len) throws Exception {
		return DataFormat.LSB2MSB(
					DataFormat.select(
							data,ofs,len));
	}	
	
	private byte[] parseYyyymmddhhmm(byte[] b, int offset, int len)
						throws Exception {

		byte[] datetime = new byte[6];
		
		int blen = b.length;
		if(blen-offset < 5)
			throw new Exception("YYMMDDHHMMSS FORMAT ERROR : "+(blen-offset));
		if(len != 5)
			throw new Exception("YYMMDDHHMMSS LEN ERROR : "+len);
		
		int idx = offset;
		int yy = DataFormat.hex2unsigned8(b[idx++]);
		int mm = DataFormat.hex2unsigned8(b[idx++]);
		int dd = DataFormat.hex2unsigned8(b[idx++]);
		int hh = DataFormat.hex2unsigned8(b[idx++]);
		int MM = DataFormat.hex2unsigned8(b[idx++]);
		
		int currcen = (Integer.parseInt(DateTimeUtil
                .getCurrentDateTimeByFormat("yyyy"))/100)*100;
	
		int year   = yy;
		if(year != 0){
			year = yy + currcen;
		}

		datetime[0] = (byte)((year >> 8) & 0xff);
		datetime[1] = (byte)(year & 0xff);
		datetime[2] = (byte) mm;
		datetime[3] = (byte) dd;
		datetime[4] = (byte) hh;
		datetime[5] = (byte) MM;
		
		return datetime;
		
	}
	
	private String getYyyymmddhhmm(byte[] b, int offset, int len)
							throws Exception {
		
		int blen = b.length;
		if(blen-offset < 5)
			throw new Exception("YYMMDDHHMMSS FORMAT ERROR : "+(blen-offset));
		if(len != 5)
			throw new Exception("YYMMDDHHMMSS LEN ERROR : "+len);
		
		int idx = offset;
		
		int yy = DataFormat.hex2unsigned8(b[idx++]);
		int mm = DataFormat.hex2unsigned8(b[idx++]);
		int dd = DataFormat.hex2unsigned8(b[idx++]);
		int hh = DataFormat.hex2unsigned8(b[idx++]);
		int MM = DataFormat.hex2unsigned8(b[idx++]);

		StringBuffer ret = new StringBuffer();
		
		int currcen = (Integer.parseInt(DateTimeUtil
                .getCurrentDateTimeByFormat("yyyy"))/100)*100;
	
		int year   = yy;
		if(year != 0){
			year = yy + currcen;
		}
		
		ret.append(Util.frontAppendNStr('0',Integer.toString(year),4));
		ret.append(Util.frontAppendNStr('0',Integer.toString(mm),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(dd),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(hh),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(MM),2));
		
		return ret.toString();
			
	}

}
