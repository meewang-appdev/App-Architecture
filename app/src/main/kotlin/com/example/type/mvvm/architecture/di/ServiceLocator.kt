// =======================================================================================
// 파일 경로: di/ServiceLocator.kt
// 계층: 의존성 주입 (DI) 계층
// 역할: 앱의 각 구성 요소(클래스)를 생성하고, 필요한 곳에 연결(주입)해주는 공장 역할.
//      UseCase 계층이 사라지고, ViewModel이 Repository를 직접 사용하도록 구조가 단순화되었습니다.
// =======================================================================================
package com.example.type.mvvm.architecture.di

import com.example.type.mvvm.architecture.data.datasource.ApiService
import com.example.type.mvvm.architecture.data.datasource.FakeApiService
import com.example.type.mvvm.architecture.data.repository.UserRepositoryImpl
import com.example.type.mvvm.architecture.domain.repository.UserRepository

object ServiceLocator {
    private const val TAG = "ServiceLocator"
    private val apiService: ApiService by lazy {
        println("[$TAG] Creating ApiService instance")
        FakeApiService()
    }
    val userRepository: UserRepository by lazy {
        println("[$TAG] Creating UserRepository instance")
        UserRepositoryImpl(apiService)
    }
}