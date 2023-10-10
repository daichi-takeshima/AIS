package jp.co.actn.ais.recyclerview

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import jp.co.actn.ais.R
import jp.co.actn.ais.RaspiDetailActivity
import jp.co.actn.ais.data.Raspi_Data
import jp.co.actn.ais.interfaces.OnItemClickListener

class RaspiAdapter(private val listener: OnItemClickListener) : RecyclerView.Adapter<RaspiAdapter.ViewHolder>(){

    var dataArray: Array<Raspi_Data> = emptyArray()

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        //TextView
        val name: TextView
        val number: TextView
        val site: TextView
        val id: TextView
        //ステータスアイコン
        val alertView: ImageView
        val runView: ImageView
        val notificationView: ImageView
        val comView: ImageView
        val typeView: ImageView
        val parentRunView: ImageView

        init {
            // Define click listener for the ViewHolder's View.
            //TextView
            name = view.findViewById(R.id.name)
            number = view.findViewById(R.id.number)
            site = view.findViewById(R.id.site)
            id = view.findViewById(R.id.id)
            //ステータスアイコン
            alertView = view.findViewById(R.id.alertView)
            runView = view.findViewById(R.id.runView)
            notificationView = view.findViewById(R.id.notificationView)
            comView = view.findViewById(R.id.comView)
            typeView = view.findViewById(R.id.typeView)
            parentRunView = view.findViewById(R.id.parentRunView)

            view.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position)
                }
            }
        }

    }

    private inner class ItemClickListener : View.OnClickListener{

        override fun onClick(view : View){
            // 詳細ページを遷移させるロジックを記述する
            val context = view.context
            val intent = Intent(context, RaspiDetailActivity::class.java)
            intent.putExtra("a","a")
            context.startActivity(intent)
        }
    }





    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.raspi_item_view, viewGroup, false)
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        val data = dataArray[position]
        //TextView
        viewHolder.name.text = "名称　　：" + data.name
        viewHolder.number.text = "番号　　：" + data.number
        viewHolder.site.text = "設置場所：" + data.site
        viewHolder.id.text = "ID：" + data.id
        //ステータスアイコン
        if (data.type == "CONTACT") {
            //接点ユニット
            viewHolder.typeView.setImageResource(R.drawable.contact)
            //発報有無
            when (data.alert) {
                true -> {
                    viewHolder.alertView.setImageResource(R.drawable.exist_alert)
                }
                false -> {
                    viewHolder.alertView.setImageResource(R.drawable.no_alert)
                }
                else -> {
                    viewHolder.alertView.setImageDrawable(null)
                }
            }
            //温湿度親機起動エラー 非表示
            viewHolder.parentRunView.setImageDrawable(null)
        } else if (data.type == "ARIA") {
            //温湿度ユニット
            viewHolder.typeView.setImageResource(R.drawable.temp_hum)
            //発報有無　表示なし(改修予定)
            viewHolder.alertView.setImageDrawable(null)
            //親機起動エラー
            if (data.run == true) {
                if (data.palErr == true) {
                    viewHolder.parentRunView.setImageResource(R.drawable.pal_err)
                } else {
                    viewHolder.parentRunView.setImageResource(R.drawable.pal_run)
                }
            } else {
                viewHolder.parentRunView.setImageResource(R.drawable.pal_stop)
            }
        }
        //運転状態
        if (data.run == true) {
            viewHolder.runView.setImageResource(R.drawable.unit_run)
            //通信エラー
            if (data.comErr == true) {
                viewHolder.comView.setImageResource(R.drawable.com_err)
            } else {
                viewHolder.comView.setImageResource(R.drawable.com_normal)
            }
        } else {
            viewHolder.runView.setImageResource(R.drawable.unit_stop)
            viewHolder.comView.setImageResource(R.drawable.com_stop)
        }

        //通知設定
        //////////
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataArray.size
}