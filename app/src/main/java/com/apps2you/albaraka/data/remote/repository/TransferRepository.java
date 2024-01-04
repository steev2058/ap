package com.apps2you.albaraka.data.remote.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.apps2you.albaraka.data.model.FavoriteAccount;
import com.apps2you.albaraka.data.model.Operator;
import com.apps2you.albaraka.data.model.Partner;
import com.apps2you.albaraka.data.model.QrCode;
import com.apps2you.albaraka.data.model.SYGSType;
import com.apps2you.albaraka.data.model.Transaction;
import com.apps2you.albaraka.data.model.TransferChannelType;
import com.apps2you.albaraka.data.model.TransferData;
import com.apps2you.albaraka.data.remote.networkUtils.ApiService;
import com.apps2you.albaraka.data.remote.networkUtils.MyResponse;
import com.apps2you.albaraka.data.remote.networkUtils.NetworkBoundResource;
import com.apps2you.albaraka.data.remote.networkUtils.Resource;
import com.apps2you.albaraka.data.remote.responseModel.SYGSData;
import com.apps2you.albaraka.data.remote.responseModel.SygsCommissionData;
import com.apps2you.albaraka.ui.transfer.accountsTransfer.sygs.SYGSTransferForm;

import java.util.ArrayList;

import javax.inject.Inject;

import retrofit2.Call;

public class TransferRepository {
    private final ApiService apiService;

    @Inject
    public TransferRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public LiveData<Resource<Transaction>> alBarakaTransfer(TransferChannelType channelType,
                                                            String fromAccountNumber,
                                                            String fromAccountCode,
                                                            String number,
                                                            String amount,
                                                            String currencyCode,
                                                            String reason,
                                                            String myAccountName,
                                                            String pinCode) {
        return new NetworkBoundResource<Transaction>() {
            @NonNull
            @Override
            protected Call<MyResponse<Transaction>> createCall() {
                return apiService.alBarakaTransfer(
                        channelType != null ? channelType.toString() : null,
                        fromAccountNumber,
                        fromAccountCode,
                        number,
                        amount,
                        currencyCode,
                        reason,
                        myAccountName,
                        pinCode
                );
            }
        }.getAsLiveServerData();
    }


