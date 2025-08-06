// =======================================================================================
// 파일 경로: data/repository/UserRepositoryImpl.kt
// 계층: 데이터 계층 (Data Layer)
// 역할: Repository 인터페이스를 구현. 데이터 접근과 '비즈니스 로직'을 모두 처리.
// =======================================================================================
package com.example.type.repository.architecture.data.repository

import com.example.type.repository.architecture.data.datasource.ApiService
import com.example.type.repository.architecture.domain.model.SubscriptionStatus
import com.example.type.repository.architecture.domain.repository.UserRepository

class UserRepositoryImpl(private val apiService: ApiService) : UserRepository {
    private val TAG = "UserRepositoryImpl"

    override suspend fun getFormattedUserName(): String {
        println("[$TAG] getFormattedUserName: 실행")
        val user = apiService.fetchUser()
        if (user.name.equals("Admin", ignoreCase = true)) {
            return "👑 ${user.name}"
        }
        return when (user.status) {
            SubscriptionStatus.PREMIUM -> "⭐️ ${user.name}"
            SubscriptionStatus.FREE -> user.name
        }
    }

    override suspend fun changeSubscription(newStatus: SubscriptionStatus): Boolean {
        println("[$TAG] changeSubscription: 실행, newStatus: $newStatus")
        val currentUser = apiService.fetchUser()
        if (currentUser.name.equals("Admin", ignoreCase = true)) {
            println("[$TAG] changeSubscription: Admin 유저는 변경 불가")
            return false
        }
        val updatedUser = currentUser.copy(status = newStatus)
        return apiService.saveUser(updatedUser)
    }
}