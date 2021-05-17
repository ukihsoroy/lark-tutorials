package io.ukihsoroy.feishu.auth;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.ukihsoroy.feishu.auth.entity.AccessTokenRequest;
import io.ukihsoroy.feishu.auth.entity.AppAccessTokenResponse;
import io.ukihsoroy.feishu.auth.entity.TenantAccessTokenResponse;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class AccessTokenTests {

    private Retrofit retrofit;

    private final String appId = "cli_a017ae4441b89013";
    private final String appSecret = "h7d32n1J4QppFcUd3nFALbJhzvamT5lG";

    @BeforeEach
    public void buildRetrofit() {
        retrofit = new Builder()
                .baseUrl("https://open.feishu.cn/open-apis/")
                //添加json转换器
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }

    @Test
    public void testQueryAppAccessToken() throws IOException {
        AccessToken accessToken = retrofit.create(AccessToken.class);
        Call<AppAccessTokenResponse> appAccessTokenResponseCall =
                accessToken.queryAppAccessToken(new AccessTokenRequest(appId, appSecret));
        Response<AppAccessTokenResponse> execute = appAccessTokenResponseCall.execute();

        assert execute.isSuccessful() && execute.code() == 200 && null != execute.body();
        System.out.println(execute.body().getAppAccessToken());
    }

    @Test
    public void testQueryTenantAccessToken() throws IOException {
        AccessToken accessToken = retrofit.create(AccessToken.class);
        Call<TenantAccessTokenResponse> tenantAccessTokenResponseCall =
                accessToken.queryTenantAccessToken(new AccessTokenRequest(appId, appSecret));
        Response<TenantAccessTokenResponse> execute = tenantAccessTokenResponseCall.execute();

        assert execute.isSuccessful() && execute.code() == 200 && null != execute.body();
        System.out.println(execute.body().getTenantAccessToken());
    }

}
