package com.aimir.fep.protocol.nip.command;

import java.util.HashMap;

import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;

public class NbPlcInformation extends AbstractCommand{
	private String venderCode;
    private String fWVersioin;
    private String ipV6Address;
    private String lqi;
    private int tmr;
    private Modulation modulation;
    private BandPlan bandPlan;
 
    public NbPlcInformation() {
        super(new byte[] {(byte)0x10, (byte)0x02});
    }
    
    public enum Modulation {
        Robo((byte)0x00),
        Bpsk ((byte)0x01),
        Qpsk ((byte)0x02),
        Eightpsk ((byte)0x03),
        SuperRobo ((byte)0x05);
        
        private byte code;
        
        Modulation(byte code) {
            this.code = code;
        }
        
        public byte getCode() {
            return this.code;
        }
    }
    
    public Modulation getModulation() {
        return modulation;
    }
    
    public enum BandPlan {
    	CENELEC_A_36((byte)0x00),
    	CENELEC_A_25((byte)0x01),
    	CENELEC_B((byte)0x02),
    	CENELEC_BC((byte)0x03),
    	CENELEC_BCD((byte)0x04),
    	FCC_LOW_BAND((byte)0x05),
    	FCC_HIGH_BAND((byte)0x06),
    	FCC_FULL_BAND((byte)0x07);
        
        private byte code;
        
        BandPlan(byte code) {
            this.code = code;
        }
        
        public byte getCode() {
            return this.code;
        }
    }
    
    public BandPlan getBandPlan() {
        return bandPlan;
    }
    
    public String getVenderCode() {
 		return venderCode;
 	}

 	public void setVenderCode(String venderCode) {
 		this.venderCode = venderCode;
 	}

 	public String getFWVersioin() {
 		return fWVersioin;
 	}

 	public void setFWVersioin(String fWVersioin) {
 		this.fWVersioin = fWVersioin;
 	}

 	public String getIpv6Address() {
 		return ipV6Address;
 	}

 	public void setIpv6Address(String ipV6Address) {
 		this.ipV6Address = ipV6Address;
 	}

 	public String getLqi() {
 		return lqi;
 	}

 	public void setLqi(String lqi) {
 		this.lqi = lqi;
 	}

 	public int getTmr() {
 		return tmr;
 	}

 	public void setTmr(int tmr) {
 		this.tmr = tmr;
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
    public Command trap() throws Exception {
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
    public void decode(byte[] bx) {
        int pos = 0;
        
        byte[] b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        venderCode = String.valueOf(DataUtil.getIntToByte(b[0]));
        
        b = new byte[4];
        pos += b.length;
        System.arraycopy(bx, pos, b, 0, b.length);
        fWVersioin = new String(b).trim();
        //fWVersioin = String.valueOf(DataUtil.getIntToByte(b[0]));

        b = new byte[16];
        pos += b.length;
        System.arraycopy(bx, pos, b, 0, b.length);
        ipV6Address = DataUtil.decodeIPv6Addr(b);
        
        b = new byte[16];
        System.arraycopy(bx, pos, b, 0, b.length);
        pos += b.length;
        lqi = new String(b).trim();
        
        b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        tmr = DataUtil.getIntToByte(b[0]);
        pos += b.length;
        
        b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        for (Modulation m : Modulation.values()) {
            if (m.getCode() == b[0]) {
            	modulation = m;
                break;
            }
        }
        pos += b.length;
        
        b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        for (BandPlan m : BandPlan.values()) {
            if (m.getCode() == b[0]) {
            	bandPlan = m;
                break;
            }
        }
    }
    
	@Override
	public String toString() {
	    return "[NbPlcInformation]"+
	    	   "[venderCode:"+venderCode+"]"+
	    	   "[fWVersioin:"+fWVersioin+"]"+
	    	   "[ipV6Address:"+ipV6Address+"]"+
	    	   "[lqi:"+lqi+"]"+
	    	   "[tmr:"+tmr+"]"+
	    	   "[modulation:"+modulation.name()+"]"+
	    	   "[bandPlan:"+bandPlan.name()+"]";
	}

	@Override
	public Command get(HashMap p) throws Exception{return null;}
	@Override
	public Command set() throws Exception{return null;}
	@Override
	public Command set(HashMap p) throws Exception{return null;}

    @Override
    public void decode(byte[] p1, CommandType commandType)
                    throws Exception {
        // TODO Auto-generated method stub
        
    }
}
