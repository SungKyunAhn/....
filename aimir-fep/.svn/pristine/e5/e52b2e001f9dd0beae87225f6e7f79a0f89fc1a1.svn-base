package com.aimir.fep.meter.parser.amuKepco_2_5_0Table;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.parser.amuKepco_dlmsTable.LPComparator;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.Util;

/**
 * KEPCO v2.5.0 LP DATA Field
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 4. 12. 오전 11:41:37$
 */
public class KEPCO_2_5_0_LP {

	private Log log = LogFactory.getLog(KEPCO_2_5_0_LP.class);
	
	private static final int	OFS_LP_COUNT		= 0;
	private static final int	OFS_LP_DATA_SET 	= 1;
	
	private static final int	LEN_LP_DATA_SET 	= 12;

    /* ************************************************************** */
    /**
     *  ONE LP DATA Feild Information
     *  하나의  LP DATA(12byte)의  Description
     **/
    public static final int CHANNEL_CNT				= 4;
    
    public static final int OFS_PULSE 				= 0;
    public static final int OFS_LP_STATUS			= 7;
    public static final int OFS_LP_DATE 			= 8;
    
    public static final int LEN_PULSE 				= 2;
    public static final int LEN_LP_DATE 			= 4;
	
    public static final int MASK_LP_DATA	 		= 0x00003fff;
    //public static final int MASK_CH_1	 			= 0x000fff30;
    //public static final int MASK_CH_2	 			= 0x03fff000;
    //public static final int MASK_CH_3	 			= 0xfff30000;
  
    /* ************************************************************** */
    private int 			resolution				= 15;
    private int 			regK					= 1;
    private int 			ctvt					= 1;
    private double 			ke 						= 1;
    DecimalFormat 			dformat 				= new DecimalFormat("###############0.000000");
    private byte[] 			rawData 				= null;
    
    /**
	 * Constructor
	 */
	public KEPCO_2_5_0_LP(byte[] rawData) {
		this.rawData = rawData;
	}	
	
	/**
	 * Constructor 
	 * @param data - read data (header,crch,crcl)
	 */
	public KEPCO_2_5_0_LP(byte[] rawData, int regK, double ke) {
		this.rawData 	= rawData;
		this.regK 		= regK;
        this.ke 		= ke;
	}
	
	/**
	 * Constructor 
	 * @param data 
	 */
	public KEPCO_2_5_0_LP(byte[] rawData, int regK, int ctvt , double ke ) {
		this.rawData 	= rawData;
		this.regK 		= regK;
        this.ke 		= ke;
        this.ctvt		= ctvt;
	}
	
	/**
	 * get Interval Time
	 * @return
	 */
	public int getINTERVAL_TIME() {
        return resolution;
    }
	
	/**
     * get LP COUNT
     * @return 
     */
    public int getLpCount(){
        
    	int ret = 0;
        try{
        	ret = DataFormat.hex2unsigned8(rawData[OFS_LP_COUNT]);
        }catch(Exception e){
            log.warn("get LP COUNT Failed --> "+e.getMessage());
        }

        return ret;
    }
    
    /**
	 * LP DATA Parse
	 * @return
	 * @throws Exception
	 */
    @SuppressWarnings("unchecked")
	public LPData[] parse() throws Exception {
		 
        ArrayList<LPData> list = new ArrayList<LPData>();
	    LPData[] interval = new LPData[getLpCount()];
	    log.debug("interval.length ="+interval.length);
	    resolution = getINTERVAL_TIME();
	    for (int i = 0; i < interval.length; i++) {
	    	
	    	byte[] blk = new byte[LEN_LP_DATA_SET];
            
            blk =DataFormat.select(rawData,OFS_LP_DATA_SET+(i*LEN_LP_DATA_SET),LEN_LP_DATA_SET);
            log.debug("LP DATA SET : " + Hex.decode(blk));
            list.add(parseChannel(blk));
	    }
		    
	    Collections.sort(list,LPComparator.TIMESTAMP_ORDER);        
        return checkEmptyLP(list);
	    
	}
    
