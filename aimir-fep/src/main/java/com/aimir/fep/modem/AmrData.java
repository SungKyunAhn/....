package com.aimir.fep.modem;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for amrData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="amrData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="alarmFlag" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="alarmMask" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="consumptionLocation" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="customerName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fixedReset" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="lpChoice" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="meterSerialNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="meteringDay" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="meteringHour" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="noJoinCnt" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="permitMode" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="permitState" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="repeatingDay" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="repeatingHour" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="repeatingSetupSec" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="testFlag" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="vendor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "amrData", propOrder = {
    "alarmFlag",
    "alarmMask",
    "consumptionLocation",
    "customerName",
    "fixedReset",
    "lpChoice",
    "meterSerialNumber",
    "meteringDay",
    "meteringHour",
    "noJoinCnt",
    "permitMode",
    "permitState",
    "repeatingDay",
    "repeatingHour",
    "repeatingSetupSec",
    "testFlag",
    "vendor"
})
public class AmrData implements java.io.Serializable
{
	private static final long serialVersionUID = -2272385690296673311L;
	private String meterSerialNumber;
    private String vendor;
    private String customerName;
    private String consumptionLocation;
    private int fixedReset;
    private int testFlag;
    @XmlElement(nillable = true)
    protected List<Integer> meteringDay;
    @XmlElement(nillable = true)
    protected List<Integer> meteringHour;
    @XmlElement(nillable = true)
    protected List<Integer> repeatingDay;
    @XmlElement(nillable = true)
    protected List<Integer> repeatingHour;
    // private int[] meteringDay;
    // private int[] meteringHour;
    // private int[] repeatingDay;
    // private int[] repeatingHour;
    private int repeatingSetupSec;
    private int lpChoice;
    private int alarmFlag;
    private int noJoinCnt;
    private int permitMode;
    private int permitState;
    private int alarmMask;
    
    public AmrData() {
        meteringDay = new ArrayList<Integer>();
        meteringHour = new ArrayList<Integer>();
        repeatingDay = new ArrayList<Integer>();
        repeatingHour = new ArrayList<Integer>();
    }
    
    public String getMeterSerialNumber()
    {
        return meterSerialNumber;
    }
    public void setMeterSerialNumber(String meterSerialNumber)
    {
        this.meterSerialNumber = meterSerialNumber;
    }
    public String getVendor()
    {
        return vendor;
    }
    public void setVendor(String vendor)
    {
        this.vendor = vendor;
    }
    public String getCustomerName()
    {
        return customerName;
    }
    public void setCustomerName(String customerName)
    {
        this.customerName = customerName;
    }
    public String getConsumptionLocation()
    {
        return consumptionLocation;
    }
    public void setConsumptionLocation(String consumptionLocation)
    {
        this.consumptionLocation = consumptionLocation;
    }
    public int getFixedReset()
    {
        return fixedReset;
    }
    public void setFixedReset(int fixedReset)
    {
        this.fixedReset = fixedReset;
    }
    public int getTestFlag()
    {
        return testFlag;
    }
    public void setTestFlag(int testFlag)
    {
        this.testFlag = testFlag;
    }
    public int[] getMeteringDay()
    {
        int[] _meteringDay = new int[meteringDay.size()];
        for (int i = 0; i < _meteringDay.length; i++) {
            _meteringDay[i] = meteringDay.get(i);
        }
        return _meteringDay;
    }
    public void setMeteringDay(int[] meteringDay)
    {
        for (int i = 0; i < meteringDay.length; i++) {
            this.meteringDay.add(meteringDay[i]);
        }
    }
    public void setMeteringDay(List<Integer> meteringDay) {
        this.meteringDay = meteringDay;
    }
    
    public int[] getMeteringHour()
    {
        int[] _meteringHour = new int[meteringHour.size()];
        for (int i = 0; i < _meteringHour.length; i++) {
            _meteringHour[i] = meteringHour.get(i);
        }
        return _meteringHour;
    }
    public void setMeteringHour(int[] meteringHour)
    {
        for (int i = 0; i < meteringHour.length; i++) {
            this.meteringHour.add(meteringHour[i]);
        }
    }
    public void setMeteringHour(List<Integer> meteringHour) {
        this.meteringHour = meteringHour;
    }
    
