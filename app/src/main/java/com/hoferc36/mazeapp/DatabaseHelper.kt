package com.hoferc36.mazeapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.hoferc36.mazeapp.objects.SettingsData
import com.hoferc36.mazeapp.objects.UserData

class DatabaseHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object{
        private const val DATABASE_VERSION = 3
        private const val DATABASE_NAME = "maze_data.db"

        private const val TABLE_USERDATA = "table_user"
        private const val USER_ID = "id"
        private const val USER_NAME = "name"
        private const val USER_WINS = "wins"
        //TODO add settings to user

        private const val TABLE_SETTINGS = "table_settings"
        private const val SETTINGS_ID = "id"
        private const val SETTINGS_HEIGHT = "height"
        private const val SETTINGS_WIDTHS = "width"
        private const val SETTINGS_BUTTONS = "buttons"
        private const val SETTINGS_CORRIDOR = "corridor"
        private const val SETTINGS_START_Y = "start_y"
        private const val SETTINGS_START_X = "start_x"
        private const val SETTINGS_END_Y = "end_y"
        private const val SETTINGS_END_X = "end_x"
        private const val SETTINGS_SEED = "seed"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableUsers = ("CREATE TABLE $TABLE_USERDATA ($USER_ID INTEGER PRIMARY KEY " +
                "AUTOINCREMENT, $USER_NAME TEXT, $USER_WINS INTEGER)")
        //TODO add settings to user
        db?.execSQL(createTableUsers)
        val createTableSettings = ("CREATE TABLE $TABLE_SETTINGS ($SETTINGS_ID INTEGER PRIMARY KEY " +
                "AUTOINCREMENT, $SETTINGS_HEIGHT INTEGER, $SETTINGS_WIDTHS INTEGER, " +
                "$SETTINGS_BUTTONS BOOL, $SETTINGS_CORRIDOR BOOL, $SETTINGS_START_Y INTEGER, $SETTINGS_START_X INTEGER, " +
                "$SETTINGS_END_Y INTEGER, $SETTINGS_END_X INTEGER, $SETTINGS_SEED INTEGER)")
        db?.execSQL(createTableSettings)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_USERDATA")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SETTINGS")
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
            //TODO add settings to user
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
                //TODO add settings to user
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

        var user = UserData()

        if(cursor.moveToFirst()){
            do{
                with(user) {
                    id = cursor.getInt(0)
                    name = cursor.getString(1)
                    wins = cursor.getInt(2)
                    //TODO add settings to user
                }
                userList.add(user)
                user = UserData()

            }while(cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return userList
    }

    fun searchForSettings(id: Long):SettingsData?{
        val settingsList = getAllSettings()
        var settings:SettingsData? = null
        if (settingsList.size > 0) {
            settingsList.forEach {
                if(it.id == id){
                    settings = it
                }
            }
        }
        return settings
    }
    fun addSettings(settings:SettingsData): Long{
        var results = searchForExistingSettings(settings)

        if(results == -1L) {
            val db = this.writableDatabase

            val contValues = ContentValues()
            with(contValues) {
                put(SETTINGS_HEIGHT, settings.height)
                put(SETTINGS_WIDTHS, settings.width)
                put(SETTINGS_BUTTONS, settings.buttonToggle)
                put(SETTINGS_CORRIDOR, settings.corridor)
                put(SETTINGS_START_Y, settings.startY)
                put(SETTINGS_START_X, settings.startX)
                put(SETTINGS_END_Y, settings.endY)
                put(SETTINGS_END_X, settings.endX)
                put(SETTINGS_SEED, settings.seed)
            }

            results = db.insert(TABLE_SETTINGS, null, contValues)
            db.close()
        }

        return results

    }

    fun searchForExistingSettings(settings: SettingsData): Long {
        val settingsList = getAllSettings()
        settingsList.forEach {
            if (settings.equals(it)) {
                Log.d("chandra", "setting already exists settings id ${it.id}")
                return it.id
            }
        }
        Log.d("chandra", "setting did not exists settings id ${settings.id}")
        return -1L
    }

    fun getAllSettings():ArrayList<SettingsData>{
        val settingsList: ArrayList<SettingsData> = ArrayList()
        val selectQuery = "SELECT * FROM $TABLE_SETTINGS"
        val db = this.readableDatabase

        val cursor = db.rawQuery(selectQuery, null)

        var settings = SettingsData()

        if(cursor.moveToFirst()){
            do{
                with(settings) {
                    id = cursor.getLong(0)
                    height = cursor.getInt(1)
                    width = cursor.getInt(2)
                    buttonToggle = cursor.getInt(3) == 1
                    corridor = cursor.getInt(4) == 1
                    startY = cursor.getInt(5)
                    startX = cursor.getInt(6)
                    endY = cursor.getInt(7)
                    endX = cursor.getInt(8)
                    seed = cursor.getInt(9)
                }
//                Log.d("chandra", "get all settings $settings")

                settingsList.add(settings)
                settings = SettingsData()

            }while(cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return settingsList
    }
}