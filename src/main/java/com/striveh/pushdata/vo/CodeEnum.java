package com.striveh.pushdata.vo;

public enum CodeEnum {
    SUCCESS(200, "请求成功"),
    ERROR(500, "请求失败");

    private Integer code;
    private String msg;

    private CodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }
}