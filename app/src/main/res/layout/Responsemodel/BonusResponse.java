package com.app.earningpoints.Responsemodel;

import com.google.gson.annotations.SerializedName;

public class BonusResponse{

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private int status;

	public String getMessage(){
		return message;
	}

	public int getStatus(){
		return status;
	}
}