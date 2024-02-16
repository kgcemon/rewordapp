package com.app.earningpoints.Responsemodel;

import com.google.gson.annotations.SerializedName;

public class TaskResp {


    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("point")
    private String point;

    @SerializedName("video_id")
    private String videoId;

    @SerializedName("status")
    private int status;

    @SerializedName("url")
    private String url;

    @SerializedName("app_name")
    private String appName;

    @SerializedName("image")
    private String image;

    @SerializedName("details")
    private String details;

    @SerializedName("appurl")
    private String appurl;

    @SerializedName("timer")
    private String timer;

    @SerializedName("inserted_at")
    private String insertedAt;

    @SerializedName("points")
    private String points;

    public String getTimer() {
        return timer;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPoint() {
        return point;
    }

    public String getVideoId() {
        return videoId;
    }

    public int getStatus() {
        return status;
    }

    @SerializedName("browser_type")
    private String browser_type;

    public String getBrowser_type() {
        return browser_type;
    }
	private int viewType=0;

	public int getViewType() {
		return viewType;
	}

	public TaskResp setViewType(int viewType) {
		this.viewType = viewType;
		return  this;
	}

    public String getUrl() {
        return url;
    }

    public String getAppName() {
        return appName;
    }

    public String getImage() {
        return image;
    }

    public String getDetails() {
        return details;
    }

    public String getAppurl() {
        return appurl;
    }

    public String getInsertedAt() {
        return insertedAt;
    }

    public String getPoints() {
        return points;
    }
}