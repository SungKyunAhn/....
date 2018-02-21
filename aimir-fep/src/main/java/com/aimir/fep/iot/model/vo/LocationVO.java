package com.aimir.fep.iot.model.vo;

import java.io.Serializable;

public class LocationVO extends CntInstCommonVO implements Serializable {

	private Double GPIOX;
	private Double GPIOY;
	private Double GPIOZ;
	private String ADDR1;
	public Double getGPIOX() {
		return GPIOX;
	}
	public void setGPIOX(Double gPIOX) {
		GPIOX = gPIOX;
	}
	public Double getGPIOY() {
		return GPIOY;
	}
	public void setGPIOY(Double gPIOY) {
		GPIOY = gPIOY;
	}
	public Double getGPIOZ() {
		return GPIOZ;
	}
	public void setGPIOZ(Double gPIOZ) {
		GPIOZ = gPIOZ;
	}
	public String getADDR1() {
		return ADDR1;
	}
	public void setADDR1(String aDDR1) {
		ADDR1 = aDDR1;
	}


}
