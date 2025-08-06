// 파일 경로: domain/repository/UserRepository.kt
package com.example.type.clean.architecture.domain.repository

import com.example.type.clean.architecture.domain.model.User

// --- Domain Layer: Repository 인터페이스 ---
// '무엇을' 할 수 있는지 정의 (HOW는 모름)
interface UserRepository {
    suspend fun getUser(): User
    suspend fun updateUser(user: User): Boolean
}