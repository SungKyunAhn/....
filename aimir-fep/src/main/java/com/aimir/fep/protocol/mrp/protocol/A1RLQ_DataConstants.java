package com.aimir.fep.protocol.mrp.protocol;

import com.aimir.fep.util.FMPProperty;

public class A1RLQ_DataConstants {
	
	public static final byte STX            = 0x02;

	public static final byte CB_WHOAREYOU   = 0x18;
	public static final byte CB_CLASSREAD   = 0x05;
	public static final byte CB_INSTRUMENT  = 0x1c;
	
	public static final byte CLASS_0        = 0x00;
	public static final byte CLASS_2        = 0x02;
	public static final byte CLASS_6        = 0x06;
	public static final byte CLASS_9        = 0x09;
	public static final byte CLASS_10       = 0x0a;
	public static final byte CLASS_11       = 0x0b;
	public static final byte CLASS_12       = 0x0c;
	public static final byte CLASS_14       = 0x0e;
	public static final byte CLASS_15       = 0x0f;
	public static final byte CLASS_16       = 0x10;
	public static final byte CLASS_18       = 0x12;
	
	public static final byte CLASS_50       = 0x32;
	public static final byte CLASS_52       = 0x34;
	public static final byte CLASS_53       = 0x35;
	
	/*------------ Short Format Commands ---------------*/
	public static final byte CB_TERMINATE    = (byte)0x80;
	public static final byte CB_CONTINUEREAD = (byte)0x81;
	public static final byte CB_RECENTLP     = (byte)0x82;
	public static final byte CB_OK           = (byte)0x83;
	public static final byte CB_TAKECONTROL  = (byte)0x84;
	public static final byte CB_QUIT         = (byte)0x85;
	public static final byte CB_BRATE1       = (byte)0x90;
	public static final byte CB_BRATE2       = (byte)0x93; 
	/*--------------------------------------------------*/
	
	/*-------- Function Without Data Commands --------*/ 
	public static final byte FUNC_RESET         = 0x01;	// Demand Reset
	public static final byte FUNC_CLEAR         = 0x0E;	// Clear All Occurrence and Status Bit (ar, sc, pf, tc, dr, & con) and perform function 0D
	public static final byte FUNC_READEDBILL    = 0x10;	// Remote billing read complete
	public static final byte FUNC_READEDALARM   = 0x11;	// Remote alarm read complete
	public static final byte FUNC_BRATE1        = 0x12;	// Set Baud to 9600
	public static final byte FUNC_BRATE2        = 0x13;	// Set Baud to 19200
	public static final byte FUNC_RESETLP       = 0x14;	// Reset the Load Profile System
	public static final byte FUNC_RESETEVENTLOG = 0x15;	// Reset the Event Log
	public static final byte FUNC_COPYCLASS     = 0x17;	// Copy Class 11 to Class 12, “Freeze”
	/*------------------------------------------------*/
	
	/*----------- Function With Data Commands --------*/ 
	public static final byte FUNC_PASSWDCHECK = 0x01;
	public static final byte FUNC_TIMESET     = 0x02;
	public static final byte FUNC_WHOAREYOU   = 0x06;
	public static final byte FUNC_BILL        = 0x07;
	public static final byte FUNC_CALLBACK    = 0x08;
	public static final byte FUNC_PACKETSIZE  = 0x09;
	public static final byte FUNC_TIMESYNC    = 0x0c;
	public static final byte FUNC_SETTIMEOUT  = (byte)0xf2;
	/*------------------------------------------------*/
	
	public static final byte PAD_NULL    = 0x00;
	public static final byte PAD_TIMEOUT = 0x00;	// 0x06 -> 0.5 sec
	//public static final byte PAD_TIMEOUT = 0x06;
	public static final byte ACK = 0x00;
	
	public static final int HEADER  = 5;	// header 다음 데이터 처음 필드 인덱스 (헤더길이).
	public static final int TAIL    = 2;	// tail length (crc,crch,crcl).
	public static final int POS_LEN = 4;	// data length position.

	protected int DEFAULT_RETRY    = Integer.parseInt(FMPProperty.getProperty("Elster_A1RL.WHO.RETRY"));
	protected int DEFAULT_WAITTIME = Integer.parseInt(FMPProperty.getProperty("Elster_A1RL.DEFAULT.TIMEOUT"));
	protected int SHORT_WAITTIME   = Integer.parseInt(FMPProperty.getProperty("Elster_A1RL.SHORT.TIMEOUT"));
	
	public static final int MAX_PACKET_SIZE = 63;	// 0~ 63
	
	public static final int OFS_CUMDR   = 44;
	public static final int OFS_DATATR  = 35;
	
