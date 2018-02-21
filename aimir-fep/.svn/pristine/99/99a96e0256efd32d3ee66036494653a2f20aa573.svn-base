package com.aimir.fep.meter.parser;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.Instrument;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.data.MeterTimeSyncData;
import com.aimir.fep.meter.data.MeteringFail;
import com.aimir.fep.meter.data.TOU_BLOCK;
import com.aimir.fep.meter.parser.SM110Table.AT055;
import com.aimir.fep.meter.parser.SM110Table.BT055;
import com.aimir.fep.meter.parser.SM110Table.MT000;
import com.aimir.fep.meter.parser.SM110Table.MT067;
import com.aimir.fep.meter.parser.SM110Table.MT070;
import com.aimir.fep.meter.parser.SM110Table.MT075;
import com.aimir.fep.meter.parser.SM110Table.MT078;
import com.aimir.fep.meter.parser.SM110Table.MT113;
import com.aimir.fep.meter.parser.SM110Table.NT023;
import com.aimir.fep.meter.parser.SM110Table.NT025;
import com.aimir.fep.meter.parser.SM110Table.NT026;
import com.aimir.fep.meter.parser.SM110Table.NT067;
import com.aimir.fep.meter.parser.SM110Table.NT078;
import com.aimir.fep.meter.parser.SM110Table.ST001;
import com.aimir.fep.meter.parser.SM110Table.ST003;
import com.aimir.fep.meter.parser.SM110Table.ST005;
import com.aimir.fep.meter.parser.SM110Table.ST012;
import com.aimir.fep.meter.parser.SM110Table.ST021;
import com.aimir.fep.meter.parser.SM110Table.ST022;
import com.aimir.fep.meter.parser.SM110Table.ST023Short;
import com.aimir.fep.meter.parser.SM110Table.ST025Short;
import com.aimir.fep.meter.parser.SM110Table.ST026;
import com.aimir.fep.meter.parser.SM110Table.ST055;
import com.aimir.fep.meter.parser.SM110Table.ST061;
import com.aimir.fep.meter.parser.SM110Table.ST062;
import com.aimir.fep.meter.parser.SM110Table.ST063;
import com.aimir.fep.meter.parser.SM110Table.ST064Short;
import com.aimir.fep.meter.parser.SM110Table.ST071;
import com.aimir.fep.meter.parser.SM110Table.ST072;
import com.aimir.fep.meter.parser.SM110Table.ST076;
import com.aimir.fep.meter.parser.SM110Table.ST130;
import com.aimir.fep.meter.parser.SM110Table.ST132;
import com.aimir.fep.meter.parser.SM110Table.UNIT_OF_MTR;
import com.aimir.fep.meter.parser.SM110Table.ST025;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.Util;
import com.aimir.model.system.Supplier;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;

/**
 * 검침 데이터 업로드 주기를 1시간으로 바꿈으로 인해 GE 검침 데이터의
 * S023과 S025가 각각 13byte와 19byte로 줄어듬
 * 이를 반영한 파서(제주도에서 사용)
 * parsing SM110 Meter Data
 * implemented in Autrailia
 *
 * @author Yeon Kyoung Park (goodjob@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2006-12-14 12:00:15 +0900 $,
 */
public class SM110Short extends SM110 implements java.io.Serializable
{
	private static final long serialVersionUID = 3861913823968873895L;
	private static Log log = LogFactory.getLog(SM110Short.class);
    private byte[] rawData = null;
    private int lpcount;
    private Double meteringValue = null;
    private String meterId = null;
    private int flag = 0;

    private byte[] s001 = null;
    private byte[] s003 = null;
    private byte[] s005 = null;
    private byte[] s012 = null;
    private byte[] s021 = null;
    private byte[] s022 = null;
    private byte[] s023 = null;
    private byte[] s025 = null;
    private byte[] s026 = null;
    private byte[] s055 = null;
    private byte[] s061 = null;
    private byte[] s062 = null;
    private byte[] s063 = null;
    private byte[] s064 = null;
    private byte[] s071 = null;
    private byte[] s072 = null;
    private byte[] s076 = null;
    private byte[] s130 = null;
    private byte[] s132 = null;
    private byte[] m000 = null;
    private byte[] m067 = null;
    private byte[] m070 = null;
    private byte[] m075 = null;
    private byte[] m078 = null;
    private byte[] m113 = null;
    private byte[] t001 = null;
    private byte[] t002 = null;
    private byte[] b055 = null;
    private byte[] a055 = null;
    private byte[] n055 = null;
    private byte[] n023 = null;
    private byte[] n025 = null;
    private byte[] n026 = null;
    private byte[] n067 = null;
    private byte[] n078 = null;

    private ST001 st001 = null;
    private ST003 st003 = null;
    private ST005 st005 = null;
    private ST012 st012 = null;
    private ST021 st021 = null;
    private ST022 st022 = null;
    private ST023Short st023 = null;
    private ST025Short st025 = null;
    private ST026 st026 = null;
    private ST055 st055 = null;
    private ST061 st061 = null;
    private ST062 st062 = null;
    private ST071 st071 = null;
    private ST072 st072 = null;
    private ST076 st076 = null;
    private ST063 st063 = null;
    private ST064Short st064 = null;
    private ST130 st130 = null;
    private ST132 st132 = null;

