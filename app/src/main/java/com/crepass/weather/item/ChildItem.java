package com.crepass.weather.item;

public class ChildItem {
    private String time;
    private String temperature;
    private String rainPercent;
    private int iconRes;

    public ChildItem(String time, String temperature, String rainPercent, int iconRes) {
        this.time = time;
        this.temperature = temperature;
        this.rainPercent = rainPercent;
        this.iconRes = iconRes;
    }

    public String getTime() {
        return time;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getRainPercent() {
        return rainPercent;
    }

    public int getIconRes() {
        return iconRes;
    }
}