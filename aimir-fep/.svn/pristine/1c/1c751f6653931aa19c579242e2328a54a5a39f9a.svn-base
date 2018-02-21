package com.aimir.fep.protocol.nip.command;

import com.aimir.fep.protocol.nip.command.ResponseResult.Status;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;

import java.util.HashMap;

public class MeteringInterval extends AbstractCommand{
    public MeteringInterval() {
        super(new byte[] {(byte)0x20, (byte)0x05});
    }

    private int interval=-1;
    private Status status;

    public int getInterval() {
        return interval;
    }

    // MeteringInterval delivered as seconds. Convert it to time form.
    public String getIntervalAsTime(){
        int itv = getInterval();
        if(itv < 0){
            return "0:0:-1";
        }
        String result = "";
        int itvHour = itv / 3600;
        int itvMinute = (itv % 3600) / 60;
        int itvSecond = (itv % 3600) % 60;
        result = itvHour + ":" + itvMinute + ":" + itvSecond;
        return result;
    }

    public Status getStatus() {
        return status;
    }

    public String getStatusString(){
        String toStatus = status==null? "No Error":status.name();
        return toStatus;
    }
    
    @Override
    public Command get() throws Exception {
        Command command = new Command();
        Command.Attribute attr = command.newAttribute();
        Command.Attribute.Data[] datas = attr.newData(1);
        
        command.setCommandFlow(CommandFlow.Request);
        command.setCommandType(CommandType.Get);
        datas[0].setId(getAttributeID());
        
        attr.setData(datas);
        command.setAttribute(attr);
        return command;
    }
  
    @Override
    public Command set(HashMap info) throws Exception {
        Command command = new Command();
        Command.Attribute attr = command.newAttribute();
        Command.Attribute.Data[] datas = attr.newData(1);
        
        command.setCommandFlow(CommandFlow.Request);
        command.setCommandType(CommandType.Set);
        datas[0].setId(getAttributeID());
        datas[0].setValue(DataUtil.get2ByteToInt((int)info.get("interval")));
        
        attr.setData(datas);
        command.setAttribute(attr);
        return command;
    }
  
    @Override
    public void decode(byte[] data) {
        // status result
        byte[] b = new byte[2];
        System.arraycopy(data, 0, b, 0, b.length);

        for (Status s : Status.values()) {
            if (s.getCode()[0] == b[0] && s.getCode()[1] == b[1]) {
                status = s;
                break;
            }
        }
        if((status == null) || (status == Status.Success)){
            interval = DataUtil.getIntTo2Byte(b);
        }
    }
	
    @Override
    public String toString() {
        String toStatus = status==null? "No Error":status.name();
        String toInterval = interval<0? "Unknown":Integer.toString(interval);
        return "[Metering Interval]"+
                "[status: "+toStatus+"]"+
                "[interval: "+toInterval+"]";
    }

    @Override
    public Command get(HashMap p) throws Exception{return null;}
    @Override
    public Command set() throws Exception{return null;}
    @Override
    public Command trap() throws Exception{return null;}

    @Override
    public void decode(byte[] p1, CommandType commandType)
                    throws Exception {
        // TODO Auto-generated method stub
        
    }
	
}
