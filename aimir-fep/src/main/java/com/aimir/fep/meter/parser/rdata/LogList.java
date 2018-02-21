package com.aimir.fep.meter.parser.rdata;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.parser.rdata.RDataConstant.MeteringCategoryType;
import com.aimir.fep.util.DataUtil;

/**
 * Log List
 * 
 * @author kaze
 * 
 */
public class LogList {
    private static Log log = LogFactory.getLog(LogList.class);
    private int pos;
    private byte[] LOGCATEGORYPAYLOAD;
    private byte[] CATEGORY = new byte[1];
    private byte[] LOGLENGTH = new byte[1];
    private byte[] LOGTIME = new byte[5];
    private byte[] LOGVALUE;

    private MeteringCategoryType category;
    private int logLength;
    private String logTime;
        
    /**
     * 
     * @param pos - 각각의 LogCategory가 시작하는 인덱스
     * @param payLoad - LogList 전체 데이터의 Raw Data
     */
    public LogList(int pos, byte[] payLoad) {
        super();
        this.pos = pos;
        LOGCATEGORYPAYLOAD = payLoad;
    }

    public int parsingPayLoad() throws Exception {      
        System.arraycopy(LOGCATEGORYPAYLOAD, pos, CATEGORY, 0, CATEGORY.length);
        pos += CATEGORY.length;     
        category = RDataConstant.getMeteringCategoryType(DataUtil.getIntToBytes(CATEGORY));
        System.arraycopy(LOGCATEGORYPAYLOAD, pos, LOGLENGTH, 0, LOGLENGTH.length);
        pos += LOGLENGTH.length;
        logLength = DataUtil.getIntToBytes(LOGLENGTH);
        System.arraycopy(LOGCATEGORYPAYLOAD, pos, LOGTIME, 0, LOGTIME.length);
        pos += LOGTIME.length;
        logTime = DataUtil.getTimeStamp5(LOGTIME);
        
        LOGVALUE = new byte[category.getLength()];
        System.arraycopy(LOGCATEGORYPAYLOAD, pos, LOGVALUE, 0, LOGVALUE.length);
        pos += LOGVALUE.length;
        return pos;
    }

    /**
     * @return the log
     */
    public static Log getLog() {
        return log;
    }

    /**
     * @param log the log to set
     */
    public static void setLog(Log log) {
        LogList.log = log;
    }

    /**
     * @return the category
     */
    public MeteringCategoryType getCategory() {
        return category;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(MeteringCategoryType category) {
        this.category = category;
    }

    /**
     * @return the logLength
     */
    public int getLogLength() {
        return logLength;
    }

    /**
     * @param logLength the logLength to set
     */
    public void setLogLength(int logLength) {
        this.logLength = logLength;
    }

    /**
     * @return the logTime
     */
    public String getLogTime() {
        return logTime;
    }

    /**
     * @param logTime the logTime to set
     */
    public void setLogTime(String logTime) {
        this.logTime = logTime;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "LogList [category=" + category + ", logLength=" + logLength + ", logTime=" + logTime + "]";
    }   
    
}
