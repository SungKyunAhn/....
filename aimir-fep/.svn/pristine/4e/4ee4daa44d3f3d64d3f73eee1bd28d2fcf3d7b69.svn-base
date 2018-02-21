package com.aimir.fep.protocol.mrp.protocol;


import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.protocol.mrp.command.frame.ReceiveDataFrame;
import com.aimir.fep.util.ByteArray;

/**
 * RequestDataFrame
 * 
 * @author Kang, Soyi
 */
public class DLMSKepco_ReceiveDataFrame extends ReceiveDataFrame
{
    private static Log log = LogFactory.getLog(DLMSKepco_ReceiveDataFrame.class);
    public int cnt;
    private ArrayList<byte[]> list = new ArrayList<byte[]>();

    /**
     * constructor
     */
    public DLMSKepco_ReceiveDataFrame()
    {
    }
    

    public void append(byte[] b)
    {
        log.debug("append =>"+new OCTET(b).toHexString());
        list.add(b);
        this.cnt++;
    }

    public byte[] encode() throws Exception
    {
        ByteArray array = new ByteArray();
        Iterator<byte[]> it = list.iterator();
        int idx = 0;
        while(it.hasNext()){
            byte[] temp = (byte[])it.next();
            array.append(temp); 
            idx++;
        }
        
        return array.toByteArray();
    }
    
    protected byte[] cutHeaderTail(byte[] org, boolean isFirst )
    {
        log.debug("org =>"+new OCTET(org).toHexString());
        byte[] ret = null;
        if(isFirst){
            ret = new byte[org.length - (7+2)];
            System.arraycopy(org, 0, ret, 0, ret.length);
        }else{
            ret = new byte[org.length - (7+2)];
            System.arraycopy(org, 0, ret, 0, ret.length);
        }

        return ret;
    }
    
    /**
     * decode
     *
     * @param bytebuffer <code>ByteBuffer</code> bytebuffer
     * @return frame <code>GeneralDataFrame</code> frame
     */
    public static DLMSKepco_ReceiveDataFrame decode(byte[] b) 
        throws Exception
    {
    	DLMSKepco_ReceiveDataFrame frame = null;
        return frame;
    }

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
