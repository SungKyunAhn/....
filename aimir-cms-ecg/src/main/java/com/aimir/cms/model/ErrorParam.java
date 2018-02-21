package com.aimir.cms.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ErrorParam", propOrder = {
    "errorId",
    "errorMsg"
})
public class ErrorParam {
    @XmlElement(name = "ErrorID")
    private Long errorId;
    @XmlElement(name = "ErrorMsg")
    private String errorMsg;
    
    public Long getErrorId() {
        return errorId;
    }
    public void setErrorId(Long errorId) {
        this.errorId = errorId;
    }
    public String getErrorMsg() {
        return errorMsg;
    }
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
