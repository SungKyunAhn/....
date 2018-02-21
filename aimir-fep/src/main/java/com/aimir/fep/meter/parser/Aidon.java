package com.aimir.fep.meter.parser;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.LinkedHashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.Instrument;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.Meter;
import com.aimir.util.TimeLocaleUtil;

/**
 * parsing Aidon Meter Data
 * implemented in Norway HTS Office
 *
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2006-09-11 12:32:15 +0900 $,
 */
public class Aidon extends MeterDataParser implements java.io.Serializable 
{
	private static final long serialVersionUID = 1799286698590569483L;

	private static Log _log = LogFactory.getLog(Aidon.class);
    
    private byte[] rawData = null;
    private Double meteringValue = null;
    private String meterId = null;
    private int flag = 0;
    private boolean meterStatus = true;

    private Hashtable<String, String> oidValues =  new Hashtable<String, String>();
    private double activePositive = -1; //1.8.0
    private double activeNegative = -1; //2.8.0
    private double reactivePositive = -1; //3.8.0
    private double reactiveNegative = -1; //4.8.0
    private double vaphase1 = -1; //32.7.0
    private double vbphase2 = -1; //52.7.0
    private double vcphase3 = -1; //72.7.0
    private double iaphase1 = -1; //31.7.0
    private double ibphase2 = -1; //51.7.0
    private double icphase3 = -1; //71.7.0
    private double vaaquality = -1; // 32.24.0
    private double vbaquality = -1; // 52.24.0
    private double vcaquality = -1; // 72.24.0
    private double hoursInUse = -1; //96.8.0
    private String customSerial = null; //0.0.0
    private String meterSerial = null;  //0.0.1
    private String meterLog = null;  //97.97.1
    private String manufactureDate = null; // 96.1.3
    private String activeTariff = null; // 96.89.3

    private static final String MSTATUSXX7="<dt><font color=blue>Communication activity after power down</font></dt>";
    private static final String MSTATUSXX6="<dt><font color=red>L1 missing</font></dt>";
    private static final String MSTATUSXX5="<dt><font color=red>L2 missing</font></dt>";
    private static final String MSTATUSXX4="<dt><font color=red>L3 missing</font></dt>";
    private static final String MSTATUSXX3="<dt>Measuring to QIV</dt>";
    private static final String MSTATUSXX2="<dt>Measuring to QIII</dt>";
    private static final String MSTATUSXX1="<dt>Measuring to QII</dt>";
    private static final String MSTATUSXX0="<dt>Measuring to QI</dt>";

    private static final String MSTATUSYY7="<dt>Block 7 CRC error</dt>";
    private static final String MSTATUSYY6="<dt>Block 6 CRC error</dt>";
    private static final String MSTATUSYY5="<dt>Block 5 CRC error</dt>";
    private static final String MSTATUSYY4="<dt>Block 4 CRC error</dt>";
    private static final String MSTATUSYY3="<dt>Block 3 CRC error</dt>";
    private static final String MSTATUSYY2="<dt>Block 2 CRC error</dt>";
    private static final String MSTATUSYY1="<dt>Block 1 CRC error</dt>";
    private static final String MSTATUSYY0="<dt>Reserved</dt>";

    private static final String MSTATUSZZ7="<dt>Reserved</dt>";
    private static final String MSTATUSZZ6="<dt>Reserved</dt>";
    private static final String MSTATUSZZ5="<dt>Import/export energy at the same time</dt>";
    private static final String MSTATUSZZ4="<dt><font color=red>Reversed phase sequency</font></dt>";
    private static final String MSTATUSZZ3="<dt><font color=red>Storage data corrupted</font></dt>";
    private static final String MSTATUSZZ2="<dt>ADI fault</dt>";
    private static final String MSTATUSZZ1="<dt>EEPROM fault</dt>";
    private static final String MSTATUSZZ0="<dt>Watchdog Activated</dt>";
    
    private List<EventLogData> meterEventStatus;
    private String datetime;
    private Meter meter;

