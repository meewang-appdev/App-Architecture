// =======================================================================================
// 파일 경로: domain/repository/UserRepository.kt
// 계층: 모델 계층 (Model Layer) - Repository 인터페이스
// 역할: ViewModel이 데이터에 접근하기 위해 호출할 함수들의 명세를 정의합니다.
//      비즈니스 로직을 포함하므로, 함수 이름이 더 구체적입니다. (예: getUser -> getFormattedUserName)
// =======================================================================================
package com.example.type.mvvm.architecture.domain.repository

import com.example.type.mvvm.architecture.domain.model.SubscriptionStatus

interface UserRepository {
    suspend fun getFormattedUserName(): String
    suspend fun changeSubscription(newStatus: SubscriptionStatus): Boolean
}