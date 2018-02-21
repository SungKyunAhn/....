package com.aimir.fep.meter.parser.actarisVCTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.StringUtil;

public class VM_SEVCDINFO
{
    private static Log log = LogFactory.getLog(VM_SEVCDINFO.class);

    private byte[] data;

    private final int _pac_code = 0;
    private final int _pac_sgerg88_len = 25;
    private final int _pac_sgerg88_fz_ofs = 0;
    private final int _pac_sgerg88_fz_len = 1;
    private final int _pac_sgerg88_pb_ofs = 1;
    private final int _pac_sgerg88_pb_len = 4;
    private final int _pac_sgerg88_tb_ofs = 5;
    private final int _pac_sgerg88_tb_len = 4;
    private final int _pac_sgerg88_densrel_ofs = 9;
    private final int _pac_sgerg88_densrel_len = 4;
    private final int _pac_aganx19_len = 21;
    private final int _pac_aganx19_fz_ofs = 0;
    private final int _pac_aganx19_fz_len = 1;
    private final int _pac_aganx19_pb_ofs = 1;
    private final int _pac_aganx19_pb_len = 4;
    private final int _pac_aganx19_tb_ofs = 5;
    private final int _pac_aganx19_tb_len = 4;
    private final int _pac_aganx19_densrel_ofs = 9;
    private final int _pac_aganx19_densrel_len = 4;    
    private final int _pac_ptconv_len = 13;    //pt conversion, t conversion
    private final int _pac_ptconv_fz_ofs = 0;
    private final int _pac_ptconv_fz_len = 1;
    private final int _pac_ptconv_pb_ofs = 1;
    private final int _pac_ptconv_pb_len = 4;
    private final int _pac_ptconv_tb_ofs = 5;
    private final int _pac_ptconv_tb_len = 4;
    private final int _pac_ptconv_z_ofs = 9;
    private final int _pac_ptconv_z_len = 4;    
    private final int _pac_aga8_len = 29;
    private final int _pac_aga8_fz_ofs = 0;
    private final int _pac_aga8_fz_len = 1;
    private final int _pac_aga8_pb_ofs = 1;
    private final int _pac_aga8_pb_len = 4;
    private final int _pac_aga8_tb_ofs = 5;
    private final int _pac_aga8_tb_len = 4;
    private final int _pac_aga8_densrel_ofs = 9;
    private final int _pac_aga8_densrel_len = 4;    
    private final int _pac_16coef_len = 73;
    private final int _pac_16coef_fz_ofs = 0;
    private final int _pac_16coef_fz_len = 1;
    private final int _pac_16coef_pb_ofs = 1;
    private final int _pac_16coef_pb_len = 4;
    private final int _pac_16coef_tb_ofs = 5;
    private final int _pac_16coef_tb_len = 4;
    
    private final int _pap_code = 3;
    private final int _pap_pmin_ofs = 0;
    private final int _pap_pmin_len = 4;
    private final int _pap_pmax_ofs = 4;
    private final int _pap_pmax_len = 4;

    private final int _pat_code = 5;
    private final int _pat_tmin_ofs = 0;
    private final int _pat_tmin_len = 4;
    private final int _pat_tmax_ofs = 4;
    private final int _pat_tmax_len = 4;
    
    private final int _pg1_code = 11;
    private final int _pg1_date_ofs = 0;
    private final int _pg1_date_len = 3;
    
    private final int _pg2_code = 12;
    private final int _pg2_liba_ofs = 0;
    private final int _pg2_liba_len = 20;
    
    private final int _pva_code = 6;
    private final int _pva_ivca_ofs = 0;
    private final int _pva_ivca_len = 8;
    private final int _pva_ivba_ofs = 8;
    private final int _pva_ivba_len = 8;
    private final int _pva_cvba_ofs = 16;
    private final int _pva_cvba_len = 8;
    
    private final int _pvn_code = 7;
    private final int _pvn_ivc_ofs = 0;
    private final int _pvn_ivc_len = 8;
    private final int _pvn_ivb_ofs = 8;
    private final int _pvn_ivb_len = 8;
    private final int _pvn_cvc_ofs = 16;
    private final int _pvn_cvc_len = 8;
    private final int _pvn_pim_ofs = 24;
    private final int _pvn_pim_len = 1;
    
    private final int _uni_code = 25;
    private final int _uni_uni_off = 0;
    private final int _uni_uni_len = 3;
    
