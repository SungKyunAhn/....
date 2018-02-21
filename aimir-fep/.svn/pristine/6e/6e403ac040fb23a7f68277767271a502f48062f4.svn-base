package com.aimir.fep.meter.parser;

public class ModemLPData implements java.io.Serializable
{
	private static final long serialVersionUID = 2458139504517837309L;
	private String lpDate = null;
    private double[][] lp = null;
    private double[] basePulse = null;
    
    public String getLpDate()
    {
        return lpDate;
    }
    public void setLpDate(String lpDate)
    {
        this.lpDate = lpDate;
    }
    public double[][] getLp()
    {
        return lp;
    }
    public void setLp(double[][] lp)
    {
        this.lp = lp;
    }
    public double[] getBasePulse()
    {
        return basePulse;
    }
    public void setBasePulse(double[] basePulse)
    {
        this.basePulse = basePulse;
    }
    
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("LPDate[" + lpDate + "]\r\n");
        buf.append("BasePulse[");
        for (int ch = 0; ch < basePulse.length; ch++) {
            buf.append("ch"+ch+":"+basePulse[ch]+"\r\n");
        }
        if (lp != null) {
            buf.append("]\r\nLP[");
            for (int ch = 0; ch < lp.length; ch++) {
                buf.append("ch"+ch+"("+ lp[ch].length+"):");
                for (int i = 0 ; i < lp[ch].length; i++)
                    buf.append(lp[ch][i]+",");
                buf.append("\r\n");
            }
            buf.append("]");
        }
        return buf.toString();
    }
}
