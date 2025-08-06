// =======================================================================================
// 파일 경로: domain/repository/UserRepository.kt
// 계층: 도메인 계층 (Domain Layer)
// 역할: 데이터 계층이 '무엇을' 해야 하는지에 대한 규칙(계약)을 인터페이스로 정의.
// =======================================================================================
package com.example.type.usercase.architecture.domain.repository

import com.example.type.usercase.architecture.domain.model.User

interface UserRepository {
    suspend fun getUser(): User
    suspend fun updateUser(user: User): Boolean
}