package com.aimir.fep.meter.parser;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.HMData;
import com.aimir.fep.meter.data.MeteringFail;
import com.aimir.fep.meter.parser.MBusTable.BaseRecordParent;
import com.aimir.model.system.Supplier;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeLocaleUtil;

/**
 * parsing MBus meter data
 *
 * @author kaze
 */
public class MBus extends MeterDataParser implements java.io.Serializable
{
	private static final long serialVersionUID = -6293342833382860362L;

	private static Log log = LogFactory.getLog(MBus.class);

    private MeterDataParser parser = null;

    protected byte[] BANK = new byte[1];
    protected byte[] METERTYPE = new byte[1]; // New MBUS Data Format meter type
    protected byte[] LPYEAR = new byte[2];
    protected byte[] LPMONTH = new byte[1];
    protected byte[] LPDAY = new byte[1];
    protected byte[] BASERECORD = new byte[136];
    protected byte[] LPRECORDDATA = new byte[1536];

    protected byte[] BASEPULSE = new byte[4];
    protected byte[] LP = new byte[2];
    protected byte[] ERRORCODE = new byte[2];

    protected int meterType = 255;
    protected String gmt = null;//year+month+day
    protected String timestamp = null;//year+month+day+hour+min+sec
    protected byte[] rawData = null;
    protected Double lp = null;
    protected Double lpValue = null;
    protected int flag = 0;
    protected String meterId = null;//Meter Serial in Fixed Data Header
    protected String meterLog = "";
    protected int lpChannelCount = 9;

    protected int period = 1;
    protected int resolution = 60;
    protected int startChar=0;
    protected int statusCode=0;
    protected int portNumber=0;
    protected int errorCode = 0;
    protected boolean isCommError=true;
    protected int pConst = 1;//EMUtil.getDefaultPulseConst(MBus.class.getName());
    protected String mcuRevision = null;

    protected HMData[] currentData = null;//current lp class for one day
    protected HMData[] lpData = null;//lp class for one day
    protected HMData[] dayData = null;//day lp class
    protected HMData[] monthData = null;//month lp class

    protected BaseRecordParent baseRecord = null;//Base Record : 136byte

    public MBus() {
    }

    /**
     * constructor
     */
	public MBus(String meterId) {
		this.meterId = meterId;
	}

    /**
     * getRawData
     */
	public byte[] getRawData() {
		return rawData;
	}

	/**
     * get raw data length
     * @return length
     */
	public int getLength() {
		if (rawData == null) {
			return 0;
		}

		return rawData.length;
	}

	public String getTimestamp() {
		return timestamp;
	}

    /**
     * parse meter mesurement data
     * @param data
     * (non-Javadoc)
     * @see nuri.aimir.service.common.parser.em.EnergyMeterDataParser#parse(byte[])
     */
	public void parse(byte[] data) throws Exception {
    	if (parser instanceof Multical401COMPAT) {
    		((Multical401COMPAT)parser).parse(data);
        }
        else if (parser instanceof Ultraheat) {
            ((Ultraheat)parser).parse(data);
        }
	}

    public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public int getStartChar() {
		return startChar;
	}

	public void setStartChar(int startChar) {
		this.startChar = startChar;
	}

	/**
     * get flag
     * @return flag measurement flag
     */
	public int getFlag() {
		return flag;
	}

    /**
     * set flag
     * @param flag measurement flag
     */
    public void setFlag(int flag)
    {
        this.flag = flag;
    }

    public boolean isCommError() {
		return isCommError;
	}

	public void setCommError(boolean isCommError) {
		this.isCommError = isCommError;
	}

	/**
     * get pulse constant
     * @return pulseConst
     */
    public int getPulseConst()
    {
        return this.pConst;
    }

    /**
     * set pulseConst
     * @param pulseConst The Pulse Constant
     */
	public void setPulseConst(int pulseConst) {
		this.pConst = pulseConst;
	}

    /**
     * @return
     */
    public int getPortNumber() {
		return portNumber;
	}

	/**
	 * @param portNumber
	 */
	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

	public int getPeriod() {
        return period;
    }

    /**
     * @return
     */
    public MeteringFail getMeteringFail() {

        MeteringFail meteringFail = null;
        if(this.errorCode > 0){
             meteringFail = new MeteringFail();
             meteringFail.setModemErrCode(this.errorCode);
             meteringFail.setModemErrCodeName(NURI_T002.getMODEM_ERROR_NAME(this.errorCode));
             return meteringFail;
        }else{
            return null;
        }
    }

	/**
	 * @return
	 */
	public HMData[] getCurrentData() {
		return currentData;
	}

    /**
     * @return
     */
    public HMData[] getLPHMData(){
        return lpData;
    }

	/**
	 * @param currentData
	 */
	public void setCurrentData(HMData[] currentData) {
		this.currentData = currentData;
	}

	/**
	 * @return
	 */
	public HMData[] getDayData() {
		return dayData;
	}

	/**
	 * @param dayData
	 */
	public void setDayData(HMData[] dayData) {
		this.dayData = dayData;
	}

	/**
	 * @return
	 */
	public HMData[] getMonthData() {
		return monthData;
	}

	/**
	 * @param monthData
	 */
	public void setMonthData(HMData[] monthData) {
		this.monthData = monthData;
	}

