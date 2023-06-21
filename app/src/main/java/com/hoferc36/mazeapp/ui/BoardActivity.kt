package com.hoferc36.mazeapp.ui

import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GestureDetectorCompat
import com.hoferc36.mazeapp.DatabaseHelper
import com.hoferc36.mazeapp.logic.*
import com.hoferc36.mazeapp.objects.*
import com.hoferc36.mazeapp.R
import com.hoferc36.mazeapp.databinding.ActivityBoardBinding
import kotlin.math.abs

class BoardActivity : AppCompatActivity() {
    private lateinit var bind: ActivityBoardBinding
    private lateinit var gestureDetector: GestureDetectorCompat
    private lateinit var database: DatabaseHelper

    private lateinit var settings: SettingsData
    private var user:UserData? = null

    private var cellSize = 5
    private val marginDP = 8

    private lateinit var boardMaze: BoardMaze

    private var isButtonOn: Boolean = false
    private lateinit var upButton: Button
    private lateinit var leftButton: Button
    private lateinit var rightButton: Button
    private lateinit var downButton: Button

    private var isEndGame: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityBoardBinding.inflate(layoutInflater)
        setContentView(bind.board1)

        database = DatabaseHelper(applicationContext)

        val settingId = intent.getIntExtra("mazeSettings", 1)
        settings  = if(database.searchForSettings(settingId) != null) database.searchForSettings(settingId)!! else SettingsData()

        val username = intent.getStringExtra("previousUser")
        user = if(username != null) database.searchForUser(username) else null

        boardMaze = BoardMaze(settings, this)
        boardMaze.startCellCoord = Pair(settings.startX, settings.startY)
        boardMaze.endCellCoord = Pair(settings.endX,settings.endY)

        buttonSetUp()
        cellCreation()
        gestureDetector = GestureDetectorCompat(this, GestureListener1())