	public static final int LEN_CUMDR   = 1;
	public static final int LEN_DATATR  = 3;
	
	/* -------- class 2 config -----*/
	public static final int OFS_UMTRSN = 0;
	public static final int LEN_UMTRSN = 5;
	
	
	/* -------- TOU block 1 --------*/ 
	public static final int OFS_AKWH1   = 0;
	public static final int OFS_AKW1    = 7;
	public static final int OFS_ATD1    = 10;
	public static final int OFS_AKWCUM1 = 15;
	public static final int OFS_AKWC1   = 18;
	
	public static final int OFS_BKWH1   = 21;
	public static final int OFS_BKW1    = 28;
	public static final int OFS_BTD1    = 31;
	public static final int OFS_BKWCUM1 = 36;
	public static final int OFS_BKWC1   = 39;
	
	public static final int OFS_CKWH1   = 42;
	public static final int OFS_CKW1    = 49;
	public static final int OFS_CTD1    = 52;
	public static final int OFS_CKWCUM1 = 57;
	public static final int OFS_CKWC1   = 60;
	
	public static final int OFS_DKWH1   = 63;
	public static final int OFS_DKW1    = 70;
	public static final int OFS_DTD1    = 73;
	public static final int OFS_DKWCUM1 = 78;
	public static final int OFS_DKWC1   = 81;
	/* -------- TOU block 1 --------*/ 
	
	/* -------- TOU block 2 --------*/ 
	public static final int OFS_AKWH2   = 84;
	public static final int OFS_AKW2    = 91;
	public static final int OFS_ATD2    = 94;
	public static final int OFS_AKWCUM2 = 99;
	public static final int OFS_AKWC2   = 102;
	
	public static final int OFS_BKWH2   = 105;
	public static final int OFS_BKW2    = 112;
	public static final int OFS_BTD2    = 115;
	public static final int OFS_BKWCUM2 = 120;
	public static final int OFS_BKWC2   = 123;
	
	public static final int OFS_CKWH2   = 126;
	public static final int OFS_CKW2    = 133;
	public static final int OFS_CTD2    = 136;
	public static final int OFS_CKWCUM2 = 141;
	public static final int OFS_CKWC2   = 144;
	
	public static final int OFS_DKWH2   = 147;
	public static final int OFS_DKW2    = 154;
	public static final int OFS_DTD2    = 157;
	public static final int OFS_DKWCUM2 = 162;
	public static final int OFS_DKWC2   = 165;
	/* -------- TOU block 2 --------*/ 
	
	/* ----------- Quadrant KVARh data ------------ */
	public static final int OFS_EKVARH4 = 168;
	public static final int OFS_EKVARH3 = 175;
	public static final int OFS_EKVARH2 = 182;
	public static final int OFS_EKVARH1 = 189;
	/* ----------- Quadrant KVARh data ------------ */
	
	public static final int OFS_ETKWH1  = 196;
	public static final int OFS_ETKWH2  = 203;
	public static final int OFS_EAVGPF  = 210;
	
	/* -------- TOU block 1 --------*/ 
	public static final int LEN_AKWH1   = 7;
	public static final int LEN_AKW1    = 3;
	public static final int LEN_ATD1    = 5;
	public static final int LEN_AKWCUM1 = 3;
	public static final int LEN_AKWC1   = 3;
	
	public static final int LEN_BKWH1   = 7;
	public static final int LEN_BKW1    = 3;
	public static final int LEN_BTD1    = 5;
	public static final int LEN_BKWCUM1 = 3;
	public static final int LEN_BKWC1   = 3;
	
	public static final int LEN_CKWH1   = 7;
	public static final int LEN_CKW1    = 3;
	public static final int LEN_CTD1    = 5;
	public static final int LEN_CKWCUM1 = 3;
	public static final int LEN_CKWC1   = 3;
	
	public static final int LEN_DKWH1   = 7;
	public static final int LEN_DKW1    = 3;
	public static final int LEN_DTD1    = 5;
	public static final int LEN_DKWCUM1 = 3;
	public static final int LEN_DKWC1   = 3;
	/* -------- TOU block 1 --------*/ 
	
	/* -------- TOU block 2 --------*/ 
	public static final int LEN_AKWH2   = 7;
	public static final int LEN_AKW2    = 3;
	public static final int LEN_ATD2    = 5;
	public static final int LEN_AKWCUM2 = 3;
	public static final int LEN_AKWC2   = 3;
	
	public static final int LEN_BKWH2   = 7;
	public static final int LEN_BKW2    = 3;
	public static final int LEN_BTD2    = 5;
	public static final int LEN_BKWCUM2 = 3;
	public static final int LEN_BKWC2   = 3;
	
