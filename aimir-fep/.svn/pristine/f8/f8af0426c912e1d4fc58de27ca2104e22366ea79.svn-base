package com.aimir.fep.bypass.sts.cmd;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.bypass.sts.STSDataFrame;
import com.aimir.fep.bypass.sts.STSException;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

public class GetSuniFirmwareUpdateInfoRes extends STSDataFrame {
    private static Log log = LogFactory.getLog(GetSuniFirmwareUpdateInfoRes.class);
    
    private int status = 0;
    private String moduleSerial = "";
    private int moduleTypeId = 0;
    private int moduleRevision = 0;
    private int moduleMinorVer = 0;
    private int moduleMajorVer = 0;
    private int fileCtrl = 0;
    private int fileTypeId = 0;
    private int fileRevision = 0;
    private int fileMinorVer = 0;
    private int fileMajorVer = 0;
    private int fileByteSize = 0;
    private Map<String, Object> dataMap = null;
    
    public GetSuniFirmwareUpdateInfoRes(byte[] bx) throws Exception {
        super.decode(bx);
        parse();
    }
    
    private void parse() throws Exception {
        if (res.getResult() == 0x00) {
        	dataMap = new HashMap<String, Object>();
            int pos = 0;
            byte[] b = new byte[1];
            System.arraycopy(res.getRdata(), pos, b, 0, b.length);
            pos += b.length;
            status = DataUtil.getIntToBytes(b);
            dataMap.put("status", status);
            log.debug("STATUS[" + status + "]");
            
            b = new byte[8];
            System.arraycopy(res.getRdata(), pos, b, 0, b.length);
            pos += b.length;
            moduleSerial = new String(b);
            dataMap.put("moduleSerial", Hex.decode(b));
            log.debug("MODULE_SERIAL[" + moduleSerial + "] HEX[" + Hex.decode(b) + "]");
            
            b = new byte[2];
            System.arraycopy(res.getRdata(), pos, b, 0, b.length);
            pos += b.length;
            moduleTypeId = DataUtil.getIntTo2Byte(b);
            dataMap.put("moduleTypeId", moduleTypeId);
            log.debug("MODULE_TYPE_ID[" + moduleTypeId + "]");
            
            b = new byte[1];
            System.arraycopy(res.getRdata(), pos, b, 0, b.length);
            pos += b.length;
            moduleRevision = DataUtil.getIntToBytes(b);
            dataMap.put("moduleRevision", moduleRevision);
            log.debug("MODULE_REVISION[" + moduleRevision + "]");
            
            b = new byte[1];
            System.arraycopy(res.getRdata(), pos, b, 0, b.length);
            pos += b.length;
            moduleMinorVer = DataUtil.getIntToBytes(b);
            dataMap.put("moduleMinorVer", moduleMinorVer);
            log.debug("MODULE_MINOR_VER[" + moduleMinorVer + "]");
            
            b = new byte[1];
            System.arraycopy(res.getRdata(), pos, b, 0, b.length);
            pos += b.length;
            moduleMajorVer = DataUtil.getIntToBytes(b);
            dataMap.put("moduleMajorVer", moduleMajorVer);
            log.debug("MODULE_MAJOR_VER[" + moduleMajorVer + "]");
            
            b = new byte[1];
            System.arraycopy(res.getRdata(), pos, b, 0, b.length);
            pos += b.length;
            fileCtrl = DataUtil.getIntToBytes(b);
            dataMap.put("fileCtrl", fileCtrl);
            log.debug("FILE_CONTROL[" + fileCtrl + "]");
            
            b = new byte[2];
            System.arraycopy(res.getRdata(), pos, b, 0, b.length);
            pos += b.length;
            fileTypeId = DataUtil.getIntTo2Byte(b);
            dataMap.put("fileTypeId", fileTypeId);
            log.debug("FILE_TYPE_ID[" + fileTypeId + "]");
            
            b = new byte[1];
            System.arraycopy(res.getRdata(), pos, b, 0, b.length);
            pos += b.length;
            fileRevision = DataUtil.getIntToBytes(b);
            dataMap.put("fileRevision", fileRevision);
            log.debug("FILE_REVISION[" + fileRevision + "]");
            
            b = new byte[1];
            System.arraycopy(res.getRdata(), pos, b, 0, b.length);
            pos += b.length;
            fileMinorVer = DataUtil.getIntToBytes(b);
            dataMap.put("fileMinorVer", fileMinorVer);
            log.debug("FILE_MINOR_VER[" + fileMinorVer + "]");
            
            b = new byte[1];
            System.arraycopy(res.getRdata(), pos, b, 0, b.length);
            pos += b.length;
            fileMajorVer = DataUtil.getIntToBytes(b);
            dataMap.put("fileMajorVer", fileMajorVer);
            log.debug("FILE_MAJOR_VER[" + fileMajorVer + "]");
            
            b = new byte[4];
            System.arraycopy(res.getRdata(), pos, b, 0, b.length);
            pos += b.length;
            fileByteSize = DataUtil.getIntTo4Byte(b);
            dataMap.put("fileByteSize", fileByteSize);
            log.debug("FILE_BYTE_SIZE[" + fileByteSize + "]");
            
            b = new byte[2];
            System.arraycopy(res.getRdata(), pos, b, 0, b.length);
            pos += b.length;
            log.debug("FILE_CRC[" + Hex.decode(b) + "]");
        }
        else throw new STSException(res.getRdata());
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getModuleSerial() {
        return moduleSerial;
    }

    public void setModuleSerial(String moduleSerial) {
        this.moduleSerial = moduleSerial;
    }

    public int getModuleTypeId() {
        return moduleTypeId;
    }

    public void setModuleTypeId(int moduleTypeId) {
        this.moduleTypeId = moduleTypeId;
    }

    public int getModuleRevision() {
        return moduleRevision;
    }

    public void setModuleRevision(int moduleRevision) {
        this.moduleRevision = moduleRevision;
    }

    public int getModuleMinorVer() {
        return moduleMinorVer;
    }

    public void setModuleMinorVer(int moduleMinorVer) {
        this.moduleMinorVer = moduleMinorVer;
    }

    public int getModuleMajorVer() {
        return moduleMajorVer;
    }

    public void setModuleMajorVer(int moduleMajorVer) {
        this.moduleMajorVer = moduleMajorVer;
    }

    public int getFileCtrl() {
        return fileCtrl;
    }

    public void setFileCtrl(int fileCtrl) {
        this.fileCtrl = fileCtrl;
    }

    public int getFileTypeId() {
        return fileTypeId;
    }

    public void setFileTypeId(int fileTypeId) {
        this.fileTypeId = fileTypeId;
    }

    public int getFileRevision() {
        return fileRevision;
    }

    public void setFileRevision(int fileRevision) {
        this.fileRevision = fileRevision;
    }

    public int getFileMinorVer() {
        return fileMinorVer;
    }

    public void setFileMinorVer(int fileMinorVer) {
        this.fileMinorVer = fileMinorVer;
    }

    public int getFileMajorVer() {
        return fileMajorVer;
    }

    public void setFileMajorVer(int fileMajorVer) {
        this.fileMajorVer = fileMajorVer;
    }

    public long getFileByteSize() {
        return fileByteSize;
    }

    public void setFileByteSize(int fileByteSize) {
        this.fileByteSize = fileByteSize;
    }
    
    public Map<String, Object> getDataMap() {
        return dataMap;
    }
    
}
