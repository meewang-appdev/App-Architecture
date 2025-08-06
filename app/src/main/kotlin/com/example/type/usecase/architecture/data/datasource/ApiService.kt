// =======================================================================================
// 파일 경로: data/datasource/ApiService.kt
// 계층: 데이터 계층 (Data Layer)
// 역할: 네트워크 API, 로컬 DB 등 실제 데이터 소스와의 통신을 담당.
// =======================================================================================
package com.example.type.usercase.architecture.data.datasource

import com.example.type.usercase.architecture.domain.model.User
import com.example.type.usercase.architecture.domain.model.SubscriptionStatus
import kotlinx.coroutines.delay

interface ApiService {
    suspend fun fetchUser(): User
    suspend fun saveUser(user: User): Boolean
}

class FakeApiService : ApiService {
    private val TAG = "FakeApiService"
    private var currentUser = User("John Doe", SubscriptionStatus.PREMIUM)
    override suspend fun fetchUser(): User {
        println("[$TAG] fetchUser: API 요청 수신...")
        delay(500)
        println("[$TAG] fetchUser: 데이터 반환: $currentUser")
        return currentUser
    }
    override suspend fun saveUser(user: User): Boolean {
        println("[$TAG] saveUser: API 요청 수신... 저장할 데이터: $user")
        delay(500)
        currentUser = user
        println("[$TAG] saveUser: 저장 완료. 성공(true) 반환")
        return true
    }
}
