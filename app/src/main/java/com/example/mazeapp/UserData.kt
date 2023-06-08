package com.example.mazeapp

import java.io.Serializable

data class UserData (var id:Int = -1, var name: String = ""): Serializable{
    var wins:Int = 0
    companion object{

    }

}