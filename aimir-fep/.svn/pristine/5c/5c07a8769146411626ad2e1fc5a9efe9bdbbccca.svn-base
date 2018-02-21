package com.aimir.fep.protocol.fmp.datatype;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.mina.core.buffer.IoBuffer;


/**
 * represent BYTE Data Type
 * 
 * <pre>
 * &lt;complexType name="byte">
 *   &lt;complexContent>
 *     &lt;extension base="{http://mbean.command.fep.aimir.com/}fmpVariable">
 *       &lt;sequence>
 *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "byte", propOrder = {
    "value"
})
public class BYTE extends FMPVariable
{
    private int value;

    /**
     * constructor
     */
    public BYTE()
    {
    }
    
    /**
     * constructor
     *
     * @param value <code>int</code> byte int value
     */
    public BYTE(int value)
    {
        this.value = value;
    }

    /**
     * constructor
     *
     * @param value <code>byte</code> byte value
     */
    public BYTE(byte value)
    {
        this.value = (value & 0xff);
    }

    /**
     * get int value
     *
     * @return result <code>int</code>
     */
    public int getValue()
    {
        return value;
    }

    /**
     * set byte value
     *
     * @param value <code>int</code>
     */
    public void setValue(int value)
    {
        this.value = value;
    }
    /**
     * set byte value
     *
     * @param value <code>byte</code>
     */
    public void setValue(byte value)
    {
        this.value = (value & 0xff);
    }

    /**
     * encode BYTE Value
     *
     * @return value <code>byte[]</code> encoded byte array
     */
    public byte[] encode()
    {
        byte[] bval = new byte[1];
        bval[0] = (byte)this.value;
        return bval;
    }

    /**
     * decode BYTE Value
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
        return DataType.BYTE;
    }

    /**
     * get java syntax
     *
     *@returnsyntax<code>String</code>
     */
    public String getJavaSyntax()
    {
        return Integer.class.getName();
    }

    public String getMIBName() { return "byteEntry"; }

    /**
     * get BYTE String Value
     *
     * @return value <code>String</code>
     */
    public String toString()
    {
        return Integer.toString(this.value);
    }
}
