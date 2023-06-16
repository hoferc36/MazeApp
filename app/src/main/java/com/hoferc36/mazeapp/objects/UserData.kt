package com.hoferc36.mazeapp.objects

import java.io.Serializable

class UserData (var name: String = ""): Serializable{
    var id:Int = -1
    var wins:Int = 0

    override fun toString(): String{
        return "User: $name \nWins: $wins"
    }
}