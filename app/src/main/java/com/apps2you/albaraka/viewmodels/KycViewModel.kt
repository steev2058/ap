package com.apps2you.albaraka.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.apps2you.albaraka.data.model.Complaint
import com.apps2you.albaraka.data.model.Kyc
import com.apps2you.albaraka.data.remote.repository.AppRepository
import com.apps2you.albaraka.ui.base.BaseViewModel
import javax.inject.Inject

 class KycViewModel @Inject constructor(private val appRepository: AppRepository) : BaseViewModel() {


    private val _kyc = MutableLiveData<Kyc>()
    val kyc: LiveData<Kyc> get() = _kyc

     init {
         _kyc.value = Kyc()
     }



     fun onFirstNameChanged(newFirstName: String) {
        _kyc.value = _kyc.value?.copy(firstName = newFirstName) ?: Kyc(firstName = newFirstName)
    }

    // Function to update jobbh in the Kyc object
    fun onJobbhChanged(newJobbh: String) {
        _kyc.value = _kyc.value?.copy(jobbh = newJobbh) ?: Kyc(jobbh = newJobbh)
    }

    // Function to update dateOfBirth in the Kyc object
    fun onDateOfBirthChanged(newDateOfBirth: String) {
        _kyc.value = _kyc.value?.copy(dateOfBirth = newDateOfBirth) ?: Kyc(dateOfBirth = newDateOfBirth)
    }

    // Function to update email in the Kyc object
    fun onEmailChanged(newEmail: String) {
        _kyc.value = _kyc.value?.copy(email = newEmail) ?: Kyc(email = newEmail)
    }

    // Function to update address in the Kyc object
    fun onAddressChanged(newAddress: String) {
        _kyc.value = _kyc.value?.copy(address = newAddress) ?: Kyc(address = newAddress)
    }

    // Function to update fatherName in the Kyc object
    fun onFatherNameChanged(newFatherName: String) {
        _kyc.value = _kyc.value?.copy(fatherName = newFatherName) ?: Kyc(fatherName = newFatherName)
    }

    // Function to update fatherPercentage in the Kyc object
    fun onFatherPercentageChanged(newFatherPercentage: String) {
        _kyc.value = _kyc.value?.copy(fatherPercentage = newFatherPercentage) ?: Kyc(fatherPercentage = newFatherPercentage)
    }

    // Function to update birthPlace in the Kyc object
    fun onBirthPlaceChanged(newBirthPlace: String) {
        _kyc.value = _kyc.value?.copy(birthPlace = newBirthPlace) ?: Kyc(birthPlace = newBirthPlace)
    }

    // Function to update motherName in the Kyc object
    fun onMotherNameChanged(newMotherName: String) {
        _kyc.value = _kyc.value?.copy(motherName = newMotherName) ?: Kyc(motherName = newMotherName)
    }

    // Function to update motherPercentage in the Kyc object
    fun onMotherPercentageChanged(newMotherPercentage: String) {
        _kyc.value = _kyc.value?.copy(motherPercentage = newMotherPercentage) ?: Kyc(motherPercentage = newMotherPercentage)
    }

    // Function to update nationality in the Kyc object
    fun onNationalityChanged(newNationality: String) {
        _kyc.value = _kyc.value?.copy(nationality = newNationality) ?: Kyc(nationality = newNationality)
    }

    // Function to update gender in the Kyc object
    fun onGenderChanged(newGender: String) {
        _kyc.value = _kyc.value?.copy(gender = newGender) ?: Kyc(gender = newGender)
    }

    // Function to update gender2 in the Kyc object
    fun onGender2Changed(newGender2: String) {
        _kyc.value = _kyc.value?.copy(gender2 = newGender2) ?: Kyc(gender2 = newGender2)
    }

    // Function to update typeId in the Kyc object
    fun onTypeIdChanged(newTypeId: String) {
        _kyc.value = _kyc.value?.copy(typeId = newTypeId) ?: Kyc(typeId = newTypeId)
    }

    // Function to update nationalNumber in the Kyc object
    fun onNationalNumberChanged(newNationalNumber: String) {
        _kyc.value = _kyc.value?.copy(nationalNumber = newNationalNumber) ?: Kyc(nationalNumber = newNationalNumber)
    }

    // Function to update nationalPlace in the Kyc object
    fun onNationalPlaceChanged(newNationalPlace: String) {
        _kyc.value = _kyc.value?.copy(nationalPlace = newNationalPlace) ?: Kyc(nationalPlace = newNationalPlace)
    }

    // Function to update kayed in the Kyc object
    fun onKayedChanged(newKayed: String) {
        _kyc.value = _kyc.value?.copy(kayed = newKayed) ?: Kyc(kayed = newKayed)
    }

    // Function to update addrr in the Kyc object
    fun onAddrrChanged(newAddrr: String) {
        _kyc.value = _kyc.value?.copy(addrr = newAddrr) ?: Kyc(addrr = newAddrr)
    }

    // Function to update mobileNumber in the Kyc object
    fun onMobileNumberChanged(newMobileNumber: String) {
        _kyc.value = _kyc.value?.copy(mobileNumber = newMobileNumber) ?: Kyc(mobileNumber = newMobileNumber)
    }

    // Function to update job in the Kyc object
    fun onJobChanged(newJob: String) {
        _kyc.value = _kyc.value?.copy(job = newJob) ?: Kyc(job = newJob)
    }

    // Function to update studentStatus in the Kyc object
    fun onStudentStatusChanged(newStudentStatus: String) {
        _kyc.value = _kyc.value?.copy(studentStatus = newStudentStatus) ?: Kyc(studentStatus = newStudentStatus)
    }

    // Function to update governorate in the Kyc object
    fun onGovernorateChanged(newGovernorate: String) {
        _kyc.value = _kyc.value?.copy(governorate = newGovernorate) ?: Kyc(governorate = newGovernorate)
    }

    // Function to update university in the Kyc object
    fun onUniversityChanged(newUniversity: String) {
        _kyc.value = _kyc.value?.copy(university = newUniversity) ?: Kyc(university = newUniversity)
    }

    // Function to update college in the Kyc object
    fun onCollegeChanged(newCollege: String) {
        _kyc.value = _kyc.value?.copy(college = newCollege) ?: Kyc(college = newCollege)
    }

    // Function to update fatcaCompliance in the Kyc object
    fun onFatcaComplianceChanged(newFatcaCompliance: String) {
        _kyc.value = _kyc.value?.copy(fatcaCompliance = newFatcaCompliance) ?: Kyc(fatcaCompliance = newFatcaCompliance)
    }

    // Function to update personalImage in the Kyc object
    fun onPersonalImageChanged(newPersonalImage: String) {
        _kyc.value = _kyc.value?.copy(personalImage = newPersonalImage) ?: Kyc(personalImage = newPersonalImage)
    }

    // Function to update idBackImage in the Kyc object
    fun onIdBackImageChanged(newIdBackImage: String) {
        _kyc.value = _kyc.value?.copy(idBackImage = newIdBackImage) ?: Kyc(idBackImage = newIdBackImage)
    }

    // Function to update idFrontImage in the Kyc object
    fun onIdFrontImageChanged(newIdFrontImage: String) {
        _kyc.value = _kyc.value?.copy(idFrontImage = newIdFrontImage) ?: Kyc(idFrontImage = newIdFrontImage)
    }

    // Function to update universityCardImage in the Kyc object
    fun onUniversityCardImageChanged(newUniversityCardImage: String) {
        _kyc.value = _kyc.value?.copy(universityCardImage = newUniversityCardImage) ?: Kyc(universityCardImage = newUniversityCardImage)
    }

    // Function to update agreementCheckbox in the Kyc object
    fun onAgreementCheckboxChanged(newAgreementCheckbox: Boolean) {
        _kyc.value = _kyc.value?.copy(agreementCheckbox = newAgreementCheckbox) ?: Kyc(agreementCheckbox = newAgreementCheckbox)
    }

    // Function to update firstNameEn in the Kyc object
    fun onFirstNameEnChanged(newFirstNameEn: String) {
        _kyc.value = _kyc.value?.copy(firstNameEn = newFirstNameEn) ?: Kyc(firstNameEn = newFirstNameEn)
    }

    // Function to update fatherNameEn in the Kyc object
    fun onFatherNameEnChanged(newFatherNameEn: String) {
        _kyc.value = _kyc.value?.copy(fatherNameEn = newFatherNameEn) ?: Kyc(fatherNameEn = newFatherNameEn)
    }

    // Function to update lastNameEn in the Kyc object
    fun onLastNameEnChanged(newLastNameEn: String) {
        _kyc.value = _kyc.value?.copy(lastNameEn = newLastNameEn) ?: Kyc(lastNameEn = newLastNameEn)
    }

    // Function to update country in the Kyc object
    fun onCountryChanged(newCountry: String) {
        _kyc.value = _kyc.value?.copy(country = newCountry) ?: Kyc(country = newCountry)
    }

    // Function to update agreementCheckbox1 in the Kyc object
    fun onAgreementCheckbox1Changed(newAgreementCheckbox1: Boolean) {
        _kyc.value = _kyc.value?.copy(agreementCheckbox1 = newAgreementCheckbox1) ?: Kyc(agreementCheckbox1 = newAgreementCheckbox1)
    }

    // Function to update branchSpinnerId in the Kyc object
    fun onBranchSpinnerIdChanged(newBranchSpinnerId: String) {
        _kyc.value = _kyc.value?.copy(branchSpinnerId = newBranchSpinnerId) ?: Kyc(branchSpinnerId = newBranchSpinnerId)
    }

    // Function to update agreementCheckbox2 in the Kyc object
    fun onAgreementCheckbox2Changed(newAgreementCheckbox2: Boolean) {
        _kyc.value = _kyc.value?.copy(agreementCheckbox2 = newAgreementCheckbox2) ?: Kyc(agreementCheckbox2 = newAgreementCheckbox2)
    }

//     Function to update captchaInput in the Kyc object
    fun onCaptchaInputChanged(newCaptchaInput: String) {
        _kyc.value = _kyc.value?.copy(captchaInput = newCaptchaInput) ?: Kyc(captchaInput = newCaptchaInput)
    }
}
