package com.apps2you.albaraka.ui.transfer.accountsTransfer.alBarakaTransfer;

import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;

import com.apps2you.albaraka.data.model.FavoriteAccount;
import com.apps2you.albaraka.ui.transfer.base.BaseTransferForm;
import com.apps2you.albaraka.utils.text.LocalizedStringLiveData;

public class AlBarakaTransferForm extends BaseTransferForm {
    public final MutableLiveData<Boolean> transferToCIF = new MutableLiveData<>(true);

    public final LocalizedStringLiveData toCIFNumber = new LocalizedStringLiveData("");

    public final LocalizedStringLiveData toGSMNumber = new LocalizedStringLiveData("");

    public final MutableLiveData<FavoriteAccount> selectedFavoriteAccount = new MutableLiveData<>();


    @Override
    public boolean allowed() {
        return super.allowed()
                && (((isCIFSelected() && !TextUtils.isEmpty(toCIFNumber.getValue()))
                || (!isCIFSelected() && !TextUtils.isEmpty(toGSMNumber.getValue()))
              //     || (!isCIFSelected() && com.apps2you.albaraka.utils.text.TextUtils.isValidPhoneNumber(toGSMNumber.getValue()))
        ) || (selectedFavoriteAccount.getValue() != null));
    }

    public boolean isCIFEmpty() {
        return TextUtils.isEmpty(toCIFNumber.getValue());
    }

    public boolean isGSSMEmpty() {
        return TextUtils.isEmpty(toGSMNumber.getValue());
    }

    public void setTransferUsingCIF(boolean isCIF) {
        transferToCIF.setValue(isCIF);
    }

    public String getNumber() {
        return isCIFSelected() ? toCIFNumber.getLocalizedValue() : toGSMNumber.getLocalizedValue();
    }

//    @Override
//    public int getErrorResource() {
//        if (!isCIFSelected() && !com.apps2you.albaraka.utils.text.TextUtils.isValidPhoneNumber(toGSMNumber.getValue())) {
//            return R.string.please_enter_valid_phone_number;
//        }
//        return super.getErrorResource();
//    }

    public boolean isCIFSelected() {
        return transferToCIF.getValue();
    }
}
