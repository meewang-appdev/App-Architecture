// 파일 경로: domain/model/User.kt
package com.example.type.clean.architecture.domain.model

// --- Domain Layer: 핵심 데이터 모델 (Entity) ---
enum class SubscriptionStatus { FREE, PREMIUM }
data class User(val name: String, val status: SubscriptionStatus)