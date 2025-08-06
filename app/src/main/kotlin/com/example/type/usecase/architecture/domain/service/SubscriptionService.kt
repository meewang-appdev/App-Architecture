// =======================================================================================
// 파일 경로: domain/service/SubscriptionService.kt
// 계층: 도메인 계층 (Domain Layer) - Service/UseCase
// 역할: 구독 상태를 변경하는 '비즈니스 로직'을 담당.
// =======================================================================================
package com.example.type.usercase.architecture.domain.service

import com.example.type.usercase.architecture.domain.model.SubscriptionStatus
import com.example.type.usercase.architecture.domain.repository.UserRepository

class SubscriptionService(private val userRepository: UserRepository) {
    private val TAG = "SubscriptionService"
    suspend fun changeSubscription(newStatus: SubscriptionStatus): Boolean {
        println("[$TAG] changeSubscription: 실행, newStatus: $newStatus")
        val currentUser = userRepository.getUser()
        if (currentUser.name.equals("Admin", ignoreCase = true)) {
            println("[$TAG] changeSubscription: Admin 유저는 변경 불가")
            return false
        }
        val updatedUser = currentUser.copy(status = newStatus)
        return userRepository.updateUser(updatedUser)
    }
}