package com.aimir.fep.protocol.nip.command;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

public class CloneOnOff extends AbstractCommand {
	public CloneOnOff() {
		super(new byte[] {(byte)0x00, (byte)0x0B});
	}	
	
	private String cloneCode="empty";
	private int cloneCount=-1;
	
	public int getCloneCount() {
		return this.cloneCount;
	}
	
	public String getCloneCode() {
		return this.cloneCode;
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
	public Command set(HashMap info) throws Exception {
		Command command = new Command();
		Command.Attribute attr = command.newAttribute();
		Command.Attribute.Data[] datas = attr.newData(1);

		command.setCommandFlow(CommandFlow.Request);
		command.setCommandType(CommandType.Set);
		datas[0].setId(getAttributeID());

		// DELETE START SP-681
		// Clone Code (PreDefined = 0x0314)
//		byte[] cc = new byte[] { (byte) 0x03, (byte) 0x14 };
		// DELETE END SP-681		
		
		// Clone Count
		int clCount = -1;
        Object obj = info.get("count");
		if (obj instanceof Integer) {
			clCount = (int)obj;
			//datas[0].setValue(new byte[] { DataUtil.getByteToInt((int) obj) });
		} else if (obj instanceof String) {
			clCount = Integer.parseInt((String) obj);
			//datas[0].setValue(new byte[] { DataUtil.getByteToInt(Integer.parseInt((String) obj)) });
		}
		
		// INSERT START SP-681
		String code = "0314";
        obj = info.get("code");
        if ((obj != null) && (obj instanceof String)) {
        	code = (String) obj;
		}		
		
		String version = "";
        obj = info.get("version");
        if ((obj != null) && (obj instanceof String)) {
        	version = (String) obj;
		}
        
        int euiCount=-1;
        obj = info.get("euicount");
		if ((obj != null) && (obj instanceof Integer)) {
			euiCount = DataUtil.getByteToInt((int)obj);
		} else if ((obj != null) &&(obj instanceof String)) {
			euiCount = Integer.parseInt((String) obj);
		}
		
		String[] euiArray = new String[20];
		obj = info.get("euitable");
        if ((obj != null) && (obj instanceof String[])) {
        	euiArray = (String[]) obj;
		}
		
		// INSERT END SP-681
		
		ByteArrayOutputStream out = null;
		try {
			out = new ByteArrayOutputStream();
			// UPDATE START SP-681
//			out.write(cc);
			out.write(Hex.encode(code));			
			// UPDATE END SP-681
			
			out.write(DataUtil.getByteToInt(clCount));
			//log.info("## clone command-set length : " + out.toByteArray().length);

			// INSERT START SP-681
			if (version.length() > 0) {
				out.write(Hex.encode(version));
			}
			if (euiCount >= 0) {
				out.write(DataUtil.getByteToInt(euiCount));
				for(int i=0; i < euiCount; i++ ) {
					if (euiArray[i] != null) {
						out.write(Hex.encode(euiArray[i]));
					}
				}
			}
			
			//System.out.println(new String(Hex.decode(out.toByteArray())));
			// INSERT END SP-681
			
			datas[0].setValue(out.toByteArray());
			
			attr.setData(datas);
			command.setAttribute(attr);
			return command;
		} catch(Exception e) {
        	log.error(e);
        	throw e;
        }
		finally {
            if (out != null) out.close();
        }
				
		//datas[0].setValue(new byte[] { (byte) 0x03, (byte) 0x14 });	// 코드 값 : 0x0314      		
	}
		
	@Override
	public void decode(byte[] data) throws Exception {
		//log.info("## Clone On/OFF bx :" + Hex.decode(data));
		
		// clone code(2byte==0x0314)
		byte[] b = new byte[2];
		System.arraycopy(data, 0, b, 0, b.length);
		cloneCode =  Hex.decode(b);
		
		// clone count(1byte)
		byte[] c = new byte[1];
		System.arraycopy(data, b.length, c, 0, c.length);
		cloneCount = DataUtil.getIntToByte(c[0]);
		
		log.info("## Clone On/OFF cloneCode :" + cloneCode + ", cloneCount :" + cloneCount);
	}
	
	public String toString() {
		String toCode = cloneCode;
		String toCount = cloneCount<0? "Unknown":Integer.toString(cloneCount);
		return "[Clone OnOff][CloneCode: "+ toCode + "][CloneCount: "+toCount+"*15min]";
	}
	
	@Override
	public void decode(byte[] bx, CommandType commandType) throws Exception {
		log.info("bx :" + bx + ", " + "commandType : " + commandType);
	}

	@Override
    public Command get(HashMap info) throws Exception {
		return null;
    }

	@Override
	public Command set() throws Exception {
		return null;
	}
	
	@Override
	public Command trap() throws Exception {
		return null;
	}

    
}
