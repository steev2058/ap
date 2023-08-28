package com.apps2you.albaraka.viewmodels

import androidx.lifecycle.LiveData
import com.apps2you.albaraka.data.model.NotificationContent
import com.apps2you.albaraka.data.remote.networkUtils.Resource
import com.apps2you.albaraka.data.remote.repository.AppRepository
import com.apps2you.albaraka.ui.base.BaseViewModel
import javax.inject.Inject

class NotificationsViewModel @Inject constructor(private val appRepository: AppRepository) : BaseViewModel() {

    fun getNotifications(): LiveData<Resource<ArrayList<NotificationContent>>> = appRepository.notifications

    fun deleteNotification(id: Int) : LiveData<Resource<String>> = appRepository.deleteNotification(id)
}