package com.aimir.fep.protocol.fmp.frame.amu;

import java.io.ByteArrayOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.frame.AMUDataFrameStatus;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Hex;

/**
 * Link Frame Data
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 2. 19. 오후 4:02:51$
 */
public class LinkData extends AMUFramePayLoad{

	private static final long serialVersionUID = 5045124511394860283L;
	
	private Log log = LogFactory.getLog(LinkData.class);
	
	LinkFrameControl  frameControl ;
	
	// Request
	byte linkResTime;	// Link Response Time
	byte[] linkTimeOut;	// Link Time Out
	byte[] parentMIUAddr;	// Parent MIU Addr
	byte[] authCode;	// Authentication Code
	
	/**
	 * constructor
	 */
	public LinkData(){
	}
	
	/**
	 * constructor
	 * @param rawData
	 * @throws Exception
	 */
	public LinkData(byte[] framePayLoad)throws Exception{

		try{
			decode(framePayLoad);
		}catch(Exception e){
			throw e;
		}
	}
	
	/**
	 * get Link Frame Control
	 * @return
	 */
	public LinkFrameControl getFrameControl() {
		return frameControl;
	}

	/**
	 * set Link Frame Control
	 * @param frameControl
	 */
	public void setFrameControl(LinkFrameControl frameControl) {
		this.frameControl = frameControl;
	}

	public byte getLinkResTime() {
		return linkResTime;
	}

	public void setLinkResTime(byte linkResTime) {
		this.linkResTime = linkResTime;
	}

	public byte[] getLinkTimeOut() {
		return linkTimeOut;
	}

	public void setLinkTimeOut(byte[] linkTimeOut) {
		this.linkTimeOut = linkTimeOut;
	}

	public byte[] getParentMIUAddr() {
		return parentMIUAddr;
	}

	public void setParentMIUAddr(byte[] parentMIUAddr) {
		this.parentMIUAddr = parentMIUAddr;
	}

	public byte[] getAuthCode() {
		return authCode;
	}

	public void setAuthCode(byte[] authCode) {
		this.authCode = authCode;
	}

