package com.sizii.homty.Remote;

import com.sizii.homty.Model.MyPlaces;
import com.sizii.homty.Model.PlaceDetail;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface IGoogleApiService {
    @GET
    Call<MyPlaces> getNearbyPlaces(@Url String url);

    @GET
    Call<PlaceDetail> getDetailPlaces(@Url String url);
}
