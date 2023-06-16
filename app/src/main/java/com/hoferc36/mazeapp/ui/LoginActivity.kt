package com.hoferc36.mazeapp.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.hoferc36.mazeapp.DatabaseHelper
import com.hoferc36.mazeapp.objects.*
import com.hoferc36.mazeapp.databinding.ActivityLoginBinding

class LoginActivity: AppCompatActivity() {
    private lateinit var bind: ActivityLoginBinding
    private lateinit var returnIntent: Intent
    private lateinit var database: DatabaseHelper

    private var user: UserData? = null

    private lateinit var createButton: Button
    private lateinit var loginButton: Button
    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(bind.login1)

        database = DatabaseHelper(applicationContext)


        user = if(intent.getSerializableExtra("previousUser") != null)
            intent.getSerializableExtra("previousUser") as UserData else null
        returnIntent = Intent(this, MainActivity::class.java)
        pageRefresh()

        createButton = bind.buttonCreate
        createButton.setOnClickListener {
            val username = bind.editTextUsername.text.toString()
            if(username != ""){
                //check database for duplicates
                if(database.searchForUser(username) == null){
                    //add to database
                    user = UserData(username)
                    database.addUser(user!!)
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
                if(username != "") {
                    //check database for user
                    if (database.searchForUser(username) != null) {
                        //get user from database
                        user = database.searchForUser(username)
                        pageRefresh()
                        Toast.makeText(applicationContext, "User Login", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(applicationContext, "User doesn't exists", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(applicationContext, "Enter Something", Toast.LENGTH_SHORT).show()
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
            bind.textViewUserData.text = user!!.toString()
            bind.buttonLogin.text = "Logout"
            bind.buttonCreate.visibility = View.INVISIBLE
            bind.editTextUsername.visibility = View.INVISIBLE
            returnIntent.putExtra("userData", user!!.name)
            setResult(Activity.RESULT_OK,returnIntent)
        } else {
            bind.textViewUserData.text = "No User Data"
            bind.buttonLogin.text = "Login"
            bind.buttonCreate.visibility = View.VISIBLE
            bind.editTextUsername.visibility = View.VISIBLE
            setResult(Activity.RESULT_CANCELED,returnIntent)
        }
    }

//    private fun tempCheckDatabase(user:String): UserData?{
//        var userData: UserData? = null
//        userList.forEach {
//             if(it.name == user)userData = it
//        }
//        return userData
//    }
}