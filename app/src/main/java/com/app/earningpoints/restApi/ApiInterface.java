package com.app.earningpoints.restApi;

import com.app.earningpoints.Responsemodel.ConfigResp;
import com.app.earningpoints.Responsemodel.BannerResponse;
import com.app.earningpoints.Responsemodel.CallbackResp;
import com.app.earningpoints.Responsemodel.GameResponse;
import com.app.earningpoints.Responsemodel.HistoryResp;
import com.app.earningpoints.Responsemodel.LangDataResp;
import com.app.earningpoints.Responsemodel.LeaderboardResp;
import com.app.earningpoints.Responsemodel.OfferResponse;
import com.app.earningpoints.Responsemodel.OfferwallResp;
import com.app.earningpoints.Responsemodel.RedeemResponse;
import com.app.earningpoints.Responsemodel.RefListResp;
import com.app.earningpoints.Responsemodel.SocialResp;
import com.app.earningpoints.Responsemodel.TaskResp;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET(WebApi.Api.ABOUTS)
    Call<ConfigResp> getConfig();

    @GET(WebApi.Api.Offers)
    Call<OfferResponse> getOffers();

    @GET(WebApi.Api.SLIDE_BANNER)
    Call<BannerResponse> SLideBanner();

    @GET(WebApi.Api.get_lang)
    Call<List<SocialResp>> get_lang();

    @GET(WebApi.Api.get_lang_data)
    Call<List<LangDataResp>> getLangData(@Path("lang") String lang);

    @GET(WebApi.Api.get_refer_list)
    Call<RefListResp> getReferList(@Path("refid") String refid);

    @GET(WebApi.Api.GAMES)
    Call<GameResponse> getGame();

    @GET(WebApi.Api.Offerwall)
    Call<List<OfferwallResp>> getOfferwall();

    @GET(WebApi.Api.SOCIAL_LINK)
    Call<List<SocialResp>> getSocialLinks();

    @POST(WebApi.Api.VERIFY_OTP)
    Call<CallbackResp> Verify_OTP(@Query("otp") String otp,
                              @Query("email") String email);
    @FormUrlEncoded
    @POST(WebApi.Api.UPDATE_PROFILE_PASS)
    Call<CallbackResp> updateProfilePass(@Query("data") String data);

    @FormUrlEncoded
    @POST(WebApi.Api.UPDATE_PROFILE)
    Call<CallbackResp> updateProfile(@Field("data") String data);


    @Multipart
    @POST(WebApi.Api.UPDATE_PROFILE)
    Call<CallbackResp> updateProfilewithProfile(@Part MultipartBody.Part part, @Query("data") String data);

    @POST(WebApi.Api.RESET_PASSWORD)
    Call<CallbackResp> ResetPass(@Query("email") String email);

    @POST(WebApi.Api.UPDATE_PASSWORD)
    Call<CallbackResp> UpdatePass(@Query("email") String email,
                                   @Query("password") String password,
                                   @Query("otp") String otp);


    @GET(WebApi.Api.REWARDS)
    Call<RedeemResponse> getRedeem();

    @GET(WebApi.Api.LEADERBOARD)
    Call<List<LeaderboardResp>> getLeaderboard();

    @GET(WebApi.Api.CREDIT_GAME)
    Call<CallbackResp> GameReward();

    @FormUrlEncoded
    @POST("api/v1/datas")
    Call<List<HistoryResp>> ApiTransaction(@Field("data") String data);

    @FormUrlEncoded
    @POST("api/v1/datas")
    Call<List<TaskResp>> ApiTask(@Field("data") String data);

    @FormUrlEncoded
    @POST("api/v1/datas")
    Call<CallbackResp> Api(@Field("data") String body);

    @FormUrlEncoded
    @POST("api/v1/user")
    Call<CallbackResp> ApiUser(@Field("data") String body);



}
