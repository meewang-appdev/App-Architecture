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
