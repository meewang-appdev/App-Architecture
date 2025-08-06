// =======================================================================================
// 파일 경로: domain/model/User.kt
// 계층: 모델 계층 (Model Layer) - 데이터 모델
// 역할: 앱에서 사용하는 데이터의 구조를 정의합니다. 이 모델은 여러 계층에서 사용됩니다.
// =======================================================================================
package com.example.type.mvvm.architecture.domain.model

enum class SubscriptionStatus { FREE, PREMIUM }
data class User(val name: String, val status: SubscriptionStatus)