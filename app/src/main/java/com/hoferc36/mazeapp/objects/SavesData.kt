package com.hoferc36.mazeapp.objects

class SavesData {
    var id:Long = 1
    var user:String = "NULL"
    var settingsId: Long = 1
    var wins:Int = 0
    override fun toString(): String {
        return "Saves: id=$id, user='$user', settingsId=$settingsId, wins=$wins"
    }
}