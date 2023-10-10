package jp.co.actn.ais

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import jp.co.actn.ais.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        println("onCreate")
        auth = Firebase.auth
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        //test
        println("Debug test" + binding.editEmailAddress.text)
        //ログインボタンの処理
        binding.loginButton.setOnClickListener {
            println("Debug: " + "ログインボタン押下")
            //textEdit所得
            val mailEdit = binding.editEmailAddress
            val passEdit = binding.editPassword
            //未入力がある場合
            if (mailEdit.text.isEmpty() || passEdit.text.isEmpty()) {
                println("Debug: " + "エンプティ")
                AlertDialog.Builder(this) // FragmentではActivityを取得して生成
                    .setTitle("未入力の項目があります")
                    .setMessage("メールアドレスとパスワードの両方を入力してください")
                    .setPositiveButton("OK"){ dialog, which -> }.show()
            } else {
                println("Debug: " + "両方入力済み")
                val email = mailEdit.text.toString()
                val pass = passEdit.text.toString()
                println("Debug: " + "サインイン開始")
                auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(this) {task ->
                    println("Debug: " + "サインイン処理終了")
                    if (task.isSuccessful) {
                        //成功時の処理
                        println("Debug: " + "サインイン成功")
                        finish()
                    } else {
                        println("Debug: " + "サインイン失敗")
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
            }
        }
        binding.signUpButton.setOnClickListener { println("Debug: " + "新規作成タップ") }
    }


}