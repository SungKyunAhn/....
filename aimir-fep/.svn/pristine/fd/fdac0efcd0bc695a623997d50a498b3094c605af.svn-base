package com.aimir.fep.meter.parser;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.HMData;
import com.aimir.fep.meter.parser.multical401CompatTable.BaseRecord;
import com.aimir.fep.meter.parser.multical401CompatTable.LPRecordData;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

public class Ultraheat extends MBus {
	private static Log log = LogFactory.getLog(Ultraheat.class);

    protected LPRecordData lpRecordData = null;//LP Record Data

    public Ultraheat() {
    	BANK = new byte[1];
    	LPYEAR = new byte[2];
    	LPMONTH = new byte[1];
    	LPDAY = new byte[1];
    	BASERECORD = new byte[59];
    	LPRECORDDATA = new byte[816];

    	BASEPULSE = new byte[4];
    	LP = new byte[2];
    	ERRORCODE = new byte[2];
    	lpChannelCount = 7;
    }

    /**
     * constructor
     */
    public Ultraheat(String meterId)
    {
    	this();
        this.meterId = meterId;
    }

    /**
     * parse meter mesurement data
     * @param data
     */
    /* (non-Javadoc)
     * @see nuri.aimir.service.common.parser.em.EnergyMeterDataParser#parse(byte[], int)
     */
    public void parse(byte[] data) throws Exception
    {
    	log.debug("Ultraheat LENGTH["+data.length+"] RAW["+Hex.decode(data)+"]");
    	rawData=data;
        int pos = 0;

        log.debug("LPPERIOD[" + period + "]");

        //Metering Fail
        if (period == 0) {
            System.arraycopy(data, pos, ERRORCODE, 0, ERRORCODE.length);
            pos += ERRORCODE.length;
            DataUtil.convertEndian(ERRORCODE);
            errorCode = DataUtil.getIntTo2Byte(ERRORCODE);
        }
        //Metering Success
        else {
        	DecimalFormat df = new DecimalFormat("######.######");
            int dateCnt = 1;
            log.debug("DATECNT[" + dateCnt + "]");
            byte[] mbusTotalRom = new byte[data.length - pos];//MBus Total Metering Log Data
            System.arraycopy(data, pos, mbusTotalRom, 0, mbusTotalRom.length);
            
            int totalpos = 0;
            
            ArrayList<HMData> currentList = null;
            ArrayList<HMData> lpList = null;
            ArrayList<HMData> dayList= null;
            ArrayList<HMData> monthList= null;
            
            for (int i = 0; totalpos < mbusTotalRom.length; i++) {
            	
            	pos = 0;
            	
                /*
            	System.arraycopy(data, pos, BANK, 0, BANK.length);//1byte
                pos += BANK.length;
                int bank = DataUtil.getIntToBytes(BANK);
                */
            	
            	//old int lpLength = BANK.length + LPYEAR.length + LPMONTH.length + LPDAY.length + BASERECORD.length + LPRECORDDATA.length;
            	int lpLength = METERTYPE.length + LPYEAR.length + LPMONTH.length + LPDAY.length + BASERECORD.length + LPRECORDDATA.length;
            	
                System.arraycopy(data, totalpos, METERTYPE, 0, METERTYPE.length);//1byte
                
                pos += METERTYPE.length;
                meterType = DataUtil.getIntToBytes(METERTYPE);
                log.debug("meterType[" + meterType + "]");

            	byte[] mbusDayRom = new byte[lpLength];//MBus One Day Metering Log Data : 880 byte
            	System.arraycopy(mbusTotalRom, totalpos, mbusDayRom, 0, mbusDayRom.length);
            	
            	//-------------------
                //GMT : 4byte
            	//-------------------
            	System.arraycopy(mbusDayRom, pos, LPYEAR, 0, LPYEAR.length);//2byte
                pos += LPYEAR.length;
                int year = DataUtil.getIntToBytes(LPYEAR);
                System.arraycopy(mbusDayRom, pos, LPMONTH, 0, LPMONTH.length);//1byte
                pos += LPMONTH.length;
                int month = DataUtil.getIntToBytes(LPMONTH);
                System.arraycopy(mbusDayRom, pos, LPDAY, 0, LPDAY.length);//1byte
                pos += LPDAY.length;
                int day = DataUtil.getIntToBytes(LPDAY);
                gmt=year  + (month < 10? "0"+month:""+month)+ (day < 10? "0"+day:""+day);//GMT from Sensor Rom
                timestamp=gmt+"000000";
                log.debug("GMT["+gmt+"]");

                //-------------------
                //Base Record(59 byte)
                //-------------------
                System.arraycopy(mbusDayRom, pos, BASERECORD, 0, BASERECORD.length);//baseRecord : 59 byte
                
                log.debug("Ultraheat mbusDayRom["+mbusDayRom.length+"] RAW["+Hex.decode(mbusDayRom)+"]");
                
                baseRecord = new BaseRecord(BASERECORD);
            	setPortNumber(baseRecord.getAddress());
                pos += BASERECORD.length;
                if(baseRecord.getStart1()!=0x68){
                	isCommError=true;
                }else{
                	isCommError=false;
                }


                if(baseRecord!=null && !isCommError()){
	                //-------------------
	                //LP Record Data(34 x 24 = 816 byte)
	                //-------------------
                	log.debug("pos["+pos+"] mbusDayRom.length["+mbusDayRom.length+"] LPRECORDDATA.length["+LPRECORDDATA.length+"]");
	                System.arraycopy(mbusDayRom, pos, LPRECORDDATA, 0, LPRECORDDATA.length);//lpRecordData : 816 byte
	            	pos += LPRECORDDATA.length;

	                int tempHour=0;
	                int tempMin=0;

	                String hhmm="";

	                pos = 0;
	                
	                if(currentList==null)
	                	currentList= new ArrayList<HMData>();
	                if(lpList==null)
	                	lpList= new ArrayList<HMData>();
	                if(dayList==null)
	                	dayList= new ArrayList<HMData>();
	                if(monthList==null)
	                	monthList= new ArrayList<HMData>();
	                
	                for(int j=0;j<24*period;j++,tempMin += resolution){

	                	byte[] mbusOnePeriodRom = new byte[34];//MBus One Period Metering Log Data
	                	System.arraycopy(LPRECORDDATA, pos, mbusOnePeriodRom, 0, mbusOnePeriodRom.length);
	                	pos += mbusOnePeriodRom.length;
	                	lpRecordData=new LPRecordData(j, mbusOnePeriodRom,baseRecord.getControlInformation());

	                	//make hhmm
	                	if (tempMin == 60) {
	                        tempHour++;
	                        tempMin = 0;
	                    }
	                    hhmm = (tempHour < 10? "0"+tempHour:""+tempHour) + (tempMin < 10? "0"+tempMin:""+tempMin);

	                	//---------------------------
	                    //Setting current Data
	                    //---------------------------
	                    if(!lpRecordData.getDataBlock()[0].isEmpty()){
		                    HMData currentHmData = new HMData();
		                    currentHmData.setKind("CURRENT");
		                    currentHmData.setDate(gmt);//yyyymmdd
		                    currentHmData.setTime(hhmm);//hhmm
		                    currentHmData.setChannelCnt(7);
		                    currentHmData.setCh(1,new Double(df.format(lpRecordData.getDataBlock()[2].getDataValue()*lpRecordData.getDataBlock()[2].getDataMultiplier())));//Heat Power
		                    currentHmData.setCh(2,new Double(df.format(lpRecordData.getDataBlock()[0].getDataValue()*lpRecordData.getDataBlock()[0].getDataMultiplier())));//Heat Quantity
		                    currentHmData.setCh(3,new Double(df.format(lpRecordData.getDataBlock()[1].getDataValue()*lpRecordData.getDataBlock()[1].getDataMultiplier())));//Volume
		                    currentHmData.setCh(4,new Double(df.format(lpRecordData.getDataBlock()[4].getDataValue()*lpRecordData.getDataBlock()[4].getDataMultiplier())));//Flow Temperature
		                    currentHmData.setCh(5,new Double(df.format(lpRecordData.getDataBlock()[3].getDataValue()*lpRecordData.getDataBlock()[3].getDataMultiplier())));//Flowrate
		                    currentHmData.setCh(6,new Double(df.format(lpRecordData.getDataBlock()[5].getDataValue()*lpRecordData.getDataBlock()[5].getDataMultiplier())));//Return Temperature
		                    if(lpRecordData.getDataBlock()[6].isEmpty()) {
								double diff = lpRecordData.getDataBlock()[4].getDataValue()*lpRecordData.getDataBlock()[4].getDataMultiplier()
										- lpRecordData.getDataBlock()[5].getDataValue()*lpRecordData.getDataBlock()[5].getDataMultiplier();
			                    currentHmData.setCh(7,new Double(df.format(diff)));//Temperature Difference
		                    } else {
			                    currentHmData.setCh(7,new Double(df.format(lpRecordData.getDataBlock()[6].getDataValue()*lpRecordData.getDataBlock()[6].getDataMultiplier())));//Temperature Difference
		                    }
		                    currentHmData.setFlag(0);
		                    currentList.add(currentHmData);
		                    timestamp=gmt+hhmm+"00";
		                    isCommError=false;
		                    this.lpValue = currentList.get(currentList.size()-1).getCh()[1];
	                    }else{
	                    	log.debug("TIME["+gmt+hhmm+"] CURRENT IS EMPTY");
	                    }
	                    //---------------------------
	                    //Setting lp Data
	                    //---------------------------
	                    if(!lpRecordData.getDataBlock()[0].isEmpty()){
		                    HMData lpHmData = new HMData();
		                    lpHmData.setKind("LP");
		                    lpHmData.setDate(gmt);//yyyymmdd
		                    lpHmData.setTime(hhmm);//hhmm
		                    lpHmData.setChannelCnt(7);
		                    lpHmData.setCh(1,new Double(df.format(lpRecordData.getDataBlock()[2].getDataValue()*lpRecordData.getDataBlock()[2].getDataMultiplier())));//Heat Power
		                    lpHmData.setCh(2,new Double(df.format(lpRecordData.getDataBlock()[0].getDataValue()*lpRecordData.getDataBlock()[0].getDataMultiplier())));//Heat Quantity
		                    lpHmData.setCh(3,new Double(df.format(lpRecordData.getDataBlock()[1].getDataValue()*lpRecordData.getDataBlock()[1].getDataMultiplier())));//Volume
		                    lpHmData.setCh(4,new Double(df.format(lpRecordData.getDataBlock()[4].getDataValue()*lpRecordData.getDataBlock()[4].getDataMultiplier())));//Flow Temperature
		                    lpHmData.setCh(5,new Double(df.format(lpRecordData.getDataBlock()[3].getDataValue()*lpRecordData.getDataBlock()[3].getDataMultiplier())));//Flowrate
		                    lpHmData.setCh(6,new Double(df.format(lpRecordData.getDataBlock()[5].getDataValue()*lpRecordData.getDataBlock()[5].getDataMultiplier())));//Return Temperature
		                    if(lpRecordData.getDataBlock()[6].isEmpty()) {
								double diff = lpRecordData.getDataBlock()[4].getDataValue()*lpRecordData.getDataBlock()[4].getDataMultiplier()
										- lpRecordData.getDataBlock()[5].getDataValue()*lpRecordData.getDataBlock()[5].getDataMultiplier();
								lpHmData.setCh(7,new Double(df.format(diff)));//Temperature Difference
		                    } else {
		                    	lpHmData.setCh(7,new Double(df.format(lpRecordData.getDataBlock()[6].getDataValue()*lpRecordData.getDataBlock()[6].getDataMultiplier())));//Temperature Difference
		                    }
		                    lpHmData.setCh(7,new Double(df.format(lpRecordData.getDataBlock()[6].getDataValue()*lpRecordData.getDataBlock()[6].getDataMultiplier())));//Temperature Difference
		                    lpHmData.setFlag(0);

		                    lpList.add(lpHmData);
		                    timestamp=gmt+hhmm+"00";
		                    isCommError=false;
	                    }else{
	                    	log.debug("TIME["+gmt+hhmm+"] LP IS EMPTY");
	                    }
	                }

	                //---------------------------
	                //Setting Day Data
	                //---------------------------
	                if(!baseRecord.getDataBlocks().getDataBlock()[0].isEmpty()){
	                	HMData dayHmData = new HMData();
		                dayHmData.setKind("DAY");
		                dayHmData.setDate(gmt);//yyyymmdd
		            	dayHmData.setTime("0000");//hhmm
		                dayHmData.setChannelCnt(7);

	                    dayHmData.setCh(1,new Double(df.format(baseRecord.getDataBlocks().getDataBlock()[4].getDataValue()*baseRecord.getDataBlocks().getDataBlock()[4].getDataMultiplier())));//Heat Power
		                dayHmData.setCh(2,new Double(df.format(baseRecord.getDataBlocks().getDataBlock()[2].getDataValue()*baseRecord.getDataBlocks().getDataBlock()[2].getDataMultiplier())));//Heat Quantity
		                dayHmData.setCh(3,new Double(df.format(baseRecord.getDataBlocks().getDataBlock()[3].getDataValue()*baseRecord.getDataBlocks().getDataBlock()[3].getDataMultiplier())));//Volume
		                dayHmData.setCh(4,new Double(df.format(baseRecord.getDataBlocks().getDataBlock()[6].getDataValue()*baseRecord.getDataBlocks().getDataBlock()[6].getDataMultiplier())));//Flow Temperature
		                dayHmData.setCh(5,new Double(df.format(baseRecord.getDataBlocks().getDataBlock()[5].getDataValue()*baseRecord.getDataBlocks().getDataBlock()[5].getDataMultiplier())));//Flowrate
		                dayHmData.setCh(6,new Double(df.format(baseRecord.getDataBlocks().getDataBlock()[7].getDataValue()*baseRecord.getDataBlocks().getDataBlock()[7].getDataMultiplier())));//Return Temperature
	                    if(baseRecord.getDataBlocks().getDataBlock()[6].isEmpty()) {
							double diff = baseRecord.getDataBlocks().getDataBlock()[6].getDataValue()*baseRecord.getDataBlocks().getDataBlock()[6].getDataMultiplier()
									- baseRecord.getDataBlocks().getDataBlock()[7].getDataValue()*baseRecord.getDataBlocks().getDataBlock()[7].getDataMultiplier();
							dayHmData.setCh(7,new Double(df.format(diff)));//Temperature Difference
	                    } else {
	                    	dayHmData.setCh(7,new Double(df.format(baseRecord.getDataBlocks().getDataBlock()[8].getDataValue()*baseRecord.getDataBlocks().getDataBlock()[8].getDataMultiplier())));//Temperature Difference
	                    }
		                dayHmData.setCh(7,new Double(df.format(baseRecord.getDataBlocks().getDataBlock()[8].getDataValue()*baseRecord.getDataBlocks().getDataBlock()[8].getDataMultiplier())));//Temperature Difference
		                dayHmData.setFlag(0);
		                dayList.add(dayHmData);
	                }else{
	                	log.debug("TIME["+gmt+"0000"+"] DAY LP IS EMPTY");
	                }


	                //---------------------------
	                //Setting Month Data
	                //---------------------------
	                if(dayList.get(dayList.size()-1)!=null && dayList.get(dayList.size()-1).getDate().substring(6, 8).equals("01")){
			            HMData monthHmData = new HMData();
			            monthHmData.setKind("MONTH");
			            monthHmData.setDate(dayList.get(dayList.size()-1).getDate());//yyyymmdd
			            monthHmData.setTime(dayList.get(dayList.size()-1).getTime());//hhmm
			            monthHmData.setChannelCnt(7);
			            monthHmData.setCh(1,dayList.get(dayList.size()-1).getCh()[0]);//Heat Power
			            monthHmData.setCh(2,dayList.get(dayList.size()-1).getCh()[1]);//Heat Quantity
			            monthHmData.setCh(3,dayList.get(dayList.size()-1).getCh()[2]);//Volume
			            monthHmData.setCh(4,dayList.get(dayList.size()-1).getCh()[3]);//Flow Temperature
			            monthHmData.setCh(5,dayList.get(dayList.size()-1).getCh()[4]);//Flowrate
			            monthHmData.setCh(6,dayList.get(dayList.size()-1).getCh()[5]);//Return Temperature
			            monthHmData.setCh(7,dayList.get(dayList.size()-1).getCh()[6]);//Temperature Difference
			            monthHmData.setFlag(dayList.get(dayList.size()-1).getFlag());
			            monthList.add(monthHmData);
	            	}
	            }else{
	            	log.error("MBus Master/Slave Communication Error!");
	            }
                
                totalpos = (i+1)*lpLength;
            }
            
            if(currentList!=null)
            	currentData=currentList.toArray(new HMData[currentList.size()]);
            if(lpList!=null)
            	lpData=lpList.toArray(new HMData[lpList.size()]);
            if(dayList!=null)
            	dayData=dayList.toArray(new HMData[dayList.size()]);
            if(monthList!=null)
            	monthData=monthList.toArray(new HMData[monthList.size()]);
            
            startChar=baseRecord.getStart2();
            statusCode=baseRecord.getFixedDataHeader()!=null ? baseRecord.getFixedDataHeader().getStatusCode():1;
            meterId=baseRecord.getFixedDataHeader()!=null ? baseRecord.getFixedDataHeader().getIdentificationNumber():null;
            meterLog=baseRecord.getFixedDataHeader()!=null ? baseRecord.getFixedDataHeader().getStatusStr():null;
            portNumber=baseRecord.getAddress();

            log.debug("timeStamp: "+timestamp);
            log.debug("isCommError: "+isCommError);
            log.debug("LP length: "+(lpData!=null ? lpData.length:0));
            log.debug("CURRENT length: "+(currentData!=null ? currentData.length:0));
            log.debug("DAY length: "+(dayData!=null ? dayData.length:0));
            log.debug("MONTH length: "+(monthData!=null ? monthData.length:0));
        }
    }


