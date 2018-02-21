package com.aimir.fep.protocol.nip.command;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashMap;

import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;

public class MeterTimeSync extends AbstractCommand{
    private int meterCount;
    private String yyyymmddhhmmss;
    private int[] port;
    
    public MeterTimeSync() {
        super(new byte[] {(byte)0x20, (byte)0x04});
    }
    
    public int getMeterCount() {
        return meterCount;
    }

    public void setMeterCount(int meterCount) {
        this.meterCount = meterCount;
    }

    public String getYyyymmddhhmmss() {
        return yyyymmddhhmmss;
    }

    public void setYyyymmddhhmmss(String yyyymmddhhmmss) {
        this.yyyymmddhhmmss = yyyymmddhhmmss;
    }

    public int[] getPort() {
        return port;
    }

    public void setPort(int[] port) {
        this.port = port;
    }
	
    @Override
    public Command set(HashMap info) throws Exception {
        Command command = new Command();
        Command.Attribute attr = command.newAttribute();
        Command.Attribute.Data[] datas = attr.newData(1);
        
        command.setCommandFlow(CommandFlow.Request);
        command.setCommandType(CommandType.Set);
        datas[0].setId(getAttributeID());
        int meterCount = (int)info.get("meterCount");
        String yyyymmddhhmmss = (String)info.get("yyyymmddhhmmss");
        int[] port = (int[])info.get("port");
        
        ByteArrayOutputStream out = null;
        
        try {
            out = new ByteArrayOutputStream();
            //meterCount
            out.write(new byte[]{DataUtil.getByteToInt(meterCount)});
            
            //port
            
            out.write(DataUtil.getByteToInt(port.length));
            for (int i = 0; i < port.length; i++) {
                out.write(DataUtil.getByteToInt(port[i]));
            }
            
            //yyyymmddhhmmss
            byte[] b = DataUtil.get2ByteToInt(Integer.parseInt(yyyymmddhhmmss.substring(0, 4)));
            out.write(new byte[]{b[0], b[1],
                    DataUtil.getByteToInt(Integer.parseInt(yyyymmddhhmmss.substring(4,6))),
                    DataUtil.getByteToInt(Integer.parseInt(yyyymmddhhmmss.substring(6,8))),
                    DataUtil.getByteToInt(Integer.parseInt(yyyymmddhhmmss.substring(8,10))),
                    DataUtil.getByteToInt(Integer.parseInt(yyyymmddhhmmss.substring(10,12))),
                    DataUtil.getByteToInt(Integer.parseInt(yyyymmddhhmmss.substring(12,14)))});
            
            datas[0].setValue(out.toByteArray());
            attr.setData(datas);
            command.setAttribute(attr);
            return command;
        }
        finally {
            if (out != null) out.close();
        }
    }
 
    @Override
    public void decode(byte[] bx) {
        int pos = 0;
        byte[] b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        meterCount = DataUtil.getIntToByte(b[0]);
        pos += b.length;
        
        b = new byte[meterCount];
        System.arraycopy(bx, pos, b, 0, b.length);
        for(int i=0 ; i<b.length;i++){
            port[i]=DataUtil.getIntToByte(b[i]);
        }
        pos += b.length;
        
        b = new byte[7];
        System.arraycopy(bx, pos, b, 0, b.length);
        yyyymmddhhmmss = String.format("%4d%02d%02d%02d%02d%02d", 
                DataUtil.getIntTo2Byte(new byte[]{b[0], b[1]}),
                DataUtil.getIntToByte(b[2]),
                DataUtil.getIntToByte(b[3]),
                DataUtil.getIntToByte(b[4]),
                DataUtil.getIntToByte(b[5]),
                DataUtil.getIntToByte(b[6]));
        pos += b.length;
    }
    
    @Override
    public String toString() {
        return "[MeterTimeSync]"+
                "[meterCount:"+meterCount+"]"+
                "[yyyymmddhhmmss:"+yyyymmddhhmmss+"]"+
                "[port:"+Arrays.toString(port)+"]";
    }

	@Override
	public Command get() throws Exception{return null;}
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
