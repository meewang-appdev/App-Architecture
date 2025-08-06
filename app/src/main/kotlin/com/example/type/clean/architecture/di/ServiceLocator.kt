// 파일 경로: di/ServiceLocator.kt
package com.example.type.clean.architecture.di

import com.example.type.clean.architecture.data.datasource.ApiService
import com.example.type.clean.architecture.data.datasource.FakeApiService
import com.example.type.clean.architecture.data.repository.UserRepositoryImpl
import com.example.type.clean.architecture.domain.repository.UserRepository
import com.example.type.clean.architecture.domain.usecase.ChangeSubscriptionUseCase
import com.example.type.clean.architecture.domain.usecase.FormatDisplayUserNameUseCase

// --- 의존성 주입을 위한 간단한 서비스 로케이터 ---
// 주: 이 계층과 하위 계층(data, domain)은 안드로이드 프레임워크에 의존하지 않으므로,
// android.util.Log 대신 표준 println을 사용합니다.
object ServiceLocator {
    private const val TAG = "ServiceLocator"

    // Data Layer
    private val apiService: ApiService by lazy {
        println("[$TAG] Creating ApiService instance")
        FakeApiService()
    }
    private val userRepository: UserRepository by lazy {
        println("[$TAG] Creating UserRepository instance")
        UserRepositoryImpl(apiService)
    }

    // Domain Layer
    val formatDisplayUserNameUseCase: FormatDisplayUserNameUseCase by lazy {
        println("[$TAG] Creating FormatDisplayUserNameUseCase instance")
        FormatDisplayUserNameUseCase(userRepository)
    }
    val changeSubscriptionUseCase: ChangeSubscriptionUseCase by lazy {
        println("[$TAG] Creating ChangeSubscriptionUseCase instance")
        ChangeSubscriptionUseCase(userRepository)
    }
}