package com.aimir.fep.protocol.nip.command;

import java.util.HashMap;

import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;

import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;

public class GatheringDataAction extends AbstractCommand{
    private int status;
    private String tid;
    
    public GatheringDataAction() {
        super(new byte[] {(byte)0xC2, (byte)0x02});
    }

    public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	@Override
	public void decode(byte[] bx) {
        int pos = 0;
        /**
         * status 
         * 0 : 명령을 정상적으로 수신함.
		 * 1 : 이미 다른 Transaction을 수행중
		 * 2 : 모뎀이 다른 동작 수행중으로 인해 처리 불가능
         * 
         */

        byte[] b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        status =  DataUtil.getIntToByte(b[0]);
        pos += b.length;
        
        /**
         * status 
         * 0 : 전달받은 TID
		 * 1 : 이미 수행중인 TID
		 * 2 : 0x0000
         * 
         */
        b = new byte[2];
        System.arraycopy(bx, pos, b, 0, b.length);
        tid=  String.valueOf(DataUtil.getIntTo2Byte(b));
    }
	
	@Override
	public String toString() {
	    return "[GatheringDataAction]"+
	    	   "[status:"+status+"]"+
	    	   "[tid:"+tid+"]";
	}
	
	@Override
	public Command set(HashMap info)throws Exception{
			//int tid, int obisCnt, String[] xdlmsApdu) throws Exception {
       Command command = new Command();
       Command.Attribute attr = command.newAttribute();
       Command.Attribute.Data[] datas = attr.newData(1);
       
       command.setCommandFlow(CommandFlow.Request);
       command.setCommandType(CommandType.Get);
       int obisCount =(int)info.get("obisCnt");
       datas[0].setId(getAttributeID());
       
       ByteArrayOutputStream out = null;
       
       try {
           out = new ByteArrayOutputStream();
           out.write(DataUtil.get2ByteToInt((int)info.get("tid")));
           out.write((new byte[]{DataUtil.getByteToInt(obisCount)}));
           
           for(int i=0;i< obisCount;i++){
        	   out.write(((String[])info.get("xdlmsApdu"))[i].getBytes());
           }
           datas[0].setValue(out.toByteArray());
       }
       finally {
           if (out != null) out.close();
       }
       
       attr.setData(datas);
       command.setAttribute(attr);
       return command;
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
