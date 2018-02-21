package com.aimir.fep.bypass.sts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.command.conf.KamstrupCIDMeta;
import com.aimir.fep.util.CRC16;
import com.aimir.fep.util.CRCUtil;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FrameUtil;
import com.aimir.fep.util.Hex;

public abstract class STSDataFrame {
    private static Log log = LogFactory.getLog(STSDataFrame.class);
    
    private byte[] KMP_ADDR = new byte[8];
    private byte[] KMP_CID = new byte[2];
    // private byte[] SOF = new byte[]{0x5F};
    // private byte[] len = new byte[2];
    private byte[] data = null;
    private byte[] crc = new byte[2];
    
    protected STSDataReq req = null;
    protected STSDataRes res = null;
    
    public enum CMD {
        SetPaymentMode((byte)0x01),
        GetPaymentMode((byte)0x11),
        GetRemainingCredit((byte)0x02),
        SetToken((byte)0x03),
        GetToken((byte)0x13),
        SetTariff((byte)0x04),
        GetTariff((byte)0x14),
        SetFriendlyCredit((byte)0x05),
        GetFriendlyCredit((byte)0x15),
        SetEmergencyCredit((byte)0x06),
        GetEmergencyCredit((byte)0x16),
        GetPreviousMonthNetCharge((byte)0x07),
        GetSpecificMonthNetCharge((byte)0x17),
        Message((byte)0x08),
        FirmwareUpdateKeyWrite((byte)0x09),
        FirmwareUpdateKeyRead((byte)0x19),
        GetFirmwareUpdateInfo((byte)0x0A),
        GetCIUCommStateHistory((byte)0x0B),
        FirmwareUpdateControl((byte)0x0C),
        FirmwareUpdateBlockWrite((byte)0x0D),
        FirmwareUpdateBlockRead((byte)0x1D),
        SetSTSSetup((byte)0x0E),
        GetSTSSetup((byte)0x1E),
        SetRFSetup((byte)0x0F),
        GetRFSetup((byte)0x1F);
        
        private byte cmd;
        
        CMD(byte cmd) {
            this.cmd = cmd;
        }
        
        public byte getCmd() {
            return this.cmd;
        }
    }
    
    public void decode(byte[] bx) throws Exception {
        byte[] destuff_data = new byte[bx.length-2]; // SOF(1), EOF(1)
        System.arraycopy(bx, 1, destuff_data, 0, destuff_data.length);
        log.debug("BEFORE_DESTUFF[" + Hex.decode(destuff_data) + "]");
        destuff_data = destuff(destuff_data);
        log.debug("AFTER_DESTUFF[" + Hex.decode(destuff_data) + "]");
        
        int pos = 0;
        System.arraycopy(destuff_data, pos, KMP_ADDR, 0, KMP_ADDR.length);
        pos += KMP_ADDR.length;
        log.debug("KMP_ADDR[" + Hex.decode(KMP_ADDR) + "]");
        
        System.arraycopy(destuff_data, pos, KMP_CID, 0, KMP_CID.length);
        pos += KMP_CID.length;
        log.debug("KMP_CID[" + Hex.decode(KMP_CID) + "]");
        
        // System.arraycopy(bx, pos, SOF, 0, SOF.length);
        // pos += SOF.length;
        
        // System.arraycopy(bx, pos, len, 0, len.length);
        // pos += len.length;
        // int _len = DataUtil.getIntTo2Byte(len);
        
        data = new byte[destuff_data.length - KMP_ADDR.length - KMP_CID.length - crc.length];
        System.arraycopy(destuff_data, pos, data, 0, data.length);
        pos += data.length;
        log.debug("Data[" + Hex.decode(data) + "]");
        
        System.arraycopy(destuff_data, pos, crc, 0, crc.length);
        log.debug("ORG_CRC[" + Hex.decode(crc) + "]");
        
        byte[] crc_data = new byte[destuff_data.length - crc.length];
        System.arraycopy(destuff_data, 0, crc_data, 0, crc_data.length);
        // 0x3F 0x01 => CRC : 0x05 0x8A
        byte[] validate_crc = CRCUtil.calculate_Xmodem_Ccitt(crc_data);
        log.debug("VAL_CRC[" + Hex.decode(validate_crc) + "]");

         if (validate_crc[0] != crc[0] || validate_crc[1] != crc[1]) throw new Exception("CRC Error : Invalid Frame");
        // if (!FrameUtil.checkCRC(bx)) throw new Exception("CRC Error : Invalid Frame");
        
        res = new STSDataRes();
        res.decode(data);
    }
    
    public byte[] encode(byte[] req) throws IOException {
        ByteArrayOutputStream out = null;
        try {
            byte[] addr = new byte[]{(byte)0xFE, 0x01, 0x03, 0x00, (byte)0x02, 0x00, (byte)0xAB, 0x01};
            byte[] cid = new byte[]{0x1E, 0x03};
            out = new ByteArrayOutputStream();
            out.write(addr);
            out.write(cid);
            out.write(req);
            byte[] crc = CRCUtil.calculate_Xmodem_Ccitt(out.toByteArray());
            out.write(crc);
            byte[] stuff_data = KamstrupCIDMeta.stuff(out.toByteArray());
            
            out = new ByteArrayOutputStream();
            out.write(new byte[]{(byte)0x80});
            out.write(stuff_data);
            out.write(new byte[]{(byte)0x0D});
            
            return out.toByteArray();
        }
        finally {
            if (out != null) out.close();
        }
    }
    
    public byte[] encode() throws Exception {
        return encode(req.encode());
    }
    
    public STSDataRes getDataRes() {
        return res;
    }
    
    public static byte[] destuff(byte[] res){
        String decodeStr=Hex.decode(res);
        if(decodeStr.contains("1B")){
            decodeStr=decodeStr.replaceAll("1B7F", "80");
            decodeStr=decodeStr.replaceAll("1BBF", "40");
            decodeStr=decodeStr.replaceAll("1BF2", "0D");
            decodeStr=decodeStr.replaceAll("1BF9", "06");
            decodeStr=decodeStr.replaceAll("1BE4", "1B");
            return Hex.encode(decodeStr);
        }
        else return Hex.encode(decodeStr);        
    }
}
