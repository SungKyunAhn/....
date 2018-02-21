package com.aimir.fep.protocol.fmp.frame.service;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import com.aimir.fep.meter.data.Response;
import com.aimir.fep.protocol.fmp.datatype.BYTE;
import com.aimir.fep.protocol.fmp.datatype.OID;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.datatype.WORD;
import com.aimir.fep.protocol.fmp.exception.FMPEncodeException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

/**
 * Command Service Data Class
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */

public class CommandData extends ServiceData
{
    public OID cmd = new OID();
    public BYTE tid = new BYTE();
    public BYTE attr = new BYTE((byte)0x80);
    public BYTE errCode = new BYTE(0);
    public WORD cnt = new WORD(0);
    private ArrayList<SMIValue> smiValues = new ArrayList<SMIValue>();
    private ArrayList objValues = new ArrayList();
    private Response response;
    
    private static Log log = LogFactory.getLog(CommandData.class);
    private final Integer serviceType = new Integer(1);

    /**
     * constructor
     */
    public CommandData()
    {
    }
    
    /**
     * constructor for attr
     */
    public CommandData(byte b) {
    	attr = new BYTE(b);
    }

    /**
     * constructor
     *
     * @param cmd <code>OID</code> command code
     */
    public CommandData(OID cmd)
    {
        this.cmd = cmd;
    }

    /**
     * get command code
     *
     * @return cmd <code>OID</code> command code
     */
    public OID getCmd()
    {
        return this.cmd;
    }
    /**
     * set command code
     *
     * @param cmd <code>OID</code> command code
     */
    public void setCmd(OID cmd)
    {
        this.cmd = cmd;
    }

    /**
     * get target id 
     *
     * @return tid <code>BYTE</code> target id 
     */
    public BYTE getTid()
    {
        return this.tid;
    }
    /**
     * set target id 
     *
     * @param tid <code>BYTE</code> target id 
     */
    public void setTid(BYTE tid)
    {
        this.tid = tid;
    }

    /**
     * get command attribute
     *
     * @return attr <code>BYTE</code> command attribute
     */
    public BYTE getAttr()
    {
        return this.attr;
    }
    /**
     * set command attribute
     *
     * @param attr <code>BYTE</code> command attribute
     */
    public void setAttr(BYTE attr)
    {
        this.attr = attr;
    }

    /**
     * get error code
     *
     * @return errCode <code>BYTE</code> errCode
     */
    public BYTE getErrCode()
    {
        return this.errCode;
    }
    /**
     * set error code
     *
     * @param errCode <code>BYTE</code> errCode
     */
    public void setErrCode(BYTE errCode)
    {
        this.errCode = errCode;
    }

    /**
     * get smivalaue count
     *
     * @return cnt <code>BYTE</code> smivalue count
     */
    public WORD getCnt()
    {
        return this.cnt;
    }
    /**
     * set smivalaue count
     *
     * @param cnt <code>BYTE</code> smivalue count
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
        cnt = new WORD(cnt.getValue() + 1);
    }
    
    public void append(Object objValue)
    {
        objValues.add(objValue);
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

    public Object[] getObjValue(){
    	return (Object[])objValues.toArray(new Object[0]);
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
            // setCnt(new WORD(smiValues.size()));
            byte[] bx = null;

            bx = cmd.encode();
            bao.write(bx,0,bx.length);
            bx = tid.encode();
            bao.write(bx[0]);
            bx = attr.encode();
            bao.write(bx[0]);
            bx = errCode.encode();
            bao.write(bx[0]);
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
            log.error("CommandData::encode failed :",ex);
            throw new FMPEncodeException(
                    "CommandData::encode failed :"+ex.getMessage());
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
        sb.append("Command Service Header\n");
        sb.append("mcuId=").append(getMcuId()).append(',');
        sb.append("cmd=").append(cmd).append(',');
        sb.append("tid=").append(tid).append(',');
        sb.append("attr=").append(attr).append(',');
        sb.append("errCode=").append(errCode).append(',');
        sb.append("cnt=").append(cnt).append('\n');

        SMIValue[] smiValues = getSMIValue();
        sb.append("Command Service Data\n");
        for(int i = 0 ; i < smiValues.length ; i++)
        {
            sb.append("SMIValue[").append(i).append("]=");
            sb.append(smiValues[i].toString()).append("\n");
        }

        return sb.toString();
    }
    
    /**
     * remove smivalues
     */
    public void removeSmiValues() {
    	this.smiValues = new ArrayList();
    }
    
    @Test
    public static void test() {
    	CommandData cd = new CommandData();
    	System.out.println(cd.getType());
    }

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}
    
}
