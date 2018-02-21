package com.aimir.fep.protocol.nip.frame.payload;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.datatype.WORD;
import com.aimir.fep.protocol.fmp.frame.service.MDData;
import com.aimir.fep.util.Hex;

public class MeteringData extends PayloadFrame {
    private static Log log = LogFactory.getLog(MeteringData.class);
    
    private MDData mdData;
    
    public enum HeaderType {
        None ((byte)0x00),
        IF4Data ((byte)0x01);
        
        private byte code;
        
        HeaderType(byte code) {
            this.code = code;
        }
        
        public byte getCode() {
            return this.code;
        }
    }
    
    public enum DataType {
        None ((byte)0x00),
        DLMS ((byte)0x01),
        PULSE ((byte)0x02),
        KEPCO_DLMS ((byte)0x03),
        IF4_DLMS ((byte)0x04),
        MBUS ((byte)0x05),
        DUMMY((byte)0xAA);
        
        private byte code;
        
        DataType(byte code) {
            this.code = code;
        }
        
        public byte getCode() {
            return this.code;
        }
    }
    
    private byte[] data;
    
    private HeaderType _headerType;
    private DataType _dataType;
    private FrameHeader _frameHeader;
    private DlmsMeter[] _dlmsMeter;
    private PulseMeter[] _pulseMeter;
    private KepcoDlmsMeter[] _kepcoDlmsMeter;
    private If4DlmsMeter[] _if4DlmsMeter;
    private DummyMeter[] _dummyMeter;
    
    public DummyMeter[] getDummyMeter() {
        return _dummyMeter;
    }

    public void setDummyMeter(DummyMeter[] _dummyMeter) {
        this._dummyMeter = _dummyMeter;
    }
    
    public If4DlmsMeter[] getIf4DlmsMeter() {
        return _if4DlmsMeter;
    }

    public void setIf4DlmsMeter(If4DlmsMeter[] _if4DlmsMeter) {
        this._if4DlmsMeter = _if4DlmsMeter;
    }
    
    public KepcoDlmsMeter[] getKepcoDlmsMeter() {
        return _kepcoDlmsMeter;
    }

    public void setKepcoDlmsMeter(KepcoDlmsMeter[] _kepcoDlmsMeter) {
        this._kepcoDlmsMeter = _kepcoDlmsMeter;
    }
	
    public PulseMeter[] getPulseMeter() {
        return _pulseMeter;
    }

    public void setPulseMeter(PulseMeter[] _pulseMeter) {
        this._pulseMeter = _pulseMeter;
    }
    
    public DlmsMeter[] getDlmsMeter() {
        return _dlmsMeter;
    }

    public void setDlmsMeter(DlmsMeter[] _dlmsMeter) {
        this._dlmsMeter = _dlmsMeter;
    }
    
    public void newFrameHeader(){
        this._frameHeader = new FrameHeader();
    }
    
    public FrameHeader getFrameHeader() {
        return _frameHeader;
    }

    public void setFrameHeader(FrameHeader _frameHeader) {
        this._frameHeader = _frameHeader;
    }

    public void setHeaderType(byte code) {
        for (HeaderType f : HeaderType.values()) {
            if (f.getCode() == code) {
                _headerType = f;
                break;
            }
        }
    }
    
    public void setDataType(byte code) {
        for (DataType f : DataType.values()) {
            if (f.getCode() == code) {
                _dataType = f;
                break;
            }
        }
    }
    
    public HeaderType getHeaderType() {
        return this._headerType;
    }
    
    public DataType getDataType() {
        return this._dataType;
    }
    
    public byte[] getData() {
        return this.data;
    }
    
    public MDData getMDData() {
        return this.mdData;
    }
    
    @Override
    public void decode(byte[] bx) {
        log.debug(Hex.decode(bx));
        this.data = bx;
        
        int pos = 0;
        
        byte[] b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);//headerType
        pos+=b.length;
        setHeaderType(b[0]);
        log.debug("HEADER_TYPE[" + getHeaderType() + "]");
        
