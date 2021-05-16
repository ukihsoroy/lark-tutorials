package io.ukihsoroy.bean;

/**
 * 返回消息通用实体
 * @author K.O
 */
public class ResponseEntity {

    private Integer code;

    private String msg;

    public ResponseEntity(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResponseEntity() {}

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "ResponseEntity{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}
