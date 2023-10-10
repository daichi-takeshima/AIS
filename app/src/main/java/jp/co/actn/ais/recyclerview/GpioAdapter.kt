package jp.co.actn.ais.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import jp.co.actn.ais.R
import jp.co.actn.ais.data.GPIO_Data
import jp.co.actn.ais.data.Raspi_Data
import jp.co.actn.ais.interfaces.OnItemClickListener

class GpioAdapter(private val listener: OnItemClickListener,private val context: Context,private val raspiData:Raspi_Data): RecyclerView.Adapter<GpioAdapter.ViewHolder>() {

    var dataArray: Array<GPIO_Data> = emptyArray()

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        //textView
        val termNumberView: TextView
        val typeView: TextView
        val nameView: TextView
        val dateView: TextView
        val countView: TextView
        val statusImage: ImageView
        init {

            view.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position)
                }
            }
            termNumberView = view.findViewById(R.id.termNumberView)
            typeView = view.findViewById(R.id.typeView)
            nameView = view.findViewById(R.id.nameView)
            dateView = view.findViewById(R.id.dateView)
            countView = view.findViewById(R.id.countView)
            statusImage = view.findViewById(R.id.statusImage)

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
            //クリック時の処理をここに書く
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.gpio_item_view, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //TextView
        val data = dataArray[position]
        val termNumberView = holder.termNumberView
        termNumberView.text = "端子\n${position + 1}"
        if(data.inUse == true) {
            when (data.type) {
                0 -> {//運転信号
                    termNumberView.setBackgroundColor(ContextCompat.getColor(context, R.color.run_green))
                }

                1 -> {//警報信号
                    termNumberView.setBackgroundColor(ContextCompat.getColor(context,R.color.alert_red))
                }

                2 -> {//カウント
                    termNumberView.setBackgroundColor(ContextCompat.getColor(context,R.color.count_blue))
                }

                else -> {//不使用
                    termNumberView.setBackgroundColor(Color.Gray.hashCode())
                }
            }
        } else {//不使用
            termNumberView.setBackgroundColor(Color.Gray.hashCode())
        }
        holder.typeView.text = data.typeString()
        holder.nameView.text = data.name
        holder.dateView.text = data.latestUpdateDate?.substring(0,21) ?: ""
        //ステータス
        val runStatus = raspiData.run
        val countView = holder.countView
        val statusImage = holder.statusImage
        if (data.inUse == false) {
            countView.text = ""
            statusImage.setImageDrawable(null)
        } else {
            if (data.type == 2) {//カウントデータ
                //運転状態に関わらずカウントデータを表示
                //今日のカウントを取得する
                val todayCount = data.latestStatusStr()
                countView.text = todayCount
                statusImage.setImageDrawable(null) //ステータス画像は非表示
            } else {//運転・警報
                countView.text = "" //カウントデータは表示しない
                if (raspiData.run == true) {//ユニット運転中
                    //ステータスに合わせて表示を変更
                    when(data.latestStatusStr()){
                        "停止中" -> {//運転停止
                            statusImage.setImageResource(R.drawable.stop)
                        }
                        "運転中" -> {//運転中
                            statusImage.setImageResource(R.drawable.run)
                        }
                        "警報停止" -> {//警報停止
                            statusImage.setImageResource(R.drawable.no_alert)
                        }
                        "発報中" -> {//発報中
                            statusImage.setImageResource(R.drawable.alert_progress)
                        }
                        else -> {
                            statusImage.setImageDrawable(null)
                        }
                    }
                } else {//ユニット停止中
                    countView.text = ""
                    statusImage.setImageDrawable(null)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        println("getItemCount:${dataArray.size}")
        return dataArray.size
    }
}