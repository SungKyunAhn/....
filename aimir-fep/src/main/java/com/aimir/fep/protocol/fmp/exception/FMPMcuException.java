package com.aimir.fep.protocol.fmp.exception;

/**
 * FMP MCU Exception
 * 
 * @author yskim
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class FMPMcuException extends FMPException
{
    private String msg = null;
    private int code = 0;

    /**
     * constructor
     */
    public FMPMcuException()
    {
        super();
    }

    public FMPMcuException(String msg)
    {
        super();
        this.msg = msg;
    }
    
    /**
     * constructor
     *
     * @param msg <code>String</code> message
     */
    public FMPMcuException(String msg, int code)
    {
        super();
        this.msg = msg;
        this.code = code;
    }

    /**
     * get Message
     *
     * @return code <code>int</code> message
     */
    public String getMessage()
    {
        return this.msg;
    }

    /**
     * get error code
     *
     * @return code <code>int</code> message
     */
    public int getCode()
    {
        return this.code;
    }
}
