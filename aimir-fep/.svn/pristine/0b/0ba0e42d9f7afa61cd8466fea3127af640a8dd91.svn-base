/*
 * Created on 2004. 12. 27.
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.aimir.fep.meter.parser.zmdTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;

/**
 * @author Park YeonKyoung yeonkyoung@hanmail.net
 *
 * LandisGyr+ ZMD4 meter Class. <p> 
 * Include BILL Data . <p>
 */
public class Class13 {
	
	public final static int OFS_RESETCOUNT = 33;	// offset lp count.
	public final static int OFS_RESETTIME = 20;
	
	public final static int OFS_RATE1_ACTIVE = 38;
	public final static int OFS_RATE2_ACTIVE = 47;
	public final static int OFS_RATE3_ACTIVE = 56;
	
	public final static int OFS_RATE1_REACTIVE = 65;
	public final static int OFS_RATE2_REACTIVE = 74;
	public final static int OFS_RATE3_REACTIVE = 83;
	
	public final static int OFS_RATE1_ACTIVE_MAX = 92;
	public final static int OFS_RATE1_ACTIVE_MAXTIME = 98;
	
	public final static int OFS_RATE2_ACTIVE_MAX = 111;
	public final static int OFS_RATE2_ACTIVE_MAXTIME = 117;
	
	private int OFS_RATE3_ACTIVE_MAX     = 9;
	private int OFS_RATE3_ACTIVE_MAXTIME = 15;
	
	private int OFS_RATE1_REACTIVE_MAX     = 28;
	private int OFS_RATE1_REACTIVE_MAXTIME = 34;
	
	private int OFS_RATE2_REACTIVE_MAX = 47;
	private int OFS_RATE2_REACTIVE_MAXTIME = 53;
	
	private int OFS_RATE3_REACTIVE_MAX = 66;
	private int OFS_RATE3_REACTIVE_MAXTIME = 72;
	
	
	public final static int LEN_RESETCOUNT = 4;	// length lp count.
	public final static int LEN_RESETTIME = 8;
	
	public final static int LEN_RATE1_ACTIVE = 8;
	public final static int LEN_RATE2_ACTIVE = 8;
	public final static int LEN_RATE3_ACTIVE = 8;
	
	public final static int LEN_RATE1_REACTIVE = 8;
	public final static int LEN_RATE2_REACTIVE = 8;
	public final static int LEN_RATE3_REACTIVE = 8;
	
	public final static int LEN_RATE1_ACTIVE_MAX = 4;
	public final static int LEN_RATE1_ACTIVE_MAXTIME = 8;
	
	public final static int LEN_RATE2_ACTIVE_MAX = 4;
	public final static int LEN_RATE2_ACTIVE_MAXTIME = 8;
	
	public final static int LEN_RATE3_ACTIVE_MAX = 4;
	public final static int LEN_RATE3_ACTIVE_MAXTIME = 8;
	
	public final static int LEN_RATE1_REACTIVE_MAX = 4;
	public final static int LEN_RATE1_REACTIVE_MAXTIME = 8;
	
	public final static int LEN_RATE2_REACTIVE_MAX = 4;
	public final static int LEN_RATE2_REACTIVE_MAXTIME = 8;
	
	public final static int LEN_RATE3_REACTIVE_MAX = 4;
	public final static int LEN_RATE3_REACTIVE_MAXTIME = 8;
	
	private byte[] data;

    private static Log logger = LogFactory.getLog(Class13.class);
	
