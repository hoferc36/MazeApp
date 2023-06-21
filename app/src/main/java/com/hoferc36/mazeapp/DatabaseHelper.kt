package com.hoferc36.mazeapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.hoferc36.mazeapp.objects.SettingsData
import com.hoferc36.mazeapp.objects.UserData

class DatabaseHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object{
        private const val DATABASE_VERSION = 2
        private const val DATABASE_NAME = "maze_data.db"

        private const val TABLE_USERDATA = "table_user"
        private const val USER_ID = "id"
        private const val USER_NAME = "name"
        private const val USER_WINS = "wins"

        private const val TABLE_SETTINGS = "table_settings"
        private const val SETTINGS_ID = "id"
        private const val SETTINGS_USERNAME = "name"
        private const val SETTINGS_HEIGHT = "height"
        private const val SETTINGS_WIDTHS = "width"
        private const val SETTINGS_BUTTONS = "buttons"
        private const val SETTINGS_CORRIDOR = "corridor"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableUsers = ("CREATE TABLE $TABLE_USERDATA ($USER_ID INTEGER PRIMARY KEY " +
                "AUTOINCREMENT, $USER_NAME TEXT, $USER_WINS INTEGER)")
        db?.execSQL(createTableUsers)
        val createTableSettings = ("CREATE TABLE $TABLE_SETTINGS ($SETTINGS_ID INTEGER PRIMARY KEY " +
                "AUTOINCREMENT, $SETTINGS_USERNAME TEXT, $SETTINGS_HEIGHT INTEGER, $SETTINGS_WIDTHS INTEGER, $SETTINGS_BUTTONS BOOL, $SETTINGS_CORRIDOR BOOL)")
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

        var user = UserData()

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

    fun searchForSettings(id: Int):SettingsData?{
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
    fun addSettings(settings:SettingsData): Int{
        val db = this.writableDatabase

        val contValues = ContentValues()
        with(contValues){
            put(SETTINGS_USERNAME, "default")
            put(SETTINGS_HEIGHT, settings.height)
            put(SETTINGS_WIDTHS, settings.width)
            put(SETTINGS_BUTTONS, settings.buttonToggle)
            put(SETTINGS_CORRIDOR, settings.corridor)
        }

        val success = db.insert(TABLE_SETTINGS, null, contValues)
        db.close()

        return if(success == -1L){
            -1
        }else {
            if (getAllSettings().size > 0) getAllSettings()[0].id else -1
        }
    }

    fun updateSettings(id: Int, newSettings: SettingsData):Boolean{
        val oldSettings= searchForSettings(id)
        if(oldSettings != null) {
            val db = this.writableDatabase

            val contValues = ContentValues()
            with(contValues) {
                put(SETTINGS_HEIGHT, newSettings.height)
                put(SETTINGS_WIDTHS, newSettings.width)
                put(SETTINGS_BUTTONS, newSettings.buttonToggle)
                put(SETTINGS_CORRIDOR, newSettings.corridor)
            }

            val success = db.update(TABLE_SETTINGS,contValues, "$SETTINGS_ID = ${oldSettings.id}", null)
            db.close()
            return success != -1
        }
        return false
    }
    fun getAllSettings():ArrayList<SettingsData>{
        val settingsList: ArrayList<SettingsData> = ArrayList()
        val selectQuery = "SELECT * FROM $TABLE_SETTINGS"
        val db = this.readableDatabase

        val cursor = db.rawQuery(selectQuery, null)

        var settings = SettingsData()

        if(cursor.moveToFirst()){
            do{
                settings.id = cursor.getInt(0)
                settings.height = cursor.getInt(2)
                settings.width = cursor.getInt(3)
                settings.buttonToggle = cursor.getInt(4) == 1
                settings.corridor = cursor.getInt(5) == 1

                settingsList.add(settings)
                settings = SettingsData()

            }while(cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return settingsList
    }
}