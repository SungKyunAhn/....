/** 
 * @(#)MT17.java       1.0 06/07/01 *
 * 
 * Source Definition Class.
 * Copyright (c) 2006-2007 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.a1830rlnTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author Park YeonKyoung yeonkyoung@hanmail.net
 */
public class MT17 implements java.io.Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1596272363984826011L;
	private Log logger = LogFactory.getLog(getClass());

	public MT17() {

	}

	public static String getSource(int sourceid){
		
		switch(sourceid){
			case  0: return "WH delivered";
			case  1: return "VARH delivered";
			case  2: return "WH received";
			case  3: return "VARH received";
			case  4: return "Q1 VARH";
			case  5: return "Q2 VARH";
			case  6: return "Q3 VARH";
			case  7: return "Q4 VARH";
			case  8: return "Relay Input #1";
			case  9: return "Relay Input #2";
			case 10: return "Relay Input #3";
			case 11: return "Relay Input #4";
			case 12: return "Total WH";
			case 13: return "Net WH";
			case 14: return "VARH w/WH Del (Q1 VARH+Q4 VARH)";
			case 15: return "VARH w/WH Rec (Q2 VARH+Q3 VARH)";
			case 16: return "Total VARH";
			case 17: return "Net VAR w/WH Del (Q1 VARH-Q4 VARH)";
			case 18: return "Net VAR w/WH Rec (Q2 VARH-Q3 VARH)";
			case 19: return "Net VAR w/WH Rec (Q3 VARH-Q2 VARH)";
			case 20: return "Net VARH";
			case 21: return "Vectorial kVA delivered = SQRT(WHdel2 + [Q1+Q4]";
			case 22: return "Vectorial kVA delivered = SQRT(WHdel2 + Q12)";
			case 23: return "Vectorial kVA delivered = SQRT(WHdel2 + Q42)";
			case 24: return "Vectorial kVA delivered = SQRT(WHdel2 + [Q1-Q4]2)";
			case 25: return "Vectorial kVA received = SQRT(WHrcv2 + [Q2+Q3]2)";
			case 26: return "Vectorial kVA received = SQRT(WHrcv2 + Q22)";
			case 27: return "Vectorial kVA received = SQRT(WHrcv2 + Q32)";
			case 28: return "Vectorial kVA received = SQRT(WHrcv2 + [Q2-Q3]2)";
			case 29: return "Vectorial kVA received = SQRT(WHrcv2 + [Q3-Q2]2)";
			case 30: return "Vectorial kVA total = SQRT(WHtotal2 + VARHtotal2)";
			case 31: return "Power Factor: 0 & 1";
			case 32: return "Power Factor: 0 & 4";
			case 33: return "Power Factor: 0 & 7";
			case 34: return "Power Factor: 0 & 14";
			case 35: return "Power Factor: 2 & 3";
			case 36: return "Power Factor: 2 & 5";
			case 37: return "Power Factor: 2 & 6";
			case 38: return "Power Factor: 2 & 15";
			case 39: return "Power Factor: 0 & 21";
			case 40: return "Power Factor: 0 & 22";
			case 41: return "Power Factor: 0 & 23";
			case 42: return "Power Factor: 2 & 25";
			case 43: return "Power Factor: 2 & 26";
			case 44: return "Power Factor: 2 & 27";
			case 45: return "Power Factor: 12 & 16";
			case 46: return "Power Factor: 12 & 30";
			case 50: return "Frequency";
			case 51: return "Phase A current";
			case 52: return "Phase B current";
			case 53: return "Phase C current";
			case 54: return "Phase A voltage";
			case 55: return "Phase B voltage";
			case 56: return "Phase C voltage";
			case 57: return "Phase A Watts";
			case 58: return "Phase B Watts";
			case 59: return "Phase C Watts";
			case 60: return "Phase A VA";
			case 61: return "Phase B VA";
			case 62: return "Phase C VA";
			case 63: return "Phase A voltage angle with respect to phase A voltage";			
			case 64: return "Phase B voltage angle with respect to phase";
			case 65: return "Phase C voltage angle with respect to phase";
			case 66: return "Fundamental (1st harmonic) current magnitude,";
			case 67: return "Fundamental (1st harmonic) current magnitude,";
			case 68: return "Fundamental (1st harmonic) current magnitude,";
			case 69: return "Fundamental (1st harmonic) voltage magnitude,";
			case 70: return "Fundamental (1st harmonic) voltage magnitude,";
			case 71: return "Fundamental (1st harmonic) voltage magnitude,";
			case 72: return "2nd harmonic current magnitude, Phase";
			case 73: return "2nd harmonic current magnitude, Phase";
			case 74: return "2nd harmonic current magnitude, Phase";
			case 75: return "2nd harmonic voltage magnitude, Phase";
			case 76: return "2nd harmonic voltage magnitude, Phase";
			case 77: return "2nd harmonic voltage magnitude, Phase";
			case 78: return "Phase A voltage % THD";
			case 79: return "Phase B voltage % THD";
			case 80: return "Phase C voltage % THD";
			case 81: return "Phase A current % THD";
			case 82: return "Phase B current % THD";
			case 83: return "Phase C current % THD";
			case 84: return "Phase A Harmonic Current (sum of 2nd";
			case 85: return "Phase B Harmonic Current (sum of 2nd";
			case 86: return "Phase C Harmonic Current (sum of 2nd";
			case 164: return "Reserved";
			case 165: return "System Watts";
			case 166: return "System VA - arithmetic (VA = Vrms*Irms)";
			case 167: return "Phase A PF";
			case 168: return "Phase B PF";
			case 169: return "Phase C PF";
			case 170: return "System PF - arithmetic";
			case 171: return "Phase A PF Angle";
			case 172: return "Phase B PF Angle";
			case 173: return "Phase C PF Angle";
			case 174: return "System PF Angle - arithmetic";
			case 175: return "Phase A current angle with respect to phase";
			case 176: return "Phase B current angle with respect to phase";
			case 177: return "Phase C current angle with respect to phase";
			case 178: return "Phase A VARs - vectorial";
			case 179: return "Phase B VARs - vectorial";
			case 180: return "Phase C VARs - vectorial";
			case 181: return "System VARs - vectorial";
			case 182: return "System VA - vectorial";
			case 183: return "System VAR - arithmetic";
			case 184: return "System PF - vectorial";
			case 185: return "System PF Angle - vectorial";
			case 186: return "2nd harmonic voltage %, Phase A";
			case 187: return "2nd harmonic voltage %, Phase B";
			case 188: return "2nd harmonic voltage %, Phase C";
			case 189: return "TDD Phase A";
			case 190: return "TDD Phase B";
			case 191: return "TDD Phase C";
			default : return "Reserved";
		}
	}


}
