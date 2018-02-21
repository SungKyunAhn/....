package com.aimir.fep.protocol.mrp.protocol;

/**
 * RequestData Constants
 * 
 * @author Kang, Soyi
 * @version $Rev: 1 $, $Date: 2008-01-05 15:59:15 +0900 $,
 */
public class EDMI_Mk6N_DataConstants
{
    public static final byte STX = 0x02;
    public static final byte ETX = 0x03;
    public static final byte ACK = 0x06;
    public static final byte CAN = 0x18; //CRC was correct but the command was unsuccessful.
    public static final byte DLE = 0x10; //HEX10
    public static final byte XON = 0x11; //HEX11
    public static final byte XOFF = 0x13;//HEX13
    public static final byte ESC = 0x1b;

    public static final byte[] RESPONSE_FOR_ATCMD = new byte[]{'O', 'K', 0x0D, 0x0A};
    
    public static final byte[] CMD_LOGON 					= new byte[]{'L'};
    public static final byte[] CMD_EXIT 					= new byte[]{'X'};
    public static final byte[] CMD_INFORMATION 				= new byte[]{'I'};
    public static final byte[] CMD_READ_REGISTER 			= new byte[]{'R'};
    public static final byte[] CMD_READ_REGISTER_EXTENDED 	= new byte[]{'M'};
    public static final byte[] CMD_READ_REGISTER_ARRAY 		= new byte[]{'A'};
    public static final byte[] CMD_READ_ACCESS_FILE_INFO 	= new byte[]{'F','I'};
    public static final byte[] CMD_READ_ACCESS_FILE_READ 	= new byte[]{'F','R'};
    public static final int CMD_MULT_READ_EXT				= 0x0000FFF0;
    
//  ------------------------------------------------------------------------------
//  EDMI Register Definitions (Meter Information)
// ------------------------------------------------------------------------------
    public static final int REGISTER_MODEL_ID_NO 		= 0x0000F000;
    public static final int REGISTER_METER_ID 			= 0x0000F002;
    public static final int REGISTER_EZIO_STATUS		= 0x0000FCB0;
    public static final int REGISTER_CURRENT_STATUS		= 0x0000F016;
    public static final int REGISTER_LATCHED_STATUS		= 0x0000F017;
    public static final int REGISTER_CURR_DATE_TIME		= 0x0000F03D;
    public static final int REGISTER_DST_DATE_TIME		= 0x0000F061;
    public static final int REGISTER_DST_ACTIVE			= 0x0000F015;
    public static final int REGISTER_CT_RATIO_MUL		= 0x0000F700;
    public static final int REGISTER_PT_RATIO_MUL		= 0x0000F701;
    public static final int REGISTER_CT_RATIO_DIV		= 0x0000F702;
    public static final int REGISTER_PT_RATIO_DIV		= 0x0000F703;
    public static final int REGISTER_MEASURE_METHOD		= 0x0000F00A;
    public static final int REGISTER_MEASURE_OPTION		= 0x0000FCE5;
    public static final int REGISTER_NUM_OF_PWR_UP		= 0x0000F092;
    public static final int REGISTER_LAST_DT_OUTAGE		= 0x0000FC20;
    public static final int REGISTER_LAST_DT_RECOVERY	= 0x0000F093;
    
//  ------------------------------------------------------------------------------
//  EDMI Defined Length (Meter Information)
// ------------------------------------------------------------------------------
    public static final int LEN_MODEL_ID_NO 		= 10;
    public static final int LEN_METER_ID 			= 12;
    public static final int LEN_EZIO_STATUS			= 1;
    public static final int LEN_CURRENT_STATUS		= 16;
    public static final int LEN_LATCHED_STATUS		= 16;
    public static final int LEN_CURR_DATE_TIME		= 6;
    public static final int LEN_DST_DATE_TIME		= 6;
    public static final int LEN_DST_ACTIVE			= 1;
    public static final int LEN_CT_RATIO_MUL		= 4;
    public static final int LEN_PT_RATIO_MUL		= 4;
    public static final int LEN_CT_RATIO_DIV		= 4;
    public static final int LEN_PT_RATIO_DIV		= 4;
    public static final int LEN_MEASURE_METHOD		= 1;
    public static final int LEN_MEASURE_OPTION		= 2;
    public static final int LEN_NUM_OF_PWR_UP		= 4;
    public static final int LEN_LAST_DT_OUTAGE		= 6;
    public static final int LEN_LAST_DT_RECOVERY	= 6;
    
//  ------------------------------------------------------------------------------
//  EDMI Register Definitions (Previous Billing)
// ------------------------------------------------------------------------------
    public static final int REGISTER_LAST_DT_DEMAND_RESET	= 0x0000FC00;
    public static final int REGISTER_CURRENT_SEASON			= 0x0000F737;
    public static final int REGISTER_NBR_DEMAND_RESETS		= 0x0000F032;

