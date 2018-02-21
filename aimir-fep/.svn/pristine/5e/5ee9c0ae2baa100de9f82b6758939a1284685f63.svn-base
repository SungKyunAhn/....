/**
 * @(#)MT110.java       1.0 06/12/14 *
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

import com.aimir.fep.util.DataFormat;

/**
 * @author Park YeonKyoung goodjob@nuritelecom.com
 */
public class MT110 {

	public static final int LEN_PREVIOUS_INT_DEMANDS     = 5;
	public static final int LEN_DEMANDS                  = 4;
	public static final int LEN_KW_DMD_FUND_PLUS         = 4;
	public static final int LEN_KW_DMD_FUND_ONLY         = 4;
	public static final int LEN_KVAR_DMD_FUND_PLUS       = 4;
	public static final int LEN_KVAR_DMD_FUND_ONLY       = 4;
	public static final int LEN_DISTORTION_KVA_DMD       = 4;
	public static final int LEN_APPARENT_KVA_DMD         = 4;
	public static final int LEN_V_L_TO_N_FUND_PLUS       = 2;
	public static final int LEN_V_L_TO_N_FUND_ONLY       = 2;
	public static final int LEN_V_L_TO_L_FUND_PLUS       = 2;
	public static final int LEN_V_L_TO_L_FUND_ONLY       = 2;
	public static final int LEN_CURR_FUND_PLUS           = 2;
	public static final int LEN_CURR_FUND_ONLY           = 2;
	public static final int LEN_IMPUTED_NEUTRAL_CURR     = 2;
	public static final int LEN_POWER_FACTOR             = 1;
	public static final int LEN_FREQUENCY                = 2;
	public static final int LEN_TDD                      = 1;
	public static final int LEN_ITHD                     = 1;
	public static final int LEN_VTHD                     = 1;
	public static final int LEN_DISTORTION_PF            = 1;
	public static final int LEN_TIME_REMAINING_IN_SUBINT = 1;

	private double[] previous_int_demands;
	private double[] demands;
	private double[] kw_dmd_fund_plus;
	private double[] kw_dmd_fund_only;
	private double[] kvar_dmd_fund_plus;
	private double[] kvar_dmd_fund_only;
	private double[] distortion_kva_dmd;
	private double[] apparent_kva_dmd;
	private double[] v_l_to_n_fund_plus;
	private double[] v_l_to_n_fund_only;
	private double[] v_l_to_l_fund_plus;
	private double[] v_l_to_l_fund_only;
	private double[] curr_fund_plus;
	private double[] curr_fund_only;
	private int      imputed_neutral_curr;
	private double   power_factor;
	private double   frequency;
	private Double[]    tdd;
	private Double[]    ithd;
	private Double[]    vthd;
	private Double[]    distortion_pf;
	private int      time_remaining_in_subint;

	private byte[] data;
    private static Log log = LogFactory.getLog(MT110.class);

	/**
	 * Constructor .<p>
	 *
	 * @param data - read data (header,crch,crcl)
	 */
	public MT110(byte[] data) {
		this.data = data;
		this.previous_int_demands = new double[5];
		this.demands              = new double[5];
		this.kw_dmd_fund_plus     = new double[3];
		this.kw_dmd_fund_only     = new double[3];
		this.kvar_dmd_fund_plus   = new double[3];
		this.kvar_dmd_fund_only   = new double[3];
		this.distortion_kva_dmd   = new double[3];
		this.apparent_kva_dmd     = new double[3];
		this.v_l_to_n_fund_plus   = new double[3];
		this.v_l_to_n_fund_only   = new double[3];
		this.v_l_to_l_fund_plus   = new double[3];
		this.v_l_to_l_fund_only   = new double[3];
		this.curr_fund_plus       = new double[3];
		this.curr_fund_only       = new double[3];
		this.tdd                  = new Double[3];
		this.ithd                 = new Double[3];
		this.vthd                 = new Double[3];
		this.distortion_pf        = new Double[4];
	}

