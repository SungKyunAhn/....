package com.aimir.fep.protocol.mrp.protocol;

public class EDMI_Mk10_DataConstants {

    public static final byte STX = 0x02;
    public static final byte ETX = 0x03;
    public static final byte ACK = 0x06;
    public static final byte CAN = 0x18; //CRC was correct but the command was unsuccessful.
    public static final byte DLE = 16;
    public static final byte XON = 17;
    public static final byte XOFF = 19;
    public static final byte ESC = 0x1b;

    public static final int[] REGISTER_EVENTLOG_STARTDATES			= new int[]{0xD810,0xD811,0xD812,0xD813,0xD814};//Event logs 1-5 start dates, in meter time format.
    
    public static final int[] REGISTER_EVENTLOG_FIRSTENTRYNUMBER    = new int[]{0xD815,0xD816,0xD817,0xD818,0xD819};//Event logs 1-5 first entry number
    
    public static final int[] REGISTER_EVENTLOG_LASTENTRYNUMBER		= new int[]{0xD81A,0xD81B,0xD81C,0xD81D,0xD81E};//Event logs 1-5 last entry
    
    
    public static final byte[] OPTION_LOAD_SURVEYS_1  = new byte[]{'0', 0x00};
    public static final byte[] OPTION_LOAD_SURVEYS_2  = new byte[]{'1', 0x00};
    public static final byte[] OPTION_EVENT_SURVEYS_1 = new byte[]{'2', 0x00}; //SYSTEM EVENT
    public static final byte[] OPTION_EVENT_SURVEYS_2 = new byte[]{'3', 0x00}; //SETUP EVENT
    public static final byte[] OPTION_EVENT_SURVEYS_3 = new byte[]{'4', 0x00}; //TAMPER EVENT
    public static final byte[] OPTION_EVENT_SURVEYS_4 = new byte[]{'5', 0x00}; //TRIG EVENT
    public static final byte[] OPTION_EVENT_SURVEYS_5 = new byte[]{'6', 0x00}; //DIAG EVENT
    public static final byte[] RESPONSE_FOR_ATCMD = new byte[]{'O', 'K', 0x0D, 0x0A};
    
    public static final byte[] CMD_LOGON 					= new byte[]{'L'};
    public static final byte[] CMD_SEQURE_LOGON 			= new byte[]{'K'};
    public static final byte[] CMD_EXIT 					= new byte[]{'X'};
    public static final byte[] CMD_INFORMATION 				= new byte[]{'I'};
    public static final byte[] CMD_READ_REGISTER 			= new byte[]{'R'};
    public static final byte[] CMD_WRITE_REGISTER           = new byte[]{'W'};
    public static final byte[] CMD_READ_REGISTER_EXTENDED 	= new byte[]{'E'};
    //public static final byte[] CMD_READ_REGISTER_ARRAY 		= new byte[]{'A'};
    public static final byte[] CMD_READ_ACCESS_FILE_INFO 	= new byte[]{'F','I'};
    public static final byte[] CMD_READ_ACCESS_FILE_READ 	= new byte[]{'F','R'};
    public static final int    CMD_MULT_READ_EXT			= 0xFFF0;
    public static final byte   CMD_READ_ACCESS_FILE         = 'F';
    
//  ------------------------------------------------------------------------------
//  EDMI Register Definitions (Meter Information)
// ------------------------------------------------------------------------------
    public static final int REGISTER_MODEL_ID_NO 		= 0xF000;//20001EXX    for Mk10E and 20001AXX    for Mk10A
    public static final int REGISTER_EUIPTYPE    		= 0xF001;
    public static final int REGISTER_METER_ID 			= 0xf002;
    public static final int REGISTER_SW_VER 			= 0xF003;
    public static final int REGISTER_CUSTOMERPLATNUM	= 0xF00D;
    public static final int REGISTER_CURRENT_STATUS		= 0xF016;
    public static final int REGISTER_LATCHED_STATUS		= 0xF017;
    public static final int REGISTER_CURR_DATE_TIME		= 0xF03D;
    public static final int REGISTER_DST_DATE_TIME		= 0xF061;
    public static final int REGISTER_DST_ACTIVE			= 0xF015;
    public static final int REGISTER_CT_RATIO_MUL		= 0xF700;
    public static final int REGISTER_PT_RATIO_MUL		= 0xF701;
    public static final int REGISTER_CT_RATIO_DIV		= 0xF702;
    public static final int REGISTER_PT_RATIO_DIV		= 0xF703;
    public static final int REGISTER_MEASURE_METHOD		= 0xF00A;
    public static final int REGISTER_MEASURE_OPTION		= 0xD8A2;
    public static final int REGISTER_NUM_OF_PWR_UP		= 0xF092;
    public static final int REGISTER_LAST_DT_OUTAGE		= 0xFC20;
    public static final int REGISTER_SW_REV_NUMBER		= 0xF090;
    public static final int REGISTER_LAST_DT_RECOVERY	= 0xF093;
    
//  ------------------------------------------------------------------------------
//  EDMI Defined Length (Meter Information)
// ------------------------------------------------------------------------------
    public static final int LEN_MODEL_ID_NO 		= 10;
    public static final int LEN_METER_ID 			= 12;
    public static final int LEN_SW_VER              = 6;
    public static final int LEN_SW_REV_NUMBER		= 4;
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
    //public static final int REGISTER_LAST_DT_DEMAND_RESET	= 0xF54E;
    //public static final int REGISTER_CURRENT_SEASON			= 0x0000F737;
    public static final int REGISTER_NBR_DEMAND_RESETS		= 0xF032;
    public static final int REGISTER_PREVIOUS_TOU		= 0xF541;
    //public static final byte MAX_NBR_DM_CH =12;
    //public static final byte NBR_RATE  =4; //RATE0, RATE1, ... ,RATE7, RATE9(TOTAL)
    