    private String getMeteringStatus(byte[] data)
    {
        StringBuffer status = new StringBuffer();
        meterEventStatus = new ArrayList<EventLogData>();
        EventLogData eventlog = new EventLogData();

        if(data.length != 3)
            return status.toString();

        if(((data[0] & 0x80) >> 7) == 1){
            status.append(MSTATUSXX7);
            makeEventObject(datetime, "STS", 17);
        }
        if(((data[0] & 0x40) >> 6) == 1) {
            status.append(MSTATUSXX6);
            meterStatus = false;
            makeEventObject(datetime, "STS", 16);
        }
        if(((data[0] & 0x20) >> 5) == 1) {
            status.append(MSTATUSXX5);
            meterStatus = false;
            makeEventObject(datetime, "STS", 15);
        }
        if(((data[0] & 0x10) >> 4) == 1) {
            status.append(MSTATUSXX4);
            meterStatus = false;
            makeEventObject(datetime, "STS", 14);
        }
        if(((data[0] & 0x08) >> 3) == 1){
            status.append(MSTATUSXX3);
            makeEventObject(datetime, "STS", 13);
        }
        if(((data[0] & 0x04) >> 2) == 1){
            status.append(MSTATUSXX2);
            makeEventObject(datetime, "STS", 12);
        }
        if(((data[0] & 0x02) >> 1) == 1){
            status.append(MSTATUSXX1);
            makeEventObject(datetime, "STS", 11);
        }
        if(((data[0] & 0x01)) == 1){
            status.append(MSTATUSXX0);
            makeEventObject(datetime, "STS", 10);
        }

        if(((data[1] & 0x80) >> 7) == 1) {
            status.append(MSTATUSYY7);
            meterStatus = false;
            makeEventObject(datetime, "STS", 27);
        }
        if(((data[1] & 0x40) >> 6) == 1) {
            status.append(MSTATUSYY6);
            meterStatus = false;
            makeEventObject(datetime, "STS", 26);
        }
        if(((data[1] & 0x20) >> 5) == 1) {
            status.append(MSTATUSYY5);
            meterStatus = false;
            makeEventObject(datetime, "STS", 25);
        }
        if(((data[1] & 0x10) >> 4) == 1) {
            status.append(MSTATUSYY4);
            meterStatus = false;
            makeEventObject(datetime, "STS", 24);
        }
        if(((data[1] & 0x08) >> 3) == 1) {
            status.append(MSTATUSYY3);
            meterStatus = false;
            makeEventObject(datetime, "STS", 23);
        }
        if(((data[1] & 0x04) >> 2) == 1) {
            status.append(MSTATUSYY2);
            meterStatus = false;
            makeEventObject(datetime, "STS", 22);
        }
        if(((data[1] & 0x02) >> 1) == 1) {
            status.append(MSTATUSYY1);
            meterStatus = false;
            makeEventObject(datetime, "STS", 21);
        }
        if(((data[1] & 0x01)) == 1){
            status.append(MSTATUSYY0);
            makeEventObject(datetime, "STS", 20);
        }

        if(((data[2] & 0x80) >> 7) == 1){
            status.append(MSTATUSZZ7);
            makeEventObject(datetime, "STS", 37);
        }
        if(((data[2] & 0x40) >> 6) == 1){
            status.append(MSTATUSZZ6);
            makeEventObject(datetime, "STS", 36);
        }
        if(((data[2] & 0x20) >> 5) == 1){
            status.append(MSTATUSZZ5);
            makeEventObject(datetime, "STS", 35);
        }
        if(((data[2] & 0x10) >> 4) == 1){
            status.append(MSTATUSZZ4);
            meterStatus = false;
            makeEventObject(datetime, "STS", 34);
        }
        if(((data[2] & 0x08) >> 3) == 1) {
            status.append(MSTATUSZZ3);
            meterStatus = false;
            makeEventObject(datetime, "STS", 33);
        }
        if(((data[2] & 0x04) >> 2) == 1) {
            status.append(MSTATUSZZ2);
            meterStatus = false;
            makeEventObject(datetime, "STS", 32);
        }
        if(((data[2] & 0x02) >> 1) == 1) {
            status.append(MSTATUSZZ1);
            meterStatus = false;
            makeEventObject(datetime, "STS", 31);
        }
        if(((data[2] & 0x01)) == 1){
            status.append(MSTATUSZZ0);
            makeEventObject(datetime, "STS", 30);
        }

        return status.toString();
    }

    /**
     * constructor
     */
    public Aidon()
    {
    }

    /**
     * get LP
     */
    public Double getMeteringValue()
    {
        return this.meteringValue;
    }

    /**
     * get meter id
     * @return meter id
     */
    public String getMeterId()
    {
        return meterId;
    }

