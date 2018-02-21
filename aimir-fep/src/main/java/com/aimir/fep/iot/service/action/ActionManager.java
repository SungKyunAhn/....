package com.aimir.fep.iot.service.action;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.aimir.fep.iot.utils.CommonCode.MGMT_DEFINITION;
import com.aimir.fep.iot.utils.CommonCode.RESOURCE_TYPE;
import com.aimir.fep.util.DataUtil;

/**
 * 2017-10-11
 * @author Han Seung Woo
 * Service Bean을 관리하기 위한 클래스
 *
 */
public class ActionManager {

	private final int MAX_QUEUE = 15;
	
	private volatile static ActionManager instance = null;
	private BlockingQueue<OperationUtilAction> operationUtilQueue = null;
	
	//main resource
	private BlockingQueue<RemoteCSEAction> remoteCSEQueue = null;
	private BlockingQueue<ContainerAction> containerQueue = null;
	private BlockingQueue<ContentInstanceAction> contentInstanceQueue = null;
	private BlockingQueue<CSEBaseAction> cseBaseQueue = null;
	private BlockingQueue<MgmtCmdAction> mgmtCmdQueue = null;
	
	//sub resource
	private BlockingQueue<FirmwareAction> firmwareQueue = null;
	private BlockingQueue<DeviceInfoAction> deviceInfoQueue = null;
	
	public static ActionManager getInstance() {
		if(instance == null) {
			synchronized (ActionManager.class) {
				if(instance == null) {
					instance = new ActionManager();
				}
			}
		}
		
		return instance;
	}
	
	public ActionManager() {
		operationUtilQueue = new ArrayBlockingQueue<>(MAX_QUEUE);
		
		//main resource
		remoteCSEQueue = new ArrayBlockingQueue<>(MAX_QUEUE);
		containerQueue = new ArrayBlockingQueue<>(MAX_QUEUE);
		contentInstanceQueue = new ArrayBlockingQueue<>(MAX_QUEUE);
		cseBaseQueue = new ArrayBlockingQueue<>(MAX_QUEUE);
		mgmtCmdQueue = new ArrayBlockingQueue<>(MAX_QUEUE);
		
		//sub resource
		firmwareQueue = new ArrayBlockingQueue<>(MAX_QUEUE);
		deviceInfoQueue = new ArrayBlockingQueue<>(MAX_QUEUE);
				
		init();
	}
	
	public void init() {
		for(int i=0; i<MAX_QUEUE; i++) {
			pushOperationBean();
			//main resource
			pushBean(RESOURCE_TYPE.REMOTE_CSE);
			pushBean(RESOURCE_TYPE.CONTAINER);
			pushBean(RESOURCE_TYPE.CONTENT_INSTANCE);
			pushBean(RESOURCE_TYPE.CSE_BASE);
			pushBean(RESOURCE_TYPE.MGMT_CMD);
			
			//sub resource
			pushBean(RESOURCE_TYPE.MGMT_OBJ, MGMT_DEFINITION.FIRMWARE);
			pushBean(RESOURCE_TYPE.MGMT_OBJ, MGMT_DEFINITION.DEVICE_INFO);
		}
	}
	
	private void pushOperationBean() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					operationUtilQueue.add(DataUtil.getBean(OperationUtilAction.class));
				}catch(Exception e) {}
			}
		}).start();
	}
	
	private void pushBean(final RESOURCE_TYPE resourceType, MGMT_DEFINITION mgmtDefinition) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					switch(mgmtDefinition) {
					case FIRMWARE:
						firmwareQueue.add(DataUtil.getBean(FirmwareAction.class));
						break;			
					case DEVICE_INFO:
						deviceInfoQueue.add(DataUtil.getBean(DeviceInfoAction.class));
						break;
					default:
						break;
					}
				}catch(Exception e) {}
			}
		}).start();
	}
	
	private void pushBean(final RESOURCE_TYPE resourceType) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					switch(resourceType) {
					case CSE_BASE:
						cseBaseQueue.add(DataUtil.getBean(CSEBaseAction.class));
						break;
					case AE:
						break;
					case GROUP:
						break;
					case LOCATION_POLICY:
						break;
					case REMOTE_CSE:
						remoteCSEQueue.add(DataUtil.getBean(RemoteCSEAction.class));
						break;
					case CONTAINER:
						containerQueue.add(DataUtil.getBean(ContainerAction.class));
						break;
					case CONTENT_INSTANCE:
						contentInstanceQueue.add(DataUtil.getBean(ContentInstanceAction.class)); 
						break;
					case MGMT_CMD:
						mgmtCmdQueue.add(DataUtil.getBean(MgmtCmdAction.class));
						break;
					default:
						break;
					}
				}catch(Exception e) {}
			}
		}).start();
	}
	
	public OperationUtilAction getOperationBean() {
		pushOperationBean();
		return operationUtilQueue.poll();
	}
	
	public ActionService getBean(RESOURCE_TYPE resourceType, MGMT_DEFINITION mgmtDefinition) {
		ActionService actionService = null;
		
		try {
			switch(mgmtDefinition) {
			case FIRMWARE:
				actionService = firmwareQueue.poll();
				pushBean(resourceType, mgmtDefinition);
				break;			
			case DEVICE_INFO:
				actionService = deviceInfoQueue.poll();
				pushBean(resourceType, mgmtDefinition);
				break;
			default:
				actionService = null;
			}
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return actionService;
	}
	
	public ActionService getBean(RESOURCE_TYPE resourceType) {
		ActionService actionService = null;
		
		try {
			switch(resourceType) {
			case CSE_BASE:
				actionService = cseBaseQueue.poll();
				pushBean(resourceType);
				break;
			case AE:
				actionService = null;
				break;
			case GROUP:
				actionService = null;
				break;
			case LOCATION_POLICY:
				actionService = null;
				break;
			case REMOTE_CSE:
				actionService = remoteCSEQueue.poll();
				pushBean(resourceType); 
				break;
			case CONTAINER:
				actionService = containerQueue.poll();
				pushBean(resourceType);
				break;
			case CONTENT_INSTANCE:
				actionService = contentInstanceQueue.poll();
				pushBean(resourceType);
				break;
			case MGMT_CMD:
				actionService = mgmtCmdQueue.poll();
				pushBean(resourceType);
				break;
			default:
				actionService = null;
			}
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return actionService;
	}
	
}
