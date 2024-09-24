package com.crepass.weather.item;

import android.util.Log;

import java.util.Date;
import java.util.List;

public class WeatherItem {
    private String groupTitle;
    private Date date;
    private String tmx;
    private String tmn;
    private int rainProbability;
    private List<ChildItem> childItems;
    private String highTemperature;
    private String lowTemperature;

    public WeatherItem(String groupTitle, Date date, String tmx, String tmn,int rainProbability, List<ChildItem> childItems) {
        this.groupTitle = groupTitle;
        this.date = date;
        this.tmx = tmx;
        this.tmn = tmn;
        this.rainProbability = rainProbability;
        this.childItems = childItems;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public Date getDate() {


        return date;
    }
    public String getTmx() {
        Log.d("getTmx",tmx);
        return tmx;
    }
    public String getTmn() {
        return tmn;
    }

    public void setTmx(String tmx) {
        this.tmx = tmx;
    }

    public int getRainProbability() {
        return rainProbability;
    }

    public void setRainProbability(int rainProbability) {
        this.rainProbability = rainProbability;
    }
    public void setTmn(String tmn) {
        this.tmn = tmn;
    }
    public void setChildItems(List<ChildItem> childItems) {
        this.childItems = childItems;
    }
    public List<ChildItem> getChildItems() {
        return childItems;
    }


}
