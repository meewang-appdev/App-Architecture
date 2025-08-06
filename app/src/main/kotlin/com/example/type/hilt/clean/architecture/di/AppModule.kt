// =======================================================================================
// 파일 경로: di/AppModule.kt
// 계층: 의존성 주입 (DI) 계층
// 역할: Hilt에게 인터페이스와 클래스를 어떻게 생성하고 주입할지 알려주는 설정 파일입니다.
//      앱의 전체적인 의존성 그래프를 구성합니다.
// =======================================================================================
package com.example.type.hilt.clean.architecture.di

import com.example.type.hilt.clean.architecture.data.datasource.ApiService
import com.example.type.hilt.clean.architecture.data.datasource.FakeApiService
import com.example.type.hilt.clean.architecture.data.repository.UserRepositoryImpl
import com.example.type.hilt.clean.architecture.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// --- 의존성 제공 방법을 Hilt에게 알려주는 모듈 ---
@Module
@InstallIn(SingletonComponent::class) // 앱 전역에서 싱글톤으로 관리
abstract class AppModule {

    // 인터페이스(UserRepository)에 대한 구현체(UserRepositoryImpl)를 바인딩
    @Binds
    @Singleton
    abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    companion object {
        // 인터페이스(ApiService)에 대한 구현체(FakeApiService)를 제공
        @Provides
        @Singleton
        fun provideApiService(): ApiService {
            println("[AppModule] Providing ApiService instance")
            return FakeApiService()
        }
    }
}