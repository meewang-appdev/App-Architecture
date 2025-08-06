// =======================================================================================
// 파일 경로: presentation/viewmodel/UserViewModelFactory.kt
// 계층: 프레젠테이션 계층 (Presentation Layer)
// 역할: ViewModel에 의존성을 수동으로 주입하기 위한 Factory 클래스.
// =======================================================================================
package com.example.type.usercase.architecture.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.type.usercase.architecture.domain.service.SubscriptionService
import com.example.type.usercase.architecture.domain.service.UserDisplayService

class UserViewModelFactory(
    private val displayService: UserDisplayService,
    private val subscriptionService: SubscriptionService
) : ViewModelProvider.Factory {
    private val TAG = "UserViewModelFactory"
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        Log.d(TAG, "create: ViewModel 인스턴스 생성 요청")
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(displayService, subscriptionService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}