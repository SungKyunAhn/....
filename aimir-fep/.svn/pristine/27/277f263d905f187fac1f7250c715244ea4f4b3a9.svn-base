/** 
 * @(#)Class7_2.java       1.0 04/10/19 *
 * Copyright (c) 2004-2005 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
package com.aimir.fep.meter.parser.sl7000Table;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;

/**
 * @author Park YeonKyoung yeonkyoung@hanmail.net
 *
 * ACRATIS SL7000 meter Class. <p> * 
 * Include LP Data . <p>
 * 
 * -----------------------------------
 * LP Sequence Tree Configuration.
 * -----------------------------------
 * 
 * sequence of
 * {
 * 		sequence
 * 		{
 * 			choice
 * 			{
 * 				sequence
 * 				{
 * 					start of interval : UTC
 * 					status : bitstring
 * 				}
 * 				null-data
 * 			}
 * 			choice
 * 			{
 * 				sequence
 * 				{
 * 					end of interval : UTC
 * 					status : bitstring
 * 				}
 * 				null-data
 * 			}
 * 			choice
 * 			{
 * 				sequence
 * 				{
 * 					time 1 : UTC
 * 					status : bitstring
 * 				}
 * 				null-data
 * 			}
 * 			choice
 * 			{
 * 				sequence
 * 				{
 * 					time 2 : UTC
 * 					status : bitstring
 * 				}
 * 				null-data
 * 			}
 * 			choice
 * 			{
 * 				unsigned 16 (ch 1)
 * 				integer 16
 * 			}
 * 			choice
 * 			{
 * 			 	unsigned 16 (ch 1)
 * 				integer 16
 * 			}
 * 		}
 * } 
 * 
 * start of interval : used to record new date or 
 * the new time when the interval startted unusually.
 * 
 * 
 * 
 */
public class Class7_2 {
	/* Data Blocl Sequence Block Code */
	private final byte   SEQ_CODE     = 0x02;
	private final byte[] INTV_CODE    = {0x09,0x0c};
	private final byte[] STATUS_CODE  = {0x04,0x06};
	private final byte   DATANOT_CODE = 0x00;
	private final byte   CHANNEL_CODE = 0x10;
	
	private byte[] data;
	private byte[] starttime;
	private byte[] lasttime;
	private int LP_CHANNEL_COUNT = 2;
	private int LP_INTERVAL = 15;

    private static Log log = LogFactory.getLog(Class7_2.class);
	
	private int LP_BLOCK_SIZE = 16; // lp 0ne block size 16 byte
	private int LP_CHAN_SIZE  = 4; //lp channel size  4byte

	/**
	 * Contructor. <p>
	 * @param data - read data. <p>
	 * @param nchannel - lp channel's count. <p>
	 */
	public Class7_2(byte[] data, int nchannel){
		this.data = data;
		this.LP_CHANNEL_COUNT = nchannel;
	}


	/**
	 * Contructor. <p>
	 * @param data - read data. <p>
	 */
	public Class7_2(byte[] data){
		this.data = data;
		//log.debug("class7_2 data\n"+Util.getHexString(data));
	}
	
	
	public int parseLPCount() {
		
		if(data.length > 22){
			return DataFormat.hex2unsigned8(data[19]);
		}else{
			return 0;
		}
	}
	
	public int parseLPTotSize(){
		
		if(data.length > 22){
			return DataFormat.hex2unsigned8(data[19])*LP_BLOCK_SIZE;//16 is lp one block size
		}else{
			return 0;
		}
	}
	
	
	/**
	 * Get All LP Data - (parsed)
	 * @return
	 */
	public byte[] parseLPData() {
		

		byte[] lp = new byte[DataFormat.hex2unsigned8(data[19])*LP_BLOCK_SIZE];

		Vector pulses   = new Vector();
		
		try {
		
			pulses = getPulses(cutHeaderTail());
		
			Enumeration en  = pulses.elements();	
			lasttime = new byte[6];
			int cnt = 0;
			int offset = 0;
			while(en.hasMoreElements()){

				byte[] lptime = new byte[6];

				//char   powerflag = 0x00;
				byte   eventflag = 0x00;
				byte   lpstat    = 0x00;
			
				byte[] temp = (byte[])en.nextElement();

				if(checkStartInterval(temp))
				{
					lptime = getStartInterval(temp);
					lpstat = getStartIntervalStat(temp);
					lasttime = lptime;				
				}
				else if(checkEndInterval(temp))
				{
					lptime = getEndInterval(temp);
					lpstat = getEndIntervalStat(temp);
					if( lptime[0] == (byte)0xff && lptime[1] == (byte)0xff && 
						lptime[2] == (byte)0xff && lptime[3] == (byte)0xff)
					{
						lptime[0] = lasttime[0];
						lptime[1] = lasttime[1];
						lptime[2] = lasttime[2];
						lptime[3] = lasttime[3];
					}
					lasttime = lptime;	
				}
				else if(checkTime1(temp))
				{
					lptime = getTime1(temp);
					lpstat = getTime1Stat(temp);
					if( lptime[0] == (byte)0xff && lptime[1] == (byte)0xff && 
						lptime[2] == (byte)0xff && lptime[3] == (byte)0xff)
					{
						lptime[0] = lasttime[0];
						lptime[1] = lasttime[1];
						lptime[2] = lasttime[2];
						lptime[3] = lasttime[3];
					}
					lasttime = lptime;	
				}
				else if(checkTime2(temp))
				{
					lptime = getTime2(temp);
					lpstat = getTime2Stat(temp);
					if( lptime[0] == (byte)0xff && lptime[1] == (byte)0xff && 
						lptime[2] == (byte)0xff && lptime[3] == (byte)0xff)
					{
						lptime[0] = lasttime[0];
						lptime[1] = lasttime[1];
						lptime[2] = lasttime[2];
						lptime[3] = lasttime[3];
					}
					lasttime = lptime;	
				}
				else
				{
					lptime = DataFormat.strDate2Hex(
								Util.addMinYymmdd(DataFormat.getYymmddhhmm(lasttime),LP_INTERVAL),6);

					lasttime = lptime;
				}
			
				if(cnt == 0)
					starttime = lasttime;

				//powerflag = checkPowerfail(lpstat);
				eventflag = checkEvent(lpstat);
			
				System.arraycopy(lptime,0,lp,offset,6);
				offset += 6;
				System.arraycopy(getChannel(temp),0,lp,offset,8);
				offset += 8;
				lp[offset++] = 0x00;		//event code first byte
				lp[offset++] = eventflag;	//event code second byte
				cnt++;

			}
  
		} catch(Exception e){
			log.debug(e);
		}

		return lp;
	}

