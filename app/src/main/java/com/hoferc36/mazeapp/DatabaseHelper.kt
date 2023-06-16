package com.hoferc36.mazeapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.hoferc36.mazeapp.objects.UserData

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
        val createTableUsers = ("CREATE TABLE $TABLE_USERDATA ($USER_ID INTEGER PRIMARY KEY " +
                "AUTOINCREMENT, $USER_NAME TEXT, $USER_WINS TEXT)")
        db?.execSQL(createTableUsers)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_USERDATA")
        onCreate(db)
    }

    fun searchForUser(name: String):UserData?{
        val userList = getAllUsers()
        var user:UserData? = null
        if (userList.size > 0) {
            userList.forEach {
                if(it.name == name){
                    user = it
                }
            }
        }
        return user
    }

    fun addUser(user: UserData): Boolean{
        val db = this.writableDatabase

        val contValues = ContentValues()
        with(contValues){
            put(USER_NAME, user.name)
            put(USER_WINS, user.wins)
        }

        val success = db.insert(TABLE_USERDATA, null, contValues)
        db.close()
        return success != -1L
    }

    fun updateUser(name: String, newUserData: UserData):Boolean{
        val oldUserData = searchForUser(name)
        if(oldUserData != null) {
            val db = this.writableDatabase

            val contValues = ContentValues()
            with(contValues) {
                put(USER_NAME, newUserData.name)
                put(USER_WINS, newUserData.wins)
            }

            val success = db.update(TABLE_USERDATA,contValues, "$USER_ID = ${oldUserData.id}", null)
            db.close()
            return success != -1
        }
        return false
    }

    fun getAllUsers():ArrayList<UserData>{
        val userList: ArrayList<UserData> = ArrayList()
        val selectQuery = "SELECT * FROM $TABLE_USERDATA"
        val db = this.readableDatabase

        val cursor = db.rawQuery(selectQuery, null)

        var user : UserData = UserData()

        if(cursor.moveToFirst()){
            do{
                user.id = cursor.getInt(0)
                user.name = cursor.getString(1)
                user.wins = cursor.getInt(2)

                userList.add(user)
                user = UserData()

            }while(cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return userList
    }
}