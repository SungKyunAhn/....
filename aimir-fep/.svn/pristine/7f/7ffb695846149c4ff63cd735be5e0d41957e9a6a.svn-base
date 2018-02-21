package com.aimir.fep.meter.parser.amuKmpMc601Table;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.HMData;
import com.aimir.util.TimeUtil;

/**
 * KMP MC601 LP DATA( HOUR Data )
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 3. 19. 오전 10:25:01$
 */
public class KMPMC601_LP implements java.io.Serializable{
	
	private static Log log = LogFactory.getLog(KMPMC601_LP.class);
	
	public static String TABLE_KIND 		= "LP";
	public String chUnitName				= "";
	private byte[] rawData 					= null;
	protected HMData[] hmData 				= null;
    protected ArrayList hmDataArray 		= null;
    protected String table_kind 			= null;
	DecimalFormat dformat 					= new DecimalFormat("#0.000000");
	protected byte flowSIGNEXP 				= 0x43; // 현재 사용하지 않고 있음
	/**
	 * Constructor
	 */
	 public KMPMC601_LP(){
	 }
	/**
	 * Constructor
	 */
	public KMPMC601_LP(byte[] rawData, String table_kind ) {
        this.rawData = rawData;
        this.table_kind = table_kind;
        try{
            parse();
        }catch(Exception e)
        {
            log.error(e,e);
        }
	}
	
	
	/**
	 * get HM Data
	 * @return
	 */
	public HMData[] getData()
    {
        return this.hmData;
    }

