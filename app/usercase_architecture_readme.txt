ViewModel → Service/UseCase → Repository
이 구조는 비즈니스 로직을 담당하는 Service/UseCase 계층이 명확하게 분리되어 있어, 각 계층의 책임이 명확하고 확장성이 뛰어납니다.

src/main/kotlin/com/example/type/usercase/architecture/
│
├── 📁 data
│   ├── 📁 datasource
│   │   └── 📄 ApiService.kt
│   └── 📁 repository
│       └── 📄 UserRepositoryImpl.kt
│
├── 📁 di
│   └── 📄 ServiceLocator.kt
│
├── 📁 domain
│   ├── 📁 model
│   │   └── 📄 User.kt
│   ├── 📁 repository
│   │   └── 📄 UserRepository.kt
│   └── 📁 service              # (또는 usecase) 비즈니스 로직
│       ├── 📄 SubscriptionService.kt
│       └── 📄 UserDisplayService.kt
│
└── 📁 presentation
    ├── 📁 view
    │   └── 📄 MainActivity.kt
    └── 📁 viewmodel
        ├── 📄 UserViewModel.kt
        └── 📄 UserViewModelFactory.kt


// =======================================================================================
// 파일 경로: di/ServiceLocator.kt
// 계층: 의존성 주입 (DI) 계층
// 역할: 앱의 각 구성 요소(클래스)를 생성하고, 필요한 곳에 연결(주입)해주는 공장 역할.
// =======================================================================================
package com.example.type.usercase.architecture.di

import com.example.type.usercase.architecture.data.datasource.ApiService
import com.example.type.usercase.architecture.data.datasource.FakeApiService
import com.example.type.usercase.architecture.data.repository.UserRepositoryImpl
import com.example.type.usercase.architecture.domain.repository.UserRepository
import com.example.type.usercase.architecture.domain.service.SubscriptionService
import com.example.type.usercase.architecture.domain.service.UserDisplayService

object ServiceLocator {
    private const val TAG = "ServiceLocator"
    private val apiService: ApiService by lazy {
        println("[$TAG] Creating ApiService instance")
        FakeApiService()
    }
    private val userRepository: UserRepository by lazy {
        println("[$TAG] Creating UserRepository instance")
        UserRepositoryImpl(apiService)
    }
    val userDisplayService: UserDisplayService by lazy {
        println("[$TAG] Creating UserDisplayService instance")
        UserDisplayService(userRepository)
    }
    val subscriptionService: SubscriptionService by lazy {
        println("[$TAG] Creating SubscriptionService instance")
        SubscriptionService(userRepository)
    }
}

// =======================================================================================
// 파일 경로: domain/model/User.kt
// 계층: 도메인 계층 (Domain Layer)
// 역할: 앱의 가장 핵심적인 데이터 구조(Entity)를 정의.
// =======================================================================================
package com.example.type.usercase.architecture.domain.model

enum class SubscriptionStatus { FREE, PREMIUM }
data class User(val name: String, val status: SubscriptionStatus)

// =======================================================================================
// 파일 경로: domain/repository/UserRepository.kt
// 계층: 도메인 계층 (Domain Layer)
// 역할: 데이터 계층이 '무엇을' 해야 하는지에 대한 규칙(계약)을 인터페이스로 정의.
// =======================================================================================
package com.example.type.usercase.architecture.domain.repository

import com.example.type.usercase.architecture.domain.model.User

interface UserRepository {
    suspend fun getUser(): User
    suspend fun updateUser(user: User): Boolean
}

// =======================================================================================
// 파일 경로: domain/service/UserDisplayService.kt
// 계층: 도메인 계층 (Domain Layer) - Service/UseCase
// 역할: 사용자 이름을 UI에 맞게 가공하는 '비즈니스 로직'을 담당.
// =======================================================================================
package com.example.type.usercase.architecture.domain.service

import com.example.type.usercase.architecture.domain.model.SubscriptionStatus
import com.example.type.usercase.architecture.domain.repository.UserRepository

class UserDisplayService(private val userRepository: UserRepository) {
    private val TAG = "UserDisplayService"
    suspend fun getFormattedUserName(): String {
        println("[$TAG] getFormattedUserName: 실행")
        val user = userRepository.getUser()
        if (user.name.equals("Admin", ignoreCase = true)) {
            return "👑 ${user.name}"
        }
        return when (user.status) {
            SubscriptionStatus.PREMIUM -> "⭐️ ${user.name}"
            SubscriptionStatus.FREE -> user.name
        }
    }
}

// =======================================================================================
// 파일 경로: domain/service/SubscriptionService.kt
// 계층: 도메인 계층 (Domain Layer) - Service/UseCase
// 역할: 구독 상태를 변경하는 '비즈니스 로직'을 담당.
// =======================================================================================
package com.example.type.usercase.architecture.domain.service

import com.example.type.usercase.architecture.domain.model.SubscriptionStatus
import com.example.type.usercase.architecture.domain.repository.UserRepository

