package com.aimir.fep.protocol.fmp.frame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.datatype.INT;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.util.Hex;

/**
 * ControlDataFrame
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class ControlDataFrame extends GeneralDataFrame
{
    private static Log log = LogFactory.getLog(ControlDataFrame.class);
    public byte code = 0;
    public OCTET arg = new OCTET();  

    private int rcvBodyLength = 0;

    /**
     * constructor
     */
    public ControlDataFrame()
    {
    }

    /**
     * constructor
     *
     * @param code <code>byte</code> code
     */
    public ControlDataFrame(byte code)
    {
        //this.attr = (byte) (GeneralDataConstants.ATTR_FRAME | GeneralDataConstants.ATTR_COMPRESS | GeneralDataConstants.ATTR_START | GeneralDataConstants.ATTR_END);
        super(GeneralDataConstants.SOH,(byte)0,
                new INT(0),(byte)0x80,(byte)0);
        this.code = code; 
        if(this.code == ControlDataConstants.CODE_ENQ) {
            setArg(ControlDataConstants.CODE_ENQ_ARG);
        }
        else if(this.code == ControlDataConstants.CODE_NEG)
        	setArg(ControlDataConstants.CODE_NEG_ARG);
        else if(this.code == ControlDataConstants.CODE_NEGR)
        	setArg(ControlDataConstants.CODE_NEGR_ARG);
        else if(this.code == ControlDataConstants.CODE_ACK)
            setArg(ControlDataConstants.CODE_ACK_ARG);
        else if(this.code == ControlDataConstants.CODE_NAK)
            setArg(ControlDataConstants.CODE_NAK_ARG);
        else if(this.code == ControlDataConstants.CODE_CAN)
            setArg(ControlDataConstants.CODE_CAN_ARG);
        else if(this.code == ControlDataConstants.CODE_EOT)
            setArg(ControlDataConstants.CODE_EOT_ARG);
        else if(this.code == ControlDataConstants.CODE_AUT)
            setArg(ControlDataConstants.CODE_AUT_ARG);
        else {
            setArg(ControlDataConstants.CODE_ENQ_ARG);
        }
    }
    
    public void setNameSpace(byte code, byte[] nameSpace)
    {
    	
    	byte[] code_neg_arg = ControlDataConstants.CODE_NEG_ARG.getValue(); // encode();
    	code_neg_arg[5] = nameSpace[0];
    	code_neg_arg[6] = nameSpace[1];

        this.code = code; 
        if(this.code == ControlDataConstants.CODE_NEG)
        	setArg(new OCTET(code_neg_arg));

    }

	public int getRcvBodyLength() {
		return rcvBodyLength;
	}

	public void setRcvBodyLength(int rcvBodyLength) {
		this.rcvBodyLength = rcvBodyLength;
	}

	/**
     * get code
     *
     * @result code <code>byte</code> code
     */
    public byte getCode()
    {
        return this.code;
    }

    /**
     * set code
     *
     * @param code <code>byte</code> code
     */
    public void setCode(byte code)
    {
        this.code = code;
    }

    /**
     * get arg
     *
     * @result arg <code>OCTET</code> arg
     */
    public OCTET getArg()
    {
        return this.arg;
    }

    /**
     * set arg
     *
     * @param arg <code>OCTET</code> arg
     */
    public void setArg(OCTET arg)
    {
        this.arg = arg;
    }

    /**
     * decode
     *
     * @param bytebuffer <code>ByteBuffer</code> bytebuffer
     * @return frame <code>GeneralDataFrame</code> frame
     */
    /*
    public static ControlDataFrame decode(
            IoBuffer bytebuffer) throws Exception
    {
        try
        {
        	ControlDataFrame frame = null;
            byte[] header = new byte[GeneralDataConstants.HEADER_LEN];
            bytebuffer.get(header,0,header.length);
            byte[] lengthField = 
                new byte[GeneralDataConstants.LENGTH_LEN];
            System.arraycopy(header,3,lengthField,0,
                    lengthField.length);
            DataUtil.convertEndian(lengthField); 

            int bodylen = 0;
            bytebuffer.rewind(); 
            if(FrameUtil.isSetBit(header[2],
                    GeneralDataConstants.ATTR_FRAME)) 
            { 
                frame = (ControlDataFrame) 
                    CodecUtil.unpack(ControlDataFrame.class.getName(),
                            bytebuffer); 
                frame.setRcvBodyLength(frame.getLength().getValue()-GeneralDataConstants.HEADER_LEN);
            } 

            return frame;
        }catch(Exception  ex)
        {
            log.error("ControlDataFrame failed : " ,ex);
            throw new FMPDecodeException("ControlDataFrame failed :"
                    + ex.getMessage());
        }
    } 
    */
    
    /**
     * encode
     *
     * @return result <code>byte[]</code> 
     */
    /*
    public byte[] encode() throws Exception
    {
        try
        {
            byte[] bx = CodecUtil.pack(this);

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
    */
    
    /**
     * get string
     */
    public String toString()
    {
        StringBuffer strbuf = new StringBuffer();
        strbuf.append(super.toString());
        strbuf.append("CLASS["+this.getClass().getName()+"]\n");
        strbuf.append("code : " + code + "\n");
        strbuf.append("arg: " + Hex.decode(arg.getValue()) + "\n");

        return strbuf.toString();
    }
}
