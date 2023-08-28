package com.apps2you.albaraka.ui.transfer.accountsTransfer.sygs;

import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;

import com.apps2you.albaraka.data.model.SYGSType;
import com.apps2you.albaraka.ui.base.BaseForm;
import com.apps2you.albaraka.utils.text.LocalizedStringLiveData;

public class SYGSTransferForm extends BaseForm {
    public final MutableLiveData<FormStatus> status = new MutableLiveData<>();

    public final MutableLiveData<String> bankAddress = new MutableLiveData<>("");
    public final MutableLiveData<String> beneficiaryAddress = new MutableLiveData<>("");
    public final MutableLiveData<String> reason = new MutableLiveData<>("");
    public final MutableLiveData<String> fullName = new MutableLiveData<>("");
    public final LocalizedStringLiveData accountNo = new LocalizedStringLiveData("");
    public final LocalizedStringLiveData amount = new LocalizedStringLiveData("");
    public final LocalizedStringLiveData landNo = new LocalizedStringLiveData("");
    public final LocalizedStringLiveData propertyNo = new LocalizedStringLiveData("");
    public final LocalizedStringLiveData propertyArea = new LocalizedStringLiveData("");
    public final LocalizedStringLiveData contractNo = new LocalizedStringLiveData("");
    public final LocalizedStringLiveData contractDate = new LocalizedStringLiveData("");
    public final LocalizedStringLiveData vehicleNo = new LocalizedStringLiveData("");
    public final LocalizedStringLiveData vehicleModel = new LocalizedStringLiveData("");
    public final MutableLiveData<String> vehicleType = new MutableLiveData<>("");
    public final MutableLiveData<String> province = new MutableLiveData<>("");
    public final LocalizedStringLiveData vehicleClass = new LocalizedStringLiveData("");
    public final LocalizedStringLiveData chassisNo = new LocalizedStringLiveData("");

    SYGSType type;

    public SYGSTransferForm(SYGSType type) {
        this.type = type;
        addMutableLiveDataStringField(beneficiaryAddress, fullName, accountNo, amount);
    }

    @Override
    public boolean allowed() {
        if (TextUtils.isEmpty(fullName.getValue())) {
            status.setValue(FormStatus.ERROR_FULL_NAME);
        } else if (TextUtils.isEmpty(accountNo.getValue())) {
            status.setValue(FormStatus.ERROR_BANK_ACCOUNT);
        } else if (TextUtils.isEmpty(beneficiaryAddress.getValue())) {
            status.setValue(FormStatus.ERROR_BENEFICIARY_ADDRESS);
        } else if (TextUtils.isEmpty(amount.getValue())) {
            status.setValue(FormStatus.ERROR_AMOUNT);
        } else if (type == SYGSType.General && TextUtils.isEmpty(reason.getValue())) {
            status.setValue(FormStatus.ERROR_REASON);
        } else if (type == SYGSType.Lands) {
            if (TextUtils.isEmpty(landNo.getValue())) {
                status.setValue(FormStatus.ERROR_LAND_NO);
            } else if (TextUtils.isEmpty(propertyArea.getValue())) {
                status.setValue(FormStatus.ERROR_PROPERTY_AREA);
            } else if (TextUtils.isEmpty(contractNo.getValue())) {
                status.setValue(FormStatus.ERROR_CONTRACT_NO);
            } else if (TextUtils.isEmpty(contractDate.getValue())) {
                status.setValue(FormStatus.ERROR_CONTRACT_DATE);
            } else return true;

        } else if (type == SYGSType.Property) {
            if (TextUtils.isEmpty(propertyNo.getValue())) {
                status.setValue(FormStatus.ERROR_PROPERTY_NO);
            } else if (TextUtils.isEmpty(propertyArea.getValue())) {
                status.setValue(FormStatus.ERROR_PROPERTY_AREA);
            } else if (TextUtils.isEmpty(contractNo.getValue())) {
                status.setValue(FormStatus.ERROR_CONTRACT_NO);
            } else if (TextUtils.isEmpty(contractDate.getValue())) {
                status.setValue(FormStatus.ERROR_CONTRACT_DATE);
            } else return true;

        } else if (type == SYGSType.Vehicles) {
            if (TextUtils.isEmpty(vehicleNo.getValue())) {
                status.setValue(FormStatus.ERROR_VEHICLE_NO);
            } else if (TextUtils.isEmpty(vehicleType.getValue())) {
                status.setValue(FormStatus.ERROR_VEHICLE_TYPE);
            } else if (TextUtils.isEmpty(vehicleModel.getValue())) {
                status.setValue(FormStatus.ERROR_VEHICLE_MODEL);
            } else if (TextUtils.isEmpty(province.getValue())) {
                status.setValue(FormStatus.ERROR_PROVINCE);
            } else if (TextUtils.isEmpty(vehicleClass.getValue())) {
                status.setValue(FormStatus.ERROR_VEHICLE_CLASS);
            } else if (TextUtils.isEmpty(contractNo.getValue())) {
                status.setValue(FormStatus.ERROR_CONTRACT_NO);
            } else if (TextUtils.isEmpty(contractDate.getValue())) {
                status.setValue(FormStatus.ERROR_CONTRACT_DATE);
            } else if (TextUtils.isEmpty(chassisNo.getValue())) {
                status.setValue(FormStatus.ERROR_VEHICLE_CHASSIS);
            } else return true;
        } else {
            return true;
        }
        return false;
    }

    public void setType(SYGSType type) {
        this.type = type;
        if (type == SYGSType.Lands) {
            addMutableLiveDataStringField(landNo, propertyArea, contractNo, contractDate);
        } else if (type == SYGSType.Property) {
            addMutableLiveDataStringField(propertyNo, propertyArea, contractNo, contractDate);
        } else if (type == SYGSType.Vehicles) {
            addMutableLiveDataStringField(vehicleNo, vehicleType, vehicleModel, province, vehicleClass, chassisNo, contractNo, contractDate);

        } else if (type == SYGSType.General) addMutableLiveDataStringField(reason);
    }

    public void reset() {
        bankAddress.setValue("");
        beneficiaryAddress.setValue("");
        reason.setValue("");
        fullName.setValue("");
        accountNo.setValue("");
        amount.setValue("");
        landNo.setValue("");
        propertyNo.setValue("");
        propertyArea.setValue("");
        contractNo.setValue("");
        contractDate.setValue("");
        vehicleNo.setValue("");
        vehicleModel.setValue("");
        vehicleType.setValue("");
        province.setValue("");
        vehicleClass.setValue("");
        chassisNo.setValue("");
    }

    public enum FormStatus {
        ALLOWED,
        ERROR_BANK_ADDRESS,
        ERROR_FULL_NAME,
        ERROR_BANK_ACCOUNT,
        ERROR_BENEFICIARY_ADDRESS,
        ERROR_AMOUNT,
        ERROR_REASON,

        ERROR_LAND_NO,
        ERROR_PROPERTY_NO,
        ERROR_PROPERTY_AREA,
        ERROR_CONTRACT_NO,
        ERROR_CONTRACT_DATE,
        ERROR_VEHICLE_NO,
        ERROR_VEHICLE_TYPE,
        ERROR_VEHICLE_MODEL,
        ERROR_PROVINCE,
        ERROR_VEHICLE_CLASS,
        ERROR_VEHICLE_CHASSIS
    }
}
