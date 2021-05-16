package io.ukihsoroy.feishu.auth;

import io.ukihsoroy.feishu.auth.entity.AccessTokenRequest;
import io.ukihsoroy.feishu.auth.entity.AppAccessTokenResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AccessToken {

    @POST("open-apis/auth/v3/app_access_token/internal/")
    Call<AppAccessTokenResponse> queryAppAccessToken(@Body AccessTokenRequest request);

}
