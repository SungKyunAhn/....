package com.aimir.fep.protocol.fmp.common;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;

import com.aimir.fep.protocol.fmp.frame.GeneralDataConstants;
import com.aimir.fep.protocol.fmp.frame.GeneralDataFrame;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FrameUtil;
import com.aimir.fep.util.Hex;

/**
 * Multi Frame Slide Window
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */

public class SlideWindow
{
    private Log log = LogFactory.getLog(SlideWindow.class);

    private ArrayList<Object> framelist = new ArrayList<Object>();
    /* modified by D.J Park in 2006.10.11
     * Window Size does not fixed. so Window Size fixed by calculation 
     * WCK's start and end sequence first received from MCU 
     */
    private Object[] window = 
        new Object[GeneralDataConstants.FRAME_WINSIZE];
    // private final int FRAME_MAX_WINSIZE = 16;
    private int FRAME_WINSIZE = 0;
    // private Object[] window = new Object[FRAME_MAX_WINSIZE];
    private int sequence = 0;
    private boolean isReceivedEndFrame = false;
    private boolean isReceivedAllFrame = false;
    private boolean isReceivedWindow = false;
    private boolean isMultiFrame = false;
    public static final int COMPRESS_HEADER_LEN = 5;
    public static final int COMPRESS_TYPE_LEN = 1;
    public static final int SOURCE_SIZE_LEN = 4;
    
    /**
     * constructor
     */
    public SlideWindow()
    {
        framelist.clear();
    }

