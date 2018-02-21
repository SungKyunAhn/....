package com.aimir.fep.meter.parser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.util.DateTimeUtil;

/**
 * Multi-Channel LP 검침 데이터 형식 - IF4 Protocol Specification 2.4.2.2 Multi-Channel LP 참조
 * SX2 계량기부터 적용
 * 
 * @author elevas
 * @since 2012.10.12
 */
public class MultiChannelParser extends MeterDataParser {
    private static Log log = LogFactory.getLog(MultiChannelParser.class);
    
    protected byte[] rawData = null;
    protected byte[] meterData = null;
    protected Double meteringValue = null;
    protected MeteringInfo meteringInfo = null;
    protected ChannelInfo[] channelInfos = null;
    protected LPData[] lpData = null;
    protected MeterData[] meterDatas = null;
    
    @Override
    public byte[] getRawData() {
        return rawData;
    }

    @Override
    public int getLength() {
        if(rawData == null) {
            return 0;
        }

        return rawData.length;
    }

    @Override
    public void parse(byte[] data) throws Exception {
        int pos = 0;
        
        // 미터 정보 파싱
        meteringInfo = new MeteringInfo();
        pos = meteringInfo.parse(data, pos);
        
        // 채널 정보 파싱
        channelInfos = new ChannelInfo[meteringInfo.getChCount()];
        // 미터 정보의 채널 개수만큼 생성한다.
        for (int i = 0; i < channelInfos.length; i++) {
            channelInfos[i] = new ChannelInfo();
            pos = channelInfos[i].parse(data, pos, meteringInfo.isBigEndian());
        }
        
        // 현재 검침값에 대한 채널별 저장이 안되어 있어서 activeEnergy, 사용량에 대해서만 저장한다.
        meteringValue = channelInfos[0].getCurrentValue();
        
        // LP 데이타 생성
        lpData = new LPData[meteringInfo.getLpCount()];
        for (int i = 0; i < lpData.length; i++) {
            lpData[i] = new LPData();
            pos = lpData[i].parse(data, pos, meteringInfo.isBigEndian(),
                    meteringInfo.getChCount(), meteringInfo.getPeriod(), 
                    channelInfos);
        }
        
        // 미터별로 다른 정보이므로 이 파서를 상속받아서 처리해야 한다.
        meterData = new byte[data.length - pos];
        System.arraycopy(data, pos, meterData, 0, meterData.length);
        log.debug("METER_DATA[" + Hex.decode(meterData) + "]");
        List<MeterData> mdList = new ArrayList<MeterData>();
        while (pos + 1 < data.length) {
            MeterData md = new MeterData();
            pos = md.parse(data, pos);
            mdList.add(md);
        }
        meterDatas = mdList.toArray(new MeterData[0]);
    }

