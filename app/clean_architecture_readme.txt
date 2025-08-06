src/main/kotlin/com/example/type/clean/architecture/
│
├── 📁 data                    # 데이터 계층: 데이터의 출처와 실제 구현을 담당
│   ├── 📁 datasource
│   │   └── 📄 ApiService.kt      # 네트워크 API, 로컬 DB 등 실제 데이터 소스와의 통신
│   └── 📁 repository
│       └── 📄 UserRepositoryImpl.kt # Domain 계층의 Repository 인터페이스에 대한 실제 구현체
│
├── 📁 di                      # 의존성 주입 계층
│   └── 📄 ServiceLocator.kt      # Hilt/Koin 등을 사용하지 않을 경우, 수동으로 의존성을 주입하는 설정
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
        ├── 📄 UserViewModel.kt
        └── 📄 UserViewModelFactory.kt


각 계층의 역할
📁 domain (도메인 계층): 앱의 심장과 같은 부분으로, 가장 핵심적인 비즈니스 규칙과 데이터 모델(User)을 담고 있습니다. 이 계층은 다른 어떤 계층에도 의존하지 않는 순수한 Kotlin 코드로 작성됩니다.

📁 data (데이터 계층): 도메인 계층에서 정의한 규칙(UserRepository 인터페이스)을 실제로 구현하는 부분입니다. 네트워크 API(ApiService)나 로컬 데이터베이스에서 데이터를 어떻게 가져올지를 결정합니다.

📁 presentation (프레젠테이션 계층): 사용자에게 보여지는 화면(MainActivity)과 해당 화면의 상태를 관리하는 로직(UserViewModel)을 담당합니다. 이 계층은 도메인 계층의 Use Case를 호출하여 비즈니스 로직을 실행합니다.

📁 di (의존성 주입 계층): 각 계층의 구성 요소들을 만들고 서로 연결(주입)해주는 역할을 합니다.


전체 예제 코드

// =======================================================================================
// 파일 경로: di/ServiceLocator.kt
// 계층: 의존성 주입 (DI) 계층
// 역할: Hilt/Koin 등을 사용하지 않을 경우, 수동으로 의존성을 생성하고 주입하는 설정 파일입니다.
// =======================================================================================
package com.example.type.hilt.clean.architecture.di

import com.example.type.hilt.clean.architecture.data.datasource.ApiService
import com.example.type.hilt.clean.architecture.data.datasource.FakeApiService
import com.example.type.hilt.clean.architecture.data.repository.UserRepositoryImpl
import com.example.type.hilt.clean.architecture.domain.repository.UserRepository
import com.example.type.hilt.clean.architecture.domain.usecase.ChangeSubscriptionUseCase
import com.example.type.hilt.clean.architecture.domain.usecase.FormatDisplayUserNameUseCase

// --- 의존성 주입을 위한 간단한 서비스 로케이터 ---
object ServiceLocator {
    private const val TAG = "ServiceLocator"

    // Data Layer
    private val apiService: ApiService by lazy {
        println("[$TAG] Creating ApiService instance")
        FakeApiService()
    }
    private val userRepository: UserRepository by lazy {
        println("[$TAG] Creating UserRepository instance")
        UserRepositoryImpl(apiService)
    }

    // Domain Layer
    val formatDisplayUserNameUseCase: FormatDisplayUserNameUseCase by lazy {
        println("[$TAG] Creating FormatDisplayUserNameUseCase instance")
        FormatDisplayUserNameUseCase(userRepository)
    }
    val changeSubscriptionUseCase: ChangeSubscriptionUseCase by lazy {
        println("[$TAG] Creating ChangeSubscriptionUseCase instance")
        ChangeSubscriptionUseCase(userRepository)
    }
}

// =======================================================================================

// 파일 경로: domain/model/User.kt
// 계층: 도메인 계층 (Domain Layer)
// 역할: 앱의 가장 핵심적인 데이터 구조(Entity)를 정의합니다.
// =======================================================================================
package com.example.type.hilt.clean.architecture.domain.model

// --- Domain Layer: 핵심 데이터 모델 (Entity) ---
enum class SubscriptionStatus { FREE, PREMIUM }
data class User(val name: String, val status: SubscriptionStatus)


// =======================================================================================

// 파일 경로: domain/repository/UserRepository.kt
// 계층: 도메인 계층 (Domain Layer)
// 역할: 데이터 계층이 '무엇을' 해야 하는지에 대한 규칙(계약)을 인터페이스로 정의합니다.
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
// =======================================================================================
package com.example.type.hilt.clean.architecture.domain.usecase

