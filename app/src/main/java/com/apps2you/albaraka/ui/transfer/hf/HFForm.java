package com.apps2you.albaraka.ui.transfer.hf;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.apps2you.albaraka.data.remote.repository.UserRepository;
import com.apps2you.albaraka.ui.transfer.accountsTransfer.sygs.SYGSTransferForm;
import com.apps2you.albaraka.ui.transfer.base.BaseTransferForm;
import com.apps2you.albaraka.utils.text.LocalizedStringLiveData;
import com.apps2you.albaraka.viewmodels.transfer.base.TransferViewModel;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class HFForm extends BaseTransferForm {
    public LocalizedStringLiveData phoneNumber = new LocalizedStringLiveData("");
//    public final MutableLiveData<HFForm.FormStatus> status = new MutableLiveData<>();
    public final LocalizedStringLiveData firstName = new LocalizedStringLiveData("");
    public final LocalizedStringLiveData fatherName = new LocalizedStringLiveData("");
    public final LocalizedStringLiveData lastName = new LocalizedStringLiveData("");
    public final LocalizedStringLiveData reason = new LocalizedStringLiveData("");


    public final LocalizedStringLiveData benefAddress = new LocalizedStringLiveData("");
    public final MutableLiveData<String> beneficiaryAddress = new MutableLiveData<>("");
    public final MutableLiveData<Integer> benefAddressId = new MutableLiveData<>(0);
    public final MutableLiveData<String> fullName = new MutableLiveData<>("");
    public final LocalizedStringLiveData accountNo = new LocalizedStringLiveData("");
    public final LocalizedStringLiveData amount = new LocalizedStringLiveData("");

    private BigDecimal commission;
    private BigDecimal totalCost;


    public HFForm() {
        addMutableLiveDataStringField(phoneNumber);
        addMutableLiveDataStringField(firstName);
        addMutableLiveDataStringField(fatherName);
        addMutableLiveDataStringField(lastName);
        addMutableLiveDataStringField(amount);
        addMutableLiveDataStringField(reason);
        addMutableLiveDataStringField(beneficiaryAddress);
        addMutableLiveDataStringField(benefAddress);
    }

    public String getTotalCost() {
        NumberFormat formatter = NumberFormat.getInstance(Locale.ENGLISH);
        return formatter.format(totalCost);
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public void setAmount(BigDecimal amount) {
        this.amount.postValue(amount.toString());
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    @Override
    public boolean allowed() {

        return true;
    }




    public boolean isAmountInvalid(int min , int max) {
        String amountValueString = amount.getLocalizedNumber();
        if (TextUtils.isEmpty(amountValueString)) {
            return true; // Treat empty amount as invalid
        }
        int amount = Integer.parseInt(amountValueString);

        BigDecimal amountValue = new BigDecimal(amountValueString);
        return amountValue.compareTo(BigDecimal.ZERO) <= 0
                || amount < min
                || amount >= max;
    }

    public boolean isAmountEmpty() {
        return TextUtils.isEmpty(amount.getValue());
    }

    public boolean isReasonEmpty() {
        return TextUtils.isEmpty(reason.getValue());
    }


//    public boolean isAmountInvalid(Context context) {
//        BigDecimal amountValue = new BigDecimal(amount.getLocalizedNumber());
//        boolean isInvalid = amountValue.compareTo(BigDecimal.ZERO) <= 0
//                && amountValue.compareTo(BigDecimal.valueOf(tf.getTransferMinLimit())) >= 0
//                && amountValue.compareTo(BigDecimal.valueOf(tf.getTransferMaxLimit())) <= 0;
//
//        if (amountValue.compareTo(BigDecimal.ZERO) <= 0) {
//            Toast.makeText(context, "يجب أن يكون المبلغ أكبر من الصفر", Toast.LENGTH_SHORT).show();
//        } else if (amountValue.compareTo(BigDecimal.valueOf(tf.getTransferMinLimit())) >= 0) {
//            Toast.makeText(context, "يجب أن يكون المبلغ أكبر من أو يساوي الحد الأدنى", Toast.LENGTH_SHORT).show();
//        } else if (amountValue.compareTo(BigDecimal.valueOf(tf.getTransferMaxLimit())) <= 0) {
//            Toast.makeText(context, "يجب أن يكون المبلغ أقل من أو يساوي الحد الأقصى", Toast.LENGTH_SHORT).show();
//        }
//        if (isInvalid) {
//            Toast.makeText(context, "Invalid amount", Toast.LENGTH_SHORT).show();
//        }
//        return isInvalid;
//    }




    public Integer getAmountValue() {
        return stringValueToInt(amount);
    }
}

