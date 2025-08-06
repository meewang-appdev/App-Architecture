// =======================================================================================
// 파일 경로: data/datasource/ApiService.kt
// 계층: 데이터 계층 (Data Layer)
// 역할: 네트워크 API, 로컬 DB 등 실제 데이터 소스와의 통신을 담당합니다.
//      Retrofit 인터페이스나 Room DAO 등이 여기에 해당됩니다.
// =======================================================================================
package com.example.type.hilt.clean.architecture.data.datasource

import com.example.type.hilt.clean.architecture.domain.model.User
import com.example.type.hilt.clean.architecture.domain.model.SubscriptionStatus
import kotlinx.coroutines.delay
import javax.inject.Inject

// --- Data Layer: 데이터 소스 ---
interface ApiService {
    suspend fun fetchUser(): User
    suspend fun saveUser(user: User): Boolean
}

class FakeApiService @Inject constructor() : ApiService {
    private val TAG = "FakeApiService"
    private var currentUser = User("John Doe", SubscriptionStatus.PREMIUM)

    override suspend fun fetchUser(): User {
        println("[$TAG] fetchUser: API 요청 수신...")
        delay(500)
        println("[$TAG] fetchUser: 현재 유저 정보 반환: $currentUser")
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