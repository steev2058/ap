package com.apps2you.albaraka.viewmodels.transfer.payment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.apps2you.albaraka.data.model.LineType;
import com.apps2you.albaraka.data.model.Operator;
import com.apps2you.albaraka.data.model.PaymentCategory;
import com.apps2you.albaraka.data.remote.repository.TransferRepository;
import com.apps2you.albaraka.data.remote.repository.UserRepository;
import com.apps2you.albaraka.ui.transfer.payment.mobile.MobilePaymentForm;
import com.apps2you.albaraka.utils.Constants;
import com.apps2you.albaraka.utils.lifecyle.Event;
import com.apps2you.albaraka.utils.text.TextUtils;
import com.apps2you.albaraka.viewmodels.transfer.base.TransferViewModel;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class MobilePaymentViewModel extends TransferViewModel {
    public MobilePaymentForm form = new MobilePaymentForm();

    private final MediatorLiveData<List<Operator>> _operatorList = new MediatorLiveData<>();
    public final LiveData<List<Operator>> operatorList = _operatorList;

    private final MutableLiveData<Operator> _selectedOperator = new MutableLiveData<>();
    public final LiveData<Operator> selectedOperator = _selectedOperator;

    private final MutableLiveData<LineType> _selectedLineType = new MutableLiveData<>();
    public final LiveData<LineType> selectedLineType = _selectedLineType;

    private final MutableLiveData<PaymentCategory> _selectedPaymentCategory = new MutableLiveData<>();
    public final LiveData<PaymentCategory> selectedPaymentCategory = _selectedPaymentCategory;

    private final MutableLiveData<Boolean> _isOperatorsLoading = new MutableLiveData<>(false);
    public final LiveData<Boolean> isOperatorsLoading = _isOperatorsLoading;

    private final MutableLiveData<Event<Boolean>> _paymentConfirmed = new MutableLiveData<>();
    public final LiveData<Event<Boolean>> paymentConfirmed = _paymentConfirmed;

    @Inject
    public MobilePaymentViewModel(UserRepository userRepository,
                                  TransferRepository transferRepository) {
        super(userRepository, transferRepository);
        fetchOperators();
    }

    @Override
    public int getTransferTypeId() {
        return -1;
    }

    @Override
    public void transfer() {
        if (selectedAccount.getValue() != null && selectedOperator.getValue() != null
                && selectedLineType.getValue() != null && form.allowed()) {
            if (isLoadingValue()) {
                return;
            }
            _transferStatus.addSource(
                    transferRepository.mobilePaymentTransfer(
                            isPostpaid() ? form.amount.getLocalizedNumber() :
                                    selectedPaymentCategory.getValue() != null ? selectedPaymentCategory.getValue().getAmount().toString() : null,
                            selectedOperator.getValue().getId(),
                            selectedLineType.getValue().getLineId(),
                            (!isPostpaid() && selectedPaymentCategory.getValue() != null) ? selectedPaymentCategory.getValue().getCategoryId() : null,
                            TextUtils.withoutCountryCode(form.gsmNumber.getLocalizedValue()),
                            selectedAccount.getValue().getNumber(),
                            selectedAccount.getValue().getAccountCode(),
                            selectedAccount.getValue().getType(),
                            pinCode
                    )
                    , this::handleTransferResponse
            );
        }
    }

    @Override
    public void calculateCommission() {

    }

    @Override
    public void fetchFees() {
        // these fees are not needed with mobile transfer
    }

    public void fetchOperators() {
        if (isOperatorsLoading.getValue() != null && isOperatorsLoading.getValue()) {
            return;
        }
        _operatorList.addSource(
                transferRepository.getOperators(),
                resource -> {
                    _isOperatorsLoading.setValue(false);
                    switch (resource.status) {
                        case LOADING:
                            _isOperatorsLoading.setValue(true);
                            break;
                        case ERROR:
                            setError(resource.error);
                            break;
                        case SUCCESS:
                            _operatorList.setValue(resource.data);
                            break;
                    }
                }
        );
    }

    public void selectOperator(Operator operator) {
        _selectedOperator.setValue(operator);
        selectLineType(null);
    }

    public void selectLineType(LineType lineType) {
        _selectedLineType.setValue(lineType);
        selectPaymentCategory(null);
    }

    public void selectPaymentCategory(PaymentCategory paymentCategory) {
        _selectedPaymentCategory.setValue(paymentCategory);
    }

    public void confirmPayment() {
        _paymentConfirmed.setValue(Event.of(true));
    }

    public boolean isPostpaid() {
        return _selectedLineType.getValue() != null && _selectedLineType.getValue().getType().equals(Constants.POST_PAID);
    }

    public BigDecimal getDisplayFees() {
        return isPostpaid() ? selectedOperator.getValue().getPostPaidFee() : selectedPaymentCategory.getValue().getFees();
    }

    public BigDecimal getDisplayTax() {
        return isPostpaid() ? selectedOperator.getValue().getPostPaidTax() : selectedPaymentCategory.getValue().getTax();
    }

    public boolean showFees() {
        return !getDisplayFees().equals(BigDecimal.ZERO);
    }

    public boolean showTax() {
        return !getDisplayTax().equals(BigDecimal.ZERO);
    }

    public String getTotal() {
        BigDecimal total = isPostpaid() ? new BigDecimal(form.amount.getLocalizedNumber())
                .add(selectedOperator.getValue().getPostPaidFee())
                .add(selectedOperator.getValue().getPostPaidTax())
                : selectedPaymentCategory.getValue().getTotalCost();

        NumberFormat formatter = NumberFormat.getInstance(Locale.ENGLISH);
        return formatter.format(total);
    }
}
