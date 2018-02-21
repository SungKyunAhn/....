/**
 * 
 */
package com.aimir.fep.protocol.nip.client.actions;

import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.ModemDao;
import com.aimir.fep.bypass.BypassDevice;
import com.aimir.fep.protocol.nip.CommandNIProxy;
import com.aimir.fep.protocol.nip.client.multisession.MultiSession;
import com.aimir.fep.protocol.nip.frame.GeneralFrame;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.FrameControl_Ack;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.FrameOption_Type;
import com.aimir.fep.protocol.nip.frame.payload.Firmware;
import com.aimir.fep.trap.actions.SP.EV_SP_200_63_0_Action;
import com.aimir.fep.trap.actions.SP.EV_SP_200_65_0_Action;
import com.aimir.fep.trap.actions.SP.EV_SP_200_66_0_Action;
import com.aimir.fep.trap.common.EV_Action.OTA_UPGRADE_RESULT_CODE;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.Device.DeviceType;
import com.aimir.model.device.Modem;
import com.aimir.util.DateTimeUtil;

/**
 * @author simhanger
 *
 */

public class NI_cmdModemOTAStart_Action_SP extends NICommandAction {
	private static Logger logger = LoggerFactory.getLogger(NI_cmdModemOTAStart_Action_SP.class);

	/*
	 * Modem F/W OTA용
	 */
	private boolean needImangeBlockTransferRetry = false;
	private Timer blockTransferRetryTimer = new Timer();
	private NeedImangeBlockTransferRetry blockTransferRetryTask;
	private TargetClass tClass;

	/************** Please don't change ***********************************/
	private final int NEED_IMAGE_BLOCK_TRANSFER_MAX_RETRY_COUNT = 5;
	private final int SEND_IAMGE_RETRY_TIMEOUT = 17;
	/**********************************************************************/

	private String actionTitle = "NI_cmdModemOTAStart_Action_SP";

	public NI_cmdModemOTAStart_Action_SP() {
		actionTitle += "_" + DateTimeUtil.getCurrentDateTimeByFormat(null);
		logger.debug("### Action Title = {}", actionTitle);
	}

	@Override
	public String getActionTitle() {
		return actionTitle;
	}

	@Override
	public Object executeStart(MultiSession session, GeneralFrame generalFrame) throws Exception {

		/*
		 * ACK = ON
		 * Upgrad Data Command의 경우 response command가 따로 없이 ack 로 날아오기때문에 필요.
		 */
		setUseAck(true);

		/*
		 * 1. F/W Image 준비
		 */
		long startTime = System.currentTimeMillis();
		BypassDevice bd = session.getBypassDevice();
		bd.setStartOTATime(startTime);

		/*
		 * 2. Frame 구성 및 첫번째 Request  - Upgrade Start Request
		 */
		GeneralFrame newGFrame = CommandNIProxy.setGeneralFrameOption(FrameOption_Type.Firmware, null);
		newGFrame.setFrame();
		newGFrame.setNetworkType(generalFrame.getNetworkType());
		((Firmware) newGFrame.payload).setTargetType(0); // 0 : Modem F/W upgrade
		((Firmware) newGFrame.payload).setUpgradeCommand(3); // 03 : Upgrade Start Request      
		((Firmware) newGFrame.payload).setUpgradeSequenceNumber(Integer.parseInt(bd.getFwVersion())); // Upgrade Sequence Number => F/W 버전으로 설정 2byte.

		// INSERT START SP-681
		((Firmware) newGFrame.payload).setFwVersion(bd.getOptionalVersion());
		((Firmware) newGFrame.payload).setFwModel(bd.getOptionalModel());
		// INSERT END SP-681		
		
		byte[] generalFrameData = newGFrame.encode(null);

		session.write(generalFrameData);
		logger.debug("### [Upgrade Start Request] Session write => {}", newGFrame.toString());

		CommandActionResult actionResult = generalFrame.getCommandActionResult();
		actionResult.setSuccess(true);
		actionResult.setResultValue("Proceeding...");

		ModemDao modemDao = DataUtil.getBean(ModemDao.class);
		Modem modem = modemDao.get(bd.getModemId());
		tClass = TargetClass.valueOf(modem.getModemType().name());

		return null;
	}

