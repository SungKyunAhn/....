package com.aimir.fep.bypass.sts.cmd;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.bypass.sts.STSDataFrame;
import com.aimir.fep.bypass.sts.STSException;
import com.aimir.fep.util.DataUtil;

public class GetTariffRes extends STSDataFrame {
    private static Log log = LogFactory.getLog(GetTariffRes.class);
    
    private int tariffMode = 0;
    private String yyyymmdd = null;
    private int condLimit1 = 0;
    private int condLimit2 = 0;
    private int count = 0;
    private int[] cons;
    private double[] fixedRate;
    private double[] varRate;
    private double[] condRate1;
    private double[] condRate2;
    
    public GetTariffRes(byte[] bx) throws Exception {
        super.decode(bx);
        parse();
    }
    
    public void parse() throws Exception {
        if (res.getResult() == 0x00) {
            int pos = 0;
            
            // Tariff_mode
            byte[] b = new byte[1];
            System.arraycopy(res.getRdata(), pos, b, 0, b.length);
            pos += b.length;
            tariffMode = DataUtil.getIntToBytes(b);
            
            log.debug("Tariff_mode[" + tariffMode + "]");
            
            // APPLY_DATE
            b = new byte[4];
            System.arraycopy(res.getRdata(), pos, b, 0, b.length);
            pos += b.length;
            yyyymmdd = String.format("%4d%02d%02d", 
                    DataUtil.getIntTo2Byte(new byte[]{b[0], b[1]}),
                    DataUtil.getIntToByte(b[2]),
                    DataUtil.getIntToByte(b[3]));
            log.debug("APPLY_DATE[" + yyyymmdd + "]");
            
            // COND_LIMIT1
            b = new byte[2];
            System.arraycopy(res.getRdata(), pos, b, 0, b.length);
            pos += b.length;
            condLimit1 = DataUtil.getIntTo2Byte(b);
            log.debug("COND_LIMIT_1[" + condLimit1 + "]");
            
            // COND_LIMIT2
            b = new byte[2];
            System.arraycopy(res.getRdata(), pos, b, 0, b.length);
            pos += b.length;
            condLimit2 = DataUtil.getIntTo2Byte(b);
            log.debug("COND_LIMIT_2[" + condLimit2 + "]");
            
            // COUNT
            b = new byte[1];
            System.arraycopy(res.getRdata(), pos, b, 0, b.length);
            pos += b.length;
            count = DataUtil.getIntToBytes(b);
            log.debug("COUNT[" + count + "]");
            
            // CONSUMP and PRICE
            cons = new int[count];
            fixedRate = new double[count];
            varRate = new double[count];
            condRate1 = new double[count];
            condRate2 = new double[count];
            
            for (int i = 0; i < count; i++) {
                b = new byte[2];
                System.arraycopy(res.getRdata(), pos, b, 0, b.length);
                pos += b.length;
                
                // CONS
                cons[i] = DataUtil.getIntTo2Byte(b);
                
                b = new byte[4];
                System.arraycopy(res.getRdata(), pos, b, 0, b.length);
                pos += b.length;
                
                // FIXED RATE
                fixedRate[i] = DataUtil.getIntTo4Byte(b);
                
                // fixedRate_decimal
                b = new byte[1];
                System.arraycopy(res.getRdata(), pos, b, 0, b.length);
                pos += b.length;
                fixedRate[i] /= Math.pow(10, DataUtil.getIntToBytes(b));

                //varRate
                b = new byte[4];
                System.arraycopy(res.getRdata(), pos, b, 0, b.length);
                pos += b.length;
                varRate[i] = DataUtil.getIntTo4Byte(b);
                
                //varRate_decimal
                b = new byte[1];
                System.arraycopy(res.getRdata(), pos, b, 0, b.length);
                pos += b.length;
                varRate[i] /= Math.pow(10, DataUtil.getIntToBytes(b));
                log.debug(i + " CONS[" + cons[i] + "] FIXED_RATE[" + fixedRate[i] + "] VAR_RATE[" + varRate[i] + "]");
                
                // condRate1
                b = new byte[4];
                System.arraycopy(res.getRdata(), pos, b, 0, b.length);
                pos += b.length;
                condRate1[i] = DataUtil.getIntTo4Byte(b);

                // condRate1_decimal
                b = new byte[1];
                System.arraycopy(res.getRdata(), pos, b, 0, b.length);
                pos += b.length;
                
                condRate1[i] /= Math.pow(10, DataUtil.getIntToBytes(b));
                
                // condRate2
                b = new byte[4];
                System.arraycopy(res.getRdata(), pos, b, 0, b.length);
                pos += b.length;
                condRate2[i] = DataUtil.getIntTo4Byte(b);

                // condRate2_decimal
                b = new byte[1];
                System.arraycopy(res.getRdata(), pos, b, 0, b.length);
                pos += b.length;
                
                condRate2[i] /= Math.pow(10, DataUtil.getIntToBytes(b));
                
                log.debug("COND_RATE_1[" + condRate1[i] + "] COND_RATE_2[" + condRate2[i] + "]");
            }
        }
        else throw new STSException(res.getRdata());
    }

    public int getTariffMode() {
        return tariffMode;
    }

    public String getYyyymmdd() {
        return yyyymmdd;
    }

    public int getCondLimit1() {
        return condLimit1;
    }
    
    public int getCondLimit2() {
        return condLimit2;
    }

    public int getCount() {
        return count;
    }

    public int[] getCons() {
        return cons;
    }

    public double[] getFixedRate() {
        return fixedRate;
    }
    
    public double[] getVarRate() {
        return varRate;
    }
    
    public double[] getCondRate1() {
        return condRate1;
    }
    
    public double[] getCondRate2() {
        return condRate2;
    }
}
