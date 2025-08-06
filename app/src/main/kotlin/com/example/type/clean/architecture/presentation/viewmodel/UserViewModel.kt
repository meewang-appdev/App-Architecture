// 파일 경로: presentation/viewmodel/UserViewModel.kt
package com.example.type.clean.architecture.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.type.clean.architecture.domain.model.SubscriptionStatus
import com.example.type.clean.architecture.domain.usecase.ChangeSubscriptionUseCase
import com.example.type.clean.architecture.domain.usecase.FormatDisplayUserNameUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// --- Presentation Layer: ViewModel ---
class UserViewModel(
    private val formatDisplayUserNameUseCase: FormatDisplayUserNameUseCase,
    private val changeSubscriptionUseCase: ChangeSubscriptionUseCase
) : ViewModel() {
    private val TAG = "UserViewModel"

    private val _userName = MutableStateFlow("Loading...")
    val userName: StateFlow<String> = _userName

    init {
        Log.d(TAG, "ViewModel 초기화")
    }

    fun onFetchUser() {
        Log.d(TAG, "onFetchUser: 유저 정보 가져오기 이벤트 수신")
        viewModelScope.launch {
            _userName.value = "Fetching user..."
            val formattedName = formatDisplayUserNameUseCase.execute()
            Log.d(TAG, "onFetchUser: UseCase로부터 받은 포맷된 이름: $formattedName")
            _userName.value = formattedName
        }
    }

    fun onChangeSubscription() {
        Log.d(TAG, "onChangeSubscription: 구독 상태 변경 이벤트 수신")
        viewModelScope.launch {
            val currentFormattedName = formatDisplayUserNameUseCase.execute()
            val isPremium = currentFormattedName.contains("⭐️")
            val newStatus = if (isPremium) SubscriptionStatus.FREE else SubscriptionStatus.PREMIUM
            Log.d(TAG, "onChangeSubscription: 새로운 구독 상태 결정: $newStatus")

            val success = changeSubscriptionUseCase.execute(newStatus)
            Log.d(TAG, "onChangeSubscription: 상태 변경 결과: $success")

            if (success) {
                onFetchUser() // 성공 시 UI 갱신
            }
        }
    }
}