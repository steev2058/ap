package com.apps2you.albaraka.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.apps2you.albaraka.data.model.Finance
import com.apps2you.albaraka.data.remote.repository.AppRepository
import com.apps2you.albaraka.ui.base.BaseViewModel
import javax.inject.Inject

class FinanceFormViewModel @Inject constructor(private val appRepository: AppRepository) : BaseViewModel() {


    private val _finance = MutableLiveData<Finance>()
    val finance: LiveData<Finance> get() = _finance

    init {
        _finance.value = Finance()
    }



    fun onFirstNameChanged(newFirstName: String) {
        _finance.value = _finance.value?.copy(firstName = newFirstName) ?: Finance(firstName = newFirstName)
    }

    // Function to update jobbh in the Finance object
    fun onJobbhChanged(newJobbh: String) {
        _finance.value = _finance.value?.copy(jobbh = newJobbh) ?: Finance(jobbh = newJobbh)
    }

    // Function to update dateOfBirth in the Finance object
    fun onDateOfBirthChanged(newDateOfBirth: String) {
        _finance.value = _finance.value?.copy(dateOfBirth = newDateOfBirth) ?: Finance(dateOfBirth = newDateOfBirth)
    }

    // Function to update email in the Finance object
    fun onEmailChanged(newEmail: String) {
        _finance.value = _finance.value?.copy(email = newEmail) ?: Finance(email = newEmail)
    }

    // Function to update address in the Finance object
    fun onAddressChanged(newAddress: String) {
        _finance.value = _finance.value?.copy(address = newAddress) ?: Finance(address = newAddress)
    }

    // Function to update fatherName in the Finance object
    fun onFatherNameChanged(newFatherName: String) {
        _finance.value = _finance.value?.copy(fatherName = newFatherName) ?: Finance(fatherName = newFatherName)
    }

    // Function to update fatherPercentage in the Finance object
    fun onFatherPercentageChanged(newFatherPercentage: String) {
        _finance.value = _finance.value?.copy(fatherPercentage = newFatherPercentage) ?: Finance(fatherPercentage = newFatherPercentage)
    }

    // Function to update birthPlace in the Finance object
    fun onBirthPlaceChanged(newBirthPlace: String) {
        _finance.value = _finance.value?.copy(birthPlace = newBirthPlace) ?: Finance(birthPlace = newBirthPlace)
    }

    // Function to update motherName in the Finance object
    fun onMotherNameChanged(newMotherName: String) {
        _finance.value = _finance.value?.copy(motherName = newMotherName) ?: Finance(motherName = newMotherName)
    }

    // Function to update motherPercentage in the Finance object
    fun onMotherPercentageChanged(newMotherPercentage: String) {
        _finance.value = _finance.value?.copy(motherPercentage = newMotherPercentage) ?: Finance(motherPercentage = newMotherPercentage)
    }

    // Function to update nationality in the Finance object
    fun onNationalityChanged(newNationality: String) {
        _finance.value = _finance.value?.copy(nationality = newNationality) ?: Finance(nationality = newNationality)
    }

    // Function to update gender in the Finance object
    fun onGenderChanged(newGender: String) {
        _finance.value = _finance.value?.copy(gender = newGender) ?: Finance(gender = newGender)
    }

    // Function to update gender2 in the Finance object
    fun onGender2Changed(newGender2: String) {
        _finance.value = _finance.value?.copy(gender2 = newGender2) ?: Finance(gender2 = newGender2)
    }

    // Function to update typeId in the Finance object
    fun onTypeIdChanged(newTypeId: String) {
        _finance.value = _finance.value?.copy(typeId = newTypeId) ?: Finance(typeId = newTypeId)
    }

    // Function to update nationalNumber in the Finance object
    fun onNationalNumberChanged(newNationalNumber: String) {
        _finance.value = _finance.value?.copy(nationalNumber = newNationalNumber) ?: Finance(nationalNumber = newNationalNumber)
    }

    // Function to update nationalPlace in the Finance object
    fun onNationalPlaceChanged(newNationalPlace: String) {
        _finance.value = _finance.value?.copy(nationalPlace = newNationalPlace) ?: Finance(nationalPlace = newNationalPlace)
    }

    // Function to update kayed in the Finance object
    fun onKayedChanged(newKayed: String) {
        _finance.value = _finance.value?.copy(kayed = newKayed) ?: Finance(kayed = newKayed)
    }

    // Function to update addrr in the Finance object
    fun onAddrrChanged(newAddrr: String) {
        _finance.value = _finance.value?.copy(addrr = newAddrr) ?: Finance(addrr = newAddrr)
    }

    // Function to update mobileNumber in the Finance object
    fun onMobileNumberChanged(newMobileNumber: String) {
        _finance.value = _finance.value?.copy(mobileNumber = newMobileNumber) ?: Finance(mobileNumber = newMobileNumber)
    }