import com.example.type.hilt.clean.architecture.domain.model.SubscriptionStatus
import com.example.type.hilt.clean.architecture.domain.repository.UserRepository

// --- Domain Layer: Use Case ---
class FormatDisplayUserNameUseCase(private val userRepository: UserRepository) {
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

// --- Domain Layer: Use Case ---
class ChangeSubscriptionUseCase(private val userRepository: UserRepository) {
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
// 역할: 실제 데이터 소스와의 통신을 담당합니다.
// =======================================================================================
package com.example.type.hilt.clean.architecture.data.datasource

import com.example.type.hilt.clean.architecture.domain.model.User
import com.example.type.hilt.clean.architecture.domain.model.SubscriptionStatus
import kotlinx.coroutines.delay

// --- Data Layer: 데이터 소스 ---
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
// =======================================================================================
package com.example.type.hilt.clean.architecture.data.repository

import com.example.type.hilt.clean.architecture.data.datasource.ApiService
import com.example.type.hilt.clean.architecture.domain.model.User
import com.example.type.hilt.clean.architecture.domain.repository.UserRepository

// --- Data Layer: Repository 구현체 ---
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
// 역할: UI(View)에 필요한 상태를 관리하고, Use Case를 호출하여 비즈니스 로직을 실행합니다.
// =======================================================================================
package com.example.type.hilt.clean.architecture.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.type.hilt.clean.architecture.domain.model.SubscriptionStatus
import com.example.type.hilt.clean.architecture.domain.usecase.ChangeSubscriptionUseCase
import com.example.type.hilt.clean.architecture.domain.usecase.FormatDisplayUserNameUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// --- Presentation Layer: ViewModel ---
class UserViewModel(
    private val formatDisplayUserNameUseCase: FormatDisplayUserNameUseCase,
    private val changeSubscriptionUseCase: ChangeSubscriptionUseCase
) : ViewModel() {
    private val TAG = "UserViewModel"

    private val _userName = MutableStateFlow("Loading...")
    val userName: StateFlow<String> = _userName

    init {
        Log.d(TAG, "ViewModel 초기화")
    }

    fun onFetchUser() {
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

// 파일 경로: presentation/viewmodel/UserViewModelFactory.kt
// 계층: 프레젠테이션 계층 (Presentation Layer)
// 역할: ViewModel에 의존성을 수동으로 주입하기 위한 Factory 클래스입니다.
// =======================================================================================
package com.example.type.hilt.clean.architecture.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.type.hilt.clean.architecture.domain.usecase.ChangeSubscriptionUseCase
import com.example.type.hilt.clean.architecture.domain.usecase.FormatDisplayUserNameUseCase

// --- Presentation Layer: ViewModel Factory ---
class UserViewModelFactory(
    private val formatUseCase: FormatDisplayUserNameUseCase,
    private val changeUseCase: ChangeSubscriptionUseCase
) : ViewModelProvider.Factory {
    private val TAG = "UserViewModelFactory"

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        Log.d(TAG, "create: ViewModel 인스턴스 생성 요청")
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(formatUseCase, changeUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

// =======================================================================================

// 파일 경로: presentation/view/MainActivity.kt
// 계층: 프레젠테이션 계층 (Presentation Layer)
// 역할: 사용자에게 보여지는 UI를 담당합니다.
// =======================================================================================
package com.example.type.hilt.clean.architecture.presentation.view

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.type.hilt.clean.architecture.di.ServiceLocator
import com.example.type.hilt.clean.architecture.presentation.viewmodel.UserViewModel
import com.example.type.hilt.clean.architecture.presentation.viewmodel.UserViewModelFactory
import kotlinx.coroutines.launch

// --- Presentation Layer: Activity (UI) ---
class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var viewModel: UserViewModel
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

        // ViewModel 인스턴스 생성
        Log.d(TAG, "onCreate: ViewModel 초기화 시작")
        val viewModelFactory = UserViewModelFactory(
            ServiceLocator.formatDisplayUserNameUseCase,
            ServiceLocator.changeSubscriptionUseCase
        )
        viewModel = ViewModelProvider(this, viewModelFactory).get(UserViewModel::class.java)
        Log.d(TAG, "onCreate: ViewModel 초기화 완료")

        lifecycleScope.launch {
            Log.d(TAG, "collect: ViewModel의 userName 구독 시작")
            viewModel.userName.collect { name ->
                Log.d(TAG, "collect: 새로운 userName 수신: $name")
                nameTextView.text = name
            }
        }

        viewModel.onFetchUser()
    }
}
