/** 
 * @(#)PowerQuality.java       1.0 06/06/09 *
 * 
 * Power Quality Data Class.
 * Copyright (c) 2006-2007 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
package com.aimir.fep.meter.data;

/**
 * @author PYK
 */
public class PowerQualityMonitor implements java.io.Serializable{	

	/**
	 * 
	 */
	private static final long serialVersionUID = -2687531071017978574L;
	private int vol_a_sag_cnt;
	private long vol_a_sag_dur;
	private int vol_b_sag_cnt;
	private long vol_b_sag_dur;
	private int vol_c_sag_cnt;
	private long vol_c_sag_dur;
	private int vol_sag_cnt;  
	private long vol_sag_dur;
	private int vol_sag_stat;
	private int vol_a_swell_cnt; 
	private long vol_a_swell_dur; 
	private int vol_b_swell_cnt;
	private long vol_b_swell_dur; 
	private int vol_c_swell_cnt;
	private long vol_c_swell_dur; 
	private int vol_swell_cnt;
	private long vol_swell_dur;
	private int vol_swell_stat;	
	private int vol_cut_cnt;        		 
	private long vol_cut_dur;
	private int vol_cut_stat;
	private int vol_flicker_cnt;
	private long vol_flicker_dur;	 
	private int vol_flicker_stat;
	private int vol_fluctuation_cnt;
	private long vol_fluctuation_dur;
	private int vol_fluctuation_stat;
	private int low_vol_cnt; 
	private long low_vol_dur; 
	private int low_vol_stat;
	private int high_vol_cnt;
	private long high_vol_dur;
	private int high_vol_stat;
	private int high_frequency_cnt;
	private long high_frequency_dur;
	private int high_frequency_stat;
	private int polarity_cross_phase_cnt;	
	private long polarity_cross_phase_dur;
	private int polarity_cross_phase_stat;
	private int reverse_pwr_cnt;
	private long reverse_pwr_dur;
	private int reverse_pwr_stat;
	private int low_curr_cnt;
	private long low_curr_dur;
	private int low_curr_stat;
	private int over_curr_cnt;
	private long over_curr_dur;
	private int over_curr_stat;
	private int pfactor_cnt;
	private long pfactor_dur;
	private int pfactor_stat;
	private int harmonic_cnt;
	private long harmonic_dur;
	private int harmonic_stat;
	private int thd_curr_cnt;
	private long thd_curr_dur;
	private int thd_curr_stat;
	private int thd_vol_cnt;
	private long thd_vol_dur;
	private int thd_vol_stat;
	private int tdd_cnt;
	private long tdd_dur;	 
	private int tdd_stat;
	private int distortion_a_cnt;
	private long distortion_a_dur;
	private int distortion_b_cnt;	
	private long distortion_b_dur;	
	private int distortion_c_cnt;
	private long distortion_c_dur;  
	private int distortion_cnt;
	private long distortion_dur;
	private int distortion_stat;	
	private int imbalance_vol_cnt;
	private long imbalance_vol_dur;
	private int imbalance_vol_stat;
	private int imbalance_curr_cnt;    
	private long imbalance_curr_dur;  
	private int imbalance_curr_stat;
	private int service_vol_cnt;
	private long service_vol_dur;
	private int service_vol_stat;
	private int high_neutral_curr_cnt;	
	private long high_neutral_curr_dur;
	private int high_neutral_curr_stat;
	
	public PowerQualityMonitor() 
	{

	}
	
	public int getVOL_A_SAG_CNT()
	{
		return this.vol_a_sag_cnt;
	}
	
	public long getVOL_A_SAG_DUR()
	{
		return this.vol_a_sag_dur;
	}
	
	public int getVOL_B_SAG_CNT()
	{
		return this.vol_b_sag_cnt;
	}
	 
	public long getVOL_B_SAG_DUR()
	{
		return this.vol_b_sag_dur;
	}
	
	public int getVOL_C_SAG_CNT()
	{
		return this.vol_c_sag_cnt;
	}
	
	public long getVOL_C_SAG_DUR()
	{
		return this.vol_c_sag_dur;
	}
	 
