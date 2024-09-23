package com.crepass.weather.item;

import java.util.List;

public class WeatherItem {
    private String groupTitle;
    private String date;
    private List<ChildItem> childItems;
    private String highTemperature;
    private String lowTemperature;

    public WeatherItem(String groupTitle, String date, List<ChildItem> childItems) {
        this.groupTitle = groupTitle;
        this.date = date;
        this.childItems = childItems;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public String getDate() {
        return date;
    }
    public void setChildItems(List<ChildItem> childItems) {
        this.childItems = childItems;
    }
    public List<ChildItem> getChildItems() {
        return childItems;
    }


}