    public LiveData<Resource<Transaction>> myTransfer(String fromAccountNumber,
                                                      String fromAccountCode,
                                                      String toAccountNo,
                                                      String toAccountCode,
                                                      String amount,
                                                      String currencyCode,
                                                      String reason,
                                                      String myAccountName,
                                                      String pinCode) {
        return new NetworkBoundResource<Transaction>() {
            @NonNull
            @Override
            protected Call<MyResponse<Transaction>> createCall() {
                return apiService.myTransfer(
                        "ACCOUNT_NO",
                        fromAccountNumber,
                        fromAccountCode,
                        toAccountNo,
                        toAccountCode,
                        amount,
                        currencyCode,
                        reason,
                        myAccountName,
                        pinCode
                );
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<Transaction>> zakatTransfer(String fromAccountNumber,
                                                         String fromAccountCode,
                                                         String amount,
                                                         String currencyCode,
                                                         String reason,
                                                         String myAccountName,
                                                         String pinCode) {
        return new NetworkBoundResource<Transaction>() {
            @NonNull
            @Override
            protected Call<MyResponse<Transaction>> createCall() {
                return apiService.zakatTransfer(
                        fromAccountNumber,
                        fromAccountCode,
                        amount,
                        currencyCode,
                        reason,
                        myAccountName,
                        pinCode
                );
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<ArrayList<Partner>>> getCharities() {
        return new NetworkBoundResource<ArrayList<Partner>>() {
            @NonNull
            @Override
            protected Call<MyResponse<ArrayList<Partner>>> createCall() {
                return apiService.getCharities();
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<ArrayList<Partner>>> getADSLProviders() {
        return new NetworkBoundResource<ArrayList<Partner>>() {
            @NonNull
            @Override
            protected Call<MyResponse<ArrayList<Partner>>> createCall() {
                return apiService.getADSLProviders();
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<ArrayList<Partner>>> getUniversities() {
        return new NetworkBoundResource<ArrayList<Partner>>() {
            @NonNull
            @Override
            protected Call<MyResponse<ArrayList<Partner>>> createCall() {
                return apiService.getUniversities();
            }
        }.getAsLiveServerData();
    }


    public LiveData<Resource<ArrayList<Partner>>> getSchools() {
        return new NetworkBoundResource<ArrayList<Partner>>() {
            @NonNull
            @Override
            protected Call<MyResponse<ArrayList<Partner>>> createCall() {
                return apiService.getSchools();
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<ArrayList<Partner>>> getRestaurants() {
        return new NetworkBoundResource<ArrayList<Partner>>() {
            @NonNull
            @Override
            protected Call<MyResponse<ArrayList<Partner>>> createCall() {
                return apiService.getRestaurants();
            }
        }.getAsLiveServerData();
    }


    public LiveData<Resource<ArrayList<Operator>>> getOperators() {
        return new NetworkBoundResource<ArrayList<Operator>>() {
            @NonNull
            @Override
            protected Call<MyResponse<ArrayList<Operator>>> createCall() {
                return apiService.getOperators();
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<SYGSData>> getSYGSData() {
        return new NetworkBoundResource<SYGSData>() {
            @NonNull
            @Override
            protected Call<MyResponse<SYGSData>> createCall() {
                return apiService.getSYGSData();
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<SygsCommissionData>> calculateSygsCommission(String amount, int type, String currencyCode) {
        return new NetworkBoundResource<SygsCommissionData>() {
            @NonNull
            @Override
            protected Call<MyResponse<SygsCommissionData>> createCall() {
                return apiService.calculateSygsCommission(amount, type, currencyCode);
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<Transaction>> sadakaTransfer(String accountNumber,
                                                          String fromAccountCode,
                                                          int charityId, String charityAccountNumber,
                                                          String phoneNumber,
                                                          String amount, String currencyCode,
                                                          String reason,
                                                          String myAccountName,
                                                          String pinCode) {
        return new NetworkBoundResource<Transaction>() {
            @NonNull
            @Override
            protected Call<MyResponse<Transaction>> createCall() {
                return apiService.sadakaTransfer(
                        accountNumber,
                        fromAccountCode,
                        charityId,
                        charityAccountNumber,
                        phoneNumber,
                        amount,
                        currencyCode,
                        reason,
                        myAccountName,
                        pinCode
                );
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<Transaction>> adslTransfer(String accountNumber,
                                                        String fromAccountCode,
                                                        int providerId,
                                                        String phoneNumber,
                                                        String amount,
                                                        String reason,
                                                        int cityId,
                                                        String userPhone,
                                                        String pinCode) {

        return new NetworkBoundResource<Transaction>() {
            @NonNull
            @Override
            protected Call<MyResponse<Transaction>> createCall() {
                return apiService.adslTransfer(
                        accountNumber,
                        fromAccountCode,
                        providerId,
                        phoneNumber,
                        amount,
                        reason,
                        userPhone,
                        cityId,
                        pinCode
                );
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<Transaction>> restaurantTransfer(String fromAccountNo,
                                                              String fromAccountCode,
                                                              String toAccountNo,
                                                              String amount, String currencyCode,
                                                              String myAccountName,
                                                              String billNumber,
                                                              String tips,
                                                              String reason,
                                                              int restaurantId,
                                                              String pinCode) {

        return new NetworkBoundResource<Transaction>() {
            @NonNull
            @Override
            protected Call<MyResponse<Transaction>> createCall() {
                return apiService.restaurantTransfer(
                        fromAccountNo,
                        fromAccountCode,
                        toAccountNo,
                        amount,
                        currencyCode,
                        myAccountName,
                        billNumber,
                        tips,
                        reason,
                        restaurantId,
                        pinCode);
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<Transaction>> universityTransfer(String fromAccountNo,
                                                              String fromAccountCode,
                                                              String toAccountNo,
                                                              String amount, String currencyCode,
                                                              String myAccountName,
                                                              String reason,
                                                              String studentName, String studentYear, String studentNumber,
                                                              int universityId,
                                                              String phoneNumber,
                                                              String pinCode) {

        return new NetworkBoundResource<Transaction>() {
            @NonNull
            @Override
            protected Call<MyResponse<Transaction>> createCall() {
                return apiService.universityTransfer(
                        fromAccountNo,
                        fromAccountCode,
                        toAccountNo,
                        amount,
                        currencyCode,
                        myAccountName,
                        reason,
                        studentName,
                        studentYear,
                        studentNumber,
                        universityId,
                        phoneNumber,
                        pinCode
                );
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<Transaction>> schoolTransfer(String fromAccountNo,
                                                          String fromAccountCode,
                                                          String toAccountNo,
                                                          String amount, String currencyCode,
                                                          String myAccountName,
                                                          String reason,
                                                          String studentName, String studentYear, String studentNumber,
                                                          String phoneNumber, int schoolId, String pinCode) {

        return new NetworkBoundResource<Transaction>() {
            @NonNull
            @Override
            protected Call<MyResponse<Transaction>> createCall() {
                return apiService.schoolTransfer(
                        fromAccountNo,
                        fromAccountCode,
                        toAccountNo,
                        amount,
                        currencyCode,
                        myAccountName,
                        reason,
                        studentName,
                        studentYear,
                        studentNumber,
                        phoneNumber,
                        schoolId,
                        pinCode
                );
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<Transaction>> mobilePaymentTransfer(String amount,
                                                                 int providerId,
                                                                 int lineTypeId,
                                                                 Integer paymentCategoryId,
                                                                 String mobileNumber,
                                                                 String fromAccountNo, String fromAccountCode, String myAccountName,
                                                                 String pinCode) {

        return new NetworkBoundResource<Transaction>() {
            @NonNull
            @Override
            protected Call<MyResponse<Transaction>> createCall() {
                return apiService.mobilePayment(
                        amount,
                        providerId,
                        lineTypeId,
                        paymentCategoryId,
                        mobileNumber,
                        fromAccountNo,
                        fromAccountCode,
                        "Mobile payment",
                        myAccountName,
                        pinCode
                );
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<Transaction>> sygsPaymentTransfer(SYGSTransferForm form, SYGSType type,
                                                               String fromAccountNo, String fromAccountCode, String myAccountName,
                                                               int bankId, String bankCode,
                                                               String currencyCode,
                                                               String pinCode) {

        return new NetworkBoundResource<Transaction>() {
            @NonNull
            @Override
            protected Call<MyResponse<Transaction>> createCall() {
                switch (type) {
                    case Property:
                        return apiService.propertyPayment(fromAccountNo, fromAccountCode, form.amount.getLocalizedNumber(), currencyCode, myAccountName, form.beneficiaryAddress.getValue(),
                                form.accountNo.getLocalizedValue(), bankId, bankCode, form.bankAddress.getValue(), form.fullName.getValue(),
                                form.propertyNo.getLocalizedValue(), form.propertyArea.getLocalizedValue(), form.contractNo.getLocalizedValue(),
                                form.contractDate.getLocalizedValue(), pinCode);
                    case Vehicles:
                        return apiService.vehiclePayment(fromAccountNo, fromAccountCode, form.amount.getLocalizedNumber(), currencyCode, myAccountName, form.beneficiaryAddress.getValue(),
                                form.accountNo.getLocalizedValue(), bankId, bankCode, form.bankAddress.getValue(),
                                form.fullName.getValue(), form.vehicleNo.getLocalizedValue(), form.vehicleType.getValue(), form.province.getValue(),
                                form.vehicleClass.getLocalizedValue(), form.chassisNo.getLocalizedValue(), form.vehicleModel.getLocalizedValue(),
                                form.contractNo.getLocalizedValue(), form.contractDate.getLocalizedValue(), pinCode);
                    case Lands:
                        return apiService.landsPayment(fromAccountNo, fromAccountCode, form.amount.getLocalizedNumber(), currencyCode, myAccountName, form.beneficiaryAddress.getValue(),
                                form.accountNo.getLocalizedValue(), bankId, bankCode, form.bankAddress.getValue(), form.fullName.getValue(),
                                form.landNo.getLocalizedValue(), form.propertyArea.getLocalizedValue(), form.contractNo.getLocalizedValue(), form.contractDate.getLocalizedValue(), pinCode);
                    case General:
                        return apiService.generalSYGSPayment(fromAccountNo, fromAccountCode, form.amount.getLocalizedNumber(), currencyCode, form.reason.getValue(),
                                myAccountName, form.beneficiaryAddress.getValue(), form.accountNo.getLocalizedValue(), bankId, bankCode,
                                form.bankAddress.getValue(), form.fullName.getValue(), pinCode);

                }
                return null;

            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<FavoriteAccount>> addFavoriteAccount(String accountName, String cifNumber, String gsmNumber) {
        return new NetworkBoundResource<FavoriteAccount>() {
            @NonNull
            @Override
            protected Call<MyResponse<FavoriteAccount>> createCall() {
                return apiService.addFavoriteAccount(
                        accountName,
                        cifNumber,
                        gsmNumber
                );
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<ArrayList<FavoriteAccount>>> getMyFavoriteAccounts() {
        return new NetworkBoundResource<ArrayList<FavoriteAccount>>() {
            @NonNull
            @Override
            protected Call<MyResponse<ArrayList<FavoriteAccount>>> createCall() {
                return apiService.getMyFavorites();
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<String>> deleteFavoriteAccount(int favoriteAccountId) {
        return new NetworkBoundResource<String>() {
            @NonNull
            @Override
            protected Call<MyResponse<String>> createCall() {
                return apiService.deleteFavorite(favoriteAccountId);
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<FavoriteAccount>> updateFavoriteAccount(int favoriteAccountId, FavoriteAccount updatedFavoriteAccount) {
        return new NetworkBoundResource<FavoriteAccount>() {
            @NonNull
            @Override
            protected Call<MyResponse<FavoriteAccount>> createCall() {
                return apiService.updateFavorite(favoriteAccountId, updatedFavoriteAccount);
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<String>> getBillsLink() {
        return new NetworkBoundResource<String>() {

            @NonNull
            @Override
            protected Call<MyResponse<String>> createCall() {
                return apiService.getEpayLink();
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<QrCode>> checkQrValidity(String code) {
        return new NetworkBoundResource<QrCode>() {

            @NonNull
            @Override
            protected Call<MyResponse<QrCode>> createCall() {
                return apiService.checkQrValidity(code);
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<Transaction>> qrPaymentTransfer(String code, String fromAccountNo, String fromAccountCode, String fromAccountName, String amount, String currencyCode, String pinCode, String reason) {
        return new NetworkBoundResource<Transaction>() {

            @NonNull
            @Override
            protected Call<MyResponse<Transaction>> createCall() {
                return apiService.qrPaymentTransfer(code, fromAccountNo, fromAccountCode, fromAccountName, amount, currencyCode, pinCode, reason);
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<Transaction>> alphaTransfer(String alphaClientId, String fromAccountNo, String fromAccountCode, String fromAccountName, String amount, String currencyCode, String pinCode) {
        return new NetworkBoundResource<Transaction>() {

            @NonNull
            @Override
            protected Call<MyResponse<Transaction>> createCall() {
                return apiService.alphaTransfer(alphaClientId, fromAccountNo, fromAccountCode, fromAccountName, amount, currencyCode, pinCode);
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<ArrayList<TransferData>>> getTransferSettings() {
        return new NetworkBoundResource<ArrayList<TransferData>>() {

            @NonNull
            @Override
            protected Call<MyResponse<ArrayList<TransferData>>> createCall() {
                return apiService.getTransferSettings();
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<SygsCommissionData>> getBarakaTransferFees(TransferChannelType channelType,
                                                                 String fromAccountNumber,
                                                                 String fromAccountCode,
                                                                 String amount,
                                                                 String cif,
                                                                 String toCif,String phoneNumber
                                                                 ) {
        return new NetworkBoundResource<SygsCommissionData>() {

            @NonNull
            @Override
            protected Call<MyResponse<SygsCommissionData>> createCall() {
                return apiService.alBarakaTransferFees( channelType.toString(),fromAccountNumber,fromAccountCode,amount,cif,toCif,phoneNumber);
            }
        }.getAsLiveServerData();
    }
}
