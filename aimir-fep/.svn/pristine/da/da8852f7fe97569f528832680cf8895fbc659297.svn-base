package com.aimir.fep.meter.parser.SM110Table;


import java.util.LinkedHashMap;

import com.aimir.fep.meter.data.ChannelInfo;

public class UNIT_OF_MTR
{
    protected LinkedHashMap<String, ChannelInfo> map 
    = new LinkedHashMap<String, ChannelInfo>(16,0.74f,false);
    
    public UNIT_OF_MTR()
    {
        init();
    }

    private void init()
    {        
        map.put("00024500",new ChannelInfo("00024500","Delivered Active","kWh"));//Q1+Q4 fund + harmonics (LP)
        map.put("00018500",new ChannelInfo("00018500","Received Active","kWh"));//Q2+Q3 fund + harmonics (LP)        
        map.put("0003C500",new ChannelInfo("0003C500","Total Active","kWh"));//(Q1+Q4)+(Q2+Q3) fund + harmonics (LP)        
        map.put("0007C500",new ChannelInfo("0007C500","(Delivered-Received) Active","kWh"));//(Q1+Q4)-(Q2+Q3) fund + harmonics (LP)        
        map.put("00024000",new ChannelInfo("00024000","Delivered Active","kWh"));//Q1+Q4 fund + harmonics (read)        
        map.put("00018000",new ChannelInfo("00018000","Received Active","kWh"));//Q2+Q3 fund + harmonics (read)        
        map.put("0003C000",new ChannelInfo("0003C000","Total Active","kWh"));//(Q1+Q4)+(Q2+Q3) fund + harmonics (read)        
        map.put("0007C000",new ChannelInfo("0007C000","(Delivered-Received) Active","kWh"));//(Q1+Q4)-(Q2+Q3) fund + harmonics (read)        
        map.put("0000C501",new ChannelInfo("0000C501","Delivered Rective","kvarh"));//Q1+Q2 fund + harmonics (LP)        
        map.put("00030501",new ChannelInfo("00030501","Received Reactive","kvarh"));//Q3+Q4 fund + harmonics (LP)        
        map.put("0003C501",new ChannelInfo("0003C501","Total Reactive","kvarh"));//(Q1+Q2)+(Q3+Q4) fund + harmonics (LP)        
        map.put("0007C501",new ChannelInfo("0007C501","(Delivered-Received) Reactive","kvarh"));//(Q1+Q2)-(Q3+Q4) fund + harmonics (LP)        
        map.put("0000C001",new ChannelInfo("0000C001","Delivered Rective","kvarh"));//Q1+Q2 fund + harmonics (read)        
        map.put("00030001",new ChannelInfo("00030001","Received Reactive","kvarh"));//Q3+Q4 fund + harmonics (read)        
        map.put("0003C001",new ChannelInfo("0003C001","Total Reactive","kvarh"));//(Q1+Q2)+(Q3+Q4) fund + harmonics (read)        
        map.put("0007C001",new ChannelInfo("0007C001","(Delivered-Received) Reactive","kvarh"));//(Q1+Q2)-(Q3+Q4) fund + harmonics (read)        
        map.put("00024400",new ChannelInfo("00024400","Q1+Q4 fund + harmonics","kW"));        
        map.put("00018400",new ChannelInfo("00018400","Q2+Q3 fund + harmonics","W"));        
        map.put("0003C400",new ChannelInfo("0003C400","(Q1+Q4)+(Q2+Q3) fund + harmonics","W"));    
        map.put("0007C400",new ChannelInfo("0007C400","(Q1+Q4)-(Q2+Q3) fund + harmonics","W"));    
        map.put("0000C401",new ChannelInfo("0000C401","Q1+Q2 fund + harmonics","var"));          
        map.put("00030401",new ChannelInfo("00030401","Q3+Q4 fund + harmonics","var"));    
        map.put("0003C401",new ChannelInfo("0003C401","(Q1+Q2)+(Q3+Q4) fund + harmonics","var")); 
        map.put("0007C401",new ChannelInfo("0007C401","(Q1+Q2)-(Q3+Q4) fund + harmonics","var"));         
        map.put("0003C503",new ChannelInfo("0003C503","Phasor Apparent VAh, fund + harmonics (LP)","VAh"));    
        map.put("0003C003",new ChannelInfo("0003C003","Phasor Apparent VAh, fund + harmonics (read)","VAh"));    
        map.put("0003C403",new ChannelInfo("0003C403","Phasor Apparent VA, fund + harmonics","VA"));   
        map.put("80280208",new ChannelInfo("80280208","VA fund + harmonics","VA"));  
        map.put("80280209",new ChannelInfo("80280209","Average VA fund + harmonics","V")); 
 
    }
    
    public ChannelInfo getChannelInfo(String code) throws Exception
    {
        ChannelInfo tmp = (ChannelInfo)map.get(code);
        if(tmp == null)
            throw new Exception("There's no match channel information with code=>"+code);
        
        return tmp;
    }
    
    public ChannelInfo[] getChannelInfos(String[] code) throws Exception
    {
        ChannelInfo[] info = new ChannelInfo[code.length];
        for(int i = 0; i < code.length; i++){
            ChannelInfo tmp = (ChannelInfo)map.get(code[i]);
            if(tmp == null){
                throw new Exception("There's no match channel information with code=>"+code[i]);
            }else{
                info[i] = new ChannelInfo();
                info[i] = tmp;
            }
        }
        return info;
    }
    
    public String getChannelMap(String[] code) throws Exception {
        
        StringBuffer sb = new StringBuffer();
        ChannelInfo[] chinfo = null;
        chinfo = getChannelInfos(code);
        if(chinfo != null){
            for(int i = 0; i < chinfo.length; i++){
                sb.append(chinfo[i].getChannelString(i+1));
            }
            sb.append("pf=pf");
        }
        return sb.toString();
        
    }    
}
