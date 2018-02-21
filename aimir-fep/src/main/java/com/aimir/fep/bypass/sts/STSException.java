package com.aimir.fep.bypass.sts;

public class STSException extends Exception {
    private String msg = null;
    
    public STSException(byte[] code) {
        setMsg(code);
    }
    
    public STSException(byte[] code, Throwable t) {
        super(t);
        setMsg(code);
    }
    
    private void setMsg(byte[] code) {
        switch (code[0]) {
        case 0x01 : msg = "INVALID_TOT_LEN"; break;
        case 0x02 : msg = "INVALID_CMD"; break;
        case 0x03 : msg = "FCS_ERR"; break;
        case 0x04 : msg = "INVALID_MODE"; break;
        case 0x05 : msg = "INVALID_LEN"; break;
        case 0x06 : msg = "INVALID_DT"; break;
        case 0x07 : msg = "INVALID_DEC_POINT"; break;
        case 0x08 : msg = "INVALID_TOKEN"; break;
        case 0x09 : msg = "DUP_TOKEN"; break;
        case 0x0A : msg = "STS_SETUP_INVALID"; break;
        case 0x0B : msg = "STS_KEY_CHANGE_FAIL"; break;
        case 0x0C : msg = "STS_KEY_CHANGE_ALREADY_DONE"; break;
        case 0x10 : msg = "INVALID_DATE"; break;
        case 0x11 : msg = "INVALID_TARIFF_MODE"; break;
        case 0x12 : msg = "INVALID_COUNT"; break;
        case 0x20 : msg = "INVALID_DAYTYPE"; break;
        case 0x21 : msg = "INVALID_FC_MODE"; break;
        case 0x22 : msg = "INVALID_EC_MODE"; break;
        case 0x23 : msg = "INVALID_MONTH"; break;
        case 0x30 : msg = "FIRMWARE_UPDATE_CONTROL_INVALID"; break;
        case 0x31 : msg = "FIRMWARE_UPDATE_ERASE_FAIL"; break;
        case 0x32 : msg = "FIRMWARE_UPDATE_CHECK_FAI"; break;
        case 0x33 : msg = "FIRMWARE_UPDATE_START_FAIL"; break;
        case 0x34 : msg = "FIRMWARE_UPDATE_READ_FAIL"; break;
        case 0x35 : msg = "FIRMWARE_UPDATE_VERIFY_FAIL"; break;
        case 0x36 : msg = "FIRMWARE_UPDATE_VERIFY_READ_FAIL"; break;
        case 0x37 : msg = "FIRMWARE_UPDATE_WRITE_FAIL"; break;
        case 0x38 : msg = "FIRMWARE_UPDATE_WRITE_OVER"; break;
        case 0x39 : msg = "FIRMWARE_UPDATE_INFORMATION_READ_FAIL"; break;
        case 0x3A : msg = "FIRMWARE_UPDATE_INVALID_KEY_NUMBER"; break;
        case 0x3B : msg = "FIRMWARE_UPDATE_INVALID_LENGTH"; break;
        case 0x40 : msg = "DISPLAY_MESSAGE_LENGTH_INVALID"; break;
        case 0x41 : msg = "DISPLAY_MESSAGE_NO_SPACE"; break;
        case 0x45 : msg = "RF_SETUP_FAIL"; break;
        }
    }
    
    public String getMsg() {
        return msg;
    }
}
