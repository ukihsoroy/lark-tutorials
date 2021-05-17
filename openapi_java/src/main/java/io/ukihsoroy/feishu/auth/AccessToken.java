package io.ukihsoroy.feishu.auth;

import io.ukihsoroy.feishu.auth.entity.AccessTokenRequest;
import io.ukihsoroy.feishu.auth.entity.AppAccessTokenResponse;
import io.ukihsoroy.feishu.auth.entity.TenantAccessTokenResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * 企业自建应用，获取access token
 * @author K.O
 * @email ko.shen@hotmail.com
 */
public interface AccessToken {

    @POST("auth/v3/app_access_token/internal/")
    Call<AppAccessTokenResponse> queryAppAccessToken(@Body AccessTokenRequest request);

    @POST("auth/v3/tenant_access_token/internal/")
    Call<TenantAccessTokenResponse> queryTenantAccessToken(@Body AccessTokenRequest request);

}
