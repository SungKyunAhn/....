package com.aimir.fep.iot.service.utils.filter;

import java.io.File;

public class DatFileFilter implements java.io.FilenameFilter
{ 
    public boolean accept(File dir, String name) 
    { 
        boolean result = false; 
        try 
        { 
            if(name.endsWith(".TMP") && name.contains("_HE")) 
                return true;
            else if(name.endsWith(".TMP") && name.contains("_BE")) 
                return true;
            
        }catch(Exception ex) {} 
        return result; 
    }
}
