package com.hoferc36.mazeapp.logic

import com.hoferc36.mazeapp.objects.*
import com.hoferc36.mazeapp.ui.*
import java.util.Stack
import kotlin.math.abs
import kotlin.random.Random

class BoardMaze (private val settings: SettingsData = SettingsData(), private val activity: BoardActivity) {

    enum class PATH {
        TOP, LEFT, RIGHT, BOTTOM;
    }

    val boardData = BoardData()

    val board: Array<Array<CellPieces>> = Array(settings.rows) { Array(settings.cols) { CellPieces() } }

    private val stack: Stack<Pair<Int, Int>> = Stack<Pair<Int, Int>>()
    private var visitedCellCount = 0

    private var hereCell = CellPieces()

    private var currentSeed = (2029L + settings.rows - settings.cols)
    private var revisitedCorridor: Boolean = false

    var startTime = 0L
    var endTime = 0L
    var timerStarted:Boolean = false

    init {
        boardData.seed = if (settings.seed <= 0) Random.nextInt(1, 3000) else settings.seed
        currentSeed *= boardData.seed
        boardSetUp()
        pathSetUp()

        hereCell = board[settings.startY][settings.startX]
        hereCell.start = true
        hereCell.here = true
        hereCell.visited = true

        board[settings.endY][settings.endX].end = true
    }

    private fun boardSetUp() {
        board.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { colIndex, cell ->
                cell.position = rowIndex * settings.cols + colIndex
                cell.coord = Pair(rowIndex, colIndex)
            }
        }
        hereCell = board[0][0]
        hereCell.here = true
        hereCell.visited = true
    }

    private fun pathSetUp() {
        stack.push(hereCell.coord)
        visitedCellCount = 1
        while (visitedCellCount < settings.rows * settings.cols) {
            pathSelector(hereCell.coord.first,hereCell.coord.second)
            hereCell.here = false
            hereCell = board[stack.peek().first][stack.peek().second]
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

    private fun pathSelector(row:Int, col:Int) {
        val availablePaths = mutableListOf<PATH>()

        if (row > 0 && !board[row - 1][col].visited) {
            availablePaths.add(PATH.TOP)
        }
        if (row < settings.rows-1 && !board[row + 1][col].visited) {
            availablePaths.add(PATH.BOTTOM)
        }
        if (col > 0 && !board[row][col - 1].visited) {
            availablePaths.add(PATH.LEFT)
        }
        if (col < settings.cols-1 && !board[row][col + 1].visited) {
            availablePaths.add(PATH.RIGHT)
        }

        if (availablePaths.size > 0) {
            currentSeed += Random(currentSeed).nextInt(1, 3000)
            currentSeed = if(currentSeed == 0L) 2029L else abs(currentSeed)
//            Log.d("chandra", "current seed $currentSeed")

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
            visitedCellCount++
        }else{
            if(!stack.empty()){
                stack.pop()
            }
        }
    }

    fun dragToNextPath(row: Int, col: Int){
        if((row == hereCell.coord.first) xor (col == hereCell.coord.second) ){
            if(row == hereCell.coord.first-1){
                moveUp()
            }else if(row == hereCell.coord.first+1){
                moveDown()
            }else if (col == hereCell.coord.second-1){
                moveLeft()
            }else if (col == hereCell.coord.second+1){
                moveRight()
            }
        }
    }

    fun moveUp(){
//        Log.d("chandra", "move up")
        if (hereCell.top) {
            if(board[hereCell.coord.first-1][hereCell.coord.second].visited) {
                if (settings.corridor) {
                    revisitedCorridor = true
                } else {
                    boardData.revisited++
                }
            }
            moveCharacter(hereCell.coord.first-1, hereCell.coord.second)
            if(hereCell.top && !hereCell.left && !hereCell.right
                && settings.corridor && !hereCell.end){
                moveUp()
            } else {
                boardData.totalMoves++
                if(revisitedCorridor){
                    boardData.revisited++
                    revisitedCorridor = false
                }
                activity.boardRefresh()
            }
        }else{
            boardData.missSteps++
        }
    }

    fun moveLeft(){
//        Log.d("chandra", "move left")
        if(hereCell.left) {
            if (board[hereCell.coord.first][hereCell.coord.second-1].visited){
                if (settings.corridor) {
                    revisitedCorridor = true
                } else {
                    boardData.revisited++
                }
            }
            moveCharacter(hereCell.coord.first, hereCell.coord.second-1)
            if(!hereCell.top && hereCell.left && !hereCell.bottom
                && settings.corridor && !hereCell.end){
                moveLeft()
            } else {
                boardData.totalMoves++
                if(revisitedCorridor){
                    boardData.revisited++
                    revisitedCorridor = false
                }
                activity.boardRefresh()
            }
        }else{
            boardData.missSteps++
        }
    }
    fun moveRight(){
//        Log.d("chandra", "move right")
        if(hereCell.right){
            if (board[hereCell.coord.first][hereCell.coord.second+1].visited){
                if (settings.corridor) {
                    revisitedCorridor = true
                } else {
                    boardData.revisited++
                }
            }
            moveCharacter(hereCell.coord.first, hereCell.coord.second+1)
            if(!hereCell.top && hereCell.right && !hereCell.bottom
                && settings.corridor && !hereCell.end){
                moveRight()
            } else {
                boardData.totalMoves++
                if(revisitedCorridor){
                    boardData.revisited++
                    revisitedCorridor = false
                }
                activity.boardRefresh()
            }
        }else{
            boardData.missSteps++
        }
    }

    fun moveDown(){
//        Log.d("chandra", "move down")
        if(hereCell.bottom){
            if(board[hereCell.coord.first+1][hereCell.coord.second].visited) {
                if (settings.corridor) {
                    revisitedCorridor = true
                } else {
                    boardData.revisited++
                }
            }
            moveCharacter(hereCell.coord.first+1, hereCell.coord.second)
            if(!hereCell.left && !hereCell.right && hereCell.bottom
                && settings.corridor && !hereCell.end){
                moveDown()
            } else {
                boardData.totalMoves++
                if(revisitedCorridor){
                    boardData.revisited++
                    revisitedCorridor = false
                }
                activity.boardRefresh()
            }
        }else{
            boardData.missSteps++
        }
    }
    private fun moveCharacter(newRow:Int, newCol:Int){
        if (!timerStarted){
            startTime = System.currentTimeMillis()
            timerStarted = true
        }
        hereCell.here = false

        hereCell = board[newRow][newCol]
        hereCell.here = true
        hereCell.visited = true
        if (hereCell.end) {
            boardData.totalMoves++
            endTime = System.currentTimeMillis()
            boardData.timeTaken = endTime - startTime
            //TODO minus off off-app time
            activity.endGame()
        }
        activity.moveCharacter(newRow, newCol)
    }
}