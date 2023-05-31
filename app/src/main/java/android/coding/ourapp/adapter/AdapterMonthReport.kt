package android.coding.ourapp.adapter

import android.coding.ourapp.data.datasource.model.Month
import android.coding.ourapp.databinding.ListItemAssessmentShimmerBinding
import android.coding.ourapp.databinding.ListItemReportBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class AdapterMonthReport(
    private val data : MutableList<Month>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val ITEM_ACTUAL = 0
    private val ITEM_SHIMMER = 1

    private var listenerOnClick: ((String) -> Unit)? = null

    fun setItemClickListener(listener: (String) -> Unit) {
        listenerOnClick = listener
    }

    inner class MonthViewHolder(val binding : ListItemReportBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(data : List<Month>, position : Int){
           binding.apply {
               tittle.text = data[position].name
               val count = "${data[position].count.toString()} laporan telah dibuat"
               subTittle.text = count
               card.setCardBackgroundColor(data[position].background)
               card.setOnClickListener {
                   listenerOnClick?.invoke(data[position].name)
               }
           }
        }
    }

    inner class SViewHolder(val binding : ListItemAssessmentShimmerBinding):RecyclerView.ViewHolder(binding.root){
        fun startShimmerEffect() {
            binding.shimmerLayout.startShimmer()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            ITEM_ACTUAL -> {
                return MonthViewHolder(ListItemReportBinding.inflate(LayoutInflater.from(parent.context),parent,false))
            }
            ITEM_SHIMMER -> {
                SViewHolder(ListItemAssessmentShimmerBinding.inflate(LayoutInflater.from(parent.context),parent,false))
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }

    }



    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MonthViewHolder) {
            if(data.isNotEmpty()){
                holder.bind(data,position)
            }
        } else if (holder is SViewHolder) {
            holder.startShimmerEffect()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (data.isNotEmpty()) {
            ITEM_ACTUAL
        } else {
            ITEM_SHIMMER
        }
    }

    override fun getItemCount(): Int  = if(data.isEmpty()) 12 else data.size



    interface OnClick{
        fun onDetail(name : String)
    }
}