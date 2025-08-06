// 파일 경로: domain/usecase/FormatDisplayUserNameUseCase.kt
package com.example.type.clean.architecture.domain.usecase

import com.example.type.clean.architecture.domain.model.SubscriptionStatus
import com.example.type.clean.architecture.domain.repository.UserRepository

// --- Domain Layer: Use Case (데이터를 읽고 가공하는 로직) ---
class FormatDisplayUserNameUseCase(private val userRepository: UserRepository) {
    private val TAG = "FormatDisplayUserNameUseCase"

    suspend fun execute(): String {
        println("[$TAG] execute: Use case 실행")
        val user = userRepository.getUser()
        println("[$TAG] execute: Repository로부터 받은 User: $user")

        if (user.name.equals("Admin", ignoreCase = true)) {
            val formattedName = "👑 ${user.name}"
            println("[$TAG] execute: Admin 유저 확인, 포맷된 이름: $formattedName")
            return formattedName
        }

        val formattedName = when (user.status) {
            SubscriptionStatus.PREMIUM -> "⭐️ ${user.name}"
            SubscriptionStatus.FREE -> user.name
        }
        println("[$TAG] execute: 구독 상태에 따라 포맷된 이름: $formattedName")
        return formattedName
    }
}