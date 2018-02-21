package com.aimir.fep.modem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;

import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.util.Mask;

/**
 * Sensor AmrData 
 * EX1) new SensorAmrData((byte[]), getTimeVal(), getCurrentPulseVal()....
 * EX2) new SensorAmrData() setMeteringDay(byte[])... getSetAmrDataStream(byte)
 * @author Administrator
 *
 */
public class ModemAmrData implements java.io.Serializable
{
	private static final long serialVersionUID = -6152453488195402468L;
	// SetAmrStream Header
    private int amrMask = 0x00;
    private byte[] amrData;
    
    // AmrData
    private byte[] time = new byte[11]; 
    private byte[] timeZone = new byte[2];
    private byte[] dst = new byte[2];
    private byte[] currentPulse = new byte[4];
    private byte[] lpPeriod = new byte[1];
    private byte[] operatingDay = new byte[2];
    private byte[] activeMin = new byte[2];
    private byte[] reset = new byte[1];
    private byte[] meteringDay = new byte[4];
    private byte[] meteringHour = new byte[12];
    private byte[] repeatingDay = new byte[4];
    private byte[] repeatingHour = new byte[12];
    private byte[] repeatingSetupSec = new byte[2];
    private byte[] lastPulse = new byte[4];
    private byte[] lpChoice = new byte[1];
    private byte[] alarmFlag = new byte[1];

    // AmrData parsing Data
    private String timeVal;
    private int timeZoneVal;
    private int dstVal;
    private int currentPulseVal;
    private int lpPeriodVal;
    private int operatingDayVal;
    private int activeMinVal;
    private int resetVal;
    private String meteringDayVal;
    private String meteringHourVal;
    private String repeatingDayVal;
    private String repeatingHourVal;
    private int repeatingSetupSecVal;
    private int lastPulseVal;   
    private int lpChoiceVal;
    private int alarmFlagVal;
    
    private static int LEN_TIME = 11;
    private static int LEN_TIME_TIMEZONE = 2;
    private static int LEN_TIME_DST = 2;
    private static int LEN_TIME_YYYY = 2;
    private static int LEN_TIME_MM = 1;
    private static int LEN_TIME_DD = 1;
    private static int LEN_TIME_HOUR = 1;
    private static int LEN_TIME_MINUTE = 1;
    private static int LEN_TIME_SECOND = 1;
    
