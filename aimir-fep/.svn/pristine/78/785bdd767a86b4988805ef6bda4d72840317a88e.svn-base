package com.aimir.fep.modem;

import com.aimir.fep.modem.ModemCommandData.Event;

public class EventMessage implements java.io.Serializable {

	private static final long serialVersionUID = -5019489080096183115L;
	private String eventTime;
    private Event event;
    private boolean eventStatus=false;
    private String temperature;
    public String getEventTime() {
        return eventTime;
    }
    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }
    public Event getEvent() {
        return event;
    }
    public void setEvent(Event event) {
        this.event = event;
    }
    public boolean isEventStatus() {
        return eventStatus;
    }
    public void setEventStatus(boolean eventStatus) {
        this.eventStatus = eventStatus;
    }
    public String getTemperature() {
        return temperature;
    }
    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }
    
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("eventTime=" + eventTime);
        buf.append(", event=" + event);
        buf.append(", eventStatus=" + eventStatus);
        buf.append(", temperature=" + temperature);
        
        return buf.toString();
    }
}
