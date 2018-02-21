/** 
 * @(#)Modem.java       1.0 03/09/01 *
 * Copyright (c) 2003-2004 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */

package com.aimir.fep.protocol.mrp.command.frame;

import com.aimir.fep.util.FMPProperty;

/** 
 * Modem Class. 
 * 
 * @version     1.0 1 Sep 2003 
 * @author		Park YeonKyoung yeonkyoung@hanmail.net  
 */

public class Modem {

    String scuid;        //modem number
    String serverip;     
    String port;        
    String period;       //metering cycle(packet cycle)
    String metertype;    //meter model
    String meternum;     //meter serial number
    String ssnkey;       //session key
    String reset;        //hardware reset cycle
    String speed;        //model comm speed 9600/4800/2400
    String version;      //firmware version
    String rcvlevel;     //rf signal quality

    public Modem() {

    }

    public String getScuid()     { return this.scuid;     }

    public String getServerip()  { return this.serverip;  }

    public String getPort()      { return this.port;      }

    public String getPeriod()    { 
        String period = "";
        try {
            period 
                = String.valueOf(Integer.parseInt(this.period)*15) + " min";
        } catch(NumberFormatException e) {
        } catch(Exception e) {
        }
        return period;
    }

    public String getMetertype() { 

        String ret = "";
        try {
            ret = FMPProperty.getProperty("Meter.Type."+this.metertype); 
        } catch(Exception e) {
        }
        return ret;
    }

    public String getMeternum()  { return this.meternum;  }

    public String getSsnkey()    { return this.ssnkey;   }

    public String getReset()     { return this.reset;     }

    public String getVersion()   { return this.version;   }

    public String getRcvlevel()  { return this.rcvlevel;  }

    public String getSpeed() { 

        if(this.speed.equals("0")) { 
            return "2400";
        } else if(this.speed.equals("1")) {      
            return "4800";
        } else if(this.speed.equals("2")) {     
            return "9600";
        } else if(this.speed.equals("3")) {     
            return "19200";
        } else {
            return "2400";
        }

    }


    public void setScuid(String scuid)         { this.scuid     = scuid;     }

    public void setServerip(String serverip)   { this.serverip  = serverip;  }

    public void setPort(String port)           { this.port      = port;      }

    public void setPeriod(String period)       { this.period    = period;    }

    public void setMetertype(String metertype) { this.metertype = metertype; }

    public void setMeternum(String meternum)   { this.meternum  = meternum;  }

    public void setSsnkey(String ssnkey)       { this.ssnkey    = ssnkey;    }

    public void setReset(String reset)         { this.reset     = reset;     }

    public void setVersion(String version)     { this.version   = version;   }

    public void setRcvlevel(String rcvlevel)   { this.rcvlevel  = rcvlevel;  }

    public void setSpeed(String speed)         { this.speed     = speed;     }

}
