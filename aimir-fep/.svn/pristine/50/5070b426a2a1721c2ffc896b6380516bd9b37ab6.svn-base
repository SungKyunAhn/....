package com.aimir.fep.protocol.fmp.frame.amu;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;

/**
 * Stack Up event network Parameter
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 4. 19. 오후 4:08:19$
 */
public class EventDataStackUpNetWorkParam {

	private static Log log = LogFactory.getLog(EventDataStackUpNetWorkParam.class);
	
	public static final int OFS_AUTO_FORMING_JOING 	= 0;
	public static final int OFS_ROUTE_DISCOVERY 	= 1;
	public static final int OFS_CHANNEL				= 2;
	public static final int OFS_PAN_ID				= 3;
	public static final int OFS_EXTENDED_PAN_ID		= 5;
	public static final int OFS_TX_POWER			= 13;
	public static final int OFS_TX_POWER_MODE		= 14;
	public static final int OFS_PERMIT_TIME			= 15;
	
	public static final int LEN_CHANNEL				= 1;
	public static final int LEN_PAN_ID 				= 2;
	public static final int LEN_EXTENDED_PAN_ID 	= 8;
	
	byte[] rawData 		= null;
	
	/**
	 * constructor
	 */
	public EventDataStackUpNetWorkParam(){
	}
	
	/**
	 * constructor
	 * @param rawData
	 * @throws Exception
	 */
	public EventDataStackUpNetWorkParam(byte[] rawData) throws Exception{
		
		try{
			this.rawData = rawData;
		}catch(Exception e){
			throw e;
		}
	}
	
	/**
	 * get Auto forming or joining
	 * @return
	 * @throws Exception
	 */
	public int getAutoFormingOrJoining()throws Exception{
		return DataFormat.hex2unsigned8(rawData[OFS_AUTO_FORMING_JOING]); 
	}
	
	/**
	 * get Route discovery
	 * @return
	 * @throws Exception
	 */
	public int getRouteDiscovery() throws Exception{
		return DataFormat.hex2unsigned8(rawData[OFS_ROUTE_DISCOVERY]); 
	}
	
	/**
	 * get Channel
	 * @return
	 * @throws Exception
	 */
	public String getChannel() throws Exception{
		return new String(DataFormat.select(rawData, OFS_CHANNEL, LEN_CHANNEL));
	}
	
	/**
	 * get PAN ID
	 * @return
	 * @throws Exception
	 */
	public int getPanId()throws Exception{
		return DataFormat.hex2dec(DataFormat.select(rawData, OFS_PAN_ID, LEN_PAN_ID));
	}
	
	/**
	 * get Extended PAN ID
	 * @return
	 * @throws Exception
	 */
	public double getExtendedPanId()throws Exception{
		return DataFormat.hex2double64(rawData, OFS_EXTENDED_PAN_ID);
	}
	
	/**
	 * get TX Power
	 * @return
	 * @throws Exception
	 */
	public int getTxPower()throws Exception{
		return DataFormat.hex2unsigned8(rawData[OFS_TX_POWER]); 
	}
	
	/**
	 * get TX Power Mode
	 * @return
	 * @throws Exception
	 */
	public int getTxPowerMode()throws Exception{
		return DataFormat.hex2unsigned8(rawData[OFS_TX_POWER_MODE]); 
	}
	
	/**
	 * get Permit Time
	 * @return
	 * @throws Exception
	 */
	public int getPermitTime()throws Exception{
		return DataFormat.hex2unsigned8(rawData[OFS_PERMIT_TIME]); 
	}
	
	public String toString() {
		
        StringBuffer sb = new StringBuffer();
        
        try{
        	sb.append(" AUTO FORMING OR JOINING[" + getAutoFormingOrJoining() + "]"
        		 	+ ", ROUTE DISCOVERY[" + getRouteDiscovery() + "]"
                    + ", CHANNEL[" + getChannel() + "]"
                    + ", PAN ID[" + getPanId() + "]"
                    + ", EXTENDED PAN ID[" + getExtendedPanId() + "]"
                    + ", TX POWER[" + getTxPower() + "]"
                    + ", TX POWER MODE [" + getTxPowerMode() + "]"
                    + ", PERMIT TIME[" + getPermitTime() + "]" );
        }catch (Exception e) {
        	log.warn("EventData StackUp NewworkParam TO STRING ERR=>"+e.getMessage());
		}
        return sb.toString();
	 }
}


