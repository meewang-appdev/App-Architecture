// =======================================================================================
// 파일 경로: data/repository/UserRepositoryImpl.kt
// 계층: 데이터 계층 (Data Layer)
// 역할: Domain 계층의 Repository 인터페이스에 대한 실제 구현체입니다.
//      데이터 소스(ApiService)를 사용하여 데이터를 가져오거나 저장하는 방법을 정의합니다.
// =======================================================================================
package com.example.type.hilt.clean.architecture.data.repository

import com.example.type.hilt.clean.architecture.data.datasource.ApiService
import com.example.type.hilt.clean.architecture.domain.model.User
import com.example.type.hilt.clean.architecture.domain.repository.UserRepository
import javax.inject.Inject

// --- Data Layer: Repository 구현체 ---
class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : UserRepository {
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