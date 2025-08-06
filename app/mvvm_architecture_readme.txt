클린 아키텍처와 MVVM의 관계 및 변경점
클린 아키텍처: 비즈니스 로직을 담당하는 UseCase 계층을 명확히 분리하여, 계층 간의 의존성을 엄격하게 관리하는 데 중점을 둡니다.

표준 MVVM: 클린 아키텍처보다 구조가 더 단순하며, Repository가 데이터 소스와의 통신뿐만 아니라 관련된 비즈니스 로직까지 함께 처리하는 경우가 많습니다. UseCase 계층이 사라지고, ViewModel이 Repository에 직접 의존하는 구조입니다.

이 구조는 UseCase 계층이 없는, 더 단순하고 실용적인 MVVM 패턴을 따르고 있습니다.

src/main/kotlin/com/example/type/mvvm/architecture/
│
├── 📁 data
│   ├── 📁 datasource
│   │   └── 📄 ApiService.kt      # (Model) 실제 데이터 소스와의 통신
│   └── 📁 repository
│       └── 📄 UserRepositoryImpl.kt # (Model) 데이터 로직과 비즈니스 로직을 모두 처리
│
├── 📁 di
│   └── 📄 ServiceLocator.kt      # 의존성 주입 설정
│
├── 📁 domain
│   ├── 📁 model
│   │   └── 📄 User.kt            # (Model) 데이터 구조 정의
│   └── 📁 repository
│       └── 📄 UserRepository.kt  # (Model) 데이터 접근 규칙 정의 (인터페이스)
│
└── 📁 presentation
    ├── 📁 view
    │   └── 📄 MainActivity.kt    # (View) UI 담당
    └── 📁 viewmodel
        ├── 📄 UserViewModel.kt   # (ViewModel) UI 상태 관리 및 로직 호출
        └── 📄 UserViewModelFactory.kt


각 계층의 역할 요약
Model Layer (data, domain): 앱의 데이터와 비즈니스 로직을 모두 담당합니다. Repository가 데이터 소스(ApiService)를 사용하여 데이터를 가져오고, 필요한 비즈니스 규칙을 직접 처리합니다.

View Layer (presentation/view): 사용자에게 보여지는 UI(MainActivity)를 담당하며, ViewModel의 상태 변화를 관찰하여 화면을 업데이트합니다.

ViewModel Layer (presentation/viewmodel): View와 Model 사이의 중재자 역할을 합니다. View로부터 사용자 입력을 받아 Model(Repository)에 데이터 처리를 요청하고, Model로부터 받은 결과를 View가 사용하기 쉬운 형태로 가공하여 제공합니다.


// =======================================================================================
// 파일 경로: di/ServiceLocator.kt
// 계층: 의존성 주입 (DI) 계층
// 역할: 앱의 각 구성 요소(클래스)를 생성하고, 필요한 곳에 연결(주입)해주는 공장 역할.
//      UseCase 계층이 사라지고, ViewModel이 Repository를 직접 사용하도록 구조가 단순화되었습니다.
// =======================================================================================
package com.example.type.mvvm.architecture.di

import com.example.type.mvvm.architecture.data.datasource.ApiService
import com.example.type.mvvm.architecture.data.datasource.FakeApiService
import com.example.type.mvvm.architecture.data.repository.UserRepositoryImpl
import com.example.type.mvvm.architecture.domain.repository.UserRepository

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
// 계층: 모델 계층 (Model Layer) - 데이터 모델
// 역할: 앱에서 사용하는 데이터의 구조를 정의합니다. 이 모델은 여러 계층에서 사용됩니다.
// =======================================================================================
package com.example.type.mvvm.architecture.domain.model

enum class SubscriptionStatus { FREE, PREMIUM }
data class User(val name: String, val status: SubscriptionStatus)

// =======================================================================================
// 파일 경로: domain/repository/UserRepository.kt
// 계층: 모델 계층 (Model Layer) - Repository 인터페이스
// 역할: ViewModel이 데이터에 접근하기 위해 호출할 함수들의 명세를 정의합니다.
//      비즈니스 로직을 포함하므로, 함수 이름이 더 구체적입니다. (예: getUser -> getFormattedUserName)
// =======================================================================================
package com.example.type.mvvm.architecture.domain.repository

import com.example.type.mvvm.architecture.domain.model.SubscriptionStatus

interface UserRepository {
    suspend fun getFormattedUserName(): String
    suspend fun changeSubscription(newStatus: SubscriptionStatus): Boolean
}

// =======================================================================================
// 파일 경로: data/datasource/ApiService.kt
// 계층: 모델 계층 (Model Layer) - 데이터 소스
// 역할: 네트워크 API, 로컬 DB 등 실제 데이터 소스와의 통신을 담당하는 최하위 계층입니다.
// =======================================================================================
package com.example.type.mvvm.architecture.data.datasource

import com.example.type.mvvm.architecture.domain.model.User
import com.example.type.mvvm.architecture.domain.model.SubscriptionStatus
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
// 계층: 모델 계층 (Model Layer) - Repository 구현체
// 역할: Repository 인터페이스를 구현합니다. 데이터 소스(ApiService)를 사용하여 데이터를 가져오고,
//      관련된 비즈니스 로직(이름 포맷팅, 변경 가능 여부 확인 등)을 직접 처리합니다.
// =======================================================================================
package com.example.type.mvvm.architecture.data.repository

import com.example.type.mvvm.architecture.data.datasource.ApiService
import com.example.type.mvvm.architecture.domain.model.SubscriptionStatus
import com.example.type.mvvm.architecture.domain.repository.UserRepository

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
// 계층: 뷰모델 계층 (ViewModel Layer)
// 역할: View(Activity)에 필요한 상태(StateFlow)를 관리하고 노출합니다.
//      사용자 이벤트가 발생하면 Repository를 직접 호출하여 데이터 처리를 요청합니다.
// =======================================================================================
package com.example.type.mvvm.architecture.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.type.mvvm.architecture.domain.model.SubscriptionStatus
import com.example.type.mvvm.architecture.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val userRepository: UserRepository // UseCase 대신 Repository를 직접 주입받음
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
// 계층: 뷰모델 계층 (ViewModel Layer)
// 역할: ViewModel에 의존성(Repository)을 수동으로 주입하기 위한 Factory 클래스입니다.
// =======================================================================================
package com.example.type.mvvm.architecture.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.type.mvvm.architecture.domain.repository.UserRepository

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
// 계층: 뷰 계층 (View Layer)
// 역할: 사용자에게 보여지는 UI를 담당합니다. ViewModel의 상태를 구독하여 화면을 그리고,
//      사용자 이벤트를 ViewModel에 전달합니다. View는 비즈니스 로직을 포함하지 않습니다.
// =======================================================================================
package com.example.type.mvvm.architecture.presentation.view

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.type.mvvm.architecture.di.ServiceLocator
import com.example.type.mvvm.architecture.presentation.viewmodel.UserViewModel
import com.example.type.mvvm.architecture.presentation.viewmodel.UserViewModelFactory
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