    private String pac_fz = "aganx19";        // conversion type
    private double pac_pb;       // base pressure
    private double pac_tb;       // base temperature
    private float pac_z;        // compress factor
    private float pac_densrel = 1;  // gas relative density
    private float pap_pmin;     // pressure min limit
    private float pap_pmax;     // pressure max limit
    private float pat_tmin;     // temperature min limit
    private float pat_tmax;     // temperature max limit
    private String pg1_date;   // install date
    private String pg2_liba;   // site name
    private double pva_ivca;     // converted index value at latest alarm
    private double pva_ivba;     // total index value at latest alarm
    private double pva_cvba;     // total usage factor
    private double pvn_ivc;      // converted volume index
    private double pvn_ivb;      // base volume index
    private double pvn_cvc;      // converted volume coefficient
    private float pvn_pim;       // pulse weight 
    private String uni_tuni = "C";      // temperature unit
    private String uni_puni = "bar";      // pressure unit
    private String uni_vuni = "m3";      // volume unit
    
    
    public VM_SEVCDINFO(byte[] data) {
        this.data = data;
        
        try{
            parse();
            log.debug(toString());
        }catch(Exception e){
            log.warn("SEVCD parse Failed=>"+e,e);
        }
    }
    
    public void parse() throws Exception
    {
        int pos = 0;
        int code = 0;
        int len = 0;
        while(pos<data.length){
        	code =  DataUtil.getIntToByte(data[pos++]);
        	len = DataUtil.getIntToByte(data[pos++]);
            switch(code) {
            case _pac_code :
                switch(len) {
                case _pac_sgerg88_len :
                    pac_fz = "sgerg88";
                    pac_pb = DataFormat.bytesToFloat(DataFormat.LSB2MSB(DataUtil.select(data, pos + _pac_sgerg88_pb_ofs, _pac_sgerg88_pb_len)))*100;
                    pac_tb = DataFormat.bytesToFloat(DataFormat.LSB2MSB(DataUtil.select(data, pos + _pac_sgerg88_tb_ofs, _pac_sgerg88_tb_len)));
                    break;
                case _pac_aganx19_len :
                    pac_fz = "aganx19";
                    pac_pb = DataFormat.bytesToFloat(DataFormat.LSB2MSB(DataUtil.select(data, pos + _pac_aganx19_pb_ofs, _pac_aganx19_pb_len)))*100;
                    pac_tb = DataFormat.bytesToFloat(DataFormat.LSB2MSB(DataUtil.select(data, pos + _pac_aganx19_tb_ofs, _pac_aganx19_tb_len)));
                    pac_densrel = DataFormat.bytesToFloat(DataFormat.LSB2MSB(DataUtil.select(data, pos + _pac_aganx19_densrel_ofs, _pac_aganx19_densrel_len)));
                    break;
                case _pac_ptconv_len :
                    pac_fz = "ptconv";
                    pac_pb = DataFormat.bytesToFloat(DataFormat.LSB2MSB(DataUtil.select(data, pos + _pac_ptconv_pb_ofs, _pac_ptconv_pb_len)))*100;
                    pac_tb = DataFormat.bytesToFloat(DataFormat.LSB2MSB(DataUtil.select(data, pos + _pac_ptconv_tb_ofs, _pac_ptconv_tb_len)));
                    pac_z = DataFormat.bytesToFloat(DataFormat.LSB2MSB(DataUtil.select(data, pos + _pac_ptconv_z_ofs, _pac_ptconv_z_len)));
                    break;
                case _pac_aga8_len :
                    pac_fz = "aga8";
                    pac_pb = DataFormat.bytesToFloat(DataFormat.LSB2MSB(DataUtil.select(data, pos + _pac_aga8_pb_ofs, _pac_aga8_pb_len)))*100;
                    pac_densrel = DataFormat.bytesToFloat(DataFormat.LSB2MSB(DataUtil.select(data, pos + _pac_aga8_densrel_ofs, _pac_aga8_densrel_len)));
                    break;
                case _pac_16coef_len :
                    pac_fz = "16coef";
                    pac_pb = DataFormat.bytesToFloat(DataFormat.LSB2MSB(DataUtil.select(data, pos + _pac_16coef_pb_ofs, _pac_16coef_pb_len)))*100;
                    pac_tb = DataFormat.bytesToFloat(DataFormat.LSB2MSB(DataUtil.select(data, pos + _pac_16coef_tb_ofs, _pac_16coef_tb_len)));
                    break;
                }
                break;
            case _pap_code :
                pap_pmin = DataFormat.bytesToFloat(DataFormat.LSB2MSB(DataUtil.select(data, pos + _pap_pmin_ofs, _pap_pmin_len)));
                pap_pmax = DataFormat.bytesToFloat(DataFormat.LSB2MSB(DataUtil.select(data, pos + _pap_pmax_ofs, _pap_pmax_len)));                
                break;
            case _pat_code :
                pat_tmin = DataFormat.bytesToFloat(DataFormat.LSB2MSB(DataUtil.select(data, pos + _pat_tmin_ofs, _pat_tmin_len)));
                pat_tmax = DataFormat.bytesToFloat(DataFormat.LSB2MSB(DataUtil.select(data, pos + _pat_tmax_ofs, _pat_tmax_len)));                
                break;
            case _pg1_code :
                int dd = DataUtil.getIntToByte(data[pos]);
                int mm = DataUtil.getIntToByte(data[pos+1]);
                int yy = DataUtil.getIntToByte(data[pos+2]);
                int year = Integer.parseInt(DateTimeUtil
                        .getCurrentDateTimeByFormat("yyyy").substring(0, 2) + "00") + yy;
                pg1_date = String.valueOf(year)
                           + StringUtil.frontAppendNStr('0', String.valueOf(mm), 2)
                           + StringUtil.frontAppendNStr('0', String.valueOf(dd), 2);
                break;
            case _pg2_code :
                pg2_liba = new String(data, pos + _pg2_liba_ofs, _pg2_liba_len);
                break;
            case _pva_code :
                pva_ivca = DataFormat.hex2double64(data, pos + _pva_ivca_ofs);
                pva_ivba = DataFormat.hex2double64(data, pos + _pva_ivba_ofs);
                pva_cvba = DataFormat.hex2double64(data, pos + _pva_cvba_ofs);
                break;
            case _pvn_code :
                pvn_ivc = DataFormat.hex2double64(data, pos + _pvn_ivc_ofs);
                pvn_ivb = DataFormat.hex2double64(data, pos + _pvn_ivb_ofs);               
                pvn_cvc = DataFormat.hex2double64(data, pos + _pvn_cvc_ofs);
                int scale = DataFormat.hex2signed8(data[pos + _pvn_pim_ofs]);
                pvn_pim =  (float) Math.pow(10,scale);
                break;
            case _uni_code :
                int tuni_tuni = DataUtil.getIntToByte(data[pos]);
                int tuni_puni = DataUtil.getIntToByte(data[pos+1]);
                int tuni_vuni = DataUtil.getIntToByte(data[pos+2]);

                switch(tuni_tuni){
                    case 0: uni_tuni = "C";break;
                    case 1: uni_tuni = "F";break;
                }
                switch(tuni_puni){
                    case 0: uni_puni = "bar";break;
                    case 1: uni_puni = "psi";break;
                    case 2: uni_puni = "kPa";break;
                }
                switch(tuni_vuni){
                    case 0:uni_vuni = "m3";break;
                    case 1:uni_vuni = "ft3";break;
                }
                break;
            }
            pos += len;
        }
    }

