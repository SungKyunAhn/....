package com.aimir.fep.protocol.nip.command;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;


public class RomRead extends AbstractCommand{
    private int type;
    private RomReadDataReq req;
    private RomReadDataRes res;
    private byte[][] data;
    public RomRead() {
        super(new byte[] {(byte)0xC2, (byte)0x01});
    }
    
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
    // SP-179
    public byte[][] getRomData(){
    	data = new byte[res.getPollData().length][];
    	int i=0;
    	for (RomReadDataRes.PollData pd : res.getPollData()) {
    		data[i] = pd.getData();
    	}
    	return this.data;
    }
    // SP-179
	
    @Override
    public void decode(byte[] bx) {
        res = new RomReadDataRes();
        
        int pos = 0;
        byte[] b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        type =  DataUtil.getIntToByte(b[0]);
        pos += b.length;
        
        //Raw ROM Read
        if(type == 0){
        	b = new byte[2];
        	System.arraycopy(bx, pos, b, 0, b.length);
        	res.setLength(DataUtil.getIntTo2Byte(b));
            pos += b.length;
            
            b = new byte[res.getLength()];
            System.arraycopy(bx, pos, b, 0, b.length);
            res.setData(b);
        //LP Metering Data Read
        }
        else if(type == 2){	    // SP-179
            res.parse(pos, bx);
        }
        
    }

    @Override
    public String toString() {
        return res.toString(type);
    }
	
    @Override
    public Command get(HashMap info) throws Exception {
        ByteArrayOutputStream out = null;
        try {
            Command command = new Command();
            Command.Attribute attr = command.newAttribute();
            Command.Attribute.Data[] datas = attr.newData(1);
            //int type = (int)info.get("type");
            type = (int)info.get("type");
            req = (RomReadDataReq)info.get("romReadData");
            command.setCommandFlow(CommandFlow.Request);
            command.setCommandType(CommandType.Get);
            datas[0].setId(getAttributeID());
           
            out = new ByteArrayOutputStream();
            out.write(DataUtil.getByteToInt(type));
            //Poll Address
            if(type == 0){
                out.write(DataUtil.get4ByteToInt(Integer.parseInt(req.getStartAddress())));
                out.write(DataUtil.get2ByteToInt(req.getReadSize()));
                //Poll Information
            }
            else if(type == 2){
                out.write(DataUtil.getByteToInt(req.getPollData().length));
                for (RomReadDataReq.PollData pd : req.getPollData()) {
                    out.write(pd.toByteArray());
                }
            }
            datas[0].setValue(out.toByteArray());
            attr.setData(datas);
            command.setAttribute(attr);
            
            log.debug("get type:" + type);
            return command;
        }
        finally {
            if (out != null) out.close();
        }
    }
	
    @Override
    public void decode(byte[] p1, CommandType p2) throws Exception{}

    @Override
    public Command get() throws Exception{return null;}
 
    @Override
    public Command set() throws Exception{return null;}

    @Override
    public Command set(HashMap p) throws Exception{return null;}

    @Override
    public Command trap() throws Exception{return null;}
	
}
