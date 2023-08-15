package com.hoferc36.mazeapp.objects

class SettingsData{
    var id:Long = 1
    var height:Int = 5
        set(value) {
            if(endY == height-1){
                endY = value-1
            }
            field = value
        }
    var width:Int = 5
        set(value) {
            if(endX == width-1){
                endX = value-1
            }
            field = value
        }
    var buttonToggle: Boolean = false
    var startY: Int = 0
    var startX: Int = 0
    var endY: Int = height-1
    var endX: Int = width-1
    var seed: Int = 0
    var corridor: Boolean = false
    override fun toString(): String {
        return "settings: id=$id, height=$height, width=$width, buttonToggle=$buttonToggle, start=$startY, $startX, end=$endY, $endX, seed=$seed, corridor=$corridor"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SettingsData

        if (height != other.height) return false
        if (width != other.width) return false
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
        var result = height
        result = 31 * result + width
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