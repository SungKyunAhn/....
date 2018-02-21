package com.aimir.fep.meter.parser.Mk10Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataUtil;

public class ConfigurationLPChannel implements java.io.Serializable{

	private static final long serialVersionUID = -4200298155774630120L;

	private static Log log = LogFactory.getLog(ConfigurationLPChannel.class);

	byte[] rawData;
	long ch_scaling_factor;
	double decimalPosition;
	
	/**
	 * Survey 1 channel definitions for the first 32 channels.
	Bits 07
	give the source definition. 0xff if no channel.
	Bits 89
	give the acc/min/max/inst setting, bottom 2 bits
	0 – Accumulated
	1 – Minimum
	2 – Maximum
	3 – Instantaneous
	4 – Average
	5 – Lower word of 32 entry (TOU channel in LS)
	6 – Upper word of 32 entry (TOU channel in LS)
	The data in channel 5 and 6 combine to form a 32 bit value. The
	scaling values in the 2 channel definitions will be the same.
	Bits 1011
	give the scaling code 0=””,1=k,2=M,3=G
	Bits 1214
	give the position of the decimal point.
	Bit 15 gives the acc/min/max/inst setting, top bit
	 * @param rawData
	 */
	public ConfigurationLPChannel(byte[] rawData) {
		this.rawData = rawData;
	}
	
	public Double getDecimalPoint() {
		try {
			int val = DataUtil.getIntTo2Byte(rawData);
			val = (val & 0x7FFF) >> 12;
			switch(val){
				case 1 : decimalPosition  =  0.1;
				break;
				case 2 : decimalPosition  =  0.01;
				break;
				case 3 : decimalPosition  =  0.001;
				break;
				case 4 : decimalPosition  =  0.0001;
				break;
				case 5 : decimalPosition  =  0.00001;
				break;
				case 6 : decimalPosition  =  0.000001;
				break;
				case 7 : decimalPosition  =  0.0000001;
				break;
				default :
					decimalPosition  = 1;
				break;
			}

			//log.debug("DecimalPosition="+decimalPosition+",val="+val);
		} catch (Exception e) {
			log.error("Error get Ch Decimal position->"+e.getMessage());
		}
		return decimalPosition;
	}
	
	public long getCh_scaling_factor() {
		try {
			int val = DataUtil.getIntTo2Byte(rawData);
			val = (val & 0x0FFF) >> 10;
			switch(val){
				case 1 : ch_scaling_factor  =  1000;
				break;
				case 2 : ch_scaling_factor  =  1000000;
				break;
				case 3 : ch_scaling_factor  =  1000000000;
				break;
				default :
					ch_scaling_factor  = 1;
				break;
			}
			//log.debug("ScaleFactor="+ch_scaling_factor+"val,="+DataUtil.getIntTo2Byte(rawData));
			
		} catch (Exception e) {
			log.error("Error get Ch Scaling Factor->"+e.getMessage());
		}
		return ch_scaling_factor;
	}
	
}