	@Override
	public Object executeTransaction(MultiSession session, GeneralFrame gFrame) throws Exception {
		Firmware firmwareFrame = (Firmware) gFrame.getPayload();
		logger.debug("Firmware Frame = {}", firmwareFrame.toString());
		BypassDevice bd = session.getBypassDevice();

		GeneralFrame newGFrame = CommandNIProxy.setGeneralFrameOption(FrameOption_Type.Firmware, null);
		newGFrame.setFrame();
		newGFrame.setNetworkType(gFrame.getNetworkType());

		switch (firmwareFrame.get_upgradeCommand()) {
		case UpgradeStartResponse:
			int address = firmwareFrame.getAddress();

			if (bd.isTakeOver()) {
				bd.setOffset(address);
			} else {
				bd.setOffset(0);
			}

			bd.setRemainPackateLength(bd.getFw_bin().length - bd.getOffset());
			logger.debug("### [{} ]ModemId={}, MeterId={}, GeneralFrame = {}, TakeOver={},  Address={}", firmwareFrame.get_upgradeCommand(), bd.getModemId(), bd.getMeterId(), newGFrame.toString(), bd.isTakeOver(), address);

			sendImage(session, bd, newGFrame);

			break;
		case UpgradeEndResponse:
			logger.debug("### [{}] ModemId={}, MeterId={}, ResultCode={}", firmwareFrame.get_upgradeCommand(), bd.getModemId(), bd.getMeterId(), firmwareFrame.get_imageCode().name());

			switch (firmwareFrame.get_imageCode()) {
			case NoError:
				/*
				 * Upgrade Image Install Request
				 */
				((Firmware) newGFrame.payload).setTargetType(0); // 0 : Modem F/W upgrade
				((Firmware) newGFrame.payload).setUpgradeCommand(8); // 08 : Image Install Request  
				((Firmware) newGFrame.payload).setImageLength(bd.getFw_bin().length); // Image length
				((Firmware) newGFrame.payload).setCrc(bd.getFwCRC()); // Image CRC
				
				// INSERT START SP-681
				((Firmware) newGFrame.payload).setInstallTime(bd.getOptionalInstallTime());
				// INSERT END SP-681
				
				
				byte[] generalFrameData = newGFrame.encode(null);

				session.write(generalFrameData);
				logger.debug("### [Image Install Request] Session write => [{}][{}]", newGFrame.toString(), Hex.decode(generalFrameData));

				break;
			case CRCFail:
				logger.debug("### [{}] ModemId={}, MeterId={}, Modem OTA Fail~~~!!", firmwareFrame.get_upgradeCommand(), bd.getModemId(), bd.getMeterId());
				logger.debug("### [{}] ModemId={}, MeterId={}, Modem OTA Fail~~~!!", firmwareFrame.get_upgradeCommand(), bd.getModemId(), bd.getMeterId());
				logger.debug("### [{}] ModemId={}, MeterId={}, Modem OTA Fail~~~!!", firmwareFrame.get_upgradeCommand(), bd.getModemId(), bd.getMeterId());

				return new Exception("Upgrade End Response CRC Fail");
			case UnknownError:
				logger.debug("### [{}] ModemId={}, MeterId={}, Modem OTA Fail~~~!!", firmwareFrame.get_upgradeCommand(), bd.getModemId(), bd.getMeterId());
				logger.debug("### [{}] ModemId={}, MeterId={}, Modem OTA Fail~~~!!", firmwareFrame.get_upgradeCommand(), bd.getModemId(), bd.getMeterId());
				logger.debug("### [{}] ModemId={}, MeterId={}, Modem OTA Fail~~~!!", firmwareFrame.get_upgradeCommand(), bd.getModemId(), bd.getMeterId());

				return new Exception("Upgrade End Response Unknown Error");
			default:
				break;
			}
			break;
		case UpgradeImageInstallResponse:
			logger.debug("### [{}] ModemId={}, MeterId={}, ResultCode={}", firmwareFrame.get_upgradeCommand(), bd.getModemId(), bd.getMeterId(), firmwareFrame.get_imageCode().name());

			boolean result = false;
			switch (firmwareFrame.get_imageCode()) {
			case NoError:
				result = true;
				break;
			case CRCFail:
				logger.debug("### [{}] ModemId={}, MeterId={}, Modem OTA Fail~~~!!", firmwareFrame.get_upgradeCommand(), bd.getModemId(), bd.getMeterId());
				logger.debug("### [{}] ModemId={}, MeterId={}, Modem OTA Fail~~~!!", firmwareFrame.get_upgradeCommand(), bd.getModemId(), bd.getMeterId());
				logger.debug("### [{}] ModemId={}, MeterId={}, Modem OTA Fail~~~!!", firmwareFrame.get_upgradeCommand(), bd.getModemId(), bd.getMeterId());
				return new Exception("Upgrade Image Install Response CRC Fail");
			case UnknownError:
				logger.debug("### [{}] ModemId={}, MeterId={}, Modem OTA Fail~~~!!", firmwareFrame.get_upgradeCommand(), bd.getModemId(), bd.getMeterId());
				logger.debug("### [{}] ModemId={}, MeterId={}, Modem OTA Fail~~~!!", firmwareFrame.get_upgradeCommand(), bd.getModemId(), bd.getMeterId());
				logger.debug("### [{}] ModemId={}, MeterId={}, Modem OTA Fail~~~!!", firmwareFrame.get_upgradeCommand(), bd.getModemId(), bd.getMeterId());
				return new Exception("Upgrade Image Install Response Unknown Error");
			default:
				break;
			}

			/*
			 * OTA 종료후 Event 저장
			 */
			long endTime = System.currentTimeMillis();
			String elapseTime = DateTimeUtil.getElapseTimeToString((endTime - bd.getStartOTATime()));

			String openTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
			EV_SP_200_65_0_Action action1 = new EV_SP_200_65_0_Action();
			action1.makeEvent(tClass, bd.getModemId(), tClass, openTime, elapseTime, OTA_UPGRADE_RESULT_CODE.OTAERR_NOERROR, "F/W Version=[" + bd.getFwVersion() + "]", "HES");
			action1.updateOTAHistory(bd.getModemId(), DeviceType.Modem, openTime, OTA_UPGRADE_RESULT_CODE.OTAERR_NOERROR, "F/W Version=[" + bd.getFwVersion() + "]");

			EV_SP_200_66_0_Action action2 = new EV_SP_200_66_0_Action();
			action2.makeEvent(tClass, bd.getModemId(), tClass, openTime, OTA_UPGRADE_RESULT_CODE.OTAERR_NOERROR, null, "HES");
			action2.updateOTAHistory(bd.getModemId(), DeviceType.Modem, openTime, OTA_UPGRADE_RESULT_CODE.OTAERR_NOERROR);

			logger.debug("#### [Upgrade Fininsed] Meter={}, Modem={} F/W Upgrade finished. result = {}!!! ", bd.getMeterId(), bd.getModemId(), result);
			logger.debug("#### [Upgrade Fininsed] Meter={}, Modem={} F/W Upgrade finished. result = {}!!! ", bd.getMeterId(), bd.getModemId(), result);
			logger.debug("#### [Upgrade Fininsed] Meter={}, Modem={} F/W Upgrade finished. result = {}!!! ", bd.getMeterId(), bd.getModemId(), result);

			logger.debug("Call executeStop Start");
		//	notifyToObservers("[NI_cmdModemOTAStart_Action_SP] ModemId=" + bd.getModemId() + ", MeterId=" + bd.getMeterId());
			executeStop(session);
			logger.debug("Call executeStop Stop");
		default:
			break;
		}

		return null;
	}