    /**
     * get raw Data
     */
    public byte[] getRawData()
    {
        return this.rawData;
    }

    public int getLength()
    {
        return this.rawData.length;
    }

    public boolean getMeterStatus() {
        return meterStatus;
    }
    
    /**
     * parseing Energy Meter Data of Aidon Meter
     * @param data stream of result command
     */
    public void parse(byte[] data) throws Exception
    {
        this.rawData = data;

        String dataStr = new String(data);
        _log.debug("data["+dataStr+"]");
        makeOidValues(dataStr);

        String val;

        // Active cumulative positive energy
        val = (String)oidValues.get("1.8.0");
        if(val != null)
        {
            val = val.substring(0,val.indexOf("*"));
            activePositive = Double.parseDouble(val);
            this.meteringValue = new Double(activePositive);
        }

        // Active cumulative negative energy
        val = (String)oidValues.get("2.8.0");
        if(val != null)
        {
        	// exception
        	// by js. 2008.08.28
        	int idx = val.indexOf("*");
        	if (idx != -1)
        		val = val.substring(0,idx);
        	else
        		val = val.substring(0, val.length()-1);
        	
            activeNegative = Double.parseDouble(val);
        }

        // Reactive cumulative positive energy
        val = (String)oidValues.get("3.8.0");
        if(val != null)
        {
            val = val.substring(0,val.indexOf("*"));
            reactivePositive = Double.parseDouble(val);
        }

        // Reactive cumulative negative energy
        val = (String)oidValues.get("4.8.0");
        if(val != null)
        {
            val = val.substring(0,val.indexOf("*"));
            reactiveNegative = Double.parseDouble(val);
        }
       
        // Va instant voltage,phase1 
        val = (String)oidValues.get("32.7.0");
        if(val != null)
        {
            val = val.substring(0,val.indexOf("*"));
            vaphase1 = Double.parseDouble(val);
        }

        // Vb instant voltage,phase1 
        val = (String)oidValues.get("52.7.0");
        if(val != null)
        {
            val = val.substring(0,val.indexOf("*"));
            vbphase2 = Double.parseDouble(val);
        }

        // Vc instant voltage,phase1 
        val = (String)oidValues.get("72.7.0");
        if(val != null)
        {
            val = val.substring(0,val.indexOf("*"));
            vcphase3 = Double.parseDouble(val);
        }

        // Va average voltage over 10 minutes
        val = (String)oidValues.get("32.24.0");
        if (val != null)
        {
            val = val.substring(0, val.indexOf("*"));
            vaaquality = Double.parseDouble(val);
        }

        // Vb average voltage over 10 minutes
        val = (String)oidValues.get("52.24.0");
        if (val != null)
        {
            val = val.substring(0, val.indexOf("*"));
            vbaquality = Double.parseDouble(val);
        }

        // Vc average voltage over 10 minutes
        val = (String)oidValues.get("72.24.0");
        if (val != null)
        {
            val = val.substring(0, val.indexOf("*"));
            vcaquality = Double.parseDouble(val);
        }
        
        // Ia instant voltage,phase1 
        val = (String)oidValues.get("31.7.0");
        if(val != null)
        {
            val = val.substring(0,val.indexOf("*"));
            iaphase1 = Double.parseDouble(val);
        }

        // Ib instant voltage,phase1 
        val = (String)oidValues.get("51.7.0");
        if(val != null)
        {
            if(val.indexOf("*") > 0)
                val = val.substring(0,val.indexOf("*"));
            else
                val = "0";
            ibphase2 = Double.parseDouble(val);
        }

        // Ic instant voltage,phase1 
        val = (String)oidValues.get("71.7.0");
        if(val != null)
        {
            val = val.substring(0,val.indexOf("*"));
            icphase3 = Double.parseDouble(val);
        }

        // Hours In Use
        val = (String)oidValues.get("96.8.0");
        if(val != null)
        {
            val = val.substring(0,val.indexOf("*"));
            hoursInUse = Double.parseDouble(val);
        }

        // Custmer Serial Number
        val = (String)oidValues.get("0.0.0");
        _log.debug("val=["+val+"]");
        if(val != null)
        {
            customSerial = val;
        }

        // Manufacturer Serial Number
        val = (String)oidValues.get("0.0.1");
        if(val != null)
        {
            meterSerial = val;
        }

        // Manufacturer date
        val = (String)oidValues.get("96.1.3");
        if(val != null)
        {
            manufactureDate = val;
        }
        
        // Active tariff
        val = (String)oidValues.get("96.89.3");
        if(val != null)
        {
            activeTariff = val;
        }
        
        // Meter Status
        val = (String)oidValues.get("97.97.1");
        if(val != null)
        {
            meterLog = val;
        }

        _log.debug("Aidon Data Parse Finished :: DATA["+toString()+"]");
    }

