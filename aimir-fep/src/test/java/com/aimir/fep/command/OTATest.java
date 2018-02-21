package com.aimir.fep.command;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.aimir.constants.CommonConstants.FW_EQUIP;
import com.aimir.dao.device.FirmwareDao;
import com.aimir.dao.device.FirmwareTriggerDao;
import com.aimir.dao.device.ZRUDao;
import com.aimir.fep.BaseTestCase;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.protocol.fmp.exception.FMPMcuException;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.device.Firmware;
import com.aimir.model.device.FirmwareTrigger;
import com.aimir.model.device.ZRU;
import com.aimir.util.DateTimeUtil;

/**
 * 태국 MEA&PEA, 집중기 SX2 미터 OTA 명령 단위테스트를 위해 만들었습니다.
 * @author kskim
 * @date 2012-04-23
 */
public class OTATest extends BaseTestCase {
	static {
        DataUtil.setApplicationContext(new ClassPathXmlApplicationContext(new String[]{"/config/spring.xml"}));
    }
	
	public static int SCOUR_MD5_BYTE_LIMIT = (4000 * 1024);
	
	/**
	 * 펌웨어 파일정보를 DB에 등록할때만 사용.
	 */
	@Ignore
	public void addFirmware(){
		
		String name = "SX2_FW_1204230.bin";
		
		FirmwareDao frDao = DataUtil.getBean(FirmwareDao.class);
		Firmware fr = new Firmware();
		
		fr.setFirmwareId(name);
		fr.setArm(false);
		fr.setBinaryFileName(name);
		fr.setBuild("0");
		fr.setDevicemodel_id(107);//SX2
		fr.setEquipKind("Meter");
		fr.setEquipModel("SX2");
		fr.setEquipType("Electricity");
		fr.setEquipVendor("MITSUBISHI");
		fr.setFwVersion("1204230");
		fr.setHwVersion("1204230");
		fr.setReleasedDate("20120423"); // yyyyMMdd
		
		//테스트에선 일단 널처리
		fr.setSupplier(null);
		fr.setSupplierId(null);
		
		frDao.saveOrUpdate(fr);
		frDao.flush();
	}
	
	@Ignore
	public void FirmwareTrigger() throws Exception{
		
		FirmwareDao frDao = DataUtil.getBean(FirmwareDao.class);
		
		// OTA 할 펌웨어 파일
		Firmware fr = frDao.getByFirmwareId("SX2_FW_1204230.bin");
		
		FirmwareTriggerDao frTrgDao = DataUtil.getBean(FirmwareTriggerDao.class);
		
		FirmwareTrigger ftrigger = frTrgDao.getFirmwareTrigger(fr.getId().toString());
		
		
		ftrigger.setCreateDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
		
		//이전 펌웨어 파일명
    	ftrigger.setSrcFirmware("SX2_FW_1204229.bin");
    	
    	// 새롭게 적용할 펌웨어 파일명
    	ftrigger.setTargetFirmware("SX2_FW_1204230.bin");
    	
    	// 빌드번호 정보
    	ftrigger.setTargetFWBuild("0");
    	
    	// 버전정보
    	ftrigger.setTargetFWVer("1");
    	
    	// 하드웨어 버전 정보
    	ftrigger.setTargetHWVer("1");
		
    	frTrgDao.saveOrUpdate(ftrigger);
    	frTrgDao.flush();
	}
	
	@Ignore
	public void addModem(){
		ZRUDao zDao = DataUtil.getBean(ZRUDao.class);
		ZRU zru = new ZRU();
		zru.setDeviceSerial("000D6F00015D6E88");
		zru.setInstallDate("20120423163432");
		zru.setModemType("ZRU");
		zru.setLocationId(6);
		zru.setModelId(100);
		zru.setMcuId(100);
		zru.setSupplierId(22);
		
		zDao.saveOrUpdate(zru);
		zDao.flush();
		
		
	}
	
