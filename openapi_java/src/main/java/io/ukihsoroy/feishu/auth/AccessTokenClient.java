package io.ukihsoroy.feishu.auth;

import io.ukihsoroy.feishu.auth.entity.AccessTokenRequest;
import io.ukihsoroy.feishu.auth.entity.AppAccessTokenResponseEntity;
import io.ukihsoroy.feishu.auth.entity.TenantAccessTokenResponseEntity;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * 企业自建应用，获取access token
 * @author K.O
 * @email ko.shen@hotmail.com
 */
public interface AccessTokenClient {

    @Headers({
            "Content-Type: application/json; charset=utf-8"
    })
    @POST("auth/v3/app_access_token/internal/")
    Call<AppAccessTokenResponseEntity> queryAppAccessToken(@Body AccessTokenRequest request);

    @Headers({
            "Content-Type: application/json; charset=utf-8"
    })
    @POST("auth/v3/tenant_access_token/internal/")
    Call<TenantAccessTokenResponseEntity> queryTenantAccessToken(@Body AccessTokenRequest request);

}
