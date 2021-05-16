package io.ukihsoroy.feishu.auth.entity;

import java.io.Serializable;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * 签名实体类
 * @author K.O
 *
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class) //开启驼峰转下划线
public class AccessTokenRequest implements Serializable {

    private String appId;

    private String appSecret;

    public AccessTokenRequest(String appId, String appSecret) {
        this.appId = appId;
        this.appSecret = appSecret;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }
}
