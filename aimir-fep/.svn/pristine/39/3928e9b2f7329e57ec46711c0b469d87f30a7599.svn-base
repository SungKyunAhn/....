package com.aimir.fep.meter.parser;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Calendar;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.Instrument;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.data.MeteringFail;
import com.aimir.fep.util.CmdUtil;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Util;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;

/**
 * parsing Kamstrup 382,351 kmp protocol meter data
 *
 * @author Y.K Park (goodjob@nuritelecom.com)
 */
public class NURI_KamstrupKMP extends MeterDataParser implements java.io.Serializable 
{	   
	private static final long serialVersionUID = 5343385313014465345L;

	private static Log log = LogFactory.getLog(NURI_KamstrupKMP.class);
    
    private byte[] MDM = new byte[90];
    private byte[] TIMEZONE = new byte[2];
    private byte[] DST = new byte[2];
    private byte[] YEAR = new byte[2];
    private byte[] MONTH = new byte[1];
    private byte[] DAY = new byte[1];
    private byte[] HOUR = new byte[1];
    private byte[] MINUTE = new byte[1];
    private byte[] SECOND = new byte[1];
    private byte[] CURPULSE = new byte[4];
    private byte[] LPPERIOD = new byte[1];
    private byte[] LPCNT = new byte[2];

    private byte[] LPYEAR = new byte[2];
    private byte[] LPMONTH = new byte[1];
    private byte[] LPDAY = new byte[1];    
    private byte[] LPHOUR = new byte[1];
    private byte[] LPMIN = new byte[1];
    private byte[] LPSEC = new byte[1];
    
    private byte[] LASTPULSE = new byte[4];
    private byte[] LP = new byte[4];
    private byte[] LPCODE = new byte[1];
    private byte[] ERRORCODE = new byte[2];
    
    private byte[] EVCOUNT = new byte[1];
    
    private String timestamp = null;
    private byte[] rawData = null;
    private Double lp = null;
    private Double lpValue = null;
    private int flag = 0;
    private String meterId = null;
    private int period = 0;
    private int errorCode = 0;
    private String lastMeteringTime = null;
    
    private double voltageL1 = 0;
    private double voltageL2 = 0;
    private double voltageL3 = 0;
    private double currentL1 = 0;
    private double currentL2 = 0;
    private double currentL3 = 0;
    
    private KAMSTRUPKMP_MDM nuri_mdm = null;
    private EventLogData[] eventlogdata = null;
    private int _evTimeLen = 7;
    private int _evCodeLen = 1;
    private int _evStatusLen = 1;
    
    private static int pConst = 1;

    private LPData[] lpData = null;

    public NURI_KamstrupKMP()
    {
    }
    
    /**
     * getRawData
     */
    public byte[] getRawData()
    {
        return rawData;
    }

    /**
     * get data length
     * @return length
     */
    public int getLength()
    {
        if(rawData == null)
            return 0;

        return rawData.length;
    }

    public String getTimestamp()
    {
        return this.timestamp;
    }
    