class SubscriptionService(private val userRepository: UserRepository) {
    private val TAG = "SubscriptionService"
    suspend fun changeSubscription(newStatus: SubscriptionStatus): Boolean {
        println("[$TAG] changeSubscription: 실행, newStatus: $newStatus")
        val currentUser = userRepository.getUser()
        if (currentUser.name.equals("Admin", ignoreCase = true)) {
            println("[$TAG] changeSubscription: Admin 유저는 변경 불가")
            return false
        }
        val updatedUser = currentUser.copy(status = newStatus)
        return userRepository.updateUser(updatedUser)
    }
}

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

// =======================================================================================
// 파일 경로: data/repository/UserRepositoryImpl.kt
// 계층: 데이터 계층 (Data Layer)
// 역할: Domain 계층의 Repository 인터페이스에 대한 실제 구현체.
// =======================================================================================
package com.example.type.usercase.architecture.data.repository

import com.example.type.usercase.architecture.data.datasource.ApiService
import com.example.type.usercase.architecture.domain.model.User
import com.example.type.usercase.architecture.domain.repository.UserRepository

class UserRepositoryImpl(private val apiService: ApiService) : UserRepository {
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
// 역할: UI(View)에 필요한 상태를 관리하고, Service(UseCase)를 호출하여 비즈니스 로직을 실행.
// =======================================================================================
package com.example.type.usercase.architecture.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.type.usercase.architecture.domain.model.SubscriptionStatus
import com.example.type.usercase.architecture.domain.service.SubscriptionService
import com.example.type.usercase.architecture.domain.service.UserDisplayService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val userDisplayService: UserDisplayService,
    private val subscriptionService: SubscriptionService
) : ViewModel() {
    private val TAG = "UserViewModel"
    private val _userName = MutableStateFlow("Loading...")
    val userName: StateFlow<String> = _userName

    init {
        Log.d(TAG, "ViewModel 초기화")
    }

    fun onFetchUser() {
        Log.d(TAG, "onFetchUser: 이벤트 수신")
        viewModelScope.launch {
            _userName.value = userDisplayService.getFormattedUserName()
            Log.d(TAG, "onFetchUser: UI 상태 업데이트: ${_userName.value}")
        }
    }

    fun onChangeSubscription() {
        Log.d(TAG, "onChangeSubscription: 이벤트 수신")
        viewModelScope.launch {
            val isPremium = _userName.value.contains("⭐️")
            val newStatus = if (isPremium) SubscriptionStatus.FREE else SubscriptionStatus.PREMIUM
            Log.d(TAG, "onChangeSubscription: 새로운 상태 결정: $newStatus")
            if (subscriptionService.changeSubscription(newStatus)) {
                Log.d(TAG, "onChangeSubscription: 상태 변경 성공, UI 새로고침")
                onFetchUser()
            } else {
                Log.d(TAG, "onChangeSubscription: 상태 변경 실패")
            }
        }
    }
}

// =======================================================================================
// 파일 경로: presentation/viewmodel/UserViewModelFactory.kt
// 계층: 프레젠테이션 계층 (Presentation Layer)
// 역할: ViewModel에 의존성을 수동으로 주입하기 위한 Factory 클래스.
// =======================================================================================
package com.example.type.usercase.architecture.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.type.usercase.architecture.domain.service.SubscriptionService
import com.example.type.usercase.architecture.domain.service.UserDisplayService

class UserViewModelFactory(
    private val displayService: UserDisplayService,
    private val subscriptionService: SubscriptionService
) : ViewModelProvider.Factory {
    private val TAG = "UserViewModelFactory"
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        Log.d(TAG, "create: ViewModel 인스턴스 생성 요청")
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(displayService, subscriptionService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

// =======================================================================================
// 파일 경로: presentation/view/MainActivity.kt
// 계층: 프레젠테이션 계층 (Presentation Layer)
// 역할: 사용자에게 보여지는 UI를 담당. ViewModel의 상태를 구독하여 화면을 그리고,
//      사용자 이벤트를 ViewModel에 전달.
// =======================================================================================
package com.example.type.usercase.architecture.presentation.view

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.type.usercase.architecture.di.ServiceLocator
import com.example.type.usercase.architecture.presentation.viewmodel.UserViewModel
import com.example.type.usercase.architecture.presentation.viewmodel.UserViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var viewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: Activity 생성")
        val nameTextView = TextView(this).apply { textSize = 24f; textAlignment = TextView.TEXT_ALIGNMENT_CENTER }
        val button = Button(this).apply {
            text = "Toggle Subscription"
            setOnClickListener {
                Log.d(TAG, "Button onClick: 이벤트 발생")
                viewModel.onChangeSubscription()
                Toast.makeText(this@MainActivity, "Updating...", Toast.LENGTH_SHORT).show()
            }
        }
        setContentView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(nameTextView)
            addView(button)
        })

        Log.d(TAG, "onCreate: ViewModel 초기화 시작")
        val factory = UserViewModelFactory(ServiceLocator.userDisplayService, ServiceLocator.subscriptionService)
        viewModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)
        Log.d(TAG, "onCreate: ViewModel 초기화 완료")

        lifecycleScope.launch {
            Log.d(TAG, "collect: ViewModel 상태 구독 시작")
            viewModel.userName.collect { name ->
                Log.d(TAG, "collect: 새로운 상태 수신: $name")
                nameTextView.text = name
            }
        }
        viewModel.onFetchUser()
    }
}
