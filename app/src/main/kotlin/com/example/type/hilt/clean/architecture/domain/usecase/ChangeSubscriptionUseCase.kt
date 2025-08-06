// =======================================================================================
// 파일 경로: domain/usecase/ChangeSubscriptionUseCase.kt
// 계층: 도메인 계층 (Domain Layer)
// 역할: 데이터를 변경하는 비즈니스 로직을 캡슐화합니다.
// =======================================================================================
package com.example.type.hilt.clean.architecture.domain.usecase

import com.example.type.hilt.clean.architecture.domain.model.SubscriptionStatus
import com.example.type.hilt.clean.architecture.domain.repository.UserRepository
import javax.inject.Inject

// --- Domain Layer: Use Case ---
class ChangeSubscriptionUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    private val TAG = "ChangeSubscriptionUseCase"

    suspend fun execute(newStatus: SubscriptionStatus): Boolean {
        println("[$TAG] execute: Use case 실행, newStatus: $newStatus")
        val currentUser = userRepository.getUser()
        println("[$TAG] execute: 현재 유저 정보: $currentUser")

        if (currentUser.name.equals("Admin", ignoreCase = true)) {
            println("[$TAG] execute: Admin 유저는 상태 변경 불가. false 반환")
            return false
        }

        val updatedUser = currentUser.copy(status = newStatus)
        println("[$TAG] execute: 업데이트 할 유저 정보: $updatedUser")
        return userRepository.updateUser(updatedUser)
    }
}