	public void parse()
	{
		try{
			int ofs = 0;
			for(int i = 0; i < this.previous_int_demands.length;i++){
				this.previous_int_demands[i] = DataFormat.hex2dec(
				        DataFormat.LSB2MSB(
							DataFormat.select(
								data,ofs,LEN_PREVIOUS_INT_DEMANDS)));
				ofs += LEN_PREVIOUS_INT_DEMANDS;
			}

			for(int i = 0; i < this.demands.length;i++){
				this.demands[i] = DataFormat.hex2dec(
						DataFormat.LSB2MSB(
								DataFormat.select(
									data,ofs,LEN_DEMANDS)));
				ofs += LEN_DEMANDS;
			}

			for(int i = 0; i < this.kw_dmd_fund_plus.length; i++){
				this.kw_dmd_fund_plus[i] = DataFormat.hex2dec(
				        DataFormat.LSB2MSB(
								DataFormat.select(
									data,ofs,LEN_KW_DMD_FUND_PLUS)));
				ofs += LEN_KW_DMD_FUND_PLUS;
			}

			for(int i = 0; i < this.kw_dmd_fund_only.length; i++){
				this.kw_dmd_fund_only[i] = DataFormat.hex2dec(
				        DataFormat.LSB2MSB(
								DataFormat.select(
									data,ofs,LEN_KW_DMD_FUND_ONLY)));
				ofs += LEN_KW_DMD_FUND_ONLY;
			}

			for(int i = 0; i < this.kvar_dmd_fund_plus.length; i++){
				this.kvar_dmd_fund_plus[i] = DataFormat.hex2dec(
				        DataFormat.LSB2MSB(
								DataFormat.select(
									data,ofs,LEN_KVAR_DMD_FUND_PLUS)));
				ofs += LEN_KVAR_DMD_FUND_PLUS;
			}

			for(int i = 0; i < this.kvar_dmd_fund_only.length; i++){
				this.kvar_dmd_fund_only[i] = DataFormat.hex2dec(
				        DataFormat.LSB2MSB(
								DataFormat.select(
									data,ofs,LEN_KVAR_DMD_FUND_ONLY)));
				ofs += LEN_KVAR_DMD_FUND_ONLY;
			}

			for(int i = 0; i < this.distortion_kva_dmd.length; i++){
				this.distortion_kva_dmd[i] = DataFormat.hex2dec(
				        DataFormat.LSB2MSB(
								DataFormat.select(
									data,ofs,LEN_DISTORTION_KVA_DMD)));
				ofs += LEN_DISTORTION_KVA_DMD;
			}

			for(int i = 0; i < this.apparent_kva_dmd.length; i++){
				this.apparent_kva_dmd[i] = DataFormat.hex2dec(
				        DataFormat.LSB2MSB(
								DataFormat.select(
									data,ofs,LEN_APPARENT_KVA_DMD)));
				ofs += LEN_APPARENT_KVA_DMD;
			}

			for(int i = 0; i < this.v_l_to_n_fund_plus.length; i++){
				this.v_l_to_n_fund_plus[i] = DataFormat.hex2unsigned16(
				        DataFormat.LSB2MSB(
								DataFormat.select(
									data,ofs,LEN_V_L_TO_N_FUND_PLUS)));
				ofs += LEN_V_L_TO_N_FUND_PLUS;
			}

			for(int i = 0; i < this.v_l_to_n_fund_only.length; i++){
				this.v_l_to_n_fund_only[i] = DataFormat.hex2unsigned16(
				        DataFormat.LSB2MSB(
								DataFormat.select(
									data,ofs,LEN_V_L_TO_N_FUND_ONLY)));
				ofs += LEN_V_L_TO_N_FUND_ONLY;
			}

			for(int i = 0; i < this.v_l_to_l_fund_plus.length; i++){
				this.v_l_to_l_fund_plus[i] = DataFormat.hex2unsigned16(
				        DataFormat.LSB2MSB(
								DataFormat.select(
									data,ofs,LEN_V_L_TO_L_FUND_PLUS)));
				ofs += LEN_V_L_TO_L_FUND_PLUS;
			}

			for(int i = 0; i < this.v_l_to_l_fund_only.length; i++){
				this.v_l_to_l_fund_only[i] = DataFormat.hex2unsigned16(
				        DataFormat.LSB2MSB(
								DataFormat.select(
									data,ofs,LEN_V_L_TO_L_FUND_ONLY)));
				ofs += LEN_V_L_TO_L_FUND_ONLY;
			}

			for(int i = 0; i < this.curr_fund_plus.length; i++){
				this.curr_fund_plus[i] = DataFormat.hex2unsigned16(
				        DataFormat.LSB2MSB(
								DataFormat.select(
									data,ofs,LEN_CURR_FUND_PLUS)));
				ofs += LEN_CURR_FUND_PLUS;
			}

			for(int i = 0; i < this.curr_fund_only.length; i++){
				this.curr_fund_plus[i] = DataFormat.hex2unsigned16(
				        DataFormat.LSB2MSB(
								DataFormat.select(
									data,ofs,LEN_CURR_FUND_ONLY)));
				ofs += LEN_CURR_FUND_ONLY;
			}

			this.imputed_neutral_curr = DataFormat.hex2unsigned16(
			        DataFormat.LSB2MSB(
							DataFormat.select(
								data,ofs,LEN_CURR_FUND_ONLY)));
			ofs += LEN_IMPUTED_NEUTRAL_CURR;

			this.power_factor = DataFormat.hex2unsigned8(data[ofs])/100;
			ofs += LEN_POWER_FACTOR;

			this.frequency = DataFormat.hex2unsigned16(
			        DataFormat.LSB2MSB(
							DataFormat.select(
								data,ofs,LEN_FREQUENCY)))/100;
			ofs += LEN_FREQUENCY;

			for(int i = 0; i < this.tdd.length; i++){
				this.tdd[i] = new Double(DataFormat.hex2unsigned8(data[ofs]));
				ofs += LEN_TDD;
			}

			for(int i = 0; i < this.ithd.length; i++){
				this.ithd[i] = new Double(DataFormat.hex2unsigned8(data[ofs]));
				ofs += LEN_ITHD;
			}

			for(int i = 0; i < this.vthd.length; i++){
				this.vthd[i] = new Double(DataFormat.hex2unsigned8(data[ofs]));
				ofs += LEN_VTHD;
			}

			for(int i = 0; i < this.distortion_pf.length; i++){
				this.distortion_pf[i] = new Double(DataFormat.hex2unsigned8(data[ofs]));
				ofs += LEN_DISTORTION_PF;
			}

			this.time_remaining_in_subint = DataFormat.hex2unsigned8(data[ofs]);
			ofs += LEN_TIME_REMAINING_IN_SUBINT;

		}catch(Exception e){
			log.warn(e.getMessage());
		}

	}

