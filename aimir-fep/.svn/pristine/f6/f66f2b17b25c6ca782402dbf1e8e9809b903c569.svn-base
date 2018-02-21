package com.aimir.fep.meter.parser.amuKepco_2_5_0Table;

import java.text.DecimalFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.TOU_BLOCK;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Hex;

/**
 * Billing Data Format.
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 4. 11. 오후 4:21:30$
 */
public class BillingData {

	private Log log = LogFactory.getLog(BillingData.class);
	// 유효
    private final int OFS_SUMMATION					= 0;	// 에너지 데이터
	private final int OFS_MAX_DEMAND 				= 180;	// 최대 수요 전력 데이터
    private final int OFS_CUM_DEMAND 				= 360;	// 누적 수요  데이터
    private final int OFS_MAX_DEMAND_TIME 			= 540;	// 최대 수요 발생일시
    
    // 무효 
    private final int OFS_SUMMATION_KVAR			= 12;	
    private final int OFS_MAX_DEMAND_KVAR 			= 192;
    private final int OFS_CUM_DEMAND_KVAR 			= 372;
    private final int OFS_OFS_MAX_DEMAND_TIME_KVAR 	= 561;
    
    private final int LEN_SUMMATION					= 4;
    private final int LEN_MAX_DEMAND 				= 4;
    private final int LEN_CUM_DEMAND 				= 4;
    private final int LEN_MAX_DEMAND_TIME 			= 7;
    
    // total과 a등(b, c) 사이의 전력량 간의 간격
    private final int INTERVAL_DEMAND 				= 36;
    // total 과  a등(b, c) 발생일시 간의 간격
    private final int INTERVAL_TIME 				= 63;
    
    private byte[] 		rawData;
    private final int 	NBR_TIERS 					= 2;
    private final int 	CNT_BLOCK 					= 5; 	//total, a,b,c,d
    private double 		ke 							= 1;
    private double 		wTokw						= 0.0001;
    private String 		resetTime					= "";
    private int 		resetCount					= 0;
    DecimalFormat 		dformat 					= new DecimalFormat("###############0.000000");
    
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
	public BillingData(byte[] rawData , String resetTime ,int resetCount) {
		this.rawData 	= rawData;
		this.resetTime  = resetTime;
		this.resetCount = resetCount;
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
	 * Constructor
	 */
	public BillingData(byte[] rawData, double ke , String resetTime , int resetCount){
		this.rawData 	= rawData;
		this.ke			= ke;
		this.resetTime	= resetTime;
		this.resetCount = resetCount;
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
		
		log.debug("################ KEPCO_2_5_0 BillingData Parse Start ################");

		for(int i = 0; i < this.CNT_BLOCK; i++){			
			
			tou_block[i] = new TOU_BLOCK(NBR_TIERS, 
                                         NBR_TIERS, 
                                         NBR_TIERS, 
                                         NBR_TIERS, 
                                         NBR_TIERS);
			/*
			 * 에너지 데이터     -> summation
				수요전력       	-> maxdemand
				누적수요 		-> cumm 
			 */
			if(i ==0){
				tou_block[0].setResetTime(resetTime);
				tou_block[0].setResetCount(resetCount);
			}
			// 누적 전력 에너지
			Double a = new Double(dformat.format((double) (DataFormat.hex2signeddec(DataFormat.LSB2MSB(
								DataFormat.select(rawData,OFS_SUMMATION+(i*INTERVAL_DEMAND),LEN_SUMMATION)))*ke*wTokw)));
                
            Double a1 = new Double(dformat.format((double) (DataFormat.hex2signeddec(DataFormat.LSB2MSB(
            					DataFormat.select(rawData,OFS_SUMMATION_KVAR+(i*INTERVAL_DEMAND),LEN_SUMMATION)))*ke*wTokw)));
            
            log.debug("##### Summations[a] [" + a +"] Hex [ "+Hex.decode(DataFormat.select(rawData,OFS_SUMMATION+(i*INTERVAL_DEMAND),LEN_SUMMATION))+" ]" );
            log.debug("##### Summations[a1] [" + a1 +"] Hex [ "+Hex.decode(DataFormat.select(rawData,OFS_SUMMATION_KVAR+(i*INTERVAL_DEMAND),LEN_SUMMATION))+" ]" );
            
            tou_block[i].setSummations(0, a);
            tou_block[i].setSummations(1, a1);
            
            // 최대 수요전력
            Double b = new Double(dformat.format((double)(DataFormat.hex2signeddec(DataFormat.LSB2MSB(
            					DataFormat.select(rawData,OFS_MAX_DEMAND+(i*36),LEN_MAX_DEMAND)))*ke*wTokw)));
           
            Double b1 = new Double(dformat.format((double)(DataFormat.hex2signeddec(DataFormat.LSB2MSB(
            					DataFormat.select(rawData,OFS_MAX_DEMAND_KVAR+(i*36),LEN_MAX_DEMAND)))*ke*wTokw)));
           
            log.debug("##### CurrDemand[b] [" + b +"] Hex [ "+Hex.decode(DataFormat.select(rawData,OFS_MAX_DEMAND+(i*36),LEN_MAX_DEMAND))+" ]" );
            log.debug("##### CurrDemand[b1] [" + b1 +"] Hex [ "+Hex.decode(DataFormat.select(rawData,OFS_MAX_DEMAND_KVAR+(i*36),LEN_MAX_DEMAND))+" ]" );
            
            tou_block[i].setCurrDemand(0,b);
            tou_block[i].setCurrDemand(1,b1);
            
            // 누적 수요 전력
            Double c = new Double(dformat.format((double) (DataFormat.hex2signeddec(DataFormat.LSB2MSB(
            					DataFormat.select(rawData,OFS_CUM_DEMAND+(i*36),LEN_CUM_DEMAND)))*ke*wTokw)));
            
            Double c1 = new Double(dformat.format((double) (DataFormat.hex2signeddec(DataFormat.LSB2MSB(
            					DataFormat.select(rawData,OFS_CUM_DEMAND_KVAR+(i*36),LEN_CUM_DEMAND)))*ke*wTokw)));
            
            log.debug("##### CumDemand[c] [" + c +"] Hex [ "+Hex.decode(DataFormat.select(rawData,OFS_CUM_DEMAND+(i*36),LEN_CUM_DEMAND))+" ]" );
            log.debug("##### CumDemand[c1] [" + c1 +"] Hex [ "+Hex.decode(DataFormat.select(rawData,OFS_CUM_DEMAND_KVAR+(i*36),LEN_CUM_DEMAND))+" ]" );
            
            tou_block[i].setCumDemand(0,c);
            tou_block[i].setCumDemand(1,c1);
            
            // 발생일시
            tou_block[i].setEventTime(0,(new DateTimeFormat(
            					DataFormat.select(rawData,OFS_MAX_DEMAND_TIME+(i*INTERVAL_TIME),LEN_MAX_DEMAND_TIME))).getDateTime());
            					
            tou_block[i].setEventTime(1,(new DateTimeFormat(
            					DataFormat.select(rawData,OFS_OFS_MAX_DEMAND_TIME_KVAR+(i*INTERVAL_TIME),LEN_MAX_DEMAND_TIME))).getDateTime());
            
            tou_block[i].setCoincident(0, new Double(0.0));
            tou_block[i].setCoincident(1, new Double(0.0));  
		}
		log.debug("################ KEPCO_2_5_0 BillingData Parse End ################");
	}
}


