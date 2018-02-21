package com.aimir.fep.protocol.fmp.frame;

import java.nio.ByteBuffer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;

import com.aimir.fep.bypass.decofactory.consts.HLSAuthForIF;
import com.aimir.fep.bypass.decofactory.consts.HLSAuthForIF.HLSSecurityControl;
import com.aimir.fep.protocol.fmp.datatype.INT;
import com.aimir.fep.protocol.fmp.exception.FMPDecodeException;
import com.aimir.fep.protocol.fmp.exception.FMPEncodeException;
import com.aimir.fep.util.Bin;
import com.aimir.fep.util.CodecUtil;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.FrameUtil;
import com.aimir.fep.util.Hex;

/**
 * GeneralDataFrame
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public abstract class GeneralDataFrame
{
    private static Log log = LogFactory.getLog(GeneralDataFrame.class);
    
    public byte soh = GeneralDataConstants.SOH;
    public byte  sequence = 0x00;
    public byte  attr = 0x00;
    public INT   length = new INT(0);
    public byte  svc = 0x00;
    private int rcvBodyLength = 0;
    private int unCompressedLength = 0;
    
    /**
     * constructor
     */
    protected GeneralDataFrame()
    {
    }

    /**
     * constructor
     *
     * @param soh <code>String</code> Start of header
     * @param sequence <code>String</code> sequence number
     * @param length <code>String</code> frame length
     * @param attr <code>String</code> frame attribute
     * @param svc <code>String</code> service type
     */
    protected GeneralDataFrame(byte soh, byte sequence,
            INT length, byte attr, byte svc)
    {
        this.soh = soh;
        this.sequence = sequence;
        this.length = length;
        this.attr = attr;
        this.svc = svc;
    }

    /**
     * get SOH
     *
     * @return soh <code>byte</code> start of header
     */
    public byte getSoh()
    {
        return this.soh;
    }

    /**
     * set SOH
     *
     * @param soh <code>byte</code> start of header
     */
    public void setSoh(byte soh)
    {
        this.soh = soh;
    }

    /**
     * get sequence
     *
     * @return sequence <code>byte</code> sequence 
     */
    public byte getSequence()
    {
        return this.sequence;
    }

    /**
     * set sequence
     *
     * @param sequence <code>byte</code> sequence 
     */
    public void setSequence(byte sequence)
    {
        this.sequence = sequence;
    }

    /**
     * get length
     *
     * @return length <code>INT</code> length 
     */
    public INT getLength()
    {
        return this.length;
    }

    /**
     * set length
     *
     * @param length <code>INT</code> length 
     */
    public void setLength(INT length)
    {
        this.length = length;
    }

    /**
     * set length
     *
     * @param length <code>int</code> length 
     */
    public void setLength(int length)
    {
        this.length = new INT(length);
    }

    /**
     * get attribute
     *
     * @return attr <code>byte</code> attribute 
     */
    public byte getAttr()
    {
        return this.attr;
    }

    /**
     * set attribute
     *
     * @param attr <code>byte</code> attribute 
     */
    public void setAttr(byte attr)
    {
        this.attr = attr;
    }

    /**
     * get service type
     *
     * @return svc <code>byte</code> service type 
     */
    public byte getSvc()
    {
        return svc;
    }

    /**
     * set service type
     *
     * @param svc <code>byte</code> service type 
     */
    public void setSvc(byte svc)
    {
        this.svc = svc;
    }

    public int getRcvBodyLength()
    {
        return rcvBodyLength;
    }

    public void setRcvBodyLength(int rcvBodyLength)
    {
        this.rcvBodyLength = rcvBodyLength;
    }
    
	public int getUnCompressedLength() {
		return unCompressedLength;
	}

    /**
     * decode
     *
     * @param bytebuffer <code>ByteBuffer</code> bytebuffer
     * @return frame <code>GeneralDataFrame</code> frame
     */
    public static GeneralDataFrame decode(String ns,
            IoBuffer bytebuffer) throws Exception
    {
        try
        {
            GeneralDataFrame frame = null;
            byte[] header = new byte[GeneralDataConstants.HEADER_LEN];
            bytebuffer.get(header,0,header.length);
            byte[] lengthField = 
                new byte[GeneralDataConstants.LENGTH_LEN];
            System.arraycopy(header,3,lengthField,0,
                    lengthField.length);
            DataUtil.convertEndian(lengthField); 
            
            log.info("GeneralDataFrame.decode() header="+Bin.bytesToBinaryString(header)); 

            int bodylen = 0;
            if(FrameUtil.isSetBit(header[2],
                        GeneralDataConstants.ATTR_COMPRESS)) 
            { 
                log.info("GeneralDataFrame.decode() uncompress"); 
                bodylen = DataUtil.getIntTo4Byte(lengthField); 
                byte[] body = new byte[bodylen]; 
                bytebuffer.get(body,0,body.length); 
                try{ 
                    log.info("compress size[" + bodylen + "]");
                    body = FrameUtil.unzlib(body); 
                    log.info("uncompress size[" + body.length + "]");
                    log.debug("UNCOMPRESSED DATA[" + Hex.decode(body)+"]");
                }catch(Exception ex) { 
                    log.error("unzlib faild : " + ex);
                    throw new FMPDecodeException(
                            "GeneralDataFrame encode failed(unzlib" 
                            + ex.getMessage()); 
                }
                lengthField = DataUtil.get4ByteToInt(body.length);
                DataUtil.convertEndian(lengthField); 
                System.arraycopy(lengthField,0,header,3,
                        lengthField.length);
                 
                bytebuffer = IoBuffer.allocate(header.length+body.length);
                // bytebuffer.rewind();
                // bytebuffer.setAutoExpand(true); 
                bytebuffer.put(header,0,header.length); 
                bytebuffer.put(body,0,body.length); 
            } 
            bytebuffer.rewind(); 
            if(FrameUtil.isSetBit(header[2],
                    GeneralDataConstants.ATTR_FRAME)) 
            { 
                frame = (ControlDataFrame) 
                    CodecUtil.unpack(ns,ControlDataFrame.class.getName(),
                            bytebuffer); 
                frame.setRcvBodyLength(frame.getLength().getValue()-GeneralDataConstants.HEADER_LEN);
            } 
            else 
            { 
                frame = (ServiceDataFrame)CodecUtil.unpack(
                        ns,ServiceDataFrame.class.getName(),bytebuffer);
                int svclen = frame.getLength().getValue() - 
                    ServiceDataConstants.HEADER_LEN;
                frame.setRcvBodyLength(frame.getRcvBodyLength()+(bodylen==0? svclen:bodylen));
                byte[] bx = new byte[svclen]; 
                bytebuffer.get(bx,0,bx.length); 
                ((ServiceDataFrame)frame).setSvcBody(bx); 
            } 
            return frame;
        }catch(Exception  ex)
        {
            log.error("GeneralDataFrame failed : " ,ex);
            throw new FMPDecodeException("GeneralDataFrame failed :"
                    + ex.getMessage());
        }
    } 
    
    /**
     * decode
     *
     * @param bytebuffer <code>ByteBuffer</code> bytebuffer
     * @return frame <code>decrypt</code>     
     */
    public static IoBuffer decryptFrame(IoBuffer bytebuffer) throws Exception
    {
    	byte[] header = new byte[GeneralDataConstants.HEADER_LEN];
        bytebuffer.get(header,0,header.length);
        
        byte[] lengthField = new byte[GeneralDataConstants.LENGTH_LEN];
        System.arraycopy(header, 3, lengthField, 0, lengthField.length);
        DataUtil.convertEndian(lengthField);
        
        int bodylen = DataUtil.getIntTo4Byte(lengthField);
        
        byte[] sc = new byte[GeneralDataConstants.SC_LEN];
        bytebuffer.get(sc,0,sc.length);
        
        byte[] ic = new byte[GeneralDataConstants.IC_LEN];
        bytebuffer.get(ic,0,ic.length);
        
        int cipherlen = bodylen - (GeneralDataConstants.SC_LEN + GeneralDataConstants.IC_LEN);
        byte[] cipherText = new byte[cipherlen];
        bytebuffer.get(cipherText,0,cipherText.length);
        
        byte svc = header[7]; 
        
        String AP_TITLE = FMPProperty.getProperty("cipher.ap_title");
        
        if(svc == GeneralDataConstants.SVC_M 
        		|| svc == GeneralDataConstants.SVC_N 
        		|| svc == GeneralDataConstants.SVC_S 
        		|| svc == GeneralDataConstants.SVC_G) { // MesurementService
        	
        	HLSAuthForIF auth = new HLSAuthForIF(HLSSecurityControl.getItem(sc[0]));
        	byte[] payload = auth.doDecryption(ic, AP_TITLE.getBytes(), cipherText);
        	
        	byte[] payloadlen = DataUtil.get4ByteToInt(payload.length);
        	DataUtil.convertEndian(payloadlen);
        	
        	IoBuffer buff = IoBuffer.allocate(header.length+payload.length+GeneralDataConstants.TAIL_LEN);              
        
        	buff.put(header, 0, 3); // SOH + SEQ + ATTR
        	buff.put(payloadlen); // LENG
        	buff.put(svc); // SVC
        	buff.put(payload, 0, payload.length); // DATA
        	buff.put(FrameUtil.getCRC(buff, 0, buff.limit())); // CRC
        	buff.flip();
        	
        	bytebuffer.rewind();
        	log.debug("decryptFrame PlainText [" + buff.getHexDump() + "]");
        	
        	return buff;
        	
        } else {
        	bytebuffer.rewind();        	
        	return bytebuffer;
        }
        
    }

    /**
     * get string
     * @return result <code>String</code>
     */
    public String toString()
    {
        StringBuffer strbuf = new StringBuffer();
        strbuf.append("CLASS["+this.getClass().getName()+"]\n");
        strbuf.append("soh : " + soh + "\n");
        strbuf.append("sequence : " + sequence + "\n");
        strbuf.append("attr : " + attr + "\n");
        strbuf.append("length : " + length + "\n");
        strbuf.append("svc : " + svc + "\n");

        return strbuf.toString();
    }

    /**
     * encode
     *
     * @return result <code>byte[]</code> 
     */
    public byte[] encode() throws Exception
    {
        try
        {
            byte[] bx = CodecUtil.pack(this);
            // SOURCE SIZE 에서 HEADER(8byte)의 길이 및 CRC(2byte)의 길이를 빼면 COMPRESS DATA를 Uncompressing 했을 때 필요한 Buffer의 길이
            unCompressedLength = bx.length - GeneralDataConstants.HEADER_LEN;
            
            if(isAttrByte(GeneralDataConstants.ATTR_COMPRESS))
            {
                log.info("GeneralDataFrame.encode() compress use gzip");
                try
                {
                    byte[] zipbx = FrameUtil.gzip(bx, 
                            GeneralDataConstants.HEADER_LEN, 
                            (bx.length - 
                             GeneralDataConstants.HEADER_LEN)); 
                    bx = DataUtil.arrayAppend(bx,0, 
                            GeneralDataConstants.HEADER_LEN,zipbx,0, 
                            zipbx.length); 
                }catch(Exception exx) { 
                    log.error("GeneralDataFrame::encode gzip failed",
                            exx); 
                    setAttrByte(GeneralDataConstants.ATTR_COMPRESS,
                            false); 
                } 
            } 
            setLength(bx.length - GeneralDataConstants.HEADER_LEN); 
            byte[] bxx = this.length.encode();
            System.arraycopy(bxx,0,bx,3,bxx.length);
            return bx;
        }catch(Exception ex)
        {
            log.error("GeneralDataFrame::encode failed ", ex);
            throw new FMPEncodeException(
                    "GeneralDataFrame::encode failed :"
                    +ex.getMessage());
        }
    }
    
    /**
     * encode
     *
     * @return result <code>byte[]</code> 
     */
    public byte[] encodeWithCompress() throws Exception
    {
        try
        {
            byte[] bx = CodecUtil.pack(this);
            // SOURCE SIZE 에서 HEADER(8byte)의 길이 및 CRC(2byte)의 길이를 빼면 COMPRESS DATA를 Uncompressing 했을 때 필요한 Buffer의 길이
            unCompressedLength = bx.length - GeneralDataConstants.HEADER_LEN;
            
            if(isAttrByte(GeneralDataConstants.ATTR_COMPRESS))
            {
                log.info("GeneralDataFrame.encode() compress use zlib.");
                try
                {
                    byte[] zipbx = FrameUtil.zlib(bx, 
                            GeneralDataConstants.HEADER_LEN, 
                            (bx.length - 
                             GeneralDataConstants.HEADER_LEN)); 
                    bx = DataUtil.arrayAppend(bx,0, 
                            GeneralDataConstants.HEADER_LEN,zipbx,0, 
                            zipbx.length); 
                }catch(Exception exx) { 
                    log.error("GeneralDataFrame::encode zlib failed",
                            exx); 
                    setAttrByte(GeneralDataConstants.ATTR_COMPRESS,
                            false); 
                } 
            } 
            setLength(bx.length - GeneralDataConstants.HEADER_LEN); 
            byte[] bxx = this.length.encode();
            System.arraycopy(bxx,0,bx,3,bxx.length);
            return bx;
        }catch(Exception ex)
        {
            log.error("GeneralDataFrame::encode failed ", ex);
            throw new FMPEncodeException(
                    "GeneralDataFrame::encode failed :"
                    +ex.getMessage());
        }
    }

    /**
     * set data length
     *
     * @throws Exception
     */
    public void setDataLength() throws Exception
    {
        int size = CodecUtil._sizeOf(this,this.getClass());
        setLength(size);
    }

    /**
     * check attribute ( end frame)
     *
     * @return result <code>boolean</code>
     */
    public boolean isEndFrame()
    {
        return isAttrByte(GeneralDataConstants.ATTR_END);
    }

    /**
     * check attribute ( service data frame)
     *
     * @return result <code>boolean</code>
     */
    public boolean isServiceDataFrame()
    {
        return !isAttrByte(GeneralDataConstants.ATTR_FRAME);
    }

    /**
     * check attribute ( control data frame)
     *
     * @return result <code>boolean</code>
     */
    public boolean isControlDataFrame()
    {
        return isAttrByte(GeneralDataConstants.ATTR_FRAME);
    }

    /**
     * check attribute
     *
     * @return result <code>boolean</code>
     */
    public boolean isAttrByte(byte attrByte)
    {
        byte orbit = (byte)(this.attr & attrByte);
        int ival = DataUtil.getIntToByte(orbit);
        if(ival > 0)
            return true;

        return false;
    }

    /**
     * set attribute
     *
     * @param attrByte <code>byte</code>
     */
    public void setAttrByte(byte attrByte)
    { 
        this.attr = (byte)(this.attr | attrByte);
    }

    /**
     * set attribute
     *
     * @param attrByte <code>byte</code>
     * @param isOr <code>boolean</code>
     */
    public void setAttrByte(byte attrByte, boolean isOr)
    { 
        if(isOr)
            this.attr = (byte)(this.attr | attrByte);
        else
        {
            byte orgattr = this.attr;
            byte andByte = (byte)(this.attr & attrByte);
            byte exByte = (byte)(~andByte);
            this.attr = (byte)(this.attr & exByte);
        }
    }    
}