        currentFocus?.clearFocus()
    }

    private fun buttonSetUp() {
        isButtonOn = settings.buttonToggle

        upButton = bind.buttonUp
        if (isButtonOn) {
            upButton.visibility = View.VISIBLE
            upButton.setOnClickListener {
                boardMaze.moveUp()
            }
        } else upButton.visibility = View.GONE


        leftButton = bind.buttonLeft
        if (isButtonOn) {
            leftButton.visibility = View.VISIBLE
            leftButton.setOnClickListener {
                boardMaze.moveLeft()
            }
        }else leftButton.visibility = View.GONE


        rightButton = bind.buttonRight
        if (isButtonOn) {
            rightButton.visibility = View.VISIBLE
            rightButton.setOnClickListener {
                boardMaze.moveRight()
            }
        } else rightButton.visibility = View.GONE


        downButton = bind.buttonDown
        if (isButtonOn){
            downButton.visibility = View.VISIBLE
            downButton.setOnClickListener {
                boardMaze.moveDown()
            }
        } else downButton.visibility = View.GONE

    }

    fun endGame(){
        isEndGame = true
        if (user != null ) {
            user!!.wins++
            database.updateUser(user!!.name, user!!)
        }

        val intent = Intent(this, WinActivity::class.java)
        intent.putExtra("boardData", boardMaze.boardData)
        if(user != null) {
            intent.putExtra("previousUser", user!!.name)
        }
        startActivity(intent)
        finish()
    }

    override fun finish() {
        if(isEndGame){
            super.finish()
        }else{
            val builder = AlertDialog.Builder(this)
            with(builder) {
                setTitle("Warning")
                setMessage("Do you want to quit?")
                setPositiveButton("Yes") { dialog, which -> super.finish() }
                setNegativeButton("No"){ dialog, which -> dialog.dismiss() }
                show()
            }
        }
    }

    fun boardRefresh() {
        var cellPayer: View
        boardMaze.board.forEach { row ->
            row.forEach { cell ->
                setCellBackgroundColor(cell)
                cellPayer = bind.boardPlayerGround.getChildAt(cell.position)
                if(cell.here) {
                    cellPayer.setBackgroundResource(R.drawable.character3)
                }else if(cell.end){
                    cellPayer.setBackgroundResource(R.drawable.character3_garage)
                }else{
                    cellPayer.setBackgroundResource(R.color.transparent)
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP -> {
                boardMaze.moveUp()
                true
            }KeyEvent.KEYCODE_DPAD_LEFT -> {
                boardMaze.moveLeft()
                true
            }KeyEvent.KEYCODE_DPAD_RIGHT -> {
                boardMaze.moveRight()
                true
            }KeyEvent.KEYCODE_DPAD_DOWN -> {
                boardMaze.moveDown()
                true
            }else -> { super.onKeyDown(keyCode, event)}
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (gestureDetector.onTouchEvent(event)) {
            true
        } else {
            super.onTouchEvent(event)
        }
    }

    private fun cellCreation() {
        val displayMetrics = Resources.getSystem().displayMetrics

        val marginPixel = marginDP * (displayMetrics.densityDpi/160)

        var heightButtons = 0
        if(isButtonOn) {
            heightButtons = 70 * (displayMetrics.densityDpi/ 160)
        }
        val heightRow = (displayMetrics.heightPixels - marginPixel*2 - heightButtons*3) /boardMaze.rows
        val widthCol = (displayMetrics.widthPixels - marginPixel*2) /boardMaze.cols
        cellSize = if (widthCol < heightRow) widthCol else heightRow
        cellSize = if(cellSize<25) 25 else cellSize
        //centre maze
        val marginPixelWidth =  (displayMetrics.widthPixels - cellSize*boardMaze.cols)/2
//        Log.d("chandra", "mdp:$marginDP mp:$marginP w:$displayMetrics.widthPixels cw:$cellSize")//maxWidth

        boardMaze.board.forEach {row ->
            row.forEach {cell ->
                val cellBackground = ImageView(this)
                cellBackground.minimumHeight = cellSize
                cellBackground.minimumWidth = cellSize
                cellBackground.y = 1F * cellSize * cell.coord.first + marginPixel
                cellBackground.x = 1F * cellSize * cell.coord.second + marginPixelWidth
                bind.boardBackground.addView(cellBackground)
                setCellBackgroundColor(cell)

                val cellForeground = ImageView(this)
                cellForeground.minimumHeight = cellSize
                cellForeground.minimumWidth = cellSize
                cellForeground.y = 1F * cellSize * cell.coord.first + marginPixel
                cellForeground.x = 1F * cellSize * cell.coord.second + marginPixelWidth
                bind.boardForeground.addView(cellForeground)
                cellOrientation(cell)

                val cellPlayerGround = ImageView(this)
                cellPlayerGround.minimumHeight = cellSize
                cellPlayerGround.minimumWidth = cellSize
                cellPlayerGround.y = 1F * cellSize * cell.coord.first + marginPixel
                cellPlayerGround.x = 1F * cellSize * cell.coord.second + marginPixelWidth
                bind.boardPlayerGround.addView(cellPlayerGround)
                cellPlayerGround.setBackgroundResource(R.color.transparent)
            }
        }
        boardRefresh()
    }

    private fun cellOrientation(cell: CellPieces) {
        val cellForeground = bind.boardForeground.getChildAt(cell.position)
        if (cell.top) {
            if (cell.left) {
                if (cell.right) {
                    if (cell.bottom) {
                        cellForeground.setBackgroundResource(R.drawable.cell_4path)
                    } else {
                        cellForeground.setBackgroundResource(R.drawable.cell_3path_nobottom)
                    }
                } else {
                    if (cell.bottom) {
                        cellForeground.setBackgroundResource(R.drawable.cell_3path_noright)
                    } else {
                        cellForeground.setBackgroundResource(R.drawable.cell_2path_top_left)
                    }
                }
            } else {
                if (cell.right) {
                    if (cell.bottom) {
                        cellForeground.setBackgroundResource(R.drawable.cell_3path_noleft)
                    } else {
                        cellForeground.setBackgroundResource(R.drawable.cell_2path_top_right)
                    }
                } else {
                    if (cell.bottom) {
                        cellForeground.setBackgroundResource(R.drawable.cell_2path_top_bottom)
                    } else {
                        cellForeground.setBackgroundResource(R.drawable.cell_1path_top)
                    }
                }
            }
        } else {
            if (cell.left) {
                if (cell.right) {
                    if (cell.bottom) {
                        cellForeground.setBackgroundResource(R.drawable.cell_3path_notop)
                    } else {
                        cellForeground.setBackgroundResource(R.drawable.cell_2path_left_right)
                    }
                } else {
                    if (cell.bottom) {
                        cellForeground.setBackgroundResource(R.drawable.cell_2path_left_bottom)
                    } else {
                        cellForeground.setBackgroundResource(R.drawable.cell_1path_left)
                    }
                }
            } else {
                if (cell.right) {
                    if (cell.bottom) {
                        cellForeground.setBackgroundResource(R.drawable.cell_2path_right_bottom)
                    } else {
                        cellForeground.setBackgroundResource(R.drawable.cell_1path_right)
                    }
                } else {
                    if (cell.bottom) {
                        cellForeground.setBackgroundResource(R.drawable.cell_1path_bottom)
                    } else {
                        cellForeground.setBackgroundResource(R.drawable.cell_0path)
                    }
                }
            }
        }
    }

    private fun setCellBackgroundColor(cell: CellPieces) {
        val cellBackground = bind.boardBackground.getChildAt(cell.position)
        if(cell.end){
            cellBackground.setBackgroundResource(R.color.end_cell)
        }else if(cell.start){
            cellBackground.setBackgroundResource(R.color.start_cell)
        }else if(cell.visited){
            cellBackground.setBackgroundResource(R.color.visited_cell)
        }else{
            cellBackground.setBackgroundResource(R.color.unvisited_cell)
        }

    }

    inner class GestureListener1 : GestureDetector.SimpleOnGestureListener(){
        private val swipeThreshold: Int = 100
        private val swipeVelocityThreshold: Int = 100

        override fun onFling(downEvent: MotionEvent, moveEvent: MotionEvent,
                             velocityX: Float, velocityY: Float): Boolean {

            val diffX = moveEvent.x.minus(downEvent.x)
            val diffY = moveEvent.y.minus(downEvent.y)

            return if (abs(diffX) > abs(diffY)) {
                // this is a left or right swipe
                if (abs(diffX) > swipeThreshold && abs(velocityX) > swipeVelocityThreshold) {
                    if (diffX > 0 ) {
                        boardMaze.moveRight()
                    } else {
                        boardMaze.moveLeft()
                    }
                    true
                } else  {
                    super.onFling(downEvent, moveEvent, velocityX, velocityY)
                }
            } else {
                // this is either a bottom or top swipe.
                if (abs(diffY) > swipeThreshold && abs(velocityY) > swipeVelocityThreshold) {
                    if (diffY > 0) {
                        boardMaze.moveDown()
                    } else {
                        boardMaze.moveUp()
                    }
                    true
                } else {
                    super.onFling(downEvent, moveEvent, velocityX, velocityY)
                }
            }
        }
    }

}