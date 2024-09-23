package com.crepass.weather.retrofit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherRepository {

    private static final String BASE_URL = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/";
    private static final String SERVICE_KEY = ""; // 공공데이터 포털에서 발급받은 API 키를 입력하세요.

    private WeatherApiService apiService;

    public WeatherRepository() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(WeatherApiService.class);
    }

    public void getWeather(String baseDate, String baseTime, String nx, String ny,int numOfRows, final WeatherCallback callback) {
        Call<WeatherResponse> call = apiService.getWeather(SERVICE_KEY, numOfRows, 1, "JSON", baseDate, baseTime, nx, ny);


        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure("API 응답 실패: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                callback.onFailure("API 호출 실패: " + t.getMessage());
            }
        });
    }

    public interface WeatherCallback {
        void onSuccess(WeatherResponse weatherResponse);
        void onFailure(String errorMessage);
    }
}
