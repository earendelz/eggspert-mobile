package com.example.eggspert_mobile;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Header;

public interface ApiService {

    // API Login
    @POST("/api/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    // API Logout
//    @POST("/api/logout")
//    Call<Void> logout(@Header("Authorization") String token);

}
