package com.aimir.fep.meter.parser.a1830rlnTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.PowerQualityMonitor;

public class A1800_PQ implements java.io.Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = -7349106471852686593L;
	private Log log = LogFactory.getLog(A1800_PQ.class);
	private byte[] rawData = null;
	private byte[] m50 = new byte[321];
	private byte[] m54 = new byte[25];
	private byte[] m53 = new byte[0];
	private MT50 mt50 = null;
	private MT54 mt54 = null;
	private MT53 mt53 = null;
	
	
	public A1800_PQ(byte[] rawData){
		this.rawData = rawData;
		parse();
	}
	
	public void parse() {		
		
        int pos = 0;
        System.arraycopy(rawData, pos, m50, 0, m50.length);
        pos += m50.length;
        this.mt50 = new MT50(m50);
        
        System.arraycopy(rawData, pos, m54, 0, m54.length);
        pos += m54.length;        
		this.mt54 = new MT54(m54);
		
		m53 = new byte[rawData.length - pos];
        System.arraycopy(rawData, pos, m53, 0, m53.length);
        pos += m53.length;        
		this.mt53 = new MT53(m53);
		
		
	}
	
	
	public PowerQualityMonitor getPowerQualityMonitor(){
		
		PowerQualityMonitor pqm = new PowerQualityMonitor();

		try {
						
			pqm.setSERVICE_VOL_CNT(mt50.getSvcVolCount());
			pqm.setSERVICE_VOL_DUR(mt50.getSvcVolCount());
			pqm.setSERVICE_VOL_STAT(mt50.getSvcVolStatus());
			
			pqm.setLOW_VOL_CNT(mt50.getLowVolCount());
			pqm.setLOW_VOL_DUR(mt50.getLowVolDuration());
			pqm.setLOW_VOL_STAT(mt50.getLowVolStatus());
			
			pqm.setHIGH_VOL_CNT(mt50.getHighVolCount());
			pqm.setHIGH_VOL_DUR(mt50.getHighVolDuration());
			pqm.setHIGH_VOL_STAT(mt50.getHighVolStatus());
			 
			
			pqm.setREVERSE_PWR_CNT(mt50.getReversePowerCount());
			pqm.setREVERSE_PWR_DUR(mt50.getReversePowerDuration());
			pqm.setREVERSE_PWR_STAT(mt50.getReversePowerStatus());
			
			pqm.setLOW_CURR_CNT(mt50.getLowCurrentCount());
			pqm.setLOW_CURR_DUR(mt50.getLowCurrentDuration());
			pqm.setLOW_CURR_STAT(mt50.getLowCurrentStatus());
			
			pqm.setPFACTOR_CNT(mt50.getPowerFactorCount());
			pqm.setPFACTOR_DUR(mt50.getPowerFactorDuration());
			pqm.setPFACTOR_STAT(mt50.getPowerFactorStatus());			
			
			pqm.setHARMONIC_CNT(mt50.getHarmonicCount());
			pqm.setHARMONIC_DUR(mt50.getHarmonicDuration());
			pqm.setHARMONIC_STAT(mt50.getHarmonicStatus());
			
			pqm.setTHD_VOL_CNT(mt50.getTHDVolCount());
			pqm.setTHD_VOL_DUR(mt50.getTHDVolDuration());
			pqm.setTHD_VOL_STAT(mt50.getTHDVolStatus());
			
			pqm.setTHD_CURR_CNT(mt50.getTHDCurrCount());
			pqm.setTHD_CURR_DUR(mt50.getTHDCurrDuration());
			pqm.setTHD_CURR_STAT(mt50.getTHDCurrStatus());		
			
			pqm.setTDD_CNT(mt50.getTDDCount());
			pqm.setTDD_DUR(mt50.getTDDDuration());
			pqm.setTDD_STAT(mt50.getTDDStatus());
		
			pqm.setIMBALANCE_VOL_CNT(mt50.getVolImbCount());
			pqm.setIMBALANCE_VOL_DUR(mt50.getVolImbDuration());
			pqm.setIMBALANCE_VOL_STAT(mt50.getVolImbStatus());			
						
			pqm.setIMBALANCE_CURR_CNT(mt50.getCurrImbCount());
			pqm.setIMBALANCE_CURR_DUR(mt50.getCurrImbDuration());
			pqm.setIMBALANCE_CURR_STAT(mt50.getCurrImbStatus());

			pqm.setVOL_A_SAG_CNT(mt54.getA_CUM_SAG_COUNTER());
			pqm.setVOL_A_SAG_DUR(mt54.getA_CUM_SAG_TIMER());
			pqm.setVOL_B_SAG_CNT(mt54.getB_CUM_SAG_COUNTER());
			pqm.setVOL_B_SAG_DUR(mt54.getB_CUM_SAG_TIMER());
			pqm.setVOL_C_SAG_CNT(mt54.getC_CUM_SAG_COUNTER());
			pqm.setVOL_C_SAG_DUR(mt54.getC_CUM_SAG_TIMER());
			
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e,e);
		}

		return pqm;
	}
}
