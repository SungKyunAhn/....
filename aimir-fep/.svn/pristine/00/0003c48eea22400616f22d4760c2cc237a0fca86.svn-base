package com.aimir.fep.meter.parser.amuKmpMc601Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.HMData;

/**
 * KMP MC601 Month Data Field
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 3. 18. 오전 10:30:39$
 */
public class KMPMC601_MONTH extends KMPMC601_LP{

	private static final long serialVersionUID = 8139834253138210700L;
	
	private static Log log = LogFactory.getLog(KMPMC601_MONTH.class);
	public static String TABLE_KIND = "MONTH";
	private HMData[] hmData = null;
	
	/**
	 * Constructor
	 */
	public KMPMC601_MONTH(byte[] rawData,String table_kind) {
        super(rawData,table_kind);
	}
        
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("KMP MC601 MONTH DATA["); 
            for(int i = 0; i < hmData.length; i++){
                sb.append(hmData[i].toString());
            }
            sb.append("]\n");
        }catch(Exception e){
            log.warn("KMP MC601 MONTH TO STRING ERROR=>"+e.getMessage());
        }
        return sb.toString();
    }
}


