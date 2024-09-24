package com.crepass.weather.item;

public class ChildItem {
    private String time; // 시간
    private String temperature; // 온도
    private String rainPercent; // 강수 확률
    private int iconRes; // 아이콘 리소스

    public ChildItem(String time, String temperature, String rainPercent, int iconRes) {
        this.time = time;
        this.temperature = temperature;
        this.rainPercent = rainPercent;
        this.iconRes = iconRes;
    }

    // Getter 및 Setter 메서드 추가
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getRainPercent() {
        return rainPercent;
    }

    public void setRainPercent(String rainPercent) {
        this.rainPercent = rainPercent;
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }
}