    @Override
    public Double getMeteringValue() {
        return meteringValue;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getFlag() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setFlag(int flag) {
        // TODO Auto-generated method stub
    }

    public int getPeriod() {
        return meteringInfo.getPeriod();
    }
    
    public String getTimestamp() {
        return meteringInfo.getTimestamp();
    }
    
    public LPData[] getLpData() {
        return lpData;
    }
    
    public MeterData[] getMeterDatas() {
        return meterDatas;
    }
    
    public class MeteringInfo implements Serializable {
        /*
         * Data Format
         */
        private byte[] DF = new byte[1];
        /*
         * GMT timestamp
         */
        private byte[] GT = new byte[11];
        /*
         * LP period
         */
        private byte[] Period = new byte[1];
        /*
         * little endian
         */
        private byte[] CNT = new byte[2];
        
        private boolean bigEndian = false;
        private int chCount = 0;
        private String timestamp = "";
        private int period = 0; // 0 : Metering Fail
        private int lpCount = 0; 
        
        public int parse(byte[] data, int pos) {
            System.arraycopy(data, pos, DF, 0, DF.length);
            pos += DF.length;
            
            // 비트 처리
            // 0~5 : channel count, 7 : big endian
            int df = DataUtil.getIntToBytes(DF);
            bigEndian = (df & 0x80) != 0; // 7bit mask check 1000 0000
            chCount = df & 0x3F;          // 7bit unmask 0011 1111
            log.debug("DF[" + Hex.decode(DF)+"] BIG_ENDIAN[" + bigEndian + "] CH_COUNT[" +  chCount + "]");
            
            System.arraycopy(data, pos, GT, 0, GT.length);
            pos += GT.length;
            int year = DataUtil.getIntTo2Byte(new byte[] {GT[5], GT[4]}); // little -> big endian
            int month = DataUtil.getIntToByte(GT[6]);
            int day = DataUtil.getIntToByte(GT[7]);
            int hour = DataUtil.getIntToByte(GT[8]);
            int min = DataUtil.getIntToByte(GT[9]);
            int sec = DataUtil.getIntToByte(GT[10]);
            timestamp = String.format("%04d%02d%02d%02d%02d%02d", year, month, day, hour, min, sec);
            log.debug("GT[" + Hex.decode(GT)+"] TIMESTAMP[" + timestamp + "]");
            
            System.arraycopy(data, pos, Period, 0, Period.length);
            pos += Period.length;
            period = DataUtil.getIntToBytes(Period);
            log.debug("Period[" + Hex.decode(Period)+"] PERIOD[" + period + "]");
            
            System.arraycopy(data, pos, CNT, 0, CNT.length);
            pos += CNT.length;
            DataUtil.convertEndian(CNT);
            lpCount = DataUtil.getIntTo2Byte(CNT);
            log.debug("BIG_ENDIAN_CNT[" + Hex.decode(CNT)+"] LP_CNT[" + lpCount + "]");
            
            return pos;
        }
        
        public boolean isBigEndian() {
            return this.bigEndian;
        }
        
        public int getChCount() {
            return chCount;
        }
        
        public String getTimestamp() {
            return timestamp;
        }
        
        public int getPeriod() {
            return period;
        }
        
        public int getLpCount() {
            return lpCount;
        }
    }
    
    public class ChannelInfo implements Serializable {
        
        /*
         * 2.4.3 Service Type R Metering Format에 정의된 Channel Type 참고
         * Channel Type
         */
        private byte[] CT = new byte[1];
        /*
         * 2012.10.12 지원하고 있지 않음.
         * 단위
         */
        private byte[] UNIT = new byte[1];
        /*
         * little endian
         * Meter Constant
         */
        private byte[] MC = new byte[2];
        /*
         * DF big endian 여부
         * Current Value
         */
        private byte[] CV = new byte[4];
        
        private int channelType = 0;
        private int meterConstant = 0;
        private double currentValue = 0.0;
        
        public int parse(byte[] data, int pos, boolean isBigEndian) {
            System.arraycopy(data, pos, CT, 0, CT.length);
            pos += CT.length;
            channelType = DataUtil.getIntToBytes(CT);
            log.debug("CT[" + Hex.decode(CT) + "] ChannelType[" + channelType + "]");
            
            System.arraycopy(data, pos, UNIT, 0, UNIT.length);
            pos += UNIT.length;
            log.debug("UNIT[" + Hex.decode(UNIT) + "] not supported");
            
            System.arraycopy(data, pos, MC, 0, MC.length);
            pos += MC.length;
            DataUtil.convertEndian(MC);
            meterConstant = DataUtil.getIntTo2Byte(MC);
            log.debug("MC[" + Hex.decode(MC) + "] MeterConstant[" + meterConstant + "]");
            
            System.arraycopy(data, pos, CV, 0, CV.length);
            pos += CV.length;
            DataUtil.convertEndian(!isBigEndian, CV);
            currentValue = (double)DataUtil.getIntTo4Byte(CV) / (double)meterConstant;
            log.debug("CV[" + Hex.decode(CV) + "] CurrentValue[" + currentValue + "]");
            
            return pos;
        }
        
        public int getChannelType() {
            return channelType;
        }
        
        public int getMeterConstant() {
            return meterConstant;
        }
        
        public double getCurrentValue() {
            return currentValue;
        }
        
        public void setCurrentValue(double currentValue) {
            this.currentValue = currentValue;
        }
    }
    
    public class LPData implements Serializable {
        /*
         * LP Timestamp
         * DF의 big endian 여부
         */
        private byte[] LT = new byte[4];
        /*
         * Base Pulse
         * DF의 big endian 여부
         */
        private byte[] BP = new byte[4];
        /*
         * Load Profile - WORD Type (2bytes)
         * DF의 big endian 여부
         */
        private byte[] LP = new byte[2];
        
        private String lpDate;
        private double[] basePulse;
        private double[][] lp;
        
        public int parse(byte[] data, int pos, boolean isBigEndian, int chCount, int lpPeriod, ChannelInfo[] channelInfos) {
            System.arraycopy(data, pos, LT, 0, LT.length);
            pos += LT.length;
            DataUtil.convertEndian(!isBigEndian, LT);
            lpDate = String.format("%04d%02d%02d", 
                    DataUtil.getIntTo2Byte(new byte[]{LT[0], LT[1]}),
                    DataUtil.getIntToBytes(new byte[]{LT[2]}),
                            DataUtil.getIntToBytes(new byte[]{LT[3]}));
            log.debug("LT[" + Hex.decode(LT) + "] LP_DATE[" + lpDate + "]");
            
            int lpInterval = 60 / lpPeriod;
            // 한 주기 만큼 뺀 시간을 가져온다. 당일 검침데이타는 한 주기 전 시간까지의 데이타만 사용한다.
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, -1*lpInterval);
            String yyyymmddhhmm = DateTimeUtil.getDateString(cal.getTime()).substring(0, 12);
            String lpdatetime = null;
            
            basePulse = new double[chCount];
            for (int i = 0; i < chCount; i++) {
                System.arraycopy(data, pos, BP, 0, BP.length);
                pos += BP.length;
                DataUtil.convertEndian(!isBigEndian, BP);
                basePulse[i] = (double)DataUtil.getIntTo4Byte(BP) / (double)channelInfos[i].getMeterConstant();
                log.debug("CH[" + i + "] BASEPULSE[" + basePulse[i] + "]");
            }
            
            // 채널별 데이타를 넣는다.
            double _lp[] = new double[chCount];
            // 채널별 데이타를 시간순서대로 넣는다.
            List<double[]> lpList = new ArrayList<double[]>();
            for (int h=0, m=0; ;m+=lpInterval) {
                _lp = new double[chCount];
                if (m >= 60) {
                    h++;
                    m = 0;
                }
                
                if (h == 24) break;
                
                lpdatetime = lpDate + String.format("%02d%02d", h, m);
                if (lpdatetime.compareTo(yyyymmddhhmm) < 0) {
                    for (int i = 0; i < chCount; i++) {
                        System.arraycopy(data, pos, LP, 0, LP.length);
                        pos += LP.length;
                        _lp[i] = (double)DataUtil.getIntTo2Byte(LP);
                        if (_lp[i] != 65535) {
                            _lp[i] /= (double)channelInfos[i].getMeterConstant();
                        }
                        log.debug("LP_TIME[" + lpdatetime + "] CH["+i+"]"+_lp[i]);
                    }
                    lpList.add(_lp);
                }
                else {
                    // 채널 개수만큼 위치 이동
                    for (int i = 0; i < chCount; i++) pos += LP.length;
                }
            }
            
            lp = new double[chCount][lpList.size()];
            for (int i = 0; i < lpList.size(); i++) {
                _lp = lpList.get(i);
                for (int ch = 0; ch < chCount; ch++) {
                    lp[ch][i] = _lp[ch];
                    log.debug("CH[" + ch + "] #["+i+"] VALUE[" + lp[ch][i]+"]");
                }
            }
            
            return pos;
        }
        
        public double[] getBasePulse() {
            return basePulse;
        }
        
        public double[][] getLp() {
            return lp;
        }
        
        public String getLpDate() {
            return lpDate;
        }
        
        public void setBasePulse(int ch, double basePulse) {
            this.basePulse[ch] = basePulse;
        }
        
        public void setLp(int ch, int i, double lp) {
            this.lp[ch][i] = lp;
        }
    }

