// =======================================================================================
// 파일 경로: data/repository/UserRepositoryImpl.kt
// 계층: 데이터 계층 (Data Layer)
// 역할: Domain 계층의 Repository 인터페이스에 대한 실제 구현체.
// =======================================================================================
package com.example.type.usercase.architecture.data.repository

import com.example.type.usercase.architecture.data.datasource.ApiService
import com.example.type.usercase.architecture.domain.model.User
import com.example.type.usercase.architecture.domain.repository.UserRepository

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