	/**
	 * 펌웨어 롤백기능
	 */
	@Test
	public void cmdMergeAndInstall(){
		String mcuId = "1010";
		String mcuIdDeviceSerial = "000D6F00015D6E88"; //43380 
    	
    	//명령 전송.
		CommandGW cgw = new CommandGW();
		try {
			cgw.cmdMeterFactoryReset(mcuId, mcuIdDeviceSerial);
		} catch (FMPMcuException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * OTA Command Method 테스트용.
	 * @throws Exception 
	 * @throws FMPMcuException 
	 */
	@Ignore
	public void cmdOTATest() throws FMPMcuException, Exception{
		String mcuId = "3993";
		
		FirmwareDao frDao = DataUtil.getBean(FirmwareDao.class);
		
		// OTA 할 펌웨어 파일
		Firmware fr = frDao.getByFirmwareId("SX2_FW_1204230.bin");
		
		//Trigger ID 생성 로직
		FirmwareTriggerDao frTrgDao = DataUtil.getBean(FirmwareTriggerDao.class);
		
		FirmwareTrigger ftrigger = frTrgDao.getFirmwareTrigger("797");
    	
    	// 파라미터 설정.
    	String triggerId = String.valueOf(ftrigger.getId());

    	
    	/**
    	 * OTA 유형, OTA를 수행할 대상
    	 * 0 : Concentrator, 집중기 펌웨어 업그레이드
    	 * 1 : Sensor, 센서 펌웨어 업그레이드 (리피터 포함)
    	 * 2 : Coordinator, 코디네이터 펌웨어 업그레이드
    	 */
    	int equipKind = FW_EQUIP.Modem.getKind();
    	
    	
    	StringBuilder sbModel = new StringBuilder();
//    	sbModel.append(fr.getEquipVendor());
//    	sbModel.append(" ");
    	sbModel.append(fr.getEquipModel()); //fr.getEquipModel()
    	
    	String model = sbModel.toString();
    	
    	/**
    	 * 0 : Auto, 50대 기준으로 Multicast/Unicast가 자동 결정됨
    	 * 1 : Multicast (펌웨어 전송만 Multicast, Verify, Install은 Unicast로 진행)
    	 * 2 : Unicast
    	 */
    	int transferType = 1;
    	
    	/**
    	 * 0x01 = 센서 정보 수집 및 버전 확인 (필수)
    	 * 0x02 = 파일 전송 (Data Send)
    	 * 0x04 = 전송 이미지 확인(Verify)
    	 * 0x08 = 설치 (Install)
    	 * 0x10 = 업그레이드된 버전 확인 (Scan)
    	 * ALL  = 0x1F (남은 비트는 확장 가능 영역으로 남김)
    	 */
    	int otaStep = 0x1f; //30
    	
    	/**
    	 * Multicast일때, 펌웨어를 Multicast로 전송시 같은 프레임을 몇번 전송할것인지 결정한다.
    	 * 즉, 멀티캐스트 성공율을 높이기 위함이며 값이 증가하면 전송시간이 증가되므로 유의해야 한다.
    	 * 멀티캐스트가 아닌경우 이값은 (Default=1)로 지정한다.
    	 */
    	int multiWriteCount = 1;
    	
    	//default 10
    	int maxRetryCount = 10;
    	
    	//default 1
    	int otaThreadCount = 1;
    	
    	/**
    	 * 0 : AUTO, 같은 버전 설치 안함 (현재 버전이 New Version과 동일하면 설치 안함)
    	 * 1 : REINSTALL, 무조건 재설치, 설치 테스트 및 다시 내릴 경우, 버전이 같아도 내려짐.
    	 * 2 : MATCH, Old H/W, S/W, Build Version이 모두 동일하면 New Version으로 설치
    	 * 3 : FORCE, REINSTALL 처럼 무조건 재설치를 하고 File도 무조건 Base file을 다운 받는다
    	 */
    	int installType = 1;
    	
    	/**
    	 *  버전정보
    	 *  0x0103 = 1.3
    	 */
    	
    	int oldHwVersion = 0x0102;
    	int oldFwVersion = 0x0102;
    	int oldBuild = 1;
    	
    	int newHwVersion = 0x0103;
    	int newFwVersion = 0x0103;
    	int newBuild = 2;
    	
    	String binaryURL = "http://187.1.30.129:8080/aimir-web/gadget/device/firmware/downfw.jsp"+
    						"?fileType=binary&fileName=" + fr.getBinaryFileName().toLowerCase();
    	
    	int dotIdx = fr.getBinaryFileName().lastIndexOf(".");
    	String fileDirectory = fr.getBinaryFileName().substring(0,dotIdx);
    	String fileHomePath =  FMPProperty.getProperty("firmware.dir")+File.separator+fileDirectory;
    	
    	String filepath = fileHomePath+File.separator+fr.getBinaryFileName();
    	File file = new File(filepath);
    	if(!file.exists()){
    		System.out.println("파일 없음");
    	}
    	String binaryMD5 = getFileMD5Sum(file);
    	
    	String diffURL = null;
    	String diffMD5 = null;
    	
    	
    	String mcuIdDeviceSerial = "000D6F00005942EF"; //43380 
    	List<Object> mcuIdDeviceSerialList = new ArrayList<Object>();
    	mcuIdDeviceSerialList.add(mcuIdDeviceSerial);
    	
    	ArrayList<String> equipIdList = new ArrayList<String>();
//    	equipIdList.add(mcuIdDeviceSerialList);
    	equipIdList.add(mcuIdDeviceSerial);
    	
    	//명령 전송.
		CommandGW cgw = new CommandGW();
		cgw.cmdDistribution(mcuId, triggerId, equipKind, model, transferType, 
				otaStep, multiWriteCount, maxRetryCount, otaThreadCount, installType, 
				oldHwVersion, oldFwVersion, oldBuild, newHwVersion, newFwVersion, newBuild, binaryURL, binaryMD5, diffURL, diffMD5,
				equipIdList);
	}
    
    
	
	  /**
     * Method: getFileMD5Sum Purpose: get the MD5 sum of a file. Scour exchange only
     * counts the first SCOUR_MD5_BYTE_LIMIT bytes of a file for caclulating checksums
     * (probably for efficiency or better comaprison counts against unfinished downloads).
     *
     * @param f
     *            the file to read
     * @return the MD5 sum string
     * @throws IOException
     *             on IO error
     */
    public static String getFileMD5Sum(File f) throws Exception 
    {
        String sum = null;
        FileInputStream in;

       
		in = new FileInputStream(f.getAbsolutePath());
        byte[] b = new byte[1024];
        int num = 0;
        ByteArrayOutputStream out = new ByteArrayOutputStream();        	
    	
		while ((num = in.read(b)) != -1)
		{
		    out.write(b, 0, num);

		    if (out.size() > SCOUR_MD5_BYTE_LIMIT)
		    {
		        sum = md5Sum(out.toByteArray(), 10000);
		        break;
		    }
		}
        if (sum == null)
            sum = md5Sum(out.toByteArray(), SCOUR_MD5_BYTE_LIMIT);

        in.close();
        out.close();			




        return sum;
    }	
    public static String md5Sum(byte[] input, int limit)
    {
        try
        {
        	MessageDigest md = MessageDigest.getInstance("MD5");

            md.reset();
            byte[] digest;

            if (limit == -1)
            {
                digest = md.digest(input);
            }
            else
            {
                md
                        .update(input, 0, limit > input.length ? input.length
                                : limit);
                digest = md.digest();
            }

            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < digest.length; i++)
            {
                hexString.append(hexDigit(digest[i]));
            }

            return hexString.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new IllegalStateException(e.getMessage());
        }
    }
    /**
     * Method: hexDigit Purpose: convert a hex digit to a String, used by md5Sum.
     *
     * @param x
     *            the digit to translate
     * @return the hex code for the digit
     */
    static private String hexDigit(byte x)
    {
        StringBuffer sb = new StringBuffer();
        char c;

        // First nibble
        c = (char) ((x >> 4) & 0xf);
        if (c > 9)
        {
            c = (char) ((c - 10) + 'a');
        }
        else
        {
            c = (char) (c + '0');
        }

        sb.append(c);

        // Second nibble
        c = (char) (x & 0xf);
        if (c > 9)
        {
            c = (char) ((c - 10) + 'a');
        }
        else
        {
            c = (char) (c + '0');
        }

        sb.append(c);
        return sb.toString();
    }
    
}
