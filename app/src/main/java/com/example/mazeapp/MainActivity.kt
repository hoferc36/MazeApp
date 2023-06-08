package com.example.mazeapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.mazeapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var bind: ActivityMainBinding

    private lateinit var buttonMaze: Button
    private lateinit var buttonSettings: Button
    private lateinit var buttonLogin: Button

    private var user: String = ""
    private lateinit var settings: SettingsData


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.main1)

        settings = SettingsData()

        buttonMaze = bind.buttonMaze
        buttonMaze.setOnClickListener {
            val intent = Intent(this, BoardActivity::class.java)
            intent.putExtra("maze settings", settings)
            startActivity(intent)
        }

        buttonSettings = bind.buttonSettings
        buttonSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("PreviousSettings", settings)
            startActivityForResult(intent, 2)
        }

        buttonLogin = bind.buttonLogin
        buttonLogin.setOnClickListener {
            if(buttonLogin.text.toString() == "Login") {
                val intent = Intent(this, LoginActivity::class.java)
                startActivityForResult(intent, 1)
            }else {
                user = ""
            }
        }
        checkUser()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1){
            if(resultCode == Activity.RESULT_OK){
                user = data!!.getStringExtra("UserData").toString()
                checkUser()
            }
        }else if (requestCode == 2){
            if(resultCode == Activity.RESULT_OK){
                settings = if(data!!.getSerializableExtra("SettingsData") != null)
                    data.getSerializableExtra("SettingsData") as SettingsData else SettingsData()
            }
        }
    }

    private fun checkUser() {
        if (user != "") {
            bind.textViewUserData.text = "$user"
            bind.buttonLogin.text = "Logout"
        } else {
            bind.textViewUserData.text = "No User Data"
            bind.buttonLogin.text = "Login"
        }
    }
}