package com.aimir.fep.meter.parser.actarisVCTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.Util;

public class VM_CORUSINFO
{
    private static Log log = LogFactory.getLog(VM_CORUSINFO.class);

    private byte[] data;

    private final int _xpcfv_code = 0;
    private final int _xpcfd_code = 89;
    private final int _mcf_code = 15;
    private final int _mcrp_code = 19;
    private final int _mcrt_code = 24;
    private final int _mptl_code = 30;
    private final int _mpth_code = 31;
    private final int _mttl_code = 40;
    private final int _mtth_code = 41;
    private final int _xup_code = 98;
    private final int _xut_code = 99;
    private final int _xuv_code = 100;
    private final int _xsm_code = 105;
    private final int _dgh_code = 140;
    private final int _dgd_code = 141;
    private final int _dm_code = 142;
    private final int _mvri_code = 148;
    private final int _mvbi_code = 149;
    private final int _mvrc_code = 150;
    private final int _mvbc_code = 151;
    private final int _mfrg_code = 152;
    private final int _mfbg_code = 153;
    private final int _mtg_code = 158;
    private final int _mpg_code = 159;
    private final int _mcm_code = 160;
    private final int _mzr_code = 162;
    
    private String xpcfv;             // firmware version
    private String xpcfd;             // firmware release date
    private String mcf ="AGANX19 Standard";                  // convert type;
    private float mcrp;               // base pressure
    private float mcrt;               // base temperature
    private float mptl;               // lowest limit pressure
    private float mpth;               // highest limit pressure
    private float mttl;               // lowest limit temperature
    private float mtth;               // highest limit temperature
    private String xup = "bar";                  // pressure unit
    private String xut = "C";                  // temperature unit
    private String xuv = "m3";                  // volume unit
    private String xsm;                  // power supply type
    private int dgh;                  // gas hour
    private int dgd;                  // gas day
    private int dm;                   // meter status
    private Double mvri = 0d;              // unconverted usage index
    private Double mvbi = 0d;              // converted usage index
    private Double mvrc = 0d;              // unconverted counter under alarm
    private Double mvbc = 0d;              // converted counter
    private float mfrg;               // unconverted instantaneous flow rate
    private float mfbg;               // converted instantaneous flow rate
    private float mtg;                // current temperature (ref. xut)
    private float mpg;                // current pressure (ref. xup)
    private float mcm;                // conversion factor
    private float mzr;                // compress factor
    
    public VM_CORUSINFO(byte[] data) {
        this.data = data;
        log.debug("DATA[" + Hex.decode(data) + "]");
        try{
            parse();
            //log.debug(toString());
        }catch(Exception e){
            log.warn("CORUS parse Failed=>"+e,e);
        }
    }
    
