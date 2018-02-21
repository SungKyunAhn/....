package com.aimir.cms.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.aimir.annotation.ReferencedBy;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServPoint", propOrder = {
    "servPointId",
    "address1",
    "address2",
    "address3",
    "geoCode",
    "meter",
    "tariff",
    "blockFlag",
    "blockReason",
    "exist"
})

@Entity
@Table(name="WS_CMS_SERVPOINT")
public class ServPoint implements Serializable { 

	private static final long serialVersionUID = 2321569315037277520L;

	@XmlElement(name = "servPointID")
	@Id
    @Column(name="SERVPOINT_ID", length=20)
    private String servPointId;
    
    @XmlElement(name = "Address_1")
    @Column(name = "ADDRESS_1", length=90)
    private String address1;
    
    @XmlElement(name = "Address_2")
    @Column(name = "ADDRESS_2", length=130)
    private String address2;
    
    @XmlElement(name = "Address_3")
    @Column(name = "ADDRESS_3", length=80)
    private String address3;
    
    @XmlElement(name = "Geo_code")
    @Column(name = "GEO_CODE", length=30)
    private String geoCode;
    
    @XmlElement(name = "meter")
    @OneToOne(fetch=FetchType.LAZY)    
    @JoinColumns({
        @JoinColumn(name="METER_SERIAL_NO", referencedColumnName="METER_SERIAL_NO"),
        @JoinColumn(name="MAKE", referencedColumnName="MAKE")
        })
    private MeterEnt meter;
    
    @XmlTransient
    @Column(name="METER_SERIAL_NO", length=20, updatable=false, insertable=false)
    private String meterSerialNo;
    
    @XmlElement(name = "tariff")
    @ManyToOne(fetch=FetchType.LAZY)    
    @JoinColumns({
        @JoinColumn(name="TARIFF_CODE", referencedColumnName="TARIFF_CODE"),
        @JoinColumn(name="TARIFF_GROUP", referencedColumnName="TARIFF_GROUP")
        })
    private TariffEnt tariff;
    
    @XmlTransient
    @Column(name="TARIFF_CODE", length=20, updatable=false, insertable=false)
    private String tariffCode;
    
    @XmlTransient
    @Column(name="TARIFF_GROUP", length=5, updatable=false, insertable=false)
    private Integer tariffGroup;
    
    @XmlElement(name = "blockFlag")
    @Column(name = "BLOCK_FLAG")
    private Boolean blockFlag;
    
    @XmlElement(name = "blockReason")
    @Column(name = "BLOCK_REASON", length=1024)
    private String blockReason;
    
    @XmlElement(name = "Exist")
    @Column(name = "EXIST")
    private Boolean exist;
    
    @XmlTransient
    @Column(name = "WRITE_DATE", length=14)
    private String writeDate;
    
    @XmlTransient
    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "customerId", nullable=true)
    @ReferencedBy(name="customerId")
    private CustEnt custEnt;
    
    public String getServPointId() {
        return servPointId;
    }
    public void setServPointId(String servPointId) {
        this.servPointId = servPointId;
    }
    public String getGeoCode() {
        return geoCode;
    }
    public void setGeoCode(String geoCode) {
        this.geoCode = geoCode;
    }
    
    @XmlTransient
    public MeterEnt getMeter() {
        return meter;
    }
    public void setMeter(MeterEnt meter) {
        this.meter = meter;
    }
    
    @XmlTransient
    public TariffEnt getTariff() {
        return tariff;
    }
    public void setTariff(TariffEnt tariff) {
        this.tariff = tariff;
    }
    public Boolean isExist() {
        return exist;
    }
    public void setExist(Boolean exist) {
        this.exist = exist;
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
	public String getWriteDate() {
		return writeDate;
	}
	public void setWriteDate(String writeDate) {
		this.writeDate = writeDate;
	}

	public Boolean getBlockFlag() {
        return blockFlag;
    }
    public void setBlockFlag(Boolean blockFlag) {
        this.blockFlag = blockFlag;
    }
    public String getBlockReason() {
        return blockReason;
    }
    public void setBlockReason(String blockReason) {
        this.blockReason = blockReason;
    }
    
    public CustEnt getCustEnt() {
        return custEnt;
    }
    public void setCustEnt(CustEnt custEnt) {
        this.custEnt = custEnt;
    }
    
    public String getMeterSerialNo() {
        return meterSerialNo;
    }
    public void setMeterSerialNo(String meterSerialNo) {
        this.meterSerialNo = meterSerialNo;
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
