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
public class DebtEntPk extends BasePk {

	private static final long serialVersionUID = 3801120852112519366L;
	
	@Column(name="CUSTOMER_ID", length=20)
    private String customerId;
    
	@Column(name="DEBT_REF", length=50)
    private String debtRef;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getDebtRef() {
        return debtRef;
    }

    public void setDebtRef(String debtRef) {
        this.debtRef = debtRef;
    }
}