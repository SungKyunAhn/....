package com.aimir.fep.meter.parser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.HMData;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.data.vc.VCEventLogData;
import com.aimir.fep.meter.data.vc.VCParameterLogData;
import com.aimir.fep.meter.data.vc.VMCommonData;
import com.aimir.fep.meter.parser.actarisVCTable.VM_CORUSINFO;
import com.aimir.fep.meter.parser.actarisVCTable.VM_EVENTLOG;
import com.aimir.fep.meter.parser.actarisVCTable.VM_PARAMLOG;
import com.aimir.fep.meter.parser.actarisVCTable.VM_SEVCDINFO;
import com.aimir.fep.meter.parser.vcTable.LP;
import com.aimir.fep.meter.parser.vcTable.VM_DAYMAX;
import com.aimir.fep.meter.parser.vcTable.VM_DAYSTAT;
import com.aimir.fep.meter.parser.vcTable.VM_MONTHMAX;
import com.aimir.fep.meter.parser.vcTable.VM_MONTHSTAT;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Util;
import com.aimir.model.system.Supplier;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeLocaleUtil;

public class CORUS extends VCParser 
{
	private static final long serialVersionUID = -3572506194588182173L;

	private static Log log = LogFactory.getLog(CORUS.class);

    private byte[] data;

    private byte[] _vcid = new byte[20];
    private byte[] _portno = new byte[1];
    private byte[] _vct = new byte[7];
    private byte[] _cuv = new byte[8];
    private byte[] _cv = new byte[8];
    private byte[] _ct = new byte[4];
    private byte[] _cp = new byte[4];
    private byte[] _period = new byte[1];
    private byte[] _pw = new byte[1];
    private byte[] _tu = new byte[1];
    private byte[] _pu = new byte[1];
    private byte[] _lpBlockCount = new byte[1];
    private int[] _periodType = {0,5,15,30,60,60*24};
    private int _ltLen = 7;
    private int _lpCountLen = 1;
    private int _lpLen = 14;
    private int _vmKindLen = 1;
    private int _vmDataLen = 2;
    private byte[] _errCode = new byte[1];

    private String meterId = null;
    private LP lpTable = null;
    private ArrayList<LPData> lpList = null;
    private ArrayList vmList = null;
    private HMData currentData = null;
    private int flag = 0;
    /**
     * Volume corrector id
     */
    private String vcid = null;
    /**
     * port number
     */
    private int portno = 0;
    /**
     * Volume corrector timestamp
     */
    private String vct = null;
    /**
     * Current Unconverted volume
     * real value = cuv * pw 
     */
    private double cuv = 0d;
    /**
     * Current converted volume
     * real value = cv * pw 
     */
    private double cv = 0d;
    /**
     * Current Temperature
     * real value = ct * tu 
     */
    private double ct = 0d;
    /**
     * Current Pressure
     * real value = cp * pu
     */
    private double cp = 0d;
    /**
     * Period
     * 1(5min) 2(15min) 3(30min) 4(1hour) 5(1day)
     */
    private int period = 30;
    /**
     * Pulse weight
     * -127 ~ 127 (m3)
     */
    private int pw = 0;
    /**
     * Temperature unit
     * -127 ~ 127  ct * 10^n
     */
    private int tu = 0;
    /**
     * Pressure unit
     * -127 ~ 127  ct * 10^n
     */
    private int pu = 0;
    /**
     * Date count
     */
    private int lpBlockCount = 0;    

    public CORUS(String meterId) {
        this.meterId = meterId;
    }


