// =======================================================================================
// 파일 경로: domain/repository/UserRepository.kt
// 계층: 도메인 계층 (Domain Layer)
// 역할: 데이터 계층이 '무엇을' 해야 하는지에 대한 규칙(계약)을 인터페이스로 정의합니다.
//      실제 구현 방법(HOW)에 대해서는 전혀 알지 못합니다.
// =======================================================================================
package com.example.type.hilt.clean.architecture.domain.repository

import com.example.type.hilt.clean.architecture.domain.model.User

// --- Domain Layer: Repository 인터페이스 ---
interface UserRepository {
    suspend fun getUser(): User
    suspend fun updateUser(user: User): Boolean
}