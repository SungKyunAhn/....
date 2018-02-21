package com.aimir.fep.protocol.nip.command;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import com.aimir.fep.protocol.nip.command.ResponseResult.Status;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.protocol.security.OacServerApi;

public class MeterSharedKey extends AbstractCommand{
    public enum RequestInfo {
        DCU_HES((byte)0x00),
        Modem((byte)0x01);
        private byte code;
        RequestInfo(byte code) {
            this.code = code;
        }
        public byte getCode() {
            return this.code;
        }
    }
    
    private RequestInfo _requestInfo;
    private String modemEui;
    private int meterSerialSize;
    private String meterSerial;
    private String masterKey;
    private String unicastKey;
    private String multicastKey;
    private String authenticationKey;

    /**
	 * @return the masterKey
	 */
	public String getMasterKey() {
		return masterKey;
	}

	/**
	 * @param masterKey the masterKey to set
	 */
	public void setMasterKey(String masterKey) {
		this.masterKey = masterKey;
	}

	/**
	 * @return the unicastKey
	 */
	public String getUnicastKey() {
		return unicastKey;
	}

	/**
	 * @param unicastKey the unicastKey to set
	 */
	public void setUnicastKey(String unicastKey) {
		this.unicastKey = unicastKey;
	}

	/**
	 * @return the multicastKey
	 */
	public String getMulticastKey() {
		return multicastKey;
	}

	/**
	 * @param multicastKey the multicastKey to set
	 */
	public void setMulticastKey(String multicastKey) {
		this.multicastKey = multicastKey;
	}

	/**
	 * @return the authenticationKey
	 */
	public String getAuthenticationKey() {
		return authenticationKey;
	}

	/**
	 * @param authenticationKey the authenticationKey to set
	 */
	public void setAuthenticationKey(String authenticationKey) {
		this.authenticationKey = authenticationKey;
	}

	public MeterSharedKey() {
        super(new byte[] {(byte)0xC0, (byte)0x05});
    }
    
    public String getModemEui() {
        return modemEui;
    }

    public void setModemEui(String modemEui) {
        this.modemEui = modemEui;
    }

    public int getMeterSerialSize() {
        return meterSerialSize;
    }

    public void setMeterSerialSize(int meterSerialSize) {
        this.meterSerialSize = meterSerialSize;
    }

    public String getMeterSerial() {
        return meterSerial;
    }

    public void setMeterSerial(String meterSerial) {
        this.meterSerial = meterSerial;
    }

//    public String getMeterSharedKey() {
//        return meterSharedKey;
//    }
//
//    public void setMeterSharedKey(String meterSharedKey) {
//        this.meterSharedKey = meterSharedKey;
//    }
    
    private Status status;
    
    public Status getStatus() {
        return status;
    }
    
    public void setRequestInfo(byte code) {
        for (RequestInfo c : RequestInfo.values()) {
            if (c.getCode() == code) {
                _requestInfo = c;
                break;
            }
        }
    }
    
    @Override
    public void decode(byte[] bx) {
//        int pos = 0;
//        byte[] b = new byte[2];
//        System.arraycopy(bx, 0, b, 0, b.length);
//        pos+=b.length;
//        for (Status s : Status.values()) {
//            if (s.getCode()[0] == b[0] && s.getCode()[1] == b[1]) {
//                status = s;
//                break;
//            }
//        }
//        
//        b = new byte[16];
//        System.arraycopy(bx, pos, b, 0, b.length);
//        meterSharedKey = DataUtil.getString(b);
    	int pos = 0;
    	byte[]  b = new byte[16];
    	System.arraycopy(bx, pos, b, 0, b.length);
    	masterKey = DataUtil.getString(b);
    	pos += b.length;
    	System.arraycopy(bx, pos, b, 0, b.length);
    	unicastKey = DataUtil.getString(b);
    	
    	pos += b.length;
    	System.arraycopy(bx, pos, b, 0, b.length);
    	multicastKey = DataUtil.getString(b);
    	
    	pos += b.length;
    	System.arraycopy(bx, pos, b, 0, b.length);
    	authenticationKey = DataUtil.getString(b); 	
    }
	
