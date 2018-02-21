package com.aimir.fep.bypass;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.DefaultCmdResult;
import com.aimir.constants.CommonConstants.McuType;
import com.aimir.constants.CommonConstants.MeterProgramKind;
import com.aimir.constants.CommonConstants.TR_OPTION;
import com.aimir.dao.device.AsyncCommandLogDao;
import com.aimir.dao.device.AsyncCommandParamDao;
import com.aimir.dao.system.MeterProgramLogDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.protocol.fmp.client.sms.SMSClient;
import com.aimir.fep.protocol.fmp.common.Target;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.mrp.command.frame.sms.RequestFrame;
import com.aimir.fep.util.CmdUtil;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.AsyncCommandLog;
import com.aimir.model.device.AsyncCommandParam;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.system.MeterProgram;
import com.aimir.model.system.MeterProgramLog;
import com.aimir.util.TimeUtil;

/**
 * <p>Bypass 비즈니스 로직 추상화 클래스.</p>
 * 각 미터의 비즈니스 로직은 반드시 하위 패키지명 "com.aimir.fep.bypass.actions" 에 있는 ("디바이스모델명Action.java") 파일을 참조하도록 되어있다.
 * @author kskim
 */
public abstract class Bypass implements java.io.Serializable  {

	public static final String CLOSE="1";
	public static final String OPEN="0";
	
	protected static Log log = LogFactory.getLog(Bypass.class); 

	private static final long serialVersionUID = -8112166506359868597L;
	
	protected Meter meter;
	protected Modem modem;
	
	protected List<MeterProgramLog> mpLogs;
	protected List<Map<String, List<SMIValue>>> command;
	protected IoSession session = null;	
	protected Thread t=null;	
	
	/**
	 * Bypass 실행 명령을 받는 외부 인터페이스
	 * @param srcId
	 * @param session
	 * @throws Exception
	 */
	public void execute(IoSession session, String modemId, String meterId){
		log.info(String.format("Excute Modem[%s], Meter[%s]",modemId,meterId));
		log.info(String.format("Target[%s]", session.getRemoteAddress().toString()));
		
		this.session = session;
		
		SessionFactory sessionFactory = DataUtil.getBean(SessionFactory.class);
		
		//중복되지 않고 처리되지 않은 meterProgramlog list 읽어온다.
		///////////////////////////////////////////////////////////////////////////////////////
		
		mpLogs = new ArrayList<MeterProgramLog>();
		
		Session dbsession = sessionFactory.openSession();
		Query query = null;

		List<MeterProgramLog> logs = new ArrayList<MeterProgramLog>();
		for(MeterProgramKind k : MeterProgramKind.values()){	
			String q = "FROM MeterProgramLog mpl WHERE mpl.meter.id=:METER AND mpl.meterProgram.kind=:KIND ORDER BY mpl.id DESC";
			query = dbsession.createQuery(q);
			query.setParameter("KIND", k);
			query.setInteger("METER", meter.getId());
			query.setMaxResults(1);
			List list = query.list();
			
			if(list!=null && list.size() !=0){
				MeterProgramLog mpl = (MeterProgramLog) list.get(0);
				
				//실행 한적이 없거나 실패한 경우만 리스트로 추가한다.
				if(mpl.getResult()==null || 
						mpl.getResult() == DefaultCmdResult.FAILURE || 
						mpl.getResult() == DefaultCmdResult.TIMEOUT){
					mpLogs.addAll(list);
				}
			}
		}
		
		log.debug("Meter Program Count : " + mpLogs.size());
		
		dbsession.clear();
		dbsession.close();
		///////////////////////////////////////////////////////////////////////////////////////

		// 등록된 명령이 없을경우
		if(mpLogs==null || mpLogs.size()==0){
			log.info("Cannot be found bypass-command from MeterProgramLog. modem serial:"+modemId);
			session.closeNow();
			return;
		}
		
		log.info("Registered commands");
		for (MeterProgramLog mpLog : mpLogs) {
			log.info("Meter program log id : " + mpLog.getId());
		}
	}

	public Meter getMeter() {
		return meter;
	}

	public Modem getModem() {
		return modem;
	}

	public List<MeterProgramLog> getMpLogs() {
		return mpLogs;
	}

