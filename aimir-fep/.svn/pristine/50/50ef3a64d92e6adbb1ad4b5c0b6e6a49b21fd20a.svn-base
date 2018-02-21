package com.aimir.fep.meter.parser.rdata;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.parser.rdata.RDataConstant.ServiceType;
import com.aimir.fep.meter.parser.rdata.RDataConstant.Vendor;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.Hex;

/**
 * InventoryData Format
 * 
 * @author kaze
 * 
 */
public class InventoryRData extends RData {
	private static Log log = LogFactory.getLog(InventoryRData.class);
	private byte[] SHORTID = new byte[4];
	private byte[] IDTYPE = new byte[1];
	private byte[] IDLENGTH = new byte[1];
	private byte[] ID;
	private byte[] PORTNUM = new byte[1];
	private byte[] METERIDTYPE = new byte[1];
	private byte[] METERIDLENGTH = new byte[1];
	private byte[] METERID;
	private byte[] PARSERTYPE = new byte[4];
	private byte[] SERVICETYPE = new byte[1];
	private byte[] VENDOR = new byte[1];
	private byte[] MODELLENGTH = new byte[1];
	private byte[] MODEL;
	private byte[] HWVERSION = new byte[2];
	private byte[] SWVERSION = new byte[2];
	private byte[] SWBUILD = new byte[1];
	private byte[] INSTALLDATETIME = new byte[7];
	private byte[] LPPERIOD = new byte[1];

	private int shortId;
	private RDataConstant.InventoryIdType idType;
	private int idLength;
	private String id;
	private int portNum;
	private RDataConstant.InventoryMeterIdType meterIdType;
	private int meterIdLength;
	private String meterId;
	private RDataConstant.InventoryParserType parserType;
	private ServiceType serviceType;
	private Vendor vendor;
	private int modelLength;
	private String model;
	private String hwVersion;
	private String swVersion;
	private String swBuild;
	private String installDateTime;
	private int lpPeriod;