    private void makeOidValues(String data) throws Exception
    {
        String[] lines = data.split("\n");

        String[] items;
        int idx;
        String oid;
        String value;
        for(int i = 0 ; i < lines.length ; i++)
        {
            idx = lines[i].indexOf("(");
            if(idx < 1)
                continue;
            oid = lines[i].substring(0,idx);
            value = lines[i].substring(idx+1);
            idx = value.indexOf(")");
            if(idx < 1)
                continue;
            value = value.substring(0,idx);

            oid = oid.trim();
            _log.debug("oid["+oid+"] value["+value+"]");
            oidValues.put(oid,value);
        }
    }

    /**
     * get flag
     * @return flag measurement flag
     */
    public int getFlag()
    {
        return this.flag;
    }

    /**
     * set flag
     * @param flag measurement flag
     */
    public void setFlag(int flag)
    {
        this.flag = flag;
    }

    public void setDateTime(String timestamp)
    {
        this.datetime = timestamp;
    }
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        
        sb.append("Aidon Meter DATA[");
        sb.append("(meteringValue=").append(meteringValue).append("),");
        sb.append("(meterId=").append(meterId).append("),");
        sb.append("(flag=").append(flag).append("),");
        sb.append("(activePositive=").append(activePositive).append("),");
        sb.append("(activeNegative=").append(activeNegative).append("),");
        sb.append("(reactivePositive=").append(reactivePositive).append("),");
        sb.append("(reactiveNegative=").append(reactiveNegative).append("),");
        sb.append("(vaphase1=").append(vaphase1).append("),");
        sb.append("(vbphase2=").append(vbphase2).append("),");
        sb.append("(vcphase3=").append(vcphase3).append("),");
        sb.append("(vaaquality=").append(vaaquality).append("),");
        sb.append("(vbaquality=").append(vbaquality).append("),");
        sb.append("(vcaquality=").append(vcaquality).append(')');
        sb.append("(iaphase1=").append(iaphase1).append("),");
        sb.append("(ibphase2=").append(ibphase2).append("),");
        sb.append("(icphase3=").append(icphase3).append("),");
        sb.append("(hoursInUse=").append(hoursInUse).append("),");
        sb.append("(customSerial=").append(customSerial).append("),");
        sb.append("(meterSerial=").append(meterSerial).append(')');
        sb.append("(manufactureDate=").append(manufactureDate).append(')');
        sb.append("(activeTariff=").append(activeTariff).append(')');
        sb.append("(meterLog=").append(meterLog).append(')');
        sb.append("(datetime=").append(datetime).append(')');
        sb.append("]\n");

