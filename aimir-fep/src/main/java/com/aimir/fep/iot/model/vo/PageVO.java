package com.aimir.fep.iot.model.vo;

//import egovframework.rte.fdl.property.EgovPropertyService;

import javax.annotation.Resource;

/**
 * 전자정부 프레임 워크의 페이징 관련 변수만 모은 객체
 * 페이징이 필요한 VO 들은 이 객체를 상속 받아 구현하도록 한다.
 * Created with IntelliJ IDEA.
 * User: kaze
 * Date: 15. 1. 15
 * Time: 오후 1:34
 * To change this template use File | Settings | File Templates.
 */
public class PageVO {
    /** EgovPropertyService */
    //context-properties.xml을 사용함
    /*@Resource(name = "propertiesService")
    protected EgovPropertyService propertiesService; by ask*/

    /** 현재 페이지 */
    private int pageIndex = 1;
    /** 시작 인덱스 */
    private int firstIndex = 1;
    /** 끝 인덱스 */
    private int lastIndex = 1;

    /** 페이지 갯수 */
    private int pageUnit; // properties에서 설정
    /** 페이지 사이즈 */
    private int pageSize;//properties에서 설정
    /**페이지 별 레코드 갯수  */
    private int recordCountPerPage;//context-properties.xml에서 설정



    /**
     * Gets page index.
     *
     * @return the page index
     */
    public int getPageIndex() {
        return pageIndex;
    }

    /**
     * Sets page index.
     *
     * @param pageIndex the page index
     */
    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    /**
     * Gets page unit.
     *
     * @return the page unit
     */
    public int getPageUnit() {
        return pageUnit;
    }

    /**
     * Sets page unit.
     *
     * @param pageUnit the page unit
     */
    public void setPageUnit(int pageUnit) {
        this.pageUnit = pageUnit;
    }

    /**
     * Gets page size.
     *
     * @return the page size
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * Sets page size.
     *
     * @param pageSize the page size
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * Gets first index.
     *
     * @return the first index
     */
    public int getFirstIndex() {
        return firstIndex;
    }

    /**
     * Sets first index.
     *
     * @param firstIndex the first index
     */
    public void setFirstIndex(int firstIndex) {
        this.firstIndex = firstIndex;
    }

    /**
     * Gets last index.
     *
     * @return the last index
     */
    public int getLastIndex() {
        return lastIndex;
    }

    /**
     * Sets last index.
     *
     * @param lastIndex the last index
     */
    public void setLastIndex(int lastIndex) {
        this.lastIndex = lastIndex;
    }

    /**
     * Gets record count per page.
     *
     * @return the record count per page
     */
    public int getRecordCountPerPage() {
        return recordCountPerPage;
    }

    /**
     * Sets record count per page.
     *
     * @param recordCountPerPage the record count per page
     */
    public void setRecordCountPerPage(int recordCountPerPage) {
        this.recordCountPerPage = recordCountPerPage;
    }
}
