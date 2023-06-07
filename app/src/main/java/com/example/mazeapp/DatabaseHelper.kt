package com.example.mazeapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object{
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "maze_data.db"
        private const val TABLE_USERDATA = "table_user"
        private const val USER_ID = "id"
        private const val USER_NAME = "name"
        private const val USER_WINS = "wins"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableUsers = ("CREATE TABLE $TABLE_USERDATA + ($USER_ID INTEGER PRIMARY KEY, " +
                "$USER_NAME TEXT, $USER_WINS TEXT)")
        db?.execSQL(createTableUsers)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_USERDATA")
        onCreate(db)
    }

    fun insertUser(user: UserData): Boolean{
        val db = this.writableDatabase

        val contValues = ContentValues()
        with(contValues){
            put(USER_ID, user.id)
            put(USER_NAME, user.name)
            put(USER_WINS, user.wins)
        }

        val success = db.insert(TABLE_USERDATA, null, contValues)
        db.close()
        return success != -1L
    }

    fun getAllUsers():ArrayList<UserData>{
        val userList: ArrayList<UserData> = ArrayList()
        val selectQuery = "SELECT * FROM $TABLE_USERDATA"
        val db = this.readableDatabase

        val cursor: Cursor?

        try{
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: Exception){
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var id: Int
        var name: String
        var wins: Int
        var user :UserData

        if(cursor.moveToFirst()){
            do{
                var index = cursor.getColumnIndex(USER_ID)
                if(index != -1){
                    id = cursor.getInt(index)
                    index = cursor.getColumnIndex(USER_NAME)
                    if(index != -1){
                        name = cursor.getString(index)
                        index = cursor.getColumnIndex(USER_WINS)
                        if(index != -1){
                            wins = cursor.getInt(index)

                            user = UserData(id = id, name = name)
                            user.wins = wins
                            userList.add(user)
                        }
                    }
                }
            }while(cursor.moveToNext())
        }
        return userList
    }
}