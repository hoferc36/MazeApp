package com.example.mazeapp

import android.os.Bundle
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

            val heightText = if(bind.editTextHeight.text.toString() != "") bind.editTextHeight.text.toString() else "5"
            val height:Int = heightText.toInt()
            val widthText = if(bind.editTextWidth.text.toString() != "") bind.editTextWidth.text.toString() else "5"
            val width:Int = widthText.toInt()

            if(height in 1..40 && width in 1..40){
                intent.putExtra("heightMaze", height)
                intent.putExtra("widthMaze", width)
                startActivity(intent)
            }else{
                Toast.makeText(applicationContext, "Number need to be between 2 and 40", Toast.LENGTH_SHORT).show()
//                finish()
            }
        }

    }
}