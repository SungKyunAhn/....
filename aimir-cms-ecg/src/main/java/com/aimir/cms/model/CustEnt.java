package com.aimir.cms.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.aimir.annotation.ReferencedBy;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustEnt", propOrder = {
    "customerId",
    "surname",
    "otherNames",
    "idNo",
    "idType",
    "address1",
    "address2",
    "address3",
    "telephone1",
    "telephone2",
    "telephone3",
    "fax",
    "email",
    "taxRefNo",
    "exist"
})

@Entity
@Table(name="WS_CMS_CUSTENT")
public class CustEnt implements Serializable  { 

	private static final long serialVersionUID = 2527621800371935758L;
	
	@XmlElement(name = "CustomerID")
	@Id
    @Column(name="CUSTOMER_ID", length=20)
    private String customerId;
	
    @XmlElement(name = "Surname")
    @Column(name="SURNAME", length=60)
    private String surname;
    
    @XmlElement(name = "Other_names")
    @Column(name = "OTHER_NAMES", length=80)
    private String otherNames;
    
    @XmlElement(name = "Id_no")
    @Column(name = "ID_NO", length=20)
    private String idNo;
    
    @XmlElement(name = "Id_type")
    @Column(name = "ID_TYPE", length=5)
    private String idType;
    
    @XmlElement(name = "Address_1")
    @Column(name = "ADDRESS_1", length=90)
    private String address1;
    
    @XmlElement(name = "Address_2")
    @Column(name = "ADDRESS_2", length=130)
    private String address2;
    
    @XmlElement(name = "Address_3")
    @Column(name = "ADDRESS_3", length=80)
    private String address3;
    
    @XmlElement(name = "Telephone_1")
    @Column(name = "TELEPHONE_1", length=15)
    private String telephone1;
    
    @XmlElement(name = "Telephone_2")
    @Column(name = "TELEPHONE_2", length=15)
    private String telephone2;
    
    @XmlElement(name = "Telephone_3")
    @Column(name = "TELEPHONE_3", length=15)
    private String telephone3;
    
    @XmlElement(name = "Fax")
    @Column(name = "FAX", length=22)
    private String fax;
    
    @XmlElement(name = "email")
    @Column(name = "EMAIL", length=50)
    private String email;
    
    @XmlElement(name = "Tax_ref_no")
    @Column(name = "TAX_REF_NO", length=14)
    private String taxRefNo;
    
    @XmlElement(name = "Exist")
    @Column(name = "EXIST")
    private Boolean exist;
    
    @XmlTransient
    @Column(name = "WRITE_DATE", length=14)
    private String writeDate;
    
    @XmlTransient
    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "servPointId", nullable=true)
    @ReferencedBy(name="servPointId")
    private ServPoint servPoint;
    
    public String getCustomerId() {
        return customerId;
    }
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    public String getSurname() {
        return surname;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }
    public String getOtherNames() {
        return otherNames;
    }
    public void setOtherNames(String otherNames) {
        this.otherNames = otherNames;
    }
    public String getIdNo() {
        return idNo;
    }
    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }
    public String getIdType() {
        return idType;
    }
    public void setIdType(String idType) {
        this.idType = idType;
    }
    public String getAddress1() {
        return address1;
    }
    public void setAddress1(String address1) {
        this.address1 = address1;
    }
    public String getAddress2() {
        return address2;
    }
    public void setAddress2(String address2) {
        this.address2 = address2;
    }
    public String getAddress3() {
        return address3;
    }
    public void setAddress3(String address3) {
        this.address3 = address3;
    }
    public String getTelephone1() {
        return telephone1;
    }
    public void setTelephone1(String telephone1) {
        this.telephone1 = telephone1;
    }
    public String getTelephone2() {
        return telephone2;
    }
    public void setTelephone2(String telephone2) {
        this.telephone2 = telephone2;
    }
    public String getTelephone3() {
        return telephone3;
    }
    public void setTelephone3(String telephone3) {
        this.telephone3 = telephone3;
    }
    public String getFax() {
        return fax;
    }
    public void setFax(String fax) {
        this.fax = fax;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getTaxRefNo() {
        return taxRefNo;
    }
    public void setTaxRefNo(String taxRefNo) {
        this.taxRefNo = taxRefNo;
    }
    public Boolean isExist() {
        return exist;
    }
    public void setExist(Boolean exist) {
        this.exist = exist;
    }    
    
	public String getWriteDate() {
		return writeDate;
	}
	public void setWriteDate(String writeDate) {
		this.writeDate = writeDate;
	}
	//@Override
	//public String getInstanceName() {
	//	return this.getCustomerId();
	//}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}
	public ServPoint getServPoint() {
        return servPoint;
    }
    public void setServPoint(ServPoint servPoint) {
        this.servPoint = servPoint;
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
    
}