	private boolean sendImage(MultiSession session, BypassDevice bd, GeneralFrame newGFrame) throws Exception {
		boolean hasRemainData = true;

		byte[] sendPacket = null;
		int remainPackateLength = bd.getRemainPackateLength();
		int offSet = bd.getOffset();

		logger.debug("### SEND IMAGE START - ModemId={}, MeterId={} Offset={}, RemainPacketLength={}/total={} ###", bd.getModemId(), bd.getMeterId(), offSet, remainPackateLength, bd.getFw_bin().length);

		if (0 < remainPackateLength) {
			if (bd.getPacket_size() < remainPackateLength) {
				sendPacket = new byte[bd.getPacket_size()];
			} else {
				sendPacket = new byte[remainPackateLength];
			}
			System.arraycopy(bd.getFw_bin(), offSet, sendPacket, 0, sendPacket.length);

			// Firmware Upgrad Frame Setting            
			newGFrame.setFrameControl_Ack(FrameControl_Ack.Ack);
			newGFrame.setFrameSequence(bd.getNextFrameSequence());

			((Firmware) newGFrame.payload).setTargetType(0); // 0 : Modem F/W upgrade
			((Firmware) newGFrame.payload).setUpgradeCommand(5); // 05 : UpgradeData    
			((Firmware) newGFrame.payload).setAddress(offSet); // 전송하고자 하는 Image의 상대적인 주소
			((Firmware) newGFrame.payload).setLength(sendPacket.length); // 전송할 data의 길이
			((Firmware) newGFrame.payload).setData(sendPacket); // 전송할 data

			byte[] generalFrameData = newGFrame.encode(null);

			logger.debug("SEND_DATA_PACKET = {}", Hex.decode(sendPacket));

			if (generalFrameData != null && 0 < generalFrameData.length) {
				session.write(generalFrameData);
				logger.debug("### [Upgrade Data] Image Send Session write => {}", newGFrame.toString());
			} else {
				needImangeBlockTransferRetry = false;
				throw new Exception("[Upgrade Data] Image Send Encoding Error");
			}

			remainPackateLength -= sendPacket.length;
			if (remainPackateLength <= 0) {
				remainPackateLength = 0;
			}
			bd.setRemainPackateLength(remainPackateLength);

			bd.setOffset(offSet += sendPacket.length);

			double tempa = bd.getFw_bin().length;
			double tempb = offSet;
			logger.info("[Upgrade Data][ModemId={}, MeterId={}] Sended Packet Size={}, Offset={}, RemainPacket Size={}/{}, ProgressRate={}%"
					, bd.getModemId(), bd.getMeterId(), sendPacket.length, offSet, remainPackateLength, tempa, String.format("%.2f", tempb / tempa * 100));

			/*
			 *  재전송해야할 필요가 있는지 체크하는 타이머
			 *  SEND_IAMGE_RETRY_TIMEOUT 초뒤에 실행, SEND_IAMGE_RETRY_TIMEOUT 초 간격으로 NEED_IMAGE_BLOCK_TRANSFER_MAX_RETRY_COUNT 만큼 재실행
			 */
			needImangeBlockTransferRetry = true;
			blockTransferRetryTask = new NeedImangeBlockTransferRetry(session, generalFrameData, NEED_IMAGE_BLOCK_TRANSFER_MAX_RETRY_COUNT);
			blockTransferRetryTimer.scheduleAtFixedRate(blockTransferRetryTask, SEND_IAMGE_RETRY_TIMEOUT * 1000, SEND_IAMGE_RETRY_TIMEOUT * 1000);
		} else {
			hasRemainData = false;

			// 다 보내면 타이머 해지
			stopTransferImageTimer();
			logger.debug("## Timer 다보낸뒤 해지~! ==> needImangeBlockTransferRetry={}", needImangeBlockTransferRetry);

			/*
			 * OTA Download Event save.
			 */
			String openTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
			EV_SP_200_63_0_Action action2 = new EV_SP_200_63_0_Action();
			action2.makeEvent(
					//TargetClass.Modem
					tClass, bd.getModemId()
					//, TargetClass.Modem
					, tClass, openTime, "HES");
			action2.updateOTAHistory(bd.getModemId(), DeviceType.Modem, openTime);
		}

		logger.debug("### SEND IMAGE - ok ###");
		return hasRemainData;
	}

