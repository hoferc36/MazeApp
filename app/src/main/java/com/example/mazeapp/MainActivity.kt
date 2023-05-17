package com.example.mazeapp

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.mazeapp.databinding.ActivityMainBinding
import android.content.*
import android.view.MotionEvent


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

            val heightText = if(bind.editTextHeight.text.toString() != "") bind.editTextHeight.text.toString() else "5"
            val height:Int = heightText.toInt()
            val widthText = if(bind.editTextWidth.text.toString() != "") bind.editTextWidth.text.toString() else "5"
            val width:Int = widthText.toInt()

            if(height in 5..21 && width in 5..21){
                intent.putExtra("heightMaze", height)
                intent.putExtra("widthMaze", width)
                startActivity(intent)
            }else{
                Toast.makeText(applicationContext, "Number need to be between 5 and 21", Toast.LENGTH_SHORT).show()

            }
        }

    }
}