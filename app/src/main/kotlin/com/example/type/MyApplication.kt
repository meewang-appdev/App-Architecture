// =======================================================================================
// 파일 경로: MyApplication.kt (최상위 패키지)
// 계층: 프레젠테이션 계층 (Application Level)
// 역할: Hilt 의존성 주입 컨테이너를 초기화하는 앱의 진입점입니다.
// =======================================================================================
package com.example.type

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// Hilt 설정의 시작점
@HiltAndroidApp
class MyApplication : Application()