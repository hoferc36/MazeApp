package com.hoferc36.mazeapp.objects

class UserData (var name: String = "Peter Parker"){
    var id:Long = -1
    var wins:Int = 0
    var settingsId: Long = 1

    override fun toString(): String{
        return "User: $name \nWins: $wins \nSettings: ${if(settingsId == 1L) "default" else "user's"}"
    }
}