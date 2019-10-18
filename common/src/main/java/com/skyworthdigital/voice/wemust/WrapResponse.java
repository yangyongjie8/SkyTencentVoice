package com.skyworthdigital.voice.wemust;

public class WrapResponse {
    private String en_data;
    private String cli_sys_id;
    private String sign;

    public String getEn_data() {
        return en_data;
    }

    public void setEn_data(String en_data) {
        this.en_data = en_data;
    }

    public String getCli_sys_id() {
        return cli_sys_id;
    }

    public void setCli_sys_id(String cli_sys_id) {
        this.cli_sys_id = cli_sys_id;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
