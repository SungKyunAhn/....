package com.aimir.fep.meter.parser.amuLsrwRs232Table;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;

/**
 * LSRW_LP LP DATA Field
 * LGRW3410의  LP DATA Field를 동일한 형태 LP DATA Field
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 3. 4. 오전 11:23:49$
 */
public class LSRWRS232_LP {

	private static Log log = LogFactory.getLog(LSRWRS232_LP.class);
	
	private static final int	OFS_LP_COUNT		= 0;
	private static final int	OFS_LP_PERIOD		= 1;
	private static final int	OFS_LP_DATA_SET 	= 2;
	
	private static final int	LEN_LP_DATA_SET 	= 12;

    /* ************************************************************** */
    /**
     *  ONE LP DATA Feild Information
     *  하나의  LP DATA(12byte)의  Description
     **/
    public static final int CHANNEL_CNT				= 4;
    
    public static final int OFS_PULSE 				= 0;
    public static final int OFS_LP_DATE 			= 8;
    
    public static final int LEN_PULSE 				= 2;
    public static final int LEN_LP_DATE 			= 4;
    
    public static final int MASK_LP_DATA 			= 0x3fff;
    
    /* ************************************************************** */
    private int 			resolution				= 15;
	private int 			regK					= 1;
	private int 			ctvt					= 1;
    private double 			ke 						= 1;
    DecimalFormat 			dformat 				= new DecimalFormat("###############0.000000");
    private byte[] 			rawData					= null;			
    /**
	 * Constructor
	 */
	public LSRWRS232_LP(byte[] rawData) {
		this.rawData = rawData;
	}	
	
	/**
	 * Constructor 
	 * @param data - read data (header,crch,crcl)
	 */
	public LSRWRS232_LP(byte[] rawData, int regK, double ke) {
		this.rawData 	= rawData;
		this.regK 		= regK;
        this.ke 		= ke;
	}
	
	/**
	 * Constructor 
	 * @param data 
	 */
	public LSRWRS232_LP(byte[] rawData, int regK, int ctvt , double ke ) {
		this.rawData 	= rawData;
		this.regK 		= regK;
        this.ke 		= ke;
        this.ctvt		= ctvt;
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
     * get LP Period
     * @return 
     */
	public int getResolution(){
		
		int ret = 0;
        try{
        	ret = DataFormat.hex2unsigned8(rawData[OFS_LP_PERIOD]);
        }catch(Exception e){
            log.warn("get LP Period Failed --> "+e.getMessage());
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
	    resolution = getResolution();
	    for (int i = 0; i < interval.length; i++) {
	    	
	    	byte[] blk = new byte[LEN_LP_DATA_SET];
            
            blk =DataFormat.select(rawData,OFS_LP_DATA_SET+(i*LEN_LP_DATA_SET),LEN_LP_DATA_SET);
            list.add(parseChannel(blk));
	    }
		     
	    Collections.sort(list,LPComparator.TIMESTAMP_ORDER);        
        return checkEmptyLP(list);
	}
	
	@SuppressWarnings({ "unused", "unchecked" })
    private LPData[] checkEmptyLP(ArrayList<LPData> list) throws Exception
    {
        int interval = getResolution();
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
	 * get LPDATA Channel
	 * @param block
	 * @return
	 * @throws Exception
	 */
    private LPData parseChannel(byte[] block) throws Exception{

        LPData lpdata = new LPData();
        
        Double[] ch  = new Double[CHANNEL_CNT];	// 전력량
        Double[] v  = new Double[CHANNEL_CNT];	// 전력
        
        for(int i = 0; i < CHANNEL_CNT; i++){
        	
        	// 전력량 
            ch[i] =  new Double(dformat.format(
            		(double)(DataFormat.hex2dec( 
            				DataFormat.select(block,OFS_PULSE+(i*LEN_PULSE),LEN_PULSE)) & MASK_LP_DATA) *ke*0.001));
            // 전력
            v[i] = new Double(dformat.format(ch[i]*((double)60/resolution)));
        }
        
        lpdata.setV(v);
        lpdata.setCh(ch);
        lpdata.setFlag(0);
        lpdata.setPF(getPF(ch[0], ch[1]));
        lpdata.setDatetime(new LPIntervalTime(DataFormat.select(
                            block,OFS_LP_DATE, LEN_LP_DATE)).getIntervalTime());
        return lpdata;
    }
	
    /**
     * get PF
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


