package com.aimir.fep.meter.parser.vcTable;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.vc.VMCommonData;
import com.aimir.fep.meter.parser.VCParser;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;

public class VM_DAYMAX
{
    private static Log log = LogFactory.getLog(VM_DAYMAX.class);

    private byte[] data;

    private int _dataCntLen = 1;
    private int _timestampLen = 7;
    private int _maxCuvLen = 4;
    private int _maxCvLen = 4;
    private int _ctLen = 2;
    private int _cpLen = 3;
    private int pw = 0;
    private int tu = 0;
    private int pu = 0;

    private int dataCount = 0;

    private VMCommonData[] vmData;
    private List<VMCommonData> vmlist = null;
    
    public VM_DAYMAX(byte[] data, int pw, int tu, int pu) throws Exception {
        this.data = data;
        this.pw = pw;
        this.tu = tu;
        this.pu = pu;
        try {
            parseData();
            getVMData();
            log.debug(toString());
        } catch(Exception e){
            log.error("parsing error["+e.getMessage()+"]",e);
            throw e;
        }
    }
    
    public void parseData() throws Exception {
        int pos = 0;
                
        dataCount = DataUtil.getIntToBytes(DataUtil.select(data, pos, _dataCntLen));
        pos += _dataCntLen;
        if(dataCount!=0){
            vmlist = new ArrayList<VMCommonData>();
        }

        for(int i=0; i<dataCount;i++) {
            if(data[pos] == 0x00 && 
                    data[pos+1] == 0x00 && 
                    data[pos+2] == 0x00 && 
                    data[pos+3]== 0x00){
                     return;
                 }
            VMCommonData vmData = new VMCommonData();
            vmData = new VMCommonData();
            vmData.setVM_KIND(VCParser.VM_KIND_DAYMAX);
            String _datetime = DataFormat.getDateTime(DataUtil.select(data, pos, _timestampLen));
            vmData.setTimestamp(_datetime);
            vmData.setYyyymmdd(_datetime.substring(0,8));
            vmData.setYyyymm(_datetime.substring(0,6));
            vmData.setHhmmss(_datetime.substring(8,14));
            vmData.setHhmm(_datetime.substring(8,12));
            pos += _timestampLen;
            vmData.setMax_cuv(DataFormat.hex2dec(DataUtil.select(data, pos, _maxCuvLen)) * Math.pow(10, pw));
            pos += _maxCuvLen;
            vmData.setMax_cv(DataFormat.hex2dec(DataUtil.select(data, pos, _maxCvLen)) * Math.pow(10, pw));
            pos += _maxCvLen;
            vmData.setCt(DataFormat.hex2signed16(DataUtil.select(data, pos, _ctLen)) * Math.pow(10, tu));
            pos += _ctLen;
            vmData.setCp(DataFormat.hex2dec(DataUtil.select(data, pos, _cpLen)) * Math.pow(10, pu));
            pos += _cpLen;            
            vmlist.add(vmData);
        }
    }
    
    public VMCommonData[] getVMData() {
        
        if(vmlist != null && vmlist.size() > 0){
            vmData = null;
            Object[] obj = vmlist.toArray();
            
            vmData = new VMCommonData[obj.length];
            for(int i = 0; i < obj.length; i++){
                vmData[i] = (VMCommonData)obj[i];
            }
            return vmData;
        }
        else
        {
            return null;
        }
    }
    
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        if(vmData != null && vmData.length > 0){
            for(int i = 0; i < vmData.length; i++)
            {
                sb.append(vmData[i].toString());
            }
        }
        return sb.toString();
    }
}
