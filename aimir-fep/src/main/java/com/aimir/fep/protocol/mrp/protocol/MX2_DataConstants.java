package com.aimir.fep.protocol.mrp.protocol;

/**
 * @author kskim
 *
 */
public class MX2_DataConstants {
	
	public enum BypassCMD{
		TOUCALENDAR,SUMMERTIME;
	}
	
	
	public enum Calendar{
		Present((byte)0x00),
		Future((byte)0x01),
		Nothing((byte)0xff);
		
		private byte cal; 
		Calendar(byte cal){
			this.cal = cal;
		}
		public byte getValue(){
			return this.cal;
		}
		
	}
	
	public enum Season{
		Season1((byte)0x01),
		Season2((byte)0x02),
		Season3((byte)0x03),
		Season4((byte)0x04);
		private byte code;
		Season(byte code){
			this.code = code;
		}
		public byte getValue(){
			return this.code;
		}
	}
	
	
	public static final byte[] MeterModel = "Mitsubishi-MX2  ".getBytes();
	
	
	public static final int CMD_ALL = 0;
    


    /*
     * Table ID 목록
     */
    public static final char TABLE_METER_ID		=	0x09D3;
    public static final char TABLE_METER_RATING	=	0x09D5;
    public static final char TABLE_METER_TIME   = 0x0C30;	//Time Synchronize (MX2_AMR_Communication_Specification-2011-06-16_Signed.pdf:35p)
    public static final char TABLE_BILLINGDATE_TIME = 0x0AC1;
    public static final char TABLE_PRESENT_ENERGY = 0x0911;
    public static final char TABLE_PREVIOUS_MAX_DM = 0x0A51;
    public static final char TABLE_PREVIOUS_ENERGY = 0x0A21;
    public static final char TABLE_PREVIOUS_CUM_DM = 0x0A6C;
    public static final char TABLE_MULTIPLIER = 0x09D1;
    public static final char TABLE_ERROR_CODE = 0x09F6;
    public static final char TABLE_EVENT_LOG = 0x0AD3;
    public static final char TABLE_LOAD_RECENT_DATA = 0x0BA0;
    public static final char TABLE_MANUAL_DEMAND_RESET = 0x0CF0;
    public static final char TABLE_UPDATE_ABSOLUTE_POINTER = 0x0CF2;
    public static final char TABLE_CLEAR_MEASUR_LP = 0x0CF6;
    public static final char TABLE_TIMESYNCH = 0x0C30;
    public static final char TABLE_SUMMERTIME1 = 0x0E43;
    public static final char TABLE_SUMMERTIME2 = 0x0E44;
    public static final char TABLE_SUMMERTIME3 = 0x0E45;
    public static final char TABLE_PHASE_ANGLE_V = 0x09B1;
    public static final char TABLE_PHASE_ANGLE_I = 0x09B2;
    public static final char TABLE_CHARACTER_CODE = 0x09D8;
    public static final char TABLE_LONG_CHARACTER_CODE = 0x09D9;
    public static final char TABLE_DAYLIGHT_SAVING_TIME = 0x09F0; // Daylight saving present time(Date & time)
    
    /*
     * Display Item Setting Table List
     */
    public static final char TABLE_NORMAL_DISPLAY_ITEMS_SELECT = 0x0E30;
    public static final char TABLE_NORMAL_DISPLAY_ID_SET1 = 0x0E31;
    public static final char TABLE_NORMAL_DISPLAY_ID_SET2 = 0x0E32;
    public static final char TABLE_ALTERVATE_DISPLAY_ITEMS_SELECT = 0x0E33;
    public static final char TABLE_ALTERVATE_DISPLAY_ID_SET1 = 0x0E34;
    public static final char TABLE_ALTERVATE_DISPLAY_ID_SET2 = 0x0E35;
    public static final char TABLE_TEST_DISPLAY_ITEMS_SELECT = 0x0E36;
    public static final char TABLE_TEST_DISPLAY_ID = 0x0E37;
    
    /*
     * TOU Calendar Table ID List
     */
    public static final char TABLE_ACTIVATION_DATE = 0x0D00;
    public static final char TABLE_SEASON_CHANGE = 0x0D01;
    public static final char TABLE_DAY_PATTERN_SEASON1 = 0x0D02;
    public static final char TABLE_DAY_PATTERN_SEASON2 = 0x0D03;
    public static final char TABLE_DAY_PATTERN_SEASON3 = 0x0D04;
    public static final char TABLE_DAY_PATTERN_SEASON4 = 0x0D05;
    public static final char TABLE_FIXED_RECURRING_HOLIDAY_SET1 = 0x0D06;
    public static final char TABLE_FIXED_RECURRING_HOLIDAY_SET2 = 0x0D07;
    public static final char TABLE_NON_RECURRING_HOLIDAY = 0x0D08;
    public static final char TABLE_END_MESSAGE = 0x0D1C;
    
    /*
     * Data Size
     */
    public static final int LEN_MeterId		=	1;
    public static final int LEN_CRC			=	2;	//crc 길이
    public static final int LEN_HEADER		=	6;	//packet 헤더 길이 (stp:1, identity:1, ctrl:1, seq-nbr:1, length:2)
    

    public static long SLEEPTIME = 1 * 1000;

    public static final int[] CHANNEL_IDXS = new int[]{0,1,3,4,5,6};
    public static final int CNT_CHANNEL = CHANNEL_IDXS.length;
    public static final int CNT_LPDATA_RECORD = 240; //(0~239)


	
    
    /**
     * constructor
     */
    public MX2_DataConstants()
    {
    }
}
