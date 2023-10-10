package jp.co.actn.ais.data

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import io.grpc.okhttp.internal.Util
import jp.co.actn.ais.data.model.DateChange
import java.time.YearMonth

class GPIO_Data(val snapshot: DocumentSnapshot) {
    var reference: DocumentReference? = null
    var id: String? = snapshot.id
    var pin: Int? = null
    //ユーザー設定
    var inUse: Boolean? = null
    var type: Int? = null
    var setting: Int? = null
    var name: String? = null
    //ステータス
    var latestStatus: Int? = null
    var latestUpdateDate: String? = null
    //接点データ key:yyyy-MM value:HistoryData
    val historyDataDic:MutableMap<String,HistoryData> = mutableMapOf()
    //カウントデータ key:yyyy-MM value:CountData
    val countDataDic:MutableMap<String,CountData> = mutableMapOf()

    init {
        reference = snapshot.reference
        var pinLong = snapshot.get("pin") as? Long
        pin = pinLong?.toInt()
        //ユーザー設定
        inUse = snapshot.get("inUse") as? Boolean
        var typeLong = snapshot.get("type") as? Long
        type = typeLong?.toInt()
        var settingLong = snapshot.get("setting") as? Long
        setting = settingLong?.toInt()
        name = snapshot.get("name") as? String
        //ステータス
        var latestStatusLong = snapshot.get("latestStatus") as? Long
        latestStatus = latestStatusLong?.toInt()
        latestUpdateDate = snapshot.get("latestUpdateDate") as? String
    }

    fun typeString():String? {
        when (inUse) {
            true -> {
                return when (type) {
                    0 -> {
                        "運転信号"
                    }
                    1 -> {
                        "警報信号"
                    }
                    2 -> {
                        "カウント"
                    }
                    else -> {
                        null
                    }
                }
            }
            false -> {
                return "不使用"
            }
            else -> {
                return null
            }
        }
    }

    //文字列で現在の状態を返す
    fun latestStatusStr():String? {
        return when(type){
            0 -> {//運転信号
                if (latestStatus == 0) {
                    return "停止中"
                } else {
                    return "運転中"
                }
            }
            1 -> {//警報信号
                if (latestStatus == 0) {
                    return "警報停止"
                } else {
                    return "発報中"
                }
            }
            2 -> {
                //今月のカウントデータ取得→今日のカウントデータ取得
                val dateChange = DateChange()
                val thisMonth = dateChange.thisMonthString()
                val thisMonthData = countDataDic[thisMonth]
                val todayStr = dateChange.todayString()
                if (thisMonthData == null) {
                    return "0"
                } else {
                    return thisMonthData.targetDateData(todayStr).toString()
                }
            }
            else -> {return null}
        }
    }

    //カウントデータの取得
    //今月のデータ
    fun getThisMonthCount(completion:(()->Unit)?) {
        val thisMonth = DateChange().thisMonthString()
        val docRef = reference?.collection("Count")?.document(thisMonth)
        docRef?.let {
            it.addSnapshotListener{snapshot,err ->
                err?.let { err
                    Log.w(ContentValues.TAG, "Listen failed.", err)
                }
                snapshot?.let {snapshot
                    countDataDic[thisMonth] = CountData(snapshot)
                    completion?.let { it1 -> it1() }
                }
            }
        }
    }
    //指定した月のカウントデータ取得
    fun getTargetMonthCount(yearMonth: String) {
        val docRef = reference?.collection("Count")?.document(yearMonth)
        docRef?.let {
            it.get().addOnSuccessListener{snapshot ->
                snapshot?.let {snapshot
                    countDataDic[yearMonth] = CountData(snapshot)
                }
            }
        }
    }
}

//月別の接点履歴
class HistoryData(snapshot: DocumentSnapshot){

}
//月別のカウントデータ
class CountData(snapshot: DocumentSnapshot){
    var reference: DocumentReference
    var yearMonth: String
    var fieldValues: MutableMap<String,Int> = mutableMapOf()//<日付,カウント>
    init {
        reference = snapshot.reference
        yearMonth = snapshot.id
        snapshot.data?.forEach {
            val key = it.key //DateString yyyy-MM-dd(E)
            val value = it.value as? Long //カウントデータ
            fieldValues[key] = value?.toInt() ?: 0
        }
    }
    //対象の日のカウントデータを返す
    fun targetDateData(dateString:String):Int {
        return fieldValues[dateString] ?:0
    }
}