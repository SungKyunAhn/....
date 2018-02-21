package com.aimir.fep.protocol.nip.command;

public class KepcoMeteringSchedule {
	
    public Type type;
    //Type : 0x00
    public String meteringInterval;
    public int meteringTimeRange;
    //Type : 0x01
    public int hour;
    public int minute;
    public int second;
    //Type : 0x02
    public String sendTime;
	
    public String getMeteringInterval() {
        return meteringInterval;
    }

    public void setMeteringInterval(String meteringInterval) {
        this.meteringInterval = meteringInterval;
    }

    public int getMeteringTimeRange() {
        return meteringTimeRange;
    }

    public void setMeteringTimeRange(int meteringTimeRange) {
        this.meteringTimeRange = meteringTimeRange;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public enum Type {
        PeriodSchedule((byte)0x00),
        fixSchedule((byte)0x01),
        oneSchedule((byte)0x02);

        private byte code;
        
        Type(byte code) {
            this.code = code;
        }
        
        public byte getCode() {
            return this.code;
        }
    }
	
    public void setType(Type type) {
        this.type = type;
    }
	
    public Type getType() {
        return type;
    }
}