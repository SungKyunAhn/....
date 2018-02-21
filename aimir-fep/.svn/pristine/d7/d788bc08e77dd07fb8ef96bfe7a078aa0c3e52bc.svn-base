package com.aimir.fep.meter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.MeterEventDao;
import com.aimir.dao.device.MeterEventLogDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.device.PowerAlarmLogDao;
import com.aimir.dao.mvm.BillingDayEMDao;
import com.aimir.dao.mvm.BillingMonthEMDao;
import com.aimir.dao.mvm.DayEMDao;
import com.aimir.dao.mvm.LpEMDao;
import com.aimir.dao.mvm.MeteringDataDao;
import com.aimir.dao.mvm.MonthEMDao;
import com.aimir.dao.mvm.PowerQualityDao;
import com.aimir.dao.mvm.PowerQualityStatusDao;
import com.aimir.dao.mvm.RealTimeBillingEMDao;
import com.aimir.dao.system.Co2FormulaDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.Meter;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;

public class SaveTest extends AbstractMDSaver {

    @Override
    protected boolean save(IMeasurementData md) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    @Test
    public void test() throws Exception {
        DataUtil.setApplicationContext(new ClassPathXmlApplicationContext(new String[]{"/config/spring.xml"}));
        
        double[][] lplist = new double[][] {{1, 2, 3, 4, 5, 6, 7, 8, 9}, 
                 {1, 2, 3, 4, 5, 6, 7, 8, 9},
                {1, 2, 3, 4, 5, 6, 7, 8, 9}};
        int[] flaglist = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0};
        
        JpaTransactionManager txManager = 
                (JpaTransactionManager)DataUtil.getBean("transactionManager");
        
        TransactionStatus txStatus = null;
        
        try {
            long starttime = System.currentTimeMillis();
            txStatus = txManager.getTransaction(null);
            
            meterDao = DataUtil.getBean(MeterDao.class);
            //mcuDao = DataUtil.getBean(MCUDao.class);
            modemDao = DataUtil.getBean(ModemDao.class);
            lpEMDao = DataUtil.getBean(LpEMDao.class);
            dayEMDao = DataUtil.getBean(DayEMDao.class);
            monthEMDao = DataUtil.getBean(MonthEMDao.class);;
            meteringDataDao = DataUtil.getBean(MeteringDataDao.class);;
            powerQualityDao = DataUtil.getBean(PowerQualityDao.class);
            powerQualityStatusDao = DataUtil.getBean(PowerQualityStatusDao.class);
            co2FormulaDao = DataUtil.getBean(Co2FormulaDao.class);
            billingDayEMDao = DataUtil.getBean(BillingDayEMDao.class);
            billingMonthEMDao = DataUtil.getBean(BillingMonthEMDao.class);
            realTimeBillingEMDao = DataUtil.getBean(RealTimeBillingEMDao.class);
            meterEventDao = DataUtil.getBean(MeterEventDao.class);
            meterEventLogDao = DataUtil.getBean(MeterEventLogDao.class);
            powerAlarmLogDao = DataUtil.getBean(PowerAlarmLogDao.class);
            codeDao = DataUtil.getBean(CodeDao.class);
            
            /*
            Meter meter = meterDao.get("Test-0001");
            
            saveLPData(MeteringType.Normal, "20120814", "080000",
                    lplist, flaglist, 10000, meter,
                    DeviceType.MCU, "1001", DeviceType.Meter, "Test-0001");
                    */

            Set condition = new HashSet<Condition>();
            condition.add(new Condition("id.mdevType", new Object[]{DeviceType.Meter}, null, Restriction.EQ));
            condition.add(new Condition("id.mdevId", new Object[]{"00886004"}, null, Restriction.EQ));
            condition.add(new Condition("id.dst", new Object[]{0}, null, Restriction.EQ));
            condition.add(new Condition("id.yyyymmddhh", new Object[]{"2012081300", "2012081323"}, null, Restriction.BETWEEN));
            
            starttime = System.currentTimeMillis();
            log.info("starttime[" + starttime + "]");
            List list = lpEMDao.getLpEMsByListCondition(condition);
            long endtime = System.currentTimeMillis();
            log.info("1st Get Duration[" + (endtime - starttime) + " ms]");
            
            condition = new HashSet<Condition>();
            condition.add(new Condition("id.mdevType", new Object[]{DeviceType.Meter}, null, Restriction.EQ));
            condition.add(new Condition("id.mdevId", new Object[]{"00886004"}, null, Restriction.EQ));
            condition.add(new Condition("id.dst", new Object[]{0}, null, Restriction.EQ));
            condition.add(new Condition("id.yyyymmddhh", new Object[]{"2012081400", "2012081423"}, null, Restriction.BETWEEN));
            
            starttime = System.currentTimeMillis();
            list = lpEMDao.getLpEMsByListCondition(condition);
            endtime = System.currentTimeMillis();
            
            log.info("2nd Get Duration[" + (endtime - starttime) + " ms]");
            
            txManager.commit(txStatus);
            
        }
        catch (Exception e) {
            if (txStatus != null)
                txManager.rollback(txStatus);
            e.printStackTrace();
        }
    }
}
