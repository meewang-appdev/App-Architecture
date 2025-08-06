// =======================================================================================
// 파일 경로: data/repository/UserRepositoryImpl.kt
// 계층: 모델 계층 (Model Layer) - Repository 구현체
// 역할: Repository 인터페이스를 구현합니다. 데이터 소스(ApiService)를 사용하여 데이터를 가져오고,
//      관련된 비즈니스 로직(이름 포맷팅, 변경 가능 여부 확인 등)을 직접 처리합니다.
// =======================================================================================
package com.example.type.mvvm.architecture.data.repository

import com.example.type.mvvm.architecture.data.datasource.ApiService
import com.example.type.mvvm.architecture.domain.model.SubscriptionStatus
import com.example.type.mvvm.architecture.domain.repository.UserRepository

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