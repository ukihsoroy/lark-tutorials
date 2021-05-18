package io.ukihsoroy.feishu.auth.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.ukihsoroy.bean.ResponseEntity;

/**
 * 返回实体类
 * @author K.O
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class) //开启驼峰转下划线
public class AppAccessTokenResponseEntity extends ResponseEntity {

    private String appAccessToken;

    private String tenantAccessToken;

    private int expire;

    public AppAccessTokenResponseEntity(Integer code, String msg, String appAccessToken, String tenantAccessToken, int expire) {
        super(code, msg);
        this.appAccessToken = appAccessToken;
        this.tenantAccessToken = tenantAccessToken;
        this.expire = expire;
    }

    public AppAccessTokenResponseEntity(String appAccessToken, String tenantAccessToken, int expire) {
        this.appAccessToken = appAccessToken;
        this.tenantAccessToken = tenantAccessToken;
        this.expire = expire;
    }

    public AppAccessTokenResponseEntity() {}

    public String getAppAccessToken() {
        return appAccessToken;
    }

    public void setAppAccessToken(String appAccessToken) {
        this.appAccessToken = appAccessToken;
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
