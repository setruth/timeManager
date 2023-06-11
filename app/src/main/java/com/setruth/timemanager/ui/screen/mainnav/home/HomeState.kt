package com.setruth.timemanager.ui.screen.mainnav.home


data class DateState(
    var date:String="",
    var week:String=""
)

data class TimeState(
    var hour:Int=0,
    var minute:Int=0,
    var second:Int=0,
)

data class UIState(
    var timeMode: TimeMode=TimeMode.PM,
    var immersionShow:Boolean=false,
    var loadingShow:Boolean=false,
    var timeMax:Boolean=true,
)
enum class TimeMode{
    PM,
    AM
}