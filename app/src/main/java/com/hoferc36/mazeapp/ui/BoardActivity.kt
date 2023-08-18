package com.hoferc36.mazeapp.ui

import android.content.ClipData
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

    private lateinit var boardMaze: BoardMaze

    private var isEndGame: Boolean = false

    private var cellSize = 0
    private var marginPixelWidth = 0
    private var marginPixelHeight = 0
    private lateinit var player: ImageView
    private lateinit var garage: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityBoardBinding.inflate(layoutInflater)
        setContentView(bind.board1)

        database = DatabaseHelper(applicationContext)
        database.savesLookUp()

        settings = database.settingsSearch(database.saves1.settingsId)

        user = if(database.saves1.user != "NULL") {
            database.userSearch(database.saves1.user)
        } else null

        garage = bind.garageView
        player = bind.playerView
        boardMaze = BoardMaze(settings, this)

        buttonSetUp()
        cellCreation()
        gestureDetector = GestureDetectorCompat(this, GestureListener1())

        player.setOnTouchListener { view, motionEvent ->
            if(motionEvent.action == MotionEvent.ACTION_MOVE) {
                val data = ClipData.newPlainText("", "")
                val shadow = View.DragShadowBuilder(view)
                view.startDragAndDrop(data, shadow, view, 0)
//                true
            }
            false
        }

        garage.setOnDragListener(DragListener1())
    }

    inner class DragListener1: View.OnDragListener{
        override fun onDrag(view: View?, dragEvent: DragEvent?): Boolean {
            when(dragEvent!!.action){
                DragEvent.ACTION_DRAG_ENTERED->{
                    val row = ((view!!.y - marginPixelHeight) / cellSize).toInt()
                    val col = ((view.x - marginPixelWidth) / cellSize).toInt()
                    boardMaze.dragToNextPath(row, col)
                }
                DragEvent.ACTION_DRAG_EXITED->{
//                    Toast.makeText(applicationContext, "Player exiting ${view!!.contentDescription}", Toast.LENGTH_SHORT).show()
                }
                DragEvent.ACTION_DROP->{
//                    Toast.makeText(applicationContext, "Player dropped ${view!!.contentDescription}", Toast.LENGTH_SHORT).show()
                }
                DragEvent.ACTION_DRAG_LOCATION->{
//                    Toast.makeText(applicationContext, "Player location ${view.toString()}", Toast.LENGTH_SHORT).show()
                }
            }
            return true
        }

    }

    //TODO add a onResume and onPause overrides to track time taken better

    fun endGame(){
        isEndGame = true
        if (user != null ) {
            user!!.wins++
            database.userUpdate(user!!.name, user!!)
        }
        //TODO add device wins
        database.savesUpdateWins(database.saves1.wins++)

        val intent = Intent(this, WinActivity::class.java)
        intent.putExtra("boardData", boardMaze.boardData)
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
                setMessage("Do you want to return to leave map?")
                setPositiveButton("Yes") { _, _ -> super.finish() }
                setNegativeButton("No"){ dialog, _ -> dialog.dismiss() }
                show()
            }
        }
    }

    fun moveCharacter(row: Int, col:Int){
        player.y = 1F * cellSize * row + marginPixelHeight
        player.x = 1F * cellSize * col + marginPixelWidth
    }

    fun boardRefresh() {
        boardMaze.board.forEach { row ->
            row.forEach { cell ->
                setCellBackgroundColor(cell)
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

    private fun cellCreation() {
        val displayMetrics = Resources.getSystem().displayMetrics
        val marginDP = 8
        val marginPixel = marginDP * (displayMetrics.densityDpi/160)

        val heightButtons = if(settings.buttonToggle) 70 * (displayMetrics.densityDpi/ 160) else 0

        val heightRow = (displayMetrics.heightPixels - marginPixel*2 - heightButtons*3) / settings.rows
        val widthCol = (displayMetrics.widthPixels - marginPixel*2) / settings.cols
        cellSize = if (widthCol < heightRow) widthCol else heightRow
        cellSize = if(cellSize<25) 25 else cellSize

        //centre maze
        marginPixelWidth =  (displayMetrics.widthPixels - cellSize*settings.cols)/2
        marginPixelHeight =  (displayMetrics.heightPixels - heightButtons*3 - cellSize*settings.rows)/2
//        Log.d("chandra", "mdp:$marginDP mp:$marginP w:$displayMetrics.widthPixels cw:$cellSize")//maxWidth

        boardMaze.board.forEach {row ->
            row.forEach {cell ->
                val cellBackground = ImageView(this)
                cellBackground.minimumHeight = cellSize
                cellBackground.minimumWidth = cellSize
                cellBackground.y = 1F * cellSize * cell.coord.first + marginPixelHeight
                cellBackground.x = 1F * cellSize * cell.coord.second + marginPixelWidth
                bind.boardBackground.addView(cellBackground)

                val cellForeground = ImageView(this)
                cellForeground.minimumHeight = cellSize
                cellForeground.minimumWidth = cellSize
                cellForeground.contentDescription = "r:${cell.coord.first} c:${cell.coord.second}"//TODO check if needed
                cellForeground.y = 1F * cellSize * cell.coord.first + marginPixelHeight
                cellForeground.x = 1F * cellSize * cell.coord.second + marginPixelWidth
                bind.boardForeground.addView(cellForeground)
                cellOrientation(cell)
                cellForeground.setOnDragListener(DragListener1())
            }
        }

        garage.minimumHeight = cellSize
        garage.minimumWidth = cellSize
        garage.contentDescription = "garage"//TODO check if needed
        garage.y = 1F * cellSize * settings.endY + marginPixelHeight
        garage.x = 1F * cellSize * settings.endX + marginPixelWidth

        player.minimumHeight = cellSize
        player.minimumWidth = cellSize
        player.y = 1F * cellSize * settings.startY + marginPixelHeight
        player.x = 1F * cellSize * settings.startX + marginPixelWidth

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

    private fun buttonSetUp() {
        val upButton = bind.buttonUp
        val leftButton = bind.buttonLeft
        val rightButton = bind.buttonRight
        val downButton = bind.buttonDown

        if (settings.buttonToggle) {
            upButton.visibility = View.VISIBLE
            upButton.setOnClickListener { boardMaze.moveUp() }

            leftButton.visibility = View.VISIBLE
            leftButton.setOnClickListener { boardMaze.moveLeft() }

            rightButton.visibility = View.VISIBLE
            rightButton.setOnClickListener { boardMaze.moveRight() }

            downButton.visibility = View.VISIBLE
            downButton.setOnClickListener { boardMaze.moveDown() }
        } else {
            upButton.visibility = View.GONE
            leftButton.visibility = View.GONE
            rightButton.visibility = View.GONE
            downButton.visibility = View.GONE
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (gestureDetector.onTouchEvent(event)) {
            true
        } else {
            super.onTouchEvent(event)
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
