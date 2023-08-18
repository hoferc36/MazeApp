package com.hoferc36.mazeapp.ui

import android.os.Bundle
import android.widget.*
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import com.hoferc36.mazeapp.DatabaseHelper
import com.hoferc36.mazeapp.R
import com.hoferc36.mazeapp.objects.*
import com.hoferc36.mazeapp.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var bind: ActivitySettingsBinding
    private lateinit var database: DatabaseHelper

    private lateinit var settings: SettingsData
    private var user: UserData? = null

    private val minSize = 2
    private val maxSize = 40

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(bind.settings1)

        database = DatabaseHelper(applicationContext)
        database.savesLookUp()

        settings = database.settingsSearch(database.saves1.settingsId)

        user = if(database.saves1.user != "NULL") {
            database.userSearch(database.saves1.user)
        } else null

        bind.toggleButtons.setOnClickListener {
            if (bind.toggleButtons.isChecked){
                bind.toggleButtons.setBackgroundResource(R.color.success)
            }else{
                bind.toggleButtons.setBackgroundResource(R.color.warning)
            }
        }

        bind.buttonCorridor.setOnClickListener {
            if (bind.buttonCorridor.isChecked){
                bind.buttonCorridor.setBackgroundResource(R.color.success)
            }else{
                bind.buttonCorridor.setBackgroundResource(R.color.warning)
            }
        }

        if(user != null) {
            bind.buttonSetAsUser.visibility = View.VISIBLE
            bind.buttonSetAsUser.setOnClickListener {
                if (updateSettings()) {
                    val setId = database.settingsAdd(settings)
                    settings.id = if(setId == -1L) database.settingsSearchForIdentical(settings) else setId

                    user!!.settingsId = settings.id
                    database.userUpdate(user!!.name, user!!)

                    Toast.makeText(applicationContext, "User settings updated", Toast.LENGTH_SHORT).show()
                }
            }
        }else{
            bind.buttonSetAsUser.visibility = View.GONE
        }

        bind.buttonResetToDefault.setOnClickListener {
            settings = SettingsData()
            database.savesUpdateSettings(settings.id)
            setSettings()
            Toast.makeText(applicationContext, "Settings Reset", Toast.LENGTH_SHORT).show()
        }

        bind.buttonSave.setOnClickListener {
            if(updateSettings()) {
                val setId = database.settingsAdd(settings)
                settings.id = if(setId == -1L) database.settingsSearchForIdentical(settings) else setId

                database.savesUpdateSettings(settings.id)
                finish()
            }
        }

        bind.buttonCancel.setOnClickListener {
            finish()
        }

        setSettings()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        //TODO added quick select for easy, medium or hard
    }
    private fun setSettings(){
        if(settings.cols != 5) bind.editTextWidth.setText(settings.cols.toString())
            else bind.editTextWidth.text.clear()
        if(settings.rows != 5) bind.editTextHeight.setText(settings.rows.toString())
            else bind.editTextHeight.text.clear()

        if(settings.startX != 0) bind.editTextStartX.setText(settings.startX.toString())
            else bind.editTextStartX.text.clear()
        if(settings.startY != 0) bind.editTextStartY.setText(settings.startY.toString())
            else bind.editTextStartY.text.clear()

        if(settings.endX != settings.cols-1) bind.editTextEndX.setText(settings.endX.toString())
            else bind.editTextEndX.text.clear()
        if(settings.endY != settings.rows-1) bind.editTextEndY.setText(settings.endY.toString())
            else bind.editTextEndY.text.clear()

        if(settings.seed!= 0) bind.editTextSeed.setText(settings.seed.toString())
            else bind.editTextSeed.text.clear()

        if(settings.buttonToggle && !bind.toggleButtons.isChecked) bind.toggleButtons.performClick()
        if(settings.corridor && !bind.buttonCorridor.isChecked) bind.buttonCorridor.performClick()
    }
    private fun updateSettings(): Boolean {
        //maze height
        val height = getIntFromView(bind.editTextHeight, 5)
        if (height in minSize..maxSize) {
            settings.rows = height
        } else {
            Toast.makeText(applicationContext, "Height needs to be " +
                        "between $minSize and $maxSize", Toast.LENGTH_SHORT).show()
            return false
        }

        //maze width
        val width = getIntFromView(bind.editTextWidth, 5)
        if (width in minSize..maxSize) {
            settings.cols = width
        } else {
            Toast.makeText(applicationContext, "Width needs to be " +
                        "between $minSize and $maxSize", Toast.LENGTH_SHORT).show()
            return false
        }

        //maze start coord
        val startCoordY = getIntFromView(bind.editTextStartY, 0)
        val startCoordX = getIntFromView(bind.editTextStartX, 0)
        if (startCoordX in 0 until settings.cols && startCoordY in 0 until settings.rows) {
            settings.startY = startCoordY
            settings.startX = startCoordX
        } else {
            Toast.makeText(applicationContext, "Start Coordinate have " +
                        "to be in the maze", Toast.LENGTH_SHORT).show()
            return false
        }

        //maze end coord
        val endCoordY = getIntFromView(bind.editTextEndY, settings.rows-1)
        val endCoordX = getIntFromView(bind.editTextEndX, settings.cols-1)
        if (endCoordX in 0 until settings.cols && endCoordY in 0 until settings.rows) {
            settings.endY = endCoordY
            settings.endX = endCoordX
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
        settings.buttonToggle = bind.toggleButtons.isChecked
        settings.corridor = bind.buttonCorridor.isChecked

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