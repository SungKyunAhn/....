package com.aimir.fep.protocol.fmp.exception;

/**
 * FMP Exception
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class FMPException extends Exception
{
    private String msg = null;
    /**
     * constructor
     */
    public FMPException()
    {
        super();
    }

    /**
     * constructor
     *
     * @param msg <code>String</code> message
     */
    public FMPException(String msg)
    {
        super(msg);
        this.msg = msg;
    }

    public FMPException(Throwable t)
    {
        super(t);
    }
    
    /**
     * get message
     *
     * @return msg <code>String</code> message
     */
    public String getMessage()
    {
        return this.msg;
    }
}