    @Override
    public LinkedHashMap<?, ?> getData() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public class MeterData implements Serializable {
        /**
         * SX2 Meter Value Length
         * ID + VALUE
         */
        private byte[] LEN = new byte[1];
        
        /**
         * SX2 ID
         * D9 : Reverse current log packet
         * DA : Terminal cover log packet
         * DB : Earth load log packet
         * DC : Bypass mainline log packet
         * DD : Overload current log packet
         * E8 : Front cover log packet
         * E7 : Tamper code display status (6 bytes)
         *      x(first) = 'F'
         *      x(otehr) = 0, 1
         */
        private byte[] ID = new byte[1];
        
        /**
         * Meter Value (ASCII 0-9, A-F)
         */
        private byte[] VALUE = null;
        
        public int parse(byte[] data, int pos) {
            System.arraycopy(data, pos, LEN, 0, LEN.length);
            int len = DataUtil.getIntToBytes(LEN);
            pos += LEN.length;
            
            System.arraycopy(data, pos, ID, 0, ID.length);
            pos += ID.length;
            
            VALUE = new byte[len-ID.length];
            System.arraycopy(data, pos, VALUE, 0, VALUE.length);
            pos += VALUE.length;
            
            log.debug("ID[" + Hex.decode(ID) + "] VALUE[" + Hex.decode(VALUE) + "]");
            
            return pos;
        }
        
        public byte[] getId() {
            return ID;
        }
        
        public byte[] getValue() {
            return VALUE;
        }
    }

}
