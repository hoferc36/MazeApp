package com.hoferc36.mazeapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.hoferc36.mazeapp.objects.*

class DatabaseHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    var saves1 = SavesData()

    companion object{
        private const val DATABASE_VERSION = 7
        private const val DATABASE_NAME = "maze_data.db"

        private const val TABLE_USERDATA = "table_user"
        private const val USER_ID = "id"
        private const val USER_NAME = "name"
        private const val USER_WINS = "wins"
        private const val USER_SETTINGS = "settings"

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

        private const val TABLE_SAVESDATA = "table_saves"
        private const val SAVES_ID = "id"
        private const val SAVES_USER = "name"
        private const val SAVES_SETTINGS = "settings"
        private const val SAVES_WINS = "wins"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        Log.d("chandra", "creating new database")
        val createTableUsers = ("CREATE TABLE $TABLE_USERDATA ($USER_ID INTEGER PRIMARY KEY " +
                "AUTOINCREMENT, $USER_NAME TEXT, $USER_WINS INTEGER, $USER_SETTINGS INTEGER)")
        db?.execSQL(createTableUsers)
        val createTableSettings = ("CREATE TABLE $TABLE_SETTINGS ($SETTINGS_ID INTEGER PRIMARY KEY " +
                "AUTOINCREMENT, $SETTINGS_HEIGHT INTEGER, $SETTINGS_WIDTHS INTEGER, " +
                "$SETTINGS_BUTTONS BOOL, $SETTINGS_CORRIDOR BOOL, $SETTINGS_START_Y INTEGER, $SETTINGS_START_X INTEGER, " +
                "$SETTINGS_END_Y INTEGER, $SETTINGS_END_X INTEGER, $SETTINGS_SEED INTEGER)")
        db?.execSQL(createTableSettings)
        val createTableSaves = ("CREATE TABLE $TABLE_SAVESDATA ($SAVES_ID INTEGER PRIMARY KEY " +
                "AUTOINCREMENT, $SAVES_USER TEXT, $SAVES_SETTINGS INTEGER, $SAVES_WINS INTEGER)")
        db?.execSQL(createTableSaves)
        Log.d("chandra", "done creating new database")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.d("chandra", "upgrading database")
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_USERDATA")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SETTINGS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SAVESDATA")
        onCreate(db)
        Log.d("chandra", "done upgrading database")
    }

    fun savesUpdateUser(user:String): Boolean {
        savesLookUp()
        val db = this.writableDatabase

        val contValues = ContentValues().apply {
            put(SAVES_USER, user)
        }

        val count = db.update(TABLE_SAVESDATA, contValues,"$SAVES_ID = 1", null)
        db.close()
        if(count != -1){
            saves1.user = user
        }
        return count != -1
    }

    fun savesUpdateSettings(settings:Long):Boolean{
        savesLookUp()
        val db = this.writableDatabase

        val contValues = ContentValues().apply {
            put(SAVES_SETTINGS, settings)
        }

        val count = db.update(TABLE_SAVESDATA, contValues,"$SAVES_ID = 1", null)
        db.close()
        if(count != -1){
            saves1.settingsId = settings
        }
        return count != -1
    }

    fun savesUpdateWins(wins:Int):Boolean{
        savesLookUp()
        val db = this.writableDatabase

        val contValues = ContentValues().apply {
            put(SAVES_WINS, wins)
        }

        val count = db.update(TABLE_SAVESDATA, contValues,"$SAVES_ID = 1", null)
        db.close()

        if(count != -1){
            saves1.wins = wins
        }
        return count != -1
    }

    fun savesLookUp(): SavesData{
        val savesList = savesListRetriever()
        return if(savesList.isEmpty()) {
            val db = this.writableDatabase

            val contValues = ContentValues().apply {
                put(SAVES_ID, 1)
                put(SAVES_USER, "NULL")
                put(SAVES_SETTINGS, 1)
                put(SAVES_WINS, 0)
            }

//            val newRowId =
                db.insert(TABLE_SAVESDATA, null, contValues)
            db.close()
            SavesData()
        } else {
            saves1 = savesList[0]
            savesList[0]
        }
    }

    private fun savesListRetriever():ArrayList<SavesData>{
        val savesList: ArrayList<SavesData> = ArrayList()
        val selectQuery = "SELECT * FROM $TABLE_SAVESDATA"
        val db = this.readableDatabase

        val cursor = db.rawQuery(selectQuery, null)

        var saves = SavesData()

        if(cursor.moveToFirst()){
            do{
                with(saves) {
                    id = cursor.getLong(0)
                    user = cursor.getString(1)
                    settingsId = cursor.getLong(2)
                    wins = cursor.getInt(3)
                }
                savesList.add(saves)
                saves = SavesData()
            }while(cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return savesList
    }

    fun userSearch(name: String):UserData?{
        val userList = userListRetriever()
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

    fun userAdd(username: String): Long{
        return if(userSearch(username) == null) {
            val db = this.writableDatabase

            val contValues = ContentValues().apply {
                put(USER_NAME, username)
                put(USER_WINS, 0)
                put(USER_SETTINGS, 1)
            }

            val newRowId = db.insert(TABLE_USERDATA, null, contValues)
            db.close()
            newRowId
        }else -1L
    }

    fun userUpdate(name: String, newUserData: UserData):Boolean{
        val oldUserData = userSearch(name)
        if(oldUserData != null) {
            val db = this.writableDatabase

            val contValues = ContentValues().apply{
                put(USER_NAME, newUserData.name)
                put(USER_WINS, newUserData.wins)
                put(USER_SETTINGS, newUserData.settingsId)
            }

            val count = db.update(TABLE_USERDATA,contValues, "$USER_ID = ${oldUserData.id}", null)
            db.close()
            return count != -1
        }
        return false
    }

    private fun userListRetriever():ArrayList<UserData>{
        val userList: ArrayList<UserData> = ArrayList()
        val selectQuery = "SELECT * FROM $TABLE_USERDATA"
        val db = this.readableDatabase

        val cursor = db.rawQuery(selectQuery, null)

        var user = UserData()

        if(cursor.moveToFirst()){
            do{
                with(user) {
                    id = cursor.getLong(0)
                    name = cursor.getString(1)
                    wins = cursor.getInt(2)
                    settingsId = cursor.getLong(3)
                }
                userList.add(user)
                user = UserData()

            }while(cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return userList
    }

    fun settingsSearch(id: Long):SettingsData{
        val settingsList = settingsListRetriever()
        var settings= SettingsData()

        if (settingsList.size > 0) {
            settingsList.forEach {
                if(it.id == id){
                    settings = it
                }
            }
        }else {
            settingsAdd(SettingsData())
        }
        return settings
    }

    fun settingsAdd(settings:SettingsData): Long{
        if(settingsListRetriever().size > 0){
            val firstSettings = SettingsData()
            val db = this.writableDatabase

            val contValues = ContentValues().apply {
                put(SETTINGS_HEIGHT, firstSettings.rows)
                put(SETTINGS_WIDTHS, firstSettings.cols)
                put(SETTINGS_BUTTONS, firstSettings.buttonToggle)
                put(SETTINGS_CORRIDOR, firstSettings.corridor)
                put(SETTINGS_START_Y, firstSettings.startY)
                put(SETTINGS_START_X, firstSettings.startX)
                put(SETTINGS_END_Y, firstSettings.endY)
                put(SETTINGS_END_X, firstSettings.endX)
                put(SETTINGS_SEED, firstSettings.seed)
            }

            db.insert(TABLE_SETTINGS, null, contValues)
            db.close()
        }

        var results = if(settings.id != 1L){
            settingsSearchForIdentical(settings)
        } else -1L

        if(results == -1L) {
            val db = this.writableDatabase

            val contValues = ContentValues().apply {
                put(SETTINGS_HEIGHT, settings.rows)
                put(SETTINGS_WIDTHS, settings.cols)
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

    fun settingsSearchForIdentical(settings: SettingsData): Long {
        val settingsList = settingsListRetriever()

        if (settingsList.size > 0) {
            settingsList.forEach {
                if (settings.equals(it)) {
                    return it.id
                }
            }
        }else{
            settingsAdd(SettingsData())
        }
        return -1L
    }

    private fun settingsListRetriever():ArrayList<SettingsData>{
        val settingsList: ArrayList<SettingsData> = ArrayList()
        val selectQuery = "SELECT * FROM $TABLE_SETTINGS"
        val db = this.readableDatabase

        val cursor = db.rawQuery(selectQuery, null)

        var settings = SettingsData()

        if(cursor.moveToFirst()){
            do{
                with(settings) {
                    id = cursor.getLong(0)
                    rows = cursor.getInt(1)
                    cols = cursor.getInt(2)
                    buttonToggle = cursor.getInt(3) == 1
                    corridor = cursor.getInt(4) == 1
                    startY = cursor.getInt(5)
                    startX = cursor.getInt(6)
                    endY = cursor.getInt(7)
                    endX = cursor.getInt(8)
                    seed = cursor.getInt(9)
                }

                settingsList.add(settings)
                settings = SettingsData()

            }while(cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return settingsList
    }
}