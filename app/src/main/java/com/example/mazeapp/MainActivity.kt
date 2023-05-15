package com.example.mazeapp

import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.mazeapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var bind: ActivityMainBinding
    private lateinit var displayMetrics: DisplayMetrics

    private val rows = 5 //19
    private val cols = 5 //21 //10
    private val board = Array(rows) { r ->
        IntArray(cols){ c ->
            (r * rows + c + 1000)
        }
    }
    var cellSize = 50
    val margin = 8
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.main1)

        displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        cellCreation()

//        var cell = bind.main1.getChildAt(0)
//        cell.setBackgroundResource(R.color.purple_1)
//        bind.main1.getChildAt( rows * cols * 2 - 1).setBackgroundResource(R.color.white)
    }

    private fun cellCreation() {
        val height = (displayMetrics.heightPixels - margin*2) /rows
        val width = (displayMetrics.widthPixels - margin*2) /cols
        cellSize = if(width < height) width else height
        cellSize = if(cellSize < 50) 50 else cellSize

//        var boardStatus = Array<ImageView>(board[rows][cols]){cell->
//            cell = ImageView(this)
//        }

        board.forEachIndexed{ rowIndex, row ->
            row.forEachIndexed{colIndex, col ->
                val cellBackground = ImageView(this)
                cellBackground.minimumHeight = cellSize
                cellBackground.minimumWidth = cellSize
                cellBackground.x = 1F * cellSize * colIndex + margin
                cellBackground.y = 1F * cellSize * rowIndex + margin
//                cellBackground.id = col
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
                bind.main1.addView(cellBackground)

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
                bind.main1.addView(cellForeground)
            }
        }
    }
}
