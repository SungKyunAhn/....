package com.aimir.fep.protocol.nip.command;

import java.util.HashMap;

import com.aimir.fep.protocol.nip.command.ObisData.ObisCodes;
import com.aimir.fep.protocol.nip.command.ResponseResult.ObisStatus;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;

public class ObisAdd extends AbstractCommand{
    public ObisCodes obisCode;
	public int selectiveAccessLength;
	public String selectiveAccessData;
	
	public ObisAdd() {
	    super(new byte[] {(byte)0x40, (byte)0x02});
	}
	
    public String getSelectiveAccessData() {
		return selectiveAccessData;
	}

	public void setSelectiveAccessData(String selectiveAccessData) {
		this.selectiveAccessData = selectiveAccessData;
	}

	private ObisStatus obisStatus;

	public ObisCodes getObisCode() {
		return obisCode;
	}

	public void setObisCode(ObisCodes obisCode) {
		this.obisCode = obisCode;
	}

	public int getSelectiveAccessLength() {
		return selectiveAccessLength;
	}

	public void setSelectiveAccessLength(int selectiveAccessLength) {
		this.selectiveAccessLength = selectiveAccessLength;
	}
	@Override
	public void decode(byte[] bx) {
        int pos = 0;
        byte[] b = new byte[1];
        System.arraycopy(bx, 0, b, 0, b.length);
        for (ObisStatus s : ObisStatus.values()) {
            if (s.getCode() == b[0]) {
            	obisStatus = s;
                break;
            }
        }
    }
	@Override
	public Command set(HashMap info) throws Exception {
        Command command = new Command();
        Command.Attribute attr = command.newAttribute();
        Command.Attribute.Data[] datas = attr.newData(1);
        
        command.setCommandFlow(CommandFlow.Request);
        command.setCommandType(CommandType.Set);
        ObisCodes obisCode = (ObisCodes)info.get("obisCode");
        int selectiveAccessLength = (int)info.get("selectiveAccessLength");
        datas[0].setId(getAttributeID());
        datas[0].setValue(DataUtil.get3ByteToInt(Integer.parseInt(obisCode.serviceTypes)));
        datas[0].setValue(DataUtil.get2ByteToInt(Integer.parseInt(obisCode.classId)));
        datas[0].setValue(DataUtil.get6ByteToInt(Integer.parseInt(obisCode.obis)));
        datas[0].setValue((new byte[]{DataUtil.getByteToInt(obisCode.attribute)}));
        datas[0].setValue((new byte[]{DataUtil.getByteToInt(selectiveAccessLength)}));
        attr.setData(datas);
        command.setAttribute(attr);
        return command;
    }
	@Override
	public String toString() {
	    return "[ObisAdd]"+
	    	   "[obiusStatus:"+obisStatus.name()+"]";
	}
	@Override
	public void decode(byte[] p1, CommandType p2) throws Exception{}
	@Override
	public Command get() throws Exception{return null;}
	@Override
	public Command get(HashMap p) throws Exception{return null;}
	@Override
	public Command set() throws Exception{return null;}
	@Override
	public Command trap() throws Exception{return null;}
}
