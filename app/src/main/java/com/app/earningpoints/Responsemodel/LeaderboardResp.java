package com.app.earningpoints.Responsemodel;

import com.google.gson.annotations.SerializedName;

public class LeaderboardResp {

    @SerializedName("name")
    private  String name;
    @SerializedName("balance")
    private  String coin;

    @SerializedName("profile")
    private  String profile;

    public String getProfile() {
        return profile;
    }

    public String getName() {
        return name;
    }

    public String getCoin() {
        return coin;
    }


}
