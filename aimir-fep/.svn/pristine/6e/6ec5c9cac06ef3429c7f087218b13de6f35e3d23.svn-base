package com.aimir.fep.meter.parser.MBusTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataUtil;

public class ControlInformation implements java.io.Serializable{
	private static Log log = LogFactory.getLog(Control.class);
	private byte[] rawData = null;
	private int controlInformation=0;
	private String controlInfomationString="";
	//1: LSB First, 2:MSB First
	private int mode=1;

	/**
	 * @param data
	 */
	public ControlInformation(byte[] data){
		rawData=data;
		controlInformation = DataUtil.getIntToBytes(rawData);

		//Set Mode(if third bit is 0 then mode1, if third bit is 1 then mode2)
        if((controlInformation&0x04)==4){
        	mode=2;
        }

        //CI-Field codes used by the master
        if(controlInformation==0x51 || controlInformation==0x55){
        	controlInfomationString="data send";
        }
        else if(controlInformation==0x52 || controlInformation==0x56){
        	controlInfomationString="selection of slaves";
        }
        else if(controlInformation==0x50){
        	controlInfomationString="application reset";
        }
        else if(controlInformation==0x54){
        	controlInfomationString="synronize action";
        }
        else if(controlInformation==0xB8){
        	controlInfomationString="set baudrate to 300 baud";
        }
        else if(controlInformation==0xB9){
        	controlInfomationString="set baudrate to 600 baud";
        }
        else if(controlInformation==0xBA){
        	controlInfomationString="set baudrate to 1200 baud";
        }
        else if(controlInformation==0xBB){
        	controlInfomationString="set baudrate to 2400 baud";
        }
        else if(controlInformation==0xBC){
        	controlInfomationString="set baudrate to 4800 baud";
        }
        else if(controlInformation==0xBD){
        	controlInfomationString="set baudrate to 9600 baud";
        }
        else if(controlInformation==0xBE){
        	controlInfomationString="set baudrate to 19200 baud";
        }
        else if(controlInformation==0xBF){
        	controlInfomationString="set baudrate to 38400 baud";
        }
        else if(controlInformation==0xB1){
        	controlInfomationString="request readout of complete RAM content";
        }
        else if(controlInformation==0xB2){
        	controlInfomationString="send user data (not standardized RAM write)";
        }
        else if(controlInformation==0xB3){
        	controlInfomationString="initialize test calibration mode";
        }
        else if(controlInformation==0xB4){
        	controlInfomationString="EEPROM read";
        }
        else if(controlInformation==0xB6){
        	controlInfomationString="start software test";
        }
        else if(controlInformation>=0x90 && controlInformation<=0x97){
        	controlInfomationString="codes used for hashing";
        }

        //CI-Field codes used by the slave
        else if(controlInformation==0x70){
        	controlInfomationString="report of general application errors";
        }
        else if(controlInformation==0x71){
        	controlInfomationString="report of alarm status";
        }
        else if(controlInformation==0x72 || controlInformation==0x76){
        	controlInfomationString="variable data respond";
        }
        else if(controlInformation==0x73 || controlInformation==0x77){
        	controlInfomationString="fixed data respond";
        }
	}

	/**
	 * @return
	 */
	public byte[] getRawData() {
		return rawData;
	}

	/**
	 * @param rawData
	 */
	public void setrawData(byte[] rawData) {
		this.rawData = rawData;
	}

	/**
	 * @return
	 */
	public int getControlInformation() {
		return controlInformation;
	}

	/**
	 * @param controlInformation
	 */
	public void setControlInformation(int controlInformation) {
		this.controlInformation = controlInformation;
	}

	/**
	 * @return
	 */
	public String getControlInfomationString() {
		return controlInfomationString;
	}

	/**
	 * @param controlInfomationString
	 */
	public void setControlInfomationString(String controlInfomationString) {
		this.controlInfomationString = controlInfomationString;
	}

	/**
	 * 1: LSB First, 2:MSB First
	 * @return
	 */
	public int getMode() {
		return mode;
	}

	/**
	 * 1: LSB First, 2:MSB First
	 * @param mode
	 */
	public void setMode(int mode) {
		this.mode = mode;
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
	    final String TAB = "\n";

	    StringBuffer retValue = new StringBuffer();

	    retValue.append("ControlInformation ( ")
	        .append("controlInformation = ").append(this.controlInformation).append(TAB)
	        .append("controlInfomationString = ").append(this.controlInfomationString).append(TAB)
	        .append("mode = ").append(this.mode).append(TAB)
	        .append(" )");

	    return retValue.toString();
	}
}
