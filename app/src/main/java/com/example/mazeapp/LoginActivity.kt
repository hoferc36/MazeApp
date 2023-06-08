package com.example.mazeapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.mazeapp.databinding.ActivityLoginBinding

class LoginActivity: AppCompatActivity() {
    private lateinit var bind: ActivityLoginBinding
    private lateinit var returnIntent: Intent

    private var user: UserData? = null
    private var userCount: Int = 0

    private lateinit var createButton: Button
    private lateinit var loginButton: Button
    private lateinit var backButton: Button

    private lateinit var database: MutableList <UserData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(bind.login1)

        database = mutableListOf()
        returnIntent = Intent(this, MainActivity::class.java)
        pageRefresh()

        createButton = bind.buttonCreate
        createButton.setOnClickListener {
            val username = bind.editTextUsername.text.toString()
            if(username != ""){
                //check database for duplicates
                if(tempCheckDatabase(username) == null){
                    //add to database
                    user = UserData(userCount++, username)
                    database.add(user!!)
                    pageRefresh()
                    Toast.makeText(applicationContext, "User Created", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(applicationContext, "User already exists", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(applicationContext, "Enter Something", Toast.LENGTH_SHORT).show()
            }
        }

        loginButton = bind.buttonLogin
        loginButton.setOnClickListener {
            val username = bind.editTextUsername.text.toString()
            if(bind.buttonLogin.text.toString() == "Login"){
                //check database for user
                if(tempCheckDatabase(username) != null){
                    //get user from database
                    user = tempCheckDatabase(username)
                    pageRefresh()
                    Toast.makeText(applicationContext, "User Login", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(applicationContext, "User doesn't exists", Toast.LENGTH_SHORT).show()
                }
            }else{
                //logout button
                user = null
                pageRefresh()
                Toast.makeText(applicationContext, "User Logout", Toast.LENGTH_SHORT).show()
            }
        }

        backButton = bind.buttonBack
        backButton.setOnClickListener {
            pageRefresh()
            finish()
        }
    }

    private fun pageRefresh() {
        if (user != null) {
            bind.textViewUserData.text = "${user!!.name}: /nWins ${user!!.wins}"
            bind.buttonLogin.text = "Logout"
            bind.buttonCreate.visibility = View.INVISIBLE
            bind.editTextUsername.visibility = View.INVISIBLE
            returnIntent.putExtra("UserData", user!!.name)
            setResult(Activity.RESULT_OK,returnIntent)
        } else {
            bind.textViewUserData.text = "No User Data"
            bind.buttonLogin.text = "Login"
            bind.buttonCreate.visibility = View.VISIBLE
            bind.editTextUsername.visibility = View.VISIBLE
            returnIntent.putExtra("UserData", "")
            setResult(Activity.RESULT_OK,returnIntent)
        }
    }

    private fun tempCheckDatabase(user:String):UserData?{
        var userData: UserData? = null
        database.forEach {
             if(it.name == user)userData = it
        }
        return userData
    }
}