package com.aimir.cms.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.aimir.annotation.ColumnInfo;

import net.sf.json.JSONString;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DebtEnt", propOrder = {
    "customerId",
    "debtType",
    "debtAmount",
    "debtRef",
    "installmentDueDate",
    "debtStatus"
})

@Entity
@Table(name="WS_CMS_DEBTENT")
public class DebtEnt implements Serializable, JSONString { 

	private static final long serialVersionUID = 4958774426119312422L;

	@XmlTransient
    @EmbeddedId public DebtEntPk id = new DebtEntPk();
	
    @XmlElement(name = "Debt_type")
    @Column(name="DEBT_TYPE", length=5)
    private String debtType;
    
    @XmlElement(name = "Debt_amount")
    @Column(name="DEBT_AMOUNT", precision=15, scale=3)
    private Double debtAmount;
    
    @XmlElement(name = "Instalment_due_date")
    @Column(name="INSTALLMENT_DUE_DATE", length=8)
    private String installmentDueDate;
    
    @XmlElement(name = "Debt_status")
    @Column(name="DEBT_STATUS", length=20)
    private String debtStatus;    
    
    @XmlTransient
    @Column(name = "WRITE_DATE", length=14)
    private String writeDate;
    
	/**
	 * 분할납부시 사용
	 * FIRSTARREARS, ARREARS_CONTRACT_COUNT, ARREARS_PAYMENT_COUNT
	 * 
	 */
	 
    @XmlTransient
	@Column(name = "FIRST_DEBT")
	@ColumnInfo(name="FIRST_DEBT", descr="처음 입력한 미납금 (미납금 지불 금액(초기미수금을 DebtContractCount로 나눈 값)을 알기위함), 분할납부시 사용")
	private Double firstDebt;
	
    @XmlTransient
	@Column(name = "DEBT_CONTRACT_COUNT")
	@ColumnInfo(name="DEBT_CONTRACT_COUNT", descr="미납금 지불 계약 횟수, 분할납부시 사용")
	private Integer debtContractCount;
	
    @XmlTransient
	@Column(name = "DEBT_PAYMENT_COUNT")
	@ColumnInfo(name="DEBT_PAYMENT_COUNT", descr="미납금 지불 완료 횟수, 분할납부시 사용, 0이 default, 정상적으로 분할납부 완료시 해당 값은 null")
	private Integer debtPaymentCount;
    
    @XmlElement(name = "CustomerID")
    public String getCustomerId() {
        return id.getCustomerId();
    }
    public void setCustomerId(String customerId) {
        id.setCustomerId(customerId);
    }
    public String getDebtType() {
        return debtType;
    }
    public void setDebtType(String debtType) {
        this.debtType = debtType;
    }
    public Double getDebtAmount() {
        return debtAmount;
    }
    public void setDebtAmount(Double debtAmount) {
        this.debtAmount = debtAmount;
    }
    public String getDebtStatus() {
        return debtStatus;
    }
    public void setDebtStatus(String debtStatus) {
        this.debtStatus = debtStatus;
    }
	public String getWriteDate() {
		return writeDate;
	}
	public void setWriteDate(String writeDate) {
		this.writeDate = writeDate;
	}

	@XmlElement(name = "Debt_ref")
	public String getDebtRef() {
        return id.getDebtRef();
    }
    public void setDebtRef(String debtRef) {
        id.setDebtRef(debtRef);
    }
    public String getInstallmentDueDate() {
        return installmentDueDate;
    }
    public void setInstallmentDueDate(String installmentDueDate) {
        this.installmentDueDate = installmentDueDate;
    }

	public Double getFirstDebt() {
		return firstDebt;
	}
	public void setFirstDebt(Double firstDebt) {
		this.firstDebt = firstDebt;
	}
	public Integer getDebtContractCount() {
		return debtContractCount;
	}
	public void setDebtContractCount(Integer debtContractCount) {
		this.debtContractCount = debtContractCount;
	}
	public Integer getDebtPaymentCount() {
		return debtPaymentCount;
	}
	public void setDebtPaymentCount(Integer debtPaymentCount) {
		this.debtPaymentCount = debtPaymentCount;
	}
	@Override
	public String toString() {
		return "DebtEnt "+toJSONString();
	}
	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public String toJSONString() {
		String retValue = "";
		
	    retValue = "{"
	        + "customerId:'" + this.id.getCustomerId()
	        + "',debtRef:'" + this.id.getDebtRef() 
	        + "',debtType:'" + this.debtType 
	        + "',debtAmount:'" + this.debtAmount 
	        + "',installmentDueDate:'" + this.installmentDueDate  
	        + "',debtStatus:'" + this.debtStatus  
	        + "',writeDate:'" + this.writeDate  
	        + "',firstDebt:'" + this.firstDebt  
	        //+ "',debtContractCount:'" + this.debtContractCount  
	        //+ "',debtPaymentCount:'" + this.debtPaymentCount  
	        + "'}";
	    
	    return retValue;
	}    
}
