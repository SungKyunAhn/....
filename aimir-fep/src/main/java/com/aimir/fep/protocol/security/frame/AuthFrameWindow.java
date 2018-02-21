package com.aimir.fep.protocol.security.frame;


import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;

import com.aimir.fep.protocol.fmp.frame.GeneralDataConstants;
import com.aimir.fep.protocol.fmp.frame.GeneralDataFrame;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FrameUtil;

/**
 * Multi Frame Window
 * 
 * @author 
 * @version
 */

public class AuthFrameWindow
{
    private Log log = LogFactory.getLog(AuthFrameWindow.class);

    private ArrayList<Object> framelist = new ArrayList<Object>();
    private Object[] window = 
        new Object[AuthFrameConstants.FRAME_MAX_SEQ+1];

    private int sequence = 0;
    private int frameCnt = 0;
    private int endSeqNumber = -1;
//    private int frameType = 0;
    private FrameType frameType ;
    private short version = 0;
    private byte[] deviceSerial = null;
    private boolean isReceivedEndFrame = false;
    private boolean isFrameGenerated = false;
//    private boolean isReceivedWindow = false;
 //   private boolean isMultiFrame = false;

    /**
     * constructor
     */
    public AuthFrameWindow()
    {
        framelist.clear();
    }

    /**
     * put received frame stream to window 
     *
     * @param buff <code>ByteBuffer</code> received bytebuffer
     */
    public  void put(IoBuffer buff) throws Exception
    {
        byte frameOption = 0x00;
//        log.debug("Buffer pos=" + buff.position()+ ",limit=" + buff.limit());

        // get Start flag
        byte startFlag[] = new byte[AuthFrameConstants.START_FLAG_LEN];
        buff.get(startFlag, 0, startFlag.length);
        
        // Frame Option 
        frameOption = buff.get();
        log.debug("frameOption = " + frameOption);
//        log.debug("Buffer pos=" + buff.position()+ ",limit=" + buff.limit());   
        
        byte pending = AuthFrameUtil.getOptionPending(frameOption);
        log.debug("pending = " + pending);
//        log.debug("Buffer pos=" + buff.position()+ ",limit=" + buff.limit());
        
        int  frameCount =  AuthFrameUtil.getOptionFrameCount(frameOption);
        log.debug("frameCount = " + frameCount);
//        log.debug("Buffer pos=" + buff.position()+ ",limit=" + buff.limit());  
        
       
        byte ftypeByte =  AuthFrameUtil.getOptionFrametype(frameOption);
        FrameType tmpframeType = frameType = FrameType.getByCode(ftypeByte);
        
        
        // Version
        byte[] version = new byte[AuthFrameConstants.VERSION_LEN];
        buff.get(version, 0, version.length);
        this.version = (short)DataUtil.getIntTo2Byte(version);
        
        // Sequence Number
        byte sequenceNumber = buff.get();
        this.sequence = DataUtil.getIntToByte(sequenceNumber);
        
        if(pending == AuthGeneralFrame.Pending.FALSE.getCode() ){
        	isReceivedEndFrame = true;
        	endSeqNumber = this.sequence;
        }
        
        // Device serial
        byte[] deviceSerial = new byte[AuthFrameConstants.DEVICE_SERIAL_LEN];
        buff.get(deviceSerial, 0, deviceSerial.length);
        this.deviceSerial = deviceSerial;

        // check FrameType 
        if ( this.frameType == null ){
        	this.frameType = tmpframeType;
        }
        else {
        	if ( this.frameType != tmpframeType) {
        		resetAll();
        		log.error("Invalid FrameType(SeqNo=" + sequenceNumber + ") : " + tmpframeType.name() + "(previous FrameType = " + this.frameType.name());
        		throw new Exception("Invalid FrameType");
        	}
        }
        log.debug("frameType = " + frameType);
        
        buff.rewind();
        byte[] frame = new byte[buff.limit()];
        buff.get(frame,0,frame.length); 
        //window[this.sequence%GeneralDataConstants.FRAME_WINSIZE] = frame;
        if ( window[this.sequence%window.length] != null )
        	frameCnt++;
        window[this.sequence%window.length] = frame;
    }

    /**
     * reset window
     */
    private void resetWindow()
    {
        //for(int i = 0 ; i < GeneralDataConstants.FRAME_WINSIZE ; i++) 
        for(int i = 0 ; i < window.length; i++) 
        {
            window[i] =null;
        }
        frameCnt = 0;
    }

    /**
     * put window to frame buffer
     */
    private void pushWindow()
    { 
        for(int i = 0 ; i < window.length; i++) 
        { 
            if(window[i] != null)
                framelist.add(window[i]); 
        } 
        resetWindow();
    }

    /**
     * check window and return invalid frame sequence numbers
     *
     * @param start <code>int</code> start sequence number
     * @param end <code>int</code> end sequence number
     * @return sequence numbers <code>byte[]</code>
     */
    public byte[] checkWindow()
    {
//        if(FRAME_WINSIZE == 0)
//        {
//            Object[] tmpwindow = new Object[end-start+1];
//            FRAME_WINSIZE = tmpwindow.length;
//            for(int i = 0 ; i < tmpwindow.length ; i++)
//            {
//                tmpwindow[i] = window[i];
//            }
//            window = null;
//            window = tmpwindow;
//        }
//        //int startseq = start%GeneralDataConstants.FRAME_WINSIZE;
//        //int endseq = end%GeneralDataConstants.FRAME_WINSIZE;
//        int startseq = start%FRAME_WINSIZE;
//        int endseq = end%FRAME_WINSIZE;
//
//        log.info("FRAME_WINSIZE[" + FRAME_WINSIZE + "] startseq[" + startseq + 
//                "] endseq[" + endseq + "]");

    	ArrayList<String> al = new ArrayList<String>();
        for(int i = 0 ; i <= endSeqNumber ; i++)
        {
            if(window[i] == null)
                al.add(new String(""+i));
        } 
        if(al.size() == 0)
        {
            pushWindow();
            return  new byte[0];
        }

        String[] strarr = (String[])al.toArray(new String[0]);
        byte[] naklist = new byte[strarr.length];
        for(int i = 0 ; i < strarr.length ; i++)
        {
            naklist[i] = DataUtil.getByteToInt(
                    Integer.parseInt(strarr[i]));
            log.info(naklist[i] + " frame missing");
        }

        return naklist;
    }