	public void parsingPayLoad() throws Exception{
		int pos = 0;

		// ----------------------------
		// [step1] Raw Data를 잘라서 Byte에 지정
		// ----------------------------
		System.arraycopy(payload, pos, SHORTID, 0, SHORTID.length);
		pos += SHORTID.length;
		System.arraycopy(payload, pos, IDTYPE, 0, IDTYPE.length);
		pos += IDTYPE.length;
		System.arraycopy(payload, pos, IDLENGTH, 0, IDLENGTH.length);
		pos += IDLENGTH.length;

		ID = new byte[DataUtil.getIntToBytes(IDLENGTH)];
		System.arraycopy(payload, pos, ID, 0, ID.length);
		pos += ID.length;

		System.arraycopy(payload, pos, PORTNUM, 0, PORTNUM.length);
		pos += PORTNUM.length;
		System.arraycopy(payload, pos, METERIDTYPE, 0, METERIDTYPE.length);
		pos += METERIDTYPE.length;
		System.arraycopy(payload, pos, METERIDLENGTH, 0, METERIDLENGTH.length);
		pos += METERIDLENGTH.length;

		METERID = new byte[DataUtil.getIntToBytes(METERIDLENGTH)];
		System.arraycopy(payload, pos, METERID, 0, METERID.length);
		pos += METERID.length;

		System.arraycopy(payload, pos, PARSERTYPE, 0, PARSERTYPE.length);
		pos += PARSERTYPE.length;
		System.arraycopy(payload, pos, SERVICETYPE, 0, SERVICETYPE.length);
		pos += SERVICETYPE.length;
		System.arraycopy(payload, pos, VENDOR, 0, VENDOR.length);
		pos += VENDOR.length;
		System.arraycopy(payload, pos, MODELLENGTH, 0, MODELLENGTH.length);
		pos += MODELLENGTH.length;

		MODEL = new byte[DataUtil.getIntToBytes(MODELLENGTH)];
		System.arraycopy(payload, pos, MODEL, 0, MODEL.length);
		pos += MODEL.length;

		System.arraycopy(payload, pos, HWVERSION, 0, HWVERSION.length);
		pos += HWVERSION.length;
		System.arraycopy(payload, pos, SWVERSION, 0, SWVERSION.length);
		pos += SWVERSION.length;
		System.arraycopy(payload, pos, SWBUILD, 0, SWBUILD.length);
		pos += SWBUILD.length;
		System.arraycopy(payload, pos, INSTALLDATETIME, 0, INSTALLDATETIME.length);
		pos += INSTALLDATETIME.length;

		// 2012.04.24 by elevas
		System.arraycopy(payload, pos, LPPERIOD, 0, LPPERIOD.length);
		pos += LPPERIOD.length;
		
		// ------------------------------
		// [step2] 각각 잘린 Byte로 부터 데이터를 파싱
		// ------------------------------
		shortId = DataUtil.getIntToBytes(SHORTID);
		log.debug("SHORT_ID[" + shortId + "]");
		idType = RDataConstant.getInventoryIdType(DataUtil.getIntToBytes(IDTYPE));
		log.debug("ID_TYPE[" + idType.name() + "]");
		if (idType == RDataConstant.InventoryIdType.ASCII) {
			id = DataUtil.getString(ID);
		} else if (idType == RDataConstant.InventoryIdType.BCD) {
			id = DataUtil.getBCDtoBytes(ID);
		} else if (idType == RDataConstant.InventoryIdType.ByteStream) {

		} else if (idType == RDataConstant.InventoryIdType.Eui64) {
			id = Hex.decode(ID);
		} else if (idType == RDataConstant.InventoryIdType.IpV4) {

		} else if (idType == RDataConstant.InventoryIdType.IpV6) {

		} else if (idType == RDataConstant.InventoryIdType.Mac) {

		} else if (idType == RDataConstant.InventoryIdType.SignedNumber) {

		} else if (idType == RDataConstant.InventoryIdType.UnSignedNumber) {
			id = DataUtil.getIntToBytes(ID) + "";
		}
		log.debug("ID[" + id + "]");
		portNum = DataUtil.getIntToBytes(PORTNUM);
		log.debug("PORT_NUM[" + portNum + "]");
		meterIdType = RDataConstant.getInventoryMeterIdType(DataUtil.getIntToBytes(METERIDTYPE));
		log.debug("METERID_TYPE[" + meterIdType.name() + "]");
		meterId = DataUtil.getString(METERID).trim();
		log.debug("METER_ID[" + meterId + "]");
		parserType = RDataConstant.getInventoryParserType(DataUtil.getIntToBytes(PARSERTYPE));
		log.debug("PARSER_TYPE[" + parserType.name() + "]");
		serviceType = RDataConstant.getServiceType(DataUtil.getIntToBytes(SERVICETYPE));
		log.debug("SERVICE_TYPE[" + serviceType + "]");
		vendor = RDataConstant.getVendor(DataUtil.getIntToBytes(VENDOR));
		log.debug("VENDOR[" + vendor + "]");
		model = DataUtil.getString(MODEL);
		log.debug("MODEL[" + model + "]");
		hwVersion = DataUtil.getIntToByte(HWVERSION[0])+"."+DataUtil.getIntToByte(HWVERSION[1]);
		log.debug("HW_VERSION[" + hwVersion + "]");
		swVersion = DataUtil.getIntToByte(SWVERSION[0])+"."+DataUtil.getIntToByte(SWVERSION[1]);
		log.debug("SW_VERSION[" + swVersion + "]");
		swBuild = DataUtil.getIntToBytes(SWBUILD)+"";
		log.debug("SW_BUILD[" + swBuild + "]");
		installDateTime = DataUtil.getTimeStamp(INSTALLDATETIME);
		log.debug("INSTALL_DATE[" + installDateTime+ "]");
		lpPeriod = DataUtil.getIntToBytes(LPPERIOD);
		log.debug("LP_PERIOD[" + lpPeriod + "]");
		// lp period가 0이 되면 안된다. 디폴트 값을 가져오도록 한다. by elevas, 2012.06.11
		if (lpPeriod == 0) {
		    lpPeriod = 60 / Integer.parseInt(FMPProperty.getProperty("lp.resolution.default"));
		    log.debug("LP_PERIOD[" + lpPeriod + "]");
		}
	}

