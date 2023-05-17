package com.example.mazeapp

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.mazeapp.databinding.ActivityMainBinding
import android.content.*


class MainActivity : AppCompatActivity() {
    private lateinit var bind: ActivityMainBinding

    private lateinit var buttonStartBoard: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.main1)

        buttonStartBoard = bind.startBoardButton
        buttonStartBoard.setOnClickListener {
            val intent = Intent(this, BoardActivity::class.java)
            startActivity(intent)
        }
    }
}