	@Override
	public void executeAck(MultiSession session, GeneralFrame generalFrame) throws Exception {
		BypassDevice bd = session.getBypassDevice();
		logger.debug("### [cmdModemOTA Received ACK] ModemId={}, MeterId={}, GeneralFrame = {}", bd.getModemId(), bd.getMeterId(), generalFrame.toString());
		
		/*
		 * 보냈던 sequence number가 아니면 동일한 블럭을 재전송한다.
		 */
		int receivedSeq = generalFrame.getFrameSequence();
		int sendedSeq = bd.getFrameSequence();
		
		/*
		 * receivedSeq == 0 
		 *  : frameSequence를 사용하지 않는 버전일경우 처리
		 */
		if(receivedSeq == 0 || receivedSeq == sendedSeq){
			needImangeBlockTransferRetry = false;
			blockTransferRetryTask.cancel();
			int temp = blockTransferRetryTimer.purge();
			logger.debug("##퍼지 됬음.  ==>> {}", temp);

			GeneralFrame newGFrame = CommandNIProxy.setGeneralFrameOption(FrameOption_Type.Firmware, null);
			newGFrame.setFrame();
			newGFrame.setNetworkType(generalFrame.getNetworkType());

			try {
				if (!sendImage(session, bd, newGFrame)) {

					/*
					 * Upgrade End Request
					 */
					newGFrame.setFrameControl_Ack(FrameControl_Ack.None);
					//generalFrame.setFrame();  필요시 주석풀어서 사용
					((Firmware) newGFrame.payload).setTargetType(0); // 0 : Modem F/W upgrade
					((Firmware) newGFrame.payload).setUpgradeCommand(6); // 06 : Upgrade End Request    
					((Firmware) newGFrame.payload).setImageLength(bd.getFw_bin().length); // Image length
					((Firmware) newGFrame.payload).setCrc(bd.getFwCRC()); // Image CRC
					byte[] generalFrameData = newGFrame.encode(null);

					session.write(generalFrameData);

					logger.debug("### [Upgrade End Request] Session write => [{}][{}]", newGFrame.toString(), Hex.decode(generalFrameData));
				}
			} catch (Exception e) {
				logger.error("Send Image Error - " + e, e);

				stopTransferImageTimer();
				logger.debug("## Timer 실패시 해지~! ==> needImangeBlockTransferRetry={}", needImangeBlockTransferRetry);

				throw new Exception("Send Image Error - ", e);
			}			
		}else{
			logger.warn("Invalid frame sequence received. SendedFrameSeq=[{}], ReceivedFrameSeq=[{}]", sendedSeq, receivedSeq);
		}
	}

