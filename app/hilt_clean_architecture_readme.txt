src/main/kotlin/com/example/type/hilt/clean/architecture/
│
├── 📄 MyApplication.kt          # Hilt 설정을 초기화하는 앱의 진입점
│
├── 📁 data                    # 데이터 계층: 데이터의 출처와 실제 구현을 담당
│   ├── 📁 datasource
│   │   └── 📄 ApiService.kt      # 네트워크 API, 로컬 DB 등 실제 데이터 소스와의 통신
│   └── 📁 repository
│       └── 📄 UserRepositoryImpl.kt # Domain 계층의 Repository 인터페이스에 대한 실제 구현체
│
├── 📁 di                      # 의존성 주입 계층
│   └── 📄 AppModule.kt          # Hilt에게 의존성을 어떻게 생성하고 주입할지 알려주는 설정
│
├── 📁 domain                  # 도메인 계층: 앱의 핵심 비즈니스 로직 (가장 중요)
│   ├── 📁 model
│   │   └── 📄 User.kt            # 핵심 비즈니스 모델 (Entity). 외부 계층에 의존하지 않음
│   ├── 📁 repository
│   │   └── 📄 UserRepository.kt  # 데이터가 '무엇을' 할 수 있는지 정의하는 규칙 (인터페이스)
│   └── 📁 usecase
│       ├── 📄 ChangeSubscriptionUseCase.kt
│       └── 📄 FormatDisplayUserNameUseCase.kt
│
└── 📁 presentation            # 프레젠테이션 계층: UI와 UI 상태 관리를 담당
    ├── 📁 view
    │   └── 📄 MainActivity.kt    # 사용자에게 보여지는 화면 (Activity, Fragment, Composable)
    └── 📁 viewmodel
        └── 📄 UserViewModel.kt   # UI 상태를 관리하고 Use Case를 호출 (Factory는 Hilt가 대체)


각 계층의 역할 요약
📁 domain (도메인 계층): 앱의 심장과 같은 부분으로, 가장 핵심적인 비즈니스 규칙과 데이터 모델(User)을 담고 있습니다. 이 계층은 다른 어떤 계층에도 의존하지 않는 순수한 Kotlin 코드로 작성됩니다.

📁 data (데이터 계층): 도메인 계층에서 정의한 규칙(UserRepository 인터페이스)을 실제로 구현하는 부분입니다. 네트워크 API(ApiService)나 로컬 데이터베이스에서 데이터를 어떻게 가져올지를 결정합니다.

📁 presentation (프레젠테이션 계층): 사용자에게 보여지는 화면(MainActivity)과 해당 화면의 상태를 관리하는 로직(UserViewModel)을 담당합니다. 이 계층은 도메인 계층의 Use Case를 호출하여 비즈니스 로직을 실행합니다.

📁 di (의존성 주입 계층): Hilt를 사용하여 각 계층의 구성 요소들을 만들고 서로 연결(주입)해주는 역할을 합니다. ViewModelFactory와 ServiceLocator가 이 계층으로 자동화되었습니다.


전체 예제 코드

// =======================================================================================
// build.gradle(.kts) 파일 설정 (필수)
// =======================================================================================
/*
// 1. 프로젝트 수준 build.gradle.kts
plugins {
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
}

// 2. 앱 수준 build.gradle.kts
plugins {
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}

dependencies {
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-compiler:2.51.1")
}
*/

// =======================================================================================
// AndroidManifest.xml 설정 (필수)
// =======================================================================================
/*
<application
    android:name=".MyApplication"
    ... >
    <activity ... >
    </activity>
</application>
*/

// =======================================================================================
// 파일 경로: MyApplication.kt (최상위 패키지)
// 계층: 프레젠테이션 계층 (Application Level)
// 역할: Hilt 의존성 주입 컨테이너를 초기화하는 앱의 진입점입니다.
// =======================================================================================
package com.example.type.hilt.clean.architecture

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// Hilt 설정의 시작점
@HiltAndroidApp
class MyApplication : Application()

// =======================================================================================
// 파일 경로: di/AppModule.kt
// 계층: 의존성 주입 (DI) 계층
// 역할: Hilt에게 인터페이스와 클래스를 어떻게 생성하고 주입할지 알려주는 설정 파일입니다.
//      앱의 전체적인 의존성 그래프를 구성합니다.
// =======================================================================================
package com.example.type.hilt.clean.architecture.di

import com.example.type.hilt.clean.architecture.data.datasource.ApiService
import com.example.type.hilt.clean.architecture.data.datasource.FakeApiService
import com.example.type.hilt.clean.architecture.data.repository.UserRepositoryImpl
import com.example.type.hilt.clean.architecture.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// --- 의존성 제공 방법을 Hilt에게 알려주는 모듈 ---
@Module
@InstallIn(SingletonComponent::class) // 앱 전역에서 싱글톤으로 관리
abstract class AppModule {

