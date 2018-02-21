package com.aimir.fep.meter.parser.rdata;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataUtil;

/**
 * ChannelConfigure Format
 * 
 * @author kaze
 * 
 */
public class ChannelConfigure  {
	private static Log log = LogFactory.getLog(ChannelConfigure.class);
	private byte[] CHANNELCONFIGURE = new byte[6];
	private byte INDEX;	
	private byte UNIT;
	private byte SIGEXP;
	private byte VALUETYPE;
	private byte OBJECTTYPE;
	private byte CHANNELTYPE;
	

	private int index;
	private RDataConstant.ConfigurationUnit unit;
	private Double sigExp;
	private RDataConstant.ConfigurationValueType valueType;
	private RDataConstant.ConfigurationObjectType objectType;
	private RDataConstant.ConfigurationElectricityChannelType channelType;

	public ChannelConfigure(byte[] cHANNELCONFIGURE) {
		super();
		CHANNELCONFIGURE = cHANNELCONFIGURE;
	}

	public void parsingPayLoad() throws Exception{
		int pos = 0;
		INDEX = CHANNELCONFIGURE[pos++];
		UNIT = CHANNELCONFIGURE[pos++];
		SIGEXP = CHANNELCONFIGURE[pos++];
		VALUETYPE = CHANNELCONFIGURE[pos++];
		OBJECTTYPE = CHANNELCONFIGURE[pos++];
		CHANNELTYPE = CHANNELCONFIGURE[pos++];
		
		index = DataUtil.getIntToByte(INDEX);
		unit= RDataConstant.getConfigurationUnit(DataUtil.getIntToByte(UNIT));
		sigExp = null;
		valueType = RDataConstant.getConfigurationValueType(DataUtil.getIntToByte(VALUETYPE));
		objectType = RDataConstant.getConfigurationObjectType(DataUtil.getIntToByte(OBJECTTYPE));
		channelType = RDataConstant.getConfigurationElectricityChannelType(DataUtil.getIntToByte(CHANNELTYPE));
	}

	/**
	 * Channel Index or number
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}


	/**
	 * @return the unit
	 */
	public RDataConstant.ConfigurationUnit getUnit() {
		return unit;
	}

	/**
	 * @return the sigExp
	 */
	public Double getSigExp() {
		return sigExp;
	}		

	/**
	 * @return the objectType
	 */
	public RDataConstant.ConfigurationObjectType getObjectType() {
		return objectType;
	}

	/**
	 * Value 값의 종류
	 * @return the valueType
	 */
	public RDataConstant.ConfigurationValueType getValueType() {
		return valueType;
	}

	/**
	 * @return the channelType
	 */
	public RDataConstant.ConfigurationElectricityChannelType getChannelType() {
		return channelType;
	}	
}
