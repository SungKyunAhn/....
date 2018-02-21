package com.aimir.fep.protocol.fmp.log;

import  java.io.File;

public class LogFilenameFilter implements java.io.FilenameFilter
{ 
    public boolean accept(File dir,String name) 
    { 
        boolean result = false; 
        try 
        { 
            if(name.endsWith(".log")) 
                return true; 
        }catch(Exception ex) {} 
        return result; 
    }
}