    /**
     * parse meter mesurement data
     * @param data 
     */
    public void parse(byte[] data) throws Exception
    {
        rawData = data;
        int pos = 0;
        log.debug("[TOTAL] len=["+data.length+"] data=>"+Util.getHexString(data));
        System.arraycopy(data, pos, MDM, 0, MDM.length);
        pos += MDM.length;
        this.nuri_mdm = new KAMSTRUPKMP_MDM(MDM);
        log.debug("MDM["+this.nuri_mdm+"]");
        
        if(!nuri_mdm.isEventOnly())
        {
            
            System.arraycopy(data, pos, TIMEZONE, 0, TIMEZONE.length);
            pos += TIMEZONE.length;
            DataUtil.convertEndian(TIMEZONE);
            int timeZone = DataUtil.getIntTo2Byte(TIMEZONE);
            log.debug("TIMEZONE[" + timeZone + "]");
            
            System.arraycopy(data, pos, DST,0, DST.length);
            pos += DST.length;
            int dst = DataUtil.getIntTo2Byte(DST);
            log.debug("DST[" + dst + "]");
            
            System.arraycopy(data, pos, YEAR, 0, YEAR.length);
            pos += YEAR.length;
            DataUtil.convertEndian(YEAR);
            int year = DataUtil.getIntTo2Byte(YEAR);
            log.debug("YEAR[" + year + "]");
            
            System.arraycopy(data, pos, MONTH, 0, MONTH.length);
            pos += MONTH.length;
            int month = DataUtil.getIntToBytes(MONTH);
            log.debug("MONTH[" + month + "]");
            
            System.arraycopy(data, pos, DAY, 0, DAY.length);
            pos += DAY.length;
            int day = DataUtil.getIntToBytes(DAY);
            log.debug("DAY[" + day + "]");
            
            System.arraycopy(data, pos, HOUR, 0, HOUR.length);
            pos += HOUR.length;
            int hour = DataUtil.getIntToBytes(HOUR);
            log.debug("HOUR[" + hour + "]");
            
            System.arraycopy(data, pos, MINUTE, 0, MINUTE.length);
            pos += MINUTE.length;
            int minute = DataUtil.getIntToBytes(MINUTE);
            log.debug("MINUTE[" + minute + "]");
            
            System.arraycopy(data, pos, SECOND, 0, SECOND.length);
            pos += SECOND.length;
            int second = DataUtil.getIntToBytes(SECOND);
            log.debug("SECOND[" + second + "]");
            
            hour += timeZone;
            
    		Calendar cal = Calendar.getInstance();
    		cal.set(year, month - 1, day, hour, minute, second );
            
            timestamp = TimeUtil.getFormatTime(cal);
            

            
            System.arraycopy(data, pos, CURPULSE, 0, CURPULSE.length);
            pos += CURPULSE.length;
            DataUtil.convertEndian(CURPULSE);
            lp = new Double(DataUtil.getLongToBytes(CURPULSE));
            log.debug("CURPULSE[" + lp + "]");

            this.lpValue = new Double(this.lp.doubleValue());
            log.debug("LPVALUE[" + lpValue + "]");
            
            // from here, lp
            System.arraycopy(data, pos, LPPERIOD, 0, LPPERIOD.length);
            pos += LPPERIOD.length;
            this.period = DataUtil.getIntToBytes(LPPERIOD);
            log.debug("LPPERIOD[" + period + "]");
            
            if (period == 0) {
                System.arraycopy(data, pos, ERRORCODE, 0, ERRORCODE.length);
                pos += ERRORCODE.length;
                DataUtil.convertEndian(ERRORCODE);
                errorCode = DataUtil.getIntTo2Byte(ERRORCODE);
            }
            else {

                System.arraycopy(data, pos, LPCNT, 0, LPCNT.length);
                pos += LPCNT.length;
                DataUtil.convertEndian(LPCNT);
                int lpcnt = DataUtil.getIntTo2Byte(LPCNT);
                log.debug("LPCNT[" + lpcnt + "]");
            	
                long lastPulse = 0;
                System.arraycopy(data, pos, LASTPULSE, 0, LASTPULSE.length);
                pos += LASTPULSE.length;              
                DataUtil.convertEndian(LASTPULSE);
                lastPulse = DataUtil.getLongToBytes(LASTPULSE);
                log.debug("LASTPULSE[" + lastPulse + "]");
                
                if(lpcnt > 0){
                    this.lp = new Double(lastPulse);
                    this.lpValue = new Double(lastPulse);
                }


                int lpLength = (LPYEAR.length 
                		+ LPMONTH.length 
                		+ LPDAY.length 
                		+ LPHOUR.length 
                		+ LPMIN.length 
                		+ LPSEC.length 
                		+ LP.length 
                		+ LPCODE.length);
                
                byte[] bx = new byte[lpcnt * lpLength];
                System.arraycopy(data, pos, bx, 0, bx.length);
                pos += bx.length;
                          
                lpData = new LPData[lpcnt];
                int bxpos = 0;

                int _year = year;
                int _month = month;
                int _day = day;
                int _hour = hour;
                int _min = minute;
                int _sec = second;
                boolean isLPCorrect = false;
                boolean isPeriod = false;
                boolean isChangeDateTime = false;

                LPData[] previousLPData = null;
                for (int i = 0; i < lpcnt; i++) {                	
                   
                    System.arraycopy(bx, bxpos, LPYEAR, 0, LPYEAR.length);
                    bxpos += LPYEAR.length;
                    DataUtil.convertEndian(LPYEAR);
                    System.arraycopy(bx, bxpos, LPMONTH, 0, LPMONTH.length);
                    bxpos += LPMONTH.length;
                    System.arraycopy(bx, bxpos, LPDAY, 0, LPDAY.length);
                    bxpos += LPDAY.length;
                    System.arraycopy(bx, bxpos, LPHOUR, 0, LPHOUR.length);
                    bxpos += LPHOUR.length;
                    System.arraycopy(bx, bxpos, LPMIN, 0, LPMIN.length);
                    bxpos += LPMIN.length;
                    System.arraycopy(bx, bxpos, LPSEC, 0, LPSEC.length);
                    bxpos += LPSEC.length;

                    year = DataUtil.getIntToBytes(LPYEAR);
                    month = DataUtil.getIntToBytes(LPMONTH);
                    day = DataUtil.getIntToBytes(LPDAY);
                    hour = DataUtil.getIntToBytes(LPHOUR);
                    minute = DataUtil.getIntToBytes(LPMIN);
                    second = DataUtil.getIntToBytes(LPSEC);

                    System.arraycopy(bx, bxpos, LP, 0, LP.length);
                    bxpos += LP.length;
                    DataUtil.convertEndian(LP);
                    System.arraycopy(bx, bxpos, LPCODE, 0, LPCODE.length);
                    if(nuri_mdm.getFW_VER().equals("NG610")) {
                    	if(LPCODE[0] == (byte) 0x01){
	                		isPeriod = false;
	                		isChangeDateTime = true;
                    	}else{
	                		isPeriod = true;
	                		isChangeDateTime = false;
                    	}
                    }else {
	                    int ret = DataFormat.hex2unsigned8(LPCODE[0]) & 0x80;	
	                	if(ret > 0){
	                		isLPCorrect = true;
	                	}else{
	                		isLPCorrect = false;
	                	}
	                    ret = DataFormat.hex2unsigned8(LPCODE[0]) & 0x01;	
	                	if(ret > 0){
	                		isPeriod = false;
	                		isChangeDateTime = true;
	                	}else{
	                		isPeriod = true;
	                		isChangeDateTime = false;
	                	}
                    }
                    bxpos += LPCODE.length;

                    String tempDate1 = year
                                      + (month < 10 ? "0" + month : "" + month)
                                      + (day < 10 ? "0" + day : "" + day)
                                      + (hour < 10 ? "0" + hour : "" + hour)
                                      + (minute < 10 ? "0" + minute : "" + minute);
                    String tempDate2 = TimeUtil.getPreHour(tempDate1+"00");
                    log.debug("original lpdate"+tempDate1+", changed lpdate="+tempDate2);
                    
                    /*
                     * @TODO added kamstrup GPRS Modem fw version check.
                     */
					if(i==0) {
						previousLPData = CmdUtil.getPreviousLp(meterId, tempDate2.substring(0,8));
					}

                    lpData[i] = new LPData();
                    lpData[i].setDatetime(tempDate2);


                    if(LP[0] == (byte)0xFF && LP[1] == (byte)0xFF && LP[2] == (byte)0xFF && LP[3] == (byte)0xFF){

                        lpData[i].setBasePulse(0);
                        lpData[i].setCh(new Double[]{new Double(0)});
                        lpData[i].setV(new Double[]{new Double(0)});
                    	lpData[i].setFlag(6);
                    }else if(LPCODE[0] == (byte)0x01){
                        lpData[i].setBasePulse(0);
                        lpData[i].setCh(new Double[]{new Double(0)});
                        lpData[i].setV(new Double[]{new Double(0)});
                    	lpData[i].setFlag(6);
                    }else {
                        lpData[i].setBasePulse(DataUtil.getIntTo4Byte(LP));
                        lpData[i].setCh(new Double[]{new Double(DataUtil.getIntTo4Byte(LP))});
                        lpData[i].setV(new Double[]{new Double(DataUtil.getIntTo4Byte(LP))});
                        lpData[i].setFlag(0);
                    }

                    lpData[i].setPF(new Double(1));
                }    

				if(previousLPData!= null){
					lpData = mergeLPData(previousLPData,lpData);
				}

                long totalPulse = 0;
                long lastLp = 0;
                long lp = 0;
                ArrayList<LPData> tempLpList = null;
                LPData tempLp = null;
                
				if (this.nuri_mdm.isRecoveryMetering())
				{
					totalPulse = -1;
					if (lpData.length > 0) {
						tempLpList = new ArrayList<LPData>();
						boolean isStart = false;

						for (int i = 0; i < lpData.length; i++) {
							log.debug("lucky > DateTime:"+lpData[i].getDatetime()+" bp:"+lpData[i].getBasePulse());
							log.debug("lucky > totalPulse="+totalPulse);
							if(lpData[i].getDatetime().endsWith("230000") && lpData[i].getFlag()==0) {
								isStart = true;
							} else if(lpData[i].getDatetime().endsWith("230000") && lpData[i].getFlag()==6){
								isStart = false;
								totalPulse = -1;
							}
							if(isStart){
								if (lpData[i].getDatetime().endsWith("230000")) {
									if (totalPulse > -1) {
										lp = lpData[i].getBasePulse() - totalPulse;
										tempLp = new LPData();
										tempLp.setLp((double)totalPulse);
										tempLp.setLpValue((double)totalPulse);
										tempLp.setBasePulse(lp);
										tempLp.setCh(new Double[] { (double)lp });
										tempLp.setV(new Double[] { (double)lp });
										tempLp.setDatetime(lpData[i].getDatetime());
										tempLp.setPF(1d);
										tempLpList.add(tempLp);
										totalPulse = lpData[i].getBasePulse();
									} else {
										totalPulse = lpData[i].getBasePulse();
									}
								} else {
									lp = lpData[i].getBasePulse();
									tempLp = new LPData();
									tempLp.setLp((double)totalPulse);
									tempLp.setLpValue((double)totalPulse);
									tempLp.setBasePulse(lp);
									tempLp.setCh(new Double[] { (double)lp });
									tempLp.setV(new Double[] { (double)lp });
									tempLp.setDatetime(lpData[i].getDatetime());
									tempLp.setPF(1d);
									tempLpList.add(tempLp);
									totalPulse += lpData[i].getBasePulse();
								}
							}
						}
					}
					lpData = null;
					if (tempLpList != null && tempLpList.size() > 0) {
						Object[] obj = tempLpList.toArray();
						lpData = new LPData[obj.length];
						for (int i = 0; i < obj.length; i++) {
							lpData[i] = (LPData) obj[i];
							log.debug("finalData:"+lpData[i].toString());
						}

						if (lpData != null && lpData.length > 0
								&& lpData[lpData.length - 1].getLp() != null) {
							this.lp = new Double(lpData[lpData.length - 1]
									.getLp());
							this.lpValue = new Double(lpData[lpData.length - 1]
									.getLpValue());
							this.lastMeteringTime = lpData[lpData.length - 1]
									.getDatetime();
						}
					}
				} else {
					totalPulse = -1;
					if (lpData.length > 0) {
						tempLpList = new ArrayList<LPData>();

						boolean isStart = false;
						for (int i = 0; i < lpData.length; i++) {
							log.debug("lucky > DateTime:"+lpData[i].getDatetime()+" bp:"+lpData[i].getBasePulse());
							log.debug("lucky > totalPulse="+totalPulse);

							if(lpData[i].getDatetime().endsWith("230000") && lpData[i].getFlag()==0) {
								isStart = true;
							} else if(lpData[i].getDatetime().endsWith("230000") && lpData[i].getFlag()==6){
								isStart = false;
								totalPulse = -1;
							}
							if(isStart){
								if (lpData[i].getDatetime().substring(8, 12).equals("2300")) {
									if (totalPulse > -1) {
										lp = lpData[i].getBasePulse() - totalPulse;
										tempLp = new LPData();
										tempLp.setLp((double)totalPulse);
										tempLp.setLpValue((double)totalPulse);
										tempLp.setBasePulse(lp);
										tempLp.setCh(new Double[] { (double)lp });
										tempLp.setV(new Double[] { (double)lp });
										tempLp.setDatetime(lpData[i].getDatetime());
										tempLp.setPF(1d);
										tempLpList.add(tempLp);
										totalPulse = lpData[i].getBasePulse();
									} else {
										totalPulse = lpData[i].getBasePulse();
									}
								} else {
									lp = lpData[i].getBasePulse();
									tempLp = new LPData();
									tempLp.setLp((double)totalPulse);
									tempLp.setLpValue((double)totalPulse);
									tempLp.setBasePulse(lp);
									tempLp.setCh(new Double[] { (double)lp });
									tempLp.setV(new Double[] { (double)lp });
									tempLp.setDatetime(lpData[i].getDatetime());
									tempLp.setPF(1d);
									tempLpList.add(tempLp);
									totalPulse += lpData[i].getBasePulse();
								}
							}
						}
						lpData = null;
						if (tempLpList != null && tempLpList.size() > 0) {
							Object[] obj = tempLpList.toArray();
							lpData = new LPData[obj.length];
							for (int i = 0; i < obj.length; i++) {
								lpData[i] = (LPData) obj[i];
								log.debug("finalData:"+lpData[i].toString());
							}

							if (lpData != null && lpData.length > 0
									&& lpData[lpData.length - 1].getLp() != null) {
								this.lp = new Double(lpData[lpData.length - 1]
										.getLp());
								this.lpValue = new Double(lpData[lpData.length - 1]
										.getLpValue());
								this.lastMeteringTime = lpData[lpData.length - 1]
										.getDatetime();
							}
						}
					}
				}

                byte[] km = new byte[42];
                System.arraycopy(data, pos, km, 0, km.length);
                parseInstrument(km);    
                pos += km.length;
                parseEvent(pos,data);
            }
        }
        else
        {
            parseEvent(MDM.length,data);
        }

    }
    
