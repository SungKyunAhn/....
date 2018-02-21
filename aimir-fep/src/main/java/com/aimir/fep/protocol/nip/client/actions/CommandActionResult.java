/**
 * 
 */
package com.aimir.fep.protocol.nip.client.actions;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author simhanger
 *
 */
public class CommandActionResult {
	private String commnad;
	private boolean isSuccess;
	private Object resultValue;

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
