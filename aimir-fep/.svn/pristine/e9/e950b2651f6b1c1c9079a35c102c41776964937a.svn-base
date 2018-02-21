package com.aimir.fep.protocol.smcp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;

import com.aimir.fep.protocol.fmp.datatype.INT;
import com.aimir.fep.protocol.fmp.datatype.UINT;

/**
 * SMCPDataFrame
 * 
 * @author goodjob (goodjob@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2014-09-30 1200:00 +0900 $,
 */
public abstract class SMCPDataFrame
{
    private static Log log = LogFactory.getLog(SMCPDataFrame.class);
    public byte soh = SMCPDataConstants.SOH;
    
    public byte ctl = 0x00;
    public byte ver = 0x00;
    public byte[] did = new byte[10];//10bytes
    public byte[] sid = new byte[10];//10bytes
    public UINT psn = new UINT();//4bytes
    public byte[] datalen = new byte[2]; //2bytes
    public byte cmd =  0x00;
    
    public byte[] dataField = null;
    
    

    //SMCP FIELD
    //CRC 2
    //EOF 1`
    

    private int rcvBodyLength = 0;
    /**
     * constructor
     */
    protected SMCPDataFrame()
    {
    }

    /**
     * constructor
     *
     * @param soh <code>String</code> Start of header

     */
    protected SMCPDataFrame(byte soh, byte sequence,
            INT length, byte attr, byte svc)
    {
        this.soh = soh;

    }

    /**
     * get SOH
     *
     * @return soh <code>byte</code> start of header
     */
    public byte getSoh()
    {
        return this.soh;
    }

    /**
     * set SOH
     *
     * @param soh <code>byte</code> start of header
     */
    public void setSoh(byte soh)
    {
        this.soh = soh;
    }


    public int getRcvBodyLength()
    {
        return rcvBodyLength;
    }

    public void setRcvBodyLength(int rcvBodyLength)
    {
        this.rcvBodyLength = rcvBodyLength;
    }

    /**
     * decode
     *
     * @param bytebuffer <code>ByteBuffer</code> bytebuffer
     * @return frame <code>SMCPDataFrame</code> frame
     */
    public static SMCPDataFrame decode(
            IoBuffer bytebuffer) throws Exception
    {
		return null;

    } 

    /**
     * get string
     * @return result <code>String</code>
     */
    public String toString()
    {
        StringBuffer strbuf = new StringBuffer();
        strbuf.append("CLASS["+this.getClass().getName()+"]\n");
        strbuf.append("soh : " + soh + "\n");

        return strbuf.toString();
    }

    /**
     * encode
     *
     * @return result <code>byte[]</code> 
     */
    public byte[] encode() throws Exception
    {
		return null;

    }


    /**
     * set data length
     *
     * @throws Exception
     */
    public void setDataLength() throws Exception
    {

    }
    
    
    

}
