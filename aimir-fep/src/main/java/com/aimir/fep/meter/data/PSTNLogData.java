package com.aimir.fep.meter.data;


/**
 * meter PSTN comm log Data Class
 *
 * @author Kang, Soyi
 */
public class PSTNLogData
{
    private String protocolType = null; // 0: lan, 1: pstn
    private String operationDateTime = null;
    private String portNo = null;
    private String callDateTime = null;
    private String phoneNumber = null;
    private String callResult = null;
    private String callResultDateTime = null;
    
    /**
     * Constructor
     */
    public PSTNLogData()
    {
    }
    /**
     * get protocolType
     * @return protocolType
     */
    public String getProtocolType()
    {
        return this.protocolType;
    }
    /**
     * set protocolType
     * @param protocolType
     */
    public void setProtocolType(String protocolType)
    {
        this.protocolType = protocolType;
    }
    /**
     * get operationDateTime
     * @return operationDateTime
     */
    public String getOperationDateTime()
    {
        return this.operationDateTime;
    }
    /**
     * set operationDateTime
     * @param operationDateTime
     */
    public void setOperationDateTime(String operationDateTime)
    {
        this.operationDateTime = operationDateTime;
    }
    /**
     * get portNo
     * @return portNo
     */
    public String getPortNo()
    {
        return this.portNo;
    }
    /**
     * set portNo
     * @param portNo
     */
    public void setPortNo(String portNo)
    {
        this.portNo = portNo;
    }
    /**
     * get callDateTime
     * @return callDateTime
     */
    public String getCallDateTime()
    {
        return this.callDateTime;
    }
    /**
     * set callDateTime
     * @param callDateTime
     */
    public void setCallDateTime(String callDateTime)
    {
        this.callDateTime = callDateTime;
    }
    /**
     * get phoneNumber
     * @return phoneNumber
     */
    public String getPhoneNumber()
    {
        return this.phoneNumber;
    }
    /**
     * set phoneNumber
     * @param phoneNumber
     */
    public void setPhoneNumber(String phoneNumber)
    {
        this.phoneNumber = phoneNumber;
    }
    /**
     * get callResult
     * @return callResult
     */
    public String getCallResult()
    {
        return this.callResult;
    }
    /**
     * set callResult
     * @param callResult
     */
    public void setCallResult(String callResult)
    {
        this.callResult = callResult;
    }
    /**
     * get callResultDateTime
     * @return callResultDateTime
     */
    public String getCallResultDateTime()
    {
        return this.callResultDateTime;
    }
    /**
     * set callResultDateTime
     * @param callResultDateTime
     */
    public void setCallResultDateTime(String callResultDateTime)
    {
        this.callResultDateTime = callResultDateTime;
    }
    
	/**
     * get string
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("protocolType=[").append(protocolType).append("],");
        sb.append("operationDateTime=[").append(operationDateTime).append("],");
        sb.append("portNo=[").append(portNo).append("],");
        sb.append("callDateTime=[").append(callDateTime).append("],");
        sb.append("phoneNumber=[").append(phoneNumber).append("],");
        sb.append("callResult=[").append(callResult).append("],");
        sb.append("callResultDateTime=[").append(callResultDateTime).append(']');
        
        return sb.toString();
    }
}
