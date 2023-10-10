package jp.co.actn.ais.data

import com.google.firebase.firestore.DocumentSnapshot

class Raspi_Data(val snapshot: DocumentSnapshot) {
    var id: String? = snapshot.id
    var pass: String? = null //読み込み・書き込み権限パスワード
    //ユーザー任意
    var name: String? = null //ラズパイの名称
    var number: String? = null //任意の識別番号
    var site: String? = null //設置場所
    var type: String? = null
    //ステータス
    var alert: Boolean? = null //警報発生の有無
    var run: Boolean? = null
    var comErr: Boolean? = null
    var palErr: Boolean? = null
    var numberOfContact:Int? = null

    init {
        pass = snapshot.get("pass") as? String
        //ユーザー任意
        name = snapshot.get("name") as? String
        number = snapshot.get("number") as? String
        site = snapshot.get("site") as? String
        type = snapshot.get("type") as? String
        //ステータス
        alert = snapshot.get("alert") as? Boolean
        run = snapshot.get("run") as? Boolean
        comErr = snapshot.get("comErr") as? Boolean
        palErr = snapshot.get("palErr") as? Boolean
        numberOfContact = snapshot.get("numberOfContact") as? Int

    }

}