    /**
     * parse data
     * @param data 
     */
    public void parse(byte[] data) throws Exception
    {
        this.data = data;
        int pos = 0;
        log.debug("this.data.length["+this.data.length+"]");
        log.debug("_vcid.length+_portno.length+_errCode.length["+(_vcid.length+_portno.length+_errCode.length)+"]");
        if(this.data.length == (_vcid.length+_portno.length+_errCode.length))
        {
            System.arraycopy(data, pos, _vcid, 0, _vcid.length);
            pos += _vcid.length;
            System.arraycopy(data, pos, _portno, 0, _portno.length);
            pos += _portno.length;
            this.flag = data[pos];
            vcid = new String(_vcid).trim();
            log.debug("VCID[" + vcid + "]");
            portno = DataUtil.getIntToBytes(_portno);
            log.debug("PORTNO[" + portno + "]");
            log.debug("ERRCODE[" + this.flag + "]");
            return;
        }else{
        	log.warn("'this.data.length' does not equal to '_vcid.length+_portno.length+_errCode.length'");
        }

        System.arraycopy(data, pos, _vcid, 0, _vcid.length);
        pos += _vcid.length;
        System.arraycopy(data, pos, _portno, 0, _portno.length);
        pos += _portno.length;
        System.arraycopy(data, pos, _vct, 0, _vct.length);
        pos += _vct.length;
        System.arraycopy(data, pos, _cuv, 0, _cuv.length);
        pos += _cuv.length;
        System.arraycopy(data, pos, _cv, 0, _cv.length);
        pos += _cv.length;
        System.arraycopy(data, pos, _ct, 0, _ct.length);
        pos += _ct.length;
        System.arraycopy(data, pos, _cp, 0, _cp.length);
        pos += _cp.length;
        System.arraycopy(data, pos, _period, 0, _period.length);
        pos += _period.length;
        System.arraycopy(data, pos, _pw, 0, _pw.length);
        pos += _pw.length;
        System.arraycopy(data, pos, _tu, 0, _tu.length);
        pos += _tu.length;
        System.arraycopy(data, pos, _pu, 0, _pu.length);
        pos += _pu.length;
        System.arraycopy(data, pos, _lpBlockCount, 0, _lpBlockCount.length);
        pos += _lpBlockCount.length;

        vcid = new String(_vcid).trim();
        log.debug("VCID[" + vcid + "]");
        portno = DataUtil.getIntToBytes(_portno);
        log.debug("PORTNO[" + portno + "]");
        vct = DataFormat.getDateTime(_vct);
        log.debug("VCT[" + vct + "]");
        pw = DataFormat.hex2signed8(_pw[0]);
        log.debug("PW[" + pw + "]");
        tu = DataFormat.hex2signed8(_tu[0]);
        log.debug("TU[" + tu + "]");
        pu = DataFormat.hex2signed8(_pu[0]);
        log.debug("PU[" + pu + "]");
        cuv = DataFormat.hex2long(_cuv) * Math.pow(10, pw);
        log.debug("CUV[" + cuv + "]");
        cv = DataFormat.hex2long(_cv) * Math.pow(10, pw);
        log.debug("CV[" + cv + "]");
        ct = DataFormat.hex2dec(_ct) * Math.pow(10, tu);
        log.debug("CT[" + ct + "]");
        cp = DataFormat.hex2dec(_cp) * Math.pow(10, pu);
        log.debug("CP[" + cp + "]");
        period = _periodType[DataUtil.getIntToBytes(_period)];
        log.debug("PERIOD[" + period + "]");
        lpBlockCount = DataUtil.getIntToBytes(_lpBlockCount);
        log.debug("LPBLOCKCNT[" + lpBlockCount + "]");        
       
        String lt = "";
        int lpcnt = 0;
        byte[] lp = null;
        lpList = new ArrayList<LPData>();
        for (int i = 0; i < lpBlockCount; i++) {
            lt = DataFormat.getDateTime(DataUtil.select(data, pos, _ltLen));
            pos += _ltLen;
            lpcnt = DataUtil.getIntToBytes(DataUtil.select(data, pos, _lpCountLen));
            if(lpcnt == 0){
            	log.info("LPCNT is 0");
            	return;
            }
            	
            pos += _lpCountLen;
            lp = DataUtil.select(data, pos, lpcnt * _lpLen);
            pos += (lpcnt * _lpLen);
            
            
            lpTable = new LP(lt, lpcnt, pw, tu, pu, period, meterId, lp);
            for (int j = 0; lpTable != null && lpTable.getLPData() != null && j < lpTable.getLPData().length; j++) {
                lpList.add(lpTable.getLPData()[j]);
            }
        }
        
        currentData = new HMData();
        String _datetime = vct;
        Double momentFlow = 0d;
        if(lpList != null && lpList.size() > 0)
        {
            LPData lastLP = (LPData)lpList.get(lpList.size()-1);
            Double[] tempch = lastLP.getCh();
            if(tempch.length == 5){
                if(lastLP.getDatetime().substring(0,8).equals(_datetime.substring(0,8))){
                    momentFlow = tempch[4];
                }
            }
        }

        log.debug("VCT TIMESTAMP[" + _datetime + "]");
        currentData.setDate(_datetime.substring(0,8));
        currentData.setTime(_datetime.substring(8,12));
        currentData.setKind("current");  
        currentData.setChannelCnt(5);
        currentData.setCh(new Double[]{new Double(cuv),
                                       new Double(cv),
                                       new Double(ct),
                                       new Double(cp),
                                       momentFlow});
        currentData.setFlag(0);
        log.debug("["+ct+"] ["+cp+"]");
        log.debug(currentData.toString());
       
        while(data.length>pos) {
            if(vmList == null)
                vmList = new ArrayList();

            int vm_kind = DataUtil.getIntToByte(data[pos]);
            log.debug("VM_KIND[" + VM_KIND_NAME[vm_kind] + "]");
            pos += _vmKindLen;
            int vm_length = DataUtil.getIntTo2Byte(DataUtil.select(data, pos, _vmDataLen));
            log.debug("VM_LENGTH[" + vm_length + "]");
            pos += _vmDataLen;
            log.debug("DATA LENGTH[" + data.length + "] CURSOR POS[" + pos + "]");
            log.debug("VM_DATA[" + Util.getHexString(DataUtil.select(data, pos, vm_length)) + "]");
            
            switch(vm_kind) {
            case VM_KIND_VCINFO:
                VM_CORUSINFO vm_vcinfo = new VM_CORUSINFO(DataUtil.select(data, pos, vm_length));
                
                vmList.add(vm_vcinfo);
                break;
            case VM_KIND_EVENTLOG:
                VM_EVENTLOG vm_eventlog = new VM_EVENTLOG(DataUtil.select(data, pos, vm_length));
                
                vmList.add(vm_eventlog);
                break;
            case VM_KIND_PARAMETERLOG:
                VM_PARAMLOG vm_paramlog = new VM_PARAMLOG(DataUtil.select(data, pos, vm_length),pw);
                
                vmList.add(vm_paramlog);
                break;
            case VM_KIND_DAYSTAT:
                VM_DAYSTAT vm_daystat = new VM_DAYSTAT(DataUtil.select(data, pos, vm_length));
                
                vmList.add(vm_daystat);
                break;
            case VM_KIND_DAYMAX:
                VM_DAYMAX vm_daymax = new VM_DAYMAX(DataUtil.select(data, pos, vm_length),pw,tu,pu);
                
                vmList.add(vm_daymax);
                break;
            case VM_KIND_MONTHSTAT:
                VM_MONTHSTAT vm_monthstat = new VM_MONTHSTAT(DataUtil.select(data, pos, vm_length),pw);
                
                vmList.add(vm_monthstat);
                break;
            case VM_KIND_MONTHMAX:
                VM_MONTHMAX vm_monthmax = new VM_MONTHMAX(DataUtil.select(data, pos, vm_length),pw,tu,pu);
                
                vmList.add(vm_monthmax);
                break;
            }
            pos += vm_length;
        }
    }

