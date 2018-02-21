package com.aimir.fep.meter.parser.MBusTable;

public abstract class DataBlocks implements java.io.Serializable{
	protected byte[] rawData = null;
	protected int hourCnt = 0;
	protected ControlInformation controlInformation = null;
	protected DataBlock dataBlock[] = null;
	protected String[] dataNames = null;

	public static final int DATABLOCK_CASE_BASE = 0;
	public static final int DATABLOCK_CASE_LP = 1;
	protected int dataBlockCase = DATABLOCK_CASE_BASE;

	/**
	 * Constructor(Default Base Info)
	 * @param hourCnt
	 * @param rawData
	 * @param controlInformation
	 */
	public DataBlocks(int hourCnt, byte[] rawData, ControlInformation controlInformation) {
		this.rawData=rawData;
		this.hourCnt = hourCnt;
		this.controlInformation = controlInformation;
	}

	/**
	 * Constructor
	 * @param hourCnt
	 * @param rawData
	 * @param controlInformation
	 * @param dataBlockCase Base(DATABLOCK_CASE_BASE), LP(DATABLOCK_CASE_LP)
	 */
	public DataBlocks(int hourCnt, byte[] rawData, ControlInformation controlInformation, int dataBlockCase) {
		this.rawData=rawData;
		this.hourCnt = hourCnt;
		this.controlInformation = controlInformation;
		this.dataBlockCase = dataBlockCase;
	}

	public DataBlock[] getDataBlock() {
		return dataBlock;
	}
	public void setDataBlock(DataBlock[] dataBlock) {
		this.dataBlock = dataBlock;
	}
}