    // 인터페이스(UserRepository)에 대한 구현체(UserRepositoryImpl)를 바인딩
    @Binds
    @Singleton
    abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    companion object {
        // 인터페이스(ApiService)에 대한 구현체(FakeApiService)를 제공
        @Provides
        @Singleton
        fun provideApiService(): ApiService {
            println("[AppModule] Providing ApiService instance")
            return FakeApiService()
        }
    }
}

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


// =======================================================================================

// 파일 경로: domain/repository/UserRepository.kt
// 계층: 도메인 계층 (Domain Layer)
// 역할: 데이터 계층이 '무엇을' 해야 하는지에 대한 규칙(계약)을 인터페이스로 정의합니다.
//      실제 구현 방법(HOW)에 대해서는 전혀 알지 못합니다.
// =======================================================================================
package com.example.type.hilt.clean.architecture.domain.repository

import com.example.type.hilt.clean.architecture.domain.model.User

// --- Domain Layer: Repository 인터페이스 ---
interface UserRepository {
    suspend fun getUser(): User
    suspend fun updateUser(user: User): Boolean
}

// =======================================================================================

// 파일 경로: domain/usecase/FormatDisplayUserNameUseCase.kt
// 계층: 도메인 계층 (Domain Layer)
// 역할: 단일 책임을 가진 앱의 핵심 비즈니스 로직을 캡슐화합니다.
//      Repository 인터페이스에 의존하여 데이터를 처리합니다.
// =======================================================================================
package com.example.type.hilt.clean.architecture.domain.usecase

import com.example.type.hilt.clean.architecture.domain.model.SubscriptionStatus
import com.example.type.hilt.clean.architecture.domain.repository.UserRepository
import javax.inject.Inject

// --- Domain Layer: Use Case ---
class FormatDisplayUserNameUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    private val TAG = "FormatDisplayUserNameUseCase"

    suspend fun execute(): String {
        println("[$TAG] execute: Use case 실행")
        val user = userRepository.getUser()
        println("[$TAG] execute: Repository로부터 받은 User: $user")

        if (user.name.equals("Admin", ignoreCase = true)) {
            val formattedName = "👑 ${user.name}"
            println("[$TAG] execute: Admin 유저 확인, 포맷된 이름: $formattedName")
            return formattedName
        }

        val formattedName = when (user.status) {
            SubscriptionStatus.PREMIUM -> "⭐️ ${user.name}"
            SubscriptionStatus.FREE -> user.name
        }
        println("[$TAG] execute: 구독 상태에 따라 포맷된 이름: $formattedName")
        return formattedName
    }
}

// =======================================================================================

// 파일 경로: domain/usecase/ChangeSubscriptionUseCase.kt
// 계층: 도메인 계층 (Domain Layer)
// 역할: 데이터를 변경하는 비즈니스 로직을 캡슐화합니다.
// =======================================================================================
package com.example.type.hilt.clean.architecture.domain.usecase

import com.example.type.hilt.clean.architecture.domain.model.SubscriptionStatus
import com.example.type.hilt.clean.architecture.domain.repository.UserRepository
import javax.inject.Inject

// --- Domain Layer: Use Case ---
class ChangeSubscriptionUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    private val TAG = "ChangeSubscriptionUseCase"

    suspend fun execute(newStatus: SubscriptionStatus): Boolean {
        println("[$TAG] execute: Use case 실행, newStatus: $newStatus")
        val currentUser = userRepository.getUser()
        println("[$TAG] execute: 현재 유저 정보: $currentUser")

        if (currentUser.name.equals("Admin", ignoreCase = true)) {
            println("[$TAG] execute: Admin 유저는 상태 변경 불가. false 반환")
            return false
        }

        val updatedUser = currentUser.copy(status = newStatus)
        println("[$TAG] execute: 업데이트 할 유저 정보: $updatedUser")
        return userRepository.updateUser(updatedUser)
    }
}

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

// =======================================================================================

// 파일 경로: data/repository/UserRepositoryImpl.kt
// 계층: 데이터 계층 (Data Layer)
// 역할: Domain 계층의 Repository 인터페이스에 대한 실제 구현체입니다.
//      데이터 소스(ApiService)를 사용하여 데이터를 가져오거나 저장하는 방법을 정의합니다.
// =======================================================================================
package com.example.type.hilt.clean.architecture.data.repository

import com.example.type.hilt.clean.architecture.data.datasource.ApiService
import com.example.type.hilt.clean.architecture.domain.model.User
import com.example.type.hilt.clean.architecture.domain.repository.UserRepository
import javax.inject.Inject

