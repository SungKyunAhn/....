package com.aimir.fep.protocol.fmp.datatype;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * represent Non fixed Data Type
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,

* <pre>
* &lt;complexType name="fmpNonFixedVariable">
*   &lt;complexContent>
*     &lt;extension base="{http://server.ws.command.fep.aimir.com/}fmpVariable">
*       &lt;sequence>
*         &lt;element name="isFixed" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
*         &lt;element name="len" type="{http://www.w3.org/2001/XMLSchema}int"/>
*       &lt;/sequence>
*     &lt;/extension>
*   &lt;/complexContent>
* &lt;/complexType>
* </pre>
* 
* 
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "fmpNonFixedVariable", propOrder = {
   "isFixed",
   "len",
   "value"
})
@XmlSeeAlso({
   HEX.class,
   OCTET.class,
   TIMESTAMP.class
})
public abstract class FMPNonFixedVariable extends FMPVariable
{
    protected byte[] value = null; 
    protected int len = 0;
    protected boolean isFixed = false;


    /**
     * get value length
     *
     * @return length <code>int</code>
     */
    public final int getLen()
    {
        return this.len;
    }
    /**
     * set value length
     *
     * @param length <code>int</code>
     */
    public final void setLen(int len)
    {
        this.value = new byte[len];
        this.len = len;
    }

    /**
     * check whether variable is fixed or not fixed
     *
     * @return result <code>boolean</code>
     */
    public boolean getIsFixed()
    {
        return this.isFixed;
    }
    /**
     * set fixed variable
     *
     * @param isFixed <code>boolean</code>
     */
    public void setIsFixed(boolean isFixed)
    {
        this.isFixed = isFixed;
    }

    /**
     * encode
     *
     * @param isCompact <code>boolean</code>
     */
    public abstract byte[] encode(boolean isCompact);
    /**
     * decode
     *
     * @param buff <code>IoBuffer</code>
     */
    public abstract void decode(String ns, IoBuffer buff);
}
