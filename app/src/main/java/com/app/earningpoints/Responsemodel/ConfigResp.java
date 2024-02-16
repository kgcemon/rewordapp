package com.app.earningpoints.Responsemodel;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ConfigResp {

	@SerializedName("data")
	private List<DataItem> data;

	@SerializedName("offers")
	private List<OfferItem> offers;

	@SerializedName("lang")
	private List<LangDataResp> lang;

	public List<LangDataResp> getLang() {
		return lang;
	}

	@SerializedName("success")
	private int success;

	@SerializedName("message")
	private String message;

	@SerializedName("vpn")
	private boolean vpn;

	public boolean isVpn() {
		return vpn;
	}

	public List<OfferItem> getOffers() {
		return offers;
	}

	@SerializedName("spin")
	private List<SpinItem> spin;

	public List<DataItem> getData(){
		return data;
	}

	public String getMessage() {
		return message;
	}

	public int getSuccess(){
		return success;
	}

	public List<SpinItem> getSpin(){
		return spin;
	}

	public static class DataItem {
		@SerializedName("app_icon")
		private String appIcon;

		@SerializedName("app_contact")
		private String appContact;

		@SerializedName("adConfig")
		private String adConfig;

		@SerializedName("vpn")
		private boolean vpn;

		@SerializedName("interstital")
		private boolean interstital;

		@SerializedName("interstital_type")
		private String interstital_type;

		@SerializedName("interstital_count")
		private int interstital_count;

		@SerializedName("interstital_ID")
		private String interstital_ID;

		@SerializedName("share_msg")
		private String share_msg;

		@SerializedName("up_msg")
		private String up_msg;

		@SerializedName("up_link")
		private String up_link;

		@SerializedName("up_mode")
		private String up_mode;

		@SerializedName("up_status")
		private boolean up_status;

		@SerializedName("up_btn")
		private boolean up_btn;

		@SerializedName("up_version")
		private int up_version;

		@SerializedName("nativeType")
		private String nativeType;

		@SerializedName("nativeId")
		private String nativeId;

		public String getAdConfig() {
			return adConfig;
		}

		public String getAppIcon() {
			return appIcon;
		}

		public String getAppContact() {
			return appContact;
		}
		@SerializedName("nativeCount")
		private int nativeCount;

		public String getShare_msg() {
			return share_msg;
		}

		public String getUp_msg() {
			return up_msg;
		}

		public String getUp_link() {
			return up_link;
		}

		public String getUp_mode() {
			return up_mode;
		}

		public boolean isUp_status() {
			return up_status;
		}

		public int getUp_version() {
			return up_version;
		}

		public boolean isUp_btn() {
			return up_btn;
		}

		@SerializedName("app_author")
		private String appAuthor;

		@SerializedName("id")
		private int id;

		@SerializedName("bannerid")
		private String bannerid;


		@SerializedName("banner_type")
		private String bannerType;

		@SerializedName("privacy_policy")
		private String privacyPolicy;

		@SerializedName("homepage")
		private int home_style;

		@SerializedName("offerwall_style")
		private int offerwall_style;

		@SerializedName("offerwall_layout")
		private int offerwall_layout;

		@SerializedName("survey_style")
		private int survey_style;

		@SerializedName("survey_layout")
		private int survey_layout;

		public int getOfferwall_style() {
			return offerwall_style;
		}

		public int getOfferwall_layout() {
			return offerwall_layout;
		}

		public int getSurvey_style() {
			return survey_style;
		}

		public int getSurvey_layout() {
			return survey_layout;
		}

		@SerializedName("app_description")
		private String appDescription;

		public int getHome_style() {
			return home_style;
		}

		public void setHome_style(int home_style) {
			this.home_style = home_style;
		}

		public boolean isInterstital() {
			return interstital;
		}

		public String getInterstital_type() {
			return interstital_type;
		}

		public int getInterstital_count() {
			return interstital_count;
		}

		public String getInterstital_ID() {
			return interstital_ID;
		}

		public boolean isVpn() {
			return vpn;
		}

		public String getAppAuthor() {
			return appAuthor;
		}
		public int getId() {
			return id;
		}

		public String getBannerid() {
			return bannerid;
		}
		public String getBannerType() {
			return bannerType;
		}
		public String getPrivacyPolicy() {
			return privacyPolicy;
		}

		public String getAppDescription() {
			return appDescription;
		}

		public String getNativeType() {
			return nativeType;
		}

		public String getNativeId() {
			return nativeId;
		}

		public int getNativeCount() {
			return nativeCount;
		}
	}

	public class SpinItem{

		@SerializedName("position_1")
		private String position1;

		@SerializedName("position_2")
		private String position2;

		@SerializedName("position_3")
		private String position3;

		@SerializedName("position_4")
		private String position4;

		@SerializedName("position_5")
		private String position5;

		@SerializedName("position_6")
		private String position6;

		@SerializedName("position_7")
		private String position7;

		@SerializedName("pc_1")
		private String pc1;

		@SerializedName("position_8")
		private String position8;

		@SerializedName("pc_2")
		private String pc2;

		@SerializedName("pc_3")
		private String pc3;

		@SerializedName("pc_8")
		private String pc8;

		@SerializedName("id")
		private int id;

		@SerializedName("pc_4")
		private String pc4;

		@SerializedName("pc_5")
		private String pc5;

		@SerializedName("pc_6")
		private String pc6;

		@SerializedName("pc_7")
		private String pc7;

		public String getPosition1(){
			return position1;
		}

		public String getPosition2(){
			return position2;
		}

		public String getPosition3(){
			return position3;
		}

		public String getPosition4(){
			return position4;
		}

		public String getPosition5(){
			return position5;
		}

		public String getPosition6(){
			return position6;
		}

		public String getPosition7(){
			return position7;
		}

		public String getPc1(){
			return pc1;
		}

		public String getPosition8(){
			return position8;
		}

		public String getPc2(){
			return pc2;
		}

		public String getPc3(){
			return pc3;
		}

		public String getPc8(){
			return pc8;
		}

		public int getId(){
			return id;
		}

		public String getPc4(){
			return pc4;
		}

		public String getPc5(){
			return pc5;
		}

		public String getPc6(){
			return pc6;
		}

		public String getPc7(){
			return pc7;
		}
	}

	public static class OfferItem{

		public void setOfferTitle(String offerTitle) {
			this.offerTitle = offerTitle;
		}

		public void setOffer_icon(String offer_icon) {
			this.offer_icon = offer_icon;
		}

		public void setType(String type) {
			this.type = type;
		}

		public void setStatus(int status) {
			this.status = status;
		}

		@SerializedName("offer_title")
		private String offerTitle;

		@SerializedName("offer_icon")
		private String offer_icon;

		@SerializedName("type")
		private String type;

		public String getType() {
			return type;
		}

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