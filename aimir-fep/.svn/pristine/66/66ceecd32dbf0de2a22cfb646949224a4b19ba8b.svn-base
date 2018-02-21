package com.aimir.fep.meter.parser.amuKepco_2_5_0Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;

/**
 * Meter Data Format
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 4. 9. 오후 4:36:38$
 */
public class DataFormatForMetering {

	private Log log = LogFactory.getLog(DataFormatForMetering.class);
	
	public static final int OFS_KW_FORMAT  			= 0;	// 전력
    public static final int OFS_KWH_FORMAT  		= 1;	// 전력량
    public static final int OFS_PF_FORMAT  			= 2;	// 역률
    public static final int OFS_V_FORMAT  			= 3;	// 전압
    public static final int OFS_HZ_FORMAT  			= 4;	// 주파수
           
	private byte[] data;
    
	/**
	 * Constructor
	 */
	public DataFormatForMetering(byte[] data) {
		this.data = data;
	}
	
    /**
     * decimal count of KW data
     */
    public int getKW_FORMAT() {
        return DataFormat.hex2unsigned8(data[OFS_KW_FORMAT]);
    }
	
    /**
     * decimal count of KWh data
     */
    public int getKWH_FORMAT() {
        return DataFormat.hex2unsigned8(data[OFS_KWH_FORMAT]);
    }
    
    /**
     * decimal count of PF data
     */
    public int getPF_FORMAT() {
        return DataFormat.hex2unsigned8(data[OFS_PF_FORMAT]);
    }
    
    /**
     * decimal count of V data
     */
    public int getV_FORMAT() {
        return DataFormat.hex2unsigned8(data[OFS_V_FORMAT]);
    }
    
    /**
     * decimal count of Hz data
     */
    public int getHZ_FORMAT() {
        return DataFormat.hex2unsigned8(data[OFS_HZ_FORMAT]);
    }
    
    /**
     * to String
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("DataFormatForMetering DATA[");        
            sb.append("(KW_FORMAT=").append(getKW_FORMAT()).append("),");
            sb.append("(KWH_FORMAT=").append(getKWH_FORMAT()).append("),");
            sb.append("(PF_FORMAT()=").append(getPF_FORMAT()).append("),");
            sb.append("(V_FORMAT()=").append(getV_FORMAT()).append("),");
            sb.append("(HZ_FORMAT=").append(getHZ_FORMAT()).append(')');
            sb.append("]\n");
        }catch(Exception e){
            log.warn("DataFormatForMetering TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }

}