    public void parseInstrument(byte[] data) throws Exception
    {
        String unit="";        
        int cntRegVal;
		cntRegVal = 0;
        int signInt=0;
        int signExp=0;
        int exp=0;        
        byte byteSiEx;
        double siEx=0;
        double regVal=0;
    	int idx = 0;
    	int len = 4;
    	
    	for(int i = 0; i < 6; i++)
    	{
            unit=getUnit(DataUtil.getIntToByte(data[idx++]));//getUnit
            cntRegVal=DataUtil.getIntToByte(data[idx++]);//getValueCount            
            byteSiEx=data[idx++];
            signInt=(byteSiEx & 128)/128;
            signExp=(byteSiEx & 64)/64;
            exp=((byteSiEx&32) + (byteSiEx&16) + (byteSiEx&8) + (byteSiEx&4) + (byteSiEx&2) + (byteSiEx&1));
            siEx=Math.pow(-1, signInt)*Math.pow(10, Math.pow(-1, signExp)*exp);//-1^SI*-1^SE*exponent                
                                
            byte temp[]=new byte[len];            
            temp=DataUtil.select(data,idx,len);
            DataUtil.convertEndian(temp);
            idx += len;            
            
            regVal=DataUtil.getIntToBytes(temp)*siEx;
            
            if(i == 0){
                voltageL1=regVal;
            }
            else if(i == 1){
                voltageL2=regVal;
            }
            else if(i == 2){
                voltageL3=regVal;
            }
            else if(i == 3){
                currentL1=regVal;
            }
            else if(i == 4){
                currentL2=regVal;
            }
            else if(i == 5){
                currentL3=regVal;
            }
            
            log.info("["+i+"]: "+regVal+" "+unit);
    	}
   
    }
    
