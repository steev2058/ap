package com.apps2you.albaraka.data.remote.networkUtils;

import androidx.annotation.Nullable;

import com.apps2you.albaraka.data.model.About;
import com.apps2you.albaraka.data.model.Account;
import com.apps2you.albaraka.data.model.AtmCard;
import com.apps2you.albaraka.data.model.Branch;
import com.apps2you.albaraka.data.model.City;
import com.apps2you.albaraka.data.model.Complaint;
import com.apps2you.albaraka.data.model.Currency;
import com.apps2you.albaraka.data.model.DepositResult;
import com.apps2you.albaraka.data.model.FavoriteAccount;
import com.apps2you.albaraka.data.model.FinancingResult;
import com.apps2you.albaraka.data.model.FinancingType;
import com.apps2you.albaraka.data.model.MobForm;
import com.apps2you.albaraka.data.model.NotificationContent;
import com.apps2you.albaraka.data.model.Operator;
import com.apps2you.albaraka.data.model.Partner;
import com.apps2you.albaraka.data.model.PrivacyPolicy;
import com.apps2you.albaraka.data.model.ProductCategory;
import com.apps2you.albaraka.data.model.ProfitsResult;
import com.apps2you.albaraka.data.model.QrCode;
import com.apps2you.albaraka.data.model.QuickService;
import com.apps2you.albaraka.data.model.Title;
import com.apps2you.albaraka.data.model.Transaction;
import com.apps2you.albaraka.data.model.TransferData;
import com.apps2you.albaraka.data.model.User;
import com.apps2you.albaraka.data.remote.responseModel.AtmServicesData;
import com.apps2you.albaraka.data.remote.responseModel.CalculatorData;
import com.apps2you.albaraka.data.remote.responseModel.ExchangeData;
import com.apps2you.albaraka.data.remote.responseModel.HomeData;
import com.apps2you.albaraka.data.remote.responseModel.SYGSData;
import com.apps2you.albaraka.data.remote.responseModel.SygsCommissionData;
import com.apps2you.albaraka.utils.Constants;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    static String getApiBaseURL() {
        return Constants.BASE_URL.concat("/api/");
    }

    //User Repo
    @FormUrlEncoded
    @POST("login")
    Call<MyResponse<User>> login(@Field("CIF") String cif_number,
                                 @Field("password") String password,
                                 @Field("fcm_token") String fcm_token);

    @FormUrlEncoded
    @POST("reset_password")
    Call<MyResponse<String>> resetPassword(@Field("old_password") String currentPass,
                                           @Field("new_password") String newPass,
                                           @Field("new_password_confirmation") String confirmNewPass);

    @FormUrlEncoded
    @POST("reset_pin_code")
    Call<MyResponse<String>> resetPin(@Field("old_pin_code") String currentPass,
                                      @Field("new_pin_code") String newPass,
                                      @Field("new_pin_code_confirmation") String confirmNewPass,
                                      @Field("otp") String otpCode);

    @POST("logout")
    Call<MyResponse<String>> logout();

    @GET("home_page_data")
    Call<MyResponse<HomeData>> getHomeData();

    @GET("get_transaction_data")
    Call<MyResponse<SYGSData>> getSYGSData();

    @FormUrlEncoded
    @POST("get_charge_sygs_mob")
    Call<MyResponse<SygsCommissionData>> calculateSygsCommission(@Field("amount") String amount,
                                                                 @Field("type_code") int type,
                                                                 @Field("currency_code") String currencyCode);

    @GET("recent_transactions")
    Call<MyResponse<ArrayList<Transaction>>> getRecentTransactions();

    @GET("exchange_rate")
    Call<MyResponse<ExchangeData>> getExchangeRates();

    @GET("my_accounts")
    Call<MyResponse<ArrayList<Account>>> getAccounts();

    @GET("statement")
    Call<MyResponse<ArrayList<Transaction>>> getTransactions(@Query("account_no") String accountNumber,
                                                             @Query("my_account_name") String accountName,
                                                             @Query("from_date") String fromDate,
                                                             @Query("to_date") String toDate);

    @GET("my_statement")
    Call<ResponseBody> getStatementFile();

    @GET("about_us")
    Call<MyResponse<About>> getAbout();

    @GET("privacy_policy")
    Call<MyResponse<PrivacyPolicy>> getPrivacyPolicy();

    @FormUrlEncoded
    @POST("change_client_language")
    Call<MyResponse<String>> changeClientLanguage(@Field("lang") String lang);

    @GET("get_branches")
    Call<MyResponse<ArrayList<Branch>>> getBranches(@Query("type") String type);

    @GET("get_cities")
    Call<MyResponse<ArrayList<City>>> getCities();

    @GET("complaint_titles")
    Call<MyResponse<ArrayList<Title>>> getComplaintTitles();

    @FormUrlEncoded
    @POST("complaint")
    Call<MyResponse<Complaint>> complaint(@Field("first_name") String first_name,
                                          @Field("last_name") String last_name,
                                          @Field("client_status") String client_status,
                                          @Field("mobile_number") String mobile_number,
                                          @Field("phone_number") String phone_number,
                                          @Field("email") String email,
                                          @Field("contact_time") String contact_time,
                                          @Field("complaint_title_id") @Nullable Integer complaint_title_id,
                                          @Field("branch_id") @Nullable Integer branch_id,
                                          @Field("complaint_date") String complaint_date,
                                          @Field("message") String message);


    @GET("complaint_titles")
    Call<MyResponse<ArrayList<Title>>> getMobFormTitles();

    @FormUrlEncoded
    @POST("saveMobileData")
    Call<MyResponse<MobForm>> mobForm(@Field("national_id") String national_id,
                                        @Field("cif_id") String cif_id,
                                        @Field("mobile_id") String mobile_id,
                                        @Field("captcha_challenge") String captcha,
                                        @Field("complaint_title_id") @Nullable Integer complaint_title_id);

    @GET("get_notifications")
    Call<MyResponse<ArrayList<NotificationContent>>> getNotifications();

    @POST("notifications/delete/{id}")
    Call<MyResponse<String>> deleteNotification(@Path("id") int id);

    @POST("reset_notification_counter")
    Call<MyResponse<String>> resetNotificationCounter();

    @FormUrlEncoded
    @POST("turn_notifications")
    Call<MyResponse<String>> turnNotifications(@Field("status") int status);

    @GET("get_quick_services")
    Call<MyResponse<ArrayList<QuickService>>> getQuickServices();

    @GET("get_categories_services")
    Call<MyResponse<ArrayList<ProductCategory>>> getProductsCategories();

    @GET("get_transaction_details/{id}")
    Call<MyResponse<Transaction>> getTransactionDetails(@Path("id") int id, @Query("branch_code") String branchCode);

    @FormUrlEncoded
    @POST("client_services")
    Call<MyResponse<String>> personalizeQuickServices(@Field("quick_services") String quickServicesList);

    @FormUrlEncoded
    @POST("check_pin_code")
    Call<MyResponse<String>> checkPinCode(@Field("pin_code") String pinCode);

    @FormUrlEncoded
    @POST("baraka_transfer")
    Call<MyResponse<Transaction>> alBarakaTransfer(@Field("channel_type") String channelType,
                                                   @Field("from_account_no") String fromAccountNo,
                                                   @Field("from_account_code") String fromAccountCode,
                                                   @Field("number") String number,
                                                   @Field("amount") String amount,
                                                   @Field("currency_code") String currencyCode,
                                                   @Field("reason") String reason,
                                                   @Field("my_account_name") String myAccountName,
                                                   @Field("pin_code") String pinCode);

    @FormUrlEncoded
    @POST("baraka_transfer_fees")
    Call<MyResponse<SygsCommissionData>> alBarakaTransferFees(@Field("channel_type") String channelType,
                                                   @Field("from_account_no") String fromAccountNo,
                                                   @Field("from_account_code") String fromAccountCode,
                                                   @Field("amount") String amount,
                                                   @Field("cif") String cif,@Field("to_cif") String to_cif,@Field("to_gsm") String to_gsm);

    @FormUrlEncoded
    @POST("my_transfer")
    Call<MyResponse<Transaction>> myTransfer(@Field("channel_type") String channelType,
                                             @Field("from_account_no") String fromAccountNo,
                                             @Field("from_account_code") String fromAccountCode,
                                             @Field("to_account_no") String toAccountNo,
                                             @Field("to_account_code") String toAccountCode,
                                             @Field("amount") String amount,
                                             @Field("currency_code") String currencyCode,
                                             @Field("reason") String reason,
                                             @Field("my_account_name") String myAccountName,
                                             @Field("pin_code") String pinCode);

    @FormUrlEncoded
    @POST("zakat_transfer")
    Call<MyResponse<Transaction>> zakatTransfer(@Field("from_account_no") String fromAccountNo,
                                                @Field("from_account_code") String fromAccountCode,
                                                @Field("amount") String amount,
                                                @Field("currency_code") String currencyCode,
                                                @Field("reason") String reason,
                                                @Field("my_account_name") String myAccountName,
                                                @Field("pin_code") String pinCode);

    @GET("get_partners/Charities")
    Call<MyResponse<ArrayList<Partner>>> getCharities();


    @GET("get_adsl_providers")
    Call<MyResponse<ArrayList<Partner>>> getADSLProviders();

    @GET("get_partners/University")
    Call<MyResponse<ArrayList<Partner>>> getUniversities();

    @GET("get_partners/School")
    Call<MyResponse<ArrayList<Partner>>> getSchools();

    @GET("get_partners/Restaurants")
    Call<MyResponse<ArrayList<Partner>>> getRestaurants();

    @FormUrlEncoded
    @POST("sadaka_transfer")
    Call<MyResponse<Transaction>> sadakaTransfer(@Field("from_account_no") String accountNo,
                                                 @Field("from_account_code") String fromAccountCode,
                                                 @Field("partner_id") int charityId,
                                                 @Field("to_account_no") String charityAccountNumber,
                                                 @Field("phone_number") String phoneNumber,
                                                 @Field("amount") String amount,
                                                 @Field("currency_code") String currencyCode,
                                                 @Field("reason") String reason,
                                                 @Field("my_account_name") String myAccountName,
                                                 @Field("pin_code") String pinCode);

    @FormUrlEncoded
    @POST("adsl_payment")
    Call<MyResponse<Transaction>> adslTransfer(@Field("from_account_no") String accountNo,
                                               @Field("from_account_code") String fromAccountCode,
                                               @Field("adsl_provider_id") int providerId,
                                               @Field("telephone_number") String phoneNumber,
                                               @Field("amount") String amount,
                                               @Field("note") String reason,
                                               @Field("GSM") String gsm,
                                               @Field("city_id") int citId,
                                               @Field("pin_code") String pinCode
    );


    @FormUrlEncoded
    @POST("restaurant_transfer")
    Call<MyResponse<Transaction>> restaurantTransfer(@Field("from_account_no") String fromAccountNo,
                                                     @Field("from_account_code") String fromAccountCode,
                                                     @Field("to_account_no") String toAccountNo,
                                                     @Field("amount") String amount,
                                                     @Field("currency_code") String currencyCode,
                                                     @Field("my_account_name") String myAccountName,
                                                     @Field("bill_number") String billNumber,
                                                     @Field("tips") String tips,
                                                     @Field("reason") String reason,
                                                     @Field("partner_id") int restaurantId,
                                                     @Field("pin_code") String pinCode
    );

    @FormUrlEncoded
    @POST("university_transfer")
    Call<MyResponse<Transaction>> universityTransfer(@Field("from_account_no") String fromAccountNo,
                                                     @Field("from_account_code") String fromAccountCode,
                                                     @Field("to_account_no") String toAccountNo,
                                                     @Field("amount") String amount,
                                                     @Field("currency_code") String currencyCode,
                                                     @Field("my_account_name") String myAccountName,
                                                     @Field("reason") String reason,
                                                     @Field("student_name") String studentName,
                                                     @Field("student_year") String studentYear,
                                                     @Field("student_number") String studentNumber,
                                                     @Field("partner_id") int universityId,
                                                     @Field("phone_number") String phoneNumber,
                                                     @Field("pin_code") String pinCode

    );

    @FormUrlEncoded
    @POST("school_transfer")
    Call<MyResponse<Transaction>> schoolTransfer(@Field("from_account_no") String fromAccountNo,
                                                 @Field("from_account_code") String fromAccountCode,
                                                 @Field("to_account_no") String toAccountNo,
                                                 @Field("amount") String amount,
                                                 @Field("currency_code") String currencyCode,
                                                 @Field("my_account_name") String myAccountName,
                                                 @Field("reason") String reason,
                                                 @Field("student_name") String studentName,
                                                 @Field("student_year") String studentYear,
                                                 @Field("student_number") String studentNumber,
                                                 @Field("phone_number") String phoneNumber,
                                                 @Field("partner_id") int schoolId,
                                                 @Field("pin_code") String pinCode
    );

    @FormUrlEncoded
    @POST("mobile_payment")
    Call<MyResponse<Transaction>> mobilePayment(
            @Field("amount") String amount,
            @Field("provider_id") int providerId,
            @Field("line_type_id") int lineTypeId,
            @Field("payment_category_id") Integer paymentCategoryId,
            @Field("mobile_number") String mobileNumber,
            @Field("from_account_no") String fromAccountNo,
            @Field("from_account_code") String fromAccountCode,
            @Field("operation_name") String operationName,
            @Field("my_account_name") String myAccountName,
            @Field("pin_code") String pinCode
    );

    @FormUrlEncoded
    @POST("sygs_lands_transfer")
    Call<MyResponse<Transaction>> landsPayment(
            @Field("from_account_no") String fromAccountNo,
            @Field("from_account_code") String fromAccountCode,
            @Field("amount") String amount,
            @Field("currency_code") String currencyCode,
            @Field("my_account_name") String myAccountName,
            @Field("to_account_address") String myAccountAddress,
            @Field("to_account_no") String toAccountNo,
            @Field("bank_id") int bankId,
            @Field("bank_code") String bankCode,
            @Field("bank_address") String bankAddress,
            @Field("to_account_name") String toAccName,
            @Field("estate_number") String propertyNumber,
            @Field("estate_area") String propertyArea,
            @Field("sale_contract_number") String contractNumber,
            @Field("sale_contract_date") String contractDate,
            @Field("pin_code") String pinCode
    );

    @FormUrlEncoded
    @POST("sygs_vehicles_transfer")
    Call<MyResponse<Transaction>> vehiclePayment(
            @Field("from_account_no") String fromAccountNo,
            @Field("from_account_code") String fromAccountCode,
            @Field("amount") String amount,
            @Field("currency_code") String currencyCode,
            @Field("my_account_name") String myAccountName,
            @Field("to_account_address") String myAccountAddress,
            @Field("to_account_no") String toAccountNo,
            @Field("bank_id") int bankId,
            @Field("bank_code") String bankCode,
            @Field("bank_address") String bankAddress,
            @Field("to_account_name") String toAccName,
            @Field("vehicle_number") String vehicleNo,
            @Field("vehicle_type") String vehicleType,
            @Field("city") String province,
            @Field("category") String vehicleClass,
            @Field("chassis_number") String chassisNo,
            @Field("vehicle_modal") String vehicleModel,
            @Field("sale_contract_number") String contractNumber,
            @Field("sale_contract_date") String contractDate,
            @Field("pin_code") String pinCode
    );

    @FormUrlEncoded
    @POST("sygs_estate_transfer")
    Call<MyResponse<Transaction>> propertyPayment(
            @Field("from_account_no") String fromAccountNo,
            @Field("from_account_code") String fromAccountCode,
            @Field("amount") String amount,
            @Field("currency_code") String currencyCode,
            @Field("my_account_name") String myAccountName,
            @Field("to_account_address") String myAccountAddress,
            @Field("to_account_no") String toAccountNo,
            @Field("bank_id") int bankId,
            @Field("bank_code") String bankCode,
            @Field("bank_address") String bankAddress,
            @Field("to_account_name") String toAccName,
            @Field("estate_number") String propertyNumber,
            @Field("estate_area") String propertyArea,
            @Field("sale_contract_number") String contractNumber,
            @Field("sale_contract_date") String contractDate,
            @Field("pin_code") String pinCode
    );

    @FormUrlEncoded
    @POST("sygs_general_transfer")
    Call<MyResponse<Transaction>> generalSYGSPayment(
            @Field("from_account_no") String fromAccountNo,
            @Field("from_account_code") String fromAccountCode,
            @Field("amount") String amount,
            @Field("currency_code") String currencyCode,
            @Field("reason") String reason,
            @Field("my_account_name") String myAccountName,
            @Field("to_account_address") String myAccountAddress,
            @Field("to_account_no") String toAccountNo,
            @Field("bank_id") int bankId,
            @Field("bank_code") String bankCode,
            @Field("bank_address") String bankAddress,
            @Field("to_account_name") String toAccName,
            @Field("pin_code") String pinCode
    );

    @FormUrlEncoded
    @POST("favorites")
    Call<MyResponse<FavoriteAccount>> addFavoriteAccount(@Field("name") String accountName,
                                                         @Field("CIF") String cifNumber,
                                                         @Field("GSM") String gsmNumber);

    @GET("favorites")
    Call<MyResponse<ArrayList<FavoriteAccount>>> getMyFavorites();

    @DELETE("favorites/{id}")
    Call<MyResponse<String>> deleteFavorite(@Path("id") int favoriteId);

    @PUT("favorites/{id}")
    Call<MyResponse<FavoriteAccount>> updateFavorite(@Path("id") int favoriteId, @Body FavoriteAccount updatedFavoirteAccount);


    @GET("get_operators")
    Call<MyResponse<ArrayList<Operator>>> getOperators();

    @GET("epay_link")
    Call<MyResponse<String>> getEpayLink();


    @GET("financing_calculator/types")
    Call<MyResponse<ArrayList<FinancingType>>> getFinancingTypes();

    @FormUrlEncoded
    @POST("financing_calculator/calculate")
    Call<MyResponse<CalculatorData<FinancingResult>>> calculateFinancing(@Field("parameter_id") int typeId,
                                                                         @Field("months") String months,
                                                                         @Field("amount") String amount);

    @GET("profit_calculator/currencies")
    Call<MyResponse<ArrayList<Currency>>> getProfitsCurrencies();

    @FormUrlEncoded
    @POST("profit_calculator/calculate")
    Call<MyResponse<CalculatorData<ProfitsResult>>> calculateProfits(@Field("currency_id") int currencyId,
                                                                     @Field("period") int months,
                                                                     @Field("amount") String amount,
                                                                     @Field("date") String date);

    @FormUrlEncoded
    @POST("deposit_calculator/calculate")
    Call<MyResponse<CalculatorData<DepositResult>>> calculateDeposit(@Field("period") int months,
                                                                     @Field("amount") String amount,
                                                                     @Field("date") String date);

    @FormUrlEncoded
    @POST("qr_accounts/check")
    Call<MyResponse<QrCode>> checkQrValidity(@Field("code") String code);

    @FormUrlEncoded
    @POST("qr_payment_transfer")
    Call<MyResponse<Transaction>> qrPaymentTransfer(@Field("code") String code,
                                                    @Field("from_account_no") String fromAccountNo,
                                                    @Field("from_account_code") String fromAccountCode,
                                                    @Field("my_account_name") String fromAccountName,
                                                    @Field("amount") String amount,
                                                    @Field("currency_code") String currencyCode,
                                                    @Field("pin_code") String pinCode,
                                                    @Field("reason") String reason);

    @FormUrlEncoded
    @POST("alpha_transfer")
    Call<MyResponse<Transaction>> alphaTransfer(@Field("alpha_client_id") String alphaClientId,
                                                @Field("from_account_no") String fromAccountNo,
                                                @Field("from_account_code") String fromAccountCode,
                                                @Field("my_account_name") String fromAccountName,
                                                @Field("amount") String amount,
                                                @Field("currency_code") String currencyCode,
                                                @Field("pin_code") String pinCode);

    @GET("my_services/cards_details")
    Call<MyResponse<ArrayList<AtmCard>>> getAtmCards();

    @GET("my_services/data")
    Call<MyResponse<AtmServicesData>> getAtmServicesData();

    @FormUrlEncoded
    @POST("my_services/reset_card_pin")
    Call<MyResponse<String>> resendPinCode(@Field("card_no") String cardNumber,
                                           @Field("pin_code") String pinCode);

    @FormUrlEncoded
    @POST("my_services/reset_card_limit")
    Call<MyResponse<String>> updateLimit(@Field("card_no") String cardNumber,
                                         @Field("limit") String limit,
                                         @Field("pin_code") String pinCode);

    @FormUrlEncoded
    @POST("my_services/change_card_status")
    Call<MyResponse<String>> updateStatus(@Field("card_no") String cardNumber,
                                          @Field("status") int active, // send card's current status
                                          @Field("pin_code") String pinCode);

    @GET("transaction_settings")
        // get transfer fees and limits
    Call<MyResponse<ArrayList<TransferData>>> getTransferSettings();

    @GET("check_visitor")
    Call<MyResponse<Boolean>> checkVisitor();
}
