package com.crepass.weather;

// 필요한 데이터 클래스 정의
class WeatherData {
    String fcstDate;
    String category;
    String fcstTime;
    String fcstValue;

    public WeatherData(String fcstDate,String category, String fcstTime, String fcstValue) {
        this.fcstDate = fcstDate;
        this.category = category;
        this.fcstTime = fcstTime;
        this.fcstValue = fcstValue;
    }

    @Override
    public String toString() {
        return "fcstDate: "+fcstDate+", Category: " + category + ", Forecast Time: " + fcstTime + ", Value: " + fcstValue;
    }
}