    public void parseEvent(int offset,byte[] data) throws Exception
    {
    	int pos = offset;            	
        System.arraycopy(data, pos, EVCOUNT, 0, EVCOUNT.length);
        pos += EVCOUNT.length;
        int evCount = DataUtil.getIntToBytes(EVCOUNT);
        log.debug("EVCOUNT[" + evCount + "]");
        
        if(evCount > 0)
        {
        	eventlogdata = new EventLogData[evCount];
        	for(int i = 0; i < evCount; i++)
        	{
        		try { 
	        		String evTime = DataFormat.getDateTime(DataUtil.select(data, pos, _evTimeLen));
	        		pos += _evTimeLen;
	        		int evCode = DataFormat.hex2dec(data, pos, _evCodeLen);
	        		pos += _evCodeLen;   
	        		int evStatus = DataFormat.hex2dec(data, pos, _evStatusLen);
	        		pos += _evStatusLen; 
	        		
	        		eventlogdata[i] = new EventLogData();
	        		eventlogdata[i].setDate(evTime.substring(0,8));
	        		eventlogdata[i].setTime(evTime.substring(8,14));
	        		eventlogdata[i].setAppend(evStatus+"");
	        		switch(evCode)
	        		{
	        		//Flag는 기존에 구분없이 0으로 metereventlog에 저장되어 있는것으로 인해 
	        		//metereventlog에서 각 이벤트에 맞게 구분이 안되었다.
	        		//flag값에 100+evCode 형태로 저장한다.
	        		case 0: eventlogdata[i].setKind("203.10.0");//power fail
	        				eventlogdata[i].setMsg("Meter Power Failure");
	        				this.errorCode = Integer.parseInt(
	        		                CommonConstants.getMeterStatusCode(MeterStatus.PowerDown));
	        				eventlogdata[i].setFlag(1000);
	        			break;
	        		case 1: eventlogdata[i].setKind("203.10.0");//power restore
	        				eventlogdata[i].setMsg("Meter Power Restore");
	        				this.errorCode = Integer.parseInt(
	        		                CommonConstants.getMeterStatusCode(MeterStatus.Normal));
	        				eventlogdata[i].setFlag(1001);
	            		break;
	        		case 2: eventlogdata[i].setKind("203.10.0");//line missing
	        				eventlogdata[i].setMsg("Line Missing"+getLinePhase(evStatus));
	        				eventlogdata[i].setFlag(1100 + evStatus);
	            		break;
	        		case 3: eventlogdata[i].setKind("203.10.0");//line restore
	        				eventlogdata[i].setMsg("Line Restore"+getLinePhase(evStatus));
	        				eventlogdata[i].setFlag(1200 + evStatus);
	            		break;
	        		case 4: eventlogdata[i].setKind("214.4.0");//ota success
	        				eventlogdata[i].setMsg("Success");
	        				eventlogdata[i].setFlag(1000);
	            		break;
	        		case 5: eventlogdata[i].setKind("214.4.0");//ota fail
	        				eventlogdata[i].setMsg("Fail");
	        				eventlogdata[i].setFlag(1001);
	            		break;
	        		}
	        		
	        		log.debug(eventlogdata[i].toString());
        		}catch(Exception e){ }
        	}
        }
    }
    
