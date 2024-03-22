package com.example.asm_ph42207;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface APIService {
    String DOMAIN = "http://192.168.1.10:4000/";

    @GET("/api/list")
    Call<List<CarModel>> getCars();

    @DELETE("/api/delete/{id}")
    Call<Void> deleteCar(@Path("id") String id);

    @POST("/api/add")
    Call<CarModel> addCar(@Body CarModel carModel);
    @PUT("/api/edit/{id}")
    Call<CarModel> updateCar(@Path("id") String id, @Body CarModel carModel);

}