	public int getVOL_SAG_CNT()
	{
		return this.vol_sag_cnt;  
	}
	  
	public long getVOL_SAG_DUR()
	{
		return this.vol_sag_dur;
	}
	
	public int getVOL_SAG_STAT()
	{
		return this.vol_sag_stat;
	}
	
	public int getVOL_A_SWELL_CNT()
	{
		return this.vol_a_swell_cnt; 
	}
	 
	public long getVOL_A_SWELL_DUR()
	{
		return this.vol_a_swell_dur; 
	}
	 
	public int getVOL_B_SWELL_CNT()
	{
		return this.vol_b_swell_cnt;
	}
	
	public long getVOL_B_SWELL_DUR()
	{
		return this.vol_b_swell_dur; 
	}
	 
	public int getVOL_C_SWELL_CNT()
	{
		return this.vol_c_swell_cnt;
	}
	
	public long getVOL_C_SWELL_DUR()
	{
		return this.vol_c_swell_dur; 
	}
	 
	public int getVOL_SWELL_CNT()
	{
		return this.vol_swell_cnt;
	}
	
	public long getVOL_SWELL_DUR()
	{
		return this.vol_swell_dur;
	}
	
	public int getVOL_SWELL_STAT()
	{
		return this.vol_swell_stat;	
	}
		
	public int getVOL_CUT_CNT()
	{
		return this.vol_cut_cnt;        		 
	}
	        		 
	public long getVOL_CUT_DUR()
	{
		return this.vol_cut_dur;
	}
	
	public int getVOL_CUT_STAT()
	{
		return this.vol_cut_stat;
	}
	
	public int getVOL_FLICKER_CNT()
	{
		return this.vol_flicker_cnt;
	}
	
	public long getVOL_FLICKER_DUR()
	{
		return this.vol_flicker_dur;	 
	}
		 
	public int getVOL_FLICKER_STAT()
	{
		return this.vol_flicker_stat;
	}
	
	public int getVOL_FLUCTUATION_CNT()
	{
		return this.vol_fluctuation_cnt;
	}
	
	public long getVOL_FLUCTUATION_DUR()
	{
		return this.vol_fluctuation_dur;
	}
	
	public int getVOL_FLUCTUATION_STAT()
	{
		return this.vol_fluctuation_stat;
	}
	
	public int getLOW_VOL_CNT()
	{
		return this.low_vol_cnt; 
	}
	 
	public long getLOW_VOL_DUR()
	{
		return this.low_vol_dur; 
	}
	 
	public int getLOW_VOL_STAT()
	{
		return this.low_vol_stat;
	}
	
	public int getHIGH_VOL_CNT()
	{
		return this.high_vol_cnt;
	}
	
	public long getHIGH_VOL_DUR()
	{
		return this.high_vol_dur;
	}
	
	public int getHIGH_VOL_STAT()
	{
		return this.high_vol_stat;
	}
	
	public int getHIGH_FREQUENCY_CNT()
	{
		return this.high_frequency_cnt;
	}
	
	public long getHIGH_FREQUENCY_DUR()
	{
		return this.high_frequency_dur;
	}
	
	public int getHIGH_FREQUENCY_STAT()
	{
		return this.high_frequency_stat;
	}
	
	public int getPOLARITY_CROSS_PHASE_CNT()
	{
		return this.polarity_cross_phase_cnt;	
	}
		
	public long getPOLARITY_CROSS_PHASE_DUR()
	{
		return this.polarity_cross_phase_dur;
	}
	
	public int getPOLARITY_CROSS_PHASE_STAT()
	{
		return this.polarity_cross_phase_stat;
	}
	
	public int getREVERSE_PWR_CNT()
	{
		return this.reverse_pwr_cnt;
	}
	
	public long getREVERSE_PWR_DUR()
	{
		return this.reverse_pwr_dur;
	}
	
	public int getREVERSE_PWR_STAT()
	{
		return this.reverse_pwr_stat;
	}
	
	public int getLOW_CURR_CNT()
	{
		return this.low_curr_cnt;
	}
	
	public long getLOW_CURR_DUR()
	{
		return this.low_curr_dur;
	}
	
