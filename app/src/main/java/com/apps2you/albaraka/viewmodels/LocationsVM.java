package com.apps2you.albaraka.viewmodels;

import androidx.lifecycle.LiveData;

import com.apps2you.albaraka.data.model.Branch;
import com.apps2you.albaraka.data.remote.networkUtils.MyResponse;
import com.apps2you.albaraka.data.remote.networkUtils.NetworkBoundResource;
import com.apps2you.albaraka.data.remote.networkUtils.Resource;
import com.apps2you.albaraka.data.remote.repository.AppRepository;
import com.apps2you.albaraka.ui.base.BaseViewModel;
import com.apps2you.albaraka.utils.Constants;

import java.util.ArrayList;

import javax.inject.Inject;

import retrofit2.Call;

public class LocationsVM extends BaseViewModel {

    private final AppRepository repository;
    private final ArrayList<Branch> branches = new ArrayList<>();
    private final ArrayList<Branch> atms = new ArrayList<>();
    private final ArrayList<Branch> pos = new ArrayList<>();

    private Call<MyResponse<ArrayList<Branch>>> branchesCall;
    private String selectedType = Constants.TYPE_BRANCH;

    @Inject
    LocationsVM(AppRepository appRepository) {
        this.repository = appRepository;
    }

    public LiveData<Resource<ArrayList<Branch>>> getLocations(String type) {
        // if user clicked "Branch"/ "ATM"/ "POS" buttons one after the other
        // before the first response is delivered
        // the map will contain markers for both branches and ATMs
        cancelPreviousCall();

        setSelectedType(type);
        NetworkBoundResource<ArrayList<Branch>> request = repository.getBranchesRequest(null/*send null so the response contains branches, atms and pos */);
        branchesCall = request.getCall();
        return request.getAsLiveServerData();
    }

    public void clearLocationsLists() {
        this.branches.clear();
        this.atms.clear();
        this.pos.clear();
    }

    public void setSelectedType(String selectedType) {
        this.selectedType = selectedType;
    }

    public void addAllLocations(ArrayList<Branch> branches) {
        for (Branch branch : branches) {
            if (branch.isBranch())
                this.branches.add(branch);
            else if (branch.isAtm())
                this.atms.add(branch);
            else if (branch.isPos())
                this.pos.add(branch);
        }
//        this.branches.addAll(branches);
    }

    public Branch getLocation(final int position) {
        if (selectedType.equals(Constants.TYPE_BRANCH) && position >= 0 && position < this.branches.size())
            return this.branches.get(position);
        else if (selectedType.equals(Constants.TYPE_ATM) && position >= 0 && position < this.atms.size())
            return this.atms.get(position);
        else if (selectedType.equals(Constants.TYPE_POS) && position >= 0 && position < this.pos.size())
            return this.pos.get(position);
        else
            return null;
    }

    public ArrayList<Branch> getBranches() {
        return this.branches;
    }

    public ArrayList<Branch> getAtms() {
        return this.atms;
    }

    public ArrayList<Branch> getPos() {
        return this.pos;
    }

    private void cancelPreviousCall() {
        if (branchesCall != null && !branchesCall.isCanceled())
            branchesCall.cancel();
    }
}
