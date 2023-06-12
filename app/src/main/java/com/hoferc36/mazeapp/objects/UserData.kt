package com.hoferc36.mazeapp.objects

import java.io.Serializable

data class UserData (var id:Int = -1, var name: String = ""): Serializable{
    var wins:Int = 0
}