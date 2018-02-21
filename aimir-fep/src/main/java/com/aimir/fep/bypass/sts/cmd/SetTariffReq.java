package com.aimir.fep.bypass.sts.cmd;

import java.io.ByteArrayOutputStream;

import com.aimir.fep.bypass.sts.STSDataFrame;
import com.aimir.fep.bypass.sts.STSDataReq;
import com.aimir.fep.util.DataUtil;

public class SetTariffReq extends STSDataFrame {
    public SetTariffReq(String yyyymmdd, int condLimit1, int condLimit2,
            int[] cons, double[] fixedRate, double[] varRate, double[] condRate1, double[] condRate2) throws Exception {
        ByteArrayOutputStream out = null;
        
        try {
            out = new ByteArrayOutputStream();
            
            // E_DT
            out.write(DataUtil.get2ByteToInt(Integer.parseInt(yyyymmdd.substring(0,4))));
            out.write(new byte[]{DataUtil.getByteToInt(Integer.parseInt(yyyymmdd.substring(4, 6)))});
            out.write(new byte[]{DataUtil.getByteToInt(Integer.parseInt(yyyymmdd.substring(6, 8)))});
            
            // condLimit1
            out.write(DataUtil.get2ByteToInt(condLimit1));
            
            // condLimit2
            out.write(DataUtil.get2ByteToInt(condLimit2));
            
            // COUNT
            out.write(new byte[]{DataUtil.getByteToInt(cons.length)});
            
            String s_value = null;
            int dec_point = 0;
            for (int i = 0; i < cons.length; i++) {
                // CONS_n
                out.write(DataUtil.get2ByteToInt(cons[i]));
                
                // FixedRate_n
                s_value = fixedRate[i]+"";
                dec_point = s_value.indexOf(".");
                out.write(DataUtil.get4ByteToInt(Integer.parseInt(s_value.substring(0,  dec_point) + s_value.substring(dec_point+1))));
                out.write(DataUtil.getByteToInt(s_value.length() - (dec_point+1)));
                
                // varRate
                s_value = varRate[i]+"";
                dec_point = s_value.indexOf(".");
                out.write(DataUtil.get4ByteToInt(Integer.parseInt(s_value.substring(0,  dec_point) + s_value.substring(dec_point+1))));
                out.write(DataUtil.getByteToInt(s_value.length() - (dec_point+1)));
                
                // condRate1
                s_value = condRate1[i]+"";
                dec_point = s_value.indexOf(".");
                out.write(DataUtil.get4ByteToInt(Integer.parseInt(s_value.substring(0,  dec_point) + s_value.substring(dec_point+1))));
                out.write(DataUtil.getByteToInt(s_value.length() - (dec_point+1)));
                
                // condRate2
                s_value = condRate2[i]+"";
                dec_point = s_value.indexOf(".");
                out.write(DataUtil.get4ByteToInt(Integer.parseInt(s_value.substring(0,  dec_point) + s_value.substring(dec_point+1))));
                out.write(DataUtil.getByteToInt(s_value.length() - (dec_point+1)));
            }
            
            byte[] b = out.toByteArray();
            out = new ByteArrayOutputStream();
            // out.write(DataUtil.getByteToInt(b.length));
            out.write(b);
            
            req = new STSDataReq(CMD.SetTariff, out.toByteArray());
        }
        finally {
            if (out != null) out.close();
        }
    }
}
