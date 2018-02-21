package com.aimir.fep.meter.parser.DLMSNamjunTable;

public class CaptureObject {

	private long classId;
	private String logicalName;
	private int attributeIndex;
	private long dataIndex;
	public long getClassId() {
		return classId;
	}
	public void setClassId(long classId) {
		this.classId = classId;
	}
	public String getLogicalName() {
		return logicalName;
	}
	public void setLogicalName(String logicalName) {
		this.logicalName = logicalName;
	}
	public int getAttributeIndex() {
		return attributeIndex;
	}
	public void setAttributeIndex(int attributeIndex) {
		this.attributeIndex = attributeIndex;
	}
	public long getDataIndex() {
		return dataIndex;
	}
	public void setDataIndex(long dataIndex) {
		this.dataIndex = dataIndex;
	}

}
