package jp.co.actn.ais

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import jp.co.actn.ais.data.GPIO_Data
import jp.co.actn.ais.data.Raspi_Data
import jp.co.actn.ais.databinding.ActivityRaspiDetailBinding
import jp.co.actn.ais.interfaces.OnItemClickListener
import jp.co.actn.ais.recyclerview.GpioAdapter

class RaspiDetailActivity : AppCompatActivity(), OnItemClickListener {
    private lateinit var binding: ActivityRaspiDetailBinding
    private lateinit var db: FirebaseFirestore
    private var raspiRef: DocumentReference? = null
    private lateinit var adapter: GpioAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_raspi_detail)

        db = Firebase.firestore

        //view bindingの設定
        binding = ActivityRaspiDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val getId = intent.getStringExtra("id")
        getId?.let {
            println("取得データ:$it")
            //ラズパイドキュメントRefに入れる
            raspiRef = db.collection("RaspberryPi").document(it)
            snapshotListenRaspi()
            getGpioData(completion = null)
        } ?: run {
            println("取得データ：なし")
        }

        //リスト
        //recyclerViewの取得
        val gpioListView = binding.gpioList

        //adapterの設定
        //gpioListView.adapter = adapter

        //境界線追加
        val dividerItemDecoration = DividerItemDecoration(this, LinearLayoutManager(this).orientation)
        gpioListView.addItemDecoration(dividerItemDecoration)
        //layoutManagerの設定
        val layoutManager = LinearLayoutManager(this)
        gpioListView.layoutManager = layoutManager
    }

    //ラズパイのデータ取得
    private fun snapshotListenRaspi() {
        raspiRef?.let {
            it.addSnapshotListener{value, err ->
                if (err != null) {
                    Log.w(ContentValues.TAG, "Listen failed.", err)
                }
                value?.let {
                    val raspiData = Raspi_Data(snapshot = it)
                    //リストの更新
                    adapter = GpioAdapter(this, context = this, raspiData = raspiData)
                    //recyclerViewの取得
                    val gpioListView = binding.gpioList
                    //adapterの設定
                    gpioListView.adapter = adapter
                    //textViewの更新
                    binding.nameView.text =   "名称　　：" + raspiData.name
                    binding.numberView.text = "番号　　：" + raspiData.number
                    binding.siteView.text =   "設置場所：" + raspiData.site
                    binding.idView.text =     "ID    　：" + raspiData.id
                    //imageButtonの設定
                    if (raspiData.run == true) {
                        //運転
                        binding.runButton.setImageResource(R.drawable.unit_run)
                        //通信状態
                        if (raspiData.comErr == true) {
                            binding.comButton.setImageResource(R.drawable.com_err)
                        } else if (raspiData.comErr == false) {
                            binding.comButton.setImageResource(R.drawable.com_normal)
                        }
                    } else {
                        //運転
                        binding.runButton.setImageResource(R.drawable.unit_stop)
                        //通信状態
                        binding.comButton.setImageResource(R.drawable.com_stop)
                    }
                    //通知
                    ///////////////
                }
            }
        }?:{
            //エラーを通知
            AlertDialog.Builder(this)
                .setTitle("データの取得に失敗しました")
                .setTitle("前画面からやり直してください")
                .setPositiveButton("OK"){dialog, which -> }.show()
        }

    }

    override fun onItemClick(position: Int) {
        println("クリック$position")
    }

    //GPIOデータの取得→リストの更新
    private fun getGpioData(completion:(() -> Unit)?) {
        //GPIOデータの取得
        raspiRef?.let { it ->
            val gpioColRef = it.collection("GPIO")
            gpioColRef.orderBy("pin").addSnapshotListener{value, err ->
                if (err != null) {
                    Log.w(ContentValues.TAG, "Listen failed.", err)
                    //エラーを通知
                    AlertDialog.Builder(this)
                        .setTitle("データの取得に失敗しました")
                        .setTitle("前画面からやり直してください")
                        .setPositiveButton("OK"){dialog, which -> }.show()
                }
                //配列を空にする
                adapter.dataArray = emptyArray()
                //配列に取得したデータを入れる
                value?.let {
                    for (doc in it) {
                        println("gpio_data:${doc}")
                        var gpioData = GPIO_Data(snapshot = doc)
                        adapter.dataArray += gpioData
                        if (gpioData.type == 2) {
                            gpioData.getThisMonthCount(completion = {
                                adapter.notifyDataSetChanged()
                            })
                        }
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }
}