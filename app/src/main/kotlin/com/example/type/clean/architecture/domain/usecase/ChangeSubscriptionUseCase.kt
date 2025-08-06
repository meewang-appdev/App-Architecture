// 파일 경로: domain/usecase/ChangeSubscriptionUseCase.kt
package com.example.type.clean.architecture.domain.usecase

import com.example.type.clean.architecture.domain.model.SubscriptionStatus
import com.example.type.clean.architecture.domain.repository.UserRepository

// --- Domain Layer: Use Case (데이터를 변경하고 결과를 반환하는 로직) ---
class ChangeSubscriptionUseCase(private val userRepository: UserRepository) {
    private val TAG = "ChangeSubscriptionUseCase"

    suspend fun execute(newStatus: SubscriptionStatus): Boolean {
        println("[$TAG] execute: Use case 실행, newStatus: $newStatus")
        val currentUser = userRepository.getUser()
        println("[$TAG] execute: 현재 유저 정보: $currentUser")

        // 비즈니스 규칙: 관리자는 상태를 변경할 수 없다.
        if (currentUser.name.equals("Admin", ignoreCase = true)) {
            println("[$TAG] execute: Admin 유저는 상태 변경 불가. false 반환")
            return false
        }

        val updatedUser = currentUser.copy(status = newStatus)
        println("[$TAG] execute: 업데이트 할 유저 정보: $updatedUser")
        return userRepository.updateUser(updatedUser)
    }
}