        b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);//dataType
        pos+=b.length;
        setDataType(b[0]);
        log.debug("DATA_TYPE[" + getDataType() + "]");
        
        /*
         * Frame Header and Metering Data is IF4 measurement data for soria
         */
        b = new byte[bx.length - 2];
        System.arraycopy(bx, pos, b, 0, b.length);
        pos += b.length;
        
        mdData = new MDData();
        mdData.setCnt(new WORD(1));
        mdData.setMdData(b);
        mdData.setNS("SP");
        /*
        b = new byte[8];
        System.arraycopy(bx, pos, b, 0, b.length);
        pos+=b.length;
        _frameHeader.setSid(DataUtil.getString(b) );
        
        b = new byte[20];
        System.arraycopy(bx, pos, b, 0, b.length);
        pos+=b.length;
        _frameHeader.setMid(DataUtil.getString(b) );
        
        b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        pos+=b.length;
        _frameHeader.setsType(DataUtil.getIntToByte(b[0]));
        
        b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        pos+=b.length;
        _frameHeader.setSvc(DataUtil.getIntToByte(b[0]));
        
        b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        pos+=b.length;
        _frameHeader.setVendor(b[0]);
        
        b = new byte[2];
        System.arraycopy(bx, pos, b, 0, b.length);
        pos+=b.length;
        _frameHeader.setDataCnt(DataUtil.getIntTo2Byte(b));
        
        b = new byte[2];
        System.arraycopy(bx, pos, b, 0, b.length);
        pos+=b.length;
        _frameHeader.setLength(DataUtil.getIntTo2Byte(b));
        
        switch (_dataType) {
            case DLMS: //only soria
                _dlmsMeter = new DlmsMeter[_frameHeader.getDataCnt()];
                    for (int i = 0; i < _dlmsMeter.length; i++) {
                        _dlmsMeter[i] = new DlmsMeter();
                        b = new byte[6];
                        System.arraycopy(bx, pos, b, 0, b.length);
                        pos+=b.length;
                        _dlmsMeter[i].setHeaderObis(DataUtil.getString(b));
    			
                        b = new byte[2];
                        System.arraycopy(bx, pos, b, 0, b.length);
                        pos+=b.length;
                        _dlmsMeter[i].setHeaderClass(DataUtil.getString(b));
    			
                        b = new byte[1];
                        System.arraycopy(bx, pos, b, 0, b.length);
                        pos+=b.length;
                        _dlmsMeter[i].setHeaderAttr(DataUtil.getString(b));
    			
                        b = new byte[2];
                        System.arraycopy(bx, pos, b, 0, b.length);
                        pos+=b.length;
                        _dlmsMeter[i].setHeaderLength(DataUtil.getIntTo2Byte(b));
    			
                        b = new byte[1];
                        System.arraycopy(bx, pos, b, 0, b.length);
                        pos+=b.length;
                        _dlmsMeter[i].setTagTag(DataUtil.getString(b));
    			
                        b = new byte[_frameHeader.getLength()];
                        System.arraycopy(bx, pos, b, 0, b.length);
                        pos+=b.length;
                        _dlmsMeter[i].setTagDataOrLenData(DataUtil.getString(b));
                    }
                    break;
            case PULSE:
                break;
            case KEPCO_DLMS:
                break;
            case IF4_DLMS:
                break;
            case MBUS:
                break;
            case DUMMY:
                break;
        }
        b= new byte[bx.length - 2];
        System.arraycopy(bx, pos, b, 0, b.length);
        */
    }
    
    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(
                "[MeteringData]"+
                "[headerType:"+_headerType.name()+"]"+
                "[dataType:"+_dataType.name()+"]");
                /*
                "[sid:"+_frameHeader.getSid()+"]"+
                "[mid:"+_frameHeader.getMid()+"]"+
                "[sType:"+_frameHeader.getsType()+"]"+
                "[svc:"+_frameHeader.getSvc()+"]"+
                "[vender:"+_frameHeader.getVendor().name()+"]"+
                "[dataCnt:"+_frameHeader.getDataCnt()+"]"+
                "[length:"+_frameHeader.getLength()+"]");
                */
        
        if (_dlmsMeter != null && _dlmsMeter.length > 0) {
            for (DlmsMeter dm : _dlmsMeter) {
                buf.append("[headerObis:"+dm.getHeaderObis()+"]"
                        + "[headerClass:"+dm.getHeaderClass()+"]"
                        + "[headerAttr:"+dm.getHeaderAttr()+"]"
                        + "[tagTag:"+dm.getTagTag()+"]"
                        + "[tagDataOrLenData:"+dm.getTagDataOrLenData()+"]"
                        );
            }
        }
        return buf.toString();
	}
	
    @Override
    public byte[] encode() throws Exception {return null;}

    @Override
    public void setCommandFlow(byte code){ }
   
    @Override
    public void setCommandType(byte code){ }
    
    @Override
    public byte[] getFrameTid(){ return null;}
    
    @Override
    public void setFrameTid(byte[] code){}
    
}
