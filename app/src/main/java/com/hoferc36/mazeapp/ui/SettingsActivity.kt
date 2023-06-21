package com.hoferc36.mazeapp.ui

import android.app.Activity
import android.os.Bundle
import android.widget.*
import android.content.*
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import com.hoferc36.mazeapp.DatabaseHelper
import com.hoferc36.mazeapp.R
import com.hoferc36.mazeapp.objects.*
import com.hoferc36.mazeapp.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var bind: ActivitySettingsBinding
    private lateinit var database: DatabaseHelper

    private lateinit var buttonSave: Button
    private lateinit var buttonCancel: Button

    private lateinit var settings: SettingsData

    private val minSize = 2
    private val maxSize = 40

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(bind.settings1)

        database = DatabaseHelper(applicationContext)

        val settingId = intent.getIntExtra("previousSettings", 1)
        settings  = if(database.searchForSettings(settingId) != null) database.searchForSettings(settingId)!! else SettingsData()

        val buttonToggle = bind.toggleButton
        buttonToggle.setOnClickListener {
            if (bind.toggleButton.isChecked){
                bind.toggleButton.setBackgroundResource(R.color.success)
                settings.buttonToggle = true
            }else{
                bind.toggleButton.setBackgroundResource(R.color.warning)
                settings.buttonToggle = false
            }
        }

        buttonSave = bind.buttonSave
        buttonSave.setOnClickListener {
            if(updateSettings()) {
                val returnIntent = Intent(this, MainActivity::class.java)
                returnIntent.putExtra("settingsData", settings.id)
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }
        }

        buttonCancel = bind.buttonCancel
        buttonCancel.setOnClickListener {
            finish()
        }

        val buttonCorridor = bind.buttonCorridor
        buttonCorridor.setOnClickListener {
            if (bind.buttonCorridor.isChecked){
                bind.buttonCorridor.setBackgroundResource(R.color.success)
                settings.corridor = true
            }else{
                bind.buttonCorridor.setBackgroundResource(R.color.warning)
                settings.corridor = false
            }
        }

        setSettings()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
    }
    private fun setSettings(){
        if(settings.width != 5) bind.editTextWidth.setText(settings.width.toString())
        if(settings.height != 5) bind.editTextHeight.setText(settings.height.toString())

        if(settings.startX != 0) bind.editTextStartX.setText(settings.startX.toString())
        if(settings.startY!= 0) bind.editTextStartY.setText(settings.startY.toString())
        if(settings.endX != settings.width-1) bind.editTextEndX.setText(settings.endX.toString())
        if(settings.endY != settings.height-1) bind.editTextEndY.setText(settings.endY.toString())

        if(settings.seed!= 0) bind.editTextSeed.setText(settings.seed.toString())

        if(settings.buttonToggle) bind.toggleButton.performClick()
        if(settings.corridor) bind.buttonCorridor.performClick()
    }
    private fun updateSettings(): Boolean {
        //maze height
        val height = getIntFromView(bind.editTextHeight, 5)
        if (height in minSize..maxSize) {
            settings.height = height
        } else {
            Toast.makeText(applicationContext, "Height needs to be " +
                        "between $minSize and $maxSize", Toast.LENGTH_SHORT).show()
            return false
        }

        //maze width
        val width = getIntFromView(bind.editTextWidth, 5)
        if (width in minSize..maxSize) {
            settings.width = width
        } else {
            Toast.makeText(applicationContext, "Width needs to be " +
                        "between $minSize and $maxSize", Toast.LENGTH_SHORT).show()
            return false
        }

        //maze start coord
        val startCoordX = getIntFromView(bind.editTextStartX, 0)
        val startCoordY = getIntFromView(bind.editTextStartY, 0)
        if (startCoordX in 0 until width && startCoordY in 0 until height) {
            settings.startX = startCoordX
            settings.startY = startCoordY
        } else {
            Toast.makeText(applicationContext, "Start Coordinate have " +
                        "to be in the maze", Toast.LENGTH_SHORT).show()
            return false
        }

        //maze end coord
        val endCoordX = getIntFromView(bind.editTextEndX, width-1)
        val endCoordY = getIntFromView(bind.editTextEndY, height-1)
        if (endCoordX in 0 until width && endCoordY in 0 until height) {
            settings.endX = endCoordX
            settings.endY = endCoordY
        } else {
            Toast.makeText(applicationContext, "End Coordinate have " +
                        "to be in the maze", Toast.LENGTH_SHORT).show()
            return false
        }

        //maze width
        val seed = getIntFromView(bind.editTextSeed, 0)
        if (seed >= 0 ) {
            settings.seed = seed
        } else {
            Toast.makeText(applicationContext, "Maze Seed needs to be >= 0",
                Toast.LENGTH_SHORT).show()
            return false
        }
        settings.buttonToggle = bind.toggleButton.isChecked
        settings.corridor = bind.buttonCorridor.isChecked

        database.updateSettings(settings.id, settings)
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