// --- Data Layer: Repository 구현체 ---
class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : UserRepository {
    private val TAG = "UserRepositoryImpl"

    override suspend fun getUser(): User {
        println("[$TAG] getUser: ApiService에 유저 정보 요청")
        return apiService.fetchUser()
    }

    override suspend fun updateUser(user: User): Boolean {
        println("[$TAG] updateUser: ApiService에 유저 정보 업데이트 요청")
        return apiService.saveUser(user)
    }
}

// =======================================================================================

// 파일 경로: presentation/viewmodel/UserViewModel.kt
// 계층: 프레젠테이션 계층 (Presentation Layer)
// 역할: UI(View)에 필요한 상태를 관리하고, Use Case를 호출하여 비즈니스 로직을 실행합니다.
//      UI의 생명주기와 안드로이드 프레임워크에 대한 의존성을 가집니다.
// =======================================================================================
package com.example.type.hilt.clean.architecture.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.type.hilt.clean.architecture.domain.model.SubscriptionStatus
import com.example.type.hilt.clean.architecture.domain.usecase.ChangeSubscriptionUseCase
import com.example.type.hilt.clean.architecture.domain.usecase.FormatDisplayUserNameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// --- Presentation Layer: ViewModel ---
@HiltViewModel
class UserViewModel @Inject constructor(
    private val formatDisplayUserNameUseCase: FormatDisplayUserNameUseCase,
    private val changeSubscriptionUseCase: ChangeSubscriptionUseCase
) : ViewModel() {
    private val TAG = "UserViewModel"

    private val _userName = MutableStateFlow("Loading...")
    val userName: StateFlow<String> = _userName

    init {
        Log.d(TAG, "ViewModel 초기화")
        onFetchUser()
    }

    private fun onFetchUser() {
        Log.d(TAG, "onFetchUser: 유저 정보 가져오기 이벤트 수신")
        viewModelScope.launch {
            _userName.value = "Fetching user..."
            val formattedName = formatDisplayUserNameUseCase.execute()
            Log.d(TAG, "onFetchUser: UseCase로부터 받은 포맷된 이름: $formattedName")
            _userName.value = formattedName
        }
    }

    fun onChangeSubscription() {
        Log.d(TAG, "onChangeSubscription: 구독 상태 변경 이벤트 수신")
        viewModelScope.launch {
            val currentFormattedName = formatDisplayUserNameUseCase.execute()
            val isPremium = currentFormattedName.contains("⭐️")
            val newStatus = if (isPremium) SubscriptionStatus.FREE else SubscriptionStatus.PREMIUM
            Log.d(TAG, "onChangeSubscription: 새로운 구독 상태 결정: $newStatus")

            val success = changeSubscriptionUseCase.execute(newStatus)
            Log.d(TAG, "onChangeSubscription: 상태 변경 결과: $success")

            if (success) {
                onFetchUser() // 성공 시 UI 갱신
            }
        }
    }
}

// =======================================================================================

// 파일 경로: presentation/view/MainActivity.kt
// 계층: 프레젠테이션 계층 (Presentation Layer)
// 역할: 사용자에게 보여지는 UI를 담당합니다. ViewModel의 상태를 구독하여 화면을 그리고,
//      사용자 이벤트를 ViewModel에 전달합니다.
// =======================================================================================
package com.example.type.hilt.clean.architecture.presentation.view

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.type.hilt.clean.architecture.presentation.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

// --- Presentation Layer: Activity (UI) ---
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    // Hilt가 ViewModel을 알아서 생성하고 주입
    private val viewModel: UserViewModel by viewModels()

    private lateinit var nameTextView: TextView
    private lateinit var changeStatusButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: Activity 생성")

        // 간단한 UI 설정
        nameTextView = TextView(this).apply {
            textSize = 24f
            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        }
        changeStatusButton = Button(this).apply {
            text = "Toggle Subscription Status"
            setOnClickListener {
                Log.d(TAG, "changeStatusButton: 클릭 이벤트 발생")
                viewModel.onChangeSubscription()
                Toast.makeText(this@MainActivity, "Updating status...", Toast.LENGTH_SHORT).show()
            }
        }
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(nameTextView)
            addView(changeStatusButton)
        }
        setContentView(layout)

        // ViewModel의 상태를 구독하여 UI 업데이트
        lifecycleScope.launch {
            Log.d(TAG, "collect: ViewModel의 userName 구독 시작")
            viewModel.userName.collect { name ->
                Log.d(TAG, "collect: 새로운 userName 수신: $name")
                nameTextView.text = name
            }
        }
    }
}
