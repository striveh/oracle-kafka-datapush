package com.striveh.pushdata.vo;

import java.util.HashMap;
import java.util.Map;

public class Result<T> {
    private T data;
    private Integer code;
    private String msg;

    public static <T> Result<T> ok() {
        return okWith( null, CodeEnum.SUCCESS.getCode(), CodeEnum.SUCCESS.getMsg());
    }

    public static <T> Result<T> ok(String msg) {
        return okWith(null, CodeEnum.SUCCESS.getCode(), msg);
    }

    public static <T> Result<T> ok(T model, String msg) {
        return okWith(model, CodeEnum.SUCCESS.getCode(), msg);
    }

    public static <T> Result<T> ok(T model) {
        return okWith(model, CodeEnum.SUCCESS.getCode(), "");
    }

    public static <T> Result<T> okWith(T data, Integer code, String msg) {
        return new Result(data, code, msg);
    }

    public static <T> Result<T> error() {
        return errorWith(null, CodeEnum.ERROR.getCode(), CodeEnum.ERROR.getMsg());
    }

    public static <T> Result<T> error(String msg) {
        return errorWith(null, CodeEnum.ERROR.getCode(), msg);
    }

    public static <T> Result<T> error(T model, String msg) {
        return errorWith(model, CodeEnum.ERROR.getCode(), msg);
    }

    public static <T> Result<T> errorWith(T data, Integer code, String msg) {
        return new Result(data, code, msg);
    }

    public static Result<Map<String, Object>> list(Object data) {
        Map<String, Object> resp = new HashMap(1);
        resp.put("list", data);
        return new Result(resp, CodeEnum.SUCCESS.getCode(), "");
    }

    public Result(final T data, final Integer code, final String msg) {
        this.data = data;
        this.code = code;
        this.msg = msg;
    }

    public Result() {
    }

    public T getData() {
        return this.data;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setData(final T data) {
        this.data = data;
    }

    public void setCode(final Integer code) {
        this.code = code;
    }

    public void setMsg(final String msg) {
        this.msg = msg;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Result)) {
            return false;
        } else {
            Result<?> other = (Result) o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label47:
                {
                    Object this$data = this.getData();
                    Object other$data = other.getData();
                    if (this$data == null) {
                        if (other$data == null) {
                            break label47;
                        }
                    } else if (this$data.equals(other$data)) {
                        break label47;
                    }

                    return false;
                }

                Object this$code = this.getCode();
                Object other$code = other.getCode();
                if (this$code == null) {
                    if (other$code != null) {
                        return false;
                    }
                } else if (!this$code.equals(other$code)) {
                    return false;
                }

                Object this$msg = this.getMsg();
                Object other$msg = other.getMsg();
                if (this$msg == null) {
                    if (other$msg != null) {
                        return false;
                    }
                } else if (!this$msg.equals(other$msg)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Result;
    }

    public int hashCode() {
        int result = 1;
        Object $data = this.getData();
        result = result * 59 + ($data == null ? 43 : $data.hashCode());
        Object $code = this.getCode();
        result = result * 59 + ($code == null ? 43 : $code.hashCode());
        Object $msg = this.getMsg();
        result = result * 59 + ($msg == null ? 43 : $msg.hashCode());
        return result;
    }

    public String toString() {
        return "Result(data=" + this.getData() + ", code=" + this.getCode() + ", msg=" + this.getMsg() + ")";
    }
}