        return sb.toString();
    }

    public EventLogData[] getEventLogData(){
        if(meterEventStatus != null && meterEventStatus.size() > 0){
            EventLogData[] evlog = null;
            Object[] obj = meterEventStatus.toArray();            
            evlog = new EventLogData[obj.length];
            for(int i = 0; i < obj.length; i++){
                evlog[i] = (EventLogData)obj[i];
            }
            return evlog;
        }
        else
        {
            return null;
        }
    }
    
    public Instrument[] getInstrument(){
        Instrument[] instruments = new Instrument[1];
        Instrument inst = new Instrument();
        inst.setVOL_A(vaphase1);
        inst.setVOL_B(vbphase2);
        inst.setVOL_C(vcphase3);
        //inst.setVOL_THD_A(vaaquality);
        //inst.setVOL_THD_B(vbaquality);
        //inst.setVOL_THD_C(vcaquality);
        inst.setCURR_A(iaphase1);
        inst.setCURR_B(ibphase2);
        inst.setCURR_C(icphase3);
        instruments[0] = inst;
        return instruments;
    }
    
    /**
     * get Data
     * @param pulseConst 펄스식 미터의 경우 필요함. 디폴트 100
     */
    @Override
    public LinkedHashMap getData()
    {
        LinkedHashMap res = new LinkedHashMap(16,0.75f,false);
        
        DecimalFormat df3 = new DecimalFormat("#.######");
        if(meter != null){
        	df3 = TimeLocaleUtil.getDecimalFormat(meter.getSupplier());
        }
        
        String val;

        // Active cumulative positive energy
        val = (String)oidValues.get("1.8.0");
        if(val != null)
            res.put("Active cumulative positive energy(kWh)",""+df3.format(activePositive));

        // Active cumulative negative energy
        val = (String)oidValues.get("2.8.0");
        if(val != null)
            res.put("Active cumulative negative energy(kWh)",""+df3.format(activeNegative));

        // Reactive cumulative positive energy
        val = (String)oidValues.get("3.8.0");
            res.put("Reactive cumulative positive energy(kvarh)",""+df3.format(reactivePositive));

        // Reactive cumulative negative energy
        val = (String)oidValues.get("4.8.0");
        if(val != null)
            res.put("Reactive cumulative negative energy(kvarh)",""+df3.format(reactiveNegative));
       
        // Va instant voltage,phase1 
        val = (String)oidValues.get("32.7.0");
        if(val != null)
            res.put("Va instant voltage,phase1(V)",""+df3.format(vaphase1));
        // Ia instant voltage,phase1 
        val = (String)oidValues.get("31.7.0");
        if(val != null)
            res.put("Ia instant voltage,phase1(A)",""+df3.format(iaphase1));


        // Vb instant voltage,phase1 
        val = (String)oidValues.get("52.7.0");
        if(val != null)
            res.put("Vb instant voltage,phase2(V)",""+df3.format(vbphase2));
        // Ib instant voltage,phase1 
        val = (String)oidValues.get("51.7.0");
        if(val != null)
            res.put("Ib instant voltage,phase2(A)",""+df3.format(ibphase2));


        // Vc instant voltage,phase1 
        val = (String)oidValues.get("72.7.0");
        if(val != null)
            res.put("Vc instant voltage,phase3(V)",""+df3.format(vcphase3));
        
        // Va average voltage over 10 minutes
        val = (String)oidValues.get("32.24.0");
        if (val != null)
            res.put("Va average voltage over 10 minutes", ""+df3.format(vaaquality));

        // Vb average voltage over 10 minutes
        val = (String)oidValues.get("52.24.0");
        if (val != null)
            res.put("Vb average voltage over 10 minutes", ""+df3.format(vbaquality));
        
        // Vc average voltage over 10 minutes
        val = (String)oidValues.get("72.24.0");
        if (val != null)
            res.put("Vc average voltage over 10 minutes", ""+df3.format(vaaquality));  
        
        // Ic instant voltage,phase1 
        val = (String)oidValues.get("71.7.0");
        if(val != null)
            res.put("Ic instant voltage,phase3(A)",""+df3.format(icphase3));

        // Hours In Use
        val = (String)oidValues.get("96.8.0");
        if(val != null)
            res.put("Hours In Use(hour)",""+(new Double(hoursInUse)).intValue());

        // Custmer Serial Number
        val = (String)oidValues.get("0.0.0");
        if(val != null)
            res.put("Customer Serial Number",val);

        // Manufacturer Serial Number
        val = (String)oidValues.get("0.0.1");
        if(val != null)
            res.put("Manufacturer Serial Number",val);
        
        // Manufacture date
        val = (String)oidValues.get("96.1.3");
        if (val != null)
            res.put("Manufacture date", ""+manufactureDate);
        
        // Active tariff
        val = (String)oidValues.get("96.89.3");
        if (val != null)
            res.put("Active tariff", ""+activeTariff);
        
        // Meter Status 
        val = (String)oidValues.get("97.97.1");
        if(val != null)
        {
            byte[] statusData = new byte[0];
            try { statusData  = Hex.encode(val); }catch(Exception ex) {}
            res.put("Diagnosis",getMeteringStatus(statusData));
        }

        return res;
    }
    
    private void makeEventObject(String datetime, String kind, int flag){
        EventLogData eventlog = new EventLogData();
        if(datetime != null && datetime.length() == 14){
            eventlog.setDate(datetime.substring(0,8));
            eventlog.setTime(datetime.substring(8,14));
            eventlog.setKind(kind);
            eventlog.setFlag(flag);
            meterEventStatus.add(eventlog);
        }
    }
}