    public void parse() throws Exception
    {
        int pos = 0;
        int code = 0;
        int len = 0;
        while(pos<data.length){
        	code = DataUtil.getIntToByte(data[pos++]);
        	len = DataUtil.getIntToByte(data[pos++]);
        	log.debug("CODE ["+code+"]"+"POS[" + pos + "] LEN[" + len + "]");
            switch(code) {
            case _xpcfv_code :
                xpcfv = new String(data, pos, len).trim(); 
                log.debug("XPCFV[" + xpcfv + "]");
                break;
            case _xpcfd_code :
                String yyyy = Util.frontAppendNStr('0',""+DataUtil.getIntToBytes(DataUtil.select(data, pos, 2)),4);
                String mm = Util.frontAppendNStr('0',""+DataUtil.getIntToByte(data[pos+2]),2);
                String dd = Util.frontAppendNStr('0',""+DataUtil.getIntToByte(data[pos+3]),2);
                xpcfd = ""+yyyy+""+mm+""+dd+"000000";
                log.debug("XPCFD[" + xpcfd + "]");
                break;
            case _mcf_code :
                int tmcf = DataUtil.getIntToBytes(DataUtil.select(data, pos, len));
                switch(tmcf){
                    case 0: mcf = "AGANX19 Standard";break;
                    case 1: mcf = "S-GERG88";break;
                    case 2: mcf = "PT";break;
                    case 3: mcf = "AGANX19 modified";break;
                    case 4: mcf = "Not used";break;
                    case 5: mcf = "T";break;
                    case 6: mcf = "16 Coefficients";break;
                    case 7: mcf = "AGA8";break;
                }
                log.debug("MCF[" + mcf + "]");
                break;
            case _mcrp_code :
                //mcrp = DataUtil.getFloat(DataFormat.big_endian(DataFormat.select(data, pos, _mcrp_len)),0);
                mcrp = DataUtil.getFloat(DataFormat.big_endian(DataFormat.select(data, pos, len)),0);
                log.debug("MCRP[" + mcrp + "]");
                break;
            case _mcrt_code :
                //mcrt = DataUtil.getFloat(DataFormat.big_endian(DataFormat.select(data, pos, _mcrt_len)),0);
                mcrt = DataUtil.getFloat(DataFormat.big_endian(DataFormat.select(data, pos, len)),0);
                log.debug("MCRT[" + mcrt + "]");
                break;
            case _mptl_code :
                //mptl = DataUtil.getFloat(DataFormat.big_endian(DataFormat.select(data, pos, _mptl_len)),0);
                mptl = DataUtil.getFloat(DataFormat.big_endian(DataFormat.select(data, pos, len)),0);
                log.debug("MPTL[" + mptl + "]");
                break;
            case _mpth_code :
                //mpth = DataUtil.getFloat(DataFormat.big_endian(DataFormat.select(data, pos, _mpth_len)),0);
                mpth = DataUtil.getFloat(DataFormat.big_endian(DataFormat.select(data, pos, len)),0);
                log.debug("MPTH[" + mpth + "]");
                break;
            case _mttl_code :
                //mttl = DataUtil.getFloat(DataFormat.big_endian(DataFormat.select(data, pos, _mttl_len)),0);
                mttl = DataUtil.getFloat(DataFormat.big_endian(DataFormat.select(data, pos, len)),0);
                log.debug("MTTL[" + mttl + "]");
                break;
            case _mtth_code :
                //mtth = DataUtil.getFloat(DataFormat.big_endian(DataFormat.select(data, pos, _mtth_len)),0);
                mtth = DataUtil.getFloat(DataFormat.big_endian(DataFormat.select(data, pos, len)),0);
                log.debug("MTTH[" + mtth + "]");
                break;
            case _xup_code :
                int txup = DataUtil.getIntToBytes(DataUtil.select(data, pos, len));
                switch(txup){
                    case 0: xup = "bar";break;
                    case 1: xup = "psi";break;
                    case 2: xup = "kPa";break;
                }
                log.debug("XUP[" + xup + "]");
                break;
            case _xut_code :
                int txut = DataUtil.getIntToBytes(DataUtil.select(data, pos, len));
                switch(txut){
                    case 0: xut = "C";break;
                    case 1: xut = "F";break;
                }
                log.debug("XUT[" + xut + "]");
                break;
            case _xuv_code :
                int txuv = DataUtil.getIntToBytes(DataUtil.select(data, pos, len));
                switch(txuv){
                    case 0:xuv = "m3";break;
                    case 1:xuv = "ft3";break;
                }
                log.debug("XUV[" + xuv + "]");
                break;
            case _xsm_code :
                xsm = ""+DataUtil.getIntToBytes(DataUtil.select(data, pos, len));
                /*
                switch(txsm){
                    case 0: xsm = "battery";break;
                    case 1: xsm = "External power suppply";break;
                }*/
                log.debug("XSM[" + xsm + "]");
                break;
            case _dgh_code :
                dgh = DataUtil.getIntToBytes(DataUtil.select(data, pos, len));
                log.debug("DGH[" + dgh + "]");
                break;
            case _dgd_code :
                dgd = DataUtil.getIntToBytes(DataUtil.select(data, pos, len));
                log.debug("DGD[" + dgd + "]");
                break;
            case _mvri_code :
                mvri = DataUtil.getLongToBytes(DataFormat.LSB2MSB(DataUtil.select(data, pos, len/2)))
                     + DataUtil.getLongToBytes(DataFormat.LSB2MSB(DataUtil.select(data, pos + len/2, len/2))) / Math.pow(10,8);
                log.debug("PMVRI[" + mvri + "]");
                break;
            case _mvbi_code :
                mvbi = DataUtil.getLongToBytes(DataFormat.LSB2MSB(DataUtil.select(data, pos, len/2)))
                     + DataUtil.getLongToBytes(DataFormat.LSB2MSB(DataUtil.select(data, pos + len/2, len/2))) / Math.pow(10,8);
                log.debug("MVBI[" + mvbi + "]");
                break;
            case _mvrc_code :
                mvrc = DataUtil.getLongToBytes(DataFormat.LSB2MSB(DataUtil.select(data, pos, len/2)))
                     + DataUtil.getLongToBytes(DataFormat.LSB2MSB(DataUtil.select(data, pos + len/2, len/2))) / Math.pow(10,8);
                log.debug("MVRC[" + mvrc + "]");
                break;
            case _mvbc_code :
                mvbc = DataUtil.getLongToBytes(DataFormat.LSB2MSB(DataUtil.select(data, pos, len/2)))
                     + DataUtil.getLongToBytes(DataFormat.LSB2MSB(DataUtil.select(data, pos + len/2, len/2))) / Math.pow(10,8);
                log.debug("MVBC[" + mvbc + "]");
                break;
            case _mfrg_code :
                //mfrg = DataUtil.getFloat(DataFormat.big_endian(DataFormat.select(data, pos, _mfrg_len)),0);
                mfrg = DataUtil.getFloat(DataFormat.big_endian(DataFormat.select(data, pos, len)),0);
                log.debug("MFRG[" + mfrg + "]");
                break;
            case _mfbg_code :
                //mfbg = DataUtil.getFloat(DataFormat.big_endian(DataFormat.select(data, pos, _mfbg_len)),0);
                mfbg = DataUtil.getFloat(DataFormat.big_endian(DataFormat.select(data, pos, len)),0);
                log.debug("MFBG[" + mfbg + "]");
                break;
            case _mtg_code :
                //mtg = DataUtil.getFloat(DataFormat.big_endian(DataFormat.select(data, pos, _mtg_len)),0);
                mtg = DataUtil.getFloat(DataFormat.big_endian(DataFormat.select(data, pos, len)),0);
                log.debug("MTG[" + mtg + "]");
                break;
            case _mpg_code :
                //mpg = DataUtil.getFloat(DataFormat.big_endian(DataFormat.select(data, pos, _mpg_len)),0);
                mpg = DataUtil.getFloat(DataFormat.big_endian(DataFormat.select(data, pos, len)),0);
                log.debug("MPQ[" + mpg + "]");
                break;
            case _mcm_code :
                //mcm = DataUtil.getFloat(DataFormat.big_endian(DataFormat.select(data, pos, _mcm_len)),0);
                mcm = DataUtil.getFloat(DataFormat.big_endian(DataFormat.select(data, pos, len)),0);
                log.debug("MCM[" + mcm + "]");
                break;
            case _mzr_code :
                //mzr = DataUtil.getFloat(DataFormat.big_endian(DataFormat.select(data, pos, _mzr_len)),0);
                mzr = DataUtil.getFloat(DataFormat.big_endian(DataFormat.select(data, pos, len)),0);
                log.debug("MZR[" + mzr + "]");
                break;
            }
            pos += len;
        }
    }

