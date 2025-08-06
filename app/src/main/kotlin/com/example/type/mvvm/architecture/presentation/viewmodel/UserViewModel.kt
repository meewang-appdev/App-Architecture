// =======================================================================================
// 파일 경로: presentation/viewmodel/UserViewModel.kt
// 계층: 뷰모델 계층 (ViewModel Layer)
// 역할: View(Activity)에 필요한 상태(StateFlow)를 관리하고 노출합니다.
//      사용자 이벤트가 발생하면 Repository를 직접 호출하여 데이터 처리를 요청합니다.
// =======================================================================================
package com.example.type.mvvm.architecture.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.type.mvvm.architecture.domain.model.SubscriptionStatus
import com.example.type.mvvm.architecture.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val userRepository: UserRepository // UseCase 대신 Repository를 직접 주입받음
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
            _userName.value = userRepository.getFormattedUserName()
            Log.d(TAG, "onFetchUser: UI 상태 업데이트: ${_userName.value}")
        }
    }

    fun onChangeSubscription() {
        Log.d(TAG, "onChangeSubscription: 이벤트 수신")
        viewModelScope.launch {
            val currentFormattedName = userRepository.getFormattedUserName()
            val isPremium = currentFormattedName.contains("⭐️")
            val newStatus = if (isPremium) SubscriptionStatus.FREE else SubscriptionStatus.PREMIUM
            Log.d(TAG, "onChangeSubscription: 새로운 상태 결정: $newStatus")

            if (userRepository.changeSubscription(newStatus)) {
                Log.d(TAG, "onChangeSubscription: 상태 변경 성공, UI 새로고침")
                onFetchUser()
            } else {
                Log.d(TAG, "onChangeSubscription: 상태 변경 실패")
            }
        }
    }
}