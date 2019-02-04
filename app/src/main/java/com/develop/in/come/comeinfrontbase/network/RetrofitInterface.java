package com.develop.in.come.comeinfrontbase.network;

import com.develop.in.come.comeinfrontbase.models.Response;
import com.develop.in.come.comeinfrontbase.models.User;
import retrofit2.http.*;
import rx.Observable;

public interface RetrofitInterface {

    @POST("users")
    Observable<Response> register(@Body String phone, @Body String psw);

    @POST("users/login")
    Observable<Response> login();

    @GET("users/{email}")
    Observable<User> getProfile(@Path("email") String email);

    @PUT("users/{email}")
    Observable<Response> changePassword(@Path("email") String email, @Body User user);

    @POST("users/{email}/password")
    Observable<Response> resetPasswordInit(@Path("email") String email);

    @POST("users/{email}/password")
    Observable<Response> resetPasswordFinish(@Path("email") String email, @Body User user);
}
