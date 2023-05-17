package com.example.mazeapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Im
import android.util.DisplayMetrics
import android.widget.ImageView
import android.widget.Toast
import com.example.mazeapp.databinding.ActivityBoardBinding

class BoardActivity : AppCompatActivity() {
    private lateinit var bind: ActivityBoardBinding
    private lateinit var displayMetrics: DisplayMetrics

    private var rows = 5 //19
    private var cols = 5 //21 //10
    private lateinit var board: Array<Array<Int>>
    private var cellSizeMin = 50
    private var cellSize = cellSizeMin
    private val margin = 8
    lateinit var boardCells: Array<CellPieces>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityBoardBinding.inflate(layoutInflater)
        setContentView(bind.board1)

        displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        rows = intent.getIntExtra("heightMaze", 5)
        cols = intent.getIntExtra("widthMaze", 5)

        board = Array(rows) { r ->
            Array(cols) { c ->
                (r * rows + c)
            }
        }
        cellCreation()

        bind.boardBackground.getChildAt(rows * cols / 2).setBackgroundResource(R.color.purple_2)
    }

    private fun cellCreation() {
        val height = (displayMetrics.heightPixels - margin *2) /rows
        val width = (displayMetrics.widthPixels - margin *2) /cols
        cellSize = if(width < height) width else height
        cellSize = if(cellSize < cellSizeMin) cellSizeMin else cellSize

        board.forEachIndexed{ rowIndex, row ->
            row.forEachIndexed{colIndex, col ->
                foregroundSetup(colIndex, rowIndex)
                backgroundSetup(colIndex, rowIndex)
            }
        }
    }

    private fun foregroundSetup(colIndex: Int, rowIndex: Int) {
        val cellForeground = ImageView(this)
        cellForeground.minimumHeight = cellSize
        cellForeground.minimumWidth = cellSize
        cellForeground.x = 1F * cellSize * colIndex + margin
        cellForeground.y = 1F * cellSize * rowIndex + margin
        when (rowIndex) {
            0 -> {
                when (colIndex) {
                    0 -> {
                        cellForeground.setBackgroundResource(R.drawable.cell_2path_right_bottom)
                    }

                    cols - 1 -> {
                        cellForeground.setBackgroundResource(R.drawable.cell_2path_left_bottom)
                    }

                    else -> {
                        cellForeground.setBackgroundResource(R.drawable.cell_3path_notop)
                    }
                }
            }

            rows - 1 -> {
                when (colIndex) {
                    0 -> {
                        cellForeground.setBackgroundResource(R.drawable.cell_2path_top_right)
                    }

                    cols - 1 -> {
                        cellForeground.setBackgroundResource(R.drawable.cell_2path_top_left)
                    }

                    else -> {
                        cellForeground.setBackgroundResource(R.drawable.cell_3path_nobottom)
                    }
                }
            }

            else -> {
                when (colIndex) {
                    0 -> {
                        cellForeground.setBackgroundResource(R.drawable.cell_3path_noleft)
                    }

                    cols - 1 -> {
                        cellForeground.setBackgroundResource(R.drawable.cell_3path_noright)
                    }

                    else -> {
                        cellForeground.setBackgroundResource(R.drawable.cell_4path)
                    }
                }
            }
        }
        bind.boardForeground.addView(cellForeground)
    }

    private fun backgroundSetup(colIndex: Int, rowIndex: Int) {
        val cellBackground = ImageView(this)
        cellBackground.minimumHeight = cellSize
        cellBackground.minimumWidth = cellSize
        cellBackground.x = 1F * cellSize * colIndex + margin
        cellBackground.y = 1F * cellSize * rowIndex + margin
        if (rowIndex.mod(2) == 1) {
            if (colIndex.mod(2) == 1) {
                cellBackground.setBackgroundResource(R.color.blank_cell)
            } else {
                cellBackground.setBackgroundResource(R.color.orange)
            }
        } else {
            if (colIndex.mod(2) == 1) {
                cellBackground.setBackgroundResource(R.color.dark_orange)
            } else {
                cellBackground.setBackgroundResource(R.color.light_orange)
            }
        }
        bind.boardBackground.addView(cellBackground)
    }

}

class CellPieces {

}