    public static final byte MAX_NBR_DM_CH =12;
    public static final byte NBR_RATE  =9; //RATE0, RATE1, ... ,RATE7, RATE9(TOTAL)
    
    public static final int REGISTER_DEMAND							= 0x0000F780;
    
    public static final int REGISTER_PREV_BILL_ENERGY_RATE			= 0x00000020; // Energy, ChX, Previous, Rate 0
    public static final int REGISTER_PREV_BILL_ENERGY_TOTAL			= 0x00000029; // Energy, ChX, Previous, Rate 0
    public static final int REGISTER_PREV_BILL_MAX_DM_RATE			= 0x00001020; // Maximum Demand, ChX, Previous, Rate 0
    public static final int REGISTER_PREV_BILL_MAX_DM_TOTAL			= 0x00001029; // Maximum Demand, ChX, Previous, Rate 0
    public static final int REGISTER_PREV_BILL_MAX_DM_TIME_RATE		= 0x00008020; // Time of Max. Demand, ChX, Previous, Rate 0
    public static final int REGISTER_PREV_BILL_MAX_DM_TIME_TOTAL	= 0x00008029; // Time of Max. Demand, ChX, Previous, Rate 0
    
//  ------------------------------------------------------------------------------
//  EDMI Register Definitions (Current Billing)
// ------------------------------------------------------------------------------
    
    public static final int REGISTER_CURR_BILL_ENERGY_RATE			= 0x00000000;	// Energy, ChX, Current, Rate 0
    public static final int REGISTER_CURR_BILL_MAX_DM_RATE			= 0x00001000;	// Maximum Demand, ChX, Current, Rate 0
    public static final int REGISTER_CURR_BILL_MAX_DM_TIME_RATE		= 0x00008000;	// Time of Max. Demand, ChX, Current, Rate 0
    
//  ------------------------------------------------------------------------------
//  EDMI Defined Length (Billing)
// ------------------------------------------------------------------------------
    public static final int LEN_LAST_DT_DEMAND_RESET	= 6;
    public static final int LEN_CURRENT_SEASON			= 1;
    public static final int LEN_NBR_DEMAND_RESETS		= 1;
    
    public static final int LEN_DEMAND					= 4;
    public static final int LEN_BILL_ENERGY				= 8;
    public static final int LEN_BILL_MAX_DM				= 4;
    public static final int LEN_BILL_MAX_DM_TIME		= 6;
    
//  ------------------------------------------------------------------------------
//  EDMI Register Definitions (Load Profile)
// ------------------------------------------------------------------------------
    public static final int EDMI_LP_READ_COUNT			= 16;
    
    public static final int REGISTER_INTERVAL_TIME		= 0x0305F014;
    public static final int REGISTER_NBR_LP_CH			= 0x0305F012;
    public static final int REGISTER_ENTRY_WIDTH		= 0x0305F018;
    public static final int REGISTER_WIDEST_CHANNEL		= 0x0305F019;
    public static final int REGISTER_STORED_START_TIME	= 0x0305F020;
    public static final int REGISTER_STORED_TOTAL_LP	= 0x0305F021;
    public static final int REGISTER_CH_NAME			= 0x0305E400;
    
    public static final int REGISTER_LP_CH_REG			= 0x0305E000;
    public static final int REGISTER_LP_CH_SIZE			= 0x0305E100;
    public static final int REGISTER_LP_CH_TYPE			= 0x0305E200;
    public static final int REGISTER_LP_CH_UNIT			= 0x0305E300;
    public static final int REGISTER_LP_CH_RECORD_OFFSET= 0x0305E500;
    public static final int REGISTER_LP_CH_SCALING		= 0x0305E600;
    public static final int REGISTER_LP_CH_SCALING_FACTOR=0x0305E800;
    
