package com.example.mazeapp

import android.os.Bundle
import android.widget.*
import android.content.*
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import com.example.mazeapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var bind: ActivityMainBinding
    private lateinit var database: DatabaseHelper

    private lateinit var buttonStartBoard: Button

    private val minSize = 2
    private val maxSize = 40

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.main1)
        database = DatabaseHelper(this)

        val buttonToggle = bind.toggleButton
        buttonToggle.setOnClickListener {
            if (bind.toggleButton.isChecked){
                bind.toggleButton.setBackgroundResource(R.color.success)
            }else{
                bind.toggleButton.setBackgroundResource(R.color.warning)
            }
        }

        buttonStartBoard = bind.startBoardButton
        buttonStartBoard.setOnClickListener {
            val intent = Intent(this, BoardActivity::class.java)
            if (getInputForMaze(intent))startActivity(intent)
        }

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
    }

    private fun getInputForMaze(intent: Intent): Boolean {
        //maze height
        val height = getIntFromView(bind.editTextHeight, 5)
        if (height in minSize..maxSize) {
            intent.putExtra("heightMaze", height)
        } else {
            Toast.makeText(applicationContext, "Height needs to be " +
                        "between $minSize and $maxSize", Toast.LENGTH_SHORT).show()
            return false
        }

        //maze width
        val width = getIntFromView(bind.editTextWidth, 5)
        if (width in minSize..maxSize) {
            intent.putExtra("widthMaze", width)
        } else {
            Toast.makeText(applicationContext, "Width needs to be " +
                        "between $minSize and $maxSize", Toast.LENGTH_SHORT).show()
            return false
        }

        //maze start coord
        val startCoordX = getIntFromView(bind.editTextStartX, 0)
        val startCoordY = getIntFromView(bind.editTextStartY, 0)
        if (startCoordX in 0 until width && startCoordY in 0 until height) {
            intent.putExtra("startCoordX", startCoordX)
            intent.putExtra("startCoordY", startCoordY)
        } else {
            Toast.makeText(applicationContext, "Start Coordinate have " +
                        "to be in the maze", Toast.LENGTH_SHORT).show()
            return false
        }

        //maze end coord
        val endCoordX = getIntFromView(bind.editTextEndX, width-1)
        val endCoordY = getIntFromView(bind.editTextEndY, height-1)
        if (endCoordX in 0 until width && endCoordY in 0 until height) {
            intent.putExtra("endCoordX", endCoordX)
            intent.putExtra("endCoordY", endCoordY)
        } else {
            Toast.makeText(applicationContext, "End Coordinate have " +
                        "to be in the maze", Toast.LENGTH_SHORT).show()
            return false
        }

        //maze width
        val seed = getIntFromView(bind.editTextSeed, 0)
        if (seed >= 0 ) {
            intent.putExtra("mazeSeed", seed)
        } else {
            Toast.makeText(applicationContext, "Maze Seed needs to be >= 0",
                Toast.LENGTH_SHORT).show()
            return false
        }

        intent.putExtra("isButtonOn", bind.toggleButton.isChecked)
        return true
    }

    private fun getIntFromView(text: EditText, defaultVal: Int):Int{
        val textString = text.text.toString()
        return if(textString != ""){
            try {
                textString.toInt()
            }catch(e: NumberFormatException) {
                textString.hashCode()
            }
        }else defaultVal
    }
}