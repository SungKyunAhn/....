package com.aimir.fep.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.aimir.fep.meter.parser.plc.PLCDataConstants;
import com.aimir.fep.meter.parser.plc.PLCDataFrame;
import com.aimir.fep.protocol.emnv.frame.EMnVConstants;
import com.aimir.fep.protocol.fmp.datatype.BYTE;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.protocol.fmp.frame.AMUGeneralDataConstants;
import com.aimir.fep.protocol.fmp.frame.AMUGeneralDataFrame;
import com.aimir.fep.protocol.fmp.frame.ControlDataConstants;
import com.aimir.fep.protocol.fmp.frame.ControlDataFrame;
import com.aimir.fep.protocol.fmp.frame.GeneralDataConstants;
import com.aimir.fep.protocol.fmp.frame.GeneralDataFrame;
import com.aimir.fep.protocol.fmp.frame.AMUFrameControl;
import com.aimir.fep.protocol.fmp.frame.PartialDataFrame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

/**
 * Frame Utility
 *
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class FrameUtil
{
    private static Log log = LogFactory.getLog(FrameUtil.class);
    private static int tid = 0;

    /**
     * constructor
     */
    public FrameUtil()
    {
    }

    // Control Data Frame
    /**
     * get ENQ ControlDataFrame
     *
     * @return enq <code>ControlDataFrame</code>
     */
    public static ControlDataFrame getENQ()
    {
        ControlDataFrame res = new ControlDataFrame(
                ControlDataConstants.CODE_ENQ);
        return res;
    }
    
    public static ControlDataFrame getNEG(String nameSpace)
    {
        ControlDataFrame res = new ControlDataFrame(
                ControlDataConstants.CODE_NEG);
        res.setNameSpace(ControlDataConstants.CODE_NEG, nameSpace.getBytes());
        
        return res;
    }
    
    public static ControlDataFrame getNEGR()
    {
        ControlDataFrame res = new ControlDataFrame(
                ControlDataConstants.CODE_NEGR);
        return res;
    }

    /**
     * Get AMU Ack frame
     * @param frame
     * @return
     */
    public static AMUGeneralDataFrame getAMUACK(IoSession session ,AMUGeneralDataFrame frame){
        /**
         * ACK 요청이 오면 
         *  - src addr <-> dest addr
         *  - ACK bit =  0 Setting
         *  - PayLoad  = null
         */
        AMUGeneralDataFrame res = new AMUGeneralDataFrame();
        // SOH
        res.setSoh(AMUGeneralDataConstants.SOH);
        // Frame Control
        byte frmaType = frame.getAmuFrameControl().getFrameType();
        byte dToSType = frame.getAmuFrameControl().getSourceAddrType();
        byte sToDType = frame.getAmuFrameControl().getDestAddrType();

        AMUFrameControl ac = new AMUFrameControl
                (frmaType , false, false, false, false, dToSType , sToDType);
        res.setAmuFrameControl(ac);
        // Dest Address && Source Address
        byte[] dToSAddr = frame.getSource_addr();
        byte[] sToDAddr = frame.getDest_addr();
        res.setDest_addr(dToSAddr);
        res.setSource_addr(sToDAddr);
        // Sequence
        int seq =0 ;
        if (session.getAttribute(AMUGeneralDataConstants.TX_SEQ) != null) {
            seq = (Integer)session.getAttribute(AMUGeneralDataConstants.TX_SEQ);
        }
        res.setSequence(DataUtil.getByteToInt(seq+1));
        // payload length
        res.setPayload_len(new byte[]{0x00});
        res.setFp(null);

        return res;
    }
    
    /**
     * get ACK ControlDataFrame
     *
     * @param gen <code>GeneralDataFrame</code> general data frame
     * @return ack <code>ControlDataFrame</code>
     */
    public static ControlDataFrame getACK(GeneralDataFrame gen)
    {
        ControlDataFrame res = new ControlDataFrame(
                ControlDataConstants.CODE_ACK);
        byte[] bx =  new byte[] { gen.getSequence() };
        res.setArg(new OCTET(bx));
        return res;
    }

    /**
     * Get PLC Ack frame
     * @param frame
     * @return
     */
    public static PLCDataFrame getPLCACK(PLCDataFrame frame){
        PLCDataFrame res = new PLCDataFrame();
        res.setSof(PLCDataConstants.SOF);
        res.setProtocolDirection(PLCDataConstants.PROTOCOL_DIRECTION_FEP_IRM);
        res.setProtocolVersion(frame.getProtocolVersion());
        res.setDId(frame.getSId());
        res.setSId(frame.getDId());
        res.setLength(PLCDataConstants.ACK_LEN);
        res.setCommand(PLCDataConstants.COMMAND_A);
        res.setData(null);
        return res;
    }

    /**
     * IRM->FEP Frame need Ack(exclude ack, nak frame)
     * @param pdf
     * @return
     */
    public static boolean isAck(PLCDataFrame pdf){
        boolean isAck = false;
        if(pdf.getProtocolDirection()==PLCDataConstants.PROTOCOL_DIRECTION_IRM_FEP){
            if(pdf.getCommand()!=PLCDataConstants.COMMAND_A || pdf.getCommand()!=PLCDataConstants.COMMAND_a){
                isAck = true;
            }
        }
        return isAck;
    }
    
    /**
     * is Frame Ack 
     * @param agdf
     * @return
     */
    public static boolean isAmuAck(AMUGeneralDataFrame agdf){
        boolean isAck = false;
        
            if(agdf.getAmuFrameControl().isAckRequest()){
                isAck = true;
            }
        
        return isAck;
    }

    /**
     * get ACK ControlDataFrame
     *
     * @param sequence <code>byte</code> sequence
     * @return ack <code>ControlDataFrame</code>
     */
    public static ControlDataFrame getACK(byte sequence)
    {
        ControlDataFrame res = new ControlDataFrame(
                ControlDataConstants.CODE_ACK);
        byte[] bx = new byte[] { sequence };
        res.setArg(new OCTET(bx));
        return res;
    }

    /**
     * get NAK  ControlDataFrame
     *
     * @param gen <code>GeneralDataFrame</code> general data frame
     * @return nak <code>ControlDataFrame</code>
     */
    public static ControlDataFrame getNAK(GeneralDataFrame gen)
    {
        ControlDataFrame res = new ControlDataFrame(
                ControlDataConstants.CODE_NAK);
        byte[] bx =  new byte[] { gen.getSequence() };
        res.setArg(new OCTET(bx));
        return res;
    }

    /**
     * get NAK  ControlDataFrame
     *
     * @param sequence <code>int</code> sequence
     * @return nak <code>ControlDataFrame</code>
     */
    public static ControlDataFrame getNAK(byte sequence)
    {
        ControlDataFrame res = new ControlDataFrame(
                ControlDataConstants.CODE_NAK);
        byte[] bx = new byte[] { sequence };
        res.setArg(new OCTET(bx));
        return res;
    }

    public static PLCDataFrame getPLCNAK(PLCDataFrame frame, byte errCode)
    {
        PLCDataFrame res = new PLCDataFrame(frame, errCode);
        return res;
    }

    /**
     * get NAK  ControlDataFrame
     *
     * @param sequences <code>byte[]</code> sequences
     * @return nak <code>ControlDataFrame</code>
     */
    public static ControlDataFrame getNAK(byte[] sequences)
    {
        ControlDataFrame res = new ControlDataFrame(
                ControlDataConstants.CODE_NAK);
        res.setArg(new OCTET(sequences));
        return res;
    }

    /**
     * get CAN  ControlDataFrame
     *
     * @return can <code>ControlDataFrame</code>
     */
    public static ControlDataFrame getCAN()
    {
        ControlDataFrame res = new ControlDataFrame(
                ControlDataConstants.CODE_CAN);
        return res;
    }

    /**
     * get EOT  ControlDataFrame
     *
     * @return eot <code>ControlDataFrame</code>
     */
    public static ControlDataFrame getEOT()
    {
        ControlDataFrame res = new ControlDataFrame(
                ControlDataConstants.CODE_EOT);
        return res;
    }

    /**
     * get AUT  ControlDataFrame
     *
     * @return aut <code>ControlDataFrame</code>
     */
    public static ControlDataFrame getAUT()
    {
        ControlDataFrame res = new ControlDataFrame(
                ControlDataConstants.CODE_AUT);
        return res;
    }

    /**
     * get WCK  ControlDataFrame
     *
     * @return wck <code>ControlDataFrame</code>
     */
    public static ControlDataFrame getWCK()
    {
        ControlDataFrame res = new ControlDataFrame(
                ControlDataConstants.CODE_WCK);
        return res;
    }

    /**
     * get WCK  ControlDataFrame
     *
     * @param start <code>int</code> start sequence
     * @param end <code>int</code> end sequence
     * @return wck <code>ControlDataFrame</code>
     */
    public static ControlDataFrame getWCK(int start, int end)
    {
        ControlDataFrame res = new ControlDataFrame(
                ControlDataConstants.CODE_WCK);
        byte[] bx = new byte[2];
        bx[0]=DataUtil.getByteToInt(start);
        bx[1]=DataUtil.getByteToInt(end);
        res.setArg(new OCTET(bx));
        return res;
    }


    /**
     *  verify frame length
     *
     * @param in <code>ByteBuffer</code> input frame buffer
     * @return result <code>ControlDataFrame</code>
     */
    public static boolean checkLength(IoBuffer in)
    {
        int totallen = in.remaining();
        int headerlen = GeneralDataConstants.HEADER_LEN;
        byte[] lengthField = new byte[
            GeneralDataConstants.LENGTH_LEN];
        DataUtil.arraycopy(in,3,lengthField,0,lengthField.length);
        DataUtil.convertEndian(lengthField);
        int bodylen = DataUtil.getIntToBytes(lengthField);
        //log.debug("totallen :  " + totallen );
        //log.debug("bodylen :  " + bodylen );
        return totallen == (headerlen + bodylen + 2);
    }

    /**
     * @param in
     * @return
     */
    //TODO PLC용 crc 체크 합수가 정상 동작하는지 검증 필요
    public static boolean checkPLCCRC(IoBuffer in)
    {
        byte[] tail = new byte[2];
        CRC16 crc16 = new CRC16();
        int len = in.limit() - PLCDataConstants.SOF_LEN - PLCDataConstants.CRC_LEN - PLCDataConstants.EOF_LEN;
        //log.debug("checkCRC:: len :  " + len );
        crc16.reset();
        crc16.update(in,PLCDataConstants.SOF_LEN,len);
        int checkSum = crc16.getValue();
        tail[0] = in.get(len+1);
        tail[1] = in.get(len+2);
        DataUtil.convertEndian(tail);
        //log.debug("checkCRC:: checkSum  : " + checkSum);
        //log.debug("checkCRC:: tail  : " + DataUtil.getIntTo2Byte(tail));
        return checkSum == DataUtil.getIntTo2Byte(tail);
    }

    /**
     *  verify frame CRC
     *
     * @param in <code>ByteBuffer</code> input frame buffer
     * @return result <code>ControlDataFrame</code>
     */
    public static boolean checkCRC(byte[] in)
    {
        byte[] tail = new byte[2];
        CRC16 crc16 = new CRC16();
        int len = in.length - 2;
        //log.debug("checkCRC:: len :  " + len );
        crc16.reset();
        crc16.update(in,0,len);
        int checkSum = crc16.getValue();
        tail[0] = in[len];
        tail[1] = in[len+1];
        DataUtil.convertEndian(tail);
        //log.debug("checkCRC:: checkSum  : " + checkSum);
        //log.debug("checkCRC:: tail  : " + DataUtil.getIntTo2Byte(tail));
        return checkSum == DataUtil.getIntTo2Byte(tail);
    }
    
    /**
     *  verify frame CRC
     *
     * @param in <code>ByteBuffer</code> input frame buffer
     * @return result <code>ControlDataFrame</code>
     */
    public static boolean checkCRC(IoBuffer in)
    {
        byte[] tail = new byte[2];
        CRC16 crc16 = new CRC16();
        int len = in.limit() - 2;
        //log.debug("checkCRC:: len :  " + len );
        crc16.reset();
        crc16.update(in,0,len);
        int checkSum = crc16.getValue();
        tail[0] = in.get(len);
        tail[1] = in.get(len+1);
        DataUtil.convertEndian(tail);
        //log.debug("checkCRC:: checkSum  : " + checkSum);
        //log.debug("checkCRC:: tail  : " + DataUtil.getIntTo2Byte(tail));
        return checkSum == DataUtil.getIntTo2Byte(tail);
    }
    
    /**
     * AMU Calculate_ZigBee_Crc CRC 체크 합수가 정상인지 Check
     *
     * Server로 들어온 Data를    CRC를 제외한 (FH, FP) 과  CRC부분으로 나누고
     * CRC가 제외된 부분을 decode
     *
     * @param in
     * @return
     */
    public static boolean checkAMUCRC(IoBuffer in)
    {
        /**
         * ex) in.limit = 29
         *  len = 27
         *  tail[0] = in.get(27)
         *  tail[1] = in.get(28)
         *  
         *  data = new byte[26]
         */
        byte[] tail = new byte[AMUGeneralDataConstants.FRAME_CRC_LEN];
        int len     = in.limit()- AMUGeneralDataConstants.FRAME_CRC_LEN;
        
        tail[0] = in.get(len);
        tail[1] = in.get(len+1);
        // DataUtil.convertEndian(tail);
        log.debug("tail(Crc) : " + Hex.decode(tail));
        byte[] data = new byte[len];
        DataUtil.arraycopy(in, 0, data, 0, data.length);
        log.debug("data  : " + Hex.decode(data));
        
        byte[] checkCrc= CRCUtil.Calculate_ZigBee_Crc(data,(char)0x0000);
        byte[] convertCheckCrc = checkCrc;
        DataUtil.convertEndian(convertCheckCrc);
        log.debug("checkCrc  : " + Hex.decode(checkCrc) + ", convertCheckCrc : " + Hex.decode(convertCheckCrc));
        int itail   = DataUtil.getIntTo2Byte(tail);
        int iCheck  = DataUtil.getIntTo2Byte(checkCrc);
        int iCCheck = DataUtil.getIntTo2Byte(convertCheckCrc);
        
        return  itail == iCheck || itail == iCCheck;

    }
    /**
     *  get CRC value
     *
     * @param out <code>byte[]</code> byte array to caculate crc
     * @return crc <code>byte[]</code>
     */
    public static byte[] getCRC(byte[] out)
    {
        byte[] crc = new byte[2];
        CRC16 crc16 = new CRC16();
        crc16.reset();
        crc16.update(out);
        int checkSum = crc16.getValue();
        //log.debug("checkSum  : " + checkSum);
        crc=DataUtil.get2ByteToInt(checkSum);
        DataUtil.convertEndian(crc);
        return crc;
    }

    /**
     *  get CRC value
     *
     * @param out <code>byte[]</code> byte array to caculate crc
     * @param off <code>int</code> start offset
     * @param len <code>int</code> length
     * @return crc <code>byte[]</code>
     */
    public static byte[] getCRC(byte[] out,int off,int len)
    {
        byte[] crc = new byte[2];
        CRC16 crc16 = new CRC16();
        crc16.reset();
        crc16.update(out,off,len);
        int checkSum = crc16.getValue();
        crc=DataUtil.get2ByteToInt(checkSum);
        DataUtil.convertEndian(crc);
        return crc;
    }

    /**
     *  get CRC value
     *
     * @param buf <code>ByteBuffer</code> byte buffer to caculate crc
     * @param off <code>int</code> start offset
     * @param len <code>int</code> length
     * @return crc <code>byte[]</code>
     */
    public static byte[] getCRC(IoBuffer buf,int off, int len)
    {
        byte[] crc = new byte[2];
        CRC16 crc16 = new CRC16();
        crc16.reset();
        crc16.update(buf,off,len);
        int checkSum = crc16.getValue();
        crc=DataUtil.get2ByteToInt(checkSum);
        DataUtil.convertEndian(crc);
        return crc;
    }

    /**
     *  gzip
     *
     * @param buf <code>byte[]</code> source
     * @return output <code>byte[]</code>
     */
    public static byte[] gzip(byte[] buf) throws Exception
    {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        GZIPOutputStream gzipout = new GZIPOutputStream(bao);
        gzipout.write(buf,0,buf.length);
        gzipout.close();
        return bao.toByteArray();
    }

    /**
     *  gzip
     *
     * @param buf <code>byte[]</code> source
     * @param off <code>int</code> start offset
     * @param len <code>int</code> length
     * @return output <code>byte[]</code>
     */
    public static byte[] gzip(byte[] buf,int off, int len)
        throws Exception
    {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        GZIPOutputStream gzipout = new GZIPOutputStream(bao);
        gzipout.write(buf,off,len);
        gzipout.close();
        return bao.toByteArray();
    }

    public static byte[] unzlib(byte[] buf) throws Exception
    {
        Inflater decompresser = new Inflater();
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        decompresser.setInput(buf, 0, buf.length);
        int read = 0;
        byte[] tmpbuff = new byte[1024];
        while (!decompresser.finished()) {
            read = decompresser.inflate(tmpbuff);
            bao.write(tmpbuff, 0, read);
        }
        decompresser.end();

        return bao.toByteArray();
    }
    
    public static byte[] zlib(byte[] buf,int off, int len) throws Exception
    {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        Deflater d = new Deflater();
        DeflaterOutputStream dout = new DeflaterOutputStream(bao, d);
        dout.write(buf,off,len);
        dout.close();
        return bao.toByteArray();
    }

    /**
     * ungzip
     *
     * @param buff <code>byte[]</code> source
     * @return output <code>byte[]</code>
     */
    public static byte[] ungzip(byte[] buf) throws Exception
    {
        ByteArrayInputStream bai = new ByteArrayInputStream(buf);
        GZIPInputStream gzipin = new GZIPInputStream(bai);

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        int read = 0;
        byte[] tmpbuff = new byte[1024];
        while((read=gzipin.read(tmpbuff,0,tmpbuff.length)) > -1) {
            bao.write(tmpbuff,0,read);
        }
        return bao.toByteArray();
    }

    //private String zipEntryName = "FMPZip";
    /**
     *  zip
     *
     * @param buff <code>byte[]</code> source
     * @return output <code>byte[]</code>
     */
    public static byte[] zip(byte[] buff) throws Exception
    {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        ZipOutputStream zipout = new ZipOutputStream(bao);
        zipout.putNextEntry(new ZipEntry("test"));
        zipout.write(buff,0,buff.length);
        zipout.close();

        return bao.toByteArray();
    }

    /**
     *  zip
     *
     * @param buff <code>byte[]</code> source
     * @param off <code>int</code> start offset
     * @param len <code>int</code> length
     * @return output <code>byte[]</code>
     */
    public static byte[] zip(byte[] buff,int off, int len)
        throws Exception
    {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        ZipOutputStream zipout = new ZipOutputStream(bao);
        zipout.putNextEntry(new ZipEntry("test"));
        zipout.write(buff,off,len);
        zipout.close();

        return bao.toByteArray();
    }

    /**
     * unzip
     *
     * @param buff <code>byte[]</code> source
     * @return output <code>byte[]</code>
     */
    public static byte[] unzip(byte[] buff) throws Exception
    {
        ByteArrayInputStream bai = new ByteArrayInputStream(buff);
        ZipInputStream zipin = new ZipInputStream(bai);

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        byte[] tmpbuff = new byte[1024];
        while(zipin.getNextEntry() != null)
        {
            int read = 0;
            while((read=zipin.read(tmpbuff,0,tmpbuff.length)) > -1)
            {
                bao.write(tmpbuff,0,read);
            }
        }

        return bao.toByteArray();
    }

    /**
     *  check bit mask
     *
     * @param src <code>byte</code> source byte
     * @param test <code>byte</code> test byte
     * @return result <code>boolean</code>
     */
    public static boolean isSetBit(byte src, byte test)
    {
        byte orbit = (byte)(src & test);
        int ival = DataUtil.getIntToByte(orbit);
        if(ival > 0) {
            return true;
        }
        return false;
    }

    /**
     *  check whether control data frame attribute is setting or not
     *
     * @param attr <code>byte</code> general data frame attribute bit
     * @return result <code>boolean</code>
     */
    public static boolean isControlDataFrame(byte attr)
    {
        return isSetBit(attr,GeneralDataConstants.ATTR_FRAME);
    }

    /**
     *  check whether service data frame attribute is setting or not
     *
     * @param attr <code>byte</code> general data frame attribute bit
     * @return result <code>boolean</code>
     */
    public static boolean isServiceDataFrame(byte attr)
    {
        return !isSetBit(attr,GeneralDataConstants.ATTR_FRAME);
    }

    /**
     *  check where single frame attribute is setting or not
     *
     * @param attr <code>byte</code> general data frame attribute bit
     * @return result <code>boolean</code>
     */
    public static boolean isSingleFrame(byte attr)
    {
        if(isSetBit(attr,GeneralDataConstants.ATTR_START)
                &&isSetBit(attr,GeneralDataConstants.ATTR_END)) {
            return true;
        }
        return false;
    }

    public static boolean isPLCDataFrame(byte attr)
    {
        if(attr==PLCDataConstants.SOF) {
            return true;
        }
        return false;
    }

    public static boolean isEMnVGeneralFrame(byte[] sofByte){
        if(DataUtil.getString(sofByte).equals(EMnVConstants.General.SOF.getString())){
            return true;
        }
        return false;
    }
    
    
    /**
     *  check whether service data frame attribute is setting or not
     *
     * @param attr <code>byte</code> general data frame attribute bit
     * @return result <code>boolean</code>
     */
    public static boolean isCryptFrame(byte attr)
    {
        return isSetBit(attr,GeneralDataConstants.ATTR_CRYPT);
    }
    
    /**
     *  set bit
     *
     * @param src <code>byte</code> attribute byte
     * @param attr <code>byte</code> mask byte
     * @return result <code>byte</code>
     */
    public static byte setAttrByte(byte src,byte attr)
    {
        return (byte)(src | attr);
    }

    /**
     *  set bit
     *
     * @param src <code>byte</code> attribute byte
     * @param attr <code>byte</code> mask byte
     * @param isOr <code>boolean</code> `or` operation set
     * @return result <code>byte</code>
     */
    public static byte setAttrByte(byte src, byte attr, boolean isOr)
    {
        if(isOr) {
            return (byte)(src| attr);
        }
        else
        {
            byte andByte = (byte)(src & attr);
            byte exByte = (byte)(~andByte);
            return (byte)(src & exByte);
        }
    }

    /**
     *  splite frame to multi frame
     *
     * @param buf <code>byte[]</code> frame buffer
     * @return multi frame collection <code>ArrayList</code>
    public static ArrayList makeMultiEncodedFrame(byte[] buf)
    {
        log.debug("makeMultiEncodedFrame::FRAME_MAX_LEN["+
                GeneralDataConstants.FRAME_MAX_LEN+"]");
        log.debug("makeMultiEncodedFrame::FRAME_WINSIZE["+
                GeneralDataConstants.FRAME_WINSIZE+"]");
        int header_len = GeneralDataConstants.HEADER_LEN;
        int length_len = GeneralDataConstants.LENGTH_LEN;
        int tail_len = GeneralDataConstants.TAIL_LEN;
        ArrayList list = new ArrayList();
        if(buf.length < (GeneralDataConstants.FRAME_MAX_LEN - 2))
        {
            buf[2] = setAttrByte(buf[2],
                    GeneralDataConstants.ATTR_START);
            buf[2] = setAttrByte(buf[2],
                    GeneralDataConstants.ATTR_END);
            byte[] crc = getCRC(buf);
            byte[] bx = new byte[buf.length + crc.length];
            System.arraycopy(buf,0,bx,0,buf.length);
            System.arraycopy(crc,0,bx,buf.length,crc.length);
            list.add(bx);
            return list;
        }

        int datalen = buf.length - header_len;
        int divsize =GeneralDataConstants.FRAME_MAX_LEN -
            (header_len + tail_len);
        int maxseq = datalen / divsize;
        int remain = datalen % divsize;
        byte[] header = new byte[header_len];
        System.arraycopy(buf,0,header,0,header.length);
        byte[] tmpheader = new byte[header_len];

        byte[] lengthField = DataUtil.get4ByteToInt(divsize);
        DataUtil.convertEndian(lengthField);

        for(int seq = 0 ; seq < maxseq ; seq++)
        {
            System.arraycopy(header,0,tmpheader,0,tmpheader.length);
            tmpheader[1] = DataUtil.getByteToInt(
                    (seq%GeneralDataConstants.FRAME_MAX_SEQ));
            if(seq == 0) {
                tmpheader[2] = setAttrByte(tmpheader[2],
                    GeneralDataConstants.ATTR_START);
            } else
            {
                tmpheader[2] = setAttrByte(tmpheader[2],
                    GeneralDataConstants.ATTR_START,false);
            }

            if(remain == 0 && seq == (maxseq -1))
            {
                tmpheader[2] = setAttrByte(tmpheader[2],
                    GeneralDataConstants.ATTR_END);
            }
            else
            {
                tmpheader[2] = setAttrByte(tmpheader[2],
                    GeneralDataConstants.ATTR_END,false);
            }

            System.arraycopy(lengthField,0,tmpheader,3,
                    lengthField.length);

            byte[] bx = new byte[divsize+(header_len+tail_len)];
            System.arraycopy(tmpheader,0,bx,0,tmpheader.length);
            System.arraycopy(buf,((divsize*seq) + header_len),
                    bx,tmpheader.length,divsize);
            byte[] crc = getCRC(bx,0,(divsize+header_len));
            System.arraycopy(crc,0,bx,divsize+tmpheader.length,
                    crc.length);
            list.add(bx);
        }

        if(remain > 0)
        {
            System.arraycopy(header,0,tmpheader,0,header.length);
            tmpheader[1] = DataUtil.getByteToInt(
                    (maxseq%GeneralDataConstants.FRAME_MAX_SEQ));
            tmpheader[2] = setAttrByte(tmpheader[2],
                    GeneralDataConstants.ATTR_START,false);
            tmpheader[2] = setAttrByte(tmpheader[2],
                    GeneralDataConstants.ATTR_END);

            lengthField = DataUtil.get4ByteToInt(remain);
            DataUtil.convertEndian(lengthField);
            System.arraycopy(lengthField,0,tmpheader,3,
                    lengthField.length);

            byte[] bx = new byte[remain+(header_len+tail_len)];
            System.arraycopy(tmpheader,0,bx,0,tmpheader.length);
            System.arraycopy(buf,((divsize*maxseq) + header_len),
                    bx,tmpheader.length,remain);
            byte[] crc = getCRC(bx,0,(remain+header_len));
            System.arraycopy(crc,0,bx,remain+tmpheader.length,
                    crc.length);
            list.add(bx);
        }

        return list;
    }
    */

    /**
     *  modified by D.J Park in 2006.09.28
     *  include header and tail to multi-frame
     *  splite frame to multi frame
     *  
     *  modifed by JS in 2012.02.02
     *  include frameMaxLen and frameWinSize to multi-frame
     *
     * @param buf <code>byte[]</code> frame buffer
     * @param frameMaxLen frame buffer size
     * @param frameWinSize frame window size
     * @return multi frame collection <code>ArrayList</code>
     */
    public static ArrayList<byte[]> makeMultiEncodedFrame(byte[] buf, IoSession session){
        return makeMultiEncodedFrame(buf, session, false, buf.length);
    }
    
    public static ArrayList<byte[]> makeMultiEncodedFrame(byte[] buf, IoSession session, boolean isCompressd, int unCompressedLength) {
        log.debug("###################### [START] Partial Frame debuging을 위한 임시코드 ########");
        log.debug("IsCompressed? ["+ isCompressd +"], Uncompressd Length[" + unCompressedLength + "], HEX =[" + Hex.decode(buf) + "]");

//      int frameMaxLen = 1024;
//      int frameWinSize = 8;
//      isCompressd = false;
//      unCompressedLength = buf.length;
        
        
        int frameMaxLen = session.getAttribute("frameMaxLen") != null ? (Integer)session.getAttribute("frameMaxLen"):GeneralDataConstants.FRAME_MAX_LEN;
        int frameWinSize = session.getAttribute("frameWinSize") != null ? (Integer)session.getAttribute("frameWinSize"):GeneralDataConstants.FRAME_WINSIZE;
                
        int frameDataMaxLen = frameMaxLen - GeneralDataConstants.HEADER_LEN - GeneralDataConstants.TAIL_LEN;
        log.debug("makeMultiEncodedFrame::FRAME_MAX_LEN["+frameMaxLen+"], FRAME_DATA_MAX_LEN[" + frameDataMaxLen + "]");
        log.debug("makeMultiEncodedFrame::FRAME_WINSIZE["+frameWinSize+"]");
        boolean isPartial = (buf.length <= frameDataMaxLen ? false : true);
        log.debug("makeMultiEncodedFrame::RAW_FRAMESIZE["+buf.length+"], is Partial? = " + isPartial);
        

        /*
         *  Raw data Frame
         */
        byte[] rawDataFrame = new byte[]{};
        ArrayList<byte[]> partialFrameList = new ArrayList<>();
        buf[2] = setAttrByte(buf[2],GeneralDataConstants.ATTR_START);
        buf[2] = setAttrByte(buf[2],GeneralDataConstants.ATTR_END);
        byte[] tmpcrc = getCRC(buf);
        buf = DataUtil.append(buf, tmpcrc);

        /*
         * Compress Header make
         *  - In case partial frame, header frame include compress Header.
         */
        if(!isPartial){  // If not Partial Frame
          partialFrameList.add(buf);
          log.debug("MultiEncoded Frame. Block size=" + buf.length + ", Hex=[" + Hex.decode(buf) + "]");
        }else{
            byte[] compressHeader = new byte[]{};
            byte compressType; 
            if(isCompressd){
                compressType = GeneralDataConstants.COMP_TYPE_ZLIB; // fep는 기본적으로 zlib사용. CompressType = 1
            }else{
                compressType = GeneralDataConstants.COMP_TYPE_NONE; // CompressType = 0
            }
            
            compressHeader = DataUtil.append(new byte[]{ compressType }, DataUtil.get4ByteToInt(unCompressedLength));
            
            log.debug("*. CompressHeader - Type=["+ compressType +"], UncompressData SourceSize=[" + unCompressedLength + "], Hex=[" + Hex.decode(compressHeader) + "]");
            
            rawDataFrame = DataUtil.append(rawDataFrame, compressHeader);
            rawDataFrame = DataUtil.append(rawDataFrame, buf);
            
            /*
             * Total Data division
             *  : data list for slicing and sending
             */
            List<byte[]> dividedFrameList = new ArrayList<>();
            int remainSize = rawDataFrame.length;
            int dFrameMaxLen = frameDataMaxLen;
            int pos = 0;
            byte[] dFrame = null;
            while(0 < remainSize){
                dFrame = new byte[dFrameMaxLen];
                System.arraycopy(rawDataFrame, pos, dFrame, 0, dFrame.length);
                
                pos += dFrame.length;
                remainSize = rawDataFrame.length - pos;
                
                if(remainSize < dFrameMaxLen){
                    dFrameMaxLen = remainSize;
                }

                dividedFrameList.add(dFrame);
                log.debug(dividedFrameList.size() + ". Partial Block - size=" + dFrame.length + ", Hex=[" + Hex.decode(dFrame) + "]");
            }
            
            /*
             *  Make Partial frame list
             *  PartialDataFrame
             */
            try {
                int seq = 0;
                for(byte[]dfFrame : dividedFrameList){
                    PartialDataFrame frame = new PartialDataFrame();
                    frame.setSequence(DataUtil.getByteToInt(seq));
                    
                    if(seq == 0 && dividedFrameList.size() == 1){
                        frame.setAttrByte(GeneralDataConstants.ATTR_START);
                        frame.setAttrByte(GeneralDataConstants.ATTR_END);
                    }else if(seq == 0){
                        frame.setAttrByte(GeneralDataConstants.ATTR_START);
                    }else if(seq == (dividedFrameList.size()-1)){
                        frame.setAttrByte(GeneralDataConstants.ATTR_END);
                    }else{
                        frame.setAttrByte((byte)0x00);
                    }

                    frame.setSvc(GeneralDataConstants.SVC_P);                   
                    frame.setSvcBody(dfFrame);

                    byte[] partialFrame = frame.encode();
                    partialFrame = DataUtil.append(partialFrame, getCRC(partialFrame));
                    log.debug("PartialFrame (size=" + partialFrame.length + ")- " + seq + " = " + Hex.decode(partialFrame));
                    partialFrameList.add(partialFrame);
                    seq++;
                }
                
            } catch (Exception e) {
                log.error("PartialFrame making error - " + e, e);
            }
        }

        log.debug("Make MultiEncodedFrame. frame list size = " + partialFrameList.size());
        log.debug("###################### [STOP]Partial Frame debuging을 위한 임시코드 ########");
        
        return partialFrameList;
        
    }
    
    /**
     *  modified by D.J Park in 2006.09.28
     *  include header and tail to multi-frame
     *  splite frame to multi frame
     *
     * @param buf <code>byte[]</code> frame buffer
     * @return multi frame collection <code>ArrayList</code>
     */
    public static ArrayList<byte[]> makePartialFrame(byte[] buf)
    {
        log.debug("makeMultiEncodedFrame::FRAME_MAX_LEN["+
                GeneralDataConstants.FRAME_MAX_LEN+"]");
        log.debug("makeMultiEncodedFrame::FRAME_WINSIZE["+
                GeneralDataConstants.FRAME_WINSIZE+"]");

        int header_len = GeneralDataConstants.HEADER_LEN;
        //int length_len = GeneralDataConstants.LENGTH_LEN;
        int tail_len = GeneralDataConstants.TAIL_LEN;
        ArrayList<byte[]> list = new ArrayList<byte[]>();

        buf[2] = setAttrByte(buf[2],GeneralDataConstants.ATTR_START);
        buf[2] = setAttrByte(buf[2],GeneralDataConstants.ATTR_END);
        byte[] tmpcrc = getCRC(buf);
        byte[] tmpbx = new byte[buf.length + tmpcrc.length];
        System.arraycopy(buf,0,tmpbx,0,buf.length);
        System.arraycopy(tmpcrc,0,tmpbx,buf.length,tmpcrc.length);

        buf = null;
        buf = tmpbx;
        if(buf.length < GeneralDataConstants.FRAME_MAX_LEN)
        {
            list.add(buf);
            return list;
        }

        int datalen = buf.length;
        int divsize =GeneralDataConstants.FRAME_MAX_LEN;

        int maxseq = datalen / divsize;
        int remain = datalen % divsize;

        byte[] header = new byte[header_len];
        System.arraycopy(buf,0,header,0,header.length);
        byte[] tmpheader = new byte[header_len];

        byte[] lengthField = DataUtil.get4ByteToInt(divsize);
        DataUtil.convertEndian(lengthField);

        if (maxseq > 0) {
            int compressHeaderLen = 5;
            byte[] compressHeader = new byte[compressHeaderLen];
            compressHeader[0] = 0x00;
            byte[] bodylen = DataUtil.get4ByteToInt(datalen);
            System.arraycopy(bodylen, 0, compressHeader, 1, bodylen.length);
            byte[] bx = new byte[compressHeaderLen+datalen];
            System.arraycopy(compressHeader, 0, bx, 0, compressHeader.length);
            System.arraycopy(buf, 0, bx, compressHeaderLen, buf.length);
            
            buf = null;
            buf = bx;
        }
        
        for(int seq = 0 ; seq < maxseq ; seq++)
        {
            System.arraycopy(header,0,tmpheader,0,tmpheader.length);
            tmpheader[1] = DataUtil.getByteToInt(
                    (seq%GeneralDataConstants.FRAME_MAX_SEQ));
            if(seq == 0) {
                tmpheader[2] = setAttrByte(tmpheader[2],
                    GeneralDataConstants.ATTR_START);
            } else
            {
                tmpheader[2] = setAttrByte(tmpheader[2],
                    GeneralDataConstants.ATTR_START,false);
            }
            
            if(remain == 0 && seq == (maxseq -1))
            {
                tmpheader[2] = setAttrByte(tmpheader[2],
                    GeneralDataConstants.ATTR_END);
            }
            else
            {
                tmpheader[2] = setAttrByte(tmpheader[2],
                    GeneralDataConstants.ATTR_END,false);
            }

            boolean isStartFrame = 
                    FrameUtil.isSetBit(tmpheader[2],GeneralDataConstants.ATTR_START);
                boolean isEndFrame = 
                    FrameUtil.isSetBit(tmpheader[2],GeneralDataConstants.ATTR_END);
            
            if(!(isStartFrame && isEndFrame)){
                tmpheader[7] = GeneralDataConstants.SVC_P;
            }
            
            System.arraycopy(lengthField,0,tmpheader,3,
                    lengthField.length);

            byte[] bx = new byte[divsize+(header_len+tail_len)];
            System.arraycopy(tmpheader,0,bx,0,tmpheader.length);
            System.arraycopy(buf,divsize*seq,bx,tmpheader.length,divsize);
            byte[] crc = getCRC(bx,0,(divsize+header_len));
            System.arraycopy(crc,0,bx,divsize+tmpheader.length,
                    crc.length);
            list.add(bx);
        }

        if(remain > 0)
        {
            System.arraycopy(header,0,tmpheader,0,header.length);
            tmpheader[1] = DataUtil.getByteToInt(
                    (maxseq%GeneralDataConstants.FRAME_MAX_SEQ));
            tmpheader[2] = setAttrByte(tmpheader[2],
                    GeneralDataConstants.ATTR_START,false);
            tmpheader[2] = setAttrByte(tmpheader[2],
                    GeneralDataConstants.ATTR_END);

            boolean isStartFrame = 
                    FrameUtil.isSetBit(tmpheader[2],GeneralDataConstants.ATTR_START);
                boolean isEndFrame = 
                    FrameUtil.isSetBit(tmpheader[2],GeneralDataConstants.ATTR_END);
            
            if(!(isStartFrame && isEndFrame)){
                tmpheader[7] = GeneralDataConstants.SVC_P;
            }
            
            lengthField = DataUtil.get4ByteToInt(remain);
            DataUtil.convertEndian(lengthField);
            System.arraycopy(lengthField,0,tmpheader,3,
                    lengthField.length);

            byte[] bx = new byte[remain+(header_len+tail_len)];
            System.arraycopy(tmpheader,0,bx,0,tmpheader.length);
            System.arraycopy(buf,divsize*maxseq,bx,tmpheader.length,remain);
            byte[] crc = getCRC(bx,0,(remain+header_len));
            System.arraycopy(crc,0,bx,remain+tmpheader.length,
                    crc.length);
            list.add(bx);
        }

        return list;
    }

    /**
     *  get request command  target identification
     *
     * @return tid <code>BYTE</code>
     */
    public static BYTE getCommandTid()
    {
        return new BYTE((tid++)%255);
    }

    /**
     *  get sequence from ACK ControlDataFrame
     *
     * @param cdf <code>ControlDataFrame</code> ACK
     * @return sequence <code>int</code>
     */
    public static int getAckSequence(ControlDataFrame cdf)
    {
        int seq = 0;
        try
        {
            OCTET arg = cdf.getArg();
            seq = DataUtil.getIntToByte(arg.getValue()[0]);
        }catch(Exception ex) { }
        return seq;
    }

    /**
     *  get sequence from NAK ControlDataFrame
     *
     * @param cdf <code>ControlDataFrame</code> NAK
     * @return sequences <code>int[]</code>
     */
    public static int[] getNakSequence(ControlDataFrame cdf)
    {
        int[] seq = new int[0];

        try
        {
            OCTET arg = cdf.getArg();
            byte[] bx = arg.getValue();
            seq = new int[bx.length];
            for(int i = 0 ; i < bx.length ; i++) {
                seq[i] = DataUtil.getIntToByte(bx[i]);
            }
        }catch(Exception ex) { }

        return seq;
    }


    /**
     *  wait between multi frmaes
     */
    public static void waitSendFrameInterval()
    {
        try {
            log.debug("Waiting after frame send ... " + GeneralDataConstants.WAITTIME_SEND_FRAMES + " milliseconds");
            Thread.sleep(GeneralDataConstants.WAITTIME_SEND_FRAMES);
        }catch(Exception ex)
        {
            log.error("WaitSendFrameInteval error - " + ex.getMessage(), ex);
        }
    }

    /**
     *  wait after send frame
     */
    public static void waitAfterSendFrame()
    {
        try {
            Thread.sleep(
                    GeneralDataConstants.WAITTIME_AFTER_SEND_FRAME);
        }catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * get ByteBuffer for String
     */
    public static IoBuffer getByteBuffer(String str)
    {
        log.debug("FrameUtil::getByteBuffer["+str+"]");
        IoBuffer buff = IoBuffer.allocate(str.length());

        buff.put(str.getBytes());

        buff.flip();

        return buff;
    }

    /**
     * Check is AMU General Data Frame
     *
     * @param attr
     * @return boolean
     */
    public static boolean isAmuGeneralDataFrame(byte attr)
    {
        if(attr==AMUGeneralDataConstants.SOH) {
            return true;
        }
        return false;
    }

    /**
     * get AMU Service Type
     * @param frameType
     * @return
     */
    public static int getAmuServiceType(byte frameType){

        if(frameType == AMUGeneralDataConstants.FRAMETYPE_EVENT){
            return 2;
        }else if(frameType == AMUGeneralDataConstants.FRAMETYPE_METERING){
            return 3;
        }else{
            log.equals("Can't Not Fount AMU Service Type ");
        }
        return 0;

    }
    
    /**
     * get Authentication Key 
     * @param destAddrType
     * @param destAddr
     * @param code
     * @return
     */
    public static byte[] getAuthenticationKey(byte destAddrType, byte[] destAddr, String code){
        byte[] key = new byte[16];
        log.debug(code);
        
        // miuType 에 따른 dest Address -> 16 byte로 변환
        byte[] addr  = get16ByteAddress(destAddr , destAddrType);
        
        byte[] aCode = new byte[16];
        
        for(int i=0; i<code.length(); i++){
            aCode[i] =(byte)(code.charAt(i) & 0xff);
        }
        log.debug("aCode :"+Hex.decode(aCode));
        for(int i=0; i<key.length; i++){
            key[i] = (byte)((addr[i] ^ aCode[i]) & 0xff);
        }
        
        log.debug("AuthenticationKey : "+Hex.decode(key));
        return key;
    }   
    
    /**
     * get 16Byte Address
     * @param destAddr
     * @param destAddrType
     * @return
     */
    public static byte[] get16ByteAddress(byte[] destAddr , byte destAddrType){
        
        /* 
         * Authentication code 생성을 위한  XOR 과정에서 
         * Address size가 Key size와 맞지 않는 부분은 아래와 같이 처리하여 연산 
         * 1. IP Address        일 경우 : 4Bytes address를 4번 중복하여 사용
         * 2. EUI64ID Address   일 경우 : 8Bytes address를 2번 중복하여 사용
         * 3. BCD Mobile number 일 경우 : 6Bytes address를 2번 중복하고 남은 4Bytes는  0xFF로 채움
         */
        byte[] reAddr   = new byte[]{(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff,
                (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff,
                (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff,
                (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff };
        
        byte[] tmp  =  new byte[]{(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff};
        
        int pos =0;
        
        switch(destAddrType){
        case AMUGeneralDataConstants.ATTR_ADDRTYPE_NONE: 
            log.debug("Dest . Address Not Used!"); 
        break; 
        case AMUGeneralDataConstants.ATTR_ADDRTYPE_IP: // IP Address
            for(int i =0 ; i < 4 ; i ++){
                System.arraycopy(destAddr, 0, reAddr, pos, destAddr.length);
                pos += destAddr.length;
            }
             break;
        case AMUGeneralDataConstants.ATTR_ADDRTYPE_EUI64: // EUI64 ID
            for(int i =0 ; i < 2 ; i ++){
                System.arraycopy(destAddr, 0, reAddr, pos, destAddr.length);
                pos += destAddr.length;
            }
             break;
        case AMUGeneralDataConstants.ATTR_ADDRTYPE_MOBILE: // Mobile Number
            for(int i =0 ; i < 2 ; i ++){
                System.arraycopy(destAddr, 0, reAddr, pos, destAddr.length);
                pos += destAddr.length;
            }
            System.arraycopy(tmp, 0, reAddr, pos, tmp.length);
            break;
        default :
            log.error("Length of Dest Address is not 4 , 8 , 6");
        }
        
        log.debug("16 byte Address : " + Hex.decode(reAddr));
        return reAddr;
    }
    
    /**
     * get Mcu Id 
     * if mcu is mobile number , front 5 split
     * @param sourceType
     * @param mcuId
     * @return
     */
    public static byte[] getMcuID(byte destType , byte[] destAddr){
        
        try{
            byte[] mobileMcuId = null;
            if(destType == AMUGeneralDataConstants.ATTR_ADDRTYPE_MOBILE){
                mobileMcuId = DataFormat.select(destAddr, 2, AMUGeneralDataConstants.ADDRTYPE_MOBILE_LEN);
            }
            return mobileMcuId;
        }catch(Exception e){
            log.debug("get MCU ID Failed!");
        }
        
        return destAddr;
    }
    
    /**
     * get 8Byte MCU ID
     * Command ( mcuID of Link Frame )
     * @param mcuId
     * @return
     */
    public static byte[] get8BytMcuId(byte destAddrType , byte[] destAddress) throws Exception{
        
        byte[] tmpMcuId = new byte[]{(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff,
                                     (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff};
             
        switch(destAddrType){
        case (byte)0x00:    
                    break;
        case (byte)0x01:    // 4 byte
                    System.arraycopy(destAddress, 0, tmpMcuId, 0 , AMUGeneralDataConstants.ADDRTYPE_IP_LEN);
                    break;
        case (byte)0x02:    // 8 byte
                    System.arraycopy(destAddress, 0, tmpMcuId, 0 , AMUGeneralDataConstants.ADDRTYPE_EUI64_LEN);
                    break;
        case (byte)0x03:    // 6 byte
                    System.arraycopy(destAddress, 0, tmpMcuId, 0 , AMUGeneralDataConstants.ADDRTYPE_MOBILE_LEN);
                    break;
        default :
            log.error("Can't Fount Dest Address Type, get 8byte MCU ID Failed");
        }
        return tmpMcuId;
    }
    
    /**
     * AMU Sesssion Setting init
     * @param session
     */
    public static void amuSessionInit(IoSession session){
        session.removeAttribute(AMUGeneralDataConstants.PENDING_FRAME);
        session.removeAttribute(AMUGeneralDataConstants.MCUID);
        session.removeAttribute(AMUGeneralDataConstants.METERING_POOL);
    }
}