	/**
	 * DCU에서 할당하는 장비 관리 ID
	 * @return the shortId
	 */
	public int getShortId() {
		return shortId;
	}

	/**
	 * 장비 ID Type
	 * @return the idType
	 */
	public RDataConstant.InventoryIdType getIdType() {
		return idType;
	}

	/**
	 * ID Length
	 * @return the idLength
	 */
	public int getIdLength() {
		return idLength;
	}

	/**
	 * 장비에 대한 ID
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * 내부 Port Number(장비가 서브 장비를 지니지 않은 경우는 0)
	 * @return the portNum
	 */
	public int getPortNum() {
		return portNum;
	}

	/**
	 * Meter ID Type
	 * @return the meterIdType
	 */
	public RDataConstant.InventoryMeterIdType getMeterIdType() {
		return meterIdType;
	}

	/**
	 * ID Length
	 * @return the meterIdLength
	 */
	public int getMeterIdLength() {
		return meterIdLength;
	}

	/**
	 * Meter ID
	 * @return the meterId
	 */
	public String getMeterId() {
		return meterId;
	}

	/**
	 * Meter Parser Type
	 * @return the parserType
	 */
	public RDataConstant.InventoryParserType getParserType() {
		return parserType;
	}

	/**
	 * Service Type
	 * @return the serviceType
	 */
	public ServiceType getServiceType() {
		return serviceType;
	}

	/**
	 * Meter Vendor
	 * @return the vendor
	 */
	public Vendor getVendor() {
		return vendor;
	}

	/**
	 * Model String Length
	 * @return the modelLength
	 */
	public int getModelLength() {
		return modelLength;
	}

	/**
	 * Device Model(NODE_KIND)
	 * @return the model
	 */
	public String getModel() {
		return model;
	}

	/**
	 * Hardware Version(first byte:major version, second byte: minor version)
	 * @return the hwVersion
	 */
	public String getHwVersion() {
		return hwVersion;
	}

	/**
	 * Software Version(first byte:major version, second byte: minor version)
	 * @return the swVersion
	 */
	public String getSwVersion() {
		return swVersion;
	}

	/**
	 * Software Build Number
	 * @return the swBuild
	 */
	public String getSwBuild() {
		return swBuild;
	}

	/**
	 * 설치된 시간(yyyymmddhhmmss 포맷)
	 * @return the installDateTime
	 */
	public String getInstallDateTime() {
		return installDateTime;
	}

	/**
	 * LP 주기 (60을 이 주기로 나누면 된다.)
	 * @return the lp period
	 */
	public int getLpPeriod() {
        return lpPeriod;
    }

    public void setLpPeriod(int lpPeriod) {
        this.lpPeriod = lpPeriod;
    }

    @Override
	public String toString() {
		return "InventoryRData [shortId=" + shortId + ", idType=" + idType + ", idLength=" + idLength + ", id=" + id + ", portNum=" + portNum + ", meterIdType=" + meterIdType + ", meterIdLength=" + meterIdLength + ", meterId=" + meterId
				+ ", parserType=" + parserType + ", serviceType=" + serviceType + ", vendor=" + vendor + ", modelLength=" + modelLength + ", model=" + model + ", hwVersion=" + hwVersion + ", swVersion=" + swVersion + ", swBuild=" + swBuild
				+ ", installDateTime=" + installDateTime + ", lpPeriod=" + lpPeriod + "]";
	};	
}
