package com.aimir.fep.protocol.fmp.frame.service;

import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.exception.FMPDecodeException;
import com.aimir.fep.protocol.fmp.exception.FMPEncodeException;
import com.aimir.fep.protocol.fmp.frame.GeneralDataConstants;
import com.aimir.fep.protocol.fmp.frame.ServiceDataConstants;
import com.aimir.fep.protocol.fmp.frame.ServiceDataFrame;
import com.aimir.fep.util.CodecUtil;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.nio.ByteBuffer;
import java.util.StringTokenizer;

/**
 * Abstrace Service Data Class
 *
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public abstract class ServiceData implements java.io.Serializable
{
    private String mcuId = "";
    private String ipAddr = "";
    private byte svc = 0x00;
    private int totalLength = 0;
    private int uncompressedTotalLength = 0;
    private static Log log = LogFactory.getLog(ServiceData.class);
    private String ns = "";

    /**
     * encode
     *
     * @return result <code>byte[]</code>
     * @throws com.aimir.fep.protocol.fmp.exception.FMPEncodeException 
     */
    public byte[] encode() throws FMPEncodeException
    {
        try
        {
            byte[] bx = CodecUtil.pack(this);
            return bx;
        }catch(Exception ex)
        {
            log.error("ServiceData::encode failed :",ex);
            throw new FMPEncodeException(
                    "ServiceData::encode failed :"+ex.getMessage());
        }
    }

    /**
     * set MCU ID
     *
     * @param mcuId <code>String</code> MCU ID
     */
    public void setMcuId(String mcuId)
    {
        this.mcuId = mcuId;
    }
    /**
     * get MCU ID
     *
     * @return mcuId <code>String</code> MCU ID
     */
    public String getMcuId()
    {
        return this.mcuId;
    }

    public void setIpAddr(String ipAddr)
    {
        this.ipAddr = ipAddr;
    }

    public String getIpAddr()
    {
        return this.ipAddr;
    }

    /**
     * Service Data Object Type
     *
     * @return type <code>String</code> Service Data Object Type
     */
    public final String getType()
    {
        String type = null;
        Class clazz = this.getClass();
        while(clazz != null)
        {
            String className = clazz.getName();
            int idx = className.lastIndexOf(".");
            if(type!= null)
                type = className.substring(idx+1) + "."+type;
            else
                type = className.substring(idx+1);
            if(clazz == ServiceData.class)
                break;
            clazz=clazz.getSuperclass();
        }
        return type;
    }

    public byte getSvc() {
        return svc;
    }
    
    public void setSvc(byte svc) {
        this.svc = svc;
    }
    
    public int getTotalLength() { return this.totalLength; }
    public void setTotalLength(int data) {
        this.totalLength = data; }

    public int getUncompressedTotalLength()
    {
        return uncompressedTotalLength;
    }

    public void setUncompressedTotalLength(int uncompressedTotalLength)
    {
        this.uncompressedTotalLength = uncompressedTotalLength;
    }

    /**
     * Decode
     *
     * @param sdf <code>ServiceDataFrame</code> ServiceDataFrame
     * @return data <code>ServiceData</code> ServiceData
     * @throws Exception
     */
    public static ServiceData decode(ServiceDataFrame sdf, String ipAddr)
        throws Exception
    {
        return decode(null, sdf, ipAddr);
    }
    
    /**
     * Decode
     *
     * @param sdf <code>ServiceDataFrame</code> ServiceDataFrame
     * @return data <code>ServiceData</code> ServiceData
     * @throws Exception
     */
    public static ServiceData decode(String ns, ServiceDataFrame sdf, String ipAddr)
        throws Exception
    {
        log.info("ServiceData::decode["+((char)sdf.getSvc())+"]");
        
        if(sdf.getSvc() == GeneralDataConstants.SVC_C)
        {
            byte[] bx = sdf.getSvcBody();
            try{
                CommandData cd = (CommandData)CodecUtil.unpack(
                        ns,CommandData.class.getName(),bx);
                cd.setSvc(sdf.getSvc());
                cd.setNS(ns);
                
                int pos = 0;
                int len = CodecUtil.sizeOf(cd);
                int datalen = cd.getCnt().getValue();
                //log.debug("Command["+cd.toString()+"]");
                cd.setMcuId(sdf.getMcuId().toString());
                cd.setTotalLength(GeneralDataConstants.HEADER_LEN
                        + GeneralDataConstants.TAIL_LEN
                        + ServiceDataConstants.HEADER_LEN
                        + sdf.getRcvBodyLength());
                cd.setUncompressedTotalLength(GeneralDataConstants.HEADER_LEN
                        + GeneralDataConstants.TAIL_LEN
                        + ServiceDataConstants.HEADER_LEN
                        + bx.length);
                //log.debug("bx.length["+bx.length+"] len["+len+"]");
                for(int i = 0 ; i < datalen ; i++)
                {
                    pos = len;
                    SMIValue smiValue = new SMIValue();
                    len+=smiValue.decode(ns,bx,pos);
                    log.debug("smiValue["+smiValue.toString()+"]");
                    cd.append(smiValue);
                }
                return cd;
            }catch(Exception ex)
            {
                log.error("decode failed : " , ex);
                throw new FMPDecodeException("SeviceData::decode "
                        +" failed :"+ ex.getMessage());
            }
        }
        else if(sdf.getSvc() == GeneralDataConstants.SVC_M ||
                sdf.getSvc() == GeneralDataConstants.SVC_S ||
                sdf.getSvc() == GeneralDataConstants.SVC_G)
        {
            byte[] bx = sdf.getSvcBody();

            try
            {
                MDData md = (MDData)
                    CodecUtil.unpack(ns,MDData.class.getName(),bx);
                md.setSvc(sdf.getSvc());
                md.setNS(ns);
                
                if(sdf.getMcuId().toString().equals("0")){
                    // 2011.08.24 by elevas 집중기 번호가 없는 경우 모뎀 시리얼 번호로 대체
                    byte[] b = new byte[8];
                    System.arraycopy(bx, 2, b, 0, b.length);
                    md.setMcuId(Hex.decode(b));
                    /*
                	String temp = new String(bx,50,20);//after kamstrup gprs mmiu version, modem number length changed to 20
                	StringTokenizer st = new StringTokenizer(temp, new String(new byte[]{0x00}));
                	if(st.hasMoreTokens()){
                		md.setMcuId(((String)st.nextToken()).trim());
                	}else{
                		md.setMcuId("0");
                	}
                	*/

                }else{
                    md.setMcuId(sdf.getMcuId().toString());
                }

                md.setTotalLength(GeneralDataConstants.HEADER_LEN
                        + GeneralDataConstants.TAIL_LEN
                        + ServiceDataConstants.HEADER_LEN
                        + sdf.getRcvBodyLength());
                md.setUncompressedTotalLength(GeneralDataConstants.HEADER_LEN
                        + GeneralDataConstants.TAIL_LEN
                        + ServiceDataConstants.HEADER_LEN
                        + bx.length);
                byte[] bxx = new byte[bx.length-2];
                System.arraycopy(bx,2,bxx,0,bxx.length);
                md.setMdData(bxx);
                return md;
            }catch(Exception ex)
            {
                log.error("decode failed : " , ex);
                throw new FMPDecodeException("SeviceData::decode "
                        +" failed :"+ ex.getMessage());
            }
        }
        else if(sdf.getSvc() == GeneralDataConstants.SVC_N)
        {
            byte[] bx = sdf.getSvcBody();
            try
            {
                NDData md = (NDData)
                    CodecUtil.unpack(ns,NDData.class.getName(),bx);
                md.setSvc(sdf.getSvc());
                md.setNS(ns);
                
                if(sdf.getMcuId().toString().equals("0")){
                	String temp = new String(bx,50,20);//after kamstrup gprs mmiu version, modem number length changed to 20
                	StringTokenizer st = new StringTokenizer(temp, new String(new byte[]{0x00}));
                	if(st.hasMoreTokens()){
                		md.setMcuId(((String)st.nextToken()).trim());
                	}else{
                		md.setMcuId("0");
                	}
                }else{
                    md.setMcuId(sdf.getMcuId().toString());
                }
                md.setTotalLength(GeneralDataConstants.HEADER_LEN
                        + GeneralDataConstants.TAIL_LEN
                        + ServiceDataConstants.HEADER_LEN
                        + sdf.getRcvBodyLength());
                md.setUncompressedTotalLength(GeneralDataConstants.HEADER_LEN
                        + GeneralDataConstants.TAIL_LEN
                        + ServiceDataConstants.HEADER_LEN
                        + bx.length);
                byte[] bxx = new byte[bx.length-2];
                System.arraycopy(bx,2,bxx,0,bxx.length);
                md.setMdData(bxx);
                return md;
            }catch(Exception ex)
            {
                log.error("decode failed : " , ex);
                throw new FMPDecodeException("SeviceData::decode "
                        +" failed :"+ ex.getMessage());
            }
        }
        else if(sdf.getSvc() == GeneralDataConstants.SVC_A)
        {
            byte[] bx = sdf.getSvcBody();
            try {
                AlarmData ad = (AlarmData)CodecUtil.unpack(
                        ns,AlarmData.class.getName(),bx);
                ad.setSvc(sdf.getSvc());
                ad.setNS(ns);
                
                ad.setMcuId(sdf.getMcuId().toString());
                ad.setTotalLength(GeneralDataConstants.HEADER_LEN
                        + GeneralDataConstants.TAIL_LEN
                        + ServiceDataConstants.HEADER_LEN
                        + sdf.getRcvBodyLength());
                ad.setUncompressedTotalLength(GeneralDataConstants.HEADER_LEN
                        + GeneralDataConstants.TAIL_LEN
                        + ServiceDataConstants.HEADER_LEN
                        + bx.length);
                return ad;
            }catch(Exception ex)
            {
                log.error("decode failed : " + ex);
                throw new FMPDecodeException("SeviceData::decode "
                        +" failed :"+ ex.getMessage());
            }
        }
        else if(sdf.getSvc() == GeneralDataConstants.SVC_E)
        {
            byte[] bx = sdf.getSvcBody();
            try{
                EventData ed = (EventData)CodecUtil.unpack(
                        ns,EventData.class.getName(),bx);
                ed.setSvc(sdf.getSvc());
                
                ed.setMcuId(sdf.getMcuId().toString());
                ed.setTotalLength(GeneralDataConstants.HEADER_LEN
                        + GeneralDataConstants.TAIL_LEN
                        + ServiceDataConstants.HEADER_LEN
                        + sdf.getRcvBodyLength());
                ed.setUncompressedTotalLength(GeneralDataConstants.HEADER_LEN
                        + GeneralDataConstants.TAIL_LEN
                        + ServiceDataConstants.HEADER_LEN
                        + bx.length);
                ed.setIpAddr(ipAddr);
                ed.setNS(ns);

                int datalen = ed.getCnt().getValue();
                int pos = 0;
                int len = CodecUtil.sizeOf(ed);
                for(int i = 0 ; i < datalen ; i++)
                {
                    pos=len;
                    SMIValue smiValue = new SMIValue();
                    len+=smiValue.decode(ns,bx,pos);
                    ed.append(smiValue);
                }
                
                if(ns != null && !"".equals(ns)){
                	EventData_1_2 ed2 = new EventData_1_2();
                	ed2.setNameSpace(new OCTET(ns));
                	ed2.setSvc(ed.getSvc());
                	ed2.setMcuId(ed.getMcuId());
                	ed2.setTotalLength(ed.getTotalLength());
                	ed2.setUncompressedTotalLength(ed.getUncompressedTotalLength());
                	ed2.setIpAddr(ipAddr);
                	for(SMIValue smiValue : ed.getSMIValue()){
                		ed2.append(smiValue);                		
                	}
                	ed2.setSrcType(ed.getSrcType());
                	ed2.setSrcId(ed.getSrcId());
                	ed2.setCnt(ed.getCnt());
                	ed2.setOid(ed.getCode());
                	ed2.setTimeStamp(ed.getTimeStamp());
                	return ed2;
                }else{
                    return ed;
                }

            }catch(Exception ex)
            {
                log.error(ex, ex);
                throw new FMPDecodeException("SeviceData::decode "
                        +" failed :"+ ex.getMessage());
            }
        }
        else if(sdf.getSvc() == GeneralDataConstants.SVC_B)
        {
            byte[] bx = sdf.getSvcBody();
            try{
                EventData_1_2 ed = (EventData_1_2)CodecUtil.unpack(
                        ns,EventData_1_2.class.getName(),bx);
                ed.setSvc(sdf.getSvc());
                
                ed.setMcuId(sdf.getMcuId().toString());
                ed.setTotalLength(GeneralDataConstants.HEADER_LEN
                        + GeneralDataConstants.TAIL_LEN
                        + ServiceDataConstants.HEADER_LEN
                        + sdf.getRcvBodyLength());
                ed.setUncompressedTotalLength(GeneralDataConstants.HEADER_LEN
                        + GeneralDataConstants.TAIL_LEN
                        + ServiceDataConstants.HEADER_LEN
                        + bx.length);
                ed.setIpAddr(ipAddr);

                String nameSpace = ed.getNameSpace().toString();
                int datalen = ed.getCnt().getValue();
                int pos = 0;
                int len = CodecUtil.sizeOf(ed);
                for(int i = 0 ; i < datalen ; i++)
                {
                    pos=len;
                    SMIValue smiValue = new SMIValue();
                    len+=smiValue.decode(nameSpace,bx,pos);
                    ed.append(smiValue);
                }
                return ed;
            }catch(Exception ex)
            {
                log.error("decode failed : " + ex);
                throw new FMPDecodeException("SeviceData::decode "
                        +" failed :"+ ex.getMessage());
            }
        }
        else if(sdf.getSvc() == GeneralDataConstants.SVC_D)
        {
            byte[] bx = sdf.getSvcBody();
            
            try
            {
                DFData md = (DFData)CodecUtil.unpack(ns,DFData.class.getName(),bx);
                md.setSvc(sdf.getSvc());
                md.setNS(ns);
                
                md.setMcuId(sdf.getMcuId().toString());

                md.setTotalLength(GeneralDataConstants.HEADER_LEN
                        + GeneralDataConstants.TAIL_LEN
                        + ServiceDataConstants.HEADER_LEN
                        + sdf.getRcvBodyLength());
                md.setUncompressedTotalLength(GeneralDataConstants.HEADER_LEN
                        + GeneralDataConstants.TAIL_LEN
                        + ServiceDataConstants.HEADER_LEN
                        + bx.length);
                
                byte[] mcuId = DataUtil.get4ByteToInt(sdf.getMcuId().getValue());
                DataUtil.convertEndian(mcuId);
                ByteBuffer bb = ByteBuffer.allocate(mcuId.length + bx.length);
                bb.put(mcuId);
                bb.put(bx);
                md.setDfData(bb.array());
                return md;
            }catch(Exception ex)
            {
                log.error("decode failed : " , ex);
                throw new FMPDecodeException("SeviceData::decode "
                        +" failed :"+ ex.getMessage());
            }
        }
        else if(sdf.getSvc() == GeneralDataConstants.SVC_L)
        {
            log.info("ServiceData::decode "
                    +" Log Service will be supported later");
        }
        else if(sdf.getSvc() == GeneralDataConstants.SVC_F)
        {
            log.info("ServiceData::decode "
                    +" File Service will be supported later");
        }
        else if(sdf.getSvc() == GeneralDataConstants.SVC_R)
        {
            byte[] bx = sdf.getSvcBody();

            try
            {
                RMDData rd = (RMDData)
                    CodecUtil.unpack(ns,RMDData.class.getName(),bx);
                rd.setSvc(sdf.getSvc());
                rd.setMcuId(sdf.getMcuId().toString());
                rd.setNS(ns);
                
                rd.setTotalLength(GeneralDataConstants.HEADER_LEN
                        + GeneralDataConstants.TAIL_LEN
                        + ServiceDataConstants.HEADER_LEN
                        + sdf.getRcvBodyLength());
                rd.setUncompressedTotalLength(GeneralDataConstants.HEADER_LEN
                        + GeneralDataConstants.TAIL_LEN
                        + ServiceDataConstants.HEADER_LEN
                        + bx.length);
                byte[] bxx = new byte[bx.length-2];
                System.arraycopy(bx,2,bxx,0,bxx.length);
                rd.setrData(bxx);
                return rd;
            }catch(Exception ex)
            {
                log.error("decode failed : " , ex);
                throw new FMPDecodeException("SeviceData::decode "
                        +" failed :"+ ex.getMessage());
            }
        }
        else
        {
            log.warn("ServiceData::decode "
                    + "Unsupported Service");
            throw new Exception("ServiceData::decode "
                    + "Unsupported Service");
        }

        return null;
    }

    public String getNS() {
        return ns;
    }

    public void setNS(String ns) {
        if (ns == null) this.ns = "";
        else this.ns = ns;
    }
    
}
