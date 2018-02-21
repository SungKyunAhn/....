package com.aimir.fep.protocol.fmp.datatype;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * represent BOOL Data Type
 * 
 * <pre>
 * &lt;complexType name="bool">
 *   &lt;complexContent>
 *     &lt;extension base="{http://mbean.command.fep.aimir.com/}fmpVariable">
 *       &lt;sequence>
 *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
@XmlType(name = "bool", propOrder = {
    "value"
})
public class BOOL extends FMPVariable
{
    private boolean value = false;

    /**
     * constructor
     */
    public BOOL()
    {
    }

    /**
     * constructor
     *
     * @param value <code>boolean</code> boolean value
     */
    public BOOL(boolean value)
    {
        this.value = value;
    }

    /**
     * constructor
     *
     * @param value <code>String</code> boolean value
     */
    public BOOL(String value)
    {
        this.value = Boolean.valueOf(value).booleanValue();
    }

    /**
     * constructor
     *
     * @param value <code>byte</code> boolean value
     */
    public BOOL(byte value)
    {
        if(value == (byte)0x00)
            this.value = false;
        else
            this.value = true;
    }

    /**
     * get boolean value
     *
     * @return result <code>boolean</code>
     */
    public boolean getValue()
    {
        return this.value;
    }

    /**
     * set boolean value
     *
     * @param value <code>boolean</code>
     */
    public void setValue(boolean value)
    {
        this.value = value;
    }

    /**
     * set boolean value
     *
     * @param value <code>byte</code>
     */
    public void setValue(byte value)
    {
        if(value == (byte)0x00)
            this.value = false;
        else
            this.value = true;
    }

    /**
     * encode BOOL Value
     *
     * @return value <code>byte[]</code> encoded byte array
     */
    public byte[] encode()
    {
        byte[] bval = new byte[1];
        if(value)
            bval[0] = (byte)0x01;
        else
            bval[0] = (byte)0x00;

        return bval;
    }

    /**
     * decode BOOL Value
     *
     * @param buff <code>IoBuffer</code> input bytebuffer
     * @param size <code>int</code> BCD Value length
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
        return DataType.BOOL;
    }

    /**
     * get java syntax
     *
     *@returnsyntax<code>String</code>
     */
    public String getJavaSyntax()
    {
        return Boolean.class.getName();
    }

    public String getMIBName() { return "boolEntry"; }

    /**
     * get BCD String Value
     *
     * @return value <code>String</code>
     */
    public String toString()
    {
        return Boolean.toString(this.value);
    }
}
