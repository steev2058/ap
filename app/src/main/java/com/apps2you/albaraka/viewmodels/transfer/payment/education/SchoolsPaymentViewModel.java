package com.apps2you.albaraka.viewmodels.transfer.payment.education;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.apps2you.albaraka.data.remote.networkUtils.Resource;
import com.apps2you.albaraka.data.remote.repository.TransferRepository;
import com.apps2you.albaraka.data.remote.repository.UserRepository;
import com.apps2you.albaraka.ui.common.model.SchoolUI;
import com.apps2you.albaraka.ui.common.model.mapper.SchoolUIMapper;
import com.apps2you.albaraka.utils.Constants;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class SchoolsPaymentViewModel extends BaseEducationViewModel<SchoolUI> {
    private final SchoolUIMapper schoolUIMapper;

    @Inject
    public SchoolsPaymentViewModel(UserRepository userRepository,
                                   TransferRepository transferRepository,
                                   SchoolUIMapper schoolUIMapper) {
        super(userRepository, transferRepository);
        this.schoolUIMapper = schoolUIMapper;
    }

    @Override
    public int getTransferTypeId() {
        return Constants.TRANSFER_SCHOOL;
    }

    @Override
    protected List<SchoolUI> filter(List<SchoolUI> data, String searchQuery) {
        return data.stream()
                .filter(
                        school -> school.getName().toLowerCase().contains(searchQuery.toLowerCase())
                )
                .collect(Collectors.toList());
    }

    @Override
    protected LiveData<Resource<List<SchoolUI>>> provideDataSource() {
        return Transformations.map(
                transferRepository.getSchools(),
                resource -> resource.mapData(schoolUIMapper::map)
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
                    transferRepository.schoolTransfer(
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
                            educationForm.phone.getLocalizedValue(),
                            selectedItemLiveData.getValue().getId(),
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
