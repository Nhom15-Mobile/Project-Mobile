package com.example.appointment.service;

import android.util.Log;

import com.example.appointment.api.CareProfileApi;
import com.example.appointment.model.ItemRecord;
import com.uithealthcare.domain.careProfile.CareProfile;
import com.uithealthcare.domain.careProfile.CareProfilesResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CareProfileService {

    public interface CareProfileCallback {
        void onSuccess(List<ItemRecord> items);
        void onError(Throwable t);
    }
    public static String TAG = "SHOW_RECORD";
    public static void showListItemRecord(String token, CareProfileCallback cb){


        CareProfileApi.CARE_PROFILE_API.showOnCardCareProfile(token)
                .enqueue(new Callback<CareProfilesResponse>() {
            @Override
            public void onResponse(Call<CareProfilesResponse> call, Response<CareProfilesResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    cb.onError(new RuntimeException("code="+response.code()));
                    return;
                }
                CareProfilesResponse data = response.body();
                List<ItemRecord> items = new ArrayList<>();
                if(data != null && data.isSuccess()){
                    List<CareProfile> list = data.getData();
                    for (CareProfile care : list){
                        items.add(new ItemRecord(care.getFullName(), care.getId(), care.getPhone()));
                    }
                    cb.onSuccess(items);
                }
            }

            @Override
            public void onFailure(Call<CareProfilesResponse> call, Throwable throwable) {
                cb.onError(throwable);
            }
        });
        Log.d(TAG, "OKE");
    }
}
