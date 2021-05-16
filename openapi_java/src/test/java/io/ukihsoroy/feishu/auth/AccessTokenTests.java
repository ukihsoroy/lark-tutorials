package io.ukihsoroy.feishu.auth;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.ukihsoroy.feishu.auth.entity.AccessTokenRequest;
import io.ukihsoroy.feishu.auth.entity.AppAccessTokenResponse;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class AccessTokenTests {

    private Retrofit retrofit;

    private final String appId = "";
    private final String appSecret = "";

    @BeforeEach
    public void buildRetrofit() {
        retrofit = new Builder()
                .baseUrl("https://open.feishu.cn/")
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

}
