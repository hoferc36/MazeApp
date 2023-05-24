package com.example.mazeapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.widget.ImageView
import com.example.mazeapp.databinding.ActivityBoardBinding
import java.util.Stack
import kotlin.random.Random

class BoardActivity : AppCompatActivity() {
    private lateinit var bind: ActivityBoardBinding
    private lateinit var displayMetrics: DisplayMetrics

    private var cellSize = 5
    private val margin = 8
    private var rows = 2
    private var cols = 2
    private lateinit var board: Array<Array<CellPieces>>
    private lateinit var stack: Stack<Pair<Int, Int>>
    private var visitedCellCount = 0

    private val startCellCoords = Pair(0,0)
    private var endCellCoords = Pair(0,0)

    private var hereCell = CellPieces()
    private var hereCoords = startCellCoords

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityBoardBinding.inflate(layoutInflater)
        setContentView(bind.board1)

        cellCreation()
        boardCreation()
    }

    private fun boardCreation() {
        //Start and end points set
        board[startCellCoords.first][startCellCoords.second].start = true
        endCellCoords = Pair(rows - 1, cols - 1)
        board[endCellCoords.first][endCellCoords.second].end = true

        //stack initialization
        stack = Stack<Pair<Int, Int>>()
        stack.push(hereCoords)
        visitedCellCount = 1

        //current position in the maze
        hereCoords = stack.peek()//hereCoord.first = row, hereCoord.second = col
        hereCell = board[hereCoords.first][hereCoords.second]
        hereCell.here = true
        hereCell.visited = true
        setCellBackgroundColor(hereCell)

        //make a maze path until visited count == maze size
        while (visitedCellCount < rows * cols) {
            //selects next cell
            pathSelector(hereCoords.first, hereCoords.second)
            hereCell.here = false
            setCellBackgroundColor(hereCell)
            hereCoords = stack.peek()//hereCoord.first = row, hereCoord.second = col
            hereCell = board[hereCoords.first][hereCoords.second]
            hereCell.here = true
            hereCell.visited = true
            setCellBackgroundColor(hereCell)
        }
        //return to start
        hereCell.here = false
        hereCoords = startCellCoords
        hereCell = board[hereCoords.first][hereCoords.second]
        hereCell.here = true

        //reset all visited cells to unvisited
        board.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { colIndex, cell ->
                cell.visited = false
                setCellBackgroundColor(cell)
            }
        }
    }

    private fun pathSelector(row:Int, col:Int) {
        val availablePaths = mutableListOf<PATH>()

        //check in the path is to the border of the cell is visited
        //add it to a list to randomly chose
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

        //path selector or to pop of stack to find a new path
        if (availablePaths.size > 0) {
            when (availablePaths[Random.nextInt(0, availablePaths.size)]) {
                PATH.TOP -> {
                    board[row][col].top = true
                    board[row - 1][col].bottom = true
                    stack.push(Pair(row - 1, col))
                    cellOrientation(board[row - 1][col])
                }
                PATH.LEFT -> {
                    board[row][col].left = true
                    board[row][col - 1].right = true
                    stack.push(Pair(row, col - 1))
                    cellOrientation(board[row][col - 1])
                }
                PATH.RIGHT -> {
                    board[row][col].right = true
                    board[row][col + 1].left = true
                    stack.push(Pair(row, col + 1))
                    cellOrientation(board[row][col + 1])
                }
                PATH.BOTTOM -> {
                    board[row][col].bottom = true
                    board[row + 1][col].top = true
                    stack.push(Pair(row + 1, col))
                    cellOrientation(board[row + 1][col])
                }
            }
            cellOrientation(board[row][col])
            visitedCellCount++
        }else{
            if(!stack.empty()){
                stack.pop()
            }
        }
    }

    private fun cellCreation() {
        //get the size of the screen
        displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        //get input value from user
        rows = intent.getIntExtra("heightMaze", 5) // 2 .. 42
        cols = intent.getIntExtra("widthMaze", 5) // 2 .. 21

        //initialize maze array
        board = Array(rows) { Array(cols) { CellPieces() } }

        //set the size of cells, cells are square
        var height = (displayMetrics.heightPixels - 100)/rows
        height = if(height < 10) 10 else height
        var width = (displayMetrics.widthPixels - 100) /cols
        width = if (width < 10) 10 else width
        cellSize = if (width < height) width else height

        Log.d("chandra", "h:"+ height+" w:"+ width+" c:"+ cellSize)//maxWidth

        //creates view for foreground(borders) and view background color for each cell
        board.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { colIndex, cell ->
                cell.position = rowIndex * cols + colIndex
                cell.coords = Pair(rowIndex, colIndex)

                val cellBackground = ImageView(this)
                cellBackground.minimumHeight = cellSize
                cellBackground.minimumWidth = cellSize
                cellBackground.y = 1F * cellSize * cell.coords.first + margin
                cellBackground.x = 1F * cellSize * cell.coords.second + margin
                bind.boardBackground.addView(cellBackground)
                setCellBackgroundColor(cell)

                val cellForeground = ImageView(this)
                cellForeground.minimumHeight = cellSize
                cellForeground.minimumWidth = cellSize
                cellForeground.y = 1F * cellSize * cell.coords.first + margin
                cellForeground.x = 1F * cellSize * cell.coords.second + margin
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

}