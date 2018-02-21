package com.aimir.fep.meter.parser.rdata;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataUtil;

/**
 * MeterConfiguration Format
 * 
 * @author kaze
 * 
 */
public class MeterConfigurationRData extends RData {
	private static Log log = LogFactory.getLog(MeterConfigurationRData.class);
	private byte[] SHORTID = new byte[4];
	private byte[] CHANNELCOUNT = new byte[1];
	private byte[] CHANNELCONFIGURE = new byte[6];

	private int shortId;
	private int channelCount;
	private ArrayList<ChannelConfigure> channelConfigures = new ArrayList<ChannelConfigure>();

	public void parsingPayLoad() throws Exception {
		int pos = 0;

		// ----------------------------
		// [step1] Raw Data를 잘라서 Byte에 지정
		// ----------------------------
		System.arraycopy(payload, pos, SHORTID, 0, SHORTID.length);
		pos += SHORTID.length;
		System.arraycopy(payload, pos, CHANNELCOUNT, 0, CHANNELCOUNT.length);
		pos += CHANNELCOUNT.length;

		// ------------------------------
		// [step2] 각각 잘린 Byte로 부터 데이터를 파싱
		// ------------------------------
		shortId = DataUtil.getIntToBytes(SHORTID);
		channelCount=DataUtil.getIntToBytes(CHANNELCOUNT);
		for(int i=0;i<channelCount;i++) {
			System.arraycopy(payload, pos, CHANNELCONFIGURE, 0, CHANNELCONFIGURE.length);
			pos += CHANNELCONFIGURE.length;
			ChannelConfigure channelConfigure = new ChannelConfigure(CHANNELCONFIGURE);
			channelConfigure.parsingPayLoad();
			channelConfigures.add(channelConfigure);			
		}
	}


	/**
	 * DCU에서 할당하는 장비 관리 ID
	 * @return
	 */
	public int getShortId() {
		return shortId;
	}


	/**
	 * Channel 수. Channel Configure가 이 수 만큼 반복됨
	 * @return
	 */
	public int getChannelCount() {
		return channelCount;
	}


	/**
	 * ChannelCount 수 만큼 ChannelConfigure 객체가 담겨 있는 리스트
	 * @return
	 */
	public ArrayList<ChannelConfigure> getChannelConfigures() {
		return channelConfigures;
	}


	@Override
	public String toString() {
		return "MeterConfigurationRData [SHORTID=" + Arrays.toString(SHORTID) + ", CHANNELCOUNT=" + Arrays.toString(CHANNELCOUNT) + ", CHANNELCONFIGURE=" + Arrays.toString(CHANNELCONFIGURE)
				+ ", shortId=" + shortId + ", channelCount=" + channelCount + ", channelConfigures=" + channelConfigures + "]";
	}		
}
