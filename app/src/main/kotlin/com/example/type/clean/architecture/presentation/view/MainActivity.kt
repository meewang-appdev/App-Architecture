// 파일 경로: presentation/view/MainActivity.kt
package com.example.type.clean.architecture.presentation.view

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.type.clean.architecture.di.ServiceLocator
import com.example.type.clean.architecture.presentation.viewmodel.UserViewModel
import com.example.type.clean.architecture.presentation.viewmodel.UserViewModelFactory
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