	/**
	 * Constructor 
	 * 
	 * @param data - read data
	 */
	public Class13(byte[] data) {
		this.data = data;
		int length =  findnextoffset(data);
		
		this.OFS_RATE3_ACTIVE_MAX     = 9+length;
		this.OFS_RATE3_ACTIVE_MAXTIME = 15+length;
	
		this.OFS_RATE1_REACTIVE_MAX     = 28+length;
		this.OFS_RATE1_REACTIVE_MAXTIME = 34+length;
	
		this.OFS_RATE2_REACTIVE_MAX = 47+length;
		this.OFS_RATE2_REACTIVE_MAXTIME = 53+length;
	
		this.OFS_RATE3_REACTIVE_MAX = 66+length;
		this.OFS_RATE3_REACTIVE_MAXTIME = 72+length;
		
	}
	
	
	private int findnextoffset(byte[] data){
		
		int ret = -1;
		
		for(int i = 1; i < data.length; i++){
			if(data[i] == 0x7e){
				ret = i+1;
				break;
			}

		}
		return ret;
	}
	
	
	/**
	 * Get LP Count. <p>
	 * @return
	 */
	public byte parseResetCount(){
		return data[OFS_RESETCOUNT];
	}
	
	
	public byte[] parseResetTime(){
		
		byte[] resettime = new byte[7];
		int idx = OFS_RESETTIME;

		try {

			resettime[0] = data[idx++];
			resettime[1] = data[idx++];
			resettime[2] = data[idx++];
			resettime[3] = data[idx++];
			idx++;
			resettime[4] = data[idx++];
			resettime[5] = data[idx++];

		}catch(Exception e){
			logger.warn(e);
		}
		return resettime;
	}
	
	public byte[] parseTime(byte[] tarray){
		
		byte[] time = new byte[6];
		int idx = 0;

		try {

			time[0] = tarray[idx++];
			time[1] = tarray[idx++];
			time[2] = tarray[idx++];
			time[3] = tarray[idx++];
			idx++;
			time[4] = tarray[idx++];
			time[5] = tarray[idx++];

		}catch(Exception e){
			logger.warn(e);
		}
		return time;
	}
	
	
	public byte[] parseTAE(){
		
		byte[] tae = new byte[8];//TODO Later Sum(r1,r2,r3)
		return tae;
	}
	
	public byte[] parseTRE(){
		
		byte[] tre = new byte[8];//TODO Later Sum(r1,r2,r3)
		return tre;
	}

	/**
	 * [8]  Get Active+ Energy Rate1.
	 * @return
	 */
	public byte[] parseR1AE() throws Exception {
		return DataFormat.select(data,OFS_RATE1_ACTIVE,LEN_RATE1_ACTIVE);
	}	
	
	/**
	 * [8]	Get Active+ Energy Rate2.
	 * @return
	 */
	public byte[] parseR2AE() throws Exception {
		return DataFormat.select(data,OFS_RATE2_ACTIVE,LEN_RATE2_ACTIVE);
	}
	
	/**
	 * [8]	Get Active+ Energy Rate3.
	 * @return
	 */
	public byte[] parseR3AE() throws Exception {
		return DataFormat.select(data,OFS_RATE3_ACTIVE,LEN_RATE2_ACTIVE);
	}
	
	/**
	 * [8]  Get Reactive+ Energy Rate1.
	 * @return
	 */
	public byte[] parseR1RE() throws Exception {
		return DataFormat.select(data,OFS_RATE1_REACTIVE,LEN_RATE1_REACTIVE);
	}
	
	/**
	 * [8]	Get Reactive+ Energy Rate2.
	 * @return
	 */
	public byte[] parseR2RE() throws Exception {		
		return DataFormat.select(data,OFS_RATE2_REACTIVE,LEN_RATE2_REACTIVE);
	}
	
	/**
	 * [8]  Get Reactive+ Energy Rate3.
	 * @return
	 */
	public byte[] parseR3RE() throws Exception {
		return DataFormat.select(data,OFS_RATE3_REACTIVE,LEN_RATE3_REACTIVE);
	}	
	
	/**
	 * [4]  Active Power+ Max. Demand Rate1.
	 * @return
	 */
	public byte[] parseR1AM() throws Exception {
		return DataFormat.select(data,OFS_RATE1_ACTIVE_MAX,LEN_RATE1_ACTIVE_MAX);
	}
		
	/**
	 * [8]  Active Power+ Max. Demand Date Time Rate1.
	 * @return
	 */
	public byte[] parseR1AMT() throws Exception {
		return parseTime(
			DataFormat.select(data,OFS_RATE1_ACTIVE_MAXTIME,LEN_RATE1_ACTIVE_MAXTIME));
	}
	
