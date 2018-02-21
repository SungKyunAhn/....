package com.aimir.fep.meter.parser.MX2Table;

import com.aimir.fep.util.DataUtil;


/**
 * @author kskim
 */
public class TOUSeasonChange implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7215267117886211678L;
	enum Season{
		Season1(1),
		Season2(2),
		Season3(3),
		Season4(4);
		
		int value;
		
		Season(int value){
			this.value=value;
		}
		
		public int getValue(){
			return this.value;
		}
	}
	
	String MMdd;
	Season season;
	
	public String getMMdd() {
		return MMdd;
	}
	public void setMMdd(String mMdd) {
		MMdd = mMdd;
	}
	public Season getSeason() {
		return season;
	}
	public void setSeason(Season season) {
		this.season = season;
	}
	public byte[] toByteArray() throws Exception {
		byte[] data = new byte[3];
		
		if(this.MMdd == null && this.MMdd.length()!=4)
			throw new Exception("Can not found MMDD data");
		
		byte[] bcd = DataUtil.getBCD(this.MMdd);
		
		System.arraycopy(bcd, 0, data, 0, 2);
		
		if(this.season==null)
			throw new Exception("Can not found Season data");
			
		data[2] = (byte) this.season.getValue();
		
		return data;
	}
	
	
}