    public static final int LP_FILE_ACCESS_POINT		= 0x0305F008;
    
//  ------------------------------------------------------------------------------
//  EDMI Defined Length (Load Profile)
// ------------------------------------------------------------------------------
    public static final int LEN_INTERVAL_TIME			= 4;
    public static final int LEN_NBR_LP_CH				= 1;
    public static final int LEN_ENTRY_WIDTH				= 2;
    public static final int LEN_WIDEST_CHANNEL			= 2;
    public static final int LEN_STORED_START_TIME		= 6;
    public static final int LEN_STORED_TOTAL_LP			= 4;
    public static final int LEN_CH_NAME					= 50;
    
    public static final int LEN_LP_CH_STATUS_CONFIG		= 15;
    
    public static final int LEN_LP_CH_REG				= 4;
    public static final int LEN_LP_CH_SIZE				= 2;
    public static final int LEN_LP_CH_TYPE				= 1;
    public static final int LEN_LP_CH_UNIT				= 1;
    public static final int LEN_LP_CH_RECORD_OFFSET		= 2;
    public static final int LEN_LP_CH_SCALING			= 1;
    public static final int LEN_LP_CH_SCALING_FACTOR	= 4;

    public static final int LEN_FI_START_RECORD			= 4;
    public static final int LEN_FI_NBR_OF_RECORD		= 4;
    public static final int LEN_RECORD_SIZE				= 2;
    public static final int LEN_FILE_TYPE				= 1;
    
    public static final int OFS_FI_START_RECORD			= 0;
    public static final int OFS_FI_NBR_OF_RECORD		= 4;
    public static final int OFS_RECORD_SIZE				= 8;
    public static final int OFS_FILE_TYPE				= 10;
            
    public static final int LEN_NBR_EVT_ENTRIES			= 2;
//  ------------------------------------------------------------------------------
//  EDMI Register Definitions (Instrumentation)
// ------------------------------------------------------------------------------
    public static final int REGISTER_ANGLE_VTA_B	= 0x0000E024;
    public static final int REGISTER_ANGLE_VTA_C	= 0x0000E025;
    public static final int REGISTER_FREQUENCY		= 0x0000E060;
    public static final int REGISTER_PH_A_VOLTAGE	= 0x0000E000;
    public static final int REGISTER_PH_B_VOLTAGE	= 0x0000E001;
    public static final int REGISTER_PH_C_VOLTAGE	= 0x0000E002;
    public static final int REGISTER_PH_A_CURRENT	= 0x0000E010;
    public static final int REGISTER_PH_B_CURRENT	= 0x0000E011;
    public static final int REGISTER_PH_C_CURRENT	= 0x0000E012;
    public static final int REGISTER_PH_A_ANGLE		= 0x0000E020;
    public static final int REGISTER_PH_B_ANGLE		= 0x0000E021;
    public static final int REGISTER_PH_C_ANGLE		= 0x0000E022;
    public static final int REGISTER_PH_A_WATTS		= 0x0000E030;
    public static final int REGISTER_PH_B_WATTS		= 0x0000E031;
    public static final int REGISTER_PH_C_WATTS		= 0x0000E032;
    public static final int REGISTER_PH_A_VAR		= 0x0000E040;
    public static final int REGISTER_PH_B_VAR		= 0x0000E041;
    public static final int REGISTER_PH_C_VAR		= 0x0000E042;
    public static final int REGISTER_PH_A_VA		= 0x0000E050;
    public static final int REGISTER_PH_B_VA		= 0x0000E051;
    public static final int REGISTER_PH_C_VA		= 0x0000E052;
    
    public static final int LEN_INSTRUMENT_DATA	= 4;
    
//  ------------------------------------------------------------------------------
//  EDMI Register Definitions (Event Log)
// ------------------------------------------------------------------------------
    public static final int EDMI_EVT_MAX			= 20;
    public static final int EDMI_EVT_READ_COUNT		= 1;
    public static final int REGISTER_NBR_EVT_CH		= 0x0055F012;

    public static final int REGISTER_EV_CH_REG			= 0x0055E000;
    public static final int REGISTER_EV_CH_SIZE			= 0x0055E100;
    public static final int REGISTER_EV_CH_TYPE			= 0x0055E200;
    public static final int REGISTER_EV_CH_UNIT			= 0x0055E300;
    public static final int REGISTER_EV_CH_RECORD_OFFSET= 0x0055E500;

