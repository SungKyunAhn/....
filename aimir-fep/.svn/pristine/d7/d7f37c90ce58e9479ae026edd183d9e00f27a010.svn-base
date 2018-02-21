package com.aimir.fep.meter.parser.rdata;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataUtil;

/**
 * MeterConfiguration Format
 * 
 * @author kaze
 * 
 */
public class MeteringDataRData extends RData {
    private static Log log = LogFactory.getLog(MeteringDataRData.class);
    private byte[] SHORTID = new byte[4];
    private byte[] CHANNELCOUNT = new byte[1];
    private byte[] BPCOUNT = new byte[1];
    
    private byte[] LPCOUNT = new byte[1];
    private byte[] LPTIME = new byte[5];
    private byte[] LPVALUE = new byte[4];
    private byte[] LOGCATEGORYCOUNT = new byte[1];
    private byte[] CATEGORY = new byte[1];
    private byte[] VALUELENGTH = new byte[1];
    private byte[] ENTRYCOUNT = new byte[1];
    private byte[] LOGTIME = new byte[5];
    private byte[] LOGVALUE;
    
    private int shortId;
    private int channelCount;
    private int bpCount;
    private List<BPList> bpLists = new ArrayList<BPList>();
    
    private int lpCount;
    private List<LPList> lpLists = new ArrayList<LPList>();
    private String lpTime;
    private Float lpValue;
    private int logCategoryCount;
    private List<LogList> logCategories = new ArrayList<LogList>();   
        
    public void parsingPayLoad() throws Exception {
        int pos = 0;

        System.arraycopy(payload, pos, SHORTID, 0, SHORTID.length);
        pos += SHORTID.length;
        shortId = DataUtil.getIntToBytes(SHORTID);
        log.debug("SHORT_ID["+shortId+"]");
        System.arraycopy(payload, pos, CHANNELCOUNT, 0, CHANNELCOUNT.length);
        pos += CHANNELCOUNT.length;
        channelCount = DataUtil.getIntToBytes(CHANNELCOUNT);
        log.debug("CH_COUNT[" + channelCount + "]");
        System.arraycopy(payload, pos, BPCOUNT, 0, BPCOUNT.length);
        pos += BPCOUNT.length;
        bpCount = DataUtil.getIntToBytes(BPCOUNT);
        log.debug("BP_COUNT[" + bpCount + "]");
        for(int i=0;i<bpCount;i++) {
            BPList bpList = new BPList(pos, payload);
            pos = bpList.parsingPayLoad(channelCount, false);
            bpLists.add(bpList);
        }       
        
        System.arraycopy(payload, pos, LPCOUNT, 0, LPCOUNT.length);
        pos += LPCOUNT.length;
        lpCount = DataUtil.getIntToBytes(LPCOUNT);
        log.debug("LP_COUNT[" + lpCount + "]");
        for(int i=0;i<lpCount;i++) {
            LPList lpList = new LPList(pos, payload);
            pos = lpList.parsingPayLoad(channelCount, false);
            lpLists.add(lpList);
        }
        
        System.arraycopy(payload, pos, LOGCATEGORYCOUNT, 0, LOGCATEGORYCOUNT.length);
        pos += LOGCATEGORYCOUNT.length;
        
        logCategoryCount = DataUtil.getIntToBytes(LOGCATEGORYCOUNT);
        log.debug("LOGCATEGORY_COUNT[" + logCategoryCount + "]");
        
        for(int i=0;i<logCategoryCount;i++) {
            LogList logList = new LogList(pos, payload);
            pos =logList.parsingPayLoad();
            logCategories.add(logList);
        }
    }

    /**
     * @return the shortId
     */
    public int getShortId() {
        return shortId;
    }

    /**
     * @param shortId the shortId to set
     */
    public void setShortId(int shortId) {
        this.shortId = shortId;
    }

    /**
     * @return the channelCount
     */
    public int getChannelCount() {
        return channelCount;
    }

    /**
     * @param channelCount the channelCount to set
     */
    public void setChannelCount(int channelCount) {
        this.channelCount = channelCount;
    }

    /**
     * @return the bpCount
     */
    public int getBpCount() {
        return bpCount;
    }

    /**
     * @param bpCount the bpCount to set
     */
    public void setBpCount(int bpCount) {
        this.bpCount = bpCount;
    }

    /**
     * @return the bpLists
     */
    public List<BPList> getBpLists() {
        return bpLists;
    }

    /**
     * @param bpLists the bpLists to set
     */
    public void setBpLists(List<BPList> bpLists) {
        this.bpLists = bpLists;
    }

    /**
     * @return the lpCount
     */
    public int getLpCount() {
        return lpCount;
    }

    /**
     * @param lpCount the lpCount to set
     */
    public void setLpCount(int lpCount) {
        this.lpCount = lpCount;
    }

    /**
     * @return the lpLists
     */
    public List<LPList> getLpLists() {
        return lpLists;
    }

    /**
     * @param lpLists the lpLists to set
     */
    public void setLpLists(List<LPList> lpLists) {
        this.lpLists = lpLists;
    }

    /**
     * @return the lpTime
     */
    public String getLpTime() {
        return lpTime;
    }

    /**
     * @param lpTime the lpTime to set
     */
    public void setLpTime(String lpTime) {
        this.lpTime = lpTime;
    }

    /**
     * @return the lpValue
     */
    public Float getLpValue() {
        return lpValue;
    }

    /**
     * @param lpValue the lpValue to set
     */
    public void setLpValue(Float lpValue) {
        this.lpValue = lpValue;
    }

    /**
     * @return the logCategoryCount
     */
    public int getLogCategoryCount() {
        return logCategoryCount;
    }

    /**
     * @param logCategoryCount the logCategoryCount to set
     */
    public void setLogCategoryCount(int logCategoryCount) {
        this.logCategoryCount = logCategoryCount;
    }

    /**
     * @return the logList
     */
    public List<LogList> getLogCategories() {
        return logCategories;
    }

    /**
     * @param logList the logCategories to set
     */
    public void setLogCategories(List<LogList> logCategories) {
        this.logCategories = logCategories;
    }   
}