    public LPData[] getLPData()
    {
        if(lpList != null && lpList.size() > 0)
            return (LPData[])lpList.toArray(new LPData[lpList.size()]);
        else
            return null;
    }

    public HMData getCurrentData()
    {
        return currentData;
    }

    public ArrayList getVmList()
    {
        return vmList;
    }

    public LinkedHashMap getData()
    {
        log.debug("getData["+getMeterId()+"]");
        int titleIndex = 0;
        DecimalFormat df3 = TimeLocaleUtil.getDecimalFormat(meter.getSupplier());
        
        LinkedHashMap res = new LinkedHashMap(16,0.75f,false);
        res.put("<b>[Current data]"+(titleIndex++)+"</b>", "");
        res.put("VolumeCorrector Id", vcid);
        res.put("Current date time", vct);
        
        res.put("Pulse Weight", pw);
        res.put("Temperature Unit", tu);
        res.put("Pressure Unit", pu);
        res.put("Current UncorrectedVolume", cuv);
        res.put("Current Volume", cv);
        res.put("Current Temperature", ct);
        res.put("Current Pressure", cp);
        res.put("LP Period", period);
        res.put("LP Block Count", lpBlockCount);
        
        if(lpList != null && lpList.size() > 0){
            res.put("<b>[Interval Data]"+(titleIndex++)+"<b>", "");
            int nbr_chn = 5;//ch1,ch2

            ArrayList chartData0 = new ArrayList();//time chart
            ArrayList[] chartDatas = new ArrayList[nbr_chn]; //channel chart(ch1,ch2,...)
            for(int k = 0; k < nbr_chn ; k++){
                chartDatas[k] = new ArrayList();                    
            }
            
            DecimalFormat decimalf=null;
            SimpleDateFormat datef14=null;
            ArrayList lpDataTime = new ArrayList();
            for(int i = 0; i < lpList.size(); i++){
                String datetime = (String)((LPData)lpList.get(i)).getDatetime();

                if(meter!=null && meter.getSupplier()!=null){
                Supplier supplier = meter.getSupplier();
                if(supplier !=null){
                    String lang = supplier.getLang().getCode_2letter();
                    String country = supplier.getCountry().getCode_2letter();
                    
                    decimalf = TimeLocaleUtil.getDecimalFormat(supplier);
                    datef14 = new SimpleDateFormat(TimeLocaleUtil.getDateFormat(14, lang, country));
                }
            	}else{
                //locail 정보가 없을때는 기본 포멧을 사용한다.
                decimalf = new DecimalFormat();
                datef14 = new SimpleDateFormat();
            }
                String date = null;
            	try {
					date = datef14.format(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(datetime+"00"));
				} catch (ParseException e) {
					e.printStackTrace();
				}
               
                String tempDateTime = (String)((LPData)lpList.get(i)).getDatetime();
                String val = "";
                Double[] ch = (Double[])((LPData)lpList.get(i)).getCh();
                for(int k = 0; k < ch.length ; k++){
                    val += "<span style='margin-right: 40px;'>ch"+(k+1)+"="+df3.format(ch[k])+"</span>";
                }
                res.put("LP"+" "+date, val);

                chartData0.add(tempDateTime.substring(6,8)
                              +tempDateTime.substring(8,10)
                              +tempDateTime.substring(10,12));
                for(int k = 0; k < ch.length ; k++){                        
                    chartDatas[k].add(ch[k].doubleValue());
                }
                lpDataTime.add((String)((LPData)lpList.get(i)).getDatetime());
            }
            
            //res.put("chartData0", chartData0);
            //for(int k = 0; k < chartDatas.length ; k++){
            //    res.put("chartData"+(k+1), chartDatas[k]);
            //}
            //res.put("lpDataTime", lpDataTime);
            //res.put("chartDatas", chartDatas);
            res.put("[ChannelCount]", nbr_chn);
        }
        
        if(vmList != null && vmList.size() > 0){
            Object o = null;
            for(Iterator iter = vmList.iterator();iter.hasNext();)
            {
                o = iter.next();
                if(o instanceof VM_SEVCDINFO) 
                {
                    res.put("<b>[Volume Corrector Information]"+(titleIndex++)+"<b>","");
                    VM_SEVCDINFO v = (VM_SEVCDINFO)o;
                    res.put("Converter Type",""+v.getPac_fz());
                    res.put("Base Pressure",""+v.getPac_pb());
                    res.put("Base Temperature",""+v.getPac_tb());
                    res.put("Compress Factor",""+v.getPac_z());
                    res.put("Gas Relative Density",""+v.getPac_densrel());
                    res.put("Lowest Limit Pressure",""+v.getPap_pmin());
                    res.put("Upper Limit Pressure",""+v.getPap_pmax());
                    res.put("Lowest Limit Temperature",""+v.getPat_tmin());
                    res.put("Upper Limit Temperature",""+v.getPat_tmax());
                    res.put("Install Date",v.getPg1_date());
                    res.put("Site Name",v.getPg2_liba());
                    res.put("Pulse Weight",""+v.getPvn_pim());
                    res.put("Temperature Unit",""+v.getUni_tuni());
                    res.put("Pressure Unit",""+v.getUni_puni());
                    res.put("Volume Unit",""+v.getUni_vuni());
                }
                else if(o instanceof VM_CORUSINFO) 
                {
                    res.put("<b>[Volume Corrector Information]"+(titleIndex++)+"<b>","");
                    VM_CORUSINFO v = (VM_CORUSINFO)o;
                    res.put("Converter Type",""+v.getMcf());
                    res.put("Base Pressure",""+v.getMcrp());
                    res.put("Base Temperature",""+v.getMcrt());
                    res.put("Compress Factor",""+v.getMzr());
                    res.put("Lowest Limit Pressure",""+v.getMptl());
                    res.put("UpperLimitPressure",""+v.getMpth());
                    res.put("Lowest Limit Pressure",""+v.getMptl());
                    res.put("Upper Limit Pressure",""+v.getMpth());
                    res.put("Temperature Unit",""+v.getXut());
                    res.put("Pressure Unit",""+v.getXup());
                    res.put("Volume Unit",""+v.getXuv());
                    res.put("Power Supply",""+v.getXsm());
                    res.put("Gas Hour",""+v.getDgh());
                    res.put("Meter Status",""+v.getDm());
                    res.put("Uncorreted Usage Index",""+v.getMvri());
                    res.put("Correted Usage Index",""+v.getMvbi());
                    res.put("Uncorrected Usage Count",""+v.getMvrc());
                    res.put("Corrected Usage Count",""+v.getMvbc());
                    res.put("Current Pressure",""+v.getMpg());
                    res.put("Current Temperature",""+v.getMtg());
                }
                else if(o instanceof VM_DAYMAX) 
                {
                    VMCommonData[] data = ((VM_DAYMAX)o).getVMData();
                    if(data != null && data.length > 0){
                        res.put("<b>Day Max Data"+(titleIndex++)+"</b>","");
                        for(int i=0;i<data.length;i++) {
                            res.put(data[i].getYyyymmdd(),data[i].toString());
                        }
                    }
                }
                else if(o instanceof VM_DAYSTAT) 
                {
                }
                else if(o instanceof VM_MONTHMAX) 
                {                    
                    VMCommonData[] data = ((VM_MONTHMAX)o).getVMData();
                    if(data != null && data.length > 0){
                        res.put("<b>[Month Max Data]"+(titleIndex++)+"</b>","");
                        for(int i=0;i<data.length;i++) {
                            res.put(data[i].getYyyymm(),data[i].toString());
                        }
                    }
                }else if(o instanceof VM_MONTHSTAT) 
                {
                    VMCommonData[] data = ((VM_MONTHSTAT)o).getVMData();
                    if(data != null && data.length > 0){
                        res.put("<b>[Month Stat Data]"+(titleIndex++)+"</b>","");
                        for(int i=0;i<data.length;i++) {
                            res.put(data[i].getYyyymm(),data[i].toString());
                        }
                    }
                }
                else if(o instanceof VM_PARAMLOG) 
                {                    
                    VCParameterLogData[] data = ((VM_PARAMLOG)o).getParamLogData();
                    if(data != null && data.length > 0){
                        res.put("<b>[Parameter Log]"+(titleIndex++)+"</b>","");
                        for(int i=0;i<data.length;i++) {
                            res.put(data[i].getDate(),data[i].toString());
                        } 
                    }
                }
                else if(o instanceof VM_EVENTLOG) 
                {                    
                    VCEventLogData[] data = ((VM_EVENTLOG)o).getEventLogData();
                    if(data != null && data.length > 0){
                        res.put("<b>[Event Log]"+(titleIndex++)+"</b>","");
                        for(int i=0;i<data.length;i++) {
                            res.put(data[i].getDate(),data[i].toString());
                        }
                    }
                }
            }
        }
        return res;
    }


    public int getFlag()
    {
        return 0;
    }
    
    public int getMeterStatusCode()
    {
        return this.flag;
    }

    public int getLength()
    {
        // TODO Auto-generated method stub
        return 0;
    }


    public String getMeterId()
    {
        return meterId;
    }


    public byte[] getRawData()
    {
        return data;
    }


    public void setFlag(int flag)
    {
        // TODO Auto-generated method stub
        
    }
    public String getVcid()
    {
        return vcid;
    }
    public int getPortno()
    {
        return portno;
    }
    public int getPeriod()
    {
        return period;
    }
    public int getResolution() 
    {
        return period;
    }

    public Double getMeteringValue() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return null;
    }
}
