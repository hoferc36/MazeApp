package com.example.mazeapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.widget.ImageView
import com.example.mazeapp.databinding.ActivityBoardBinding
import java.util.Stack
import kotlin.random.Random

class BoardActivity : AppCompatActivity() {
    private lateinit var bind: ActivityBoardBinding
    private lateinit var displayMetrics: DisplayMetrics

    private var rows = 5 //19
    private var cols = 5 //21 //10
    private lateinit var board: Array<Array<CellPieces>>
    private var cellSizeMin = 50
    private var cellSize = cellSizeMin
    private val margin = 8

    private lateinit var stack: Stack<Pair<Int, Int>>
    private var visitedCellCount = 0
    private var hereCell = CellPieces()
    private var hereCoords = Pair(0,0)
    private var herePosi = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityBoardBinding.inflate(layoutInflater)
        setContentView(bind.board1)

        displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        rows = intent.getIntExtra("heightMaze", 5) // 2 .. 42
        cols = intent.getIntExtra("widthMaze", 5) // 2 .. 21

        board = Array(rows) { Array(cols) { CellPieces() } }

        cellCreation()

//        val cell = CellPieces()
//        cell.top = false
//        cell.left = false
//        cell.right = false
//        cell.bottom = false
//        cellOrientation(cell, bind.boardForeground.getChildAt(rows*cols/2))
//        cell.visited = true
//        cell.here = false
//        cell.dead = true
//        setCellBackgroundColor(cell, bind.boardBackground.getChildAt(rows*cols/2))

        stack = Stack<Pair<Int, Int>>()
        stack.push(hereCoords)
        visitedCellCount = 1

        setHereCell()

        for(i in 0..rows*cols*2) {
                Handler().postDelayed({
                    if (visitedCellCount < rows*cols) {
                        pathSelector(hereCoords.first, hereCoords.second)
                        hereCell.here = false
                        setCellBackgroundColor(hereCell, herePosi)
                        setHereCell()
                    }
                }, 100L * i)
        }
    }

    private fun setHereCell() {
        hereCoords = stack.peek()//hereCoord.first = row, hereCoord.second = col
        hereCell = board[hereCoords.first][hereCoords.second]
        herePosi = hereCoords.first * rows + hereCoords.second
        hereCell.here = true
        hereCell.visited = true
        setCellBackgroundColor(hereCell, herePosi)
    }

    private fun pathSelector(row:Int, col:Int) {
        val availablePaths = mutableListOf<PATH>()

        if (row > 0 && !board[row - 1][col].visited) {
            availablePaths.add(PATH.TOP)
        }
        if (row < rows-1 && !board[row + 1][col].visited) {
            availablePaths.add(PATH.BOTTOM)
        }
        if (col > 0 && !board[row][col - 1].visited) {
            availablePaths.add(PATH.LEFT)
        }
        if (col < cols-1 && !board[row][col + 1].visited) {
            availablePaths.add(PATH.RIGHT)
        }

        if (availablePaths.size > 0) {
            when (availablePaths[Random.nextInt(0, availablePaths.size)]) {
                PATH.TOP -> {
                    board[row][col].top = true
                    board[row - 1][col].bottom = true
                    stack.push(Pair(row - 1, col))
                    cellOrientation(board[row - 1][col], (row - 1) * rows + col)
                }
                PATH.LEFT -> {
                    board[row][col].left = true
                    board[row][col - 1].right = true
                    stack.push(Pair(row, col - 1))
                    cellOrientation(board[row][col - 1], row * rows + (col - 1))
                }
                PATH.RIGHT -> {
                    board[row][col].right = true
                    board[row][col + 1].left = true
                    stack.push(Pair(row, col + 1))
                    cellOrientation(board[row][col + 1], row * rows + (col + 1))
                }
                PATH.BOTTOM -> {
                    board[row][col].bottom = true
                    board[row + 1][col].top = true
                    stack.push(Pair(row + 1, col))
                    cellOrientation(board[row + 1][col], (row + 1) * rows + col)
                }
            }
            cellOrientation(board[row][col], row * rows + col)
            visitedCellCount++
        }
        else{
            if(!stack.empty()){
                stack.pop()
            }
        }
    }

    private fun cellCreation() {
        val height = (displayMetrics.heightPixels - margin * 2) / rows
        val width = (displayMetrics.widthPixels - margin * 2) / cols
        cellSize = if (width < height) width else height
        cellSize = if (cellSize < cellSizeMin) cellSizeMin else cellSize

        board.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { colIndex, col ->
//                borderCellOrientation(rowIndex, colIndex, col)
                backgroundSetup(colIndex, rowIndex, col)
                foregroundSetup(colIndex, rowIndex, col)
            }
        }
    }

    private fun foregroundSetup(colIndex: Int, rowIndex: Int, cell: CellPieces) {
        val cellForeground = ImageView(this)
        cellForeground.minimumHeight = cellSize
        cellForeground.minimumWidth = cellSize
        cellForeground.x = 1F * cellSize * colIndex + margin
        cellForeground.y = 1F * cellSize * rowIndex + margin
        bind.boardForeground.addView(cellForeground)
        cellOrientation(cell, rowIndex * rows + colIndex)
    }

    private fun cellOrientation(cell: CellPieces, cellPosition: Int) {
        var cellForeground = bind.boardForeground.getChildAt(cellPosition)
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
//        setCellBackgroundColor(cell, cellPosition)
    }

    private fun backgroundSetup(colIndex: Int, rowIndex: Int, cell: CellPieces) {
        val cellBackground = ImageView(this)
        cellBackground.minimumHeight = cellSize
        cellBackground.minimumWidth = cellSize
        cellBackground.x = 1F * cellSize * colIndex + margin
        cellBackground.y = 1F * cellSize * rowIndex + margin
        bind.boardBackground.addView(cellBackground)
        setCellBackgroundColor(cell, rowIndex * rows + colIndex)
    }

    private fun setCellBackgroundColor(cell: CellPieces, cellPosition: Int) {
        var cellBackground = bind.boardBackground.getChildAt(cellPosition)
        if (!cell.here) {
            if (cell.visited) {
                if (cell.dead) {
                    cellBackground.setBackgroundResource(R.color.red)
                } else {
                    cellBackground.setBackgroundResource(R.color.light_orange)
                }
            } else {
                cellBackground.setBackgroundResource(R.color.dark_orange)
            }
        } else {
            cellBackground.setBackgroundResource(R.color.green)
        }
    }

}