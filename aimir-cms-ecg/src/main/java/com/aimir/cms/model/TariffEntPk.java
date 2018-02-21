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
public class TariffEntPk extends BasePk {

	private static final long serialVersionUID = 3801120852112519366L;
	
	
    @Column(name="TARIFF_CODE", length=20)
    private String tariffCode;
    
    @Column(name="TARIFF_GROUP", length=5)
    private Integer tariffGroup;

    public String getTariffCode() {
        return tariffCode;
    }
    public void setTariffCode(String tariffCode) {
        this.tariffCode = tariffCode;
    }
    public Integer getTariffGroup() {
        return tariffGroup;
    }
    public void setTariffGroup(Integer tariffGroup) {
        this.tariffGroup = tariffGroup;
    }
}