package com.app.earningpoints.restApi;

import com.app.earningpoints.Responsemodel.CallbackResp;
import com.app.earningpoints.Responsemodel.ConfigResp;
import com.app.earningpoints.Responsemodel.BannerResponse;
import com.app.earningpoints.Responsemodel.GameResponse;
import com.app.earningpoints.Responsemodel.HistoryResp;
import com.app.earningpoints.Responsemodel.LoginResponse;
import com.app.earningpoints.Responsemodel.RedeemResponse;
import com.app.earningpoints.Responsemodel.TaskResp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET(WebApi.Api.Offers)
    Call<OfferResponse> getOffer();

    @GET(WebApi.Api.ABOUTS)
    Call<ConfigResp> getConfig();

    @GET(WebApi.Api.APPS)
    Call<AppResponse> getOffers();

    @GET(WebApi.Api.SLIDE_BANNER)
    Call<BannerResponse> SLideBanner();

    @GET(WebApi.Api.GAMES)
    Call<GameResponse> getGame();

    @GET(WebApi.Api.VIDEOS)
    Call<TaskResp> getVideo();

    @GET(WebApi.Api.WEB)
    Call<WebResponse> getWebLink();

    @GET(WebApi.Api.CREDIT_DAILY)
    Call<CallbackResp> getDailyCheckin();

    @POST(WebApi.Api.Login)
    Call<LoginResponse> Login();

    @POST(WebApi.Api.SIGNUP)
    Call<SignupResponse> Signup();

    @POST(WebApi.Api.VERIFY_OTP)
    Call<BonusResponse> Verify_OTP(@Query("otp") String otp,
                              @Query("email") String email);

    @POST(WebApi.Api.RESET_PASSWORD)
    Call<BonusResponse> ResetPass(@Query("email") String email);

    @POST(WebApi.Api.UPDATE_PASSWORD)
    Call<BonusResponse> UpdatePass(@Query("email") String email,
                                   @Query("password") String password);

    @POST(WebApi.Api.REDEEM)
    Call<CallbackResp> Redeem();

    @GET(WebApi.Api.USER_COIN)
    Call<CallbackResp> Mycoin();

    @POST(WebApi.Api.CHECK)
    Call<CheckResponse> Check();

    @GET(WebApi.Api.CREDIT_APP)
    Call<CallbackResp> CreditApp();

    @GET(WebApi.Api.CHECK_SPIN)
    Call<SpinResponse> CheckSpin();

    @GET(WebApi.Api.CREDIT_SPIN)
    Call<CallbackResp> CreditSpin();
    @GET(WebApi.Api.CREDIT_VIDEO)
    Call<CallbackResp> CreditVideo();
    @GET(WebApi.Api.CREDIT_WEB)
    Call<CallbackResp> CreditWeb();

    @GET(WebApi.Api.REWARDS)
    Call<RedeemResponse> getRedeem();

    @GET(WebApi.Api.HISTORY)
    Call<HistoryResp> History();

    @GET(WebApi.Api.REWARD_HISTORY)
    Call<RewardHistoryResponse> RewardHistory();

    @GET(WebApi.Api.CREDIT_GAME)
    Call<CallbackResp> GameReward();


}
