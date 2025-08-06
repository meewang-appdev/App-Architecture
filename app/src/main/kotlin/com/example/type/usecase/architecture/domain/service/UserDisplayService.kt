// =======================================================================================
// 파일 경로: domain/service/UserDisplayService.kt
// 계층: 도메인 계층 (Domain Layer) - Service/UseCase
// 역할: 사용자 이름을 UI에 맞게 가공하는 '비즈니스 로직'을 담당.
// =======================================================================================
package com.example.type.usercase.architecture.domain.service

import com.example.type.usercase.architecture.domain.model.SubscriptionStatus
import com.example.type.usercase.architecture.domain.repository.UserRepository

class UserDisplayService(private val userRepository: UserRepository) {
    private val TAG = "UserDisplayService"
    suspend fun getFormattedUserName(): String {
        println("[$TAG] getFormattedUserName: 실행")
        val user = userRepository.getUser()
        if (user.name.equals("Admin", ignoreCase = true)) {
            return "👑 ${user.name}"
        }
        return when (user.status) {
            SubscriptionStatus.PREMIUM -> "⭐️ ${user.name}"
            SubscriptionStatus.FREE -> user.name
        }
    }
}