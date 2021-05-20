package io.ukihsoroy.feishu.user;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.ukihsoroy.bean.Response;
import io.ukihsoroy.feishu.contact.user.UserClient;
import io.ukihsoroy.feishu.contact.user.entity.UserView;
import io.ukihsoroy.feishu.contact.user.entity.UserRequest;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * <p></p>
 *
 * @author K.O
 * @email ko.shen@hotmail.com
 */
public class UserClientTests {

    private Retrofit retrofit;

    @BeforeEach
    public void buildRetrofit() {
        //声明日志类
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        //设定日志级别
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        //自定义OkHttpClient
        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
        //添加拦截器
        okHttpClient.addInterceptor(httpLoggingInterceptor);
        retrofit = new Builder()
                .baseUrl("https://open.feishu.cn/open-apis/")
                //添加json转换器
                .addConverterFactory(JacksonConverterFactory.create())
                .client(okHttpClient.build())
                .build();
    }

    @Test
    public void testUserCreate() throws IOException {
        UserClient userClient = retrofit.create(UserClient.class);
        UserRequest request = new UserRequest();
        request.setName("托尼");
        request.setDepartmentIds(new String[]{"0"});
        request.setMobile("13600000005");
        request.setEmployeeType(1);
        Map<String, String> params = new HashMap<>();
        params.put("user_id_type", "user_id");
        retrofit2.Response<Response<UserView>> response = userClient
                .create("Bearer t-a970ef88ef5255cb5f2d2e916133eaf982dcfc03", params, request).execute();
        assert response.isSuccessful() && response.code() == 200 && null != response.body();
        System.out.println(response.body().getData().getUser().getName());
    }

    @Test
    public void testQueryUser() throws IOException {
        UserClient userClient = retrofit.create(UserClient.class);
        Map<String, String> params = new HashMap<>();
        params.put("user_id_type", "user_id");
        retrofit2.Response<Response<UserView>> response = userClient
                .queryUser("Bearer t-", "58f289cf", params).execute();
        assert response.isSuccessful() && response.code() == 200 && null != response.body();
        System.out.println(response.body().getData().getUser().getName());
    }

    @Test
    public void testQueryUsers() throws IOException {
        UserClient userClient = retrofit.create(UserClient.class);
        Map<String, String> params = new HashMap<>();
        params.put("user_id_type", "user_id");
        retrofit2.Response<Response<UserView>> response = userClient
                .queryUsers("Bearer t-", params).execute();
        assert response.isSuccessful() && response.code() == 200 && null != response.body();
        System.out.println(response.body().getData().getItems().size());
    }

}