    private static int LEN_CURRENT_PULSE = 4;
    private static int LEN_LP_PERIOD = 1;
    private static int LEN_OPERATING_DAY = 2;
    private static int LEN_ACTIVE_MIN = 2;
    private static int LEN_RESET = 1;
    private static int LEN_RESERVED = 1;
    private static int LEN_METERING_DAY = 4;
    private static int LEN_METERING_HOUR = 12;
    private static int LEN_REPEATING_DAY = 4;
    private static int LEN_REPEATING_HOUR = 12;
    private static int LEN_REPEATING_SETUP_SEC = 2;
    private static int LEN_LAST_PULSE = 4;
    private static int LEN_LP_CHOICE = 1;
    private static int LEN_ALARM_FLAG = 1;
    private static byte[] BLANK = {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
    
    
    public String toString()
    { 
        StringBuffer sb = new StringBuffer();
        sb.append("SensorAmrData { \n")
          .append("    time:").append(timeVal).append('\n')
          .append("    timeZone:").append(timeZoneVal).append('\n')
          .append("    dst:").append(dstVal).append('\n')
          .append("    currentPulse:").append(currentPulseVal).append('\n')
          .append("    lpPeriod:").append(lpPeriodVal).append('\n')
          .append("    operatingDay:").append(operatingDayVal).append('\n')
          .append("    activeMin:").append(activeMinVal).append('\n')
          .append("    reset:").append(resetVal).append('\n')
          .append("    meteringDay:").append(meteringDayVal).append('\n')
          .append("    meteringHour:").append(meteringHourVal).append('\n')
          .append("    repeatingDay:").append(repeatingDayVal).append('\n')
          .append("    repeatingHour:").append(repeatingHourVal).append('\n')
          .append("    repeatingSetupSec:").append(repeatingSetupSecVal).append('\n')
          .append("    lastPulse:").append(lastPulseVal).append('\n')
          .append("    lpChoice:").append(lpChoice).append('\n')
          .append("    alarmFlag:").append(alarmFlagVal).append('\n')
          .append("}");
        
        return sb.toString();
    }
    public ModemAmrData(){
    }
    public ModemAmrData(byte[] amrData)
    {
        this.amrData = amrData;
        int pos = 0;
        System.arraycopy(amrData, pos, time, 0, LEN_TIME);
        pos += LEN_TIME;
        System.arraycopy(amrData, pos, currentPulse, 0, LEN_CURRENT_PULSE);
        pos += LEN_CURRENT_PULSE;
        System.arraycopy(amrData, pos, lpPeriod, 0, LEN_LP_PERIOD);
        pos += LEN_LP_PERIOD;
        System.arraycopy(amrData, pos, operatingDay, 0, LEN_OPERATING_DAY);
        pos += LEN_OPERATING_DAY;
        System.arraycopy(amrData, pos, activeMin, 0, LEN_ACTIVE_MIN);
        pos += LEN_ACTIVE_MIN;
        System.arraycopy(amrData, pos, reset, 0, LEN_RESET);
        pos += LEN_RESET + LEN_RESERVED;
        System.arraycopy(amrData, pos, meteringDay, 0, LEN_METERING_DAY);
        pos += LEN_METERING_DAY;
        System.arraycopy(amrData, pos, meteringHour, 0, LEN_METERING_HOUR);
        pos += LEN_METERING_HOUR;
        System.arraycopy(amrData, pos, repeatingDay, 0, LEN_REPEATING_DAY);
        pos += LEN_REPEATING_DAY;
        System.arraycopy(amrData, pos, repeatingHour, 0, LEN_REPEATING_HOUR);
        pos += LEN_REPEATING_HOUR;
        System.arraycopy(amrData, pos, repeatingSetupSec, 0, LEN_REPEATING_SETUP_SEC);
        pos += LEN_REPEATING_SETUP_SEC;
        System.arraycopy(amrData, pos, lastPulse, 0, LEN_LAST_PULSE);
        pos += LEN_LAST_PULSE;
        System.arraycopy(amrData, pos, lpChoice, 0, LEN_LP_CHOICE);
        pos += LEN_LP_CHOICE;
        System.arraycopy(amrData, pos, alarmFlag, 0, LEN_ALARM_FLAG);
        parseData();
    }

    private void parseData(){
        int pos = 0;

        System.arraycopy(amrData, pos, timeZone, 0, LEN_TIME_TIMEZONE);
        pos += LEN_TIME_TIMEZONE;
        timeZoneVal = DataUtil.getIntTo2Byte(timeZone);
        
        System.arraycopy(amrData, pos, dst,0, LEN_TIME_DST);
        pos += LEN_TIME_DST;
        dstVal = DataUtil.getIntTo2Byte(dst);

        byte[] YEAR = new byte[2];
        System.arraycopy(amrData, pos, YEAR, 0, LEN_TIME_YYYY);
        pos += LEN_TIME_YYYY;
        int year = DataUtil.getIntTo2Byte(YEAR);
        
        byte[] MONTH = new byte[1];
        System.arraycopy(amrData, pos, MONTH, 0, LEN_TIME_MM);
        pos += LEN_TIME_MM;
        int month = DataUtil.getIntToBytes(MONTH);
        
        byte[] DAY = new byte[1];
        System.arraycopy(amrData, pos, DAY, 0, LEN_TIME_DD);
        pos += LEN_TIME_DD;
        int day = DataUtil.getIntToBytes(DAY);
        
        byte[] HOUR = new byte[1];
        System.arraycopy(amrData, pos, HOUR, 0, LEN_TIME_HOUR);
        pos += LEN_TIME_HOUR;
        int hour = DataUtil.getIntToBytes(HOUR);
        
        byte[] MINUTE = new byte[1];
        System.arraycopy(amrData, pos, MINUTE, 0, LEN_TIME_MINUTE);
        pos += LEN_TIME_MINUTE;
        int minute = DataUtil.getIntToBytes(MINUTE);
        
        byte[] SECOND = new byte[1];
        System.arraycopy(amrData, pos, SECOND, 0, LEN_TIME_SECOND);
        pos += LEN_TIME_SECOND;
        int second = DataUtil.getIntToBytes(SECOND);
        
        timeVal = Integer.toString(year)
                  + (month < 10 ? "0" + month : "" + month)
                  + (day < 10 ? "0" + day : "" + day)
                  + (hour < 10 ? "0" + hour : "" + hour)
                  + (minute < 10 ? "0" + minute : "" + minute)
                  + (second < 10 ? "0" + second : "" + second);

        currentPulseVal = DataUtil.getIntTo4Byte(currentPulse);
        lpPeriodVal = DataUtil.getIntToByte(lpPeriod[0]);
        operatingDayVal = DataUtil.getIntTo2Byte(operatingDay);
        activeMinVal = DataUtil.getIntTo2Byte(activeMin);
        resetVal = DataUtil.getIntToByte(reset[0]);
        BigInteger b = new BigInteger(Hex.decode(meteringDay),16);
        meteringDayVal = b.toString(10);
        b = new BigInteger(Hex.decode(meteringHour),16);
        meteringHourVal = b.toString(10);
        b = new BigInteger(Hex.decode(repeatingDay),16);
        repeatingDayVal = b.toString(10);
        b = new BigInteger(Hex.decode(repeatingHour),16);
        repeatingHourVal = b.toString(10);
        repeatingSetupSecVal = DataUtil.getIntTo2Byte(repeatingSetupSec);
        lastPulseVal = DataUtil.getIntTo4Byte(lastPulse); 
        lpChoiceVal = DataUtil.getIntToByte(lpChoice[0]);
        alarmFlagVal = DataUtil.getIntToByte(alarmFlag[0]);
    }

    public byte[] getSetAmrDataStream() throws IOException 
    {
        ByteArrayOutputStream byteOut = null;
        byte[] rtnVal = null;
        try
        {
            byteOut = new ByteArrayOutputStream();
            byteOut.write(time);
            byteOut.write(currentPulse);
            byteOut.write(lpPeriod);
            byteOut.write(operatingDay);
            byteOut.write(activeMin);
            byteOut.write(reset);
            byteOut.write(getByte(BLANK,LEN_RESERVED));
            byteOut.write(meteringDay);
            byteOut.write(meteringHour);
            byteOut.write(repeatingDay);
            byteOut.write(repeatingHour);
            byteOut.write(repeatingSetupSec);
            byteOut.write(lastPulse);
            byteOut.write(lpChoice);
            byteOut.write(alarmFlag);
            
            rtnVal = byteOut.toByteArray();
        }catch(IOException i)
        {
            throw i;
        }finally{
            if(byteOut != null)
                try { byteOut.close(); } catch (IOException e) { }
        }
        return rtnVal;

    }
    
    public byte[] getByte(byte[] a, int length)
    {
        byte[] temp = new byte[length];
        System.arraycopy(a, 0, temp, 0, length);
        return temp;
    }

    /**
     * @return the time
     */
    public byte[] getTime()
    {
        return time;
    }

    /**
     * @return the currentPulse
     */
    public byte[] getCurrentPulse()
    {
        return currentPulse;
    }

    /**
     * @return the lpPeriod
     */
    public byte[] getLpPeriod()
    {
        return lpPeriod;
    }

    /**
     * @return the operatingDay
     */
    public byte[] getOperatingDay()
    {
        return operatingDay;
    }

    /**
     * @return the activeMin
     */
    public byte[] getActiveMin()
    {
        return activeMin;
    }

    /**
     * @return the reset
     */
    public byte[] getReset()
    {
        return reset;
    }

    /**
     * @return the meteringDay
     */
    public byte[] getMeteringDay()
    {
        return meteringDay;
    }

    /**
     * @return the meteringHour
     */
    public byte[] getMeteringHour()
    {
        return meteringHour;
    }

    /**
     * @return the repeatingDay
     */
    public byte[] getRepeatingDay()
    {
        return repeatingDay;
    }

    /**
     * @return the repeatingHour
     */
    public byte[] getRepeatingHour()
    {
        return repeatingHour;
    }

    /**
     * @return the repeatingSetupSec
     */
    public byte[] getRepeatingSetupSec()
    {
        return repeatingSetupSec;
    }

    /**
     * @return the lastPulse
     */
    public byte[] getLastPulse()
    {
        return lastPulse;
    }

    /**
     * @param time the time to set
     */
    public void setTime(byte[] time)
    {
        Mask mask = new Mask(amrMask);
        mask.setBit(15);
        amrMask = mask.getMask();
        
        this.time = time;
    }

    /**
     * @param currentPulse the currentPulse to set
     */
    public void setCurrentPulse(byte[] currentPulse)
    {
        Mask mask = new Mask(amrMask);
        if (currentPulse != null)
            mask.setBit(14);
        amrMask = mask.getMask();

        this.currentPulse = currentPulse;
    }

    /**
     * @param lpPeriod the lpPeriod to set
     */
    public void setLpPeriod(byte[] lpPeriod)
    {
        Mask mask = new Mask(amrMask);
        if (lpPeriod != null)
            mask.setBit(13);
        amrMask = mask.getMask();

        this.lpPeriod = lpPeriod;
    }

    /**
     * @param operatingDay the operatingDay to set
     */
    public void setOperatingDay(byte[] operatingDay)
    {
        Mask mask = new Mask(amrMask);
        if (operatingDay != null)
            mask.setBit(12);
        amrMask = mask.getMask();

        this.operatingDay = operatingDay;
    }

    /**
     * @param activeMin the activeMin to set
     */
    public void setActiveMin(byte[] activeMin)
    {
        Mask mask = new Mask(amrMask);
        if (activeMin != null)
            mask.setBit(11);
        amrMask = mask.getMask();

        this.activeMin = activeMin;
    }

    /**
     * @param reset the reset to set
     */
    public void setReset(byte[] reset)
    {
        Mask mask = new Mask(amrMask);
        if (reset != null)
            mask.setBit(10);
        amrMask = mask.getMask();

        this.reset = reset;
    }

    /**
     * @param meteringDay the meteringDay to set
     */
    public void setMeteringDay(byte[] meteringDay)
    {
        Mask mask = new Mask(amrMask);
        if (meteringDay != null)
            mask.setBit(8);
        amrMask = mask.getMask();

        this.meteringDay = meteringDay;
    }

    /**
     * @param meteringHour the meteringHour to set
     */
    public void setMeteringHour(byte[] meteringHour)
    {
        Mask mask = new Mask(amrMask);
        if (meteringHour != null)
            mask.setBit(7);
        amrMask = mask.getMask();

        this.meteringHour = meteringHour;
    }

    /**
     * @param repeatingDay the repeatingDay to set
     */
    public void setRepeatingDay(byte[] repeatingDay)
    {
        Mask mask = new Mask(amrMask);
        if (repeatingDay != null)
            mask.setBit(6);
        amrMask = mask.getMask();

        this.repeatingDay = repeatingDay;
    }

    /**
     * @param repeatingHour the repeatingHour to set
     */
    public void setRepeatingHour(byte[] repeatingHour)
    {
        Mask mask = new Mask(amrMask);
        if (repeatingHour != null)
            mask.setBit(5);
        amrMask = mask.getMask();
        
        this.repeatingHour = repeatingHour;
    }

    /**
     * @param repeatingSetupSec the repeatingSetupSec to set
     */
    public void setRepeatingSetupSec(byte[] repeatingSetupSec)
    {
        Mask mask = new Mask(amrMask);
        if (repeatingSetupSec != null)
            mask.setBit(4);
        amrMask = mask.getMask();

        this.repeatingSetupSec = repeatingSetupSec;
    }

    /**
     * @param lastPulse the lastPulse to set
     */
    public void setLastPulse(byte[] lastPulse)
    {
        Mask mask = new Mask(amrMask);
        if (lastPulse != null)
            mask.setBit(3);
        amrMask = mask.getMask();

        this.lastPulse = lastPulse;
    }

    /**
     * @return the amrMask
     */
    public int getAmrMask()
    {
        return amrMask;
    }
    public byte[] getAmrMaskStream()
    {
        return DataUtil.get2ByteToInt(amrMask);
    }
    public byte[] getTimeZone()
    {
        return timeZone;
    }
    public String getTimeVal()
    {
        return timeVal;
    }
    public int getTimeZoneVal()
    {
        return timeZoneVal;
    }
    public int getDstVal()
    {
        return dstVal;
    }
    public int getCurrentPulseVal()
    {
        return currentPulseVal;
    }
    public int getLpPeriodVal()
    {
        return lpPeriodVal;
    }
    public int getOperatingDayVal()
    {
        return operatingDayVal;
    }
    public int getActiveMinVal()
    {
        return activeMinVal;
    }
    public int getResetVal()
    {
        return resetVal;
    }
    public String getMeteringDayVal()
    {
        return meteringDayVal;
    }
    public String getMeteringHourVal()
    {
        return meteringHourVal;
    }
    public String getRepeatingDayVal()
    {
        return repeatingDayVal;
    }
    public String getRepeatingHourVal()
    {
        return repeatingHourVal;
    }
    public int getRepeatingSetupSecVal()
    {
        return repeatingSetupSecVal;
    }
    public int getLastPulseVal()
    {
        return lastPulseVal;
    }
    public void setAmrData(byte[] amrData)
    {
        this.amrData = amrData;
    }
    public byte[] getLpChoice()
    {
        return lpChoice;
    }
    public int getLpChoiceVal()
    {
        return lpChoiceVal;
    }
    public void setLpChoice(byte[] lpChoice)
    {
        Mask mask = new Mask(amrMask);
        if (lpChoice != null)
            mask.setBit(2);
        amrMask = mask.getMask();
        
        this.lpChoice = lpChoice;
    }
    public void setAlarmFlag(byte[] alarmFlag)
    {
        Mask mask = new Mask(amrMask);
        if (alarmFlag != null)
            mask.setBit(1);
        amrMask = mask.getMask();

        this.alarmFlag = alarmFlag;
    }
    public int getAlarmFlagVal()
    {
        return alarmFlagVal;
    }
}