    private MT000 mt000 = null;
    private MT067 mt067 = null;
    private MT070 mt070 = null;
    private MT075 mt075 = null;
    private MT078 mt078 = null;
    private MT113 mt113 = null;

    private NURI_T001 nuri_t001 = null;
    private NURI_T002 nuri_t002 = null;

    private BT055 bt055 = null;
    private AT055 at055 = null;
    private NT055 nt055 = null;

    private NT023 nt023 = null;
    private NT025 nt025 = null;
    private NT026 nt026 = null;
    private NT067 nt067 = null;
    private NT078 nt078 = null;

    /**
     * constructor
     */
    public SM110Short()
    {
    }
    @Test
    public void test() throws Exception {
    	parse(Hex.encode("533030300061020A8A474520200200080C0100110F030B0104FFF9E19F0300F8E081190000000000001401000000000000007C5C340006060279C6030F00000000000000DF5A30E0F8E1030300686080080000020000000800000000000000005C14140002020053303031002047452020534D313130202020010601022032303037303132393131393333393953303033000501000000005330303500142020202020202020202020202020363932343339533031310008043E05023E16023E5330313200F8004002000080010000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000450200008501000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000044020000840100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000053303231000A5F100205050A0104190053303232001F0001FFFFFF2829FFFFFF1FFFFFFFFFFFFFFFFFFFFF0000010102020303040453303233000D01DB30F20900000000000000005330323500130B0419000000000000000000000000000000005330323605BE0202013B00020B060C000000010434910900000000000000000000000000000000000000000000000000000B060A121E0000000000000C630B00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000434910900000000000000000000000000000000000000000000000000000B060A121E0000000000000C630B00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000B060D00000001F661E20900000000000000000000000000000000000000000000000000000B060A121E0000000000000C630B0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000F661E20900000000000000000000000000000000000000000000000000000B060A121E0000000000000C630B00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000005330353500090B060D05060041010F53303631000D26A2000050041046006000020F53303632000F0114000115011001000100C800C80053303633000D22460045004A000000460030005330363402510B060D0500C1F5F10900000000000000000000F20000000000BB0000000000E700000000006E00000000005100000000007100000000005C00000000005A0000000000AE0000000000D90100000000990100000000C00100000000F301000000009C01000000004E0100000000BB01000000009001000000005B0100000000D301000000007801000000006401000000005D01000000003601000000005201000000009201000000004401000000004F0100000000190100000000E800000000002F0100000000FB0000000000EF0000000000350100000000EA0000000000FB0000000000020100000000DD0000000000220100000000F50000000000E400000000000C0100000000C30000000000EE0000000000340100000000F500000000002B0100000000FF0000000000EC000000FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF53313330000201004D303730002F02000000000100000033336000000000000000000000000000000000000000000000000000000000202020202020204D303735000C3617B030DD7001C40900C409"));
    }
    /**
     * get Metering Value
     */
    public Double getMeteringValue()
    {
    	TOU_BLOCK[] curr = getCurrBilling();
    	ArrayList list = getSelfReads();

    	if(curr != null && curr.length > 0){
    		this.meteringValue = (Double) curr[0].getSummation(0);
    	}else {
    		if(list!= null && list.size() > 0){ //제일 마지막 self read 날짜의 total값을 찾아서 넣어줌 오름차순으로 데이터가 옴
            	TOU_BLOCK[] tou_day = (TOU_BLOCK[]) list.get(list.size()-1);
                if(tou_day != null && tou_day.length > 0){
                	this.meteringValue = (Double) tou_day[0].getSummation(0);//해당날짜의 total사용량
                	log.debug("tou event time: "+tou_day[0].getEventTime(0)+" tou sum:"+meteringValue);
                }
    		}
    	}
        return this.meteringValue;
    }

    /**
     * get meter id
     * @return meter id
     */
    public String getMeterId()
    {
        return meterId;
    }

    /**
     * get raw Data
     */
    public byte[] getRawData()
    {
        return this.rawData;
    }

    public int getLength()
    {
        return this.rawData.length;
    }

    public int getLPCount(){
        return this.lpcount;
    }

