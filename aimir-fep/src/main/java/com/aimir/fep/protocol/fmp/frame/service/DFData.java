package com.aimir.fep.protocol.fmp.frame.service;

import java.io.ByteArrayOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.exception.FMPEncodeException;
import com.aimir.fep.util.Hex;

/**
 * Data file Class
 * 
 * @author J.S Park (elevas@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2009-06-22 15:59:15 +0900 $,
 */
public class DFData extends ServiceData
{
    private static Log log = LogFactory.getLog(DFData.class);
    private byte[] dfData = null;
    private final Integer serviceType = new Integer(3);

    /**
     * constructor
     */
    public DFData()
    {
    }

    /**
     * get srouce id
     *
     * @return srcId <code>HEX</code> source id
     */
    public byte[] getDfData()
    {
        return this.dfData;
    }
    /**
     * set srouce id
     *
     * @param srcId <code>HEX</code> source id
     */
    public void setDfData(byte[] dfData)
    {
        this.dfData = dfData;
    }

    public Integer getServiceType() { return serviceType; }

    /**
     * encode
     *
     * @return result <code>byte[]</code>
     */
    public byte[] encode() throws FMPEncodeException
    {
        try
        {
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            bao.write(dfData,0,dfData.length);
            return bao.toByteArray();
        }catch(Exception ex)
        {
            log.error("MDData::encode failed :",ex);
            throw new FMPEncodeException(
                    "MDData::encode failed :"
                    +ex.getMessage());
        }
    }

    /**
     * get String
     *
     * @param result <code>String</code>
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("DataFile Service Header\n");
        if(dfData != null)
        {
            //sb.append("mdData=[").append(Hex.decode(mdData))
            //  .append("]\n");
            sb.append("dfData=[").append(Hex.getHexDump(dfData))
              .append("]\n");
        }
        else
            sb.append("dfData=").append('\n');

        return sb.toString();
    }
}
