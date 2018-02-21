package com.aimir.fep.protocol.nip.common;

import java.nio.ByteBuffer;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.CRCUtil;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

public class MultiDataProcessor{
    private static Log log = LogFactory.getLog(MultiDataProcessor.class);
    
    //Frame Length Defined
    private static final int startFlagLen = 2;
    private static final int networkTypeLen	= 1;
    private static final int frameControlLen = 1;
    private static final int frameOptionLen = 1;
    private static final int sequenceNumberLen = 1;
    private static final int frameCrcLen = 2;
    private static final int payloadLengthLen = 2;
    private static final int MaxByte = 255;
    
    //0 or 8
    private int sourceAddressLen = 0;
    private int destinationAddressLen = 0;
    //0 or 17(soria)
    private int networkStatusFeildLen = 0;
    //0 or N
    private int framePayloadLen = 0;
    private int frameHeaderLen = 0;
    private int framePanding = 0;
	
    public int getFramePanding() {
        return this.framePanding;
    }
    
    public void setAddressLen(byte addressType){
        if(addressType == (byte)0x10){//Sourcec
            sourceAddressLen = 8;
        }
        else if(addressType == (byte)0x20){//Destination
            destinationAddressLen = 8;
        }
        else if(addressType == (byte)0x30){//SrcDest
            sourceAddressLen = 8;
            destinationAddressLen = 8;
        }
    }
	
    public void setNetworkStatus(String networkStatus){
        if(networkStatus.equals("1")){//include
            networkStatusFeildLen = 17;
        }
    }
	
    public void setFramePayloadLen(int dataLen){
        frameHeaderLen = startFlagLen+networkTypeLen+frameControlLen
                +frameOptionLen+sequenceNumberLen+sourceAddressLen
                +destinationAddressLen+networkStatusFeildLen+payloadLengthLen;
        framePayloadLen = dataLen - frameHeaderLen - frameCrcLen;
    }
	
    public void decode(byte[] data ,int type ,int limit){
        byte[] b = new byte[1];
        String controlFrame;
        switch (type) {
            case 1:
                System.arraycopy(data, 3, b, 0, b.length);
                controlFrame = DataUtil.getBit(b[0]);
                framePanding = Integer.parseInt(DataUtil.getBitToInt(controlFrame.substring(0,1),"%d"));
                b = new byte[1];
                System.arraycopy(data, 4, b, 0, b.length);
                String optionFrame = DataUtil.getBit(b[0]);
                setNetworkStatus(optionFrame.substring(1,2));
                byte addresType = DataUtil.getByteToInt(DataUtil.getBitToInt(optionFrame.substring(2,4),"%d0000"));
                setAddressLen(addresType);
                byte[] bx = new byte[limit];
                System.arraycopy(data, 0, bx, 0, bx.length);
                setFramePayloadLen(bx.length);
                break;
            case 2:
                log.debug("data:"+Hex.decode(data));
                System.arraycopy(data, 3, b, 0, b.length);
                controlFrame = DataUtil.getBit(b[0]);
                framePanding = Integer.parseInt(DataUtil.getBitToInt(controlFrame.substring(0,1),"%d"));
                log.debug("framePanding:"+framePanding);
                break;
        }
    }
	
    public ByteBuffer chgMFtoSF(byte[] data, int cnt, int pos, int limit)
    {
        int totLen = data.length - (frameHeaderLen*(cnt-1)) - (frameCrcLen*(cnt-1));
        int totPayloadLen = (framePayloadLen*(cnt-1)) + (limit-pos)-frameHeaderLen-frameCrcLen;
        ByteBuffer recvBuff = ByteBuffer.allocate(totLen);
        int regularLen =frameHeaderLen+framePayloadLen+frameCrcLen;
        byte[] bx;
 
        for(int i=0;i<cnt;i++){
            if(i==0){//처음
                bx =new byte[6];//between frameHeader and payloadLength
                System.arraycopy(data, 0, bx, 0, bx.length);
                recvBuff.put(bx);
                //set payloadLength
                recvBuff.put(DataUtil.get2ByteToInt(totPayloadLen));
                bx =new byte[framePayloadLen];  
                System.arraycopy(data, frameHeaderLen, bx, 0, bx.length);
                recvBuff.put(bx);
            }
            else if(i==(cnt-1)){//마지막
                bx =new byte[limit-pos-frameHeaderLen-frameCrcLen];
                System.arraycopy(data, (i*regularLen)+frameHeaderLen, bx, 0, bx.length);
                recvBuff.put(bx);
            }
            else{//중간
                bx =new byte[framePayloadLen];
                System.arraycopy(data, (i*regularLen)+frameHeaderLen, bx, 0, bx.length);
                recvBuff.put(bx);
            }
        }
			
        byte[] crc = CRCUtil.Calculate_ZigBee_Crc(recvBuff.array(),(char)0x0000);
        DataUtil.convertEndian(crc);
        recvBuff.put(crc);
        
        return recvBuff;
    }
	
