package com.aimir.fep.modem;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <pre>
 * &lt;complexType name="lpData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="basePulse" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="lp" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="lpDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="period" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="pointer" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "lpData", propOrder = {
    "basePulse",
    "lp",
    "lpDate",
    "period",
    "pointer"
})
public class LPData implements java.io.Serializable
{
	private static final long serialVersionUID = 8550719746471826749L;
	private String lpDate = null;
	@XmlElement(nillable = true)
    protected List<Integer> lp;
    private long basePulse = -1;
    private int pointer = 0;
    private int period = 0;

    public LPData() {
        lp = new ArrayList<Integer>();
    }
    
    public String getLpDate()
    {
        return lpDate;
    }
    public void setLpDate(String lpDate)
    {
        this.lpDate = lpDate;
    }
    public long getBasePulse()
    {
        return basePulse;
    }
    public void setBasePulse(long basePulse)
    {
        this.basePulse = basePulse;
    }

    public int getPointer()
    {
        return pointer;
    }

    public void setPointer(int pointer)
    {
        this.pointer = pointer;
    }

    public int getPeriod()
    {
        return period;
    }

    public void setPeriod(int period)
    {
        this.period = period;
    }
    public int[] getLp()
    {
        int[] _lp = new int[lp.size()];
        for (int i=0 ; i < _lp.length; i++) {
            _lp[i] = lp.get(i);
        }
        return _lp;
    }
    public void setLp(int[] lp)
    {
        for (int i = 0; i < lp.length; i++) {
            this.lp.add(lp[i]);
        }
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        sb.append("LPDATA[");
        sb.append("(lpDate=").append(lpDate).append("),");
        for(int i=0;i<getLp().length;i++){
        sb.append("(lp["+i+"]=").append(getLp()[i]).append("),");
        }
        sb.append("(basePulse=").append(basePulse).append("),");
        sb.append("(pointer=").append(pointer).append("),");
        sb.append("(period=").append(period).append(')');
        sb.append("]\n");

        return sb.toString();
    }
}
