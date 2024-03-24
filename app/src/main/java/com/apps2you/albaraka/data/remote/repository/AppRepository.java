package com.apps2you.albaraka.data.remote.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.apps2you.albaraka.data.model.About;
import com.apps2you.albaraka.data.model.Branch;
import com.apps2you.albaraka.data.model.City;
import com.apps2you.albaraka.data.model.Complaint;
import com.apps2you.albaraka.data.model.MobForm;
import com.apps2you.albaraka.data.model.NotificationContent;
import com.apps2you.albaraka.data.model.PrivacyPolicy;
import com.apps2you.albaraka.data.model.ProductCategory;
import com.apps2you.albaraka.data.model.ResetPassForm;
import com.apps2you.albaraka.data.model.Title;
import com.apps2you.albaraka.data.model.Transaction;
import com.apps2you.albaraka.data.remote.networkUtils.ApiService;
import com.apps2you.albaraka.data.remote.networkUtils.MyResponse;
import com.apps2you.albaraka.data.remote.networkUtils.NetworkBoundResource;
import com.apps2you.albaraka.data.remote.networkUtils.Resource;
import com.apps2you.albaraka.data.remote.responseModel.ExchangeData;

import java.util.ArrayList;

import javax.inject.Inject;

import retrofit2.Call;

public class AppRepository {

    private final ApiService apiService;

    @Inject
    public AppRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public LiveData<Resource<About>> getAbout() {
        return new NetworkBoundResource<About>() {
            @NonNull
            @Override
            protected Call<MyResponse<About>> createCall() {
                return apiService.getAbout();
            }
        }
                .getAsLiveServerData();
    }

    public LiveData<Resource<PrivacyPolicy>> getPrivacyPolicy() {
        return new NetworkBoundResource<PrivacyPolicy>() {
            @NonNull
            @Override
            protected Call<MyResponse<PrivacyPolicy>> createCall() {
                return apiService.getPrivacyPolicy();
            }
        }.getAsLiveServerData();
    }


    public NetworkBoundResource<ArrayList<Branch>> getBranchesRequest(final String type) {
        return new NetworkBoundResource<ArrayList<Branch>>() {

            @NonNull
            @Override
            protected Call<MyResponse<ArrayList<Branch>>> createCall() {
                return apiService.getBranches(type);
            }
        };
    }


    public LiveData<Resource<ArrayList<City>>> getCities() {
        return new NetworkBoundResource<ArrayList<City>>() {

            @NonNull
            @Override
            protected Call<MyResponse<ArrayList<City>>> createCall() {
                return apiService.getCities();
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<ArrayList<Branch>>> getBranches(final String type) {
        return getBranchesRequest(type).getAsLiveServerData();
    }

    public LiveData<Resource<ArrayList<Title>>> getComplaintTitles() {
        return new NetworkBoundResource<ArrayList<Title>>() {
            @NonNull
            @Override
            protected Call<MyResponse<ArrayList<Title>>> createCall() {
                return apiService.getComplaintTitles();
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<Complaint>> sendComplaint(Complaint complaint) {
        return new NetworkBoundResource<Complaint>() {
            @NonNull
            @Override
            protected Call<MyResponse<Complaint>> createCall() {
                return apiService.complaint(complaint.getFirstName(),
                        complaint.getLastName(),
                        complaint.getClientStatus(),
                        complaint.getMobileNumber(),
                        complaint.getPhoneNumber(),
                        complaint.getEmail(),
                        complaint.getContactTime(),
                        complaint.getComplaintTitleID(),
                        complaint.getBranchID(),
                        complaint.getComplaintDate(),
                        complaint.getMessage());
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<ArrayList<Title>>> getMobFormTitles() {
        return new NetworkBoundResource<ArrayList<Title>>() {
            @NonNull
            @Override
            protected Call<MyResponse<ArrayList<Title>>> createCall() {
                return apiService.getMobFormTitles();
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<MobForm>> sendMobForm(MobForm mobForm) {
        return new NetworkBoundResource<MobForm>() {
            @NonNull
            @Override
            protected Call<MyResponse<MobForm>> createCall() {
                return apiService.mobForm(
                        mobForm.getNational_id(),
                        mobForm.getCif_id(),
                        mobForm.getCaptcha_challenge(),
                        mobForm.getMobileNumber(),
                        mobForm.getComplaintTitleID());
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<String>> sendResetPassForm(String nationa_id,String cif,String otp ,String reset_password,String reset_pin) {
        return new NetworkBoundResource<String>() {
            @NonNull
            @Override
            protected Call<MyResponse<String>> createCall() {
                return apiService.resetPassForm(
                        nationa_id,
                        cif,
                        otp,
                        reset_password,reset_pin);
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<ExchangeData>> getExchangeRates() {
        return new NetworkBoundResource<ExchangeData>() {
            @NonNull
            @Override
            protected Call<MyResponse<ExchangeData>> createCall() {
                return apiService.getExchangeRates();
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<ArrayList<NotificationContent>>> getNotifications() {
        return new NetworkBoundResource<ArrayList<NotificationContent>>() {
            @NonNull
            @Override
            protected Call<MyResponse<ArrayList<NotificationContent>>> createCall() {
                return apiService.getNotifications();
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<String>> deleteNotification(int id) {
        return new NetworkBoundResource<String>() {
            @NonNull
            @Override
            protected Call<MyResponse<String>> createCall() {
                return apiService.deleteNotification(id);
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<ArrayList<ProductCategory>>> getProductsCategories() {
        return new NetworkBoundResource<ArrayList<ProductCategory>>() {
            @NonNull
            @Override
            protected Call<MyResponse<ArrayList<ProductCategory>>> createCall() {
                return apiService.getProductsCategories();
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<Transaction>> getTransactionDetails(int id, String branchCode) {
        return new NetworkBoundResource<Transaction>() {
            @NonNull
            @Override
            protected Call<MyResponse<Transaction>> createCall() {
                return apiService.getTransactionDetails(id, branchCode);
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<Boolean>> checkVisitor() {
        return new NetworkBoundResource<Boolean>() {
            @NonNull
            @Override
            protected Call<MyResponse<Boolean>> createCall() {
                return apiService.checkVisitor();
            }
        }.getAsLiveServerData();
    }
}
