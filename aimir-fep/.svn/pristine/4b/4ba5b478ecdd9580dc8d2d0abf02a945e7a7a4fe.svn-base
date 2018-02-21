package com.aimir.fep.meter.parser.MBusTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.Hex;

public class BaseRecordParent implements java.io.Serializable{
	private static Log log = LogFactory.getLog(BaseRecordParent.class);

	protected int start1 = 0;
	protected int length1 = 0x0;
	protected int length2 = 0x0;
	protected int start2 = 0;
	protected Control control = null;
	protected ControlInformation controlInformation = null;
	protected FixedDataHeader fixedDataHeader = null;
	protected DataBlocks dataBlocks = null;
	protected int address = 0;
	protected int checkSum = 0;
	protected int stop = 0;

	protected byte[] rawData = null;

	public BaseRecordParent(byte[] data) {
		this.rawData=data;
		if(log.isDebugEnabled()) {
			log.debug("RAW Data : \n" + Hex.decode(data));
		}
	}

	public int getStart1() {
		return start1;
	}

	public int getStart2() {
		return start2;
	}

	public int getLength1() {
		return length1;
	}

	public int getLength2() {
		return length2;
	}

	public Control getControl() {
		return control;
	}

	public ControlInformation getControlInformation() {
		return controlInformation;
	}

	public FixedDataHeader getFixedDataHeader() {
		return fixedDataHeader;
	}

	public DataBlocks getDataBlocks() {
		return dataBlocks;
	}

	public int getAddress() {
		return address;
	}

	public int getCheckSum() {
		return checkSum;
	}

	public int getStop() {
		return stop;
	}

}