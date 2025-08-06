// =======================================================================================
// 파일 경로: presentation/viewmodel/UserViewModel.kt
// 계층: 프레젠테이션 계층 (Presentation Layer)
// 역할: UI(View)에 필요한 상태를 관리하고, Use Case를 호출하여 비즈니스 로직을 실행합니다.
//      UI의 생명주기와 안드로이드 프레임워크에 대한 의존성을 가집니다.
// =======================================================================================
package com.example.type.hilt.clean.architecture.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.type.hilt.clean.architecture.domain.model.SubscriptionStatus
import com.example.type.hilt.clean.architecture.domain.usecase.ChangeSubscriptionUseCase
import com.example.type.hilt.clean.architecture.domain.usecase.FormatDisplayUserNameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// --- Presentation Layer: ViewModel ---
@HiltViewModel
class UserViewModel @Inject constructor(
    private val formatDisplayUserNameUseCase: FormatDisplayUserNameUseCase,
    private val changeSubscriptionUseCase: ChangeSubscriptionUseCase
) : ViewModel() {
    private val TAG = "UserViewModel"

    private val _userName = MutableStateFlow("Loading...")
    val userName: StateFlow<String> = _userName

    init {
        Log.d(TAG, "ViewModel 초기화")
        onFetchUser()
    }

    private fun onFetchUser() {
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