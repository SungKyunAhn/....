package com.aimir.fep.protocol.nip.frame.payload;

import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

public class MeterEvent extends PayloadFrame {
	private int count;
	private MeterEventFrame[] _meterEventFrame;
	
	public void newMeterEventFrame(int cnt) {
		this._meterEventFrame = new MeterEventFrame[cnt];
	}
	
    public MeterEventFrame[] getMeterEventFrame() {
		return _meterEventFrame;
	}

	public void setMeterEventFrame(MeterEventFrame[] _meterEventFrame) {
		this._meterEventFrame = _meterEventFrame;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public byte[] empty = new byte[0]; 
    
	@Override
    public void decode(byte[] bx) {
    	int pos = 0;
    	byte[] b = new byte[20];
    	System.arraycopy(bx, pos, b, 0, b.length);
    	pos += b.length;
    	log.debug("METER_SERIAL[" + new String(b).trim() + "]");
    	
		b = new byte[1];
	    System.arraycopy(bx, pos, b, 0, b.length);
	    count = DataUtil.getIntToByte(b[0]);
	    _meterEventFrame = new MeterEventFrame[count];
	    pos += b.length;
	    log.debug("EVENT_COUNT[" + count + "]");
	    
	    for(int i=0; i < count; i++){
	    	_meterEventFrame[i] = new MeterEventFrame();
	    	b = new byte[1];
		    System.arraycopy(bx, pos, b, 0, b.length);
		    _meterEventFrame[i].setLength(DataUtil.getIntToByte(b[0]));
		    pos += b.length;
		    
		    b = new byte[_meterEventFrame[i].getLength()];
		    System.arraycopy(bx, pos, b, 0, b.length);
		    _meterEventFrame[i].setValue(Hex.decode(b));
		    pos += b.length;
		    
		    /*
		    b = new byte[1];
		    System.arraycopy(bx, pos, b, 0, b.length);
		    _meterEventFrame[i].setNotify(DataUtil.getIntToByte(b[0]));
		    pos += b.length;
		    
		    b = new byte[4];
		    System.arraycopy(bx, pos, b, 0, b.length);
		    _meterEventFrame[i].setPriority(DataUtil.getIntTo4Byte(b));
		    pos += b.length;
		    
		    b = new byte[14];
		    System.arraycopy(bx, pos, b, 0, b.length);
		    _meterEventFrame[i].setTime(DataUtil.getString(b));//추후 DLMS Format으로 변경 해서 처리해야 됨.
		    pos += b.length;
		    
		    b = new byte[1];
		    System.arraycopy(bx, pos, b, 0, b.length);
		    _meterEventFrame[i].setStructure(DataUtil.getIntToByte(b[0]));
		    pos += b.length;
		    
		    b = new byte[1];
		    System.arraycopy(bx, pos, b, 0, b.length);
		    _meterEventFrame[i].setDataLength(DataUtil.getIntToByte(b[0])); //Length
		    pos += b.length;
		    
		    b = new byte[8];
		    System.arraycopy(bx, pos, b, 0, b.length);
		    _meterEventFrame[i].setAlarm(String.valueOf(DataUtil.getIntTo8Byte(b))); //alarm obis code
		    pos += b.length;

		    b = new byte[5];
		    System.arraycopy(bx, pos, b, 0, b.length);
		    _meterEventFrame[i].setValue(String.valueOf(DataUtil.getIntTo4Byte(b))); //value
		    pos += b.length;
		    */
	    }
    }
    
	@Override
	public String toString() {
		StringBuffer rtn= new StringBuffer();
        for(int i=0; i < _meterEventFrame.length; i++){
        	rtn.append("[i:"+i+"]");
        	rtn.append("[OBIS:" + _meterEventFrame[i].getAlarm() + "]");
        	rtn.append("[VALUE:" + _meterEventFrame[i].getValue() + "]");
        }
        return "[MeterEvent]"+
 	   		   "[count:"+count+"]"+
 	   		   rtn.toString();	   
	}
    
    @Override
    public byte[] encode() throws Exception {
        return empty;
    }
    @Override
    public void setCommandFlow(byte code){ }
    @Override
    public void setCommandType(byte code){ }
    @Override
    public byte[] getFrameTid(){ return null;}
    @Override
    public void setFrameTid(byte[] code){}
    
    public class MeterEventFrame{
    	public int length;
    	public int notify;
    	public int priority;
    	public String time;
    	public int structure;
    	public int dataLength;
    	public String alarm;
    	public String value;
    	
    	
    	public int getLength() {
    		return length;
    	}

    	public void setLength(int length) {
    		this.length = length;
    	}
    	
        public int getNotify() {
    		return notify;
    	}

    	public void setNotify(int notify) {
    		this.notify = notify;
    	}

    	public int getPriority() {
    		return priority;
    	}

    	public void setPriority(int priority) {
    		this.priority = priority;
    	}

    	public String getTime() {
    		return time;
    	}

    	public void setTime(String time) {
    		this.time = time;
    	}

    	public int getStructure() {
    		return structure;
    	}

    	public void setStructure(int structure) {
    		this.structure = structure;
    	}

    	public int getDataLength() {
    		return dataLength;
    	}

    	public void setDataLength(int dataLength) {
    		this.dataLength = dataLength;
    	}

    	public String getAlarm() {
    		return alarm;
    	}

    	public void setAlarm(String alarm) {
    		this.alarm = alarm;
    	}

    	public String getValue() {
    		return value;
    	}

    	public void setValue(String value) {
    		this.value = value;
    	}
    	
    	public String toString() {
    	    StringBuffer buf = new StringBuffer();
    	    buf.append("LEN[" + getLength() + "]");
    	    buf.append(", NOTIFY[" + getNotify() + "]");
    	    buf.append(", PRIORITY[" + getPriority() + "]");
    	    buf.append(", TIME[" + getTime() + "]");
    	    buf.append(", STRUCTURE[" + getStructure() + "]");
    	    buf.append(", DATA_LEN[" + getDataLength() + "]");
    	    buf.append(", ALARM[" + getAlarm() + "]");
    	    buf.append(", VALUE[" + getValue() + "]");
    	    
    	    return buf.toString();
    	}
    }
}
