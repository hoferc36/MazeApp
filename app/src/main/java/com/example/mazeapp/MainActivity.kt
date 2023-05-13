package com.example.mazeapp

import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.mazeapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var bind: ActivityMainBinding

    private val rows = 5 //19
    private val cols = 5 //21 //10
    private val board = Array(rows) { r ->
        IntArray(cols){ c ->
            (r * rows + c)
        }
    }
    var cellSize = 50
    val margin = 8

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.main1)
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = (displayMetrics.heightPixels - margin*2) /rows
        val width = (displayMetrics.widthPixels - margin*2) /cols
        cellSize = if(width < height) width else height
        cellSize = if(cellSize < 50) 50 else cellSize

        board.forEachIndexed{ rowIndex, row ->
            row.forEachIndexed{colIndex, col ->
                val cell = ImageView(this)
                cell.minimumHeight = cellSize
                cell.minimumWidth = cellSize
                cell.x = 1F * cellSize * (col - rowIndex * rows) + margin
                cell.y = 1F * cellSize * rowIndex + margin

                when (rowIndex) {
                    0 -> {
                        when (colIndex) {
                            0 -> {
                                cell.setBackgroundResource(R.drawable.cell_2path_right_bottom)
                            }

                            cols-1 -> {
                                cell.setBackgroundResource(R.drawable.cell_2path_left_bottom)
                            }

                            else -> {
                                cell.setBackgroundResource(R.drawable.cell_3path_notop)
                            }
                        }
                    }
                    rows-1 -> {
                        when (colIndex) {
                            0 -> {
                                cell.setBackgroundResource(R.drawable.cell_2path_top_right)
                            }

                            cols-1 -> {
                                cell.setBackgroundResource(R.drawable.cell_2path_top_left)
                            }

                            else -> {
                                cell.setBackgroundResource(R.drawable.cell_3path_nobottom)
                            }
                        }
                    }
                    else -> {
                        when (colIndex) {
                            0 -> {
                                cell.setBackgroundResource(R.drawable.cell_3path_noleft)
                            }

                            cols-1 -> {
                                cell.setBackgroundResource(R.drawable.cell_3path_noright)
                            }

                            else -> {
                                cell.setBackgroundResource(R.drawable.cell_4path)
                            }
                        }
                    }
                }
//                cell.setBackgroundResource(R.drawable.cell)
                //cell.setBackgroundResource(R.color.orange)
                //cell.setImageDrawable(R.drawable.cell.toDrawable())
                bind.main1.addView(cell)
            }
        }
    }
}