    public static void main(String args[]){
    	Ultraheat mbus = new Ultraheat();
    	try {
    		//mbus.parse(Hex.encode("0107DA0A0668F8F86808087264170000A7320204189000000974040970040C06082204000C14251910000B2E0000000B3B0000000A5B26000A5F26000A620100FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF0C06082204000C14251910000B2E0000000B3B0000000A5B25000A5F26000A620100"),0);
    		//mbus.parse(Hex.encode("0107DA0A0768F4F46808087285000000A73204047B0C00000974040970040C06000000000C14000000000B2D0000000B3B0000000A5B00000A5F00004C140000FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF"),0);
    		//mbus.parse(Hex.encode("0107DA0A0768F4F46808087255030000A73204042E0C00000974040970040C06000000000C14000000000B2D0000000B3B0000000A5B00000A5F00004C140000FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF"),0);
    		mbus.parse(Hex.encode("0107DA0A0868F4F46808087285000000A7320404460C00000974040970040C06000000000C14000000000B2D0000000B3B0000000A5B00000A5F00004C1400000C06000000000C14000000000B2D0000000B3B0000000A5B00000A5F00004C140000FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF"));
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    /**
     * @return
     */
    public String getLPChannelMap(){
    	String res ="";
    	if(baseRecord == null) {
    		res+="ch1=Heat Power,";
    		res+="ch2=Heat Quantity,";
    		res+="ch3=Volume,";
            res+="ch4=Flow Temperature,";
            res+="ch5=Flowrate,";
            res+="ch6=Return Temperature,";
            res+="ch7=Temperature Difference";
    	} else {
    		res+="ch1=Heat Power["+baseRecord.getDataBlocks().getDataBlock()[4].getDataUnit()+"],";
    		res+="ch2=Heat Quantity["+baseRecord.getDataBlocks().getDataBlock()[2].getDataUnit()+"],";
    		res+="ch3=Volume["+baseRecord.getDataBlocks().getDataBlock()[3].getDataUnit()+"],";
            res+="ch4=Flow Temperature["+baseRecord.getDataBlocks().getDataBlock()[6].getDataUnit()+"],";
            res+="ch5=Flowrate["+baseRecord.getDataBlocks().getDataBlock()[5].getDataUnit()+"],";
            res+="ch6=Return Temperature["+baseRecord.getDataBlocks().getDataBlock()[7].getDataUnit()+"],";
            if(baseRecord.getDataBlocks().getDataBlock()[8].isEmpty()) {
            	res+="ch7=Temperature Difference[K]";
            } else {
            	res+="ch7=Temperature Difference["+baseRecord.getDataBlocks().getDataBlock()[8].getDataUnit()+"]";
            }
    	}

        return res;
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
	    final String TAB = "\n";

	    StringBuffer retValue = new StringBuffer();

	    retValue.append(this.getClass().getSimpleName()+" ( ")
	        .append(super.toString()).append(TAB)
	        .append("LPYEAR = ").append(this.LPYEAR).append(TAB)
	        .append("LPMONTH = ").append(this.LPMONTH).append(TAB)
	        .append("LPDAY = ").append(this.LPDAY).append(TAB)
	        .append("BASEPULSE = ").append(this.BASEPULSE).append(TAB)
	        .append("LP = ").append(this.LP).append(TAB)
	        .append("ERRORCODE = ").append(this.ERRORCODE).append(TAB)
	        .append("timestamp = ").append(this.timestamp).append(TAB)
	        .append("rawData = ").append(this.rawData).append(TAB)
	        .append("lp = ").append(this.lp).append(TAB)
	        .append("lpValue = ").append(this.lpValue).append(TAB)
	        .append("flag = ").append(this.flag).append(TAB)
	        .append("meterId = ").append(this.meterId).append(TAB)
	        .append("period = ").append(this.period).append(TAB)
	        .append("errorCode = ").append(this.errorCode).append(TAB)
	        .append("currentData = ").append(this.currentData).append(TAB)
	        .append("dayData = ").append(this.dayData).append(TAB)
	        .append("monthData = ").append(this.monthData).append(TAB)
	        .append("baseRecord = ").append(this.baseRecord).append(TAB)
	        .append(" )");

	    return retValue.toString();
	}
}