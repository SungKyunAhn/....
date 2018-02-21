package com.aimir.fep.iot.model.vo;

import java.util.List;


public class MenuVO {
	  /** 메뉴번호 */
    private int menuNo;
    /** 상위메뉴번호 */
    private int upperMenuId;
    /** 메뉴명 */
    private String menuNm;
    /** LINK URL */
    private String chkURL;

    List<MenuVO> children;

    public MenuVO(String no, String id, String menuNm, String chkURL) {

        try {
            int menuNo = Integer.parseInt(no);
            int upperMenuId = 0;
            if (!"Null".equals(id)) {
            	upperMenuId = Integer.parseInt(id);
            }
            this.menuNo = menuNo;
            this.upperMenuId = upperMenuId;
            this.menuNm = menuNm;
            this.chkURL = chkURL;
        } catch (Exception e) {
            System.out.println("Unable to create Menu as the data is " + menuNo + " " + upperMenuId + " " + menuNm);
        }
    }

	public List<MenuVO> getChildren() {
		return children;
	}

	public void setChildren(List<MenuVO> children) {
		this.children = children;
	}

	public int getMenuNo() {
		return menuNo;
	}

	public void setMenuNo(int menuNo) {
		this.menuNo = menuNo;
	}

	public int getUpperMenuId() {
		return upperMenuId;
	}

	public void setUpperMenuId(int upperMenuId) {
		this.upperMenuId = upperMenuId;
	}

	public String getMenuNm() {
		return menuNm;
	}

	public void setMenuNm(String menuNm) {
		this.menuNm = menuNm;
	}

	public String getChkURL() {
		return chkURL;
	}

	public void setChkURL(String chkURL) {
		this.chkURL = chkURL;
	}

}
