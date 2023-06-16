package com.hoferc36.mazeapp.ui

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import com.hoferc36.mazeapp.DatabaseHelper
import com.hoferc36.mazeapp.objects.*
import com.hoferc36.mazeapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var bind: ActivityMainBinding
    private lateinit var database: DatabaseHelper
//    private lateinit var sharedPreferences: SharedPreferences

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
//        sharedPreferences = SharedPreferences("sharedPref", MODE_PRIVATE)

        settings = SettingsData()

        buttonMaze = bind.buttonMaze
        buttonMaze.setOnClickListener {
            val intent = Intent(this, BoardActivity::class.java)
            intent.putExtra("mazeSettings", settings)
            if(user != null) {
                intent.putExtra("previousUser", user!!.name)
            }
            startActivity(intent)
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
            if(user != null) {
                intent.putExtra("previousUser", user!!.name)
            }
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
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onRestoreInstanceState(savedInstanceState, persistentState)
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