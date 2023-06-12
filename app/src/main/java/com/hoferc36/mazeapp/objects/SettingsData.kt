package com.hoferc36.mazeapp.objects

import java.io.Serializable

class SettingsData :Serializable{
    var width:Int = 5
    var height:Int = 5
    var buttonToggle: Boolean = true
    var startY: Int = 0
    var startX: Int = 0
    var endY: Int = width-1
    var endX: Int = height-1
    var seed: Int = 0
    var corridor: Boolean = true
}