package com.crepass.weather.retrofit;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WeatherResponse {
    @SerializedName("response")
    public Response response;

    public class Response {
        @SerializedName("body")
        public Body body;
    }

    public class Body {
        @SerializedName("items")
        public Items items;
    }

    public class Items {
        @SerializedName("item")
        public List<Item> itemList;
    }

    public class Item {

        @SerializedName("category")
        public String category;

        @SerializedName("fcstValue")
        public String fcstValue;
        @SerializedName("POP")
        public String pop;
        @SerializedName("fcstTime")
        public String fcstTime;
        @SerializedName("fcstDate")
        public String fcstDate;
    }
}
