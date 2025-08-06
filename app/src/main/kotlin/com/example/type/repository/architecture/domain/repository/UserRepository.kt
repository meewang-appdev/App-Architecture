// =======================================================================================
// 파일 경로: domain/repository/UserRepository.kt
// 계층: 도메인 계층 (Domain Layer)
// 역할: Repository가 이제 비즈니스 로직을 직접 처리하므로, 인터페이스에 해당 함수들을 정의.
// =======================================================================================
package com.example.type.repository.architecture.domain.repository

import com.example.type.repository.architecture.domain.model.SubscriptionStatus

interface UserRepository {
    suspend fun getFormattedUserName(): String
    suspend fun changeSubscription(newStatus: SubscriptionStatus): Boolean
}