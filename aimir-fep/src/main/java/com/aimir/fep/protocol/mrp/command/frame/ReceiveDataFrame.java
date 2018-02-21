package com.aimir.fep.protocol.mrp.command.frame;


import java.util.ArrayList;

import com.aimir.fep.protocol.fmp.datatype.OCTET;

/**
 * RequestDataFrame
 * 
 * @author Yeon Kyoung Park
 * @version $Rev: 1 $, $Date: 2008-01-05 15:59:15 +0900 $,
 */
public abstract class ReceiveDataFrame
{

    public int cnt;
    @SuppressWarnings("unused")
    private ArrayList<byte[]> list = new ArrayList<byte[]>();

    /**
     * constructor
     */
    public ReceiveDataFrame()
    {
    }
    

    public abstract void append(byte[] b);

    public abstract byte[] encode() throws Exception;

    /**
     * get string
     */
    public String toString()
    {
        StringBuffer strbuf = new StringBuffer();
        try
        {        
            strbuf.append("CLASS["+this.getClass().getName()+"]\n");
            strbuf.append("service : " + new OCTET(encode()).toHexString() + "\n");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return strbuf.toString();
    }
}
