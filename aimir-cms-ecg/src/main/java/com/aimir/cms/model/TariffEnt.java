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

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TariffEnt", propOrder = {
    "tariffCode",
    "tariffGroup"
})

@Entity
@Table(name="WS_CMS_TARIFFENT")
public class TariffEnt implements Serializable { 

	private static final long serialVersionUID = -8610338686864056918L;

	@XmlTransient
	@EmbeddedId public TariffEntPk id = new TariffEntPk();
	
    @XmlTransient
    @Column(name = "WRITE_DATE", length=14)
    private String writeDate;
    
	public String getWriteDate() {
		return writeDate;
	}
	public void setWriteDate(String writeDate) {
		this.writeDate = writeDate;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
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
	
	@XmlElement(name = "Tariff_code")
	public String getTariffCode() {
	    return id.getTariffCode();
	}
	
	public void setTariffCode(String tariffCode) {
	    id.setTariffCode(tariffCode);
	}
	
	@XmlElement(name = "Tariff_group")
	public Integer getTariffGroup() {
	    return id.getTariffGroup();
	}
	
	public void setTariffGroup(Integer tariffGroup) {
	    id.setTariffGroup(tariffGroup);
	}
}
