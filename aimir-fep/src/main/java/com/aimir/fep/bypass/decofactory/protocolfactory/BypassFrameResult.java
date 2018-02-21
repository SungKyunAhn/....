/**
 * (@)# BypassFrameResult.java
 *
 * 2016. 5. 2.
 *
 * Copyright (c) 2013 NURITELECOM, Inc.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of 
 * NURITELECOM, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with NURITELECOM, Inc.
 *
 * For more information on this product, please see
 * http://www.nuritelecom.co.kr
 *
 */
package com.aimir.fep.bypass.decofactory.protocolfactory;

import java.util.HashMap;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.aimir.fep.bypass.decofactory.protocolfactory.BypassFrameFactory.Procedure;

/**
 * @author simhanger
 *
 */
public class BypassFrameResult {
	private boolean resultState;
	private boolean finished;
	private Procedure lastProcedure;
	private Procedure step;
	private HashMap<String, Object> resultValueMap;
	private Object resultValue;

	public boolean isResultState() {
		return resultState;
	}

	public Procedure getStep() {
		return step;
	}

	public void setStep(Procedure step) {
		this.step = step;
	}

	public void setResultState(boolean resultState) {
		this.resultState = resultState;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public Procedure getLastProcedure() {
		return lastProcedure;
	}

	public void setLastProcedure(Procedure lastProcedure) {
		this.lastProcedure = lastProcedure;
	}

	public Object getResultValue() {
		return resultValue;
	}

	public void setResultValue(Object resultValue) {
		this.resultValue = resultValue;
	}

	public void addResultValue(String key, Object value) {
		if (resultValueMap == null) {
			resultValueMap = new HashMap<String, Object>();
		}
		resultValueMap.put(key, value);
	}

	public Object getResultValue(String key) {
		if (resultValueMap != null) {
			return resultValueMap.get(key);
		}
		return null;
	}
	
	public HashMap<String, Object> getValueMap(){
		return this.resultValueMap;
	}

	public int getValueSize(){
		if(resultValueMap == null){
			return 0;
		}else {
			return resultValueMap.size();
		}
	}
	
	public void clearResultValueMap() {
		if (resultValueMap != null) {
			resultValueMap.clear();
		}
	}

	public void clean() {
		resultState = false;
		finished = false;
		lastProcedure = null;
		resultValue = null;
		resultValueMap = null;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
