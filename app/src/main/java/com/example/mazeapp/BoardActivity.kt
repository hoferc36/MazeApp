package com.example.mazeapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.view.GestureDetectorCompat
import com.example.mazeapp.databinding.ActivityBoardBinding
import java.util.Stack
import kotlin.math.abs
import kotlin.random.Random

class BoardActivity : AppCompatActivity() {
    private lateinit var bind: ActivityBoardBinding
    private lateinit var displayMetrics: DisplayMetrics
    private lateinit var gestureDetector: GestureDetectorCompat

    private var cellSize = 5
    private val marginDP = 8

    private lateinit var boardMaze: BoardMaze

    private var isButtonOn: Boolean = false
    private lateinit var upButton: Button
    private lateinit var leftButton: Button
    private lateinit var rightButton: Button
    private lateinit var downButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityBoardBinding.inflate(layoutInflater)
        setContentView(bind.board1)

        boardSetUp()
        buttonSetUp()
        cellCreation()
        gestureDetector = GestureDetectorCompat(this, GestureListener1())
    }

    private fun boardSetUp() {
        boardMaze = BoardMaze(
            intent.getIntExtra("heightMaze", 5),
            intent.getIntExtra("widthMaze", 5), this
        )
        boardMaze.startCellCoords = Pair(
            intent.getIntExtra("startCoordX", 0),
            intent.getIntExtra("startCoordY", 0)
        )
        boardMaze.endCellCoords = Pair(
            intent.getIntExtra("endCoordX", boardMaze.rows - 1),
            intent.getIntExtra("endCoordY", boardMaze.cols - 1)
        )
    }

    private fun buttonSetUp() {
        isButtonOn = intent.getBooleanExtra("isButtonOn", false)

        upButton = bind.buttonUp
        upButton.visibility = if (isButtonOn) View.VISIBLE else View.GONE
        upButton.setOnClickListener {
            boardMaze.moveUp()
        }

        leftButton = bind.buttonLeft
        leftButton.visibility = if (isButtonOn) View.VISIBLE else View.GONE
        leftButton.setOnClickListener {
            boardMaze.moveLeft()
        }

        rightButton = bind.buttonRight
        rightButton.visibility = if (isButtonOn) View.VISIBLE else View.GONE
        rightButton.setOnClickListener {
            boardMaze.moveRight()
        }

        downButton = bind.buttonDown
        downButton.visibility = if (isButtonOn) View.VISIBLE else View.INVISIBLE
        downButton.setOnClickListener {
            boardMaze.moveDown()
        }
    }

    fun endGame(){
        Toast.makeText(applicationContext, "You WIN", Toast.LENGTH_SHORT).show()
        finish()
    }

    fun boardRefresh() {
        boardMaze.board.forEach { row ->
            row.forEach { cell ->
                setCellBackgroundColor(cell)
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
        displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

//        val dpi = displayMetrics.densityDpi
        val marginP = marginDP * (displayMetrics.densityDpi/160)

//        val heightDP = displayMetrics.heightPixels * 160 / dpi
//        val heightP = displayMetrics.heightPixels
        var heightButtons = 0
        if(isButtonOn) {
            heightButtons = 40 * (displayMetrics.densityDpi / 160)
        }
        val heightRow = (displayMetrics.heightPixels - marginP*2 - heightButtons*3) /boardMaze.rows
        val widthCol = (displayMetrics.widthPixels - marginP*2) /boardMaze.cols
        cellSize = if (widthCol < heightRow) widthCol else heightRow
        //centre maze
        val marginPwidth =  (displayMetrics.widthPixels - cellSize*boardMaze.cols)/2

//        Log.d("chandra", "mdp:$marginDP mp:$marginP w:$displayMetrics.widthPixels cw:$cellSize")//maxWidth

        boardMaze.board.forEach {row ->
            row.forEach {cell ->
                val cellBackground = ImageView(this)
                cellBackground.minimumHeight = cellSize
                cellBackground.minimumWidth = cellSize
                cellBackground.y = 1F * cellSize * cell.coords.first + marginP
                cellBackground.x = 1F * cellSize * cell.coords.second + marginPwidth
                bind.boardBackground.addView(cellBackground)
                setCellBackgroundColor(cell)

                val cellForeground = ImageView(this)
                cellForeground.minimumHeight = cellSize
                cellForeground.minimumWidth = cellSize
                cellForeground.y = 1F * cellSize * cell.coords.first + marginP
                cellForeground.x = 1F * cellSize * cell.coords.second + marginPwidth
                bind.boardForeground.addView(cellForeground)
                cellOrientation(cell)
            }
        }
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
        if(cell.here) {
            cellBackground.setBackgroundResource(R.color.here_cell)
        }
        else {
            if(cell.start){
                cellBackground.setBackgroundResource(R.color.start_cell)
            }else if(cell.end){
                cellBackground.setBackgroundResource(R.color.end_cell)
            }else if(cell.dead){
                cellBackground.setBackgroundResource(R.color.dead_cell)
            }else if(cell.visited){
                cellBackground.setBackgroundResource(R.color.visited_cell)
            }else{
                cellBackground.setBackgroundResource(R.color.unvisited_cell)
            }
        }

    }

    inner class GestureListener1 : GestureDetector.SimpleOnGestureListener(){
        private val SWIPE_THRESHOLD: Int = 100
        private val SWIPE_VELOCITY_THRESHOLD: Int = 100

        override fun onFling(downEvent: MotionEvent, moveEvent: MotionEvent,
                             velocityX: Float, velocityY: Float): Boolean {

            val diffX = moveEvent?.x?.minus(downEvent!!.x) ?: 0.0F
            val diffY = moveEvent?.y?.minus(downEvent!!.y) ?: 0.0F

            return if (abs(diffX) > abs(diffY)) {
                // this is a left or right swipe
                if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
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
                if (abs(diffY) > SWIPE_THRESHOLD && abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
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