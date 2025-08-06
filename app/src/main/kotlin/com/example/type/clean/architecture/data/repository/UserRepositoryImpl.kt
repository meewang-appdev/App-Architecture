// 파일 경로: data/repository/UserRepositoryImpl.kt
package com.example.type.clean.architecture.data.repository

import com.example.type.clean.architecture.data.datasource.ApiService
import com.example.type.clean.architecture.domain.model.User
import com.example.type.clean.architecture.domain.repository.UserRepository

// --- Data Layer: Repository 구현체 ---
class UserRepositoryImpl(private val apiService: ApiService) : UserRepository {
    private val TAG = "UserRepositoryImpl"

    override suspend fun getUser(): User {
        println("[$TAG] getUser: ApiService에 유저 정보 요청")
        return apiService.fetchUser()
    }

    override suspend fun updateUser(user: User): Boolean {
        println("[$TAG] updateUser: ApiService에 유저 정보 업데이트 요청")
        return apiService.saveUser(user)
    }
}