	public static final int LEN_CKWH2   = 7;
	public static final int LEN_CKW2    = 3;
	public static final int LEN_CTD2    = 5;
	public static final int LEN_CKWCUM2 = 3;
	public static final int LEN_CKWC2   = 3;
	
	public static final int LEN_DKWH2   = 7;
	public static final int LEN_DKW2    = 3;
	public static final int LEN_DTD2    = 5;
	public static final int LEN_DKWCUM2 = 3;
	public static final int LEN_DKWC2   = 3;
	/* -------- TOU block 2 --------*/ 
	
	/* ----------- Quadrant KVARh data ------------ */
	public static final int LEN_EKVARH4 = 7;
	public static final int LEN_EKVARH3 = 7;
	public static final int LEN_EKVARH2 = 7;
	public static final int LEN_EKVARH1 = 7;
	/* ----------- Quadrant KVARh data ------------ */
	
	public static final int LEN_ETKWH1  = 7;
	public static final int LEN_ETKWH2  = 7;
	public static final int LEN_EAVGPF  = 2;
	
	/*---------- Class 9 --------------*/
	public static final int OFS_SYSERR  = 1;
	public static final int OFS_SYSWARN = 4;
	public static final int OFS_SYSSTAT = 5;
	public static final int OFS_TD      = 27;
	public static final int OFS_XUME 	= 3;
	
	public static final int LEN_SYSERR  = 3;
	public static final int LEN_SYSWARN = 1;
	public static final int LEN_SYSSTAT = 1;
	public static final int LEN_TD      = 6;
	public static final int LEN_XUM 	= 1;
	
	public static final int LEN_CLASS9 	= 48;//all
	
	/*---------- Class 0 --------------*/
	public static final int OFS_UKE 	= 4;
	public static final int OFS_DPLOCE  = 11;
	public static final int OFS_DPLOCD  = 12;
	public static final int OFS_VTRATIO = 14;
	public static final int OFS_CTRATIO = 17;
	public static final int OFS_XFACTOR = 20;
	
	public static final int LEN_UKE 	= 5;
	public static final int LEN_DPLOCE  = 1;
	public static final int LEN_DPLOCD  = 1;
	public static final int LEN_VTRATIO = 2;
	public static final int LEN_CTRATIO = 2;
	public static final int LEN_XFACTOR = 4;
	
	public static final int LEN_CLASS0 	= 20; //UKH ~ CTRATIO
	
	/*---------- Class 16 --------------*/
	public static final int OFS_EVREC = 0;
	public static final int LEN_EVREC = 7;
	
	/*---------- Class 14 --------------*/
	public static final int OFS_LPLEN   = 4;
	public static final int OFS_DASIZE  = 5;
	public static final int OFS_LPMEM   = 7;
	public static final int OFS_CHANS   = 8;
		
	public static final int LEN_CHANS   = 1;
	public static final int LEN_DASIZE  = 2;
	public static final int LEN_LPLEN   = 1;
	public static final int LEN_LPMEM   = 1;
	
	/*---------- Class 10 --------------*/
	public static final int OFS_MTRSN =5;
	public static final int LEN_MTRSN =5;
	//pq
	public static final int ONE_BLOCK_SIZE = 7;
	public static final int MAX_BLOCK_COUNT = 40;
	
	private final int EVENT_BLOCK_SIZE = 9;
	public static long SLEEPTIME = 1 * 1000;

	/** 
	 * Get CRC High Byte.<p>
	 * @param data
	 * @return
	 */
    public static byte getCRCH(byte[] data){
		int crc = 0;
		crc = calcrc(data);
		return (byte) (crc >> 8);
	}
	
	/** 
	 * Get CRC Low Byte.<p>
	 * @param data
	 * @return
	 */
    public static byte getCRCL(byte[] data){
		int crc = 0;
		crc = calcrc(data);	
		return (byte)(crc & 0x00ff);
	}
    
	/** 
	 * Calculate CRC. <p>
	 */	 
    public static char calcrc(byte[] data) {
		
		int crc = 0;

		for (int i =  0; i < data.length-2; i++) {
			crc = crc ^ data[i] << 8 ;
			
			for(int j = 0; j < 8; j++){

				if((crc & 0x8000) !=0) 
					crc = (crc << 1 ^ 0x1021);
				else 
					crc = crc << 1;
			}
		}

		return (char)(crc & 0xffff);
	} 
	
	public static boolean checkCRC(byte[] src) {
		
		int endidx = 0;
		endidx = src.length-1;

		if(getCRCH(src) == src[endidx-1] && getCRCL(src) == src[endidx])
			return true;

		return false;
	}
}