	public int getLOW_CURR_STAT()
	{
		return this.low_curr_stat;
	}
	
	public int getOVER_CURR_CNT()
	{
		return this.over_curr_cnt;
	}
	
	public long getOVER_CURR_DUR()
	{
		return this.over_curr_dur;
	}
	
	public int getOVER_CURR_STAT()
	{
		return this.over_curr_stat;
	}
	
	public int getPFACTOR_CNT()
	{
		return this.pfactor_cnt;
	}
	
	public long getPFACTOR_DUR()
	{
		return this.pfactor_dur;
	}
	
	public int getPFACTOR_STAT()
	{
		return this.pfactor_stat;
	}
	
	public int getHARMONIC_CNT()
	{
		return this.harmonic_cnt;
	}
	
	public long getHARMONIC_DUR()
	{
		return this.harmonic_dur;
	}
	
	public int getHARMONIC_STAT()
	{
		return this.harmonic_stat;
	}
	
	public int getTHD_CURR_CNT()
	{
		return this.thd_curr_cnt;
	}
	
	public long getTHD_CURR_DUR()
	{
		return this.thd_curr_dur;
	}
	
	public int getTHD_CURR_STAT()
	{
		return this.thd_curr_stat;
	}
	
	public int getTHD_VOL_CNT()
	{
		return this.thd_vol_cnt;
	}
	
	public long getTHD_VOL_DUR()
	{
		return this.thd_vol_dur;
	}
	
	public int getTHD_VOL_STAT()
	{
		return this.thd_vol_stat;
	}
	
	public int getTDD_CNT()
	{
		return this.tdd_cnt;
	}
	
	public long getTDD_DUR()
	{
		return this.tdd_dur;
	}
		 
	public int getTDD_STAT()
	{
		return this.tdd_stat;
	}
	
	public int getDISTORTION_A_CNT()
	{
		return this.distortion_a_cnt;
	}
	
	public long getDISTORTION_A_DUR()
	{
		return this.distortion_a_dur;
	}
	
	public int getDISTORTION_B_CNT()
	{
		return this.distortion_b_cnt;
	}
		
	public long getDISTORTION_B_DUR()
	{
		return this.distortion_b_dur;
	}
		
	public int getDISTORTION_C_CNT()
	{
		return this.distortion_c_cnt;
	}
	
	public long getDISTORTION_C_DUR()
	{
		return this.distortion_c_dur;
	}
	  
	public int getDISTORTION_CNT()
	{
		return this.distortion_cnt;
	}
	
	public long getDISTORTION_DUR()
	{
		return this.distortion_dur;
	}
	
	public int getDISTORTION_STAT()
	{
		return this.distortion_stat;
	}
		
	public int getIMBALANCE_VOL_CNT()
	{
		return this.imbalance_vol_cnt;
	}
	
	public long getIMBALANCE_VOL_DUR()
	{
		return this.imbalance_vol_dur;
	}
	
	public int getIMBALANCE_VOL_STAT()
	{
		return this.imbalance_vol_stat;
	}
	
	public int getIMBALANCE_CURR_CNT()
	{
		return this.imbalance_curr_cnt;
	}
	    
	public long getIMBALANCE_CURR_DUR()
	{
		return this.imbalance_curr_dur;
	}
	  
	public int getIMBALANCE_CURR_STAT()
	{
		return this.imbalance_curr_stat;
	}
	
	public int getSERVICE_VOL_CNT()
	{
		return this.service_vol_cnt;
	}
	
	public long getSERVICE_VOL_DUR()
	{
		return this.service_vol_dur;
	}
	
	public int getSERVICE_VOL_STAT()
	{
		return this.service_vol_stat;
	}
	
	public int getHIGH_NEUTRAL_CURR_CNT()
	{
		return this.high_neutral_curr_cnt;
	}
		
	public long getHIGH_NEUTRAL_CURR_DUR()
	{
		return this.high_neutral_curr_dur;
	}
	
	public int getHIGH_NEUTRAL_CURR_STAT()
	{
		return this.high_neutral_curr_stat;
	}
		
