/* 
 * @(#)RegisterIDTable.java       1.0 2009-01-19 *
 * 
 * Kamstrup RID Table
 * Copyright (c) 2009-2010 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
/**
 * @author YK.Park
 */
package com.aimir.fep.meter.parser.kamstrup601Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class RegisterIDTable {    
	
	private byte[] data = null;

    private static Log log = LogFactory.getLog(DataBlock.class);
    
    public final static int DATE = 1003;
    public final static int E1 = 60;
    public final static int E2 = 94;
    public final static int E3 = 63;
    public final static int E4 = 61;
    public final static int E5 = 62;
    public final static int E6 = 95;
    public final static int E7 = 96;
    public final static int E8 = 97;
    public final static int E9 = 110;
    public final static int TA2 = 64;
    public final static int TA3 = 65;
    public final static int V1 = 68;    
    public final static int V2 	=	69;
    public final static int VA 	=	84;
    public final static int VB 	=	 85;
    public final static int M1 	=	 72;
    public final static int M2 	=	 73;
    public final static int HR 	=	1004;
    public final static int INFOEVENT 	=	 113;
    public final static int CLOCK 	=	1002;
    public final static int INFO 	=	99;
    public final static int T1 	=	86;
    public final static int T2 	=	87;
    public final static int T3 	=	88;
    public final static int T4 	=	122;
    public final static int T1_T2 	=	89;
    public final static int P1 	=	91;
    public final static int P2 	=	92;
    public final static int FLOW1 	=	74;
    public final static int FLOW2 	=	75;
    public final static int EFFEKT1 	=	80;
    public final static int MAX_FLOW1DATE1 =	123;
    public final static int MAX_FLOW1 	=	124;
    public final static int MIN_FLOW1DATE1 	=	125;
    public final static int MIN_FLOW1 	=	126;
    public final static int MAX_EFFEKT1DATE1 	=	127;
    public final static int MAX_EFFEKT1 	=	128;
    public final static int MIN_EFFEKT1DATE1 	=	129;
    public final static int MIN_EFFEKT1 	=	130;
    public final static int MAX_FLOW1DATE2 	=	138;
    public final static int MAX_FLOW2 	=	139;
    public final static int MIN_FLOW1DATE2	=	140;
    public final static int MIN_FLOW2 	=	141;
    public final static int MAX_EFFEKT1DATE2 	=	142;
    public final static int MAX_EFFEKT2 	=	143;
    public final static int MIN_EFFEKT1DATE2 	=	144;
    public final static int MIN_EFFEKT2 	=	145;
    public final static int AVR_T1 	=	146;
    public final static int AVR_T2 	=	147;
    public final static int AVR2_T1 	=	149;
    public final static int AVR2_T2 	=	150;
    public final static int TL2 	=	66;
    public final static int TL3 	=	67;
    public final static int XDAY 	=	98;
    public final static int PROG_NO 	=	152;
    public final static int CONFIG_NO_1 	=	153;
    public final static int CONFIG_NO_2 	=	168;
    public final static int SERIE_NO 	=	1001;
    public final static int METER_NO_2 	=	112;
    public final static int METER_NO_1 	=	1010;
    public final static int METER_NO_VA 	=	114;
    public final static int METER_NO_VB 	=	104;
    public final static int METER_TYPE 	=	1005;
    public final static int CHECK_SUM_1 	=	154;
    public final static int HIGH_RES 	=	155;
    public final static int TOPMODUL_ID 	=	157;
    public final static int BOTMODUL_ID 	=	158;
    
    public static String getRid(int rid){
    	
    	switch(rid)
    	{
    	case DATE : return "Current date (YYMMDD)";
    	case E1 : return "Energy register 1: Heat energy";
    	case E2 : return "Energy register 2: Control energy";
    	case E3 : return "Energy register 3: Cooling energy";
    	case E4 : return "Energy register 4: Flow energy";
    	case E5 : return "Energy register 5: Return flow energy";
    	case E6 : return "Energy register 6: Tap water energy";
    	case E7 : return "Energy register 7: Heat energy Y";
    	case E8 : return "Energy register 8: [m3.T1]";
    	case E9 : return "Energy register 9: [m3.T2]";
    	case TA2 : return "Tariff register 2";
    	case TA3 : return "Tariff register 3";
    	case V1 : return "Volume register V1";
    	case V2 : return "Volume register V2";
    	case VA : return "Input register VA";
    	case VB : return "Input register VB";
    	case M1 : return "Mass register V1";
    	case M2 : return "Mass register V2";
    	case HR : return "Operational hour counter";
    	case INFOEVENT : return "Info-event counter";
    	case CLOCK : return "Current time (hhmmss)";
    	case INFO : return "Infocode register, current";
    	case T1 : return "Current flow temperature";
    	case T2 : return "Current return flow temperature";
    	case T3 : return "Current temperature T3";
    	case T4 : return "Current temperature T4";
    	case T1_T2 : return "Current temperature difference";
    	case P1 : return "Pressure in flow";
    	case P2 : return "Pressure in return flow";
    	case FLOW1 : return "Current flow in flow";
    	case FLOW2 : return "Current flow in return flow";
    	case EFFEKT1 : return "Current power calculated on the basis of V1-T1-T2";
    	case MAX_FLOW1DATE1 : return "Date for max. this year";
    	case MAX_FLOW1 : return "Max. value this year";
    	case MIN_FLOW1DATE1: return "Date for min. this year";
    	case MIN_FLOW1 : return "Min. value this year";
    	case MAX_EFFEKT1DATE1 : return "Date for max. this year";
    	case MAX_EFFEKT1 : return "Max. value this year";
    	case MIN_EFFEKT1DATE1 : return "Date for min. this myear";
    	case MIN_EFFEKT1 : return "Min. value this year";
    	case MAX_FLOW1DATE2 : return "Date for max. this year";
    	case MAX_FLOW2 : return "Max. value this year";
    	case MIN_FLOW1DATE2 : return "Date for min. this month";
    	case MIN_FLOW2 : return "Min. value this month";
    	case MAX_EFFEKT1DATE2 : return "Date for max. this month";
    	case MAX_EFFEKT2 : return "Max. value this month";
    	case MIN_EFFEKT1DATE2 : return "Date for min. this month";
    	case MIN_EFFEKT2 : return "Min. value this month";
    	case AVR_T1 : return "Year-to-date average for T1";
    	case AVR_T2 : return "Year-to-date average for T2";
    	case AVR2_T1 : return "Month-to-date average for T1";
    	case AVR2_T2 : return "Month-to-date average for T2";
    	case TL2 : return "Tariff limit 2";
    	case TL3 : return "Tariff limit 3";
    	case XDAY : return "Target date (reading date)";
    	case PROG_NO : return "Program no. ABCCCCCC";
    	case CONFIG_NO_1 : return "Config no. DDDEE";
    	case CONFIG_NO_2 : return "Config. no. FFGGMN";
    	case SERIE_NO : return "Serial no. (unique number for each meter)";
    	case METER_NO_2 : return "Customer number (8 most important digits)";
    	case METER_NO_1 : return "Customer number (8 less important digits)";
    	case METER_NO_VA : return "Meter no. for VA";
    	case METER_NO_VB : return "Meter no. for VB";
    	case METER_TYPE : return "Software edition";
    	case CHECK_SUM_1 : return "Software check sum";
    	case HIGH_RES : return "High-resolution energy register for testing purposes";
    	case TOPMODUL_ID : return "ID number for top module";
    	case BOTMODUL_ID : return "ID number for base module";
    	}
    	return "Unknown ["+rid+"]";
    }
}