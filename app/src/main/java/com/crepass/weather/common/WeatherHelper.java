package com.crepass.weather.common;

import com.crepass.weather.retrofit.WeatherRepository;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WeatherHelper {

    // 정적 메서드로 Weather 데이터를 가져오는 함수
    public static void fetchWeatherData(String baseDate, String baseTime, String nx, String ny, int numOfRows, WeatherRepository.WeatherCallback callback) {
        WeatherRepository weatherRepository = new WeatherRepository();
        weatherRepository.getWeather(baseDate, baseTime, nx, ny, numOfRows, callback);
    }

    public static String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Base Time 계산
        if (minute < 10) {
            hour -= 1; // 10분 이전인 경우 이전 시간을 사용
        }
        if (hour == 1) {
            calendar.add(Calendar.DAY_OF_YEAR, -1);

        }
        return dateFormat.format(calendar.getTime());
    }

    public static String formatTime(String time) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("HHmm", Locale.getDefault());
            Date date = inputFormat.parse(time);

            SimpleDateFormat outputFormat = new SimpleDateFormat("a h시", Locale.getDefault());
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return time; // 변환 실패 시 원본 시간 반환
        }
    }

    public static String formatTemperature(String temperature) {
        if (temperature == null || temperature.isEmpty()) {
            return "N/A"; // 빈 문자열이거나 null일 경우 표시할 기본값
        }
        return String.format("%d°C", (int) Double.parseDouble(temperature));
    }

    public static String getBaseTime() {
        // 현재 시간 가져오기
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Base Time 계산
        if (minute < 10) {
            hour -= 1; // 10분 이전인 경우 이전 시간을 사용
        }
//        return "2300";
        // Base Time을 3시간 간격으로 설정
        if (hour >= 23 || hour == 1) {
            return "2300";
        } else if (hour >= 20) {
            return "2000";
        } else if (hour >= 17) {
            return "1700";
        } else if (hour >= 14) {
            return "1400";
        } else if (hour >= 11) {
            return "1100";
        } else if (hour >= 8) {
            return "0800";
        } else if (hour >= 5) {
            return "0500";
        } else {
            return "0200";
        }
    }

    public static int calculateAverageRainProbability(List<Integer> rainProbabilities) {
        int sum = 0;
        for (int prob : rainProbabilities) {
            sum += prob;
        }
        return sum / rainProbabilities.size(); // 평균 계산
    }

}