    /**
     * check whether frame is mutiframe or not
     *
     * @return result <code>boolean</code>
     */
    public boolean isMultiFrame()
    {
        return frameCnt > 1 ? true : false;
    }

//    /**
//     * check whether received full window or not
//     *
//     * @return result <code>boolean</code>
//     */
//    public boolean isReceivedWindow()
//    {
//        return isReceivedWindow;
//    }
//
    /**
     * check whether received end frame or not
     *
     * @return result <code>boolean</code>
     */
    public boolean isReceivedEndFrame()
    {
        return this.isReceivedEndFrame;
    }

    /**
     * reset frame buffer
     */
    public void resetAll()
    {
        this.framelist.clear();
        this.resetWindow();
        this.sequence = 0;
        this.frameCnt = 0;
        this.endSeqNumber = -1;
//        private int frameType = 0;
        this.frameType = null ;
        this.version = 0;
        this.deviceSerial = null;
        this.isReceivedEndFrame = false;
        this.isFrameGenerated = false;
    }

    /**
     * get sequence number of last received frame
     *
     * @return sequence number <code>int</code>
     */
    public int getSequence()
    {
        return this.sequence;
    }

    public short getVersion()
    {
    	return this.version;
    }
    /**
     *  get sequence number of end  frame
     *
     * @return sequence number <code>int</code>
     */
    public int getEndSeqNumber()
    {
        return this.endSeqNumber;
    }
   
    /**
     *  get deviceSerial
     *  
     * @return frameType <code>int</code>
     */
    public byte[] getdeviceSerial()
    {
    	return this.deviceSerial;
    }
    
    /**
     *  get frameType
     *  
     * @return frameType <code>int</code>
     */
    public FrameType getFrameType()
    {
    	return this.frameType;
    }
    
	public void setFrameType(byte code) {
		for (FrameType c : FrameType.values()) {
			if (c.getCode() == code){ 
				frameType = c;
				break;
			}
		}
	}
    /**
     * get received last frame
     * 
     * @return frame <code>GeneralDataFrame</code>
     * @throws Exception
     */
    public GeneralDataFrame getCurrentFrame(String ns) throws Exception
    {
        return GeneralDataFrame.decode(
                ns,(IoBuffer)framelist.get(this.sequence));
    }

    /**
     * get total length of frame
     */
    private int getTotallen()
    {
        Iterator<Object> iter = framelist.iterator();
        int totallen = 0;
        byte[] lenField = new byte[AuthFrameConstants.PAYLOAD_LENGTH_LEN];
        while(iter.hasNext())
        {
            byte[] frame = (byte[])iter.next();
            System.arraycopy(frame, AuthFrameConstants.PAYLOAD_LENGTH_OFFSET, lenField,0,lenField.length);
//            AuthFrameConstants.convertEndian(lenField);
            int len = DataUtil.getIntTo2Byte(lenField);
            totallen += len;
        }

        return totallen;
    }

    /**
     * make one frame to merge multi-frame
     *
     * @return frame bytebuffer <code>ByteBuffer</code>
     */
    public IoBuffer getCompletedFrameBuffer()
    {
        int totalbodylen = getTotallen();
        if(frameCnt == 1) // single frame
        {
            byte[] frame = (byte[])framelist.get(0);
            IoBuffer res = IoBuffer.allocate(totalbodylen);
            res.put(frame, AuthFrameConstants.FRAME_PAYLOAD_OFFSET, totalbodylen);
            res.flip();
            return res;
        }

        //log.debug("totalbodylen : " + totalbodylen);

        byte[] frame = (byte[])framelist.get(0);

        byte[] body = new byte[totalbodylen];
        Iterator<Object> iter = framelist.iterator();
        int pos = 0;
        byte[] lenField = new byte[AuthFrameConstants.PAYLOAD_LENGTH_LEN];
        while(iter.hasNext())
        {
            frame = (byte[])iter.next();
            //log.debug("frame sequence["+DataUtil.getIntToByte(
            //            frame[1])+"] frame.length["+frame.length+"]");
            System.arraycopy(frame,AuthFrameConstants.PAYLOAD_LENGTH_OFFSET,lenField,0,lenField.length);
            int len = DataUtil.getIntTo2Byte(lenField);
            System.arraycopy(frame,AuthFrameConstants.HEADER_LEN,body,pos,len);
            pos+=len;
        }

        IoBuffer res = IoBuffer.allocate(totalbodylen);
        res.put(body);
        res.flip();
        boolean isMultiFrame = frameCnt > 1 ? true : false ;
        log.debug("isMultiFrame[" + isMultiFrame   
                +"] sequence[" +this.endSeqNumber
                + "] framelist.size[" +framelist.size()
                + "] res.limit()[" +res.limit()
                + "] isReceivedEndFrame[" + this.isReceivedEndFrame + "]");
        return res;
    }

}
