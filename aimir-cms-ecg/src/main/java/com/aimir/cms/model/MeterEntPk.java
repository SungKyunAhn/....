package com.aimir.cms.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.aimir.model.BasePk;


/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * AsyncCommandLog 클래스의 Primary Key 정보를 정의한 클래스
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Embeddable
public class MeterEntPk extends BasePk {

	private static final long serialVersionUID = 3801120852112519366L;
	
	@Column(name="METER_SERIAL_NO", length=20)
    private String meterSerialNo;
    
	@Column(name="MAKE", length=5)
    private String make;

    public String getMeterSerialNo() {
        return meterSerialNo;
    }

    public void setMeterSerialNo(String meterSerialNo) {
        this.meterSerialNo = meterSerialNo;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

}