	/**
     * get Data
     */
	@Override
    public LinkedHashMap<String, Serializable> getData()
    {
        LinkedHashMap<String, Serializable> res = new LinkedHashMap<String, Serializable>(16,0.75f,false);
        HMData[] lp = null;
        /*
        HMData[] current = null;
        */
        HMData[] day = null;
        HMData[] month = null;

        DecimalFormat df3 = TimeLocaleUtil.getDecimalFormat(meter.getSupplier());
        
        try
        {
            lp = getLPHMData();
            /*
            current = getCurrentData();
            */
            day = getDayData();
            month = getMonthData();
            res.put("[Meter Information]", "");
            if(isCommError()){
            	//MOINSTANCE meter=EMUtil.getMeterMOBySensorId(, port);
            	res.put("Master/Sensor Communication","Error");
            }else{
	            res.put("Meter ID",baseRecord.getFixedDataHeader().getIdentificationNumber());
	            res.put("Meter Menufacturer",baseRecord.getFixedDataHeader().getMenufacturer());
	            res.put("Meter Version",baseRecord.getFixedDataHeader().getVersion()+"");
	            res.put("Meter Media",baseRecord.getFixedDataHeader().getMediumStr()+"");
	            res.put("Meter Port",getPortNumber()+"");
	            res.put("Meter Status",baseRecord.getFixedDataHeader().getStatusStr()+"");

                DecimalFormat decimalf=null;
                SimpleDateFormat datef14=null;
	            if(lp !=null && lp.length>0){
	            	res.put("[LP - Hourly Data]", "");
	            	for(int i = 0; i < lp.length; i++){
		            	String datetime = lp[i].getDate()+""+lp[i].getTime();

	                    if(meter!=null && meter.getSupplier()!=null){
	                    Supplier supplier = meter.getSupplier();
	                    if(supplier !=null){
	                        String lang = supplier.getLang().getCode_2letter();
	                        String country = supplier.getCountry().getCode_2letter();
	                        
	                        decimalf = TimeLocaleUtil.getDecimalFormat(supplier);
	                        datef14 = new SimpleDateFormat(TimeLocaleUtil.getDateFormat(14, lang, country));
	                    }
	                	}else{
	                    //locail 정보가 없을때는 기본 포멧을 사용한다.
	                    decimalf = new DecimalFormat();
	                    datef14 = new SimpleDateFormat();
	                }
	                    String date;
	                	date = datef14.format(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(datetime+"00"));
	                   
		            	String val = "";
		            	Double[] ch = lp[i].getCh();
		                for(int k = 0; k < ch.length ; k++){
	                        val += "<span style='margin-right: 40px;'>ch"+(k+1)+"="+df3.format(ch[k])+"</span>";
		                }
	                    res.put("LP"+" "+date, val);
	            	}
	            }

				if(day !=null && day.length>0){
					res.put("[LP - Daily Data]", "");
					for(int i = 0; i < day.length; i++){
		            	String datetime = day[i].getDate();
		            	String val = "";
		            	Double[] ch = day[i].getCh();
		                for(int k = 0; k < ch.length ; k++){
	                        val += "<span style='margin-right: 40px;'>ch"+(k+1)+"="+df3.format(ch[k])+"</span>";
		                }
		                res.put("DAY"+datetime, val);
	            	}
				}

				if(month !=null && month.length>0){
					res.put("[LP - Monthly Data]", "");
					for(int i = 0; i < month.length; i++){
		            	String datetime = month[i].getDate();
		            	String val = "";
		            	Double[] ch = month[i].getCh();
		                for(int k = 0; k < ch.length ; k++){
	                        val += "<span style='margin-right: 40px;'>ch"+(k+1)+"="+df3.format(ch[k])+"</span>";
		                }
		                res.put("MONTH"+datetime, val);
	            	}
				}

				/*
				if(current !=null && current.length>0){
					res.put("TITLE 4", "Current Data");
	            	for(int i = 0; i < current.length; i++){
		            	String datetime = current[i].getDate()+""+current[i].getTime();
		            	String val = "";
		            	Double[] ch = current[i].getCh();
		                for(int k = 0; k < ch.length ; k++){
		                    val += "ch"+(k+1)+"="+df3.format(ch[k])+"  ";
		                }
		                res.put("Current"+datetime, val);
	            	}
				}
				*/
            }
        }
        catch (Exception e)
        {
            log.warn("Get Data Error=>",e);
        }

        return res;
    }

    public Double getLp()
    {
        return lp;
    }

    public Double getLpValue()
    {
        return lpValue;
    }

    public String getMeterId()
    {
        return meterId;
    }

    public String getMeterLog(){
    	return meterLog;
    }

    public int getResolution(){
    	return resolution;
    }

    public int getLPChannelCount(){
    	return lpChannelCount;
    }

    /**
     * @return
     */
    public String getLPChannelMap(){
    	if (parser instanceof Multical401COMPAT) {
            return ((Multical401COMPAT)parser).getLPChannelMap();
        }
        else if (parser instanceof Ultraheat) {
            return ((Ultraheat)parser).getLPChannelMap();
        }
    	return "";
    }

	/**
	 * Constructs a <code>String</code> with all attributes
	 * in name = value format.
	 *
	 * @return a <code>String</code> representation
	 * of this object.
	 */
	public String toString()
	{
	    if (parser instanceof Multical401COMPAT) {
            return ((Multical401COMPAT)parser).toString();
        }
        else if (parser instanceof Ultraheat) {
            return ((Ultraheat)parser).toString();
        }
    	return "";
	}
	
	@Override
	public void setMcuRevision(String mcuRevision) {
		this.mcuRevision = mcuRevision;
	}


    @Override
    public Double getMeteringValue() {
        // TODO Auto-generated method stub
        return null;
    }
}
