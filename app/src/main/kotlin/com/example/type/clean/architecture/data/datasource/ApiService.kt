// 파일 경로: data/datasource/ApiService.kt
package com.example.type.clean.architecture.data.datasource

import com.example.type.clean.architecture.domain.model.User
import com.example.type.clean.architecture.domain.model.SubscriptionStatus
import kotlinx.coroutines.delay

// --- Data Layer: 데이터 소스 ---
interface ApiService {
    suspend fun fetchUser(): User
    suspend fun saveUser(user: User): Boolean
}

class FakeApiService : ApiService {
    private val TAG = "FakeApiService"
    private var currentUser = User("John Doe", SubscriptionStatus.PREMIUM)

    override suspend fun fetchUser(): User {
        println("[$TAG] fetchUser: API 요청 수신...")
        delay(500) // 네트워크 딜레이 시뮬레이션
        println("[$TAG] fetchUser: 현재 유저 정보 반환: $currentUser")
        return currentUser
    }

    override suspend fun saveUser(user: User): Boolean {
        println("[$TAG] saveUser: API 요청 수신... 저장할 데이터: $user")
        delay(500) // 네트워크 딜레이 시뮬레이션
        currentUser = user
        println("[$TAG] saveUser: 저장 완료. 성공(true) 반환")
        return true
    }
}