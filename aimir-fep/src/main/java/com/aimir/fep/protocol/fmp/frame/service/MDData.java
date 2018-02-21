package com.aimir.fep.protocol.fmp.frame.service;

import java.io.ByteArrayOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.datatype.WORD;
import com.aimir.fep.protocol.fmp.exception.FMPEncodeException;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.NetworkType;
import com.aimir.fep.util.Hex;

/**
 * Measurement Data Service Data Class
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class MDData extends ServiceData
{
    private static Log log = LogFactory.getLog(MDData.class);
    private byte[] mdData = null;
    public WORD  cnt = new WORD();
    private final Integer serviceType = new Integer(3);
    private NetworkType networkType;

    /**
     * constructor
     */
    public MDData()
    {
    }

    /**
     * constructor
     *
     * @param srcType <code>WORD</code> soruce type
     * @param srcId <code>HEX</code> soruce id
     * @param timeStamp <code>BCD</code> time stamp
     * @param alarmMessage <code>OCTET</code> alarm message
     */
    public MDData(WORD cnt)
    {
        this.cnt = cnt;
    }

    /**
     * get srouce type
     *
     * @return srcType <code>WORD</code> source type
     */
    public WORD getCnt()
    {
        return this.cnt;
    } 
    /**
     * set srouce type
     *
     * @param srcType <code>WORD</code> source type
     */
    public void setCnt(WORD cnt)
    {
        this.cnt = cnt;
    }
    
    /**
     * get srouce id
     *
     * @return srcId <code>HEX</code> source id
     */
    public byte[] getMdData()
    {
        return this.mdData;
    }
    /**
     * set srouce id
     *
     * @param srcId <code>HEX</code> source id
     */
    public void setMdData(byte[] mdData)
    {
        this.mdData = mdData;
    }

    public Integer getServiceType() { return serviceType; }

    public NetworkType getNetworkType() {
        return networkType;
    }

    public void setNetworkType(NetworkType networkType) {
        this.networkType = networkType;
    }

    public String getProtocolType() {
        if (networkType == NetworkType.MBB)
            return "GPRS";
        else if (networkType == NetworkType.Ethernet)
            return "IP";
        return null;
    }
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
            /*
            if(mdData == null)
                cnt.setValue(0);
            else
                cnt.setValue(mdData.length);
            */
            byte[] bx = cnt.encode();
            bao.write(bx,0,bx.length);
            if(cnt.getValue() > 0)
                bao.write(mdData,0,mdData.length);
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
        sb.append("MeasurementData Service Header\n");
        sb.append("cnt=").append(getCnt()).append(',');
        if(mdData != null)
        {
            //sb.append("mdData=[").append(Hex.decode(mdData))
            //  .append("]\n");
            sb.append("mdData=[").append(Hex.getHexDump(mdData))
              .append("]\n");
        }
        else
            sb.append("mdData=").append('\n');

        return sb.toString();
    }
}
