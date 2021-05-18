package io.ukihsoroy.feishu.auth.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.ukihsoroy.bean.ResponseEntity;

/**
 * <p>获取tenant access</p>
 * 改版后tenant和app的token是一样的了，使用哪一个api都可以
 * @author K.O
 * @email ko.shen@hotmail.com
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class) //开启驼峰转下划线
public class TenantAccessTokenResponseEntity extends ResponseEntity {

    private String tenantAccessToken;

    private int expire;

    public TenantAccessTokenResponseEntity(Integer code, String msg, String tenantAccessToken, int expire) {
        super(code, msg);
        this.tenantAccessToken = tenantAccessToken;
        this.expire = expire;
    }

    public TenantAccessTokenResponseEntity(String tenantAccessToken, int expire) {
        this.tenantAccessToken = tenantAccessToken;
        this.expire = expire;
    }

    public TenantAccessTokenResponseEntity() {
    }

    public String getTenantAccessToken() {
        return tenantAccessToken;
    }

    public void setTenantAccessToken(String tenantAccessToken) {
        this.tenantAccessToken = tenantAccessToken;
    }

    public int getExpire() {
        return expire;
    }

    public void setExpire(int expire) {
        this.expire = expire;
    }
}
