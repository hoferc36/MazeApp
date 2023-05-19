package com.example.mazeapp


class CellPieces {
    var top:Boolean = false
    var left:Boolean = false
    var right:Boolean = false
    var bottom:Boolean = false
    var visited:Boolean = false
    var here:Boolean = false
    var dead:Boolean = false

    // used for map generation
    var usedTop:Boolean = false
    var usedLeft:Boolean = false
    var usedRight:Boolean = false
    var usedBottom:Boolean = false
}

enum class PATH {
    TOP, LEFT, RIGHT, BOTTOM
}