	public void setVOL_A_SAG_CNT(int vol_a_sag_cnt)
	{
		this.vol_a_sag_cnt = vol_a_sag_cnt;
	}
	
	public void setVOL_A_SAG_DUR(long vol_a_sag_dur)
	{
		this.vol_a_sag_dur = vol_a_sag_dur;
	}
	public void setVOL_B_SAG_CNT(int vol_b_sag_cnt)
	{
		this.vol_b_sag_cnt = vol_b_sag_cnt;
	}
	 
	public void setVOL_B_SAG_DUR(long vol_b_sag_dur)
	{
		this.vol_b_sag_dur = vol_b_sag_dur;
	}
	
	public void setVOL_C_SAG_CNT(int vol_c_sag_cnt)
	{
		this.vol_c_sag_cnt = vol_c_sag_cnt;
	}
	
	public void setVOL_C_SAG_DUR(long vol_c_sag_dur)
	{
		this.vol_c_sag_dur = vol_c_sag_dur;
	}
	 
	public void setVOL_SAG_CNT(int vol_sag_cnt)
	{
		this.vol_sag_cnt = vol_sag_cnt;
	}
	  
	public void setVOL_SAG_DUR(long vol_sag_dur)
	{
		this.vol_sag_dur = vol_sag_dur;
	}
	
	public void setVOL_SAG_STAT(int vol_sag_stat)
	{
		this.vol_sag_stat = vol_sag_stat;
	}
	
	public void setVOL_A_SWELL_CNT(int vol_a_swell_cnt)
	{
		this.vol_a_swell_cnt = vol_a_swell_cnt;
	}
	 
	public void setVOL_A_SWELL_DUR(long vol_a_swell_dur)
	{
		this.vol_a_swell_dur = vol_a_swell_dur;
	}
	 
	public void setVOL_B_SWELL_CNT(int vol_b_swell_cnt)
	{
		this.vol_b_swell_cnt = vol_b_swell_cnt;
	}
	
	public void setVOL_B_SWELL_DUR(long vol_b_swell_dur)
	{
		this.vol_b_swell_dur = vol_b_swell_dur;
	}
	 
	public void setVOL_C_SWELL_CNT(int vol_c_swell_cnt)
	{
		this.vol_c_swell_cnt = vol_c_swell_cnt;
	}
	
	public void setVOL_C_SWELL_DUR(long vol_c_swell_dur)
	{
		this.vol_c_swell_dur = vol_c_swell_dur;
	}
	 
	public void setVOL_SWELL_CNT(int vol_swell_cnt)
	{
		this.vol_swell_cnt = vol_swell_cnt;
	}
	
	public void setVOL_SWELL_DUR(long vol_swell_dur)
	{
		this.vol_swell_dur = vol_swell_dur;
	}
	
	public void setVOL_SWELL_STAT(int vol_swell_stat)
	{
		this.vol_swell_stat = vol_swell_stat;
	}
		
	public void setVOL_CUT_CNT(int vol_cut_cnt)
	{
		this.vol_cut_cnt = vol_cut_cnt;
	}
	        		 
	public void setVOL_CUT_DUR(long vol_cut_dur)
	{
		this.vol_cut_dur = vol_cut_dur;
	}
	
	public void setVOL_CUT_STAT(int vol_cut_stat)
	{
		this.vol_cut_stat = vol_cut_stat;
	}
	
	public void setVOL_FLICKER_CNT(int vol_flicker_cnt)
	{
		this.vol_flicker_cnt = vol_flicker_cnt;
	}
	
	public void setVOL_FLICKER_DUR(long vol_flicker_dur)
	{
		this.vol_flicker_dur = vol_flicker_dur;
	}
		 
	public void setVOL_FLICKER_STAT(int vol_flicker_stat)
	{
		this.vol_flicker_stat = vol_flicker_stat;
	}
	
	public void setVOL_FLUCTUATION_CNT(int vol_fluctuation_cnt)
	{
		this.vol_fluctuation_cnt = vol_fluctuation_cnt;
	}
	
	public void setVOL_FLUCTUATION_DUR(long vol_fluctuation_dur)
	{
		this.vol_fluctuation_dur = vol_fluctuation_dur;
	}
	
