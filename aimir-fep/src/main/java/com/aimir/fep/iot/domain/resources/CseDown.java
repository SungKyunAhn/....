/**
 * Copyright (c) 2015, Sang June Lee < blue4444eye@hanmail.net > < blue4444eye@gmail.com >.
   All rights reserved.

   Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
   1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
   2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
   3. The name of the author may not be used to endorse or promote products derived from this software without specific prior written permission.
   
   THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package com.aimir.fep.iot.domain.resources;

import java.io.Serializable;

/**
 * CseDown domain.
 * @author <ul>
 *         <li>Sang June Lee < blue4444eye@hanmail.net > < blue4444eye@gmail.com ></li>
 *         </ul>
 */

public class CseDown implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long downKey;
	private String downCriteriaId;
	private String cseId;
	private String startDt;
	private String endDt;
	private String lastDownYn;
	private String regiDate;
	private String regiUser;

	public long getDownKey() {
		return downKey;
	}

	public void setDownKey(long downKey) {
		this.downKey = downKey;
	}

	public String getDownCriteriaId() {
		return downCriteriaId;
	}

	public void setDownCriteriaId(String downCriteriaId) {
		this.downCriteriaId = downCriteriaId;
	}

	public String getCseId() {
		return cseId;
	}

	public void setCseId(String cseId) {
		this.cseId = cseId;
	}

	public String getStartDt() {
		return startDt;
	}

	public void setStartDt(String startDt) {
		this.startDt = startDt;
	}

	public String getEndDt() {
		return endDt;
	}

	public void setEndDt(String endDt) {
		this.endDt = endDt;
	}

	public String getLastDownYn() {
		return lastDownYn;
	}

	public void setLastDownYn(String lastDownYn) {
		this.lastDownYn = lastDownYn;
	}

	public String getRegiDate() {
		return regiDate;
	}

	public void setRegiDate(String regiDate) {
		this.regiDate = regiDate;
	}

	public String getRegiUser() {
		return regiUser;
	}

	public void setRegiUser(String regiUser) {
		this.regiUser = regiUser;
	}

	@Override
	public String toString() {
		return "FaultCseDown [downKey=" + downKey + ", downCriteriaId=" + downCriteriaId + ", cseId=" + cseId + ", startDt=" + startDt + ", endDt=" + endDt + ", lastDownYn=" + lastDownYn + ", regiDate=" + regiDate + ", regiUser=" + regiUser + "]";
	}

}