	/**
	 * bypass 명령을 기록  하여 요청이 오면 처리한다. IF4 1.2
	 * @param meterPro
	 */
	public void register(MeterProgram meterPro){
		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
			txStatus = txManager.getTransaction(null);

			MeterProgramLogDao mplDao = DataUtil.getBean(MeterProgramLogDao.class);
			MeterProgramLog mpl = new MeterProgramLog();
			mpl.setAppliedDate(TimeUtil.getCurrentTime());
			mpl.setMeter(meter);
			mpl.setMeterProgram(meterPro);
			mplDao.saveOrUpdate(mpl);
			txManager.commit(txStatus);
		} catch (Exception e) {
			log.error(e);
		}
	}
	
	/**
	 * bypass 명령을 기록  하여 요청이 오면 처리한다.
	 * @param meterPro
	 */
	public void register(Map<String, String[][]> command){
		
		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
			txStatus = txManager.getTransaction(null);

			Set<String> keys = command.keySet();
			if(keys == null && keys.size() == 0){
				return;
			}			
			String commandName = keys.iterator().next();
			String[][] args = command.get(commandName);
			
			AsyncCommandLogDao asyncCommandLogDao = DataUtil.getBean(AsyncCommandLogDao.class);
			AsyncCommandParamDao asyncCommandParamDao = DataUtil.getBean(AsyncCommandParamDao.class);	
			
	        long trId = 0;
    		Long result = asyncCommandLogDao.getMaxTrId(modem.getDeviceSerial());
    		if(result != null) {
    			trId = result.intValue() + 1;
    		}		
			
			AsyncCommandLog asyncCommandLog = new AsyncCommandLog();
			asyncCommandLog.setTrId(trId);
			asyncCommandLog.setMcuId(modem.getDeviceSerial());
			asyncCommandLog.setDeviceType(McuType.MMIU.name());
			asyncCommandLog.setDeviceId(modem.getDeviceSerial());
			asyncCommandLog.setCommand(commandName);
			asyncCommandLog.setTrOption(TR_OPTION.ASYNC_OPT_RETURN_DATA_SAVE.getCode());
			asyncCommandLog.setDay(0);
			asyncCommandLog.setInitNice(0);
			asyncCommandLog.setInitTry(3);
			asyncCommandLog.setRequestTime(TimeUtil.getCurrentTime());
			asyncCommandLog.setState(0x01);
			//asyncCommandLog.setOperator(operator);

			asyncCommandLogDao.add(asyncCommandLog);
            
            if(args !=  null) {
            	int argLength = args.length;
                List<AsyncCommandParam> params = new ArrayList<AsyncCommandParam>();
                for (int i = 0; i < argLength; i++) {
                    AsyncCommandParam param = new AsyncCommandParam();
                    
                    param.setTrId(trId);
                    param.setMcuId(modem.getDeviceSerial());
                    param.setNum(i);
                    param.setParamType(args[i][0]);
                    param.setParamValue(args[i][1]);
                    
                    params.add(param);
                    asyncCommandParamDao.add(param);
                }       
            }

			txManager.commit(txStatus);
		} catch (Exception e) {
			log.error(e);
		}
	}

	public void setMeter(Meter meter) {
		this.meter = meter;
	}
	
	public void setModem(Modem modem) {
		this.modem = modem;
	}
	
	public void setMpLogs(List<MeterProgramLog> mpLogs) {
		this.mpLogs = mpLogs;
	}

	public IoSession getSession() {
		return session;
	}

	public void setSession(IoSession session) {
		this.session = session;
	}
	
	/**
	 * Bypass Close SMS 명령을 보내는 기능. 
	 */
	protected void sendSMS_BypassClose() {
		try {
			// bypass close SMS 명령 전송
			CommandGW cgb = (CommandGW) DataUtil.getBean("commandGW");

			Target target = CmdUtil.getTarget(modem);

			log.info("'bypass close' send a SMS message");
			// bypass 종료 sms전송 기능 .
			cgb.cmdSendSMS(target, RequestFrame.CMD_BYPASS, String
					.valueOf(SMSClient.getSEQ()), RequestFrame.BG,
					Bypass.CLOSE);
		} catch (Exception e) {
			log.error(e);
		}
	}
}