	public void setVOL_FLUCTUATION_STAT(int vol_fluctuation_stat)
	{
		this.vol_fluctuation_stat = vol_fluctuation_stat;
	}
	
	public void setLOW_VOL_CNT(int low_vol_cnt)
	{
		this.low_vol_cnt = low_vol_cnt;
	}
	 
	public void setLOW_VOL_DUR(long low_vol_dur)
	{
		this.low_vol_dur = low_vol_dur;
	}
	 
	public void setLOW_VOL_STAT(int low_vol_stat)
	{
		this.low_vol_stat = low_vol_stat;
	}
	
	public void setHIGH_VOL_CNT(int high_vol_cnt)
	{
		this.high_vol_cnt = high_vol_cnt;
	}
	
	public void setHIGH_VOL_DUR(long high_vol_dur)
	{
		this.high_vol_dur = high_vol_dur;
	}
	
	public void setHIGH_VOL_STAT(int high_vol_stat)
	{
		this.high_vol_stat = high_vol_stat;
	}
	
	public void setHIGH_FREQUENCY_CNT(int high_frequency_cnt)
	{
		this.high_frequency_cnt = high_frequency_cnt;
	}
	
	public void setHIGH_FREQUENCY_DUR(long high_frequency_dur)
	{
		this.high_frequency_dur = high_frequency_dur;
	}
	
	public void setHIGH_FREQUENCY_STAT(int high_frequency_stat)
	{
		this.high_frequency_stat = high_frequency_stat;
	}
	
	public void setPOLARITY_CROSS_PHASE_CNT(int polarity_cross_phase_cnt)
	{
		this.polarity_cross_phase_cnt = polarity_cross_phase_cnt;
	}
		
	public void setPOLARITY_CROSS_PHASE_DUR(long polarity_cross_phase_dur)
	{
		this.polarity_cross_phase_dur = polarity_cross_phase_dur;
	}
	
	public void setPOLARITY_CROSS_PHASE_STAT(int polarity_cross_phase_stat)
	{
		this.polarity_cross_phase_stat = polarity_cross_phase_stat;
	}
	
	public void setREVERSE_PWR_CNT(int reverse_pwr_cnt)
	{
		this.reverse_pwr_cnt = reverse_pwr_cnt;
	}
	
	public void setREVERSE_PWR_DUR(long reverse_pwr_dur)
	{
		this.reverse_pwr_dur = reverse_pwr_dur;
	}
	
	public void setREVERSE_PWR_STAT(int reverse_pwr_stat)
	{
		this.reverse_pwr_stat = reverse_pwr_stat;
	}
	
	public void setLOW_CURR_CNT(int low_curr_cnt)
	{
		this.low_curr_cnt = low_curr_cnt;
	}
	
	public void setLOW_CURR_DUR(long low_curr_dur)
	{
		this.low_curr_dur = low_curr_dur;
	}
	
	public void setLOW_CURR_STAT(int low_curr_stat)
	{
		this.low_curr_stat = low_curr_stat;
	}
	
	public void setOVER_CURR_CNT(int over_curr_cnt)
	{
		this.over_curr_cnt = over_curr_cnt;
	}
	
	public void setOVER_CURR_DUR(long over_curr_dur)
	{
		this.over_curr_dur = over_curr_dur;
	}
	
	public void setOVER_CURR_STAT(int over_curr_stat)
	{
		this.over_curr_stat = over_curr_stat;
	}
	
	public void setPFACTOR_CNT(int pfactor_cnt)
	{
		this.pfactor_cnt = pfactor_cnt;
	}
	
	public void setPFACTOR_DUR(long pfactor_dur)
	{
		this.pfactor_dur = pfactor_dur;
	}
	
	public void setPFACTOR_STAT(int pfactor_stat)
	{
		this.pfactor_stat = pfactor_stat;
	}
	
	public void setHARMONIC_CNT(int harmonic_cnt)
	{
		this.harmonic_cnt = harmonic_cnt;
	}
	
	public void setHARMONIC_DUR(long harmonic_dur)
	{
		this.harmonic_dur = harmonic_dur;
	}
	
