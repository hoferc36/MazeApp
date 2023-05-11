package com.example.mazeapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.example.mazeapp.databinding.*

class MainActivity : AppCompatActivity() {
    private lateinit var bind: ActivityMainBinding

    private val rows = 19
    private val cols = 10
    private val board = Array(rows) { r ->
        IntArray(cols){ c ->
            (r * rows + c)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.main1)
        board.forEachIndexed{ rowIndex, row ->
            row.forEach{col ->
                var cell = ImageView(this)
                cell.minimumHeight = 100
                cell.minimumWidth = 100
                cell.x = 100F * col - 100F * rowIndex * rows
                cell.y = 100F * rowIndex
                R.drawable.cell
                cell.setBackgroundResource(R.drawable.cell)
                //cell.setBackgroundResource(R.color.orange)
                //cell.setImageDrawable(R.drawable.cell.toDrawable())
                bind.main1.addView(cell)
            }
        }

//        val cell1 = ImageView(this)
//        cell1.minimumHeight = 100
//        cell1.minimumWidth = 100
//        cell1.x = 0f + 2 * 100f
//        cell1.y = 0f
//        cell1.setBackgroundResource(R.color.orange)
////        cell1.setImageDrawable(R.drawable.cell.toDrawable())
//        bind.main1.addView(cell1)
//
//
//        val cell2 = ImageView(this)
//        cell2.minimumHeight = 100
//        cell2.minimumWidth = 100
//        cell2.x = 100f
//        cell2.y = 100f
//        cell2.setBackgroundResource(R.color.orange)
////        Log.i("chandra", Color.MAGENTA.toString())
//
////        cell2.setImageDrawable(Color.GREEN.toDrawable())
//        bind.main1.addView(cell2)
    }
}