    public static final int LEN_NBR_EVT_CH			= 1;	
    public static final int LEN_EV_CH_STATUS_CONFIG = 10;
    
    public static final int LEN_EV_CH_REG			= 4;
    public static final int LEN_EV_CH_SIZE			= 2;
    public static final int LEN_EV_CH_TYPE			= 1;
    public static final int LEN_EV_CH_UNIT			= 1;
    public static final int LEN_EV_CH_RECORD_OFFSET	= 2;

    public static final int OFS_EV_CH_REG			= 0;
    public static final int OFS_EV_CH_SIZE			= 4;
    public static final int OFS_EV_CH_TYPE			= 6;
    public static final int OFS_EV_CH_UNIT			= 7;
    public static final int OFS_EV_CH_RECORD_OFFSET	= 8;
    
    public static final int EV_FILE_ACCESS_POINT		= 0x0055F008;

//  ------------------------------------------------------------------------------
//   EDMI Register Definitions (PQM)
//  ------------------------------------------------------------------------------
    public static final int REGISTER_PH_A_FUND_VOLTAGE	= 0x0000E007;
    public static final int REGISTER_PH_B_FUND_VOLTAGE	= 0x0000E008;
    public static final int REGISTER_PH_C_FUND_VOLTAGE	= 0x0000E009;
    public static final int REGISTER_PH_A_VOLTAGE_PQM	= 0x0000E00A;
    public static final int REGISTER_PH_B_VOLTAGE_PQM	= 0x0000E00B;
    public static final int REGISTER_PH_C_VOLTAGE_PQM	= 0x0000E00C;
    public static final int REGISTER_VOLTAGE_Z_SEQ		= 0x0000E00D;
    public static final int REGISTER_VOLTAGE_P_SEQ		= 0x0000E00E;
    public static final int REGISTER_VOLTAGE_N_SEQ		= 0x0000E00F;
    public static final int REGISTER_PH_A_FUND_CURRENT	= 0x0000E017;
    public static final int REGISTER_PH_B_FUND_CURRENT	= 0x0000E018;
    public static final int REGISTER_PH_C_FUND_CURRENT	= 0x0000E019;
    public static final int REGISTER_PH_A_CURRENT_PQM	= 0x0000E01A;
    public static final int REGISTER_PH_B_CURRENT_PQM	= 0x0000E01B;
    public static final int REGISTER_PH_C_CURRENT_PQM	= 0x0000E01C;
    public static final int REGISTER_CURRENT_Z_SEQ		= 0x0000E01D;
    public static final int REGISTER_CURRENT_P_SEQ		= 0x0000E01E;
    public static final int REGISTER_CURRENT_N_SEQ		= 0x0000E01F;
    public static final int REGISTER_THD_PH_A_CURRENT	= 0x00009000;
    public static final int REGISTER_THD_PH_B_CURRENT	= 0x00009100;
    public static final int REGISTER_THD_PH_C_CURRENT	= 0x00009200;
    public static final int REGISTER_THD_PH_A_VOLTAGE	= 0x00009300;
    public static final int REGISTER_THD_PH_B_VOLTAGE	= 0x00009400;
    public static final int REGISTER_THD_PH_C_VOLTAGE	= 0x00009500;
    
    public static final int LEN_PQ_DATA = 4;
    
    public static long SLEEPTIME = 1 * 1000;

    /**
     * constructor
     */
    public EDMI_Mk6N_DataConstants()
    {
    }
    
    public static char gencrc_16(char i)
    {
    	char k;
    	char crc;
	    k = (char)(i << 8);
	    crc = 0;
	    for ( int j = 0 ; j < 8 ; j++ ) {
		    if ( (( crc ^ k ) & 0x8000)!=0 )
		    	crc = (char)(( crc << 1 ) ^ 0x1021);
		    else
			    crc <<= 1;
			    k <<= 1;
	    }
	    return(crc);
    }
    
    public static char CalculateCharacterCRC16(char crc, byte c )
    {
    	return (char)( ( crc << 8 ) ^ gencrc_16((char)(((crc >> 8 ) ^ c ) & 0xff) ) );
    }
   
}
