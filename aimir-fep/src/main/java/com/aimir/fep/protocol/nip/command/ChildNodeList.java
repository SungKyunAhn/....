package com.aimir.fep.protocol.nip.command;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;

public class ChildNodeList extends AbstractCommand{
    private static Log log = LogFactory.getLog(ChildNodeList.class);
    
    private int childCount;
    private ChildInformation[] childInformation;
    
    public ChildNodeList() {
        super(new byte[] {(byte)0xC3, (byte)0x04});
    }
    
    public int getChildCount() {
		return childCount;
	}

	public void setChildCount(int childCount) {
		this.childCount = childCount;
	}

	@Override
	public void decode(byte[] bx) {
        int pos = 0;
        byte[] b = new byte[2];
        System.arraycopy(bx, pos, b, 0, b.length);
        childCount = DataUtil.getIntTo2Byte(b);
        pos += b.length;
        log.debug("CHILD_COUNT[" + childCount + "]");
        
        childInformation = new ChildInformation[childCount];
        for(int i=0; i < childInformation.length; i++){
            childInformation[i] = new ChildInformation();
            b = new byte[8];
            System.arraycopy(bx, pos, b, 0, b.length);
            childInformation[i].setDestination(String.valueOf(DataUtil.getIntTo8Byte(b)));
            pos += b.length;
             
            b = new byte[8];
            System.arraycopy(bx, pos, b, 0, b.length);
            childInformation[i].setNextNode(String.valueOf(DataUtil.getIntTo8Byte(b)));
            pos += b.length;
             
            b = new byte[4];
            System.arraycopy(bx, pos, b, 0, b.length);
            childInformation[i].setLifeTime(String.valueOf(DataUtil.getIntTo4Byte(b)));
            pos += b.length;
             
            b = new byte[1];
            System.arraycopy(bx, pos, b, 0, b.length);
            childInformation[i].setUpdated(DataUtil.getIntToByte(b[0]));
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
	
	public class ChildInformation{
		public String destination;
	    public String nextNode;
	    public String lifeTime;
	    public int updated;
	    
		public String getDestination() {
			return destination;
		}

		public void setDestination(String destination) {
			this.destination = destination;
		}

		public String getLifeTime() {
			return lifeTime;
		}

		public void setLifeTime(String lifeTime) {
			this.lifeTime = lifeTime;
		}

		public String getNextNode() {
			return nextNode;
		}

		public void setNextNode(String nextNode) {
			this.nextNode = nextNode;
		}

		public int getUpdated() {
			return updated;
		}

		public void setUpdated(int updated) {
			this.updated = updated;
		}
	}
		
	@Override
	public String toString() {
		StringBuffer rtn= new StringBuffer();
	    for(int i=0; i< childInformation.length-1 ;i++){
	    	rtn.append("[i:"+i+"]");
        	rtn.append("[Destination:"+((ChildInformation)childInformation[i]).getDestination()+"]");
        	rtn.append("[NextNode:"+((ChildInformation)childInformation[i]).getNextNode()+"]");
        	rtn.append("[LifeTime:"+((ChildInformation)childInformation[i]).getLifeTime()+"]");
        	rtn.append("[Updated:"+((ChildInformation)childInformation[i]).getUpdated()+"]");
	    }
	    return "[ChildNodeList]"+
	 		   "[childCount:"+childCount+"]"+
	 		   rtn.toString();	   
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
