package com.aimir.fep.bypass;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.aimir.constants.CommonConstants.TR_STATE;
import com.aimir.fep.bypass.decofactory.protocolfactory.BypassFrameFactory;
import com.aimir.fep.protocol.fmp.client.Client;

/**
 * 바이패스 명령을 수행하기 위한 장비의 정보 이 객체는 명령이 끝날 때까지 세션에 있어야 한다.
 * 
 * @author elevas
 *
 */
public class BypassDevice {
	private String command;
	private String modemId = null;
	private String meterId = null;

	private String modemModel = null;
	private String fwVersion = null;
	private String buildno = null;
	private String hwVersion = null;

	private byte[] fw_bin = null;
	private int packet_size = 0;

	private int offset = 0;
	private ByteArrayInputStream fw_in = null;
	private int frameSequence = 0;
	private int retry = 0;

	private long asyncTrId = 0;

	private String asyncCreateDate = null;

    private Client client;
    
	// 비동기 명령 이력의 인자
	@SuppressWarnings("rawtypes")
	private List args = new ArrayList();
	private HashMap<String, Object> argMap;
	private Long transactionId;
	private TR_STATE trState;
	private long startOTATime;

	private int remainPackateLength;
	private String fwCRC;
	private boolean takeOver;
	private int sendDelayTime;

	// INSERT START SP-681;
	private String optVersion;
	private String optModel;
	private String optTime;
	// INSERT END SP-681	
	
	
	/**
	 * Meter F/W OTA시 추가
	 */
	private BypassFrameFactory frameFactory;

	public String getModemId() {
		return modemId;
	}

	public void setModemId(String modemId) {
		this.modemId = modemId;
	}

	public String getMeterId() {
		return meterId;
	}

	public void setMeterId(String meterId) {
		this.meterId = meterId;
	}

	public byte[] getFw_bin() {
		return fw_bin;
	}

	public void setFw_bin(byte[] fw_bin) {
		this.fw_bin = fw_bin;
	}

	public int getPacket_size() {
		return packet_size;
	}

	public void setPacket_size(int packet_size) {
		this.packet_size = packet_size;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public ByteArrayInputStream getFw_in() {
		return fw_in;
	}

	public void setFw_in(ByteArrayInputStream fw_in) {
		this.fw_in = fw_in;
	}

	public String getModemModel() {
		return modemModel;
	}

	public void setModemModel(String modemModel) {
		this.modemModel = modemModel;
	}

	public String getFwVersion() {
		return fwVersion;
	}

	public void setFwVersion(String fwVersion) {
		this.fwVersion = fwVersion;
	}

	public String getBuildno() {
		return buildno;
	}

	public void setBuildno(String buildno) {
		this.buildno = buildno;
	}

	public String getHwVersion() {
		return hwVersion;
	}

	public void setHwVersion(String hwVersion) {
		this.hwVersion = hwVersion;
	}

	@SuppressWarnings("unchecked")
	public void addArg(Object arg) {
		this.args.add(arg);
	}

	@SuppressWarnings("rawtypes")
	public List getArgs() {
		return this.args;
	}

	public int getRetry() {
		return retry;
	}

	public void setRetry(int retry) {
		this.retry = retry;
	}

	public HashMap<String, Object> getArgMap() {
		return argMap;
	}

	public void setArgMap(HashMap<String, Object> argMap) {
		this.argMap = argMap;
	}

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	public TR_STATE getTrState() {
		return trState;
	}

	public void setTrState(TR_STATE trState) {
		this.trState = trState;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public long getStartOTATime() {
		return startOTATime;
	}

	public void setStartOTATime(long startOTATime) {
		this.startOTATime = startOTATime;
	}

	public BypassFrameFactory getFrameFactory() {
		return frameFactory;
	}

	public void setFrameFactory(BypassFrameFactory frameFactory) {
		this.frameFactory = frameFactory;
	}

	public long getAsyncTrId() {
		return asyncTrId;
	}

	public void setAsyncTrId(long asyncTrId) {
		this.asyncTrId = asyncTrId;
	}

	public String getAsyncCreateDate() {
		return asyncCreateDate;
	}

	public void setAsyncCreateDate(String asyncCreateDate) {
		this.asyncCreateDate = asyncCreateDate;
	}

	public int getRemainPackateLength() {
		return remainPackateLength;
	}

	public void setRemainPackateLength(int remainPackateLength) {
		this.remainPackateLength = remainPackateLength;
	}

	public String getFwCRC() {
		return fwCRC;
	}

	public void setFwCRC(String fwCRC) {
		this.fwCRC = fwCRC;
	}

	public boolean isTakeOver() {
		return takeOver;
	}

	public void setTakeOver(boolean takeOver) {
		this.takeOver = takeOver;
	}

	public int getSendDelayTime() {
		return sendDelayTime;
	}

	public void setSendDelayTime(int sendDelayTime) {
		this.sendDelayTime = sendDelayTime;
	}

	public int getFrameSequence() {
		return frameSequence;
	}

	public int getNextFrameSequence() {
		frameSequence++;
		if (31 < frameSequence) {
			frameSequence = 0;
		}

		return frameSequence;
	}

	// INSERT START SP-681;
	public String getOptionalVersion() {
		return optVersion;
	}

	public void setOptionalVersion(String version) {
		this.optVersion = version;
	}

	public String getOptionalModel() {
		return optModel;
	}

	public void setOptionalModel(String model) {
		this.optModel = model;
	}	

	public String getOptionalInstallTime() {
		return optTime;
	}

	public void setOptionalInstallTime(String time) {
		this.optTime = time;
	}
	// INSERT END SP-681	
	
	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	} 
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
