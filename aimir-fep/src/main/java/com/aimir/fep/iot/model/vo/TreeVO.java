package com.aimir.fep.iot.model.vo;

/**
 * 좌측 트리에 해당하는 모델
 */
public class TreeVO  {
    /**
     * 노드 타입을 의미(root -> PowerStationGroup-> PowerStation-> PowerGenerator-> EnergyMeter가 존재).
     */

    String nodetype;
    /**
     * 노드의 유니크 id (nodetype_해당노드의 code 형태로 만들어준다. Ex) PowerStation_1_1_1)
     * 단 root 노드의 경우 root를 nodeid로 사용함
     */
    String nodeid;

    /**
     * 해당 노드의 code 값이 들어간다.
     * DB에 입력된 1.1.1.1이 js에 들어갈때는 1_1_1_1로 변경되어 들어감
     */
    String nodeidr;

    /**
     * 노드의 이름
     */
    String nodename;

    /**
     * 부모 노드의 ID .
     * 단 root인 경우는 -1
     */
    String pnodeid;

    public String getPnodeidr() {
        return pnodeidr;
    }

    public void setPnodeidr(String pnodeidr) {
        this.pnodeidr = pnodeidr;
    }

    /**
     * 부모 노드의 code 값이 들어간다.
     * DB에 입력된 1.1.1.1이 js에 들어갈때는 1_1_1_1로 변경되어 들어감
     */
    String pnodeidr;

    /**
     * Gets nodetype.
     *
     * @return the nodetype
     */
    public String getNodetype() {
        return nodetype;
    }

    /**
     * Sets nodetype.
     *
     * @param nodetype the nodetype
     */
    public void setNodetype(String nodetype) {
        this.nodetype = nodetype;
    }

    /**
     * Gets nodeid.
     *
     * @return the nodeid
     */
    public String getNodeid() {
        return nodeid;
    }

    /**
     * Sets nodeid.
     *
     * @param nodeid the nodeid
     */
    public void setNodeid(String nodeid) {
        this.nodeid = nodeid;
    }

    /**
     * Gets pnodeid.
     *
     * @return the pnodeid
     */
    public String getPnodeid() {
        return pnodeid;
    }

    /**
     * Gets nodename.
     *
     * @return the nodename
     */
    public String getNodename() {
        return nodename;
    }

    /**
     * Sets nodename.
     *
     * @param nodename the nodename
     */
    public void setNodename(String nodename) {
        this.nodename = nodename;
    }

    /**
     * Sets pnodeid.
     *
     * @param pnodeid the pnodeid
     */
    public void setPnodeid(String pnodeid) {
        this.pnodeid = pnodeid;
    }

    /**
     * Gets nodeidr.
     *
     * @return the nodeidr
     */
    public String getNodeidr() {
        return nodeidr;
    }

    /**
     * Sets nodeidr.
     *
     * @param nodeidr the nodeidr
     */
    public void setNodeidr(String nodeidr) {
        this.nodeidr = nodeidr;
    }
}
