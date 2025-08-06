// =======================================================================================
// 파일 경로: presentation/viewmodel/UserViewModelFactory.kt
// 계층: 뷰모델 계층 (ViewModel Layer)
// 역할: ViewModel에 의존성(Repository)을 수동으로 주입하기 위한 Factory 클래스입니다.
// =======================================================================================
package com.example.type.mvvm.architecture.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.type.mvvm.architecture.domain.repository.UserRepository

class UserViewModelFactory(
    private val repository: UserRepository
) : ViewModelProvider.Factory {
    private val TAG = "UserViewModelFactory"
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        Log.d(TAG, "create: ViewModel 인스턴스 생성 요청")
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}