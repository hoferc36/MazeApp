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
    private lateinit var buttonUser: Button
    private lateinit var buttonReset: Button

    private lateinit var settings: SettingsData
    private var user: UserData? = null

    private val minSize = 2
    private val maxSize = 40

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(bind.settings1)

        database = DatabaseHelper(applicationContext)

        val settingsId = intent.getLongExtra("previousSettings", 1)
        settings  = database.searchForSettings(settingsId) ?: SettingsData()

        val username = intent.getStringExtra("previousUser")
        user = if(username != null) database.searchForUser(username) else null

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

        buttonUser = bind.buttonSetAsUser
        if(user != null) {
            buttonUser.visibility = View.VISIBLE
            buttonUser.setOnClickListener {
                if (updateSettings()) {
                    user!!.settingsId = database.addSettings(settings)
                    database.updateUser(user!!.name, user!!)
                    Toast.makeText(applicationContext, "User settings updated", Toast.LENGTH_SHORT).show()
                }
            }
        }else{
            buttonUser.visibility = View.GONE
        }

        buttonReset = bind.buttonResetToDefault
        buttonReset.setOnClickListener {
            settings = SettingsData()
            setSettings()
            Toast.makeText(applicationContext, "Settings Reset", Toast.LENGTH_SHORT).show()
            //TODO user preference?
        }

        buttonSave = bind.buttonSave
        buttonSave.setOnClickListener {
            if(updateSettings()) {
                settings.id = database.addSettings(settings)
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

        setSettings()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
    }
    private fun setSettings(){
        if(settings.width != 5) bind.editTextWidth.setText(settings.width.toString())
            else bind.editTextWidth.text.clear()
        if(settings.height != 5) bind.editTextHeight.setText(settings.height.toString())
            else bind.editTextHeight.text.clear()

        if(settings.startX != 0) bind.editTextStartX.setText(settings.startX.toString())
            else bind.editTextStartX.text.clear()
        if(settings.startY != 0) bind.editTextStartY.setText(settings.startY.toString())
            else bind.editTextStartY.text.clear()

        if(settings.endX != settings.width-1) bind.editTextEndX.setText(settings.endX.toString())
            else bind.editTextEndX.text.clear()
        if(settings.endY != settings.height-1) bind.editTextEndY.setText(settings.endY.toString())
            else bind.editTextEndY.text.clear()

        if(settings.seed!= 0) bind.editTextSeed.setText(settings.seed.toString())
            else bind.editTextSeed.text.clear()

        if(settings.buttonToggle && !bind.toggleButton.isChecked) bind.toggleButton.performClick()
        if(settings.corridor && !bind.buttonCorridor.isChecked) bind.buttonCorridor.performClick()
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
        val startCoordY = getIntFromView(bind.editTextStartY, 0)
        val startCoordX = getIntFromView(bind.editTextStartX, 0)
        if (startCoordX in 0 until settings.width && startCoordY in 0 until settings.height) {
            settings.startY = startCoordY
            settings.startX = startCoordX
        } else {
            Toast.makeText(applicationContext, "Start Coordinate have " +
                        "to be in the maze", Toast.LENGTH_SHORT).show()
            return false
        }

        //maze end coord
        val endCoordY = getIntFromView(bind.editTextEndY, settings.height-1)
        val endCoordX = getIntFromView(bind.editTextEndX, settings.width-1)
        if (endCoordX in 0 until settings.width && endCoordY in 0 until settings.height) {
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
        settings.buttonToggle = bind.toggleButton.isChecked
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