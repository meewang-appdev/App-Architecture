// =======================================================================================
// 파일 경로: domain/model/User.kt
// 계층: 도메인 계층 (Domain Layer)
// 역할: 앱의 가장 핵심적인 데이터 구조(Entity)를 정의합니다.
//      이 계층은 다른 어떤 계층에도 의존하지 않는 순수한 Kotlin 코드여야 합니다.
// =======================================================================================
package com.example.type.hilt.clean.architecture.domain.model

// --- Domain Layer: 핵심 데이터 모델 (Entity) ---
enum class SubscriptionStatus { FREE, PREMIUM }
data class User(val name: String, val status: SubscriptionStatus)