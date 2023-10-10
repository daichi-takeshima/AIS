package jp.co.actn.ais.data.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateChange {
    val timeFullFormat = "yyyy-MM-dd(E) HH:mm:ss"
    val dateFullFormat = "yyyy-MM-dd(E)"
    val nonDayFormat = "yyyy-MM-dd"

    //文字列で取得
    fun stringFromDate(date: Date,format:String):String {
        val dateFormat = SimpleDateFormat(format, Locale.JAPANESE)
        return dateFormat.format(date)
    }
    //Date型で取得
    fun dateFromString(dateString:String,format:String):Date {
        val dateFormat = SimpleDateFormat(format, Locale.JAPANESE)
        return dateFormat.parse(dateString)
    }

    //年月のみを取得
    fun thisMonthString(): String {
        val currentDateStr = stringFromDate(Date(), dateFullFormat)
        return currentDateStr.substring(0, 7)
    }
    //今日の文字列を取得
    fun todayString(): String {
        return stringFromDate(Date(),dateFullFormat)
    }
    //曜日なしの今日
    fun todayStringNonDay(): String {
        return stringFromDate(Date(),nonDayFormat)
    }


}