    /**
     * put received frame stream to window 
     *
     * @param buff <code>ByteBuffer</code> received bytebuffer
     */
    public void put(IoBuffer buff)
    {
        byte seq = buff.get(1);
        byte attr = buff.get(2);
        
        // check start and end bit
        boolean isStartFrame = 
            FrameUtil.isSetBit(attr,GeneralDataConstants.ATTR_START);
        boolean isEndFrame = 
            FrameUtil.isSetBit(attr,GeneralDataConstants.ATTR_END);

        if(isStartFrame)
        {
            framelist.clear();
        }
        if(!isStartFrame || !isEndFrame)
            isMultiFrame = true;

        if(isEndFrame)
            isReceivedEndFrame = true;
        else
        {
            isReceivedEndFrame = false;
            isReceivedAllFrame = false;
        }

        this.sequence = DataUtil.getIntToByte(seq);
        buff.rewind();
        byte[] frame = new byte[buff.limit()];
        buff.get(frame,0,frame.length); 
        //window[this.sequence%GeneralDataConstants.FRAME_WINSIZE] = frame;
        window[this.sequence%window.length] = frame;

        log.debug("window.size : " + window.length
                + ", framelist.size : " + framelist.size() 
                + ", sequence : " + this.sequence
                + ", isEndFrame : " + this.isReceivedEndFrame
                + ", frame : " + Hex.decode(frame));
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
    public byte[] checkWindow(int start,int end)
    {
        if(FRAME_WINSIZE == 0)
        {
            Object[] tmpwindow = new Object[end-start+1];
            FRAME_WINSIZE = tmpwindow.length;
            for(int i = 0 ; i < tmpwindow.length ; i++)
            {
                tmpwindow[i] = window[i];
            }
            window = null;
            window = tmpwindow;
        }
        //int startseq = start%GeneralDataConstants.FRAME_WINSIZE;
        //int endseq = end%GeneralDataConstants.FRAME_WINSIZE;
        int startseq = start%FRAME_WINSIZE;
        int endseq = end%FRAME_WINSIZE;

        log.info("FRAME_WINSIZE[" + FRAME_WINSIZE + "] startseq[" + startseq + 
                "] endseq[" + endseq + "]");
        ArrayList<String> al = new ArrayList<String>();
        for(int i = startseq ; i <= endseq ; i++)
        {
            if(window[i] == null)
                al.add(new String(""+(i+startseq)));
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
        return isMultiFrame;
    }

    /**
     * check whether received full window or not
     *
     * @return result <code>boolean</code>
     */
    public boolean isReceivedWindow()
    {
        return isReceivedWindow;
    }

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
     * check whether received all frame or not
     *
     * @return result <code>boolean</code>
     */
    public boolean isReceivedAllFrame()
    {
        return this.isReceivedAllFrame;
    }

    /**
     * reset frame buffer
     */
    public void resetAll()
    {
        this.framelist.clear();
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
        byte[] lenField = new byte[GeneralDataConstants.LENGTH_LEN];
        while(iter.hasNext())
        {
            byte[] frame = (byte[])iter.next();
            System.arraycopy(frame,3,lenField,0,lenField.length);
            DataUtil.convertEndian(lenField);
            int len = DataUtil.getIntTo4Byte(lenField);
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
        if(!isMultiFrame)
        {
            byte[] frame = (byte[])framelist.get(0);
            IoBuffer res = IoBuffer.allocate(totalbodylen+10);
            res.put(frame,0,frame.length);
            res.flip();
            return res;
        }

        //log.debug("totalbodylen : " + totalbodylen);

        byte[] frame = (byte[])framelist.get(0);

        /* modified by D.J Park in 2006.10.11
         * header is included body 
        byte[] header = new byte[8]; 
        System.arraycopy(frame,0,header,0,header.length);
        */
        byte[] body = new byte[totalbodylen];
        Iterator<Object> iter = framelist.iterator();
        int pos = 0;
        byte[] lenField = new byte[GeneralDataConstants.LENGTH_LEN];
        while(iter.hasNext())
        {
            frame = (byte[])iter.next();
            //log.debug("frame sequence["+DataUtil.getIntToByte(
            //            frame[1])+"] frame.length["+frame.length+"]");
            System.arraycopy(frame,GeneralDataConstants.LENGTH_POS,lenField,0,lenField.length);
            DataUtil.convertEndian(lenField);
            int len = DataUtil.getIntTo4Byte(lenField);
            System.arraycopy(frame,GeneralDataConstants.HEADER_LEN,body,pos,len);
            pos+=len;
        }

        /* modified by D.J Park in 2006.10.11
         * header is included body 
        ByteBuffer res = ByteBuffer.allocate(8+totalbodylen);
        byte[] lengthField = DataUtil.get4ByteToInt(totalbodylen);
        DataUtil.convertEndian(lengthField);
        header[3] = lengthField[0];
        header[4] = lengthField[1];
        header[5] = lengthField[2];
        header[6] = lengthField[3];
        res.put(header);
        */

        IoBuffer res = IoBuffer.allocate(totalbodylen);
        res.put(body);
        res.flip();
        log.debug("isMultiFrame[" +isMultiFrame 
                +"] sequence[" +this.sequence 
                + "] framelist.size[" +framelist.size()
                + "] res.limit()[" +res.limit()
                + "] isReceivedEndFrame[" + this.isReceivedEndFrame + "]");

        return res;
    }
    
    public enum COMPRESSTYPE {
        DAT(0, "dat"),
        ZLIB(1, "zlib"),
        GZIP(2, "gz"),
        RAW(3, "raw");
        
        private int code;
        private String name;
        
        COMPRESSTYPE(int code, String name) {
            this.code = code;
            this.name = name;
        }

        public int getCode()
        {
            return code;
        }

        public void setCode(int code)
        {
            this.code = code;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }
    }

    public static COMPRESSTYPE getCompressType(int code)
    throws Exception
    {
        for (int i = 0;  i < COMPRESSTYPE.values().length; i++ ) {
            if (COMPRESSTYPE.values()[i].getCode() == code)
                return COMPRESSTYPE.values()[i];
        }
        // throw new Exception("Invalid compress type[" + code + "]");
        return COMPRESSTYPE.RAW;
    }
}