    public String getXpcfv()
    {
        return xpcfv;
    }

    public String getXpcfd()
    {
        return xpcfd;
    }

    public String getMcf()
    {
        return mcf;
    }

    public float getMcrp()
    {
        return mcrp;
    }

    public float getMcrt()
    {
        return mcrt;
    }

    public float getMptl()
    {
        return mptl;
    }

    public float getMpth()
    {
        return mpth;
    }

    public float getMttl()
    {
        return mttl;
    }

    public float getMtth()
    {
        return mtth;
    }

    public String getXup()
    {
        return xup;
    }

    public String getXut()
    {
        return xut;
    }

    public String getXuv()
    {
        return xuv;
    }

    public String getXsm()
    {
        return xsm;
    }

    public int getDgh()
    {
        return dgh;
    }

    public int getDgd()
    {
        return dgd;
    }

    public int getDm()
    {
        return dm;
    }

    public Double getMvri()
    {
        return mvri;
    }

    public Double getMvbi()
    {
        return mvbi;
    }

    public Double getMvrc()
    {
        return mvrc;
    }

    public Double getMvbc()
    {
        return mvbc;
    }

    public float getMfrg()
    {
        return mfrg;
    }

    public float getMfbg()
    {
        return mfbg;
    }

    public float getMtg()
    {
        return mtg;
    }

    public float getMpg()
    {
        return mpg;
    }

    public float getMcm()
    {
        return mcm;
    }

    public float getMzr()
    {
        return mzr;
    }
}
