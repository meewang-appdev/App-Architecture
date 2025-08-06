// 파일 경로: presentation/viewmodel/UserViewModelFactory.kt
package com.example.type.clean.architecture.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.type.clean.architecture.domain.usecase.ChangeSubscriptionUseCase
import com.example.type.clean.architecture.domain.usecase.FormatDisplayUserNameUseCase

// --- Presentation Layer: ViewModel Factory ---
class UserViewModelFactory(
    private val formatUseCase: FormatDisplayUserNameUseCase,
    private val changeUseCase: ChangeSubscriptionUseCase
) : ViewModelProvider.Factory {
    private val TAG = "UserViewModelFactory"

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        Log.d(TAG, "create: ViewModel 인스턴스 생성 요청")
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(formatUseCase, changeUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}