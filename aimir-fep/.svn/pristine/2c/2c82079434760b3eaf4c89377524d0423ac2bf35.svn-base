package com.aimir.fep.meter.parser.amuKepco_dlmsTable;

import java.text.DecimalFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.TOU_BLOCK;
import com.aimir.fep.meter.parser.amuKepco_2_5_0Table.DateTimeFormat;
import com.aimir.fep.util.DataFormat;

/**
 * Billing Data Format.
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 4. 20. 오후 3:07:39$
 */
public class BillingData {

	private Log log = LogFactory.getLog(BillingData.class);

    private final int OFS_SUMMATION					= 0;	// 유효 전력 
    private final int OFS_SUMMATION_KVAR			= 4;	// 무효 전력 
	private final int OFS_MAX_DEMAND 				= 8;	// 유효 수요 전력 
    private final int OFS_CUM_DEMAND 				= 10;	// 유효 누적 수요 
    private final int OFS_MAX_DEMAND_TIME 			= 14;	// 유효 최대 수요 발생일시
	
    private final int LEN_SUMMATION					= 4;
    private final int LEN_MAX_DEMAND 				= 2;
    private final int LEN_CUM_DEMAND 				= 4;
    private final int LEN_MAX_DEMAND_TIME 			= 7;
    
    
    // T1 ,T2 ,T3 사이의 전력량 간의 간격
    private final int INTERVAL_DEMAND 				= 21;
    
    private byte[] rawData;
    private final int NBR_TIERS 					= 2;
    private final int CNT_BLOCK 					= 3; 	//T1 , T2 , T3
    private double ke 								= 1;
    DecimalFormat dformat 							= new DecimalFormat("###############0.000000");
    
    private TOU_BLOCK[] tou_block;
    
    /**
	 * Constructor
	 */
	public BillingData(byte[] rawData) {
		this.rawData 	= rawData;
		this.tou_block 	= new TOU_BLOCK[this.CNT_BLOCK];
		
		try {
			parseBillingData();
		} catch (Exception e) {
            log.warn("BillingData Parse Error :",e);
		}
	}
	
	/**
	 * Constructor
	 */
	public BillingData(byte[] rawData, double ke){
		this.rawData 	= rawData;
		this.ke			= ke;
		this.tou_block 	= new TOU_BLOCK[this.CNT_BLOCK];
		
		try {
			parseBillingData();
		} catch (Exception e) {
            log.warn("BillingData Parse Error :",e);
		}
	}
	
	/**
	 * get raw Data
	 * @return
	 */
	public byte[] getRawData() {
		return rawData;
	}

	/**
	 * set raw Data
	 * @param rawData
	 */
	public void setrawData(byte[] rawData) {
		this.rawData = rawData;
	}
	
	/**
	 * get TOU_BLOCK
	 * @return
	 */
	public TOU_BLOCK[] getTou_block() {
		return tou_block;
	}

	/**
	 * set TOU_BLOCK
	 * @param touBlock
	 */
	public void setTou_block(TOU_BLOCK[] touBlock) {
		tou_block = touBlock;
	}

	/**
	 * Parse Billing Data
	 * @throws Exception
	 */
	private void parseBillingData() throws Exception {
		
		log.debug("################ KEPCO DLSM BillingData Parse Start ################");

		for(int i = 0; i < this.CNT_BLOCK; i++){			
			
			tou_block[i] = new TOU_BLOCK(NBR_TIERS, 
                                         NBR_TIERS, 
                                         NBR_TIERS, 
                                         NBR_TIERS, 
                                         NBR_TIERS);
			/*
			 * 에너지 데이터 	-> summation
				수요전력       	-> maxdemand
				누적수요 		-> cumm 
			 */
			// 총 누적 전력량
			Double a = new Double(dformat.format((double) (DataFormat.hex2signeddec(DataFormat.LSB2MSB(
								DataFormat.select(rawData,OFS_SUMMATION+(i*INTERVAL_DEMAND),LEN_SUMMATION)))*ke*0.001)));
            tou_block[i].setSummations(0, a);
                
            Double a1 = new Double(dformat.format((double) (DataFormat.hex2signeddec(DataFormat.LSB2MSB(
            					DataFormat.select(rawData,OFS_SUMMATION_KVAR+(i*INTERVAL_DEMAND),LEN_SUMMATION)))*ke*0.001)));
            tou_block[i].setSummations(1, a1);
            
            // 누적 수요 전력량
            Double b = new Double(dformat.format((double) (DataFormat.hex2signeddec(DataFormat.LSB2MSB(
            					DataFormat.select(rawData,OFS_CUM_DEMAND+(i*INTERVAL_DEMAND),LEN_CUM_DEMAND)))*0.001)));
            tou_block[i].setCumDemand(0,b);
            
            Double b1 = new Double(0.0);
            tou_block[i].setCumDemand(1,b1);
            
            // 수요 최대 전력량
            Double c = new Double(dformat.format((double) (DataFormat.hex2signeddec(DataFormat.LSB2MSB(
            					DataFormat.select(rawData,OFS_MAX_DEMAND+(i*INTERVAL_DEMAND),LEN_MAX_DEMAND)))*ke*0.001)));
            tou_block[i].setCurrDemand(0,c);
            
            Double c1 = new Double(0.0);
            tou_block[i].setCurrDemand(1,c1);
            
            tou_block[i].setCoincident(0, new Double(0.0));
            tou_block[i].setCoincident(1, new Double(0.0));  
            
            // 발생일시
            tou_block[i].setEventTime(0,(new DateTimeFormat(
            					DataFormat.select(rawData,OFS_MAX_DEMAND_TIME+(i*INTERVAL_DEMAND),LEN_MAX_DEMAND_TIME))).getDateTime());
            tou_block[i].setEventTime(1, null);
            			
		}
		log.debug("################ KEPCO DLSM BillingData Parse End ################");
	}
}



