package com.example.mazeapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.ImageView
import com.example.mazeapp.databinding.ActivityBoardBinding

class BoardActivity : AppCompatActivity() {
    private lateinit var bind: ActivityBoardBinding
    private lateinit var displayMetrics: DisplayMetrics

    private val rows = 21 //19
    private val cols = 15 //21 //10
    private val board = Array(rows) { r ->
        IntArray(cols){ c ->
            (r * rows + c)
        }
    }
    var cellSize = 50
    val margin = 8


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityBoardBinding.inflate(layoutInflater)
        setContentView(bind.board1)

        displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        cellCreation()

        bind.boardBackground.getChildAt(100).setBackgroundResource(R.color.purple_1)

    }

    private fun cellCreation() {
        val height = (displayMetrics.heightPixels - margin) /rows
        val width = (displayMetrics.widthPixels - margin) /cols
        cellSize = if(width < height) width else height
        cellSize = if(cellSize < 50) 50 else cellSize

        board.forEachIndexed{ rowIndex, row ->
            row.forEachIndexed{colIndex, col ->
                val cellBackground = ImageView(this)
                cellBackground.minimumHeight = cellSize
                cellBackground.minimumWidth = cellSize
                cellBackground.x = 1F * cellSize * colIndex + margin
                cellBackground.y = 1F * cellSize * rowIndex + margin
                if (rowIndex.mod(2) == 1){
                    if(colIndex.mod(2) == 1) {
                        cellBackground.setBackgroundResource(R.color.blank_cell)
                    }else{
                        cellBackground.setBackgroundResource(R.color.orange)
                    }
                }else{
                    if(colIndex.mod(2) == 1) {
                        cellBackground.setBackgroundResource(R.color.dark_orange)
                    }else{
                        cellBackground.setBackgroundResource(R.color.light_orange)
                    }
                }
                bind.boardBackground.addView(cellBackground)

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
        }
    }
}