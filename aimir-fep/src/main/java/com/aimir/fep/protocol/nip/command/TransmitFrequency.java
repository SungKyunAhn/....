package com.aimir.fep.protocol.nip.command;

import com.aimir.fep.protocol.nip.command.ResponseResult.Status;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;

import java.util.HashMap;

public class TransmitFrequency extends AbstractCommand{
    
    public TransmitFrequency() {
        super(new byte[] {(byte)0x20, (byte)0x13});
    }
    
    private int transmitFrequency=-1;
    private Status status;
    
    public Status getStatus() {
        return status;
    }

    public String getStatusString(){
        String toStatus = status==null? "No Error":status.name();
        return toStatus;
    }

	public int getTransmitFrequency() {
		return transmitFrequency;
	}

    // TransmitFrequency delivered as seconds. Convert it to time form.
    public String getFrequencyAsTime(){
        int frq = getTransmitFrequency();
        if(frq < 0){
            return "0:0:-1";
        }
        String result = "";
        int frqHour = frq / 3600;
        int frqMinute = (frq % 3600) / 60;
        int frqSecond = (frq % 3600) % 60;
        result = frqHour + ":" + frqMinute + ":" + frqSecond;
        return result;
    }

	public void setTransmitFrequency(int transmitFrequency) {
		this.transmitFrequency = transmitFrequency;
	}

	@Override
	public void decode(byte[] bx) {
        int pos = 0;
        byte[] b = new byte[2];
        System.arraycopy(bx, 0, b, 0, b.length);

        for (Status s : Status.values()) {
            if (s.getCode()[0] == b[0] && s.getCode()[1] == b[1]) {
                status = s;
                break;
            }
        }
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
        //count
        if ( info.get("second") instanceof String ){
        	datas[0].setValue(DataUtil.get2ByteToInt(Integer.parseInt((String)info.get("second"))));
        	transmitFrequency = Integer.parseInt((String)info.get("second"));
        }
        else {
        	datas[0].setValue(DataUtil.get2ByteToInt((int)info.get("second")));
        	transmitFrequency = (int)info.get("second");
        }
        attr.setData(datas);
        command.setAttribute(attr);
        return command;
    }

	@Override
	public void decode(byte[] p1, CommandType commandType) throws Exception{
        if(commandType == CommandType.Get){
            int pos = 0;
            byte[] b = new byte[2];
            System.arraycopy(p1, pos, b, 0, b.length);
            transmitFrequency = DataUtil.getIntTo2Byte(b);
            status = Status.Success;
        }
        else{ // SET
            byte[] b = new byte[2];
            System.arraycopy(p1, 0, b, 0, b.length);
            for (Status s : Status.values()) {
                if (s.getCode()[0] == b[0] && s.getCode()[1] == b[1]) {
                    status = s;
                    break;
                }
            }
            if((status == null) || (status == Status.Success)){
                transmitFrequency = DataUtil.getIntTo2Byte(b);
            }
        }
	}


    @Override
    public String toString() {
        String toStatus = status==null? "No Error":status.name();
        String toInterval = transmitFrequency<0? "Unknown":Integer.toString(transmitFrequency);

        //String statusStr  =  status != null ?  status.name() : "";
        return "[Transmit Frequency]"+
                "[status: "+ toStatus +"]" +
                "[frequency: " + toInterval + "]";

    }

	@Override
	public Command get(HashMap p) throws Exception{return null;}

	@Override
	public Command set() throws Exception{return null;}

	@Override
	public Command trap() throws Exception{return null;}

}