    public static final int REGISTER_TOU_TOTAL						= 0xF54F;
    
    public static final int REGISTER_TOU							= 0xF540;    
    
    public static final int REGISTER_TOU_RATE0 = 0xF732;
    public static final int REGISTER_TOU_RATE1 = 0xF733;
    public static final int REGISTER_TOU_RATE2 = 0xF734;
    public static final int REGISTER_TOU_RATE3 = 0xF735;
    
    public static final int LEN_TOU_RATE0 = 1;
    
//  ------------------------------------------------------------------------------
//  EDMI Defined Length (Billing)
// ------------------------------------------------------------------------------
    public static final int LEN_LAST_DT_DEMAND_RESET	= 6;
    public static final int LEN_NBR_DEMAND_RESETS		= 4;
    public static final int LEN_PREVIOUS_TOU = 238;
    public static final int LEN_DEMAND					= 4;
    public static final int LEN_BILL_ENERGY				= 8;
    public static final int LEN_BILL_MAX_DM				= 4;
    public static final int LEN_BILL_MAX_DM_TIME		= 6;
    
//  ------------------------------------------------------------------------------
//  EDMI Register Definitions (Load Profile)
// ------------------------------------------------------------------------------
    public static final int EDMI_LP_READ_COUNT			= 16;
    public static final int REGISTER_LOADSURVEY_INFO    = 0xF532;
    //public static final int REGISTER_LOADSURVEY_INFO    = 0xFFE0;
    public static final int REGISTER_CHANNEL            = 0xD806;
    public static final int REGISTER_INTERVAL_TIME      = 0xD8B2;

    //public static final int REGISTER_ENTRY_WIDTH		= 0x0305F018;
    //public static final int REGISTER_WIDEST_CHANNEL		= 0x0305F019;
    public static final int REGISTER_STORED_START_TIME	= 0xD8B0;
    //public static final int REGISTER_STORED_TOTAL_LP	= 0x0305F021;
    //public static final int REGISTER_CH_NAME			= 0x0305E400;
    
    public static final int REGISTER_LP_CH_REG			= 0xD840;
    //public static final int REGISTER_LP_CH_SIZE			= 0x0305E100;
    //public static final int REGISTER_LP_CH_TYPE			= 0x0305E200;
    //public static final int REGISTER_LP_CH_UNIT			= 0x0305E300;
    //public static final int REGISTER_LP_CH_RECORD_OFFSET= 0x0305E500;
    //public static final int REGISTER_LP_CH_SCALING		= 0x0305E600;
    public static final int REGISTER_LP_CH_SCALING_FACTOR=0xD808;
    
