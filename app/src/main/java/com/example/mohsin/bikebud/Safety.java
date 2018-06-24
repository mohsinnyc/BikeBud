package com.example.mohsin.bikebud;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Safety {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("occurred_at")
    @Expose
    private Long occurred_at;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;


    public String getType() {return type;}

    public void setType(String type) {this.type = type;}

    public Long getDate() {return occurred_at;}

    public void setDate(Long occurred_at) {this.occurred_at = occurred_at;}

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

}
