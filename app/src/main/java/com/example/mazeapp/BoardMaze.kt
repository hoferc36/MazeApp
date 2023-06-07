package com.example.mazeapp

import java.util.Stack
import kotlin.math.abs
import kotlin.random.Random

class BoardMaze (val rows:Int = 2,
                 val cols:Int = 2,
                 private val activity: BoardActivity,
                 seed:Int = 0) {
    val board: Array<Array<CellPieces>> = Array(rows) { Array(cols) { CellPieces() } }

    private val stack: Stack<Pair<Int, Int>> = Stack<Pair<Int, Int>>()
    private var visitedCellCount = 0

    private var hereCoord = Pair(0,0)
    private var hereCell = CellPieces()

    private var currentSeed = 2029L + rows - cols

    var startCellCoord: Pair<Int,Int> = Pair(0,0)
        set(value) {
            if(value.first in 0 until rows && value.second in 0 until cols) {
                board[startCellCoord.first][startCellCoord.second].start = false
                hereCell.here = false
                hereCell.visited = false
                field = value
                hereCoord = value
                hereCell = board[hereCoord.first][hereCoord.second]
                hereCell.start = true
                hereCell.here = true
                hereCell.visited = true
            }
        }
    var endCellCoord = Pair(0, 0)
        set(value) {
            if(value.first in 0 until rows && value.second in 0 until cols) {
                board[endCellCoord.first][endCellCoord.second].end = false
                field = value
                board[value.first][value.second].end = true
            }

        }

    init {
        currentSeed *= if (seed <= 0) Random.nextInt(1, 3000) else seed
        boardSetUp()
        pathSetUp()

        hereCoord = Pair(0,0)
        hereCell = board[hereCoord.first][hereCoord.second]
        hereCell.here = true
        hereCell.visited = true
    }

    private fun pathSetUp() {
        stack.push(hereCoord)
        visitedCellCount = 1
        while (visitedCellCount < rows * cols) {
            pathSelector(hereCoord.first, hereCoord.second)
            hereCell.here = false
            hereCoord = stack.peek()//hereCoord.first = row, hereCoord.second = col
            hereCell = board[hereCoord.first][hereCoord.second]
            hereCell.here = true
            hereCell.visited = true
        }
        hereCell.here = false

        board.forEach { row ->
            row.forEach { cell ->
                cell.visited = false
            }
        }
    }

    private fun boardSetUp() {
        board.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { colIndex, cell ->
                cell.position = rowIndex * cols + colIndex
                cell.coord = Pair(rowIndex, colIndex)
            }

        }
        hereCell = board[startCellCoord.first][startCellCoord.second]
        hereCell.start = true
        hereCell.here = true
        hereCell.visited = true

        endCellCoord = Pair(rows - 1, cols - 1)
        board[endCellCoord.first][endCellCoord.second].end = true
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

        currentSeed += Random(currentSeed).nextInt(1, 3000)
        currentSeed = if(currentSeed == 0L) 2029L else abs(currentSeed)

        if (availablePaths.size > 0) {
            when (availablePaths[Random(currentSeed).nextInt(0, availablePaths.size)]) {
                PATH.TOP -> {
                    board[row][col].top = true
                    board[row - 1][col].bottom = true
                    stack.push(Pair(row - 1, col))
                }
                PATH.LEFT -> {
                    board[row][col].left = true
                    board[row][col - 1].right = true
                    stack.push(Pair(row, col - 1))
                }
                PATH.RIGHT -> {
                    board[row][col].right = true
                    board[row][col + 1].left = true
                    stack.push(Pair(row, col + 1))
                }
                PATH.BOTTOM -> {
                    board[row][col].bottom = true
                    board[row + 1][col].top = true
                    stack.push(Pair(row + 1, col))
                }
            }
//            Log.d("chandra", "seed: ${Random.nextInt(0, availablePaths.size)}" +
//                    " random number ${Random(currentSeed).nextInt(0, availablePaths.size)}")

            visitedCellCount++
        }else{
            if(!stack.empty()){
                stack.pop()
            }
        }
    }
    fun moveUp(){
        if (hereCell.top) {
            moveCharacter(hereCoord.first-1, hereCoord.second)
            if(hereCell.top && !hereCell.left && !hereCell.right){
                moveUp()
            }
            else {
                activity.boardRefresh()
            }
        }
    }
    fun moveLeft(){
        if(hereCell.left){
            moveCharacter(hereCoord.first, hereCoord.second-1)
            if(!hereCell.top && hereCell.left && !hereCell.bottom){
                moveLeft()
            }
            else {
                activity.boardRefresh()
            }
        }
    }
    fun moveRight(){
        if(hereCell.right){
            moveCharacter(hereCoord.first, hereCoord.second+1)
            if(!hereCell.top && hereCell.right && !hereCell.bottom){
                moveRight()
            }
            else {
                activity.boardRefresh()
            }
        }
    }

    fun moveDown(){
        if(hereCell.bottom){
            moveCharacter(hereCoord.first+1, hereCoord.second)
            if(!hereCell.left && !hereCell.right && hereCell.bottom){
                moveDown()
            }
            else {
                activity.boardRefresh()
            }
        }
    }
    private fun moveCharacter(newRow:Int, newCol:Int){
        hereCell.here = false
        hereCoord = Pair(newRow, newCol)
        hereCell = board[hereCoord.first][hereCoord.second]
        hereCell.here = true
        hereCell.visited = true
        if (hereCell.end) {
            activity.endGame()
        }
    }
}