package com.hoferc36.mazeapp.objects

import java.io.Serializable

class BoardData: Serializable{
    var seed = 0
    var missSteps = 0
    var revisited = 0
    var totalMoves = 0
    var timeTaken = 0L
}