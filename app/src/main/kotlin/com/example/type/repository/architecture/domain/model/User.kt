// =======================================================================================
// 파일 경로: domain/model/User.kt
// 계층: 도메인 계층 (Domain Layer)
// 역할: 앱의 가장 핵심적인 데이터 구조(Entity)를 정의.
// =======================================================================================
package com.example.type.repository.architecture.domain.model

enum class SubscriptionStatus { FREE, PREMIUM }
data class User(val name: String, val status: SubscriptionStatus)