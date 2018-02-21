package com.aimir.fep.protocol.nip.command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.aimir.fep.util.DataUtil;

public class RomReadDataReq{
    enum POLL_TYPE {
        LoadProfile(1),
        StandardEvent(2),
        ControlLog(3),
        PowerFailureLog(4),
        PowerQualityLog(5),
        TamperingLog(6),
        FirmwareUpgradeLog(7);
        
        private int type;
        
        POLL_TYPE(int type) {
            this.type = type;
        }
    }
    
    // Poll address
    private String startAddress;
    private int readSize;
    
    // Poll information
    private int totalCount;
    private PollData[] pollData;
    
    class PollData {
        private int type = 0;
        private int offset = 0;
        private int count = 0;
        public int getType() {
            return type;
        }
        public void setType(int type) {
            this.type = type;
        }
        public int getOffset() {
            return offset;
        }
        public void setOffset(int offset) {
            this.offset = offset;
        }
        public int getCount() {
            return count;
        }
        public void setCount(int count) {
            this.count = count;
        }
        public byte[] toByteArray() throws IOException {
            ByteArrayOutputStream out = null;
            try {
                out = new ByteArrayOutputStream();
                out.write(DataUtil.getByteToInt(type));
                out.write(DataUtil.get2ByteToInt(offset));
                out.write(DataUtil.get2ByteToInt(count));
            }
            finally {
                if (out != null) out.close();
            }
            
            return out.toByteArray();
        }
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public int getReadSize() {
        return readSize;
    }

    public void setReadSize(int readSize) {
        this.readSize = readSize;
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
    
    public void setPollData( int type, int offset, int count){
    	PollData[] pol = new PollData[1];
    	pol[0] = new PollData();
    	pol[0].setType(type);
    	pol[0].setOffset(offset);
    	pol[0].setCount(count);
    	this.pollData = pol;
    }
}