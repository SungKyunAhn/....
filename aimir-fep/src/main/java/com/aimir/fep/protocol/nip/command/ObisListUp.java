package com.aimir.fep.protocol.nip.command;

import java.util.HashMap;

import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;

public class ObisListUp extends AbstractCommand{
    private int obisCnt;
    private ObisData[] obisData;
    
    public ObisListUp() {
        super(new byte[] {(byte)0x40, (byte)0x01});
    }
    
    public int getObisCnt() {
        return obisCnt;
    }

    public void setObisCnt(int obisCnt) {
        this.obisCnt = obisCnt;
    }

    public ObisData[] getObisData() {
        return obisData;
    }

    public void setObisData(ObisData[] obisData) {
        this.obisData = obisData;
    }

    @Override
    public void decode(byte[] bx) {
        int pos = 0;
        byte[] b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        obisCnt = DataUtil.getIntToByte(b[0]);
        pos += b.length;

        obisData = new ObisData[obisCnt];
        for(int i=0; i < obisData.length-1 ;i++){
        	obisData[i] = new ObisData();
        	b = new byte[14];
        	System.arraycopy(bx, pos, b, 0, b.length);
        	obisData[i].obisIndex =  DataUtil.getIntToByte(b[0]);
        	obisData[i].obisCodes.serviceTypes = String.valueOf(DataUtil.getIntTo3Byte((new byte[]{b[3],b[2],b[1]}))); 
        	obisData[i].obisCodes.classId = String.valueOf(DataUtil.getIntTo2Byte((new byte[]{b[5],b[4]})));
        	obisData[i].obisCodes.obis = String.valueOf(DataUtil.getIntTo6Byte((new byte[]{b[11],b[10],b[9],b[8],b[7],b[6]})));
        	obisData[i].obisCodes.attribute = DataUtil.getIntToByte(b[12]);
        	obisData[i].selectiveAccessLength =  DataUtil.getIntToByte(b[13]);
        	pos += b.length;
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
    public String toString() {
        StringBuffer rtn= new StringBuffer();
        for(int i=0; i< obisData.length-1 ;i++){
            rtn.append("[i:"+i+"]");
            rtn.append("[ObisIndex:"+((ObisData)obisData[i]).getObisIndex()+"]");
            rtn.append("[ServiceTypes:"+((ObisData)obisData[i]).obisCodes.getServiceTypes()+"]");
            rtn.append("[ClassId:"+((ObisData)obisData[i]).obisCodes.getClassId()+"]");
            rtn.append("[Obis:"+((ObisData)obisData[i]).obisCodes.getObis()+"]");
            rtn.append("[Attribute:"+((ObisData)obisData[i]).obisCodes.getAttribute()+"]");
            rtn.append("[SelectiveAccessLength:"+((ObisData)obisData[i]).getSelectiveAccessLength()+"]");
        	
        }
        return "[ObisListUp]"+
        "[obisCnt:"+obisCnt+"]"+
        rtn.toString();	   
    }

    @Override
    public void decode(byte[] p1, CommandType p2) throws Exception{}

    @Override
	public Command get(HashMap p) throws Exception{return null;}

    @Override
	public Command set() throws Exception{return null;}

    @Override
	public Command set(HashMap p) throws Exception{return null;}

    @Override
	public Command trap() throws Exception{return null;}
}
