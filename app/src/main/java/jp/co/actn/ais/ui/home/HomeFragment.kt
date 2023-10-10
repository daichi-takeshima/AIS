package jp.co.actn.ais.ui.home

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import jp.co.actn.ais.LoginActivity
import jp.co.actn.ais.RaspiDetailActivity
import jp.co.actn.ais.data.Raspi_Data
import jp.co.actn.ais.databinding.FragmentHomeBinding
import jp.co.actn.ais.interfaces.OnItemClickListener
import jp.co.actn.ais.recyclerview.RaspiAdapter


class HomeFragment : Fragment(),OnItemClickListener {

    private var _binding: FragmentHomeBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //auth
    private lateinit var auth: FirebaseAuth
    //adapterの設定
    private val adapter = RaspiAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //リスト
        //recyclerViewの取得
        val raspiListView = binding.raspiList
        //adapterの設定
        raspiListView.adapter = adapter
        //境界線追加
        val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager(context).orientation)
        raspiListView.addItemDecoration(dividerItemDecoration)
        //layoutManagerの設定
        val layoutManager = LinearLayoutManager(context)
        raspiListView.layoutManager = layoutManager

        //ログイン判定
        auth = Firebase.auth
        val currentUser = auth.currentUser
        if (currentUser == null) {
            println("未ログイン ログイン画面表示")
            //未ログイン　ログイン画面の表示
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
        } else {
            println("ログイン済み　表示データ取得")
            //ログイン済み　リストデータの取得、表示の更新
            listDataSet()
        }

        return root
    }

    override fun onItemClick(position: Int) {
        val data = adapter.dataArray[position]
        val id = data.id
        val intent = Intent(context, RaspiDetailActivity::class.java)
        intent.putExtra("id",id)
        startActivity(intent)

    }

    override fun onStart() {
        super.onStart()
        println("HOME onStart")
        val currentUser = auth.currentUser
        if (currentUser != null) {
            listDataSet()
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //ラズパイデータの取得→リストの更新
    private fun listDataSet() {
        //ラズパイデータの取得
        val db = Firebase.firestore
        val collectionRef = db.collection("RaspberryPi")
        collectionRef.addSnapshotListener{value, err ->
            if (err != null) {
                Log.w(TAG, "Listen failed.", err)
            }
            //配列を空にする
            adapter.dataArray = emptyArray()
            //配列に取得したデータを入れる
            value?.let {
                for (doc in it) {
                    adapter.dataArray += Raspi_Data(snapshot = doc)
                }
            }
            //リストビューの更新
            adapter.notifyDataSetChanged()
        }

    }
}