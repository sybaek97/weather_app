package com.crepass.weather.retrofit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.os.Handler;
import android.os.Looper;

public class WeatherRepository {

    private static final String BASE_URL = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/";
    private static final String SERVICE_KEY = ""; // 공공데이터 포털에서 발급받은 API 키를 입력하세요.

    private WeatherApiService apiService;
    private final ExecutorService executorService;
    private final Handler mainHandler;

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

        // ExecutorService와 Handler 초기화
        executorService = Executors.newFixedThreadPool(4); // 4개의 스레드 풀 생성
        mainHandler = new Handler(Looper.getMainLooper()); // UI 스레드 핸들러 생성
    }

    public void getWeather(String baseDate, String baseTime, String nx, String ny,int numOfRows, final WeatherCallback callback) {
        executorService.execute(() -> {
            try {
                Call<WeatherResponse> call = apiService.getWeather(SERVICE_KEY, numOfRows, 1, "JSON", baseDate, baseTime, nx, ny);
                Response<WeatherResponse> response = call.execute(); // 동기 호출로 변경

                if (response.isSuccessful() && response.body() != null) {
                    mainHandler.post(() -> callback.onSuccess(response.body()));
                } else {
                    mainHandler.post(() -> callback.onFailure("API 응답 실패: " + response.message()));
                }
            } catch (Exception e) {
                mainHandler.post(() -> callback.onFailure("API 호출 실패: " + e.getMessage()));
            }
        });
    }

    public interface WeatherCallback {
        void onSuccess(WeatherResponse weatherResponse);
        void onFailure(String errorMessage);
    }
}