    @Override
    public Command get(HashMap info) throws Exception {
        Command command = new Command();
        Command.Attribute attr = command.newAttribute();
        Command.Attribute.Data[] datas = attr.newData(1);
       
        command.setCommandFlow(CommandFlow.Response_Trap);
        command.setCommandType(CommandType.Get);
        datas[0].setId(getAttributeID());
       
    	byte[] dummy = new byte[16];
    	for ( int i = 0; i < dummy.length; i++){
    		dummy[i] = 0;
    	}
        
        byte requestInfo =  (byte)((int)info.get("requestInfo") &0xff);
    	this.setRequestInfo(requestInfo);
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
	        if ( requestInfo == RequestInfo.Modem.getCode() ){
	        	byte [] eui = DataUtil.get8ByteToInt((long)info.get("modemEui"));
	        	this.modemEui = new String(eui);

	        	this.meterSerialSize = (int)info.get("meterSerialSize");
	        	this.meterSerial = (String)info.get("meterSerial");

	    		OacServerApi api = new OacServerApi();
	    		HashMap <String, String>  sharedKeys = api.getMeterSharedKey(this.modemEui, this.meterSerial);
	    		if ( sharedKeys == null ){
		        	out.write(dummy);  //MasterKey
		        	out.write(dummy);  //UnicastKey
		        	out.write(dummy);  //MulticastKey
		        	out.write(dummy);  //AuthenticationKey
	    		}
	    		else { 
		    		if ( (masterKey = sharedKeys.get("MasterKey"))== null){
		    			out.write(dummy); 
		    		}
		    		else{ 
		    			out.write(DataUtil.getBCD(masterKey));
		    		}
		    		if ((unicastKey = sharedKeys.get("UnicastKey")) == null){
		    			out.write(dummy); 
		    		}
		    		else {
		    			out.write(DataUtil.getBCD(unicastKey));
		    		}
	    			if ( (multicastKey = sharedKeys.get("MulticastKey")) == null ){
	    				out.write(dummy); 
	    			}else {
	    				out.write(DataUtil.getBCD(multicastKey));
	    			}
	    			if ( (authenticationKey = sharedKeys.get("AuthenticationKey")) == null ){
	    				out.write(dummy); 
	    			}else {
	    				out.write(DataUtil.getBCD(authenticationKey));
	    			} 
	    		}
	        }
	        else { //RequestInfo.DCU_HES
	        	out.write(dummy);  //MasterKey
	        	out.write(dummy);  //UnicastKey
	        	out.write(dummy);  //MulticastKey
	        	out.write(dummy);  //AuthenticationKey     	
	        }
            datas[0].setValue(out.toByteArray());
            attr.setData(datas);
            command.setAttribute(attr);
            return command; 
        }
        finally {
        	if (out != null) out.close();
        }    
	        
    //    ByteArrayOutputStream out = null;
//        try {
//            out = new ByteArrayOutputStream();
//        
//            out.write(new byte[]{DataUtil.getByteToInt((int)info.get("requestInfo"))});
//            out.write(DataUtil.get8ByteToInt((long)info.get("modemEui")));
//            out.write(new byte[]{DataUtil.getByteToInt((int)info.get("meterSerialSize"))});
//            out.write(DataUtil.getBCD((String)info.get("meterSerial")));
//           
//            datas[0].setValue(out.toByteArray());
//            attr.setData(datas);
//            command.setAttribute(attr);
//            return command;
//        }
//        finally {
//            if (out != null) out.close();
//        }
    }
	
    @Override
    public Command set(HashMap info) throws Exception {
        Command command = new Command();
        Command.Attribute attr = command.newAttribute();
        Command.Attribute.Data[] datas = attr.newData(1);
        
        command.setCommandFlow(CommandFlow.Request);
        command.setCommandType(CommandType.Set);
        datas[0].setId(getAttributeID());
        datas[0].setValue(DataUtil.getBCD((String)info.get("meterSharedKey")));
        attr.setData(datas);
        command.setAttribute(attr);
        return command;
    }
	
    @Override
    public String toString() {
        return "[MeterSharedKey]"+
                "[status:"+status.name()+"]"+
                "[masterKey:"+masterKey+"]"+
                "[unicastKey:"+unicastKey+"]"+
                "[multicastKey:"+multicastKey+"]"+
                "[authenticationKey:"+authenticationKey+"]";
    }
    
    @Override
    public Command get() throws Exception{return null;}

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