	/**
	 * get Channel Unit Name
	 * @return
	 */
    public String getChUnitName(){
    	return chUnitName;
    }
    
    
	public void parse() throws Exception{
		
		log.debug("================== KMPMC601 LP Parse Start["+ table_kind +"] ==================");
		List dbList 	= new ArrayList();
        int offset 		= 0;
        DataBlock db 	= null;
                
        while(offset < rawData.length){
        	db = new DataBlock(rawData,offset);
        	dbList.add(db);
        	offset += db.getLength();
        }

        Iterator it 		= dbList.iterator();
        
        //ArrayList dtList 	= new ArrayList();
        ArrayList ch1 		= new ArrayList();
        ArrayList ch2 		= new ArrayList();
        ArrayList ch3 		= new ArrayList();
        ArrayList ch4 		= new ArrayList();
        ArrayList ch5 		= new ArrayList();
        ArrayList ch6 		= new ArrayList();
        ArrayList ch7 		= new ArrayList();
        ArrayList ch8 		= new ArrayList();
        ArrayList ch9 		= new ArrayList();
        //ArrayList stat 		= new ArrayList();
        ArrayList count 	= new ArrayList();
        ArrayList date 		= new ArrayList();
        ArrayList time 		= new ArrayList();
        //ArrayList datetime 	= new ArrayList();

        while(it.hasNext()){
        	db = (DataBlock)it.next();
        	int ridcode = db.getRid();
        	count.add(new Integer(db.getCount()));
            
            /**
             * date	- DATE
             * time	- ClOCK
             * ch1	- E1
             * ch3	- FLOW1
             * ch4	- V1
             * ch5	- P1
             * ch6	- E8
             * ch7	- E9
             * ch8	- T1
             * ch9	- T2
             */
        
            switch(ridcode){
            case RegisterIDTable.DATE : 				//date = db.getRegVal();
											        	for(int i = 0 ; i < db.getCount() ; i ++ ){
											        		date.add(db.getRegVal().get(i));
											        	}
				        								break;
        	case RegisterIDTable.E1 :					// ch1 = db.getRegVal();  
		        										for(int i = 0 ; i < db.getRegVal().size() ; i ++ ){
		        											ch1.add(db.getRegVal().get(i));
		        										} 		
		        										chUnitName = db.getUnitName();	
		        										break;
        	case RegisterIDTable.E2 : 					break;
        	case RegisterIDTable.E3 : 					break;
        	case RegisterIDTable.E4 : 					break;
        	case RegisterIDTable.E5 : 					break;
        	case RegisterIDTable.E6 : 					break;
        	case RegisterIDTable.E7 : 					break;
        	case RegisterIDTable.E8 : 					//ch6 = db.getRegVal();
								        				for(int i =0 ; i < db.getRegVal().size() ; i ++ ){
								        					ch6.add(db.getRegVal().get(i));
								        				}
								        				break;
        	case RegisterIDTable.E9 : 					//ch7 = db.getRegVal();
								        				for(int i =0 ; i < db.getRegVal().size() ; i ++ ){
								        					ch7.add(db.getRegVal().get(i));
									        			}
								        				break;
        	case RegisterIDTable.TA2 : 					break;
        	case RegisterIDTable.TA3 : 					break;
        	case RegisterIDTable.V1 : 					//ch4 = db.getRegVal(); 
										        		for(int i =0 ; i < db.getRegVal().size() ; i ++ ){
										        			ch4.add(db.getRegVal().get(i));
											        	}
				        								flowSIGNEXP = db.getSIGNEXP();	
		        										break;
        	case RegisterIDTable.V2 : 					break;
        	case RegisterIDTable.VA : 					break;
        	case RegisterIDTable.VB : 					break;
        	case RegisterIDTable.M1 : 					break;
        	case RegisterIDTable.M2 : 					break;
        	case RegisterIDTable.HR : 					break;
        	case RegisterIDTable.INFOEVENT : 			break;
        	case RegisterIDTable.CLOCK : 				//time = db.getRegVal();
										        		for(int i =0 ; i < db.getRegVal().size() ; i ++ ){
										        			time.add(db.getRegVal().get(i));
											        	}
		        										break;
        	case RegisterIDTable.INFO : 				break;
        	case RegisterIDTable.T1 : 					//ch8 = db.getRegVal();
										        		for(int i =0 ; i < db.getRegVal().size() ; i ++ ){
										        			ch8.add(db.getRegVal().get(i));
											        	}
		        										break;
        	case RegisterIDTable.T2 : 					//ch9 = db.getRegVal();	
										        		for(int i =0 ; i < db.getRegVal().size() ; i ++ ){
										        			ch9.add(db.getRegVal().get(i));
											        	}								
										        		break;
        	case RegisterIDTable.T3 : 					break;
        	case RegisterIDTable.T4 : 					break;
        	case RegisterIDTable.T1_T2 : 				break;
        	case RegisterIDTable.P1 : 					//ch5 = db.getRegVal();	
											        	for(int i =0 ; i < db.getRegVal().size() ; i ++ ){
											    			ch5.add(db.getRegVal().get(i));
											        	}
											        	break;
        	case RegisterIDTable.P2 : 					break;
        	case RegisterIDTable.FLOW1 : 				//ch3 = db.getRegVal();
										        		for(int i =0 ; i < db.getRegVal().size() ; i ++ ){
										        			ch3.add(db.getRegVal().get(i));
											        	}
										        		break; 
        	case RegisterIDTable.FLOW2 : 				break;
        	case RegisterIDTable.EFFEKT1 : 				break;
        	case RegisterIDTable.MAX_FLOW1DATE1 : 		break;
        	case RegisterIDTable.MAX_FLOW1 : 			break;
        	case RegisterIDTable.MIN_FLOW1DATE1: 		break;
        	case RegisterIDTable.MIN_FLOW1 : 			break;
        	case RegisterIDTable.MAX_EFFEKT1DATE1 : 	break;
        	case RegisterIDTable.MAX_EFFEKT1 : 			break;
        	case RegisterIDTable.MIN_EFFEKT1DATE1 : 	break;
        	case RegisterIDTable.MIN_EFFEKT1 : 			break;
        	case RegisterIDTable.MAX_FLOW1DATE2 : 		break;
        	case RegisterIDTable.MAX_FLOW2 : 			break;
        	case RegisterIDTable.MIN_FLOW1DATE2 : 		break;
        	case RegisterIDTable.MIN_FLOW2 : 			break;
        	case RegisterIDTable.MAX_EFFEKT1DATE2 : 	break;
        	case RegisterIDTable.MAX_EFFEKT2 : 			break;
        	case RegisterIDTable.MIN_EFFEKT1DATE2 : 	break;
        	case RegisterIDTable.MIN_EFFEKT2 : 			break;
        	case RegisterIDTable.AVR_T1 : 				break;
        	case RegisterIDTable.AVR_T2 : 				break;
        	case RegisterIDTable.AVR2_T1 : 				break;
        	case RegisterIDTable.AVR2_T2 : 				break;
        	case RegisterIDTable.TL2 : 					break;
        	case RegisterIDTable.TL3 : 					break;
        	case RegisterIDTable.XDAY : 				break;
        	case RegisterIDTable.PROG_NO : 				break;
        	case RegisterIDTable.CONFIG_NO_1 : 			break;
        	case RegisterIDTable.CONFIG_NO_2 : 			break;
        	case RegisterIDTable.SERIE_NO : 			break;
        	case RegisterIDTable.METER_NO_2 : 			break;
        	case RegisterIDTable.METER_NO_1 : 			break;
        	case RegisterIDTable.METER_NO_VA : 			break;
        	case RegisterIDTable.METER_NO_VB : 			break;
        	case RegisterIDTable.METER_TYPE : 			break;
        	case RegisterIDTable.CHECK_SUM_1 : 			break;
        	case RegisterIDTable.HIGH_RES : 			break;
        	case RegisterIDTable.TOPMODUL_ID : 			break;
        	case RegisterIDTable.BOTMODUL_ID : 			break;
            }   
        }
        log.debug("date Size : " + date.size() );
        hmDataArray = new ArrayList();

        int	cnt = 0;
        if(table_kind.equals("LP")){
        	cnt = ((Integer)count.get(2)).intValue();
        }
        /**
         *  DATE_1(16) + DATE_2(16)
         */
        else if(table_kind.equals("DAY")){
        	cnt = ((Integer)count.get(0)).intValue() + ((Integer)count.get(1)).intValue();
        }else {
        	cnt = ((Integer)count.get(0)).intValue();
        }
         
        log.debug("Data Count : " + cnt );
        for(int i = 0; i < cnt; i++)
        {
         	Double ch1Value = new Double(0.0);
            Double ch2Value = new Double(0.0);
            Double ch3Value = new Double(0.0);
            Double ch4Value = new Double(0.0);
            Double ch5Value = new Double(0.0);
            Double ch6Value = new Double(0.0);
            Double ch7Value = new Double(0.0);
            Double ch8Value = new Double(0.0);
            Double ch9Value = new Double(0.0);
            Double ch10Value = new Double(0.0);
            
            if(ch1 != null && ch1.size() > i)
                ch1Value = new Double(dformat.format(((Double)ch1.get(i)).doubleValue()));
            if(ch2 != null && ch2.size() > i)
                ch2Value = new Double(dformat.format(((Double)ch2.get(i)).doubleValue()));
            if(ch3 != null && ch3.size() > i)
                ch3Value = new Double(dformat.format(((Double)ch3.get(i)).doubleValue()));
            if(ch4 != null && ch4.size() > i)
                ch4Value = new Double(dformat.format(((Double)ch4.get(i)).doubleValue()));
            if(ch5 != null && ch5.size() > i)
                ch5Value = new Double(dformat.format(((Double)ch5.get(i)).doubleValue()));
            if(ch6 != null && ch6.size() > i)
                ch6Value = new Double(dformat.format(((Double)ch6.get(i)).doubleValue()));
            if(ch7 != null && ch7.size() > i)
                ch7Value = new Double(dformat.format(((Double)ch7.get(i)).doubleValue()));   
            if(ch8 != null && ch8.size() > i)
                ch8Value = new Double(dformat.format(((Double)ch8.get(i)).doubleValue()));   
            if(ch9 != null && ch9.size() > i)
                ch9Value = new Double(dformat.format(((Double)ch9.get(i)).doubleValue()));   
            if(ch8 != null && ch8.size() > i && ch9 != null && ch9.size() > i)
            	ch10Value = new Double(dformat.format(ch8Value.doubleValue() - ch9Value.doubleValue()));
            
            String lpDatetime = "";
            if(table_kind.equals("LP")){
    			lpDatetime = (String)date.get(0)+(String)time.get(0);
            }
            
    		HMData hm = new HMData();
    		hm.setKind(table_kind);
    		
    		if(table_kind.equals("LP")){
    			String nowlpDatetime = TimeUtil.getPreHour(lpDatetime, (1*i));
    			hm.setDate(nowlpDatetime.substring(0,8));
    			hm.setTime(nowlpDatetime.substring(8,10)+"0000"); /* 분,초 는 0000 처리 */
    		} else{
    			hm.setDate((String)date.get(i));
    			if(table_kind.equals("CURRENT")){
    				hm.setTime(((String)time.get(i)).substring(0,4));
    			}
            }
            hm.setChannelCnt(10);
            
            if(table_kind.equals("CURRENT")){
            	hm.setCh(1,new Double(0.0));	
            	hm.setCh(2,ch1Value);	
            }else{
	            hm.setCh(1,ch1Value);
	            hm.setCh(2,ch2Value);
            }
            
            if(table_kind.equals("LP") || table_kind.equals("DAY")){
            	hm.setCh(3,ch4Value);
            	hm.setCh(4,new Double(0.0));
            }else {
	            hm.setCh(3,ch3Value);
	            hm.setCh(4,ch4Value);
            }
            hm.setCh(5,ch5Value);
            hm.setCh(6,ch6Value);
            hm.setCh(7,ch7Value);
            hm.setCh(8,ch8Value);
            hm.setCh(9,ch9Value);  
            hm.setCh(10,ch10Value);
            
            hmDataArray.add(hm);
           
        }
        
        Object[] obj = hmDataArray.toArray();
        log.debug("hmDataArray.size() :" + hmDataArray.size());
        hmData = new HMData[hmDataArray.size()];
        for(int i = 0; i < obj.length; i++){
        	hmData[i] = (HMData)obj[i];
        }
        
        log.debug("================== KMPMC601 LP Parse End ==================");
    }
}


