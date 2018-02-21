package com.aimir.fep.meter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.parser.DLMSKaifa;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSTable;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.OBIS;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.EnergyMeter;
import com.aimir.model.device.Meter;
import com.aimir.model.system.Code;

public class DLMSKaifaMeterDataTest {
    private static Log log = LogFactory.getLog(DLMSKaifaMeterDataTest.class);
    
    
    public static void main(String[] args) { 
    	DLMSKaifaMeterDataTest test = new DLMSKaifaMeterDataTest();
    	test.test_dlms();
    }
    
    
    
    
    public void test_dlms() {
        try {
            StringBuffer buf = new StringBuffer();
            // DLMS Header OBIS(6), CLASS(1), ATTR(1), LENGTH(2)
            // DLMS Tag Tag(1), DATA or LEN/DATA (*)

            buf.append("0000010000FF000802000E090C07E1051103030110FF8000000100630100FF00070400050600000E100100630100FF000702039201180206090C07E1050E07120000FF8000001100060017E17406000001A606000102D4060000895B0206090C07E1050E07130000FF8000001100060017EA8F06000001A606000102D406000089BD0206090C07E1050E07140000FF8000001100060017F7DC06000001A6060001030A06000089C50206090C07E1050E07150000FF8000001100060018021D06000001A6060001039106000089C50206090C07E1050E07160000FF80000011000600180E5606000001A6060001040C06000089C50206090C07E1050E07170000FF800000110006001816E006000001A6060001043206000089F20206090C07E1050F01000000FF80000011000600181F6506000001A606000104320600008A560206090C07E1050F01010000FF80000011000600182A7506000001A606000104320600008ABA0206090C07E1050F01020000FF800000110006001831FA06000001A606000104320600008B270206090C07E1050F01030000FF8000001100060018354206000001A6060001043E0600008B2A0206090C07E1050F01040000FF80000011000600183A7506000001A606000104440600008B2A0206090C07E1050F01050000FF80000011000600183CFA06000001A606000104480600008B820206090C07E1050F01060000FF8000001100060018401906000001A606000104480600008C160206090C07E1050F01070000FF8000001100060018475D06000001A6060001044C0600008C890206090C07E1050F01080000FF80000011000600184E9506000001A6060001044C0600008CF90206090C07E1050F01090000FF800000110006001851D206000001A6060001047E0600008D020206090C07E1050F010A0000FF800000110006001854B006000001A606000104A50600008D050206090C07E1050F010B0000FF800000110006001858FA06000001A606000104C00600008D3E0206090C07E1050F010C0000FF80000011000600185B3606000001A606000104C00600008DCC0206090C07E1050F010D0000FF8000001100060018600A06000001A606000104D10600008E2C0206090C07E1050F010E0000FF8000001100060018661006000001A606000104DC0600008E820206090C07E1050F010F0000FF8000001100060018695606000001A606000105630600008E8C0206090C07E1050F01100000FF80000011000600186FB006000001A6060001078C0600008E8C0206090C07E1050F01110000FF8000001100060018747F06000001A6060001095C0600008E8C0100630200FF000704000506000151800000636200FF00070201CA01180202090C07E1050E07090416FF8000001200040202090C07E1050E07092216FF8000001200040202090C07E1050E070A0417FF8000001200040202090C07E1050E070A1E20FF8000001200040202090C07E1050E07140D16FF8000001200040202090C07E1050E07142B16FF8000001200040202090C07E1050E07150D16FF8000001200040202090C07E1050E07152B16FF8000001200040202090C07E1050E07160D16FF8000001200040202090C07E1050E07162B15FF8000001200040202090C07E1050E07170D15FF8000001200040202090C07E1050F01013115FF8000001200040202090C07E1050F01021315FF8000001200040202090C07E1050F01023115FF8000001200040202090C07E1050F01031315FF8000001200040202090C07E1050F01033114FF8000001200040202090C07E1050F01041312FF8000001200040202090C07E1050F01043112FF8000001200040202090C07E1050F01051312FF8000001200040202090C07E1050F01053110FF8000001200040202090C07E1050F0106130EFF8000001200040202090C07E1050F0106310EFF8000001200040202090C07E1050F0107130EFF8000001200040202090C07E1050F0107310DFF800000120004");
            byte[] data = Hex.encode(buf.toString());

            String obisCode = "";
            int clazz = 0;
            int attr = 0;

            int pos = 0;
            int len = 0;
            // DLMS Header OBIS(6), CLASS(1), ATTR(1), LENGTH(2)
            // DLMS Tag Tag(1), DATA or LEN/DATA (*)
            byte[] OBIS = new byte[6];
            byte[] CLAZZ = new byte[2];
            byte[] ATTR = new byte[1];
            byte[] LEN = new byte[2];
            byte[] TAGDATA = null;
            
            DLMSTable dlms = null;
            while (pos < data.length) {
                dlms = new DLMSTable();
                System.arraycopy(data, pos, OBIS, 0, OBIS.length);
                pos += OBIS.length;
                obisCode = Hex.decode(OBIS);
                System.out.print("OBIS[" + obisCode + "]");
                dlms.setObis(obisCode);
                
                if(DLMSVARIABLE.OBIS.getObis(obisCode) != null){
                	System.out.print("NAME["+DLMSVARIABLE.OBIS.getObis(obisCode).getName()+ "]");
                }else{
                	System.out.print("NAME[undefined]");
                }                
                
                System.arraycopy(data, pos, CLAZZ, 0, CLAZZ.length);
                pos += CLAZZ.length;
                clazz = DataUtil.getIntTo2Byte(CLAZZ);
                System.out.print("CLASS[" + clazz + "]");
                dlms.setClazz(clazz);
                
                System.arraycopy(data, pos, ATTR, 0, ATTR.length);
                pos += ATTR.length;
                attr = DataUtil.getIntToBytes(ATTR);
                System.out.print("ATTR[" + attr + "]");
                dlms.setAttr(attr);

                System.arraycopy(data, pos, LEN, 0, LEN.length);
                pos += LEN.length;
                len = DataUtil.getIntTo2Byte(LEN);
                System.out.print("LENGTH[" + len + "]");
                dlms.setLength(len);

                TAGDATA = new byte[len];
                if (pos + TAGDATA.length <= data.length) {
                	System.arraycopy(data, pos, TAGDATA, 0, TAGDATA.length);
                	pos += TAGDATA.length;
                }
                else {
                	System.arraycopy(data, pos, TAGDATA, 0, data.length-pos);
                	pos += data.length-pos;
                }
                
                System.out.print("TAGDATA=["+Hex.decode(TAGDATA)+"]\n");
            }
            DLMSKaifa dlmsparser = new DLMSKaifa();
            Meter meter = new EnergyMeter();
            meter.setLpInterval(60);
            Code meterType = new Code();
            meterType.setName("EnergyMeter");
            meter.setMeterType(meterType);

            dlmsparser.setMeter(meter);
            dlmsparser.parse(Hex.encode(buf.toString()));

            System.out.println("MeterModel="+dlmsparser.getMeterModel());            
            System.out.println("Metering Value="+dlmsparser.getMeteringValue());
            
            LinkedHashMap<String, Map<String, Object>> map = dlmsparser.getData();
            Set<String> keys = map.keySet();
        	System.out.println("map=["+map.toString()+"]");
            for(String key: keys){

            	System.out.println("KEY=["+key+"]");
            	Object obj = map.get(key);
            	if(obj instanceof Map){
            		Map<String, Object> submap= map.get(key);
                	Set<String> subkeys = submap.keySet();
                	for(String subkey:subkeys){
                		System.out.println("KEY:SUBKEY="+key+","+subkey+","+submap.get(subkey));
                	}
            	}else{
            		//System.out.println("KEY=["+key+"] VAL=["+map.get(key)+"]");
            		System.out.println("OBJ=["+key+"] VAL=["+obj+"]");
            	}

            }

            
        }
        catch (Exception e) {
            log.error(e,e);
        }
    }
}
