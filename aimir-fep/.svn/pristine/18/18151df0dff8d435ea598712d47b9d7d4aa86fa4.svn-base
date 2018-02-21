/*
 * Created on 2007. 08. 01
 */
package com.aimir.fep.meter.data;

/**
 * @author
 * @version 1.0
 * 
 * EJB Remote Data Transger Object.
 */
public class MeterTimeSyncData implements java.io.Serializable{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 3202019741049919907L;
	private String id;
    private String btime;
    private String atime;
    private String ctime;
    private String etime;
    private int result;
    private int method;
    private String userID;
    private String content;
    private long timediff;
   
	public MeterTimeSyncData() 
    {

	}

    public String getId() 
    {
        return id;
    }
    
    public String getBtime()
    {
        return btime;
    }
    
    public String getAtime()
    {
        return atime;
    }

    public String getCtime()
    {
        return ctime;
    }
    
    public String getEtime()
    {
        return etime;
    }

    public int getResult()
    {
        return result;
    }
    
    public int getMethod()
    {
        return method;
    }
    
    public String getUserID()
    {
        return userID;
    }
    
    public String getContent()
    {
        return content;
    }
    
    public long getTimediff()
    {
        return timediff;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }    
    
    public void setAtime(String atime)
    {
        this.atime = atime;
    }
    
    public void setBtime(String btime)
    {
        this.btime = btime;
    }
    
    public void setCtime(String ctime)
    {
        this.ctime = ctime;
    }
    
    public void setEtime(String etime)
    {
        this.etime = etime;
    }

    public void setResult(int result)
    {
        this.result = result;
    }

    public void setMethod(int method)
    {
        this.method = method;
    }
    
    public void setUserID(String userID)
    {
        this.userID = userID;
    }
    
    public void setContent(String content)
    {
        this.content = content;
    }
    
    public void setTimediff(long timediff)
    {
        this.timediff = timediff;
    }

    // overiding toString.
	public String toString() {
        
		StringBuffer sb = new StringBuffer(super.toString());

		sb.append(" <BR> \n");
        sb.append("id: ").append(id).append("<BR> \n");
        sb.append("atime: ").append(atime).append("<BR> \n");
        sb.append("btime: ").append(btime).append("<BR> \n");
        sb.append("ctime: ").append(ctime).append("<BR> \n");
        sb.append("etime: ").append(etime).append("<BR> \n");
        sb.append("userID: ").append(userID).append("<BR> \n");
        sb.append("content: ").append(content).append("<BR> \n");
        sb.append("timediff: ").append(timediff).append("<BR> \n");
	
		return sb.toString();
	}

}
