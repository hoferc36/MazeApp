package com.hoferc36.mazeapp.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

    private var user: UserData? = null
    private lateinit var settings: SettingsData

    private val REQUEST_LOGIN = 1
    private val REQUEST_SETTINGS = 2
    private val REQUEST_WINS = 3


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.main1)

        database = DatabaseHelper(applicationContext)

        settings = SettingsData()

        buttonMaze = bind.buttonMaze
        buttonMaze.setOnClickListener {
            val intent = Intent(this, BoardActivity::class.java)
            intent.putExtra("mazeSettings", settings)
            intent.putExtra("previousUser", user)
            startActivityForResult(intent, REQUEST_WINS)
        }

        buttonSettings = bind.buttonSettings
        buttonSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("previousSettings", settings)
            startActivityForResult(intent, REQUEST_SETTINGS)
        }

        buttonLogin = bind.buttonLogin
        buttonLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("previousUser", user)
            startActivityForResult(intent, REQUEST_LOGIN)
        }
        checkUser()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_LOGIN){
            if(resultCode == Activity.RESULT_OK){
                val username = data!!.getStringExtra("userData")
                user = if(username != null) database.searchForUser(username) else null
                checkUser()
            }else {
                user = null
                checkUser()
            }
        }else if (requestCode == REQUEST_SETTINGS){
            if(resultCode == Activity.RESULT_OK){
                settings = if(data!!.getSerializableExtra("settingsData") != null)
                    data.getSerializableExtra("settingsData") as SettingsData else SettingsData()
            }
        }else if (requestCode == REQUEST_WINS){
            if(resultCode == Activity.RESULT_OK){
                if(user != null) {
                    if (data!!.getBooleanExtra("win",false)) user!!.wins++
                    database.updateUser(user!!.name, user!!)
                    checkUser()
                }
            }
        }
    }

    private fun checkUser() {
        if (user != null) {
            bind.textViewUserData.text = user!!.toString()
            bind.buttonLogin.text = "Logout"
        } else {
            bind.textViewUserData.text = "No User Data"
            bind.buttonLogin.text = "Login"
        }
    }
}