	/**
	 * [4]  Active Power+ Max. Demand Rate2.
	 * @return
	 */
	public byte[] parseR2AM() throws Exception {
		return DataFormat.select(data,OFS_RATE2_ACTIVE_MAX,LEN_RATE2_ACTIVE_MAX);
	}
	
	/**
	 * [8]  Active Power+ Max. Demand Date Time Rate2.
	 * @return
	 */
	public byte[] parseR2AMT() throws Exception {
		return parseTime(
			DataFormat.select(data,OFS_RATE2_ACTIVE_MAXTIME,LEN_RATE2_ACTIVE_MAXTIME));
	}
	
	/**
	 * [4]  Active Power+ Max. Demand Rate3.
	 * @return
	 */
	public byte[] parseR3AM() throws Exception {
		return DataFormat.select(data,OFS_RATE3_ACTIVE_MAX,LEN_RATE3_ACTIVE_MAX);
	}
	
	/**
	 * [8]  Active Power+ Max. Demand Date Time Rate3.
	 * @return
	 */
	public byte[] parseR3AMT() throws Exception {
		return parseTime(
			DataFormat.select(data,OFS_RATE3_ACTIVE_MAXTIME,LEN_RATE3_ACTIVE_MAXTIME));
	}
	
	/**
	 * [4]  Reactive Power+ Max. Demand Rate1.
	 * @return
	 */
	public byte[] parseR1RM() throws Exception {
		return DataFormat.select(data,OFS_RATE1_REACTIVE_MAX,LEN_RATE1_REACTIVE_MAX);
	}
	
	/**
	 * [8]  Reactive Power+ Max. Demand Date Time Rate1.
	 * @return
	 */
	public byte[] parseR1RMT() throws Exception {
		return parseTime(
			DataFormat.select(data,OFS_RATE1_REACTIVE_MAXTIME,LEN_RATE1_REACTIVE_MAXTIME));
	}
	
	/**
	 * [4]  Reactive Power+ Max. Demand Rate2.
	 * @return
	 */
	public byte[] parseR2RM() throws Exception {
		return DataFormat.select(data,OFS_RATE2_REACTIVE_MAX,LEN_RATE2_REACTIVE_MAX);
	}
	
	/**
	 * [8]  Reactive Power+ Max. Demand Date Time Rate2.
	 * @return
	 */
	public byte[] parseR2RMT() throws Exception {
		return parseTime(
			DataFormat.select(data,OFS_RATE2_REACTIVE_MAXTIME,LEN_RATE2_REACTIVE_MAXTIME));
	}
	
	/**
	 * [4]  Reactive Power+ Max. Demand Rate3.
	 * @return
	 */
	public byte[] parseR3RM() throws Exception {
		return parseTime(
			DataFormat.select(data,OFS_RATE3_REACTIVE_MAX,LEN_RATE3_REACTIVE_MAX));
	}
	
	/**
	 * [8]  Reactive Power+ Max. Demand Date Time Rate3.
	 * @return
	 */
	public byte[] parseR3RMT() throws Exception {
		return parseTime(
			DataFormat.select(data,OFS_RATE3_REACTIVE_MAXTIME,LEN_RATE3_REACTIVE_MAXTIME));
	}
	
	
	public byte[] parseR1AC(){
		byte[] arr = new byte[8];
		return arr;
	}
	
	public byte[] parseR2AC(){
		byte[] arr = new byte[8];
		return arr;
	}
	
	public byte[] parseR3AC(){
		byte[] arr = new byte[8];
		return arr;
	}
	
	public byte[] parseR1RC(){
		byte[] arr = new byte[8];
		return arr;
	}
	
	public byte[] parseR2RC(){
		byte[] arr = new byte[8];
		return arr;
	}
	
	public byte[] parseR3RC(){
		byte[] arr = new byte[8];
		return arr;
	}	

}
