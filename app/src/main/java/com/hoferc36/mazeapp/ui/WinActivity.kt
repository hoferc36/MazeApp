package com.hoferc36.mazeapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.hoferc36.mazeapp.DatabaseHelper
import com.hoferc36.mazeapp.databinding.ActivityWinBinding
import com.hoferc36.mazeapp.objects.*
import java.text.SimpleDateFormat

class WinActivity : AppCompatActivity() {
    private lateinit var bind: ActivityWinBinding
    private lateinit var database: DatabaseHelper

    private lateinit var boardData: BoardData
    private var user: UserData? = null

    private lateinit var buttonBack: Button
    private var dateForm = SimpleDateFormat("mm:ss.SSS")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityWinBinding.inflate(layoutInflater)
        setContentView(bind.win1)

        database = DatabaseHelper(applicationContext)

        boardData = intent.getSerializableExtra("boardData") as BoardData
        val username = intent.getStringExtra("previousUser")
        user = if(username != null) database.searchForUser(username) else null

        bind.textSeed.append(boardData.seed.toString())
        bind.textMissSteps.append(boardData.missSteps.toString())
        bind.textRevisited.append(boardData.revisited.toString())
        bind.textTimeTaken.append(dateForm.format(boardData.timeTaken))
        bind.textTotalMoves.append((boardData.totalMoves + boardData.missSteps).toString())

        if(user != null){
            bind.textUser.text = user!!.toString()
        }

        buttonBack = bind.buttonMenu
        buttonBack.setOnClickListener {
            finish()
        }
    }
}