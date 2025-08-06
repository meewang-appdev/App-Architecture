package com.example.type

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var nameTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(40, 40, 40, 40)
        }

        nameTextView = TextView(this).apply {
            textSize = 24f
            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            setPadding(0, 0, 0, 50) // 버튼과의 간격
        }
        layout.addView(nameTextView)

        val buttonTexts = listOf(
            "clean.architecture",
            "hilt.clean.architecture",
            "mvvm.architecture",
            "repository.architecture",
            "usercase.architecture"
        )

        buttonTexts.forEachIndexed { index, text ->
            val button = Button(this).apply {
                this.text = text
                setOnClickListener {
                    val i = index + 1 // 인덱스를 1부터 시작하도록 조정
                    val intent = when (i) {
                        1 -> Intent(this@MainActivity, com.example.type.clean.architecture.presentation.view.MainActivity::class.java)
                        2 -> Intent(this@MainActivity, com.example.type.hilt.clean.architecture.presentation.view.MainActivity::class.java)
                        3 -> Intent(this@MainActivity, com.example.type.mvvm.architecture.presentation.view.MainActivity::class.java)
                        4 -> Intent(this@MainActivity, com.example.type.repository.architecture.presentation.view.MainActivity::class.java)
                        5 -> Intent(this@MainActivity, com.example.type.usercase.architecture.presentation.view.MainActivity::class.java)
                        else -> null
                    }
                    intent?.let { startActivity(it) }
                }
            }
            layout.addView(button)
        }

        setContentView(layout)
    }
}