    public int[] getRepeatingDay()
    {
        int[] _repeatingDay = new int[repeatingDay.size()];
        for (int i = 0; i < _repeatingDay.length; i++) {
            _repeatingDay[i] = repeatingDay.get(i);
        }
        return _repeatingDay;
    }
    
    public void setRepeatingDay(int[] repeatingDay)
    {
        for (int i = 0; i < repeatingDay.length; i++) {
            this.repeatingDay.add(repeatingDay[i]);
        }
    }
    public int[] getRepeatingHour()
    {
        int[] _repeatingHour = new int[repeatingHour.size()];
        for (int i = 0; i < _repeatingHour.length; i++) {
            _repeatingHour[i] = repeatingHour.get(i);
        }
        return _repeatingHour;
    }
    public void setRepeatingHour(int[] repeatingHour)
    {
        for (int i = 0; i < repeatingHour.length; i++) {
            this.repeatingHour.add(repeatingHour[i]);
        }
    }
    public void setRepeatingHour(List<Integer> repeatingHour) {
        this.repeatingHour = repeatingHour;
    }
    public int getRepeatingSetupSec()
    {
        return repeatingSetupSec;
    }
    public void setRepeatingSetupSec(int repeatingSetupSec)
    {
        this.repeatingSetupSec = repeatingSetupSec;
    }
    
    public int getLpChoice()
    {
        return lpChoice;
    }
    public void setLpChoice(int lpChoice)
    {
        this.lpChoice = lpChoice;
    }
    
    public int getAlarmFlag()
    {
        return alarmFlag;
    }
    public void setAlarmFlag(int alarmFlag)
    {
        this.alarmFlag = alarmFlag;
    }
    public int getNoJoinCnt()
    {
        return noJoinCnt;
    }
    public void setNoJoinCnt(int noJoinCnt)
    {
        this.noJoinCnt = noJoinCnt;
    }
    public int getPermitMode()
    {
        return permitMode;
    }
    public void setPermitMode(int permitMode)
    {
        this.permitMode = permitMode;
    }
    public int getPermitState()
    {
        return permitState;
    }
    public void setPermitState(int permitState)
    {
        this.permitState = permitState;
    }
    public int getAlarmMask()
    {
        return alarmMask;
    }
    public void setAlarmMask(int alarmMask)
    {
        this.alarmMask = alarmMask;
    }
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("METER_SERIAL_NUMBER[" + meterSerialNumber + "]"
                   + ", VENDOR[" + vendor + "]"
                   + ", CUSTOMER_NAME[" + customerName + "]"
                   + ", CONSUMPTION_LOCATION[" + consumptionLocation + "]"
                   + ", FIXED_RESET[" + fixedReset + "]"
                   + ", TEST_FLAG[" + testFlag + "]"
                   + ", METERING_DAY[");
        
        for (int i = 0; i < meteringDay.size(); i++) {
            if (i != 0)
                buf.append(',');
            buf.append(meteringDay.get(i));
        }
        buf.append("]"
                   + ", METERING_HOUR[");
        for (int i = 0; i < meteringHour.size(); i++) {
            if (i != 0)
                buf.append(',');
            buf.append(meteringHour.get(i));
        }
        buf.append("]"
                   + ", REPEATING_DAY[");
        for (int i = 0; i < repeatingDay.size(); i++) {
            if (i != 0)
                buf.append(',');
            buf.append(repeatingDay.get(i));
        }
        buf.append("]"
                   + ", REPEATING_HOUR[");
        for (int i = 0; i < repeatingHour.size(); i++) {
            if (i != 0)
                buf.append(',');
            buf.append(repeatingHour.get(i));
        }
        buf.append("]"
                   + ", REPEATING_SETUP_SEC[" + repeatingSetupSec + "]"
                   + "] LP_CHOICE[" + lpChoice + "]"
                   + " ALARMFLAG[" + alarmFlag + "]"
                   + " NOJOINCNT[" + noJoinCnt + "]"
                   + " PERMITMODE[" + permitMode + "]"
                   + " PERMITSTATE[" + permitState + "]"
                   + " ALARMMASK[" + alarmMask + "]");
        
        return buf.toString();
    }
}
