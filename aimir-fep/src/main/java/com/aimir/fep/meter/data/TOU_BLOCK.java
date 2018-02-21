/** 
 * @(#)TOU_BLOCK.java       1.0 06/06/09 *
 * 
 * TOU Block Data Class.
 * Copyright (c) 2006-2007 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
package com.aimir.fep.meter.data;

import java.util.ArrayList;

/**
 * @author PYK
 */
public class TOU_BLOCK implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3833799859071058473L;
	private byte[] lowdata;
	private int resetcount;
	private String resettime;
	private ArrayList summations;
	private ArrayList event_time;
	private ArrayList cumulative_demand;
	private ArrayList current_demand;
	private ArrayList coincidents;

	public TOU_BLOCK() {}
	
	public TOU_BLOCK(int nbr_summation, 
					 int nbr_eventtime, 
					 int nbr_cumdemand, 
					 int nbr_curr_demand, 
					 int nbr_coin) {
		this.summations        = new ArrayList(nbr_summation);
		this.event_time        = new ArrayList(nbr_eventtime);
		this.cumulative_demand = new ArrayList(nbr_cumdemand);
		this.current_demand    = new ArrayList(nbr_curr_demand);
		this.coincidents       = new ArrayList(nbr_coin);
	}
	
	public void setLowData(byte[] data){
		this.lowdata = data;
	}
	
	public void setSummations(int idx, Object o){
		this.summations.add(idx,o);
	}
	
	public void setEventTime(int idx, Object o){
		this.event_time.add(idx,o);
	}
	
	public void setCumDemand(int idx, Object o){
		this.cumulative_demand.add(idx,o);
	}
	
	public void setCurrDemand(int idx, Object o){
		this.current_demand.add(idx,o);
	}
	
	public void setCoincident(int idx, Object o){
		this.coincidents.add(idx,o);
	}
	
	public int getResetCount(){
		return this.resetcount;
	}

	public String getResetTime(){
		return this.resettime;
	}
	
	public void setResetCount(int resetcount){
		this.resetcount = resetcount;
	}

	public void setResetTime(String resettime){
		this.resettime = resettime;
	}
	
	public ArrayList getSummations(){
		return this.summations;
	}
	
	public ArrayList getEventTime(){
		return this.event_time;
	}
	
	public ArrayList getCumDemand(){
		return this.cumulative_demand;
	}
	
	public ArrayList getCurrDemand(){
		return this.current_demand;
	}
	
	public ArrayList getCoincident(){
		return this.coincidents;
	}

	public Object getSummation(int idx){
		return this.summations.get(idx);
	}
	
	public Object getEventTime(int idx){
		return this.event_time.get(idx);
	}
	
	public Object getCumDemand(int idx){
		return this.cumulative_demand.get(idx);
	}
	
	public Object getCurrDemand(int idx){
		return this.current_demand.get(idx);
	}
	
	public Object getCoincident(int idx){
		return this.coincidents.get(idx);
	}

}
