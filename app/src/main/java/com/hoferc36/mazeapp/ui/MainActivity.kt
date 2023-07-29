package com.hoferc36.mazeapp.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.hoferc36.mazeapp.DatabaseHelper
import com.hoferc36.mazeapp.objects.*
import com.hoferc36.mazeapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var bind: ActivityMainBinding
    private lateinit var database: DatabaseHelper

    private lateinit var buttonMaze: Button
    private lateinit var buttonSettings: Button
    private lateinit var buttonLogin: Button

    private var user: UserData? = null//TODO add default user instead of null
    private var settings: SettingsData = SettingsData()

    private val REQUEST_LOGIN = 1
    private val REQUEST_SETTINGS = 2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.main1)

        database = DatabaseHelper(applicationContext)

        Log.d("chandra", "saved retrieved ${savedInstanceState != null}")
        if(savedInstanceState != null) {
            val settingsId = savedInstanceState.getLong("settingsSaved", 1)
            settings = database.searchForSettings(settingsId) ?: SettingsData()

            val username = savedInstanceState.getString("userName")
            user = if (username != null) database.searchForUser(username) else null
            if(user != null){
                settings = database.searchForSettings(user!!.settingsId) ?: SettingsData()
            }
            settings.id = database.addSettings(settings)
        }

        buttonMaze = bind.buttonMaze
        buttonMaze.setOnClickListener {
            val intent = Intent(this, BoardActivity::class.java)
            intent.putExtra("mazeSettings", settings.id)
            if(user != null) {
                intent.putExtra("previousUser", user!!.name)
            }
            startActivity(intent)
        }

        buttonSettings = bind.buttonSettings
        buttonSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("previousSettings", settings.id)
            if(user != null) {
                intent.putExtra("previousUser", user!!.name)
            }
            startActivityForResult(intent, REQUEST_SETTINGS)
        }

        buttonLogin = bind.buttonLogin
        buttonLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            if(user != null) {
                intent.putExtra("previousUser", user!!.name)
            }
            startActivityForResult(intent, REQUEST_LOGIN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_LOGIN){
            user = if(resultCode == Activity.RESULT_OK){
                val username = data!!.getStringExtra("userData")
                if(username != null) database.searchForUser(username) else null
            }else null
        }else if (requestCode == REQUEST_SETTINGS){
            if(resultCode == Activity.RESULT_OK){
                val settingsId = data!!.getLongExtra("settingsData", 1)
                settings = database.searchForSettings(settingsId) ?: SettingsData()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkUser()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("settingsSaved", settings.id)
        if (user != null) {
            outState.putString("userName", user!!.name)
        }
    }

    private fun checkUser() {
        user = if (user != null && user!!.name != "") database.searchForUser(user!!.name) else null
        if(user != null){
            settings =database.searchForSettings(user!!.settingsId) ?: SettingsData()
        }
        if (user != null) {
            bind.textViewUserData.text = user!!.toString()
            bind.buttonLogin.text = "Logout"
        } else {
            bind.textViewUserData.text = "No User Data"
            bind.buttonLogin.text = "Login" //R.string.button_login.toString() nope change others
        }
    }
}