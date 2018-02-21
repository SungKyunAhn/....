package com.aimir.fep.meter.saver;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.math.MathContext;
import java.util.Set;
import com.aimir.util.Condition;
import com.aimir.util.DecimalUtil;
import com.aimir.util.Condition.Restriction;

import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.constants.CommonConstants.MeteringFlag;
import com.aimir.constants.CommonConstants.MeteringType;
import com.aimir.constants.CommonConstants.MeteringDataType;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.meter.data.BillingData;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.meter.parser.DLMSTable.DLMSVARIABLE.OBIS;
import com.aimir.model.device.Meter;

/**
 * Manual Metering data saver.
 * @author bmhan
 */
@Service
public class ManualMDSaver extends AbstractMDSaver {

    @Override
    protected boolean save(IMeasurementData md) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }


	public boolean saveBilling(String meterNo, String meteringDate,
			double meteringValue, MeteringDataType mDataType ) throws Exception {
	        log.info("meterNo[" + meterNo + "] meteringDate[" + meteringDate + "] meteringValue[" + meteringValue + "]");
	        
            Meter meter = meterDao.get(meterNo);
		    System.out.println("MeterType["+meter.getMeterType()+"]");
            //meter의 meteringdata 및 readdata 갱신
        	meter.setLastMeteringValue(meteringValue);
        	meter.setLastReadDate(meteringDate+"000000");	

			meterDao.saveOrUpdate(meter);
			
			//Metering Data Save            
            saveMeteringData(MeteringType.Manual, meteringDate.substring(0, 8),
					"000000", meteringValue, meter, DeviceType.Meter, meterNo, DeviceType.Meter, meterNo, null);
            
        	BillingData bill = new BillingData();
            bill.setActiveEnergyRateTotal(meteringValue);  
            bill.setBillingTimestamp(meteringDate+"000000");
            
            saveMonthlyBilling(bill, meter, null, null, DeviceType.Meter, meterNo);            
	        
	        return true;
	}
    
    
    
	/**
	* Parser를 사용하지 않고 UI에서 직접 검침값을 입력한다.
	* @param meterNo
	* @param meteringTime
	* @param meteringValue
    * @param mDataType 일별 / 월별 / 15분 구분
    * @return 
	*/
	public boolean saveNew(String meterNo, String meteringDate,
			double meteringValue, MeteringDataType mDataType ) throws Exception
	{
        log.info("meterNo[" + meterNo + "] meteringDate[" + meteringDate + "] meteringValue[" + meteringValue + "]");
        
		int lpInterval = 15;
	
		
		
        try {
        	String meteringType = "";
    		String searchType = "";
            Meter meter = meterDao.get(meterNo);
		    System.out.println("MeterType["+meter.getMeterType()+"]");
		    meteringType = meter.getMeterType().getName();
		    if(meteringType.equals("EnergyMeter")){
		    	meteringType = "EM";
		    }else if(meteringType.equals("GasMeter")){
		    	meteringType = "GM";
		    }else if(meteringType.equals("WaterMeter")){
		    	meteringType = "WM";
		    }else if(meteringType.equals("HeatMeter")){
		    	meteringType = "HM";
		    }
		    
            meter.setLpInterval(lpInterval);

            //meter의 meteringdata 및 readdata 갱신
        	meter.setLastMeteringValue(meteringValue);
        	meter.setLastReadDate(meteringDate+"000000");	

			meterDao.saveOrUpdate(meter);

			log.debug("meter's meteringValue & lastReadDate save");
			
 			Double deltaValue = 0.0;
 			
 		    List<Object> meterList = new ArrayList<Object>();
 			
 			searchType = "LT";
 			Map<String, Object> preCondition = new HashMap<String, Object>();
 			preCondition.put("meteringType", meteringType);
 			preCondition.put("mdsId", meterNo);
 			preCondition.put("searchType", searchType);
 			preCondition.put("yyyymmddhhmmss", meteringDate+"000000");
 			List<Object> premeterList = meteringDataDao.getLastRegisterMeteringData(preCondition);

        	if(premeterList.size()!=0  && !premeterList.isEmpty()){	
        		meterList.add(premeterList.get(0));
        	}

        	searchType = "GT";
        	Map<String, Object> aftCondition = new HashMap<String, Object>();
        	aftCondition.put("meteringType", meteringType);
        	aftCondition.put("mdsId", meterNo);
        	aftCondition.put("searchType", searchType);
        	aftCondition.put("yyyymmddhhmmss", meteringDate+"000000");
 			List<Object> aftmeterList = meteringDataDao.getLastRegisterMeteringData(aftCondition);
 			
        	if(aftmeterList.size()!=0  && !aftmeterList.isEmpty()){
        		meterList.addAll(aftmeterList);
        	}
        	
			if ( meteringDate.length() == 6 ) {
					meteringDate = meteringDate + "30"; //마지막날 검침
			}

			//Metering Data Save            
            saveMeteringData(MeteringType.Manual, meteringDate.substring(0, 8),
					"000000", meteringValue, meter, DeviceType.Meter, meterNo, DeviceType.Meter, meterNo, null);
  
            if(meterList.size()==0 && meterList.isEmpty()){
            	searchType = "EQ";
            	Map<String, Object> newCondition = new HashMap<String, Object>();
            	newCondition.put("meteringType", meteringType);
            	newCondition.put("mdsId", meterNo);
            	newCondition.put("searchType", searchType);
            	newCondition.put("yyyymmddhhmmss", meteringDate+"000000");
     			List<Object> newmeterList = meteringDataDao.getLastRegisterMeteringData(newCondition);
     			if(newmeterList.size() != 0 && !newmeterList.isEmpty()){
     				meterList.add(newmeterList.get(0));
     			}else{
     				return true;
     			}
            }
            
            /*####################################################################*/
            Double premeteringValue = 0.0;
            
			for(int j = 0; j< meterList.size() ; j++){
				
 			 Map<String, Object> map = (Map<String, Object>) meterList.get(j);
 			 
				if(j==0){
					deltaValue = meteringValue-DecimalUtil.ConvertNumberToDouble(map.get("VALUE"));
				}else {
					premeteringValue = meteringValue;
					//meteringValue	 = meterList.get(j).getValue();
					meteringValue	 = DecimalUtil.ConvertNumberToDouble(map.get("VALUE"));
					deltaValue 		 = meteringValue - premeteringValue;
					
					//meteringDate 	 = meterList.get(j).getYyyymmdd();
					meteringDate     = (String) map.get("YYYYMMDDHHMMSS");
	
				}
				 
				int divideflag = 0;
				// MeteringData Type에 의하여 나누는 값을 다르게 한다.
				switch(mDataType) {
					case Day:
						divideflag = 96; // 4 * 24
						break;
					case Month:
						divideflag = 96 * 30;
						break;
					case LoadProfile: 
						divideflag = 4;
						break;
					default : 
						divideflag = 1; //
						log.error(" Not Supported mDataType["+mDataType+"]");
				}

				
				BigDecimal dValue = new BigDecimal(deltaValue);
				BigDecimal divideValue = new BigDecimal(divideflag);

				double lpValue = dValue.divide(divideValue, MathContext.DECIMAL32).setScale(
									5, BigDecimal.ROUND_DOWN).doubleValue();

				log.debug("deltaValue["+dValue+"] lpValue["+lpValue+"]");

				double[] lplist = new double[divideflag];

				double[][] lpValues = new double[4][lplist.length]; 

				for (int ch = 0; ch < lpValues.length; ch++) { 
					for (int lpcnt = 0; lpcnt < lpValues[ch].length; lpcnt++) { 
						//lpValues[ch][lpcnt] = validlplist[lpcnt].getCh()[ch]; 
						if ( ch == 0 )
							lpValues[ch][lpcnt] = lpValue;
						else 
							lpValues[ch][lpcnt] = 0;
					} 
				}

	            int[] flaglist = new int[lplist.length];
	            for (int i = 0 ; i < flaglist.length; i++) {
	                flaglist[i] = MeteringFlag.Correct.getFlag();
	            }

				//LPData save
	            saveLPData(MeteringType.Manual, meteringDate.substring(0, 8), 
					"0000", lpValues, flaglist, meteringValue, meter, DeviceType.Meter, meterNo, DeviceType.Meter, meterNo);
	            
	            
			}

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
            return false;
        }
		
		return true;
	}
}