    /**
	 * get LPDATA Channel
	 * @param block
	 * @return
	 * @throws Exception
	 */
    private LPData parseChannel(byte[] block) throws Exception{

        LPData lpdata 	= new LPData();

        Double[] ch  	= new Double[CHANNEL_CNT];	// 전력량
        Double[] v  	= new Double[CHANNEL_CNT];	// 전력
        
        
        double dCh0 = (double)((DataFormat.hex2dec(DataFormat.LSB2MSB(DataFormat.select(block, OFS_PULSE, 4))) & MASK_LP_DATA)) *ke*0.001;
    	double dCh1 = (double)((DataFormat.hex2dec(DataFormat.LSB2MSB(DataFormat.select(block, OFS_PULSE+1, 4)))>>>  6) & MASK_LP_DATA) *ke*0.001;
    	double dCh2 = (double)((DataFormat.hex2dec(DataFormat.LSB2MSB(DataFormat.select(block, OFS_PULSE+2, 4)))>>> 12) & MASK_LP_DATA)  *ke*0.001;
    	double dCh3 = (double)((DataFormat.hex2dec(DataFormat.LSB2MSB(DataFormat.select(block, OFS_PULSE+3, 4)))>>> 18) & MASK_LP_DATA)  *ke*0.001;
    	/*
    	double dCh0 = (double)((DataFormat.hex2dec(DataFormat.select(block, OFS_PULSE, 4)) & MASK_CH_0)) *ke*0.001;
    	double dCh1 = (double)((DataFormat.hex2dec(DataFormat.select(block, OFS_PULSE+1, 4)) & MASK_CH_1) >> 6) *ke*0.001;
    	double dCh2 = (double)((DataFormat.hex2dec(DataFormat.select(block, OFS_PULSE+2, 4)) & MASK_CH_2) >> 12) *ke*0.001;
    	double dCh3 = (double)((DataFormat.hex2dec(DataFormat.select(block, OFS_PULSE+3, 4)) & MASK_CH_3) >> 18) *ke*0.001;
    	*/
    	log.debug("### CH0 [" +dCh0+"]");
    	log.debug("### CH1 [" +dCh1+"]");
    	log.debug("### CH2 [" +dCh2+"]");
    	log.debug("### CH3 [" +dCh3+"]");
    	// 전력량 && 전력
    	ch[0] = new Double(dformat.format(dCh0));
    	 v[0] = new Double(dformat.format(ch[0]*((double)60/resolution)));
    	 
    	ch[1] = new Double(dformat.format(dCh1));
    	 v[1] = new Double(dformat.format(ch[1]*((double)60/resolution)));
    	 
    	ch[2] = new Double(dformat.format(dCh2));
    	 v[2] = new Double(dformat.format(ch[2]*((double)60/resolution)));
    	 
    	ch[3] = new Double(dformat.format(dCh3));
    	 v[3] = new Double(dformat.format(ch[3]*((double)60/resolution)));
    	
    	for(int i = 0; i < 4 ; i++ ){
	        log.debug("ch["+i+"] :"+" [" +ch[i] +"]");
	        log.debug("v["+i+"] :"+" [" +v[i] +"]");
        }
        log.debug("LPIntervalTime : " + new LPIntervalTime(DataFormat.LSB2MSB(DataFormat.select(
                            block,OFS_LP_DATE, LEN_LP_DATE))).getIntervalTime());
        lpdata.setV(v);
        lpdata.setCh(ch);
        lpdata.setFlag(0);
        lpdata.setPF(getPF(ch[0], ch[1]));
        lpdata.setDatetime(new LPIntervalTime(DataFormat.LSB2MSB(DataFormat.select(
                            block,OFS_LP_DATE, LEN_LP_DATE))).getIntervalTime());
        return lpdata;
    }
      
    /**
	 * get LPDATA Channel
	 * @param block
	 * @return
	 * @throws Exception
	 */
    @SuppressWarnings({ "unused", "unchecked" })
    private LPData[] checkEmptyLP(ArrayList<LPData> list) throws Exception
    {
        int interval = resolution;
        ArrayList<LPData> emptylist = new ArrayList<LPData>();
        
        Double[] ch  = new Double[CHANNEL_CNT];
        Double[] v  = new Double[CHANNEL_CNT];
        
        for(int i = 0; i < CHANNEL_CNT; i++){
        	 ch[i] = new Double(0.0);
             v[i] = new Double(0.0);
        }
        
        String prevTime = "";
        String currentTime = "";

        Iterator it = list.iterator();
        while(it.hasNext()){
            currentTime = ((LPData)it.next()).getDatetime();
           
            if(prevTime != null && !prevTime.equals("")){
            	log.debug("currentTime : " +currentTime );
            	log.debug("prevTime : " +prevTime );
            	
                String temp = Util.addMinYymmdd(prevTime, interval);
                if(!temp.equals(currentTime))
                {
                	int diffMin = (int) ((Util.getMilliTimes(prevTime+"00")-
                            Util.getMilliTimes(currentTime+"00"))/1000/60);
                    
                    for(int i = 0; i < (diffMin/interval) ; i++){
                        LPData data = new LPData();
                        data.setV(v);
                        data.setCh(ch);
                        data.setFlag(0);
                        data.setPF(1.0);
                        data.setDatetime(Util.addMinYymmdd(prevTime, interval*(i+1)));
                        
                        emptylist.add(data);
                    }                
                }
            }
            prevTime = currentTime;

        }
        
        Iterator it2 = emptylist.iterator();
        while(it2.hasNext()){
            list.add((LPData)it2.next());
        }
        
        Collections.sort(list,LPComparator.TIMESTAMP_ORDER);  
        
        if(list != null && list.size() > 0){
            LPData[] data = null;
            Object[] obj = list.toArray();            
            data = new LPData[obj.length];
            for(int i = 0; i < obj.length; i++){
                data[i] = (LPData)obj[i];
            }
            return data;
        }
        else{
            return null;
        }
    }
    
    
	/**
	 * 날짜, 시간 채널 1,2,3,4  
	 * @param ch1
	 * @param ch2
	 * @return
	 * @throws Exception
	 */
	private double getPF(double ch1, double ch2) throws Exception {

        double pf   = (float)(ch1/(Math.sqrt(Math.pow(ch1,2)+Math.pow(ch2,2))));

        if(ch1 == 0.0 && ch2 == 0.0)
            pf = (double) 1.0;
        /* Parsing Transform Results put Data Class */
        if(pf < 0.0 || pf > 1.0)
            throw new Exception("BILL PF DATA FORMAT ERROR : "+pf);
        return pf;
     }
}


