package com.aimir.fep.meter.parser.Mk6NTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;

public class ConfigurationEventChannel implements java.io.Serializable{

	private static final long serialVersionUID = 4055922252649471016L;

	private static Log log = LogFactory.getLog(ConfigurationLPChannel.class);

	private static final int OFF_CH_REG=0;
	private static final int OFF_CH_SIZE=4;
	private static final int OFF_CH_TYPE=6;
	private static final int OFF_CH_UNIT=7;
	private static final int OFF_CH_RECORD_OFFSET=8;

	private static final int LEN_CH_REG=4;
	private static final int LEN_CH_SIZE=2;
	private static final int LEN_CH_TYPE=1;
	private static final int LEN_CH_UNIT=1;
	private static final int LEN_CH_RECORD_OFFSET=2;

	byte[] rawData;
	int ch_reg;
	int ch_size;
	int ch_type;
	int ch_unit;
	int ch_record_offset;
	int ch_scaling;
	float ch_scaling_factor;
	public ConfigurationEventChannel(byte[] rawData) {
		this.rawData = rawData;
	}
	public int getCh_size() {
		try {
			ch_size=DataFormat.hex2unsigned16(DataFormat.select(rawData, OFF_CH_SIZE, LEN_CH_SIZE));
		} catch (Exception e) {
			log.error("Error get Ch Size->"+e.getMessage());
		}
		return ch_size;
	}

	public int getCh_type() {
		ch_type=DataFormat.hex2unsigned8(rawData[OFF_CH_TYPE]);
		return ch_type;
	}

	public char getCh_type_char() {
		return (char)DataFormat.hex2unsigned8(rawData[OFF_CH_TYPE]);
	}

	public int getCh_unit() {
		ch_unit=DataFormat.hex2unsigned8(rawData[OFF_CH_UNIT]);
		return ch_unit;
	}
	public String getCh_unit_Str() {
		String unitStr="";
		switch(getCh_unit()){
		case 'A':
			unitStr="Amps";
		case 'D':
			unitStr="Angle in degrees";
		case 'H':
			unitStr="Hz";
		case 'M':
			unitStr="Minutes";
		case 'N':
			unitStr="No unit";
		case 'P':
			unitStr="Percent";
		case 'Q':
			unitStr="Power Factor";
		case 'R':
			unitStr="Vars";
		case 'S':
			unitStr="VA";
		case 'T':
			unitStr="Seconds";
		case 'U':
			unitStr="Unknown";
		case 'V':
			unitStr="Volts";
		case 'W':
			unitStr="Watts";
		case 'X':
			unitStr="Wh";
		case 'Y':
			unitStr="Varh";
		case 'Z':
			unitStr="Vah";
		}
		return unitStr;
	}
	/**
	 * Constructs a <code>String</code> with all attributes
	 * in name = value format.
	 *
	 * @return a <code>String</code> representation
	 * of this object.
	 */
	public String toString()
	{
	    StringBuffer retValue = new StringBuffer();

	    retValue.append("ConfigurationEventChannel [ ")
	        .append("ch_reg = ").append(this.ch_reg).append('\n')
	        .append("ch_size = ").append(this.ch_size).append('\n')
	        .append("ch_type = ").append(this.ch_type).append('\n')
	        .append("ch_unit = ").append(this.ch_unit).append('\n')
	        .append("ch_record_offset = ").append(this.ch_record_offset).append('\n')
	        .append("ch_scaling = ").append(this.ch_scaling).append('\n')
	        .append("ch_scaling_factor = ").append(this.ch_scaling_factor).append('\n')
	        .append(" ]");

	    return retValue.toString();
	}
}