	public void setHARMONIC_STAT(int harmonic_stat)
	{
		this.harmonic_stat = harmonic_stat;
	}
	
	public void setTHD_CURR_CNT(int thd_curr_cnt)
	{
		this.thd_curr_cnt = thd_curr_cnt;
	}
	
	public void setTHD_CURR_DUR(long thd_curr_dur)
	{
		this.thd_curr_dur = thd_curr_dur;
	}
	
	public void setTHD_CURR_STAT(int thd_curr_stat)
	{
		this.thd_curr_stat = thd_curr_stat;
	}
	
	public void setTHD_VOL_CNT(int thd_vol_cnt)
	{
		this.thd_vol_cnt = thd_vol_cnt;
	}
	
	public void setTHD_VOL_DUR(long thd_vol_dur)
	{
		this.thd_vol_dur = thd_vol_dur;
	}
	
	public void setTHD_VOL_STAT(int thd_vol_stat)
	{
		this.thd_vol_stat = thd_vol_stat;
	}
	
	public void setTDD_CNT(int tdd_cnt)
	{
		this.tdd_cnt = tdd_cnt;
	}
	
	public void setTDD_DUR(long tdd_dur)
	{
		this.tdd_dur = tdd_dur;
	}
		 
	public void setTDD_STAT(int tdd_stat)
	{
		this.tdd_stat = tdd_stat;
	}
	
	public void setDISTORTION_A_CNT(int distortion_a_cnt)
	{
		this.distortion_a_cnt = distortion_a_cnt;
	}
	
	public void setDISTORTION_A_DUR(long distortion_a_dur)
	{
		this.distortion_a_dur = distortion_a_dur;
	}
	
	public void setDISTORTION_B_CNT(int distortion_b_cnt)
	{
		this.distortion_b_cnt = distortion_b_cnt;
	}
		
	public void setDISTORTION_B_DUR(long distortion_b_dur)
	{
		this.distortion_b_dur = distortion_b_dur;
	}
		
	public void setDISTORTION_C_CNT(int distortion_c_cnt)
	{
		this.distortion_c_cnt = distortion_c_cnt;
	}
	
	public void setDISTORTION_C_DUR(long distortion_c_dur)
	{
		this.distortion_c_dur = distortion_c_dur;
	}
	  
	public void setDISTORTION_CNT(int distortion_cnt)
	{
		this.distortion_cnt = distortion_cnt;
	}
	
	public void setDISTORTION_DUR(long distortion_dur)
	{
		this.distortion_dur = distortion_dur;
	}
	
	public void setDISTORTION_STAT(int distortion_stat)
	{
		this.distortion_stat = distortion_stat;
	}
		
	public void setIMBALANCE_VOL_CNT(int imbalance_vol_cnt)
	{
		this.imbalance_vol_cnt = imbalance_vol_cnt;
	}
	
	public void setIMBALANCE_VOL_DUR(long imbalance_vol_dur)
	{
		this.imbalance_vol_dur = imbalance_vol_dur;
	}
	
	public void setIMBALANCE_VOL_STAT(int imbalance_vol_stat)
	{
		this.imbalance_vol_stat = imbalance_vol_stat;
	}
	
	public void setIMBALANCE_CURR_CNT(int imbalance_curr_cnt)
	{
		this.imbalance_curr_cnt = imbalance_curr_cnt;
	}
	    
	public void setIMBALANCE_CURR_DUR(long imbalance_curr_dur)
	{
		this.imbalance_curr_dur = imbalance_curr_dur;
	}
	  
	public void setIMBALANCE_CURR_STAT(int imbalance_curr_stat)
	{
		this.imbalance_curr_stat = imbalance_curr_stat;
	}
	
	public void setSERVICE_VOL_CNT(int service_vol_cnt)
	{
		this.service_vol_cnt = service_vol_cnt;
	}
	
	public void setSERVICE_VOL_DUR(long service_vol_dur)
	{
		this.service_vol_dur = service_vol_dur;
	}
	
	public void setSERVICE_VOL_STAT(int service_vol_stat)
	{
		this.service_vol_stat = service_vol_stat;
	}
	
