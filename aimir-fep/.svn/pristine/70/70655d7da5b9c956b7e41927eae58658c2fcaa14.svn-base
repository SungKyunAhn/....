package com.aimir.fep.meter.parser.amuLsrwRs232Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;

/**
 * LSRW RS232 Information Field
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 3. 3. 오후 1:34:08$
 */
public class LSRWRS232_INFO {

	private static Log log = LogFactory.getLog(LSRWRS232_INFO.class);
	
	private static final int LEN_INFO_HEADER					= 33;	// Billing Data를 제외한 Header부분의 길이
	private static final int OFS_BILLING_DATA_START				= 33;
	
	private static final int OFS_SW_VER 						= 0;
	private static final int OFS_HW_VER 						= 1;
	//private static final int OFS_METER_SERIAL					= 2;
	/** 2010.5.31
	 * protocol(1)+제조사(1)+제조년도(1)+일련번호(3)
	 * 20byte Serial 중에서 일련번호 3byte만 추출  
	 */	
	private static final int OFS_METER_SERIAL					= 5;	
	private static final int OFS_CURRENT_TIME					= 22;

	private static final int LEN_SW_VER 						= 1; 
	private static final int LEN_HW_VER 						= 1; 
	//private static final int LEN_METER_SERIAL					= 20;
	private static final int LEN_METER_SERIAL					= 3;
	private static final int LEN_CURRENT_TIME					= 11;
	
	private byte[] 			rawData;
	private double 			ke 									= 1;
	// Billing Data
	public LSRWRS232_PB		lsRwPB								= null;
	
	/**
	 * Constructor
	 */
	public LSRWRS232_INFO(byte[] rawData , double ke) {
		this.rawData = rawData;
		this.ke = ke;
		try {
			parse();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * get LSRW_PB 
	 * @return
	 */
	public LSRWRS232_PB getLsRwPB() {
		return lsRwPB;
	}

	/**
	 * set LSRW_PB 
	 * @param lsRwPB
	 */
	public void setLsRwPB(LSRWRS232_PB lsRwPB) {
		this.lsRwPB = lsRwPB;
	}

	/**
	 * get KE 계기정수
	 * @return
	 */
	public Double getKe(){
		return this.ke;
	}
	
	/**
	 * set KE 
	 * @param ke
	 */
	public void setKe(double ke){
		this.ke =ke;
	}
	
	
	
	/**
     * get S/W version
     * @return
     */
    public String getSwVer(){
        
        String ret = "";
        try{
            ret = new String(DataFormat.select(rawData,OFS_SW_VER,LEN_SW_VER)).trim();

        }catch(Exception e){
            log.warn("invalid model->"+e.getMessage());
        }
        return ret;
    }
    
    /**
     * get H/W version
     * @return
     */
    public String getHwVer(){
        
        String ret = "";
        try{
            ret = new String(DataFormat.select(rawData,OFS_HW_VER,LEN_HW_VER)).trim();

        }catch(Exception e){
            log.warn("invalid model->"+e.getMessage());
        }
        return ret;
    }
    
    /**
     * get Meter Serial
     * @return	
     */
    public String getMeterSerial(){
        
        String ret = new String();
        try{
            int serial =  DataUtil.getIntToBytes(DataFormat.select(rawData,OFS_METER_SERIAL,LEN_METER_SERIAL));             
            ret = Integer.toString(serial);
           
        }catch(Exception e){
            log.warn("invalid model->"+e.getMessage());
        }
        return ret;
    }
    
    /**
	 * get Current Time Class
	 * @return	curTime
	 */
	public CurrentTimeData getCurrentTime() throws Exception {
		
		CurrentTimeData ret = null;
		try {
			ret = new CurrentTimeData(DataFormat.select(rawData,OFS_CURRENT_TIME,LEN_CURRENT_TIME));
		} catch (Exception e) {
			log.warn("[LSRW_INFO ] get Current Time Data Failed " + e.getMessage());
		}
		return ret;
	}
    
   
	/**
	 * Inforamtion Field Parse 
	 * @throws Exception
	 */
	public void parse() throws Exception{
		
		try{
			byte[] billingData = new byte[rawData.length - LEN_INFO_HEADER ];
			System.arraycopy(rawData, OFS_BILLING_DATA_START, billingData, 0, billingData.length);
			this.lsRwPB	= new LSRWRS232_PB(billingData, ke);
			
		}catch(Exception e){
			log.error("AMU LSRW_INFO Parse Failed ");
			throw e;
		}
	}
	
    public String toString()
    {
    
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("LSRW INFO DATA[");        
            sb.append("(SW_VER=").append(getSwVer()).append("),");
            sb.append("(HW_VER=").append(getHwVer()).append("),");
            sb.append("(Meter Serial=").append(getMeterSerial()).append("),");
            sb.append("(CurrentTime timepStamp=").append(getCurrentTime().getTimeStamp()).append("),");
            sb.append("]\n");
        }catch(Exception e){
            log.warn("LSRW_INFO toString  Error =>"+e.getMessage());
        }

        return sb.toString();
    }
    
}


