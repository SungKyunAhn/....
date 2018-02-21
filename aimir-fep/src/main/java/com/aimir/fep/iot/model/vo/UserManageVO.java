package com.aimir.fep.iot.model.vo;

/**
 * The type User manage vO.
 */
public class UserManageVO extends UserDefaultVO {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/** 이전비밀번호 - 비밀번호 변경시 사용*/
    private String oldPassword = "";

	/**
	 * 회원 ID
	 */
	private String userId;
	/**
	 * 회원명
	 */
	private String userName;
	/**
	 * 회원상태
	 */
	private String enabled;

	/**
	 * 비밀번호
	 */
	private String password;

	/**
	 * 가입 일자
	 */
	private String createDate;

	/**
	 * 이메일주소
	 */
	private String email;

    /**
     * 롤아이디
     */
    private String roleId;

    /**
     * 롤네임
     */
    private String roleName;

    /**
     * 사용자전화번호
     */
    private String mobile;

    /**
     * 문자 수신 여부 (0:미수신, 1:수신)
     */
    private String receiveType;

    /**
     * 문자 수신 시작 시간
     */
    private int receiveFrom;

    /**
     * 문자 수신 종료 시간
     */
    private int receiveTo;

    /**
     * 조회 가능한 본부 코드(전체 조회 가능한 경우 null 입력)
     */
    private String locationCd;

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    /**
	 * oldPassword attribute 값을  리턴한다.
	 * @return String
	 */
	public String getOldPassword() {
		return oldPassword;
	}
	/**
	 * oldPassword attribute 값을 설정한다.
	 * @param oldPassword String
	 */
	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}


	/**
	 * userId attribute 값을  리턴한다.
	 * @return String
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * userId attribute 값을 설정한다.
	 * @param userId String
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	/**
	 * userName attribute 값을  리턴한다.
	 * @return String
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * userName attribute 값을 설정한다.
	 * @param userName String
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * enabled attribute 값을  리턴한다.
	 * @return String
	 */
	public String getEnabled() {
		return enabled;
	}
	/**
	 * enabled attribute 값을 설정한다.
	 * @param enabled String
	 */
	public void setEnabled(String enabled) {
		this.enabled = enabled;
	}

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    /**
	 * password attribute 값을  리턴한다.
	 * @return String

	 */
	public String getPassword() {
		return password;
	}
	/**
	 * password attribute 값을 설정한다.
	 * @param password String
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * email attribute 값을  리턴한다.
	 * @return String
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * email attribute 값을 설정한다.
	 * @param email String
	 */
	public void setEmail(String email) {
		this.email = email;
	}

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getReceiveType() {
        return receiveType;
    }

    public void setReceiveType(String receiveType) {
        this.receiveType = receiveType;
    }

    public int getReceiveFrom() {
        return receiveFrom;
    }

    public void setReceiveFrom(int receiveFrom) {
        this.receiveFrom = receiveFrom;
    }

    public int getReceiveTo() {
        return receiveTo;
    }

    public void setReceiveTo(int receiveTo) {
        this.receiveTo = receiveTo;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getLocationCd() {
        return locationCd;
    }

    public void setLocationCd(String locationCd) {
        this.locationCd = locationCd;
    }
}