	/**
	 * constructor
	 * When make Response
	 * 
	 * @param isRequest
	 * @param isOpened
	 * @param isResRequest
	 * @param miuAddrType
	 * @param resStatus
	 */
	public LinkData(boolean isRequest, boolean isOpened, 
				boolean isResRequest, byte[] resStatus) 
	throws Exception{
		try{
			frameControl = new LinkFrameControl();
			frameControl.setRequest(isRequest);
			frameControl.setOpened(isOpened);
			frameControl.setResRequest(isResRequest);
			
			this.control	= frameControl.encode();
	
			this.payLoadData 	= resStatus;
		}catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * constructor
	 * When make Request
	 * 
	 * @param isRequest
	 * @param isOpened
	 * @param isResRequest
	 * @param isSetLinkTimeOut
	 * @param miuAddrType
	 * @param resTime
	 * @param timeOut
	 * @param mcuID
	 * @param authKey
	 */
	public LinkData(boolean isRequest, boolean isOpened, boolean isResRequest,
				boolean isSetLinkTimeOut, byte miuAddrType ,
				byte resTime , byte[] timeOut , 
				byte[] parentMIUAddr , byte[] authCode) throws Exception{
		try{
			frameControl = new LinkFrameControl();
			frameControl.setRequest(isRequest);
			frameControl.setOpened(isOpened);
			frameControl.setResRequest(isResRequest);
			frameControl.setLinkTimeOut(isSetLinkTimeOut);
			frameControl.setMiuAddrType(miuAddrType);
			
			this.control		= frameControl.encode();
			this.linkResTime 	= resTime;
			this.linkTimeOut 	= timeOut;
			this.parentMIUAddr	= parentMIUAddr;
			this.authCode 		= authCode;
			
		}catch(Exception e){
			throw e;
		}
	}

	/**
	 * decode 
	 * 1. Command Frame 실행시 request를 보낸 후  response를 받을때
	 * 2 .검침 데이터가 올라 오기전에 request 포맷을 받는다.
	 * @param payLoadData
	 * @throws Exception
	 */
	public void decode(byte[] framePayLoad)throws Exception{
		
		try{
			int pos =0;
			
			this.control = framePayLoad[pos];
			pos += AMUFramePayLoadConstants.FormatLength.FRAME_CONTROL;
			
			// Link Frame Control
			this.frameControl = LinkFrameControl.decode(this.control);
			
			log.debug("is Request : " + this.frameControl.isRequest());
			// 검침 데이터가 오기전에 Request를  수신
			if(this.frameControl.isRequest()){
				log.debug("Link Data Request Received");
				
				this.authCode 	= DataFormat.select(framePayLoad, pos , AMUFramePayLoadConstants.FormatLength.LINK_AUTHENTICATION_CODE);
				log.debug("authCode : " + Hex.decode(authCode));
				pos += AMUFramePayLoadConstants.FormatLength.LINK_AUTHENTICATION_CODE;
				
				this.linkResTime = framePayLoad[pos];
				log.debug("LinkResTIme : " + Hex.decode(new byte[]{this.linkResTime}));
				pos += AMUFramePayLoadConstants.FormatLength.LINK_RESPONSE_TIME;
								
				this.linkTimeOut = DataFormat.select(framePayLoad, pos, AMUFramePayLoadConstants.FormatLength.LINK_TIME_OUT);
				pos += AMUFramePayLoadConstants.FormatLength.LINK_TIME_OUT;
				log.debug("linkTimeOut : " + Hex.decode(linkTimeOut));
					
				this.parentMIUAddr	= DataFormat.select(framePayLoad, pos, AMUFramePayLoadConstants.FormatLength.LINK_PARENT_MIU_ADDR);
				log.debug("Parnet MIU ADDR : " + Hex.decode(parentMIUAddr));
			}
			
			//  Command 명령후을 보낸 후  Response을 수신
			else{
				log.debug("Link Data Response Received");
				byte[] statCode = DataFormat.select(framePayLoad, pos,AMUFramePayLoadConstants.FormatLength.LINK_RES_STATUS);
				this.frameStatus = new AMUDataFrameStatus(statCode);
			}
			
		}catch(Exception e){
			log.error("Link Frame decode failed : ", e);
			throw e;
		}
	}
	
	/**
	 * encode
	 * 
	 * @return  byte[]
	 * @throws Exception
	 */
	public byte[] encode() throws Exception{
		
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		
		try{
			
			// When Make Request
			if(isRequest(this.control)){
				bao.write(new byte[]{this.control} 		, 0 , AMUFramePayLoadConstants.FormatLength.FRAME_CONTROL);
				bao.write(this.authCode 				, 0 , AMUFramePayLoadConstants.FormatLength.LINK_AUTHENTICATION_CODE);
				bao.write(new byte[]{this.linkResTime} 	, 0 , AMUFramePayLoadConstants.FormatLength.LINK_RESPONSE_TIME);
				bao.write(this.linkTimeOut 				, 0 , AMUFramePayLoadConstants.FormatLength.LINK_TIME_OUT);
				bao.write(this.parentMIUAddr 			, 0 , AMUFramePayLoadConstants.FormatLength.LINK_PARENT_MIU_ADDR);
			}
			// When Make Response
			else{				
				bao.write(new byte[]{this.control} 		, 0 , AMUFramePayLoadConstants.FormatLength.FRAME_CONTROL);
				bao.write(this.payLoadData 				, 0 , AMUFramePayLoadConstants.FormatLength.LINK_RES_STATUS);
			}
		}catch(Exception e){
			log.error("Link Frame encode failed : ", e);
			throw e;
		}
		return bao.toByteArray();
	}
	
}