	/*
	private char checkPowerfail(char c){
		if((c & 0x40) != 0)
			return 'P';
		else
			return 'N';
	}
	*/

	public byte[] parseLPStartTime(){
		return this.starttime;
	}
	
	public byte[] parseLPEndTime(){
		return this.lasttime;
	}

	private byte checkEvent(byte b) {
		return b;//TODO Event Flag define
	}


	private boolean checkStartInterval(byte[] b) {
		return checkChoice(b,1);
	}	
	
	
	private boolean checkEndInterval(byte[] b){
		return checkChoice(b,2);
	}
	
	
	private boolean checkTime1(byte[] b){
		return checkChoice(b,3);
	}
	
	
	private boolean checkTime2(byte[] b){
		return checkChoice(b,4);
	}


	private boolean checkChoice(byte[] b, int choice_num){
		int i = 0;
		int idx = 2;	// because except 0x02 0x06
		
		try{
			while(i < LP_CHANNEL_COUNT+4){
		
				if(idx >= b.length)
					break;
				
				if(b[idx] == SEQ_CODE){
					if(i+1 == choice_num){
						//log.debug("FIND SEQUENCE : "+Integer.toString(choice_num));
						return true;
					}
					i++;
					idx = idx+19;	//add sequence+time sequence length

				} else if(b[idx] == DATANOT_CODE){
					i++;
					idx++;
				} else if(b[idx] == CHANNEL_CODE){
					i++;
					idx = idx+3;
				}

			}
		}catch(Exception e){
			log.warn(e);
		}
		
		return false;
	}


	
  	private byte[] getChannel(byte[] b){
		
		int i = 0;
		int j = 0;
		int idx = 2;	// except 0x02 0x06 
		byte[] data = new byte[LP_CHANNEL_COUNT*LP_CHAN_SIZE];
		
		try{
			while(i < LP_CHANNEL_COUNT+4){
		
				if(idx >= b.length)
					break;
				
				if(b[idx] == SEQ_CODE){
					i++;
					idx = idx+19;	//add sequence+time sequence length

				} else if(b[idx] == DATANOT_CODE){
					i++;
					idx++;
				} else if(b[idx] == CHANNEL_CODE){
					data[j++] = 0x00;
					data[j++] = 0x00;
					data[j++] = b[idx+1];
					data[j++] = b[idx+2];
					i++;
					idx = idx+3;
				}
			}
		
		}catch(Exception e){
			log.warn(e);
		}

		return data;
	}
	


	/**
	 * 
	 * @param sb
	 * @param choice_num
	 * @param kind - time or status or(time : 1 status  :2)
	 * @return
	 */
	private byte[] getChoice(byte[] b, int choice_num, int kind){
		
		int i = 0;
		int idx = 2;	// except 0x02 0x06
		byte[] data = new byte[6];
		
		try{
			while(i < LP_CHANNEL_COUNT+4){
		
				if(idx >= b.length)
					break;
				
				if(b[idx] == SEQ_CODE){
					if(i+1 == choice_num){	// compare choice number 
						if(kind == 1){
							int j = idx+4; 
							data[0] = b[j++];
							data[1] = b[j++];
							data[2] = b[j++];
							data[3] = b[j++];
							j++;// except mid field.
							data[4] = b[j++];
							data[5] = b[j++];
							if(data[4] == (byte)0xff){
								data[4] = 0x00;
								if(data[5] == (byte)0xff)
									data[5] = 0x0f;		//if hour/min  ff ff, then start 00:15			
							}
						}else if(kind == 2){
							int j = idx+18;
							data[0] = b[j];						
						}
						//log.debug("FIND SEQUENCE : "+Integer.toString(choice_num));
					
						return data;
					}
					i++;
					idx = idx+19;	//add sequence+time sequence length

				} else if(b[idx] == DATANOT_CODE){
					i++;
					idx++;
				} else if(b[idx] == CHANNEL_CODE){
					i++;
					idx = idx+3;
				}

			}
		}catch(Exception e){
			log.warn(e);
		}
		
		return data;
	}

	
	private byte[] getStartInterval(byte[] b) {
		return getChoice(b, 1, 1);
	}
		
