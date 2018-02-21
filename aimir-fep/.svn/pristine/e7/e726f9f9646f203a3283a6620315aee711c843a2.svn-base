package com.aimir.fep.meter.parser.a3rlnqTable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.Instrument;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.Util;
import com.aimir.util.DateTimeUtil;

import java.util.Arrays;

/**
 * @author Jihoon KIM 	jihoon@nuritelecom.com
 */
public class Instruments implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5649917760920957697L;

	private int NBR_BLKS_SET1;
	private int NBR_BLK_INTS_SET1;
	private int NBR_CHANS_SET1;
	private int INT_TIME_SET1;
	private int NBR_VALID_INT_SET1;

	private int NBR_BLKS_SET2;
	private int NBR_BLK_INTS_SET2;
	private int NBR_CHANS_SET2;
	private int INT_TIME_SET2;
	private int NBR_VALID_INT_SET2;

	public static final int LEN_BLK_END_TIME = 5;

	private int LEN_SIMPLE_INTVAL_STAT_SET1;
	private int LEN_NBR_INTERVAL_RECORD_SET1;
	private int LEN_EXT_INTERVAL_STATUS_SET1;
	private int LEN_INTERVAL_DATA_SET1;

	private int LEN_SIMPLE_INTVAL_STAT_SET2;
	private int LEN_NBR_INTERVAL_RECORD_SET2;
	private int LEN_EXT_INTERVAL_STATUS_SET2;
	private int LEN_INTERVAL_DATA_SET2;

	private byte[] dataset1;
	private byte[] dataset2;

	private int[] set1divisors;
	private int[] set2divisors;

	private ArrayList instSet1;
	private ArrayList instSet2;

	private Instrument[] instData;

	private byte[] inststarttime;
	private byte[] instendtime;
	private String blockendtime = null;
	private int totpulsecnt;
	private double ins_scale;
	// private String melement;
	private Log logger = LogFactory.getLog(getClass());

	/**
	 * Constructor .
	 * <p>
	 * 
	 * @param data
	 *            - read data (header,crch,crcl)
	 */
	public Instruments(byte[] dataset1, 
						int nbr_blks_set1,
						int nbr_blk_ints_set1, 
						int nbr_chans_set1, 
						int int_time_set1,
						int nbr_valid_int_set1, 
						byte[] dataset2, 
						int nbr_blks_set2,
						int nbr_blk_ints_set2, 
						int nbr_chans_set2, 
						int int_time_set2,
						int nbr_valid_int_set2, 
						int[] set1divisors, 
						int[] set2divisors,
						double ins_scale) {

		this.dataset1 = dataset1;
		System.out.println(Hex.getHexDump(dataset1));

		this.NBR_BLKS_SET1 = nbr_blks_set1;
		this.NBR_BLK_INTS_SET1 = nbr_blk_ints_set1;
		this.NBR_CHANS_SET1 = nbr_chans_set1;
		this.INT_TIME_SET1 = int_time_set1;
		this.NBR_VALID_INT_SET1 = nbr_valid_int_set1 - 1;

		this.LEN_SIMPLE_INTVAL_STAT_SET1 = (nbr_blk_ints_set1 + 7) / 8;
		this.LEN_NBR_INTERVAL_RECORD_SET1 = nbr_blk_ints_set1
				* (1 + nbr_chans_set1 * 5 / 2);
		this.LEN_EXT_INTERVAL_STATUS_SET1 = 1 + (nbr_chans_set1 / 2);
		this.LEN_INTERVAL_DATA_SET1 = nbr_chans_set1 * 2;

		this.dataset2 = dataset2;

		this.NBR_BLKS_SET2 = nbr_blks_set2;
		this.NBR_BLK_INTS_SET2 = nbr_blk_ints_set2;
		this.NBR_CHANS_SET2 = nbr_chans_set2;
		this.INT_TIME_SET2 = int_time_set2;
		this.NBR_VALID_INT_SET2 = nbr_valid_int_set2 - 1;

		this.LEN_SIMPLE_INTVAL_STAT_SET2 = (nbr_blk_ints_set2 + 7) / 8;
		this.LEN_NBR_INTERVAL_RECORD_SET2 = nbr_blk_ints_set2
				* (1 + nbr_chans_set2 * 5 / 2);
		this.LEN_EXT_INTERVAL_STATUS_SET2 = 1 + (nbr_chans_set2 / 2);
		this.LEN_INTERVAL_DATA_SET2 = nbr_chans_set2 * 2;

		this.set1divisors = set1divisors;
		this.set2divisors = set2divisors;

		this.ins_scale = ins_scale;
		// this.melement = melement;

		parseInstrumentI();
		parseInstrumentII();
		parseInstrument();
	}

	public Instrument[] getInstrument() {
		return this.instData;
	}

	public int getInstrumentICount() {
		return this.instSet1.size();
	}

	public int getInstrumentIICount() {
		return this.instSet2.size();
	}

	public void parseInstrument() {

		Double NOTVALID1 = null;
		Double NOTVALID2 = 0d;
		try {
			this.instData = new Instrument[instSet1.size()];
		} catch (Exception e) {
			logger.error(e, e);
			logger.warn(e.getMessage());
		}
		int offset1 = 0;
		int offset2 = 0;

		try {

			for (int i = 0; i < instSet1.size(); i++) {
				ArrayList lst1 = (ArrayList) instSet1.get(i);
				ArrayList lst2 = (ArrayList) instSet2.get(i);

				String pulseT1 = (String) lst1.get(0);
				byte[] stat = (byte[]) lst1.get(1);

				String pulseT2 = (String) lst2.get(0);

				Double via = NOTVALID1;
				Double vib = NOTVALID1;
				Double vic = NOTVALID1;
				Double cia = NOTVALID1;
				Double cib = NOTVALID1;
				Double cic = NOTVALID1;
				Double vaa = NOTVALID1;
				Double vab = NOTVALID1;
				Double vac = NOTVALID1;
				Double caa = NOTVALID1;
				Double cab = NOTVALID1;
				Double cac = NOTVALID1;
				Double dkvaa = NOTVALID2;
				Double dkvab = NOTVALID2;
				Double dkvac = NOTVALID2;
				Double dkvat = NOTVALID2;

				Double vthda = NOTVALID1;
				Double vthdb = NOTVALID1;
				Double vthdc = NOTVALID1;
				Double cthda = NOTVALID1;
				Double cthdb = NOTVALID1;
				Double cthdc = NOTVALID1;
				Double tdda = NOTVALID1;
				Double tddb = NOTVALID1;
				Double tddc = NOTVALID1;
				Double lf = NOTVALID1;
				Double dkwa = NOTVALID2;
				Double dkwb = NOTVALID2;
				Double dkwc = NOTVALID2;
				Double dkwt = NOTVALID2;
				Double dkvara = NOTVALID2;
				Double dkvarb = NOTVALID2;
				Double dkvarc = NOTVALID2;
				Double dkvart = NOTVALID2;

				Double rkwa = NOTVALID2;
				Double rkwb = NOTVALID2;
				Double rkwc = NOTVALID2;
				Double rkwt = NOTVALID2;
				Double rkvara = NOTVALID2;
				Double rkvarb = NOTVALID2;
				Double rkvarc = NOTVALID2;
				Double rkvart = NOTVALID2;
				Double rkvaa = NOTVALID2;
				Double rkvab = NOTVALID2;
				Double rkvac = NOTVALID2;

				int va = DataFormat.hex2signed16((byte[]) lst1.get(2));
				int vb = DataFormat.hex2signed16((byte[]) lst1.get(3));
				int vc = DataFormat.hex2signed16((byte[]) lst1.get(4));

				byte melement = getMElement(va, vb, vc);

				switch (melement) {
				case 0x00:
					via = getCh1((byte[]) lst1.get(2), this.set1divisors[0]);
					vic = getCh1((byte[]) lst1.get(4), this.set1divisors[2]);
					cia = getCh1((byte[]) lst1.get(5), this.set1divisors[3]);
					cic = getCh1((byte[]) lst1.get(7), this.set1divisors[5]);
					vaa = getCh1((byte[]) lst1.get(8), this.set1divisors[6]);
					vac = getCh1((byte[]) lst1.get(10), this.set1divisors[8]);
					caa = getCh1((byte[]) lst1.get(11), this.set1divisors[9]);
					cac = getCh1((byte[]) lst1.get(13), this.set1divisors[11]);
					vthda = getCh1((byte[]) lst2.get(2), this.set2divisors[0]);
					vthdc = getCh1((byte[]) lst2.get(4), this.set2divisors[2]);
					cthda = getCh1((byte[]) lst2.get(5), this.set2divisors[3]);
					cthdc = getCh1((byte[]) lst2.get(7), this.set2divisors[5]);
					tdda = getCh1((byte[]) lst2.get(8), this.set2divisors[6]);
					tddc = getCh1((byte[]) lst2.get(10), this.set2divisors[8]);
					lf = getCh1((byte[]) lst2.get(11), this.set2divisors[9]);
					dkvaa = getCh2((byte[]) lst1.get(14), this.set1divisors[12]);
					dkvac = getCh2((byte[]) lst1.get(16), this.set1divisors[14]);

					if (!(dkvaa == null && dkvac == null)) {
						dkvat = (dkvaa == null ? 0d : dkvaa)
								+ (dkvac == null ? 0d : dkvac);
					}

					dkwa = getCh2((byte[]) lst2.get(12), this.set2divisors[10]);
					dkwc = getCh2((byte[]) lst2.get(14), this.set2divisors[12]);

					if (!(dkwa == null && dkwc == null)) {
						dkwt = (dkwa == null ? 0d : dkwa)
								+ (dkwc == null ? 0d : dkwc);
					}

					dkvara = getCh2((byte[]) lst2.get(15),
							this.set2divisors[13]);
					dkvarc = getCh2((byte[]) lst2.get(17),
							this.set2divisors[15]);
					if (!(dkvara == null && dkvarc == null)) {
						dkvart = (dkvara == null ? 0d : dkvara)
								+ (dkvarc == null ? 0d : dkvarc);
					}
					break;
				case 0x40:
					via = getCh1((byte[]) lst1.get(2), this.set1divisors[0]);
					cia = getCh1((byte[]) lst1.get(5), this.set1divisors[3]);
					vaa = getCh1((byte[]) lst1.get(8), this.set1divisors[6]);
					caa = getCh1((byte[]) lst1.get(11), this.set1divisors[9]);
					vthda = getCh1((byte[]) lst2.get(2), this.set2divisors[0]);
					cthda = getCh1((byte[]) lst2.get(5), this.set2divisors[3]);
					tdda = getCh1((byte[]) lst2.get(8), this.set2divisors[6]);
					lf = getCh1((byte[]) lst2.get(11), this.set2divisors[9]);
					dkvaa = getCh2((byte[]) lst1.get(14), this.set1divisors[12]);
					dkvat = getCh2((byte[]) lst1.get(14), this.set1divisors[12]);
					dkwa = getCh2((byte[]) lst2.get(12), this.set2divisors[10]);
					dkvat = getCh2((byte[]) lst2.get(12), this.set2divisors[10]);
					dkvara = getCh2((byte[]) lst2.get(15),
							this.set2divisors[13]);
					dkvart = getCh2((byte[]) lst2.get(15),
							this.set2divisors[13]);
					break;
				default:
					via = getCh1((byte[]) lst1.get(2), this.set1divisors[0]);
					vib = getCh1((byte[]) lst1.get(3), this.set1divisors[1]);
					vic = getCh1((byte[]) lst1.get(4), this.set1divisors[2]);
					cia = getCh1((byte[]) lst1.get(5), this.set1divisors[3]);
					cib = getCh1((byte[]) lst1.get(6), this.set1divisors[4]);
					cic = getCh1((byte[]) lst1.get(7), this.set1divisors[5]);
					vaa = getCh1((byte[]) lst1.get(8), this.set1divisors[6]);
					vab = getCh1((byte[]) lst1.get(9), this.set1divisors[7]);
					vac = getCh1((byte[]) lst1.get(10), this.set1divisors[8]);
					caa = getCh1((byte[]) lst1.get(11), this.set1divisors[9]);
					cab = getCh1((byte[]) lst1.get(12), this.set1divisors[10]);
					cac = getCh1((byte[]) lst1.get(13), this.set1divisors[11]);
					vthda = getCh1((byte[]) lst2.get(2), this.set2divisors[0]);
					vthdb = getCh1((byte[]) lst2.get(3), this.set2divisors[1]);
					vthdc = getCh1((byte[]) lst2.get(4), this.set2divisors[2]);
					cthda = getCh1((byte[]) lst2.get(5), this.set2divisors[3]);
					cthdb = getCh1((byte[]) lst2.get(6), this.set2divisors[4]);
					cthdc = getCh1((byte[]) lst2.get(7), this.set2divisors[5]);
					tdda = getCh1((byte[]) lst2.get(8), this.set2divisors[6]);
					tddb = getCh1((byte[]) lst2.get(9), this.set2divisors[7]);
					tddc = getCh1((byte[]) lst2.get(10), this.set2divisors[8]);
					lf = getCh1((byte[]) lst2.get(11), this.set2divisors[9]);

					dkvaa = getCh2((byte[]) lst1.get(14), this.set1divisors[12]);
					dkvab = getCh2((byte[]) lst1.get(15), this.set1divisors[13]);
					dkvac = getCh2((byte[]) lst1.get(16), this.set1divisors[14]);

					if (!(dkvaa == null && dkvab == null && dkvac == null)) {
						dkvat = (dkvaa == null ? 0d : dkvaa)
								+ (dkvab == null ? 0d : dkvab)
								+ (dkvac == null ? 0d : dkvac);
					}

					dkwa = getCh2((byte[]) lst2.get(12), this.set2divisors[10]);
					dkwb = getCh2((byte[]) lst2.get(13), this.set2divisors[11]);
					dkwc = getCh2((byte[]) lst2.get(14), this.set2divisors[12]);

					if (!(dkwa == null && dkwb == null && dkwc == null)) {
						dkwt = (dkwa == null ? 0d : dkwa)
								+ (dkwb == null ? 0d : dkwb)
								+ (dkwc == null ? 0d : dkwc);
					}

					dkvara = getCh2((byte[]) lst2.get(15),
							this.set2divisors[13]);
					dkvarb = getCh2((byte[]) lst2.get(16),
							this.set2divisors[14]);
					dkvarc = getCh2((byte[]) lst2.get(17),
							this.set2divisors[15]);

					if (!(dkvara == null && dkvarb == null && dkvarc == null)) {
						dkvart = (dkvara == null ? 0d : dkvara)
								+ (dkvarb == null ? 0d : dkvarb)
								+ (dkvarc == null ? 0d : dkvarc);
					}

					instData[i] = new Instrument();
					instData[i].setDatetime(pulseT1);
					instData[i].setVOL_A(df(via));
					instData[i].setVOL_B(df(vib));
					instData[i].setVOL_C(df(vic));
					instData[i].setCURR_A(df(cia));
					instData[i].setCURR_B(df(cib));
					instData[i].setCURR_C(df(cic));
					instData[i].setVOL_ANGLE_A(df(vaa));
					instData[i].setVOL_ANGLE_B(df(vab));
					instData[i].setVOL_ANGLE_C(df(vac));
					instData[i].setCURR_ANGLE_A(df(caa));
					instData[i].setCURR_ANGLE_B(df(cab));
					instData[i].setCURR_ANGLE_C(df(cac));
					instData[i].setVOL_THD_A(df(vthda));
					instData[i].setVOL_THD_B(df(vthdb));
					instData[i].setVOL_THD_C(df(vthdc));
					instData[i].setCURR_THD_A(df(cthda));
					instData[i].setCURR_THD_B(df(cthdb));
					instData[i].setCURR_THD_C(df(cthdc));
					instData[i].setTDD_A(df(tdda));
					instData[i].setTDD_B(df(tddb));
					instData[i].setTDD_C(df(tddc));
					instData[i].setKW_A(df(dkwa));
					instData[i].setKW_B(df(dkwb));
					instData[i].setKW_C(df(dkwc));
					instData[i].setKVAR_A(df(dkvara));
					instData[i].setKVAR_B(df(dkvarb));
					instData[i].setKVAR_C(df(dkvarc));
					instData[i].setKVA_A(df(dkvaa));
					instData[i].setKVA_B(df(dkvab));
					instData[i].setKVA_C(df(dkvac));
					instData[i].setLINE_FREQUENCY(df(lf));

				}
			}

		} catch (Exception e) {
			logger.error(e, e);
			logger.warn(e.getMessage());
		}
	}

	private Double df(Double d) {

		BigDecimal bd = new BigDecimal(d + "");
		Double list = bd.setScale(4, BigDecimal.ROUND_FLOOR).doubleValue();

		return list;

	}

	@SuppressWarnings( { "unchecked", "static-access" })
	private void parseInstrumentI() {

		int fst_blk_val_cnt = this.NBR_BLK_INTS_SET1;
		int empty_blk_cnt = 0;

		int oneblocksize = LEN_BLK_END_TIME + this.LEN_SIMPLE_INTVAL_STAT_SET1
				+ this.LEN_NBR_INTERVAL_RECORD_SET1;

		int datalen = dataset1.length;
		int block_count = dataset1.length / oneblocksize;

		ArrayList[] lists = new ArrayList[block_count];

		String endtime = new String();
		String status = new String();
		byte[] instI;
		int x = 0;

		try {
			if (fst_blk_val_cnt > 0) {
				byte[] first_block = new byte[this.LEN_BLK_END_TIME
						+ this.LEN_SIMPLE_INTVAL_STAT_SET1
						+ fst_blk_val_cnt
						* (this.LEN_EXT_INTERVAL_STATUS_SET1 + this.LEN_INTERVAL_DATA_SET1)];
				first_block = DataFormat
						.select(
								dataset1,
								0,
								this.LEN_BLK_END_TIME
										+ this.LEN_SIMPLE_INTVAL_STAT_SET1
										+ fst_blk_val_cnt
										* (this.LEN_EXT_INTERVAL_STATUS_SET1 + this.LEN_INTERVAL_DATA_SET1));

				endtime = getQuaterYymmddhhmm(getYymmddhhmm(first_block, 0,
						this.LEN_BLK_END_TIME));

				status = getBlkStatus(DataFormat
						.select(first_block, this.LEN_BLK_END_TIME,
								this.LEN_SIMPLE_INTVAL_STAT_SET1),
						fst_blk_val_cnt);
				int statusCnt = 0;
				for (int i = 0; i < status.length(); i++) {
					if (status.substring(i, i + 1).equals("1"))
						statusCnt++;
				}

				// 마지막 시간 + 유효한 데이터 수
				int preDataSize = this.LEN_BLK_END_TIME
						+ this.LEN_SIMPLE_INTVAL_STAT_SET1;
				// 데이터 상태 값 + 검침값
				int dataSize = this.LEN_EXT_INTERVAL_STATUS_SET1
						+ this.LEN_INTERVAL_DATA_SET1;
				int interval = 15;
				String mm = getYymmddhhmm(first_block, 0, this.LEN_BLK_END_TIME)
						.substring(10, 12);
				// 마지막 시간이 lp주기 시간이 아닐경우 첫번째 데이터 누락
				if (Integer.parseInt(mm) % interval != 0) {
					preDataSize += dataSize;
					statusCnt--;
					fst_blk_val_cnt--;
				}

				instI = DataFormat.select(first_block, preDataSize,
						fst_blk_val_cnt * dataSize);

				lists[x] = new ArrayList();
				lists[x].add(0, new Integer(statusCnt));
				lists[x].add(1, endtime);
				lists[x].add(2, instI);
				lists[x].add(3, status);
				x++;
			} else {
				empty_blk_cnt++;
			}

			int offset = oneblocksize;

			for (int i = 1; i < block_count; i++) {

				byte[] blk = new byte[oneblocksize];
				blk = DataFormat.select(dataset1, offset, oneblocksize);

				if (blk[0] == (byte) 0xFF) {
					empty_blk_cnt++;
				} else {

					endtime = getQuaterYymmddhhmm(getYymmddhhmm(blk, 0,
							this.LEN_BLK_END_TIME));

					status = getBlkStatus(DataFormat.select(blk,
							this.LEN_BLK_END_TIME,
							this.LEN_SIMPLE_INTVAL_STAT_SET1),
							this.NBR_BLK_INTS_SET1);

					instI = DataFormat
							.select(
									blk,
									this.LEN_BLK_END_TIME
											+ this.LEN_SIMPLE_INTVAL_STAT_SET1,
									this.NBR_BLK_INTS_SET1
											* (this.LEN_EXT_INTERVAL_STATUS_SET1 + this.LEN_INTERVAL_DATA_SET1));

					lists[x] = new ArrayList();
					lists[x].add(0, new Integer(this.NBR_BLK_INTS_SET1));
					lists[x].add(1, endtime);
					lists[x].add(2, instI);
					lists[x].add(3, status);
					x++;
				}

				offset += oneblocksize;

			}

			lists = orderby(lists, empty_blk_cnt);

			ArrayList biglst = new ArrayList();
			int total_inst_cnt = 0;
			for (int i = 0; i < lists.length; i++) {
				ArrayList[] lst = parseInstI(lists[i]);
				biglst.add(i, lst);
				total_inst_cnt += lst.length;
			}

			// biglst 블럭 데이터, instlist lp 데이터
			ArrayList[] instlist = new ArrayList[total_inst_cnt];
			int idx = 0;
			for (int i = 0; i < biglst.size(); i++) {
				ArrayList[] temp = (ArrayList[]) biglst.get(i);
				int j = 0;
				while (j < temp.length) {
					instlist[idx++] = temp[j++];
				}
			}

			this.instSet1 = checkEmptyInstI(instlist);
		} catch (Exception e) {
			logger.error(e, e);
			logger.warn(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	protected ArrayList checkEmptyInstI(ArrayList[] lists) throws Exception {

		int len = lists.length;

		ArrayList newlist = new ArrayList();
		ArrayList emptytime = new ArrayList();
		String prevtime = new String();

		byte[] set1ch = { (byte) 0xFF, (byte) 0xFF };

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
						time = Util.addMinYymmdd(time, this.INT_TIME_SET1);
						ArrayList temp = new ArrayList();
						temp.add(0, time);
						temp.add(1, new byte[2]);

						for (int k = 2; k < this.NBR_CHANS_SET2 + 2; k++) {
							temp.add(k, set1ch);
						}

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

	protected ArrayList checkEmptyInstII(ArrayList[] lists) throws Exception {

		int len = lists.length;

		ArrayList newlist = new ArrayList();
		ArrayList emptytime = new ArrayList();
		String prevtime = new String();

		byte[] set1ch = { (byte) 0xFF, (byte) 0xFF };

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
						time = Util.addMinYymmdd(time, this.INT_TIME_SET2);
						ArrayList temp = new ArrayList();
						temp.add(0, time);
						temp.add(1, new byte[2]);

						for (int k = 2; k < this.NBR_CHANS_SET2 + 2; k++) {
							temp.add(k, set1ch);
						}

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

	public void parseInstrumentII() {

		int fst_blk_val_cnt = this.NBR_BLK_INTS_SET2;
		int empty_blk_cnt = 0;

		int oneblocksize = LEN_BLK_END_TIME + this.LEN_SIMPLE_INTVAL_STAT_SET2
				+ this.LEN_NBR_INTERVAL_RECORD_SET2;

		int datalen = dataset2.length;
		int block_count = dataset2.length / oneblocksize;

		ArrayList[] lists = new ArrayList[block_count];

		String endtime = new String();
		String status = new String();
		byte[] instII;
		int x = 0;

		try {
			if (fst_blk_val_cnt > 0) {
				byte[] first_block = new byte[this.LEN_BLK_END_TIME
						+ this.LEN_SIMPLE_INTVAL_STAT_SET2
						+ fst_blk_val_cnt
						* (this.LEN_EXT_INTERVAL_STATUS_SET2 + this.LEN_INTERVAL_DATA_SET2)];
				first_block = DataFormat
						.select(
								dataset2,
								0,
								this.LEN_BLK_END_TIME
										+ this.LEN_SIMPLE_INTVAL_STAT_SET2
										+ fst_blk_val_cnt
										* (this.LEN_EXT_INTERVAL_STATUS_SET2 + this.LEN_INTERVAL_DATA_SET2));

				endtime = getQuaterYymmddhhmm(getYymmddhhmm(first_block, 0,
						this.LEN_BLK_END_TIME));

				status = getBlkStatus(DataFormat
						.select(first_block, this.LEN_BLK_END_TIME,
								this.LEN_SIMPLE_INTVAL_STAT_SET2),
						fst_blk_val_cnt);

				int statusCnt = 0;
				for (int i = 0; i < status.length(); i++) {
					if (status.substring(i, i + 1).equals("1"))
						statusCnt++;
				}

				// 마지막 시간 + 유효한 데이터 수
				int preDataSize = this.LEN_BLK_END_TIME
						+ this.LEN_SIMPLE_INTVAL_STAT_SET2;
				// 데이터 상태 값 + 검침값
				int dataSize = this.LEN_EXT_INTERVAL_STATUS_SET2
						+ this.LEN_INTERVAL_DATA_SET2;
				int interval = 15;
				String mm = getYymmddhhmm(first_block, 0, this.LEN_BLK_END_TIME)
						.substring(10, 12);
				// 마지막 시간이 lp주기 시간이 아닐경우 첫번째 데이터 누락
				if (Integer.parseInt(mm) % interval != 0) {
					preDataSize += dataSize;
					statusCnt--;
					fst_blk_val_cnt--;
				}

				instII = DataFormat.select(first_block, preDataSize,
						fst_blk_val_cnt * dataSize);

				lists[x] = new ArrayList();
				lists[x].add(0, new Integer(statusCnt));
				lists[x].add(1, endtime);
				lists[x].add(2, instII);
				lists[x].add(3, status);
				x++;
			} else {
				empty_blk_cnt++;
			}

			int offset = oneblocksize;

			for (int i = 1; i < block_count; i++) {

				byte[] blk = new byte[oneblocksize];
				blk = DataFormat.select(dataset2, offset, oneblocksize);

				if (blk[0] == (byte) 0xFF) {
					empty_blk_cnt++;
				} else {

					endtime = Util.getQuaterYymmddhhmm(getYymmddhhmm(blk, 0,
							this.LEN_BLK_END_TIME), this.INT_TIME_SET1);

					status = getBlkStatus(DataFormat.select(blk,
							this.LEN_BLK_END_TIME,
							this.LEN_SIMPLE_INTVAL_STAT_SET2),
							this.NBR_BLK_INTS_SET2);

					instII = DataFormat
							.select(
									blk,
									this.LEN_BLK_END_TIME
											+ this.LEN_SIMPLE_INTVAL_STAT_SET2,
									this.NBR_BLK_INTS_SET2
											* (this.LEN_EXT_INTERVAL_STATUS_SET2 + this.LEN_INTERVAL_DATA_SET2));

					lists[x] = new ArrayList();
					lists[x].add(0, new Integer(this.NBR_BLK_INTS_SET2));
					lists[x].add(1, endtime);
					lists[x].add(2, instII);
					lists[x].add(3, status);
					x++;
				}

				offset += oneblocksize;

			}

			lists = orderby(lists, empty_blk_cnt);

			ArrayList biglst = new ArrayList();
			int total_inst_cnt = 0;
			for (int i = 0; i < lists.length; i++) {
				ArrayList[] lst = parseInstII(lists[i]);
				biglst.add(i, lst);
				total_inst_cnt += lst.length;
			}

			ArrayList[] instlist = new ArrayList[total_inst_cnt];
			int idx = 0;
			for (int i = 0; i < biglst.size(); i++) {
				ArrayList[] temp = (ArrayList[]) biglst.get(i);
				int j = 0;
				while (j < temp.length) {
					instlist[idx++] = temp[j++];
				}
			}

			this.instSet2 = checkEmptyInstII(instlist);

		} catch (Exception e) {
			logger.warn(e.getMessage());
		}
	}

	protected ArrayList[] parseInstI(ArrayList list) throws Exception {

		try {
			Integer instcount = (Integer) list.get(0);
			String endtime = (String) list.get(1);
			byte[] instIs = (byte[]) list.get(2);
			int emptycnt = 0;

			ArrayList interval = new ArrayList();

			Vector<ArrayList> vpInterval = new Vector<ArrayList>();

			int ofs = 0;

			int idx = 0;
			for (int i = 0; i < instcount.intValue(); i++) {
				String instItime = Util.addMinYymmdd(endtime,
						-this.INT_TIME_SET1 * i);
				byte[] instIstat = DataFormat.select(instIs, ofs,
						this.LEN_EXT_INTERVAL_STATUS_SET1);

				if (!Arrays.equals(instIstat, new byte[] { 0x00, 0x00, 0x00,
						0x00, 0x00, 0x00, 0x00, 0x00 }))
					break;
				ofs += this.LEN_EXT_INTERVAL_STATUS_SET1;

				interval = new ArrayList();
				interval.add(0, instItime);
				interval.add(1, instIstat);

				for (int j = 0; j < this.NBR_CHANS_SET1; j++) {
					byte[] ch = DataFormat.LSB2MSB(DataFormat.select(instIs,
							ofs, this.LEN_INTERVAL_DATA_SET1
									/ this.NBR_CHANS_SET1));
					ofs += this.LEN_INTERVAL_DATA_SET1 / this.NBR_CHANS_SET1;
					interval.add(2 + j, ch);
				}
				idx++;

				vpInterval.add(interval);
			}

			return vpInterval.toArray(new ArrayList[0]);
		} catch (Exception e) {
			throw new Exception("parseLP Error : " + e.getMessage());
		}

	}

	protected ArrayList[] parseInstII(ArrayList list) throws Exception {

		try {
			Integer instcount = (Integer) list.get(0);
			String endtime = (String) list.get(1);
			byte[] instIIs = (byte[]) list.get(2);
			int emptycnt = 0;

			ArrayList[] interval = new ArrayList[instcount.intValue()];

			int ofs = 0;
			if (instcount.intValue() > 0) {
				ofs = (instcount.intValue() - 1)
						* (this.LEN_EXT_INTERVAL_STATUS_SET2 + this.LEN_INTERVAL_DATA_SET2);
			}

			int idx = 0;
			for (int i = 0; i < instcount.intValue(); i++) {
				String instIItime = Util.addMinYymmdd(endtime,
						-this.INT_TIME_SET2 * i);
				byte[] instIIstat = DataFormat.select(instIIs, ofs,
						this.LEN_EXT_INTERVAL_STATUS_SET2);
				ofs += this.LEN_EXT_INTERVAL_STATUS_SET2;

				interval[idx] = new ArrayList();
				interval[idx].add(0, instIItime);
				interval[idx].add(1, instIIstat);

				for (int j = 0; j < this.NBR_CHANS_SET2; j++) {
					byte[] ch = DataFormat.LSB2MSB(DataFormat.select(instIIs,
							ofs, this.LEN_INTERVAL_DATA_SET2
									/ this.NBR_CHANS_SET2));
					ofs += this.LEN_INTERVAL_DATA_SET2 / this.NBR_CHANS_SET2;
					interval[idx].add(2 + j, ch);
				}
				idx++;

				ofs = instIIs.length
						- (i + 2)
						* (this.LEN_EXT_INTERVAL_STATUS_SET2 + this.LEN_INTERVAL_DATA_SET2);
			}

			interval = orderby(interval, emptycnt);
			return interval;
		} catch (Exception e) {
			throw new Exception("parseLP Error : " + e.getMessage());
		}

	}

	protected ArrayList[] orderby(ArrayList[] lists, int emptycnt) {

		int size = lists.length - emptycnt;
		ArrayList[] newlists = new ArrayList[size];

		for (int i = 0; i < size; i++) {
			newlists[i] = lists[i];
		}
		return newlists;

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

	private String getBlkStatus(byte[] b, int blkcnt) {

		String str = new String();
		for (int i = 0; i < b.length; i++) {
			int val = DataFormat.hex2unsigned8(b[i]);
			StringBuffer temp = new StringBuffer(Util.frontAppendNStr('0',
					Integer.toBinaryString(val), 8));
			str += temp.reverse();
		}

		return str;
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

	private Double getCh1(byte[] ch, int divisor) {

		Double value = null;
		try {

			if (DataFormat.hex2signed16(ch) != -1) {
				value = (DataFormat.hex2signed16(ch) * divisor * this.ins_scale);
			}

		} catch (Exception e) {
			logger.warn(e.getMessage());
		}

		return value;
	}

	private Double getCh2(byte[] ch, int divisor) {

		Double value = null;
		try {

			if (DataFormat.hex2signed16(ch) != -1) {
				value = (DataFormat.hex2signed16(ch) * divisor * this.ins_scale) * 0.001;
			}
		} catch (Exception e) {
			logger.warn(e.getMessage());
		}

		return value;
	}

	/*
	 * * Two Voltage: 0x00 (3P 3W) One Voltage: 0x40 (1P 2W) Three Voltage: 0x80
	 * (3P 4W) 2 1/2 Voltage: 0xC0 (1P 3W)
	 */
	public byte getMElement(int via, int vib, int vic) {

		byte melement = (byte) 0xFF;

		try {
			if (via != 0 && vib != 0 && vic != 0) {
				melement = (byte) 0x80;
			} else if (via != 0 && vib == 0 && vic != 0) {
				melement = 0x00;
			} else if (via != 0 && vib == 0 && vic == 0) {
				melement = 0x40;
			}
		} catch (Exception e) {
			logger.warn(e.getMessage());
		}

		return melement;

	}

}
