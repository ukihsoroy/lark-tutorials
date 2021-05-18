package io.ukihsoroy.feishu.contact.user;

import java.util.Map;

import io.ukihsoroy.bean.Response;
import io.ukihsoroy.feishu.contact.user.entity.CreateUserView;
import io.ukihsoroy.feishu.contact.user.entity.UserRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
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
    Call<Response<CreateUserView>> create(
            @Header("Authorization") String authorization,
            @QueryMap Map<String, String> params,
            @Body UserRequest user);
}
