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
@XmlType(name = "MeterEnt", propOrder = {
    "meterSerialNo",
    "batchNo",
    "make",
    "model"
})

@Entity
@Table(name="WS_CMS_METERENT")
public class MeterEnt implements Serializable { 

	private static final long serialVersionUID = -3968483322699617810L;

	@XmlTransient
    @EmbeddedId public MeterEntPk id = new MeterEntPk();
	
    @XmlElement(name = "Batch_no")
    @Column(name="BATCH_NO", length=15)
    private String batchNo;
    
    @XmlElement(name = "Model")
    @Column(name="MODEL", length=5)
    private String model;
    
    @XmlTransient
    @Column(name = "WRITE_DATE", length=14)
    private String writeDate;
    
    @XmlElement(name = "Meter_serial_no")
    public String getMeterSerialNo() {
        return id.getMeterSerialNo();
    }
    public void setMeterSerialNo(String meterSerialNo) {
        id.setMeterSerialNo(meterSerialNo);
    }
    public String getBatchNo() {
        return batchNo;
    }
    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }
    
    @XmlElement(name = "Make")
    public String getMake() {
        return id.getMake();
    }
    public void setMake(String make) {
        id.setMake(make);
    }
    
    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }    
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
}
