package com.crepass.weather.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApiService {
    // 단기예보 API의 엔드포인트 정의
    @GET("getVilageFcst")
    Call<WeatherResponse> getWeather(
            @Query("serviceKey") String serviceKey,
            @Query("numOfRows") int numOfRows,
            @Query("pageNo") int pageNo,
            @Query("dataType") String dataType,
            @Query("base_date") String baseDate,
            @Query("base_time") String baseTime,
            @Query("nx") String nx,
            @Query("ny") String ny
    );
}

