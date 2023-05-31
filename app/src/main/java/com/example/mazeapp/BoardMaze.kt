package com.example.mazeapp

import java.util.Stack
import kotlin.random.Random

class BoardMaze (val rows:Int = 2, val cols:Int = 2, val activity: BoardActivity) {
    val board: Array<Array<CellPieces>> = Array(rows) { Array(cols) { CellPieces() } }

    private val stack: Stack<Pair<Int, Int>> = Stack<Pair<Int, Int>>()
    private var visitedCellCount = 0

    private var hereCoords = Pair(0,0)
    private var hereCell = CellPieces()

    var startCellCoords: Pair<Int,Int> = Pair(0,0)
        set(value) {
            if(value.first in 0 until rows && value.second in 0 until cols) {
                board[startCellCoords.first][startCellCoords.second].start = false
                hereCell.here = false
                hereCell.visited = false
                field = value
                hereCoords = value
                hereCell = board[hereCoords.first][hereCoords.second]
                hereCell.start = true
                hereCell.here = true
                hereCell.visited = true
            }
        }
    var endCellCoords = Pair(0, 0)
        set(value) {
            if(value.first in 0 until rows && value.second in 0 until cols) {
                board[endCellCoords.first][endCellCoords.second].end = false
                field = value
                board[value.first][value.second].end = true
            }

        }

    init {
        board.forEachIndexed() { rowIndex, row ->
            row.forEachIndexed { colIndex, cell ->
                cell.position = rowIndex * cols + colIndex
                cell.coords = Pair(rowIndex, colIndex)
            }

        }
        hereCell = board[startCellCoords.first][startCellCoords.second]
        hereCell.start = true
        hereCell.here = true
        hereCell.visited = true

        endCellCoords = Pair(rows - 1, cols - 1)
        board[endCellCoords.first][endCellCoords.second].end = true

        stack.push(hereCoords)
        visitedCellCount = 1
        while (visitedCellCount < rows * cols) {
            pathSelector(hereCoords.first, hereCoords.second)
            hereCell.here = false
            hereCoords = stack.peek()//hereCoord.first = row, hereCoord.second = col
            hereCell = board[hereCoords.first][hereCoords.second]
            hereCell.here = true
            hereCell.visited = true
        }
        hereCell.here = false

        board.forEach { row ->
            row.forEach { cell ->
                cell.visited = false
            }
        }

        hereCoords = Pair(0,0)
        hereCell = board[hereCoords.first][hereCoords.second]
        hereCell.here = true
        hereCell.visited = true
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
            visitedCellCount++
        }else{
            if(!stack.empty()){
                stack.pop()
            }
        }
    }
    fun moveUp(){
        if (hereCell.top) {
            moveCharacter(hereCoords.first-1, hereCoords.second)
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
            moveCharacter(hereCoords.first, hereCoords.second-1)
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
            moveCharacter(hereCoords.first, hereCoords.second+1)
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
            moveCharacter(hereCoords.first+1, hereCoords.second)
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
        hereCoords = Pair(newRow, newCol)
        hereCell = board[hereCoords.first][hereCoords.second]
        hereCell.here = true
        hereCell.visited = true
        if (hereCell.end) {
            activity.endGame()
        }
    }
}