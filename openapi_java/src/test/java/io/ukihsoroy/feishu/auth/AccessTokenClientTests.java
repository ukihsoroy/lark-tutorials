package io.ukihsoroy.feishu.auth;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.ukihsoroy.feishu.auth.entity.AccessTokenRequest;
import io.ukihsoroy.feishu.auth.entity.AppAccessTokenResponseEntity;
import io.ukihsoroy.feishu.auth.entity.TenantAccessTokenResponseEntity;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class AccessTokenClientTests {

    private Retrofit retrofit;

    private final String appId = "cli_a017ae4441b89013";
    private final String appSecret = "";

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
        AccessTokenClient accessTokenClient = retrofit.create(AccessTokenClient.class);
        Call<AppAccessTokenResponseEntity> appAccessTokenResponseCall =
                accessTokenClient.queryAppAccessToken(new AccessTokenRequest(appId, appSecret));
        Response<AppAccessTokenResponseEntity> execute = appAccessTokenResponseCall.execute();

        assert execute.isSuccessful() && execute.code() == 200 && null != execute.body();
        System.out.println(execute.body().getAppAccessToken());
    }

    @Test
    public void testQueryTenantAccessToken() throws IOException {
        AccessTokenClient accessTokenClient = retrofit.create(AccessTokenClient.class);
        Call<TenantAccessTokenResponseEntity> tenantAccessTokenResponseCall =
                accessTokenClient.queryTenantAccessToken(new AccessTokenRequest(appId, appSecret));
        Response<TenantAccessTokenResponseEntity> execute = tenantAccessTokenResponseCall.execute();

        assert execute.isSuccessful() && execute.code() == 200 && null != execute.body();
        System.out.println(execute.body().getTenantAccessToken());
    }

}
