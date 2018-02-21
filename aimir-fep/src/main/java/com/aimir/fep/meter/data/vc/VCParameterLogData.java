package com.aimir.fep.meter.data.vc;

public class VCParameterLogData
{
    private String date;
    private int paramCode;
    private Double unconvertedIndex;
    private Double convertedIndex;
    private String oldValue;
    private String newValue;

    public String getDate()
    {
        return date;
    }
    public int getParamCode()
    {
        return paramCode;
    }
    public Double getUnconvertedIndex()
    {
        return unconvertedIndex;
    }
    public Double getConvertedIndex()
    {
        return convertedIndex;
    }
    public String getOldValue()
    {
        return oldValue;
    }
    public String getNewValue()
    {
        return newValue;
    }
    public void setDate(String date)
    {
        this.date = date;
    }
    public void setParamCode(int paramCode)
    {
        this.paramCode = paramCode;
    }
    public void setUnconvertedIndex(Double unconvertedIndex)
    {
        this.unconvertedIndex = unconvertedIndex;
    }
    public void setConvertedIndex(Double convertedIndex)
    {
        this.convertedIndex = convertedIndex;
    }
    public void setOldValue(String oldValue)
    {
        this.oldValue = oldValue;
    }
    public void setNewValue(String newValue)
    {
        this.newValue = newValue;
    }
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("date=[").append(date).append("],");
        sb.append("paramCode=[").append(paramCode).append("],");
        sb.append("unconvertedIndex=[").append(unconvertedIndex).append("],");
        sb.append("convertedIndex=[").append(convertedIndex).append("],");
        sb.append("oldValue=[").append(oldValue).append("],");
        sb.append("newValue=[").append(newValue).append("]\n");

        return sb.toString();
    }
}
