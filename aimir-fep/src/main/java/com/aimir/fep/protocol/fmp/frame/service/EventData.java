package com.aimir.fep.protocol.fmp.frame.service;

import java.util.ArrayList;
import java.io.ByteArrayOutputStream;

import com.aimir.fep.protocol.fmp.datatype.BYTE;
import com.aimir.fep.protocol.fmp.datatype.HEX;
import com.aimir.fep.protocol.fmp.datatype.OID;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.datatype.TIMESTAMP;
import com.aimir.fep.protocol.fmp.datatype.WORD;
import com.aimir.fep.protocol.fmp.exception.FMPEncodeException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Event Service Data Class
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class EventData extends ServiceData
{
    private static Log log = LogFactory.getLog(EventData.class);
    public OID code = new OID();
    public BYTE srcType = new BYTE(); 
    public HEX srcId = new HEX(8); 
    public TIMESTAMP timeStamp = new TIMESTAMP(7); 
    public WORD cnt = new WORD();
    private ArrayList<SMIValue> smiValues = new ArrayList<SMIValue>();
    private final Integer serviceType = new Integer(2);

    /**
     * constructor
     */
    public EventData()
    {
    }

    /**
     * constructor
     *
     * @param oid <code>OID</code> oid
     */
    public EventData(OID code)
    {
        this.code = code;
    }

    /**
     * constructor
     *
     * @param code <code>OID</code> event code
     * @param srcType <code>BYTE</code> source type
     * @param srcId <code>HEX</code> source id
     * @param timeStamp <code>TIMESTAMP</code> timestamp
     */
    public EventData(OID code, BYTE srcType, HEX srcId,
            TIMESTAMP timeStamp)
    {
        this.code = code;
        this.srcType = srcType;
        this.srcId = srcId;
        this.timeStamp = timeStamp;
    }

    /**
     * get event code
     *
     * @return code <code>OID</code> event code
     */
    public OID getCode()
    {
        return this.code;
    }
    /**
     * set event code
     *
     * @param code <code>OID</code> event code
     */
    public void setCode(OID code)
    {
        this.code = code;
    }

    /**
     * get source type
     *
     * @return srcType <code>BYTE</code> source type
     */
    public BYTE getSrcType()
    {
        return this.srcType;
    }
    /**
     * set source type
     *
     * @param srcType <code>BYTE</code> source type
     */
    public void setSrcType(BYTE srcType)
    {
        this.srcType = srcType;
    }

    /**
     * get source id
     *
     * @return srcId <code>HEX</code> source id
     */
    public HEX getSrcId()
    {
        return this.srcId;
    }
    /**
     * set source id
     *
     * @param srcId <code>HEX</code> source id
     */
    public void setSrcId(HEX srcId)
    {
        this.srcId = srcId;
    }

    /**
     * get time stamp
     *
     * @return timeStamp <code>TIMESTAMP</code> time stamp
     */
    public TIMESTAMP getTimeStamp()
    {
        return this.timeStamp;
    }
    /**
     * set time stamp
     *
     * @param timeStamp <code>TIMESTAMP</code> time stamp
     */
    public void setTimeStamp(TIMESTAMP timeStamp)
    {
        this.timeStamp = timeStamp;
    }

    /**
     * get smivalue count
     *
     * @return cnt <code>WORD</code> tsmivalue coun
     */
    public WORD getCnt()
    {
        return this.cnt;
    }
    /**
     * set smivalue count
     *
     * @param cnt <code>WORD</code> tsmivalue coun
     */
    public void setCnt(WORD cnt)
    {
        this.cnt = cnt;
    }

    /**
     * append SMIValue
     *
     * @param smiValue <code>SMIValue</code> SMIValue
     */
    public void append(SMIValue smiValue)
    {
        smiValues.add(smiValue);
    }

    /**
     * get SMIValue list
     *
     * @return result <code>SMIValue[]</code>
     */
    public SMIValue[] getSMIValue()
    {
        return (SMIValue[])smiValues.toArray(new SMIValue[0]);
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
            int mibsize = smiValues.size();
            setCnt(new WORD(smiValues.size()));
            byte[] bx = null;

            bx = code.encode();
            bao.write(bx,0,bx.length);
            bx = srcType.encode();
            bao.write(bx[0]);
            bx = srcId.encode();
            bao.write(bx,0,bx.length);
            bx = timeStamp.encode();
            bao.write(bx,0,bx.length);
            bx = cnt.encode();
            bao.write(bx,0,bx.length);

            SMIValue[] smiValues = getSMIValue();
            for(int i = 0 ; i < smiValues.length ; i++)
            {
                bx = smiValues[i].encode();
                bao.write(bx,0,bx.length);
            }
            return bao.toByteArray();
        }catch(Exception ex)
        {
            log.error("EventData::encode failed :",ex);
            throw new FMPEncodeException(
                    "EventData::encode failed :"+ex.getMessage());
        }

    }

    /**
     * get String
     *
     * @return result <code>String</code>
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("Event Service Header\n");
        sb.append("mcuId=").append(getMcuId()).append(',');
        sb.append("code=").append(code).append(',');
        sb.append("srcType=").append(srcType).append(',');
        sb.append("srcId=").append(srcId).append(',');
        sb.append("timeStamp=").append(timeStamp).append(',');
        sb.append("cnt=").append(cnt).append('\n');

        SMIValue[] smiValues = getSMIValue();
        sb.append("Event Service Data\n");
        for(int i = 0 ; i < smiValues.length ; i++)
        {
            sb.append("SMIValue[").append(i).append("]=");
            if (smiValues[i] == null)
                sb.append('\n');
            else 
                sb.append(smiValues[i]).append('\n');
        }

        return sb.toString();
    }
}
