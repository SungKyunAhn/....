package com.aimir.fep.protocol.fmp.datatype;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * represent VER Data Type
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ver", propOrder = {
    "value"
})
public class VER extends FMPVariable
{
    private String value = null;

    /**
     * constructor
     */
    public VER()
    {
    }

    /**
     * constructor
     *
     * @param value <code>String</code> value
     */
    public VER(String value)
    {
        this.value = value;
    }

    /**
     * constructor
     *
     * @param value <code>byte</code> value
     */
    public VER(byte value)
    {
        int pre = (value & 0xf0) >> 4;
        int post = (value & 0x0f);
        
        this.value = pre + "." + post;
    }

    /**
     * get value
     *
     * @result value <code>String</code>
     */
    public String getValue()
    {
        return this.value;
    }

    /**
     * set value
     *
     * @param value <code>String</code>
     */
    public void setValue(String value)
    {
        this.value = value;
    }

    /**
     * set value
     *
     * @param value <code>byte</code>
     */
    public void setValue(byte value)
    {
        int pre = (value & 0xf0) >> 4;
        int post = (value & 0x0f);
        
        this.value = pre + "." + post;
    }

    /**
     * encode VER Value
     *
     * @return value <code>byte[]</code> encoded byte array
     */
    public byte[] encode()
    {
        if(this.value == null)
            return new byte[0];
        int idx = this.value.indexOf(".");
        if(idx <= 0)
            return new byte[0];
        int pre = Integer.parseInt(this.value.substring(0,idx));
        int post = Integer.parseInt(this.value.substring(idx+1,
                    this.value.length()));
        byte[] bval = new byte[1];

        int f = (pre << 4) | post;

        bval[0] = (byte)(f & 0xff);

        return bval;
    }

    /**
     * decode VER Value
     *
     * @param buff <code>IoBuffer</code> input bytebuffer
     * @param size <code>int</code> Value length
     */
    public void decode(String ns, IoBuffer buff,int size)
    {
        setValue(buff.get());
    }

    public int decode(String ns, byte[] buff,int pos)
    {
        setValue(buff[pos]);
        return 1;
    }

    public int decode(String ns, byte[] buff,int pos,int size)
    {
        setValue(buff[pos]);
        return size;
    }

    /**
     * get syntax(data type)
     *
     * @return syntax <code>int</code> syntax
     */
    public int getSyntax()
    {
        return DataType.VER;
    }

    /**
     * get java syntax
     *
     *@returnsyntax<code>String</code>
     */
    public String getJavaSyntax()
    {
        return String.class.getName();
    }
    public String getMIBName() { return "verEntry"; }

    /**
     * get INT String Value
     *
     * @return value <code>String</code>
     */
    public String toString()
    {
        return this.value;
    }
}
