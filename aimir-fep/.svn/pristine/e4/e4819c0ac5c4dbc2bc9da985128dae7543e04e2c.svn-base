package com.aimir.fep.modem;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 * <pre>
 * &lt;complexType name="batteryLog">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="batteryPointer" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="batteryVolt" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="consumptionCurrent" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="offset" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "batteryLog", propOrder = {
    "batteryPointer",
    "batteryVolt",
    "consumptionCurrent",
    "offset"
})
public class BatteryLog implements java.io.Serializable
{
	private static final long serialVersionUID = 5796307398047418219L;
	private int batteryPointer;
	private int[] batteryVolt;
	private int[] consumptionCurrent;
	private int[] offset;
	@XmlTransient
    private List<Integer> _batteryVolt;
	@XmlTransient
    private List<Integer> _consumptionCurrent;
	@XmlTransient
    private List<Integer> _offset;
    
    public BatteryLog() {
        _batteryVolt = new ArrayList<Integer>();
        _consumptionCurrent = new ArrayList<Integer>();
        _offset = new ArrayList<Integer>();
    }
    
    public int getBatteryPointer()
    {
        return batteryPointer;
    }
    
    public void setBatteryPointer(int batteryPointer)
    {
        this.batteryPointer = batteryPointer;
    }
    
    public int[] getBatteryVolt()
    {
        batteryVolt = new int[_batteryVolt.size()];
        for (int i = 0; i < batteryVolt.length; i++)
            batteryVolt[i] = (Integer)_batteryVolt.get(i);
        return batteryVolt;
    }
    
    public void setBatteryVolt(int[] batteryVolt) {
        for (int i : batteryVolt) {
            _batteryVolt.add(i);
        }
    }
    
    public void setBatteryVolt(List<Integer> _batteryVolt) {
        this._batteryVolt = _batteryVolt;
    }
    
    public void addBatteryVolt(int batteryVolt)
    {
        this._batteryVolt.add(batteryVolt);
    }
    
    public int[] getConsumptionCurrent()
    {
        consumptionCurrent = new int[_consumptionCurrent.size()];
        for (int i = 0; i < consumptionCurrent.length; i++)
            consumptionCurrent[i] = (Integer)_consumptionCurrent.get(i);
        return consumptionCurrent;
    }
    
    public void setConsumptionCurrent(int[] consumptionCurrent) {
        for (int i : consumptionCurrent) {
            _consumptionCurrent.add(i);
        }
    }
    
    public void setConsumptionCurrent(List<Integer> _consumptionCurrent) {
        this._consumptionCurrent = _consumptionCurrent;
    }
    
    public void addConsumptionCurrent(int consumptionCurrent)
    {
        this._consumptionCurrent.add(consumptionCurrent);
    }
    
    public int[] getOffset()
    {
        offset = new int[_offset.size()];
        for (int i = 0; i < offset.length; i++)
            offset[i] = (Integer)_offset.get(i);
        return offset;
    }
    
    public void setOffset(int[] offset) {
        for (int i : offset) {
            _offset.add(i);
        }
    }
    
    public void setOffset(List<Integer> _offset) {
        this._offset = _offset;
    }
    
    public void addOffset(int offset)
    {
        this._offset.add(offset);
    }
    
    public String toString() {
        StringBuffer buf = new StringBuffer();
        
        buf.append("BATTERY_POINTER[" + getBatteryPointer() + "]"
                   + ", BATTERY_VOLT[ " + getBatteryVolt() + "]"
                   + ", CONSUMPTION_CURRENT[" + getConsumptionCurrent()+ "]"
                   + ", OFFSET[" + getOffset() + "]");
        
        return buf.toString();
    }
}