	private byte[] getEndInterval(byte[] b){
		return getChoice(b, 2, 1);
	}
		
	private byte[] getTime1(byte[] b){
		return getChoice(b, 3, 1);
	}
		
	private byte[] getTime2(byte[] b){
		return getChoice(b, 4, 1);
	}
	
	
	private byte getStartIntervalStat(byte[] b) {
		byte[] stat = {0x00};
		stat = getChoice(b, 1, 2);
		return stat[0];
	}
	
	private byte getEndIntervalStat(byte[] b){
		byte[] stat = {0x00};
		stat = getChoice(b, 2, 2);
		return stat[0];
	}
	
	
	private byte getTime1Stat(byte[] b){
		byte[] stat = {0x00};
		stat = getChoice(b, 3, 2);
		return stat[0];
	}
	
	
	private byte getTime2Stat(byte[] b){
		byte[] stat = {0x00};
		stat = getChoice(b, 4, 2);
		return stat[0];
	}

	
	/**
	 * 
	 * @param sb
	 * @return
	 */
	private Vector getPulses(byte[] b){		
		
		Vector npulses  = new Vector();		
		byte[] sequence = new byte[2];	
		
		sequence[0] = SEQ_CODE;
		sequence[1] = (byte)((LP_CHANNEL_COUNT+4)&0xff);
		
		int i  = 0;
		boolean flag = true;

		try {
			while(flag){
				int fidx = findSequence(b,sequence,i+1);	//i+1 next buffer ..
				if(fidx == -1)break;
				byte[] pulse = new byte[fidx-i];
				System.arraycopy(b,i,pulse,0,fidx-i);
				npulses.addElement(pulse);
				i = fidx;
			}
			byte[] pulse = new byte[b.length-i];
			System.arraycopy(b,i,pulse,0,b.length-i);
			npulses.addElement(pulse);		
		}catch(Exception e){
			log.warn(e);
		}

		return npulses;
	}
	
	/**
	 * 
	 * @return
	 */
	private boolean checkPulseFlag(){
		return true;
	}
	
	
	/**
	 * Get LP Data 
	 * Cut Header and FCS and HDLC Frame. 
	 * @return
	 */
	private byte[] cutHeaderTail(){
		
		/*
		 * previous frame format 0x7e 0xa0 --> false
		 * previous frame format 0x7e 0xa8 --> true
		 */
		boolean prev_frameformat_more = false;
		boolean frameformat_more      = false;
		
		/* find first LP Data block */
		byte[] sequence = new byte[2];	
		
		sequence[0] = SEQ_CODE;
		sequence[1] = (byte)(LP_CHANNEL_COUNT+4);
		
		int len = 0;
		int totlen = 0;
		byte[] temp = new byte[data.length];
		byte[] dat = null;
		
		try{
			
			int i = 0;
			int offset = 0;
			while(totlen < data.length){//last data no need...

				len = DataFormat.hex2unsigned8(data[i+2])+2;
				
				if(len+totlen >= data.length)
					break;
			
				if(data[i+1] == (byte)0xa0)
					frameformat_more = false;
				else if(data[i+1] == (byte)0xa8)
					frameformat_more = true;

				totlen += len;
				
				if(prev_frameformat_more){ 
					System.arraycopy(data,i+11,temp,offset,totlen-3-(i+11));
					offset += totlen-3-(i+11);
				}else{
					int fidx = findSequence(data,sequence,i);
					System.arraycopy(data,fidx,temp,offset,totlen-3-fidx);
					offset += totlen-3-fidx;
				}			
				i = totlen;
				prev_frameformat_more = frameformat_more;

			}
			
			dat = new byte[offset];
			System.arraycopy(temp,0,dat,0,offset);
			
		}catch(Exception e){
			log.debug(e);
		}
		
		return dat;
	}


	/**
	 * Get index find sequence.
	 * @param sb - data buffer
	 * @param startidx -  start index
	 * @return - sequence index.
	 */
	private int findSequence(byte[] b, byte[] sequence, int startidx){
		
		byte[] substr = new byte[sequence.length];
		
		try {
			if(sequence == null || sequence.length < 1)
				return -1;
			if((b == null) || (b.length < sequence.length) ) {
				return -1;
			}

			for(int i = startidx; i < b.length-sequence.length+1; i++) {
				substr = DataFormat.select(b,i,sequence.length);
				if(Arrays.equals(sequence,substr)) {
					return i;
				}
			}
		} catch (Exception e) {
			log.debug(e);
		}

		return -1;		

	}

}