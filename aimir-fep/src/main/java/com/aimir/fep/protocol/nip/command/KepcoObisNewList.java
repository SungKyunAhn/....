package com.aimir.fep.protocol.nip.command;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import com.aimir.fep.protocol.nip.command.ResponseResult.ObisStatus;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;

public class KepcoObisNewList extends AbstractCommand{
    private ObisStatus obisStatus;
    private int obisCnt;
    private KepcoObisData[] kepcoObisData;
    
    public KepcoObisNewList() {
        super(new byte[] {(byte)0x40, (byte)0x05});
    }
    
    public int getObisCnt() {
        return obisCnt;
    }
    
    public void setObisCnt(int obisCnt) {
        this.obisCnt = obisCnt;
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
    public String toString() {
        return "[KepcoObisNewList]"+
                "[obisStatus:"+obisStatus.name()+"]";
    }

    @Override
    public Command set(HashMap info) throws Exception {
        Command command = new Command();
        Command.Attribute attr = command.newAttribute();
        Command.Attribute.Data[] datas = attr.newData(1);
        
        command.setCommandFlow(CommandFlow.Request);
        command.setCommandType(CommandType.Set);
        
        int obisCnt = (int)info.get("obisCnt");
        KepcoObisData[] kepcoObisData = (KepcoObisData[])info.get("kepcoObisData");
        
        datas[0].setId(getAttributeID());
        byte[] typeGubn = new byte[] {(byte)0x00, (byte)0x01,(byte)0x02};
        byte[] meterPortAddress;
        
        ByteArrayOutputStream out = null;
                
        try {
            out = new ByteArrayOutputStream();
            out.write(DataUtil.getByteToInt(obisCnt));
            
            for(int i=0; i < obisCnt ;i++){
                out.write(kepcoObisData[i].obisCodeId.getCode());
                out.write((new byte[]{kepcoObisData[i].meteringSchedule.type.getCode()}));
                if(kepcoObisData[i].meteringSchedule.type.getCode() == typeGubn[0]){
                    out.write(DataUtil.get2ByteToInt(Integer.parseInt(kepcoObisData[i].meteringSchedule.getMeteringInterval())));
                    out.write((new byte[]{DataUtil.getByteToInt(kepcoObisData[i].meteringSchedule.getMeteringTimeRange())}));
                }else if(kepcoObisData[i].meteringSchedule.type.getCode() == typeGubn[1]){
                    out.write((new byte[]{DataUtil.getByteToInt(kepcoObisData[i].meteringSchedule.getHour())}));
                    out.write((new byte[]{DataUtil.getByteToInt(kepcoObisData[i].meteringSchedule.getMinute())}));
                    out.write((new byte[]{DataUtil.getByteToInt(kepcoObisData[i].meteringSchedule.getSecond())}));
                }else if(kepcoObisData[i].meteringSchedule.type.getCode() == typeGubn[2]){
                    out.write(DataUtil.get3ByteToInt(Integer.parseInt(kepcoObisData[i].meteringSchedule.getSendTime())));
                }
                out.write((new byte[]{kepcoObisData[i].meterType.getCode()}));
                out.write((new byte[]{DataUtil.getByteToInt(kepcoObisData[i].meterCount)}));
                
                /**
                 * 미터의 Port Address는 Port Address Table 참조
                 * 0xFF  : 모든 미터
                 * 0xFF일 시에는 Meter Count가 1이 된다.
                 * 미터 시리얼 뒤 2자리 - 미터정보는 어디서 가져오나???
                 */
//              meterPortAddress = new byte[kepcoObisData[i].meterCount];
//              for(int j=0; j<meterPortAddress.length;j++){
//                  
//              }
                out.write(DataUtil.get3ByteToInt(Integer.parseInt(kepcoObisData[i].obisCode.getServiceTypes())));
                out.write(DataUtil.get2ByteToInt(Integer.parseInt(kepcoObisData[i].obisCode.getClassId())));
                out.write(DataUtil.get6ByteToInt(Integer.parseInt(kepcoObisData[i].obisCode.getObis())));
                out.write((new byte[]{DataUtil.getByteToInt(kepcoObisData[i].obisCode.getAttribute())}));
                out.write((new byte[]{DataUtil.getByteToInt(kepcoObisData[i].getSelectiveAccessLength())}));
          }
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
