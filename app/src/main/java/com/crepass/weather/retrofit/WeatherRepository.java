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
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class WeatherRepository {

    private static final String BASE_URL = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/";
    private static final String SERVICE_KEY = "+v73lkCCM5EhKWsmp3nyrmWNQQoAXIDSsEeol7XnEEuYDVF+gllt5eFHZEoFNdo/L7XN/V7UQrLZUxMn0Ah9Aw=="; // 공공데이터 포털에서 발급받은 API 키를 입력하세요.
    private static final int MAX_RETRY_COUNT = 3; // 최대 재시도 횟수
    private static final long RETRY_DELAY_MS = 2000; // 재시도 사이의 지연 시간 (2초)

    private WeatherApiService apiService;
    private final ExecutorService executorService;
    private final Handler mainHandler;
    private final Context context; // Context를 추가하여 Toast를 표시할 수 있게 함

    public WeatherRepository(Context context) { // Context를 생성자에 추가
        this.context = context;

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

    public void getWeather(String baseDate, String baseTime, String nx, String ny, int numOfRows, final WeatherCallback callback) {
        getWeatherWithRetry(baseDate, baseTime, nx, ny, numOfRows, callback, 0); // 재시도 횟수를 0으로 초기화하여 시작
    }

    // 재시도를 포함한 API 호출 메서드
    private void getWeatherWithRetry(String baseDate, String baseTime, String nx, String ny, int numOfRows, final WeatherCallback callback, int retryCount) {
        executorService.execute(() -> {
            try {
                Call<WeatherResponse> call = apiService.getWeather(SERVICE_KEY, numOfRows, 1, "JSON", baseDate, baseTime, nx, ny);
                Response<WeatherResponse> response = call.execute(); // 동기 호출로 변경

                if (response.isSuccessful() && response.body() != null) {
                    mainHandler.post(() -> callback.onSuccess(response.body()));
                } else {
                    handleFailure(baseDate, baseTime, nx, ny, numOfRows, callback, retryCount, "API 응답 실패: " + response.message());
                }
            } catch (Exception e) {
                handleFailure(baseDate, baseTime, nx, ny, numOfRows, callback, retryCount, "API 호출 실패: " + e.getMessage());
            }
        });
    }

    // 실패 시 재시도를 처리하는 메서드
    private void handleFailure(String baseDate, String baseTime, String nx, String ny, int numOfRows, final WeatherCallback callback, int retryCount, String errorMessage) {
        if (retryCount < MAX_RETRY_COUNT) {
            // 재시도 횟수가 최대 재시도 횟수보다 작으면 재시도
            int nextRetryCount = retryCount + 1;

//            // Context 유효성 체크 및 ApplicationContext 사용
//            if (context != null && context.getApplicationContext() != null) {
//                Context appContext = context.getApplicationContext();
//                mainHandler.post(() -> {
//                    try {
//                        Toast.makeText(appContext, "요청에 실패하여 재시도 합니다. (" + nextRetryCount + "/" + MAX_RETRY_COUNT + ")", Toast.LENGTH_SHORT).show();
//                    } catch (Exception e) {
//                        Log.e("WeatherRepository", "Toast 메시지 표시 중 오류 발생: " + e.getMessage());
//                    }
//                });
//            } else {
//                Log.e("WeatherRepository", "Context가 유효하지 않습니다. Toast를 표시할 수 없습니다.");
//            }

            mainHandler.postDelayed(() -> {
                getWeatherWithRetry(baseDate, baseTime, nx, ny, numOfRows, callback, nextRetryCount);
            }, RETRY_DELAY_MS);
        } else {
            // 최대 재시도 횟수를 초과하면 실패로 처리
            mainHandler.post(() -> callback.onFailure(errorMessage));
        }
    }

    public interface WeatherCallback {
        void onSuccess(WeatherResponse weatherResponse);
        void onFailure(String errorMessage);
    }
}