    //public static final int LP_FILE_ACCESS_POINT		= 0x0305F008;
    
//  ------------------------------------------------------------------------------
//  EDMI Defined Length (Load Profile)
// ------------------------------------------------------------------------------
    //public static final int LEN_LOADSURVEY_INFO         = 325;
    public static final int LEN_INTERVAL_TIME	        = 1;
    public static final int LEN_NBR_LP_CH				= 1;
    public static final int LEN_ENTRY_WIDTH				= 2;
    public static final int LEN_WIDEST_CHANNEL			= 2;
    public static final int LEN_STORED_START_TIME		= 6;
    public static final int LEN_STORED_TOTAL_LP			= 4;
    public static final int LEN_CH_NAME					= 50;
    
    public static final int LEN_LP_CH_STATUS_CONFIG		= 15;
    
    public static final int LEN_LP_STATUS				= 1;
    public static final int LEN_LP_CH_REG				= 2;
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

    
    public static final int REGISTER_PH_A_VOLTAGE	= 0xE000;
    public static final int REGISTER_PH_B_VOLTAGE	= 0xE001;
    public static final int REGISTER_PH_C_VOLTAGE	= 0xE002;
    public static final int REGISTER_PH_A_CURRENT	= 0xE010;
    public static final int REGISTER_PH_B_CURRENT	= 0xE011;
    public static final int REGISTER_PH_C_CURRENT	= 0xE012;
    public static final int REGISTER_PH_A_ANGLE		= 0xE020;
    public static final int REGISTER_PH_B_ANGLE		= 0xE021;
    public static final int REGISTER_PH_C_ANGLE		= 0xE022;
    public static final int REGISTER_PH_A_WATTS		= 0xE030;
    public static final int REGISTER_PH_B_WATTS		= 0xE031;
    public static final int REGISTER_PH_C_WATTS		= 0xE032;
    public static final int REGISTER_PH_A_VAR		= 0xE040;
    public static final int REGISTER_PH_B_VAR		= 0xE041;
    public static final int REGISTER_PH_C_VAR		= 0xE042;
    public static final int REGISTER_PH_A_VA		= 0xE050;
    public static final int REGISTER_PH_B_VA		= 0xE051;
    public static final int REGISTER_PH_C_VA		= 0xE052;
    public static final int REGISTER_PH_TOTAL		= 0xE053;
    public static final int REGISTER_FREQUENCY		= 0xE060;
    
//  ------------------------------------------------------------------------------
//  EDMI Register Definitions (PQM)
// ------------------------------------------------------------------------------
   public static final int REGISTER_PH_A_FUND_VOLTAGE	= 0xE007;
   public static final int REGISTER_PH_B_FUND_VOLTAGE	= 0xE008;
   public static final int REGISTER_PH_C_FUND_VOLTAGE	= 0xE009;
   public static final int REGISTER_PH_A_VOLTAGE_PQM	= 0xE00A;
   public static final int REGISTER_PH_B_VOLTAGE_PQM	= 0xE00B;
   public static final int REGISTER_PH_C_VOLTAGE_PQM	= 0xE00C;
   public static final int REGISTER_VOLTAGE_Z_SEQ		= 0xE00D;
   public static final int REGISTER_VOLTAGE_P_SEQ		= 0xE00E;
   public static final int REGISTER_VOLTAGE_N_SEQ		= 0xE00F;
    
    public static final int LEN_INSTRUMENT_DATA	= 4;    
    public static final int LEN_PQ_DATA = 4;
    
//  ------------------------------------------------------------------------------
//  EDMI Register Definitions (Event Log)
// ------------------------------------------------------------------------------
    public static final int EDMI_EVT_MAX			= 20;
    public static final int EDMI_EVT_READ_COUNT		= 1;
    public static final int REGISTER_NBR_EVT_CH		= 0xD807;

    public static final int REGISTER_EV_CH_REG			= 0x0055E000;
    public static final int REGISTER_EV_CH_SIZE			= 0x0055E100;
    public static final int REGISTER_EV_CH_TYPE			= 0x0055E200;
    public static final int REGISTER_EV_CH_UNIT			= 0x0055E300;
    public static final int REGISTER_EV_CH_RECORD_OFFSET= 0x0055E500;
    public static final int LEN_EVENT_BLOCK			= 6;	
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



    
    public static long SLEEPTIME = 1 * 1000;
    
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
