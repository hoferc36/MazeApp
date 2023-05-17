package com.example.mazeapp


class CellPieces {
    var top:Boolean = true
    var left:Boolean = true
    var right:Boolean = true
    var bottom:Boolean = true
    var visited:Boolean = false
    var here:Boolean = false
}

enum class PATH {
    TOP, LEFT, RIGHT, BOTTOM
}