    /**
     * parseing Energy Meter Data of SM110 Meter
     * @param data stream of result command
     */
    public void parse(byte[] data) throws Exception
    {
        int totlen = data.length;
        log.debug("TOTLEN[" + totlen + "]");

        int offset = 0;
        while(offset + 6 < totlen){
            // log.debug("OFFSET[" + offset + "]");

            String tbName = new String(data,offset,4);
            offset += 4;
            int len = 0;
            len |= (data[offset++] & 0xff) << 8;
            len |= (data[offset++] & 0xff);
            byte[] b = new byte[len];

            if (data.length - offset < len)
                break;

            System.arraycopy(data,offset,b,0,len);
            offset += len;

                  if(tbName.equals("S001"))
            {
                s001 = b;
                log.debug("[s001] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("S003"))
            {
                s003 = b;
                log.debug("[s003] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("S005"))
            {
                s005 = b;
                log.debug("[s005] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("S012"))
            {
                s012 = b;
                log.debug("[s012] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("S021"))
            {
                s021 = b;
                log.debug("[s021] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("S022"))
            {
                s022 = b;
                log.debug("[s022] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("S023"))
            {
                s023 = b;
                log.debug("[s023] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("S025"))
            {
                s025 = b;
                log.debug("[s025] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("S026"))
            {
                s026 = b;
                log.debug("[s026] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("S055"))
            {
                s055 = b;
                log.debug("[s055] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("S061"))
            {
                s061 = b;
                log.debug("[s061] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("S062"))
            {
                s062 = b;
                log.debug("[s062] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("S063"))
            {
                s063 = b;
                log.debug("[s063] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("S064"))
            {
                s064 = b;
                log.debug("[s064] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("S071"))
            {
                s071 = b;
                log.debug("[s071] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("S072"))
            {
                s072 = b;
                log.debug("[s072] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("S076"))
            {
                s076 = b;
                log.debug("[s076] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("S130"))
            {
                s130 = b;
                log.debug("[s130] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("S132"))
            {
                s132 = b;
                log.debug("[s132] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("M000"))
            {
                m000 = b;
                log.debug("[m000] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("M067"))
            {
                m067 = b;
                log.debug("[m067] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("M070"))
            {
                m070 = b;
                log.debug("[m070] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("M075"))
            {
                m075 = b;
                log.debug("[m075] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("M078"))
            {
                m078 = b;
                log.debug("[m078] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("M113"))
            {
                 m113 = b;
                 log.debug("[m113] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("T001"))
            {
                 t001 = b;
                 log.debug("[t001] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("T002"))
            {
                 t002 = b;
                 log.debug("[t002] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("B055"))
            {
                 b055 = b;
                 log.debug("[b055] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("A055"))
            {
                 a055 = b;
                 log.debug("[a055] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("N055"))
            {
                 n055 = b;
                 log.debug("[n055] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("N023"))
            {
                 n023 = b;
                 log.debug("[n023] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("N025"))
            {
                 n025 = b;
                 log.debug("[n025] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("N026"))
            {
                 n026 = b;
                 log.debug("[n026] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("N067"))
            {
                 n067 = b;
                 log.debug("[n067] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("N078"))
            {
                 n078 = b;
                 log.debug("[n078] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else
            {
                log.debug("unknown table=["+tbName+"] data=>"+Util.getHexString(b));
            }
        }

        if(s001 != null){
            st001 = new ST001(s001);
            this.meterId = st001.getMSerial();
        }

        if(s005 !=null){
            st005 = new ST005(s005);
        }
        if(s012 !=null){
            st012 = new ST012(s012);
        }
        if(s021 != null){
            st021 = new ST021(s021);
        }
        if(s022 != null){
            st022 = new ST022(s022);
        }
        if(m000 != null){
            mt000 = new MT000(m000);
        }
        if(m070 != null){
            mt070 = new MT070(m070);
        }
        if(m075 != null){
            mt075 = new MT075(m075);
        }

        if(s023 != null){
        	//------------------------------------
        	// 제주용 검침 데이터에는 tou block은 한개
       	    // summaion만 2채널에 대한 값이 올라옴
        	//------------------------------------
        	if(mt075 == null) {
        		throw new Exception("meterId["+meterId+"] mt075 table is null!!");
        	}
            st023 = new ST023Short(s023,
                              1,
                              2,
                              0,
                              0,
                              mt075.getVAH_SF(),
                              mt075.getVA_SF(),
                              mt070 != null ? mt070.getDISP_SCALAR():1,
                              mt070 != null ? mt070.getDISP_MULTIPLIER():1);
        }

        if(s025 != null){
        	//------------------------------------
        	// 제주용 검침 데이터에는 tou block은 한개
       	    // summaion만 2채널에 대한 값이 올라옴
        	//------------------------------------

            st025 = new ST025Short(s025,
                              1,
                              2,
                              0,
                              0,
                              mt075.getVAH_SF(),
                              mt075.getVA_SF(),
                              mt070 != null ? mt070.getDISP_SCALAR():1,
                              mt070 != null ? mt070.getDISP_MULTIPLIER():1);
        }
        if(s026 != null){
            st026 = new ST026(s026,
                              st021.getNBR_TIERS(),
                              st021.getNBR_SUMMATIONS(),
                              st021.getNBR_DEMANDS(),
                              st021.getNBR_COINCIDENT(),
                              mt075.getVAH_SF(),
                              mt075.getVA_SF(),
                              mt070 != null ? mt070.getDISP_SCALAR():1,
                              mt070 != null ? mt070.getDISP_MULTIPLIER():1);
            try{
            	st026.parseData();
            }catch(Exception e){log.warn(e,e);}
        }

        if(n023 != null){
            nt023 = new NT023(n023,
                              st021.getNBR_TIERS(),
                              st021.getNBR_SUMMATIONS(),
                              st021.getNBR_DEMANDS(),
                              st021.getNBR_COINCIDENT(),
                              mt075.getVAH_SF(),
                              mt075.getVA_SF(),
                              mt070 != null ? mt070.getDISP_SCALAR():1,
                              mt070 != null ? mt070.getDISP_MULTIPLIER():1);
        }

        if(n025 != null){
            nt025 = new NT025(n025,
                              st021.getNBR_TIERS(),
                              st021.getNBR_SUMMATIONS(),
                              st021.getNBR_DEMANDS(),
                              st021.getNBR_COINCIDENT(),
                              mt075.getVAH_SF(),
                              mt075.getVA_SF(),
                              mt070 != null ? mt070.getDISP_SCALAR():1,
                              mt070 != null ? mt070.getDISP_MULTIPLIER():1);
        }
        if(n026 != null){
            nt026 = new NT026(n026,
                              st021.getNBR_TIERS(),
                              st021.getNBR_SUMMATIONS(),
                              st021.getNBR_DEMANDS(),
                              st021.getNBR_COINCIDENT(),
                              mt075.getVAH_SF(),
                              mt075.getVA_SF(),
                              mt070 != null ? mt070.getDISP_SCALAR():1,
                              mt070 != null ? mt070.getDISP_MULTIPLIER():1);
        }

        if(s055 != null){
            st055 = new ST055(s055);
            this.meterTime = st055.getDateTime();
            if(s003 != null)
                st003 = new ST003(s003,st055.getDateTime());
        }

        if(s061 != null){
            st061 = new ST061(s061);
            log.debug("NBR_BLKS_SET1     ="+ st061.getNBR_BLKS_SET1());
            log.debug("NBR_BLKS_INTS_SET1="+ st061.getNBR_BLKS_INTS_SET1());
            log.debug("NBR_BLKS_CHNS_SET1="+ st061.getNBR_CHNS_SET1());
            log.debug("NBR_INT_TIME_SET1="+ st061.getINT_TIME_SET1());
            if(s062!= null){
                st062 = new ST062(s062,st061.getNBR_CHNS_SET1());
            }
        }

        if(s063 != null){
            st063 = new ST063(s063);
        }

        if(s064 != null){
            st064 = new ST064Short(s064,
                        st001.getMSerial(),
                        st061.getNBR_BLKS_SET1(),
                        st061.getNBR_BLKS_INTS_SET1(),
                        st061.getNBR_CHNS_SET1(),
                        st061.getINT_TIME_SET1(),
                        mt075.getVAH_SF(),
                        mt075.getVA_SF(),
                        mt070 != null ? mt070.getDISP_SCALAR():1,
                        mt070 != null ? mt070.getDISP_MULTIPLIER():1,
                        st062,
                        st063.getNBR_VALID_INT());
        }

        if(s130!=null){
            st130 = new ST130(s130);
        }
        if(s132!=null){
            st132 = new ST132(s132);
        }

        if(s071!=null){
            st071 = new ST071(s071);
        }
        if(s072!=null){
            st072 = new ST072(s072);
        }
        if(s076!=null){
            st076 = new ST076(s076);
        }
        if(t001!=null){
            nuri_t001 = new NURI_T001(t001);
        }
        if(t002!=null){
            nuri_t002 = new NURI_T002(t002);
        }

        if(m067 !=null){
            mt067 = new MT067(m067);
        }
        if(m078 !=null){
            mt078 = new MT078(m078);
        }

        if(n067 !=null){
            nt067 = new NT067(n067);
        }
        if(n078 !=null){
            nt078 = new NT078(n078);
        }

        if(m113 != null){
            mt113 = new MT113(m113);
        }

        if(s064 != null && st064 != null){
            this.lpcount = st064.getTotpulseCount();
        }

        if(b055 != null){
            bt055 = new BT055(b055);
            if(a055 != null)
                at055 = new AT055(a055);
        }
        if(n055 != null)
            nt055 = new NT055(n055);

        log.debug("SM110 Data Parse Finished :: DATA["+toString()+"]");
    }

    /**
     * get flag
     * @return flag measurement flag
     */
    public int getFlag()
    {
        return this.flag;
    }

    /**
     * set flag
     * @param flag measurement flag
     */
    public void setFlag(int flag)
    {
        this.flag = flag;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        sb.append("SM110 Meter DATA[");
        sb.append("(meterId=").append(meterId).append("),");
        //sb.append("(meterSerial=").append(meterSerial).append(")");
        sb.append("]\n");

        return sb.toString();
    }

    public String getLPChannelMap(){

        try{
            UNIT_OF_MTR unit_of_mtr = new UNIT_OF_MTR();
            if(s061 != null && s062!= null && s012 != null){
                int[] sel_select = st062.getLP_SEL_SET1();
                String[] uom_code = st012.getUOM_CODE(sel_select);
                return unit_of_mtr.getChannelMap(uom_code);
            }
        }catch(Exception e){
            log.warn(e);
        }
        return "";
    }

    public int getLPChannelCount(){
        try{
            if(st061 != null){
                return st061.getNBR_CHNS_SET1()*2+1;
            }
            else{
                return 5;//ch1,ch2,v1,v2,pf
            }
        } catch(Exception e){
        	log.error(e,e);
        }
        return 5;//ch1,ch2,v1,v2,pf
    }

    public int getLPChannelCount(String model){
        try{
            if(st061 != null){
                return st061.getNBR_CHNS_SET1()*2+1;
            }
            else{
            	if(model.equals("12"))
            		return 3;//ch1,ch2,v1,v2,pf
            	else
            		return 5;
            }
        } catch(Exception e){
        	log.error(e,e);
        }

        if(model.equals("12"))
        	return 3;//ch1,ch2,v1,v2,pf
        else
        	return 5;
    }

    public int getResolution(){

        try{
            if(st061 != null){
                return st061.getINT_TIME_SET1();
            }
            else{
                return Integer.parseInt(FMPProperty.getProperty("def.lp.resolution"));
            }
        } catch(Exception e){
        	log.error(e,e);
        }
        return 60;
    }

    public String getMeterLog(){
        if(st003 != null){
            return st003.getMeterLog();
        }else{
            return "";
        }
    }

    public EventLogData[] getMeterStatusLog(){
        if(st003 != null){
            return st003.getEventLog();
        }else{
            return null;
        }
    }

    public MeterStatus getMeterStatusCode() {
        if (st003 != null) {
            return st003.getStatus();
        }
        else {
            return MeterStatus.Normal;
        }
    }

    public LPData[] getLPData(){
        try{
            if(st064 != null)
                return st064.getLPData();
            else
                return null;
        } catch(Exception e){
                log.warn("SM110 get LP Error:"+e.getMessage());
        }
        return null;
    }

    public TOU_BLOCK[] getPrevBilling(){
        try{
            if(st025 != null)
                return st025.getTOU_BLOCK();
            else if(nt025 != null)
                return nt025.getTOU_BLOCK();
            else
                return null;
        } catch(Exception e){
            log.warn("SM110 get Prev Billing Error:"+e.getMessage());
        }
        return null;
    }

    public TOU_BLOCK[] getCurrBilling(){
        try{
            if(st023 != null)
                return st023.getTOU_BLOCK();
            else if(nt023 != null)
                return nt023.getTOU_BLOCK();
            else
                return null;
        } catch(Exception e){
            log.warn("SM110 get Curr Billing Error:"+e.getMessage());
        }
        return null;
    }

    public ArrayList getSelfReads(){

        TOU_BLOCK[] tou = null;
        ArrayList list = new ArrayList();
        try
        {
            if(st026 != null){
                ST025[] st025s = st026.getSelfReads();

                for(int i = 0; st025s != null && i < st025s.length; i++){
                    tou = st025s[i].getTOU_BLOCK();
                    list.add(tou);
                    //for(int k = 0; k < tou.length; k++){
                    //    list.add(tou[k]);
                    //}
                }
                return list;
                //return (TOU_BLOCK[])list.toArray(new TOU_BLOCK[list.size()]);
            }
            else  if(nt026 != null){
                NT025[] nt025s = nt026.getSelfReads();

                for(int i = 0; nt025s != null && i < nt025s.length; i++){
                    tou = nt025s[i].getTOU_BLOCK();
                    list.add(tou);
                    //for(int k = 0; k < tou.length; k++){
                    //    list.add(tou[k]);
                    //}
                }
                return list;
                //return (TOU_BLOCK[])list.toArray(new TOU_BLOCK[list.size()]);
            }
            else
            {
                return null;
            }
        }
        catch(Exception e)
        {
            log.warn("SM110 get Self Read Error:",e);
        }
        return null;
    }

    public LinkedHashMap getRelayStatus(){
        try{
            if(st130 == null){
                return null;
            }else{
                return st130.getData();
            }
        } catch(Exception e){
            log.warn("SM110 get RelayStatus Error:"+e.getMessage());
        }
        return null;
    }

    public EventLogData[] getRelayEventLog(){
        try{
            if(st132 != null )
                return st132.getEventLog();
            else
                return null;
        } catch(Exception e){
            log.warn("SM110 get RelayEvent Error:"+e.getMessage());
        }
        return null;
    }

    public EventLogData[] getEventLog(){
        if(st076 != null){
            return st076.getEvent();
        }else{
            return null;
        }
    }

    public Instrument[] getInstrument(){
        if(mt113 != null){
            return mt113.getInstrument();
        }else{
            return null;
        }
    }

    /**
     * get Data
     */
    @SuppressWarnings("unchecked")
    public LinkedHashMap getData()
    {
        LinkedHashMap res = new LinkedHashMap(16,0.75f,false);
        TOU_BLOCK[] tou_block = null;
        LPData[] lplist = null;
        EventLogData[] evlog = null;
        EventLogData[] relayevlog = null;
        String meter_mode = null;

        DecimalFormat df3 = TimeLocaleUtil.getDecimalFormat(meter.getSupplier());

        try
        {
            tou_block = getCurrBilling();
            lplist = getLPData();
            evlog = getEventLog();
            relayevlog = getRelayEventLog();
            meter_mode = getMeterMode();

			res.put("<b>[Meter Configuration Data]</b>", "");
            if(st001 != null){
                res.put("Manufacturer",st001.getMANUFACTURER());
                res.put("Model",st001.getED_MODEL());
                res.put("Manufacturer Serial Number",st001.getMSerial());
                res.put("HW Version Number",df3.format(st001.getHW_VERSION_NUMBER())+"");
                res.put("HW Revision Number",df3.format(st001.getHW_REVISION_NUMBER())+"");
                res.put("FW Version Number",df3.format(st001.getFW_VERSION_NUMBER())+"");
                res.put("FW Revision Number",df3.format(st001.getFW_REVISION_NUMBER())+"");
            }
            if(st005 != null)
                res.put("Device Serial Number",st005.getMSerial());
            if(st003 != null)
                res.put("Meter Log",st003.getMeterLog());
            if(st055 != null){
                res.put("Meter Time",st055.getDateTime());
                res.put("DST Apply Flag",st055.getDstApplyOnName());
                res.put("DST Flag",st055.getDstSeasonOnName());
            }
            if(mt067 != null){
                res.put("Current Transform Ratio",df3.format(mt067.getCUR_TRANS_RATIO())+"");
                res.put("Voltage Transform Ratio",df3.format(mt067.getPOT_TRANS_RATIO())+"");
            }
            if(nt067 != null){
                res.put("Current Transform Ratio",df3.format(nt067.getCUR_TRANS_RATIO())+"");
                res.put("Voltage Transform Ratio",df3.format(nt067.getPOT_TRANS_RATIO())+"");
            }
            if(mt070 != null){
                res.put("Display Multiplier", df3.format(mt070.getDISP_MULTIPLIER())+"");
                res.put("Display Scalar", df3.format(mt070.getDISP_SCALAR())+"");
            }
            if(mt075 != null){
                res.put("[Scale Factor]", "");
                res.put("line-to-neutral voltages", df3.format(mt075.getI_SQR_HR_SF())+"");
                res.put("line-to-line voltages", df3.format(mt075.getV_RMS_SF())+"");
                res.put("Current", df3.format(mt075.getV_SQR_HR_LN_SF())+"");
                res.put("Power Scale", df3.format(mt075.getVA_SF())+"");
                res.put("Energy Scale", df3.format(mt075.getVAH_SF())+"");
            }
            if(mt113 != null){
                res.put("[Momentary Phase]", "");
                res.put("Interval phase A/C Voltage", df3.format(mt113.getRMS_VOLTAGE_PHA())+"/"+df3.format(mt113.getRMS_VOLTAGE_PHC()));
                res.put("Interval Power Factor(%)", df3.format(mt113.getMOMENTARY_INTERVAL_PF())+"");
            }
            if(mt078 != null){
                res.put("[Power Outage Information]", "");
                res.put("Last Power Outage Date",mt078.getDT_LAST_POWER_OUTAGE());
                res.put("Cummulative Power Outage(Seconds)", df3.format(mt078.getCUM_POWER_OUTAGE_SECS())+"");
                res.put("Number Of Power Outages", df3.format(mt078.getNBR_POWER_OUTAGES())+"");
            }
            if(nt078 != null){
                res.put("[Power Outage Information]", "");
                res.put("Last Power Outage Date",nt078.getDT_LAST_POWER_OUTAGE());
                res.put("Cummulative Power Outage(Seconds)", df3.format(nt078.getCUM_POWER_OUTAGE_SECS())+"");
                res.put("Number Of Power Outages", df3.format(nt078.getNBR_POWER_OUTAGES())+"");
            }
            if(st130 != null){
                res.put("[Relay Status]", "");
                res.put("Relay status" , st130.getRelayStatusString());
                res.put("Relay activate status" , st130.getRelayActivateStatusString());
            }

            if(tou_block != null){
                res.put("[Current Billing Data]", "");
                res.put("Total Active Energy(kWh)"              ,df3.format(tou_block[0].getSummation(0)));
                meteringValue = new Double(df3.format(tou_block[0].getSummation(0)));
                res.put("Total Reactive Energy(kWh)"            ,df3.format(tou_block[0].getSummation(1)));
                res.put("Total Active Power Max.Demand(kW)"     ,df3.format(tou_block[0].getCurrDemand(0)));
                res.put("Total Active Power Max.Demand Time"    ,tou_block[0].getEventTime(0));
                res.put("Total Reactive Power Max.Demand(kW)"   ,df3.format(tou_block[0].getCurrDemand(1)));
                res.put("Total Reactive Power Max.Demand Time"  ,tou_block[0].getEventTime(1));
                res.put("Total Active Power Cum.Demand(kW)"     ,df3.format(tou_block[0].getCumDemand(0)));
                res.put("Total Reactive Power Cum.Demand(kW)"   ,df3.format(tou_block[0].getCumDemand(1)));
                if(tou_block[0].getCoincident() != null && tou_block[0].getCoincident().size() > 0){
                    res.put("Total Active Power Cont.Demand(kW)"    ,df3.format(tou_block[0].getCoincident(0)));
                    res.put("Total Reactive Power Cont.Demand(kW)"  ,df3.format(tou_block[0].getCoincident(1)));
                }

                if(tou_block.length >= 2){
                    res.put("Rate A Active Energy(kWh)"             ,df3.format(tou_block[1].getSummation(0)));
                    res.put("Rate A Reactive Energy(kWh)"           ,df3.format(tou_block[1].getSummation(1)));
                    res.put("Rate A Active Power Max.Demand(kW)"    ,df3.format(tou_block[1].getCurrDemand(0)));
                    res.put("Rate A Active Power Max.Demand Time"   ,tou_block[1].getEventTime(0));
                    res.put("Rate A Reactive Power Max.Demand(kW)"  ,df3.format(tou_block[1].getCurrDemand(1)));
                    res.put("Rate A Reactive Power Max.Demand Time" ,tou_block[1].getEventTime(1));
                    res.put("Rate A Active Power Cum.Demand(kW)"    ,df3.format(tou_block[1].getCumDemand(0)));
                    res.put("Rate A Reactive Power Cum.Demand(kW)"  ,df3.format(tou_block[1].getCumDemand(1)));

                    if(tou_block[1].getCoincident() != null && tou_block[1].getCoincident().size() > 0){
                    	res.put("Rate A Active Power Cont.Demand(kW)"   ,df3.format(tou_block[1].getCoincident(0)));
                    	res.put("Rate A Reactive Power Cont.Demand(kW)" ,df3.format(tou_block[1].getCoincident(1)));
                    }
                }

                if(tou_block.length >= 3){
                    res.put("Rate B Active Energy(kWh)"             ,df3.format(tou_block[2].getSummation(0)));
                    res.put("Rate B Reactive Energy(kWh)"           ,df3.format(tou_block[2].getSummation(1)));
                    res.put("Rate B Active Power Max.Demand(kW)"    ,df3.format(tou_block[2].getCurrDemand(0)));
                    res.put("Rate B Active Power Max.Demand Time"   ,tou_block[2].getEventTime(0));
                    res.put("Rate B Reactive Power Max.Demand(kW)"  ,df3.format(tou_block[2].getCurrDemand(1)));
                    res.put("Rate B Reactive Power Max.Demand Time" ,tou_block[2].getEventTime(1));
                    res.put("Rate B Active Power Cum.Demand(kW)"    ,df3.format(tou_block[2].getCumDemand(0)));
                    res.put("Rate B Reactive Power Cum.Demand(kW)"  ,df3.format(tou_block[2].getCumDemand(1)));

                    if(tou_block[2].getCoincident() != null && tou_block[2].getCoincident().size() > 0){
                    	res.put("Rate B Active Power Cont.Demand(kW)"   ,df3.format(tou_block[2].getCoincident(0)));
                    	res.put("Rate B Reactive Power Cont.Demand(kW)" ,df3.format(tou_block[2].getCoincident(1)));
                    }
                }

                if(tou_block.length >= 4){
                    res.put("Rate C Active Energy(kWh)"             ,df3.format(tou_block[3].getSummation(0)));
                    res.put("Rate C Reactive Energy(kWh)"           ,df3.format(tou_block[3].getSummation(1)));
                    res.put("Rate C Active Power Max.Demand(kW)"    ,df3.format(tou_block[3].getCurrDemand(0)));
                    res.put("Rate C Active Power Max.Demand Time"   ,tou_block[3].getEventTime(0));
                    res.put("Rate C Reactive Power Max.Demand(kW)"  ,df3.format(tou_block[3].getCurrDemand(1)));
                    res.put("Rate C Reactive Power Max.Demand Time" ,tou_block[3].getEventTime(1));
                    res.put("Rate C Active Power Cum.Demand(kW)"    ,df3.format(tou_block[3].getCumDemand(0)));
                    res.put("Rate C Reactive Power Cum.Demand(kW)"  ,df3.format(tou_block[3].getCumDemand(1)));
                    if(tou_block[3].getCoincident() != null && tou_block[3].getCoincident().size() > 0){
                    	res.put("Rate C Active Power Cont.Demand(kW)"   ,df3.format(tou_block[3].getCoincident(0)));
                    	res.put("Rate C Reactive Power Cont.Demand(kW)" ,df3.format(tou_block[3].getCoincident(1)));
                    }
                }

            }

            if(lplist != null && lplist.length > 0){
                res.put("[Load Profile Data(kWh)]", "");
                int nbr_chn = 2;//ch1,ch2
                if(st061 != null){
                    nbr_chn = st061.getNBR_CHNS_SET1();
                }
                ArrayList chartData0 = new ArrayList();//time chart
                ArrayList[] chartDatas = new ArrayList[nbr_chn]; //channel chart(ch1,ch2,...)
                for(int k = 0; k < nbr_chn ; k++){
                    chartDatas[k] = new ArrayList();
                }
                DecimalFormat decimalf=null;
                SimpleDateFormat datef14=null;
                ArrayList lpDataTime = new ArrayList();
                for(int i = 0; i < lplist.length; i++){
                    String datetime = lplist[i].getDatetime();

                    if(meter!=null && meter.getSupplier()!=null){
                    Supplier supplier = meter.getSupplier();
                    if(supplier !=null){
                        String lang = supplier.getLang().getCode_2letter();
                        String country = supplier.getCountry().getCode_2letter();
                        
                        decimalf = TimeLocaleUtil.getDecimalFormat(supplier);
                        datef14 = new SimpleDateFormat(TimeLocaleUtil.getDateFormat(14, lang, country));
                    }
                	}else{
                    //locail 정보가 없을때는 기본 포멧을 사용한다.
                    decimalf = new DecimalFormat();
                    datef14 = new SimpleDateFormat();
                }
                    String date;
                	date = datef14.format(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(datetime+"00"));
                   
                    String tempDateTime = lplist[i].getDatetime();
                    String val = "";
                    Double[] ch = lplist[i].getCh();
                    for(int k = 0; k < ch.length ; k++){
                        val += "<span style='margin-right: 40px;'>ch"+(k+1)+"="+df3.format(ch[k])+"</span>";
                    }
                    res.put("LP"+" "+date, val);

                    chartData0.add(tempDateTime.substring(6,8)
                                  +tempDateTime.substring(8,10)
                                  +tempDateTime.substring(10,12));
                    for(int k = 0; k < ch.length ; k++){
                        chartDatas[k].add(ch[k].doubleValue());
                    }
                    lpDataTime.add(lplist[i].getDatetime());
                }

                //res.put("chartData0", chartData0);
                //for(int k = 0; k < chartDatas.length ; k++){
                //    res.put("chartData"+(k+1), chartDatas[k]);
                //}
                //res.put("lpDataTime", lpDataTime);
                //res.put("chartDatas", chartDatas);
                res.put("[ChannelCount]", nbr_chn);
            }

            if(evlog != null && evlog.length > 0){
                res.put("[Event Log]", "");
                int idx = 0;
                for(int i = 0; i < evlog.length; i++){
                    String datetime = evlog[i].getDate() + evlog[i].getTime();
                    if(!datetime.startsWith("0000") && !datetime.equals("")){
                        res.put("EV"+datetime+"00", evlog[i].getMsg());
                    }
                }
            }

            if(relayevlog != null && relayevlog.length > 0){
                res.put("[Relay Event Log]", "");
                for(int i = 0; i < relayevlog.length; i++){
                    String datetime = relayevlog[i].getDate()+relayevlog[i].getTime();
                    res.put("RE"+datetime+"00", relayevlog[i].getMsg());
                }
            }

            if(s012 != null && s061!= null && st062!= null){
                res.put("LP Channel Information", getLPChannelMap());
            }
            if(meter_mode != null){
                res.put("Meter Mode", meter_mode);
            }
        }
        catch (Exception e)
        {
            log.warn("Get Data Error=>",e);
        }

        return res;
    }

    public int getDstApplyOn() throws Exception
    {
        if(st055!= null){
            return st055.getDstApplyOn();
        }else{
            return 0;
        }

    }

    public int getDstSeasonOn() throws Exception
    {
        if(st055!= null){
            return st055.getDstSeasonOn();
        }else{
            return 0;
        }

    }

    public String getMeterMode() throws Exception
    {
        if(mt000 != null){
            return mt000.getMETER_MODE_NAME();
        }else{
            return null;
        }
    }

    public String getTimeDiff() throws Exception {

        if(st055 != null && nt055 != null)
        {
            return (int)((nt055.getTime() - st055.getTime())/1000)+"";
        }
        else
        {
            return null;
        }
    }

    public MeterTimeSyncData getMeterTimeSync(){

        MeterTimeSyncData meterTimeSyncData = new MeterTimeSyncData();

        try{
            if(st005 != null && bt055 != null && at055 != null){
                String meterTime = st055.getDateTime();
                String beforeTime = bt055.getDateTime();
                String afterTime = at055.getDateTime();
                int timeDiff = (int)((TimeUtil.getLongTime(afterTime)
                        - TimeUtil.getLongTime(beforeTime))/1000);

                meterTimeSyncData.setId(st001.getMSerial());
                meterTimeSyncData.setAtime(afterTime);
                meterTimeSyncData.setBtime(beforeTime);
                meterTimeSyncData.setCtime(meterTime);
                meterTimeSyncData.setEtime(meterTime);
                meterTimeSyncData.setMethod(1);//auto
                meterTimeSyncData.setResult(0);//success
                meterTimeSyncData.setTimediff(timeDiff);
                meterTimeSyncData.setUserID("AUTO Synchronized");
                return meterTimeSyncData;
            }
            else
            {
                return null;
            }
        }catch(Exception e){
            log.warn("get meter time sync log error: "+e.getMessage());
        }
        return null;
    }

    public boolean isSavingLP(){

        try{
            if(st063 != null){
                int blkCnt = st063.getNBR_VALID_BLOCKS();
                if(blkCnt == 0){
                    return true;
                }
            }
        }catch(Exception e){
            log.warn("Get valid lp block count Error=>"+e.getMessage());
        }
        return false;
    }

    public MeteringFail getMeteringFail(){
        if(nuri_t002 != null){
            return nuri_t002.getMeteringFail();
        }else{
            return null;
        }
    }
}