/**
 * (@)# BypassResult.java
 *
 * 2016. 6. 2.
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
package com.aimir.fep.protocol.nip.client.bypass;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author simhanger
 *
 */
public class BypassResult {
	private String commnad;
	private boolean isSuccess;
	private Object resultValue;

	public BypassResult() {
		// TODO Auto-generated constructor stub
	}

	public BypassResult(String commnad, boolean isSuccess, Object resultValue) {
		this.commnad = commnad;
		this.isSuccess = isSuccess;
		this.resultValue = resultValue;
	}

	public String getCommnad() {
		return commnad;
	}

	public void setCommnad(String commnad) {
		this.commnad = commnad;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public Object getResultValue() {
		return resultValue;
	}

	public void setResultValue(Object resultValue) {
		this.resultValue = resultValue;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	public String toJson() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}

}
