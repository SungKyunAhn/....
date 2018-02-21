package com.aimir.fep.protocol.fmp.datatype;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.mina.core.buffer.IoBuffer;

import com.aimir.fep.util.DataUtil;

/**
 * represent WORD Data Type
 *
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 * <pre>
 * &lt;complexType name="word">
 *   &lt;complexContent>
 *     &lt;extension base="{http://server.ws.command.fep.aimir.com/}fmpVariable">
 *       &lt;sequence>
 *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "word", propOrder = {
    "value"
})
public class WORD extends FMPVariable
{
    private int value = 0;

    /**
     * constructor
     */
    public WORD()
    {
    }

    /**
     * constructor
     *
     * @param value <code>byte[]</code> value
     */
    public WORD(byte[] value)
    {
        this.value = ((value[0] & 0xff) << 8)
            + ((value[1]) & 0xff);
    }

    /**
     * constructor
     *
     * @param value <code>int</code> value
     */
    public WORD(int value)
    {
        this.value = value;
    }

    /**
     * get int value
     *
     * @return result <code>int</code> value
     */
    public int getValue()
    {
        return this.value;
    }

    /**
     * set value
     *
     * @param value <code>int</code>
     */
    public void setValue(int value)
    {
        this.value = value;
    }

    /**
     * set value
     *
     * @param value <code>byte[]</code>
     */
    public void setValue(byte[] value)
    {
        this.value = ((value[0] & 0xff) << 8)
            + ((value[1]) & 0xff);
    }

    /**
     * encode WORD Value
     *
     * @return value <code>byte[]</code> encoded byte array
     */
    public byte[] encode()
    {
        byte[] bval = new byte[2];
        bval[0] = (byte)(this.value >> 8);
        bval[1] = (byte)(this.value);
        DataUtil.convertEndian(bval);

        return bval;
    }

    /**
     * decode WORD Value
     *
     * @param buff <code>IoBuffer</code> input bytebuffer
     * @param size <code>int</code> Value length
     */
    public void decode(String ns, IoBuffer buff,int size)
    {
        byte[] bx = new byte[2];
        buff.get(bx,0,bx.length);
        DataUtil.convertEndian(bx);
        setValue(bx);
    }

    public int decode(String ns, byte[] buff,int pos)
    {
        byte[] bx = new byte[2];
        System.arraycopy(buff,pos,bx,0,bx.length);
        DataUtil.convertEndian(bx);
        setValue(bx);
        return bx.length;
    }

    public int decode(String ns, byte[] buff,int pos,int size)
    {
        byte[] bx = new byte[2];
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
        return DataType.WORD;
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
    public String getMIBName() { return "wordEntry"; }

    /**
     * @param word
     * @return
     */
    public String decodeVersion(){
        byte[] b=encode();
        String version=b[1]+"."+b[0];
        return version;
    }
    /**
     * get WORD String Value
     *
     * @return value <code>String</code>
     */
    public String toString()
    {
        return Integer.toString(this.value);
    }
}
