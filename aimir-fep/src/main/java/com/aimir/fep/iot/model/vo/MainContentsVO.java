package com.aimir.fep.iot.model.vo;

import java.io.Serializable;

public class MainContentsVO implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -2202175699511921484L;
	/**
	 * 작업항목 이름
	 */
	private String workItemName;
	/**
	 * To-Do List 항목 별 업무화면 URL
	 */
	private String workItemURL;

	public MainContentsVO(){

	}

	/**
	 * 
	 * @exception Throwable
	 */
	public void finalize()
	  throws Throwable{

	}

	/**
	 * getItemCount 항목 개수 getter
	 * @return
	 */
	public int getItemCount(){
		return 0;
	}

	/**
	 * getWorkItemName To-Do List 항목 명 getter
	 * @return To-Do List 항목 명
	 */
	public String getWorkItemName(){
		return workItemName;
	}

	/**
	 * getWorkItemURL 업무화면 URL getter
	 * @return 업무화면 URL
	 */
	public String getWorkItemURL(){
		return workItemURL;
	}

	/**
	 * setItemCount 항목 개수 setter
	 * 
	 * @param itemCount    itemCount
	 */
	public void setItemCount(int itemCount){

	}

	/**
	 * setWorkItemName To-Do List 항목 명 Setter
	 * 
	 * @param workItemName    To-Do List 항목 명
	 */
	public void setWorkItemName(String workItemName){

	}

	/**
	 * setWorkItemURL 업무화면 URL setter
	 * 
	 * @param workItemURL    업무화면 URL
	 */
	public void setWorkItemURL(String workItemURL){

	}

}
