package com.aimir.fep.iot.model.vo;

import java.io.Serializable;

public class RSSIVO extends CntInstCommonVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3370575299047201462L;
	
	private Double RSSI;

	public Double getRSSI() {
		return RSSI;
	}


	public void setRSSI(Double rSSI) {
		RSSI = rSSI;
	}
	

}
