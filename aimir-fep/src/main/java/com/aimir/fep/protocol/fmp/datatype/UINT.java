package com.aimir.fep.protocol.fmp.datatype;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.mina.core.buffer.IoBuffer;

import com.aimir.fep.util.DataUtil;

/**
 * represent UINT Data Type
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 * <pre>
 * &lt;complexType name="uint">
 *   &lt;complexContent>
 *     &lt;extension base="{http://server.ws.command.fep.aimir.com/}fmpVariable">
 *       &lt;sequence>
 *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "uint", propOrder = {
    "value"
})
public class UINT extends FMPVariable
{
    private long value = 0;

    /**
     * constructor
     */
    public UINT()
    {
    }

    /**
     * constructor
     *
     * @param value <code>long</code> value
     */
    public UINT(long value)
    {
        this.value = value;
    }

    /**
     * constructor
     *
     * @param value <code>byte[]</code> value
     */
    public UINT(byte[] value)
    {
        this.value = 0;
        this.value |= (value[0] & 0xff);
        this.value <<= 8;
        this.value |= (value[1] & 0xff);
        this.value <<= 8;
        this.value |= (value[2] & 0xff);
        this.value <<= 8;
        this.value |= (value[3] & 0xff);
    }

    /**
     * get value
     *
     * @return result <code>long</code> value
     */
    public long getValue()
    {
        return this.value; 
    }

    /**
     * set value
     *
     * @param value <code>long</code> value
     */
    public void setValue(long value)
    {
        this.value = value;
    }

    /**
     * set value
     *
     * @param value <code>byte[]</code> value
     */
    public void setValue(byte[] value)
    {
        this.value = 0;
        this.value |= (value[0] & 0xff);
        this.value <<= 8;
        this.value |= (value[1] & 0xff);
        this.value <<= 8;
        this.value |= (value[2] & 0xff);
        this.value <<= 8;
        this.value |= (value[3] & 0xff);
    }

    /**
     * encode UINT Value
     *
     * @return value <code>byte[]</code> encoded byte array
     */
    public byte[] encode()
    {
        byte[] bval = new byte[4];
        bval[0] = (byte)(value >> 24);
        bval[1] = (byte)(value >> 16);
        bval[2] = (byte)(value >> 8);
        bval[3] = (byte)value;

        DataUtil.convertEndian(bval);

        return bval;
    }

    /**
     * decode UINT Value
     *
     * @param buff <code>IoBuffer</code> input bytebuffer
     * @param size <code>int</code> Value length
     */
    public void decode(String ns, IoBuffer buff,int size)
    {
        byte[] bx = new byte[4];
        buff.get(bx,0,bx.length);
        DataUtil.convertEndian(bx);
        setValue(bx);
    }

    public int decode(String ns, byte[] buff,int pos)
    {
        byte[] bx = new byte[4];
        System.arraycopy(buff,pos,bx,0,bx.length);
        DataUtil.convertEndian(bx);
        setValue(bx);
        return bx.length;
    }

    public int decode(String ns, byte[] buff,int pos,int size)
    {
        byte[] bx = new byte[4];
        System.arraycopy(buff,pos,bx,0,bx.length);
        DataUtil.convertEndian(bx);
        setValue(bx);
        return size;
    }

    /**
     * get syntax(data type)
     *
     * @return syntax <code>int</code> syntax
     */
    public int getSyntax()
    {
        return DataType.UINT;
    }

    /**
     * get java syntax
     *
     *@returnsyntax<code>String</code>
     */
    public String getJavaSyntax()
    {
        return Long.class.getName();
    }
    public String getMIBName() { return "uintEntry"; }

    /**
     * get UINT String Value
     *
     * @return value <code>String</code>
     */
    public String toString()
    {
        return Long.toString(this.value);
    }
}