	@Override
	public void executeStop(MultiSession session) throws Exception {
		logger.debug("call executeStop1 - Modem={}, Meter={}", session.getBypassDevice().getModemId(), session.getBypassDevice().getMeterId());
		deleteMultiSession(session);
		logger.debug("call executeStop2 - Modem={}, Meter={}", session.getBypassDevice().getModemId(), session.getBypassDevice().getMeterId());
	}

	/**
	 * Timer, Timer Task cancel
	 */
	private void stopTransferImageTimer() {
		needImangeBlockTransferRetry = false;
		blockTransferRetryTask.cancel();
		blockTransferRetryTimer.cancel();
		blockTransferRetryTimer = null;
		logger.debug("## Timer Task Stop.");
	}

	/**
	 * 이미지전송을 반복 실행하는 TimerTask
	 * 
	 * @author simhanger
	 *
	 */
	protected class NeedImangeBlockTransferRetry extends TimerTask {
		private MultiSession session;
		private byte[] req;
		private int maxRetryCount;
		private int retryCount;

		public NeedImangeBlockTransferRetry(MultiSession session, byte[] req, int maxRetryCount) {
			this.session = session;
			this.req = req;
			this.maxRetryCount = maxRetryCount;
		}

		@Override
		public void run() {
			if (needImangeBlockTransferRetry == true && this.retryCount < this.maxRetryCount) {
				this.session.write(req);
				logger.debug("### RETRY !!!! [Upgrade Data][Meter={}, Modem={}] Image Send Retry={} Session write => {}", session.getBypassDevice().getMeterId(), session.getBypassDevice().getModemId(), retryCount + 1, Hex.decode(req));
				this.retryCount++;
			} else {
				this.cancel();

				/* OTA END&RESULT Event */
				double tempa = session.getBypassDevice().getFw_bin().length;
				double tempb = session.getBypassDevice().getOffset();
				String progressRate = String.format("%.2f", tempb / tempa * 100);

				String openTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
				EV_SP_200_65_0_Action action2 = new EV_SP_200_65_0_Action();
				action2.makeEvent(tClass, session.getBypassDevice().getModemId(), tClass, openTime, "0", OTA_UPGRADE_RESULT_CODE.OTAERR_NI_TRN_FAIL, "Progress Rate: " + progressRate + "%, Retry count=" + retryCount, "HES");
				action2.updateOTAHistory(session.getBypassDevice().getModemId(), DeviceType.Modem, openTime, OTA_UPGRADE_RESULT_CODE.OTAERR_NI_TRN_FAIL, "Progress Rate: " + progressRate + "%, Retry count=" + retryCount);

				EV_SP_200_66_0_Action action3 = new EV_SP_200_66_0_Action();
				action3.makeEvent(tClass, session.getBypassDevice().getModemId(), tClass, openTime, OTA_UPGRADE_RESULT_CODE.OTAERR_NI_TRN_FAIL, null, "HES");
				action3.updateOTAHistory(session.getBypassDevice().getModemId(), DeviceType.Modem, openTime, OTA_UPGRADE_RESULT_CODE.OTAERR_NI_TRN_FAIL);

				logger.debug("NeedImangeBlockTransferRetry cancle!! OTA Faill");
				logger.debug("NeedImangeBlockTransferRetry cancle!! OTA Faill");
				logger.debug("NeedImangeBlockTransferRetry cancle!! OTA Faill");
			}
		}
	}

	@Override
	public void executeResponse(MultiSession session, GeneralFrame generalFrame) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
