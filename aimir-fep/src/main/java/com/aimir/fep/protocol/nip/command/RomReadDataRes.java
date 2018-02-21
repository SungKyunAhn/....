package com.aimir.fep.protocol.nip.command;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

public class RomReadDataRes{
    private static Log log = LogFactory.getLog(RomReadDataRes.class);
    
    // Poll ROM Data
    private int length;
    private byte[] data;
    
    // Poll Metering Data
    private int totalCount;
    private PollData[] pollData;
    
    class PollData {
        private int type = 0;
        private int length = 0;
        private byte[] data;
        public int getType() {
            return type;
        }
        public int getLength() {
            return length;
        }
        public void setLength(int length) {
            this.length = length;
        }
        public byte[] getData() {
            return data;
        }
        public void setData(byte[] data) {
            this.data = data;
        }
        public void setType(int type) {
            this.type = type;
        }
        
        public String toString() {
            return "TYPE[" + type + "] LEN[" + length + "] DATA[" + Hex.decode(data) + "]";
        }
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public PollData[] getPollData() {
        return pollData;
    }

    public void setPollData(PollData[] pollData) {
        this.pollData = pollData;
    }
    
    public void parse(int pos, byte[] bx) {
        byte[] b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        pos += b.length;
        totalCount = DataUtil.getIntToBytes(b);
        log.debug("TOTAL_COUNT[" + totalCount + "],bx=" +Hex.decode(bx));
        
        pollData = new PollData[totalCount];
        
        for (int i = 0; i < totalCount; i++) {
            pollData[i] = new PollData();
            
            b = new byte[1];
            System.arraycopy(bx, pos, b, 0, b.length);
            pos += b.length;
            pollData[i].setType(DataUtil.getIntToBytes(b));
            
            b = new byte[2];
            System.arraycopy(bx, pos, b, 0, b.length);
            pos += b.length;
            pollData[i].setLength(DataUtil.getIntTo2Byte(b));
            log.debug("length=" + pollData[i].getLength());
            
            b = new byte[pollData[i].getLength()];
            System.arraycopy(bx, pos, b, 0, b.length);
            pos += b.length;
            pollData[i].setData(b);
            
            log.debug(pollData[i].toString());
        }
    }
    
    public String toString(int type) {
        StringBuffer buf = new StringBuffer();
        if (type == 0) {
            buf.append("LEN[" + length + "] DATA[" + Hex.decode(data) + "]");
        }
        else if (type == 1) {
            buf.append("TOTAL_COUNT[" + totalCount + "]");
            for (PollData pd : pollData) {
                buf.append("[" + pd.toString() + "]");
            }
        }
        
        return buf.toString();
    }
}