    // Function to update job in the Finance object
    fun onJobChanged(newJob: String) {
        _finance.value = _finance.value?.copy(job = newJob) ?: Finance(job = newJob)
    }

    // Function to update studentStatus in the Finance object
    fun onStudentStatusChanged(newStudentStatus: String) {
        _finance.value = _finance.value?.copy(studentStatus = newStudentStatus) ?: Finance(studentStatus = newStudentStatus)
    }

    // Function to update governorate in the Finance object
    fun onGovernorateChanged(newGovernorate: String) {
        _finance.value = _finance.value?.copy(governorate = newGovernorate) ?: Finance(governorate = newGovernorate)
    }

    // Function to update university in the Finance object
    fun onUniversityChanged(newUniversity: String) {
        _finance.value = _finance.value?.copy(university = newUniversity) ?: Finance(university = newUniversity)
    }

    // Function to update college in the Finance object
    fun onCollegeChanged(newCollege: String) {
        _finance.value = _finance.value?.copy(college = newCollege) ?: Finance(college = newCollege)
    }

    // Function to update fatcaCompliance in the Finance object
    fun onFatcaComplianceChanged(newFatcaCompliance: String) {
        _finance.value = _finance.value?.copy(fatcaCompliance = newFatcaCompliance) ?: Finance(fatcaCompliance = newFatcaCompliance)
    }

    // Function to update personalImage in the Finance object
    fun onPersonalImageChanged(newPersonalImage: String) {
        _finance.value = _finance.value?.copy(personalImage = newPersonalImage) ?: Finance(personalImage = newPersonalImage)
    }

    // Function to update idBackImage in the Finance object
    fun onIdBackImageChanged(newIdBackImage: String) {
        _finance.value = _finance.value?.copy(idBackImage = newIdBackImage) ?: Finance(idBackImage = newIdBackImage)
    }

    // Function to update idFrontImage in the Finance object
    fun onIdFrontImageChanged(newIdFrontImage: String) {
        _finance.value = _finance.value?.copy(idFrontImage = newIdFrontImage) ?: Finance(idFrontImage = newIdFrontImage)
    }

    // Function to update universityCardImage in the Finance object
    fun onUniversityCardImageChanged(newUniversityCardImage: String) {
        _finance.value = _finance.value?.copy(universityCardImage = newUniversityCardImage) ?: Finance(universityCardImage = newUniversityCardImage)
    }

    // Function to update agreementCheckbox in the Finance object
    fun onAgreementCheckboxChanged(newAgreementCheckbox: Boolean) {
        _finance.value = _finance.value?.copy(agreementCheckbox = newAgreementCheckbox) ?: Finance(agreementCheckbox = newAgreementCheckbox)
    }

    // Function to update firstNameEn in the Finance object
    fun onFirstNameEnChanged(newFirstNameEn: String) {
        _finance.value = _finance.value?.copy(firstNameEn = newFirstNameEn) ?: Finance(firstNameEn = newFirstNameEn)
    }

    // Function to update fatherNameEn in the Finance object
    fun onFatherNameEnChanged(newFatherNameEn: String) {
        _finance.value = _finance.value?.copy(fatherNameEn = newFatherNameEn) ?: Finance(fatherNameEn = newFatherNameEn)
    }

    // Function to update lastNameEn in the Finance object
    fun onLastNameEnChanged(newLastNameEn: String) {
        _finance.value = _finance.value?.copy(lastNameEn = newLastNameEn) ?: Finance(lastNameEn = newLastNameEn)
    }

    // Function to update country in the Finance object
    fun onCountryChanged(newCountry: String) {
        _finance.value = _finance.value?.copy(country = newCountry) ?: Finance(country = newCountry)
    }

    // Function to update agreementCheckbox1 in the Finance object
    fun onAgreementCheckbox1Changed(newAgreementCheckbox1: Boolean) {
        _finance.value = _finance.value?.copy(agreementCheckbox1 = newAgreementCheckbox1) ?: Finance(agreementCheckbox1 = newAgreementCheckbox1)
    }

    // Function to update branchSpinnerId in the Finance object
    fun onBranchSpinnerIdChanged(newBranchSpinnerId: String) {
        _finance.value = _finance.value?.copy(branchSpinnerId = newBranchSpinnerId) ?: Finance(branchSpinnerId = newBranchSpinnerId)
    }

    // Function to update agreementCheckbox2 in the Finance object
    fun onAgreementCheckbox2Changed(newAgreementCheckbox2: Boolean) {
        _finance.value = _finance.value?.copy(agreementCheckbox2 = newAgreementCheckbox2) ?: Finance(agreementCheckbox2 = newAgreementCheckbox2)
    }

    //     Function to update captchaInput in the Finance object
    fun onCaptchaInputChanged(newCaptchaInput: String) {
        _finance.value = _finance.value?.copy(captchaInput = newCaptchaInput) ?: Finance(captchaInput = newCaptchaInput)
    }
}
