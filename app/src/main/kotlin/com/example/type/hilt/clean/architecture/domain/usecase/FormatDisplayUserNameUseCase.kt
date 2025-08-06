// =======================================================================================
// 파일 경로: domain/usecase/FormatDisplayUserNameUseCase.kt
// 계층: 도메인 계층 (Domain Layer)
// 역할: 단일 책임을 가진 앱의 핵심 비즈니스 로직을 캡슐화합니다.
//      Repository 인터페이스에 의존하여 데이터를 처리합니다.
// =======================================================================================
package com.example.type.hilt.clean.architecture.domain.usecase

import com.example.type.hilt.clean.architecture.domain.model.SubscriptionStatus
import com.example.type.hilt.clean.architecture.domain.repository.UserRepository
import javax.inject.Inject

// --- Domain Layer: Use Case ---
class FormatDisplayUserNameUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
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