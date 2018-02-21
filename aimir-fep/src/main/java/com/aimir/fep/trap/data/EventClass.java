package com.aimir.fep.trap.data;


/**
 * Event Attribute Class
 *
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-12-13 15:59:15 +0900 $,
 */
public class EventClass
{
    private String classId = null; 
    private String name = null;
    private String description = null;
    private Integer priority = null;
    private Integer timeout = null;
    private String message = null;

    /**
     * constructor
     */
    public EventClass()
    {
    }

    /**
     * constructor
     *
     * @param classId - Event Class Identifier
     * @param name - Event Class name
     * @param description - Event Class Description
     * @param priority - Event Class Priority
     * @param timeout - Event Class Timeout
     * @param message - Event Class Message
     */
    public EventClass(String classId,String name, String description,
            Integer priority,Integer timeout,String message)
    {
        this.classId = classId;
        this.name = name;
        this.description = description;
        this.priority = priority;
        this.timeout = timeout;
        this.message = message;
    }

    /**
     * get Event Class Identifier
     * @return Event Class Identifier
     */
    public String getClassId()
    {
        return this.classId;
    }
    /**
     * set Event Class Identifier
     * @param classId - Event Class Identifier
     */
    public void setClassId(String classId)
    {
        this.classId = classId;
    }

    /**
     * get Event Class Name
     * @return  Event Class Name
     */
    public String getName()
    {
        return this.name;
    }
    /**
     * set Event Class Name
     * @param name - Event Class Name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * get Event Class Description
     * @return Event Class Description
     */
    public String getDescription()
    {
        return this.description;
    }
    /**
     * set Event Class Description
     * @param description - Event Class Description
     */
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    /**
     * get Event Class Priority
     * @return Event Class Priority
     */
    public Integer getPriority()
    {
        return this.priority;
    }
    /**
     * set Event Class Priority
     * @param priority - Event Class Priority
     */
    public void setPriority(Integer priority)
    {
        this.priority = priority;
    }

    /**
     * get Event Class Timeout
     * @return Event Class Timeout
     */
    public Integer getTimeout()
    {
        return this.timeout;
    }
    /**
     * set Event Class Timeout
     * @param timeout - Event Class Timeout
     */
    public void setTimeout(Integer timeout)
    {
        this.timeout = timeout;
    }

    /**
     * get Event Message Format
     * @return Event Message Format
     */
    public String getMessage()
    {
        return this.message;
    }
    /**
     * set Event Message Format
     * @param message - Event Message Format
     */
    public void setMessage(String message)
    {
        this.message = message;
    }

    /**
     * get string
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        sb.append("classId = ").append(classId).append(", ");
        sb.append("name = ").append(name).append(", ");
        sb.append("description = ").append(description).append(", ");
        sb.append("priority = ").append(priority).append(", ");
        sb.append("timeout = ").append(timeout).append(", ");
        sb.append("message = ").append(message).append('\n');

        return sb.toString();
    }
}
