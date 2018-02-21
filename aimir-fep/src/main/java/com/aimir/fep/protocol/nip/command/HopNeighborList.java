package com.aimir.fep.protocol.nip.command;

import java.util.HashMap;

import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;

public class HopNeighborList extends AbstractCommand{
    private int neighborCount;
    private NodeInformationTable[] nodeInformationTable;
    
    public HopNeighborList() {
        super(new byte[] {(byte)0xC3, (byte)0x03});
    }

    public int getNeighborCount() {
        return neighborCount;
    }

    public void setNeighborCount(int neighborCount) {
        this.neighborCount = neighborCount;
    }
    
    @Override
    public void decode(byte[] bx) {
        int pos = 0;
        byte[] b = new byte[2];
        System.arraycopy(bx, pos, b, 0, b.length);
        neighborCount = DataUtil.getIntTo2Byte(b);
        pos += b.length;
        
        nodeInformationTable = new NodeInformationTable[neighborCount];
        for(int i=0;i<nodeInformationTable.length-1;i++){
            nodeInformationTable[i] = new NodeInformationTable();
            b = new byte[8];
            System.arraycopy(bx, pos, b, 0, b.length);
            nodeInformationTable[i].setEui(String.valueOf(DataUtil.getIntToBytes(b)));
            pos += b.length;
             
            b = new byte[1];
            System.arraycopy(bx, pos, b, 0, b.length);
            nodeInformationTable[i].setType(DataUtil.getIntToByte(b[0]));
            pos += b.length;
             
            b = new byte[1];
            System.arraycopy(bx, pos, b, 0, b.length);
            nodeInformationTable[i].setState(DataUtil.getIntToByte(b[0]));
            pos += b.length;
             
            b = new byte[2];
            System.arraycopy(bx, pos, b, 0, b.length);
            nodeInformationTable[i].setDistance(DataUtil.getIntTo2Byte(b));
            pos += b.length;
             
            b = new byte[2];
            System.arraycopy(bx, pos, b, 0, b.length);
            nodeInformationTable[i].setEtx(DataUtil.getIntTo2Byte(b));
            pos += b.length;
             
            b = new byte[4];
            System.arraycopy(bx, pos, b, 0, b.length);
            nodeInformationTable[i].setLifeTime(String.valueOf(DataUtil.getIntTo4Byte(b)));
            pos += b.length;
             
            b = new byte[1];
            System.arraycopy(bx, pos, b, 0, b.length);
            nodeInformationTable[i].setVer(DataUtil.getIntToByte(b[0]));
            pos += b.length;
             
            b = new byte[1];
            System.arraycopy(bx, pos, b, 0, b.length);
            nodeInformationTable[i].setDtsn(DataUtil.getIntToByte(b[0]));
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
		 for(int i=0; i< nodeInformationTable.length-1 ;i++){
		     rtn.append("[i:"+i+"]");
		     rtn.append("[eui:"+((NodeInformationTable)nodeInformationTable[i]).getEui()+"]");
		     rtn.append("[type:"+((NodeInformationTable)nodeInformationTable[i]).getType()+"]");
		     rtn.append("[state:"+((NodeInformationTable)nodeInformationTable[i]).getState()+"]");
		     rtn.append("[distance:"+((NodeInformationTable)nodeInformationTable[i]).getDistance()+"]");
		     rtn.append("[etx:"+((NodeInformationTable)nodeInformationTable[i]).getEtx()+"]");
		     rtn.append("[lifeTime:"+((NodeInformationTable)nodeInformationTable[i]).getLifeTime()+"]");
		     rtn.append("[ver:"+((NodeInformationTable)nodeInformationTable[i]).getVer()+"]");
		     rtn.append("[dtsn:"+((NodeInformationTable)nodeInformationTable[i]).getDtsn()+"]");
		 }
		 return "[HopNeighborList]"+
		 "[neighborCount:"+neighborCount+"]"+
		 rtn.toString();	   
    }
	
	
    public class NodeInformationTable{
        private String eui;
        private int type;
        private int state;
        private int distance;
        private int etx;
        private String lifeTime;
        private int ver;
        private int dtsn;
	    
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
    }

    @Override
    public Command get(HashMap p) throws Exception{return null;}
    @Override
    public Command set() throws Exception{return null;}
    @Override
    public Command set(HashMap p) throws Exception{return null;}
    @Override
    public Command trap() throws Exception{return null;}
    @Override
    public void decode(byte[] p1, CommandType commandType)
                    throws Exception {
        // TODO Auto-generated method stub
        
    }
}
