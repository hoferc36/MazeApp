package com.hoferc36.mazeapp.ui

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

    private var user: UserData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("chandra", "on create main")
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.main1)

        database = DatabaseHelper(applicationContext)
        database.savesLookUp()

        user = if(database.saves1.user != "NULL") {
             database.userSearch(database.saves1.user)
        } else null

//        Log.d("chandra", "add user hi")
//        if(database.userAdd("hi") != -1L) {
//            Log.d("chandra", "search user hi")
//            user = database.userSearch("hi")
//            if(user != null) {
//                Log.d("chandra", "update hi to saves")
//                if (database.savesUpdateUser(user!!.name)){
//                    Log.d("chandra", "success update hi to saves")
//                    database.saves1.user = user!!.name
//                }
//            }
//        }
//        database.saves1.user = "NULL"
//        database.saves1.user = "hi"
//        database.saves1.user = "NULL"

        //TODO wins need to be displayed

        buttonMaze = bind.buttonMaze
        buttonMaze.setOnClickListener {
            val intent = Intent(this, BoardActivity::class.java)
            startActivity(intent)
        }

        buttonSettings = bind.buttonSettings
        buttonSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        buttonLogin = bind.buttonLogin
        buttonLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        Log.d("chandra", "done on create main")
    }

    override fun onResume() {
        Log.d("chandra", "onResume main")
        super.onResume()
        database.savesLookUp()
        checkUser()
        Log.d("chandra", "done onResume main")
    }

    private fun checkUser() {
        user = if(database.saves1.user != "NULL") {
            database.userSearch(database.saves1.user)
        } else null
        if (user != null) {
            bind.textViewUserData.text = user!!.toString()
            bind.buttonLogin.text = "Logout"
        } else {
            bind.textViewUserData.text = "No User Data"
            bind.buttonLogin.text = "Login"
        }
    }
}