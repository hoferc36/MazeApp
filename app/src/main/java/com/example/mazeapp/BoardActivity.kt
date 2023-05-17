package com.example.mazeapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.ImageView
import com.example.mazeapp.databinding.ActivityBoardBinding

class BoardActivity : AppCompatActivity() {
    private lateinit var bind: ActivityBoardBinding
    private lateinit var displayMetrics: DisplayMetrics

    private var rows = 5 //19
    private var cols = 5 //21 //10
    private lateinit var board: Array<Array<CellPieces>>
    private var cellSizeMin = 50
    private var cellSize = cellSizeMin
    private val margin = 8


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityBoardBinding.inflate(layoutInflater)
        setContentView(bind.board1)

        displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        rows = intent.getIntExtra("heightMaze", 5) // 2 .. 42
        cols = intent.getIntExtra("widthMaze", 5) // 2 .. 21

        board = Array(rows) {Array(cols) { CellPieces() } }

        cellCreation()

        val cell = CellPieces()
//        cell.top = false
//        cell.left = false
//        cell.right = false
//        cell.bottom = false
        cellOrientation(cell, bind.boardForeground.getChildAt(rows*cols/2))
//        bind.boardBackground.getChildAt(rows * cols / 2).setBackgroundResource(R.color.purple_2)
        cell.visited = true
        cell.here = true
        setCellBackgroundColor(cell, bind.boardBackground.getChildAt(rows*cols/2))
    }

    private fun cellCreation() {
        val height = (displayMetrics.heightPixels - margin *2) /rows
        val width = (displayMetrics.widthPixels - margin *2) /cols
        cellSize = if(width < height) width else height
        cellSize = if(cellSize < cellSizeMin) cellSizeMin else cellSize

        board.forEachIndexed{ rowIndex, row ->
            row.forEachIndexed{colIndex, col ->
                borderCellOrientation(rowIndex, colIndex, col)
                foregroundSetup(colIndex, rowIndex, col)
                backgroundSetup(colIndex, rowIndex, col)
            }
        }
    }

    private fun borderCellOrientation(rowIndex: Int, colIndex: Int, cell: CellPieces) {
        when (rowIndex) {
            0 -> { when (colIndex) {
                    0 -> {
                        cell.top = false
                        cell.left = false
                    }cols - 1 -> {
                        cell.top = false
                        cell.right = false
                    }else -> {
                        cell.top = false
                    }
                }
            }rows - 1 -> { when (colIndex) {
                    0 -> {
                        cell.left = false
                        cell.bottom = false
                    }cols - 1 -> {
                        cell.right = false
                        cell.bottom = false
                    }else -> {
                        cell.bottom = false
                    }
                }
            }else -> { when (colIndex) {
                    0 -> {
                        cell.left = false
                    }cols - 1 -> {
                        cell.right = false
                    }
                }
            }
        }
    }

    private fun foregroundSetup(colIndex: Int, rowIndex: Int, cell:CellPieces) {
        val cellForeground = ImageView(this)
        cellForeground.minimumHeight = cellSize
        cellForeground.minimumWidth = cellSize
        cellForeground.x = 1F * cellSize * colIndex + margin
        cellForeground.y = 1F * cellSize * rowIndex + margin
        cellOrientation(cell, cellForeground)
        bind.boardForeground.addView(cellForeground)
    }

    private fun cellOrientation(cell: CellPieces, cellForeground: View) {
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
                        cellForeground.setBackgroundResource(R.drawable.cell_1path_right)
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

    private fun backgroundSetup(colIndex: Int, rowIndex: Int, cell:CellPieces) {
        val cellBackground = ImageView(this)
        cellBackground.minimumHeight = cellSize
        cellBackground.minimumWidth = cellSize
        cellBackground.x = 1F * cellSize * colIndex + margin
        cellBackground.y = 1F * cellSize * rowIndex + margin
        setCellBackgroundColor(cell, cellBackground)
        bind.boardBackground.addView(cellBackground)
    }

    private fun setCellBackgroundColor(cell: CellPieces, cellBackground: View) {
        if (!cell.here) {
            if (cell.visited) {
                cellBackground.setBackgroundResource(R.color.light_orange)
            } else {
                cellBackground.setBackgroundResource(R.color.dark_orange)
            }
        } else {
            cellBackground.setBackgroundResource(R.color.green)
        }
    }

}