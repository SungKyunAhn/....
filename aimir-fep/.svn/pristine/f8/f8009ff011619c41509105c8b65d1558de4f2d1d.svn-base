package com.aimir.fep.protocol.nip.command;

import java.util.HashMap;

import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;

public class ParentNodeInfo extends AbstractCommand{
    private String eui;
    private int type;
    private int state;
    private int distance;
    private int etx;
    private String lifeTime;
    private int ver;
    private int dtsn;
    
    public ParentNodeInfo() {
        super(new byte[] {(byte)0xC3, (byte)0x01});
    }
    
	public String getEui() {
		return eui;
	}

	public void setEui(String eui) {
		this.eui = eui;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public int getEtx() {
		return etx;
	}

	public void setEtx(int etx) {
		this.etx = etx;
	}

	public String getLifeTime() {
		return lifeTime;
	}

	public void setLifeTime(String lifeTime) {
		this.lifeTime = lifeTime;
	}

	public int getVer() {
		return ver;
	}

	public void setVer(int ver) {
		this.ver = ver;
	}

	public int getDtsn() {
		return dtsn;
	}

	public void setDtsn(int dtsn) {
		this.dtsn = dtsn;
	}
	@Override
	public void decode(byte[] bx) {
		
        int pos = 0;
        byte[] b = new byte[8];
        System.arraycopy(bx, pos, b, 0, b.length);
        eui = String.valueOf(DataUtil.getIntToBytes(b));
        pos += b.length;
        
        b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        type= DataUtil.getIntToByte(b[0]);
        pos += b.length;
        
        b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        state= DataUtil.getIntToByte(b[0]);
        pos += b.length;
        
        b = new byte[2];
        System.arraycopy(bx, pos, b, 0, b.length);
        distance= DataUtil.getIntTo2Byte(b);
        pos += b.length;
        
        b = new byte[2];
        System.arraycopy(bx, pos, b, 0, b.length);
        etx= DataUtil.getIntTo2Byte(b);
        pos += b.length;
        
        b = new byte[4];
        System.arraycopy(bx, pos, b, 0, b.length);
        lifeTime= String.valueOf(DataUtil.getIntTo4Byte(b));
        pos += b.length;
        
        b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        ver= DataUtil.getIntToByte(b[0]);
        pos += b.length;
        
        b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        dtsn= DataUtil.getIntToByte(b[0]);
        
    }

	@Override
	public String toString() {
	    return "[ParentNodeInfo]"+
	    	   "[eui:"+eui+"]"+
	    	   "[type:"+type+"]"+
	    	   "[state:"+state+"]"+
	    	   "[distance:"+distance+"]"+
	    	   "[etx:"+etx+"]"+
	    	   "[lifeTime:"+lifeTime+"]"+
	    	   "[ver:"+ver+"]"+
	    	   "[dtsn:"+dtsn+"]";
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
