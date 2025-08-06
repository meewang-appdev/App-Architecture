ViewModel → Repository
이 구조는 비즈니스 로직이 Repository 계층에 통합되어 있는 더 단순한 구조입니다. Service/UseCase 계층이 없어 구조가 간결하지만, Repository의 책임이 커질 수 있습니다.

src/main/kotlin/com/example/type/repository/architecture/
│
├── 📁 data
│   ├── 📁 datasource
│   │   └── 📄 ApiService.kt
│   └── 📁 repository
│       └── 📄 UserRepositoryImpl.kt # 비즈니스 로직 포함
│
├── 📁 di
│   └── 📄 ServiceLocator.kt
│
├── 📁 domain
│   ├── 📁 model
│   │   └── 📄 User.kt
│   └── 📁 repository
│       └── 📄 UserRepository.kt      # 비즈니스 로직 함수 명세 포함
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
package com.example.type.repository.architecture.di

import com.example.type.repository.architecture.data.datasource.ApiService
import com.example.type.repository.architecture.data.datasource.FakeApiService
import com.example.type.repository.architecture.data.repository.UserRepositoryImpl
import com.example.type.repository.architecture.domain.repository.UserRepository

object ServiceLocator {
    private const val TAG = "ServiceLocator"
    private val apiService: ApiService by lazy {
        println("[$TAG] Creating ApiService instance")
        FakeApiService()
    }
    val userRepository: UserRepository by lazy {
        println("[$TAG] Creating UserRepository instance")
        UserRepositoryImpl(apiService)
    }
}

// =======================================================================================
// 파일 경로: domain/model/User.kt
// 계층: 도메인 계층 (Domain Layer)
// 역할: 앱의 가장 핵심적인 데이터 구조(Entity)를 정의.
// =======================================================================================
package com.example.type.repository.architecture.domain.model

enum class SubscriptionStatus { FREE, PREMIUM }
data class User(val name: String, val status: SubscriptionStatus)

// =======================================================================================
// 파일 경로: domain/repository/UserRepository.kt
// 계층: 도메인 계층 (Domain Layer)
// 역할: Repository가 이제 비즈니스 로직을 직접 처리하므로, 인터페이스에 해당 함수들을 정의.
// =======================================================================================
package com.example.type.repository.architecture.domain.repository

import com.example.type.repository.architecture.domain.model.SubscriptionStatus

interface UserRepository {
    suspend fun getFormattedUserName(): String
    suspend fun changeSubscription(newStatus: SubscriptionStatus): Boolean
}

// =======================================================================================
// 파일 경로: data/datasource/ApiService.kt
// 계층: 데이터 계층 (Data Layer)
// 역할: 네트워크 API, 로컬 DB 등 실제 데이터 소스와의 통신을 담당.
// =======================================================================================
package com.example.type.repository.architecture.data.datasource

import com.example.type.repository.architecture.domain.model.User
import com.example.type.repository.architecture.domain.model.SubscriptionStatus
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
// 역할: Repository 인터페이스를 구현. 데이터 접근과 '비즈니스 로직'을 모두 처리.
// =======================================================================================
package com.example.type.repository.architecture.data.repository

import com.example.type.repository.architecture.data.datasource.ApiService
import com.example.type.repository.architecture.domain.model.SubscriptionStatus
import com.example.type.repository.architecture.domain.repository.UserRepository

class UserRepositoryImpl(private val apiService: ApiService) : UserRepository {
    private val TAG = "UserRepositoryImpl"

    override suspend fun getFormattedUserName(): String {
        println("[$TAG] getFormattedUserName: 실행")
        val user = apiService.fetchUser()
        if (user.name.equals("Admin", ignoreCase = true)) {
            return "👑 ${user.name}"
        }
        return when (user.status) {
            SubscriptionStatus.PREMIUM -> "⭐️ ${user.name}"
            SubscriptionStatus.FREE -> user.name
        }
    }

    override suspend fun changeSubscription(newStatus: SubscriptionStatus): Boolean {
        println("[$TAG] changeSubscription: 실행, newStatus: $newStatus")
        val currentUser = apiService.fetchUser()
        if (currentUser.name.equals("Admin", ignoreCase = true)) {
            println("[$TAG] changeSubscription: Admin 유저는 변경 불가")
            return false
        }
        val updatedUser = currentUser.copy(status = newStatus)
        return apiService.saveUser(updatedUser)
    }
}

// =======================================================================================
// 파일 경로: presentation/viewmodel/UserViewModel.kt
// 계층: 프레젠테이션 계층 (Presentation Layer)
// 역할: UI(View)에 필요한 상태를 관리하고, Repository를 직접 호출하여 로직을 실행.
// =======================================================================================
package com.example.type.repository.architecture.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.type.repository.architecture.domain.model.SubscriptionStatus
import com.example.type.repository.architecture.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val userRepository: UserRepository
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
            _userName.value = userRepository.getFormattedUserName()
            Log.d(TAG, "onFetchUser: UI 상태 업데이트: ${_userName.value}")
        }
    }

    fun onChangeSubscription() {
        Log.d(TAG, "onChangeSubscription: 이벤트 수신")
        viewModelScope.launch {
            val currentFormattedName = userRepository.getFormattedUserName()
            val isPremium = currentFormattedName.contains("⭐️")
            val newStatus = if (isPremium) SubscriptionStatus.FREE else SubscriptionStatus.PREMIUM
            Log.d(TAG, "onChangeSubscription: 새로운 상태 결정: $newStatus")

            if (userRepository.changeSubscription(newStatus)) {
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
package com.example.type.repository.architecture.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.type.repository.architecture.domain.repository.UserRepository

class UserViewModelFactory(
    private val repository: UserRepository
) : ViewModelProvider.Factory {
    private val TAG = "UserViewModelFactory"
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        Log.d(TAG, "create: ViewModel 인스턴스 생성 요청")
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(repository) as T
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
package com.example.type.repository.architecture.presentation.view

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.type.repository.architecture.di.ServiceLocator
import com.example.type.repository.architecture.presentation.viewmodel.UserViewModel
import com.example.type.repository.architecture.presentation.viewmodel.UserViewModelFactory
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
        val factory = UserViewModelFactory(ServiceLocator.userRepository)
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
