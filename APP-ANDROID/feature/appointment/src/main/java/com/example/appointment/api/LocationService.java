package com.example.appointment.api;

import com.uithealthcare.domain.location.DistrictResponse;
import com.uithealthcare.domain.location.ProvinceResponse;
import com.uithealthcare.domain.location.WardResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LocationService {
    @GET("api/locations/provinces")
    Call<ProvinceResponse> getProvinces();

    @GET("api/locations/districts")
    Call<DistrictResponse> getDistricts(
            @Query("province_code") String provinceCode
    );

    @GET("api/locations/wards")
    Call<WardResponse> getWards(@Query("district_code") String districtCode);
}
