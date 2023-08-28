package com.apps2you.albaraka.viewmodels.transfer.payment.education;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.apps2you.albaraka.data.remote.networkUtils.Resource;
import com.apps2you.albaraka.data.remote.repository.TransferRepository;
import com.apps2you.albaraka.data.remote.repository.UserRepository;
import com.apps2you.albaraka.ui.common.model.UniversityUI;
import com.apps2you.albaraka.ui.common.model.mapper.UniversityUIMapper;
import com.apps2you.albaraka.utils.Constants;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class UniversitiesPaymentViewModel extends BaseEducationViewModel<UniversityUI> {
    private final UniversityUIMapper universityUIMapper;

    @Inject
    public UniversitiesPaymentViewModel(UserRepository userRepository,
                                        TransferRepository transferRepository,
                                        UniversityUIMapper universityUIMapper) {
        super(userRepository, transferRepository);
        this.universityUIMapper = universityUIMapper;
    }

    @Override
    public int getTransferTypeId() {
        return Constants.TRANSFER_UNIVERSITY;
    }

    @Override
    protected List<UniversityUI> filter(List<UniversityUI> data, String searchQuery) {
        return data.stream()
                .filter(
                        university -> university.getName().toLowerCase().contains(searchQuery.toLowerCase())
                )
                .collect(Collectors.toList());
    }

    @Override
    protected LiveData<Resource<List<UniversityUI>>> provideDataSource() {
        return Transformations.map(
                transferRepository.getUniversities(),
                resource -> resource.mapData(universityUIMapper::map)
        );
    }

    @Override
    public void transfer() {
        if (selectedAccount.getValue() != null && selectedItemLiveData.getValue() != null
                && educationForm.allowed()) {
            if (isLoadingValue()) {
                return;
            }
            _transferStatus.addSource(
                    transferRepository.universityTransfer(
                            selectedAccount.getValue().getNumber(),
                            selectedAccount.getValue().getAccountCode(),
                            selectedItemLiveData.getValue().getAccountNumber(),
                            educationForm.amount.getLocalizedNumber(),
                            selectedAccount.getValue().getCurrency().getCode(),
                            selectedAccount.getValue().getType(),
                            educationForm.reason.getValue(),
                            educationForm.studentName.getValue(),
                            educationForm.studentYear.getLocalizedValue(),
                            educationForm.studentNumber.getLocalizedValue(),
                            selectedItemLiveData.getValue().getId(),
                            educationForm.phone.getLocalizedValue(),
                            pinCode
                    ),
                    this::handleTransferResponse
            );
        }
    }

    @Override
    public void calculateCommission() {

    }
}
