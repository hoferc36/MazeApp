package com.hoferc36.mazeapp.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.hoferc36.mazeapp.DatabaseHelper
import com.hoferc36.mazeapp.R
import com.hoferc36.mazeapp.objects.*
import com.hoferc36.mazeapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var bind: ActivityMainBinding
    private lateinit var database: DatabaseHelper

    private lateinit var buttonMaze: Button
    private lateinit var buttonSettings: Button
    private lateinit var buttonLogin: Button

    private var user: UserData? = null
    private lateinit var settings: SettingsData

    private val REQUEST_LOGIN = 1
    private val REQUEST_SETTINGS = 2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.main1)

        database = DatabaseHelper(applicationContext)

        //TODO add saved user and setting to onCreate
        settings = SettingsData()
        settings.id = database.addSettings(settings)

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

    private fun checkUser() {
        user = if (user != null && user!!.name != "") database.searchForUser(user!!.name) else null
        settings = database.searchForSettings(settings.id) ?: SettingsData()
        //TODO settings should reflect user preference
        if (user != null) {
            bind.textViewUserData.text = user!!.toString()
            bind.buttonLogin.text = "Logout"
        } else {
            bind.textViewUserData.text = "No User Data"
            bind.buttonLogin.text = R.string.button_login.toString()//"Login" TODO change others
        }
    }
}