    public HashMap<String, byte[]> chgSFtoMF(byte[] data)
    {
        byte[] b = new byte[1];
        System.arraycopy(data, 3, b, 0, b.length);
        String controlFrame = DataUtil.getBit(b[0]);
        framePanding = Integer.parseInt(DataUtil.getBitToInt(controlFrame.substring(0,1),"%d"));
        int requestAck = Integer.parseInt(DataUtil.getBitToInt(controlFrame.substring(6,8),"%d"));
        b = new byte[1];
        System.arraycopy(data, 4, b, 0, b.length);
        String optionFrame = DataUtil.getBit(b[0]);
        setNetworkStatus(optionFrame.substring(1,2));
       
        byte addresType = DataUtil.getByteToInt(DataUtil.getBitToInt(optionFrame.substring(2,4),"%d0000"));
        setAddressLen(addresType);

        byte[] bx = new byte[data.length-frameHeaderLen-frameCrcLen];
        System.arraycopy(data, 0, bx, 0, bx.length);
        setFramePayloadLen(bx.length);
        
        HashMap<String, byte[]> map = new HashMap<String, byte[]>();
        int payloadLen = MaxByte - frameHeaderLen -frameCrcLen;
        int loopCnt = framePayloadLen/payloadLen;
        if(MaxByte%payloadLen > 0){
            loopCnt++;	
            log.debug("loopCnt2:"+loopCnt);
        }
        
        byte[] payloadData = new byte[framePayloadLen+2];
        System.arraycopy(data, frameHeaderLen, payloadData, 0, payloadData.length);
        byte[] headFrame = new byte[frameHeaderLen];
        System.arraycopy(data, 0, headFrame, 0, frameHeaderLen);

        int realPayLoadLen = MaxByte - frameHeaderLen - frameCrcLen;
        byte[] payLen;
        byte[] payData;
        ByteBuffer buffer;
        for(int i=0;i< loopCnt;i++){
            payLen = new byte[2];
            if(i==loopCnt-1){//마지막
                payLen = DataUtil.get2ByteToInt(framePayloadLen-(realPayLoadLen*i)+frameCrcLen);
                buffer = ByteBuffer.allocate(frameHeaderLen+(framePayloadLen-(realPayLoadLen*i))+frameCrcLen+frameCrcLen);
                payData = new byte[framePayloadLen-(realPayLoadLen*i)+frameCrcLen];
                headFrame[3] =(byte)((byte)0x00|DataUtil.getByteToInt(requestAck)) ;
                System.arraycopy(payloadData, i*realPayLoadLen, payData, 0, payData.length);
            }
            else{
                payLen = DataUtil.get2ByteToInt(realPayLoadLen);
                buffer = ByteBuffer.allocate(MaxByte);
                payData = new byte[realPayLoadLen];
                headFrame[3] =(byte)((byte)0x80|DataUtil.getByteToInt(requestAck)) ;
                System.arraycopy(payloadData, i*realPayLoadLen, payData, 0, payData.length);
            }
            headFrame[5] =DataUtil.getByteToInt(i);
            headFrame[frameHeaderLen-2] = payLen[0];
            headFrame[frameHeaderLen-1] = payLen[1];
            buffer.put(headFrame);
            buffer.put(payData);
            byte[] crc =CRCUtil.Calculate_ZigBee_Crc(buffer.array(),(char)0x0000);
            DataUtil.convertEndian(crc);
            buffer.put(crc);
            buffer.clear();
            
            map.put(String.valueOf(i), buffer.array());
        }
        
        return map;
    }
}