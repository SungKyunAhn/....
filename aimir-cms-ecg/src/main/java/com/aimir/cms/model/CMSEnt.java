package com.aimir.cms.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CMSEnt", propOrder = {
    "customer",
    "servicePoint"
})
public class CMSEnt {
    @XmlElement(name = "Customer")
    private CustEnt customer;
    @XmlElement(name = "service_point")
    private ServPoint servicePoint;
    
    public CustEnt getCustomer() {
        return customer;
    }
    public void setCustomer(CustEnt customer) {
        this.customer = customer;
    }
    public ServPoint getSerivcePoint() {
        return servicePoint;
    }
    public void setSerivcePoint(ServPoint servicePoint) {
        this.servicePoint = servicePoint;
    }
}
