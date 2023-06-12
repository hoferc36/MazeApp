package com.hoferc36.mazeapp.objects


class CellPieces {
    var top:Boolean = false
    var left:Boolean = false
    var right:Boolean = false
    var bottom:Boolean = false

    var visited:Boolean = false
    var here:Boolean = false
    var dead:Boolean = false

    var start:Boolean = false
    var end:Boolean = false

    var position: Int = 0
    var coord: Pair<Int, Int> = Pair(0,0)

}

enum class PATH {
    TOP, LEFT, RIGHT, BOTTOM
}
