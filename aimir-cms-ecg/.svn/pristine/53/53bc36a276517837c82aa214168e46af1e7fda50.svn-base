package com.aimir.cms.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import net.sf.json.JSONString;

import com.aimir.annotation.ColumnInfo;
import com.aimir.model.BaseObject;
import com.aimir.model.system.PrepaymentLog;

/**
 * 선불내역
 *  - 선불정보는 Contract 테이블에 갱신되며, 갱신될때마다 PrepaymentLog에 기록된다.
 *  
 */
@Entity
@Table(name="DEBTLOG")
@org.hibernate.annotations.Table(appliesTo = "DEBTLOG", 
    indexes={
    		@org.hibernate.annotations.Index(name="IDX_DEBTLOG_01", columnNames={"prepaymentLog_id"}),
            @org.hibernate.annotations.Index(name="IDX_DEBTLOG_02", columnNames={"prepaymentLog_id","customerId","DEBT_REF","DEBT_TYPE"})
})
public class DebtLog extends BaseObject implements JSONString{

	private static final long serialVersionUID = 4218162564823781897L;

	@Id
	@TableGenerator(name="DEBTLOG_GEN", table="KEY_GEN_DEBTLOG", allocationSize=1) 
    @GeneratedValue(strategy=GenerationType.TABLE, generator="DEBTLOG_GEN")
	private Long id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="prepaymentLog_id")
	@ColumnInfo(name="prepaymentLog", descr="고객 선불 결제 이력")
	private PrepaymentLog prepaymentLog;
	
	@Column(name="prepaymentLog_id", nullable=true, updatable=false, insertable=false)
	@ColumnInfo(name="prepaymentLog id", descr="고객 선불 결제 이력 id")
	private Long prepaymentLogId;
	
	@Column(name="customerId")
	@ColumnInfo(name="customerId", descr="DebtEnt 테이블의 customerId값과 동일한 값, customerNumber 값이 들어감.")
	private String customerId;
	
	@Column(name="DEBT_TYPE")
	@ColumnInfo(name="debtType", descr="지불한 debt의 type")
	private String debtType;
	
    @Column(name="DEBT_REF", length=50)
    @ColumnInfo(descr="debtE")
    private String debtRef;
	
	@ColumnInfo(descr="이전에 납부한 금액(납부 전 금액)")
	@Column(name="PRE_DEBT")
	private Double preDebt;
	
	@ColumnInfo(descr="납부 후 금액")
	@Column(name="DEBT")
	private Double debt;

	@ColumnInfo(descr="남부한 Debt 금액")
	@Column(name="chargedDebt")
	private Double chargedDebt;
	
	@ColumnInfo(descr="충전 당시 분할납부 상황 ")
	@Column(name="PARTPAYINFO")
	private String partpayInfo;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
    public PrepaymentLog getPrepaymentLog() {
		return prepaymentLog;
	}
	public void setPrepaymentLog(PrepaymentLog prepaymentLog) {
		this.prepaymentLog = prepaymentLog;
	}
	public Long getPrepaymentLogId() {
		return prepaymentLogId;
	}
	public void setPrepaymentLogId(Long prepaymentLogId) {
		this.prepaymentLogId = prepaymentLogId;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getDebtType() {
		return debtType;
	}
	public void setDebtType(String debtType) {
		this.debtType = debtType;
	}
	public String getDebtRef() {
		return debtRef;
	}
	public void setDebtRef(String debtRef) {
		this.debtRef = debtRef;
	}
	public Double getPreDebt() {
		return preDebt;
	}
	public void setPreDebt(Double preDebt) {
		this.preDebt = preDebt;
	}
	public Double getDebt() {
		return debt;
	}
	public void setDebt(Double debt) {
		this.debt = debt;
	}
	public Double getChargedDebt() {
		return chargedDebt;
	}
	public void setChargedDebt(Double chargedDebt) {
		this.chargedDebt = chargedDebt;
	}
	public String getPartpayInfo() {
		return partpayInfo;
	}
	public void setPartpayInfo(String partpayInfo) {
		this.partpayInfo = partpayInfo;
	}
	public String toJSONString() {

		String retValue = "";

	    retValue = "{"
	        + "id:'" + this.id
	        + "',prepaymentLogId:'" + this.prepaymentLogId 
	        + "',customerId:'" + this.customerId 
	        + "',debtType:'" + this.debtType
	        + "',debtRef:'" + this.debtRef 
		    + "',preDebt:'" + this.preDebt
		    + "',debt:'" + this.debt
		    + "',chargedDebt:'" + this.chargedDebt
		    + "',partpayInfo:'" + this.partpayInfo
	        + "'}";

	    return retValue;
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
	public String toString() {
		// TODO Auto-generated method stub
		return toJSONString();
	}
}
