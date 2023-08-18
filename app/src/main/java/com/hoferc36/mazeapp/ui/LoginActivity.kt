package com.hoferc36.mazeapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.hoferc36.mazeapp.DatabaseHelper
import com.hoferc36.mazeapp.objects.*
import com.hoferc36.mazeapp.databinding.ActivityLoginBinding

class LoginActivity: AppCompatActivity() {
    private lateinit var bind: ActivityLoginBinding
    private lateinit var database: DatabaseHelper

    private var user: UserData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("chandra", "on create login")
        super.onCreate(savedInstanceState)
        bind = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(bind.login1)

        database = DatabaseHelper(applicationContext)
        database.savesLookUp()

        user = if(database.saves1.user != "NULL") {
            database.userSearch(database.saves1.user)
        } else null

        bind.buttonCreate.setOnClickListener {
            val textStringUsername = bind.editTextUsername.text.toString()
            if(textStringUsername != ""){
                if(database.userAdd(textStringUsername) != -1L){
                    user = database.userSearch(textStringUsername)
                    database.savesUpdateUser(user!!.name)
                    pageRefresh()

                    Toast.makeText(applicationContext, "User Created", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(applicationContext, "User already exists", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(applicationContext, "Enter Something", Toast.LENGTH_SHORT).show()
            }
        }

        bind.buttonLogin.setOnClickListener {
            if(bind.buttonLogin.text.toString() == "Login"){
                val textStringUsername2 = bind.editTextUsername.text.toString()
                if(textStringUsername2 != "") {
                    if (database.userSearch(textStringUsername2) != null) {
                        database.savesUpdateUser(textStringUsername2)
                        bind.editTextUsername.text.clear()
                        pageRefresh()

                        if(user != null && user!!.settingsId > 1 && database.saves1.settingsId == 1L){
                            database.savesUpdateSettings(user!!.settingsId)
                        }

                        Toast.makeText(applicationContext, "User Login", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(applicationContext, "User doesn't exists", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(applicationContext, "Enter Something", Toast.LENGTH_SHORT).show()
                }
            }else{
                //logout button
                database.savesUpdateUser("NULL")
                pageRefresh()
                Toast.makeText(applicationContext, "User Logout", Toast.LENGTH_SHORT).show()
            }
        }

        bind.buttonBack.setOnClickListener {
            finish()
        }

        pageRefresh()
        Log.d("chandra", "done on create login")
    }

    private fun pageRefresh() {
        user = if(database.saves1.user != "NULL") {
            database.userSearch(database.saves1.user)
        } else null
        if (user != null) {
            bind.textViewUserData.text = user!!.toString()
            bind.buttonLogin.text = "Logout"
            bind.buttonCreate.visibility = View.INVISIBLE
            bind.editTextUsername.visibility = View.INVISIBLE
        } else {
            bind.textViewUserData.text = "No User Data"
            bind.buttonLogin.text = "Login"
            bind.buttonCreate.visibility = View.VISIBLE
            bind.editTextUsername.visibility = View.VISIBLE
        }
    }
}