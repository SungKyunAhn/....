package com.aimir.fep.protocol.fmp.frame.service;

import java.util.ArrayList;
import java.io.ByteArrayOutputStream;

import com.aimir.fep.protocol.fmp.datatype.BYTE;
import com.aimir.fep.protocol.fmp.datatype.HEX;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.protocol.fmp.datatype.OID;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.datatype.TIMESTAMP;
import com.aimir.fep.protocol.fmp.datatype.WORD;
import com.aimir.fep.protocol.fmp.exception.FMPEncodeException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * EventData_1_2 Service Data Class
 * 
 * @author goodjob (goodjob@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2013-08-26 10:00:00 +0900 $,
 */
public class EventData_1_2 extends ServiceData
{
	private static final long serialVersionUID = -7469283423751784456L;
	private static Log log = LogFactory.getLog(EventData_1_2.class);
    public OCTET nameSpace = new OCTET(2);
    public OID oid = new OID();
    public BYTE srcType = new BYTE(); 
    public HEX srcId = new HEX(8); 
    public TIMESTAMP timeStamp = new TIMESTAMP(7); 
    public WORD cnt = new WORD();
    private ArrayList<SMIValue> smiValues = new ArrayList<SMIValue>();
    private final Integer serviceType = new Integer(2);

    /**
     * constructor
     */
    public EventData_1_2()
    {
    }

    /**
     * constructor
     *
     * @param oid <code>OID</code> oid
     */
    public EventData_1_2(OID oid)
    {
        this.oid = oid;
    }

    /**
     * constructor
     *
     * @param code <code>OID</code> event code
     * @param srcType <code>BYTE</code> source type
     * @param srcId <code>HEX</code> source id
     * @param timeStamp <code>TIMESTAMP</code> timestamp
     */
    public EventData_1_2(OID code, BYTE srcType, HEX srcId,
            TIMESTAMP timeStamp)
    {
        this.oid = code;
        this.srcType = srcType;
        this.srcId = srcId;
        this.timeStamp = timeStamp;
    }

    /**
     * get event code
     *
     * @return code <code>OID</code> event code
     */
    public OID getOid()
    {
        return this.oid;
    }
    /**
     * set event code
     *
     * @param code <code>OID</code> event code
     */
    public void setOid(OID oid)
    {
        this.oid = oid;
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

    public OCTET getNameSpace() {
		return this.nameSpace;
	}

	public void setNameSpace(OCTET nameSpace) {
		this.nameSpace = nameSpace;
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
            setCnt(new WORD(smiValues.size()));
            byte[] bx = null;
            bx = nameSpace.encode();
            bao.write(bx,0,bx.length);
            bx = oid.encode();
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
            log.error("EventData_1_2::encode failed :",ex);
            throw new FMPEncodeException(
                    "EventData_1_2::encode failed :"+ex.getMessage());
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
        sb.append("EventData_1_2 Service Header\n");
        sb.append("mcuId=").append(getMcuId()).append(',');
        sb.append("nameSpace=").append(nameSpace).append(',');
        sb.append("oid=").append(oid).append(',');
        sb.append("srcType=").append(srcType).append(',');
        sb.append("srcId=").append(srcId).append(',');
        sb.append("timeStamp=").append(timeStamp).append(',');
        sb.append("cnt=").append(cnt).append('\n');

        SMIValue[] smiValues = getSMIValue();
        sb.append("EventData_1_2 Service Data\n");
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
