/** 
 * @(#)ST64.java       1.0 05/07/25 *
 * 
 * Load Profile.
 * Copyright (c) 2004-2005 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */

package com.aimir.fep.meter.parser.a3rlnqTable;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.parser.lgrw3410Table.LPComparator;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;
import com.aimir.util.DateTimeUtil;

/**
 * @author Park YeonKyoung yeonkyoung@hanmail.net
 */
public class ST64 implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6728847244179134006L;

	private int totpulsecnt;

	private int NBR_BLKS_SET1;
	private int NBR_BLK_INTS_SET1;
	private int NBR_CHANS_SET1;
	private int INT_TIME_SET1;
	private int NBR_VALID_INT;
	private long ke;
	private int meterConstantScalel;
	private byte kescale;
	private ST62 st062 = null;

	private final int LEN_BLK_END_TIME = 5;
	private int LEN_SIMPLE_INTVAL_STAT;
	private int LEN_NBR_INTERVAL_RECORD;
	private int LEN_EXT_INTERVAL_STATUS;
	private int LEN_INTERVAL_DATA;

	private final int LP_BLOCK_SIZE = 16;
	private final int LP_CHAN_SIZE = 4;

	private byte[] data;
	private byte[] lpstarttime;
	private byte[] lpendtime;
	private LPData[] lpdata;
	private String blockendtime = null;
	private int emptylpcount;

	private Log logger = LogFactory.getLog(getClass());

	/**
	 * Constructor .
	 * <p>
	 * 
	 * @param data
	 *            - read data (header,crch,crcl)
	 */
	public ST64(byte[] data, int nbr_blks_set1, int nbr_blk_ints_set1,
			int nbr_chans_set1, int int_time_set1, int nbr_valid_int, long ke,
			byte kescale, ST62 st62, int meterConsScale) {
		this.data = data;
		this.NBR_BLKS_SET1 = nbr_blks_set1;
		this.NBR_BLK_INTS_SET1 = nbr_blk_ints_set1;
		this.NBR_CHANS_SET1 = nbr_chans_set1;
		this.INT_TIME_SET1 = int_time_set1;
		this.NBR_VALID_INT = nbr_valid_int - 1;
		this.ke = ke;
		this.meterConstantScalel = meterConsScale;
		this.kescale = kescale;
		this.st062 = st62;

		this.LEN_SIMPLE_INTVAL_STAT = (nbr_blk_ints_set1 + 7) / 8;
		this.LEN_NBR_INTERVAL_RECORD = nbr_blk_ints_set1
				* (1 + nbr_chans_set1 * 5 / 2);
		this.LEN_EXT_INTERVAL_STATUS = 1 + (nbr_chans_set1 / 2);
		this.LEN_INTERVAL_DATA = nbr_chans_set1 * 2;

		parseLP();
	}

	/**
	 * LP Scale : Active/Reactive Scale : 0x00,0x00
	 * 
	 * @return
	 */
	public byte[] parseLPScale() {
		byte[] scale = { kescale, kescale };
		return scale;
	}

	public LPData[] getLPData() {
		return this.lpdata;
	}

	/**
	 * Get LP Pulse Count.
	 * 
	 * @return
	 */
	public byte[] getPulseCount() throws Exception {

		int pcount = 0;
		byte[] pulsecount = new byte[2];

		pcount = this.totpulsecnt;
		pulsecount[0] = (byte) ((pcount >> 8) & 0xff);
		pulsecount[1] = (byte) (pcount & 0xff);

		return pulsecount;
	}

	public int getTotpulseCount() {
		return this.totpulsecnt;
	}

	@SuppressWarnings( { "static-access", "unchecked" })
	public void parseLP() {

		int fst_blk_val_cnt = this.NBR_BLK_INTS_SET1;// this.NBR_VALID_INT;
		int empty_blk_cnt = 0;

		int oneblocksize = LEN_BLK_END_TIME + this.LEN_SIMPLE_INTVAL_STAT
				+ this.LEN_NBR_INTERVAL_RECORD;

		int datalen = data.length;
		int block_count = data.length / oneblocksize;
		int lpcount = (block_count - 1) * this.NBR_BLK_INTS_SET1
				+ this.NBR_VALID_INT;

		ArrayList[] lists = new ArrayList[block_count];

		String endtime = new String();
		String status = new String();
		byte[] lp;
		int x = 0;

		try {
			if (fst_blk_val_cnt > 0) {
				byte[] first_block = new byte[this.LEN_BLK_END_TIME
						+ this.LEN_SIMPLE_INTVAL_STAT
						+ fst_blk_val_cnt
						* (this.LEN_EXT_INTERVAL_STATUS + this.LEN_INTERVAL_DATA)];
				first_block = DataFormat
						.select(
								data,
								0,
								this.LEN_BLK_END_TIME
										+ this.LEN_SIMPLE_INTVAL_STAT
										+ fst_blk_val_cnt
										* (this.LEN_EXT_INTERVAL_STATUS + this.LEN_INTERVAL_DATA));

				endtime = getQuaterYymmddhhmm(getYymmddhhmm(first_block, 0,
						this.LEN_BLK_END_TIME));

				status = getBlkStatus(DataFormat.select(first_block,
						this.LEN_BLK_END_TIME, this.LEN_SIMPLE_INTVAL_STAT),
						fst_blk_val_cnt);
				int statusCnt = 0;
				for (int i = 0; i < status.length(); i++) {
					if (status.substring(i, i + 1).equals("1"))
						statusCnt++;
				}

				// 마지막 시간 + 유효한 데이터 수
				int preDataSize = this.LEN_BLK_END_TIME
						+ this.LEN_SIMPLE_INTVAL_STAT;
				// 데이터 상태 값 + 검침값
				int dataSize = this.LEN_EXT_INTERVAL_STATUS
						+ this.LEN_INTERVAL_DATA;
				int interval = 15;
				String mm = getYymmddhhmm(first_block, 0, this.LEN_BLK_END_TIME)
						.substring(10, 12);
				// 마지막 시간이 lp주기 시간이 아닐경우 첫번째 데이터 누락
				if (Integer.parseInt(mm) % interval != 0) {
					preDataSize += dataSize;
					statusCnt--;
					fst_blk_val_cnt--;
				}

				lp = DataFormat.select(first_block, preDataSize,
						fst_blk_val_cnt * dataSize);

				lists[x] = new ArrayList();
				lists[x].add(0, new Integer(statusCnt));
				lists[x].add(1, endtime);
				lists[x].add(2, lp);
				lists[x].add(3, status);
				x++;
			} else {
				empty_blk_cnt++;
			}

			int offset = oneblocksize;

			for (int i = 1; i < block_count; i++) {

				byte[] blk = new byte[oneblocksize];
				blk = DataFormat.select(data, offset, oneblocksize);

				if (blk[0] == (byte) 0xFF) {
					empty_blk_cnt++;
				} else {

					endtime = Util.getQuaterYymmddhhmm(getYymmddhhmm(blk, 0,
							this.LEN_BLK_END_TIME), this.INT_TIME_SET1);

					status = getBlkStatus(
							DataFormat.select(blk, this.LEN_BLK_END_TIME,
									this.LEN_SIMPLE_INTVAL_STAT),
							this.NBR_BLK_INTS_SET1);

					lp = DataFormat
							.select(
									blk,
									this.LEN_BLK_END_TIME
											+ this.LEN_SIMPLE_INTVAL_STAT,
									this.NBR_BLK_INTS_SET1
											* (this.LEN_EXT_INTERVAL_STATUS + this.LEN_INTERVAL_DATA));

					lists[x] = new ArrayList();
					lists[x].add(0, new Integer(this.NBR_BLK_INTS_SET1));
					lists[x].add(1, endtime);
					lists[x].add(2, lp);
					lists[x].add(3, status);
					x++;
				}

				offset += oneblocksize;

			}

			ArrayList biglst = new ArrayList();
			int total_lp_cnt = 0;
			for (int i = 0; i < lists.length; i++) {
				LPData[] lst = parseLP(lists[i]);
				biglst.add(i, lst);
				total_lp_cnt += lst.length;
			}

			ArrayList tempList = new ArrayList();
			int idx = 0;
			for (int i = 0; i < biglst.size(); i++) {
				LPData[] temp = (LPData[]) biglst.get(i);
				int j = 0;
				while (j < temp.length) {
					tempList.add(j, temp[j++]);
				}
			}
			// lpDate로 소팅.
			Collections.sort(tempList, LPComparator.TIMESTAMP_ORDER);

			if (tempList != null && tempList.size() > 0) {
				LPData[] data = null;
				Object[] obj = tempList.toArray();
				data = new LPData[obj.length];
				for (int i = 0; i < obj.length; i++) {
					data[i] = (LPData) obj[i];
				}

				this.lpdata = new LPData[total_lp_cnt];
				this.lpdata = data;
			}

		} catch (Exception e) {
			logger.warn(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	protected ArrayList checkEmptyLP(ArrayList[] lists) throws Exception {

		int len = lists.length;

		ArrayList newlist = new ArrayList();
		ArrayList emptytime = new ArrayList();
		String prevtime = new String();

		try {
			int idx = 0;
			for (int i = 0; i < len; i++) {
				String nowtime = (String) lists[i].get(0);
				if (i == 0) {
					newlist.add(idx++, lists[i]);
				} else {

					int emptycnt = getEmptyCount(prevtime, nowtime);

					String time = prevtime;
					for (int j = 0; j < emptycnt; j++) {
						time = Util.addMinYymmdd(time, 15);
						ArrayList temp = new ArrayList();
						temp.add(0, time);
						temp.add(1, new byte[4]);
						temp.add(2, new byte[4]);
						temp.add(3, new byte[2]);
						newlist.add(idx++, temp);
					}
					newlist.add(idx++, lists[i]);
				}
				prevtime = nowtime;
			}

			return newlist;
		} catch (Exception e) {
			throw new Exception("check empty lp : " + e.getMessage());
		}

	}

	protected LPData[] orderby(LPData[] lists) {

		int size = lists.length;
		LPData[] newlists = new LPData[size];

		for (int i = 0; i < size; i++) {
			newlists[i] = lists[i];// lists[size-i-1];
		}
		return newlists;

	}

	protected LPData[] orderby(LPData[] lists, int emptycnt) {

		int size = lists.length - emptycnt;
		LPData[] newlists = new LPData[size];

		for (int i = 0; i < size; i++) {
			newlists[i] = lists[i];// lists[size-i-1];
		}
		return newlists;

	}

	protected LPData[] parseLP(ArrayList list) throws Exception {
		LPData[] interval = null;
		LPData[] ordered = null;
		try {
			Integer lpcount = (Integer) list.get(0);
			String endtime = (String) list.get(1);
			byte[] lps = (byte[]) list.get(2);
			int emptycnt = 0;

			interval = new LPData[lpcount.intValue()];
			ordered = new LPData[lpcount.intValue()];

			int ofs = 0;// lps.length -
						// (this.LEN_EXT_INTERVAL_STATUS+this.LEN_INTERVAL_DATA);
			int idx = 0;
			int timecnt = 0;

			Double[] ch = new Double[this.NBR_CHANS_SET1];
			Double[] v = new Double[this.NBR_CHANS_SET1];
			boolean doStart = false;

			for (int i = 0; i < lpcount.intValue(); i++) {

				ch = new Double[this.NBR_CHANS_SET1];
				v = new Double[this.NBR_CHANS_SET1];

				byte[] lpstat = DataFormat.select(lps, ofs,
						this.LEN_EXT_INTERVAL_STATUS);
				ofs += this.LEN_EXT_INTERVAL_STATUS;

				for (int k = 0; k < this.NBR_CHANS_SET1; k++) {
					ch[k] = new Double(getCh(lps, ofs, this.LEN_INTERVAL_DATA
							/ this.NBR_CHANS_SET1, k + 1));// 0));
					v[k] = new Double(getV(lps, ofs, this.LEN_INTERVAL_DATA
							/ this.NBR_CHANS_SET1, k + 1));// 0));
					ofs += this.LEN_INTERVAL_DATA / this.NBR_CHANS_SET1;
				}

				double ch1 = 0;
				double v1 = 0;
				double ch2 = 0;
				double v2 = 0;

				if (this.NBR_CHANS_SET1 >= 2) {
					ch1 = ch[0].doubleValue();
					v1 = v[0].doubleValue();
					ch2 = ch[1].doubleValue();
					v2 = v[1].doubleValue();
				} else if (this.NBR_CHANS_SET1 == 1) {
					ch1 = ch[0].doubleValue();
					v1 = v[0].doubleValue();
				}

				boolean isPowerfail = false;
				for (int n = 0; n < lpstat.length; n++) {
					if (lpstat[n] == (byte) 0xFF) {
						isPowerfail = true;
					} else {
						isPowerfail = false;
					}
				}

				int chn = 0;
				if (isPowerfail) {
					if (doStart) {
						String lptime = Util.addMinYymmdd(endtime,
								-this.INT_TIME_SET1 * timecnt);
						// log.debug(lptime +
						// " timecnt="+timecnt+" metering fail   ch=" + ch[0] +
						// " v=" +v[0] + " flag=" + Hex.getHexDump(lpstat));
						interval[idx] = new LPData();
						interval[idx].setDatetime(lptime);
						interval[idx].setCh(new Double[] { 0d });
						interval[idx].setV(new Double[] { 0d });
						interval[idx].setFlag(-1);
						interval[idx].setPF(new Double(0d));
						idx++;
						timecnt++;
						doStart = true;
					} else {
						emptycnt++;
					}
				} else {

					String lptime = Util.addMinYymmdd(endtime,
							-this.INT_TIME_SET1 * timecnt);
					// log.debug(lptime +
					// " timecnt="+timecnt+" metering correct   ch=" + ch[0] +
					// " v=" +v[0] + " flag=" + Hex.getHexDump(lpstat));
					interval[idx] = new LPData();
					interval[idx].setDatetime(lptime);
					interval[idx].setCh(ch);
					interval[idx].setV(v);
					interval[idx].setFlag(0);
					interval[idx].setPF(new Double(getPF(ch1, ch2)));
					// log.debug("["+meterId+"] lpdata=>"+interval[idx].toString());
					idx++;
					timecnt++;
					doStart = true;
				}
				// ofs = lps.length -
				// (i+2)*(this.LEN_EXT_INTERVAL_STATUS+this.LEN_INTERVAL_DATA);
				ch = null;
				v = null;
			}

			// log.debug("before orderby emptycnt=>"+emptycnt);
			ordered = orderby(interval, emptycnt);
		} catch (Exception e) {
			logger.warn("parselp err->", e);
		}
		// log.debug("after orderby");
		return ordered;

	}

	/*
	 * public void parseLP() {
	 * 
	 * int oneblocksize = LEN_BLK_END_TIME + this.LEN_SIMPLE_INTVAL_STAT +
	 * this.LEN_NBR_INTERVAL_RECORD;
	 * 
	 * int datalen = data.length; int blockcount = data.length/oneblocksize; int
	 * lpcount = (blockcount-1)*this.NBR_BLK_INTS_SET1+this.NBR_VALID_INT;
	 * this.totpulsecnt = lpcount;
	 * 
	 * byte[] buf = new byte[90*96*LP_BLOCK_SIZE];
	 * 
	 * int offset = 0;
	 * 
	 * try { for(int i = 0; i<blockcount; i++){ byte[] temp; if(i == 0){ temp =
	 * parseBlock(i * oneblocksize, oneblocksize, this.NBR_VALID_INT); }else{
	 * temp = parseBlock(i * oneblocksize, oneblocksize,
	 * this.NBR_BLK_INTS_SET1); }
	 * 
	 * System.arraycopy(temp,0,buf,offset,temp.length); offset += temp.length;
	 * 
	 * if(temp.length == 0){ totpulsecnt -= this.NBR_BLK_INTS_SET1; } }
	 * 
	 * this.lpdata = new byte[offset]; System.arraycopy(buf,0,lpdata,0,offset);
	 * this.totpulsecnt += this.emptylpcount;
	 * logger.debug("lpdata1 : \n"+Util.getHexString(lpdata)); orderbyLP();
	 * logger.debug("lpdata2 : \n"+Util.getHexString(lpdata)); } catch
	 * (Exception e) { logger.warn(e.getMessage()); }
	 * 
	 * 
	 * }
	 */

	/*
	 * public void orderbyLP() throws Exception{
	 * 
	 * byte[] temp = new byte[this.lpdata.length];
	 * System.arraycopy(lpdata,0,temp,0,lpdata.length); this.lpdata = new
	 * byte[temp.length];
	 * 
	 * for(int i = 0; i< this.totpulsecnt; i++){ byte[] block =
	 * DataFormat.select( temp,i*LP_BLOCK_SIZE,LP_BLOCK_SIZE);
	 * 
	 * 
	 * System.arraycopy(block,0,lpdata,temp.length-(i+1)*LP_BLOCK_SIZE,LP_BLOCK_SIZE
	 * );
	 * 
	 * if(i == 0){ this.lpendtime = new byte[6]; this.lpendtime =
	 * DataFormat.select(block,0,6); } if(i == this.totpulsecnt-1){
	 * this.lpstarttime = new byte[6]; this.lpstarttime =
	 * DataFormat.select(block,0,6); } }
	 * 
	 * }
	 */

	/*
	 * public byte[] orderby(byte[] notorder) throws Exception{
	 * 
	 * byte[] temp = new byte[notorder.length]; int pulsecnt =
	 * notorder.length/LP_BLOCK_SIZE;
	 * 
	 * for(int i = 0; i< pulsecnt ; i++){ byte[] block = DataFormat.select(
	 * notorder,i*LP_BLOCK_SIZE,LP_BLOCK_SIZE);
	 * 
	 * System.arraycopy(block,0,temp,notorder.length-(i+1)*LP_BLOCK_SIZE,
	 * LP_BLOCK_SIZE); }
	 * 
	 * return temp;
	 * 
	 * }
	 */

	/*
	 * public byte[] parseBlock(int offset, int len, int validcnt) throws
	 * Exception {
	 * 
	 * byte[] lpblock = new byte[validcnt*this.LP_BLOCK_SIZE];
	 * 
	 * byte[] temp = DataFormat.select(data,offset,len); boolean validlp;
	 * 
	 * String endtime = new String();
	 * 
	 * 
	 * if(Util.getNHexString(DataFormat.select(temp,0,6)).equals("ffffffffffff"))
	 * { logger.warn("Not Valid Block "); return new byte[0]; } try{ if(validcnt
	 * != this.NBR_BLK_INTS_SET1){ endtime =
	 * getQuaterYymmddhhmm(getYymmddhhmm(temp,0,LEN_BLK_END_TIME)); }else{
	 * endtime =
	 * Util.getQuaterYymmddhhmm(getYymmddhhmm(temp,0,LEN_BLK_END_TIME)); }
	 * }catch(Exception e){ logger.warn("Not Valid Block : "+e.getMessage());
	 * return new byte[0]; }
	 * 
	 * String starttime =
	 * Util.addMinYymmdd(endtime,-((validcnt-1)*INT_TIME_SET1));
	 * 
	 * int ofs = LEN_BLK_END_TIME; int blkstat = DataFormat.hex2dec(
	 * DataFormat.LSB2MSB( DataFormat.select(temp,ofs,LEN_SIMPLE_INTVAL_STAT)));
	 * ofs += LEN_SIMPLE_INTVAL_STAT; int pos = 0;
	 * 
	 * int emptycount = 0; if(blockendtime != null && validcnt > 0){ emptycount
	 * = getEmptyCount(endtime,blockendtime); this.emptylpcount += emptycount;
	 * lpblock = new byte[(validcnt+emptycount)*this.LP_BLOCK_SIZE]; }
	 * 
	 * for(int i = 0; i < validcnt; i++){
	 * 
	 * byte[] time =
	 * FEPUtil.strDate2Hex(Util.addMinYymmdd(starttime,(i*INT_TIME_SET1)),6);
	 * System.arraycopy(time,0,lpblock,pos,time.length); pos+= time.length;//6
	 * 
	 * byte[] stat = DataFormat.select(temp,ofs,LEN_EXT_INTERVAL_STATUS); ofs +=
	 * LEN_EXT_INTERVAL_STATUS;
	 * 
	 * if((blkstat & (0x01 << i)) > 0){ validlp = true; }else{ validlp = false;
	 * } if(validlp){ for(int j = 0; j < NBR_CHANS_SET1;j++){ byte[] ch =
	 * parseCh(temp,ofs,LEN_INTERVAL_DATA/NBR_CHANS_SET1); ofs +=
	 * LEN_INTERVAL_DATA/NBR_CHANS_SET1;
	 * System.arraycopy(ch,0,lpblock,pos,ch.length); pos += 4; } }else { for(int
	 * j = 0; j < NBR_CHANS_SET1;j++){ byte[] ch = new byte[4];//setting zero(0)
	 * ofs += LEN_INTERVAL_DATA/NBR_CHANS_SET1;
	 * System.arraycopy(ch,0,lpblock,pos,ch.length); pos += 4; } }
	 * 
	 * System.arraycopy(stat,0,lpblock,pos,LEN_EXT_INTERVAL_STATUS); pos += 2;
	 * 
	 * }
	 * 
	 * for(int i = 0;i < emptycount; i++){ byte[] time =
	 * FEPUtil.strDate2Hex(Util.addMinYymmdd(endtime,((i+1)*INT_TIME_SET1)),6);
	 * System.arraycopy(time,0,lpblock,pos,time.length); pos+= time.length;//6
	 * for(int j = 0; j < NBR_CHANS_SET1;j++){ byte[] ch = new byte[4];
	 * System.arraycopy(ch,0,lpblock,pos,ch.length); pos += 4; } byte[] stat =
	 * new byte[2]; System.arraycopy(stat,0,lpblock,pos,stat.length); pos +=
	 * stat.length; }
	 * 
	 * if(validcnt > 0){ this.blockendtime = starttime; }
	 * 
	 * return orderby(lpblock); }
	 */

	public String getEndTime() {
		try {
			return DataFormat.hexDateToStr(this.lpendtime);
		} catch (Exception e) {
			logger.warn(e.getMessage());
		}
		return null;
	}

	public String getStartTime() {
		try {
			return DataFormat.hexDateToStr(this.lpstarttime);
		} catch (Exception e) {
			logger.warn(e.getMessage());
		}
		return null;
	}

	public byte[] parseEndTime() {
		return this.lpendtime;
	}

	public byte[] parseStartTime() {
		return this.lpstarttime;
	}

	/**
	 * LP Channel data parsing.
	 * <p>
	 * 
	 * @param s
	 * @param len
	 * @return
	 */
	private double getCh(byte[] b, int start, int len, int ch) throws Exception {

		int val = DataFormat.hex2dec(DataFormat.LSB2MSB(DataFormat.select(b,
				start, len)));

		return (val * (ke * Math.pow(10, this.meterConstantScalel)) * (st062
				.getDIVISORst64(ch) / st062.getSCALARst64(ch)));

	}

	private double getV(byte[] b, int start, int len, int ch) throws Exception {

		int val = DataFormat.hex2dec(DataFormat.LSB2MSB(DataFormat.select(b,
				start, len)));

		return (val * (ke * Math.pow(10, this.meterConstantScalel)) * (st062
				.getDIVISORst64(ch) / st062.getSCALARst64(ch)))
				* 60 / this.INT_TIME_SET1;
	}

	public double getPF(double ch1, double ch2) throws Exception {

		double pf = (float) (ch1 / (Math.sqrt(Math.pow(ch1, 2)
				+ Math.pow(ch2, 2))));

		if (ch1 == 0.0 && ch2 == 0.0)
			pf = 1.0;
		/* Parsing Transform Results put Data Class */
		if (pf < 0.0 || pf > 1.0)
			throw new Exception("BILL PF DATA FORMAT ERROR : " + pf);
		return pf;
	}

	private byte[] parseCh(byte[] b, int start, int len) throws Exception {

		byte[] ch = new byte[4];
		int val = DataFormat.hex2dec(DataFormat.LSB2MSB(DataFormat.select(b,
				start, len)));

		val = (int) (val * ke * 60 / 15);

		ch[0] = (byte) (val >> 24);
		ch[1] = (byte) (val >> 16);
		ch[2] = (byte) (val >> 8);
		ch[3] = (byte) val;

		return ch;

	}

	private String getYymmddhhmm(byte[] b, int offset, int len)
			throws Exception {

		int blen = b.length;
		if (blen - offset < 5)
			throw new Exception("YYMMDDHHMMSS FORMAT ERROR : "
					+ (blen - offset));
		if (len != 5)
			throw new Exception("YYMMDDHHMMSS LEN ERROR : " + len);

		int idx = offset;

		int yy = DataFormat.hex2unsigned8(b[idx++]);
		int mm = DataFormat.hex2unsigned8(b[idx++]);
		int dd = DataFormat.hex2unsigned8(b[idx++]);
		int hh = DataFormat.hex2unsigned8(b[idx++]);
		int MM = DataFormat.hex2unsigned8(b[idx++]);

		StringBuffer ret = new StringBuffer();

		int currcen = (Integer.parseInt(DateTimeUtil
				.getCurrentDateTimeByFormat("yyyy")) / 100) * 100;

		int year = yy;
		if (year != 0) {
			year = yy + currcen;
		}

		ret.append(Util.frontAppendNStr('0', Integer.toString(year), 4));
		ret.append(Util.frontAppendNStr('0', Integer.toString(mm), 2));
		ret.append(Util.frontAppendNStr('0', Integer.toString(dd), 2));
		ret.append(Util.frontAppendNStr('0', Integer.toString(hh), 2));
		ret.append(Util.frontAppendNStr('0', Integer.toString(MM), 2));

		return ret.toString();

	}

	private String getQuaterYymmddhhmm(String yymmddHHMM) throws Exception {

		String dateString = "";

		try {
			if (yymmddHHMM == null || yymmddHHMM.equals(""))
				throw new Exception("INPUT NULL");
			if (yymmddHHMM.length() < 12)
				throw new Exception("Length Error");

			int yy = Integer.parseInt(yymmddHHMM.substring(0, 4));
			int mm = Integer.parseInt(yymmddHHMM.substring(4, 6));
			int day = Integer.parseInt(yymmddHHMM.substring(6, 8));
			int HH = Integer.parseInt(yymmddHHMM.substring(8, 10));
			int MM = Integer.parseInt(yymmddHHMM.substring(10, 12));

			if (((MM != 0) && (MM % this.INT_TIME_SET1 != 0))) {
				MM = (MM) - (MM) % this.INT_TIME_SET1;
			}

			dateString = "" + yy + (mm < 10 ? "0" + mm : "" + mm)
					+ (day < 10 ? "0" + day : "" + day)
					+ (HH < 10 ? "0" + HH : "" + HH)
					+ (MM < 10 ? "0" + MM : "" + MM);

		} catch (NumberFormatException e) {
			throw new Exception("Util.getQuaterYymmdd() : " + e.getMessage());
		}
		return dateString;

	}

	private int getEmptyCount(String time1, String time2) {

		int ret = 0;
		long sec = 0;
		try {
			sec = Util.getDuration(time1, time2);
		} catch (Exception e) {
			logger.warn(e.getMessage());
		}
		ret = (int) (sec / this.INT_TIME_SET1) - 1;
		if (ret < 0)
			ret = 0;
		return ret;
	}

	private String getBlkStatus(byte[] b, int blkcnt) {

		String str = new String();
		for (int i = 0; i < b.length; i++) {
			int val = DataFormat.hex2unsigned8(b[i]);
			StringBuffer temp = new StringBuffer(Util.frontAppendNStr('0',
					Integer.toBinaryString(val), 8));
			str += temp.reverse();
		}

		return str.substring(0, blkcnt);
	}

}
