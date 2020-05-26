package com.sizii.homty;

import com.sizii.homty.Model.Results;
import com.sizii.homty.Remote.IGoogleApiService;
import com.sizii.homty.Remote.RetrofitClient;

public class Common {
    public static Results currentResult;

    private static final String GOOGLE_API_URL = "https://maps.googleapis.com/";

    public static IGoogleApiService getGoogleApiService() {
        return RetrofitClient.getClient(GOOGLE_API_URL).create(IGoogleApiService.class);
    }
}
