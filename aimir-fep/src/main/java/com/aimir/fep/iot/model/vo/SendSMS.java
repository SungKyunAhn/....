package com.aimir.fep.iot.model.vo;

/**
 * Created with IntelliJ IDEA.
 * User: jhkim
 * Date: 15. 1. 17
 * Time: 오전 2:58
 * To change this template use File | Settings | File Templates.
 */
public class SendSMS {
    private String name;
    private String tel;
    private String yn;
    private String message;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getYn() {
        return yn;
    }

    public void setYn(String yn) {
        this.yn = yn;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
