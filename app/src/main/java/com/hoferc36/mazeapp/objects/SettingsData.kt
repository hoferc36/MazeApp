package com.hoferc36.mazeapp.objects

class SettingsData{
    var id:Long = 1
    var rows:Int = 5
        set(value) {
            if(endY == rows-1){
                endY = value-1
            }
            field = value
        }
    var cols:Int = 5
        set(value) {
            if(endX == cols-1){
                endX = value-1
            }
            field = value
        }
    var buttonToggle: Boolean = false
    var startY: Int = 0
    var startX: Int = 0
    var endY: Int = rows-1
    var endX: Int = cols-1
    var seed: Int = 0
    var corridor: Boolean = false
    override fun toString(): String {
        return "settings: id=$id, height=$rows, width=$cols, buttonToggle=$buttonToggle, start=$startY, $startX, end=$endY, $endX, seed=$seed, corridor=$corridor"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SettingsData

        if (rows != other.rows) return false
        if (cols != other.cols) return false
        if (buttonToggle != other.buttonToggle) return false
        if (startY != other.startY) return false
        if (startX != other.startX) return false
        if (endY != other.endY) return false
        if (endX != other.endX) return false
        if (seed != other.seed) return false
        if (corridor != other.corridor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = rows
        result = 31 * result + cols
        result = 31 * result + buttonToggle.hashCode()
        result = 31 * result + startY
        result = 31 * result + startX
        result = 31 * result + endY
        result = 31 * result + endX
        result = 31 * result + seed
        result = 31 * result + corridor.hashCode()
        return result
    }


}