	public void setHIGH_NEUTRAL_CURR_CNT(int high_neutral_curr_cnt)
	{
		this.high_neutral_curr_cnt = high_neutral_curr_cnt;
	}
		
	public void setHIGH_NEUTRAL_CURR_DUR(long high_neutral_curr_dur)
	{
		this.high_neutral_curr_dur = high_neutral_curr_dur;
	}
	
	public void setHIGH_NEUTRAL_CURR_STAT(int high_neutral_curr_stat)
	{
		this.high_neutral_curr_stat = high_neutral_curr_stat;
	}
	
    /**
     * get string
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        sb.append("PowerQuality{\n");
        sb.append("vol_a_sag_cnt=[").append(vol_a_sag_cnt).append("],");
        sb.append("vol_a_sag_dur=[").append(vol_a_sag_dur).append("],");
        sb.append("vol_b_sag_cnt=[").append(vol_b_sag_cnt).append("],");
        sb.append("vol_b_sag_dur=[").append(vol_b_sag_dur).append("],");
        sb.append("vol_c_sag_cnt=[").append(vol_c_sag_cnt).append("],");
        sb.append("vol_c_sag_dur=[").append(vol_c_sag_dur).append("],\n");
        sb.append("vol_sag_cnt=[").append(vol_sag_cnt).append("],");  
        sb.append("vol_sag_dur=[").append(vol_sag_dur).append("],");
        sb.append("vol_sag_stat=[").append(vol_sag_stat).append("],\n");
        sb.append("vol_a_swell_cnt=[").append(vol_a_swell_cnt).append("],");
        sb.append("vol_a_swell_dur=[").append(vol_a_swell_dur).append("],"); 
        sb.append("vol_b_swell_cnt=[").append(vol_b_swell_cnt).append("],");
        sb.append("vol_b_swell_dur=[").append(vol_b_swell_dur).append("],"); 
        sb.append("vol_c_swell_cnt=[").append(vol_c_swell_cnt).append("],");
        sb.append("vol_c_swell_dur=[").append(vol_c_swell_dur).append("],\n"); 
        sb.append("vol_swell_cnt=[").append(vol_swell_cnt).append("],");
        sb.append("vol_swell_dur=[").append(vol_swell_dur).append("],");
        sb.append("vol_swell_stat=[").append(vol_swell_stat).append("],\n"); 
        sb.append("vol_cut_cnt=[").append(vol_cut_cnt).append("],"); 
        sb.append("vol_cut_dur=[").append(vol_cut_dur).append("],");
        sb.append("vol_cut_stat=[").append(vol_cut_stat).append("],\n");
        sb.append("vol_flicker_cnt=[").append(vol_flicker_cnt).append("],");
        sb.append("vol_flicker_dur=[").append(vol_flicker_dur).append("],");     
        sb.append("vol_flicker_stat=[").append(vol_flicker_stat).append("],\n");
        sb.append("vol_fluctuation_cnt=[").append(vol_fluctuation_cnt).append("],");
        sb.append("vol_fluctuation_dur=[").append(vol_fluctuation_dur).append("],");
        sb.append("vol_fluctuation_stat=[").append(vol_fluctuation_stat).append("],\n");
        sb.append("low_vol_cnt=[").append(low_vol_cnt).append("],"); 
        sb.append("low_vol_dur=[").append(low_vol_dur).append("],"); 
        sb.append("low_vol_stat=[").append(low_vol_stat).append("],\n");
        sb.append("high_vol_cnt=[").append(high_vol_cnt).append("],");
        sb.append("high_vol_dur=[").append(high_vol_dur).append("],");
        sb.append("high_vol_stat=[").append(high_vol_stat).append("],\n");
        sb.append("high_frequency_cnt=[").append(high_frequency_cnt).append("],");
        sb.append("high_frequency_dur=[").append(high_frequency_dur).append("],");
        sb.append("high_frequency_stat=[").append(high_frequency_stat).append("],\n");
        sb.append("polarity_cross_phase_cnt=[").append(polarity_cross_phase_cnt).append("],");   
        sb.append("polarity_cross_phase_dur=[").append(polarity_cross_phase_dur).append("],");
        sb.append("polarity_cross_phase_stat=[").append(polarity_cross_phase_stat).append("],\n");
        sb.append("reverse_pwr_cnt=[").append(reverse_pwr_cnt).append("],");
        sb.append("reverse_pwr_dur=[").append(reverse_pwr_dur).append("],");
        sb.append("reverse_pwr_stat=[").append(reverse_pwr_stat).append("],\n");
        sb.append("low_curr_cnt=[").append(low_curr_cnt).append("],");
        sb.append("low_curr_dur=[").append(low_curr_dur).append("],");
        sb.append("low_curr_stat=[").append(low_curr_stat).append("],\n");
        sb.append("over_curr_cnt=[").append(over_curr_cnt).append("],");
        sb.append("over_curr_dur=[").append(over_curr_dur).append("],");
        sb.append("over_curr_stat=[").append(over_curr_stat).append("],");
        sb.append("pfactor_cnt=[").append(pfactor_cnt).append("],");
        sb.append("pfactor_dur=[").append(pfactor_dur).append("],");
        sb.append("pfactor_stat=[").append(pfactor_stat).append("],\n");
        sb.append("harmonic_cnt=[").append(harmonic_cnt).append("],");
        sb.append("harmonic_dur=[").append(harmonic_dur).append("],");
        sb.append("harmonic_stat=[").append(harmonic_stat).append("],\n");
        sb.append("thd_curr_cnt=[").append(thd_curr_cnt).append("],");
        sb.append("thd_curr_dur=[").append(thd_curr_dur).append("],");
        sb.append("thd_curr_stat=[").append(thd_curr_stat).append("],\n");
        sb.append("thd_vol_cnt=[").append(thd_vol_cnt).append("],");
        sb.append("thd_vol_dur=[").append(thd_vol_dur).append("],");
        sb.append("thd_vol_stat=[").append(thd_vol_stat).append("],\n");
        sb.append("tdd_cnt=[").append(tdd_cnt).append("],");
        sb.append("tdd_dur=[").append(tdd_dur).append("],");     
        sb.append("tdd_stat=[").append(tdd_stat).append("],\n");
        sb.append("distortion_a_cnt=[").append(distortion_a_cnt).append("],");
        sb.append("distortion_a_dur=[").append(distortion_a_dur).append("],");
        sb.append("distortion_b_cnt=[").append(distortion_b_cnt).append("],");   
        sb.append("distortion_b_dur=[").append(distortion_b_dur).append("],");   
        sb.append("distortion_c_cnt=[").append(distortion_c_cnt).append("],");
        sb.append("distortion_c_dur=[").append(distortion_c_dur).append("],\n");  
        sb.append("distortion_cnt=[").append(distortion_cnt).append("],");
        sb.append("distortion_dur=[").append(distortion_dur).append("],");
        sb.append("distortion_stat=[").append(distortion_stat).append("],\n");    
        sb.append("imbalance_vol_cnt=[").append(imbalance_vol_cnt).append("],");
        sb.append("imbalance_vol_dur=[").append(imbalance_vol_dur).append("],");
        sb.append("imbalance_vol_stat=[").append(imbalance_vol_stat).append("],\n");
        sb.append("imbalance_curr_cnt=[").append(imbalance_curr_cnt).append("],");    
        sb.append("imbalance_curr_dur=[").append(imbalance_curr_dur).append("],");  
        sb.append("imbalance_curr_stat=[").append(imbalance_curr_stat).append("],\n");
        sb.append("service_vol_cnt=[").append(service_vol_cnt).append("],");
        sb.append("service_vol_dur=[").append(service_vol_dur).append("],");
        sb.append("service_vol_stat=[").append(service_vol_stat).append("],\n");
        sb.append("high_neutral_curr_cnt=[").append(high_neutral_curr_cnt).append("],");  
        sb.append("high_neutral_curr_dur=[").append(high_neutral_curr_dur).append("],");
        sb.append("high_neutral_curr_stat=[").append(high_neutral_curr_stat).append("]}\n");

        return sb.toString();
    }
}
