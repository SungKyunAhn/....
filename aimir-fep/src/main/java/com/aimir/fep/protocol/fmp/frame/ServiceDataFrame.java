package com.aimir.fep.protocol.fmp.frame;

import com.aimir.fep.protocol.fmp.datatype.INT;
import com.aimir.fep.protocol.fmp.datatype.UINT;
import com.aimir.fep.util.Hex;


/**
 * ServiceDataFrame
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class ServiceDataFrame extends GeneralDataFrame
{
    public UINT mcuId = new UINT();
    public byte[] svcBody = null;

    /**
     * constructor
     */
    public ServiceDataFrame()
    {
        super(GeneralDataConstants.SOH,(byte)0,
                new INT(0),(byte)0x00,(byte)0);
    }

    /**
     * get MCU ID
     *
     * @return mcuId <code>UINT</code> MCU ID
     */
    public UINT getMcuId()
    {
        return this.mcuId;
    }
    /**
     * set MCU ID
     *
     * @param mcuId <code>UINT</code> MCU ID
     */
    public void setMcuId(UINT mcuId)
    {
        this.mcuId = mcuId;
    }

    /**
     * get Service Body 
     *
     * @return mcuId <code>byte[]</code> Service Body
     */
    public byte[] getSvcBody()
    {
        return this.svcBody;
    }
    /**
     * set Service Body 
     *
     * @param mcuId <code>byte[]</code> Service Body
     */
    public void setSvcBody(byte[] svcBody)
    {
        this.svcBody = svcBody;
    }

    /**
     * get string
     *
     * @return result <code>String</code>
     */
    public String toString()
    {
        StringBuffer strbuf = new StringBuffer();
        strbuf.append(super.toString());
        strbuf.append("CLASS["+this.getClass().getName()+"]\n");
        strbuf.append("mcuId : " + mcuId + "\n");
        strbuf.append("svcBody : " + Hex.decode(svcBody) + "\n");

        return strbuf.toString();
    }
}
