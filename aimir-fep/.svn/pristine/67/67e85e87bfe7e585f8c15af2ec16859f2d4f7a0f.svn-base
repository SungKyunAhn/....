package com.aimir.fep.protocol.fmp.frame.service.entry;

import com.aimir.fep.protocol.fmp.datatype.*;
import com.aimir.fep.protocol.fmp.frame.service.Entry;

public class drAssetEntry extends Entry {
	private HEX  id = new HEX();
	private BOOL bDRAsset = new BOOL();
	private BYTE nCurrentLevel = new BYTE();
	private TIMESTAMP lastUpdate = new TIMESTAMP();
	private BYTE nLevelCount = new BYTE();
	private String aLevelArray;

	public HEX getId() {
		return id;
	}

	public void setId(HEX id) {
		this.id = id;
	}

	public BOOL getbDRAsset() {
		return bDRAsset;
	}

	public void setbDRAsset(BOOL bDRAsset) {
		this.bDRAsset = bDRAsset;
	}

	public BYTE getnCurrentLevel() {
		return nCurrentLevel;
	}

	public void setnCurrentLevel(BYTE nCurrentLevel) {
		this.nCurrentLevel = nCurrentLevel;
	}

	public TIMESTAMP getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(TIMESTAMP lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public BYTE getnLevelCount() {
		return nLevelCount;
	}

	public void setnLevelCount(BYTE nLevelCount) {
		this.nLevelCount = nLevelCount;
	}
	
	public String toString() {
        StringBuffer sb = new StringBuffer();

		sb.append("CLASS["+this.getClass().getName()+"]\n");
		sb.append("id: " + id + "\n");
		sb.append("bDRAsset: " + bDRAsset + "\n");
		sb.append("nCurrentLevel: " + nCurrentLevel + "\n");
		sb.append("lastUpdate: " + lastUpdate + "\n");
		sb.append("nLevelCount: " + nLevelCount + "\n");
		sb.append("aLevelArray: " + aLevelArray + "\n");

        return sb.toString();
	}

	public String getaLevelArray() {
		return aLevelArray;
	}

	public void setaLevelArray(String aLevelArray) {
		this.aLevelArray = aLevelArray;
	}
}