    protected String getLinePhase(int status)
    {
    	String ret = "";
    	if((int)(status & 0x04) > 0){
    		ret += " ,L1";
    	}
    	if((int)(status & 0x02) > 0){
    		ret += " ,L2";
    	}
    	if((int)(status & 0x01) > 0){
    		ret += " ,L3";
    	}
    	return ret;
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

    /**
     * get pulse constant 
     * @return pulseConst 
     */
    public int getPulseConst()
    {
        return pConst;
    }

    /**
     * set pulseConst
     * @param pulseConst The Pulse Constant
     */
    public void setPulseConst(int pulseConst)
    {
        pConst = pulseConst;
    }

    public int getPeriod() {
        return this.period;
    }
    
    public int getMeterStatusCode() {
    	return this.errorCode;
    }
    
    public MeteringFail getMeteringFail() {
        return null;
        /*
        MeteringFail meteringFail = null;
        if(this.errorCode == Integer.parseInt(
                CommonConstants.getMeterStatusCode(MeterStatus.MeteringFail))) {
             meteringFail = new MeteringFail();
             meteringFail.setModemErrCode(this.errorCode);
             meteringFail.setModemErrCodeName("Metering Fail");
             return meteringFail;
        }else{
            return null;
        }
        */
    }
    
    /**
     * get String
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        
        sb.append("NURI_KamstrupKMP DATA[");
        sb.append("(lp=").append(lp).append("),");
        sb.append("(lpValue=").append(lpValue).append("),");
        sb.append("(pulseConstant=").append(pConst).append(')');
        sb.append("(lp=(");
        for(int i = 0 ; i < 24 ; i++)
        {
            // sb.append("Hour["+i+"]=["+preDayLPValue[i]+"], ");
        }
        sb.append(")\n");
        sb.append("]\n");

        return sb.toString();
    }

    /**
     * get Data
     */
    @SuppressWarnings("unchecked")
    @Override
    public LinkedHashMap getData()
    {
        LinkedHashMap res = new LinkedHashMap(16,0.75f,false);
        DecimalFormat df3 = TimeLocaleUtil.getDecimalFormat(meter.getSupplier());
        
        //res.put("name","ZEUPLS");
        res.put("Metering Time", timestamp);
        res.put("LP",""+df3.format(lp));
        res.put("LP Value",""+df3.format(lpValue));
        res.put("Resolution",""+period);
        
        for (int i = 0; i < lpData.length; i++) {
            //res.put("BasePulse Date", lpData[i].getDatetime());
            //res.put("BasePulse", lpData[i].getBasePulse());
            res.put(lpData[i].getDatetime(), lpData[i].getLp()+","+lpData[i].getCh()[0]);
        }
        
        Instrument[] inst = getInstrument();
        for(int i = 0; inst != null && inst.length > i ; i++){
        	res.put("Voltage L1",inst[i].getVOL_A()+"");
        	res.put("Voltage L2",inst[i].getVOL_B()+"");
        	res.put("Voltage L3",inst[i].getVOL_C()+"");
        	res.put("Current L1",inst[i].getCURR_A()+"");
        	res.put("Current L2",inst[i].getCURR_B()+"");
        	res.put("Current L3",inst[i].getCURR_C()+"");
        }
        
        EventLogData[] evlog = getEventLog();
        if(evlog != null && evlog.length > 0){
            res.put("[Event Log]", "");
            for(int i = 0; i < evlog.length; i++){
                String datetime = evlog[i].getDate() + evlog[i].getTime();
                if(!datetime.startsWith("0000") && !datetime.equals("")){
                    res.put("EV"+i+datetime+"00", evlog[i].getMsg());
                }
            }
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
    
    public String getSwVersion(){
    	
   	 String version = "";
	 try
     {
		 version=  this.nuri_mdm.getMeterVersion();
     }catch(Exception e){log.warn("get meter sw version ",e);}
        log.debug("getSwVersion() :"+version);
        return version;
   }
   
   public String getSwName(){
   	
   	String swname ="";
	 try
     {
		 swname =  this.nuri_mdm.getMeterVersion();
     }catch(Exception e){log.warn("get meter sw name ",e);}
        log.debug("getSwName() :"+swname);
        return swname;
   }

    public String getMeterId()
    {
        return meterId;
    }
    
    public String getLastMeteringTime(){
    	return this.lastMeteringTime;
    }

    public LPData[] getLPData() {
        return this.lpData;
    }
    
    public int getResolution(){
        return this.period;
    }
    
    public int getLPChannelCount(){
        return 3;//ch1,v1,pf
    }
    
    public EventLogData[] getEventLog(){
        return eventlogdata;
    }
    
    public Instrument[] getInstrument(){
        Instrument[] instruments = new Instrument[1];
        Instrument inst = new Instrument();
        inst.setVOL_A(voltageL1);
        inst.setVOL_B(voltageL2);
        inst.setVOL_C(voltageL3);
        inst.setCURR_A(currentL1);
        inst.setCURR_B(currentL2);
        inst.setCURR_C(currentL3);
        instruments[0] = inst;
        return instruments;
    }
    
    @SuppressWarnings("unchecked")
    public HashMap getMdmData(){
        
        HashMap<String, String> map = null;

        try{
            if(this.nuri_mdm != null){
                map = new HashMap<String, String>();
                map.put("mcuType","5");
                
                if(this.nuri_mdm.getFW_VER().startsWith("NG")){
                    map.put("protocolType","11");//gprs/sms
                }else{
                    map.put("protocolType","1");//cdma
                }
                map.put("errorStatus", this.nuri_mdm.getERROR_STATUS_STRING());
                map.put("simNumber", this.nuri_mdm.getIMSI_NUM());
                
                map.put("sysPhoneNumber", this.nuri_mdm.getPHONE_NUM());
                map.put("id", this.nuri_mdm.getId());
                map.put("swVersion", this.nuri_mdm.getFW_VER());
                map.put("hwVersion", this.nuri_mdm.getHW_VER());
                map.put("meterSWVersion", this.nuri_mdm.getMeterVersion());
                map.put("networkStatus", "1");
                map.put("csq", this.nuri_mdm.getCSQ_LEVEL()+"");
            }
        }catch(Exception e){
            log.warn(e,e);
        }
        return map;
    }
    
    public String getUnit(int unit){
        switch(unit){
        case 1:
            return "Wh";
        case 2:
            return "kWh";
        case 3:
            return "MWh";
        case 4:
            return "GWh";
        case 13:
            return "varh";
        case 14:
            return "kvarh";
        case 15:
            return "Mvarh";
        case 16:
            return "Gvarh";
        case 17:
            return "VAh";
        case 18:
            return "kVAh";
        case 19:
            return "MVAh";
        case 20:
            return "GVAh";
        case 21:
            return "W";
        case 22:
            return "kW";
        case 23:
            return "MW";
        case 24:
            return "GW";
        case 25:
            return "var";
        case 26:
            return "kvar";
        case 27:
            return "Mvar";
        case 28:
            return "Gvar";
        case 29:
            return "VA";
        case 30:
            return "kVA";
        case 31:
            return "MVA";
        case 32:
            return "GVA";
        case 33:
            return "V";
        case 34:
            return "A";
        case 35:
            return "kV";
        case 36:
            return "kA";
        case 37:
            return "c";
        case 38:
            return "K";
        case 39:
            return "l";
        case 40:
            return "m3";
        case 46:
            return "h";
        case 47:
            return "clock";
        case 48:
            return "dato1";
        case 51:
            return "number";
        case 53:
            return "RTC";
        case 54:
            return "ASCII coded data";
        case 55:
            return "m3 x 10";
        case 56:
            return "ton x 10";
        case 57:
            return "Gj x 10";   
        default:
            return "";
        }
        
    }

    private LPData[] mergeLPData(LPData[] previousLPData, LPData[] lpData) {
    	SortedMap<String, LPData> sorter = new TreeMap<String, LPData>();
    	for(int i=0;i<previousLPData.length;i++) {
    		sorter.put(previousLPData[i].getDatetime(), previousLPData[i]);
    	}
    	for(int i=0;i<lpData.length;i++) {
    		sorter.put(lpData[i].getDatetime(), lpData[i]);
    	}

    	LPData[] merged = new LPData[sorter.size()];		
    	Iterator<String> iterKeys = sorter.keySet().iterator();

    	for (int i = 0; iterKeys.hasNext(); i++) {
    		merged[i] = (LPData)sorter.get(iterKeys.next());
    	}
    	return merged;
	}


	@Override
	public Double getMeteringValue() {
		return this.lpValue;
	}
}
