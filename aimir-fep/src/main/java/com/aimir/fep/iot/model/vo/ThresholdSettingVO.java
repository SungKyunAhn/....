package com.aimir.fep.iot.model.vo;

/**
 * 사용자 권한을 저장하는 객체
 * Created with IntelliJ IDEA.
 * User: kaze
 * Date: 15. 1. 15
 * Time: 오후 1:27
 * To change this template use File | Settings | File Templates.
 */
public class ThresholdSettingVO extends PageVO {
    private Integer num;
    private String loginId;
    private String TargetCd;
    private Integer max;
    private Integer min;
    private String dsuj;
    private String formatDsuj;

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getTargetCd() {
        return TargetCd;
    }

    public void setTargetCd(String targetCd) {
        TargetCd = targetCd;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    public String getDsuj() {
        return dsuj;
    }

    public void setDsuj(String dsuj) {
//        this.formatDsuj = DateUtil.convertDate(this.dsuj, "yyyyMMddHHmmss", "yyyy-MM-dd HH:mm:ss");
        this.dsuj = dsuj;
    }

    public String getFormatDsuj() {
        return formatDsuj;
    }

    public void setDate(String formatDsuj) {
        this.formatDsuj = formatDsuj;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ThresholdSettingVO that = (ThresholdSettingVO) o;

        if (TargetCd != null ? !TargetCd.equals(that.TargetCd) : that.TargetCd != null) return false;
        if (dsuj != null ? !dsuj.equals(that.dsuj) : that.dsuj != null) return false;
        if (loginId != null ? !loginId.equals(that.loginId) : that.loginId != null) return false;
        if (max != null ? !max.equals(that.max) : that.max != null) return false;
        if (min != null ? !min.equals(that.min) : that.min != null) return false;
        if (num != null ? !num.equals(that.num) : that.num != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = num != null ? num.hashCode() : 0;
        result = 31 * result + (loginId != null ? loginId.hashCode() : 0);
        result = 31 * result + (TargetCd != null ? TargetCd.hashCode() : 0);
        result = 31 * result + (max != null ? max.hashCode() : 0);
        result = 31 * result + (min != null ? min.hashCode() : 0);
        result = 31 * result + (dsuj != null ? dsuj.hashCode() : 0);
        return result;
    }
}
