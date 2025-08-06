// =======================================================================================
// 파일 경로: presentation/viewmodel/UserViewModel.kt
// 계층: 프레젠테이션 계층 (Presentation Layer)
// 역할: UI(View)에 필요한 상태를 관리하고, Service(UseCase)를 호출하여 비즈니스 로직을 실행.
// =======================================================================================
package com.example.type.usercase.architecture.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.type.usercase.architecture.domain.model.SubscriptionStatus
import com.example.type.usercase.architecture.domain.service.SubscriptionService
import com.example.type.usercase.architecture.domain.service.UserDisplayService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val userDisplayService: UserDisplayService,
    private val subscriptionService: SubscriptionService
) : ViewModel() {
    private val TAG = "UserViewModel"
    private val _userName = MutableStateFlow("Loading...")
    val userName: StateFlow<String> = _userName

    init {
        Log.d(TAG, "ViewModel 초기화")
    }

    fun onFetchUser() {
        Log.d(TAG, "onFetchUser: 이벤트 수신")
        viewModelScope.launch {
            _userName.value = userDisplayService.getFormattedUserName()
            Log.d(TAG, "onFetchUser: UI 상태 업데이트: ${_userName.value}")
        }
    }

    fun onChangeSubscription() {
        Log.d(TAG, "onChangeSubscription: 이벤트 수신")
        viewModelScope.launch {
            val isPremium = _userName.value.contains("⭐️")
            val newStatus = if (isPremium) SubscriptionStatus.FREE else SubscriptionStatus.PREMIUM
            Log.d(TAG, "onChangeSubscription: 새로운 상태 결정: $newStatus")
            if (subscriptionService.changeSubscription(newStatus)) {
                Log.d(TAG, "onChangeSubscription: 상태 변경 성공, UI 새로고침")
                onFetchUser()
            } else {
                Log.d(TAG, "onChangeSubscription: 상태 변경 실패")
            }
        }
    }
}