	public double[] getPREVIOUS_INT_DEMANDS()
	{
		return this.previous_int_demands;
	}

	public double[] getDEMANDS()
	{
		return this.demands;
	}

	public double[] getKW_DMD_FUND_PLUS()
	{
		return this.kw_dmd_fund_plus;
	}

	public double[] getKW_DMD_FUND_ONLY()
	{
		return this.kw_dmd_fund_only;
	}

	public double[] getKVAR_DMD_FUND_PLUS()
	{
		return this.kvar_dmd_fund_plus;
	}

	public double[] getKVAR_DMD_FUND_ONLY()
	{
		return this.kvar_dmd_fund_only;
	}

	public double[] getDISTORTION_KVA_DMD()
	{
		return this.distortion_kva_dmd;
	}

	public double[] getAPPARENT_KVA_DMD()
	{
		return this.apparent_kva_dmd;
	}

	public double[] getV_L_TO_N_FUND_PLUS()
	{
		return this.v_l_to_n_fund_plus;
	}

	public double[] getV_L_TO_N_FUND_ONLY()
	{
		return this.v_l_to_n_fund_only;
	}

	public double[] getV_L_TO_L_FUND_PLUS()
	{
		return this.v_l_to_l_fund_plus;
	}

	public double[] getV_L_TO_L_FUND_ONLY()
	{
		return this.v_l_to_l_fund_only;
	}

	public double[] getCURR_FUND_PLUS(){
		return this.curr_fund_plus;
	}

	public double[] getCURR_FUND_ONLY(){
		return this.curr_fund_only;
	}

	public double getIMPUTED_NEUTRAL_CURR(){
		return this.imputed_neutral_curr;
	}

	public double getPOWER_FACTOR(){
		return this.power_factor;
	}

	public double getFREQUENCY(){
		return this.frequency;
	}

	public Double[] getTDD(){
		return this.tdd;
	}

	public Double[] getITHD(){
		return this.ithd;
	}

	public Double[] getVTHD(){
		return this.vthd;
	}

	public Double[] getDISTORTION_PF(){
		return this.distortion_pf;
	}

	public int getTIME_REMAINING_IN_SUBINT(){
		return this.time_remaining_in_subint;
	}
}
