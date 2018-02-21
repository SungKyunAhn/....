package com.aimir.fep.meter.parser.a3rlnqTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.util.DataFormat;

public class A3_LD implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7437620972093421580L;
	private Log log = LogFactory.getLog(A3_LD.class);
	private byte[] rawData = null;
	
	private byte[] NBR_BLKS_SET1 = new byte[2];
	private byte[] NBR_BLKS_INTS_SET1 = new byte[2];
	private byte[] INT_TIME_SET1 = new byte[1];
	private byte[] NBR_CHNS_SET1 = new byte[1];
	
	private byte[] SCALARS_SET1 = new byte[2];
	private byte[] DIVISORS_SET1 =  new byte[2];
	private byte[] LP_BLOCK = new byte[0];
	
	private int nbr_blks_set1 = 0;
	private int nbr_blks_ints_set1 = 0;
	private int lpInterval = 0;
	private int channelCount = 0;
	private long ke = 0L;
	private int meterConstantScale=0;
//	private ST62 st62 = null;
	private ST64 st64  = null;
	
	
	public A3_LD(byte[] rawData, long ke, int meterConstantScale) {
        this.rawData = rawData;
        this.ke = ke;
        this.meterConstantScale = meterConstantScale;
		parse();
	}
	
	public void parse() {
		ST62 st62 = new ST62();
        int pos = 0;
//        2byte NBR_BLKS_SET1 
        System.arraycopy(rawData, pos, NBR_BLKS_SET1, 0, NBR_BLKS_SET1.length);
        pos += NBR_BLKS_SET1.length; 
        try {        	
			this.nbr_blks_set1 = DataFormat.getIntTo2Byte( DataFormat.LSB2MSB(NBR_BLKS_SET1)  );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        log.debug("NBR_BLKS_SET1[" + nbr_blks_set1 + "]");
        
//        2byte NBR_BLKS_INTS_SET1 
        System.arraycopy(rawData, pos, NBR_BLKS_INTS_SET1, 0, NBR_BLKS_INTS_SET1.length);
        pos += NBR_BLKS_INTS_SET1.length;
        try {
			this.nbr_blks_ints_set1 = DataFormat.getIntTo2Byte( DataFormat.LSB2MSB(NBR_BLKS_INTS_SET1) );
//			this.lpInterval = this.nbr_blks_ints_set1;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        log.debug("NBR_BLKS_INTS_SET1[" + nbr_blks_ints_set1 + "]");        
        
//        1byte INT_TIME_SET1
        System.arraycopy(rawData, pos, INT_TIME_SET1, 0, INT_TIME_SET1.length);
        pos += INT_TIME_SET1.length;
        this.lpInterval = DataFormat.getIntToByte( INT_TIME_SET1[0]);        
        log.debug("INT_TIME_SET1[" + lpInterval + "]");
        
//        1byte NBR_CHNS_SET1
        System.arraycopy(rawData, pos, NBR_CHNS_SET1, 0, NBR_CHNS_SET1.length);
        pos += NBR_CHNS_SET1.length;
        this.channelCount = DataFormat.getIntToByte(NBR_CHNS_SET1[0]);        
        log.debug("NBR_CHNS_SET1[" + channelCount + "]");
        
//        2*NBR_CHNS_SET1 SCALARS_SET1
        SCALARS_SET1 = new byte[SCALARS_SET1.length*channelCount];
        System.arraycopy(rawData, pos, SCALARS_SET1, 0, SCALARS_SET1.length);
        pos += SCALARS_SET1.length;
        
//        2*NBR_CHNS_SET1 DIVISORS_SET1
        DIVISORS_SET1 = new byte[DIVISORS_SET1.length*channelCount];
        System.arraycopy(rawData, pos, DIVISORS_SET1, 0, DIVISORS_SET1.length);
        pos += DIVISORS_SET1.length;
        
//        st62 set
        try {
			st62.setSCALARst64(SCALARS_SET1);
			st62.setDIVISORst64(DIVISORS_SET1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
//        LP Data
        LP_BLOCK = new byte[rawData.length - pos];
        System.arraycopy(rawData, pos, LP_BLOCK, 0, LP_BLOCK.length);
        pos += LP_BLOCK.length;
        this.st64 = new ST64(LP_BLOCK,nbr_blks_set1,nbr_blks_ints_set1,channelCount,lpInterval,1,ke,(byte)0x00, st62, this.meterConstantScale);
        if(this.st64 == null){
			log.debug("st64 is null");	
		}else{
			log.debug("st64 is null");
		}
	}
	
	public LPData[] getLPData(){
		if(this.st64 == null){
			log.debug("st64 is null in getLPData");	
			return null;
		}else{
			log.debug("st64 is NOT null in getLPData");
			log.debug(this.st64.getLPData());
			return this.st64.getLPData();
		}
		
	}
	
	public int getLpInterval(){
		return this.lpInterval;
	}


}