    public String getPac_fz()
    {
        return pac_fz;
    }

    public double getPac_pb()
    {
        return pac_pb;
    }

    public double getPac_tb()
    {
        return pac_tb;
    }

    public float getPac_z()
    {
        return pac_z;
    }

    public float getPac_densrel()
    {
        return pac_densrel;
    }

    public float getPap_pmin()
    {
        return pap_pmin;
    }

    public float getPap_pmax()
    {
        return pap_pmax;
    }

    public float getPat_tmin()
    {
        return pat_tmin;
    }

    public float getPat_tmax()
    {
        return pat_tmax;
    }

    public String getPg1_date()
    {
        return pg1_date;
    }

    public String getPg2_liba()
    {
        return pg2_liba;
    }

    public double getPva_ivca()
    {
        return pva_ivca;
    }

    public double getPva_ivba()
    {
        return pva_ivba;
    }

    public double getPva_cvba()
    {
        return pva_cvba;
    }

    public double getPvn_ivc()
    {
        return pvn_ivc;
    }

    public double getPvn_ivb()
    {
        return pvn_ivb;
    }

    public double getPvn_cvc()
    {
        return pvn_cvc;
    }

    public float getPvn_pim()
    {
        return pvn_pim;
    }

    public String getUni_tuni()
    {
        return uni_tuni;
    }

    public String getUni_puni()
    {
        return uni_puni;
    }

    public String getUni_vuni()
    {
        return uni_vuni;
    }
    
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("Converter Type=[").append(getPac_fz()).append(']');
        sb.append("Base Pressure=[").append(getPac_pb()).append(']');
        sb.append("Base Temperature=[").append(getPac_tb()).append(']');
        sb.append("Compress Factor=[").append(getPac_z()).append(']');
        sb.append("Gas Relative Density=[").append(getPac_densrel()).append(']');
        sb.append("Lowest Limit Pressure=[").append(getPap_pmin()).append(']');
        sb.append("Upper Limit Pressure=[").append(getPap_pmax()).append(']');
        sb.append("Lowest Limit Temperature=[").append(getPat_tmin()).append(']');
        sb.append("Upper Limit Temperature=[").append(getPat_tmax()).append(']');
        sb.append("Install Date=[").append(getPg1_date()).append(']');
        sb.append("Site Name=[").append(getPg2_liba()).append(']');
        sb.append("Pulse Weight=[").append(getPvn_pim()).append(']');
        sb.append("Temperature Unit=[").append(getUni_tuni()).append(']');
        sb.append("Pressure Unit=[").append(getUni_puni()).append(']');
        sb.append("Volume Unit=[").append(getUni_vuni()).append("]\n");
        return sb.toString();
    }
}
