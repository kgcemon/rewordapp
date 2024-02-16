package com.app.earningpoints.Responsemodel;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OfferResponse {

	@SerializedName("data")
	private List<DataItem> data;

	@SerializedName("code")
	private int code;

	@SerializedName("msg")
	private String msg;

	public List<DataItem> getData(){
		return data;
	}

	public int getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

	public class DataItem{

		@SerializedName("offer_title")
		private String offerTitle;

		@SerializedName("offer_icon")
		private String offer_icon;

		public String getOffer_icon() {
			return offer_icon;
		}

		@SerializedName("status")
		private int status;

		public String getOfferTitle(){
			return offerTitle;
		}

		public int getStatus(){
			return status;
		}
	}
}