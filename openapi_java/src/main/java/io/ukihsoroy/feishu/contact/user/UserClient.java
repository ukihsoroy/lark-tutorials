package io.ukihsoroy.feishu.contact.user;

import java.util.Map;

import io.ukihsoroy.bean.Response;
import io.ukihsoroy.feishu.contact.user.entity.UserView;
import io.ukihsoroy.feishu.contact.user.entity.UserRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * <p>用户访问接口</p>
 *
 * @author K.O
 * @email ko.shen@hotmail.com
 */
public interface UserClient {

    @Headers({
            "Content-Type: application/json; charset=utf-8"
    })
    @POST("contact/v3/users")
    Call<Response<UserView>> create(
            @Header("Authorization") String authorization,
            @QueryMap Map<String, String> params,
            @Body UserRequest user);

    @GET("contact/v3/users/{user_id}")
    Call<Response<UserView>> queryUser(@Header("Authorization") String authorization,
                                       @Path("user_id") String userId,
                                       @QueryMap Map<String, String> params);

    @GET("contact/v3/users")
    Call<Response<UserView>> queryUsers(@Header("Authorization") String authorization,
                                        @QueryMap Map<String, String> params);
}
